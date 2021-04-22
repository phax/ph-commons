package com.helger.commons.datetime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;

import org.junit.Test;

/**
 * Test class for class {@link XMLOffsetDateTime}
 *
 * @author Philip Helger
 */
public final class XMLOffsetDateTimeTest
{
  @Test
  public void testBasic ()
  {
    final LocalDate aLD = PDTFactory.createLocalDate (2020, Month.JANUARY, 2);
    final LocalTime aLT = PDTFactory.createLocalTime (19, 57, 12);

    // No timezone
    XMLOffsetDateTime aDT = XMLOffsetDateTime.of (aLD, aLT, null);
    assertEquals ("2020-01-02T19:57:12", aDT.getAsString ());
    assertNull (aDT.getOffset ());
    assertEquals (aLD, aDT.toLocalDate ());
    assertEquals (aLT, aDT.toLocalTime ());
    assertEquals (aLD.atTime (aLT), aDT.toLocalDateTime ());

    assertEquals (0, aDT.compareTo (aDT));
    assertEquals (-1, aDT.compareTo (aDT.plusSeconds (1)));
    assertEquals (+1, aDT.compareTo (aDT.minusSeconds (1)));
    assertEquals (0, aDT.compareTo (aDT.withOffsetSameInstant (ZoneOffset.UTC)));
    assertEquals (-1, aDT.compareTo (aDT.withOffsetSameInstant (ZoneOffset.ofHours (2))));
    assertEquals (+1, aDT.compareTo (aDT.withOffsetSameInstant (ZoneOffset.ofHours (-2))));

    // UTC
    aDT = XMLOffsetDateTime.of (aLD, aLT, ZoneOffset.UTC);
    assertEquals ("2020-01-02T19:57:12Z", aDT.getAsString ());
    assertSame (ZoneOffset.UTC, aDT.getOffset ());
    assertEquals (aLD, aDT.toLocalDate ());
    assertEquals (aLT, aDT.toLocalTime ());
    assertEquals (aLD.atTime (aLT), aDT.toLocalDateTime ());

    assertEquals (0, aDT.compareTo (aDT));
    assertEquals (-1, aDT.compareTo (aDT.plusSeconds (1)));
    assertEquals (+1, aDT.compareTo (aDT.minusSeconds (1)));
    assertEquals (0, aDT.compareTo (aDT.withOffsetSameInstant (null)));
    assertEquals (-1, aDT.compareTo (aDT.withOffsetSameInstant (ZoneOffset.ofHours (2))));
    assertEquals (+1, aDT.compareTo (aDT.withOffsetSameInstant (ZoneOffset.ofHours (-2))));

    // +2 hours
    final ZoneOffset aZO = ZoneOffset.ofHours (2);
    aDT = XMLOffsetDateTime.of (aLD, aLT, aZO);
    assertEquals ("2020-01-02T19:57:12+02:00", aDT.getAsString ());
    assertEquals (aZO, aDT.getOffset ());
    assertEquals (aLD, aDT.toLocalDate ());
    assertEquals (aLT, aDT.toLocalTime ());
    assertEquals (aLD.atTime (aLT), aDT.toLocalDateTime ());

    assertEquals (0, aDT.compareTo (aDT));
    assertEquals (-1, aDT.compareTo (aDT.plusSeconds (1)));
    assertEquals (+1, aDT.compareTo (aDT.minusSeconds (1)));
    assertEquals (+1, aDT.compareTo (aDT.withOffsetSameInstant (null)));
    assertEquals (0, aDT.compareTo (aDT.withOffsetSameInstant (ZoneOffset.ofHours (2))));
    assertEquals (+1, aDT.compareTo (aDT.withOffsetSameInstant (ZoneOffset.ofHours (-2))));
  }
}
