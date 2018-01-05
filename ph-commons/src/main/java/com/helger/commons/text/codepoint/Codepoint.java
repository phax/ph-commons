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
package com.helger.commons.text.codepoint;

import java.nio.charset.Charset;
import java.util.Arrays;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.compare.CompareHelper;
import com.helger.commons.compare.IComparable;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * Represents a single Unicode Codepoint
 *
 * @author Apache Abdera
 * @author Philip Helger
 */
@NotThreadSafe
public class Codepoint implements IComparable <Codepoint>
{
  private final int m_nValue;

  /**
   * Create a Codepoint from a byte array with the specified charset encoding.
   * Length must equal 1
   *
   * @param aBytes
   *        Bytes
   * @param aEncoding
   *        Charset
   */
  public Codepoint (@Nonnull final byte [] aBytes, @Nonnull final Charset aEncoding)
  {
    this (StringHelper.decodeBytesToChars (aBytes, aEncoding));
  }

  private static int _getValueFromCharSequence (@Nonnull final CharSequence s)
  {
    ValueEnforcer.notNull (s, "CharSequence");
    final int nLength = s.length ();
    if (nLength == 1)
      return s.charAt (0);
    if (nLength > 2)
      throw new IllegalArgumentException ("Too many chars: " + s);
    return Character.toCodePoint (s.charAt (0), s.charAt (1));
  }

  /**
   * Create a Codepoint from a CharSequence. Length must equal 1 or 2
   *
   * @param aCS
   *        {@link CharSequence}
   */
  public Codepoint (@Nonnull final CharSequence aCS)
  {
    this (_getValueFromCharSequence (aCS));
  }

  /**
   * Create a Codepoint from a String. Length must equal 1 or 2
   *
   * @param sValue
   *        String
   */
  public Codepoint (@Nonnull final String sValue)
  {
    this (sValue.toCharArray ());
  }

  private static int _getValueFromCharArray (@Nonnull final char [] aChars)
  {
    ValueEnforcer.notEmpty (aChars, "CharArray");
    final int nLength = aChars.length;
    if (nLength == 1)
      return aChars[0];
    if (nLength > 2)
      throw new IllegalArgumentException ("Too many chars: " + Arrays.toString (aChars));
    return Character.toCodePoint (aChars[0], aChars[1]);
  }

  /**
   * Create a Codepoint from a char array. Length must equal 1 or 2
   *
   * @param aChars
   *        char array
   */
  public Codepoint (@Nonnull final char [] aChars)
  {
    this (_getValueFromCharArray (aChars));
  }

  /**
   * Create a codepoint from a single char
   *
   * @param cChar
   *        single char
   */
  public Codepoint (final char cChar)
  {
    this ((int) cChar);
  }

  /**
   * Create a codepoint from a surrogate pair
   *
   * @param cHigh
   *        high surrogate
   * @param cLow
   *        low surrogate
   */
  public Codepoint (final char cHigh, final char cLow)
  {
    this (Character.toCodePoint (cHigh, cLow));
  }

  /**
   * Create a codepoint as a copy of another codepoint
   *
   * @param aCodepoint
   *        Object to copy
   */
  public Codepoint (@Nonnull final Codepoint aCodepoint)
  {
    this (aCodepoint.m_nValue);
  }

  /**
   * Create a codepoint from a specific integer value
   *
   * @param nValue
   *        int value
   */
  public Codepoint (@Nonnegative final int nValue)
  {
    ValueEnforcer.isTrue (Character.isValidCodePoint (nValue), () -> "Invalid Codepoint: " + nValue);
    m_nValue = nValue;
  }

  /**
   * Special protected constructor that allows creating special codepoints that
   * are invalid.
   *
   * @param nValue
   *        The codepoint value to be used. Must not be a valid codepoint.
   * @param bDummyUnchecked
   *        Dummy parameter to create a different signature
   */
  protected Codepoint (@Nonnegative final int nValue, final boolean bDummyUnchecked)
  {
    m_nValue = nValue;
  }

  /**
   * @return The codepoint value
   */
  @Nonnegative
  public final int getValue ()
  {
    return m_nValue;
  }

