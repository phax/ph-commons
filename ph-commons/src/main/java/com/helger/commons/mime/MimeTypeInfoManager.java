/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.commons.mime;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.Singleton;
import com.helger.commons.annotation.VisibleForTesting;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.multimap.IMultiMapListBased;
import com.helger.commons.collection.multimap.MultiTreeMapArrayListBased;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.io.file.FilenameHelper;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.microdom.IMicroDocument;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.MicroDocument;
import com.helger.commons.microdom.convert.MicroTypeConverter;
import com.helger.commons.microdom.serialize.MicroReader;
import com.helger.commons.mime.MimeTypeInfo.ExtensionWithSource;
import com.helger.commons.mime.MimeTypeInfo.MimeTypeWithSource;
import com.helger.commons.state.EChange;
import com.helger.commons.string.StringHelper;

/**
 * This is the central manager for all {@link MimeTypeInfo} objects.
 *
 * @author Philip Helger
 */
@ThreadSafe
@Singleton
public class MimeTypeInfoManager
{
  private static final class SingletonHolder
  {
    // Read default
    static final MimeTypeInfoManager s_aInstance = new MimeTypeInfoManager ().readDefault ();
  }

  public static final String MIME_TYPE_INFO_XML = "codelists/mime-type-info.xml";

  private static boolean s_bDefaultInstantiated = false;

  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
  @GuardedBy ("m_aRWLock")
  private final List <MimeTypeInfo> m_aList = new ArrayList <MimeTypeInfo> ();
  @GuardedBy ("m_aRWLock")
  private final IMultiMapListBased <IMimeType, MimeTypeInfo> m_aMapMimeType = new MultiTreeMapArrayListBased <IMimeType, MimeTypeInfo> ();
  @GuardedBy ("m_aRWLock")
  private final IMultiMapListBased <String, MimeTypeInfo> m_aMapExt = new MultiTreeMapArrayListBased <String, MimeTypeInfo> ();

  /**
   * Create a new empty (!!) instance.
   */
  public MimeTypeInfoManager ()
  {}

  public static boolean isDefaultInstantiated ()
  {
    return s_bDefaultInstantiated;
  }

  /**
   * @return The default instance that contains all predefined
   *         {@link MimeTypeInfo}s.
   */
  @Nonnull
  public static MimeTypeInfoManager getDefaultInstance ()
  {
    final MimeTypeInfoManager ret = SingletonHolder.s_aInstance;
    s_bDefaultInstantiated = true;
    return ret;
  }

  /**
   * Read the default resource.
   *
   * @return this
   * @see #MIME_TYPE_INFO_XML
   */
  @Nonnull
  public MimeTypeInfoManager readDefault ()
  {
    return read (new ClassPathResource (MIME_TYPE_INFO_XML));
  }

  /**
   * Read the information from the specified resource.
   *
   * @param aRes
   *        The resource to read. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public MimeTypeInfoManager read (@Nonnull final IReadableResource aRes)
  {
    ValueEnforcer.notNull (aRes, "Resource");

    final IMicroDocument aDoc = MicroReader.readMicroXML (aRes);
    if (aDoc == null)
      throw new IllegalArgumentException ("Failed to read MimeTypeInfo resource " + aRes);

    for (final IMicroElement eItem : aDoc.getDocumentElement ().getAllChildElements ())
    {
      final MimeTypeInfo aInfo = MicroTypeConverter.convertToNative (eItem, MimeTypeInfo.class);
      registerMimeType (aInfo);
    }
    return this;
  }

  /**
   * Remove all registered mime types
   *
   * @return {@link EChange}.
   */
  @Nonnull
  public EChange clearCache ()
  {
    return m_aRWLock.writeLocked ( () -> {
      EChange ret = EChange.UNCHANGED;
      if (!m_aList.isEmpty ())
      {
        m_aList.clear ();
        ret = EChange.CHANGED;
      }
      if (!m_aMapExt.isEmpty ())
      {
        m_aMapExt.clear ();
        ret = EChange.CHANGED;
      }
      if (!m_aMapMimeType.isEmpty ())
      {
        m_aMapMimeType.clear ();
        ret = EChange.CHANGED;
      }
      return ret;
    });
  }

