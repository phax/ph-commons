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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZonedDateTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.junit.Test;

/**
 * Test class for class {@link PDTFromString}.
 *
 * @author Philip Helger
 */
public final class PDTFromStringTest
{
  @Test
  public void testFromString ()
  {
    // No chronology
    DateTimeFormatter aDTF = PDTFormatter.getForPattern ("uuuu/MM/dd HH:mm:ss");
    assertNotNull (aDTF);
    LocalDateTime aLDT = PDTFromString.getLocalDateTimeFromString ("2009/03/28 15:06:34", aDTF);
    assertNotNull (aLDT);
    assertEquals (IsoChronology.INSTANCE, aLDT.getChronology ());

    // Our default chronology
    aDTF = PDTFormatter.getForPattern ("uuuu/MM/dd HH:mm:ss");
    assertNotNull (aDTF);
    aLDT = PDTFromString.getLocalDateTimeFromString ("2009/03/28 15:06:34", aDTF);
    assertNotNull (aLDT);
    assertEquals ("2009/03/28 15:06:34",
                  aDTF.format (PDTFactory.createLocalDateTime (2009, Month.MARCH, 28, 15, 6, 34)));

    // 'y' instead of 'u'
    // y == year of era
    // u == year
    aDTF = PDTFormatter.getForPattern ("yyyy/MM/dd HH:mm:ss");
    assertNotNull (aDTF);
    aLDT = PDTFromString.getLocalDateTimeFromString ("2009/03/28 15:06:34", aDTF);
    assertNull (aLDT);
    assertEquals ("2009/03/28 15:06:34",
                  aDTF.format (PDTFactory.createLocalDateTime (2009, Month.MARCH, 28, 15, 6, 34)));
  }

  @Test
  public void testDateTimeFromString ()
  {
    assertEquals (LocalDateTime.of (2000, Month.JULY, 6, 0, 0),
                  PDTFromString.getLocalDateTimeFromString ("2000.07.06 00:00", "uuuu.MM.dd HH:mm"));
    assertNull (PDTFromString.getLocalDateTimeFromString ("2000.07.06", "uuuu.MM.dd"));
    assertNull (PDTFromString.getLocalDateTimeFromString ("2000.07.06 abc", "uuuu.MM.dd"));
    assertNull (PDTFromString.getLocalDateTimeFromString (null, "uuuu.MM.dd"));

    try
    {
      // Illegal DT pattern
      PDTFromString.getLocalDateTimeFromString ("2000.07.06", "abc");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      // null DT pattern
      PDTFromString.getLocalDateTimeFromString ("2000.07.06", (DateTimeFormatter) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testLocalDateFromString ()
  {
    assertEquals (LocalDate.of (2000, Month.JULY, 6),
                  PDTFromString.getLocalDateFromString ("2000.07.06", "uuuu.MM.dd"));
    assertNull (PDTFromString.getLocalDateFromString ("2000.07.06 abc", "uuuu.MM.dd"));
    assertNull (PDTFromString.getLocalDateFromString (null, "uuuu.MM.dd"));
    // No February 30th
    assertNull (PDTFromString.getLocalDateFromString ("2000.02.30", "uuuu.MM.dd"));

    // test after year 2018 for the milliseconds since 1970 issue
    assertEquals (LocalDate.of (2038, Month.JANUARY, 20),
                  PDTFromString.getLocalDateFromString ("20.01.2038", Locale.GERMANY));
    assertEquals (LocalDate.of (2038, Month.JANUARY, 20),
                  PDTFromString.getLocalDateFromString ("20.01.2038",
                                                        PDTFormatter.getFormatterDate (PDTFormatter.DEFAULT_STYLE,
                                                                                       Locale.GERMANY,
                                                                                       EDTFormatterMode.PARSE)));
    assertEquals (LocalDate.of (2938, Month.JANUARY, 20),
                  PDTFromString.getLocalDateFromString ("20.01.2938", Locale.GERMANY));

    try
    {
      // Illegal DT pattern
      PDTFromString.getLocalDateFromString ("2000.07.06", "abc");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      // null DT pattern
      PDTFromString.getLocalDateFromString ("2000.07.06", (DateTimeFormatter) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testDefaultToStringAndBack ()
  {
    final ZonedDateTime aDT = PDTFactory.getCurrentZonedDateTime ();
    String sDT = aDT.toString ();
    assertEquals (aDT, ZonedDateTime.parse (sDT));

    final LocalDateTime aLDT = PDTFactory.getCurrentLocalDateTime ();
    sDT = aLDT.toString ();
    assertEquals (aLDT, LocalDateTime.parse (sDT));

    final LocalDate aLD = PDTFactory.getCurrentLocalDate ();
    sDT = aLD.toString ();
    assertEquals (aLD, LocalDate.parse (sDT));

    final LocalTime aLT = PDTFactory.getCurrentLocalTime ();
    sDT = aLT.toString ();
    assertEquals (aLT, LocalTime.parse (sDT));
  }
}
