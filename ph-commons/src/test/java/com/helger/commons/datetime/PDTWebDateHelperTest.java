/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import javax.annotation.Nonnull;

import org.junit.Test;

/**
 * Test class for class {@link PDTWebDateHelper}.
 *
 * @author Philip Helger
 */
public final class PDTWebDateHelperTest
{
  @Test
  public void testRfc822 ()
  {
    final ZonedDateTime d = ZonedDateTime.now (PDTConfig.getDefaultZoneId ()).withNano (0);
    final String s = PDTWebDateHelper.getAsStringRFC822 (d);
    assertNotNull (s);
    final ZonedDateTime d2 = PDTWebDateHelper.getDateTimeFromRFC822 (s);
    assertNotNull ("Failed to parse RFC822: " + s, d2);
    assertEquals ("Difference after parsing: " + s, d, d2);

    final String sNow = PDTWebDateHelper.getCurrentDateTimeAsStringRFC822 ();
    assertNotNull (PDTWebDateHelper.getDateTimeFromRFC822 (sNow));

    assertNotNull (PDTWebDateHelper.getDateTimeFromRFC822 ("Sun, 03 Jan 2016 23:15:42"));
    assertNotNull (PDTWebDateHelper.getDateTimeFromRFC822 ("Sun, 03 Jan 2016 23:15:42 Z"));
    assertNotNull (PDTWebDateHelper.getDateTimeFromRFC822 ("Sun, 03 Jan 2016 23:15:42 UT"));
    assertNotNull (PDTWebDateHelper.getDateTimeFromRFC822 ("Sun, 03 Jan 2016 23:15:42 UTC"));
    assertNotNull (PDTWebDateHelper.getDateTimeFromRFC822 ("Sun, 03 Jan 2016 23:15:42 GMT"));
    assertNotNull (PDTWebDateHelper.getDateTimeFromRFC822 ("Sun, 03 Jan 2016 23:15:42 EST"));
    assertNotNull (PDTWebDateHelper.getDateTimeFromRFC822 ("Sun, 03 Jan 2016 23:15:42 EDT"));
    assertNotNull (PDTWebDateHelper.getDateTimeFromRFC822 ("Sun, 03 Jan 2016 23:15:42 CST"));
    assertNotNull (PDTWebDateHelper.getDateTimeFromRFC822 ("Sun, 03 Jan 2016 23:15:42 CDT"));
    assertNotNull (PDTWebDateHelper.getDateTimeFromRFC822 ("Sun, 03 Jan 2016 23:15:42 MST"));
    assertNotNull (PDTWebDateHelper.getDateTimeFromRFC822 ("Sun, 03 Jan 2016 23:15:42 MDT"));
    assertNotNull (PDTWebDateHelper.getDateTimeFromRFC822 ("Sun, 03 Jan 2016 23:15:42 PST"));
    assertNotNull (PDTWebDateHelper.getDateTimeFromRFC822 ("Sun, 03 Jan 2016 23:15:42 PDT"));
    assertNotNull (PDTWebDateHelper.getDateTimeFromRFC822 ("Sun, 03 Jan 2016 23:15:42 +0100"));
    assertNotNull (PDTWebDateHelper.getDateTimeFromRFC822 ("Sun, 03 Jan 2016 23:15:42 +0000"));
    assertNotNull (PDTWebDateHelper.getDateTimeFromRFC822 ("Sun, 03 Jan 2016 23:15:42 -0100"));

    assertNull (PDTWebDateHelper.getAsStringRFC822 ((ZonedDateTime) null));
    assertNull (PDTWebDateHelper.getAsStringRFC822 ((OffsetDateTime) null));
    assertNull (PDTWebDateHelper.getAsStringRFC822 ((LocalDateTime) null));
  }

  private static void _testW3C (@Nonnull final OffsetDateTime aSrc)
  {
    // The default pattern has no milliseconds!
    final String s = PDTWebDateHelper.getAsStringW3C (aSrc);
    assertNotNull (s);
    final OffsetDateTime aDT2 = PDTWebDateHelper.getDateTimeFromW3C (s);
    assertNotNull ("Failed to parse W3C date '" + s + "'", aDT2);
    assertEquals (aSrc.withNano (0), aDT2);
  }