  /**
   * @return <code>true</code> if this codepoint is supplementary
   */
  public final boolean isSupplementary ()
  {
    return Character.isSupplementaryCodePoint (m_nValue);
  }

  /**
   * @return <code>true</code> if this codepoint is a low surrogate
   */
  public final boolean isLowSurrogate ()
  {
    return Character.isLowSurrogate ((char) m_nValue);
  }

  /**
   * @return <code>true</code> if this codepoint is a high surrogate
   */
  public final boolean isHighSurrogate ()
  {
    return Character.isHighSurrogate ((char) m_nValue);
  }

  /**
   * @return Get the high surrogate of this Codepoint
   */
  public final char getHighSurrogate ()
  {
    return CodepointHelper.getHighSurrogate (m_nValue);
  }

  /**
   * @return Get the low surrogate of this Codepoint
   */
  public final char getLowSurrogate ()
  {
    return CodepointHelper.getLowSurrogate (m_nValue);
  }

  /**
   * @return <code>true</code> if this Codepoint is a bidi control char
   */
  public boolean isBidi ()
  {
    return CodepointHelper.isBidi (m_nValue);
  }

  public boolean isDigit ()
  {
    return Character.isDigit (m_nValue);
  }

  public boolean isAlpha ()
  {
    return Character.isLetter (m_nValue);
  }

  public boolean isAlphaDigit ()
  {
    return Character.isLetterOrDigit (m_nValue);
  }

  @Nonnull
  @Nonempty
  public String getAsString ()
  {
    return CodepointHelper.getAsString (m_nValue);
  }

  @Nonnull
  @ReturnsMutableCopy
  public char [] getAsChars ()
  {
    return Character.toChars (m_nValue);
  }

  /**
   * @return The number of chars necessary to represent this codepoint. Returns
   *         2 if this is a supplementary codepoint, 1 otherwise.
   */
  @Nonnegative
  public int getCharCount ()
  {
    return Character.charCount (m_nValue);
  }

  @Nonnull
  public byte [] getAsBytes (@Nonnull final Charset aCharset)
  {
    return getAsString ().getBytes (aCharset);
  }

  /**
   * Plane 0 (0000–FFFF): Basic Multilingual Plane (BMP). This is the plane
   * containing most of the character assignments so far. A primary objective
   * for the BMP is to support the unification of prior character sets as well
   * as characters for writing systems in current use.<br>
   * Plane 1 (10000–1FFFF): Supplementary Multilingual Plane (SMP).<br>
   * Plane 2 (20000–2FFFF): Supplementary Ideographic Plane (SIP)<br>
   * Planes 3 to 13 (30000–DFFFF) are unassigned<br>
   * Plane 14 (E0000–EFFFF): Supplementary Special-purpose Plane (SSP)<br>
   * Plane 15 (F0000–FFFFF) reserved for the Private Use Area (PUA)<br>
   * Plane 16 (100000–10FFFF), reserved for the Private Use Area (PUA)
   *
   * @return Plane number
   **/
  public final int getPlane ()
  {
    return m_nValue / (0xFFFF + 1);
  }

  /**
   * @return Get the next codepoint
   */
  @Nonnull
  public final Codepoint next ()
  {
    if (m_nValue == 0x10ffff)
      throw new IndexOutOfBoundsException ();
    return new Codepoint (m_nValue + 1);
  }

  /**
   * @return Get the previous codepoint
   */
  @Nonnull
  public final Codepoint previous ()
  {
    if (m_nValue == 0)
      throw new IndexOutOfBoundsException ();
    return new Codepoint (m_nValue - 1);
  }

  public void appendTo (@Nonnull final StringBuilder aSB)
  {
    aSB.append (getAsChars ());
  }

  public int compareTo (@Nonnull final Codepoint o)
  {
    return CompareHelper.compare (m_nValue, o.m_nValue);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (this == o)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final Codepoint rhs = (Codepoint) o;
    return m_nValue == rhs.m_nValue;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_nValue).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("value", m_nValue).getToString ();
  }
}
