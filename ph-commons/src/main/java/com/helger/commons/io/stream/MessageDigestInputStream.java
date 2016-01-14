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

import javax.annotation.Nonnull;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.messagedigest.EMessageDigestAlgorithm;
import com.helger.commons.messagedigest.IMessageDigestGenerator;
import com.helger.commons.messagedigest.NonBlockingMessageDigestGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * A wrapper around an {@link InputStream} that performs a hashing while
 * reading.
 *
 * @see IMessageDigestGenerator
 * @author Philip Helger
 */
public class MessageDigestInputStream extends WrappedInputStream
{
  private final IMessageDigestGenerator m_aMDGen;

  public MessageDigestInputStream (@Nonnull final InputStream aSourceIS,
                                   @Nonnull final EMessageDigestAlgorithm eMDAlgorithm)
  {
    super (aSourceIS);
    m_aMDGen = new NonBlockingMessageDigestGenerator (eMDAlgorithm);
  }

  @Override
  public int read () throws IOException
  {
    final int ret = super.read ();
    if (ret != -1)
      m_aMDGen.update ((byte) ret);
    return ret;
  }

  @Override
  public int read (final byte [] b, final int nOffset, final int nLength) throws IOException
  {
    final int ret = super.read (b, nOffset, nLength);
    if (ret != -1)
      m_aMDGen.update (b, nOffset, ret);
    return ret;
  }

  /**
   * Get the message digest of this stream. Call this only once the reading has
   * been finished. Never call this in the middle of reading a stream, because
   * the digest cannot be updated afterwards.
   *
   * @return The message digest of this stream. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public byte [] getAllDigestBytes ()
  {
    return m_aMDGen.getAllDigestBytes ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("mdgen", m_aMDGen).toString ();
  }
}
