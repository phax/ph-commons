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

import com.helger.annotation.Nonnull;
import com.helger.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.builder.IBuilder;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * The default implementation of {@link ITrustStoreDescriptor}.
 *
 * @author Philip Helger
 * @since 11.1.10
 */
public class TrustStoreDescriptor implements ITrustStoreDescriptor
{
  private final IKeyStoreType m_aType;
  private final String m_sPath;
  private final char [] m_aPassword;
  private final Provider m_aProvider;
  // Lazily initialized
  private LoadedKeyStore m_aLTS;

  public TrustStoreDescriptor (@Nonnull final IKeyStoreType aType,
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
  public final IKeyStoreType getTrustStoreType ()
  {
    return m_aType;
  }

  @Nonnull
  @Nonempty
  public final String getTrustStorePath ()
  {
    return m_sPath;
  }

  @Nonnull
  @ReturnsMutableObject
  public final char [] getTrustStorePassword ()
  {
    return m_aPassword;
  }

  @Nullable
  public final Provider getProvider ()
  {
    return m_aProvider;
  }

  @Nonnull
  public LoadedKeyStore loadTrustStore ()
  {
    LoadedKeyStore ret = m_aLTS;
    if (ret == null)
      ret = m_aLTS = KeyStoreHelper.loadKeyStore (m_aType, m_sPath, m_aPassword, m_aProvider);
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
   * @return A new builder for {@link TrustStoreDescriptor} objects. Never
   *         <code>null</code>.
   */
  @Nonnull
  public static TrustStoreDescriptorBuilder builder ()
  {
    return new TrustStoreDescriptorBuilder ();
  }

  /**
   * Create a new builder using the provided descriptor.
   *
   * @param a
   *        The existing descriptor. May not be <code>null</code>.
   * @return A new builder for {@link TrustStoreDescriptor} objects. Never
   *         <code>null</code>.
   */
  @Nonnull
  public static TrustStoreDescriptorBuilder builder (@Nonnull final TrustStoreDescriptor a)
  {
    return new TrustStoreDescriptorBuilder (a);
  }

  /**
   * Builder class for class {@link TrustStoreDescriptor}.
   *
   * @author Philip Helger
   */
  public static class TrustStoreDescriptorBuilder implements IBuilder <TrustStoreDescriptor>
  {
    private IKeyStoreType m_aType;
    private String m_sPath;
    private char [] m_aPassword;
    private Provider m_aProvider;

    public TrustStoreDescriptorBuilder ()
    {}

    public TrustStoreDescriptorBuilder (@Nonnull final TrustStoreDescriptor aSrc)
    {
      type (aSrc.m_aType).path (aSrc.m_sPath).password (aSrc.m_aPassword).provider (aSrc.m_aProvider);
    }

    @Nonnull
    public final TrustStoreDescriptorBuilder type (@Nullable final IKeyStoreType a)
    {
      m_aType = a;
      return this;
    }

    @Nonnull
    public final TrustStoreDescriptorBuilder path (@Nullable final String s)
    {
      m_sPath = s;
      return this;
    }

    @Nonnull
    public final TrustStoreDescriptorBuilder password (@Nullable final String s)
    {
      return password (s == null ? null : s.toCharArray ());
    }

    @Nonnull
    public final TrustStoreDescriptorBuilder password (@Nullable final char [] a)
    {
      m_aPassword = a;
      return this;
    }

    @Nonnull
    public final TrustStoreDescriptorBuilder provider (@Nullable final Provider a)
    {
      m_aProvider = a;
      return this;
    }

    @Nonnull
    public TrustStoreDescriptor build () throws IllegalStateException
    {
      if (m_aType == null)
        throw new IllegalStateException ("Type is missing");
      if (StringHelper.hasNoText (m_sPath))
        throw new IllegalStateException ("Path is empty");
      if (m_aPassword == null)
        throw new IllegalStateException ("Password is missing");
      // Provider may be null

      return new TrustStoreDescriptor (m_aType, m_sPath, m_aPassword, m_aProvider);
    }
  }
}
