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
package com.helger.commons.locale.country;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.CGlobal;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.Singleton;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.locale.LocaleCache;
import com.helger.commons.locale.LocaleHelper;
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
@Singleton
public final class CountryCache
{
  private static final class SingletonHolder
  {
    static final CountryCache s_aInstance = new CountryCache ();
  }

  private static final Logger s_aLogger = LoggerFactory.getLogger (CountryCache.class);

  private static boolean s_bDefaultInstantiated = false;

  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();

  /** Contains all known countries (as ISO 3166 2-letter codes). */
  private final Set <String> m_aCountries = new HashSet <String> ();

  private CountryCache ()
  {
    reinitialize ();
  }

  public static boolean isInstantiated ()
  {
    return s_bDefaultInstantiated;
  }

  @Nonnull
  public static CountryCache getInstance ()
  {
    final CountryCache ret = SingletonHolder.s_aInstance;
    s_bDefaultInstantiated = true;
    return ret;
  }

  @Nonnull
  EChange addCountry (@Nonnull final String sCountry)
  {
    ValueEnforcer.notNull (sCountry, "Country");
    final String sValidCountry = LocaleHelper.getValidCountryCode (sCountry);
    if (sValidCountry == null)
      throw new IllegalArgumentException ("illegal country code '" + sCountry + "'");
    if (!sCountry.equals (sValidCountry))
      throw new IllegalArgumentException ("invalid casing of '" + sCountry + "'");

    return EChange.valueOf (m_aRWLock.writeLocked ( () -> m_aCountries.add (sValidCountry)));
  }

  @Nullable
  public Locale getCountry (@Nullable final Locale aCountry)
  {
    return aCountry == null ? null : getCountry (aCountry.getCountry ());
  }

  @Nullable
  public Locale getCountry (@Nullable final String sCountry)
  {
    if (StringHelper.hasNoText (sCountry))
      return null;

    // Was something like "_AT" (e.g. the result of getCountry (...).toString
    // ()) passed in? -> indirect recursion
    if (sCountry.indexOf (CGlobal.LOCALE_SEPARATOR) >= 0)
      return getCountry (LocaleCache.getInstance ().getLocale (sCountry));

    final String sValidCountry = LocaleHelper.getValidCountryCode (sCountry);
    if (!containsCountry (sValidCountry))
      s_aLogger.warn ("Trying to retrieve unsupported country " + sCountry);
    return LocaleCache.getInstance ().getLocale ("", sValidCountry, "");
  }

  /**
   * @return a set with all contained countries. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public Set <String> getAllCountries ()
  {
    return m_aRWLock.readLocked ( () -> CollectionHelper.newSet (m_aCountries));
  }

  /**
   * @return a set with all contained country locales. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public Set <Locale> getAllCountryLocales ()
  {
    final Set <Locale> ret = new HashSet <Locale> ();
    for (final String sCountry : getAllCountries ())
      ret.add (LocaleCache.getInstance ().getLocale ("", sCountry, ""));
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
  public boolean containsCountry (@Nullable final Locale aCountry)
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
  public boolean containsCountry (@Nullable final String sCountry)
  {
    if (sCountry == null)
      return false;

    final String sValidCountry = LocaleHelper.getValidCountryCode (sCountry);
    if (sValidCountry == null)
      return false;
    return m_aRWLock.readLocked ( () -> m_aCountries.contains (sValidCountry));
  }

  /**
   * Reset the cache to the initial state.
   */
  public void reinitialize ()
  {
    m_aRWLock.writeLocked ( () -> m_aCountries.clear ());

    for (final Locale aLocale : LocaleCache.getInstance ().getAllLocales ())
    {
      final String sCountry = aLocale.getCountry ();
      if (StringHelper.hasText (sCountry))
        addCountry (sCountry);
    }

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("Reinitialized " + CountryCache.class.getName ());
  }
}
