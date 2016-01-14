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
package com.helger.commons.locale;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.CGlobal;
import com.helger.commons.annotation.ELockType;
import com.helger.commons.annotation.MustBeLocked;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.Singleton;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.string.StringHelper;

/**
 * This is a global cache for Locale objects to avoid too many object flowing
 * around.<br>
 * This cache is application independent.
 *
 * @author Philip Helger
 */
@ThreadSafe
@Singleton
public final class LocaleCache
{
  private static final class SingletonHolder
  {
    static final LocaleCache s_aInstance = new LocaleCache ();
  }

  private static final Logger s_aLogger = LoggerFactory.getLogger (LocaleCache.class);
  private static boolean s_bDefaultInstantiated = false;

  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();

  /** maps a string to a locale. */
  @GuardedBy ("m_aRWLock")
  private final Map <String, Locale> m_aLocales = new HashMap <String, Locale> ();

  private LocaleCache ()
  {
    reinitialize ();
  }

  public static boolean isInstantiated ()
  {
    return s_bDefaultInstantiated;
  }

  @Nonnull
  public static LocaleCache getInstance ()
  {
    final LocaleCache ret = SingletonHolder.s_aInstance;
    s_bDefaultInstantiated = true;
    return ret;
  }

  /**
   * Get the {@link Locale} object matching the given language.
   *
   * @param sLanguage
   *        The language to use. May be <code>null</code> or empty.
   * @return <code>null</code> if the passed language string is
   *         <code>null</code> or empty
   */
  @Nullable
  public Locale getLocale (@Nullable final String sLanguage)
  {
    if (sLanguage != null && sLanguage.length () > 2)
    {
      // parse
      final String [] aParts = StringHelper.getExplodedArray (CGlobal.LOCALE_SEPARATOR, sLanguage, 3);
      if (aParts.length == 3)
        return getLocale (aParts[0], aParts[1], aParts[2]);
      if (aParts.length == 2)
        return getLocale (aParts[0], aParts[1], "");
      // else fall through
    }
    return getLocale (sLanguage, "", "");
  }

  /**
   * Get the {@link Locale} object matching the given language and country.
   *
   * @param sLanguage
   *        The language to use. May be <code>null</code> or empty.
   * @param sCountry
   *        The country to use. May be <code>null</code>.
   * @return <code>null</code> if the passed language string is
   *         <code>null</code> or empty
   */
  @Nullable
  public Locale getLocale (@Nullable final String sLanguage, @Nullable final String sCountry)
  {
    return getLocale (sLanguage, sCountry, "");
  }

  /**
   * Build the locale key internally used. Note: this is not the same string as
   * returned by {@link Locale#toString()}!!
   *
   * @param sLanguage
   *        Language to use
   * @param sCountry
   *        Country to use
   * @param sVariant
   *        Variant to use
   * @return String
   */
  @Nonnull
  private static String _buildLocaleString (@Nonnull final String sLanguage,
                                            @Nonnull final String sCountry,
                                            @Nonnull final String sVariant)
  {
    final StringBuilder aLocaleSB = new StringBuilder ();
    if (sLanguage.length () > 0)
      aLocaleSB.append (sLanguage);
    if (sCountry.length () > 0)
      aLocaleSB.append (CGlobal.LOCALE_SEPARATOR).append (sCountry);
    if (sVariant.length () > 0)
      aLocaleSB.append (CGlobal.LOCALE_SEPARATOR).append (sVariant);
    return aLocaleSB.toString ();
  }

  /**
   * Get the {@link Locale} object matching the given locale string
   *
   * @param sLanguage
   *        The language to use. May be <code>null</code> or empty.
   * @param sCountry
   *        Optional country to use. May be <code>null</code>.
   * @param sVariant
   *        Optional variant. May be <code>null</code>.
   * @return <code>null</code> if all the passed parameters are
   *         <code>null</code> or empty
   */
  @Nullable
  public Locale getLocale (@Nullable final String sLanguage,
                           @Nullable final String sCountry,
                           @Nullable final String sVariant)
  {
    final String sRealLanguage = StringHelper.getNotNull (LocaleHelper.getValidLanguageCode (sLanguage));
    final String sRealCountry = StringHelper.getNotNull (LocaleHelper.getValidCountryCode (sCountry));
    final String sRealVariant = StringHelper.getNotNull (sVariant);
    final String sLocaleKey = _buildLocaleString (sRealLanguage, sRealCountry, sRealVariant);
    if (sLocaleKey.length () == 0)
      return null;

    // try to resolve locale
    Locale aLocale = m_aRWLock.readLocked ( () -> m_aLocales.get (sLocaleKey));
    if (aLocale == null)
    {
      aLocale = m_aRWLock.writeLocked ( () -> {
        // Try fetching again in writeLock
        Locale aWLocale = m_aLocales.get (sLocaleKey);
        if (aWLocale == null)
        {
          // not yet in cache, create a new one
          // -> may lead to illegal locales, but simpler than the error handling
          // for all the possible illegal values
          aWLocale = new Locale (sRealLanguage, sRealCountry, sRealVariant);
          m_aLocales.put (sLocaleKey, aWLocale);
        }
        return aWLocale;
      });
    }
    return aLocale;
  }

