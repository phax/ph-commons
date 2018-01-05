/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.commons.id.factory;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * An {@link IStringIDFactory} implementation that uses a constant prefix and an
 * int supplied from {@link GlobalIDFactory#getNewIntID()} to create unique IDs.
 *
 * @author Philip Helger
 */
public class StringIDFactory implements IStringIDFactory
{
  private final String m_sPrefix;
  private final IStringIDFactory m_aBaseFactory;

  public StringIDFactory (@Nonnull final IStringIDFactory aBaseFactory)
  {
    this (GlobalIDFactory.DEFAULT_PREFIX, aBaseFactory);
  }

  public StringIDFactory (@Nonnull final String sPrefix, @Nonnull final IStringIDFactory aBaseFactory)
  {
    m_sPrefix = ValueEnforcer.notNull (sPrefix, "Prefix");
    m_aBaseFactory = ValueEnforcer.notNull (aBaseFactory, "BaseFactory");
  }

  @Nonnull
  public String getPrefix ()
  {
    return m_sPrefix;
  }

  @Nonnull
  public String getNewID ()
  {
    return m_sPrefix + m_aBaseFactory.getNewID ();
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final StringIDFactory rhs = (StringIDFactory) o;
    return m_sPrefix.equals (rhs.m_sPrefix) && m_aBaseFactory.equals (rhs.m_aBaseFactory);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sPrefix).append (m_aBaseFactory).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("prefix", m_sPrefix)
                                       .append ("BaseFactory", m_aBaseFactory)
                                       .getToString ();
  }
}
