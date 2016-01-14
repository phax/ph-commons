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
package com.helger.commons.datetime;

import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.annotation.ELockType;
import com.helger.commons.annotation.IsLocked;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.Singleton;
import com.helger.commons.cache.AbstractNotifyingCache;

/**
 * This class provides a cache for {@link DateTimeFormatter} instances. It
 * caches up to a limited number of compiled {@link DateTimeFormatter} objects.
 *
 * @author Philip Helger
 */
@ThreadSafe
@Singleton
public final class DateTimeFormatterCache extends AbstractNotifyingCache <DateTimeFormatterPattern, DateTimeFormatter>
{
  private static final class SingletonHolder
  {
    static final DateTimeFormatterCache s_aInstance = new DateTimeFormatterCache ();
  }

  /** The default number of items to keep in the cache */
  public static final int MAX_CACHE_SIZE = 1000;

  private static boolean s_bDefaultInstantiated = false;

  private DateTimeFormatterCache ()
  {
    super (MAX_CACHE_SIZE, DateTimeFormatterCache.class.getName ());
  }

  public static boolean isInstantiated ()
  {
    return s_bDefaultInstantiated;
  }

  @Nonnull
  public static DateTimeFormatterCache getInstance ()
  {
    final DateTimeFormatterCache ret = SingletonHolder.s_aInstance;
    s_bDefaultInstantiated = true;
    return ret;
  }

  @Override
  @Nonnull
  @IsLocked (ELockType.WRITE)
  protected DateTimeFormatter getValueToCache (@Nonnull final DateTimeFormatterPattern aPattern)
  {
    return aPattern.getAsFormatter ();
  }

  /**
   * Get the cached DateTimeFormatter using STRICT resolving.
   *
   * @param sPattern
   *        The pattern to retrieve. May neither be <code>null</code> nor empty.
   * @return The compiled DateTimeFormatter and never <code>null</code> .
   * @throws IllegalArgumentException
   *         If the pattern is invalid
   */
  @Nonnull
  public static DateTimeFormatter getDateTimeFormatterStrict (@Nonnull @Nonempty final String sPattern)
  {
    return getDateTimeFormatter (sPattern, ResolverStyle.STRICT);
  }

  /**
   * Get the cached DateTimeFormatter using SMART resolving.
   *
   * @param sPattern
   *        The pattern to retrieve. May neither be <code>null</code> nor empty.
   * @return The compiled DateTimeFormatter and never <code>null</code> .
   * @throws IllegalArgumentException
   *         If the pattern is invalid
   */
  @Nonnull
  public static DateTimeFormatter getDateTimeFormatterSmart (@Nonnull @Nonempty final String sPattern)
  {
    return getDateTimeFormatter (sPattern, ResolverStyle.SMART);
  }

  /**
   * Get the cached DateTimeFormatter using LENIENT resolving.
   *
   * @param sPattern
   *        The pattern to retrieve. May neither be <code>null</code> nor empty.
   * @return The compiled DateTimeFormatter and never <code>null</code> .
   * @throws IllegalArgumentException
   *         If the pattern is invalid
   */
  @Nonnull
  public static DateTimeFormatter getDateTimeFormatterLenient (@Nonnull @Nonempty final String sPattern)
  {
    return getDateTimeFormatter (sPattern, ResolverStyle.LENIENT);
  }

  /**
   * Get the cached DateTimeFormatter using the provided resolver style.
   *
   * @param sPattern
   *        The pattern to retrieve. May neither be <code>null</code> nor empty.
   * @param eResolverStyle
   *        The resolver style to be used. May not be <code>null</code>.
   * @return The compiled DateTimeFormatter and never <code>null</code>.
   * @throws IllegalArgumentException
   *         If the pattern is invalid
   */
  @Nonnull
  public static DateTimeFormatter getDateTimeFormatter (@Nonnull @Nonempty final String sPattern,
                                                        @Nonnull final ResolverStyle eResolverStyle)
  {
    return getInstance ().getFromCache (new DateTimeFormatterPattern (sPattern, eResolverStyle));
  }
}
