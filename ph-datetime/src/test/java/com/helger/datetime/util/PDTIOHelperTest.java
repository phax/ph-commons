/*
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
package com.helger.datetime.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.Test;

import com.helger.commons.datetime.PDTFromString;

/**
 * Test class for class {@link PDTIOHelper}.
 *
 * @author Philip Helger
 */
public final class PDTIOHelperTest
{
  @Test
  public void testAll ()
  {
    assertNotNull (PDTIOHelper.getCurrentLocalDateTimeForFilename ());
    assertNotNull (PDTIOHelper.getCurrentDateForFilename ());
    assertNotNull (PDTIOHelper.getCurrentTimeForFilename ());

    final LocalDateTime aLDT = LocalDateTime.of (2005, 10, 12, 3, 45, 12);
    assertEquals ("20051012_034512", PDTIOHelper.getLocalDateTimeForFilename (aLDT));
    assertEquals (aLDT,
                  PDTFromString.getLocalDateTimeFromString (PDTIOHelper.getLocalDateTimeForFilename (aLDT), PDTIOHelper.PATTERN_DATETIME));

    final LocalDate aLD = LocalDate.of (2005, 10, 12);
    assertEquals ("20051012", PDTIOHelper.getDateForFilename (aLD));
    assertEquals (aLD, PDTFromString.getLocalDateFromString (PDTIOHelper.getDateForFilename (aLD), PDTIOHelper.PATTERN_DATE));

    final LocalTime aLT = LocalTime.of (3, 45, 12);
    assertEquals ("034512", PDTIOHelper.getTimeForFilename (aLT));
    assertEquals (aLT, PDTFromString.getLocalTimeFromString (PDTIOHelper.getTimeForFilename (aLT), PDTIOHelper.PATTERN_TIME));
  }
}
