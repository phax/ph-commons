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
package com.helger.base.codec.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.BitSet;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.WillNotClose;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.codec.DecodeException;
import com.helger.base.codec.EncodeException;
import com.helger.base.codec.IByteArrayCodec;
import com.helger.base.string.StringHex;

/**
 * Encoder and decoder for quoted printable stuff. Uses "=" as the escape char.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class QuotedPrintableCodec implements IByteArrayCodec
{
  public static final byte ESCAPE_CHAR = '=';
  public static final byte TAB = '\t';
  public static final byte SPACE = ' ';

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
   * @return A copy of the default bit set to be used.
   */
  @NonNull
  @ReturnsMutableCopy
  public static BitSet getDefaultPrintableChars ()
  {
    return (BitSet) PRINTABLE_CHARS.clone ();
  }

  private final BitSet m_aPrintableChars;

  /**
   * Default constructor with the UTF-8 charset.
   */
  public QuotedPrintableCodec ()
  {
    this (PRINTABLE_CHARS);
  }

  public QuotedPrintableCodec (@NonNull final BitSet aPrintableChars)
  {
    m_aPrintableChars = (BitSet) aPrintableChars.clone ();
  }

  /**
   * @return A copy of the default bit set to be used. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public BitSet getPrintableChars ()
  {
    return (BitSet) m_aPrintableChars.clone ();
  }

  /**
   * Encodes byte into its quoted-printable representation. It will always be 3 characters.
   *
   * @param b
   *        byte to encode
   * @param aOS
   *        the output stream to write to
   * @throws IOException
   *         In case writing to the OutputStream failed
   */
  public static final void writeEncodeQuotedPrintableByte (final int b, @NonNull final OutputStream aOS)
                                                                                                         throws IOException
  {
    final char cHigh = StringHex.getHexCharUpperCase ((b >> 4) & 0xF);
    final char cLow = StringHex.getHexCharUpperCase (b & 0xF);
    aOS.write (ESCAPE_CHAR);
    aOS.write (cHigh);
    aOS.write (cLow);
  }

  public void encode (final byte @Nullable [] aDecodedBuffer,
                      @Nonnegative final int nOfs,
                      @Nonnegative final int nLen,
                      @NonNull @WillNotClose final OutputStream aOS)
  {
    if (aDecodedBuffer == null || nLen == 0)
      return;

    try
    {
      for (int i = 0; i < nLen; ++i)
      {
        final int b = aDecodedBuffer[nOfs + i] & 0xff;
        if (m_aPrintableChars.get (b))
          aOS.write (b);
        else
          writeEncodeQuotedPrintableByte (b, aOS);
      }
    }
    catch (final IOException ex)
    {
      throw new EncodeException ("Failed to encode quoted-printable", ex);
    }
  }

  public void decode (final byte @Nullable [] aEncodedBuffer,
                      @Nonnegative final int nOfs,
                      @Nonnegative final int nLen,
                      @NonNull @WillNotClose final OutputStream aOS)
  {
    if (aEncodedBuffer == null || nLen == 0)
      return;

    try
    {
      for (int i = 0; i < nLen; i++)
      {
        final int b = aEncodedBuffer[nOfs + i];
        if (b == ESCAPE_CHAR)
        {
          if (i >= nLen - 2)
            throw new DecodeException ("Invalid quoted-printable encoding. Premature end of input after escape char");
          final char cHigh = (char) aEncodedBuffer[nOfs + i + 1];
          final char cLow = (char) aEncodedBuffer[nOfs + i + 2];
          i += 2;
          final int nDecodedValue = StringHex.getHexByte (cHigh, cLow);
          if (nDecodedValue < 0)
            throw new DecodeException ("Invalid quoted-printable encoding for " + (int) cHigh + " and " + (int) cLow);

          aOS.write (nDecodedValue);
        }
        else
        {
          aOS.write (b);
        }
      }
    }
    catch (final IOException ex)
    {
      throw new DecodeException ("Failed to decode quoted-printable", ex);
    }
  }
}
