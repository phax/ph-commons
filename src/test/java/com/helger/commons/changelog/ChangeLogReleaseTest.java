/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.changelog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;

import com.helger.commons.mock.PHTestUtils;
import com.helger.commons.version.Version;

/**
 * Test class for class {@link ChangeLogRelease}.
 *
 * @author Philip Helger
 */
public final class ChangeLogReleaseTest
{
  @Test
  public void testAll ()
  {
    final Date aDate = new Date ();
    final Version aVersion = new Version (1, 2, 3);

    final ChangeLogRelease aRelease = new ChangeLogRelease (aDate, aVersion);
    assertEquals (aDate, aRelease.getDate ());
    assertTrue (aDate != aRelease.getDate ());
    assertSame (aVersion, aRelease.getVersion ());

    PHTestUtils.testDefaultImplementationWithEqualContentObject (aRelease, new ChangeLogRelease (aDate, aVersion));
    PHTestUtils.testDefaultImplementationWithDifferentContentObject (aRelease,
                                                                        new ChangeLogRelease (new GregorianCalendar (2010,
                                                                                                                     Calendar.JULY,
                                                                                                                     6).getTime (),
                                                                                              aVersion));
    PHTestUtils.testDefaultImplementationWithDifferentContentObject (aRelease,
                                                                        new ChangeLogRelease (aDate, new Version (1,
                                                                                                                  2,
                                                                                                                  4)));

    try
    {
      // null date
      new ChangeLogRelease (null, aVersion);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // null version
      new ChangeLogRelease (aDate, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
