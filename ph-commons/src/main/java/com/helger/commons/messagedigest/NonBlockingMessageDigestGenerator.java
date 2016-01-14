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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.util.Arrays;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * Base class for creating a cryptographic hash value. Don't mix it up with the
 * {@link com.helger.commons.hashcode.HashCodeGenerator} which is used to
 * generate hash values for Java objects.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public final class NonBlockingMessageDigestGenerator implements IMessageDigestGenerator
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (NonBlockingMessageDigestGenerator.class);

  private final MessageDigest m_aMessageDigest;
  private byte [] m_aDigest;

  @Nonnull
  private static MessageDigest _createMD (@Nullable final String sProvider,
                                          @Nonnull final EMessageDigestAlgorithm... aAlgorithms)
  {
    MessageDigest aMessageDigest = null;
    for (final EMessageDigestAlgorithm eMD : aAlgorithms)
      try
      {
        if (sProvider == null)
          aMessageDigest = MessageDigest.getInstance (eMD.getAlgorithm ());
        else
          aMessageDigest = MessageDigest.getInstance (eMD.getAlgorithm (), sProvider);
        break;
      }
      catch (final NoSuchAlgorithmException ex)
      {
        // Unknown algorithm -> goto next
        s_aLogger.warn ("Unsupported message digest algorithm '" + eMD.getAlgorithm () + "' found");
      }
      catch (final NoSuchProviderException ex)
      {
        // Unknown provider - stop iteration
        throw new IllegalArgumentException ("Unsupported security provider '" + sProvider + "' found", ex);
      }

    if (aMessageDigest == null)
    {
      // None of the passed algorithms was suitable
      throw new IllegalArgumentException ("None of the algorithms in " +
                                          Arrays.toString (aAlgorithms) +
                                          " was applicable!");
    }
    return aMessageDigest;
  }

  /**
   * Create a default hash generator with the default algorithm.
   */
  public NonBlockingMessageDigestGenerator ()
  {
    this (DEFAULT_ALGORITHM);
  }

  /**
   * Create a default hash generator with the default algorithm and the
   * specified security provider
   *
   * @param sProvider
   *        Security provider to be used. May be <code>null</code> to indicate
   *        the default.
   */
  public NonBlockingMessageDigestGenerator (@Nullable final String sProvider)
  {
    this (sProvider, DEFAULT_ALGORITHM);
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
  public NonBlockingMessageDigestGenerator (@Nonnull @Nonempty final EMessageDigestAlgorithm... aAlgorithms)
  {
    this (null, aAlgorithms);
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
  public NonBlockingMessageDigestGenerator (@Nullable final String sProvider,
                                            @Nonnull @Nonempty final EMessageDigestAlgorithm... aAlgorithms)
  {
    ValueEnforcer.notEmpty (aAlgorithms, "Algortihms");
    m_aMessageDigest = _createMD (sProvider, aAlgorithms);
  }

  @Nonnull
  public Provider getSecurityProvider ()
  {
    return m_aMessageDigest.getProvider ();
  }

  @Nonnull
  public String getAlgorithmName ()
  {
    return m_aMessageDigest.getAlgorithm ();
  }

  @Nonnegative
  public int getDigestLength ()
  {
    return m_aMessageDigest.getDigestLength ();
  }

  @Nonnull
  public IMessageDigestGenerator update (final byte aValue)
  {
    if (m_aDigest != null)
      throw new IllegalStateException ("The hash has already been finished. Call reset manually!");
    m_aMessageDigest.update (aValue);
    return this;
  }

  @Nonnull
  public IMessageDigestGenerator update (@Nonnull final byte [] aValue,
                                         @Nonnegative final int nOfs,
                                         @Nonnegative final int nLen)
  {
    ValueEnforcer.isArrayOfsLen (aValue, nOfs, nLen);
    if (m_aDigest != null)
      throw new IllegalStateException ("The hash has already been finished. Call reset manually!");

    m_aMessageDigest.update (aValue, nOfs, nLen);
    return this;
  }

  public void reset ()
  {
    m_aMessageDigest.reset ();
    m_aDigest = null;
  }

  @Nonnull
  @ReturnsMutableObject ("design")
  private byte [] _getAllDigestBytes ()
  {
    if (m_aDigest == null)
      m_aDigest = m_aMessageDigest.digest ();
    return m_aDigest;
  }

  @Nonnull
  @ReturnsMutableCopy
  public byte [] getAllDigestBytes ()
  {
    return ArrayHelper.getCopy (_getAllDigestBytes ());
  }

  @Nonnull
  @ReturnsMutableCopy
  public byte [] getAllDigestBytes (@Nonnegative final int nLength)
  {
    return ArrayHelper.getCopy (_getAllDigestBytes (), 0, nLength);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("messageDigest", m_aMessageDigest)
                                       .appendIfNotNull ("digest", m_aDigest)
                                       .toString ();
  }
}
