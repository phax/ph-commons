package com.helger.commons.datetime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.time.LocalTime;
import java.time.ZoneOffset;

import org.junit.Test;

/**
 * Test class for class {@link XMLOffsetTime}
 *
 * @author Philip Helger
 */
public final class XMLOffsetTimeTest
{
  @Test
  public void testBasic ()
  {
    final LocalTime aLT = PDTFactory.createLocalTime (19, 57, 12);

    // No timezone
    XMLOffsetTime aDT = XMLOffsetTime.of (aLT, null);
    assertEquals ("19:57:12", aDT.getAsString ());
    assertNull (aDT.getOffset ());
    assertFalse (aDT.hasOffset ());
    assertEquals (aLT, aDT.toLocalTime ());

    assertEquals (0, aDT.compareTo (aDT));
    assertEquals (-1, aDT.compareTo (aDT.plusSeconds (1)));
    assertEquals (+1, aDT.compareTo (aDT.minusSeconds (1)));
    assertEquals (0, aDT.compareTo (aDT.withOffsetSameInstant (ZoneOffset.UTC)));
    assertEquals (-1, aDT.compareTo (aDT.withOffsetSameInstant (ZoneOffset.ofHours (2))));
    assertEquals (+1, aDT.compareTo (aDT.withOffsetSameInstant (ZoneOffset.ofHours (-2))));

    // UTC
    aDT = XMLOffsetTime.of (aLT, ZoneOffset.UTC);
    assertEquals ("19:57:12Z", aDT.getAsString ());
    assertSame (ZoneOffset.UTC, aDT.getOffset ());
    assertTrue (aDT.hasOffset ());
    assertEquals (aLT, aDT.toLocalTime ());

    assertEquals (0, aDT.compareTo (aDT));
    assertEquals (-1, aDT.compareTo (aDT.plusSeconds (1)));
    assertEquals (+1, aDT.compareTo (aDT.minusSeconds (1)));
    assertEquals (0, aDT.compareTo (aDT.withOffsetSameInstant (null)));
    assertEquals (-1, aDT.compareTo (aDT.withOffsetSameInstant (ZoneOffset.ofHours (2))));
    assertEquals (+1, aDT.compareTo (aDT.withOffsetSameInstant (ZoneOffset.ofHours (-2))));

    // +2 hours
    final ZoneOffset aZO = ZoneOffset.ofHours (2);
    aDT = XMLOffsetTime.of (aLT, aZO);
    assertEquals ("19:57:12+02:00", aDT.getAsString ());
    assertEquals (aZO, aDT.getOffset ());
    assertEquals (aLT, aDT.toLocalTime ());

    assertEquals (0, aDT.compareTo (aDT));
    assertEquals (-1, aDT.compareTo (aDT.plusSeconds (1)));
    assertEquals (+1, aDT.compareTo (aDT.minusSeconds (1)));
    assertEquals (+1, aDT.compareTo (aDT.withOffsetSameInstant (null)));
    assertEquals (0, aDT.compareTo (aDT.withOffsetSameInstant (ZoneOffset.ofHours (2))));
    assertEquals (+1, aDT.compareTo (aDT.withOffsetSameInstant (ZoneOffset.ofHours (-2))));
  }
}
