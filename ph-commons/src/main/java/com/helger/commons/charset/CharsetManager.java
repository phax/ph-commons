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
package com.helger.commons.charset;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.SortedMap;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.annotation.ReturnsImmutableObject;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.string.StringHelper;

/**
 * Whole lotta charset management routines.
 *
 * @author Philip Helger
 */
@Immutable
public final class CharsetManager
{
  private static final SortedMap <String, Charset> s_aAllCharsets;

  static
  {
    // Returns an immutable object
    s_aAllCharsets = Charset.availableCharsets ();
  }

  @PresentForCodeCoverage
  private static final CharsetManager s_aInstance = new CharsetManager ();

  private CharsetManager ()
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
    if (StringHelper.hasText (sCharsetName))
      try
      {
        return getCharsetFromName (sCharsetName);
      }
      catch (final IllegalArgumentException ex)
      {
        // Fall through
      }
    return null;
  }

  /**
   * @return An immutable collection of all available charsets from the standard
   *         charset provider.
   */
  @Nonnull
  @ReturnsImmutableObject
  public static SortedMap <String, Charset> getAllCharsets ()
  {
    return s_aAllCharsets;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static byte [] getAsBytes (@Nonnull final String sText, @Nonnull final Charset aCharset)
  {
    ValueEnforcer.notNull (sText, "Text");
    ValueEnforcer.notNull (aCharset, "Charset");
    if (!aCharset.canEncode ())
      throw new IllegalArgumentException ("Cannot encode to " + aCharset);

    return sText.getBytes (aCharset);
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

    return getAsString (getAsBytes (sText, aCurrentCharset), aNewCharset);
  }

  @Nonnull
  public static String getAsString (@Nonnull final byte [] aBuffer, @Nonnull final Charset aCharset)
  {
    ValueEnforcer.notNull (aBuffer, "Buffer");

    return getAsString (aBuffer, 0, aBuffer.length, aCharset);
  }

  @Nonnull
  public static String getAsString (@Nonnull final byte [] aBuffer,
                                    @Nonnegative final int nOfs,
                                    @Nonnegative final int nLength,
                                    @Nonnull final Charset aCharset)
  {
    ValueEnforcer.isArrayOfsLen (aBuffer, nOfs, nLength);
    ValueEnforcer.notNull (aCharset, "Charset");

    return new String (aBuffer, nOfs, nLength, aCharset);
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
}
