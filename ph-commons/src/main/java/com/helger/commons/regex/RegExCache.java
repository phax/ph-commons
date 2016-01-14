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
package com.helger.commons.regex;

import java.util.regex.Pattern;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.RegEx;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.annotation.ELockType;
import com.helger.commons.annotation.IsLocked;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.Singleton;
import com.helger.commons.cache.AbstractNotifyingCache;

/**
 * This class provides a cached for compiled regular expressions. It caches up
 * to a limited number of compiled {@link Pattern} objects.
 *
 * @author Philip Helger
 */
@ThreadSafe
@Singleton
public final class RegExCache extends AbstractNotifyingCache <RegExPattern, Pattern>
{
  private static final class SingletonHolder
  {
    static final RegExCache s_aInstance = new RegExCache ();
  }

  /** The default number of items to keep in the cache */
  public static final int MAX_CACHE_SIZE = 1000;

  private static boolean s_bDefaultInstantiated = false;

  private RegExCache ()
  {
    super (MAX_CACHE_SIZE, RegExCache.class.getName ());
  }

  public static boolean isInstantiated ()
  {
    return s_bDefaultInstantiated;
  }

  @Nonnull
  public static RegExCache getInstance ()
  {
    final RegExCache ret = SingletonHolder.s_aInstance;
    s_bDefaultInstantiated = true;
    return ret;
  }

  @Override
  @Nonnull
  @IsLocked (ELockType.WRITE)
  protected Pattern getValueToCache (@Nonnull final RegExPattern aRegEx)
  {
    return aRegEx.getAsPattern ();
  }

  /**
   * Get the cached regular expression pattern.
   *
   * @param sRegEx
   *        The regular expression to retrieve. May neither be <code>null</code>
   *        nor empty.
   * @return The compiled regular expression pattern and never <code>null</code>
   *         .
   * @throws IllegalArgumentException
   *         If the passed regular expression has an illegal syntax
   */
  @Nonnull
  public static Pattern getPattern (@Nonnull @Nonempty @RegEx final String sRegEx)
  {
    return getInstance ().getFromCache (new RegExPattern (sRegEx));
  }

  /**
   * Get the cached regular expression pattern.
   *
   * @param sRegEx
   *        The regular expression to retrieve. May neither be <code>null</code>
   *        nor empty.
   * @param nOptions
   *        The options used for Pattern.compile
   * @return The compiled regular expression pattern and never <code>null</code>
   *         .
   * @see Pattern#compile(String, int)
   * @throws IllegalArgumentException
   *         If the passed regular expression has an illegal syntax
   */
  @Nonnull
  public static Pattern getPattern (@Nonnull @Nonempty @RegEx final String sRegEx, @Nonnegative final int nOptions)
  {
    return getInstance ().getFromCache (new RegExPattern (sRegEx, nOptions));
  }
}
