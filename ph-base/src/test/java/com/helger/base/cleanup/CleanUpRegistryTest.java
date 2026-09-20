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
package com.helger.base.cleanup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Test;

/**
 * Test class for class {@link CleanUpRegistry} and {@link BaseCleanUpRegistrarSPI}.
 *
 * @author Philip Helger
 */
public final class CleanUpRegistryTest
{
  @After
  public void restoreRegistry ()
  {
    // Drop anything this test registered
    CleanUpRegistry.getInstance ().reinitialize ();
  }

  @Test
  public void testGetInstance ()
  {
    final CleanUpRegistry aRegistry = CleanUpRegistry.getInstance ();
    assertNotNull (aRegistry);
    assertTrue (CleanUpRegistry.isInstantiated ());
    // Always the same instance
    assertSame (aRegistry, CleanUpRegistry.getInstance ());
  }

  @Test
  public void testRegisterAndPerform ()
  {
    final CleanUpRegistry aRegistry = CleanUpRegistry.getInstance ();
    final List <String> aCalls = new ArrayList <> ();

    // The actions are sorted ascending by priority, so the lowest runs first
    aRegistry.registerCleanup (ICleanUpRegistry.PRIORITY_MAX, () -> aCalls.add ("max"));
    aRegistry.registerCleanup (ICleanUpRegistry.PRIORITY_MIN, () -> aCalls.add ("min"));

    aRegistry.performCleanUp ();

    assertTrue (aCalls.contains ("max"));
    assertTrue (aCalls.contains ("min"));
    assertTrue (aCalls.indexOf ("min") < aCalls.indexOf ("max"));
  }

  @Test
  public void testReinitializeDropsOwnActions ()
  {
    final CleanUpRegistry aRegistry = CleanUpRegistry.getInstance ();
    final List <String> aCalls = new ArrayList <> ();
    aRegistry.registerCleanup (ICleanUpRegistry.PRIORITY_MAX, () -> aCalls.add ("own"));

    aRegistry.reinitialize ();
    aRegistry.performCleanUp ();
    assertEquals (0, aCalls.size ());
  }

  @Test
  public void testBaseCleanUpRegistrarSPI ()
  {
    // The SPI registers its action on the passed registry
    final List <String> aRegistered = new ArrayList <> ();
    new BaseCleanUpRegistrarSPI ().registerCleanUpAction ((nPriority, aRunnable) -> {
      aRegistered.add ("prio=" + nPriority);
      // Run it, so that the contained actions are executed as well
      aRunnable.run ();
    });
    assertEquals (1, aRegistered.size ());
  }

  @Test
  public void testInvalidParams ()
  {
    try
    {
      CleanUpRegistry.getInstance ().registerCleanup (0, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {
      // expected
    }
  }
}
