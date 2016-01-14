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
package com.helger.commons.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link TimeValue}.
 *
 * @author Philip Helger
 */
public final class TimeValueTest
{
  @Test
  public void testAll ()
  {
    final TimeValue t = new TimeValue (TimeUnit.SECONDS, 5);
    assertEquals (TimeUnit.SECONDS, t.getTimeUnit ());
    assertEquals (5, t.getDuration ());
    assertEquals (0, t.getAsDays ());
    assertEquals (0, t.getAsHours ());
    assertEquals (0, t.getAsMinutes ());
    assertEquals (5, t.getAsSeconds ());
    assertEquals (5000, t.getAsMillis ());
    assertEquals (5000000, t.getAsMicros ());
    assertEquals (5000000000L, t.getAsNanos ());

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new TimeValue (TimeUnit.SECONDS, 5),
                                                                       new TimeValue (TimeUnit.SECONDS, 5));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new TimeValue (TimeUnit.SECONDS, 5),
                                                                           new TimeValue (TimeUnit.SECONDS, 4));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new TimeValue (TimeUnit.SECONDS, 5),
                                                                           new TimeValue (TimeUnit.NANOSECONDS, 5));
    try
    {
      new TimeValue (null, 5);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
