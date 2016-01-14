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
import java.io.Writer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.CGlobal;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.system.SystemProperties;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * This is a non-blocking version of {@link java.io.BufferedWriter}. It is 1:1
 * rip without the synchronized statements.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class NonBlockingBufferedWriter extends Writer
{
  private static final int DEFAULT_CHAR_BUFFER_SIZE = 16 * CGlobal.BYTES_PER_KILOBYTE;

  private Writer m_aWriter;
  private char [] m_aBuf;
  private final int m_nChars;
  private int m_nNextChar;

  /**
   * Line separator string. This is the value of the line.separator property at
   * the moment that the stream was created.
   */
  private final String m_sLineSeparator;

  /**
   * Creates a buffered character-output stream that uses a default-sized output
   * buffer.
   *
   * @param aWriter
   *        A Writer
   */
  public NonBlockingBufferedWriter (@Nonnull final Writer aWriter)
  {
    this (aWriter, DEFAULT_CHAR_BUFFER_SIZE);
  }

  /**
   * Creates a new buffered character-output stream that uses an output buffer
   * of the given size.
   *
   * @param aWriter
   *        A Writer
   * @param nBufSize
   *        Output-buffer size, a positive integer
   * @exception IllegalArgumentException
   *            If size is &le; 0
   */
  public NonBlockingBufferedWriter (@Nonnull final Writer aWriter, @Nonnegative final int nBufSize)
  {
    super (aWriter);
    ValueEnforcer.isGT0 (nBufSize, "BufSize");
    m_aWriter = aWriter;
    m_aBuf = new char [nBufSize];
    m_nChars = nBufSize;
    m_nNextChar = 0;

    m_sLineSeparator = SystemProperties.getLineSeparator ();
  }

  /**
   * Checks to make sure that the stream has not been closed
   *
   * @throws IOException
   *         of the writer is not open
   */
  private void _ensureOpen () throws IOException
  {
    if (m_aWriter == null)
      throw new IOException ("Stream closed");
  }

  /**
   * Flushes the output buffer to the underlying character stream, without
   * flushing the stream itself. This method is non-private only so that it may
   * be invoked by PrintStream.
   *
   * @throws IOException
   *         of the writer is not open
   */
  void flushBuffer () throws IOException
  {
    _ensureOpen ();
    if (m_nNextChar != 0)
    {
      m_aWriter.write (m_aBuf, 0, m_nNextChar);
      m_nNextChar = 0;
    }
  }

  /**
   * Writes a single character.
   *
   * @exception IOException
   *            If an I/O error occurs
   */
  @Override
  public void write (final int c) throws IOException
  {
    _ensureOpen ();
    if (m_nNextChar >= m_nChars)
      flushBuffer ();
    m_aBuf[m_nNextChar++] = (char) c;
  }

  /**
   * Writes a portion of an array of characters.
   * <p>
   * Ordinarily this method stores characters from the given array into this
   * stream's buffer, flushing the buffer to the underlying stream as needed. If
   * the requested length is at least as large as the buffer, however, then this
   * method will flush the buffer and write the characters directly to the
   * underlying stream. Thus redundant <code>BufferedWriter</code>s will not
   * copy data unnecessarily.
   *
   * @param cbuf
   *        A character array
   * @param nOfs
   *        Offset from which to start reading characters
   * @param nLen
   *        Number of characters to write
   * @exception IOException
   *            If an I/O error occurs
   */
  @Override
  @SuppressFBWarnings ("IL_INFINITE_LOOP")
  public void write (final char [] cbuf, final int nOfs, final int nLen) throws IOException
  {
    _ensureOpen ();
    ValueEnforcer.isArrayOfsLen (cbuf, nOfs, nLen);

    if (nLen == 0)
      return;

    if (nLen >= m_nChars)
    {
      /*
       * If the request length exceeds the size of the output buffer, flush the
       * buffer and then write the data directly. In this way buffered streams
       * will cascade harmlessly.
       */
      flushBuffer ();
      m_aWriter.write (cbuf, nOfs, nLen);
    }
    else
    {
      int b = nOfs;
      final int t = nOfs + nLen;
      while (b < t)
      {
        final int d = Math.min (m_nChars - m_nNextChar, t - b);
        System.arraycopy (cbuf, b, m_aBuf, m_nNextChar, d);
        b += d;
        m_nNextChar += d;
        if (m_nNextChar >= m_nChars)
          flushBuffer ();
      }
    }
  }

  /**
   * Writes a portion of a String.
   * <p>
   * If the value of the <tt>len</tt> parameter is negative then no characters
   * are written. This is contrary to the specification of this method in the
   * {@linkplain java.io.Writer#write(java.lang.String,int,int) superclass},
   * which requires that an {@link IndexOutOfBoundsException} be thrown.
   *
   * @param s
   *        String to be written
   * @param off
   *        Offset from which to start reading characters
   * @param len
   *        Number of characters to be written
   * @exception IOException
   *            If an I/O error occurs
   */
  @Override
  @SuppressFBWarnings ("IL_INFINITE_LOOP")
  public void write (final String s, final int off, final int len) throws IOException
  {
    _ensureOpen ();

    int b = off;
    final int t = off + len;
    while (b < t)
    {
      final int d = Math.min (m_nChars - m_nNextChar, t - b);
      s.getChars (b, b + d, m_aBuf, m_nNextChar);
      b += d;
      m_nNextChar += d;
      if (m_nNextChar >= m_nChars)
        flushBuffer ();
    }
  }

  /**
   * Writes a line separator. The line separator string is defined by the system
   * property <tt>line.separator</tt>, and is not necessarily a single newline
   * ('\n') character.
   *
   * @exception IOException
   *            If an I/O error occurs
   */
  public void newLine () throws IOException
  {
    write (m_sLineSeparator);
  }

  /**
   * Flushes the stream.
   *
   * @exception IOException
   *            If an I/O error occurs
   */
  @Override
  public void flush () throws IOException
  {
    flushBuffer ();
    m_aWriter.flush ();
  }

  @Override
  public void close () throws IOException
  {
    if (m_aWriter != null)
    {
      try
      {
        flushBuffer ();
      }
      finally
      {
        m_aWriter.close ();
        m_aWriter = null;
        m_aBuf = null;
      }
    }
  }
}
