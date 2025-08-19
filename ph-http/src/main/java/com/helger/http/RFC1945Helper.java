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

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.array.ArrayHelper;
import com.helger.base.exception.InitializationException;
import com.helger.base.string.StringHelper;

import jakarta.annotation.Nullable;

/**
 * HTTP string helper. Based on RFC 1945 (HTTP/1.0) http://tools.ietf.org/html/rfc1945
 *
 * @author Philip Helger
 */
@Immutable
public final class RFC1945Helper
{
  /** Minimum index (inclusive) */
  public static final int MIN_INDEX = 0;
  /** Maximum index (inclusive) */
  public static final int MAX_INDEX = 127;
  /** Comment start character */
  public static final char COMMENT_BEGIN = '(';
  /** Comment end character */
  public static final char COMMENT_END = ')';
  /** Quoted text start character */
  public static final char QUOTEDTEXT_BEGIN = '"';
  /** Quoted text end character */
  public static final char QUOTEDTEXT_END = '"';

  public static final int CHAR_TAB = 9;
  public static final int CHAR_LF = 10;
  public static final int CHAR_CR = 13;
  public static final int CHAR_SPACE = 32;

  private static final int UALPHA = 0x00000001;
  private static final int LALPHA = 0x00000002;
  private static final int ALPHA = 0x00000004;
  private static final int DIGIT = 0x00000008;
  private static final int CTL = 0x00000010;
  private static final int LWS = 0x00000020;
  private static final int HEX = 0x00000040;
  private static final int LHEX = 0x00000080;
  private static final int NON_TOKEN = 0x00000100;
  private static final int NON_TEXT = 0x00000200;
  private static final int NON_COMMENT = 0x00000400;
  private static final int NON_QUOTEDTEXT = 0x00000800;
  private static final int RESERVED = 0x00001000;
  private static final int EXTRA = 0x00002000;
  private static final int SAFE = 0x00004000;
  private static final int UNSAFE = 0x00008000;

