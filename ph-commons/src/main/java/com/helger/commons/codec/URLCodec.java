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
package com.helger.commons.codec;

import java.io.IOException;
import java.io.OutputStream;
import java.util.BitSet;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillNotClose;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.string.StringHelper;

/**
 * Encoder and decoder for URL stuff
 *
 * @author Philip Helger
 */
@ThreadSafe
public class URLCodec implements IByteArrayCodec
{
  private static final byte ESCAPE_CHAR = '%';
  private static final byte SPACE = ' ';
  private static final byte PLUS = '+';

  /**
   * BitSet of www-form-url safe characters. RFC 3986 unreserved characters
   */
  private static final BitSet PRINTABLE_CHARS = new BitSet (256);

  static
  {
    // alpha characters
    for (int i = 'a'; i <= 'z'; i++)
      PRINTABLE_CHARS.set (i);
    for (int i = 'A'; i <= 'Z'; i++)
      PRINTABLE_CHARS.set (i);
    // numeric characters
    for (int i = '0'; i <= '9'; i++)
      PRINTABLE_CHARS.set (i);
    // special chars
    PRINTABLE_CHARS.set ('-');
    PRINTABLE_CHARS.set ('_');
    PRINTABLE_CHARS.set ('.');
    PRINTABLE_CHARS.set ('~');
    // blank to be replaced with +
    PRINTABLE_CHARS.set (SPACE);
    // Apache Http-client also adds "*" to printable chars
  }

  /**
   * @return A copy of the default bit set to be used.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static BitSet getDefaultPrintableChars ()
  {
    return (BitSet) PRINTABLE_CHARS.clone ();
  }

  private final BitSet m_aPrintableChars;

  /**
   * Default constructor with the UTF-8 charset.
   */
  public URLCodec ()
  {
    this (PRINTABLE_CHARS);
  }

  public URLCodec (@Nonnull final BitSet aPrintableChars)
  {
    m_aPrintableChars = (BitSet) aPrintableChars.clone ();
  }

  /**
   * @return A copy of the default bit set to be used. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public BitSet getPrintableChars ()
  {
    return (BitSet) m_aPrintableChars.clone ();
  }

  /**
   * Encodes byte into its URL representation.
   *
   * @param b
   *        byte to encode
   * @param aOS
   *        the output stream to write to. May not be <code>null</code>.
   * @throws IOException
   *         In case writing to the OutputStream failed
   */
  public static final void writeEncodedURLByte (final int b, @Nonnull final OutputStream aOS) throws IOException
  {
    // Hex chars should be upper case as defined in RFC 3986 section 2.1
    final char cHigh = StringHelper.getHexCharUpperCase ((b >> 4) & 0xF);
    final char cLow = StringHelper.getHexCharUpperCase (b & 0xF);
    aOS.write (ESCAPE_CHAR);
    aOS.write (cHigh);
    aOS.write (cLow);
  }

  public void encode (@Nullable final byte [] aDecodedBuffer,
                      @Nonnegative final int nOfs,
                      @Nonnegative final int nLen,
                      @Nonnull @WillNotClose final OutputStream aOS)
  {
    if (aDecodedBuffer == null || nLen == 0)
      return;

    try
    {
      for (int i = 0; i < nLen; ++i)
      {
        final int b = aDecodedBuffer[nOfs + i] & 0xff;
        if (m_aPrintableChars.get (b))
        {
          if (b == SPACE)
            aOS.write (PLUS);
          else
            aOS.write (b);
        }
        else
        {
          writeEncodedURLByte (b, aOS);
        }
      }
    }
    catch (final IOException ex)
    {
      throw new EncodeException ("Failed to encode URL", ex);
    }
  }

  public void decode (@Nullable final byte [] aEncodedBuffer,
                      @Nonnegative final int nOfs,
                      @Nonnegative final int nLen,
                      @Nonnull @WillNotClose final OutputStream aOS)
  {
    if (aEncodedBuffer == null || nLen == 0)
      return;

    try
    {
      for (int i = 0; i < nLen; i++)
      {
        final int b = aEncodedBuffer[nOfs + i];
        if (b == PLUS)
          aOS.write (SPACE);
        else
          if (b == ESCAPE_CHAR)
          {
            if (i >= nLen - 2)
              throw new DecodeException ("Invalid URL encoding. Premature end of input after escape char");
            final char cHigh = (char) aEncodedBuffer[nOfs + i + 1];
            final char cLow = (char) aEncodedBuffer[nOfs + i + 2];
            i += 2;
            final int nDecodedValue = StringHelper.getHexByte (cHigh, cLow);
            if (nDecodedValue < 0)
              throw new DecodeException ("Invalid URL encoding for " + (int) cHigh + " and " + (int) cLow);

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
      throw new DecodeException ("Failed to decode URL", ex);
    }
  }
}
