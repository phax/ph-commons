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
package com.helger.commons.codec;

import java.nio.charset.Charset;
import java.util.BitSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.charset.CCharset;
import com.helger.commons.charset.CharsetManager;
import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;
import com.helger.commons.string.StringHelper;

/**
 * Encoder and decoder for quoted printable stuff
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class QuotedPrintableCodec implements IByteArrayCodec
{
  private static final byte ESCAPE_CHAR = '=';
  private static final byte TAB = 9;
  private static final byte SPACE = 32;

  /**
   * BitSet of printable characters as defined in RFC 1521.
   */
  private static final BitSet PRINTABLE_CHARS = new BitSet (256);

  static
  {
    PRINTABLE_CHARS.set (TAB);
    PRINTABLE_CHARS.set (SPACE);
    for (int i = 33; i <= 126; i++)
      if (i != ESCAPE_CHAR)
        PRINTABLE_CHARS.set (i);
  }

  /**
   * Default constructor with the UTF-8 charset.
   */
  public QuotedPrintableCodec ()
  {}

  /**
   * @return A copy of the default bit set to be used.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static BitSet getDefaultBitSet ()
  {
    return (BitSet) PRINTABLE_CHARS.clone ();
  }

  /**
   * Encodes byte into its quoted-printable representation.
   *
   * @param b
   *        byte to encode
   * @param aBAOS
   *        the buffer to write to
   */
  public static final void writeEncodeQuotedPrintableByte (final int b,
                                                           @Nonnull final NonBlockingByteArrayOutputStream aBAOS)
  {
    final char cHigh = StringHelper.getHexCharUpperCase ((b >> 4) & 0xF);
    final char cLow = StringHelper.getHexCharUpperCase (b & 0xF);
    aBAOS.write (ESCAPE_CHAR);
    aBAOS.write (cHigh);
    aBAOS.write (cLow);
  }

  @Nullable
  @ReturnsMutableCopy
  public static byte [] getEncodedQuotedPrintable (@Nonnull final BitSet aPrintableBitSet,
                                                   @Nullable final byte [] aDecodedBuffer)
  {
    ValueEnforcer.notNull (aPrintableBitSet, "PrintableBitSet");
    if (aDecodedBuffer == null)
      return null;

    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream (aDecodedBuffer.length * 2);
    for (final byte nByte : aDecodedBuffer)
    {
      final int b = nByte & 0xff;
      if (aPrintableBitSet.get (b))
        aBAOS.write (b);
      else
        writeEncodeQuotedPrintableByte (b, aBAOS);
    }
    return aBAOS.toByteArray ();
  }

  @Nullable
  @ReturnsMutableCopy
  public static byte [] getEncodedQuotedPrintable (@Nullable final byte [] aDecodedBuffer)
  {
    return getEncodedQuotedPrintable (PRINTABLE_CHARS, aDecodedBuffer);
  }

  @Nullable
  @ReturnsMutableCopy
  public byte [] getEncoded (@Nullable final byte [] aDecodedBuffer)
  {
    return getEncodedQuotedPrintable (aDecodedBuffer);
  }

  @Nullable
  @ReturnsMutableCopy
  public static byte [] getDecodedQuotedPrintable (@Nullable final byte [] aEncodedBuffer)
  {
    if (aEncodedBuffer == null)
      return null;

    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ())
    {
      final int nMax = aEncodedBuffer.length;
      for (int i = 0; i < nMax; i++)
      {
        final int b = aEncodedBuffer[i];
        if (b == ESCAPE_CHAR)
        {
          if (i >= nMax - 2)
            throw new DecodeException ("Invalid quoted-printable encoding. Premature of string after escape char");
          final char cHigh = (char) aEncodedBuffer[++i];
          final char cLow = (char) aEncodedBuffer[++i];
          final int nDecodedValue = StringHelper.getHexByte (cHigh, cLow);
          if (nDecodedValue < 0)
            throw new DecodeException ("Invalid quoted-printable encoding for " + cHigh + cLow);

          aBAOS.write (nDecodedValue);
        }
        else
        {
          aBAOS.write (b);
        }
      }
      return aBAOS.toByteArray ();
    }
  }

  @Nullable
  @ReturnsMutableCopy
  public static byte [] getDecodedQuotedPrintable (@Nullable final String sEncodedText)
  {
    if (sEncodedText == null)
      return null;

    return getDecodedQuotedPrintable (CharsetManager.getAsBytes (sEncodedText, CCharset.CHARSET_US_ASCII_OBJ));
  }

  @Nullable
  @ReturnsMutableCopy
  public byte [] getDecoded (@Nullable final byte [] aEncodedBuffer)
  {
    return getDecodedQuotedPrintable (aEncodedBuffer);
  }

  @Nullable
  public static String getEncodedQuotedPrintableString (@Nonnull final BitSet aPrintableBitSet,
                                                        @Nonnull final byte [] aDecodedBuffer)
  {
    final byte [] aEncodedBytes = getEncodedQuotedPrintable (aPrintableBitSet, aDecodedBuffer);
    return aEncodedBytes == null ? null : CharsetManager.getAsString (aEncodedBytes, CCharset.CHARSET_US_ASCII_OBJ);
  }

  @Nullable
  public static String getEncodedQuotedPrintableString (@Nonnull final byte [] aDecodedBuffer)
  {
    return getEncodedQuotedPrintableString (PRINTABLE_CHARS, aDecodedBuffer);
  }

  @Nullable
  public static String getEncodedQuotedPrintableString (@Nonnull final BitSet aPrintableBitSet,
                                                        @Nullable final String sDecodedText,
                                                        @Nonnull final Charset aSourceCharset)
  {
    if (StringHelper.hasNoText (sDecodedText))
      return sDecodedText;

    return getEncodedQuotedPrintableString (aPrintableBitSet, CharsetManager.getAsBytes (sDecodedText, aSourceCharset));
  }

  @Nullable
  public static String getEncodedQuotedPrintableString (@Nullable final String sDecodedText,
                                                        @Nonnull final Charset aSourceCharset)
  {
    return getEncodedQuotedPrintableString (PRINTABLE_CHARS, sDecodedText, aSourceCharset);
  }

  @Nullable
  public static String getDecodedQuotedPrintableString (@Nullable final String sEncodedText,
                                                        @Nonnull final Charset aDestCharset)
  {
    if (StringHelper.hasNoText (sEncodedText))
      return sEncodedText;

    return CharsetManager.getAsString (getDecodedQuotedPrintable (sEncodedText), aDestCharset);
  }
}
