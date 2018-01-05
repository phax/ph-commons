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
package com.helger.datetime.period;

import java.time.LocalDate;

import javax.annotation.Nullable;

import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Default implementation of {@link ILocalDatePeriod}.
 * 
 * @author Philip Helger
 */
public class LocalDatePeriod implements ILocalDatePeriod
{
  private final LocalDate m_aStart;
  private final LocalDate m_aEnd;

  public LocalDatePeriod (@Nullable final LocalDate aStart, @Nullable final LocalDate aEnd)
  {
    m_aStart = aStart;
    m_aEnd = aEnd;
  }

  @Nullable
  public LocalDate getStart ()
  {
    return m_aStart;
  }

  @Nullable
  public LocalDate getEnd ()
  {
    return m_aEnd;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final LocalDatePeriod rhs = (LocalDatePeriod) o;
    return EqualsHelper.equals (m_aStart, rhs.m_aStart) && EqualsHelper.equals (m_aEnd, rhs.m_aEnd);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aStart).append (m_aEnd).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Start", m_aStart).append ("End", m_aEnd).getToString ();
  }
}
