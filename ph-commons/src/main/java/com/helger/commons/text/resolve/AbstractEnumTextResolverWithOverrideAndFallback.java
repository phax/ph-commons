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
package com.helger.commons.text.resolve;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.lang.EnumHelper;
import com.helger.commons.statistics.IMutableStatisticsHandlerCounter;
import com.helger.commons.statistics.IMutableStatisticsHandlerKeyedCounter;
import com.helger.commons.statistics.StatisticsManager;
import com.helger.commons.text.IHasText;
import com.helger.commons.text.IHasTextWithArgs;
import com.helger.commons.text.util.TextHelper;

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

  private static final IMutableStatisticsHandlerKeyedCounter s_aStatsGetText = StatisticsManager.getKeyedCounterHandler (AbstractEnumTextResolverWithOverrideAndFallback.class.getName () +
                                                                                                                         "$getText");
  private static final IMutableStatisticsHandlerKeyedCounter s_aStatsGetTextWithArgs = StatisticsManager.getKeyedCounterHandler (AbstractEnumTextResolverWithOverrideAndFallback.class.getName () +
                                                                                                                                 "$getTextWithArgs");
  private static final IMutableStatisticsHandlerCounter s_aStatsOverride = StatisticsManager.getCounterHandler (AbstractEnumTextResolverWithOverrideAndFallback.class.getName () +
                                                                                                                "$OVERRIDE");
  private static final IMutableStatisticsHandlerCounter s_aStatsFallback = StatisticsManager.getCounterHandler (AbstractEnumTextResolverWithOverrideAndFallback.class.getName () +
                                                                                                                "$FALLBACK");

  private boolean m_bCheckForOverride = DEFAULT_CHECK_FOR_OVERRIDE;
  private boolean m_bCheckForFallback = DEFAULT_CHECK_FOR_FALLBACK;

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
  protected String internalGetText (@Nonnull final Enum <?> aEnum,
                                    @Nonnull final IHasText aTP,
                                    @Nonnull final Locale aContentLocale,
                                    final boolean bIsWithArgs)
  {
    // Get the unique text element ID
    final String sID = EnumHelper.getEnumID (aEnum);

    // Increment the statistics first
    (bIsWithArgs ? s_aStatsGetTextWithArgs : s_aStatsGetText).increment (sID);

    if (m_bCheckForOverride)
    {
      // Is there an override available?
      final String ret = internalGetOverrideString (sID, aContentLocale);
      if (ret != null)
      {
        // An override string was found!
        s_aStatsOverride.increment ();
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
      s_aStatsFallback.increment ();
      return internalGetFallbackString (sID, aContentLocale);
    }

    return null;
  }

  @Nullable
  public final String getText (@Nonnull final Enum <?> aEnum,
                               @Nonnull final IHasText aTP,
                               @Nonnull final Locale aContentLocale)
  {
    return internalGetText (aEnum, aTP, aContentLocale, false);
  }

  @Nullable
  public final String getTextWithArgs (@Nonnull final Enum <?> aEnum,
                                       @Nonnull final IHasTextWithArgs aTP,
                                       @Nonnull final Locale aContentLocale,
                                       @Nullable final Object... aArgs)
  {
    // The same as getText
    final String sText = internalGetText (aEnum, aTP, aContentLocale, true);
    // And if something was found, resolve the arguments
    return TextHelper.getFormattedText (sText, aArgs);
  }
}
