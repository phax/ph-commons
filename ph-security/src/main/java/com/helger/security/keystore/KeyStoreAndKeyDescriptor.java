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

import java.security.KeyStore.PrivateKeyEntry;
import java.security.Provider;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnull;
import com.helger.annotation.Nullable;
import com.helger.annotation.misc.ReturnsMutableObject;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.builder.IBuilder;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * The default implementation of {@link IKeyStoreAndKeyDescriptor}.
 *
 * @author Philip Helger
 * @since 11.1.10
 */
public class KeyStoreAndKeyDescriptor implements IKeyStoreAndKeyDescriptor
{
  private final IKeyStoreType m_aType;
  private final String m_sPath;
  private final char [] m_aPassword;
  private final Provider m_aProvider;
  private final String m_sKeyAlias;
  private final char [] m_aKeyPassword;
  // Lazily initialized
  private LoadedKeyStore m_aLKS;
  private LoadedKey <PrivateKeyEntry> m_aLK;

  public KeyStoreAndKeyDescriptor (@Nonnull final IKeyStoreType aType,
                                   @Nonnull @Nonempty final String sPath,
                                   @Nonnull final char [] aPassword,
                                   @Nullable final Provider aProvider,
                                   @Nonnull @Nonempty final String sKeyAlias,
                                   @Nonnull final char [] aKeyPassword)
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

  @Nonnull
  @Nonempty
  public final String getKeyAlias ()
  {
    return m_sKeyAlias;
  }

  @Nonnull
  @ReturnsMutableObject
  public final char [] getKeyPassword ()
  {
    return m_aKeyPassword;
  }

  @Nonnull
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
   * @return A new builder for {@link KeyStoreAndKeyDescriptor} objects. Never
   *         <code>null</code>.
   */
  @Nonnull
  public static KeyStoreAndKeyDescriptorBuilder builder ()
  {
    return new KeyStoreAndKeyDescriptorBuilder ();
  }

  /**
   * Create a new builder using the provided descriptor.
   *
   * @param a
   *        The existing descriptor. May not be <code>null</code>.
   * @return A new builder for {@link KeyStoreAndKeyDescriptor} objects. Never
   *         <code>null</code>.
   */
  @Nonnull
  public static KeyStoreAndKeyDescriptorBuilder builder (@Nonnull final KeyStoreAndKeyDescriptor a)
  {
    return new KeyStoreAndKeyDescriptorBuilder (a);
  }

  /**
   * Builder class for class {@link KeyStoreAndKeyDescriptor}.
   *
   * @author Philip Helger
   */
  public static class KeyStoreAndKeyDescriptorBuilder implements IBuilder <KeyStoreAndKeyDescriptor>
  {
    private IKeyStoreType m_aType;
    private String m_sPath;
    private char [] m_aPassword;
    private Provider m_aProvider;
    private String m_sKeyAlias;
    private char [] m_aKeyPassword;

    public KeyStoreAndKeyDescriptorBuilder ()
    {}

    public KeyStoreAndKeyDescriptorBuilder (@Nonnull final KeyStoreAndKeyDescriptor aSrc)
    {
      type (aSrc.m_aType).path (aSrc.m_sPath)
                         .password (aSrc.m_aPassword)
                         .provider (aSrc.m_aProvider)
                         .keyAlias (aSrc.m_sKeyAlias)
                         .keyPassword (aSrc.m_aKeyPassword);
    }

    @Nonnull
    public final KeyStoreAndKeyDescriptorBuilder type (@Nullable final IKeyStoreType a)
    {
      m_aType = a;
      return this;
    }

    @Nonnull
    public final KeyStoreAndKeyDescriptorBuilder path (@Nullable final String s)
    {
      m_sPath = s;
      return this;
    }

    @Nonnull
    public final KeyStoreAndKeyDescriptorBuilder password (@Nullable final String s)
    {
      return password (s == null ? null : s.toCharArray ());
    }

    @Nonnull
    public final KeyStoreAndKeyDescriptorBuilder password (@Nullable final char [] a)
    {
      m_aPassword = a;
      return this;
    }

    @Nonnull
    public final KeyStoreAndKeyDescriptorBuilder provider (@Nullable final Provider a)
    {
      m_aProvider = a;
      return this;
    }

    @Nonnull
    public final KeyStoreAndKeyDescriptorBuilder keyAlias (@Nullable final String s)
    {
      m_sKeyAlias = s;
      return this;
    }

    @Nonnull
    public final KeyStoreAndKeyDescriptorBuilder keyPassword (@Nullable final String s)
    {
      return keyPassword (s == null ? null : s.toCharArray ());
    }

    @Nonnull
    public final KeyStoreAndKeyDescriptorBuilder keyPassword (@Nullable final char [] a)
    {
      m_aKeyPassword = a;
      return this;
    }

    @Nonnull
    public KeyStoreAndKeyDescriptor build () throws IllegalStateException
    {
      if (m_aType == null)
        throw new IllegalStateException ("Type is missing");
      if (StringHelper.hasNoText (m_sPath))
        throw new IllegalStateException ("Path is empty");
      if (m_aPassword == null)
        throw new IllegalStateException ("Password is missing");
      // Provider may be null
      if (StringHelper.hasNoText (m_sKeyAlias))
        throw new IllegalStateException ("KeyAlias is empty");
      if (m_aKeyPassword == null)
        throw new IllegalStateException ("KeyPassword is missing");

      return new KeyStoreAndKeyDescriptor (m_aType, m_sPath, m_aPassword, m_aProvider, m_sKeyAlias, m_aKeyPassword);
    }
  }
}
