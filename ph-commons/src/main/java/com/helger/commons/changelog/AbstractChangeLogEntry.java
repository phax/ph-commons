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
package com.helger.commons.changelog;

import java.io.Serializable;
import java.time.LocalDate;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Base class for all changelog entries
 *
 * @author Philip Helger
 */
public abstract class AbstractChangeLogEntry implements Serializable
{
  private final LocalDate m_aLocalDate;

  /**
   * Constructor.
   *
   * @param aDate
   *        The release date. May not be <code>null</code>.
   */
  public AbstractChangeLogEntry (@Nonnull final LocalDate aDate)
  {
    m_aLocalDate = ValueEnforcer.notNull (aDate, "Date");
  }

  /**
   * @return The date of the entry.
   */
  @Nonnull
  public final LocalDate getDate ()
  {
    return m_aLocalDate;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final AbstractChangeLogEntry rhs = (AbstractChangeLogEntry) o;
    return m_aLocalDate.equals (rhs.m_aLocalDate);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aLocalDate).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("LocalDate", m_aLocalDate).toString ();
  }
}
