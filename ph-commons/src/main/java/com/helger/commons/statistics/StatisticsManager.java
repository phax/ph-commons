/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.concurrent.SimpleReadWriteLock;

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

  @Nonnull
  public static IMutableStatisticsHandlerCache getCacheHandler (@Nonnull final Class <?> aClass)
  {
    ValueEnforcer.notNull (aClass, "Class");

    return getCacheHandler (aClass.getName ());
  }

  @Nonnull
  public static IMutableStatisticsHandlerCache getCacheHandler (@Nonnull @Nonempty final String sName)
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

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsSet <String> getAllCacheHandler ()
  {
    return RWL_CACHE.readLockedGet (HDL_CACHE::copyOfKeySet);
  }

  @Nonnull
  public static IMutableStatisticsHandlerTimer getTimerHandler (@Nonnull final Class <?> aClass)
  {
    ValueEnforcer.notNull (aClass, "Class");

    return getTimerHandler (aClass.getName ());
  }

  @Nonnull
  public static IMutableStatisticsHandlerTimer getTimerHandler (@Nonnull @Nonempty final String sName)
  {
    ValueEnforcer.notEmpty (sName, "Name");

    StatisticsHandlerTimer aHdl = RWL_TIMER.readLockedGet ( () -> HDL_TIMER.get (sName));
    if (aHdl == null)
    {
      aHdl = RWL_TIMER.writeLockedGet ( () -> HDL_TIMER.computeIfAbsent (sName, k -> new StatisticsHandlerTimer ()));
    }

    return aHdl;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsSet <String> getAllTimerHandler ()
  {
    return RWL_TIMER.readLockedGet (HDL_TIMER::copyOfKeySet);
  }

  @Nonnull
  public static IMutableStatisticsHandlerKeyedTimer getKeyedTimerHandler (@Nonnull final Class <?> aClass)
  {
    ValueEnforcer.notNull (aClass, "Class");

    return getKeyedTimerHandler (aClass.getName ());
  }

  @Nonnull
  public static IMutableStatisticsHandlerKeyedTimer getKeyedTimerHandler (@Nonnull @Nonempty final String sName)
  {
    ValueEnforcer.notEmpty (sName, "Name");

    StatisticsHandlerKeyedTimer aHdl = RWL_KEYED_TIMER.readLockedGet ( () -> HDL_KEYED_TIMER.get (sName));

    if (aHdl == null)
    {
      aHdl = RWL_KEYED_TIMER.writeLockedGet ( () -> HDL_KEYED_TIMER.computeIfAbsent (sName, k -> new StatisticsHandlerKeyedTimer ()));
    }
    return aHdl;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsSet <String> getAllKeyedTimerHandler ()
  {
    return RWL_KEYED_TIMER.readLockedGet (HDL_KEYED_TIMER::copyOfKeySet);
  }

  @Nonnull
  public static IMutableStatisticsHandlerSize getSizeHandler (@Nonnull final Class <?> aClass)
  {
    ValueEnforcer.notNull (aClass, "Class");

    return getSizeHandler (aClass.getName ());
  }

  @Nonnull
  public static IMutableStatisticsHandlerSize getSizeHandler (@Nonnull @Nonempty final String sName)
  {
    ValueEnforcer.notEmpty (sName, "Name");

    StatisticsHandlerSize aHdl = RWL_SIZE.readLockedGet ( () -> HDL_SIZE.get (sName));

    if (aHdl == null)
    {
      aHdl = RWL_SIZE.writeLockedGet ( () -> HDL_SIZE.computeIfAbsent (sName, k -> new StatisticsHandlerSize ()));
    }
    return aHdl;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsSet <String> getAllSizeHandler ()
  {
    return RWL_SIZE.readLockedGet (HDL_SIZE::copyOfKeySet);
  }

  @Nonnull
  public static IMutableStatisticsHandlerKeyedSize getKeyedSizeHandler (@Nonnull final Class <?> aClass)
  {
    ValueEnforcer.notNull (aClass, "Class");

    return getKeyedSizeHandler (aClass.getName ());
  }

  @Nonnull
  public static IMutableStatisticsHandlerKeyedSize getKeyedSizeHandler (@Nonnull @Nonempty final String sName)
  {
    ValueEnforcer.notEmpty (sName, "Name");

    StatisticsHandlerKeyedSize aHdl = RWL_KEYED_SIZE.readLockedGet ( () -> HDL_KEYED_SIZE.get (sName));

    if (aHdl == null)
    {
      aHdl = RWL_KEYED_SIZE.writeLockedGet ( () -> HDL_KEYED_SIZE.computeIfAbsent (sName, k -> new StatisticsHandlerKeyedSize ()));
    }
    return aHdl;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsSet <String> getAllKeyedSizeHandler ()
  {
    return RWL_KEYED_SIZE.readLockedGet (HDL_KEYED_SIZE::copyOfKeySet);
  }

  @Nonnull
  public static IMutableStatisticsHandlerCounter getCounterHandler (@Nonnull final Class <?> aClass)
  {
    ValueEnforcer.notNull (aClass, "Class");

    return getCounterHandler (aClass.getName ());
  }

  @Nonnull
  public static IMutableStatisticsHandlerCounter getCounterHandler (@Nonnull @Nonempty final String sName)
  {
    ValueEnforcer.notEmpty (sName, "Name");

    StatisticsHandlerCounter aHdl = RWL_COUNTER.readLockedGet ( () -> HDL_COUNTER.get (sName));

    if (aHdl == null)
    {
      aHdl = RWL_COUNTER.writeLockedGet ( () -> HDL_COUNTER.computeIfAbsent (sName, k -> new StatisticsHandlerCounter ()));
    }
    return aHdl;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsSet <String> getAllCounterHandler ()
  {
    return RWL_COUNTER.readLockedGet (HDL_COUNTER::copyOfKeySet);
  }

  @Nonnull
  public static IMutableStatisticsHandlerKeyedCounter getKeyedCounterHandler (@Nonnull final Class <?> aClass)
  {
    ValueEnforcer.notNull (aClass, "Class");

    return getKeyedCounterHandler (aClass.getName ());
  }

  @Nonnull
  public static IMutableStatisticsHandlerKeyedCounter getKeyedCounterHandler (@Nonnull @Nonempty final String sName)
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

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsSet <String> getAllKeyedCounterHandler ()
  {
    return RWL_KEYED_COUNTER.readLockedGet (HDL_KEYED_COUNTER::copyOfKeySet);
  }

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
