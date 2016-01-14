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
package com.helger.commons.io.stream;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.CGlobal;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;

/**
 * The {@link NonBlockingBitOutputStream} allows writing individual bits to a
 * general Java OutputStream. Like the various Stream-classes from Java, the
 * BitOutputStream has to be created based on another OutputStream. This class
 * is able to write a single bit to a stream (even though a byte has to be
 * filled until the data is flushed to the underlying output stream). It is also
 * able to write an integer value to the stream using the specified number of
 * bits.<br>
 * For a thread-safe version see {@link BitOutputStream}
 *
 * @author Andreas Jakl
 * @author Philip Helger
 */
public class NonBlockingBitOutputStream implements Closeable, Flushable
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (NonBlockingBitOutputStream.class);

  /**
   * The Java OutputStream that is used to write completed bytes.
   */
  private OutputStream m_aOS;

  private final boolean m_bHighOrderBitFirst;

  /**
   * The temporary buffer containing the individual bits until a byte has been
   * completed and can be committed to the output stream.
   */
  private int m_nBuffer;

  /**
   * Counts how many bits have been cached up to now.
   */
  private int m_nBufferedBitCount;

  /**
   * Create a new bit output stream based on an existing Java OutputStream.
   *
   * @param aOS
   *        the output stream this class should use. May not be
   *        <code>null</code>.
   * @param aByteOrder
   *        The non-<code>null</code> byte order to use.
   */
  public NonBlockingBitOutputStream (@Nonnull final OutputStream aOS, @Nonnull final ByteOrder aByteOrder)
  {
    ValueEnforcer.notNull (aOS, "OutputStream");
    ValueEnforcer.notNull (aByteOrder, "ByteOrder");

    m_aOS = aOS;
    m_bHighOrderBitFirst = aByteOrder.equals (ByteOrder.LITTLE_ENDIAN);
    m_nBufferedBitCount = 0;
  }

  /**
   * @return The byte order used. Never <code>null</code>.
   */
  @Nonnull
  public ByteOrder getByteOrder ()
  {
    return m_bHighOrderBitFirst ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
  }

  /**
   * Write a single bit to the stream. It will only be flushed to the underlying
   * OutputStream when a byte has been completed or when flush() manually.
   *
   * @param aBit
   *        1 if the bit should be set, 0 if not
   * @throws IOException
   *         In case writing to the output stream failed
   */
  public void writeBit (final int aBit) throws IOException
  {
    if (m_aOS == null)
      throw new IllegalStateException ("BitOutputStream is already closed");
    if (aBit != CGlobal.BIT_NOT_SET && aBit != CGlobal.BIT_SET)
      throw new IllegalArgumentException (aBit + " is not a bit");

    if (aBit == CGlobal.BIT_SET)
      if (m_bHighOrderBitFirst)
        m_nBuffer |= (aBit << (7 - m_nBufferedBitCount));
      else
        m_nBuffer |= (aBit << m_nBufferedBitCount);

    if (++m_nBufferedBitCount == CGlobal.BITS_PER_BYTE)
      flush ();
  }

  /**
   * Write the specified number of bits from the int value to the stream.
   * Corresponding to the InputStream, the bits are written starting at the
   * highest bit ( &gt;&gt; aNumberOfBits ), going down to the lowest bit (
   * &gt;&gt; 0 ).
   *
   * @param aValue
   *        the int containing the bits that should be written to the stream.
   * @param nNumBits
   *        how many bits of the integer should be written to the stream.
   * @throws IOException
   *         In case writing to the output stream failed
   */
  public void writeBits (final int aValue, @Nonnegative final int nNumBits) throws IOException
  {
    ValueEnforcer.isBetweenInclusive (nNumBits, "NumberOfBits", 1, CGlobal.BITS_PER_INT);

    for (int i = nNumBits - 1; i >= 0; i--)
      writeBit ((aValue >> i) & 1);
  }

  /**
   * Write the current cache to the stream and reset the buffer.
   *
   * @throws IOException
   *         In case writing to the output stream failed
   */
  public void flush () throws IOException
  {
    if (m_nBufferedBitCount > 0)
    {
      if (m_nBufferedBitCount != CGlobal.BITS_PER_BYTE)
        if (s_aLogger.isDebugEnabled ())
          s_aLogger.debug ("Flushing BitOutputStream with only " + m_nBufferedBitCount + " bits");
      m_aOS.write ((byte) m_nBuffer);
      m_nBufferedBitCount = 0;
      m_nBuffer = 0;
    }
  }

  /**
   * Flush the data and close the underlying output stream.
   */
  public void close ()
  {
    StreamHelper.flush (this);
    StreamHelper.close (m_aOS);
    m_aOS = null;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("OS", m_aOS)
                                       .append ("highOrderBitFirst", m_bHighOrderBitFirst)
                                       .append ("buffer", m_nBuffer)
                                       .append ("bitCount", m_nBufferedBitCount)
                                       .toString ();
  }
}
