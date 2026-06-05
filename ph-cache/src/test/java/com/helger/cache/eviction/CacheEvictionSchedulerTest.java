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
package com.helger.cache.eviction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.Test;

import com.helger.base.state.EChange;
import com.helger.cache.impl.ManualCache;
import com.helger.cache.impl.ProviderCache;

/**
 * Test class for {@link CacheEvictionScheduler}.
 *
 * @author Philip Helger
 */
public final class CacheEvictionSchedulerTest
{
  @After
  public void tearDown ()
  {
    // Make sure subsequent tests start with a clean scheduler
    CacheEvictionScheduler.getInstance ().shutdown ();
  }

  @Test
  public void testRegisterStartsExecutor ()
  {
    final CacheEvictionScheduler aScheduler = CacheEvictionScheduler.getInstance ();
    assertFalse (aScheduler.isRunning ());

    final var c = ManualCache.builder ().name ("SchedRegister").expireAfterWrite (Duration.ofSeconds (10)).build ();
    aScheduler.register (c, Duration.ofSeconds (60));
    try
    {
      assertTrue (aScheduler.isRunning ());
      assertEquals (1, aScheduler.getRegistrationCount ());
    }
    finally
    {
      aScheduler.unregister (c);
    }
    assertFalse (aScheduler.isRunning ());
    assertEquals (0, aScheduler.getRegistrationCount ());
  }

  @Test
  public void testBackgroundEvictionRemovesEntries () throws InterruptedException
  {
    final AtomicReference <LocalDateTime> aNow = new AtomicReference <> (LocalDateTime.of (2026, 1, 1, 12, 0));
    final var c = ProviderCache.builder ()
                               .name ("SchedEvict")
                               .expireAfterWrite (Duration.ofMillis (50))
                               .clockSupplier (aNow::get)
                               .valueProvider (x -> x)
                               .build ();

    c.getFromCache ("a");
    c.getFromCache ("b");
    assertEquals (2, c.size ());

    final CacheEvictionScheduler aScheduler = CacheEvictionScheduler.getInstance ();
    aScheduler.register (c, CacheEvictionScheduler.MIN_EVICTION_INTERVAL);
    try
    {
      // Advance the cache's clock so its entries are expired
      aNow.set (aNow.get ().plusSeconds (1));

      // Wait up to ~4s for the scheduler thread (which fires at the min eviction interval) to run
      final long nDeadline = System.currentTimeMillis () + 4_000;
      while (c.size () > 0 && System.currentTimeMillis () < nDeadline)
        Thread.sleep (50);

      assertEquals ("Scheduler should have evicted both entries", 0, c.size ());
    }
    finally
    {
      aScheduler.unregister (c);
    }
  }

  @Test
  public void testRegisterRejectsSubMinimumInterval ()
  {
    final CacheEvictionScheduler aScheduler = CacheEvictionScheduler.getInstance ();
    final var c = ManualCache.builder ().name ("SchedTooFast").expireAfterWrite (Duration.ofSeconds (10)).build ();
    try
    {
      aScheduler.register (c, Duration.ofMillis (500));
      fail ("Sub-minimum interval should have been rejected");
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
    assertEquals (0, aScheduler.getRegistrationCount ());
  }

  @Test
  public void testReRegisterReplacesExisting ()
  {
    final CacheEvictionScheduler aScheduler = CacheEvictionScheduler.getInstance ();
    final var c = ManualCache.builder ().name ("SchedReRegister").expireAfterWrite (Duration.ofSeconds (10)).build ();
    aScheduler.register (c, Duration.ofSeconds (60));
    aScheduler.register (c, Duration.ofSeconds (120));
    try
    {
      assertEquals (1, aScheduler.getRegistrationCount ());
    }
    finally
    {
      aScheduler.unregister (c);
    }
  }

  @Test
  public void testUnregisterUnknownReturnsUnchanged ()
  {
    final CacheEvictionScheduler aScheduler = CacheEvictionScheduler.getInstance ();
    final var c = ManualCache.builder ().name ("SchedUnknown").expireAfterWrite (Duration.ofSeconds (10)).build ();
    assertEquals (EChange.UNCHANGED, aScheduler.unregister (c));
  }

  @Test
  public void testShutdownClearsAll ()
  {
    final CacheEvictionScheduler aScheduler = CacheEvictionScheduler.getInstance ();
    final var c1 = ManualCache.builder ().name ("SchedShutdown1").expireAfterWrite (Duration.ofSeconds (10)).build ();
    final var c2 = ManualCache.builder ().name ("SchedShutdown2").expireAfterWrite (Duration.ofSeconds (10)).build ();
    aScheduler.register (c1, Duration.ofSeconds (60));
    aScheduler.register (c2, Duration.ofSeconds (60));
    assertEquals (2, aScheduler.getRegistrationCount ());
    assertTrue (aScheduler.isRunning ());

    aScheduler.shutdown ();
    assertEquals (0, aScheduler.getRegistrationCount ());
    assertFalse (aScheduler.isRunning ());

    // Re-registering after shutdown restarts the executor
    aScheduler.register (c1, Duration.ofSeconds (60));
    try
    {
      assertTrue (aScheduler.isRunning ());
    }
    finally
    {
      aScheduler.unregister (c1);
    }
  }

  @Test
  public void testBuilderEvictionIntervalRequiresTTL ()
  {
    try
    {
      ManualCache.builder ().name ("MissingTTL").evictionInterval (Duration.ofSeconds (60)).build ();
      fail ("Expected IllegalStateException because TTL is missing");
    }
    catch (final IllegalStateException ex)
    {
      assertNotNull (ex.getMessage ());
    }
  }

  @Test
  public void testBuilderEvictionIntervalRegisters ()
  {
    final CacheEvictionScheduler aScheduler = CacheEvictionScheduler.getInstance ();
    final var c = ManualCache.builder ()
                             .name ("BuilderRegister")
                             .expireAfterWrite (Duration.ofSeconds (10))
                             .evictionInterval (Duration.ofSeconds (60))
                             .build ();
    try
    {
      assertEquals (1, aScheduler.getRegistrationCount ());
      assertTrue (aScheduler.isRunning ());
    }
    finally
    {
      aScheduler.unregister (c);
    }
  }
}
