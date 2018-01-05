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
package com.helger.security.keystore;

import java.io.Serializable;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchProviderException;
import java.security.Provider;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.id.IHasID;

/**
 * Base interface for the different types of key stores (like JKS or PKCS12).
 *
 * @author Philip Helger
 * @since 9.0.0
 */
public interface IKeyStoreType extends IHasID <String>, Serializable
{
  /**
   * @return A Key store object of this type. Never <code>null</code>.
   * @throws KeyStoreException
   *         if no Provider supports a KeyStoreSpi implementation for the
   *         specified type.
   */
  @Nonnull
  default KeyStore getKeyStore () throws KeyStoreException
  {
    return KeyStore.getInstance (getID ());
  }

  /**
   * @param sProvider
   *        Security provider to be used. E.g. for BouncyCastle. May neither be
   *        <code>null</code> nor empty.
   * @return A Key store object of this type. Never <code>null</code>.
   * @throws KeyStoreException
   *         if no Provider supports a KeyStoreSpi implementation for the
   *         specified type.
   * @exception NoSuchProviderException
   *            if the specified provider is not registered in the security
   *            provider list.
   */
  @Nonnull
  default KeyStore getKeyStore (@Nonnull @Nonempty final String sProvider) throws KeyStoreException,
                                                                           NoSuchProviderException
  {
    return KeyStore.getInstance (getID (), sProvider);
  }

  /**
   * @param aProvider
   *        Security provider to be used. E.g. for BouncyCastle. May not be
   *        <code>null</code>.
   * @return A Key store object of this type. Never <code>null</code>.
   * @throws KeyStoreException
   *         if no Provider supports a KeyStoreSpi implementation for the
   *         specified type.
   */
  @Nonnull
  default KeyStore getKeyStore (@Nonnull final Provider aProvider) throws KeyStoreException
  {
    return KeyStore.getInstance (getID (), aProvider);
  }
}
