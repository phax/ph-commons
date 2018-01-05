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
package com.helger.commons.datetime;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalQuery;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.string.ToStringGenerator;

/**
 * Tuple with date time pattern string and query to parse into object.
 *
 * @author Philip Helger
 * @param <T>
 *        Temporal type to use.
 * @since 8.6.5
 */
public class PDTMask <T extends Temporal & Serializable> implements Serializable
{
  // Typedef
  protected static interface ITemporalQuery <R> extends TemporalQuery <R>, Serializable
  {}

  private final String m_sPattern;
  private final TemporalQuery <T> m_aQuery;

  protected PDTMask (@Nonnull @Nonempty final String sPattern, @Nonnull final ITemporalQuery <T> aQuery)
  {
    ValueEnforcer.notEmpty (sPattern, "Pattern");
    ValueEnforcer.notNull (aQuery, "Query");
    m_sPattern = sPattern;
    m_aQuery = aQuery;
  }

  @Nonnull
  @Nonempty
  public String getPattern ()
  {
    return m_sPattern;
  }

  @Nonnull
  public TemporalQuery <T> getQuery ()
  {
    return m_aQuery;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Pattern", m_sPattern).append ("Query", m_aQuery).getToString ();
  }

  @Nonnull
  public static PDTMask <ZonedDateTime> zonedDateTime (@Nonnull @Nonempty final String sPattern)
  {
    return new PDTMask <> (sPattern, ZonedDateTime::from);
  }

  @Nonnull
  public static PDTMask <OffsetDateTime> offsetDateTime (@Nonnull @Nonempty final String sPattern)
  {
    return new PDTMask <> (sPattern, OffsetDateTime::from);
  }

  @Nonnull
  public static PDTMask <LocalDateTime> localDateTime (@Nonnull @Nonempty final String sPattern)
  {
    return new PDTMask <> (sPattern, LocalDateTime::from);
  }

  @Nonnull
  public static PDTMask <LocalDate> localDate (@Nonnull @Nonempty final String sPattern)
  {
    return new PDTMask <> (sPattern, LocalDate::from);
  }

  @Nonnull
  public static PDTMask <YearMonth> yearMonth (@Nonnull @Nonempty final String sPattern)
  {
    return new PDTMask <> (sPattern, YearMonth::from);
  }

  @Nonnull
  public static PDTMask <Year> year (@Nonnull @Nonempty final String sPattern)
  {
    return new PDTMask <> (sPattern, Year::from);
  }
}
