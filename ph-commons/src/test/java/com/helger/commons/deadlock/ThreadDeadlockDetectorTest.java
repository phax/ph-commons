/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.commons.deadlock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;

import org.junit.Test;

/**
 * Test class for the classes of the deadlock package.
 *
 * @author Philip Helger
 */
public final class ThreadDeadlockDetectorTest
{
  private static ThreadDeadlockInfo _createInfo (final StackTraceElement [] aStackTrace)
  {
    final Thread aThread = Thread.currentThread ();
    final ThreadInfo aThreadInfo = ManagementFactory.getThreadMXBean ().getThreadInfo (aThread.getId ());
    return new ThreadDeadlockInfo (aThreadInfo, aThread, aStackTrace);
  }

  @Test
  public void testThreadDeadlockInfo ()
  {
    final StackTraceElement [] aStackTrace = Thread.currentThread ().getStackTrace ();
    final ThreadDeadlockInfo aTDI = _createInfo (aStackTrace);
    assertNotNull (aTDI.getThreadInfo ());
    assertSame (Thread.currentThread (), aTDI.getThread ());
    assertTrue (aTDI.hasStackTrace ());
    assertEquals (aStackTrace.length, aTDI.getAllStackTraceElements ().length);
    assertNotNull (aTDI.toString ());

    // Without a stack trace
    final ThreadDeadlockInfo aTDI2 = _createInfo (null);
    assertFalse (aTDI2.hasStackTrace ());
    assertNull (aTDI2.getAllStackTraceElements ());
    assertNotNull (aTDI2.toString ());
  }

  @Test
  public void testLoggingCallback ()
  {
    // Just ensure that nothing goes wrong
    new LoggingThreadDeadlockCallback ().onDeadlockDetected (new ThreadDeadlockInfo [] { _createInfo (Thread.currentThread ()
                                                                                                            .getStackTrace ()) });
    new LoggingThreadDeadlockCallback ().onDeadlockDetected (new ThreadDeadlockInfo [0]);
  }

  @Test
  public void testDetector ()
  {
    final ThreadDeadlockDetector aTDD = new ThreadDeadlockDetector ();
    assertNotNull (aTDD.callbacks ());
    assertTrue (aTDD.callbacks ().isEmpty ());

    // No deadlock is expected in this JVM
    aTDD.findDeadlockedThreads ();

    aTDD.callbacks ().add (new LoggingThreadDeadlockCallback ());
    assertEquals (1, aTDD.callbacks ().size ());
    aTDD.findDeadlockedThreads ();
  }

  @Test
  public void testDetectionTimer ()
  {
    final ThreadDeadlockDetectionTimer aTDDT = new ThreadDeadlockDetectionTimer ();
    assertTrue (aTDDT.stop ().isChanged ());
    // Stopping twice changes nothing
    assertFalse (aTDDT.stop ().isChanged ());
  }
}
