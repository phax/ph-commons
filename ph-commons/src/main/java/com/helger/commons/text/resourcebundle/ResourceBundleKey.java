/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.text.resourcebundle;

import java.io.Serializable;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PropertyKey;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * The key of a resource bundle.
 *
 * @author Philip Helger
 */
@Immutable
public class ResourceBundleKey implements Serializable
{
  private final String m_sBundleName;
  private final String m_sKey;

  public ResourceBundleKey (@Nonnull @Nonempty final String sBundleName,
                            @Nonnull @Nonempty @PropertyKey final String sKey)
  {
    m_sBundleName = ValueEnforcer.notEmpty (sBundleName, "BundleName");
    m_sKey = ValueEnforcer.notEmpty (sKey, "Key");
  }

  @Nonnull
  @Nonempty
  public String getBundleName ()
  {
    return m_sBundleName;
  }

  @Nonnull
  @Nonempty
  @PropertyKey
  public String getKey ()
  {
    return m_sKey;
  }

  @Nullable
  public String getString (@Nonnull final Locale aContentLocale)
  {
    return ResourceBundleHelper.getString (m_sBundleName, aContentLocale, m_sKey);
  }

  @Nullable
  public String getString (@Nonnull final Locale aContentLocale, @Nonnull final ClassLoader aClassLoader)
  {
    return ResourceBundleHelper.getString (m_sBundleName, aContentLocale, m_sKey, aClassLoader);
  }

  @Nullable
  public String getUtf8String (@Nonnull final Locale aContentLocale)
  {
    return ResourceBundleHelper.getUtf8String (m_sBundleName, aContentLocale, m_sKey);
  }

  @Nullable
  public String getUtf8String (@Nonnull final Locale aContentLocale, @Nonnull final ClassLoader aClassLoader)
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
    return new ToStringGenerator (this).append ("bundleName", m_sBundleName).append ("key", m_sKey).toString ();
  }
}
