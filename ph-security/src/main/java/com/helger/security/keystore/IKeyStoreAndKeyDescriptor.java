/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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

import java.security.KeyStore.PrivateKeyEntry;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonempty;

/**
 * Interface describing the parameters needed to reference a key store with a private key.
 *
 * @author Philip Helger
 * @since 11.1.10
 */
public interface IKeyStoreAndKeyDescriptor extends IKeyStoreDescriptor
{
  /**
   * Note: the case sensitivity of the key alias depends on the key store type.
   *
   * @return The alias of the key inside a key store. May neither be <code>null</code> nor empty.
   */
  @NonNull
  @Nonempty
  String getKeyAlias ();

  /**
   * @return The password required to access the key inside the key store. May not be
   *         <code>null</code> but may be empty.
   */
  char @NonNull [] getKeyPassword ();

  /**
   * @return The loaded key based on the loaded key store and the parameters in this descriptor.
   */
  @NonNull
  LoadedKey <PrivateKeyEntry> loadKey ();
}
