/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.stats;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.annotations.PresentForCodeCoverage;
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.collections.CollectionHelper;

/**
 * Provides a central manager for the internal statistics.
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class StatisticsManager
{
  private static final ReadWriteLock s_aRWLockCache = new ReentrantReadWriteLock ();
  private static final ReadWriteLock s_aRWLockTimer = new ReentrantReadWriteLock ();
  private static final ReadWriteLock s_aRWLockKeyedTimer = new ReentrantReadWriteLock ();
  private static final ReadWriteLock s_aRWLockSize = new ReentrantReadWriteLock ();
  private static final ReadWriteLock s_aRWLockKeyedSize = new ReentrantReadWriteLock ();
  private static final ReadWriteLock s_aRWLockCounter = new ReentrantReadWriteLock ();
  private static final ReadWriteLock s_aRWLockKeyedCounter = new ReentrantReadWriteLock ();
  private static final Map <String, StatisticsHandlerCache> s_aHdlCache = new HashMap <String, StatisticsHandlerCache> ();
  private static final Map <String, StatisticsHandlerTimer> s_aHdlTimer = new HashMap <String, StatisticsHandlerTimer> ();
  private static final Map <String, StatisticsHandlerKeyedTimer> s_aHdlKeyedTimer = new HashMap <String, StatisticsHandlerKeyedTimer> ();
  private static final Map <String, StatisticsHandlerSize> s_aHdlSize = new HashMap <String, StatisticsHandlerSize> ();
  private static final Map <String, StatisticsHandlerKeyedSize> s_aHdlKeyedSize = new HashMap <String, StatisticsHandlerKeyedSize> ();
  private static final Map <String, StatisticsHandlerCounter> s_aHdlCounter = new HashMap <String, StatisticsHandlerCounter> ();
  private static final Map <String, StatisticsHandlerKeyedCounter> s_aHdlKeyedCounter = new HashMap <String, StatisticsHandlerKeyedCounter> ();

  @PresentForCodeCoverage
  private static final StatisticsManager s_aInstance = new StatisticsManager ();

  private StatisticsManager ()
  {}

  @Nonnull
  public static IStatisticsHandlerCache getCacheHandler (@Nonnull final Class <?> aClass)
  {
    ValueEnforcer.notNull (aClass, "Class");

    return getCacheHandler (aClass.getName ());
  }

  @Nonnull
  public static IStatisticsHandlerCache getCacheHandler (@Nonnull @Nonempty final String sName)
  {
    ValueEnforcer.notEmpty (sName, "Name");

    StatisticsHandlerCache aHdl;
    s_aRWLockCache.readLock ().lock ();
    try
    {
      aHdl = s_aHdlCache.get (sName);
    }
    finally
    {
      s_aRWLockCache.readLock ().unlock ();
    }

    if (aHdl == null)
    {
      s_aRWLockCache.writeLock ().lock ();
      try
      {
        // Try again in write lock
        aHdl = s_aHdlCache.get (sName);
        if (aHdl == null)
        {
          aHdl = new StatisticsHandlerCache ();
          s_aHdlCache.put (sName, aHdl);
        }
      }
      finally
      {
        s_aRWLockCache.writeLock ().unlock ();
      }
    }

    return aHdl;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static Set <String> getAllCacheHandler ()
  {
    s_aRWLockCache.readLock ().lock ();
    try
    {
      return CollectionHelper.newSet (s_aHdlCache.keySet ());
    }
    finally
    {
      s_aRWLockCache.readLock ().unlock ();
    }
  }

  @Nonnull
  public static IStatisticsHandlerTimer getTimerHandler (@Nonnull final Class <?> aClass)
  {
    ValueEnforcer.notNull (aClass, "Class");

    return getTimerHandler (aClass.getName ());
  }

  @Nonnull
  public static IStatisticsHandlerTimer getTimerHandler (@Nonnull @Nonempty final String sName)
  {
    ValueEnforcer.notEmpty (sName, "Name");

    StatisticsHandlerTimer aHdl;
    s_aRWLockTimer.readLock ().lock ();
    try
    {
      aHdl = s_aHdlTimer.get (sName);
    }
    finally
    {
      s_aRWLockTimer.readLock ().unlock ();
    }

    if (aHdl == null)
    {
      s_aRWLockTimer.writeLock ().lock ();
      try
      {
        // Try again in write lock
        aHdl = s_aHdlTimer.get (sName);
        if (aHdl == null)
        {
          aHdl = new StatisticsHandlerTimer ();
          s_aHdlTimer.put (sName, aHdl);
        }
      }
      finally
      {
        s_aRWLockTimer.writeLock ().unlock ();
      }
    }

    return aHdl;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static Set <String> getAllTimerHandler ()
  {
    s_aRWLockTimer.readLock ().lock ();
    try
    {
      return CollectionHelper.newSet (s_aHdlTimer.keySet ());
    }
    finally
    {
      s_aRWLockTimer.readLock ().unlock ();
    }
  }

  @Nonnull
  public static IStatisticsHandlerKeyedTimer getKeyedTimerHandler (@Nonnull final Class <?> aClass)
  {
    ValueEnforcer.notNull (aClass, "Class");

    return getKeyedTimerHandler (aClass.getName ());
  }

  @Nonnull
  public static IStatisticsHandlerKeyedTimer getKeyedTimerHandler (@Nonnull @Nonempty final String sName)
  {
    ValueEnforcer.notEmpty (sName, "Name");

    StatisticsHandlerKeyedTimer aHdl;
    s_aRWLockKeyedTimer.readLock ().lock ();
    try
    {
      aHdl = s_aHdlKeyedTimer.get (sName);
    }
    finally
    {
      s_aRWLockKeyedTimer.readLock ().unlock ();
    }

    if (aHdl == null)
    {
      s_aRWLockKeyedTimer.writeLock ().lock ();
      try
      {
        // Try again in write lock
        aHdl = s_aHdlKeyedTimer.get (sName);
        if (aHdl == null)
        {
          aHdl = new StatisticsHandlerKeyedTimer ();
          s_aHdlKeyedTimer.put (sName, aHdl);
        }
      }
      finally
      {
        s_aRWLockKeyedTimer.writeLock ().unlock ();
      }
    }
    return aHdl;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static Set <String> getAllKeyedTimerHandler ()
  {
    s_aRWLockKeyedTimer.readLock ().lock ();
    try
    {
      return CollectionHelper.newSet (s_aHdlKeyedTimer.keySet ());
    }
    finally
    {
      s_aRWLockKeyedTimer.readLock ().unlock ();
    }
  }

  @Nonnull
  public static IStatisticsHandlerSize getSizeHandler (@Nonnull final Class <?> aClass)
  {
    ValueEnforcer.notNull (aClass, "Class");

    return getSizeHandler (aClass.getName ());
  }

  @Nonnull
  public static IStatisticsHandlerSize getSizeHandler (@Nonnull @Nonempty final String sName)
  {
    ValueEnforcer.notEmpty (sName, "Name");

    StatisticsHandlerSize aHdl;
    s_aRWLockSize.readLock ().lock ();
    try
    {
      aHdl = s_aHdlSize.get (sName);
    }
    finally
    {
      s_aRWLockSize.readLock ().unlock ();
    }

    if (aHdl == null)
    {
      s_aRWLockSize.writeLock ().lock ();
      try
      {
        // Try again in write lock
        aHdl = s_aHdlSize.get (sName);
        if (aHdl == null)
        {
          aHdl = new StatisticsHandlerSize ();
          s_aHdlSize.put (sName, aHdl);
        }
      }
      finally
      {
        s_aRWLockSize.writeLock ().unlock ();
      }
    }
    return aHdl;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static Set <String> getAllSizeHandler ()
  {
    s_aRWLockSize.readLock ().lock ();
    try
    {
      return CollectionHelper.newSet (s_aHdlSize.keySet ());
    }
    finally
    {
      s_aRWLockSize.readLock ().unlock ();
    }
  }

  @Nonnull
  public static IStatisticsHandlerKeyedSize getKeyedSizeHandler (@Nonnull final Class <?> aClass)
  {
    ValueEnforcer.notNull (aClass, "Class");

    return getKeyedSizeHandler (aClass.getName ());
  }

  @Nonnull
  public static IStatisticsHandlerKeyedSize getKeyedSizeHandler (@Nonnull @Nonempty final String sName)
  {
    ValueEnforcer.notEmpty (sName, "Name");

    StatisticsHandlerKeyedSize aHdl;
    s_aRWLockKeyedSize.readLock ().lock ();
    try
    {
      aHdl = s_aHdlKeyedSize.get (sName);
    }
    finally
    {
      s_aRWLockKeyedSize.readLock ().unlock ();
    }

    if (aHdl == null)
    {
      s_aRWLockKeyedSize.writeLock ().lock ();
      try
      {
        // Try again in write lock
        aHdl = s_aHdlKeyedSize.get (sName);
        if (aHdl == null)
        {
          aHdl = new StatisticsHandlerKeyedSize ();
          s_aHdlKeyedSize.put (sName, aHdl);
        }
      }
      finally
      {
        s_aRWLockKeyedSize.writeLock ().unlock ();
      }
    }
    return aHdl;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static Set <String> getAllKeyedSizeHandler ()
  {
    s_aRWLockKeyedSize.readLock ().lock ();
    try
    {
      return CollectionHelper.newSet (s_aHdlKeyedSize.keySet ());
    }
    finally
    {
      s_aRWLockKeyedSize.readLock ().unlock ();
    }
  }

  @Nonnull
  public static IStatisticsHandlerCounter getCounterHandler (@Nonnull final Class <?> aClass)
  {
    ValueEnforcer.notNull (aClass, "Class");

    return getCounterHandler (aClass.getName ());
  }

  @Nonnull
  public static IStatisticsHandlerCounter getCounterHandler (@Nonnull @Nonempty final String sName)
  {
    ValueEnforcer.notEmpty (sName, "Name");

    StatisticsHandlerCounter aHdl;
    s_aRWLockCounter.readLock ().lock ();
    try
    {
      aHdl = s_aHdlCounter.get (sName);
    }
    finally
    {
      s_aRWLockCounter.readLock ().unlock ();
    }

    if (aHdl == null)
    {
      s_aRWLockCounter.writeLock ().lock ();
      try
      {
        // Try again in write lock
        aHdl = s_aHdlCounter.get (sName);
        if (aHdl == null)
        {
          aHdl = new StatisticsHandlerCounter ();
          s_aHdlCounter.put (sName, aHdl);
        }
      }
      finally
      {
        s_aRWLockCounter.writeLock ().unlock ();
      }
    }
    return aHdl;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static Set <String> getAllCounterHandler ()
  {
    s_aRWLockCounter.readLock ().lock ();
    try
    {
      return CollectionHelper.newSet (s_aHdlCounter.keySet ());
    }
    finally
    {
      s_aRWLockCounter.readLock ().unlock ();
    }
  }

  @Nonnull
  public static IStatisticsHandlerKeyedCounter getKeyedCounterHandler (@Nonnull final Class <?> aClass)
  {
    ValueEnforcer.notNull (aClass, "Class");

    return getKeyedCounterHandler (aClass.getName ());
  }

  @Nonnull
  public static IStatisticsHandlerKeyedCounter getKeyedCounterHandler (@Nonnull @Nonempty final String sName)
  {
    ValueEnforcer.notEmpty (sName, "Name");

    StatisticsHandlerKeyedCounter aHdl;
    s_aRWLockKeyedCounter.readLock ().lock ();
    try
    {
      aHdl = s_aHdlKeyedCounter.get (sName);
    }
    finally
    {
      s_aRWLockKeyedCounter.readLock ().unlock ();
    }

    if (aHdl == null)
    {
      s_aRWLockKeyedCounter.writeLock ().lock ();
      try
      {
        // Try again in write lock
        aHdl = s_aHdlKeyedCounter.get (sName);
        if (aHdl == null)
        {
          aHdl = new StatisticsHandlerKeyedCounter ();
          s_aHdlKeyedCounter.put (sName, aHdl);
        }
      }
      finally
      {
        s_aRWLockKeyedCounter.writeLock ().unlock ();
      }
    }
    return aHdl;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static Set <String> getAllKeyedCounterHandler ()
  {
    s_aRWLockKeyedCounter.readLock ().lock ();
    try
    {
      return CollectionHelper.newSet (s_aHdlKeyedCounter.keySet ());
    }
    finally
    {
      s_aRWLockKeyedCounter.readLock ().unlock ();
    }
  }

  public static void clearCache ()
  {
    s_aRWLockCache.writeLock ().lock ();
    try
    {
      s_aHdlCache.clear ();
    }
    finally
    {
      s_aRWLockCache.writeLock ().unlock ();
    }

    s_aRWLockTimer.writeLock ().lock ();
    try
    {
      s_aHdlTimer.clear ();
    }
    finally
    {
      s_aRWLockTimer.writeLock ().unlock ();
    }

    s_aRWLockKeyedTimer.writeLock ().lock ();
    try
    {
      s_aHdlKeyedTimer.clear ();
    }
    finally
    {
      s_aRWLockKeyedTimer.writeLock ().unlock ();
    }

    s_aRWLockSize.writeLock ().lock ();
    try
    {
      s_aHdlSize.clear ();
    }
    finally
    {
      s_aRWLockSize.writeLock ().unlock ();
    }

    s_aRWLockKeyedSize.writeLock ().lock ();
    try
    {
      s_aHdlKeyedSize.clear ();
    }
    finally
    {
      s_aRWLockKeyedSize.writeLock ().unlock ();
    }

    s_aRWLockCounter.writeLock ().lock ();
    try
    {
      s_aHdlCounter.clear ();
    }
    finally
    {
      s_aRWLockCounter.writeLock ().unlock ();
    }

    s_aRWLockKeyedCounter.writeLock ().lock ();
    try
    {
      s_aHdlKeyedCounter.clear ();
    }
    finally
    {
      s_aRWLockKeyedCounter.writeLock ().unlock ();
    }
  }
}
