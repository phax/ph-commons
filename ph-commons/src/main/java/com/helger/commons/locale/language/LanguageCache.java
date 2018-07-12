/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
import com.helger.commons.locale.LocaleCache;
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
    private static final LanguageCache s_aInstance = new LanguageCache ();
  }

  private static final Logger s_aLogger = LoggerFactory.getLogger (LanguageCache.class);

  private static boolean s_bDefaultInstantiated = false;

  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();

  /** Contains all known countries (as ISO 639-1 2-letter codes). */
  @GuardedBy ("m_aRWLock")
  private final ICommonsSet <String> m_aLanguages = new CommonsHashSet <> ();

  private LanguageCache ()
  {
    reinitialize ();
  }

  public static boolean isInstantiated ()
  {
    return s_bDefaultInstantiated;
  }

  @Nonnull
  public static LanguageCache getInstance ()
  {
    final LanguageCache ret = SingletonHolder.s_aInstance;
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

    return m_aRWLock.writeLocked ( () -> m_aLanguages.addObject (sValidLanguage));
  }

  @Nullable
  public Locale getLanguage (@Nullable final Locale aLanguage)
  {
    return aLanguage == null ? null : getLanguage (aLanguage.getLanguage ());
  }

  @Nullable
  public Locale getLanguage (@Nullable final String sLanguage)
  {
    if (StringHelper.hasNoText (sLanguage))
      return null;

    // Was something like "de_" passed in? -> indirect recursion
    if (sLanguage.indexOf (LocaleHelper.LOCALE_SEPARATOR) >= 0)
      return getLanguage (LocaleCache.getInstance ().getLocale (sLanguage));

    final String sValidLanguage = LocaleHelper.getValidLanguageCode (sLanguage);
    if (!containsLanguage (sValidLanguage))
      if (s_aLogger.isWarnEnabled ())
        s_aLogger.warn ("Trying to retrieve unsupported language '" + sLanguage + "'");
    return LocaleCache.getInstance ().getLocale (sValidLanguage, "", "");
  }

  /**
   * @return a set with all contained languages. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsSet <String> getAllLanguages ()
  {
    return m_aRWLock.readLocked (m_aLanguages::getClone);
  }

  /**
   * @return a set with all contained language locales. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsSet <Locale> getAllLanguageLocales ()
  {
    return m_aRWLock.readLocked ( () -> new CommonsHashSet <> (m_aLanguages,
                                                               sLanguage -> LocaleCache.getInstance ()
                                                                                       .getLocale (sLanguage, "", "")));
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
    return m_aRWLock.readLocked ( () -> m_aLanguages.contains (sValidLanguage));
  }

  /**
   * Reset the cache to the initial state.
   */
  public void reinitialize ()
  {
    m_aRWLock.writeLocked (m_aLanguages::clear);

    for (final Locale aLocale : LocaleCache.getInstance ().getAllLocales ())
    {
      final String sLanguage = aLocale.getLanguage ();
      if (StringHelper.hasText (sLanguage))
        addLanguage (sLanguage);
    }

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("Reinitialized " + LanguageCache.class.getName ());
  }
}
