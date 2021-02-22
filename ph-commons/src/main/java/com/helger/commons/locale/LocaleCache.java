/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.Singleton;
import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.CommonsLinkedHashSet;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.collection.impl.ICommonsOrderedSet;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.debug.GlobalDebug;
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
public class LocaleCache
{
  /**
   * Internal interface for a callback handler to be invoked, if a non-existing
   * locale is found.
   *
   * @author Philip Helger
   * @since 9.3.9
   */
  @FunctionalInterface
  public static interface IMissingLocaleHandler
  {
    /**
     * Called if a Locale is not yet present.
     *
     * @param sLocaleKey
     *        The key for the internal map. Never <code>null</code>.
     * @param sLanguage
     *        Language to use. Never <code>null</code>.
     * @param sCountry
     *        Country to use. Never <code>null</code>.
     * @param sVariant
     *        variant to use. Never <code>null</code>.
     * @return The created Locale or <code>null</code>.
     */
    @Nullable
    Locale onMissingLocale (@Nonnull String sLocaleKey, @Nonnull String sLanguage, @Nonnull String sCountry, @Nonnull String sVariant);
  }

  private static final class SingletonHolder
  {
    private static final LocaleCache INSTANCE = new LocaleCache ();
  }

  private static final Logger LOGGER = LoggerFactory.getLogger (LocaleCache.class);
  private static final AtomicBoolean SILENT_MODE = new AtomicBoolean (GlobalDebug.DEFAULT_SILENT_MODE);

  private static boolean s_bDefaultInstantiated = false;

  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();

  /** maps a string to a locale. */
  @GuardedBy ("m_aRWLock")
  private final ICommonsOrderedMap <String, Locale> m_aLocales = new CommonsLinkedHashMap <> ();

  private final IMissingLocaleHandler m_aMissingLocaleHandlerInsert = (sLocaleKey, l, c, v) -> {
    // Insert in write lock
    if (StringHelper.hasNoText (sLocaleKey))
      return null;
    return m_aRWLock.writeLockedGet ( () -> m_aLocales.computeIfAbsent (sLocaleKey, k -> new Locale (l, c, v)));
  };

  protected LocaleCache ()
  {
    reinitialize ();
  }

  /**
   * @return <code>true</code> if logging is disabled, <code>false</code> if it
   *         is enabled.
   * @since 9.4.0
   */
  public static boolean isSilentMode ()
  {
    return SILENT_MODE.get ();
  }

  /**
   * Enable or disable certain regular log messages.
   *
   * @param bSilentMode
   *        <code>true</code> to disable logging, <code>false</code> to enable
   *        logging
   * @return The previous value of the silent mode.
   * @since 9.4.0
   */
  public static boolean setSilentMode (final boolean bSilentMode)
  {
    return SILENT_MODE.getAndSet (bSilentMode);
  }

  public static boolean isInstantiated ()
  {
    return s_bDefaultInstantiated;
  }

  @Nonnull
  public static LocaleCache getInstance ()
  {
    final LocaleCache ret = SingletonHolder.INSTANCE;
    s_bDefaultInstantiated = true;
    return ret;
  }

  /**
   * @return The {@link IMissingLocaleHandler} implementation of this instance
   *         that adds a missing locale to the set. Never <code>null</code>.
   * @since 9.4.2
   */
  @Nonnull
  public final IMissingLocaleHandler getDefaultMissingLocaleHandler ()
  {
    return m_aMissingLocaleHandlerInsert;
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
    return getLocaleExt (sLanguage, m_aMissingLocaleHandlerInsert);
  }

