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

import java.time.Month;
import java.time.ZoneOffset;
import java.util.Locale;

import org.junit.Test;

import com.helger.base.system.EJavaVersion;

/**
 * Test class for class {@link PDTToString}.
 *
 * @author Philip Helger
 */
public final class PDTToStringTest
{
  @Test
  public void testGetAsStringDate ()
  {
    Locale aDisplayLocale = Locale.US;

    // US
    assertEquals ("Feb 3, 2021",
                  PDTToString.getAsString (PDTFactory.createLocalDate (2021, Month.FEBRUARY, 3), aDisplayLocale));

    assertEquals ("Feb 3, 2021 +0100",
                  PDTToString.getAsString (PDTFactory.createOffsetDate (2021,
                                                                        Month.FEBRUARY,
                                                                        3,
                                                                        ZoneOffset.ofHours (1)),
                                           aDisplayLocale));

    assertEquals ("Feb 3, 2021 +0100",
                  PDTToString.getAsString (PDTFactory.createXMLOffsetDate (2021,
                                                                           Month.FEBRUARY,
                                                                           3,
                                                                           ZoneOffset.ofHours (1)),
                                           aDisplayLocale));
    assertEquals ("Feb 3, 2021",
                  PDTToString.getAsString (PDTFactory.createXMLOffsetDate (2021, Month.FEBRUARY, 3), aDisplayLocale));

    aDisplayLocale = Locale.GERMANY;

    // Germany
    assertEquals ("03.02.2021",
                  PDTToString.getAsString (PDTFactory.createLocalDate (2021, Month.FEBRUARY, 3), aDisplayLocale));

    assertEquals ("03.02.2021 +0100",
                  PDTToString.getAsString (PDTFactory.createOffsetDate (2021,
                                                                        Month.FEBRUARY,
                                                                        3,
                                                                        ZoneOffset.ofHours (1)),
                                           aDisplayLocale));

    assertEquals ("03.02.2021 +0100",
                  PDTToString.getAsString (PDTFactory.createXMLOffsetDate (2021,
                                                                           Month.FEBRUARY,
                                                                           3,
                                                                           ZoneOffset.ofHours (1)),
                                           aDisplayLocale));
    assertEquals ("03.02.2021",
                  PDTToString.getAsString (PDTFactory.createXMLOffsetDate (2021, Month.FEBRUARY, 3), aDisplayLocale));
  }

  @Test
  public void testGetAsStringTime ()
  {
    // US
    Locale aDisplayLocale = Locale.US;
    final String sAmPmSep = EJavaVersion.getCurrentVersion ().isNewerOrEqualsThan (EJavaVersion.JDK_21) ? " " : " ";

    assertEquals ("10:45:07" + sAmPmSep + "AM",
                  PDTToString.getAsString (PDTFactory.createLocalTime (10, 45, 7), aDisplayLocale));
    assertEquals ("10:45:07" + sAmPmSep + "AM +0100",
                  PDTToString.getAsString (PDTFactory.createOffsetTime (10, 45, 7, ZoneOffset.ofHours (1)),
                                           aDisplayLocale));
    assertEquals ("10:45:07+01:00", PDTFactory.createOffsetTime (10, 45, 7, ZoneOffset.ofHours (1)).toString ());

    assertEquals ("10:45:07" + sAmPmSep + "AM +0100",
                  PDTToString.getAsString (PDTFactory.createXMLOffsetTime (10, 45, 7, ZoneOffset.ofHours (1)),
                                           aDisplayLocale));
    assertEquals ("10:45:07" + sAmPmSep + "AM",
                  PDTToString.getAsString (PDTFactory.createXMLOffsetTime (10, 45, 7), aDisplayLocale));

    // Germany
    aDisplayLocale = Locale.GERMANY;

    assertEquals ("10:45:07", PDTToString.getAsString (PDTFactory.createLocalTime (10, 45, 7), aDisplayLocale));
    assertEquals ("10:45:07 +0100",
                  PDTToString.getAsString (PDTFactory.createOffsetTime (10, 45, 7, ZoneOffset.ofHours (1)),
                                           aDisplayLocale));
    assertEquals ("10:45:07+01:00", PDTFactory.createOffsetTime (10, 45, 7, ZoneOffset.ofHours (1)).toString ());

    assertEquals ("10:45:07 +0100",
                  PDTToString.getAsString (PDTFactory.createXMLOffsetTime (10, 45, 7, ZoneOffset.ofHours (1)),
                                           aDisplayLocale));
    assertEquals ("10:45:07", PDTToString.getAsString (PDTFactory.createXMLOffsetTime (10, 45, 7), aDisplayLocale));
  }

