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

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillNotClose;

import com.helger.commons.exception.InitializationException;
import com.helger.commons.math.MathHelper;

/**
 * Base32 encoder and decoder based on Apache Commons Codec Base32. Defined in
 * RFC 4648. BASE32 characters are 5 bits in length. They are formed by taking a
 * block of five octets to form a 40-bit string, which is converted into eight
 * BASE32 characters.<br>
 * RFC 3548 and defines only the "regular encoding". RFC 4648 adds the "hex
 * encoding". So when using the "regular encoding" it is compliant to both
 * RFCs.<br>
 * Source: https://tools.ietf.org/html/rfc4648<br>
 * Source: https://tools.ietf.org/html/rfc3548
 *
 * @author Philip Helger
 */
public class Base32Codec implements IByteArrayCodec
{
  /**
   * This array is a lookup table that translates Unicode characters drawn from
   * the "Base32 Alphabet" (as specified in Table 3 of RFC 4648) into their
   * 5-bit positive integer equivalents. Characters that are not in the Base32
   * alphabet but fall within the bounds of the array are translated to -1.
   */
  private static final byte [] DECODE_TABLE = { // 00-0f
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                // 10-1f
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                // 20-2f
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                // 30-3f 2-7
                                                -1,
                                                -1,
                                                26,
                                                27,
                                                28,
                                                29,
                                                30,
                                                31,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                -1,
                                                // 40-4f A-N
                                                -1,
                                                0,
                                                1,
                                                2,
                                                3,
                                                4,
                                                5,
                                                6,
                                                7,
                                                8,
                                                9,
                                                10,
                                                11,
                                                12,
                                                13,
                                                14,
                                                // 50-5a O-Z
                                                15,
                                                16,
                                                17,
                                                18,
                                                19,
                                                20,
                                                21,
                                                22,
                                                23,
                                                24,
                                                25 };

  /**
   * This array is a lookup table that translates 5-bit positive integer index
   * values into their "Base32 Alphabet" equivalents as specified in Table 3 of
   * RFC 4648.
   */
  private static final byte [] ENCODE_TABLE = { 'A',
                                                'B',
                                                'C',
                                                'D',
                                                'E',
                                                'F',
                                                'G',
                                                'H',
                                                'I',
                                                'J',
                                                'K',
                                                'L',
                                                'M',
                                                'N',
                                                'O',
                                                'P',
                                                'Q',
                                                'R',
                                                'S',
                                                'T',
                                                'U',
                                                'V',
                                                'W',
                                                'X',
                                                'Y',
                                                'Z',
                                                '2',
                                                '3',
                                                '4',
                                                '5',
                                                '6',
                                                '7' };

  /**
   * This array is a lookup table that translates Unicode characters drawn from
   * the "Base32 |Hex Alphabet" (as specified in Table 3 of RFC 4648) into their
   * 5-bit positive integer equivalents. Characters that are not in the Base32
   * Hex alphabet but fall within the bounds of the array are translated to -1.
   */
  private static final byte [] HEX_DECODE_TABLE = { // 00-0f
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    // 10-1f
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    // 20-2f
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    // 30-3f 2-7
                                                    0,
                                                    1,
                                                    2,
                                                    3,
                                                    4,
                                                    5,
                                                    6,
                                                    7,
                                                    8,
                                                    9,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    -1,
                                                    // 40-4f A-N
                                                    -1,
                                                    10,
                                                    11,
                                                    12,
                                                    13,
                                                    14,
                                                    15,
                                                    16,
                                                    17,
                                                    18,
                                                    19,
                                                    20,
                                                    21,
                                                    22,
                                                    23,
                                                    24,
                                                    // 50-57 O-V
                                                    25,
                                                    26,
                                                    27,
                                                    28,
                                                    29,
                                                    30,
                                                    31,
                                                    32

  };

  /**
   * This array is a lookup table that translates 5-bit positive integer index
   * values into their "Base32 Hex Alphabet" equivalents as specified in Table 3
   * of RFC 4648.
   */
  private static final byte [] HEX_ENCODE_TABLE = { '0',
                                                    '1',
                                                    '2',
                                                    '3',
                                                    '4',
                                                    '5',
                                                    '6',
                                                    '7',
                                                    '8',
                                                    '9',
                                                    'A',
                                                    'B',
                                                    'C',
                                                    'D',
                                                    'E',
                                                    'F',
                                                    'G',
                                                    'H',
                                                    'I',
                                                    'J',
                                                    'K',
                                                    'L',
                                                    'M',
                                                    'N',
                                                    'O',
                                                    'P',
                                                    'Q',
                                                    'R',
                                                    'S',
                                                    'T',
                                                    'U',
                                                    'V' };

