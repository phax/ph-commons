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
import com.helger.base.string.StringHelper;

/**
 * Builder class for class {@link KeyStoreDescriptor}.
 *
 * @author Philip Helger
 */
public class KeyStoreDescriptorBuilder implements IBuilder <KeyStoreDescriptor>
{
  private IKeyStoreType m_aType;
  private String m_sPath;
  private char [] m_aPassword;
  private Provider m_aProvider;

  /**
   * Default constructor.
   */
  public KeyStoreDescriptorBuilder ()
  {}

  /**
   * Copy constructor from an existing descriptor.
   *
   * @param aSrc
   *        The source descriptor to copy from. May not be <code>null</code>.
   */
  public KeyStoreDescriptorBuilder (@NonNull final KeyStoreDescriptor aSrc)
  {
    type (aSrc.m_aType).path (aSrc.m_sPath).password (aSrc.m_aPassword).provider (aSrc.m_aProvider);
  }

  /**
   * Set the key store type.
   *
   * @param a
   *        The key store type to use. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final KeyStoreDescriptorBuilder type (@Nullable final IKeyStoreType a)
  {
    m_aType = a;
    return this;
  }

  /**
   * Set the key store path.
   *
   * @param s
   *        The path to the key store. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final KeyStoreDescriptorBuilder path (@Nullable final String s)
  {
    m_sPath = s;
    return this;
  }

  /**
   * Set the key store password as a String.
   *
   * @param s
   *        The password for the key store. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final KeyStoreDescriptorBuilder password (@Nullable final String s)
  {
    return password (s == null ? null : s.toCharArray ());
  }

  /**
   * Set the key store password as a char array.
   *
   * @param a
   *        The password for the key store. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final KeyStoreDescriptorBuilder password (final char @Nullable [] a)
  {
    m_aPassword = a;
    return this;
  }

  /**
   * Set the security provider to use.
   *
   * @param a
   *        The security provider. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final KeyStoreDescriptorBuilder provider (@Nullable final Provider a)
  {
    m_aProvider = a;
    return this;
  }

  /**
   * Build the {@link KeyStoreDescriptor} from the provided parameters.
   *
   * @return A new {@link KeyStoreDescriptor} instance. Never <code>null</code>.
   * @throws IllegalStateException
   *         if any required parameter is missing.
   */
  @NonNull
  public KeyStoreDescriptor build () throws IllegalStateException
  {
    if (m_aType == null)
      throw new IllegalStateException ("Type is missing");
    if (StringHelper.isEmpty (m_sPath))
      throw new IllegalStateException ("Path is empty");
    if (m_aPassword == null)
      throw new IllegalStateException ("Password is missing");
    // Provider may be null

    return new KeyStoreDescriptor (m_aType, m_sPath, m_aPassword, m_aProvider);
  }
}
