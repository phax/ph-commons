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
import java.security.Provider;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;

/**
 * The default implementation of {@link IKeyStoreAndKeyDescriptor}.
 *
 * @author Philip Helger
 * @since 11.1.10
 */
public class KeyStoreAndKeyDescriptor implements IKeyStoreAndKeyDescriptor
{
  final IKeyStoreType m_aType;
  final String m_sPath;
  final char [] m_aPassword;
  final Provider m_aProvider;
  final String m_sKeyAlias;
  final char [] m_aKeyPassword;
  // Lazily initialized
  private LoadedKeyStore m_aLKS;
  private LoadedKey <PrivateKeyEntry> m_aLK;

  /**
   * Constructor with all required parameters.
   *
   * @param aType
   *        The key store type. May not be <code>null</code>.
   * @param sPath
   *        The path to the key store. May neither be <code>null</code> nor empty.
   * @param aPassword
   *        The password for the key store. May not be <code>null</code>.
   * @param aProvider
   *        The optional security provider. May be <code>null</code>.
   * @param sKeyAlias
   *        The alias of the key inside the key store. May neither be <code>null</code> nor empty.
   * @param aKeyPassword
   *        The password for the key entry. May not be <code>null</code>.
   */
  public KeyStoreAndKeyDescriptor (@NonNull final IKeyStoreType aType,
                                   @NonNull @Nonempty final String sPath,
                                   final char @NonNull [] aPassword,
                                   @Nullable final Provider aProvider,
                                   @NonNull @Nonempty final String sKeyAlias,
                                   final char @NonNull [] aKeyPassword)
  {
    ValueEnforcer.notNull (aType, "Type");
    ValueEnforcer.notEmpty (sPath, "Path");
    ValueEnforcer.notNull (aPassword, "Password");
    ValueEnforcer.notEmpty (sKeyAlias, "KeyAlias");
    ValueEnforcer.notNull (aKeyPassword, "KeyPassword");
    m_aType = aType;
    m_sPath = sPath;
    m_aPassword = aPassword;
    m_aProvider = aProvider;
    m_sKeyAlias = sKeyAlias;
    m_aKeyPassword = aKeyPassword;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  public final IKeyStoreType getKeyStoreType ()
  {
    return m_aType;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Nonempty
  public final String getKeyStorePath ()
  {
    return m_sPath;
  }

  /**
   * {@inheritDoc}
   */
  @ReturnsMutableObject
  public final char @NonNull [] getKeyStorePassword ()
  {
    return m_aPassword;
  }

  /**
   * {@inheritDoc}
   */
  @Nullable
  public final Provider getProvider ()
  {
    return m_aProvider;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  public LoadedKeyStore loadKeyStore ()
  {
    LoadedKeyStore ret = m_aLKS;
    if (ret == null)
      ret = m_aLKS = KeyStoreHelper.loadKeyStore (m_aType, m_sPath, m_aPassword, m_aProvider);
    return ret;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Nonempty
  public final String getKeyAlias ()
  {
    return m_sKeyAlias;
  }

  /**
   * {@inheritDoc}
   */
  @ReturnsMutableObject
  public final char @NonNull [] getKeyPassword ()
  {
    return m_aKeyPassword;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  public LoadedKey <PrivateKeyEntry> loadKey ()
  {
    LoadedKey <PrivateKeyEntry> ret = m_aLK;
    if (ret == null)
    {
      ret = m_aLK = KeyStoreHelper.loadPrivateKey (loadKeyStore ().getKeyStore (),
                                                   m_sPath,
                                                   m_sKeyAlias,
                                                   m_aKeyPassword);
    }
    return ret;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("Type", m_aType)
                                       .append ("Path", m_sPath)
                                       .appendPassword ("Password")
                                       .appendIfNotNull ("Provider", m_aProvider)
                                       .append ("KeyAlias", m_sKeyAlias)
                                       .appendPassword ("KeyPassword")
                                       .getToString ();
  }

  /**
   * @return A new builder for {@link KeyStoreAndKeyDescriptor} objects. Never <code>null</code>.
   */
  @NonNull
  public static KeyStoreAndKeyDescriptorBuilder builder ()
  {
    return new KeyStoreAndKeyDescriptorBuilder ();
  }

  /**
   * Create a new builder using the provided descriptor.
   *
   * @param a
   *        The existing descriptor. May not be <code>null</code>.
   * @return A new builder for {@link KeyStoreAndKeyDescriptor} objects. Never <code>null</code>.
   */
  @NonNull
  public static KeyStoreAndKeyDescriptorBuilder builder (@NonNull final KeyStoreAndKeyDescriptor a)
  {
    return new KeyStoreAndKeyDescriptorBuilder (a);
  }
}
