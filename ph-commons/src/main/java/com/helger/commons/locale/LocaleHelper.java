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
package com.helger.commons.locale;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.CGlobal;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.CodingStyleguideUnaware;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.annotation.ReturnsImmutableObject;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.cache.Cache;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.regex.RegExHelper;
import com.helger.commons.state.EChange;
import com.helger.commons.string.StringHelper;
import com.helger.commons.system.SystemHelper;

/**
 * Misc locale utility methods.
 *
 * @author Philip Helger
 */
@Immutable
public final class LocaleHelper
{
  /**
   * Small cache for the resolved locales.
   *
   * @author Philip Helger
   */
  @ThreadSafe
  private static final class LocaleListCache extends Cache <Locale, List <Locale>>
  {
    public LocaleListCache ()
    {
      // Unlimited size
      super (aBaseLocale -> {
        ValueEnforcer.notNull (aBaseLocale, "BaseLocale");

        // List has a maximum of 3 entries
        final ICommonsList <Locale> ret = new CommonsArrayList <> (3);
        final String sLanguage = aBaseLocale.getLanguage ();
        if (sLanguage.length () > 0)
        {
          final LocaleCache aLC = LocaleCache.getInstance ();

          // Use only the language
          ret.add (0, aLC.getLocale (sLanguage));
          final String sCountry = aBaseLocale.getCountry ();
          if (sCountry.length () > 0)
          {
            // Use language + country
            ret.add (0, aLC.getLocale (sLanguage, sCountry));
            final String sVariant = aBaseLocale.getVariant ();
            if (sVariant.length () > 0)
            {
              // Use language + country + variant
              ret.add (0, aLC.getLocale (sLanguage, sCountry, sVariant));
            }
          }
        }
        return ret.getAsUnmodifiable ();
      }, CGlobal.ILLEGAL_UINT, LocaleListCache.class.getName ());
    }
  }

  /**
   * Separates language and country in a locale string representation.
   */
  public static final char LOCALE_SEPARATOR = '_';

  /**
   * The language string representing the "all" locale. See {@link #LOCALE_ALL}.
   */
  public static final String STR_ALL = "all";

  /**
   * the default locale which means "all locales".
   */
  public static final Locale LOCALE_ALL = new Locale (STR_ALL, "", "");

  /**
   * The language string representing the "independent" locale. See
   * {@link #LOCALE_INDEPENDENT}.
   */
  public static final String STR_INDEPENDENT = "independent";

  /**
   * the default locale which means "locale independent".
   */
  public static final Locale LOCALE_INDEPENDENT = new Locale (STR_INDEPENDENT, "", "");

  private static final String LOCALE_ALL_STR = LOCALE_ALL.toString ();
  private static final String LOCALE_INDEPENDENT_STR = LOCALE_INDEPENDENT.toString ();
  private static final LocaleListCache s_aLocaleListCache = new LocaleListCache ();

  @PresentForCodeCoverage
  private static final LocaleHelper s_aInstance = new LocaleHelper ();

  private LocaleHelper ()
  {}

  /**
   * Get the display name of the passed language in the currently selected UI
   * language.
   *
   * @param aLocale
   *        The locale from which the display name is required. May be
   *        <code>null</code>.
   * @param aContentLocale
   *        The locale in which the name of the locale is required. If aLocale
   *        is "de" and display locale is "de" the result would be "Deutsch"
   *        whereas if display locale is "en" the result would be "German".
   * @return the display name of the language or a fixed text if the passed
   *         Locale is <code>null</code>, "all" or "independent"
   * @see #LOCALE_ALL
   * @see #LOCALE_INDEPENDENT
   */
  @Nonnull
  public static String getLocaleDisplayName (@Nullable final Locale aLocale, @Nonnull final Locale aContentLocale)
  {
    ValueEnforcer.notNull (aContentLocale, "ContentLocale");

    if (aLocale == null || aLocale.equals (LOCALE_INDEPENDENT))
      return ELocaleName.ID_LANGUAGE_INDEPENDENT.getDisplayText (aContentLocale);
    if (aLocale.equals (LOCALE_ALL))
      return ELocaleName.ID_LANGUAGE_ALL.getDisplayText (aContentLocale);
    return aLocale.getDisplayName (aContentLocale);
  }