  @Test
  public void testW3CTime ()
  {
    _testW3C (PDTFactory.createOffsetDateTime (2010, Month.FEBRUARY, 4));
    _testW3C (PDTFactory.createOffsetDateTime (12345678000L));
    _testW3C (PDTFactory.getCurrentOffsetDateTime ());

    final String sNow = PDTWebDateHelper.getCurrentDateTimeAsStringW3C ();
    assertNotNull (PDTWebDateHelper.getDateTimeFromW3C (sNow));

    assertNotNull (PDTWebDateHelper.getDateTimeFromW3C ("1997"));
    assertNotNull (PDTWebDateHelper.getDateTimeFromW3C ("1997-07"));
    assertNotNull (PDTWebDateHelper.getDateTimeFromW3C ("1997-07-16"));
    assertNotNull (PDTWebDateHelper.getDateTimeFromW3C ("1997-07-16T19:20Z"));
    assertNotNull (PDTWebDateHelper.getDateTimeFromW3C ("1997-07-16T19:20+01:00"));
    assertNotNull (PDTWebDateHelper.getDateTimeFromW3C ("1997-07-16T19:20:30.145Z"));
    assertNotNull (PDTWebDateHelper.getDateTimeFromW3C ("1997-07-16T19:20:30.145+0100"));
    assertNotNull (PDTWebDateHelper.getDateTimeFromW3C ("1997-07-16T19:20:30.145+01:00"));
    assertNotNull (PDTWebDateHelper.getDateTimeFromW3C ("1997-07-16T19:20:30+01:00"));
    assertNotNull (PDTWebDateHelper.getDateTimeFromW3C ("1994-11-05T08:15:30-05:00"));
    assertNotNull (PDTWebDateHelper.getDateTimeFromW3C ("1997-07-16T19:20+01:00"));

    assertNull (PDTWebDateHelper.getAsStringW3C ((ZonedDateTime) null));
    assertNull (PDTWebDateHelper.getAsStringW3C ((OffsetDateTime) null));
    assertNull (PDTWebDateHelper.getAsStringW3C ((LocalDateTime) null));
  }

  @Test
  public void testXSDDateTime ()
  {
    // Millis only for Java 11+
    final ZonedDateTime aDT = PDTFactory.getCurrentZonedDateTimeMillisOnlyUTC ();
    final String s = PDTWebDateHelper.getAsStringXSD (aDT);
    assertNotNull (s);
    assertEquals (aDT, PDTWebDateHelper.getZonedDateTimeFromXSD (s));
    assertNull (PDTWebDateHelper.getAsStringXSD ((ZonedDateTime) null));
  }

  @Test
  public void testXSDLocalDate ()
  {
    final LocalDate aDT = PDTFactory.getCurrentLocalDate ();
    final String s = PDTWebDateHelper.getAsStringXSD (aDT);
    assertNotNull (s);
    assertEquals (aDT, PDTWebDateHelper.getLocalDateFromXSD (s));
    assertNull (PDTWebDateHelper.getAsStringXSD ((LocalDate) null));
    assertNotNull (PDTWebDateHelper.getLocalDateFromXSD ("2005-06-20"));
  }

  @Test
  public void testXSDLocalTime ()
  {
    // Millis only for Java 11+
    final LocalTime aDT = PDTFactory.getCurrentLocalTimeMillisOnly ();
    final String s = PDTWebDateHelper.getAsStringXSD (aDT);
    assertNotNull (s);
    assertEquals (aDT, PDTWebDateHelper.getLocalTimeFromXSD (s));
    assertNull (PDTWebDateHelper.getAsStringXSD ((LocalTime) null));
    assertNotNull (PDTWebDateHelper.getLocalTimeFromXSD ("11:30:00.0Z"));
    assertNotNull (PDTWebDateHelper.getLocalTimeFromXSD ("09:15:23.123-05:00"));
    assertNotNull (PDTWebDateHelper.getLocalTimeFromXSD ("09:15:23.12-05:00"));
    assertNotNull (PDTWebDateHelper.getLocalTimeFromXSD ("09:15:23.1-05:00"));
    assertNotNull (PDTWebDateHelper.getLocalTimeFromXSD ("09:15:23.000-05:00"));
    assertNotNull (PDTWebDateHelper.getLocalTimeFromXSD ("09:15:23.00-05:00"));
    assertNotNull (PDTWebDateHelper.getLocalTimeFromXSD ("09:15:23.0-05:00"));
    assertNotNull (PDTWebDateHelper.getLocalTimeFromXSD ("09:15:23-05:00"));
  }

  @Test
  public void testXSDOffsetTime ()
  {
    // Millis only for Java 11+
    final OffsetTime aDT = PDTFactory.getCurrentOffsetTimeMillisOnly ();
    final String s = PDTWebDateHelper.getAsStringXSD (aDT);
    assertNotNull (s);
    assertEquals (aDT, PDTWebDateHelper.getOffsetTimeFromXSD (s));
    assertNull (PDTWebDateHelper.getAsStringXSD ((OffsetTime) null));
    assertNotNull (PDTWebDateHelper.getOffsetTimeFromXSD ("11:30:00.0Z"));
    assertNotNull (PDTWebDateHelper.getOffsetTimeFromXSD ("09:15:23.123-05:00"));
    assertNotNull (PDTWebDateHelper.getOffsetTimeFromXSD ("09:15:23.12-05:00"));
    assertNotNull (PDTWebDateHelper.getOffsetTimeFromXSD ("09:15:23.1-05:00"));
    assertNotNull (PDTWebDateHelper.getOffsetTimeFromXSD ("09:15:23.000-05:00"));
    assertNotNull (PDTWebDateHelper.getOffsetTimeFromXSD ("09:15:23.00-05:00"));
    assertNotNull (PDTWebDateHelper.getOffsetTimeFromXSD ("09:15:23.0-05:00"));
    assertNotNull (PDTWebDateHelper.getOffsetTimeFromXSD ("09:15:23-05:00"));
  }