  /**
   * Get the {@link Locale} object matching the given language.
   *
   * @param sLanguage
   *        The language to use. May be <code>null</code> or empty.
   * @param aMissingHandler
   *        An optional handler to be invoked if the provided locale is not yet
   *        contained. May be <code>null</code>.
   * @return <code>null</code> if the passed language string is
   *         <code>null</code> or empty
   * @since 9.3.9
   */
  @Nullable
  public Locale getLocaleExt (@Nullable final String sLanguage, @Nullable final IMissingLocaleHandler aMissingHandler)
  {
    if (sLanguage != null && sLanguage.length () > 2)
    {
      // parse
      final String [] aParts = StringHelper.getExplodedArray (LocaleHelper.LOCALE_SEPARATOR, sLanguage, 3);
      if (aParts.length == 3)
        return getLocale (aParts[0], aParts[1], aParts[2], aMissingHandler);
      if (aParts.length == 2)
        return getLocale (aParts[0], aParts[1], "", aMissingHandler);
      // else fall through
    }
    return getLocale (sLanguage, "", "", aMissingHandler);
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
  public Locale getLocale (@Nullable final String sLanguage, @Nullable final String sCountry, @Nullable final String sVariant)
  {
    // Try fetching again in writeLock
    // not yet in cache, create a new one
    // -> may lead to illegal locales, but simpler than the error handling
    // for all the possible illegal values
    return getLocale (sLanguage, sCountry, sVariant, m_aMissingLocaleHandlerInsert);
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
  private static String _buildLocaleString (@Nonnull final String sLanguage, @Nonnull final String sCountry, @Nonnull final String sVariant)
  {
    final StringBuilder aLocaleSB = new StringBuilder ();
    if (sLanguage.length () > 0)
      aLocaleSB.append (sLanguage);
    if (sCountry.length () > 0)
      aLocaleSB.append (LocaleHelper.LOCALE_SEPARATOR).append (sCountry);
    if (sVariant.length () > 0)
      aLocaleSB.append (LocaleHelper.LOCALE_SEPARATOR).append (sVariant);
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
   * @param aMissingHandler
   *        An optional handler to be invoked if the provided locale is not yet
   *        contained. May be <code>null</code>.
   * @return <code>null</code> if all the passed parameters are
   *         <code>null</code> or empty
   * @since 9.3.9
   */
  @Nullable
  public Locale getLocale (@Nullable final String sLanguage,
                           @Nullable final String sCountry,
                           @Nullable final String sVariant,
                           @Nullable final IMissingLocaleHandler aMissingHandler)
  {
    final String sRealLanguage = StringHelper.getNotNull (LocaleHelper.getValidLanguageCode (sLanguage));
    final String sRealCountry = StringHelper.getNotNull (LocaleHelper.getValidCountryCode (sCountry));
    final String sRealVariant = StringHelper.getNotNull (sVariant);
    final String sLocaleKey = _buildLocaleString (sRealLanguage, sRealCountry, sRealVariant);

    Locale aLocale = null;
    if (sLocaleKey.length () > 0)
    {
      // try to resolve locale
      aLocale = m_aRWLock.readLockedGet ( () -> m_aLocales.get (sLocaleKey));
    }

    if (aLocale == null && aMissingHandler != null)
      aLocale = aMissingHandler.onMissingLocale (sLocaleKey, sRealLanguage, sRealCountry, sRealVariant);

    return aLocale;
  }

  /**
   * Get all contained locales except the locales "all" and "independent"
   *
   * @return a set with all contained locales, except "all" and "independent".
   *         Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <Locale> getAllLocales ()
  {
    final ICommonsList <Locale> ret = m_aRWLock.readLockedGet (m_aLocales::copyOfValues);
    ret.remove (LocaleHelper.LOCALE_ALL);
    ret.remove (LocaleHelper.LOCALE_INDEPENDENT);
    return ret;
  }

  /**
   * Get all contained locales that consist only of a non-empty language.
   *
   * @return a set with all contained languages, except "all" and "independent"
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsSet <Locale> getAllLanguages ()
  {
    final ICommonsSet <Locale> ret = new CommonsHashSet <> ();
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
      final String [] aParts = StringHelper.getExplodedArray (LocaleHelper.LOCALE_SEPARATOR, sLanguage, 3);
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
  public boolean containsLocale (@Nullable final String sLanguage, @Nullable final String sCountry, @Nullable final String sVariant)
  {
    final String sLocaleKey = _createLocaleKey (sLanguage, sCountry, sVariant);
    if (sLocaleKey.length () == 0)
      return false;
    return m_aRWLock.readLockedBoolean ( () -> m_aLocales.containsKey (sLocaleKey));
  }

  /**
   * @return A set of all system default locales. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsOrderedSet <Locale> getAllDefaultLocales ()
  {
    final ICommonsOrderedSet <Locale> ret = new CommonsLinkedHashSet <> (1024);

    // add pseudo locales
    ret.add (LocaleHelper.LOCALE_ALL);
    ret.add (LocaleHelper.LOCALE_INDEPENDENT);

    // add all predefined languages
    for (final Locale aLocale : Locale.getAvailableLocales ())
    {
      ret.add (aLocale);
      final String sCountry = aLocale.getCountry ();
      final String sLanguage = aLocale.getLanguage ();
      if (StringHelper.hasText (sCountry) && StringHelper.hasText (sLanguage))
      {
        // Add as country-only and as language-only locales as well
        ret.add (new Locale ("", sCountry));
        ret.add (new Locale (sLanguage, ""));
      }
    }

    // http://forums.sun.com/thread.jspa?threadID=525482&tstart=1411
    for (final String sCountry : Locale.getISOCountries ())
      ret.add (new Locale ("", sCountry));

    for (final String sLanguage : Locale.getISOLanguages ())
      ret.add (new Locale (sLanguage, ""));

    return ret;
  }

  /**
   * Reset the cache to the initial state.
   */
  public final void reinitialize ()
  {
    final ICommonsOrderedSet <Locale> aDefLocales = getAllDefaultLocales ();

    // Update map
    m_aRWLock.writeLocked ( () -> {
      m_aLocales.clear ();
      for (final Locale aLocale : aDefLocales)
        m_aLocales.put (aLocale.toString (), aLocale);
    });

    if (!isSilentMode ())
      if (LOGGER.isDebugEnabled ())
        LOGGER.debug ("Reinitialized " + LocaleCache.class.getName ());
  }
}