  /**
   * Get the display name of the passed locale <em>in</em> the passed locale.
   *
   * @param aLocale
   *        The locale to use. May not be <code>null</code>.
   * @return The native display name of the passed locale.
   */
  @Nonnull
  public static String getLocaleNativeDisplayName (@Nonnull final Locale aLocale)
  {
    ValueEnforcer.notNull (aLocale, "Locale");
    return getLocaleDisplayName (aLocale, aLocale);
  }

  /**
   * Get all possible locale names in the passed locale
   *
   * @param aContentLocale
   *        the locale ID in which the language list is required
   * @return The mapping from the input locale to the display text. The result
   *         map is not ordered.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsMap <Locale, String> getAllLocaleDisplayNames (@Nonnull final Locale aContentLocale)
  {
    ValueEnforcer.notNull (aContentLocale, "ContentLocale");

    return new CommonsHashMap <> (LocaleCache.getInstance ().getAllLocales (),
                                  Function.identity (),
                                  aLocale -> getLocaleDisplayName (aLocale, aContentLocale));
  }

  /**
   * Get a list with all valid locale permutations of the passed locale. If the
   * passed locale has no language, always an empty list is returned.<br>
   * Examples:
   * <ul>
   * <li>"de_AT" =&gt; ["de_AT", "de"]</li>
   * <li>"en_US" =&gt; ["en_US", "en"]</li>
   * <li>"de" =&gt; ["de"]</li>
   * <li>"de_DE_Variant" =&gt; ["de_DE_Variant", "de_DE", "de"]</li>
   * <li>"" =&gt; []</li>
   * <li>"_AT" =&gt; []</li>
   * <li>"_AT_Variant" =&gt; []</li>
   * </ul>
   *
   * @param aLocale
   *        The locale to get the permutation from. May not be <code>null</code>
   *        .
   * @return A non-<code>null</code> unmodifiable list of all permutations of
   *         the locale, with the most specific locale being first. The returned
   *         list has never more than three entries. The returned list may have
   *         no entries, if the passed locale has no language.
   */
  @Nonnull
  @ReturnsImmutableObject
  @CodingStyleguideUnaware
  public static List <Locale> getCalculatedLocaleListForResolving (@Nonnull final Locale aLocale)
  {
    ValueEnforcer.notNull (aLocale, "Locale");

    return s_aLocaleListCache.getFromCache (aLocale);
  }

  /**
   * Convert a String in the form "language-country-variant" to a Locale object.
   * Language needs to have exactly 2 characters. Country is optional but if
   * present needs to have exactly 2 characters. Variant is optional.
   *
   * @param sLocaleAsString
   *        The string representation to be converted to a Locale.
   * @return Never <code>null</code>. If the passed parameter is
   *         <code>null</code> or empty, {@link SystemHelper#getSystemLocale()}
   *         is returned.
   */
  @Nonnull
  public static Locale getLocaleFromString (@Nullable final String sLocaleAsString)
  {
    if (StringHelper.hasNoText (sLocaleAsString))
    {
      // not specified => getDefault
      return SystemHelper.getSystemLocale ();
    }

    String sLanguage;
    String sCountry;
    String sVariant;

    int i1 = sLocaleAsString.indexOf (LOCALE_SEPARATOR);
    if (i1 < 0)
    {
      // No separator present -> use as is
      sLanguage = sLocaleAsString;
      sCountry = "";
      sVariant = "";
    }
    else
    {
      // Language found
      sLanguage = sLocaleAsString.substring (0, i1);
      ++i1;

      // Find next separator
      final int i2 = sLocaleAsString.indexOf (LOCALE_SEPARATOR, i1);
      if (i2 < 0)
      {
        // No other separator -> country is the rest
        sCountry = sLocaleAsString.substring (i1);
        sVariant = "";
      }
      else
      {
        // We have country and variant
        sCountry = sLocaleAsString.substring (i1, i2);
        sVariant = sLocaleAsString.substring (i2 + 1);
      }
    }

    // Unify elements
    if (sLanguage.length () == 2)
      sLanguage = sLanguage.toLowerCase (Locale.US);
    else
      sLanguage = "";

    if (sCountry.length () == 2)
      sCountry = sCountry.toUpperCase (Locale.US);
    else
      sCountry = "";

    if (sVariant.length () > 0 && (sLanguage.length () == 2 || sCountry.length () == 2))
      sVariant = sVariant.toUpperCase (Locale.US);
    else
      sVariant = "";

    // And now resolve using the locale cache
    return LocaleCache.getInstance ().getLocale (sLanguage, sCountry, sVariant);
  }

