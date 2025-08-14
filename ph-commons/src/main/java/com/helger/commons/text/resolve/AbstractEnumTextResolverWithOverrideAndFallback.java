/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.commons.text.resolve;

import java.util.Locale;

import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.base.statistics.IMutableStatisticsHandlerCounter;
import com.helger.base.statistics.IMutableStatisticsHandlerKeyedCounter;
import com.helger.commons.lang.EnumHelper;
import com.helger.commons.statistics.StatisticsManager;
import com.helger.commons.text.IHasText;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Resolves texts either from an override, a text provider or otherwise uses a
 * fallback, based on the given enum constant.
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

  private boolean m_bCheckForOverride = DEFAULT_CHECK_FOR_OVERRIDE;
  private boolean m_bCheckForFallback = DEFAULT_CHECK_FOR_FALLBACK;

  protected AbstractEnumTextResolverWithOverrideAndFallback ()
  {}

  public final boolean isCheckForOverride ()
  {
    return m_bCheckForOverride;
  }

  public final void setCheckForOverride (final boolean bCheckForOverride)
  {
    m_bCheckForOverride = bCheckForOverride;
  }

  public final boolean isCheckForFallback ()
  {
    return m_bCheckForFallback;
  }

  public final void setCheckForFallback (final boolean bCheckForFallback)
  {
    m_bCheckForFallback = bCheckForFallback;
  }

  /**
   * This method must return the override string for the passed parameters. This
   * method is only called if {@link #isCheckForOverride()} is <code>true</code>
   * .
   *
   * @param sID
   *        Unique string ID
   * @param aContentLocale
   *        locale to use.
   * @return The string in the passed locale. May be <code>null</code>.
   */
  @Nullable
  protected abstract String internalGetOverrideString (@Nonnull String sID, @Nonnull Locale aContentLocale);

  /**
   * This method must return the fallback string for the passed parameters. This
   * method is only called if {@link #isCheckForFallback()} is <code>true</code>
   * .
   *
   * @param sID
   *        Unique string ID
   * @param aContentLocale
   *        locale to use.
   * @return The string in the passed locale. May be <code>null</code>.
   */
  @Nullable
  protected abstract String internalGetFallbackString (@Nonnull String sID, @Nonnull Locale aContentLocale);

  @Nullable
  public final String getText (@Nonnull final Enum <?> aEnum,
                               @Nonnull final IHasText aTP,
                               @Nonnull final Locale aContentLocale)
  {
    // Get the unique text element ID
    final String sID = EnumHelper.getEnumID (aEnum);

    // Increment the statistics first
    STATS_GET_TEXT.increment (sID);

    if (m_bCheckForOverride)
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

    if (m_bCheckForFallback)
    {
      // The text was not found -> try the fallback (e.g. for different
      // locale)
      STATS_FALLBACK.increment ();
      return internalGetFallbackString (sID, aContentLocale);
    }

    return null;
  }
}
