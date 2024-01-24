/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
  JKS ("JKS", true),
  /**
   * The PKCS11 key store type is used for secure storage such as Smart Cards
   * and HSM. A key store path is not required (and not supported) for such key
   * store types.
   *
   * @since 11.0.3
   */
  PKCS11 ("PKCS11", false),
  /**
   * The PKCS12 key store type is slightly better than JKS and is supported only
   * in Java 1.9. In Java 1.8 and earlier this is only supported when
   * BouncyCastle is contained.
   */
  PKCS12 ("PKCS12", true),
  /**
   * The first "BKS" is a keystore that will work with the keytool in the same
   * fashion as the Sun "JKS" keystore. The keystore is resistant to tampering
   * but not inspection.<br>
   * This keystore type requires the BouncyCastle Security Provider.
   *
   * @since 11.1.2
   */
  BKS ("BKS", true),
  /**
   * The BCFKS key store is designed to be FIPS compliant. It is available in
   * approved-mode operation and is also capable of storing some secret key
   * types in addition to public/private keys and certificates. The BCFKS key
   * store uses PBKDF2 with HMAC SHA512 for password to key conversion and AES
   * CCM for encryption. Passwords are encoded for conversion into keys using
   * PKCS#12 format (as in each 16 bit character is converted into 2 bytes).<br>
   * This keystore type requires the BouncyCastle FIPS Security Provider.
   *
   * @since 9.2.1
   */
  BCFKS ("BCFKS", true);

  private final String m_sID;
  private final boolean m_bKeyStorePathRequired;

  EKeyStoreType (@Nonnull @Nonempty final String sID, final boolean bKeyStorePathRequired)
  {
    m_sID = sID;
    m_bKeyStorePathRequired = bKeyStorePathRequired;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  public boolean isKeyStorePathRequired ()
  {
    return m_bKeyStorePathRequired;
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
  public static EKeyStoreType getFromIDCaseInsensitiveOrDefault (@Nullable final String sID,
                                                                 @Nullable final EKeyStoreType eDefault)
  {
    return EnumHelper.getFromIDCaseInsensitiveOrDefault (EKeyStoreType.class, sID, eDefault);
  }
}