  @Nullable
  public static Locale getLocaleToUseOrNull (@Nonnull final Locale aRequestLocale,
                                             @Nonnull final Collection <Locale> aAvailableLocales)
  {
    return getLocaleToUseOrFallback (aRequestLocale, aAvailableLocales, null);
  }

  @Nullable
  public static Locale getLocaleToUseOrFallback (@Nonnull final Locale aRequestLocale,
                                                 @Nonnull final Collection <Locale> aAvailableLocales,
                                                 @Nullable final Locale aFallback)
  {
    ValueEnforcer.notNull (aRequestLocale, "RequestLocale");
    ValueEnforcer.notNull (aAvailableLocales, "AvailableLocales");

    // first check direct match
    if (aAvailableLocales.contains (aRequestLocale))
      return aRequestLocale;

    // try to resolve a more general locale than asked for
    for (final Locale aCurrentLocale : getCalculatedLocaleListForResolving (aRequestLocale))
      if (aAvailableLocales.contains (aCurrentLocale))
        return aCurrentLocale;

    // try to resolve a more specific locale than asked for (only check
    // language)
    final String sRequestLanguage = aRequestLocale.getLanguage ();
    if (sRequestLanguage != null)
      for (final Locale aCurrentAvailableLocale : aAvailableLocales)
        if (sRequestLanguage.equals (aCurrentAvailableLocale.getLanguage ()))
          return aCurrentAvailableLocale;

    // If none matched, check if "all" is provided
    if (aAvailableLocales.contains (LOCALE_ALL))
      return LOCALE_ALL;

    // If none matched, check if "independent" is provided
    if (aAvailableLocales.contains (LOCALE_INDEPENDENT))
      return LOCALE_INDEPENDENT;

    // No matching found -> fallback locale
    return aFallback;
  }

  /**
   * Check if the passed locale is one of the special locales "all" or
   * "independent"
   *
   * @param aLocale
   *        The locale to check. May be <code>null</code>.
   * @return if the passed locale is not <code>null</code> and a special locale.
   * @see #LOCALE_ALL
   * @see #LOCALE_INDEPENDENT
   */
  public static boolean isSpecialLocale (@Nullable final Locale aLocale)
  {
    return LOCALE_ALL.equals (aLocale) || LOCALE_INDEPENDENT.equals (aLocale);
  }

  /**
   * Check if the passed locale is one of the special locales "all" or
   * "independent"
   *
   * @param sLocale
   *        The locale to check. May be <code>null</code>.
   * @return if the passed locale is not <code>null</code> and a special locale.
   * @see #LOCALE_ALL
   * @see #LOCALE_INDEPENDENT
   */
  public static boolean isSpecialLocaleCode (@Nullable final String sLocale)
  {
    return LOCALE_ALL_STR.equalsIgnoreCase (sLocale) || LOCALE_INDEPENDENT_STR.equalsIgnoreCase (sLocale);
  }

  @Nullable
  public static String getValidLanguageCode (@Nullable final String sCode)
  {
    if (StringHelper.hasText (sCode) &&
        (RegExHelper.stringMatchesPattern ("[a-zA-Z]{2,8}", sCode) || isSpecialLocaleCode (sCode)))
    {
      return sCode.toLowerCase (CGlobal.LOCALE_FIXED_NUMBER_FORMAT);
    }
    return null;
  }

  @Nullable
  public static String getValidCountryCode (@Nullable final String sCode)
  {
    if (StringHelper.hasText (sCode) && RegExHelper.stringMatchesPattern ("[a-zA-Z]{2}|[0-9]{3}", sCode))
    {
      return sCode.toUpperCase (CGlobal.LOCALE_FIXED_NUMBER_FORMAT);
    }
    return null;
  }

  /**
   * Clear all stored locale lists
   *
   * @return {@link EChange}.
   */
  @Nonnull
  public static EChange clearCache ()
  {
    return s_aLocaleListCache.clearCache ();
  }
}
