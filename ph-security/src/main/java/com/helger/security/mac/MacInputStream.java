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
package com.helger.security.mac;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.crypto.Mac;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.io.stream.WrappedInputStream;
import com.helger.commons.string.ToStringGenerator;

/**
 * A transparent stream that updates the associated Mac using the bits going
 * through the stream.
 * <p>
 * To complete the Mac computation, call one of the <code>doFinal</code> methods
 * on the associated Mac after your calls to one of this Mac input stream's
 * {@link #read() read} methods.
 * <p>
 * It is possible to turn this stream on or off (see {@link #setOn(boolean)
 * on}). When it is on, a call to one of the {@code read} methods results in an
 * update on the Mac. But when it is off, the Mac is not updated. The default is
 * for the stream to be on.
 * <p>
 * Note that Mac objects can compute only one digest (see {@link Mac}), so that
 * in order to compute intermediate digests, a caller should retain a handle
 * onto the Mac object, and clone it for each digest to be computed, leaving the
 * original digest untouched.
 *
 * @author Philip Helger
 * @since 9.1.7
 */
public class MacInputStream extends WrappedInputStream
{
  public static final boolean DEFAULT_ON = true;

  /**
   * The Mac associated with this stream.
   */
  private Mac m_aMac;

  private boolean m_bOn = DEFAULT_ON;

  /**
   * Creates a Mac input stream, using the specified input stream and Mac.
   *
   * @param aIS
   *        the input stream.
   * @param aMac
   *        the Mac to associate with this stream.
   */
  public MacInputStream (@Nonnull final InputStream aIS, @Nonnull final Mac aMac)
  {
    super (aIS);
    setMac (aMac);
  }

  /**
   * Returns the Mac associated with this stream.
   *
   * @return the Mac associated with this stream.
   * @see #setMac(Mac)
   */
  @Nonnull
  public final Mac getMac ()
  {
    return m_aMac;
  }

  /**
   * Associates the specified Mac with this stream.
   *
   * @param aMac
   *        the Mac to be associated with this stream.
   * @see #getMac()
   */
  public final void setMac (@Nonnull final Mac aMac)
  {
    ValueEnforcer.notNull (aMac, "Mac");
    m_aMac = aMac;
  }

  /**
   * Turns the function on or off. The default is on. When it is on, a call to
   * one of the {@code read} methods results in an update on the Mac. But when
   * it is off, the Mac is not updated.
   *
   * @param bOn
   *        <code>true</code> to turn the function on, <code>false</code> to
   *        turn it off.
   */
  public final void setOn (final boolean bOn)
  {
    m_bOn = bOn;
  }

  /**
   * @return <code>true</code> if Mac processing is on, <code>false</code> if it
   *         is off
   */
  public final boolean isOn ()
  {
    return m_bOn;
  }

  /**
   * Reads a byte, and updates the Mac (if the function is on). That is, this
   * method reads a byte from the input stream, blocking until the byte is
   * actually read. If the function is on (see {@link #setOn(boolean) on}), this
   * method will then call {@code update} on the Mac associated with this
   * stream, passing it the byte read.
   *
   * @return the byte read.
   * @exception IOException
   *            if an I/O error occurs.
   * @see Mac#update(byte)
   */
  @Override
  public int read () throws IOException
  {
    final int ch = in.read ();
    if (m_bOn && ch != -1)
    {
      m_aMac.update ((byte) ch);
    }
    return ch;
  }

  /**
   * Reads into a byte array, and updates the Mac (if the function is on). That
   * is, this method reads up to {@code len} bytes from the input stream into
   * the array {@code b}, starting at offset {@code off}. This method blocks
   * until the data is actually read. If the function is on (see
   * {@link #setOn(boolean) on}), this method will then call {@code update} on
   * the Mac associated with this stream, passing it the data.
   *
   * @param aBuf
   *        the array into which the data is read.
   * @param nOfs
   *        the starting offset into {@code b} of where the data should be
   *        placed.
   * @param nLen
   *        the maximum number of bytes to be read from the input stream into b,
   *        starting at offset {@code off}.
   * @return the actual number of bytes read. This is less than {@code len} if
   *         the end of the stream is reached prior to reading {@code len}
   *         bytes. -1 is returned if no bytes were read because the end of the
   *         stream had already been reached when the call was made.
   * @exception IOException
   *            if an I/O error occurs.
   * @see Mac#update(byte[], int, int)
   */
  @Override
  public int read (@Nonnull final byte [] aBuf,
                   @Nonnegative final int nOfs,
                   @Nonnegative final int nLen) throws IOException
  {
    final int result = in.read (aBuf, nOfs, nLen);
    if (m_bOn && result != -1)
    {
      m_aMac.update (aBuf, nOfs, result);
    }
    return result;
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("Mac", m_aMac).append ("On", m_bOn).getToString ();
  }
}
