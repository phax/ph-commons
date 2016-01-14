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
package com.helger.commons.base64;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * A {@link Base64InputStream} will read data from another <tt>InputStream</tt>,
 * given in the constructor, and encode/decode to/from Base64 notation on the
 * fly.
 *
 * @see Base64
 * @since 1.3
 */
public class Base64InputStream extends FilterInputStream
{
  // Encoding or decoding
  private final boolean m_bEncode;
  // Current position in the buffer
  private int m_nPosition;
  // Small buffer holding converted data
  private final byte [] m_aBuffer;
  // Length of buffer (3 or 4)
  private final int m_nBufferLength;
  // Number of meaningful bytes in the buffer
  private int m_nNumSigBytes;
  private int m_nLineLength;
  // Break lines at less than 80 characters
  private final boolean m_bBreakLines;
  // Record options used to create the stream.
  private final int m_nOptions;
  // Local copies to avoid extra method calls
  private final byte [] m_aDecodabet;

  /**
   * Constructs a {@link Base64InputStream} in DECODE mode.
   *
   * @param pin
   *        the <tt>InputStream</tt> from which to read data.
   * @since 1.3
   */
  public Base64InputStream (@Nonnull final InputStream pin)
  {
    this (pin, Base64.DECODE);
  }

  /**
   * Constructs a {@link Base64InputStream} in either ENCODE or DECODE mode.
   * <p>
   * Valid options:
   *
   * <pre>
   *   ENCODE or DECODE: Encode or Decode as data is read.
   *   DO_BREAK_LINES: break lines at 76 characters
   *     (only meaningful when encoding)
   * </pre>
   * <p>
   * Example: <code>new Base64.InputStream( in, Base64.DECODE )</code>
   *
   * @param pin
   *        the <tt>InputStream</tt> from which to read data.
   * @param poptions
   *        Specified options
   * @see Base64#ENCODE
   * @see Base64#DECODE
   * @see Base64#DO_BREAK_LINES
   * @since 2.0
   */
  public Base64InputStream (@Nonnull final InputStream pin, final int poptions)
  {
    super (pin);
    m_nOptions = poptions; // Record for later
    m_bBreakLines = (poptions & Base64.DO_BREAK_LINES) > 0;
    m_bEncode = (poptions & Base64.ENCODE) > 0;
    m_nBufferLength = m_bEncode ? 4 : 3;
    m_aBuffer = new byte [m_nBufferLength];
    m_nPosition = -1;
    m_nLineLength = 0;
    m_aDecodabet = Base64._getDecodabet (poptions);
  }

  /**
   * Reads enough of the input stream to convert to/from Base64 and returns the
   * next byte.
   *
   * @return next byte
   * @since 1.3
   */
  @Override
  public int read () throws IOException
  {
    // Do we need to get data?
    if (m_nPosition < 0)
    {
      if (m_bEncode)
      {
        final byte [] b3 = new byte [3];
        int numBinaryBytes = 0;
        for (int i = 0; i < 3; i++)
        {
          final int b = in.read ();

          // If end of stream, b is -1.
          if (b < 0)
            break;

          b3[i] = (byte) b;
          numBinaryBytes++;
        }

        if (numBinaryBytes > 0)
        {
          Base64._encode3to4 (b3, 0, numBinaryBytes, m_aBuffer, 0, m_nOptions);
          m_nPosition = 0;
          m_nNumSigBytes = 4;
        }
        else
        {
          return -1; // Must be end of stream
        }
      }

      // Else decoding
      else
      {
        final byte [] b4 = new byte [4];
        int i;
        for (i = 0; i < 4; i++)
        {
          // Read four "meaningful" bytes:
          int b;
          do
          {
            b = in.read ();
          } while (b >= 0 && m_aDecodabet[b & 0x7f] <= Base64.WHITE_SPACE_ENC);

          if (b < 0)
            break; // Reads a -1 if end of stream

          b4[i] = (byte) b;
        }

        if (i == 4)
        {
          m_nNumSigBytes = Base64._decode4to3 (b4, 0, m_aBuffer, 0, m_nOptions);
          m_nPosition = 0;
        }
        else
          if (i == 0)
          {
            return -1;
          }
          else
          {
            // Must have broken out from above.
            throw new IOException ("Improperly padded Base64 input.");
          } // end

      }
    }

    // Got data?
    if (m_nPosition >= 0)
    {
      if ( /* !encode && */m_nPosition >= m_nNumSigBytes)
      {
        return -1;
      }

      if (m_bEncode && m_bBreakLines && m_nLineLength >= Base64.MAX_LINE_LENGTH)
      {
        m_nLineLength = 0;
        return '\n';
      }
      {
        m_nLineLength++; // This isn't important when decoding
        // but throwing an extra "if" seems
        // just as wasteful.

        final int b = m_aBuffer[m_nPosition++];

        if (m_nPosition >= m_nBufferLength)
          m_nPosition = -1;

        return b & 0xFF; // This is how you "cast" a byte that's
                         // intended to be unsigned.
      }
    }

    // Else error
    throw new IOException ("Error in Base64 code reading stream.");
  }

  /**
   * Calls {@link #read()} repeatedly until the end of stream is reached or
   * <em>len</em> bytes are read. Returns number of bytes read into array or -1
   * if end of stream is encountered.
   *
   * @param aDest
   *        array to hold values
   * @param nOfs
   *        offset for array
   * @param nLen
   *        max number of bytes to read into array
   * @return bytes read into array or -1 if end of stream is encountered.
   * @since 1.3
   */
  @Override
  public int read (@Nonnull final byte [] aDest,
                   @Nonnegative final int nOfs,
                   @Nonnegative final int nLen) throws IOException
  {
    int i = 0;
    for (; i < nLen; i++)
    {
      final int b = read ();

      if (b >= 0)
        aDest[nOfs + i] = (byte) b;
      else
        if (i == 0)
          return -1;
        else
        {
          // Out of 'for' loop
          break;
        }
    }
    return i;
  }
}