  static
  {
    if (ENCODE_TABLE.length != 32)
      throw new InitializationException ("ENCODE_TABLE");
    if (HEX_ENCODE_TABLE.length != 32)
      throw new InitializationException ("HEX_ENCODE_TABLE");
  }

  /** Mask used to extract 5 bits, used when encoding Base32 bytes */
  private static final int MASK_5BITS = 0x1f;

  /**
   * Byte used to pad output.
   */
  private static final byte DEFAULT_PAD = '=';

  private byte m_nPad = DEFAULT_PAD;
  private boolean m_bAddPadding = true;

  /**
   * Encode table to use.
   */
  private byte [] m_aEncodeTable;

  /**
   * Decode table to use.
   */
  private byte [] m_aDecodeTable;

  /**
   * Creates a Base32 codec used for decoding and encoding.
   */
  public Base32Codec ()
  {
    this (false);
  }

  /**
   * Creates a Base32 codec used for decoding and encoding.
   *
   * @param bUseHex
   *        <code>true</code> to use hex encoding, <code>false</code> to use
   *        regular encoding.
   */
  public Base32Codec (final boolean bUseHex)
  {
    if (bUseHex)
    {
      m_aEncodeTable = HEX_ENCODE_TABLE;
      m_aDecodeTable = HEX_DECODE_TABLE;
    }
    else
    {
      m_aEncodeTable = ENCODE_TABLE;
      m_aDecodeTable = DECODE_TABLE;
    }
  }

  public boolean isHexEncoding ()
  {
    return m_aEncodeTable == HEX_ENCODE_TABLE;
  }

  public byte getPad ()
  {
    return m_nPad;
  }

  /**
   * Returns whether or not the {@code nOctet} is in the Base32 alphabet.
   *
   * @param nOctet
   *        The value to test
   * @return {@code true} if the value is defined in the the Base32 alphabet
   *         {@code false} otherwise.
   */
  private boolean _isInAlphabet (final byte nOctet)
  {
    return nOctet >= 0 && nOctet < m_aDecodeTable.length && m_aDecodeTable[nOctet] != -1;
  }

  /**
   * Checks if a byte value is whitespace or not. Whitespace is taken to mean:
   * space, tab, CR, LF
   *
   * @param nByte
   *        the byte to check
   * @return <code>true</code> if byte is whitespace, <code>false</code>
   *         otherwise
   */
  private static boolean _isWhiteSpace (final byte nByte)
  {
    return nByte == ' ' || nByte == '\n' || nByte == '\r' || nByte == '\t';
  }

  @Nonnull
  public Base32Codec setPad (final byte nPad)
  {
    if (_isInAlphabet (nPad) || _isWhiteSpace (nPad))
      throw new IllegalArgumentException ("pad must not be in alphabet or whitespace");
    m_nPad = nPad;
    return this;
  }

  public boolean isAddPadding ()
  {
    return m_bAddPadding;
  }

  @Nonnull
  public Base32Codec setAddPaddding (final boolean bAddPadding)
  {
    m_bAddPadding = bAddPadding;
    return this;
  }

  @Override
  public int getEncodedLength (final int nLen)
  {
    return MathHelper.getRoundedUp (nLen * 8 / 5, 8);
  }

