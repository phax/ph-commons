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
package com.helger.commons.statistics;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link StatisticsManager}.
 *
 * @author Philip Helger
 */
public final class StatisticsManagerTest
{
  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testCacheHandler ()
  {
    assertNotNull (StatisticsManager.getCacheHandler (StatisticsManagerTest.class));
    assertNotNull (StatisticsManager.getCacheHandler (StatisticsManagerTest.class));
    assertFalse (StatisticsManager.getAllCacheHandler ().isEmpty ());
    try
    {
      StatisticsManager.getCacheHandler ((Class <?>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StatisticsManager.getCacheHandler ((String) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StatisticsManager.getCacheHandler ("");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testTimerHandler ()
  {
    assertNotNull (StatisticsManager.getTimerHandler (StatisticsManagerTest.class));
    assertNotNull (StatisticsManager.getTimerHandler (StatisticsManagerTest.class));
    assertFalse (StatisticsManager.getAllTimerHandler ().isEmpty ());
    try
    {
      StatisticsManager.getTimerHandler ((Class <?>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StatisticsManager.getTimerHandler ((String) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StatisticsManager.getTimerHandler ("");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testKeyedTimerHandler ()
  {
    assertNotNull (StatisticsManager.getKeyedTimerHandler (StatisticsManagerTest.class));
    assertNotNull (StatisticsManager.getKeyedTimerHandler (StatisticsManagerTest.class));
    assertFalse (StatisticsManager.getAllKeyedTimerHandler ().isEmpty ());
    try
    {
      StatisticsManager.getKeyedTimerHandler ((Class <?>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StatisticsManager.getKeyedTimerHandler ((String) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StatisticsManager.getKeyedTimerHandler ("");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testSizeHandler ()
  {
    assertNotNull (StatisticsManager.getSizeHandler (StatisticsManagerTest.class));
    assertNotNull (StatisticsManager.getSizeHandler (StatisticsManagerTest.class));
    assertFalse (StatisticsManager.getAllSizeHandler ().isEmpty ());
    try
    {
      StatisticsManager.getSizeHandler ((Class <?>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StatisticsManager.getSizeHandler ((String) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StatisticsManager.getSizeHandler ("");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testKeyedSizeHandler ()
  {
    assertNotNull (StatisticsManager.getKeyedSizeHandler (StatisticsManagerTest.class));
    assertNotNull (StatisticsManager.getKeyedSizeHandler (StatisticsManagerTest.class));
    assertFalse (StatisticsManager.getAllKeyedSizeHandler ().isEmpty ());
    try
    {
      StatisticsManager.getKeyedSizeHandler ((Class <?>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StatisticsManager.getKeyedSizeHandler ((String) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StatisticsManager.getKeyedSizeHandler ("");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testCounterHandler ()
  {
    assertNotNull (StatisticsManager.getCounterHandler (StatisticsManagerTest.class));
    assertNotNull (StatisticsManager.getCounterHandler (StatisticsManagerTest.class));
    assertFalse (StatisticsManager.getAllCounterHandler ().isEmpty ());
    try
    {
      StatisticsManager.getCounterHandler ((Class <?>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StatisticsManager.getCounterHandler ((String) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StatisticsManager.getCounterHandler ("");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testKeyedCounterHandler ()
  {
    assertNotNull (StatisticsManager.getKeyedCounterHandler (StatisticsManagerTest.class));
    assertNotNull (StatisticsManager.getKeyedCounterHandler (StatisticsManagerTest.class));
    assertFalse (StatisticsManager.getAllKeyedCounterHandler ().isEmpty ());
    try
    {
      StatisticsManager.getKeyedCounterHandler ((Class <?>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StatisticsManager.getKeyedCounterHandler ((String) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StatisticsManager.getKeyedCounterHandler ("");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }
}
