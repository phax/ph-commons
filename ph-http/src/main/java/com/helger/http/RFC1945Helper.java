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

import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.array.ArrayHelper;
import com.helger.base.exception.InitializationException;
import com.helger.base.string.StringHelper;

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

  /**
   * Check if the passed value is a valid CHAR as defined in RFC 1945.
   *
   * @param n
   *        The value to check.
   * @return <code>true</code> if the value is in the range {@link #MIN_INDEX} to
   *         {@link #MAX_INDEX}.
   */
  public static boolean isChar (final int n)
  {
    return n >= MIN_INDEX && n <= MAX_INDEX;
  }

  /**
   * Check if the passed value is a valid OCTET as defined in RFC 1945.
   *
   * @param n
   *        The value to check.
   * @return <code>true</code> if the value is in the range 0 to 255.
   */
  public static boolean isOctet (final int n)
  {
    return n >= MIN_INDEX && n < 256;
  }

  /**
   * Check if the passed value is an upper-case alpha character (A-Z).
   *
   * @param n
   *        The value to check.
   * @return <code>true</code> if the value is an upper-case alpha character.
   */
  public static boolean isUpperAlphaChar (final int n)
  {
    return isChar (n) && (MAPPINGS[n] & UALPHA) == UALPHA;
  }

  /**
   * Check if the passed value is a lower-case alpha character (a-z).
   *
   * @param n
   *        The value to check.
   * @return <code>true</code> if the value is a lower-case alpha character.
   */
  public static boolean isLowerAlphaChar (final int n)
  {
    return isChar (n) && (MAPPINGS[n] & LALPHA) == LALPHA;
  }

  /**
   * Check if the passed value is an alpha character (A-Z or a-z).
   *
   * @param n
   *        The value to check.
   * @return <code>true</code> if the value is an alpha character.
   */
  public static boolean isAlphaChar (final int n)
  {
    return isChar (n) && (MAPPINGS[n] & ALPHA) == ALPHA;
  }

  /**
   * Check if the passed value is a digit character (0-9).
   *
   * @param n
   *        The value to check.
   * @return <code>true</code> if the value is a digit character.
   */
  public static boolean isDigitChar (final int n)
  {
    return isChar (n) && (MAPPINGS[n] & DIGIT) == DIGIT;
  }

  /**
   * Check if the passed value is a control character (CTL).
   *
   * @param n
   *        The value to check.
   * @return <code>true</code> if the value is a control character.
   */
  public static boolean isControlChar (final int n)
  {
    return isChar (n) && (MAPPINGS[n] & CTL) == CTL;
  }

  /**
   * Check if the passed value is a carriage return character (CR).
   *
   * @param n
   *        The value to check.
   * @return <code>true</code> if the value is a CR character.
   */
  public static boolean isCRChar (final int n)
  {
    return n == CHAR_CR;
  }

  /**
   * Check if the passed value is a line feed character (LF).
   *
   * @param n
   *        The value to check.
   * @return <code>true</code> if the value is a LF character.
   */
  public static boolean isLFChar (final int n)
  {
    return n == CHAR_LF;
  }

  /**
   * Check if the passed value is a space character (SP).
   *
   * @param n
   *        The value to check.
   * @return <code>true</code> if the value is a space character.
   */
  public static boolean isSpaceChar (final int n)
  {
    return n == CHAR_SPACE;
  }

  /**
   * Check if the passed value is a horizontal tab character (HT).
   *
   * @param n
   *        The value to check.
   * @return <code>true</code> if the value is a tab character.
   */
  public static boolean isTabChar (final int n)
  {
    return n == CHAR_TAB;
  }

  /**
   * Check if the passed value is a linear whitespace character (LWS), which includes CR, LF, SP and
   * HT.
   *
   * @param n
   *        The value to check.
   * @return <code>true</code> if the value is a linear whitespace character.
   */
  public static boolean isLinearWhitespaceChar (final int n)
  {
    return isChar (n) && (MAPPINGS[n] & LWS) == LWS;
  }

  /**
   * Check if the passed value is a double-quote character.
   *
   * @param n
   *        The value to check.
   * @return <code>true</code> if the value is a double-quote character.
   */
  public static boolean isQuoteChar (final int n)
  {
    return n == 34;
  }

  /**
   * Check if the passed value is a hexadecimal character (0-9, A-F, a-f).
   *
   * @param n
   *        The value to check.
   * @return <code>true</code> if the value is a hex character.
   */
  public static boolean isHexChar (final int n)
  {
    return isChar (n) && (MAPPINGS[n] & HEX) == HEX;
  }

  /**
   * Check if the passed character array is non-empty and consists only of hex characters.
   *
   * @param aChars
   *        The character array to check. May be <code>null</code>.
   * @return <code>true</code> if the array is non-empty and all characters are hex characters.
   */
  public static boolean isHexNotEmpty (final char @Nullable [] aChars)
  {
    if (ArrayHelper.isEmpty (aChars))
      return false;
    for (final char c : aChars)
      if (!isHexChar (c))
        return false;
    return true;
  }

  /**
   * Check if the passed string is non-empty and consists only of hex characters.
   *
   * @param sStr
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the string is non-empty and all characters are hex characters.
   */
  public static boolean isHexNotEmpty (@Nullable final String sStr)
  {
    if (StringHelper.isEmpty (sStr))
      return false;
    return isHexNotEmpty (sStr.toCharArray ());
  }

  /**
   * Check if the passed value is a lower-case hexadecimal character (0-9, a-f).
   *
   * @param n
   *        The value to check.
   * @return <code>true</code> if the value is a lower-case hex character.
   */
  public static boolean isLowerHexChar (final int n)
  {
    return isChar (n) && (MAPPINGS[n] & LHEX) == LHEX;
  }

  /**
   * Check if the passed character array is non-empty and consists only of lower-case hex characters.
   *
   * @param aChars
   *        The character array to check. May be <code>null</code>.
   * @return <code>true</code> if the array is non-empty and all characters are lower-case hex
   *         characters.
   */
  public static boolean isLowerHexNotEmpty (final char @Nullable [] aChars)
  {
    if (ArrayHelper.isEmpty (aChars))
      return false;
    for (final char c : aChars)
      if (!isLowerHexChar (c))
        return false;
    return true;
  }

  /**
   * Check if the passed string is non-empty and consists only of lower-case hex characters.
   *
   * @param sStr
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the string is non-empty and all characters are lower-case hex
   *         characters.
   */
  public static boolean isLowerHexNotEmpty (@Nullable final String sStr)
  {
    if (StringHelper.isEmpty (sStr))
      return false;
    return isLowerHexNotEmpty (sStr.toCharArray ());
  }

  /**
   * Check if the passed value is a non-token character (separator).
   *
   * @param n
   *        The value to check.
   * @return <code>true</code> if the value is a non-token character.
   */
  public static boolean isNonTokenChar (final int n)
  {
    return isChar (n) && (MAPPINGS[n] & NON_TOKEN) == NON_TOKEN;
  }

  /**
   * Check if the passed value is a valid token character as defined in RFC 1945.
   *
   * @param n
   *        The value to check.
   * @return <code>true</code> if the value is a valid token character.
   */
  public static boolean isTokenChar (final int n)
  {
    return isChar (n) && (MAPPINGS[n] & (CTL | NON_TOKEN)) == 0;
  }

  /**
   * Check if the passed character array represents a valid token as defined in RFC 1945.
   *
   * @param aChars
   *        The character array to check. May be <code>null</code>.
   * @return <code>true</code> if the array is non-empty and all characters are valid token
   *         characters.
   */
  public static boolean isToken (final char @Nullable [] aChars)
  {
    if (ArrayHelper.isEmpty (aChars))
      return false;
    for (final char c : aChars)
      if (!isTokenChar (c))
        return false;
    return true;
  }

  /**
   * Check if the passed string represents a valid token as defined in RFC 1945.
   *
   * @param sStr
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the string is non-empty and all characters are valid token
   *         characters.
   */
  public static boolean isToken (@Nullable final String sStr)
  {
    if (StringHelper.isEmpty (sStr))
      return false;
    return isToken (sStr.toCharArray ());
  }

  /**
   * Check if the passed value is a valid TEXT character as defined in RFC 1945.
   *
   * @param n
   *        The value to check.
   * @return <code>true</code> if the value is a valid TEXT character.
   */
  public static boolean isTextChar (final int n)
  {
    if (n < MIN_INDEX)
      return false;
    // Any octet allowed!
    if (n > MAX_INDEX)
      return n < 256;
    return (MAPPINGS[n] & NON_TEXT) == 0;
  }

  /**
   * Check if the passed value is a valid comment character as defined in RFC 1945.
   *
   * @param n
   *        The value to check.
   * @return <code>true</code> if the value is a valid comment character.
   */
  public static boolean isCommentChar (final int n)
  {
    if (n < MIN_INDEX)
      return false;
    // Any octet allowed!
    if (n > MAX_INDEX)
      return n < 256;
    return (MAPPINGS[n] & (NON_TEXT | NON_COMMENT)) == 0;
  }

  /**
   * Check if the passed character array represents a valid comment as defined in RFC 1945. A
   * comment is enclosed in parentheses.
   *
   * @param aChars
   *        The character array to check. May be <code>null</code>.
   * @return <code>true</code> if the array represents a valid comment.
   */
  public static boolean isComment (final char @Nullable [] aChars)
  {
    if (ArrayHelper.getSize (aChars) < 2 || aChars[0] != COMMENT_BEGIN || aChars[aChars.length - 1] != COMMENT_END)
      return false;
    for (int i = 1; i < aChars.length - 1; ++i)
      if (!isCommentChar (aChars[i]))
        return false;
    return true;
  }

  /**
   * Check if the passed string represents a valid comment as defined in RFC 1945. A comment is
   * enclosed in parentheses.
   *
   * @param sStr
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the string represents a valid comment.
   */
  public static boolean isComment (@Nullable final String sStr)
  {
    if (StringHelper.getLength (sStr) < 2)
      return false;
    return isComment (sStr.toCharArray ());
  }

  /**
   * Check if the passed value is a valid quoted-text character as defined in RFC 1945.
   *
   * @param n
   *        The value to check.
   * @return <code>true</code> if the value is a valid quoted-text character.
   */
  public static boolean isQuotedTextChar (final int n)
  {
    return isChar (n) && (MAPPINGS[n] & (NON_TEXT | NON_QUOTEDTEXT)) == 0;
  }

  /**
   * Check if the passed character array represents a valid quoted-text as defined in RFC 1945.
   * Quoted text is enclosed in double-quote characters.
   *
   * @param aChars
   *        The character array to check. May be <code>null</code>.
   * @return <code>true</code> if the array represents a valid quoted-text.
   */
  public static boolean isQuotedText (final char @Nullable [] aChars)
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

  /**
   * Check if the passed string represents a valid quoted-text as defined in RFC 1945. Quoted text
   * is enclosed in double-quote characters.
   *
   * @param sStr
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the string represents a valid quoted-text.
   */
  public static boolean isQuotedText (@Nullable final String sStr)
  {
    if (StringHelper.getLength (sStr) < 2)
      return false;
    return isQuotedText (sStr.toCharArray ());
  }

  /**
   * Wrap the passed string in double-quote characters to form a quoted-text value.
   *
   * @param sStr
   *        The string to quote. May be <code>null</code>.
   * @return The quoted string or <code>null</code> if the input was <code>null</code>.
   */
  @Nullable
  public static String getQuotedTextString (@Nullable final String sStr)
  {
    if (sStr == null)
      return null;
    return QUOTEDTEXT_BEGIN + sStr + QUOTEDTEXT_END;
  }

  /**
   * Check if the passed character array consists only of valid quoted-text content characters
   * (without the enclosing double-quotes).
   *
   * @param aChars
   *        The character array to check. May be <code>null</code>.
   * @return <code>true</code> if all characters are valid quoted-text content characters.
   */
  public static boolean isQuotedTextContent (final char @Nullable [] aChars)
  {
    if (aChars == null)
      return false;
    for (final char c : aChars)
      if (!isQuotedTextChar (c))
        return false;
    return true;
  }

  /**
   * Check if the passed string consists only of valid quoted-text content characters (without the
   * enclosing double-quotes).
   *
   * @param sStr
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if all characters are valid quoted-text content characters.
   */
  public static boolean isQuotedTextContent (@Nullable final String sStr)
  {
    if (sStr == null)
      return false;
    return isQuotedTextContent (sStr.toCharArray ());
  }

  /**
   * Check if the passed character array is a valid word (either a token or a quoted-text).
   *
   * @param aChars
   *        The character array to check. May be <code>null</code>.
   * @return <code>true</code> if the array is a valid word.
   */
  public static boolean isWord (final char @Nullable [] aChars)
  {
    return isToken (aChars) || isQuotedText (aChars);
  }

  /**
   * Check if the passed string is a valid word (either a token or a quoted-text).
   *
   * @param sStr
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the string is a valid word.
   */
  public static boolean isWord (@Nullable final String sStr)
  {
    if (StringHelper.isEmpty (sStr))
      return false;
    return isWord (sStr.toCharArray ());
  }

  /**
   * Check if the passed value is a reserved character as defined in RFC 1945.
   *
   * @param n
   *        The value to check.
   * @return <code>true</code> if the value is a reserved character.
   */
  public static boolean isReservedChar (final int n)
  {
    // ";" | "/" | "?" | ":" | "@" | "&" | "=" | "+"
    return isChar (n) && (MAPPINGS[n] & RESERVED) == RESERVED;
  }

  /**
   * Check if the passed value is an extra character as defined in RFC 1945.
   *
   * @param n
   *        The value to check.
   * @return <code>true</code> if the value is an extra character.
   */
  public static boolean isExtraChar (final int n)
  {
    // "!" | "*" | "'" | "(" | ")" | ","
    return isChar (n) && (MAPPINGS[n] & EXTRA) == EXTRA;
  }

  /**
   * Check if the passed value is a safe character as defined in RFC 1945.
   *
   * @param n
   *        The value to check.
   * @return <code>true</code> if the value is a safe character.
   */
  public static boolean isSafeChar (final int n)
  {
    // "$" | "-" | "_" | "."
    return isChar (n) && (MAPPINGS[n] & SAFE) == SAFE;
  }

  /**
   * Check if the passed value is an unsafe character as defined in RFC 1945.
   *
   * @param n
   *        The value to check.
   * @return <code>true</code> if the value is an unsafe character.
   */
  public static boolean isUnsafeChar (final int n)
  {
    // CTL | SP | <"> | "#" | "%" | "<" | ">"
    return isChar (n) && (MAPPINGS[n] & UNSAFE) == UNSAFE;
  }

  /**
   * Check if the passed value is a national character as defined in RFC 1945 (any OCTET excluding
   * ALPHA, DIGIT, reserved, extra, safe, and unsafe).
   *
   * @param n
   *        The value to check.
   * @return <code>true</code> if the value is a national character.
   */
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

  /**
   * Check if the passed value is an unreserved character as defined in RFC 1945 (ALPHA, DIGIT,
   * safe, extra, or national).
   *
   * @param n
   *        The value to check.
   * @return <code>true</code> if the value is an unreserved character.
   */
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
