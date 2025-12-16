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
package com.helger.http;

import java.util.BitSet;

import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.codec.RFC5234Helper;
import com.helger.base.string.StringHelper;

/**
 * Helper class for RFC 7230
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

    return isValidToken (s.toCharArray ());
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

    for (final char c : a)
      if (!isValidTokenChar (c))
        return false;
    return true;
  }

  public static boolean isBackslash (final char c)
  {
    return c == '\\';
  }
}
