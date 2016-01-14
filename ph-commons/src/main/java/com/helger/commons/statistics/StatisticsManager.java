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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
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
  private static final Map <String, StatisticsHandlerCache> s_aHdlCache = new HashMap <String, StatisticsHandlerCache> ();
  private static final Map <String, StatisticsHandlerTimer> s_aHdlTimer = new HashMap <String, StatisticsHandlerTimer> ();
  private static final Map <String, StatisticsHandlerKeyedTimer> s_aHdlKeyedTimer = new HashMap <String, StatisticsHandlerKeyedTimer> ();
  private static final Map <String, StatisticsHandlerSize> s_aHdlSize = new HashMap <String, StatisticsHandlerSize> ();
  private static final Map <String, StatisticsHandlerKeyedSize> s_aHdlKeyedSize = new HashMap <String, StatisticsHandlerKeyedSize> ();
  private static final Map <String, StatisticsHandlerCounter> s_aHdlCounter = new HashMap <String, StatisticsHandlerCounter> ();
  private static final Map <String, StatisticsHandlerKeyedCounter> s_aHdlKeyedCounter = new HashMap <String, StatisticsHandlerKeyedCounter> ();

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
      aHdl = s_aRWLockCache.writeLocked ( () -> {
        // Try again in write lock
        StatisticsHandlerCache aWLHdl = s_aHdlCache.get (sName);
        if (aWLHdl == null)
        {
          aWLHdl = new StatisticsHandlerCache ();
          s_aHdlCache.put (sName, aWLHdl);
        }
        return aWLHdl;
      });
    }

    return aHdl;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static Set <String> getAllCacheHandler ()
  {
    return s_aRWLockCache.readLocked ( () -> CollectionHelper.newSet (s_aHdlCache.keySet ()));
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
      aHdl = s_aRWLockTimer.writeLocked ( () -> {
        // Try again in write lock
        StatisticsHandlerTimer aWLHdl = s_aHdlTimer.get (sName);
        if (aWLHdl == null)
        {
          aWLHdl = new StatisticsHandlerTimer ();
          s_aHdlTimer.put (sName, aWLHdl);
        }
        return aWLHdl;
      });
    }

    return aHdl;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static Set <String> getAllTimerHandler ()
  {
    return s_aRWLockTimer.readLocked ( () -> CollectionHelper.newSet (s_aHdlTimer.keySet ()));
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
      aHdl = s_aRWLockKeyedTimer.writeLocked ( () -> {
        // Try again in write lock
        StatisticsHandlerKeyedTimer aWLHdl = s_aHdlKeyedTimer.get (sName);
        if (aWLHdl == null)
        {
          aWLHdl = new StatisticsHandlerKeyedTimer ();
          s_aHdlKeyedTimer.put (sName, aWLHdl);
        }
        return aWLHdl;
      });
    }
    return aHdl;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static Set <String> getAllKeyedTimerHandler ()
  {
    return s_aRWLockKeyedTimer.readLocked ( () -> CollectionHelper.newSet (s_aHdlKeyedTimer.keySet ()));
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
      aHdl = s_aRWLockSize.writeLocked ( () -> {
        // Try again in write lock
        StatisticsHandlerSize aWLHdl = s_aHdlSize.get (sName);
        if (aWLHdl == null)
        {
          aWLHdl = new StatisticsHandlerSize ();
          s_aHdlSize.put (sName, aWLHdl);
        }
        return aWLHdl;
      });
    }
    return aHdl;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static Set <String> getAllSizeHandler ()
  {
    return s_aRWLockSize.readLocked ( () -> CollectionHelper.newSet (s_aHdlSize.keySet ()));
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
      aHdl = s_aRWLockKeyedSize.writeLocked ( () -> {
        // Try again in write lock
        StatisticsHandlerKeyedSize aWLHdl = s_aHdlKeyedSize.get (sName);
        if (aWLHdl == null)
        {
          aWLHdl = new StatisticsHandlerKeyedSize ();
          s_aHdlKeyedSize.put (sName, aWLHdl);
        }
        return aWLHdl;
      });
    }
    return aHdl;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static Set <String> getAllKeyedSizeHandler ()
  {
    return s_aRWLockKeyedSize.readLocked ( () -> CollectionHelper.newSet (s_aHdlKeyedSize.keySet ()));
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
      aHdl = s_aRWLockCounter.writeLocked ( () -> {
        // Try again in write lock
        StatisticsHandlerCounter aWLHdl = s_aHdlCounter.get (sName);
        if (aWLHdl == null)
        {
          aWLHdl = new StatisticsHandlerCounter ();
          s_aHdlCounter.put (sName, aWLHdl);
        }
        return aWLHdl;
      });
    }
    return aHdl;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static Set <String> getAllCounterHandler ()
  {
    return s_aRWLockCounter.readLocked ( () -> CollectionHelper.newSet (s_aHdlCounter.keySet ()));
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
      aHdl = s_aRWLockKeyedCounter.writeLocked ( () -> {
        // Try again in write lock
        StatisticsHandlerKeyedCounter aWLHdl = s_aHdlKeyedCounter.get (sName);
        if (aWLHdl == null)
        {
          aWLHdl = new StatisticsHandlerKeyedCounter ();
          s_aHdlKeyedCounter.put (sName, aWLHdl);
        }
        return aWLHdl;
      });
    }
    return aHdl;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static Set <String> getAllKeyedCounterHandler ()
  {
    return s_aRWLockKeyedCounter.readLocked ( () -> CollectionHelper.newSet (s_aHdlKeyedCounter.keySet ()));
  }

  public static void clearCache ()
  {
    s_aRWLockCache.writeLocked ( () -> s_aHdlCache.clear ());
    s_aRWLockTimer.writeLocked ( () -> s_aHdlTimer.clear ());
    s_aRWLockKeyedTimer.writeLocked ( () -> s_aHdlKeyedTimer.clear ());
    s_aRWLockSize.writeLocked ( () -> s_aHdlSize.clear ());
    s_aRWLockKeyedSize.writeLocked ( () -> s_aHdlKeyedSize.clear ());
    s_aRWLockCounter.writeLocked ( () -> s_aHdlCounter.clear ());
    s_aRWLockKeyedCounter.writeLocked ( () -> s_aHdlKeyedCounter.clear ());

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("Cache was cleared: " + StatisticsManager.class.getName ());
  }
}
