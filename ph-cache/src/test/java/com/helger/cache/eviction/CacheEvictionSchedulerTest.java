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
import com.helger.cache.impl.CacheBuilder;

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

    final var c = new CacheBuilder <String, String> ().name ("SchedRegister")
                                                      .expireAfterWrite (Duration.ofSeconds (10))
                                                      .buildManualCache ();
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
    final var c = new CacheBuilder <String, String> ().name ("SchedEvict")
                                                      .expireAfterWrite (Duration.ofMillis (50))
                                                      .clockSupplier (aNow::get)
                                                      .valueProvider (x -> x)
                                                      .buildProviderCache ();

    c.getFromCache ("a");
    c.getFromCache ("b");
    assertEquals (2, c.size ());

    final CacheEvictionScheduler aScheduler = CacheEvictionScheduler.getInstance ();
    aScheduler.register (c, Duration.ofMillis (50));
    try
    {
      // Advance the cache's clock so its entries are expired
      aNow.set (aNow.get ().plusSeconds (1));

      // Wait up to ~2s for the scheduler thread to run an eviction
      final long nDeadline = System.currentTimeMillis () + 2_000;
      while (c.size () > 0 && System.currentTimeMillis () < nDeadline)
        Thread.sleep (25);

      assertEquals ("Scheduler should have evicted both entries", 0, c.size ());
    }
    finally
    {
      aScheduler.unregister (c);
    }
  }

  @Test
  public void testReRegisterReplacesExisting ()
  {
    final CacheEvictionScheduler aScheduler = CacheEvictionScheduler.getInstance ();
    final var c = new CacheBuilder <String, String> ().name ("SchedReRegister")
                                                      .expireAfterWrite (Duration.ofSeconds (10))
                                                      .buildManualCache ();
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
    final var c = new CacheBuilder <String, String> ().name ("SchedUnknown")
                                                      .expireAfterWrite (Duration.ofSeconds (10))
                                                      .buildManualCache ();
    assertEquals (EChange.UNCHANGED, aScheduler.unregister (c));
  }

  @Test
  public void testShutdownClearsAll ()
  {
    final CacheEvictionScheduler aScheduler = CacheEvictionScheduler.getInstance ();
    final var c1 = new CacheBuilder <String, String> ().name ("SchedShutdown1")
                                                       .expireAfterWrite (Duration.ofSeconds (10))
                                                       .buildManualCache ();
    final var c2 = new CacheBuilder <String, String> ().name ("SchedShutdown2")
                                                       .expireAfterWrite (Duration.ofSeconds (10))
                                                       .buildManualCache ();
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
      new CacheBuilder <String, String> ().name ("MissingTTL")
                                          .evictionInterval (Duration.ofSeconds (60))
                                          .buildManualCache ();
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
    final var c = new CacheBuilder <String, String> ().name ("BuilderRegister")
                                                      .expireAfterWrite (Duration.ofSeconds (10))
                                                      .evictionInterval (Duration.ofSeconds (60))
                                                      .buildManualCache ();
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
