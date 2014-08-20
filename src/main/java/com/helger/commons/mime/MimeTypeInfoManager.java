/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.collections.multimap.IMultiMapListBased;
import com.helger.commons.collections.multimap.MultiTreeMapArrayListBased;
import com.helger.commons.io.IReadableResource;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.microdom.IMicroDocument;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.convert.MicroTypeConverter;
import com.helger.commons.microdom.impl.MicroDocument;
import com.helger.commons.microdom.serialize.MicroReader;
import com.helger.commons.mime.MimeTypeInfo.ExtensionWithSource;
import com.helger.commons.mime.MimeTypeInfo.MimeTypeWithSource;
import com.helger.commons.string.StringHelper;
import com.phloc.commons.annotations.ReturnsMutableCopy;

/**
 * This is the central manager for all {@link MimeTypeInfo} objects.
 *
 * @author Philip Helger
 */
public class MimeTypeInfoManager
{
  private static final MimeTypeInfoManager s_aDefaultInstance = new MimeTypeInfoManager ();

  static
  {
    s_aDefaultInstance.read (new ClassPathResource ("codelists/mime-type-info.xml"));
  }

  private final List <MimeTypeInfo> m_aList = new ArrayList <MimeTypeInfo> ();
  private final IMultiMapListBased <MimeType, MimeTypeInfo> m_aMapMimeType = new MultiTreeMapArrayListBased <MimeType, MimeTypeInfo> ();
  private final IMultiMapListBased <String, MimeTypeInfo> m_aMapExt = new MultiTreeMapArrayListBased <String, MimeTypeInfo> ();

  public MimeTypeInfoManager ()
  {}

  /**
   * @return The default instance that contains all predefined
   *         {@link MimeTypeInfo}s.
   */
  @Nonnull
  public static MimeTypeInfoManager getDefaultInstance ()
  {
    return s_aDefaultInstance;
  }

  public void read (@Nonnull final IReadableResource aRes)
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
  }

  @Nonnull
  @ReturnsMutableCopy
  public IMicroDocument getAsDocument ()
  {
    final IMicroDocument aDoc = new MicroDocument ();
    final IMicroElement eRoot = aDoc.appendElement ("mime-type-info");
    for (final MimeTypeInfo aInfo : ContainerHelper.getSorted (m_aList, new ComparatorMimeTypeInfoPrimaryMimeType ()))
      eRoot.appendChild (MicroTypeConverter.convertToMicroElement (aInfo, "item"));
    return aDoc;
  }

  public void registerMimeType (@Nonnull final MimeTypeInfo aInfo)
  {
    ValueEnforcer.notNull (aInfo, "Info");
    final Set <MimeTypeWithSource> aMimeTypes = aInfo.getAllMimeTypes ();
    final Set <ExtensionWithSource> aExtensions = aInfo.getAllExtensions ();

    // Check if MimeType is unique
    // Note: Extension must not be unique
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

    // Perform changes
    m_aList.add (aInfo);
    for (final MimeTypeWithSource aMimeType : aMimeTypes)
      m_aMapMimeType.putSingle (aMimeType.getMimeType (), aInfo);
    for (final ExtensionWithSource aExt : aExtensions)
      m_aMapExt.putSingle (aExt.getExtension (), aInfo);
  }

  public void addExtension (@Nonnull final MimeTypeInfo aInfo, @Nonnull final ExtensionWithSource aExt)
  {
    ValueEnforcer.notNull (aInfo, "Info");
    ValueEnforcer.notNull (aExt, "Ext");
    m_aMapExt.putSingle (aExt.getExtension (), aInfo);
    aInfo.addExtension (aExt);
  }

  @Nullable
  public List <MimeTypeInfo> getAllInfosOfExtension (@Nullable final String sExtension)
  {
    if (StringHelper.hasNoText (sExtension))
      return null;

    List <MimeTypeInfo> ret = m_aMapExt.get (sExtension);
    if (ret == null)
    {
      // Especially on Windows, sometimes file extensions like "JPG" can be
      // found. Therefore also test for the lowercase version of the extension.
      ret = m_aMapExt.get (sExtension.toLowerCase (Locale.US));
    }

    // Create a copy if present
    return ret == null ? null : ContainerHelper.newList (ret);
  }

  @Nullable
  public List <MimeTypeInfo> getAllInfosOfMimeType (@Nullable final MimeType aMimeType)
  {
    if (aMimeType == null)
      return null;

    final List <MimeTypeInfo> ret = m_aMapMimeType.get (aMimeType);

    // Create a copy if present
    return ret == null ? null : ContainerHelper.newList (ret);
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <MimeTypeInfo> getAllMimeTypeInfos ()
  {
    return ContainerHelper.newList (m_aList);
  }
}
