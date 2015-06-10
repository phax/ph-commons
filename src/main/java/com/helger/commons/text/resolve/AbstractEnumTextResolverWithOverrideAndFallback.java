/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
  private static final IMutableStatisticsHandlerKeyedCounter s_aStatsGetText = StatisticsManager.getKeyedCounterHandler (AbstractEnumTextResolverWithOverrideAndFallback.class.getName () +
                                                                                                                         "$getText");
  private static final IMutableStatisticsHandlerKeyedCounter s_aStatsGetTextWithArgs = StatisticsManager.getKeyedCounterHandler (AbstractEnumTextResolverWithOverrideAndFallback.class.getName () +
                                                                                                                                 "$getTextWithArgs");
  private static final IMutableStatisticsHandlerCounter s_aStatsOverride = StatisticsManager.getCounterHandler (AbstractEnumTextResolverWithOverrideAndFallback.class.getName () +
                                                                                                                "$OVERRIDE");
  private static final IMutableStatisticsHandlerCounter s_aStatsFallback = StatisticsManager.getCounterHandler (AbstractEnumTextResolverWithOverrideAndFallback.class.getName () +
                                                                                                                "$FALLBACK");

  /**
   * This method must return the override string for the passed parameters.
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
   * This method must return the fallback string for the passed parameters.
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
  private String _getText (@Nonnull final Enum <?> aEnum,
                           @Nonnull final IHasText aTP,
                           @Nonnull final Locale aContentLocale,
                           final boolean bIsWithArgs)
  {
    // Get the unique text element ID
    final String sID = EnumHelper.getEnumID (aEnum);
    // Increment the statistics
    (bIsWithArgs ? s_aStatsGetTextWithArgs : s_aStatsGetText).increment (sID);

    // Is there an override available?
    String ret = internalGetOverrideString (sID, aContentLocale);
    if (ret != null)
    {
      // An override string was found!
      s_aStatsOverride.increment ();
    }
    else
    {
      // No override was found
      // -> Try to get the text from the text provider
      ret = aTP.getText (aContentLocale);
      if (ret == null)
      {
        // The text was not found -> try the fallback (e.g. for different
        // locale)
        s_aStatsFallback.increment ();
        ret = internalGetFallbackString (sID, aContentLocale);
      }
    }
    return ret;
  }

  @Nullable
  public final String getText (@Nonnull final Enum <?> aEnum,
                               @Nonnull final IHasText aTP,
                               @Nonnull final Locale aContentLocale)
  {
    return _getText (aEnum, aTP, aContentLocale, false);
  }

  @Nullable
  public final String getTextWithArgs (@Nonnull final Enum <?> aEnum,
                                       @Nonnull final IHasTextWithArgs aTP,
                                       @Nonnull final Locale aContentLocale,
                                       @Nullable final Object... aArgs)
  {
    // The same as getText
    final String sText = _getText (aEnum, aTP, aContentLocale, true);
    // And if something was found, resolve the arguments
    return TextHelper.getFormattedText (sText, aArgs);
  }
}
