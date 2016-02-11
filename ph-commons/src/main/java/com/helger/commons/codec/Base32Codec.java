package com.helger.commons.codec;

import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillNotClose;

import com.helger.commons.charset.CCharset;
import com.helger.commons.exception.InitializationException;
import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;
import com.helger.commons.math.MathHelper;

/**
 * Base32 encoder and decoder based on Apache Commons Codec Base32. Defined in
 * RFC 4648. BASE32 characters are 5 bits in length. They are formed by taking a
 * block of five octets to form a 40-bit string, which is converted into eight
 * BASE32 characters.
 *
 * @see "https://tools.ietf.org/html/rfc4648"
 * @author Philip Helger
 */
public class Base32Codec implements IByteArrayEncoder
{
  /**
   * This array is a lookup table that translates Unicode characters drawn from
   * the "Base32 Alphabet" (as specified in Table 3 of RFC 4648) into their
   * 5-bit positive integer equivalents. Characters that are not in the Base32
   * alphabet but fall within the bounds of the array are translated to -1.
   */
  private static final byte [] DECODE_TABLE = { -1,
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
                                                // 00-0f
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
                                                // 30-3f 2-7
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
                                                // 40-4f A-N
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
                                                25
      // 50-5a O-Z
  };

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
  private static final byte [] HEX_DECODE_TABLE = { -1,
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
                                                    // 00-0f
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
                                                    // 30-3f 2-7
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
                                                    // 40-4f A-N
                                                    25,
                                                    26,
                                                    27,
                                                    28,
                                                    29,
                                                    30,
                                                    31,
                                                    32
      // 50-57 O-V
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
                                                    'V', };

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

  private final byte m_nPad;

  /**
   * Encode table to use.
   */
  private final byte [] m_aEncodeTable;

  /**
   * Decode table to use.
   */
  private final byte [] m_aDecodeTable;

  /**
   * Creates a Base32 codec used for decoding and encoding.
   * <p>
   * When encoding the line length is 0 (no chunking).
   * </p>
   */
  public Base32Codec ()
  {
    this (false);
  }

  /**
   * Creates a Base32 codec used for decoding and encoding.
   * <p>
   * When encoding the line length is 0 (no chunking).
   * </p>
   *
   * @param nPad
   *        byte used as padding byte.
   */
  public Base32Codec (final byte nPad)
  {
    this (false, nPad);
  }

  /**
   * Creates a Base32 codec used for decoding and encoding.
   * <p>
   * When encoding the line length is 0 (no chunking).
   * </p>
   *
   * @param bUseHex
   *        if {@code true} then use Base32 Hex alphabet
   */
  public Base32Codec (final boolean bUseHex)
  {
    this (bUseHex, DEFAULT_PAD);
  }

  /**
   * Creates a Base32 codec used for decoding and encoding.
   * <p>
   * When encoding the line length is 0 (no chunking).
   * </p>
   *
   * @param bUseHex
   *        if {@code true} then use Base32 Hex alphabet
   * @param nPad
   *        byte used as padding byte.
   */
  public Base32Codec (final boolean bUseHex, final byte nPad)
  {
    m_nPad = nPad;
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

    if (isInAlphabet (nPad) || isWhiteSpace (nPad))
      throw new IllegalArgumentException ("pad must not be in alphabet or whitespace");
  }

  /**
   * Returns whether or not the {@code octet} is in the Base32 alphabet.
   *
   * @param nOctet
   *        The value to test
   * @return {@code true} if the value is defined in the the Base32 alphabet
   *         {@code false} otherwise.
   */
  private boolean isInAlphabet (final byte nOctet)
  {
    return nOctet >= 0 && nOctet < m_aDecodeTable.length && m_aDecodeTable[nOctet] != -1;
  }

  /**
   * Checks if a byte value is whitespace or not. Whitespace is taken to mean:
   * space, tab, CR, LF
   *
   * @param byteToCheck
   *        the byte to check
   * @return true if byte is whitespace, false otherwise
   */
  private static boolean isWhiteSpace (final byte byteToCheck)
  {
    return byteToCheck == ' ' || byteToCheck == '\n' || byteToCheck == '\r' || byteToCheck == '\t';
  }

