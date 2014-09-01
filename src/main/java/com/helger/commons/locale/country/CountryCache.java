/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.locale.country;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.CGlobal;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.PresentForCodeCoverage;
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.locale.LocaleCache;
import com.helger.commons.locale.LocaleUtils;
import com.helger.commons.state.EChange;
import com.helger.commons.string.StringHelper;

/**
 * This is a global cache for country objects to avoid too many object flowing
 * around.<br>
 * This cache is application independent.
 * 
 * @author Philip Helger
 */
@ThreadSafe
public final class CountryCache
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (CountryCache.class);

  private static final ReadWriteLock s_aRWLock = new ReentrantReadWriteLock ();

  /** Contains all known countries (as ISO 3166 2-letter codes). */
  private static final Set <String> s_aCountries = new HashSet <String> ();

  static
  {
    _initialFillCache ();
  }

  @PresentForCodeCoverage
  @SuppressWarnings ("unused")
  private static final CountryCache s_aInstance = new CountryCache ();

  private CountryCache ()
  {}

  private static void _initialFillCache ()
  {
    for (final Locale aLocale : LocaleCache.getAllLocales ())
    {
      final String sCountry = aLocale.getCountry ();
      if (StringHelper.hasText (sCountry))
        addCountry (sCountry);
    }
  }

  @Nonnull
  static EChange addCountry (@Nonnull final String sCountry)
  {
    ValueEnforcer.notNull (sCountry, "Country");
    final String sValidCountry = LocaleUtils.getValidCountryCode (sCountry);
    if (sValidCountry == null)
      throw new IllegalArgumentException ("illegal country code '" + sCountry + "'");
    if (!sCountry.equals (sValidCountry))
      throw new IllegalArgumentException ("invalid casing of '" + sCountry + "'");

    s_aRWLock.writeLock ().lock ();
    try
    {
      return EChange.valueOf (s_aCountries.add (sValidCountry));
    }
    finally
    {
      s_aRWLock.writeLock ().unlock ();
    }
  }

  @Nullable
  public static Locale getCountry (@Nullable final Locale aCountry)
  {
    return aCountry == null ? null : getCountry (aCountry.getCountry ());
  }

  @Nullable
  public static Locale getCountry (@Nullable final String sCountry)
  {
    if (StringHelper.hasNoText (sCountry))
      return null;

    // Was something like "_AT" (e.g. the result of getCountry (...).toString
    // ()) passed in? -> indirect recursion
    if (sCountry.indexOf (CGlobal.LOCALE_SEPARATOR) >= 0)
      return getCountry (LocaleCache.getLocale (sCountry));

    final String sValidCountry = LocaleUtils.getValidCountryCode (sCountry);
    if (!containsCountry (sValidCountry))
      s_aLogger.warn ("Trying to retrieve unsupported country " + sCountry);
    return LocaleCache.getLocale ("", sValidCountry, "");
  }

  /**
   * @return a set with all contained countries. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static Set <String> getAllCountries ()
  {
    s_aRWLock.readLock ().lock ();
    try
    {
      return ContainerHelper.newSet (s_aCountries);
    }
    finally
    {
      s_aRWLock.readLock ().unlock ();
    }
  }

  /**
   * @return a set with all contained country locales. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static Set <Locale> getAllCountryLocales ()
  {
    final Set <Locale> ret = new HashSet <Locale> ();
    for (final String sCountry : getAllCountries ())
      ret.add (LocaleCache.getLocale ("", sCountry, ""));
    return ret;
  }

  /**
   * Check if the passed country is known.
   * 
   * @param aCountry
   *        The country to check. May be <code>null</code>.
   * @return <code>true</code> if the passed country is contained,
   *         <code>false</code> otherwise.
   */
  public static boolean containsCountry (@Nullable final Locale aCountry)
  {
    return aCountry != null && containsCountry (aCountry.getCountry ());
  }

  /**
   * Check if the passed country is known.
   * 
   * @param sCountry
   *        The country to check. May be <code>null</code>.
   * @return <code>true</code> if the passed country is contained,
   *         <code>false</code> otherwise.
   */
  public static boolean containsCountry (@Nullable final String sCountry)
  {
    if (sCountry == null)
      return false;

    final String sValidCountry = LocaleUtils.getValidCountryCode (sCountry);
    if (sValidCountry == null)
      return false;
    s_aRWLock.readLock ().lock ();
    try
    {
      return s_aCountries.contains (sValidCountry);
    }
    finally
    {
      s_aRWLock.readLock ().unlock ();
    }
  }

  /**
   * Reset the cache to the initial state.
   */
  public static void resetCache ()
  {
    s_aRWLock.writeLock ().lock ();
    try
    {
      s_aCountries.clear ();
      _initialFillCache ();
      if (s_aLogger.isDebugEnabled ())
        s_aLogger.debug ("Cache was reset: " + CountryCache.class.getName ());
    }
    finally
    {
      s_aRWLock.writeLock ().unlock ();
    }
  }
}
