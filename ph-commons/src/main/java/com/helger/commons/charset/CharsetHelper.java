/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.commons.charset;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.SortedMap;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillNotClose;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.CodingStyleguideUnaware;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.io.IHasInputStream;
import com.helger.commons.io.stream.NonBlockingPushbackInputStream;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.string.StringHelper;

/**
 * Whole lotta charset management routines.
 *
 * @author Philip Helger
 */
@Immutable
public final class CharsetHelper
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (CharsetHelper.class);

  @CodingStyleguideUnaware
  private static final SortedMap <String, Charset> s_aAllCharsets;

  static
  {
    // Returns an immutable object
    s_aAllCharsets = Charset.availableCharsets ();
  }

  @PresentForCodeCoverage
  private static final CharsetHelper s_aInstance = new CharsetHelper ();

  private CharsetHelper ()
  {}

  /**
   * Resolve the charset by the specified name. The difference to
   * {@link Charset#forName(String)} is, that this method has no checked
   * exceptions but only unchecked exceptions.
   *
   * @param sCharsetName
   *        The charset to be resolved. May neither be <code>null</code> nor
   *        empty.
   * @return The Charset object
   * @throws IllegalArgumentException
   *         If the charset could not be resolved.
   */
  @Nonnull
  public static Charset getCharsetFromName (@Nonnull @Nonempty final String sCharsetName)
  {
    ValueEnforcer.notNull (sCharsetName, "CharsetName");
    try
    {
      return Charset.forName (sCharsetName);
    }
    catch (final IllegalCharsetNameException ex)
    {
      // Not supported in any version
      throw new IllegalArgumentException ("Charset '" + sCharsetName + "' unsupported in Java", ex);
    }
    catch (final UnsupportedCharsetException ex)
    {
      // Unsupported on this platform
      throw new IllegalArgumentException ("Charset '" + sCharsetName + "' unsupported on this platform", ex);
    }
  }

  /**
   * Resolve the charset by the specified name. The difference to
   * {@link Charset#forName(String)} is, that this method throws no exceptions.
   *
   * @param sCharsetName
   *        The charset to be resolved. May be <code>null</code> or empty.
   * @return The Charset object or <code>null</code> if no such charset was
   *         found.
   */
  @Nullable
  public static Charset getCharsetFromNameOrNull (@Nullable final String sCharsetName)
  {
    return getCharsetFromNameOrDefault (sCharsetName, null);
  }

  /**
   * Resolve the charset by the specified name. The difference to
   * {@link Charset#forName(String)} is, that this method throws no exceptions.
   *
   * @param sCharsetName
   *        The charset to be resolved. May be <code>null</code> or empty.
   * @param aDefault
   *        the default charset to be returned if none is provided. May be
   *        <code>null</code>.
   * @return The Charset object or the provided default if no such charset was
   *         found.
   * @deprecated Use {@link #getCharsetFromNameOrDefault(String,Charset)}
   *             instead
   */
  @Deprecated
  @Nullable
  public static Charset getCharsetFromNameOrNull (@Nullable final String sCharsetName, @Nullable final Charset aDefault)
  {
    return getCharsetFromNameOrDefault (sCharsetName, aDefault);
  }

  /**
   * Resolve the charset by the specified name. The difference to
   * {@link Charset#forName(String)} is, that this method throws no exceptions.
   *
   * @param sCharsetName
   *        The charset to be resolved. May be <code>null</code> or empty.
   * @param aDefault
   *        the default charset to be returned if none is provided. May be
   *        <code>null</code>.
   * @return The Charset object or the provided default if no such charset was
   *         found.
   */
  @Nullable
  public static Charset getCharsetFromNameOrDefault (@Nullable final String sCharsetName,
                                                     @Nullable final Charset aDefault)
  {
    if (StringHelper.hasText (sCharsetName))
      try
      {
        return getCharsetFromName (sCharsetName);
      }
      catch (final IllegalArgumentException ex)
      {
        // Fall through
      }
    return aDefault;
  }

  /**
   * @return An immutable collection of all available charsets from the standard
   *         charset provider.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsOrderedMap <String, Charset> getAllCharsets ()
  {
    return new CommonsLinkedHashMap <> (s_aAllCharsets);
  }

  @Nullable
  public static String getAsStringInOtherCharset (@Nullable final String sText,
                                                  @Nonnull final Charset aCurrentCharset,
                                                  @Nonnull final Charset aNewCharset)
  {
    ValueEnforcer.notNull (aCurrentCharset, "CurrentCharset");
    ValueEnforcer.notNull (aNewCharset, "NewCharset");

    if (sText == null || aCurrentCharset.equals (aNewCharset))
      return sText;

    return new String (sText.getBytes (aCurrentCharset), aNewCharset);
  }

  /**
   * Get the number of bytes necessary to represent the passed string as an
   * UTF-8 string.
   *
   * @param s
   *        The string to count the length. May be <code>null</code> or empty.
   * @return A non-negative value.
   */
  @Nonnegative
  public static int getUTF8ByteCount (@Nullable final String s)
  {
    return s == null ? 0 : getUTF8ByteCount (s.toCharArray ());
  }

  /**
   * Get the number of bytes necessary to represent the passed char array as an
   * UTF-8 string.
   *
   * @param aChars
   *        The characters to count the length. May be <code>null</code> or
   *        empty.
   * @return A non-negative value.
   */
  @Nonnegative
  public static int getUTF8ByteCount (@Nullable final char [] aChars)
  {
    int nCount = 0;
    if (aChars != null)
      for (final char c : aChars)
        nCount += getUTF8ByteCount (c);
    return nCount;
  }

  @Nonnegative
  public static int getUTF8ByteCount (final char c)
  {
    return getUTF8ByteCount ((int) c);
  }

  /**
   * Get the number of bytes necessary to represent the passed character.
   *
   * @param c
   *        The character to be evaluated.
   * @return A non-negative value.
   */
  @Nonnegative
  public static int getUTF8ByteCount (@Nonnegative final int c)
  {
    ValueEnforcer.isBetweenInclusive (c, "c", Character.MIN_VALUE, Character.MAX_VALUE);

    // see JVM spec 4.4.7, p 111
    // http://java.sun.com/docs/books/jvms/second_edition/html/ClassFile.doc.html
    // #1297
    if (c == 0)
      return 2;

    // Source: http://icu-project.org/apiref/icu4c/utf8_8h_source.html
    if (c <= 0x7f)
      return 1;
    if (c <= 0x7ff)
      return 2;
    if (c <= 0xd7ff)
      return 3;

    // It's a surrogate...
    return 0;
  }

  public static final class InputStreamAndCharset implements IHasInputStream
  {
    private final InputStream m_aIS;
    private final EUnicodeBOM m_eBOM;
    private final Charset m_aCharset;

    public InputStreamAndCharset (@Nonnull final InputStream aIS,
                                  @Nullable final EUnicodeBOM eBOM,
                                  @Nullable final Charset aCharset)
    {
      m_aIS = aIS;
      m_eBOM = eBOM;
      m_aCharset = aCharset;
    }

    @Nonnull
    public InputStream getInputStream ()
    {
      return m_aIS;
    }

    public boolean isReadMultiple ()
    {
      return false;
    }

    @Nullable
    public EUnicodeBOM getBOM ()
    {
      return m_eBOM;
    }

    public boolean hasBOM ()
    {
      return m_eBOM != null;
    }

    @Nullable
    public Charset getCharset ()
    {
      return m_aCharset;
    }

    public boolean hasCharset ()
    {
      return m_aCharset != null;
    }

    @Nullable
    public Charset getCharset (@Nullable final Charset aFallbackCharset)
    {
      return m_aCharset != null ? m_aCharset : aFallbackCharset;
    }
  }

  /**
   * If a BOM is present in the {@link InputStream} it is read and if possible
   * the charset is automatically determined from the BOM.
   *
   * @param aIS
   *        The input stream to use. May not be <code>null</code>.
   * @return Never <code>null</code>. Always use the input stream contained in
   *         the returned object and never the one passed in as a parameter,
   *         because the returned IS is a push-back InputStream that has a
   *         couple of bytes already buffered!
   */
  @Nonnull
  public static InputStreamAndCharset getInputStreamAndCharsetFromBOM (@Nonnull @WillNotClose final InputStream aIS)
  {
    ValueEnforcer.notNull (aIS, "InputStream");

    // Check for BOM
    final int nMaxBOMBytes = EUnicodeBOM.getMaximumByteCount ();
    final NonBlockingPushbackInputStream aPIS = new NonBlockingPushbackInputStream (StreamHelper.getBuffered (aIS),
                                                                                    nMaxBOMBytes);
    try
    {
      // Try to read as many bytes as necessary to determine all supported BOMs
      final byte [] aBOM = new byte [nMaxBOMBytes];
      final int nReadBOMBytes = aPIS.read (aBOM);
      EUnicodeBOM eBOM = null;
      Charset aDeterminedCharset = null;
      if (nReadBOMBytes > 0)
      {
        // Some byte BOMs were read - determine
        eBOM = EUnicodeBOM.getFromBytesOrNull (ArrayHelper.getCopy (aBOM, 0, nReadBOMBytes));
        if (eBOM == null)
        {
          // Unread the whole BOM
          aPIS.unread (aBOM, 0, nReadBOMBytes);
          // aDeterminedCharset stays null
        }
        else
        {
          if (s_aLogger.isDebugEnabled ())
            s_aLogger.debug ("Found " + eBOM + " on " + aIS.getClass ().getName ());

          // Unread the unnecessary parts of the BOM
          final int nBOMBytes = eBOM.getByteCount ();
          if (nBOMBytes < nReadBOMBytes)
            aPIS.unread (aBOM, nBOMBytes, nReadBOMBytes - nBOMBytes);

          // Use the Charset of the BOM - maybe null!
          aDeterminedCharset = eBOM.getCharset ();
        }
      }
      return new InputStreamAndCharset (aPIS, eBOM, aDeterminedCharset);
    }
    catch (final IOException ex)
    {
      s_aLogger.error ("Failed to determine BOM", ex);
      throw new UncheckedIOException (ex);
    }
  }

  @Nonnull
  public static InputStreamReader getReaderByBOM (@Nonnull final InputStream aIS,
                                                  @Nonnull final Charset aFallbackCharset)
  {
    ValueEnforcer.notNull (aIS, "InputStream");
    ValueEnforcer.notNull (aFallbackCharset, "FallbackCharset");

    // Determine BOM/Charset
    final InputStreamAndCharset aISAndBOM = getInputStreamAndCharsetFromBOM (aIS);

    // Create the reader with the current position of the InputStream and the
    // correct charset
    final Charset aEffectiveCharset = aISAndBOM.getCharset (aFallbackCharset);
    return StreamHelper.createReader (aISAndBOM.getInputStream (), aEffectiveCharset);
  }
}
