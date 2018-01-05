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
package com.helger.commons.text.util;

import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.PresentForCodeCoverage;

/**
 * This class contains the ABNF (RFC 5234 https://tools.ietf.org/html/rfc5234)
 * core rules.
 *
 * @author Philip Helger
 */
@Immutable
public final class ABNF
{
  private static final int BIT_ALPHA = 0x0001;
  private static final int BIT_BIT = 0x0002;
  private static final int BIT_CHAR = 0x0004;
  private static final int BIT_CR = 0x0008;
  private static final int BIT_CTL = 0x0010;
  private static final int BIT_DIGIT = 0x0020;
  private static final int BIT_DQUOTE = 0x0040;
  private static final int BIT_HEXDIGIT = 0x0080;
  private static final int BIT_HTAB = 0x0100;
  private static final int BIT_LF = 0x0200;
  private static final int BIT_SP = 0x0400;
  private static final int BIT_VCHAR = 0x0800;
  private static final int BIT_WSP = 0x1000;
  // ph extensions
  // Hex digit, case insensitive
  private static final int BIT_HEXDIGIT_CI = 0x10000000;

  private static final int [] s_aBits = new int [] { // 0x00-0x0f
                                                     BIT_CTL,
                                                     BIT_CHAR | BIT_CTL,
                                                     BIT_CHAR | BIT_CTL,
                                                     BIT_CHAR | BIT_CTL,
                                                     BIT_CHAR | BIT_CTL,
                                                     BIT_CHAR | BIT_CTL,
                                                     BIT_CHAR | BIT_CTL,
                                                     BIT_CHAR | BIT_CTL,
                                                     BIT_CHAR | BIT_CTL,
                                                     BIT_CHAR | BIT_CTL | BIT_HTAB | BIT_WSP,
                                                     BIT_CHAR | BIT_CTL | BIT_LF,
                                                     BIT_CHAR | BIT_CTL,
                                                     BIT_CHAR | BIT_CTL,
                                                     BIT_CHAR | BIT_CTL | BIT_CR,
                                                     BIT_CHAR | BIT_CTL,
                                                     BIT_CHAR | BIT_CTL,
                                                     // 0x10-0x1f
                                                     BIT_CHAR | BIT_CTL,
                                                     BIT_CHAR | BIT_CTL,
                                                     BIT_CHAR | BIT_CTL,
                                                     BIT_CHAR | BIT_CTL,
                                                     BIT_CHAR | BIT_CTL,
                                                     BIT_CHAR | BIT_CTL,
                                                     BIT_CHAR | BIT_CTL,
                                                     BIT_CHAR | BIT_CTL,
                                                     BIT_CHAR | BIT_CTL,
                                                     BIT_CHAR | BIT_CTL,
                                                     BIT_CHAR | BIT_CTL,
                                                     BIT_CHAR | BIT_CTL,
                                                     BIT_CHAR | BIT_CTL,
                                                     BIT_CHAR | BIT_CTL,
                                                     BIT_CHAR | BIT_CTL,
                                                     BIT_CHAR | BIT_CTL,
                                                     // 0x20-0x2f
                                                     BIT_CHAR | BIT_SP | BIT_WSP,
                                                     BIT_CHAR | BIT_VCHAR,
                                                     BIT_CHAR | BIT_DQUOTE | BIT_VCHAR,
                                                     BIT_CHAR | BIT_VCHAR,
                                                     BIT_CHAR | BIT_VCHAR,
                                                     BIT_CHAR | BIT_VCHAR,
                                                     BIT_CHAR | BIT_VCHAR,
                                                     BIT_CHAR | BIT_VCHAR,
                                                     BIT_CHAR | BIT_VCHAR,
                                                     BIT_CHAR | BIT_VCHAR,
                                                     BIT_CHAR | BIT_VCHAR,
                                                     BIT_CHAR | BIT_VCHAR,
                                                     BIT_CHAR | BIT_VCHAR,
                                                     BIT_CHAR | BIT_VCHAR,
                                                     BIT_CHAR | BIT_VCHAR,
                                                     BIT_CHAR | BIT_VCHAR,
                                                     // 0x30-0x3f
                                                     BIT_BIT |
                                                                           BIT_CHAR |
                                                                           BIT_DIGIT |
                                                                           BIT_HEXDIGIT |
                                                                           BIT_VCHAR |
                                                                           BIT_HEXDIGIT_CI,
                                                     BIT_BIT |
                                                                                            BIT_CHAR |
                                                                                            BIT_DIGIT |
                                                                                            BIT_HEXDIGIT |
                                                                                            BIT_VCHAR |
                                                                                            BIT_HEXDIGIT_CI,
                                                     BIT_CHAR | BIT_DIGIT | BIT_HEXDIGIT | BIT_VCHAR | BIT_HEXDIGIT_CI,
                                                     BIT_CHAR | BIT_DIGIT | BIT_HEXDIGIT | BIT_VCHAR | BIT_HEXDIGIT_CI,
                                                     BIT_CHAR | BIT_DIGIT | BIT_HEXDIGIT | BIT_VCHAR | BIT_HEXDIGIT_CI,
                                                     BIT_CHAR | BIT_DIGIT | BIT_HEXDIGIT | BIT_VCHAR | BIT_HEXDIGIT_CI,
                                                     BIT_CHAR | BIT_DIGIT | BIT_HEXDIGIT | BIT_VCHAR | BIT_HEXDIGIT_CI,
                                                     BIT_CHAR | BIT_DIGIT | BIT_HEXDIGIT | BIT_VCHAR | BIT_HEXDIGIT_CI,
                                                     BIT_CHAR | BIT_DIGIT | BIT_HEXDIGIT | BIT_VCHAR | BIT_HEXDIGIT_CI,
                                                     BIT_CHAR | BIT_DIGIT | BIT_HEXDIGIT | BIT_VCHAR | BIT_HEXDIGIT_CI,
                                                     BIT_CHAR | BIT_VCHAR,
                                                     BIT_CHAR | BIT_VCHAR,
                                                     BIT_CHAR | BIT_VCHAR,
                                                     BIT_CHAR | BIT_VCHAR,
                                                     BIT_CHAR | BIT_VCHAR,
                                                     BIT_CHAR | BIT_VCHAR,
                                                     // 0x40-0x4f
                                                     BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_HEXDIGIT | BIT_VCHAR | BIT_HEXDIGIT_CI,
                                                     BIT_ALPHA | BIT_CHAR | BIT_HEXDIGIT | BIT_VCHAR | BIT_HEXDIGIT_CI,
                                                     BIT_ALPHA | BIT_CHAR | BIT_HEXDIGIT | BIT_VCHAR | BIT_HEXDIGIT_CI,
                                                     BIT_ALPHA | BIT_CHAR | BIT_HEXDIGIT | BIT_VCHAR | BIT_HEXDIGIT_CI,
                                                     BIT_ALPHA | BIT_CHAR | BIT_HEXDIGIT | BIT_VCHAR | BIT_HEXDIGIT_CI,
                                                     BIT_ALPHA | BIT_CHAR | BIT_HEXDIGIT | BIT_VCHAR | BIT_HEXDIGIT_CI,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     // 0x50-0x5f
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_CHAR | BIT_VCHAR,
                                                     BIT_CHAR | BIT_VCHAR,
                                                     BIT_CHAR | BIT_VCHAR,
                                                     // 0x60-0x6f
                                                     BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR | BIT_HEXDIGIT_CI,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR | BIT_HEXDIGIT_CI,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR | BIT_HEXDIGIT_CI,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR | BIT_HEXDIGIT_CI,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR | BIT_HEXDIGIT_CI,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR | BIT_HEXDIGIT_CI,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     // 0x70-0x7f
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_ALPHA | BIT_CHAR | BIT_VCHAR,
                                                     BIT_CHAR | BIT_VCHAR,
                                                     BIT_CHAR | BIT_VCHAR,
                                                     BIT_CHAR | BIT_VCHAR,
                                                     BIT_CHAR | BIT_VCHAR,
                                                     BIT_CHAR | BIT_CTL };

