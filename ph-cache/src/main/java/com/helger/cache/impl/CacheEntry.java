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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;

/**
 * Internal cache value holder that combines the cached value with an optional
 * expiration date time. A <code>null</code> expiration means the entry never
 * expires by time. The presence of a {@link CacheEntry} in the internal cache
 * map represents "the key is in the cache" (independently of whether the
 * contained value is <code>null</code>).
 * <p>
 * This type mirrors the shape of
 * <code>com.helger.datetime.expiration.ExpiringObject</code> but is kept inside
 * ph-cache to avoid a module dependency cycle with ph-datetime.
 *
 * @author Philip Helger
 * @param <VALUETYPE>
 *        The cache value type.
 * @since 12.3.0
 */
@Immutable
public final class CacheEntry <VALUETYPE>
{
  private final VALUETYPE m_aValue;
  private final LocalDateTime m_aExpirationDT;

  /**
   * Constructor.
   *
   * @param aValue
   *        The cached value. May be <code>null</code>.
   * @param aExpirationDT
   *        The expiration date time. May be <code>null</code> if the entry
   *        never expires by time.
   */
  public CacheEntry (@Nullable final VALUETYPE aValue, @Nullable final LocalDateTime aExpirationDT)
  {
    m_aValue = aValue;
    m_aExpirationDT = aExpirationDT;
  }

  /**
   * @return The cached value. May be <code>null</code>.
   */
  @Nullable
  public VALUETYPE getValue ()
  {
    return m_aValue;
  }

  /**
   * @return The expiration date time of this entry, or <code>null</code> if no
   *         expiration is defined.
   */
  @Nullable
  public LocalDateTime getExpirationDateTime ()
  {
    return m_aExpirationDT;
  }

  /**
   * @return <code>true</code> if an expiration date time is defined,
   *         <code>false</code> otherwise.
   */
  public boolean hasExpirationDateTime ()
  {
    return m_aExpirationDT != null;
  }

  /**
   * @param aDT
   *        The date time to check against. May not be <code>null</code>.
   * @return <code>true</code> if an expiration date time is defined and the
   *         provided date time is after the expiration date time,
   *         <code>false</code> otherwise.
   */
  public boolean isExpiredAt (@NonNull final LocalDateTime aDT)
  {
    ValueEnforcer.notNull (aDT, "DT");
    return m_aExpirationDT != null && aDT.isAfter (m_aExpirationDT);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("Value", m_aValue).appendIfNotNull ("ExpirationDT", m_aExpirationDT).getToString ();
  }

  /**
   * Create a new {@link CacheEntry} that never expires by time.
   *
   * @param <VALUETYPE>
   *        The cache value type.
   * @param aValue
   *        The cached value. May be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static <VALUETYPE> CacheEntry <VALUETYPE> ofNoExpiration (@Nullable final VALUETYPE aValue)
  {
    return new CacheEntry <> (aValue, null);
  }

  /**
   * Create a new {@link CacheEntry} that expires at the provided date time.
   *
   * @param <VALUETYPE>
   *        The cache value type.
   * @param aValue
   *        The cached value. May be <code>null</code>.
   * @param aExpirationDT
   *        The expiration date time. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static <VALUETYPE> CacheEntry <VALUETYPE> ofExpirationDateTime (@Nullable final VALUETYPE aValue,
                                                                         @NonNull final LocalDateTime aExpirationDT)
  {
    ValueEnforcer.notNull (aExpirationDT, "ExpirationDT");
    return new CacheEntry <> (aValue, aExpirationDT);
  }

  /**
   * Create a new {@link CacheEntry} that expires after the provided duration
   * starting from the provided "now".
   *
   * @param <VALUETYPE>
   *        The cache value type.
   * @param aValue
   *        The cached value. May be <code>null</code>.
   * @param aNow
   *        The current date time. May not be <code>null</code>.
   * @param aTimeToLive
   *        The validity duration. Must be positive and not <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static <VALUETYPE> CacheEntry <VALUETYPE> ofTimeToLive (@Nullable final VALUETYPE aValue,
                                                                 @NonNull final LocalDateTime aNow,
                                                                 @NonNull final Duration aTimeToLive)
  {
    ValueEnforcer.notNull (aNow, "Now");
    ValueEnforcer.notNull (aTimeToLive, "TimeToLive");
    ValueEnforcer.isFalse (aTimeToLive::isNegative, "TimeToLive must not be negative");
    return new CacheEntry <> (aValue, aNow.plus (aTimeToLive));
  }
}