  @Test
  public void testGetAsStringDateTime ()
  {
    final String sOld = PDTConfig.getDefaultZoneId ().getId ();
    try
    {
      PDTConfig.setDefaultDateTimeZoneID ("UTC");

      // US
      Locale aDisplayLocale = Locale.US;
      final String sComma = EJavaVersion.getCurrentVersion ().isNewerOrEqualsThan (EJavaVersion.JDK_9) ? "," : "";
      final String sAmPmSep = EJavaVersion.getCurrentVersion ().isNewerOrEqualsThan (EJavaVersion.JDK_21) ? " " : " ";

      assertEquals ("Feb 3, 2021" + sComma + " 10:45:07" + sAmPmSep + "AM",
                    PDTToString.getAsString (PDTFactory.createLocalDateTime (2021, Month.FEBRUARY, 3, 10, 45, 7),
                                             aDisplayLocale));
      assertEquals ("Feb 3, 2021" + sComma + " 10:45:07" + sAmPmSep + "AM UTC",
                    PDTToString.getAsString (PDTFactory.createZonedDateTime (2021, Month.FEBRUARY, 3, 10, 45, 7),
                                             aDisplayLocale));
      assertEquals ("Feb 3, 2021" + sComma + " 10:45:07" + sAmPmSep + "AM +0100",
                    PDTToString.getAsString (PDTFactory.createOffsetDateTime (2021,
                                                                              Month.FEBRUARY,
                                                                              3,
                                                                              10,
                                                                              45,
                                                                              7,
                                                                              ZoneOffset.ofHours (1)),
                                             aDisplayLocale));
      assertEquals ("Feb 3, 2021" + sComma + " 10:45:07" + sAmPmSep + "AM +0100",
                    PDTToString.getAsString (PDTFactory.createXMLOffsetDateTime (2021,
                                                                                 Month.FEBRUARY,
                                                                                 3,
                                                                                 10,
                                                                                 45,
                                                                                 7,
                                                                                 ZoneOffset.ofHours (1)),
                                             aDisplayLocale));
      assertEquals ("Feb 3, 2021" + sComma + " 10:45:07" + sAmPmSep + "AM",
                    PDTToString.getAsString (PDTFactory.createXMLOffsetDateTime (2021, Month.FEBRUARY, 3, 10, 45, 7),
                                             aDisplayLocale));

      // Germany
      aDisplayLocale = Locale.GERMANY;

      assertEquals ("03.02.2021, 10:45:07",
                    PDTToString.getAsString (PDTFactory.createLocalDateTime (2021, Month.FEBRUARY, 3, 10, 45, 7),
                                             aDisplayLocale));
      assertEquals ("03.02.2021, 10:45:07 UTC",
                    PDTToString.getAsString (PDTFactory.createZonedDateTime (2021, Month.FEBRUARY, 3, 10, 45, 7),
                                             aDisplayLocale));
      assertEquals ("03.02.2021, 10:45:07 +0100",
                    PDTToString.getAsString (PDTFactory.createOffsetDateTime (2021,
                                                                              Month.FEBRUARY,
                                                                              3,
                                                                              10,
                                                                              45,
                                                                              7,
                                                                              ZoneOffset.ofHours (1)),
                                             aDisplayLocale));
      assertEquals ("03.02.2021, 10:45:07 +0100",
                    PDTToString.getAsString (PDTFactory.createXMLOffsetDateTime (2021,
                                                                                 Month.FEBRUARY,
                                                                                 3,
                                                                                 10,
                                                                                 45,
                                                                                 7,
                                                                                 ZoneOffset.ofHours (1)),
                                             aDisplayLocale));
      assertEquals ("03.02.2021, 10:45:07",
                    PDTToString.getAsString (PDTFactory.createXMLOffsetDateTime (2021, Month.FEBRUARY, 3, 10, 45, 7),
                                             aDisplayLocale));
    }
    finally
    {
      PDTConfig.setDefaultDateTimeZoneID (sOld);
    }
  }
}
