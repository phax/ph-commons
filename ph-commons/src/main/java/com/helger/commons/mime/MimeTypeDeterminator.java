/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.concurrent.ELockType;
import com.helger.annotation.concurrent.MustBeLocked;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.misc.Singleton;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.charset.EUnicodeBOM;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.ICommonsCollection;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.state.EChange;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Contains a basic set of MimeType determination method.
 *
 * @author Philip Helger
 */
@ThreadSafe
@Singleton
public final class MimeTypeDeterminator
{
  private static final class SingletonHolder
  {
    private static final MimeTypeDeterminator INSTANCE = new MimeTypeDeterminator ();
  }

  public static final IMimeType DEFAULT_MIME_TYPE = CMimeType.APPLICATION_OCTET_STREAM;

  private static final Logger LOGGER = LoggerFactory.getLogger (MimeTypeDeterminator.class);
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

  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();

  // Contains all byte[] to mime type mappings
  private final ICommonsSet <MimeTypeContent> m_aMimeTypeContents = new CommonsHashSet <> ();

  private MimeTypeDeterminator ()
  {
    _registerDefaultMimeTypeContents ();
  }

  @MustBeLocked (ELockType.WRITE)
  private void _registerDefaultMimeTypeContents ()
  {
    m_aMimeTypeContents.add (new MimeTypeContent (MIME_ID_GIF87A, false, CMimeType.IMAGE_GIF));
    m_aMimeTypeContents.add (new MimeTypeContent (MIME_ID_GIF89A, false, CMimeType.IMAGE_GIF));
    m_aMimeTypeContents.add (new MimeTypeContent (MIME_ID_JPG, false, CMimeType.IMAGE_JPG));
    m_aMimeTypeContents.add (new MimeTypeContent (MIME_ID_PNG, false, CMimeType.IMAGE_PNG));
    m_aMimeTypeContents.add (new MimeTypeContent (MIME_ID_TIFF_MOTOROLLA, false, CMimeType.IMAGE_TIFF));
    m_aMimeTypeContents.add (new MimeTypeContent (MIME_ID_TIFF_INTEL, false, CMimeType.IMAGE_TIFF));
    m_aMimeTypeContents.add (new MimeTypeContent (MIME_ID_PSD, false, CMimeType.IMAGE_PSD));
    m_aMimeTypeContents.add (new MimeTypeContent (MIME_ID_PDF, false, CMimeType.APPLICATION_PDF));
    m_aMimeTypeContents.add (new MimeTypeContent (MIME_ID_XLS, false, CMimeType.APPLICATION_MS_EXCEL));

    // Add all XML mime types: as the combination of all BOMs and all character
    // encodings as determined by
    // http://www.w3.org/TR/REC-xml/#sec-guessing
    final ICommonsList <byte []> aXMLStuff = new CommonsArrayList <> ();
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
    aXMLStuff.forEach (aXML -> registerMimeTypeContent (new MimeTypeContent (aXML, false, CMimeType.TEXT_XML)));

    // Register all type with the BOM
    for (final EUnicodeBOM eBOM : EUnicodeBOM.values ())
      for (final byte [] aXML : aXMLStuff)
      {
        final byte [] aData = ArrayHelper.getConcatenated (eBOM.getAllBytes (), aXML);
        registerMimeTypeContent (new MimeTypeContent (aData, false, CMimeType.TEXT_XML));
      }
  }

  public static boolean isInstantiated ()
  {
    return s_bDefaultInstantiated;
  }

  @Nonnull
  public static MimeTypeDeterminator getInstance ()
  {
    final MimeTypeDeterminator ret = SingletonHolder.INSTANCE;
    s_bDefaultInstantiated = true;
    return ret;
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

    return m_aRWLock.writeLockedGet ( () -> m_aMimeTypeContents.addObject (aMimeTypeContent));
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

    return m_aRWLock.writeLockedGet ( () -> m_aMimeTypeContents.removeObject (aMimeTypeContent));
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
  public IMimeType getMimeTypeFromString (@Nullable final String s, @Nonnull final Charset aCharset, @Nullable final IMimeType aDefault)
  {
    return getMimeTypeFromBytes (s == null ? null : s.getBytes (aCharset), aDefault);
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
   * @param aBytes
   *        The byte array to parse. May be <code>null</code> or empty.
   * @param aDefault
   *        The default MIME type to be returned, if no matching MIME type was
   *        found. May be <code>null</code>.
   * @return The supplied default value, if no matching MIME type was found. May
   *         be <code>null</code>.
   */
  @Nullable
  public IMimeType getMimeTypeFromBytes (@Nullable final byte [] aBytes, @Nullable final IMimeType aDefault)
  {
    if (aBytes == null || aBytes.length == 0)
      return aDefault;

    return m_aRWLock.readLockedGet ( () -> {
      for (final MimeTypeContent aMTC : m_aMimeTypeContents)
        if (aMTC.matchesBeginning (aBytes))
          return aMTC.getMimeType ();

      // default fallback
      return aDefault;
    });
  }

  /**
   * @return A copy of all registered {@link MimeTypeContent} objects. Never
   *         <code>null</code> but maybe empty.
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsCollection <MimeTypeContent> getAllMimeTypeContents ()
  {
    return m_aRWLock.readLockedGet (m_aMimeTypeContents::getClone);
  }

  /**
   * Reset the MimeTypeContent cache to the initial state.
   *
   * @see #registerMimeTypeContent(MimeTypeContent)
   * @see #unregisterMimeTypeContent(MimeTypeContent)
   */
  public void reinitialize ()
  {
    m_aRWLock.writeLocked ( () -> {
      m_aMimeTypeContents.clear ();
      _registerDefaultMimeTypeContents ();
    });

    if (LOGGER.isDebugEnabled ())
      LOGGER.debug ("Reinitialized " + MimeTypeDeterminator.class.getName ());
  }
}
