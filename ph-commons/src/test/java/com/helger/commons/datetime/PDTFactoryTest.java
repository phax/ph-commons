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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
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
    assertNotNull (PDTFactory.createLocalDate (PDTFactory.getCurrentLocalDateTime ()));
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
}
