/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.charset.CCharset;
import com.helger.commons.charset.CharsetManager;
import com.helger.commons.io.streams.NonBlockingByteArrayOutputStream;
import com.helger.commons.io.streams.StreamUtils;
import com.helger.commons.string.StringHelper;

/**
 * Encoder and decoder for URL stuff
 * 
 * @author Philip Helger
 */
public class URLCodec extends AbstractCodec implements IStringCodec
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
   * The default charset used for string decoding and encoding.
   */
  private final Charset m_aCharset;

  /**
   * Default constructor with the UTF-8 charset.
   */
  public URLCodec ()
  {
    this (CCharset.CHARSET_UTF_8_OBJ);
  }

  /**
   * Constructor which allows for the selection of a default charset
   * 
   * @param aCharset
   *        the default string charset to use.
   */
  public URLCodec (@Nonnull final Charset aCharset)
  {
    m_aCharset = ValueEnforcer.notNull (aCharset, "Charset");
  }

  @Nonnull
  public Charset getCharset ()
  {
    return m_aCharset;
  }

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
   * Encodes byte into its URL representation.
   * 
   * @param b
   *        byte to encode
   * @param aBAOS
   *        the buffer to write to
   */
  public static final void encodeURL (final int b, @Nonnull final NonBlockingByteArrayOutputStream aBAOS)
  {
    final char cHigh = Character.toUpperCase (StringHelper.getHexChar ((b >> 4) & 0xF));
    final char cLow = Character.toUpperCase (StringHelper.getHexChar (b & 0xF));
    aBAOS.write (ESCAPE_CHAR);
    aBAOS.write (cHigh);
    aBAOS.write (cLow);
  }

  @Nonnull
  public static byte [] encodeURL (@Nonnull final BitSet aPrintableBitSet, @Nonnull final byte [] aDecodedBuffer)
  {
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
        encodeURL (b, aBAOS);
    }
    return aBAOS.toByteArray ();
  }

  @Nullable
  public byte [] encode (@Nullable final byte [] aDecodedBuffer)
  {
    if (aDecodedBuffer == null)
      return null;

    return encodeURL (PRINTABLE_CHARS, aDecodedBuffer);
  }

  @Nullable
  public static byte [] decodeURL (@Nullable final byte [] aEncodedBuffer)
  {
    if (aEncodedBuffer == null)
      return null;

    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
    try
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
              throw new DecoderException ("Invalid URL encoding. Premature of string after escape char");
            final char cHigh = (char) aEncodedBuffer[++i];
            final char cLow = (char) aEncodedBuffer[++i];
            final int nDecodedValue = StringHelper.getHexByte (cHigh, cLow);
            if (nDecodedValue < 0)
              throw new DecoderException ("Invalid URL encoding for " + cHigh + cLow);

            aBAOS.write (nDecodedValue);
          }
          else
          {
            aBAOS.write (b);
          }
      }
      return aBAOS.toByteArray ();
    }
    finally
    {
      StreamUtils.close (aBAOS);
    }
  }

  @Nullable
  public byte [] decode (@Nullable final byte [] aEncodedBuffer)
  {
    return decodeURL (aEncodedBuffer);
  }

  @Nullable
  public String encodeText (@Nullable final String sDecoded)
  {
    return encodeText (sDecoded, m_aCharset);
  }

  @Nullable
  public String encodeText (@Nonnull final BitSet aBitSet, @Nullable final String sDecoded)
  {
    return encodeText (aBitSet, sDecoded, m_aCharset);
  }

  /**
   * Encode the passed text using the default BitSet.
   * 
   * @param sDecoded
   *        The original string to be encoded. May be <code>null</code>.
   * @param aCharset
   *        The charset to be used. May not be <code>null</code>.
   * @return The encoded string in US-ASCII encoding. May be <code>null</code>
   *         if the original string is <code>null</code>.
   */
  @Nullable
  public static String encodeText (@Nullable final String sDecoded, @Nonnull final Charset aCharset)
  {
    return encodeText (PRINTABLE_CHARS, sDecoded, aCharset);
  }

  /**
   * Encode the passed text using a custom BitSet
   * 
   * @param aPrintableBitSet
   *        The BitSet with all chars to <b>NOT</b> escape. May not be
   *        <code>null</code>.
   * @param sDecoded
   *        The original string to be encoded. May be <code>null</code>.
   * @param aCharset
   *        The charset to be used. May not be <code>null</code>.
   * @return The encoded string in US-ASCII encoding. May be <code>null</code>
   *         if the original string is <code>null</code>.
   */
  @Nullable
  public static String encodeText (@Nonnull final BitSet aPrintableBitSet,
                                   @Nullable final String sDecoded,
                                   @Nonnull final Charset aCharset)
  {
    if (sDecoded == null)
      return null;

    final byte [] aEncodedData = encodeURL (aPrintableBitSet, CharsetManager.getAsBytes (sDecoded, aCharset));
    return CharsetManager.getAsString (aEncodedData, CCharset.CHARSET_US_ASCII_OBJ);
  }

  @Nullable
  public String decodeText (@Nullable final String sEncoded)
  {
    return decodeText (sEncoded, m_aCharset);
  }

  @Nullable
  public static String decodeText (@Nullable final String sEncoded, @Nonnull final Charset aCharset)
  {
    if (sEncoded == null)
      return null;
    byte [] aData = CharsetManager.getAsBytes (sEncoded, CCharset.CHARSET_US_ASCII_OBJ);
    aData = decodeURL (aData);
    return CharsetManager.getAsString (aData, aCharset);
  }
}
