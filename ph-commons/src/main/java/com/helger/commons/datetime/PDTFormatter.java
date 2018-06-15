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
package com.helger.commons.datetime;

import java.text.DateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.cache.Cache;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.StringHelper;

/**
 * Create common {@link DateTimeFormatter} objects used for printing and parsing
 * date and time objects.
 *
 * @author Philip Helger
 */
@Immutable
public final class PDTFormatter
{
  /**
   * Internal cache key for {@link LocalizedDateFormatCache}.
   *
   * @author Philip Helger
   */
  @Immutable
  public static final class CacheKey
  {
    private final EDTType m_eDTType;
    private final Locale m_aLocale;
    private final FormatStyle m_eStyle;
    private final EDTFormatterMode m_eMode;

    CacheKey (@Nonnull final EDTType eDTType,
              @Nullable final Locale aLocale,
              @Nonnull final FormatStyle eStyle,
              @Nonnull final EDTFormatterMode eMode)
    {
      ValueEnforcer.notNull (eDTType, "DTType");
      ValueEnforcer.notNull (eStyle, "Style");
      ValueEnforcer.notNull (eMode, "Mode");
      m_eDTType = eDTType;
      // Ensure a non-null Locale is used - same as in DateFormat itself
      // Having this shortcut here meaning less cache entries that when having
      // "null" as a separate key
      m_aLocale = aLocale != null ? aLocale : Locale.getDefault (Locale.Category.FORMAT);
      m_eStyle = eStyle;
      m_eMode = eMode;
    }

    @Override
    public boolean equals (final Object o)
    {
      if (o == this)
        return true;
      if (o == null || !getClass ().equals (o.getClass ()))
        return false;
      final CacheKey rhs = (CacheKey) o;
      return m_eDTType.equals (rhs.m_eDTType) &&
             m_aLocale.equals (rhs.m_aLocale) &&
             m_eStyle.equals (rhs.m_eStyle) &&
             m_eMode == rhs.m_eMode;
    }

    @Override
    public int hashCode ()
    {
      return new HashCodeGenerator (this).append (m_eDTType)
                                         .append (m_aLocale)
                                         .append (m_eStyle)
                                         .append (m_eMode)
                                         .getHashCode ();
    }
  }

  /**
   * This class caches the compiled patterns for localized date and time
   * formatter. Using e.g.
   * {@link DateTimeFormatter#ofLocalizedDate(java.time.format.FormatStyle)} is
   * not an option because the used "year of era", corresponding to pattern "y"
   * makes problems. Instead "year" with the pattern "u" must be used for best
   * backwards compatibility.
   *
   * @author Philip Helger
   */
  @ThreadSafe
  public static final class LocalizedDateFormatCache extends Cache <CacheKey, DateTimeFormatter>
  {
    public LocalizedDateFormatCache ()
    {
      super (aCacheKey -> {
        String sPattern = getSourcePattern (aCacheKey);

        // Change "year of era" to "year"
        sPattern = StringHelper.replaceAll (sPattern, 'y', 'u');

        if (false)
          if (aCacheKey.m_eMode == EDTFormatterMode.PARSE && StringHelper.getCharCount (sPattern, 'u') == 1)
          {
            // In Java 9, if CLDR mode is active, switch from a single "u" to
            // "uuuu" (for parsing)
            sPattern = StringHelper.replaceAll (sPattern, "u", "uuuu");
          }

        if (aCacheKey.m_eMode == EDTFormatterMode.PARSE &&
            "de".equals (aCacheKey.m_aLocale.getLanguage ()) &&
            aCacheKey.m_eStyle == FormatStyle.MEDIUM)
        {
          // Change from 2 required fields to 1
          sPattern = StringHelper.replaceAll (sPattern, "dd", "d");
          sPattern = StringHelper.replaceAll (sPattern, "MM", "M");
          sPattern = StringHelper.replaceAll (sPattern, "HH", "H");
          sPattern = StringHelper.replaceAll (sPattern, "mm", "m");
          sPattern = StringHelper.replaceAll (sPattern, "ss", "s");
        }

        // And finally create the cached DateTimeFormatter
        // Default to strict - can be changed afterwards
        return DateTimeFormatterCache.getDateTimeFormatterStrict (sPattern);
      }, 1000, LocalizedDateFormatCache.class.getName ());
    }

    @Nonnull
    public static String getSourcePattern (@Nonnull final CacheKey aKey)
    {
      switch (aKey.m_eDTType)
      {
        case LOCAL_TIME:
          return PDTFormatPatterns.getPatternTime (aKey.m_eStyle, aKey.m_aLocale);
        case LOCAL_DATE:
          return PDTFormatPatterns.getPatternDate (aKey.m_eStyle, aKey.m_aLocale);
        default:
          return PDTFormatPatterns.getPatternDateTime (aKey.m_eStyle, aKey.m_aLocale);
      }
    }
  }

  /** By default the medium style is used */
  public static final FormatStyle DEFAULT_STYLE = FormatStyle.MEDIUM;
  private static final LocalizedDateFormatCache s_aParserCache = new LocalizedDateFormatCache ();

  @PresentForCodeCoverage
  private static final PDTFormatter s_aInstance = new PDTFormatter ();

  private PDTFormatter ()
  {}

  @Nonnull
  public static FormatStyle toFormatStyle (final int nStyle)
  {
    switch (nStyle)
    {
      case DateFormat.FULL:
        return FormatStyle.FULL;
      case DateFormat.LONG:
        return FormatStyle.LONG;
      case DateFormat.MEDIUM:
        return FormatStyle.MEDIUM;
      case DateFormat.SHORT:
        return FormatStyle.SHORT;
      default:
        throw new IllegalArgumentException ("Invalid style passed: " + nStyle);
    }
  }

