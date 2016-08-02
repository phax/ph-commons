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
package com.helger.datetime.domain;

import java.text.DateFormatSymbols;
import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.id.IHasIntID;
import com.helger.commons.lang.EnumHelper;

/**
 * Represents all known Gregorian Calendar days of a week as a type-safe enum
 *
 * @author Philip Helger
 */
public enum EDayOfWeek implements IHasIntID
{
  MONDAY (DayOfWeek.MONDAY, Calendar.MONDAY),
  TUESDAY (DayOfWeek.TUESDAY, Calendar.TUESDAY),
  WEDNESDAY (DayOfWeek.WEDNESDAY, Calendar.WEDNESDAY),
  THURSDAY (DayOfWeek.THURSDAY, Calendar.THURSDAY),
  FRIDAY (DayOfWeek.FRIDAY, Calendar.FRIDAY),
  SATURDAY (DayOfWeek.SATURDAY, Calendar.SATURDAY),
  SUNDAY (DayOfWeek.SUNDAY, Calendar.SUNDAY);

  private final DayOfWeek m_eDayOfWeek;
  private final int m_nCalID;

  private EDayOfWeek (final DayOfWeek eDayOfWeek, final int nCalID)
  {
    m_eDayOfWeek = eDayOfWeek;
    m_nCalID = nCalID;
  }

  public int getID ()
  {
    return getDateTimeConstant ();
  }

  /**
   * @return The JDK8 time ID (1-based!)
   */
  public int getDateTimeConstant ()
  {
    return m_eDayOfWeek.getValue ();
  }

  /**
   * @return The java.util.Calendar ID (1-based!)
   */
  public int getCalendarConstant ()
  {
    return m_nCalID;
  }

  /**
   * Get the day of week in a number of days
   *
   * @param nDays
   *        Days to add. No constraints
   * @return Never <code>null</code>.
   */
  @Nonnull
  public EDayOfWeek plus (final int nDays)
  {
    return values ()[(ordinal () + nDays) % 7];
  }

  @Nullable
  public String getWeekdayName (@Nonnull final Locale aLocale)
  {
    return ArrayHelper.getSafeElement (DateFormatSymbols.getInstance (aLocale).getWeekdays (), m_nCalID);
  }

  @Nullable
  public String getWeekdayShortName (@Nonnull final Locale aLocale)
  {
    return ArrayHelper.getSafeElement (DateFormatSymbols.getInstance (aLocale).getShortWeekdays (), m_nCalID);
  }

  @Nullable
  public static EDayOfWeek getFromIDOrNull (final int nID)
  {
    return EnumHelper.getFromIDOrNull (EDayOfWeek.class, nID);
  }

  @Nullable
  public static EDayOfWeek getFromCalendarIDOrNull (final int nID)
  {
    return EnumHelper.findFirst (EDayOfWeek.class, e -> e.getCalendarConstant () == nID);
  }
}
