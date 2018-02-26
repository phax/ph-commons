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
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.time.format.ResolverStyle;
import java.util.Locale;

import org.junit.Test;

import com.helger.commons.locale.LocaleCache;
import com.helger.commons.system.EJavaVersion;

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
    for (final FormatStyle eStyle : FormatStyle.values ())
      for (final Locale aLocale : LocaleCache.getInstance ().getAllLocales ())
        for (final EDTFormatterMode eMode : EDTFormatterMode.values ())
        {
          // get formatter
          assertNotNull (PDTFormatter.getFormatterDate (eStyle, aLocale, eMode));
          assertNotNull (PDTFormatter.getFormatterTime (eStyle, aLocale, eMode));
          assertNotNull (PDTFormatter.getFormatterDateTime (eStyle, aLocale, eMode));
        }

    // Null locale
    for (final FormatStyle eStyle : FormatStyle.values ())
      for (final EDTFormatterMode eMode : EDTFormatterMode.values ())
      {
        assertNotNull (PDTFormatter.getFormatterDate (eStyle, null, eMode));
        assertNotNull (PDTFormatter.getFormatterTime (eStyle, null, eMode));
        assertNotNull (PDTFormatter.getFormatterDateTime (eStyle, null, eMode));
      }
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
                  PDTFormatter.getFormatterDate (PDTFormatter.DEFAULT_STYLE, Locale.GERMANY, EDTFormatterMode.PARSE)
                              .withResolverStyle (ResolverStyle.SMART)
                              .parse ("28.02.2015", LocalDate::from));
    try
    {
      // There is no such thing as 30.2. in strict mode
      PDTFormatter.getFormatterDate (PDTFormatter.DEFAULT_STYLE, Locale.GERMANY, EDTFormatterMode.PARSE)
                  .parse ("30.02.2015", LocalDate::from);
      fail ();
    }
    catch (final DateTimeParseException ex)
    {}

    // In smart mode this can be parsed
    assertEquals (LocalDate.of (2015, Month.FEBRUARY, 28),
                  PDTFormatter.getFormatterDate (PDTFormatter.DEFAULT_STYLE, Locale.GERMANY, EDTFormatterMode.PARSE)
                              .withResolverStyle (ResolverStyle.SMART)
                              .parse ("30.02.2015", LocalDate::from));
  }

  @Test
  public void testLeadingZero ()
  {
    DateTimeFormatter aFormatter = PDTFormatter.getFormatterDate (PDTFormatter.DEFAULT_STYLE,
                                                                  Locale.GERMANY,
                                                                  EDTFormatterMode.PARSE);
    assertEquals (LocalDate.of (2015, Month.FEBRUARY, 1),
                  aFormatter.withResolverStyle (ResolverStyle.LENIENT).parse ("1.2.2015", LocalDate::from));
    assertEquals (LocalDate.of (2015, Month.FEBRUARY, 1), aFormatter.parse ("1.2.2015", LocalDate::from));

    aFormatter = PDTFormatter.getFormatterDateTime (PDTFormatter.DEFAULT_STYLE, Locale.GERMANY, EDTFormatterMode.PARSE);
    if (EJavaVersion.JDK_9.isSupportedVersion ())
    {
      // Assume CLDR - the "," was added!
      assertEquals (LocalDateTime.of (2015, Month.FEBRUARY, 1, 3, 45, 1),
                    aFormatter.parse ("01.02.2015, 03:45:01", LocalDateTime::from));
      assertEquals (LocalDateTime.of (2015, Month.FEBRUARY, 1, 3, 45, 1),
                    aFormatter.parse ("1.2.2015, 3:45:1", LocalDateTime::from));
    }
    else
    {
      assertEquals (LocalDateTime.of (2015, Month.FEBRUARY, 1, 3, 45, 1),
                    aFormatter.parse ("01.02.2015 03:45:01", LocalDateTime::from));
      assertEquals (LocalDateTime.of (2015, Month.FEBRUARY, 1, 3, 45, 1),
                    aFormatter.parse ("1.2.2015 3:45:1", LocalDateTime::from));
    }
  }

  @Test
  public void testSpecialCases ()
  {
    final LocalDateTime dt = LocalDateTime.of (2016, Month.MAY, 5, 20, 00, 00);
    assertEquals ("2016-05-05T20:00", dt.toString ());
    assertEquals ("2016-05-05T20:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format (dt));
  }

  @Test
  public void testPrintDateExplicit ()
  {
    final LocalDate aDate = LocalDate.of (2015, Month.FEBRUARY, 1);

    // de-de
    assertEquals ("Sonntag, 1. Februar 2015",
                  PDTFormatter.getFormatterDate (FormatStyle.FULL, Locale.GERMANY, EDTFormatterMode.PRINT)
                              .format (aDate));
    assertEquals ("1. Februar 2015",
                  PDTFormatter.getFormatterDate (FormatStyle.LONG, Locale.GERMANY, EDTFormatterMode.PRINT)
                              .format (aDate));
    assertEquals ("01.02.2015",
                  PDTFormatter.getFormatterDate (FormatStyle.MEDIUM, Locale.GERMANY, EDTFormatterMode.PRINT)
                              .format (aDate));
    assertEquals ("01.02.15",
                  PDTFormatter.getFormatterDate (FormatStyle.SHORT, Locale.GERMANY, EDTFormatterMode.PRINT)
                              .format (aDate));

    // en-us
    assertEquals ("Sunday, February 1, 2015",
                  PDTFormatter.getFormatterDate (FormatStyle.FULL, Locale.US, EDTFormatterMode.PRINT).format (aDate));
    assertEquals ("February 1, 2015",
                  PDTFormatter.getFormatterDate (FormatStyle.LONG, Locale.US, EDTFormatterMode.PRINT).format (aDate));
    assertEquals ("Feb 1, 2015",
                  PDTFormatter.getFormatterDate (FormatStyle.MEDIUM, Locale.US, EDTFormatterMode.PRINT).format (aDate));
    assertEquals ("2/1/15",
                  PDTFormatter.getFormatterDate (FormatStyle.SHORT, Locale.US, EDTFormatterMode.PRINT).format (aDate));
  }

  @Test
  public void testPrintAndParseDate ()
  {
    final LocalDate aDate = LocalDate.of (2015, Month.FEBRUARY, 1);
    for (final FormatStyle eStyle : FormatStyle.values ())
      for (final Locale aLocale : new Locale [] { Locale.GERMANY, Locale.US, Locale.FRANCE })
      {
        final String sPrinted = PDTFormatter.getFormatterDate (eStyle, aLocale, EDTFormatterMode.PRINT).format (aDate);
        assertNotNull (sPrinted);

        final LocalDate aParsed = PDTFormatter.getFormatterDate (eStyle, aLocale, EDTFormatterMode.PARSE)
                                              .parse (sPrinted, LocalDate::from);
        assertNotNull (aParsed);
        assertEquals (aDate, aParsed);
      }
  }
}
