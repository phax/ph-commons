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
package com.helger.datetime.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;

import com.helger.annotation.CheckForSigned;
import com.helger.annotation.Nonnull;
import com.helger.annotation.concurrent.Immutable;

import com.helger.commons.CGlobal;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.math.MathHelper;

/**
 * Display helper methods for date/time functions.
 *
 * @author Philip Helger
 * @since 9.4.0
 */
@Immutable
public final class PDTDisplayHelper
{
  /**
   * Text abstraction for creating multilingual results.
   *
   * @author Philip Helger
   */
  public interface IPeriodTextProvider
  {
    IPeriodTextProvider EN = new IPeriodTextProvider ()
    {};
    IPeriodTextProvider DE = new IPeriodTextProvider ()
    {
      @Nonnull
      @Nonempty
      @Override
      public String getYears (@CheckForSigned final int nYears)
      {
        // Use "abs" to ensure it is "1 year" and "-1 year"
        return MathHelper.abs (nYears) == 1 ? nYears + " Jahr" : nYears + " Jahre";
      }

      @Nonnull
      @Nonempty
      @Override
      public String getMonths (@CheckForSigned final int nMonths)
      {
        return MathHelper.abs (nMonths) == 1 ? nMonths + " Monat" : nMonths + " Monate";
      }

      @Nonnull
      @Nonempty
      @Override
      public String getDays (@CheckForSigned final int nDays)
      {
        return MathHelper.abs (nDays) == 1 ? nDays + " Tag" : nDays + " Tage";
      }

      @Nonnull
      @Nonempty
      @Override
      public String getHours (@CheckForSigned final long nHours)
      {
        return MathHelper.abs (nHours) == 1 ? nHours + " Stunde" : nHours + " Stunden";
      }

      @Nonnull
      @Nonempty
      @Override
      public String getMinutes (@CheckForSigned final long nMinutes)
      {
        return MathHelper.abs (nMinutes) == 1 ? nMinutes + " Minute" : nMinutes + " Minuten";
      }

      @Nonnull
      @Nonempty
      @Override
      public String getSeconds (@CheckForSigned final long nSeconds)
      {
        return MathHelper.abs (nSeconds) == 1 ? nSeconds + " Sekunde" : nSeconds + " Sekunden";
      }

      @Nonnull
      @Override
      public String getAnd ()
      {
        return " und ";
      }
    };

    @Nonnull
    @Nonempty
    default String getYears (@CheckForSigned final int nYears)
    {
      // Use "abs" to ensure it is "1 year" and "-1 year"
      return MathHelper.abs (nYears) == 1 ? nYears + " year" : nYears + " years";
    }

    @Nonnull
    @Nonempty
    default String getMonths (@CheckForSigned final int nMonths)
    {
      return MathHelper.abs (nMonths) == 1 ? nMonths + " month" : nMonths + " months";
    }

    @Nonnull
    @Nonempty
    default String getDays (@CheckForSigned final int nDays)
    {
      return MathHelper.abs (nDays) == 1 ? nDays + " day" : nDays + " days";
    }

    @Nonnull
    @Nonempty
    default String getHours (@CheckForSigned final long nHours)
    {
      return MathHelper.abs (nHours) == 1 ? nHours + " hour" : nHours + " hours";
    }

    @Nonnull
    @Nonempty
    default String getMinutes (@CheckForSigned final long nMinutes)
    {
      return MathHelper.abs (nMinutes) == 1 ? nMinutes + " minute" : nMinutes + " minutes";
    }

    @Nonnull
    @Nonempty
    default String getSeconds (@CheckForSigned final long nSeconds)
    {
      return MathHelper.abs (nSeconds) == 1 ? nSeconds + " second" : nSeconds + " seconds";
    }

    @Nonnull
    default String getAnd ()
    {
      return " and ";
    }

    @Nonnull
    default String getComma ()
    {
      return ", ";
    }
  }

  @PresentForCodeCoverage
  private static final PDTDisplayHelper INSTANCE = new PDTDisplayHelper ();

  private PDTDisplayHelper ()
  {}

  @Nonnull
  @Nonempty
  public static String getPeriodText (final int nYears,
                                      final int nMonths,
                                      final int nDays,
                                      final long nHours,
                                      final long nMinutes,
                                      final long nSeconds,
                                      @Nonnull final IPeriodTextProvider aTextProvider)
  {
    final String sYear = aTextProvider.getYears (nYears);
    final String sMonth = aTextProvider.getMonths (nMonths);
    final String sDay = aTextProvider.getDays (nDays);
    final String sHour = aTextProvider.getHours (nHours);
    final String sMinute = aTextProvider.getMinutes (nMinutes);
    final String sSecond = aTextProvider.getSeconds (nSeconds);

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
      return aParts.get (0) + aTextProvider.getAnd () + aParts.get (1);

    final StringBuilder aSB = new StringBuilder ();
    for (int i = 0; i < nParts - 1; ++i)
    {
      if (aSB.length () > 0)
        aSB.append (aTextProvider.getComma ());
      aSB.append (aParts.get (i));
    }
    return aSB.append (aTextProvider.getAnd ()).append (aParts.getLastOrNull ()).toString ();
  }

  @Nonnull
  @Nonempty
  public static String getPeriodText (@Nonnull final LocalDateTime aNowLDT,
                                      @Nonnull final LocalDateTime aNotAfter,
                                      @Nonnull final IPeriodTextProvider aTextProvider)
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

    return getPeriodText (nYears, nMonth, nDays, nHours, nMinutes, nTotalSecs, aTextProvider);
  }

  @Nonnull
  @Nonempty
  public static String getPeriodTextDE (@Nonnull final LocalDateTime aNowLDT, @Nonnull final LocalDateTime aNotAfter)
  {
    return getPeriodText (aNowLDT, aNotAfter, IPeriodTextProvider.DE);
  }

  @Nonnull
  @Nonempty
  public static String getPeriodTextEN (@Nonnull final LocalDateTime aNowLDT, @Nonnull final LocalDateTime aNotAfter)
  {
    return getPeriodText (aNowLDT, aNotAfter, IPeriodTextProvider.EN);
  }
}
