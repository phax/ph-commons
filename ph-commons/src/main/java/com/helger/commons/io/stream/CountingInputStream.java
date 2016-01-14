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

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.string.ToStringGenerator;

/**
 * A wrapper around an {@link InputStream} that counts the number of read bytes.
 *
 * @author Philip Helger
 */
public class CountingInputStream extends WrappedInputStream
{
  private long m_nBytesRead = 0;
  private long m_nPosition = 0;
  private long m_nMark = 0;

  public CountingInputStream (@Nonnull final InputStream aSourceIS)
  {
    super (aSourceIS);
  }

  @Override
  public int read () throws IOException
  {
    final int ret = super.read ();
    if (ret != -1)
    {
      m_nBytesRead++;
      m_nPosition++;
    }
    return ret;
  }

  @Override
  public int read (final byte [] b, final int nOffset, final int nLength) throws IOException
  {
    final int ret = super.read (b, nOffset, nLength);
    if (ret != -1)
    {
      m_nBytesRead += ret;
      m_nPosition += ret;
    }
    return ret;
  }

  @Override
  public long skip (@Nonnegative final long n) throws IOException
  {
    final long nSkipped = super.skip (n);
    if (nSkipped > 0)
      m_nPosition += nSkipped;
    return nSkipped;
  }

  @SuppressWarnings ("sync-override")
  @Override
  public void mark (@Nonnegative final int nReadlimit)
  {
    // May throw an exception!
    super.mark (nReadlimit);
    m_nMark = m_nPosition;
  }

  @SuppressWarnings ("sync-override")
  @Override
  public void reset () throws IOException
  {
    // May throw an exception!
    super.reset ();
    m_nPosition = m_nMark;
  }

  /**
   * @return The number of read bytes.
   */
  @Nonnegative
  public long getBytesRead ()
  {
    return m_nBytesRead;
  }

  /**
   * @return The current position in the input stream (taking skip and
   *         mark/reset into account)
   */
  @Nonnegative
  public long getPosition ()
  {
    return m_nPosition;
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("bytesRead", m_nBytesRead)
                            .append ("position", m_nPosition)
                            .toString ();
  }
}