  public void reinitializeToDefault ()
  {
    clearCache ();
    readDefault ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public IMicroDocument getAsDocument ()
  {
    final IMicroDocument aDoc = new MicroDocument ();
    final IMicroElement eRoot = aDoc.appendElement ("mime-type-info");

    m_aRWLock.readLocked ( () -> {
      for (final MimeTypeInfo aInfo : CollectionHelper.getSorted (m_aList,
                                                                  Comparator.comparing (MimeTypeInfo::getPrimaryMimeTypeString)))
        eRoot.appendChild (MicroTypeConverter.convertToMicroElement (aInfo, "item"));
    });

    return aDoc;
  }

  public void registerMimeType (@Nonnull final MimeTypeInfo aInfo)
  {
    ValueEnforcer.notNull (aInfo, "Info");
    final Set <MimeTypeWithSource> aMimeTypes = aInfo.getAllMimeTypesWithSource ();
    final Set <ExtensionWithSource> aExtensions = aInfo.getAllExtensionsWithSource ();

    // Check if MimeType is unique
    // Note: Extension must not be unique
    m_aRWLock.readLocked ( () -> {
      for (final MimeTypeWithSource aMimeType : aMimeTypes)
      {
        final List <MimeTypeInfo> aExisting = m_aMapMimeType.get (aMimeType.getMimeType ());
        if (aExisting != null)
          throw new IllegalArgumentException ("Cannot register " +
                                              aInfo +
                                              ". A mapping for mime type '" +
                                              aMimeType +
                                              "' is already registered: " +
                                              aExisting);
      }
    });

    // Perform changes
    m_aRWLock.writeLocked ( () -> {
      m_aList.add (aInfo);
      for (final MimeTypeWithSource aMimeType : aMimeTypes)
        m_aMapMimeType.putSingle (aMimeType.getMimeType (), aInfo);
      for (final ExtensionWithSource aExt : aExtensions)
        m_aMapExt.putSingle (aExt.getExtension (), aInfo);
    });
  }

  @VisibleForTesting
  public final void addExtension (@Nonnull final MimeTypeInfo aInfo, @Nonnull final ExtensionWithSource aExt)
  {
    ValueEnforcer.notNull (aInfo, "Info");
    ValueEnforcer.notNull (aExt, "Ext");

    m_aRWLock.writeLocked ( () -> {
      m_aMapExt.putSingle (aExt.getExtension (), aInfo);
      aInfo.addExtension (aExt);
    });
  }

  @VisibleForTesting
  public final void addMimeType (@Nonnull final MimeTypeInfo aInfo, @Nonnull final MimeTypeWithSource aMimeType)
  {
    ValueEnforcer.notNull (aInfo, "Info");
    ValueEnforcer.notNull (aMimeType, "MimeType");

    m_aRWLock.writeLocked ( () -> {
      m_aMapMimeType.putSingle (aMimeType.getMimeType (), aInfo);
      aInfo.addMimeType (aMimeType);
    });
  }

  @Nullable
  @ReturnsMutableCopy
  public List <MimeTypeInfo> getAllInfosOfFilename (@Nullable final File aFile)
  {
    if (aFile == null)
      return null;

    final String sExtension = FilenameHelper.getExtension (aFile);
    return getAllInfosOfExtension (sExtension);
  }

  @Nullable
  @ReturnsMutableCopy
  public List <MimeTypeInfo> getAllInfosOfFilename (@Nullable final String sFilename)
  {
    if (StringHelper.hasNoText (sFilename))
      return null;

    final String sExtension = FilenameHelper.getExtension (sFilename);
    return getAllInfosOfExtension (sExtension);
  }

  /**
   * Get all infos associated with the specified filename extension.
   *
   * @param sExtension
   *        The extension to search. May be <code>null</code> or empty.
   * @return <code>null</code> if the passed extension is <code>null</code> or
   *         if no such extension is registered.
   */
  @Nullable
  @ReturnsMutableCopy
  public List <MimeTypeInfo> getAllInfosOfExtension (@Nullable final String sExtension)
  {
    // Extension may be empty!
    if (sExtension == null)
      return null;

    return m_aRWLock.readLocked ( () -> {
      List <MimeTypeInfo> ret = m_aMapExt.get (sExtension);
      if (ret == null)
      {
        // Especially on Windows, sometimes file extensions like "JPG" can be
        // found. Therefore also test for the lowercase version of the
        // extension.
        ret = m_aMapExt.get (sExtension.toLowerCase (Locale.US));
      }
      // Create a copy if present
      return ret == null ? null : CollectionHelper.newList (ret);
    });
  }

  /**
   * Get all infos associated with the passed mime type.
   *
   * @param aMimeType
   *        The mime type to search. May be <code>null</code>.
   * @return <code>null</code> if a <code>null</code> mime type was passed or
   *         the passed mime type is unknown.
   */
  @Nullable
  @ReturnsMutableCopy
  public List <MimeTypeInfo> getAllInfosOfMimeType (@Nullable final IMimeType aMimeType)
  {
    if (aMimeType == null)
      return null;

    final List <MimeTypeInfo> ret = m_aRWLock.readLocked ( () -> m_aMapMimeType.get (aMimeType));

    // Create a copy if present
    return ret == null ? null : CollectionHelper.newList (ret);
  }

  /**
   * @return A non-<code>null</code> set with all mime types infos known to this
   *         instance.
   */
  @Nonnull
  @ReturnsMutableCopy
  public List <MimeTypeInfo> getAllMimeTypeInfos ()
  {
    return m_aRWLock.readLocked ( () -> CollectionHelper.newList (m_aList));
  }

  /**
   * @return A non-<code>null</code> set with all mime types known to this
   *         instance.
   */
  @Nonnull
  @ReturnsMutableCopy
  public Set <IMimeType> getAllMimeTypes ()
  {
    final Set <IMimeType> ret = new LinkedHashSet <IMimeType> ();
    m_aRWLock.readLocked ( () -> {
      for (final MimeTypeInfo aInfo : m_aList)
        ret.addAll (aInfo.getAllMimeTypes ());
    });
    return ret;
  }

  /**
   * @return A non-<code>null</code> set with all mime types known to this
   *         instance.
   */
  @Nonnull
  @ReturnsMutableCopy
  public Set <String> getAllMimeTypeStrings ()
  {
    final Set <String> ret = new LinkedHashSet <String> ();
    m_aRWLock.readLocked ( () -> {
      for (final MimeTypeInfo aInfo : m_aList)
        ret.addAll (aInfo.getAllMimeTypeStrings ());
    });
    return ret;
  }

  /**
   * Check if any mime type is registered for the extension of the specified
   * filename.
   *
   * @param sFilename
   *        The filename to search. May neither be <code>null</code> nor empty.
   * @return <code>true</code> if at least one mime type is associated with the
   *         extension of the passed filename, <code>false</code> otherwise.
   */
  public boolean containsMimeTypeForFilename (@Nonnull @Nonempty final String sFilename)
  {
    ValueEnforcer.notEmpty (sFilename, "Filename");

    final String sExtension = FilenameHelper.getExtension (sFilename);
    return containsMimeTypeForExtension (sExtension);
  }

  /**
   * Get all mime types that are associated to the extension of the specified
   * filename.
   *
   * @param sFilename
   *        The filename to search. May neither be <code>null</code> nor empty.
   * @return Never <code>null</code> but maybe empty set if no mime type is
   *         associated with the extension of the passed filename.
   */
  @Nonnull
  @ReturnsMutableCopy
  public Set <IMimeType> getAllMimeTypesForFilename (@Nonnull @Nonempty final String sFilename)
  {
    ValueEnforcer.notEmpty (sFilename, "Filename");

    final String sExtension = FilenameHelper.getExtension (sFilename);
    return getAllMimeTypesForExtension (sExtension);
  }

  /**
   * Get all mime types that are associated to the extension of the specified
   * filename.
   *
   * @param sFilename
   *        The filename to search. May neither be <code>null</code> nor empty.
   * @return Never <code>null</code> but maybe empty set if no mime type is
   *         associated with the extension of the passed filename.
   */
  @Nonnull
  @ReturnsMutableCopy
  public Set <String> getAllMimeTypeStringsForFilename (@Nonnull @Nonempty final String sFilename)
  {
    ValueEnforcer.notEmpty (sFilename, "Filename");

    final String sExtension = FilenameHelper.getExtension (sFilename);
    return getAllMimeTypeStringsForExtension (sExtension);
  }

  /**
   * Get the primary (=first) mime type associated with the specified filename.
   *
   * @param sFilename
   *        The filename to retrieve the primary mime type from. May neither be
   *        <code>null</code> nor empty.
   * @return <code>null</code> if no mime type is associated with the extension
   *         of the passed filename
   */
  @Nullable
  public IMimeType getPrimaryMimeTypeForFilename (@Nonnull @Nonempty final String sFilename)
  {
    ValueEnforcer.notEmpty (sFilename, "Filename");

    final String sExtension = FilenameHelper.getExtension (sFilename);
    return getPrimaryMimeTypeForExtension (sExtension);
  }

  /**
   * Get the primary (=first) mime type associated with the specified filename.
   *
   * @param sFilename
   *        The filename to retrieve the primary mime type from. May neither be
   *        <code>null</code> nor empty.
   * @return <code>null</code> if no mime type is associated with the extension
   *         of the passed filename
   */
  @Nullable
  public String getPrimaryMimeTypeStringForFilename (@Nonnull @Nonempty final String sFilename)
  {
    ValueEnforcer.notEmpty (sFilename, "Filename");

    final String sExtension = FilenameHelper.getExtension (sFilename);
    return getPrimaryMimeTypeStringForExtension (sExtension);
  }

  /**
   * Check if any mime type is associated with the passed extension
   *
   * @param sExtension
   *        The filename extension to search. May not be <code>null</code>.
   * @return <code>true</code> if at least one mime type is associated,
   *         <code>false</code> if no mime type is associated with the extension
   */
  public boolean containsMimeTypeForExtension (@Nonnull final String sExtension)
  {
    ValueEnforcer.notNull (sExtension, "Extension");

    final List <MimeTypeInfo> aInfos = getAllInfosOfExtension (sExtension);
    return CollectionHelper.isNotEmpty (aInfos);
  }

  /**
   * Get all mime types that are associated to the specified filename extension.
   *
   * @param sExtension
   *        The filename extension to search. May not be <code>null</code>.
   * @return Never <code>null</code> but maybe empty set if no mime type is
   *         associated with the passed extension.
   */
  @Nonnull
  @ReturnsMutableCopy
  public Set <IMimeType> getAllMimeTypesForExtension (@Nonnull final String sExtension)
  {
    ValueEnforcer.notNull (sExtension, "Extension");

    final Set <IMimeType> ret = new LinkedHashSet <IMimeType> ();
    final List <MimeTypeInfo> aInfos = getAllInfosOfExtension (sExtension);
    if (aInfos != null)
      for (final MimeTypeInfo aInfo : aInfos)
        ret.addAll (aInfo.getAllMimeTypes ());
    return ret;
  }

  /**
   * Get all mime types that are associated to the specified filename extension.
   *
   * @param sExtension
   *        The filename extension to search. May not be <code>null</code>.
   * @return Never <code>null</code> but maybe empty set if no mime type is
   *         associated with the passed extension.
   */
  @Nonnull
  @ReturnsMutableCopy
  public Set <String> getAllMimeTypeStringsForExtension (@Nonnull final String sExtension)
  {
    ValueEnforcer.notNull (sExtension, "Extension");

    final Set <String> ret = new LinkedHashSet <String> ();
    final List <MimeTypeInfo> aInfos = getAllInfosOfExtension (sExtension);
    if (aInfos != null)
      for (final MimeTypeInfo aInfo : aInfos)
        ret.addAll (aInfo.getAllMimeTypeStrings ());
    return ret;
  }

  /**
   * Get the primary (=first) mime type that is associated to the specified
   * filename extension.
   *
   * @param sExtension
   *        The filename extension to search. May not be <code>null</code>.
   * @return <code>null</code> if no mime type is associated with the passed
   *         extension.
   */
  @Nullable
  public IMimeType getPrimaryMimeTypeForExtension (@Nonnull final String sExtension)
  {
    ValueEnforcer.notNull (sExtension, "Extension");

    final List <MimeTypeInfo> aInfos = getAllInfosOfExtension (sExtension);
    if (aInfos != null)
      for (final MimeTypeInfo aInfo : aInfos)
        return aInfo.getPrimaryMimeType ();
    return null;
  }

  /**
   * Get the primary (=first) mime type that is associated to the specified
   * filename extension.
   *
   * @param sExtension
   *        The filename extension to search. May not be <code>null</code>.
   * @return <code>null</code> if no mime type is associated with the passed
   *         extension.
   */
  @Nullable
  public String getPrimaryMimeTypeStringForExtension (@Nonnull final String sExtension)
  {
    ValueEnforcer.notNull (sExtension, "Extension");

    final List <MimeTypeInfo> aInfos = getAllInfosOfExtension (sExtension);
    if (aInfos != null)
      for (final MimeTypeInfo aInfo : aInfos)
        return aInfo.getPrimaryMimeTypeString ();
    return null;
  }

  /**
   * Get all extensions associated to the specified mime type
   *
   * @param aMimeType
   *        The mime type to search. May be <code>null</code>.
   * @return Never <code>null</code> but empty set if no extensions are present.
   */
  @Nonnull
  @ReturnsMutableCopy
  public Set <String> getAllExtensionsOfMimeType (@Nullable final IMimeType aMimeType)
  {
    final Set <String> ret = new LinkedHashSet <String> ();
    final List <MimeTypeInfo> aInfos = getAllInfosOfMimeType (aMimeType);
    if (aInfos != null)
      for (final MimeTypeInfo aInfo : aInfos)
        ret.addAll (aInfo.getAllExtensions ());
    return ret;
  }

  /**
   * Get the primary (=first) extension for the specified mime type.
   *
   * @param aMimeType
   *        The mime type to be searched. May be <code>null</code>.
   * @return <code>null</code> if the mime type has no file extension assigned
   */
  @Nullable
  public String getPrimaryExtensionOfMimeType (@Nullable final IMimeType aMimeType)
  {
    final List <MimeTypeInfo> aInfos = getAllInfosOfMimeType (aMimeType);
    if (aInfos != null)
      for (final MimeTypeInfo aInfo : aInfos)
      {
        // Not every info has an extension!
        final String ret = aInfo.getPrimaryExtension ();
        if (ret != null)
          return ret;
      }
    return null;
  }

  /**
   * Get all globs (=filename patterns) associated to the specified mime type
   *
   * @param aMimeType
   *        The mime type to search. May be <code>null</code>.
   * @return Never <code>null</code> but empty set if no globs are present.
   */
  @Nonnull
  @ReturnsMutableCopy
  public Set <String> getAllGlobsOfMimeType (@Nullable final IMimeType aMimeType)
  {
    final Set <String> ret = new LinkedHashSet <String> ();
    final List <MimeTypeInfo> aInfos = getAllInfosOfMimeType (aMimeType);
    if (aInfos != null)
      for (final MimeTypeInfo aInfo : aInfos)
        ret.addAll (aInfo.getAllGlobs ());
    return ret;
  }
}
