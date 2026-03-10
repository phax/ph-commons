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
package com.helger.statistics.impl;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.concurrent.SimpleReadWriteLock;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.ICommonsMap;
import com.helger.collection.commons.ICommonsSet;
import com.helger.statistics.api.IMutableStatisticsHandlerCache;
import com.helger.statistics.api.IMutableStatisticsHandlerCounter;
import com.helger.statistics.api.IMutableStatisticsHandlerKeyedCounter;
import com.helger.statistics.api.IMutableStatisticsHandlerKeyedSize;
import com.helger.statistics.api.IMutableStatisticsHandlerKeyedTimer;
import com.helger.statistics.api.IMutableStatisticsHandlerSize;
import com.helger.statistics.api.IMutableStatisticsHandlerTimer;

/**
 * Provides a central manager for the internal statistics.
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class StatisticsManager
{
  private static final SimpleReadWriteLock RWL_CACHE = new SimpleReadWriteLock ();
  private static final SimpleReadWriteLock RWL_TIMER = new SimpleReadWriteLock ();
  private static final SimpleReadWriteLock RWL_KEYED_TIMER = new SimpleReadWriteLock ();
  private static final SimpleReadWriteLock RWL_SIZE = new SimpleReadWriteLock ();
  private static final SimpleReadWriteLock RWL_KEYED_SIZE = new SimpleReadWriteLock ();
  private static final SimpleReadWriteLock RWL_COUNTER = new SimpleReadWriteLock ();
  private static final SimpleReadWriteLock RWL_KEYED_COUNTER = new SimpleReadWriteLock ();
  private static final ICommonsMap <String, StatisticsHandlerCache> HDL_CACHE = new CommonsHashMap <> ();
  private static final ICommonsMap <String, StatisticsHandlerTimer> HDL_TIMER = new CommonsHashMap <> ();
  private static final ICommonsMap <String, StatisticsHandlerKeyedTimer> HDL_KEYED_TIMER = new CommonsHashMap <> ();
  private static final ICommonsMap <String, StatisticsHandlerSize> HDL_SIZE = new CommonsHashMap <> ();
  private static final ICommonsMap <String, StatisticsHandlerKeyedSize> HDL_KEYED_SIZE = new CommonsHashMap <> ();
  private static final ICommonsMap <String, StatisticsHandlerCounter> HDL_COUNTER = new CommonsHashMap <> ();
  private static final ICommonsMap <String, StatisticsHandlerKeyedCounter> HDL_KEYED_COUNTER = new CommonsHashMap <> ();

  private static final Logger LOGGER = LoggerFactory.getLogger (StatisticsManager.class);

  @PresentForCodeCoverage
  private static final StatisticsManager INSTANCE = new StatisticsManager ();

  private StatisticsManager ()
  {}

  /**
   * Get or create a cache statistics handler for the given class.
   *
   * @param aClass
   *        The class to get the handler for. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static IMutableStatisticsHandlerCache getCacheHandler (@NonNull final Class <?> aClass)
  {
    ValueEnforcer.notNull (aClass, "Class");

    return getCacheHandler (aClass.getName ());
  }

  /**
   * Get or create a cache statistics handler for the given name.
   *
   * @param sName
   *        The name to get the handler for. May neither be <code>null</code>
   *        nor empty.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static IMutableStatisticsHandlerCache getCacheHandler (@NonNull @Nonempty final String sName)
  {
    ValueEnforcer.notEmpty (sName, "Name");

    StatisticsHandlerCache aHdl = RWL_CACHE.readLockedGet ( () -> HDL_CACHE.get (sName));

    if (aHdl == null)
    {
      // Try again in write lock
      aHdl = RWL_CACHE.writeLockedGet ( () -> HDL_CACHE.computeIfAbsent (sName, k -> new StatisticsHandlerCache ()));
    }

    return aHdl;

  }

  /**
   * @return A copy of all registered cache handler names. Never
   *         <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static ICommonsSet <String> getAllCacheHandler ()
  {
    return RWL_CACHE.readLockedGet (HDL_CACHE::copyOfKeySet);
  }

  /**
   * Get or create a timer statistics handler for the given class.
   *
   * @param aClass
   *        The class to get the handler for. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static IMutableStatisticsHandlerTimer getTimerHandler (@NonNull final Class <?> aClass)
  {
    ValueEnforcer.notNull (aClass, "Class");

    return getTimerHandler (aClass.getName ());
  }

  /**
   * Get or create a timer statistics handler for the given name.
   *
   * @param sName
   *        The name to get the handler for. May neither be <code>null</code>
   *        nor empty.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static IMutableStatisticsHandlerTimer getTimerHandler (@NonNull @Nonempty final String sName)
  {
    ValueEnforcer.notEmpty (sName, "Name");

    StatisticsHandlerTimer aHdl = RWL_TIMER.readLockedGet ( () -> HDL_TIMER.get (sName));
    if (aHdl == null)
    {
      aHdl = RWL_TIMER.writeLockedGet ( () -> HDL_TIMER.computeIfAbsent (sName, k -> new StatisticsHandlerTimer ()));
    }

    return aHdl;
  }

  /**
   * @return A copy of all registered timer handler names. Never
   *         <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static ICommonsSet <String> getAllTimerHandler ()
  {
    return RWL_TIMER.readLockedGet (HDL_TIMER::copyOfKeySet);
  }

  /**
   * Get or create a keyed timer statistics handler for the given class.
   *
   * @param aClass
   *        The class to get the handler for. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static IMutableStatisticsHandlerKeyedTimer getKeyedTimerHandler (@NonNull final Class <?> aClass)
  {
    ValueEnforcer.notNull (aClass, "Class");

    return getKeyedTimerHandler (aClass.getName ());
  }

  /**
   * Get or create a keyed timer statistics handler for the given name.
   *
   * @param sName
   *        The name to get the handler for. May neither be <code>null</code>
   *        nor empty.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static IMutableStatisticsHandlerKeyedTimer getKeyedTimerHandler (@NonNull @Nonempty final String sName)
  {
    ValueEnforcer.notEmpty (sName, "Name");

    StatisticsHandlerKeyedTimer aHdl = RWL_KEYED_TIMER.readLockedGet ( () -> HDL_KEYED_TIMER.get (sName));

    if (aHdl == null)
    {
      aHdl = RWL_KEYED_TIMER.writeLockedGet ( () -> HDL_KEYED_TIMER.computeIfAbsent (sName, k -> new StatisticsHandlerKeyedTimer ()));
    }
    return aHdl;
  }

  /**
   * @return A copy of all registered keyed timer handler names. Never
   *         <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static ICommonsSet <String> getAllKeyedTimerHandler ()
  {
    return RWL_KEYED_TIMER.readLockedGet (HDL_KEYED_TIMER::copyOfKeySet);
  }

  /**
   * Get or create a size statistics handler for the given class.
   *
   * @param aClass
   *        The class to get the handler for. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static IMutableStatisticsHandlerSize getSizeHandler (@NonNull final Class <?> aClass)
  {
    ValueEnforcer.notNull (aClass, "Class");

    return getSizeHandler (aClass.getName ());
  }

  /**
   * Get or create a size statistics handler for the given name.
   *
   * @param sName
   *        The name to get the handler for. May neither be <code>null</code>
   *        nor empty.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static IMutableStatisticsHandlerSize getSizeHandler (@NonNull @Nonempty final String sName)
  {
    ValueEnforcer.notEmpty (sName, "Name");

    StatisticsHandlerSize aHdl = RWL_SIZE.readLockedGet ( () -> HDL_SIZE.get (sName));

    if (aHdl == null)
    {
      aHdl = RWL_SIZE.writeLockedGet ( () -> HDL_SIZE.computeIfAbsent (sName, k -> new StatisticsHandlerSize ()));
    }
    return aHdl;
  }

  /**
   * @return A copy of all registered size handler names. Never
   *         <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static ICommonsSet <String> getAllSizeHandler ()
  {
    return RWL_SIZE.readLockedGet (HDL_SIZE::copyOfKeySet);
  }

  /**
   * Get or create a keyed size statistics handler for the given class.
   *
   * @param aClass
   *        The class to get the handler for. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static IMutableStatisticsHandlerKeyedSize getKeyedSizeHandler (@NonNull final Class <?> aClass)
  {
    ValueEnforcer.notNull (aClass, "Class");

    return getKeyedSizeHandler (aClass.getName ());
  }

  /**
   * Get or create a keyed size statistics handler for the given name.
   *
   * @param sName
   *        The name to get the handler for. May neither be <code>null</code>
   *        nor empty.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static IMutableStatisticsHandlerKeyedSize getKeyedSizeHandler (@NonNull @Nonempty final String sName)
  {
    ValueEnforcer.notEmpty (sName, "Name");

    StatisticsHandlerKeyedSize aHdl = RWL_KEYED_SIZE.readLockedGet ( () -> HDL_KEYED_SIZE.get (sName));

    if (aHdl == null)
    {
      aHdl = RWL_KEYED_SIZE.writeLockedGet ( () -> HDL_KEYED_SIZE.computeIfAbsent (sName, k -> new StatisticsHandlerKeyedSize ()));
    }
    return aHdl;
  }

  /**
   * @return A copy of all registered keyed size handler names. Never
   *         <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static ICommonsSet <String> getAllKeyedSizeHandler ()
  {
    return RWL_KEYED_SIZE.readLockedGet (HDL_KEYED_SIZE::copyOfKeySet);
  }

  /**
   * Get or create a counter statistics handler for the given class.
   *
   * @param aClass
   *        The class to get the handler for. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static IMutableStatisticsHandlerCounter getCounterHandler (@NonNull final Class <?> aClass)
  {
    ValueEnforcer.notNull (aClass, "Class");

    return getCounterHandler (aClass.getName ());
  }

  /**
   * Get or create a counter statistics handler for the given name.
   *
   * @param sName
   *        The name to get the handler for. May neither be <code>null</code>
   *        nor empty.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static IMutableStatisticsHandlerCounter getCounterHandler (@NonNull @Nonempty final String sName)
  {
    ValueEnforcer.notEmpty (sName, "Name");

    StatisticsHandlerCounter aHdl = RWL_COUNTER.readLockedGet ( () -> HDL_COUNTER.get (sName));

    if (aHdl == null)
    {
      aHdl = RWL_COUNTER.writeLockedGet ( () -> HDL_COUNTER.computeIfAbsent (sName, k -> new StatisticsHandlerCounter ()));
    }
    return aHdl;
  }

  /**
   * @return A copy of all registered counter handler names. Never
   *         <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static ICommonsSet <String> getAllCounterHandler ()
  {
    return RWL_COUNTER.readLockedGet (HDL_COUNTER::copyOfKeySet);
  }

  /**
   * Get or create a keyed counter statistics handler for the given class.
   *
   * @param aClass
   *        The class to get the handler for. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static IMutableStatisticsHandlerKeyedCounter getKeyedCounterHandler (@NonNull final Class <?> aClass)
  {
    ValueEnforcer.notNull (aClass, "Class");

    return getKeyedCounterHandler (aClass.getName ());
  }

  /**
   * Get or create a keyed counter statistics handler for the given name.
   *
   * @param sName
   *        The name to get the handler for. May neither be <code>null</code>
   *        nor empty.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static IMutableStatisticsHandlerKeyedCounter getKeyedCounterHandler (@NonNull @Nonempty final String sName)
  {
    ValueEnforcer.notEmpty (sName, "Name");

    StatisticsHandlerKeyedCounter aHdl = RWL_KEYED_COUNTER.readLockedGet ( () -> HDL_KEYED_COUNTER.get (sName));

    if (aHdl == null)
    {
      aHdl = RWL_KEYED_COUNTER.writeLockedGet ( () -> HDL_KEYED_COUNTER.computeIfAbsent (sName,
                                                                                              k -> new StatisticsHandlerKeyedCounter ()));
    }
    return aHdl;
  }

  /**
   * @return A copy of all registered keyed counter handler names. Never
   *         <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static ICommonsSet <String> getAllKeyedCounterHandler ()
  {
    return RWL_KEYED_COUNTER.readLockedGet (HDL_KEYED_COUNTER::copyOfKeySet);
  }

  /**
   * Clear all internal statistics data.
   */
  public static void clearCache ()
  {
    RWL_CACHE.writeLocked (HDL_CACHE::clear);
    RWL_TIMER.writeLocked (HDL_TIMER::clear);
    RWL_KEYED_TIMER.writeLocked (HDL_KEYED_TIMER::clear);
    RWL_SIZE.writeLocked (HDL_SIZE::clear);
    RWL_KEYED_SIZE.writeLocked (HDL_KEYED_SIZE::clear);
    RWL_COUNTER.writeLocked (HDL_COUNTER::clear);
    RWL_KEYED_COUNTER.writeLocked (HDL_KEYED_COUNTER::clear);

    if (LOGGER.isDebugEnabled ())
      LOGGER.debug ("Cache was cleared: " + StatisticsManager.class.getName ());
  }
}
