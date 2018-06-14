/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.charset.utf7;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

import javax.annotation.Nonnull;

/**
 * <p>
 * The CharsetEncoder used to encode both variants of the UTF-7 charset and the
 * modified-UTF-7 charset.
 * </p>
 * <p>
 * <strong>Please note this class does not behave strictly according to the
 * specification in Sun Java VMs before 1.6.</strong> This is done to get around
 * a bug in the implementation of
 * {@link java.nio.charset.CharsetEncoder#encode(CharBuffer)}. Unfortunately,
 * that method cannot be overridden.
 * </p>
 *
 * @see <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6221056">JDK
 *      bug 6221056</a>
 * @author Jaap Beetstra
 */
final class UTF7StyleCharsetEncoder extends CharsetEncoder
{
  private static final float AVG_BYTES_PER_CHAR = 1.5f;
  private static final float MAX_BYTES_PER_CHAR = 5.0f;

  private final AbstractUTF7StyleCharset m_aCharset;
  private final UTF7Base64Helper m_aBase64;
  private final byte m_nShift;
  private final byte m_nUnshift;
  private final boolean m_bStrict;
  private boolean m_bBase64mode;
  private int m_nBitsToOutput;
  private int m_nSextet;

  UTF7StyleCharsetEncoder (@Nonnull final AbstractUTF7StyleCharset aCharset,
                           @Nonnull final UTF7Base64Helper aBase64,
                           final boolean bStrict)
  {
    super (aCharset, AVG_BYTES_PER_CHAR, MAX_BYTES_PER_CHAR);
    m_aCharset = aCharset;
    m_aBase64 = aBase64;
    m_bStrict = bStrict;
    m_nShift = aCharset.shift ();
    m_nUnshift = aCharset.unshift ();
  }

  @Override
  protected void implReset ()
  {
    m_bBase64mode = false;
    m_nSextet = 0;
    m_nBitsToOutput = 0;
  }

  /**
   * {@inheritDoc}
   * <p>
   * Note that this method might return <code>CoderResult.OVERFLOW</code> (as is
   * required by the specification) if insufficient space is available in the
   * output buffer. However, calling it again on JDKs before Java 6 triggers a
   * bug in {@link java.nio.charset.CharsetEncoder#flush(ByteBuffer)} causing it
   * to throw an IllegalStateException (the buggy method is <code>final</code>,
   * thus cannot be overridden).
   * </p>
   *
   * @see <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6227608">
   *      JDK bug 6227608</a>
   * @param out
   *        The output byte buffer
   * @return A coder-result object describing the reason for termination
   */
  @Override
  protected CoderResult implFlush (final ByteBuffer out)
  {
    if (m_bBase64mode)
    {
      if (out.remaining () < 2)
        return CoderResult.OVERFLOW;
      if (m_nBitsToOutput != 0)
        out.put (m_aBase64.getChar (m_nSextet));
      out.put (m_nUnshift);
    }
    return CoderResult.UNDERFLOW;
  }

  /**
   * {@inheritDoc}
   * <p>
   * Note that this method might return <code>CoderResult.OVERFLOW</code>, even
   * though there is sufficient space available in the output buffer. This is
   * done to force the broken implementation of
   * {@link java.nio.charset.CharsetEncoder#encode(CharBuffer)} to call flush
   * (the buggy method is <code>final</code>, thus cannot be overridden).
   * </p>
   * <p>
   * However, String.getBytes() fails if CoderResult.OVERFLOW is returned, since
   * this assumes it always allocates sufficient bytes (maxBytesPerChar *
   * nr_of_chars). Thus, as an extra check, the size of the input buffer is
   * compared against the size of the output buffer. A static variable is used
   * to indicate if a broken java version is used.
   * </p>
   * <p>
   * It is not possible to directly write the last few bytes, since more bytes
   * might be waiting to be encoded then those available in the input buffer.
   * </p>
   *
   * @see <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6221056">
   *      JDK bug 6221056</a>
   * @param in
   *        The input character buffer
   * @param out
   *        The output byte buffer
   * @return A coder-result object describing the reason for termination
   */
  @Override
  protected CoderResult encodeLoop (final CharBuffer in, final ByteBuffer out)
  {
    while (in.hasRemaining ())
    {
      if (out.remaining () < 4)
        return CoderResult.OVERFLOW;
      final char ch = in.get ();
      if (m_aCharset.canEncodeDirectly (ch))
      {
        _unshift (out, ch);
        out.put ((byte) ch);
      }
      else
        if (!m_bBase64mode && ch == m_nShift)
        {
          out.put (m_nShift);
          out.put (m_nUnshift);
        }
        else
          _encodeBase64 (ch, out);
    }

    return CoderResult.UNDERFLOW;
  }

  /**
   * <p>
   * Writes the bytes necessary to leave <i>base 64 mode</i>. This might include
   * an unshift character.
   * </p>
   *
   * @param out
   * @param ch
   */
  private void _unshift (final ByteBuffer out, final char ch)
  {
    if (!m_bBase64mode)
      return;
    if (m_nBitsToOutput != 0)
      out.put (m_aBase64.getChar (m_nSextet));
    if (m_aBase64.contains (ch) || ch == m_nUnshift || m_bStrict)
      out.put (m_nUnshift);
    m_bBase64mode = false;
    m_nSextet = 0;
    m_nBitsToOutput = 0;
  }

  /**
   * <p>
   * Writes the bytes necessary to encode a character in <i>base 64 mode</i>.
   * All bytes which are fully determined will be written. The fields
   * <code>bitsToOutput</code> and <code>sextet</code> are used to remember the
   * bytes not yet fully determined.
   * </p>
   *
   * @param out
   * @param ch
   */
  private void _encodeBase64 (final char ch, final ByteBuffer out)
  {
    if (!m_bBase64mode)
      out.put (m_nShift);
    m_bBase64mode = true;
    m_nBitsToOutput += 16;
    while (m_nBitsToOutput >= 6)
    {
      m_nBitsToOutput -= 6;
      m_nSextet += (ch >> m_nBitsToOutput);
      m_nSextet &= 0x3F;
      out.put (m_aBase64.getChar (m_nSextet));
      m_nSextet = 0;
    }
    m_nSextet = (ch << (6 - m_nBitsToOutput)) & 0x3F;
  }
}