  public static int toDateStyle (@Nonnull final FormatStyle eStyle)
  {
    switch (eStyle)
    {
      case FULL:
        return DateFormat.FULL;
      case LONG:
        return DateFormat.LONG;
      case MEDIUM:
        return DateFormat.MEDIUM;
      case SHORT:
        return DateFormat.SHORT;
      default:
        throw new IllegalArgumentException ("Unsupported style passed: " + eStyle);
    }
  }

  @Nonnull
  public static String getPattern (@Nonnull final EDTType eDTType,
                                   @Nullable final Locale aLocale,
                                   @Nonnull final FormatStyle eStyle,
                                   @Nonnull final EDTFormatterMode eMode)
  {
    return LocalizedDateFormatCache.getSourcePattern (new CacheKey (eDTType, aLocale, eStyle, eMode));
  }

  /**
   * Assign the passed display locale to the passed date time formatter.
   *
   * @param aFormatter
   *        The formatter to be modified. May not be <code>null</code>.
   * @param aDisplayLocale
   *        The display locale to be used. May be <code>null</code>.
   * @return The modified date time formatter. Never <code>null</code>.
   */
  @Nonnull
  public static DateTimeFormatter getWithLocale (@Nonnull final DateTimeFormatter aFormatter,
                                                 @Nullable final Locale aDisplayLocale)
  {
    DateTimeFormatter ret = aFormatter;
    if (aDisplayLocale != null)
      ret = ret.withLocale (aDisplayLocale);
    return ret;
  }

  @Nonnull
  private static DateTimeFormatter _getFormatter (@Nonnull final CacheKey aCacheKey,
                                                  @Nullable final Locale aDisplayLocale)
  {
    final DateTimeFormatter aFormatter = s_aParserCache.getFromCache (aCacheKey);
    return getWithLocale (aFormatter, aDisplayLocale);
  }

  /**
   * Get the date formatter for the passed locale.
   *
   * @param eStyle
   *        The format style to be used. May not be <code>null</code>.
   * @param aDisplayLocale
   *        The display locale to be used. May be <code>null</code>.
   * @param eMode
   *        Print or parse? May not be <code>null</code>.
   * @return The created date formatter. Never <code>null</code>.
   * @since 8.5.6
   */
  @Nonnull
  public static DateTimeFormatter getFormatterDate (@Nonnull final FormatStyle eStyle,
                                                    @Nullable final Locale aDisplayLocale,
                                                    @Nonnull final EDTFormatterMode eMode)
  {
    return _getFormatter (new CacheKey (EDTType.LOCAL_DATE, aDisplayLocale, eStyle, eMode), aDisplayLocale);
  }

  /**
   * Get the time formatter for the passed locale.
   *
   * @param eStyle
   *        The format style to be used. May not be <code>null</code>.
   * @param aDisplayLocale
   *        The display locale to be used. May be <code>null</code>.
   * @param eMode
   *        Print or parse? May not be <code>null</code>.
   * @return The created time formatter. Never <code>null</code>.
   * @since 8.5.6
   */
  @Nonnull
  public static DateTimeFormatter getFormatterTime (@Nonnull final FormatStyle eStyle,
                                                    @Nullable final Locale aDisplayLocale,
                                                    @Nonnull final EDTFormatterMode eMode)
  {
    return _getFormatter (new CacheKey (EDTType.LOCAL_TIME, aDisplayLocale, eStyle, eMode), aDisplayLocale);
  }

  /**
   * Get the date time formatter for the passed locale.
   *
   * @param eStyle
   *        The format style to be used. May not be <code>null</code>.
   * @param aDisplayLocale
   *        The display locale to be used. May be <code>null</code>.
   * @param eMode
   *        Print or parse? May not be <code>null</code>.
   * @return The created date time formatter. Never <code>null</code>.
   * @since 8.5.6
   */
  @Nonnull
  public static DateTimeFormatter getFormatterDateTime (@Nonnull final FormatStyle eStyle,
                                                        @Nullable final Locale aDisplayLocale,
                                                        @Nonnull final EDTFormatterMode eMode)
  {
    return _getFormatter (new CacheKey (EDTType.LOCAL_DATE_TIME, aDisplayLocale, eStyle, eMode), aDisplayLocale);
  }

  /**
   * Get the {@link DateTimeFormatter} for the given pattern, using our default
   * chronology.
   *
   * @param sPattern
   *        The pattern to be parsed
   * @return The formatter object.
   * @throws IllegalArgumentException
   *         If the pattern is illegal
   */
  @Nonnull
  public static DateTimeFormatter getForPattern (@Nonnull final String sPattern)
  {
    return getForPattern (sPattern, null);
  }

  /**
   * Get the STRICT {@link DateTimeFormatter} for the given pattern and locale,
   * using our default chronology.
   *
   * @param sPattern
   *        The pattern to be parsed
   * @param aDisplayLocale
   *        The locale to be used. May be <code>null</code>.
   * @return The formatter object.
   * @throws IllegalArgumentException
   *         If the pattern is illegal
   */
  @Nonnull
  public static DateTimeFormatter getForPattern (@Nonnull final String sPattern, @Nullable final Locale aDisplayLocale)
  {
    final DateTimeFormatter aDTF = DateTimeFormatterCache.getDateTimeFormatterStrict (sPattern);
    return getWithLocale (aDTF, aDisplayLocale);
  }
}
