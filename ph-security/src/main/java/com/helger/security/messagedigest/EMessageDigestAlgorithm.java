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
package com.helger.security.messagedigest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.lang.EnumHelper;
import com.helger.commons.string.StringHelper;

/**
 * A selection of common message digest (hash) algorithms.
 *
 * @author Philip Helger
 */
public enum EMessageDigestAlgorithm
{
  MD5 ("MD5"),
  SHA_1 ("SHA-1"),
  SHA_224 ("SHA-224"),
  SHA_256 ("SHA-256"),
  SHA_384 ("SHA-384"),
  SHA_512 ("SHA-512");

  private final String m_sAlgorithm;

  private EMessageDigestAlgorithm (@Nonnull @Nonempty final String sAlgorithm)
  {
    m_sAlgorithm = sAlgorithm;
  }

  /**
   * @return The internal name of the message digest algorithm. Neither
   *         <code>null</code> nor empty.
   */
  @Nonnull
  @Nonempty
  public String getAlgorithm ()
  {
    return m_sAlgorithm;
  }

  /**
   * @return A new message digest with this message digest algorithm using the
   *         default security provider.
   * @throws IllegalStateException
   *         If this algorithm is not supported by this Java runtime.
   */
  @Nonnull
  public MessageDigest createMessageDigest ()
  {
    return createMessageDigest (null);
  }

  /**
   * @param aSecurityProvider
   *        The security provider to use. May be <code>null</code> to use the
   *        default security provider.
   * @return A new message digest with this message digest algorithm using the
   *         provided or the default security provider.
   * @throws IllegalStateException
   *         If this algorithm is not supported by this Java runtime.
   */
  @Nonnull
  public MessageDigest createMessageDigest (@Nullable final Provider aSecurityProvider)
  {
    try
    {
      if (aSecurityProvider == null)
        return MessageDigest.getInstance (m_sAlgorithm);
      return MessageDigest.getInstance (m_sAlgorithm, aSecurityProvider);
    }
    catch (final NoSuchAlgorithmException ex)
    {
      throw new IllegalStateException ("Failed to resolve MessageDigest algorithm '" + m_sAlgorithm + "'", ex);
    }
  }

  @Nullable
  public static EMessageDigestAlgorithm getFromStringIgnoreCase (@Nullable final String sAlgorithm)
  {
    if (StringHelper.hasNoText (sAlgorithm))
      return null;
    return EnumHelper.findFirst (EMessageDigestAlgorithm.class, x -> x.m_sAlgorithm.equalsIgnoreCase (sAlgorithm));
  }
}