  public void encode (@Nullable final byte [] aBuf,
                      @Nonnegative final int nOfs,
                      @Nonnegative final int nLen,
                      @Nonnull @WillNotClose final OutputStream aBAOS) throws IOException
  {
    if (aBuf == null || nLen == 0)
      return;

    // Save to local variables for performance reasons
    final byte nPad = m_nPad;
    final byte [] aEncodeTable = m_aEncodeTable;

    int nRest = nLen;
    int nIndex = nOfs;
    while (nRest > 0)
    {
      switch (nRest)
      {
        case 1:
        {
          // Only 1 octet; take top 5 bits then remainder
          final int nCur = aBuf[nIndex];
          nIndex += nRest;
          // 8-1*5
          aBAOS.write (aEncodeTable[(nCur >> 3) & MASK_5BITS]);
          // 5-3=2
          aBAOS.write (aEncodeTable[(nCur << 2) & MASK_5BITS]);
          for (int i = 0; i < 6; ++i)
            aBAOS.write (nPad);
          nRest = 0;
          break;
        }
        case 2:
        {
          // 2 octets = 16 bits to use
          final int nCur = aBuf[nIndex] << 8 | aBuf[nIndex + 1];
          nIndex += nRest;
          // 16-1*5
          aBAOS.write (aEncodeTable[(nCur >> 11) & MASK_5BITS]);
          // 16-2*5
          aBAOS.write (aEncodeTable[(nCur >> 6) & MASK_5BITS]);
          // 16-3*5
          aBAOS.write (aEncodeTable[(nCur >> 1) & MASK_5BITS]);
          // 5-1
          aBAOS.write (aEncodeTable[(nCur << 4) & MASK_5BITS]);
          for (int i = 0; i < 4; ++i)
            aBAOS.write (nPad);
          nRest = 0;
          break;
        }
        case 3:
        {
          // 3 octets = 24 bits to use
          final int nCur = aBuf[nIndex] << 16 | aBuf[nIndex + 1] << 8 | aBuf[nIndex + 2];
          nIndex += nRest;
          // 24-1*5
          aBAOS.write (aEncodeTable[(nCur >> 19) & MASK_5BITS]);
          // 24-2*5
          aBAOS.write (aEncodeTable[(nCur >> 14) & MASK_5BITS]);
          // 24-3*5
          aBAOS.write (aEncodeTable[(nCur >> 9) & MASK_5BITS]);
          // 24-4*5
          aBAOS.write (aEncodeTable[(nCur >> 4) & MASK_5BITS]);
          // 5-4
          aBAOS.write (aEncodeTable[(nCur << 1) & MASK_5BITS]);
          for (int i = 0; i < 3; ++i)
            aBAOS.write (nPad);
          nRest = 0;
          break;
        }
        case 4:
        {
          // 4 octets = 32 bits to use
          final int nCur = aBuf[nIndex] << 24 | aBuf[nIndex + 1] << 16 | aBuf[nIndex + 2] << 8 | aBuf[nIndex + 3];
          nIndex += nRest;
          // 32-1*5
          aBAOS.write (aEncodeTable[(nCur >> 27) & MASK_5BITS]);
          // 32-2*5
          aBAOS.write (aEncodeTable[(nCur >> 22) & MASK_5BITS]);
          // 32-3*5
          aBAOS.write (aEncodeTable[(nCur >> 17) & MASK_5BITS]);
          // 32-4*5
          aBAOS.write (aEncodeTable[(nCur >> 12) & MASK_5BITS]);
          // 32-5*5
          aBAOS.write (aEncodeTable[(nCur >> 7) & MASK_5BITS]);
          // 32-6*5
          aBAOS.write (aEncodeTable[(nCur >> 2) & MASK_5BITS]);
          // 5-2
          aBAOS.write (aEncodeTable[(nCur << 3) & MASK_5BITS]);
          aBAOS.write (nPad);
          nRest = 0;
          break;
        }
        default:
        {
          // More than 5 octets = 40 bits to use
          final long nCur = (long) aBuf[nIndex] << 32 |
                            aBuf[nIndex + 1] << 24 |
                            aBuf[nIndex + 2] << 16 |
                            aBuf[nIndex + 3] << 8 |
                            aBuf[nIndex + 4];
          nIndex += 5;
          // 40-1*5
          aBAOS.write (aEncodeTable[(int) (nCur >> 35) & MASK_5BITS]);
          // 40-2*5
          aBAOS.write (aEncodeTable[(int) (nCur >> 30) & MASK_5BITS]);
          // 40-3*5
          aBAOS.write (aEncodeTable[(int) (nCur >> 25) & MASK_5BITS]);
          // 40-4*5
          aBAOS.write (aEncodeTable[(int) (nCur >> 20) & MASK_5BITS]);
          // 40-5*5
          aBAOS.write (aEncodeTable[(int) (nCur >> 15) & MASK_5BITS]);
          // 40-6*5
          aBAOS.write (aEncodeTable[(int) (nCur >> 10) & MASK_5BITS]);
          // 40-7*5
          aBAOS.write (aEncodeTable[(int) (nCur >> 5) & MASK_5BITS]);
          // 40-8*5
          aBAOS.write (aEncodeTable[(int) nCur & MASK_5BITS]);
          nRest -= 5;
          break;
        }
      }
    }
  }

  public static int getEncodedLength (final int nLen)
  {
    if (nLen == 0)
      return 0;
    return MathHelper.getRoundedUp (nLen * 8 / 5, 8);
  }

  @Nullable
  public byte [] getEncoded (@Nullable final byte [] aBuf)
  {
    if (aBuf == null)
      return null;

    return getEncoded (aBuf, 0, aBuf.length);
  }

  @Nullable
  public byte [] getEncoded (@Nullable final byte [] aBuf, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    if (aBuf == null)
      return null;

    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream (getEncodedLength (nLen)))
    {
      encode (aBuf, nOfs, nLen, aBAOS);
      return aBAOS.toByteArray ();
    }
    catch (final IOException ex)
    {
      return null;
    }
  }

  @Nullable
  public String getEncodedAsString (@Nullable final byte [] aBuf)
  {
    if (aBuf == null)
      return null;

    return getEncodedAsString (aBuf, 0, aBuf.length);
  }

  @Nullable
  public String getEncodedAsString (@Nullable final byte [] aBuf,
                                    @Nonnegative final int nOfs,
                                    @Nonnegative final int nLen)
  {
    if (aBuf == null)
      return null;

    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream (getEncodedLength (nLen)))
    {
      encode (aBuf, nOfs, nLen, aBAOS);
      return aBAOS.getAsString (CCharset.DEFAULT_CHARSET_OBJ);
    }
    catch (final IOException ex)
    {
      return null;
    }
  }
}