  public void encode (@Nullable final byte [] aDecodedBuffer,
                      @Nonnegative final int nOfs,
                      @Nonnegative final int nLen,
                      @Nonnull @WillNotClose final OutputStream aOS)
  {
    if (aDecodedBuffer == null || nLen == 0)
      return;

    // Save to local variables for performance reasons
    final byte nPad = m_nPad;
    final byte [] aEncodeTable = m_aEncodeTable;

    int nRest = nLen;
    int nIndex = nOfs;
    while (nRest > 0)
    {
      try
      {
        switch (nRest)
        {
          case 1:
          {
            // Only 1 octet = 8 bits to use; 2 encoded and 6 padding bytes
            final int nCur = aDecodedBuffer[nIndex] & 0xff;
            nIndex += nRest;
            // 8-1*5
            aOS.write (aEncodeTable[(nCur >> 3) & MASK_5BITS]);
            // 5-3=2
            aOS.write (aEncodeTable[(nCur << 2) & MASK_5BITS]);
            if (m_bAddPadding)
              for (int i = 0; i < 6; ++i)
                aOS.write (nPad);
            nRest = 0;
            break;
          }
          case 2:
          {
            // 2 octets = 16 bits to use; 4 encoded and 4 padding bytes
            final int nCur = (aDecodedBuffer[nIndex] & 0xff) << 8 | (aDecodedBuffer[nIndex + 1] & 0xff);
            nIndex += nRest;
            // 16-1*5
            aOS.write (aEncodeTable[(nCur >> 11) & MASK_5BITS]);
            // 16-2*5
            aOS.write (aEncodeTable[(nCur >> 6) & MASK_5BITS]);
            // 16-3*5
            aOS.write (aEncodeTable[(nCur >> 1) & MASK_5BITS]);
            // 5-1
            aOS.write (aEncodeTable[(nCur << 4) & MASK_5BITS]);
            if (m_bAddPadding)
              for (int i = 0; i < 4; ++i)
                aOS.write (nPad);
            nRest = 0;
            break;
          }
          case 3:
          {
            // 3 octets = 24 bits to use; 5 encoded and 3 padding bytes
            final int nCur = (aDecodedBuffer[nIndex] & 0xff) << 16 |
                             (aDecodedBuffer[nIndex + 1] & 0xff) << 8 |
                             (aDecodedBuffer[nIndex + 2] & 0xff);
            nIndex += nRest;
            // 24-1*5
            aOS.write (aEncodeTable[(nCur >> 19) & MASK_5BITS]);
            // 24-2*5
            aOS.write (aEncodeTable[(nCur >> 14) & MASK_5BITS]);
            // 24-3*5
            aOS.write (aEncodeTable[(nCur >> 9) & MASK_5BITS]);
            // 24-4*5
            aOS.write (aEncodeTable[(nCur >> 4) & MASK_5BITS]);
            // 5-4
            aOS.write (aEncodeTable[(nCur << 1) & MASK_5BITS]);
            if (m_bAddPadding)
              for (int i = 0; i < 3; ++i)
                aOS.write (nPad);
            nRest = 0;
            break;
          }
          case 4:
          {
            // 4 octets = 32 bits to use; 7 encoded and 1 padding byte
            final int nCur = (aDecodedBuffer[nIndex] & 0xff) << 24 |
                             (aDecodedBuffer[nIndex + 1] & 0xff) << 16 |
                             (aDecodedBuffer[nIndex + 2] & 0xff) << 8 |
                             (aDecodedBuffer[nIndex + 3] & 0xff);
            nIndex += nRest;
            // 32-1*5
            aOS.write (aEncodeTable[(nCur >> 27) & MASK_5BITS]);
            // 32-2*5
            aOS.write (aEncodeTable[(nCur >> 22) & MASK_5BITS]);
            // 32-3*5
            aOS.write (aEncodeTable[(nCur >> 17) & MASK_5BITS]);
            // 32-4*5
            aOS.write (aEncodeTable[(nCur >> 12) & MASK_5BITS]);
            // 32-5*5
            aOS.write (aEncodeTable[(nCur >> 7) & MASK_5BITS]);
            // 32-6*5
            aOS.write (aEncodeTable[(nCur >> 2) & MASK_5BITS]);
            // 5-2
            aOS.write (aEncodeTable[(nCur << 3) & MASK_5BITS]);
            if (m_bAddPadding)
              aOS.write (nPad);
            nRest = 0;
            break;
          }
          default:
          {
            // More than 5 octets = 40 bits to use; 8 encoded bytes
            final long nCur = (long) (aDecodedBuffer[nIndex] & 0xff) << 32 |
                              (long) (aDecodedBuffer[nIndex + 1] & 0xff) << 24 |
                              (long) (aDecodedBuffer[nIndex + 2] & 0xff) << 16 |
                              (long) (aDecodedBuffer[nIndex + 3] & 0xff) << 8 |
                              (aDecodedBuffer[nIndex + 4] & 0xff);
            nIndex += 5;
            // 40-1*5
            aOS.write (aEncodeTable[(int) (nCur >> 35) & MASK_5BITS]);
            // 40-2*5
            aOS.write (aEncodeTable[(int) (nCur >> 30) & MASK_5BITS]);
            // 40-3*5
            aOS.write (aEncodeTable[(int) (nCur >> 25) & MASK_5BITS]);
            // 40-4*5
            aOS.write (aEncodeTable[(int) (nCur >> 20) & MASK_5BITS]);
            // 40-5*5
            aOS.write (aEncodeTable[(int) (nCur >> 15) & MASK_5BITS]);
            // 40-6*5
            aOS.write (aEncodeTable[(int) (nCur >> 10) & MASK_5BITS]);
            // 40-7*5
            aOS.write (aEncodeTable[(int) (nCur >> 5) & MASK_5BITS]);
            // 40-8*5
            aOS.write (aEncodeTable[(int) nCur & MASK_5BITS]);
            nRest -= 5;
            break;
          }
        }
      }
      catch (final IOException ex)
      {
        throw new EncodeException ("Failed to encode Base32", ex);
      }
    }
  }

