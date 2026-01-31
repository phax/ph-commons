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

import java.security.Provider;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.base.builder.IBuilder;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.string.StringHelper;

/**
 * Builder class for class {@link KeyStoreAndKeyDescriptor}.
 *
 * @author Philip Helger
 */
public class KeyStoreAndKeyDescriptorBuilder implements IBuilder <KeyStoreAndKeyDescriptor>
{
  private IKeyStoreType m_aType;
  private String m_sPath;
  private char [] m_aPassword;
  private Provider m_aProvider;
  private String m_sKeyAlias;
  private char [] m_aKeyPassword;

  public KeyStoreAndKeyDescriptorBuilder ()
  {}

  public KeyStoreAndKeyDescriptorBuilder (@NonNull final KeyStoreAndKeyDescriptor aSrc)
  {
    ValueEnforcer.notNull (aSrc, "SourceKeyStoreAndKeyDescriptor");
    type (aSrc.m_aType).path (aSrc.m_sPath)
                       .password (aSrc.m_aPassword)
                       .provider (aSrc.m_aProvider)
                       .keyAlias (aSrc.m_sKeyAlias)
                       .keyPassword (aSrc.m_aKeyPassword);
  }

  @NonNull
  public final KeyStoreAndKeyDescriptorBuilder type (@Nullable final IKeyStoreType a)
  {
    m_aType = a;
    return this;
  }

  @NonNull
  public final KeyStoreAndKeyDescriptorBuilder path (@Nullable final String s)
  {
    m_sPath = s;
    return this;
  }

  @NonNull
  public final KeyStoreAndKeyDescriptorBuilder password (@Nullable final String s)
  {
    return password (s == null ? null : s.toCharArray ());
  }

  @NonNull
  public final KeyStoreAndKeyDescriptorBuilder password (final char @Nullable [] a)
  {
    m_aPassword = a;
    return this;
  }

  @NonNull
  public final KeyStoreAndKeyDescriptorBuilder provider (@Nullable final Provider a)
  {
    m_aProvider = a;
    return this;
  }

  @NonNull
  public final KeyStoreAndKeyDescriptorBuilder keyAlias (@Nullable final String s)
  {
    m_sKeyAlias = s;
    return this;
  }

  @NonNull
  public final KeyStoreAndKeyDescriptorBuilder keyPassword (@Nullable final String s)
  {
    return keyPassword (s == null ? null : s.toCharArray ());
  }

  @NonNull
  public final KeyStoreAndKeyDescriptorBuilder keyPassword (final char @Nullable [] a)
  {
    m_aKeyPassword = a;
    return this;
  }

  @NonNull
  public KeyStoreAndKeyDescriptor build () throws IllegalStateException
  {
    if (m_aType == null)
      throw new IllegalStateException ("Type is missing");
    if (StringHelper.isEmpty (m_sPath))
      throw new IllegalStateException ("Path is empty");
    if (m_aPassword == null)
      throw new IllegalStateException ("Password is missing");
    // Provider may be null
    if (StringHelper.isEmpty (m_sKeyAlias))
      throw new IllegalStateException ("KeyAlias is empty");
    if (m_aKeyPassword == null)
      throw new IllegalStateException ("KeyPassword is missing");

    return new KeyStoreAndKeyDescriptor (m_aType, m_sPath, m_aPassword, m_aProvider, m_sKeyAlias, m_aKeyPassword);
  }
}