  static
  {
    if (s_aBits.length != 128)
      throw new IllegalStateException ("Bits array has an invalid length!");
  }

  @PresentForCodeCoverage
  private static final ABNF s_aInstance = new ABNF ();

  private ABNF ()
  {}

  private static boolean _isBitSet (final int nCP, final int nBit)
  {
    return nCP >= 0x00 && nCP <= 0x7f && (s_aBits[nCP] & nBit) == nBit;
  }

  /**
   * Check if the passed code point matches the following rules:
   * <code>%x41-5A / %x61-7A   ; A-Z / a-z</code>
   *
   * @param nCP
   *        Code point to check.
   * @return <code>true</code> if it matches the rules
   */
  public static boolean isAlpha (final int nCP)
  {
    return _isBitSet (nCP, BIT_ALPHA);
  }

  /**
   * Check if the passed code point matches the following rules:
   * <code>"0" / "1"</code>
   *
   * @param nCP
   *        Code point to check.
   * @return <code>true</code> if it matches the rules
   */
  public static boolean isBit (final int nCP)
  {
    return _isBitSet (nCP, BIT_BIT);
  }

  /**
   * Check if the passed code point matches the following rules:
   * <code>%x01-7F &rarr; any 7-bit US-ASCII character, excluding NUL</code>
   *
   * @param nCP
   *        Code point to check.
   * @return <code>true</code> if it matches the rules
   */
  public static boolean isChar (final int nCP)
  {
    return _isBitSet (nCP, BIT_CHAR);
  }

