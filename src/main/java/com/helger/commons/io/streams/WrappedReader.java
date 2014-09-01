/**
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
import java.io.Reader;
import java.nio.CharBuffer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;

/**
 * A wrapper around another {@link Reader}. Pass through of all {@link Reader}
 * methods.
 * 
 * @author Philip Helger
 */
public class WrappedReader extends Reader
{
  private final Reader m_aWrappedReader;

  public WrappedReader (@Nonnull final Reader aWrappedReader)
  {
    m_aWrappedReader = ValueEnforcer.notNull (aWrappedReader, "WrappedReader");
  }

  @Nonnull
  public Reader getWrappedReader ()
  {
    return m_aWrappedReader;
  }

  @Override
  public int read (@Nonnull final CharBuffer aTarget) throws IOException
  {
    return m_aWrappedReader.read (aTarget);
  }

  @Override
  public int read () throws IOException
  {
    return m_aWrappedReader.read ();
  }

  @Override
  public int read (@Nonnull final char [] aBuf) throws IOException
  {
    return read (aBuf, 0, aBuf.length);
  }

  @Override
  public int read (@Nonnull final char [] aBuf, @Nonnegative final int nOfs, @Nonnegative final int nLen) throws IOException
  {
    return m_aWrappedReader.read (aBuf, nOfs, nLen);
  }

  @Override
  public long skip (@Nonnegative final long n) throws IOException
  {
    return m_aWrappedReader.skip (n);
  }

  @Override
  public boolean ready () throws IOException
  {
    return m_aWrappedReader.ready ();
  }

  @Override
  public boolean markSupported ()
  {
    return m_aWrappedReader.markSupported ();
  }

  @Override
  public void mark (@Nonnegative final int nReadAheadLimit) throws IOException
  {
    m_aWrappedReader.mark (nReadAheadLimit);
  }

  @Override
  public void reset () throws IOException
  {
    m_aWrappedReader.reset ();
  }

  @Override
  public void close () throws IOException
  {
    m_aWrappedReader.close ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("wrappedReader", m_aWrappedReader).toString ();
  }
}
