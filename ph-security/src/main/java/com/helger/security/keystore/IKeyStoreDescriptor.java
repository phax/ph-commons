/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import java.security.Provider;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;

/**
 * Interface describing the parameters needed to reference a key store (without
 * a private key).
 *
 * @author Philip Helger
 * @since 11.1.10
 */
public interface IKeyStoreDescriptor
{
  /**
   * @return The type of the key store. May not be <code>null</code>.
   */
  @NonNull
  IKeyStoreType getKeyStoreType ();

  /**
   * @return The path to the key store. May neither be <code>null</code> nor
   *         empty. The interpretation of the path is implementation dependent.
   */
  @NonNull
  @Nonempty
  String getKeyStorePath ();

  /**
   * @return The password required to open the key store. May not be
   *         <code>null</code> but may be empty.
   */
  @NonNull
  char [] getKeyStorePassword ();

  /**
   * @return The Java security provider for loading the key store. May be
   *         <code>null</code>.
   */
  @Nullable
  Provider getProvider ();

  /**
   * @return The loaded key store based on the parameters in this descriptor.
   *         Never <code>null</code>.
   */
  @NonNull
  LoadedKeyStore loadKeyStore ();
}
