/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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
package com.helger.security.keystore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.lang.EnumHelper;

/**
 * The default key store types.
 *
 * @author Philip Helger
 * @since 9.0.0
 */
public enum EKeyStoreType implements IKeyStoreType
{
  /**
   * The default Java Key Store type. Supported by all relevant Java versions
   * since at least 1.5
   */
  JKS ("JKS"),
  /**
   * The PKCS12 key store type is slightly better than JKS and is supported only
   * in Java 1.9. In Java 1.8 and earlier this is only supported when
   * BouncyCastle is contained.
   */
  PKCS12 ("PKCS12"),
  /**
   * The BCFKS key store is designed to be FIPS compliant. It is available in
   * approved-mode operation and is also capable of storing some secret key
   * types in addition to public/private keys and certificates. The BCFKS key
   * store uses PBKDF2 with HMAC SHA512 for password to key conversion and AES
   * CCM for encryption. Passwords are encoded for conversion into keys using
   * PKCS#12 format (as in each 16 bit character is converted into 2 bytes).
   */
  BCFKS ("BCFKS");

  private final String m_sID;

  EKeyStoreType (@Nonnull @Nonempty final String sID)
  {
    m_sID = sID;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  @Nullable
  public static EKeyStoreType getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EKeyStoreType.class, sID);
  }

  @Nullable
  public static EKeyStoreType getFromIDCaseInsensitiveOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDCaseInsensitiveOrNull (EKeyStoreType.class, sID);
  }

  @Nullable
  public static EKeyStoreType getFromIDCaseInsensitiveOrDefault (@Nullable final String sID, @Nullable final EKeyStoreType eDefault)
  {
    return EnumHelper.getFromIDCaseInsensitiveOrDefault (EKeyStoreType.class, sID, eDefault);
  }
}
