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
import java.io.InputStream;
import java.nio.ByteOrder;

import javax.annotation.Nonnull;

import com.helger.commons.concurrent.SimpleLock;

/**
 * The {@link BitInputStream} allows reading individual bits from a general Java
 * InputStream. Like the various Stream-classes from Java, the BitInputStream
 * has to be created based on another Input stream. It provides a function to
 * read the next bit from the stream, as well as to read multiple bits at once
 * and write the resulting data into an integer value.<br>
 * For a non-blocking version see {@link NonBlockingBitInputStream}.
 *
 * @author Andreas Jakl
 * @author Philip Helger
 */
public class BitInputStream extends NonBlockingBitInputStream
{
  private final SimpleLock m_aLock = new SimpleLock ();

  /**
   * Create a new bit input stream based on an existing Java InputStream.
   *
   * @param aIS
   *        the input stream this class should read the bits from. May not be
   *        <code>null</code>.
   * @param aByteOrder
   *        The non-<code>null</code> byte order to use.
   */
  public BitInputStream (@Nonnull final InputStream aIS, @Nonnull final ByteOrder aByteOrder)
  {
    super (aIS, aByteOrder);
  }

  /**
   * Read the next bit from the stream.
   *
   * @return 0 if the bit is 0, 1 if the bit is 1.
   * @throws IOException
   *         In case EOF is reached
   */
  @Override
  public int readBit () throws IOException
  {
    m_aLock.lock ();
    try
    {
      return super.readBit ();
    }
    finally
    {
      m_aLock.unlock ();
    }
  }

  /**
   * Close the underlying input stream.
   */
  @Override
  public void close ()
  {
    m_aLock.locked ( () -> super.close ());
  }
}