  private static final char [] MAPPINGS = {
                                            // 0x00
                                            CTL |
                                            NON_TEXT |
                                            UNSAFE,
                                            CTL | NON_TEXT | UNSAFE,
                                            CTL | NON_TEXT | UNSAFE,
                                            CTL | NON_TEXT | UNSAFE,
                                            CTL | NON_TEXT | UNSAFE,
                                            CTL | NON_TEXT | UNSAFE,
                                            CTL | NON_TEXT | UNSAFE,
                                            CTL | NON_TEXT | UNSAFE,
                                            CTL | NON_TEXT | UNSAFE,
                                            CTL | NON_TOKEN | UNSAFE | LWS,
                                            CTL | UNSAFE | LWS,
                                            CTL | NON_TEXT | UNSAFE,
                                            CTL | NON_TEXT | UNSAFE,
                                            CTL | UNSAFE | LWS,
                                            CTL | NON_TEXT | UNSAFE,
                                            CTL | NON_TEXT | UNSAFE,
                                            // 0x10
                                            CTL | NON_TEXT | UNSAFE,
                                            CTL | NON_TEXT | UNSAFE,
                                            CTL | NON_TEXT | UNSAFE,
                                            CTL | NON_TEXT | UNSAFE,
                                            CTL | NON_TEXT | UNSAFE,
                                            CTL | NON_TEXT | UNSAFE,
                                            CTL | NON_TEXT | UNSAFE,
                                            CTL | NON_TEXT | UNSAFE,
                                            CTL | NON_TEXT | UNSAFE,
                                            CTL | NON_TEXT | UNSAFE,
                                            CTL | NON_TEXT | UNSAFE,
                                            CTL | NON_TEXT | UNSAFE,
                                            CTL | NON_TEXT | UNSAFE,
                                            CTL | NON_TEXT | UNSAFE,
                                            CTL | NON_TEXT | UNSAFE,
                                            CTL | NON_TEXT | UNSAFE,
                                            // 0x20
                                            NON_TOKEN | UNSAFE | LWS,
                                            EXTRA,
                                            NON_TOKEN | NON_QUOTEDTEXT | UNSAFE,
                                            UNSAFE,
                                            SAFE,
                                            UNSAFE,
                                            RESERVED,
                                            EXTRA,
                                            NON_TOKEN | NON_COMMENT | EXTRA,
                                            NON_TOKEN | NON_COMMENT | EXTRA,
                                            EXTRA,
                                            RESERVED,
                                            NON_TOKEN | EXTRA,
                                            SAFE,
                                            SAFE,
                                            NON_TOKEN | RESERVED,
                                            // 0x30
                                            DIGIT | HEX | LHEX,
                                            DIGIT | HEX | LHEX,
                                            DIGIT | HEX | LHEX,
                                            DIGIT | HEX | LHEX,
                                            DIGIT | HEX | LHEX,
                                            DIGIT | HEX | LHEX,
                                            DIGIT | HEX | LHEX,
                                            DIGIT | HEX | LHEX,
                                            DIGIT | HEX | LHEX,
                                            DIGIT | HEX | LHEX,
                                            NON_TOKEN | RESERVED,
                                            NON_TOKEN | RESERVED,
                                            NON_TOKEN | UNSAFE,
                                            NON_TOKEN | RESERVED,
                                            NON_TOKEN | UNSAFE,
                                            NON_TOKEN | RESERVED,
                                            // 0x40
                                            NON_TOKEN | RESERVED,
                                            UALPHA | ALPHA | HEX,
                                            UALPHA | ALPHA | HEX,
                                            UALPHA | ALPHA | HEX,
                                            UALPHA | ALPHA | HEX,
                                            UALPHA | ALPHA | HEX,
                                            UALPHA | ALPHA | HEX,
                                            UALPHA | ALPHA,
                                            UALPHA | ALPHA,
                                            UALPHA | ALPHA,
                                            UALPHA | ALPHA,
                                            UALPHA | ALPHA,
                                            UALPHA | ALPHA,
                                            UALPHA | ALPHA,
                                            UALPHA | ALPHA,
                                            UALPHA | ALPHA,
                                            // 0x50
                                            UALPHA | ALPHA,
                                            UALPHA | ALPHA,
                                            UALPHA | ALPHA,
                                            UALPHA | ALPHA,
                                            UALPHA | ALPHA,
                                            UALPHA | ALPHA,
                                            UALPHA | ALPHA,
                                            UALPHA | ALPHA,
                                            UALPHA | ALPHA,
                                            UALPHA | ALPHA,
                                            UALPHA | ALPHA,
                                            NON_TOKEN,
                                            NON_TOKEN,
                                            NON_TOKEN,
                                            0,
                                            SAFE,
                                            // 0x60
                                            0,
                                            LALPHA | ALPHA | HEX | LHEX,
                                            LALPHA | ALPHA | HEX | LHEX,
                                            LALPHA | ALPHA | HEX | LHEX,
                                            LALPHA | ALPHA | HEX | LHEX,
                                            LALPHA | ALPHA | HEX | LHEX,
                                            LALPHA | ALPHA | HEX | LHEX,
                                            LALPHA | ALPHA,
                                            LALPHA | ALPHA,
                                            LALPHA | ALPHA,
                                            LALPHA | ALPHA,
                                            LALPHA | ALPHA,
                                            LALPHA | ALPHA,
                                            LALPHA | ALPHA,
                                            LALPHA | ALPHA,
                                            LALPHA | ALPHA,
                                            // 0x70
                                            LALPHA | ALPHA,
                                            LALPHA | ALPHA,
                                            LALPHA | ALPHA,
                                            LALPHA | ALPHA,
                                            LALPHA | ALPHA,
                                            LALPHA | ALPHA,
                                            LALPHA | ALPHA,
                                            LALPHA | ALPHA,
                                            LALPHA | ALPHA,
                                            LALPHA | ALPHA,
                                            LALPHA | ALPHA,
                                            NON_TOKEN,
                                            0,
                                            NON_TOKEN,
                                            0,
                                            CTL | NON_TEXT | UNSAFE };

  static
  {
    if (MAPPINGS.length != MAX_INDEX + 1)
      throw new InitializationException ("MAPPING array is invalid!");
  }

  @PresentForCodeCoverage
  private static final RFC1945Helper INSTANCE = new RFC1945Helper ();

