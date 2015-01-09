/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.GlobalDebug;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.MustBeLocked;
import com.helger.commons.annotations.MustBeLocked.ELockType;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.charset.CharsetManager;
import com.helger.commons.charset.EUnicodeBOM;
import com.helger.commons.collections.ArrayHelper;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.exceptions.InitializationException;
import com.helger.commons.io.file.FilenameHelper;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.microdom.reader.XMLMapHandler;
import com.helger.commons.regex.RegExHelper;
import com.helger.commons.state.EChange;
import com.helger.commons.string.StringHelper;

/**
 * Contains a basic set of MimeType determination method.
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class MimeTypeDeterminator
{
  private static final class SingletonHolder
  {
    static final MimeTypeDeterminator s_aInstance = new MimeTypeDeterminator ();
  }

  public static final IMimeType DEFAULT_MIME_TYPE = CMimeType.APPLICATION_OCTET_STREAM;

  private static final Logger s_aLogger = LoggerFactory.getLogger (MimeTypeDeterminator.class);
  private static final byte [] MIME_ID_GIF87A = new byte [] { 'G', 'I', 'F', '8', '7', 'a' };
  private static final byte [] MIME_ID_GIF89A = new byte [] { 'G', 'I', 'F', '8', '9', 'a' };
  private static final byte [] MIME_ID_JPG = new byte [] { (byte) 0xff, (byte) 0xd8 };
  private static final byte [] MIME_ID_PNG = new byte [] { (byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a };
  private static final byte [] MIME_ID_TIFF_MOTOROLLA = new byte [] { 'M', 'M' };
  private static final byte [] MIME_ID_TIFF_INTEL = new byte [] { 'I', 'I' };
  private static final byte [] MIME_ID_PSD = new byte [] { '8', 'B', 'P', 'S' };
  private static final byte [] MIME_ID_PDF = new byte [] { '%', 'P', 'D', 'F' };
  private static final byte [] MIME_ID_XLS = new byte [] { (byte) 0xD0, (byte) 0xcd, 0x11, (byte) 0xe0 };

  private static boolean s_bDefaultInstantiated = false;

  private final ReadWriteLock m_aRWLock = new ReentrantReadWriteLock ();

  // Maps file extension to MIME type
  private final Map <String, String> m_aFileExtMap = new HashMap <String, String> ();

  // Contains all byte[] to mime type mappings
  private final Set <MimeTypeContent> m_aMimeTypeContents = new HashSet <MimeTypeContent> ();

  private MimeTypeDeterminator ()
  {
    // Key: extension (without dot), value (MIME type)
    if (XMLMapHandler.readMap (new ClassPathResource ("codelists/fileext-mimetype-mapping.xml"), m_aFileExtMap)
                     .isFailure ())
      throw new InitializationException ("Failed to init file extension to mimetype mapping file");

    // Validate all file extensions
    if (GlobalDebug.isDebugMode ())
      for (final Map.Entry <String, String> aEntry : m_aFileExtMap.entrySet ())
      {
        final String sFileExt = aEntry.getKey ();
        if (!RegExHelper.stringMatchesPattern ("(|[a-zA-Z0-9]+(\\.[a-z0-9]+)*)", sFileExt))
          throw new InitializationException ("MIME file extension '" + sFileExt + "' is invalid!");
        if (aEntry.getValue ().contains (" "))
          throw new InitializationException ("MIME type '" + aEntry.getValue () + "' is invalid!");
      }

    _registerDefaultMimeTypeContents ();
  }

  @MustBeLocked (ELockType.WRITE)
  private void _registerDefaultMimeTypeContents ()
  {
    m_aMimeTypeContents.add (new MimeTypeContent (MIME_ID_GIF87A, CMimeType.IMAGE_GIF));
    m_aMimeTypeContents.add (new MimeTypeContent (MIME_ID_GIF89A, CMimeType.IMAGE_GIF));
    m_aMimeTypeContents.add (new MimeTypeContent (MIME_ID_JPG, CMimeType.IMAGE_JPG));
    m_aMimeTypeContents.add (new MimeTypeContent (MIME_ID_PNG, CMimeType.IMAGE_PNG));
    m_aMimeTypeContents.add (new MimeTypeContent (MIME_ID_TIFF_MOTOROLLA, CMimeType.IMAGE_TIFF));
    m_aMimeTypeContents.add (new MimeTypeContent (MIME_ID_TIFF_INTEL, CMimeType.IMAGE_TIFF));
    m_aMimeTypeContents.add (new MimeTypeContent (MIME_ID_PSD, CMimeType.IMAGE_PSD));
    m_aMimeTypeContents.add (new MimeTypeContent (MIME_ID_PDF, CMimeType.APPLICATION_PDF));
    m_aMimeTypeContents.add (new MimeTypeContent (MIME_ID_XLS, CMimeType.APPLICATION_MS_EXCEL));

    // Add all XML mime types: as the combination of all BOMs and all character
    // encodings as determined by
    // http://www.w3.org/TR/REC-xml/#sec-guessing
    final List <byte []> aXMLStuff = new ArrayList <byte []> ();
    // UCS4
    aXMLStuff.add (new byte [] { 0x3c, 0x00, 0x00, 0x00, 0x3f, 0x00, 0x00, 0x00 });
    aXMLStuff.add (new byte [] { 0x00, 0x3c, 0x00, 0x00, 0x00, 0x3f, 0x00, 0x00 });
    aXMLStuff.add (new byte [] { 0x00, 0x00, 0x3c, 0x00, 0x00, 0x00, 0x3f, 0x00 });
    aXMLStuff.add (new byte [] { 0x00, 0x00, 0x00, 0x3c, 0x00, 0x00, 0x00, 0x3f });
    // UTF-16
    aXMLStuff.add (new byte [] { 0x00, 0x3c, 0x00, 0x3f });
    aXMLStuff.add (new byte [] { 0x3c, 0x00, 0x3f, 0x00 });
    // ISO-8859-1/UTF-8/ASCII etc.
    aXMLStuff.add (new byte [] { 0x3c, 0x3f, 0x78, 0x6d });
    // EBCDIC
    aXMLStuff.add (new byte [] { 0x4c, 0x6f, (byte) 0xa7, (byte) 0x94 });

    // Register all types without the BOM
    for (final byte [] aXML : aXMLStuff)
      registerMimeTypeContent (new MimeTypeContent (aXML, CMimeType.TEXT_XML));

    // Register all type with the BOM
    for (final EUnicodeBOM eBOM : EUnicodeBOM.values ())
      for (final byte [] aXML : aXMLStuff)
      {
        final byte [] aData = ArrayHelper.getConcatenated (eBOM.getBytes (), aXML);
        registerMimeTypeContent (new MimeTypeContent (aData, CMimeType.TEXT_XML));
      }
  }

  public static boolean isInstantiated ()
  {
    return s_bDefaultInstantiated;
  }

  @Nonnull
  public static MimeTypeDeterminator getInstance ()
  {
    s_bDefaultInstantiated = true;
    return SingletonHolder.s_aInstance;
  }

  /**
   * Register a new MIME content type.
   *
   * @param aMimeTypeContent
   *        The content type to register. May not be <code>null</code>.
   * @return {@link EChange#CHANGED} if the object was successfully registered.
   */
  @Nonnull
  public EChange registerMimeTypeContent (@Nonnull final MimeTypeContent aMimeTypeContent)
  {
    ValueEnforcer.notNull (aMimeTypeContent, "MimeTypeContent");

    m_aRWLock.writeLock ().lock ();
    try
    {
      return EChange.valueOf (m_aMimeTypeContents.add (aMimeTypeContent));
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  /**
   * Unregister an existing MIME content type.
   *
   * @param aMimeTypeContent
   *        The content type to unregister. May not be <code>null</code>.
   * @return {@link EChange#CHANGED} if the object was successfully
   *         unregistered.
   */
  @Nonnull
  public EChange unregisterMimeTypeContent (@Nullable final MimeTypeContent aMimeTypeContent)
  {
    if (aMimeTypeContent == null)
      return EChange.UNCHANGED;

    m_aRWLock.writeLock ().lock ();
    try
    {
      return EChange.valueOf (m_aMimeTypeContents.remove (aMimeTypeContent));
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  /**
   * Try to find the MIME type that matches the passed content string.
   *
   * @param s
   *        The content string to check. May be <code>null</code>.
   * @param sCharsetName
   *        The charset used to convert the string to a byte array. May neither
   *        be <code>null</code> nor empty.
   * @return <code>null</code> if no matching MIME type was found.
   */
  @Nonnull
  @Deprecated
  public IMimeType getMimeTypeFromString (@Nullable final String s, @Nonnull @Nonempty final String sCharsetName)
  {
    return getMimeTypeFromBytes (s == null ? null : CharsetManager.getAsBytes (s, sCharsetName));
  }

  /**
   * Try to find the MIME type that matches the passed content string.
   *
   * @param s
   *        The content string to check. May be <code>null</code>.
   * @param aCharset
   *        The charset used to convert the string to a byte array. May not be
   *        <code>null</code>.
   * @return {@link #DEFAULT_MIME_TYPE} if no matching MIME type was found.
   *         Never <code>null</code>.
   */
  @Nonnull
  public IMimeType getMimeTypeFromString (@Nullable final String s, @Nonnull final Charset aCharset)
  {
    return getMimeTypeFromString (s, aCharset, DEFAULT_MIME_TYPE);
  }

  /**
   * Try to find the MIME type that matches the passed content string.
   *
   * @param s
   *        The content string to check. May be <code>null</code>.
   * @param aCharset
   *        The charset used to convert the string to a byte array. May not be
   *        <code>null</code>.
   * @param aDefault
   *        The default MIME type to be returned, if no MIME type could be
   *        found. May be <code>null</code>.
   * @return <code>aDefault</code> if no matching MIME type was found. May be
   *         <code>null</code>.
   */
  @Nullable
  public IMimeType getMimeTypeFromString (@Nullable final String s,
                                          @Nonnull final Charset aCharset,
                                          @Nullable final IMimeType aDefault)
  {
    return getMimeTypeFromBytes (s == null ? null : CharsetManager.getAsBytes (s, aCharset), aDefault);
  }

  /**
   * Try to determine the MIME type from the given byte array.
   *
   * @param b
   *        The byte array to parse. May be <code>null</code> or empty.
   * @return {@link #DEFAULT_MIME_TYPE} if no specific MIME type was found.
   *         Never <code>null</code>.
   */
  @Nonnull
  public IMimeType getMimeTypeFromBytes (@Nullable final byte [] b)
  {
    return getMimeTypeFromBytes (b, DEFAULT_MIME_TYPE);
  }

  /**
   * Try to determine the MIME type from the given byte array.
   *
   * @param b
   *        The byte array to parse. May be <code>null</code> or empty.
   * @param aDefault
   *        The default MIME type to be returned, if no matching MIME type was
   *        found. May be <code>null</code>.
   * @return The supplied default value, if no matching MIME type was found. May
   *         be <code>null</code>.
   */
  @Nullable
  public IMimeType getMimeTypeFromBytes (@Nullable final byte [] b, @Nullable final IMimeType aDefault)
  {
    if (b != null && b.length > 0)
    {
      m_aRWLock.readLock ().lock ();
      try
      {
        for (final MimeTypeContent aMTC : m_aMimeTypeContents)
          if (aMTC.matchesBeginning (b))
            return aMTC.getMimeType ();
      }
      finally
      {
        m_aRWLock.readLock ().unlock ();
      }
    }

    // default fallback
    return aDefault;
  }

  /**
   * @return A copy of all registered {@link MimeTypeContent} objects. Never
   *         <code>null</code> but maybe empty.
   */
  @Nonnull
  @ReturnsMutableCopy
  public List <MimeTypeContent> getAllMimeTypeContents ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return ContainerHelper.newList (m_aMimeTypeContents);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  /**
   * Get the MIME type from the extension of the passed filename.
   *
   * @param sFilename
   *        The filename to check. May be <code>null</code>.
   * @return <code>null</code> if no MIME type was found.
   */
  @Nullable
  @Deprecated
  public String getMimeTypeFromFilename (@Nullable final String sFilename)
  {
    final String sExt = FilenameHelper.getExtension (sFilename);
    return getMimeTypeFromExtension (sExt);
  }

  /**
   * Get the MIME type object from the extension of the passed filename.
   *
   * @param sFilename
   *        The filename to check. May be <code>null</code>.
   * @return <code>null</code> if no MIME type was found.
   */
  @Nullable
  @Deprecated
  public MimeType getMimeTypeObjectFromFilename (@Nonnull final String sFilename)
  {
    final String sMimeType = getMimeTypeFromFilename (sFilename);
    return MimeTypeParser.parseMimeType (sMimeType);
  }

  /**
   * Get the MIME type from the passed filename extension.
   *
   * @param sExtension
   *        The extension to check. Must be without the leading dot, so "doc" is
   *        valid but ".doc" is not. May be <code>null</code>.
   * @return <code>null</code> if no MIME type was found.
   */
  @Nullable
  @Deprecated
  public String getMimeTypeFromExtension (@Nullable final String sExtension)
  {
    if (StringHelper.hasNoText (sExtension))
      return null;

    String ret = m_aFileExtMap.get (sExtension);
    if (ret == null)
    {
      // Especially on Windows, sometimes file extensions like "JPG" can be
      // found. Therefore also test for the lowercase version of the extension.
      ret = m_aFileExtMap.get (sExtension.toLowerCase (Locale.US));
    }
    return ret;
  }

  /**
   * Get the MIME type object from the passed filename extension.
   *
   * @param sExtension
   *        The extension to check. Must be without the leading dot, so "doc" is
   *        valid but ".doc" is not. May be <code>null</code>.
   * @return <code>null</code> if no MIME type was found.
   */
  @Nullable
  @Deprecated
  public MimeType getMimeTypeObjectFromExtension (@Nullable final String sExtension)
  {
    final String sMimeType = getMimeTypeFromExtension (sExtension);
    return MimeTypeParser.parseMimeType (sMimeType);
  }

  /**
   * @return A non-<code>null</code> list of all known MIME types as string.
   */
  @Nonnull
  @ReturnsMutableCopy
  @Deprecated
  public Collection <String> getAllKnownMimeTypes ()
  {
    return ContainerHelper.newList (m_aFileExtMap.values ());
  }

  /**
   * @return A non-<code>null</code> map from filename extension to MIME type.
   *         Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  @Deprecated
  public Map <String, String> getAllKnownMimeTypeFilenameMappings ()
  {
    return ContainerHelper.newMap (m_aFileExtMap);
  }

  /**
   * Reset the MimeTypeContent cache to the initial state.
   *
   * @see #registerMimeTypeContent(MimeTypeContent)
   * @see #unregisterMimeTypeContent(MimeTypeContent)
   */
  public void resetCache ()
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      m_aMimeTypeContents.clear ();
      _registerDefaultMimeTypeContents ();
      if (s_aLogger.isDebugEnabled ())
        s_aLogger.debug ("Cache was reset: " + MimeTypeDeterminator.class.getName ());
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }
}
