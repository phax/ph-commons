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
  HMAC_SHA256 ("HmacSHA256");

  private final String m_sAlgorithm;

  private EMacAlgorithm (@Nonnull @Nonempty final String sAlgorithm)
  {
    m_sAlgorithm = sAlgorithm;
  }

  @Nonnull
  @Nonempty
  public String getAlgorithm ()
  {
    return m_sAlgorithm;
  }

  @Nonnull
  public Mac createMac ()
  {
    return createMac (null);
  }

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
    return EnumHelper.findFirst (EMacAlgorithm.class, e -> e.m_sAlgorithm.equalsIgnoreCase (sAlgorithm));
  }
}
