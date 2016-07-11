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
package com.helger.security.keystore;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.id.IHasID;

public enum EKeyStoreLoadError implements IHasID <String>
{
  // KeyStore loading
  /** No keystore path provided. Parameters: none */
  KEYSTORE_NO_PATH ("keystore-no-path"),
  /**
   * Keystore not existing. Parameters:
   * <ul>
   * <li>KeyStore path</li>
   * <li>Exception text</li>
   * </ul>
   */
  KEYSTORE_LOAD_ERROR_NON_EXISTING ("keystore-load-error-non-existing"),
  /**
   * Invalid keystore password. Parameters:
   * <ul>
   * <li>KeyStore path</li>
   * <li>Exception text</li>
   * </ul>
   */
  KEYSTORE_INVALID_PASSWORD ("keystore-invalid-password"),
  /**
   * Invalid password or generic error. Parameters:
   * <ul>
   * <li>KeyStore path</li>
   * <li>Exception text</li>
   * </ul>
   */
  KEYSTORE_LOAD_ERROR_FORMAT_ERROR ("keystore-load-error-format-error"),
  // Key loading
  /**
   * No alias specified. Parameters:
   * <ul>
   * <li>KeyStore path</li>
   * </ul>
   */
  KEY_NO_ALIAS ("key-no-alias"),
  /**
   * No password specified. Parameters:
   * <ul>
   * <li>Alias name</li>
   * <li>KeyStore path</li>
   * </ul>
   */
  KEY_NO_PASSWORD ("key-no-password"),
  /**
   * Alias does not exist. Parameters:
   * <ul>
   * <li>Alias name</li>
   * <li>KeyStore path</li>
   * </ul>
   */
  KEY_INVALID_ALIAS ("key-invalid-alias"),
  /**
   * Key is not of expected type. Parameters:
   * <ul>
   * <li>Alias name</li>
   * <li>KeyStore path</li>
   * <li>Effective technical type name</li>
   * </ul>
   */
  KEY_INVALID_TYPE ("key-invalid-type"),
  /**
   * Invalid password for the key. Parameters:
   * <ul>
   * <li>Alias name</li>
   * <li>KeyStore path</li>
   * <li>Exception message</li>
   * </ul>
   */
  KEY_INVALID_PASSWORD ("key-invalid-password"),
  /**
   * Generic error loading the key. Parameters:
   * <ul>
   * <li>Alias name</li>
   * <li>KeyStore path</li>
   * <li>Exception message</li>
   * </ul>
   */
  KEY_LOAD_ERROR ("key-load-error");

  private final String m_sID;

  private EKeyStoreLoadError (@Nonnull @Nonempty final String sID)
  {
    m_sID = sID;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }
}
