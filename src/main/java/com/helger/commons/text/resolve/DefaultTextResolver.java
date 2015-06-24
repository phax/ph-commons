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
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.text.IHasText;
import com.helger.commons.text.IHasTextWithArgs;

/**
 * Resolves texts either from a text provider or otherwise uses a fallback to a
 * file, based on the given enum constant.
 *
 * @author Philip Helger
 */
@Immutable
public final class DefaultTextResolver
{
  private static final EnumTextResolverWithPropertiesOverrideAndFallback s_aResolver = new EnumTextResolverWithPropertiesOverrideAndFallback ();

  @PresentForCodeCoverage
  private static final DefaultTextResolver s_aInstance = new DefaultTextResolver ();

  private DefaultTextResolver ()
  {}

  @Nonnull
  public static EnumTextResolverWithPropertiesOverrideAndFallback getInternalResolver ()
  {
    return s_aResolver;
  }

  /**
   * Get the text of the given element in the given locale.
   *
   * @param aEnum
   *        The enum element required for ID resolution
   * @param aTP
   *        The text provider holding the static texts
   * @param aContentLocale
   *        The locale to be used for resolving
   * @return The text or <code>null</code> if the text could not be resolved.
   */
  @Nullable
  public static String getText (@Nonnull final Enum <?> aEnum,
                                @Nonnull final IHasText aTP,
                                @Nonnull final Locale aContentLocale)
  {
    return s_aResolver.getText (aEnum, aTP, aContentLocale);
  }

  /**
   * Get the text of the given element in the given locale using the passed
   * arguments.
   *
   * @param aEnum
   *        The enum element required for ID resolution
   * @param aTP
   *        The text provider holding the static texts
   * @param aContentLocale
   *        The locale to be used for resolving
   * @param aArgs
   *        The arguments to be inserted into the string, may be
   *        <code>null</code>
   * @return The text with the arguments replaced or <code>null</code> if the
   *         passed text could not be resolved.
   */
  @Nullable
  public static String getTextWithArgs (@Nonnull final Enum <?> aEnum,
                                        @Nonnull final IHasTextWithArgs aTP,
                                        @Nonnull final Locale aContentLocale,
                                        @Nullable final Object [] aArgs)
  {
    return s_aResolver.getTextWithArgs (aEnum, aTP, aContentLocale, aArgs);
  }

  /**
   * Clear the cache.
   */
  public static void clearCache ()
  {
    s_aResolver.clearCache ();
  }
}
