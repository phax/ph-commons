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
package com.helger.text.resolve;

import java.util.Locale;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.GuardedBy;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.base.concurrent.SimpleReadWriteLock;
import com.helger.base.lang.EnumHelper;
import com.helger.statistics.api.IMutableStatisticsHandlerCounter;
import com.helger.statistics.api.IMutableStatisticsHandlerKeyedCounter;
import com.helger.statistics.impl.StatisticsManager;
import com.helger.text.IHasText;

/**
 * Resolves texts either from an override, a text provider or otherwise uses a fallback, based on
 * the given enum constant.
 *
 * @author Philip Helger
 */
@ThreadSafe
public abstract class AbstractEnumTextResolverWithOverrideAndFallback implements IEnumTextResolver
{
  public static final boolean DEFAULT_CHECK_FOR_OVERRIDE = true;
  public static final boolean DEFAULT_CHECK_FOR_FALLBACK = true;

  private static final IMutableStatisticsHandlerKeyedCounter STATS_GET_TEXT = StatisticsManager.getKeyedCounterHandler (AbstractEnumTextResolverWithOverrideAndFallback.class.getName () +
                                                                                                                        "$getText");
  private static final IMutableStatisticsHandlerCounter STATS_OVERRIDE = StatisticsManager.getCounterHandler (AbstractEnumTextResolverWithOverrideAndFallback.class.getName () +
                                                                                                              "$OVERRIDE");
  private static final IMutableStatisticsHandlerCounter STATS_FALLBACK = StatisticsManager.getCounterHandler (AbstractEnumTextResolverWithOverrideAndFallback.class.getName () +
                                                                                                              "$FALLBACK");

  protected final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
  @GuardedBy ("m_aRWLock")
  private boolean m_bCheckForOverride = DEFAULT_CHECK_FOR_OVERRIDE;
  @GuardedBy ("m_aRWLock")
  private boolean m_bCheckForFallback = DEFAULT_CHECK_FOR_FALLBACK;

  protected AbstractEnumTextResolverWithOverrideAndFallback ()
  {}

  /**
   * @return <code>true</code> if override texts are checked,
   *         <code>false</code> if not. The default value is
   *         {@link #DEFAULT_CHECK_FOR_OVERRIDE}.
   */
  public final boolean isCheckForOverride ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_bCheckForOverride;
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  /**
   * Enable or disable checking for override texts.
   *
   * @param bCheckForOverride
   *        <code>true</code> to enable, <code>false</code> to disable.
   */
  public final void setCheckForOverride (final boolean bCheckForOverride)
  {
    m_aRWLock.writeLocked ( () -> m_bCheckForOverride = bCheckForOverride);
  }

  /**
   * @return <code>true</code> if fallback texts are checked,
   *         <code>false</code> if not. The default value is
   *         {@link #DEFAULT_CHECK_FOR_FALLBACK}.
   */
  public final boolean isCheckForFallback ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_bCheckForFallback;
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  /**
   * Enable or disable checking for fallback texts.
   *
   * @param bCheckForFallback
   *        <code>true</code> to enable, <code>false</code> to disable.
   */
  public final void setCheckForFallback (final boolean bCheckForFallback)
  {
    m_aRWLock.writeLocked ( () -> m_bCheckForFallback = bCheckForFallback);
  }

  /**
   * This method must return the override string for the passed parameters. This method is only
   * called if {@link #isCheckForOverride()} is <code>true</code> .
   *
   * @param sID
   *        Unique string ID
   * @param aContentLocale
   *        locale to use.
   * @return The string in the passed locale. May be <code>null</code>.
   */
  @Nullable
  protected abstract String internalGetOverrideString (@NonNull String sID, @NonNull Locale aContentLocale);

  /**
   * This method must return the fallback string for the passed parameters. This method is only
   * called if {@link #isCheckForFallback()} is <code>true</code> .
   *
   * @param sID
   *        Unique string ID
   * @param aContentLocale
   *        locale to use.
   * @return The string in the passed locale. May be <code>null</code>.
   */
  @Nullable
  protected abstract String internalGetFallbackString (@NonNull String sID, @NonNull Locale aContentLocale);

  @Nullable
  public final String getText (@NonNull final Enum <?> aEnum,
                               @NonNull final IHasText aTP,
                               @NonNull final Locale aContentLocale)
  {
    // Get the unique text element ID
    final String sID = EnumHelper.getEnumID (aEnum);

    // Increment the statistics first
    STATS_GET_TEXT.increment (sID);

    if (isCheckForOverride ())
    {
      // Is there an override available?
      final String ret = internalGetOverrideString (sID, aContentLocale);
      if (ret != null)
      {
        // An override string was found!
        STATS_OVERRIDE.increment ();
        return ret;
      }
    }

    // No override was found (or disabled)
    // -> Try to get the text from the text provider directly
    final String ret = aTP.getText (aContentLocale);
    if (ret != null)
      return ret;

    if (isCheckForFallback ())
    {
      // The text was not found -> try the fallback (e.g. for different
      // locale)
      STATS_FALLBACK.increment ();
      return internalGetFallbackString (sID, aContentLocale);
    }

    return null;
  }
}
