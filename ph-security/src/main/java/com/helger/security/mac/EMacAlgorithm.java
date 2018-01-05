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

import java.security.NoSuchAlgorithmException;
import java.security.Provider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.lang.EnumHelper;
import com.helger.commons.string.StringHelper;

/**
 * A selection of common hash algorithms.
 *
 * @author Philip Helger
 */
public enum EMacAlgorithm
{
  HMAC_MD5 ("HmacMD5"),
  HMAC_SHA1 ("HmacSHA1"),
  HMAC_SHA224 ("HmacSHA224"),
  HMAC_SHA256 ("HmacSHA256"),
  HMAC_SHA384 ("HmacSHA384"),
  HMAC_SHA512 ("HmacSHA512");

  private final String m_sAlgorithm;

  private EMacAlgorithm (@Nonnull @Nonempty final String sAlgorithm)
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
   * @return A new Mac with this algorithm using the default security provider.
   * @throws IllegalStateException
   *         If this algorithm is not supported by this Java runtime.
   */
  @Nonnull
  public Mac createMac ()
  {
    return createMac (null);
  }

  /**
   * @param aSecurityProvider
   *        The security provider to use. May be <code>null</code> to use the
   *        default security provider.
   * @return A new Mac with this algorithm using the provided or the default
   *         security provider.
   * @throws IllegalStateException
   *         If this algorithm is not supported by this Java runtime.
   */
  @Nonnull
  public Mac createMac (@Nullable final Provider aSecurityProvider)
  {
    try
    {
      if (aSecurityProvider == null)
        return Mac.getInstance (m_sAlgorithm);
      return Mac.getInstance (m_sAlgorithm, aSecurityProvider);
    }
    catch (final NoSuchAlgorithmException ex)
    {
      throw new IllegalStateException ("Failed to resolve Mac algorithm '" + m_sAlgorithm + "'", ex);
    }
  }

  /**
   * Create a new {@link SecretKeySpec} with this algorithm and the provided key
   * bytes.
   * 
   * @param aKey
   *        The key bytes to use. May not be <code>null</code>.
   * @return The new {@link SecretKeySpec}.
   */
  @Nonnull
  public SecretKeySpec createSecretKey (@Nonnull final byte [] aKey)
  {
    return new SecretKeySpec (aKey, m_sAlgorithm);
  }

  @Nullable
  public static EMacAlgorithm getFromStringIgnoreCase (@Nullable final String sAlgorithm)
  {
    if (StringHelper.hasNoText (sAlgorithm))
      return null;
    return EnumHelper.findFirst (EMacAlgorithm.class, x -> x.m_sAlgorithm.equalsIgnoreCase (sAlgorithm));
  }
}
