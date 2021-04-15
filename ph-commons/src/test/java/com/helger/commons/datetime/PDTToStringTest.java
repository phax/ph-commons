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

import java.time.Month;
import java.time.ZoneOffset;
import java.util.Locale;

import org.junit.Test;

import com.helger.commons.system.EJavaVersion;

/**
 * Test class for class {@link PDTToString}.
 *
 * @author Philip Helger
 */
public final class PDTToStringTest
{
  @Test
  public void testToString ()
  {
    final Locale aDisplayLocale = Locale.US;
    assertEquals ("Feb 3, 2021", PDTToString.getAsString (PDTFactory.createLocalDate (2021, Month.FEBRUARY, 3), aDisplayLocale));
    assertEquals ("10:45:07 AM", PDTToString.getAsString (PDTFactory.createLocalTime (10, 45, 7), aDisplayLocale));
    assertEquals ("10:45:07 AM", PDTToString.getAsString (PDTFactory.createOffsetTime (10, 45, 7, ZoneOffset.ofHours (1)), aDisplayLocale));
    assertEquals ("10:45:07+01:00", PDTFactory.createOffsetTime (10, 45, 7, ZoneOffset.ofHours (1)).toString ());
    final String sComma = EJavaVersion.JDK_9.isCurrentVersion () ? "," : "";
    assertEquals ("Feb 3, 2021" + sComma + " 10:45:07 AM",
                  PDTToString.getAsString (PDTFactory.createLocalDateTime (2021, Month.FEBRUARY, 3, 10, 45, 7), aDisplayLocale));
    assertEquals ("Feb 3, 2021" + sComma + " 10:45:07 AM",
                  PDTToString.getAsString (PDTFactory.createZonedDateTime (2021, Month.FEBRUARY, 3, 10, 45, 7), aDisplayLocale));
    assertEquals ("Feb 3, 2021" + sComma + " 10:45:07 AM",
                  PDTToString.getAsString (PDTFactory.createLocalDateTime (2021, Month.FEBRUARY, 3, 10, 45, 7)
                                                     .atOffset (ZoneOffset.ofHours (1)),
                                           aDisplayLocale));
  }
}
