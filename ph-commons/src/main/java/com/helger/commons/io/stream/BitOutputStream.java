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

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;

import javax.annotation.Nonnull;

import com.helger.commons.concurrent.SimpleLock;

/**
 * The BitOutputStream allows writing individual bits to a general Java
 * OutputStream. Like the various Stream-classes from Java, the BitOutputStream
 * has to be created based on another OutputStream. This class is able to write
 * a single bit to a stream (even though a byte has to be filled until the data
 * is flushed to the underlying output stream). It is also able to write an
 * integer value to the stream using the specified number of bits.<br>
 * For a non-blocking version see {@link NonBlockingBitOutputStream}.
 *
 * @author Andreas Jakl
 * @author Philip Helger
 */
public class BitOutputStream extends NonBlockingBitOutputStream
{
  private final SimpleLock m_aLock = new SimpleLock ();

  /**
   * Create a new bit output stream based on an existing Java OutputStream.
   *
   * @param aOS
   *        the output stream this class should use. May not be
   *        <code>null</code>.
   * @param aByteOrder
   *        The non-<code>null</code> byte order to use.
   */
  public BitOutputStream (@Nonnull final OutputStream aOS, @Nonnull final ByteOrder aByteOrder)
  {
    super (aOS, aByteOrder);
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
  @Override
  public void writeBit (final int aBit) throws IOException
  {
    m_aLock.lock ();
    try
    {
      super.writeBit (aBit);
    }
    finally
    {
      m_aLock.unlock ();
    }
  }

  /**
   * Write the current cache to the stream and reset the buffer.
   *
   * @throws IOException
   *         In case writing to the output stream failed
   */
  @Override
  public void flush () throws IOException
  {
    m_aLock.lock ();
    try
    {
      super.flush ();
    }
    finally
    {
      m_aLock.unlock ();
    }
  }

  /**
   * Flush the data and close the underlying output stream.
   */
  @Override
  public void close ()
  {
    m_aLock.locked ( () -> super.close ());
  }
}
