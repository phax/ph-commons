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
package com.helger.commons.messagedigest;

import java.security.Provider;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.string.ToStringGenerator;

/**
 * Base class for creating a cryptographic hash value. Don't mix it up with the
 * {@link com.helger.commons.hashcode.HashCodeGenerator} which is used to
 * generate hash values for Java objects.
 *
 * @author Philip Helger
 */
public final class MessageDigestGenerator implements IMessageDigestGenerator
{
  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
  private final IMessageDigestGenerator m_aMDGen;

  /**
   * Create a default hash generator with the default algorithm.
   */
  public MessageDigestGenerator ()
  {
    m_aMDGen = new NonBlockingMessageDigestGenerator ();
  }

  /**
   * Create a default hash generator with the default algorithm and the
   * specified security provider
   *
   * @param sProvider
   *        Security provider to be used. May be <code>null</code> to indicate
   *        the default.
   */
  public MessageDigestGenerator (@Nullable final String sProvider)
  {
    m_aMDGen = new NonBlockingMessageDigestGenerator (sProvider);
  }

  /**
   * Create a hash generator with a set of possible algorithms to use.
   *
   * @param aAlgorithms
   *        The parameters to test. May not be <code>null</code>.
   * @throws NullPointerException
   *         If the array of algorithms is <code>null</code> or if one element
   *         of the array is <code>null</code>.
   * @throws IllegalArgumentException
   *         If no algorithm was passed or if no applicable algorithm was used.
   */
  public MessageDigestGenerator (@Nonnull @Nonempty final EMessageDigestAlgorithm... aAlgorithms)
  {
    m_aMDGen = new NonBlockingMessageDigestGenerator (aAlgorithms);
  }

  /**
   * Create a hash generator with a set of possible algorithms to use.
   *
   * @param sProvider
   *        Security provider to be used. May be <code>null</code> to indicate
   *        the default.
   * @param aAlgorithms
   *        The parameters to test. May not be <code>null</code>.
   * @throws NullPointerException
   *         If the array of algorithms is <code>null</code> or if one element
   *         of the array is <code>null</code>.
   * @throws IllegalArgumentException
   *         If no algorithm was passed or if no applicable algorithm was used.
   */
  public MessageDigestGenerator (@Nullable final String sProvider,
                                 @Nonnull @Nonempty final EMessageDigestAlgorithm... aAlgorithms)
  {
    m_aMDGen = new NonBlockingMessageDigestGenerator (sProvider, aAlgorithms);
  }

  @Nonnull
  public Provider getSecurityProvider ()
  {
    return m_aMDGen.getSecurityProvider ();
  }

  @Nonnull
  public String getAlgorithmName ()
  {
    return m_aMDGen.getAlgorithmName ();
  }

  @Nonnegative
  public int getDigestLength ()
  {
    return m_aMDGen.getDigestLength ();
  }

  @Nonnull
  public MessageDigestGenerator update (final byte aValue)
  {
    m_aRWLock.writeLocked ( () -> m_aMDGen.update (aValue));
    return this;
  }

  @Nonnull
  public MessageDigestGenerator update (@Nonnull final byte [] aValue,
                                        @Nonnegative final int nOffset,
                                        @Nonnegative final int nLength)
  {
    m_aRWLock.writeLocked ( () -> m_aMDGen.update (aValue, nOffset, nLength));
    return this;
  }

  public void reset ()
  {
    m_aRWLock.writeLocked ( () -> m_aMDGen.reset ());
  }

  @Nonnull
  @ReturnsMutableCopy
  public byte [] getAllDigestBytes ()
  {
    return m_aRWLock.writeLocked ( () -> m_aMDGen.getAllDigestBytes ());
  }

  @Nonnull
  @ReturnsMutableCopy
  public byte [] getAllDigestBytes (@Nonnegative final int nLength)
  {
    // Using a writeLock because it calculates the main digest on first call
    return m_aRWLock.writeLocked ( () -> m_aMDGen.getAllDigestBytes (nLength));
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("MessageDigestGenerator", m_aMDGen).toString ();
  }
}
