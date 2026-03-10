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
package com.helger.text.resourcebundle;

import java.util.Locale;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.PropertyKey;
import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.tostring.ToStringGenerator;

/**
 * The key of a resource bundle.
 *
 * @author Philip Helger
 */
@Immutable
public class ResourceBundleKey
{
  private final String m_sBundleName;
  private final String m_sKey;

  /**
   * Constructor.
   *
   * @param sBundleName
   *        The resource bundle name. May neither be <code>null</code> nor
   *        empty.
   * @param sKey
   *        The property key within the bundle. May neither be
   *        <code>null</code> nor empty.
   */
  public ResourceBundleKey (@NonNull @Nonempty final String sBundleName, @NonNull @Nonempty @PropertyKey final String sKey)
  {
    m_sBundleName = ValueEnforcer.notEmpty (sBundleName, "BundleName");
    m_sKey = ValueEnforcer.notEmpty (sKey, "Key");
  }

  /**
   * @return The resource bundle name. Neither <code>null</code> nor empty.
   */
  @NonNull
  @Nonempty
  public String getBundleName ()
  {
    return m_sBundleName;
  }

  /**
   * @return The property key within the bundle. Neither <code>null</code> nor
   *         empty.
   */
  @NonNull
  @Nonempty
  @PropertyKey
  public String getKey ()
  {
    return m_sKey;
  }

  /**
   * Get the string value for the given locale.
   *
   * @param aContentLocale
   *        The locale to use. May not be <code>null</code>.
   * @return <code>null</code> if no such resource bundle or key exists.
   */
  @Nullable
  public String getString (@NonNull final Locale aContentLocale)
  {
    return ResourceBundleHelper.getString (m_sBundleName, aContentLocale, m_sKey);
  }

  /**
   * Get the string value for the given locale using a specific class loader.
   *
   * @param aContentLocale
   *        The locale to use. May not be <code>null</code>.
   * @param aClassLoader
   *        The class loader to use. May not be <code>null</code>.
   * @return <code>null</code> if no such resource bundle or key exists.
   */
  @Nullable
  public String getString (@NonNull final Locale aContentLocale, @NonNull final ClassLoader aClassLoader)
  {
    return ResourceBundleHelper.getString (m_sBundleName, aContentLocale, m_sKey, aClassLoader);
  }

  /**
   * Get the UTF-8 string value for the given locale.
   *
   * @param aContentLocale
   *        The locale to use. May not be <code>null</code>.
   * @return <code>null</code> if no such resource bundle or key exists.
   */
  @Nullable
  public String getUtf8String (@NonNull final Locale aContentLocale)
  {
    return ResourceBundleHelper.getUtf8String (m_sBundleName, aContentLocale, m_sKey);
  }

  /**
   * Get the UTF-8 string value for the given locale using a specific class
   * loader.
   *
   * @param aContentLocale
   *        The locale to use. May not be <code>null</code>.
   * @param aClassLoader
   *        The class loader to use. May not be <code>null</code>.
   * @return <code>null</code> if no such resource bundle or key exists.
   */
  @Nullable
  public String getUtf8String (@NonNull final Locale aContentLocale, @NonNull final ClassLoader aClassLoader)
  {
    return ResourceBundleHelper.getUtf8String (m_sBundleName, aContentLocale, m_sKey, aClassLoader);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final ResourceBundleKey rhs = (ResourceBundleKey) o;
    return m_sBundleName.equals (rhs.m_sBundleName) && m_sKey.equals (rhs.m_sKey);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sBundleName).append (m_sKey).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("bundleName", m_sBundleName).append ("key", m_sKey).getToString ();
  }
}
