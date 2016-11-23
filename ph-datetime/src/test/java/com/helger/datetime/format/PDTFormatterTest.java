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
package com.helger.datetime.format;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import org.junit.Test;

import com.helger.commons.locale.LocaleCache;

/**
 * Test class for class {@link PDTFormatter}.
 *
 * @author Philip Helger
 */
public final class PDTFormatterTest
{
  @Test
  public void testGetDefaultFormatter ()
  {
    for (final Locale aLocale : LocaleCache.getInstance ().getAllLocales ())
    {
      // get formatter
      assertNotNull (PDTFormatter.getDefaultFormatterDate (aLocale));
      assertNotNull (PDTFormatter.getDefaultFormatterTime (aLocale));
      assertNotNull (PDTFormatter.getDefaultFormatterDateTime (aLocale));
    }
    assertNotNull (PDTFormatter.getDefaultFormatterDate (null));
    assertNotNull (PDTFormatter.getDefaultFormatterTime (null));
    assertNotNull (PDTFormatter.getDefaultFormatterDateTime (null));
  }

  @Test
  public void testGetForPattern ()
  {
    for (final Locale aLocale : LocaleCache.getInstance ().getAllLocales ())
      assertNotNull (PDTFormatter.getForPattern ("uuuu-MM-dd", aLocale));
    assertNotNull (PDTFormatter.getForPattern ("uuuu-MM-dd"));
    assertNotNull (PDTFormatter.getForPattern ("uuuu-MM-dd", null));

    try
    {
      PDTFormatter.getForPattern ("abcdefghijklmnopqrstuvwxyz");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testGetDefault ()
  {
    // Must be smart because "year or era" is used internally
    assertEquals (LocalDate.of (2015, Month.FEBRUARY, 28),
                  PDTFormatter.getDefaultFormatterDate (Locale.GERMANY)
                              .withResolverStyle (ResolverStyle.SMART)
                              .parse ("28.02.2015", LocalDate::from));
    try
    {
      // There is no such thing as 30.2. in strict mode
      PDTFormatter.getDefaultFormatterDate (Locale.GERMANY).parse ("30.02.2015", LocalDate::from);
      fail ();
    }
    catch (final DateTimeParseException ex)
    {}

    // In smart mode this can be parsed
    assertEquals (LocalDate.of (2015, Month.FEBRUARY, 28),
                  PDTFormatter.getDefaultFormatterDate (Locale.GERMANY)
                              .withResolverStyle (ResolverStyle.SMART)
                              .parse ("30.02.2015", LocalDate::from));
  }

  @Test
  public void testLeadingZero ()
  {
    // Must be smart because "year or era" is used internally
    assertEquals (LocalDate.of (2015, Month.FEBRUARY, 1),
                  PDTFormatter.getDefaultFormatterDate (Locale.GERMANY)
                              .withResolverStyle (ResolverStyle.LENIENT)
                              .parse ("1.2.2015", LocalDate::from));
    assertEquals (LocalDate.of (2015, Month.FEBRUARY, 1),
                  PDTFormatter.getDefaultFormatterDate (Locale.GERMANY).parse ("1.2.2015", LocalDate::from));

    assertEquals (LocalDateTime.of (2015, Month.FEBRUARY, 1, 3, 45, 1),
                  PDTFormatter.getDefaultFormatterDateTime (Locale.GERMANY).parse ("01.02.2015 03:45:01",
                                                                                   LocalDateTime::from));
    assertEquals (LocalDateTime.of (2015, Month.FEBRUARY, 1, 3, 45, 1),
                  PDTFormatter.getDefaultFormatterDateTime (Locale.GERMANY).parse ("1.2.2015 3:45:1",
                                                                                   LocalDateTime::from));
  }

  @Test
  public void testSpecialCases ()
  {
    final LocalDateTime dt = LocalDateTime.of (2016, Month.MAY, 5, 20, 00, 00);
    assertEquals ("2016-05-05T20:00", dt.toString ());
    assertEquals ("2016-05-05T20:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format (dt));
  }
}
