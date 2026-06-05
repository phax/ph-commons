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
package com.helger.cache.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Supplier;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.CheckForSigned;
import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.OverridingMethodsMustInvokeSuper;
import com.helger.annotation.concurrent.ELockType;
import com.helger.annotation.concurrent.GuardedBy;
import com.helger.annotation.concurrent.IsLocked;
import com.helger.annotation.concurrent.MustBeLocked;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.style.CodingStyleguideUnaware;
import com.helger.annotation.style.OverrideOnDemand;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.state.EChange;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.cache.IMutableCacheWithExpiration;
import com.helger.collection.CollectionHelper;
import com.helger.collection.commons.ICommonsMap;
import com.helger.collection.map.SoftHashMap;
import com.helger.collection.map.SoftLinkedHashMap;

/**
 * A simple Map-based cache that supports an optional maximum size, optional time-based expiration,
 * statistics and active eviction. Use the explicit {@link #putInCache(Object, Object)} /
 * {@link #removeFromCache(Object)} methods to manage the contents.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        The cache key type.
 * @param <VALUETYPE>
 *        The cache value type.
 * @since 12.3.0
 */
@ThreadSafe
public abstract class AbstractMapBasedCache <KEYTYPE, VALUETYPE> extends AbstractCacheSupport implements
                                            IMutableCacheWithExpiration <KEYTYPE, VALUETYPE>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (AbstractMapBasedCache.class);

  private final int m_nMaxSize;
  private final boolean m_bAllowNullValues;
  private final Duration m_aTimeToLive;
  private final Supplier <LocalDateTime> m_aClockSupplier;
  // Status vars
  // The main cache. The presence of a CacheEntry is the marker for
  // "key is in the cache" (including cached null values). Lazily created.
  // Keys are stored as Object so that derived classes such as MappedCache can
  // transform the public key into a different storage key via getCacheKey().
  @GuardedBy ("m_aRWLock")
  private ICommonsMap <KEYTYPE, CacheEntry <VALUETYPE>> m_aMap;

  /**
   * Constructor
   *
   * @param sCacheName
   *        The internal name of the cache. May neither be <code>null</code> nor empty. This name is
   *        NOT checked for uniqueness.
   * @param nMaxSize
   *        The maximum size of the cache. All values &le; 0 indicate an unlimited size.
   * @param bAllowNullValues
   *        <code>true</code> if <code>null</code> values are allowed to be in the cache,
   *        <code>false</code> if not.
   * @param aTimeToLive
   *        Time after which a cache entry is considered expired (counted from the time it was put
   *        in the cache). May be <code>null</code> or zero or negative to disable time-based
   *        expiration.
   * @param aClockSupplier
   *        The clock supplier. May not be <code>null</code>.
   */
  protected AbstractMapBasedCache (@NonNull @Nonempty final String sCacheName,
                                   @CheckForSigned final int nMaxSize,
                                   final boolean bAllowNullValues,
                                   @Nullable final Duration aTimeToLive,
                                   @NonNull final Supplier <LocalDateTime> aClockSupplier)
  {
    super (sCacheName);
    ValueEnforcer.notNull (aClockSupplier, "ClockSupplier");

    m_nMaxSize = nMaxSize;
    m_bAllowNullValues = bAllowNullValues;
    m_aTimeToLive = aTimeToLive != null && !aTimeToLive.isZero () && !aTimeToLive.isNegative () ? aTimeToLive : null;
    m_aClockSupplier = aClockSupplier;
  }

  public final int getMaxSize ()
  {
    // No need to lock, as it is final
    return m_nMaxSize;
  }

  public final boolean hasMaxSize ()
  {
    // No need to lock, as it is final
    return m_nMaxSize > 0;
  }

  public final boolean isAllowNullValues ()
  {
    return m_bAllowNullValues;
  }

  /**
   * @return The configured time to live for cache entries, or <code>null</code> if no time-based
   *         expiration is configured.
   */
  @Nullable
  public final Duration getTimeToLive ()
  {
    return m_aTimeToLive;
  }

  /**
   * @return <code>true</code> if a positive time to live is configured, <code>false</code>
   *         otherwise.
   */
  public final boolean hasTimeToLive ()
  {
    return m_aTimeToLive != null;
  }

  /**
   * @return The clock supplier currently in use. Never <code>null</code>.
   */
  @NonNull
  public final Supplier <LocalDateTime> getClockSupplier ()
  {
    return m_aClockSupplier;
  }

  /**
   * Create a new cache map. This is the internal map that is used to store the items.
   *
   * @return Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  @OverrideOnDemand
  @CodingStyleguideUnaware
  protected ICommonsMap <KEYTYPE, CacheEntry <VALUETYPE>> createCache ()
  {
    return hasMaxSize () ? new SoftLinkedHashMap <> (m_nMaxSize) : new SoftHashMap <> ();
  }

  @NonNull
  @Nonempty
  protected String getCacheLogText ()
  {
    final StringBuilder ret = new StringBuilder ("Cache '").append (getName ()).append ("'");
    if (hasMaxSize ())
      ret.append (" with max size of ").append (m_nMaxSize);
    return ret.append (": ").toString ();
  }

  /**
   * Put a new entry into the cache. The caller must hold the write lock.
   *
   * @param aKey
   *        The storage key. May not be <code>null</code>.
   * @param aCacheEntry
   *        The cache entry. May not be <code>null</code>.
   */
  @MustBeLocked (ELockType.WRITE)
  protected final void putInCacheNotLocked (@NonNull final KEYTYPE aKey,
                                            @NonNull final CacheEntry <VALUETYPE> aCacheEntry)
  {
    ValueEnforcer.notNull (aKey, "Key");
    ValueEnforcer.notNull (aCacheEntry, "CacheEntry");

    if (m_aMap == null)
    {
      // Lazily create a new map to cache the objects
      m_aMap = createCache ();
      if (m_aMap == null)
        throw new IllegalStateException (getCacheLogText () + "Failed to create internal Map!");
    }
    m_aMap.put (aKey, aCacheEntry);
    incCacheAdd ();
  }

  /**
   * Validate that the provided value is allowed by the null-value policy. Throws otherwise.
   *
   * @param aKey
   *        The public-facing key. Used only for error reporting.
   * @param aValue
   *        The value to check.
   */
  private void _checkNullValuePolicy (final KEYTYPE aKey, final VALUETYPE aValue)
  {
    if (aValue == null && !m_bAllowNullValues)
      throw new IllegalStateException (getCacheLogText () +
                                       "The cache value of key '" +
                                       aKey +
                                       "' is null. null values are not allowed in this cache.");
  }

  /**
   * Build a {@link CacheEntry} for the provided value, honoring the configured null-value policy
   * and the cache-wide time-to-live.
   *
   * @param aKey
   *        The public-facing key. Used only for error reporting.
   * @param aValue
   *        The value to wrap. May be <code>null</code> only if {@link #isAllowNullValues()}.
   * @return Never <code>null</code>.
   */
  @NonNull
  protected final CacheEntry <VALUETYPE> buildCacheEntry (final KEYTYPE aKey, final VALUETYPE aValue)
  {
    _checkNullValuePolicy (aKey, aValue);
    if (m_aTimeToLive == null)
      return CacheEntry.ofNoExpiration (aValue);
    return CacheEntry.ofTimeToLive (aValue, m_aClockSupplier.get (), m_aTimeToLive);
  }

  /**
   * Build a {@link CacheEntry} for the provided value using an explicit per-entry time-to-live.
   * Honors the null-value policy but ignores the cache-wide TTL.
   *
   * @param aKey
   *        The public-facing key. Used only for error reporting.
   * @param aValue
   *        The value to wrap. May be <code>null</code> only if {@link #isAllowNullValues()}.
   * @param aTimeToLive
   *        The per-entry time to live. May not be <code>null</code> and must be strictly positive.
   * @return Never <code>null</code>.
   */
  @NonNull
  protected final CacheEntry <VALUETYPE> buildCacheEntry (final KEYTYPE aKey,
                                                          final VALUETYPE aValue,
                                                          @NonNull final Duration aTimeToLive)
  {
    _checkNullValuePolicy (aKey, aValue);
    return CacheEntry.ofTimeToLive (aValue, m_aClockSupplier.get (), aTimeToLive);
  }

  /**
   * Build a {@link CacheEntry} for the provided value using an explicit per-entry expiration date
   * time. Honors the null-value policy but ignores the cache-wide TTL.
   *
   * @param aKey
   *        The public-facing key. Used only for error reporting.
   * @param aValue
   *        The value to wrap. May be <code>null</code> only if {@link #isAllowNullValues()}.
   * @param aExpirationDT
   *        The per-entry expiration date time. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  protected final CacheEntry <VALUETYPE> buildCacheEntry (final KEYTYPE aKey,
                                                          final VALUETYPE aValue,
                                                          @NonNull final LocalDateTime aExpirationDT)
  {
    _checkNullValuePolicy (aKey, aValue);
    return CacheEntry.ofExpirationDateTime (aValue, aExpirationDT);
  }

  public void putInCache (final KEYTYPE aKey, final VALUETYPE aValue)
  {
    final CacheEntry <VALUETYPE> aCacheEntry = buildCacheEntry (aKey, aValue);
    m_aRWLock.writeLocked ( () -> putInCacheNotLocked (aKey, aCacheEntry));
  }

  public void putInCache (final KEYTYPE aKey, final VALUETYPE aValue, @NonNull final Duration aTimeToLive)
  {
    final CacheEntry <VALUETYPE> aCacheEntry = buildCacheEntry (aKey, aValue, aTimeToLive);
    m_aRWLock.writeLocked ( () -> putInCacheNotLocked (aKey, aCacheEntry));
  }

  public void putInCache (final KEYTYPE aKey, final VALUETYPE aValue, @NonNull final LocalDateTime aExpirationDT)
  {
    final CacheEntry <VALUETYPE> aCacheEntry = buildCacheEntry (aKey, aValue, aExpirationDT);
    m_aRWLock.writeLocked ( () -> putInCacheNotLocked (aKey, aCacheEntry));
  }

  /**
   * Look up the raw {@link CacheEntry} for the provided storage key without updating statistics.
   * The caller must hold the read lock.
   *
   * @param aKey
   *        The storage key. May be <code>null</code>.
   * @return The cache entry, or <code>null</code> if not present.
   */
  @Nullable
  @MustBeLocked (ELockType.READ)
  protected final CacheEntry <VALUETYPE> getFromCacheNoStatsNotLocked (@Nullable final KEYTYPE aKey)
  {
    return m_aMap == null ? null : m_aMap.get (aKey);
  }

  /**
   * Look up the raw {@link CacheEntry} for the provided storage key without updating statistics.
   *
   * @param aKey
   *        The storage key. May be <code>null</code>.
   * @return The cache entry, or <code>null</code> if not present.
   */
  @Nullable
  @IsLocked (ELockType.READ)
  protected final CacheEntry <VALUETYPE> getFromCacheNoStats (@Nullable final KEYTYPE aKey)
  {
    // null cache keys can never be in the cache
    if (aKey == null)
      return null;

    // Unroll for maximum performance
    m_aRWLock.readLock ().lock ();
    try
    {
      return getFromCacheNoStatsNotLocked (aKey);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  /**
   * @param aCacheEntry
   *        The entry to check. May be <code>null</code>.
   * @return <code>true</code> if the entry exists, carries an expiration timestamp (set either by
   *         the cache-wide TTL or per-entry via the explicit putInCache overloads) and that
   *         timestamp has already passed.
   */
  protected final boolean isExpired (@Nullable final CacheEntry <VALUETYPE> aCacheEntry)
  {
    return aCacheEntry != null &&
           aCacheEntry.hasExpirationDateTime () &&
           aCacheEntry.isExpiredAt (m_aClockSupplier.get ());
  }

  /**
   * Remove an entry that has been determined to be expired. The caller must hold the write lock.
   * Increments the expired-entries counter.
   *
   * @param aKey
   *        The original public key. Used only for logging.
   */
  @MustBeLocked (ELockType.WRITE)
  protected final void removeExpiredEntryNotLocked (final KEYTYPE aKey)
  {
    if (m_aMap != null)
    {
      m_aMap.remove (aKey);
      incCacheExpired ();
      if (LOGGER.isDebugEnabled ())
        LOGGER.debug (getCacheLogText () + "Cache key '" + aKey + "' expired and was removed.");
    }
  }

  /**
   * Check if the passed key is already in the cache or not. An entry that is past its time-based
   * expiration is considered as <em>not</em> in the cache for the purposes of this check.
   *
   * @param aKey
   *        The key to check. May be <code>null</code>.
   * @return <code>true</code> if the value is already in the cache, <code>false</code> if not.
   */
  public final boolean isInCache (final KEYTYPE aKey)
  {
    // Determine the internal key - may resolve to null when aKey is null and the
    // subclass key provider tolerates that.
    final CacheEntry <VALUETYPE> aEntry = getFromCacheNoStats (aKey);
    if (aEntry == null)
      return false;

    if (isExpired (aEntry))
      return false;

    return true;
  }

  /**
   * Get a value from the cache. Returns <code>null</code> if the key is not in the cache or its
   * entry has expired. Subclasses (notably {@link MappedCache}) may override this method to
   * automatically resolve missing values via a value provider.
   *
   * @param aKey
   *        The key to look up.
   * @return The cached value, or <code>null</code> if absent or expired.
   */
  @Nullable
  public VALUETYPE getFromCache (final KEYTYPE aKey)
  {
    final CacheEntry <VALUETYPE> aCacheEntry = getFromCacheNoStats (aKey);
    if (aCacheEntry == null)
    {
      incCacheMiss ();
      return null;
    }

    if (isExpired (aCacheEntry))
    {
      // Drop the expired entry
      m_aRWLock.writeLocked ( () -> {
        // Check again in write lock
        final CacheEntry <VALUETYPE> aCurrent = getFromCacheNoStatsNotLocked (aKey);
        if (aCurrent != null && isExpired (aCurrent))
          removeExpiredEntryNotLocked (aKey);
      });
      incCacheMiss ();
      return null;
    }

    incCacheHit ();
    return aCacheEntry.getValue ();
  }

  @NonNull
  @OverridingMethodsMustInvokeSuper
  public EChange removeFromCache (final KEYTYPE aKey)
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      if (m_aMap == null || m_aMap.remove (aKey) == null)
        return EChange.UNCHANGED;
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }

    incCacheRemove ();
    if (LOGGER.isDebugEnabled ())
      LOGGER.debug (getCacheLogText () + "Cache key '" + aKey + "' was removed.");
    return EChange.CHANGED;
  }

  /**
   * Remove all entries from the cache.
   *
   * @return {@link EChange#CHANGED} if at least one entry was removed, {@link EChange#UNCHANGED} if
   *         the cache was already empty.
   */
  @NonNull
  @OverridingMethodsMustInvokeSuper
  public EChange clearCache ()
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      if (m_aMap == null || m_aMap.isEmpty ())
        return EChange.UNCHANGED;

      m_aMap.clear ();
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }

    incCacheClear ();
    if (LOGGER.isDebugEnabled ())
      LOGGER.debug (getCacheLogText () + "Cache was cleared");
    return EChange.CHANGED;
  }

  /**
   * Remove all entries whose per-entry expiration timestamp has passed. Honors both the cache-wide
   * TTL and any per-entry expirations supplied via the explicit
   * {@link #putInCache(Object, Object, Duration)} /
   * {@link #putInCache(Object, Object, LocalDateTime)} overloads. For caches that contain no
   * expiring entries this is effectively a no-op returning <code>0</code>.
   *
   * @return The number of entries that were removed. Always &ge; 0.
   */
  @Nonnegative
  @OverridingMethodsMustInvokeSuper
  public int evictExpired ()
  {
    int nRemoved = 0;
    m_aRWLock.writeLock ().lock ();
    try
    {
      if (m_aMap != null && m_aMap.isNotEmpty ())
      {
        final LocalDateTime aNow = m_aClockSupplier.get ();
        final var it = m_aMap.entrySet ().iterator ();
        while (it.hasNext ())
        {
          final var aMapEntry = it.next ();
          if (aMapEntry.getValue ().isExpiredAt (aNow))
          {
            it.remove ();
            nRemoved++;
            incCacheExpired ();
          }
        }
      }
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }

    if (nRemoved > 0)
    {
      if (LOGGER.isDebugEnabled ())
        LOGGER.debug (getCacheLogText () + nRemoved + " expired entries were removed.");
    }
    return nRemoved;
  }

  /**
   * @return The number of entries currently in the cache. Always &ge; 0.
   */
  @Nonnegative
  public int size ()
  {
    return m_aRWLock.readLockedInt ( () -> CollectionHelper.getSize (m_aMap));
  }

  /**
   * @return <code>true</code> if the cache contains no entries, <code>false</code> if it contains
   *         at least one entry.
   */
  public boolean isEmpty ()
  {
    return m_aRWLock.readLockedBoolean ( () -> CollectionHelper.isEmpty (m_aMap));
  }

  /**
   * @return <code>true</code> if the cache contains at least one entry, <code>false</code> if it is
   *         empty.
   */
  @Override
  public boolean isNotEmpty ()
  {
    return m_aRWLock.readLockedBoolean ( () -> CollectionHelper.isNotEmpty (m_aMap));
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("MaxSize", m_nMaxSize)
                            .append ("AllowNullValues", m_bAllowNullValues)
                            .appendIfNotNull ("TimeToLive", m_aTimeToLive)
                            .append ("ClockSupplier", m_aClockSupplier)
                            .append ("Map", m_aMap)
                            .getToString ();
  }
}
