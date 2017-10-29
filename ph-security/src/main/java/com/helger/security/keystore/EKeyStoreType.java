/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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

import com.helger.commons.annotation.Nonempty;

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
  PKCS12 ("PKCS12");

  private final String m_sID;

  private EKeyStoreType (@Nonnull @Nonempty final String sID)
  {
    m_sID = sID;
  }

  public String getID ()
  {
    return m_sID;
  }
}
