/*
 * Copyright (C) 2014-2023 Philip Helger (www.helger.com)
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

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.Month;

import org.junit.Test;

import com.helger.commons.datetime.PDTFactory;
import com.helger.datetime.util.PDTDisplayHelper.IPeriodTextProvider;

/**
 * Test class for class {@link PDTDisplayHelper}.
 *
 * @author Philip Helger
 */
public final class PDTDisplayHelperTest
{
  @Test
  public void testGetPeriodTextNumericEN ()
  {
    final IPeriodTextProvider aTP = IPeriodTextProvider.EN;
    assertEquals ("0 seconds", PDTDisplayHelper.getPeriodText (0, 0, 0, 0, 0, 0, aTP));
    assertEquals ("1 second", PDTDisplayHelper.getPeriodText (0, 0, 0, 0, 0, 1, aTP));
    assertEquals ("2 seconds", PDTDisplayHelper.getPeriodText (0, 0, 0, 0, 0, 2, aTP));
    assertEquals ("1 minute and 2 seconds", PDTDisplayHelper.getPeriodText (0, 0, 0, 0, 1, 2, aTP));
    assertEquals ("1 hour, 0 minutes and 2 seconds", PDTDisplayHelper.getPeriodText (0, 0, 0, 1, 0, 2, aTP));
    assertEquals ("1 day, 0 hours, 0 minutes and 2 seconds", PDTDisplayHelper.getPeriodText (0, 0, 1, 0, 0, 2, aTP));
    assertEquals ("1 month, 0 days, 0 hours, 0 minutes and 2 seconds", PDTDisplayHelper.getPeriodText (0, 1, 0, 0, 0, 2, aTP));
    assertEquals ("1 year, 0 months, 0 days, 0 hours, 0 minutes and 2 seconds", PDTDisplayHelper.getPeriodText (1, 0, 0, 0, 0, 2, aTP));
    assertEquals ("1 year, 2 months, 3 days, 4 hours, 5 minutes and 6 seconds", PDTDisplayHelper.getPeriodText (1, 2, 3, 4, 5, 6, aTP));
    assertEquals ("2 years, 2 months, 3 days, 4 hours, 5 minutes and 6 seconds", PDTDisplayHelper.getPeriodText (2, 2, 3, 4, 5, 6, aTP));
  }

  @Test
  public void testGetPeriodTextNumericDE ()
  {
    final IPeriodTextProvider aTP = IPeriodTextProvider.DE;
    assertEquals ("0 Sekunden", PDTDisplayHelper.getPeriodText (0, 0, 0, 0, 0, 0, aTP));
    assertEquals ("1 Sekunde", PDTDisplayHelper.getPeriodText (0, 0, 0, 0, 0, 1, aTP));
    assertEquals ("2 Sekunden", PDTDisplayHelper.getPeriodText (0, 0, 0, 0, 0, 2, aTP));
    assertEquals ("1 Minute und 2 Sekunden", PDTDisplayHelper.getPeriodText (0, 0, 0, 0, 1, 2, aTP));
    assertEquals ("1 Stunde, 0 Minuten und 2 Sekunden", PDTDisplayHelper.getPeriodText (0, 0, 0, 1, 0, 2, aTP));
    assertEquals ("1 Tag, 0 Stunden, 0 Minuten und 2 Sekunden", PDTDisplayHelper.getPeriodText (0, 0, 1, 0, 0, 2, aTP));
    assertEquals ("1 Monat, 0 Tage, 0 Stunden, 0 Minuten und 2 Sekunden", PDTDisplayHelper.getPeriodText (0, 1, 0, 0, 0, 2, aTP));
    assertEquals ("1 Jahr, 0 Monate, 0 Tage, 0 Stunden, 0 Minuten und 2 Sekunden", PDTDisplayHelper.getPeriodText (1, 0, 0, 0, 0, 2, aTP));
    assertEquals ("1 Jahr, 2 Monate, 3 Tage, 4 Stunden, 5 Minuten und 6 Sekunden", PDTDisplayHelper.getPeriodText (1, 2, 3, 4, 5, 6, aTP));
    assertEquals ("2 Jahre, 2 Monate, 3 Tage, 4 Stunden, 5 Minuten und 6 Sekunden", PDTDisplayHelper.getPeriodText (2, 2, 3, 4, 5, 6, aTP));
  }

  @Test
  public void testGetPeriodTextLocalDateTime ()
  {
    final LocalDateTime aLDT = PDTFactory.createLocalDateTime (2019, Month.DECEMBER, 31, 9, 0, 0);
    assertEquals ("0 seconds", PDTDisplayHelper.getPeriodTextEN (aLDT, aLDT));
    assertEquals ("15 minutes and 12 seconds",
                  PDTDisplayHelper.getPeriodTextEN (PDTFactory.createLocalDateTime (2019, Month.DECEMBER, 31, 8, 44, 48), aLDT));
    assertEquals ("2 hours, 0 minutes and 0 seconds",
                  PDTDisplayHelper.getPeriodTextEN (PDTFactory.createLocalDateTime (2019, Month.DECEMBER, 31, 7, 0, 0), aLDT));
    assertEquals ("12 hours, 0 minutes and 0 seconds",
                  PDTDisplayHelper.getPeriodTextEN (PDTFactory.createLocalDateTime (2019, Month.DECEMBER, 30, 21, 0, 0), aLDT));
    assertEquals ("1 day, 12 hours, 0 minutes and 0 seconds",
                  PDTDisplayHelper.getPeriodTextEN (PDTFactory.createLocalDateTime (2019, Month.DECEMBER, 29, 21, 0, 0), aLDT));
    assertEquals ("2 days, 12 hours, 0 minutes and 0 seconds",
                  PDTDisplayHelper.getPeriodTextEN (PDTFactory.createLocalDateTime (2019, Month.DECEMBER, 28, 21, 0, 0), aLDT));
    assertEquals ("1 month, 0 days, 12 hours, 0 minutes and 0 seconds",
                  PDTDisplayHelper.getPeriodTextEN (PDTFactory.createLocalDateTime (2019, Month.NOVEMBER, 30, 21, 0, 0), aLDT));
    assertEquals ("1 year, 0 months, 0 days, 12 hours, 0 minutes and 0 seconds",
                  PDTDisplayHelper.getPeriodTextEN (PDTFactory.createLocalDateTime (2018, Month.DECEMBER, 30, 21, 0, 0), aLDT));
    assertEquals ("1 year, 0 months, 0 days, 0 hours, 0 minutes and 0 seconds",
                  PDTDisplayHelper.getPeriodTextEN (PDTFactory.createLocalDateTime (2018, Month.DECEMBER, 31, 9, 0, 0), aLDT));
    assertEquals ("1 year, 5 months, 25 days, 0 hours, 15 minutes and 13 seconds",
                  PDTDisplayHelper.getPeriodTextEN (PDTFactory.createLocalDateTime (2018, Month.JULY, 6, 8, 44, 47), aLDT));

    assertEquals ("-3 hours, 0 minutes and 0 seconds",
                  PDTDisplayHelper.getPeriodTextEN (PDTFactory.createLocalDateTime (2019, Month.DECEMBER, 31, 12, 0, 0), aLDT));
    assertEquals ("-1 day, -3 hours, 0 minutes and 0 seconds",
                  PDTDisplayHelper.getPeriodTextEN (PDTFactory.createLocalDateTime (2020, Month.JANUARY, 1, 12, 0, 0), aLDT));
  }
}
