/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.io.streams;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;

// ESCA-JAVA0143:
/**
 * A wrapper around another {@link InputStream}. Pass through of all
 * {@link InputStream} methods.
 * 
 * @author Philip Helger
 */
public class WrappedInputStream extends InputStream
{
  private final InputStream m_aWrappedIS;

  public WrappedInputStream (@Nonnull final InputStream aWrappedIS)
  {
    m_aWrappedIS = ValueEnforcer.notNull (aWrappedIS, "WrappedInputStream");
  }

  @Nonnull
  public InputStream getWrappedInputStream ()
  {
    return m_aWrappedIS;
  }

  @Override
  public int read () throws IOException
  {
    return m_aWrappedIS.read ();
  }

  @Override
  public final int read (@Nonnull final byte [] aBuf) throws IOException
  {
    return read (aBuf, 0, aBuf.length);
  }

  @Override
  public int read (@Nonnull final byte [] aBuf, @Nonnegative final int nOfs, @Nonnegative final int nLen) throws IOException
  {
    return m_aWrappedIS.read (aBuf, nOfs, nLen);
  }

  @Override
  public long skip (@Nonnegative final long n) throws IOException
  {
    return m_aWrappedIS.skip (n);
  }

  @Override
  public int available () throws IOException
  {
    return m_aWrappedIS.available ();
  }

  @Override
  public void close () throws IOException
  {
    m_aWrappedIS.close ();
  }

  @Override
  public synchronized void mark (@Nonnegative final int nReadlimit)// NOPMD
  {
    m_aWrappedIS.mark (nReadlimit);
  }

  @Override
  public synchronized void reset () throws IOException// NOPMD
  {
    m_aWrappedIS.reset ();
  }

  @Override
  public boolean markSupported ()
  {
    return m_aWrappedIS.markSupported ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("wrappedIS", m_aWrappedIS).toString ();
  }
}
