/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.datetime.helper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalQuery;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonempty;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.datetime.rt.OffsetDate;
import com.helger.datetime.xml.XMLOffsetDate;
import com.helger.datetime.xml.XMLOffsetDateTime;

/**
 * Tuple with date time pattern string and query to parse into object.
 *
 * @author Philip Helger
 * @param <T>
 *        Temporal type to use.
 * @since 8.6.5
 */
public class PDTMask <T extends Temporal>
{
  private final String m_sPattern;
  private final TemporalQuery <T> m_aQuery;

  protected PDTMask (@NonNull @Nonempty final String sPattern, @NonNull final TemporalQuery <T> aQuery)
  {
    ValueEnforcer.notEmpty (sPattern, "Pattern");
    ValueEnforcer.notNull (aQuery, "Query");
    m_sPattern = sPattern;
    m_aQuery = aQuery;
  }

  @NonNull
  @Nonempty
  public String getPattern ()
  {
    return m_sPattern;
  }

  @NonNull
  public TemporalQuery <T> getQuery ()
  {
    return m_aQuery;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Pattern", m_sPattern).append ("Query", m_aQuery).getToString ();
  }

  @NonNull
  public static PDTMask <ZonedDateTime> zonedDateTime (@NonNull @Nonempty final String sPattern)
  {
    return new PDTMask <> (sPattern, ZonedDateTime::from);
  }

  @NonNull
  public static PDTMask <OffsetDateTime> offsetDateTime (@NonNull @Nonempty final String sPattern)
  {
    return new PDTMask <> (sPattern, OffsetDateTime::from);
  }

  @NonNull
  public static PDTMask <XMLOffsetDateTime> xmlOffsetDateTime (@NonNull @Nonempty final String sPattern)
  {
    return new PDTMask <> (sPattern, XMLOffsetDateTime::from);
  }

  @NonNull
  public static PDTMask <LocalDateTime> localDateTime (@NonNull @Nonempty final String sPattern)
  {
    return new PDTMask <> (sPattern, LocalDateTime::from);
  }

  @NonNull
  public static PDTMask <OffsetDate> offsetDate (@NonNull @Nonempty final String sPattern)
  {
    return new PDTMask <> (sPattern, OffsetDate::from);
  }

  @NonNull
  public static PDTMask <XMLOffsetDate> xmlOffsetDate (@NonNull @Nonempty final String sPattern)
  {
    return new PDTMask <> (sPattern, XMLOffsetDate::from);
  }

  @NonNull
  public static PDTMask <LocalDate> localDate (@NonNull @Nonempty final String sPattern)
  {
    return new PDTMask <> (sPattern, LocalDate::from);
  }

  @NonNull
  public static PDTMask <YearMonth> yearMonth (@NonNull @Nonempty final String sPattern)
  {
    return new PDTMask <> (sPattern, YearMonth::from);
  }

  @NonNull
  public static PDTMask <Year> year (@NonNull @Nonempty final String sPattern)
  {
    return new PDTMask <> (sPattern, Year::from);
  }
}
