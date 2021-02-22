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
package com.helger.commons.locale.language;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.Singleton;
import com.helger.commons.annotation.VisibleForTesting;
import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.debug.GlobalDebug;
import com.helger.commons.locale.LocaleCache;
import com.helger.commons.locale.LocaleCache.IMissingLocaleHandler;
import com.helger.commons.locale.LocaleHelper;
import com.helger.commons.state.EChange;
import com.helger.commons.string.StringHelper;

/**
 * This is a global cache for language objects to avoid too many objects flowing
 * around.<br>
 * This cache is application independent.
 *
 * @author Philip Helger
 * @since v9.1.3
 */
@ThreadSafe
@Singleton
public final class LanguageCache
{
  private static final class SingletonHolder
  {
    private static final LanguageCache INSTANCE = new LanguageCache ();
  }

  private static final Logger LOGGER = LoggerFactory.getLogger (LanguageCache.class);
  private static final AtomicBoolean SILENT_MODE = new AtomicBoolean (GlobalDebug.DEFAULT_SILENT_MODE);

  private static boolean s_bDefaultInstantiated = false;

  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();

  /** Contains all known countries (as ISO 639-1 2-letter codes). */
  @GuardedBy ("m_aRWLock")
  private final ICommonsSet <String> m_aLanguages = new CommonsHashSet <> ();

  private LanguageCache ()
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
  public static LanguageCache getInstance ()
  {
    final LanguageCache ret = SingletonHolder.INSTANCE;
    s_bDefaultInstantiated = true;
    return ret;
  }

  @Nonnull
  @VisibleForTesting
  EChange addLanguage (@Nonnull final String sLanguage)
  {
    ValueEnforcer.notNull (sLanguage, "Language");
    final String sValidLanguage = LocaleHelper.getValidLanguageCode (sLanguage);
    ValueEnforcer.isTrue (sValidLanguage != null, () -> "illegal language code '" + sLanguage + "'");
    ValueEnforcer.isEqual (sLanguage, sValidLanguage, () -> "invalid casing of '" + sLanguage + "'");

    return m_aRWLock.writeLockedGet ( () -> m_aLanguages.addObject (sValidLanguage));
  }

  /**
   * The normed language locale associated with the provided locale.
   *
   * @param aLanguage
   *        Source locale. May be <code>null</code>.
   * @return <code>null</code> if the source locale is <code>null</code> or if
   *         the source locale does not contain language information.
   */
  @Nullable
  public Locale getLanguage (@Nullable final Locale aLanguage)
  {
    return aLanguage == null ? null : getLanguage (aLanguage.getLanguage ());
  }

  /**
   * The normed language locale associated with the provided locale.
   *
   * @param aLanguage
   *        Source locale. May be <code>null</code>.
   * @param aMissingHandler
   *        The missing locale handler to be passed to {@link LocaleCache}. May
   *        be <code>null</code> to use {@link LocaleCache} default handler.
   * @return <code>null</code> if the source locale is <code>null</code> or if
   *         the source locale does not contain language information.
   * @since 9.4.2
   */
  @Nullable
  public Locale getLanguageExt (@Nullable final Locale aLanguage, @Nullable final IMissingLocaleHandler aMissingHandler)
  {
    return aLanguage == null ? null : getLanguageExt (aLanguage.getLanguage (), aMissingHandler);
  }

  /**
   * Resolve the language from the provided string.<br>
   * Note: this method may be invoked recursively, if the language code contains
   * a locale separator char.
   *
   * @param sLanguage
   *        The language code. May be <code>null</code> or empty.
   * @return <code>null</code> if the provided language code is
   *         <code>null</code> or empty.
   */
  @Nullable
  public Locale getLanguage (@Nullable final String sLanguage)
  {
    return getLanguageExt (sLanguage, null);
  }

  /**
   * Resolve the language from the provided string.<br>
   * Note: this method may be invoked recursively, if the language code contains
   * a locale separator char.
   *
   * @param sLanguage
   *        The language code. May be <code>null</code> or empty.
   * @param aMissingHandler
   *        The missing locale handler to be passed to {@link LocaleCache}. May
   *        be <code>null</code> to use {@link LocaleCache} default handler.
   * @return <code>null</code> if the provided language code is
   *         <code>null</code> or empty.
   * @since 9.4.2
   */
  @Nullable
  public Locale getLanguageExt (@Nullable final String sLanguage, @Nullable final IMissingLocaleHandler aMissingHandler)
  {
    if (StringHelper.hasNoText (sLanguage))
      return null;

    final LocaleCache aLC = LocaleCache.getInstance ();
    final IMissingLocaleHandler aMLH = aMissingHandler != null ? aMissingHandler : aLC.getDefaultMissingLocaleHandler ();

    // Was something like "de_" passed in? -> indirect recursion
    if (sLanguage.indexOf (LocaleHelper.LOCALE_SEPARATOR) >= 0)
      return getLanguageExt (aLC.getLocaleExt (sLanguage, aMLH), aMLH);

    final String sValidLanguage = LocaleHelper.getValidLanguageCode (sLanguage);
    if (!containsLanguage (sValidLanguage))
      if (!isSilentMode ())
        if (LOGGER.isWarnEnabled ())
          LOGGER.warn ("Trying to retrieve unsupported language '" + sLanguage + "'");
    return aLC.getLocale (sValidLanguage, "", "", aMLH);
  }

  /**
   * @return a set with all contained languages. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsSet <String> getAllLanguages ()
  {
    return m_aRWLock.readLockedGet (m_aLanguages::getClone);
  }

  /**
   * @return a set with all contained language locales. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsSet <Locale> getAllLanguageLocales ()
  {
    return m_aRWLock.readLockedGet ( () -> {
      final LocaleCache aLC = LocaleCache.getInstance ();
      return new CommonsHashSet <> (m_aLanguages, sLanguage -> aLC.getLocale (sLanguage, "", ""));
    });
  }

  /**
   * Check if the passed language is known.
   *
   * @param aLanguage
   *        The language to check. May be <code>null</code>.
   * @return <code>true</code> if the passed language is contained,
   *         <code>false</code> otherwise.
   */
  public boolean containsLanguage (@Nullable final Locale aLanguage)
  {
    return aLanguage != null && containsLanguage (aLanguage.getLanguage ());
  }

  /**
   * Check if the passed language is known.
   *
   * @param sLanguage
   *        The language to check. May be <code>null</code>.
   * @return <code>true</code> if the passed language is contained,
   *         <code>false</code> otherwise.
   */
  public boolean containsLanguage (@Nullable final String sLanguage)
  {
    if (sLanguage == null)
      return false;

    final String sValidLanguage = LocaleHelper.getValidLanguageCode (sLanguage);
    if (sValidLanguage == null)
      return false;
    return m_aRWLock.readLockedBoolean ( () -> m_aLanguages.contains (sValidLanguage));
  }

  /**
   * Reset the cache to the initial state.
   */
  public void reinitialize ()
  {
    m_aRWLock.writeLocked (m_aLanguages::clear);

    for (final Locale aLocale : LocaleCache.getAllDefaultLocales ())
    {
      final String sLanguage = aLocale.getLanguage ();
      if (StringHelper.hasText (sLanguage))
      {
        // Allows for duplicates!
        addLanguage (sLanguage);
      }
    }

    if (!isSilentMode ())
      if (LOGGER.isDebugEnabled ())
        LOGGER.debug ("Reinitialized " + LanguageCache.class.getName ());
  }
}
