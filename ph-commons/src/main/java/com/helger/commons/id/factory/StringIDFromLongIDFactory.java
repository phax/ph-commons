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
 * A factory that creates String IDs based on a specified {@link ILongIDFactory}
 * . The implementation is as thread-safe as the used {@link ILongIDFactory}.
 *
 * @author Philip Helger
 */
public class StringIDFromLongIDFactory implements IStringIDFactory
{
  private final ILongIDFactory m_aLongIDFactory;
  private final String m_sPrefix;

  public StringIDFromLongIDFactory (@Nonnull final ILongIDFactory aLongIDFactory)
  {
    this (aLongIDFactory, GlobalIDFactory.DEFAULT_PREFIX);
  }

  public StringIDFromLongIDFactory (@Nonnull final ILongIDFactory aLongIDFactory, @Nonnull final String sPrefix)
  {
    m_aLongIDFactory = ValueEnforcer.notNull (aLongIDFactory, "LongIDFactory");
    m_sPrefix = ValueEnforcer.notNull (sPrefix, "Prefix");
  }

  @Nonnull
  public ILongIDFactory getLongIDFactory ()
  {
    return m_aLongIDFactory;
  }

  @Nonnull
  public String getPrefix ()
  {
    return m_sPrefix;
  }

  @Nonnull
  public String getNewID ()
  {
    return m_sPrefix + Long.toString (m_aLongIDFactory.getNewID ());
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final StringIDFromLongIDFactory rhs = (StringIDFromLongIDFactory) o;
    return m_aLongIDFactory.equals (rhs.m_aLongIDFactory) && m_sPrefix.equals (rhs.m_sPrefix);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aLongIDFactory).append (m_sPrefix).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("longIDFactory", m_aLongIDFactory)
                                       .append ("prefix", m_sPrefix)
                                       .toString ();
  }
}
