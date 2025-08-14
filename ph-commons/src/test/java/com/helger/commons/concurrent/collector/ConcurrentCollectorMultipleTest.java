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
package com.helger.commons.concurrent.collector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

import com.helger.base.state.ESuccess;

import jakarta.annotation.Nonnull;

/**
 * Test class for class {@link ConcurrentCollectorMultiple}.
 *
 * @author Philip Helger
 */
public final class ConcurrentCollectorMultipleTest
{
  @Test
  public void testCtor ()
  {
    try
    {
      new ConcurrentCollectorMultiple <String> (-1, 3);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      new ConcurrentCollectorMultiple <String> (50, -1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      new ConcurrentCollectorMultiple <String> (5, 6);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    ConcurrentCollectorMultiple <String> ccm = new ConcurrentCollectorMultiple <> (5, 5);
    assertEquals (0, ccm.getQueueLength ());

    try
    {
      // null not allowed
      ccm.setPerformer (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // no performer
      ccm.collect ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {}

    ccm = new ConcurrentCollectorMultiple <> (5, 5);
    ccm.setPerformer (aCurrentObject -> {});
    assertNotNull (ccm);
  }

  private void _test (@Nonnull final MockConcurrentCollectorMultiple aQueue) throws InterruptedException
  {
    final int nThreads = 10 + ThreadLocalRandom.current ().nextInt (20);
    final int nPerThreadQueueAdd = 10_000 + ThreadLocalRandom.current ().nextInt (10_000);

    final Thread aQueueThread = new Thread (aQueue::collect, "ph-MockConcurrentQueue");
    aQueueThread.start ();
    assertEquals (0, aQueue.getPerformCount ());
    // create and run producers
    {
      final Thread [] aThreads = new Thread [nThreads];
      for (int i = 0; i < nThreads; ++i)
        aThreads[i] = new Thread ("Thread" + i)
        {
          @Override
          public void run ()
          {
            for (int j = 0; j < nPerThreadQueueAdd; ++j)
              aQueue.queueObject (j + " " + toString ());
          }
        };

      for (int i = 0; i < nThreads; ++i)
        aThreads[i].start ();
      for (int i = 0; i < nThreads; ++i)
        aThreads[i].join ();
    }

    try
    {
      // null object
      aQueue.queueObject (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    assertSame (ESuccess.SUCCESS, aQueue.stopQueuingNewObjects ());

    try
    {
      // queue already stopped
      aQueue.queueObject ("abc");
      fail ();
    }
    catch (final IllegalStateException ex)
    {}

    // Stop again will fail
    assertSame (ESuccess.FAILURE, aQueue.stopQueuingNewObjects ());

    aQueueThread.join ();
    assertEquals (0, aQueue.getQueueLength ());
    assertEquals (nThreads * nPerThreadQueueAdd, aQueue.getPerformCount ());
  }

  @Test
  public void testAny () throws InterruptedException
  {
    _test (new MockConcurrentCollectorMultiple ());
    _test (new MockConcurrentCollectorMultiple (new ArrayBlockingQueue <> (10)));
    _test (new MockConcurrentCollectorMultiple (new LinkedBlockingQueue <> (10)));
    if (false)
    {
      // Does not work with the StopObject
      _test (new MockConcurrentCollectorMultiple (new PriorityBlockingQueue <> (10)));
    }
    _test (new MockConcurrentCollectorMultiple (new SynchronousQueue <> ()));
  }
}