  @Test
  public void testXSDXMLOffsetTime ()
  {
    // Millis only for Java 11+
    final XMLOffsetTime aDT = PDTFactory.getCurrentXMLOffsetTimeMillisOnly ();
    final String s = PDTWebDateHelper.getAsStringXSD (aDT);
    assertNotNull (s);
    assertEquals (aDT, PDTWebDateHelper.getXMLOffsetTimeFromXSD (s));
    assertNull (PDTWebDateHelper.getAsStringXSD ((XMLOffsetTime) null));
    assertNotNull (PDTWebDateHelper.getXMLOffsetTimeFromXSD ("11:30:00.0Z"));
    assertNotNull (PDTWebDateHelper.getXMLOffsetTimeFromXSD ("09:15:23.123-05:00"));
    assertNotNull (PDTWebDateHelper.getXMLOffsetTimeFromXSD ("09:15:23.12-05:00"));
    assertNotNull (PDTWebDateHelper.getXMLOffsetTimeFromXSD ("09:15:23.1-05:00"));
    assertNotNull (PDTWebDateHelper.getXMLOffsetTimeFromXSD ("09:15:23.000-05:00"));
    assertNotNull (PDTWebDateHelper.getXMLOffsetTimeFromXSD ("09:15:23.00-05:00"));
    assertNotNull (PDTWebDateHelper.getXMLOffsetTimeFromXSD ("09:15:23.0-05:00"));
    assertNotNull (PDTWebDateHelper.getXMLOffsetTimeFromXSD ("09:15:23-05:00"));
  }

  @Test
  public void testXSDLocalDateTime ()
  {
    LocalDateTime d = PDTFactory.createLocalDateTime (2011, Month.JULY, 6, 12, 34);
    String s = PDTWebDateHelper.getAsStringXSD (d);
    assertEquals ("2011-07-06T12:34:00", s);
    LocalDateTime d2 = PDTWebDateHelper.getLocalDateTimeFromXSD (s);
    assertEquals (d, d2);

    final ZonedDateTime z = d.atZone (ZoneOffset.ofHours (2));
    s = PDTWebDateHelper.getAsStringXSD (z);
    assertEquals ("2011-07-06T12:34:00+02:00", s);
    final ZonedDateTime z2 = PDTWebDateHelper.getZonedDateTimeFromXSD (s);
    assertEquals (z, z2);

    // Current time last
    // Millis only for Java 11+
    d = PDTFactory.getCurrentLocalDateTimeMillisOnly ();
    s = PDTWebDateHelper.getAsStringXSD (d);
    assertNotNull (s);
    d2 = PDTWebDateHelper.getLocalDateTimeFromXSD (s);
    assertEquals (d, d2);

    assertNotNull (PDTWebDateHelper.getLocalDateTimeFromXSD ("2020-09-18T16:40:34.078"));
    assertNotNull (PDTWebDateHelper.getLocalDateTimeFromXSD ("2020-09-18T16:40:34.07"));
    assertNotNull (PDTWebDateHelper.getLocalDateTimeFromXSD ("2020-09-18T16:40:34.0"));
    assertNotNull (PDTWebDateHelper.getLocalDateTimeFromXSD ("2020-09-18T16:40:34.178"));
    assertNotNull (PDTWebDateHelper.getLocalDateTimeFromXSD ("2020-09-18T16:40:34.17"));
    assertNotNull (PDTWebDateHelper.getLocalDateTimeFromXSD ("2020-09-18T16:40:34.1"));
    // With trailing Z
    assertNotNull (PDTWebDateHelper.getLocalDateTimeFromXSD ("2020-09-18T16:40:34.078Z"));
    assertNotNull (PDTWebDateHelper.getLocalDateTimeFromXSD ("2020-09-18T16:40:34.07Z"));
    assertNotNull (PDTWebDateHelper.getLocalDateTimeFromXSD ("2020-09-18T16:40:34.0Z"));
    assertNotNull (PDTWebDateHelper.getLocalDateTimeFromXSD ("2020-09-18T16:40:34.1Z"));
  }
}
