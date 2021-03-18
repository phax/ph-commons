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
import java.io.Writer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.string.ToStringGenerator;

/**
 * A wrapper around an {@link Writer} that counts the number of read chars.
 *
 * @author Philip Helger
 * @since 9.3.8
 */
public class CountingWriter extends WrappedWriter
{
  private long m_nCharsWritten = 0;

  public CountingWriter (@Nonnull final Writer aSourceOS)
  {
    super (aSourceOS);
  }

  @Override
  public void write (final int b) throws IOException
  {
    super.write (b);
    m_nCharsWritten++;
  }

  @Override
  public void write (@Nonnull final char [] aBuf, final int nOfs, final int nLen) throws IOException
  {
    super.write (aBuf, nOfs, nLen);
    m_nCharsWritten += nLen;
  }

  /**
   * @return The number of written chars.
   */
  @Nonnegative
  public final long getCharsWritten ()
  {
    return m_nCharsWritten;
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("CharsWritten", m_nCharsWritten).getToString ();
  }
}
