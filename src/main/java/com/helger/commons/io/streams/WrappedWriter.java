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
import java.io.Writer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;

/**
 * A wrapper around another {@link Writer}. Pass through of all {@link Writer}
 * methods.
 * 
 * @author Philip Helger
 */
public class WrappedWriter extends Writer
{
  private final Writer m_aWrappedWriter;

  public WrappedWriter (@Nonnull final Writer aWrappedWriter)
  {
    m_aWrappedWriter = ValueEnforcer.notNull (aWrappedWriter, "WrappedWriter");
  }

  @Nonnull
  public Writer getWrappedWriter ()
  {
    return m_aWrappedWriter;
  }

  @Override
  public void write (final int c) throws IOException
  {
    m_aWrappedWriter.write (c);
  }

  @Override
  public void write (@Nonnull final char [] aBuf) throws IOException
  {
    write (aBuf, 0, aBuf.length);
  }

  @Override
  public void write (@Nonnull final char [] aBuf, @Nonnegative final int nOfs, @Nonnegative final int nLen) throws IOException
  {
    m_aWrappedWriter.write (aBuf, nOfs, nLen);
  }

  @Override
  public void write (@Nonnull final String sStr) throws IOException
  {
    m_aWrappedWriter.write (sStr);
  }

  @Override
  public void write (@Nonnull final String sStr, @Nonnegative final int nOfs, @Nonnegative final int nLen) throws IOException
  {
    m_aWrappedWriter.write (sStr, nOfs, nLen);
  }

  @Override
  public WrappedWriter append (@Nullable final CharSequence csq) throws IOException
  {
    m_aWrappedWriter.append (csq);
    return this;
  }

  @Override
  public WrappedWriter append (@Nullable final CharSequence csq,
                               @Nonnegative final int nStart,
                               @Nonnegative final int nEnd) throws IOException
  {
    m_aWrappedWriter.append (csq, nStart, nEnd);
    return this;
  }

  @Override
  public WrappedWriter append (final char c) throws IOException
  {
    m_aWrappedWriter.append (c);
    return this;
  }

  @Override
  public void flush () throws IOException
  {
    m_aWrappedWriter.flush ();
  }

  @Override
  public void close () throws IOException
  {
    m_aWrappedWriter.close ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("wrappedWriter", m_aWrappedWriter).toString ();
  }
}
