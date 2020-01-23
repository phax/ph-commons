/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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

import static org.junit.Assert.assertEquals;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Period;

import javax.annotation.Nonnull;

import org.junit.Test;

import com.helger.commons.CGlobal;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.math.MathHelper;

public final class PeriodFuncTest
{
  @Nonnull
  @Nonempty
  private static String _getPeriodString (final int nYears,
                                          final int nMonths,
                                          final int nDays,
                                          final long nHours,
                                          final long nMinutes,
                                          final long nSeconds)
  {
    // Use "abs" to ensure it is "1 year" and "-1 year"
    final String sYear = MathHelper.abs (nYears) == 1 ? nYears + " year" : nYears + " years";
    final String sMonth = MathHelper.abs (nMonths) == 1 ? nMonths + " month" : nMonths + " months";
    final String sDay = MathHelper.abs (nDays) == 1 ? nDays + " day" : nDays + " days";
    final String sHour = MathHelper.abs (nHours) == 1 ? nHours + " hour" : nHours + " hours";
    final String sMinute = MathHelper.abs (nMinutes) == 1 ? nMinutes + " minute" : nMinutes + " minutes";
    final String sSecond = MathHelper.abs (nSeconds) == 1 ? nSeconds + " second" : nSeconds + " seconds";

    // Skip all "leading 0" parts
    final ICommonsList <String> aParts = new CommonsArrayList <> (6);
    if (nYears != 0)
      aParts.add (sYear);
    if (nMonths != 0 || aParts.isNotEmpty ())
      aParts.add (sMonth);
    if (nDays != 0 || aParts.isNotEmpty ())
      aParts.add (sDay);
    if (nHours != 0 || aParts.isNotEmpty ())
      aParts.add (sHour);
    if (nMinutes != 0 || aParts.isNotEmpty ())
      aParts.add (sMinute);
    aParts.add (sSecond);

    final int nParts = aParts.size ();
    if (nParts == 1)
      return aParts.get (0);
    if (nParts == 2)
      return aParts.get (0) + " and " + aParts.get (1);
    final StringBuilder aSB = new StringBuilder ();
    for (int i = 0; i < nParts - 1; ++i)
    {
      if (aSB.length () > 0)
        aSB.append (", ");
      aSB.append (aParts.get (i));
    }
    return aSB.append (" and ").append (aParts.getLast ()).toString ();
  }

  @Nonnull
  @Nonempty
  private static String _getPeriodText (@Nonnull final LocalDateTime aNowLDT, @Nonnull final LocalDateTime aNotAfter)
  {
    final Period aPeriod = Period.between (aNowLDT.toLocalDate (), aNotAfter.toLocalDate ());
    final Duration aDuration = Duration.between (aNowLDT.toLocalTime (), aNotAfter.toLocalTime ());

    final int nYears = aPeriod.getYears ();
    final int nMonth = aPeriod.getMonths ();
    int nDays = aPeriod.getDays ();

    long nTotalSecs = aDuration.getSeconds ();
    if (nTotalSecs < 0)
    {
      if (nDays > 0 || nMonth > 0 || nYears > 0)
      {
        nTotalSecs += CGlobal.SECONDS_PER_DAY;
        nDays--;
      }
    }

    final long nHours = nTotalSecs / CGlobal.SECONDS_PER_HOUR;
    nTotalSecs -= nHours * CGlobal.SECONDS_PER_HOUR;
    final long nMinutes = nTotalSecs / CGlobal.SECONDS_PER_MINUTE;
    nTotalSecs -= nMinutes * CGlobal.SECONDS_PER_MINUTE;

    return _getPeriodString (nYears, nMonth, nDays, nHours, nMinutes, nTotalSecs);
  }

  @Test
  public void testDurationBetween ()
  {
    assertEquals ("0 seconds", _getPeriodString (0, 0, 0, 0, 0, 0));
    assertEquals ("1 second", _getPeriodString (0, 0, 0, 0, 0, 1));
    assertEquals ("2 seconds", _getPeriodString (0, 0, 0, 0, 0, 2));
    assertEquals ("1 minute and 2 seconds", _getPeriodString (0, 0, 0, 0, 1, 2));
    assertEquals ("1 hour, 0 minutes and 2 seconds", _getPeriodString (0, 0, 0, 1, 0, 2));
    assertEquals ("1 day, 0 hours, 0 minutes and 2 seconds", _getPeriodString (0, 0, 1, 0, 0, 2));
    assertEquals ("1 month, 0 days, 0 hours, 0 minutes and 2 seconds", _getPeriodString (0, 1, 0, 0, 0, 2));
    assertEquals ("1 year, 0 months, 0 days, 0 hours, 0 minutes and 2 seconds", _getPeriodString (1, 0, 0, 0, 0, 2));
    assertEquals ("1 year, 2 months, 3 days, 4 hours, 5 minutes and 6 seconds", _getPeriodString (1, 2, 3, 4, 5, 6));

    final LocalDateTime aLDT = PDTFactory.createLocalDateTime (2019, Month.DECEMBER, 31, 9, 0, 0);
    assertEquals ("0 seconds", _getPeriodText (aLDT, aLDT));
    assertEquals ("15 minutes and 12 seconds",
                  _getPeriodText (PDTFactory.createLocalDateTime (2019, Month.DECEMBER, 31, 8, 44, 48), aLDT));
    assertEquals ("2 hours, 0 minutes and 0 seconds",
                  _getPeriodText (PDTFactory.createLocalDateTime (2019, Month.DECEMBER, 31, 7, 0, 0), aLDT));
    assertEquals ("12 hours, 0 minutes and 0 seconds",
                  _getPeriodText (PDTFactory.createLocalDateTime (2019, Month.DECEMBER, 30, 21, 0, 0), aLDT));
    assertEquals ("1 day, 12 hours, 0 minutes and 0 seconds",
                  _getPeriodText (PDTFactory.createLocalDateTime (2019, Month.DECEMBER, 29, 21, 0, 0), aLDT));
    assertEquals ("2 days, 12 hours, 0 minutes and 0 seconds",
                  _getPeriodText (PDTFactory.createLocalDateTime (2019, Month.DECEMBER, 28, 21, 0, 0), aLDT));
    assertEquals ("1 month, 0 days, 12 hours, 0 minutes and 0 seconds",
                  _getPeriodText (PDTFactory.createLocalDateTime (2019, Month.NOVEMBER, 30, 21, 0, 0), aLDT));
    assertEquals ("1 year, 0 months, 0 days, 12 hours, 0 minutes and 0 seconds",
                  _getPeriodText (PDTFactory.createLocalDateTime (2018, Month.DECEMBER, 30, 21, 0, 0), aLDT));
    assertEquals ("1 year, 0 months, 0 days, 0 hours, 0 minutes and 0 seconds",
                  _getPeriodText (PDTFactory.createLocalDateTime (2018, Month.DECEMBER, 31, 9, 0, 0), aLDT));
    assertEquals ("1 year, 5 months, 25 days, 0 hours, 15 minutes and 13 seconds",
                  _getPeriodText (PDTFactory.createLocalDateTime (2018, Month.JULY, 6, 8, 44, 47), aLDT));

    assertEquals ("-3 hours, 0 minutes and 0 seconds",
                  _getPeriodText (PDTFactory.createLocalDateTime (2019, Month.DECEMBER, 31, 12, 0, 0), aLDT));
    assertEquals ("-1 day, -3 hours, 0 minutes and 0 seconds",
                  _getPeriodText (PDTFactory.createLocalDateTime (2020, Month.JANUARY, 1, 12, 0, 0), aLDT));
  }
}