  private RFC1945Helper ()
  {}

  public static boolean isChar (final int n)
  {
    return n >= MIN_INDEX && n <= MAX_INDEX;
  }

  public static boolean isOctet (final int n)
  {
    return n >= MIN_INDEX && n < 256;
  }

  public static boolean isUpperAlphaChar (final int n)
  {
    return isChar (n) && (MAPPINGS[n] & UALPHA) == UALPHA;
  }

  public static boolean isLowerAlphaChar (final int n)
  {
    return isChar (n) && (MAPPINGS[n] & LALPHA) == LALPHA;
  }

  public static boolean isAlphaChar (final int n)
  {
    return isChar (n) && (MAPPINGS[n] & ALPHA) == ALPHA;
  }

  public static boolean isDigitChar (final int n)
  {
    return isChar (n) && (MAPPINGS[n] & DIGIT) == DIGIT;
  }

  public static boolean isControlChar (final int n)
  {
    return isChar (n) && (MAPPINGS[n] & CTL) == CTL;
  }

  public static boolean isCRChar (final int n)
  {
    return n == CHAR_CR;
  }

  public static boolean isLFChar (final int n)
  {
    return n == CHAR_LF;
  }

  public static boolean isSpaceChar (final int n)
  {
    return n == CHAR_SPACE;
  }

  public static boolean isTabChar (final int n)
  {
    return n == CHAR_TAB;
  }

  public static boolean isLinearWhitespaceChar (final int n)
  {
    return isChar (n) && (MAPPINGS[n] & LWS) == LWS;
  }

  public static boolean isQuoteChar (final int n)
  {
    return n == 34;
  }

  public static boolean isHexChar (final int n)
  {
    return isChar (n) && (MAPPINGS[n] & HEX) == HEX;
  }

  public static boolean isHexNotEmpty (@Nullable final char [] aChars)
  {
    if (ArrayHelper.isEmpty (aChars))
      return false;
    for (final char c : aChars)
      if (!isHexChar (c))
        return false;
    return true;
  }

  public static boolean isHexNotEmpty (@Nullable final String sStr)
  {
    if (StringHelper.isEmpty (sStr))
      return false;
    return isHexNotEmpty (sStr.toCharArray ());
  }

  public static boolean isLowerHexChar (final int n)
  {
    return isChar (n) && (MAPPINGS[n] & LHEX) == LHEX;
  }

  public static boolean isLowerHexNotEmpty (@Nullable final char [] aChars)
  {
    if (ArrayHelper.isEmpty (aChars))
      return false;
    for (final char c : aChars)
      if (!isLowerHexChar (c))
        return false;
    return true;
  }

  public static boolean isLowerHexNotEmpty (@Nullable final String sStr)
  {
    if (StringHelper.isEmpty (sStr))
      return false;
    return isLowerHexNotEmpty (sStr.toCharArray ());
  }

  public static boolean isNonTokenChar (final int n)
  {
    return isChar (n) && (MAPPINGS[n] & NON_TOKEN) == NON_TOKEN;
  }

  public static boolean isTokenChar (final int n)
  {
    return isChar (n) && (MAPPINGS[n] & (CTL | NON_TOKEN)) == 0;
  }

  public static boolean isToken (@Nullable final char [] aChars)
  {
    if (ArrayHelper.isEmpty (aChars))
      return false;
    for (final char c : aChars)
      if (!isTokenChar (c))
        return false;
    return true;
  }

  public static boolean isToken (@Nullable final String sStr)
  {
    if (StringHelper.isEmpty (sStr))
      return false;
    return isToken (sStr.toCharArray ());
  }

  public static boolean isTextChar (final int n)
  {
    if (n < MIN_INDEX)
      return false;
    // Any octet allowed!
    if (n > MAX_INDEX)
      return n < 256;
    return (MAPPINGS[n] & NON_TEXT) == 0;
  }

  public static boolean isCommentChar (final int n)
  {
    if (n < MIN_INDEX)
      return false;
    // Any octet allowed!
    if (n > MAX_INDEX)
      return n < 256;
    return (MAPPINGS[n] & (NON_TEXT | NON_COMMENT)) == 0;
  }

