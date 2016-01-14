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
import java.io.OutputStream;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.messagedigest.EMessageDigestAlgorithm;
import com.helger.commons.messagedigest.IMessageDigestGenerator;
import com.helger.commons.messagedigest.NonBlockingMessageDigestGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * A wrapper around an {@link OutputStream} that creates a message digest while
 * writing.
 *
 * @see IMessageDigestGenerator
 * @author Philip Helger
 */
public class MessageDigestOutputStream extends WrappedOutputStream
{
  private final IMessageDigestGenerator m_aMDGen;

  public MessageDigestOutputStream (@Nonnull final OutputStream aSourceOS,
                                    @Nonnull final EMessageDigestAlgorithm eMDAlgorithm)
  {
    super (aSourceOS);
    m_aMDGen = new NonBlockingMessageDigestGenerator (eMDAlgorithm);
  }

  @Override
  public void write (final int n) throws IOException
  {
    super.write (n);
    m_aMDGen.update ((byte) n);
  }

  /**
   * Get the message digest of this stream. Call this only once the writing has
   * been finished. Never call this in the middle of writing a stream, because
   * the digest cannot be updated afterwards.
   *
   * @return The message digest of this stream. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public byte [] getDigest ()
  {
    return m_aMDGen.getAllDigestBytes ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("mdgen", m_aMDGen).toString ();
  }
}
