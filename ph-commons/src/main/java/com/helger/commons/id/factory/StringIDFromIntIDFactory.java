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
package com.helger.commons.id.factory;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * A factory that creates String IDs based on a specified {@link IIntIDFactory}.
 * The implementation is as thread-safe as the used {@link IIntIDFactory}.
 *
 * @author Philip Helger
 */
public class StringIDFromIntIDFactory implements IStringIDFactory
{
  private final IIntIDFactory m_aIntIDFactory;
  private final String m_sPrefix;

  public StringIDFromIntIDFactory (@Nonnull final IIntIDFactory aIntIDFactory)
  {
    this (aIntIDFactory, GlobalIDFactory.DEFAULT_PREFIX);
  }

  public StringIDFromIntIDFactory (@Nonnull final IIntIDFactory aIntIDFactory, @Nonnull final String sPrefix)
  {
    m_aIntIDFactory = ValueEnforcer.notNull (aIntIDFactory, "IntIDFactory");
    m_sPrefix = ValueEnforcer.notNull (sPrefix, "Prefix");
  }

  @Nonnull
  public IIntIDFactory getIntIDFactory ()
  {
    return m_aIntIDFactory;
  }

  @Nonnull
  public String getPrefix ()
  {
    return m_sPrefix;
  }

  @Nonnull
  public String getNewID ()
  {
    return m_sPrefix + Integer.toString (m_aIntIDFactory.getNewID ());
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final StringIDFromIntIDFactory rhs = (StringIDFromIntIDFactory) o;
    return m_aIntIDFactory.equals (rhs.m_aIntIDFactory) && m_sPrefix.equals (rhs.m_sPrefix);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aIntIDFactory).append (m_sPrefix).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("intIDFactory", m_aIntIDFactory)
                                       .append ("prefix", m_sPrefix)
                                       .toString ();
  }
}
