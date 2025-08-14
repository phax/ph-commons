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

import com.helger.annotation.Nonempty;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.equals.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * The default implementation of {@link IKeyStoreDescriptor}.
 *
 * @author Philip Helger
 * @since 11.1.10
 */
public class KeyStoreDescriptor implements IKeyStoreDescriptor
{
  final IKeyStoreType m_aType;
  final String m_sPath;
  final char [] m_aPassword;
  final Provider m_aProvider;
  // Lazily initialized
  private LoadedKeyStore m_aLKS;

  public KeyStoreDescriptor (@Nonnull final IKeyStoreType aType,
                             @Nonnull @Nonempty final String sPath,
                             @Nonnull final char [] aPassword,
                             @Nullable final Provider aProvider)
  {
    ValueEnforcer.notNull (aType, "Type");
    ValueEnforcer.notEmpty (sPath, "Path");
    ValueEnforcer.notNull (aPassword, "Password");
    m_aType = aType;
    m_sPath = sPath;
    m_aPassword = aPassword;
    m_aProvider = aProvider;
  }

  @Nonnull
  public final IKeyStoreType getKeyStoreType ()
  {
    return m_aType;
  }

  @Nonnull
  @Nonempty
  public final String getKeyStorePath ()
  {
    return m_sPath;
  }

  @Nonnull
  @ReturnsMutableObject
  public final char [] getKeyStorePassword ()
  {
    return m_aPassword;
  }

  @Nullable
  public final Provider getProvider ()
  {
    return m_aProvider;
  }

  @Nonnull
  public LoadedKeyStore loadKeyStore ()
  {
    LoadedKeyStore ret = m_aLKS;
    if (ret == null)
      ret = m_aLKS = KeyStoreHelper.loadKeyStore (m_aType, m_sPath, m_aPassword, m_aProvider);
    return ret;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("Type", m_aType)
                                       .append ("Path", m_sPath)
                                       .appendPassword ("Password")
                                       .appendIfNotNull ("Provider", m_aProvider)
                                       .getToString ();
  }

  /**
   * @return A new builder for {@link KeyStoreDescriptor} objects. Never
   *         <code>null</code>.
   */
  @Nonnull
  public static KeyStoreDescriptorBuilder builder ()
  {
    return new KeyStoreDescriptorBuilder ();
  }

  /**
   * Create a new builder using the provided descriptor.
   *
   * @param a
   *        The existing descriptor. May not be <code>null</code>.
   * @return A new builder for {@link KeyStoreDescriptor} objects. Never
   *         <code>null</code>.
   */
  @Nonnull
  public static KeyStoreDescriptorBuilder builder (@Nonnull final KeyStoreDescriptor a)
  {
    return new KeyStoreDescriptorBuilder (a);
  }
}
