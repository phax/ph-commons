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
package com.helger.datetime.period;

import java.time.LocalDateTime;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.tostring.ToStringGenerator;

import jakarta.annotation.Nullable;

/**
 * Default implementation of {@link ILocalDateTimePeriod}.
 *
 * @author Philip Helger
 */
@Immutable
public class LocalDateTimePeriod implements ILocalDateTimePeriod
{
  private final LocalDateTime m_aStart;
  private final LocalDateTime m_aEnd;

  public LocalDateTimePeriod (@Nullable final LocalDateTime aStart, @Nullable final LocalDateTime aEnd)
  {
    m_aStart = aStart;
    m_aEnd = aEnd;
  }

  @Nullable
  public LocalDateTime getStart ()
  {
    return m_aStart;
  }

  @Nullable
  public LocalDateTime getEnd ()
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
    final LocalDateTimePeriod rhs = (LocalDateTimePeriod) o;
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
