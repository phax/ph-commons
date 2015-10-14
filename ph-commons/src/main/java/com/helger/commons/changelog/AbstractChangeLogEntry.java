/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
import java.util.Date;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Base class for all changelog entries
 *
 * @author Philip Helger
 */
public abstract class AbstractChangeLogEntry implements Serializable
{
  private final Date m_aDate;

  /**
   * Constructor.
   *
   * @param aDate
   *        The release date. May not be <code>null</code>.
   */
  public AbstractChangeLogEntry (@Nonnull final Date aDate)
  {
    ValueEnforcer.notNull (aDate, "Date");
    m_aDate = (Date) aDate.clone ();
  }

  /**
   * @return A mutable, non-<code>null</code> clone of the contained entry date.
   */
  @Nonnull
  @ReturnsMutableCopy
  public final Date getDate ()
  {
    return (Date) m_aDate.clone ();
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final AbstractChangeLogEntry rhs = (AbstractChangeLogEntry) o;
    return m_aDate.equals (rhs.m_aDate);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aDate).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("date", m_aDate).toString ();
  }
}
