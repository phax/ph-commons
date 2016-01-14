/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.time.Month;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;
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
    final LocalDate aDate = LocalDate.now ();
    final Version aVersion = new Version (1, 2, 3);

    final ChangeLogRelease aRelease = new ChangeLogRelease (aDate, aVersion);
    assertEquals (aDate, aRelease.getDate ());
    assertSame (aDate, aRelease.getDate ());
    assertSame (aVersion, aRelease.getVersion ());

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (aRelease,
                                                                       new ChangeLogRelease (aDate, aVersion));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (aRelease,
                                                                           new ChangeLogRelease (LocalDate.of (2010,
                                                                                                               Month.JULY,
                                                                                                               6),
                                                                                                 aVersion));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (aRelease,
                                                                           new ChangeLogRelease (aDate,
                                                                                                 new Version (1,
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
