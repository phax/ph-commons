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

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.GuardedBy;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.misc.Singleton;
import com.helger.base.concurrent.BasicThreadFactory;
import com.helger.base.concurrent.ExecutorServiceHelper;
import com.helger.base.concurrent.SimpleReadWriteLock;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.state.EChange;
import com.helger.cache.IMutableCache;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.ICommonsMap;

/**
 * A process-wide scheduler that periodically calls {@link IMutableCache#evictExpired()} on
 * registered caches. One single daemon thread serves all registered caches; the underlying executor
 * is started lazily on the first {@link #register(IMutableCache, Duration)} call and shut down when
 * the last cache is unregistered.
 *
 * @author Philip Helger
 * @since 12.3.0
 */
@ThreadSafe
@Singleton
public final class CacheEvictionScheduler
{
  /** Name pattern for the scheduler thread. */
  public static final String THREAD_NAME_PATTERN = "ph-cache-evictor-%d";

  private static final Logger LOGGER = LoggerFactory.getLogger (CacheEvictionScheduler.class);

  private static final class SingletonHolder
  {
    private static final CacheEvictionScheduler INSTANCE = new CacheEvictionScheduler ();
  }

  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
  @GuardedBy ("m_aRWLock")
  private final ICommonsMap <IMutableCache <?, ?>, ScheduledFuture <?>> m_aRegistrations = new CommonsHashMap <> ();
  @GuardedBy ("m_aRWLock")
  private ScheduledExecutorService m_aExecutor;

  private CacheEvictionScheduler ()
  {}

  /**
   * @return The singleton instance of this class. Never <code>null</code>.
   */
  @NonNull
  public static CacheEvictionScheduler getInstance ()
  {
    return SingletonHolder.INSTANCE;
  }

  /**
   * @return <code>true</code> if the underlying executor is currently running, <code>false</code>
   *         otherwise.
   */
  public boolean isRunning ()
  {
    return m_aRWLock.readLockedBoolean ( () -> m_aExecutor != null && !m_aExecutor.isShutdown ());
  }

  /**
   * @return The number of currently registered caches.
   */
  @Nonnegative
  public int getRegistrationCount ()
  {
    return m_aRWLock.readLockedInt (m_aRegistrations::size);
  }

  private static void _safeEvict (@NonNull final IMutableCache <?, ?> aCache)
  {
    try
    {
      aCache.evictExpired ();
    }
    catch (final RuntimeException ex)
    {
      // One cache failing must not kill the shared thread or stop the other
      // caches' eviction
      LOGGER.error ("Eviction of cache '" + aCache.getName () + "' failed", ex);
    }
  }

  /**
   * Register the provided cache for periodic eviction with the given interval. If the cache is
   * already registered, the existing registration is replaced.
   *
   * @param aCache
   *        The cache to register. May not be <code>null</code>.
   * @param aInterval
   *        The interval between successive {@link IMutableCache#evictExpired()} calls. Must be
   *        positive and non-<code>null</code>.
   */
  public void register (@NonNull final IMutableCache <?, ?> aCache, @NonNull final Duration aInterval)
  {
    ValueEnforcer.notNull (aCache, "Cache");
    ValueEnforcer.notNull (aInterval, "Interval");
    ValueEnforcer.isFalse (aInterval::isZero, "Interval must not be zero");
    ValueEnforcer.isFalse (aInterval::isNegative, "Interval must not be negative");

    m_aRWLock.writeLock ().lock ();
    try
    {
      // Cancel any existing registration
      final ScheduledFuture <?> aOld = m_aRegistrations.remove (aCache);
      if (aOld != null)
        aOld.cancel (false);

      // Lazily start the executor
      if (m_aExecutor == null || m_aExecutor.isShutdown ())
      {
        m_aExecutor = Executors.newSingleThreadScheduledExecutor (BasicThreadFactory.builder ()
                                                                                    .daemon (true)
                                                                                    .namingPattern (THREAD_NAME_PATTERN)
                                                                                    .build ());
        if (LOGGER.isDebugEnabled ())
          LOGGER.debug ("Started cache eviction scheduler thread");
      }

      final long nIntervalMillis = aInterval.toMillis ();
      final ScheduledFuture <?> aFuture = m_aExecutor.scheduleWithFixedDelay ( () -> _safeEvict (aCache),
                                                                               nIntervalMillis,
                                                                               nIntervalMillis,
                                                                               TimeUnit.MILLISECONDS);
      m_aRegistrations.put (aCache, aFuture);
      if (LOGGER.isDebugEnabled ())
        LOGGER.debug ("Registered cache '" + aCache.getName () + "' with eviction interval " + aInterval);
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  /**
   * Unregister the provided cache. The underlying executor is shut down if no caches remain
   * registered.
   *
   * @param aCache
   *        The cache to unregister. May not be <code>null</code>.
   * @return {@link EChange#CHANGED} if the cache was registered before, {@link EChange#UNCHANGED}
   *         otherwise.
   */
  @NonNull
  public EChange unregister (@NonNull final IMutableCache <?, ?> aCache)
  {
    ValueEnforcer.notNull (aCache, "Cache");

    m_aRWLock.writeLock ().lock ();
    try
    {
      final ScheduledFuture <?> aFuture = m_aRegistrations.remove (aCache);
      if (aFuture == null)
        return EChange.UNCHANGED;

      aFuture.cancel (false);
      if (LOGGER.isDebugEnabled ())
        LOGGER.debug ("Unregistered cache '" + aCache.getName () + "'");

      if (m_aRegistrations.isEmpty () && m_aExecutor != null)
      {
        ExecutorServiceHelper.shutdownAndWaitUntilAllTasksAreFinished (m_aExecutor);
        m_aExecutor = null;
        if (LOGGER.isDebugEnabled ())
          LOGGER.debug ("Stopped cache eviction scheduler thread");
      }
      return EChange.CHANGED;
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  /**
   * Cancel all registrations and shut down the underlying executor. Called during library cleanup
   * via the SPI.
   */
  public void shutdown ()
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      for (final ScheduledFuture <?> aFuture : m_aRegistrations.values ())
        aFuture.cancel (false);
      m_aRegistrations.clear ();

      if (m_aExecutor != null)
      {
        ExecutorServiceHelper.shutdownAndWaitUntilAllTasksAreFinished (m_aExecutor);
        m_aExecutor = null;
        if (LOGGER.isDebugEnabled ())
          LOGGER.debug ("Cache eviction scheduler shut down");
      }
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }
}