  /**
   * Check if the passed code point matches the following rules:
   * <code>%x0D &rarr; carriage return</code>
   *
   * @param nCP
   *        Code point to check.
   * @return <code>true</code> if it matches the rules
   */
  public static boolean isCR (final int nCP)
  {
    return _isBitSet (nCP, BIT_CR);
  }

  /**
   * Check if the passed code point matches the following rules:
   * <code>%x00-1F / %x7F &rarr; controls</code>
   *
   * @param nCP
   *        Code point to check.
   * @return <code>true</code> if it matches the rules
   */
  public static boolean isCtl (final int nCP)
  {
    return _isBitSet (nCP, BIT_CTL);
  }

  /**
   * Check if the passed code point matches the following rules:
   * <code>%x30-39 &rarr; 0-9</code>
   *
   * @param nCP
   *        Code point to check.
   * @return <code>true</code> if it matches the rules
   */
  public static boolean isDigit (final int nCP)
  {
    return _isBitSet (nCP, BIT_DIGIT);
  }

  /**
   * Check if the passed code point matches the following rules:
   * <code>%x22 &rarr; " (Double Quote)</code>
   *
   * @param nCP
   *        Code point to check.
   * @return <code>true</code> if it matches the rules
   */
  public static boolean isDQuote (final int nCP)
  {
    return _isBitSet (nCP, BIT_DQUOTE);
  }

  /**
   * Check if the passed code point matches the following rules:
   * <code>DIGIT / "A" / "B" / "C" / "D" / "E" / "F"</code>
   *
   * @param nCP
   *        Code point to check.
   * @return <code>true</code> if it matches the rules
   * @see #isHexDigitCaseInsensitive(int)
   */
  public static boolean isHexDigit (final int nCP)
  {
    return _isBitSet (nCP, BIT_HEXDIGIT);
  }

  /**
   * Check if the passed code point matches the following rules:
   * <code>DIGIT / "A" / "B" / "C" / "D" / "E" / "F"
   *             / "a" / "b" / "c" / "d" / "e" / "f" </code> <br>
   * Note: this is a non-standard extension!
   *
   * @param nCP
   *        Code point to check.
   * @return <code>true</code> if it matches the rules
   * @see #isHexDigit(int)
   */
  public static boolean isHexDigitCaseInsensitive (final int nCP)
  {
    return _isBitSet (nCP, BIT_HEXDIGIT_CI);
  }

  /**
   * Check if the passed code point matches the following rules:
   * <code>%x09 &rarr; horizontal tab</code>
   *
   * @param nCP
   *        Code point to check.
   * @return <code>true</code> if it matches the rules
   */
  public static boolean isHTab (final int nCP)
  {
    return _isBitSet (nCP, BIT_HTAB);
  }

  /**
   * Check if the passed code point matches the following rules:
   * <code>%x0A &rarr; linefeed</code>
   *
   * @param nCP
   *        Code point to check.
   * @return <code>true</code> if it matches the rules
   */
  public static boolean isLF (final int nCP)
  {
    return _isBitSet (nCP, BIT_LF);
  }

  /**
   * Check if the passed code point matches the following rules:
   * <code>%x00-FF &rarr; 8 bits of data</code>
   *
   * @param nCP
   *        Code point to check.
   * @return <code>true</code> if it matches the rules
   */
  public static boolean isOctet (final int nCP)
  {
    return nCP >= 0x00 && nCP <= 0xff;
  }

  /**
   * Check if the passed code point matches the following rules:
   * <code>%x20</code>
   *
   * @param nCP
   *        Code point to check.
   * @return <code>true</code> if it matches the rules
   */
  public static boolean isSP (final int nCP)
  {
    return _isBitSet (nCP, BIT_SP);
  }

  /**
   * Check if the passed code point matches the following rules:
   * <code>%x21-7E &rarr; visible (printing) characters</code>
   *
   * @param nCP
   *        Code point to check.
   * @return <code>true</code> if it matches the rules
   */
  public static boolean isVChar (final int nCP)
  {
    return _isBitSet (nCP, BIT_VCHAR);
  }

  /**
   * Check if the passed code point matches the following rules:
   * <code>SP / HTAB &rarr; white space</code>
   *
   * @param nCP
   *        Code point to check.
   * @return <code>true</code> if it matches the rules
   */
  public static boolean isWSP (final int nCP)
  {
    return _isBitSet (nCP, BIT_WSP);
  }
}
