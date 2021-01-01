/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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
import java.io.Reader;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.string.ToStringGenerator;

/**
 * A wrapper around a {@link Reader} that counts the number of read bytes.
 *
 * @author Philip Helger
 * @since 9.3.8
 */
public class CountingReader extends WrappedReader
{
  private long m_nCharsRead = 0;
  private long m_nPosition = 0;
  private long m_nMark = 0;

  public CountingReader (@Nonnull final Reader aSourceReader)
  {
    super (aSourceReader);
  }

  @Override
  public int read () throws IOException
  {
    final int ret = super.read ();
    if (ret != -1)
    {
      m_nCharsRead++;
      m_nPosition++;
    }
    return ret;
  }

  @Override
  public int read (final char [] aBuf, final int nOffset, final int nLength) throws IOException
  {
    final int ret = super.read (aBuf, nOffset, nLength);
    if (ret != -1)
    {
      m_nCharsRead += ret;
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

  @Override
  public void mark (@Nonnegative final int nReadlimit) throws IOException
  {
    super.mark (nReadlimit);
    m_nMark = m_nPosition;
  }

  @Override
  public void reset () throws IOException
  {
    super.reset ();
    m_nPosition = m_nMark;
  }

  /**
   * @return The number of read chars.
   */
  @Nonnegative
  public final long getCharsRead ()
  {
    return m_nCharsRead;
  }

  /**
   * @return The current position in the input stream (taking skip and
   *         mark/reset into account)
   */
  @Nonnegative
  public final long getPosition ()
  {
    return m_nPosition;
  }

  /**
   * @return The current mark
   */
  @Nonnegative
  public final long getMark ()
  {
    return m_nMark;
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("CharsRead", m_nCharsRead)
                            .append ("Position", m_nPosition)
                            .append ("Mark", m_nMark)
                            .getToString ();
  }
}
