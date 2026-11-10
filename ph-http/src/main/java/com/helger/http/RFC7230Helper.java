/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.http;

import java.nio.CharBuffer;
import java.util.BitSet;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.codec.RFC5234Helper;
import com.helger.base.string.StringHelper;

/**
 * Helper class for RFC 7230 (Hypertext Transfer Protocol (HTTP/1.1): Message Syntax and Routing)
 *
 * @author Philip Helger
 * @since 10.5.1
 */
@Immutable
public final class RFC7230Helper
{
  private static final BitSet TOKEN_CHARS = new BitSet (256);

  static
  {
    for (int i = RFC5234Helper.CHECK_RANGE_MIN_INCL; i <= RFC5234Helper.CHECK_RANGE_MAX_INCL; ++i)
      if (RFC5234Helper.isDigit (i) || RFC5234Helper.isAlpha (i))
        TOKEN_CHARS.set (i);
    TOKEN_CHARS.set ('!');
    TOKEN_CHARS.set ('#');
    TOKEN_CHARS.set ('$');
    TOKEN_CHARS.set ('%');
    TOKEN_CHARS.set ('&');
    TOKEN_CHARS.set ('\'');
    TOKEN_CHARS.set ('*');
    TOKEN_CHARS.set ('+');
    TOKEN_CHARS.set ('-');
    TOKEN_CHARS.set ('.');
    TOKEN_CHARS.set ('^');
    TOKEN_CHARS.set ('_');
    TOKEN_CHARS.set ('`');
    TOKEN_CHARS.set ('|');
    TOKEN_CHARS.set ('~');
  }

  @PresentForCodeCoverage
  private static final RFC7230Helper INSTANCE = new RFC7230Helper ();

  private RFC7230Helper ()
  {}

  /**
   * Check if the provided char is a valid token char.
   *
   * @param c
   *        character to check
   * @return <code>true</code> if it is a valid token, <code>false</code> if not.
   */
  public static boolean isValidTokenChar (final char c)
  {
    return TOKEN_CHARS.get (c);
  }

  /**
   * Check if all characters of the provided character sequence are valid token chars.
   *
   * @param aCS
   *        Character sequence to check. May not be <code>null</code>.
   * @return <code>true</code> if all characters are valid token chars, <code>false</code> if not.
   */
  private static boolean _isValidToken (@NonNull final CharSequence aCS)
  {
    final int nLen = aCS.length ();
    for (int i = 0; i < nLen; ++i)
      if (!isValidTokenChar (aCS.charAt (i)))
        return false;
    return true;
  }

  /**
   * Check if the provided String is a valid token.
   *
   * @param s
   *        String to check
   * @return <code>true</code> if it is a valid token, <code>false</code> if not.
   */
  public static boolean isValidToken (@Nullable final String s)
  {
    if (StringHelper.isEmpty (s))
      return false;

    return _isValidToken (s);
  }

  /**
   * Check if the provided char array is a valid token.
   *
   * @param a
   *        Character array to check
   * @return <code>true</code> if it is a valid token, <code>false</code> if not.
   */
  public static boolean isValidToken (final char @Nullable [] a)
  {
    if (a == null || a.length == 0)
      return false;

    return _isValidToken (CharBuffer.wrap (a));
  }

  /**
   * Check if the provided char is a backslash character.
   *
   * @param c
   *        The character to check.
   * @return <code>true</code> if the character is a backslash, <code>false</code> if not.
   */
  public static boolean isBackslash (final char c)
  {
    return c == '\\';
  }
}
