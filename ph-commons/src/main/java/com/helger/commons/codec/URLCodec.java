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

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.charset.CCharset;
import com.helger.commons.charset.CharsetManager;
import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;
import com.helger.commons.string.StringHelper;

/**
 * Encoder and decoder for URL stuff
 *
 * @author Philip Helger
 */
public class URLCodec implements IByteArrayCodec
{
  private static final byte ESCAPE_CHAR = '%';

  private static final byte SPACE = ' ';
  private static final byte PLUS = '+';

  /**
   * BitSet of www-form-url safe characters.
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
    PRINTABLE_CHARS.set ('*');
    // blank to be replaced with +
    PRINTABLE_CHARS.set (SPACE);
  }

  /**
   * Default constructor with the UTF-8 charset.
   */
  public URLCodec ()
  {}

  /**
   * @return A copy of the default bit set to be used. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static BitSet getDefaultBitSet ()
  {
    return (BitSet) PRINTABLE_CHARS.clone ();
  }

  /**
   * Encodes byte into its URL representation.
   *
   * @param b
   *        byte to encode
   * @param aBAOS
   *        the buffer to write to
   */
  public static final void writeEncodedURLByte (final int b, @Nonnull final NonBlockingByteArrayOutputStream aBAOS)
  {
    final char cHigh = StringHelper.getHexCharUpperCase ((b >> 4) & 0xF);
    final char cLow = StringHelper.getHexCharUpperCase (b & 0xF);
    aBAOS.write (ESCAPE_CHAR);
    aBAOS.write (cHigh);
    aBAOS.write (cLow);
  }

  @Nullable
  @ReturnsMutableCopy
  public static byte [] getEncodedURL (@Nonnull final BitSet aPrintableBitSet, @Nullable final byte [] aDecodedBuffer)
  {
    ValueEnforcer.notNull (aPrintableBitSet, "PrintableBitSet");
    if (aDecodedBuffer == null)
      return null;

    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream (aDecodedBuffer.length * 2);
    for (final byte nByte : aDecodedBuffer)
    {
      final int b = nByte & 0xff;
      if (aPrintableBitSet.get (b))
      {
        if (b == SPACE)
          aBAOS.write (PLUS);
        else
          aBAOS.write (b);
      }
      else
      {
        writeEncodedURLByte (b, aBAOS);
      }
    }
    return aBAOS.toByteArray ();
  }

  @Nullable
  @ReturnsMutableCopy
  public static byte [] getEncodedURL (@Nullable final byte [] aDecodedBuffer)
  {
    return getEncodedURL (PRINTABLE_CHARS, aDecodedBuffer);
  }

  @Nullable
  @ReturnsMutableCopy
  public byte [] getEncoded (@Nullable final byte [] aDecodedBuffer)
  {
    if (aDecodedBuffer == null)
      return null;

    return getEncodedURL (aDecodedBuffer);
  }

  @Nullable
  @ReturnsMutableCopy
  public static byte [] getDecodedURL (@Nullable final byte [] aEncodedBuffer)
  {
    if (aEncodedBuffer == null)
      return null;

    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ())
    {
      final int nMax = aEncodedBuffer.length;
      for (int i = 0; i < nMax; i++)
      {
        final int b = aEncodedBuffer[i];
        if (b == PLUS)
          aBAOS.write (SPACE);
        else
          if (b == ESCAPE_CHAR)
          {
            if (i >= nMax - 2)
              throw new DecodeException ("Invalid URL encoding. Premature of string after escape char");
            final char cHigh = (char) aEncodedBuffer[++i];
            final char cLow = (char) aEncodedBuffer[++i];
            final int nDecodedValue = StringHelper.getHexByte (cHigh, cLow);
            if (nDecodedValue < 0)
              throw new DecodeException ("Invalid URL encoding for " + (int) cHigh + " and " + (int) cLow);

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
  public static byte [] getDecodedURL (@Nullable final String sEncodedURL)
  {
    if (sEncodedURL == null)
      return null;

    return getDecodedURL (CharsetManager.getAsBytes (sEncodedURL, CCharset.CHARSET_US_ASCII_OBJ));
  }

  @Nullable
  @ReturnsMutableCopy
  public byte [] getDecoded (@Nullable final byte [] aEncodedBuffer)
  {
    return getDecodedURL (aEncodedBuffer);
  }

  @Nullable
  public static String getEncodedURLString (@Nonnull final BitSet aPrintableBitSet,
                                            @Nonnull final byte [] aDecodedBuffer)
  {
    final byte [] aEncodedBytes = getEncodedURL (aPrintableBitSet, aDecodedBuffer);
    return aEncodedBytes == null ? null : CharsetManager.getAsString (aEncodedBytes, CCharset.CHARSET_US_ASCII_OBJ);
  }

  @Nullable
  public static String getEncodedURLString (@Nonnull final byte [] aDecodedBuffer)
  {
    return getEncodedURLString (PRINTABLE_CHARS, aDecodedBuffer);
  }

  @Nullable
  public static String getEncodedURLString (@Nonnull final BitSet aPrintableBitSet,
                                            @Nullable final String sDecodedURL,
                                            @Nonnull final Charset aSourceCharset)
  {
    if (StringHelper.hasNoText (sDecodedURL))
      return sDecodedURL;

    return getEncodedURLString (aPrintableBitSet, CharsetManager.getAsBytes (sDecodedURL, aSourceCharset));
  }

  @Nullable
  public static String getEncodedURLString (@Nullable final String sDecodedURL, @Nonnull final Charset aSourceCharset)
  {
    return getEncodedURLString (PRINTABLE_CHARS, sDecodedURL, aSourceCharset);
  }

  @Nullable
  public static String getDecodedURLString (@Nullable final String sEncodedURL, @Nonnull final Charset aDestCharset)
  {
    if (StringHelper.hasNoText (sEncodedURL))
      return sEncodedURL;

    return CharsetManager.getAsString (getDecodedURL (sEncodedURL), aDestCharset);
  }
}
