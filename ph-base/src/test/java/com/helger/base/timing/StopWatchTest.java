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
package com.helger.base.timing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test class for class {@link StopWatch}.
 *
 * @author Philip Helger
 */
public final class StopWatchTest
{
  @Test
  public void testAll () throws InterruptedException
  {
    StopWatch sw = StopWatch.createdStopped ();
    assertFalse (sw.isStarted ());
    assertEquals (0, sw.getMillis ());
    assertEquals (0, sw.getNanos ());
    assertFalse (sw.stop ().isChanged ());
    assertTrue (sw.start ().isChanged ());
    assertFalse (sw.start ().isChanged ());
    assertTrue (sw.stop ().isChanged ());
    assertTrue (sw.getNanos () > 0);

    assertTrue (sw.start ().isChanged ());
    assertTrue (sw.isStarted ());
    Thread.sleep (1000);
    assertTrue (sw.stopAndGetMillis () > 0);
    assertFalse (sw.isStarted ());

    assertTrue (sw.start ().isChanged ());
    assertTrue (sw.isStarted ());
    Thread.sleep (10);
    assertTrue (sw.stopAndGetMillis () > 0);
    assertFalse (sw.isStarted ());
    assertTrue (sw.getNanos () > 0);
    assertTrue (sw.reset ().isChanged ());
    assertEquals (0, sw.getNanos ());
    assertFalse (sw.reset ().isChanged ());

    assertNotNull (sw.toString ());

    sw = StopWatch.createdStarted ();
    assertTrue (sw.isStarted ());
  }

  @Test
  public void testGetLapTime () throws InterruptedException
  {
    final StopWatch sw = StopWatch.createdStarted ();
    Thread.sleep (100);
    long n = sw.getLapDuration ().toMillis ();
    assertTrue ("Duration of " + n + " was too short", n >= 100);
    Thread.sleep (100);
    n = sw.stopAndGetDuration ().toMillis ();
    assertTrue (n >= 200);
  }
}
