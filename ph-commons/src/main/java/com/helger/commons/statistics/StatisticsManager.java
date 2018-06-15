/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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

import java.util.function.Supplier;

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
  private static final SimpleReadWriteLock s_aRWLockCache = new SimpleReadWriteLock ();
  private static final SimpleReadWriteLock s_aRWLockTimer = new SimpleReadWriteLock ();
  private static final SimpleReadWriteLock s_aRWLockKeyedTimer = new SimpleReadWriteLock ();
  private static final SimpleReadWriteLock s_aRWLockSize = new SimpleReadWriteLock ();
  private static final SimpleReadWriteLock s_aRWLockKeyedSize = new SimpleReadWriteLock ();
  private static final SimpleReadWriteLock s_aRWLockCounter = new SimpleReadWriteLock ();
  private static final SimpleReadWriteLock s_aRWLockKeyedCounter = new SimpleReadWriteLock ();
  private static final ICommonsMap <String, StatisticsHandlerCache> s_aHdlCache = new CommonsHashMap <> ();
  private static final ICommonsMap <String, StatisticsHandlerTimer> s_aHdlTimer = new CommonsHashMap <> ();
  private static final ICommonsMap <String, StatisticsHandlerKeyedTimer> s_aHdlKeyedTimer = new CommonsHashMap <> ();
  private static final ICommonsMap <String, StatisticsHandlerSize> s_aHdlSize = new CommonsHashMap <> ();
  private static final ICommonsMap <String, StatisticsHandlerKeyedSize> s_aHdlKeyedSize = new CommonsHashMap <> ();
  private static final ICommonsMap <String, StatisticsHandlerCounter> s_aHdlCounter = new CommonsHashMap <> ();
  private static final ICommonsMap <String, StatisticsHandlerKeyedCounter> s_aHdlKeyedCounter = new CommonsHashMap <> ();

  private static final Logger s_aLogger = LoggerFactory.getLogger (StatisticsManager.class);

  @PresentForCodeCoverage
  private static final StatisticsManager s_aInstance = new StatisticsManager ();

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

    StatisticsHandlerCache aHdl = s_aRWLockCache.readLocked ( () -> s_aHdlCache.get (sName));

    if (aHdl == null)
    {
      // Try again in write lock
      aHdl = s_aRWLockCache.writeLocked ( () -> s_aHdlCache.computeIfAbsent (sName,
                                                                             k -> new StatisticsHandlerCache ()));
    }

    return aHdl;

  }

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsSet <String> getAllCacheHandler ()
  {
    return s_aRWLockCache.readLocked ((Supplier <ICommonsSet <String>>) s_aHdlCache::copyOfKeySet);
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

    StatisticsHandlerTimer aHdl = s_aRWLockTimer.readLocked ( () -> s_aHdlTimer.get (sName));
    if (aHdl == null)
    {
      aHdl = s_aRWLockTimer.writeLocked ( () -> s_aHdlTimer.computeIfAbsent (sName,
                                                                             k -> new StatisticsHandlerTimer ()));
    }

    return aHdl;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsSet <String> getAllTimerHandler ()
  {
    return s_aRWLockTimer.readLocked ((Supplier <ICommonsSet <String>>) s_aHdlTimer::copyOfKeySet);
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

    StatisticsHandlerKeyedTimer aHdl = s_aRWLockKeyedTimer.readLocked ( () -> s_aHdlKeyedTimer.get (sName));

    if (aHdl == null)
    {
      aHdl = s_aRWLockKeyedTimer.writeLocked ( () -> s_aHdlKeyedTimer.computeIfAbsent (sName,
                                                                                       k -> new StatisticsHandlerKeyedTimer ()));
    }
    return aHdl;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsSet <String> getAllKeyedTimerHandler ()
  {
    return s_aRWLockKeyedTimer.readLocked ((Supplier <ICommonsSet <String>>) s_aHdlKeyedTimer::copyOfKeySet);
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

    StatisticsHandlerSize aHdl = s_aRWLockSize.readLocked ( () -> s_aHdlSize.get (sName));

    if (aHdl == null)
    {
      aHdl = s_aRWLockSize.writeLocked ( () -> s_aHdlSize.computeIfAbsent (sName, k -> new StatisticsHandlerSize ()));
    }
    return aHdl;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsSet <String> getAllSizeHandler ()
  {
    return s_aRWLockSize.readLocked ((Supplier <ICommonsSet <String>>) s_aHdlSize::copyOfKeySet);
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

    StatisticsHandlerKeyedSize aHdl = s_aRWLockKeyedSize.readLocked ( () -> s_aHdlKeyedSize.get (sName));

    if (aHdl == null)
    {
      aHdl = s_aRWLockKeyedSize.writeLocked ( () -> s_aHdlKeyedSize.computeIfAbsent (sName,
                                                                                     k -> new StatisticsHandlerKeyedSize ()));
    }
    return aHdl;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsSet <String> getAllKeyedSizeHandler ()
  {
    return s_aRWLockKeyedSize.readLocked ((Supplier <ICommonsSet <String>>) s_aHdlKeyedSize::copyOfKeySet);
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

    StatisticsHandlerCounter aHdl = s_aRWLockCounter.readLocked ( () -> s_aHdlCounter.get (sName));

    if (aHdl == null)
    {
      aHdl = s_aRWLockCounter.writeLocked ( () -> s_aHdlCounter.computeIfAbsent (sName,
                                                                                 k -> new StatisticsHandlerCounter ()));
    }
    return aHdl;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsSet <String> getAllCounterHandler ()
  {
    return s_aRWLockCounter.readLocked ((Supplier <ICommonsSet <String>>) s_aHdlCounter::copyOfKeySet);
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

    StatisticsHandlerKeyedCounter aHdl = s_aRWLockKeyedCounter.readLocked ( () -> s_aHdlKeyedCounter.get (sName));

    if (aHdl == null)
    {
      aHdl = s_aRWLockKeyedCounter.writeLocked ( () -> s_aHdlKeyedCounter.computeIfAbsent (sName,
                                                                                           k -> new StatisticsHandlerKeyedCounter ()));
    }
    return aHdl;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsSet <String> getAllKeyedCounterHandler ()
  {
    return s_aRWLockKeyedCounter.readLocked ((Supplier <ICommonsSet <String>>) s_aHdlKeyedCounter::copyOfKeySet);
  }

  public static void clearCache ()
  {
    s_aRWLockCache.writeLocked (s_aHdlCache::clear);
    s_aRWLockTimer.writeLocked (s_aHdlTimer::clear);
    s_aRWLockKeyedTimer.writeLocked (s_aHdlKeyedTimer::clear);
    s_aRWLockSize.writeLocked (s_aHdlSize::clear);
    s_aRWLockKeyedSize.writeLocked (s_aHdlKeyedSize::clear);
    s_aRWLockCounter.writeLocked (s_aHdlCounter::clear);
    s_aRWLockKeyedCounter.writeLocked (s_aHdlKeyedCounter::clear);

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("Cache was cleared: " + StatisticsManager.class.getName ());
  }
}
