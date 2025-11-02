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
package com.helger.io.resourceprovider;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.string.StringHelper;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.io.resource.ClassPathResource;
import com.helger.io.resource.IReadableResource;

/**
 * Simple resource provider that only uses files.
 *
 * @author Philip Helger
 */
@Immutable
public final class ClassPathResourceProvider implements IReadableResourceProvider
{
  private final String m_sPrefix;

  /**
   * Constructor without prefix.
   */
  public ClassPathResourceProvider ()
  {
    this (null);
  }

  /**
   * Constructor
   *
   * @param sPrefix
   *        The common prefix to use. May be <code>null</code>.
   */
  public ClassPathResourceProvider (@Nullable final String sPrefix)
  {
    m_sPrefix = sPrefix;
  }

  /**
   * @return The prefix as passed in the constructor. May be <code>null</code>.
   */
  @Nullable
  public String getPrefix ()
  {
    return m_sPrefix;
  }

  public boolean supportsReading (@Nullable final String sName)
  {
    // Class path resource supports all paths
    return StringHelper.isNotEmpty (sName);
  }

  @NonNull
  public IReadableResource getReadableResource (@NonNull final String sName)
  {
    ValueEnforcer.notNull (sName, "Name");

    return new ClassPathResource (m_sPrefix == null ? sName : m_sPrefix + sName);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final ClassPathResourceProvider rhs = (ClassPathResourceProvider) o;
    return EqualsHelper.equals (m_sPrefix, rhs.m_sPrefix);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sPrefix).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).appendIfNotNull ("prefix", m_sPrefix).getToString ();
  }
}
