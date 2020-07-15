/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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
package com.helger.config.source;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Abstract base class for any configuration source.
 *
 * @author Philip Helger
 */
@MustImplementEqualsAndHashcode
@Immutable
public abstract class AbstractConfigurationSource implements IConfigurationSource
{
  private final EConfigSourceType m_eSourceType;
  private final int m_nPriority;

  /**
   * Constructor
   *
   * @param eSourceType
   *        Configuration source type. May not be <code>null</code>.
   * @param nPriority
   *        The priority to use. The higher the more important.
   */
  protected AbstractConfigurationSource (@Nonnull final EConfigSourceType eSourceType, final int nPriority)
  {
    ValueEnforcer.notNull (eSourceType, "Type");
    m_eSourceType = eSourceType;
    m_nPriority = nPriority;
  }

  @Nonnull
  public final EConfigSourceType getSourceType ()
  {
    return m_eSourceType;
  }

  public final int getPriority ()
  {
    return m_nPriority;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final AbstractConfigurationSource rhs = (AbstractConfigurationSource) o;
    return m_eSourceType.equals (rhs.m_eSourceType) && m_nPriority == rhs.m_nPriority;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_eSourceType).append (m_nPriority).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("SourceType", m_eSourceType).append ("Priority", m_nPriority).getToString ();
  }
}