  public static boolean isComment (@Nullable final char [] aChars)
  {
    if (ArrayHelper.getSize (aChars) < 2 || aChars[0] != COMMENT_BEGIN || aChars[aChars.length - 1] != COMMENT_END)
      return false;
    for (int i = 1; i < aChars.length - 1; ++i)
      if (!isCommentChar (aChars[i]))
        return false;
    return true;
  }

  public static boolean isComment (@Nullable final String sStr)
  {
    if (StringHelper.getLength (sStr) < 2)
      return false;
    return isComment (sStr.toCharArray ());
  }

  public static boolean isQuotedTextChar (final int n)
  {
    return isChar (n) && (MAPPINGS[n] & (NON_TEXT | NON_QUOTEDTEXT)) == 0;
  }

  public static boolean isQuotedText (@Nullable final char [] aChars)
  {
    if (ArrayHelper.getSize (aChars) < 2 ||
        aChars[0] != QUOTEDTEXT_BEGIN ||
        aChars[aChars.length - 1] != QUOTEDTEXT_END)
      return false;
    for (int i = 1; i < aChars.length - 1; ++i)
      if (!isQuotedTextChar (aChars[i]))
        return false;
    return true;
  }

  public static boolean isQuotedText (@Nullable final String sStr)
  {
    if (StringHelper.getLength (sStr) < 2)
      return false;
    return isQuotedText (sStr.toCharArray ());
  }

  @Nullable
  public static String getQuotedTextString (@Nullable final String sStr)
  {
    if (sStr == null)
      return null;
    return QUOTEDTEXT_BEGIN + sStr + QUOTEDTEXT_END;
  }

  public static boolean isQuotedTextContent (@Nullable final char [] aChars)
  {
    if (aChars == null)
      return false;
    for (final char c : aChars)
      if (!isQuotedTextChar (c))
        return false;
    return true;
  }

  public static boolean isQuotedTextContent (@Nullable final String sStr)
  {
    if (sStr == null)
      return false;
    return isQuotedTextContent (sStr.toCharArray ());
  }

  public static boolean isWord (@Nullable final char [] aChars)
  {
    return isToken (aChars) || isQuotedText (aChars);
  }

  public static boolean isWord (@Nullable final String sStr)
  {
    if (StringHelper.isEmpty (sStr))
      return false;
    return isWord (sStr.toCharArray ());
  }

  public static boolean isReservedChar (final int n)
  {
    // ";" | "/" | "?" | ":" | "@" | "&" | "=" | "+"
    return isChar (n) && (MAPPINGS[n] & RESERVED) == RESERVED;
  }

  public static boolean isExtraChar (final int n)
  {
    // "!" | "*" | "'" | "(" | ")" | ","
    return isChar (n) && (MAPPINGS[n] & EXTRA) == EXTRA;
  }

  public static boolean isSafeChar (final int n)
  {
    // "$" | "-" | "_" | "."
    return isChar (n) && (MAPPINGS[n] & SAFE) == SAFE;
  }

  public static boolean isUnsafeChar (final int n)
  {
    // CTL | SP | <"> | "#" | "%" | "<" | ">"
    return isChar (n) && (MAPPINGS[n] & UNSAFE) == UNSAFE;
  }

  public static boolean isNationalChar (final int n)
  {
    // <any OCTET excluding ALPHA, DIGIT, reserved, extra, safe, and unsafe>
    if (n < MIN_INDEX)
      return false;
    // Any octet allowed!
    if (n > MAX_INDEX)
      return n < 256;
    return (MAPPINGS[n] & (ALPHA | DIGIT | RESERVED | EXTRA | SAFE | UNSAFE)) == 0;
  }

  public static boolean isUnreservedChar (final int n)
  {
    // ALPHA | DIGIT | safe | extra | national
    if (n < MIN_INDEX)
      return false;
    // Any octet allowed!
    if (n > MAX_INDEX)
      return n < 256;
    final int nMapping = MAPPINGS[n];
    return (nMapping & (ALPHA | DIGIT | RESERVED | EXTRA | SAFE | UNSAFE)) == 0 ||
           (nMapping & (ALPHA | DIGIT | EXTRA | SAFE)) != 0;
  }
}
