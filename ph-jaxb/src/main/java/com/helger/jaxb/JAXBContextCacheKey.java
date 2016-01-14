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
package com.helger.jaxb;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.lang.ClassLoaderHelper;
import com.helger.commons.lang.IHasClassLoader;
import com.helger.commons.string.ToStringGenerator;

/**
 * The key class for the {@link JAXBContextCache}
 *
 * @author Philip Helger
 */
@Immutable
public class JAXBContextCacheKey implements IHasClassLoader
{
  private final Package m_aPackage;
  private final ClassLoader m_aClassLoader;

  public JAXBContextCacheKey (@Nonnull final Package aPackage, @Nullable final ClassLoader aClassLoader)
  {
    m_aPackage = ValueEnforcer.notNull (aPackage, "Package");
    m_aClassLoader = aClassLoader != null ? aClassLoader : ClassLoaderHelper.getDefaultClassLoader ();
  }

  /**
   * @return The package passed in the constructor. Never <code>null</code>.
   */
  @Nonnull
  public Package getPackage ()
  {
    return m_aPackage;
  }

  /**
   * @return The class loader passed in the constructor or the default class
   *         loader. Never <code>null</code>-
   */
  @Nonnull
  public ClassLoader getClassLoader ()
  {
    return m_aClassLoader;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final JAXBContextCacheKey rhs = (JAXBContextCacheKey) o;
    return m_aPackage.equals (rhs.m_aPackage) && m_aClassLoader.equals (rhs.m_aClassLoader);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aPackage).append (m_aClassLoader).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Package", m_aPackage)
                                       .append ("ClassLoader", m_aClassLoader)
                                       .toString ();
  }
}
