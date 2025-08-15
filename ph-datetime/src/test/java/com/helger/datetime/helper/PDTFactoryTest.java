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
package com.helger.datetime.helper;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;

import com.helger.datetime.xml.XMLOffsetTime;

/**
 * Test class for {@link PDTFactory}.
 *
 * @author Philip Helger
 */
public final class PDTFactoryTest
{
  @Test
  public void testCurrent ()
  {
    assertNotNull (PDTFactory.getCurrentZonedDateTime ());
    assertNotNull (PDTFactory.getCurrentZonedDateTimeMillisOnly ());
    assertNotNull (PDTFactory.getCurrentZonedDateTimeUTC ());
    assertNotNull (PDTFactory.getCurrentZonedDateTimeMillisOnlyUTC ());

    assertNotNull (PDTFactory.getCurrentOffsetDateTime ());
    assertNotNull (PDTFactory.getCurrentOffsetDateTimeMillisOnly ());
    assertNotNull (PDTFactory.getCurrentOffsetDateTimeUTC ());
    assertNotNull (PDTFactory.getCurrentOffsetDateTimeMillisOnlyUTC ());

    assertNotNull (PDTFactory.getCurrentXMLOffsetDateTime ());
    assertNotNull (PDTFactory.getCurrentXMLOffsetDateTimeMillisOnly ());
    assertNotNull (PDTFactory.getCurrentXMLOffsetDateTimeUTC ());
    assertNotNull (PDTFactory.getCurrentXMLOffsetDateTimeMillisOnlyUTC ());

    assertNotNull (PDTFactory.getCurrentLocalDateTime ());
    assertNotNull (PDTFactory.getCurrentLocalDateTimeMillisOnly ());
    assertNotNull (PDTFactory.getCurrentLocalDateTimeUTC ());
    assertNotNull (PDTFactory.getCurrentLocalDateTimeMillisOnlyUTC ());

    assertNotNull (PDTFactory.getCurrentOffsetDate ());
    assertNotNull (PDTFactory.getCurrentOffsetDateUTC ());

    assertNotNull (PDTFactory.getCurrentXMLOffsetDate ());
    assertNotNull (PDTFactory.getCurrentXMLOffsetDateUTC ());

    assertNotNull (PDTFactory.getCurrentLocalDate ());
    assertNotNull (PDTFactory.getCurrentLocalDateUTC ());

    assertNotNull (PDTFactory.getCurrentOffsetTime ());
    assertNotNull (PDTFactory.getCurrentOffsetTimeMillisOnly ());
    assertNotNull (PDTFactory.getCurrentOffsetTimeUTC ());
    assertNotNull (PDTFactory.getCurrentOffsetTimeMillisOnlyUTC ());

    assertNotNull (PDTFactory.getCurrentXMLOffsetTime ());
    assertNotNull (PDTFactory.getCurrentXMLOffsetTimeMillisOnly ());
    assertNotNull (PDTFactory.getCurrentXMLOffsetTimeUTC ());
    assertNotNull (PDTFactory.getCurrentXMLOffsetTimeMillisOnlyUTC ());

    assertNotNull (PDTFactory.getCurrentLocalTime ());
    assertNotNull (PDTFactory.getCurrentLocalTimeMillisOnly ());
    assertNotNull (PDTFactory.getCurrentLocalTimeUTC ());
    assertNotNull (PDTFactory.getCurrentLocalTimeMillisOnlyUTC ());

    assertNotNull (PDTFactory.getCurrentYearObj ());
    assertNotNull (PDTFactory.getCurrentYearMonth ());
  }

  @Test
  public void testCreateLocalDate ()
  {
    assertNotNull (PDTFactory.createLocalDate (new Date ()));
    assertNotNull (PDTFactory.createLocalDate (new GregorianCalendar (TimeZone.getDefault (), Locale.getDefault ())));
    assertNotNull (PDTFactory.createLocalDate (Instant.ofEpochMilli (1234)));
    assertNotNull (PDTFactory.createLocalDate (PDTFactory.getCurrentLocalDateTime ()));
    assertNotNull (PDTFactory.createLocalDate (PDTFactory.getCurrentLocalDateTimeMillisOnly ()));
    assertNotNull (PDTFactory.createLocalDate (1234));
    assertNotNull (PDTFactory.createLocalDate (PDTFactory.getCurrentYearObj ()));
    assertNotNull (PDTFactory.createLocalDate (PDTFactory.getCurrentYearMonth ()));
    assertNotNull (PDTFactory.createLocalDate (2016, Month.MARCH, 13));

    assertNull (PDTFactory.createLocalDate ((Date) null));
    assertNull (PDTFactory.createLocalDate ((GregorianCalendar) null));
    assertNull (PDTFactory.createLocalDate ((Instant) null));
    assertNull (PDTFactory.createLocalDate ((LocalDateTime) null));
    assertNull (PDTFactory.createLocalDate ((Year) null));
    assertNull (PDTFactory.createLocalDate ((YearMonth) null));
  }

  @Test
  public void testCreateLocalTime ()
  {
    assertNotNull (PDTFactory.createLocalTime (new Date ()));
    assertNotNull (PDTFactory.createLocalTime (new GregorianCalendar (TimeZone.getDefault (), Locale.getDefault ())));
    assertNotNull (PDTFactory.createLocalTime (Instant.ofEpochMilli (1234)));
    assertNotNull (PDTFactory.createLocalTime (PDTFactory.getCurrentLocalDateTime ()));
    assertNotNull (PDTFactory.createLocalTime (PDTFactory.getCurrentLocalDateTimeMillisOnly ()));
    assertNotNull (PDTFactory.createLocalTime (PDTFactory.getCurrentOffsetTime ()));
    assertNotNull (PDTFactory.createLocalTime (1234));

    assertNull (PDTFactory.createLocalTime ((Date) null));
    assertNull (PDTFactory.createLocalTime ((GregorianCalendar) null));
    assertNull (PDTFactory.createLocalTime ((Instant) null));
    assertNull (PDTFactory.createLocalTime ((LocalDateTime) null));
    assertNull (PDTFactory.createLocalTime ((OffsetTime) null));
    assertNull (PDTFactory.createLocalTime ((XMLOffsetTime) null));
  }
}
