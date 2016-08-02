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
import java.time.Month;
import java.util.Calendar;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.id.IHasIntID;
import com.helger.commons.lang.EnumHelper;

/**
 * Represents all known Gregorian Calendar month as a type-safe enum
 *
 * @author Philip Helger
 */
public enum EMonth implements IHasIntID
{
  JANUARY (Month.JANUARY, Calendar.JANUARY),
  FEBRUARY (Month.FEBRUARY, Calendar.FEBRUARY),
  MARCH (Month.MARCH, Calendar.MARCH),
  APRIL (Month.APRIL, Calendar.APRIL),
  MAY (Month.MAY, Calendar.MAY),
  JUNE (Month.JUNE, Calendar.JUNE),
  JULY (Month.JULY, Calendar.JULY),
  AUGUST (Month.AUGUST, Calendar.AUGUST),
  SEPTEMBER (Month.SEPTEMBER, Calendar.SEPTEMBER),
  OCTOBER (Month.OCTOBER, Calendar.OCTOBER),
  NOVEMBER (Month.NOVEMBER, Calendar.NOVEMBER),
  DECEMBER (Month.DECEMBER, Calendar.DECEMBER);

  private final Month m_eMonth;
  private final int m_nCalID;

  private EMonth (final Month eMonth, final int nCalID)
  {
    m_eMonth = eMonth;
    m_nCalID = nCalID;
  }

  public int getID ()
  {
    return getMonthConstant ();
  }

  /**
   * @return The JDK8 time ID
   */
  public int getMonthConstant ()
  {
    return m_eMonth.getValue ();
  }

  /**
   * @return The java.util.Calendar ID
   */
  public int getCalendarConstant ()
  {
    return m_nCalID;
  }

  @Nullable
  public String getMonthName (@Nonnull final Locale aLocale)
  {
    return ArrayHelper.getSafeElement (DateFormatSymbols.getInstance (aLocale).getMonths (), m_nCalID);
  }

  @Nullable
  public String getMonthShortName (@Nonnull final Locale aLocale)
  {
    return ArrayHelper.getSafeElement (DateFormatSymbols.getInstance (aLocale).getShortMonths (), m_nCalID);
  }

  /**
   * Get the month in a number of month
   *
   * @param nMonth
   *        Month to add. No constraints
   * @return Never <code>null</code>.
   */
  @Nonnull
  public EMonth plus (final int nMonth)
  {
    return values ()[(ordinal () + nMonth) % 12];
  }

  @Nullable
  public static EMonth getFromIDOrNull (final int nID)
  {
    return EnumHelper.getFromIDOrNull (EMonth.class, nID);
  }
}
