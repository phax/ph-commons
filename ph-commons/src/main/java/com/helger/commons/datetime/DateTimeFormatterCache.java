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
package com.helger.commons.datetime;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

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
public final class DateTimeFormatterCache extends AbstractNotifyingCache <String, DateTimeFormatter>
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
  protected DateTimeFormatter getValueToCache (@Nonnull final String sPattern)
  {
    return new DateTimeFormatterBuilder ().appendPattern (sPattern).parseStrict ().toFormatter ();
  }

  /**
   * Get the cached DateTimeFormatter.
   *
   * @param sPattern
   *        The pattern to retrieve. May neither be <code>null</code> nor empty.
   * @return The compiled DateTimeFormatter and never <code>null</code> .
   * @throws IllegalArgumentException
   *         If the pattern is invalid
   */
  @Nonnull
  public static DateTimeFormatter getDateTimeFormatter (@Nonnull @Nonempty final String sPattern)
  {
    return getInstance ().getFromCache (sPattern);
  }
}
