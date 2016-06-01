package com.helger.commons.datetime;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;

/**
 * Test class for {@link PDTFactory}.
 *
 * @author Philip Helger
 */
public final class PDTFactoryTest
{
  @Test
  public void testLocalDate ()
  {
    assertNotNull (PDTFactory.createLocalDate (new Date ()));
    assertNotNull (PDTFactory.createLocalDate (new GregorianCalendar (TimeZone.getDefault (), Locale.getDefault ())));
    assertNotNull (PDTFactory.createLocalDate (Instant.ofEpochMilli (1234)));
    assertNotNull (PDTFactory.createLocalDate (LocalDateTime.now (ZoneId.systemDefault ())));
    assertNotNull (PDTFactory.createLocalDate (1234));
    assertNotNull (PDTFactory.createLocalDate (Year.now (ZoneId.systemDefault ())));
    assertNotNull (PDTFactory.createLocalDate (YearMonth.now (ZoneId.systemDefault ())));
    assertNotNull (PDTFactory.createLocalDate (2016, Month.MARCH, 13));

    assertNull (PDTFactory.createLocalDate ((Date) null));
    assertNull (PDTFactory.createLocalDate ((GregorianCalendar) null));
    assertNull (PDTFactory.createLocalDate ((Instant) null));
    assertNull (PDTFactory.createLocalDate ((LocalDateTime) null));
    assertNull (PDTFactory.createLocalDate ((Year) null));
    assertNull (PDTFactory.createLocalDate ((YearMonth) null));
  }
}