  /**
   * Get all contained locales except the locales "all" and "independent"
   *
   * @return a set with all contained locales, except "all" and "independent"
   */
  @Nonnull
  @ReturnsMutableCopy
  public Set <Locale> getAllLocales ()
  {
    return m_aRWLock.readLocked ( () -> {
      final Set <Locale> ret = CollectionHelper.newSet (m_aLocales.values ());
      ret.remove (CGlobal.LOCALE_ALL);
      ret.remove (CGlobal.LOCALE_INDEPENDENT);
      return ret;
    });
  }

  /**
   * Get all contained locales that consist only of a non-empty language.
   *
   * @return a set with all contained languages, except "all" and "independent"
   */
  @Nonnull
  @ReturnsMutableCopy
  public Set <Locale> getAllLanguages ()
  {
    final Set <Locale> ret = new HashSet <Locale> ();
    for (final Locale aLocale : getAllLocales ())
    {
      final String sLanguage = aLocale.getLanguage ();
      if (StringHelper.hasText (sLanguage))
        ret.add (getLocale (sLanguage, null, null));
    }
    return ret;
  }

  /**
   * Check if the passed language is in the cache.
   *
   * @param sLanguage
   *        The language to check.
   * @return <code>true</code> if it is in the cache, <code>false</code>
   *         otherwise.
   */
  public boolean containsLocale (@Nullable final String sLanguage)
  {
    if (sLanguage != null && sLanguage.length () > 2)
    {
      // parse
      final String [] aParts = StringHelper.getExplodedArray (CGlobal.LOCALE_SEPARATOR, sLanguage, 3);
      if (aParts.length == 3)
        return containsLocale (aParts[0], aParts[1], aParts[2]);
      if (aParts.length == 2)
        return containsLocale (aParts[0], aParts[1], "");
      // else fall through
    }
    return containsLocale (sLanguage, "", "");
  }

  /**
   * Check if the passed language is in the cache.
   *
   * @param sLanguage
   *        The language to check.
   * @param sCountry
   *        The country to check.
   * @return <code>true</code> if it is in the cache, <code>false</code>
   *         otherwise.
   */
  public boolean containsLocale (@Nullable final String sLanguage, @Nullable final String sCountry)
  {
    return containsLocale (sLanguage, sCountry, "");
  }

  @Nonnull
  private static String _createLocaleKey (@Nullable final String sLanguage,
                                          @Nullable final String sCountry,
                                          @Nullable final String sVariant)
  {
    final String sRealLanguage = StringHelper.getNotNull (LocaleHelper.getValidLanguageCode (sLanguage));
    final String sRealCountry = StringHelper.getNotNull (LocaleHelper.getValidCountryCode (sCountry));
    final String sRealVariant = StringHelper.getNotNull (sVariant);
    return _buildLocaleString (sRealLanguage, sRealCountry, sRealVariant);
  }

  /**
   * Check if the passed language is in the cache.
   *
   * @param sLanguage
   *        The language to check.
   * @param sCountry
   *        The country to check.
   * @param sVariant
   *        The variant to check.
   * @return <code>true</code> if it is in the cache, <code>false</code>
   *         otherwise.
   */
  public boolean containsLocale (@Nullable final String sLanguage,
                                 @Nullable final String sCountry,
                                 @Nullable final String sVariant)
  {
    final String sLocaleKey = _createLocaleKey (sLanguage, sCountry, sVariant);
    if (sLocaleKey.length () == 0)
      return false;
    return m_aRWLock.readLocked ( () -> m_aLocales.containsKey (sLocaleKey));
  }

  @MustBeLocked (ELockType.WRITE)
  private void _initialAdd (@Nonnull final Locale aLocale)
  {
    m_aLocales.put (aLocale.toString (), aLocale);
  }

  /**
   * Reset the cache to the initial state.
   */
  public void reinitialize ()
  {
    m_aRWLock.writeLocked ( () -> {
      m_aLocales.clear ();

      // add pseudo locales
      _initialAdd (CGlobal.LOCALE_ALL);
      _initialAdd (CGlobal.LOCALE_INDEPENDENT);

      // add all predefined languages
      for (final Locale aLocale : Locale.getAvailableLocales ())
        _initialAdd (aLocale);

      // http://forums.sun.com/thread.jspa?threadID=525482&tstart=1411
      for (final String sCountry : Locale.getISOCountries ())
        _initialAdd (new Locale ("", sCountry));
      for (final String sLanguage : Locale.getISOLanguages ())
        _initialAdd (new Locale (sLanguage, ""));
    });

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("Reinitialized " + LocaleCache.class.getName ());
  }
}