  @Override
  public int getDecodedLength (final int nLen)
  {
    return MathHelper.getRoundedUp (nLen, 8) * 5 / 8;
  }

  public void decode (@Nullable final byte [] aEncodedBuffer,
                      @Nonnegative final int nOfs,
                      @Nonnegative final int nLen,
                      @Nonnull @WillNotClose final OutputStream aOS)
  {
    if (aEncodedBuffer == null || nLen == 0)
      return;

    // Save to local variables for performance reasons
    final byte nPad = m_nPad;
    final byte [] aDecodeTable = m_aDecodeTable;

    final byte [] aDecodeBuf = new byte [8];
    int nRest = nLen;
    int nIndex = nOfs;
    while (nRest > 0)
    {
      // Decode at maximum 8 bytes
      int nBytesToDecode = Math.min (nRest, 8);
      nRest -= nBytesToDecode;
      for (int i = 0; i < nBytesToDecode; ++i)
      {
        final int n = aEncodedBuffer[nIndex++] & 0xff;
        if (n == nPad)
        {
          // Padding means end of data
          nBytesToDecode = i;
          break;
        }

        final byte b = n >= aDecodeTable.length ? -1 : aDecodeTable[n];
        if (b < 0)
          throw new DecodeException ("Cannot Base32 decode char " + n);

        aDecodeBuf[i] = b;
      }

      try
      {
        switch (nBytesToDecode)
        {
          case 0:
            break;
          case 2:
            aOS.write (aDecodeBuf[0] << 3 | aDecodeBuf[1] >> 2);
            break;
          case 4:
            aOS.write (aDecodeBuf[0] << 3 | aDecodeBuf[1] >> 2);
            aOS.write (aDecodeBuf[1] << 6 | aDecodeBuf[2] << 1 | aDecodeBuf[3] >> 4);
            break;
          case 5:
            aOS.write (aDecodeBuf[0] << 3 | aDecodeBuf[1] >> 2);
            aOS.write (aDecodeBuf[1] << 6 | aDecodeBuf[2] << 1 | aDecodeBuf[3] >> 4);
            aOS.write (aDecodeBuf[3] << 4 | aDecodeBuf[4] >> 1);
            break;
          case 7:
            aOS.write (aDecodeBuf[0] << 3 | aDecodeBuf[1] >> 2);
            aOS.write (aDecodeBuf[1] << 6 | aDecodeBuf[2] << 1 | aDecodeBuf[3] >> 4);
            aOS.write (aDecodeBuf[3] << 4 | aDecodeBuf[4] >> 1);
            aOS.write (aDecodeBuf[4] << 7 | aDecodeBuf[5] << 2 | aDecodeBuf[6] >> 3);
            break;
          case 8:
            // At least 8 bytes
            aOS.write (aDecodeBuf[0] << 3 | aDecodeBuf[1] >> 2);
            aOS.write (aDecodeBuf[1] << 6 | aDecodeBuf[2] << 1 | aDecodeBuf[3] >> 4);
            aOS.write (aDecodeBuf[3] << 4 | aDecodeBuf[4] >> 1);
            aOS.write (aDecodeBuf[4] << 7 | aDecodeBuf[5] << 2 | aDecodeBuf[6] >> 3);
            aOS.write (aDecodeBuf[6] << 5 | aDecodeBuf[7] & 0xff);
            break;
          default:
            throw new DecodeException ("Unexpected number of Base32 bytes left: " + nBytesToDecode);
        }
      }
      catch (final IOException ex)
      {
        throw new DecodeException ("Failed to decode Base32", ex);
      }
    }
  }
}
