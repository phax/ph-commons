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
import java.io.OutputStream;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.string.ToStringGenerator;

/**
 * A wrapper around an {@link OutputStream} that logs what he is doing.
 *
 * @author Philip Helger
 */
public class LoggingOutputStream extends WrappedOutputStream
{
  private static final Logger LOGGER = LoggerFactory.getLogger (LoggingOutputStream.class);

  private long m_nTotalBytesWritten = 0;

  /**
   * @param aSourceOS
   *        The output stream that should be logged. May not be
   *        <code>null</code>.
   */
  public LoggingOutputStream (@Nonnull final OutputStream aSourceOS)
  {
    super (aSourceOS);
  }

  /**
   * @return The number of written bytes. Always &ge; 0.
   */
  @Nonnegative
  public final long getBytesWritten ()
  {
    return m_nTotalBytesWritten;
  }

  @OverrideOnDemand
  protected void onWrite (@Nonnegative final int nBytesWritten, final long nTotalBytesWritten)
  {
    if (LOGGER.isInfoEnabled ())
      LOGGER.info ("Wrote " + nBytesWritten + " byte(s); now at " + nTotalBytesWritten);
  }

  @OverrideOnDemand
  protected void onClose (final long nTotalBytesWritten)
  {
    if (LOGGER.isInfoEnabled ())
      LOGGER.info ("Close at " + nTotalBytesWritten);
  }

  @Override
  public final void write (final int b) throws IOException
  {
    super.write (b);
    m_nTotalBytesWritten++;
    onWrite (1, m_nTotalBytesWritten);
  }

  @Override
  public final void write (@Nonnull final byte [] aBuf, final int nOfs, final int nLen) throws IOException
  {
    super.write (aBuf, nOfs, nLen);
    m_nTotalBytesWritten += nLen;
    onWrite (nLen, m_nTotalBytesWritten);
  }

  @Override
  public final void close () throws IOException
  {
    super.close ();
    onClose (m_nTotalBytesWritten);
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("totalBytesWritten", m_nTotalBytesWritten).getToString ();
  }
}
