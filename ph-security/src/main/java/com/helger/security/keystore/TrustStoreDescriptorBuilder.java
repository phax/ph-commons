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

import com.helger.base.builder.IBuilder;
import com.helger.base.string.StringHelper;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Builder class for class {@link TrustStoreDescriptor}.
 *
 * @author Philip Helger
 */
public class TrustStoreDescriptorBuilder implements IBuilder <TrustStoreDescriptor>
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
    if (StringHelper.isEmpty (m_sPath))
      throw new IllegalStateException ("Path is empty");
    if (m_aPassword == null)
      throw new IllegalStateException ("Password is missing");
    // Provider may be null

    return new TrustStoreDescriptor (m_aType, m_sPath, m_aPassword, m_aProvider);
  }
}
