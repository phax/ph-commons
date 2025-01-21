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
package com.helger.commons.datetime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;

import org.junit.Test;

/**
 * Test class for class {@link PDTConfig}.
 *
 * @author Philip Helger
 */
public final class PDTConfigTest
{
  @Test
  public void testTimeZones ()
  {
    final ZoneId aDTZ = PDTConfig.getDefaultZoneId ();
    assertNotNull (aDTZ);

    assertNotNull (PDTConfig.getUTCZoneId ());
    assertEquals ("Z", PDTConfig.getUTCZoneId ().getId ());

    assertNotNull (PDTConfig.getUTCTimeZone ());
    assertEquals (0, PDTConfig.getUTCTimeZone ().getRawOffset ());

    try
    {
      // Invalid
      assertFalse (PDTConfig.setDefaultDateTimeZoneID ("does not exist").isSuccess ());

      // Regular
      assertTrue (PDTConfig.setDefaultDateTimeZoneID ("Europe/Berlin").isSuccess ());
      assertEquals ("Europe/Berlin", PDTConfig.getDefaultZoneId ().getId ());

      // I hope this is not the standard time zone anywhere
      assertTrue (PDTConfig.setDefaultDateTimeZoneID ("UTC").isSuccess ());
      assertEquals ("UTC", PDTConfig.getDefaultZoneId ().getId ());
    }
    finally
    {
      assertTrue (PDTConfig.setDefaultDateTimeZoneID (aDTZ.getId ()).isSuccess ());
    }
  }

  @Test
  public void testYearThan9999 ()
  {
    final LocalDate aLD = LocalDate.parse ("+22021-05-10");
    assertNotNull (aLD);
    assertEquals (PDTFactory.createLocalDate (22021, Month.MAY, 10), aLD);
    assertEquals ("+22021-05-10", aLD.toString ());

    // Fails without the "+" or "-" prefix
    try
    {
      LocalDate.parse ("22021-05-10");
      fail ();
    }
    catch (final DateTimeParseException ex)
    {
      // expected
    }
  }
}
