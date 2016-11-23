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
package com.helger.datetime.format;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.cache.AbstractNotifyingCache;
import com.helger.commons.datetime.DateTimeFormatterCache;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.StringHelper;
import com.helger.datetime.EDTType;

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
  private static final class CacheKey
  {
    private final EDTType m_eDTType;
    private final Locale m_aLocale;
    private final int m_nStyle;

    CacheKey (@Nonnull final EDTType eDTType, @Nullable final Locale aLocale, final int nStyle)
    {
      ValueEnforcer.notNull (eDTType, "DTType");
      ValueEnforcer.isTrue (nStyle == DateFormat.SHORT ||
                            nStyle == DateFormat.MEDIUM ||
                            nStyle == DateFormat.LONG ||
                            nStyle == DateFormat.FULL,
                            "Invalid style provided");
      m_eDTType = eDTType;
      // Ensure a non-null Locale is used - same as in DateFormat itself
      // Having this shortcut here meaning less cache entries that when having
      // "null" as a separate key
      m_aLocale = aLocale != null ? aLocale : Locale.getDefault (Locale.Category.FORMAT);
      m_nStyle = nStyle;
    }

    @Override
    public boolean equals (final Object o)
    {
      if (o == this)
        return true;
      if (o == null || !getClass ().equals (o.getClass ()))
        return false;
      final CacheKey rhs = (CacheKey) o;
      return m_eDTType.equals (rhs.m_eDTType) && m_aLocale.equals (rhs.m_aLocale) && m_nStyle == rhs.m_nStyle;
    }

    @Override
    public int hashCode ()
    {
      return new HashCodeGenerator (this).append (m_eDTType).append (m_aLocale).append (m_nStyle).getHashCode ();
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
  private static final class LocalizedDateFormatCache extends AbstractNotifyingCache <CacheKey, DateTimeFormatter>
  {
    public LocalizedDateFormatCache ()
    {
      super (LocalizedDateFormatCache.class.getName ());
    }

    @Nonnull
    public String getSourcePattern (@Nonnull final CacheKey aKey)
    {
      String sPattern;
      if (true)
      {
        FormatStyle eFS;
        switch (aKey.m_nStyle)
        {
          case DateFormat.SHORT:
            eFS = FormatStyle.SHORT;
            break;
          case DateFormat.MEDIUM:
            eFS = FormatStyle.MEDIUM;
            break;
          case DateFormat.LONG:
            eFS = FormatStyle.LONG;
            break;
          case DateFormat.FULL:
            eFS = FormatStyle.FULL;
            break;
          default:
            throw new IllegalStateException ("Invalid style present: " + aKey.m_nStyle);
        }
        switch (aKey.m_eDTType)
        {
          case LOCAL_TIME:
            sPattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern (null,
                                                                             eFS,
                                                                             IsoChronology.INSTANCE,
                                                                             aKey.m_aLocale);
            break;
          case LOCAL_DATE:
            sPattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern (eFS,
                                                                             null,
                                                                             IsoChronology.INSTANCE,
                                                                             aKey.m_aLocale);
            break;
          default:
            sPattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern (eFS,
                                                                             eFS,
                                                                             IsoChronology.INSTANCE,
                                                                             aKey.m_aLocale);
            break;
        }
      }
      else
      {
        final DateFormat aDF;
        switch (aKey.m_eDTType)
        {
          case LOCAL_TIME:
            aDF = DateFormat.getTimeInstance (aKey.m_nStyle, aKey.m_aLocale);
            break;
          case LOCAL_DATE:
            aDF = DateFormat.getDateInstance (aKey.m_nStyle, aKey.m_aLocale);
            break;
          default:
            aDF = DateFormat.getDateTimeInstance (aKey.m_nStyle, aKey.m_nStyle, aKey.m_aLocale);
            break;
        }
        sPattern = ((SimpleDateFormat) aDF).toPattern ();
      }
      return sPattern;
    }

    @Override
    protected DateTimeFormatter getValueToCache (@Nonnull final CacheKey aKey)
    {
      String sPattern = getSourcePattern (aKey);

      // Change "year of era" to "year"
      sPattern = StringHelper.replaceAll (sPattern, 'y', 'u');
      // Change from 2 required fields to 1
      sPattern = StringHelper.replaceAll (sPattern, "dd", "d");
      sPattern = StringHelper.replaceAll (sPattern, "MM", "M");
      sPattern = StringHelper.replaceAll (sPattern, "HH", "H");
      sPattern = StringHelper.replaceAll (sPattern, "mm", "m");
      sPattern = StringHelper.replaceAll (sPattern, "ss", "s");

      // And finally create the cached DateTimeFormatter
      // Default to strict - can be changed afterwards
      final DateTimeFormatter ret = DateTimeFormatterCache.getDateTimeFormatterStrict (sPattern);
      return ret;
    }
  }

  private static final int DEFAULT_STYLE = DateFormat.DEFAULT;
  private static final LocalizedDateFormatCache s_aCache = new LocalizedDateFormatCache ();

  @PresentForCodeCoverage
  private static final PDTFormatter s_aInstance = new PDTFormatter ();

  private PDTFormatter ()
  {}

  @Nonnull
  public static String getPattern (@Nonnull final EDTType eDTType, @Nullable final Locale aLocale, final int nStyle)
  {
    return s_aCache.getSourcePattern (new CacheKey (eDTType, aLocale, nStyle));
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
  private static DateTimeFormatter _getFormatterDate (@Nullable final Locale aDisplayLocale, final int nStyle)
  {
    return getWithLocale (s_aCache.getFromCache (new CacheKey (EDTType.LOCAL_DATE, aDisplayLocale, nStyle)),
                          aDisplayLocale);
  }

  /**
   * Get the default date formatter for the passed locale. This used medium
   * style.
   *
   * @param aDisplayLocale
   *        The display locale to be used. May be <code>null</code>.
   * @return The created date time formatter. Never <code>null</code>.
   */
  @Nonnull
  public static DateTimeFormatter getDefaultFormatterDate (@Nullable final Locale aDisplayLocale)
  {
    return _getFormatterDate (aDisplayLocale, DEFAULT_STYLE);
  }

  /**
   * Get the short date formatter for the passed locale.
   *
   * @param aDisplayLocale
   *        The display locale to be used. May be <code>null</code>.
   * @return The created date time formatter. Never <code>null</code>.
   */
  @Nonnull
  public static DateTimeFormatter getShortFormatterDate (@Nullable final Locale aDisplayLocale)
  {
    return _getFormatterDate (aDisplayLocale, DateFormat.SHORT);
  }

  /**
   * Get the medium date formatter for the passed locale.
   *
   * @param aDisplayLocale
   *        The display locale to be used. May be <code>null</code>.
   * @return The created date time formatter. Never <code>null</code>.
   */
  @Nonnull
  public static DateTimeFormatter getMediumFormatterDate (@Nullable final Locale aDisplayLocale)
  {
    return _getFormatterDate (aDisplayLocale, DateFormat.MEDIUM);
  }

  /**
   * Get the long date formatter for the passed locale.
   *
   * @param aDisplayLocale
   *        The display locale to be used. May be <code>null</code>.
   * @return The created date time formatter. Never <code>null</code>.
   */
  @Nonnull
  public static DateTimeFormatter getLongFormatterDate (@Nullable final Locale aDisplayLocale)
  {
    return _getFormatterDate (aDisplayLocale, DateFormat.LONG);
  }

  /**
   * Get the full date formatter for the passed locale.
   *
   * @param aDisplayLocale
   *        The display locale to be used. May be <code>null</code>.
   * @return The created date time formatter. Never <code>null</code>.
   */
  @Nonnull
  public static DateTimeFormatter getFullFormatterDate (@Nullable final Locale aDisplayLocale)
  {
    return _getFormatterDate (aDisplayLocale, DateFormat.FULL);
  }

  @Nonnull
  private static DateTimeFormatter _getFormatterTime (@Nullable final Locale aDisplayLocale, final int nStyle)
  {
    return getWithLocale (s_aCache.getFromCache (new CacheKey (EDTType.LOCAL_TIME, aDisplayLocale, nStyle)),
                          aDisplayLocale);
  }

  /**
   * Get the default time formatter for the passed locale. This used medium
   * style.
   *
   * @param aDisplayLocale
   *        The display locale to be used. May be <code>null</code>.
   * @return The created date time formatter. Never <code>null</code>.
   */
  @Nonnull
  public static DateTimeFormatter getDefaultFormatterTime (@Nullable final Locale aDisplayLocale)
  {
    return _getFormatterTime (aDisplayLocale, DEFAULT_STYLE);
  }

  /**
   * Get the short time formatter for the passed locale.
   *
   * @param aDisplayLocale
   *        The display locale to be used. May be <code>null</code>.
   * @return The created date time formatter. Never <code>null</code>.
   */
  @Nonnull
  public static DateTimeFormatter getShortFormatterTime (@Nullable final Locale aDisplayLocale)
  {
    return _getFormatterTime (aDisplayLocale, DateFormat.SHORT);
  }

  /**
   * Get the medium time formatter for the passed locale.
   *
   * @param aDisplayLocale
   *        The display locale to be used. May be <code>null</code>.
   * @return The created date time formatter. Never <code>null</code>.
   */
  @Nonnull
  public static DateTimeFormatter getMediumFormatterTime (@Nullable final Locale aDisplayLocale)
  {
    return _getFormatterTime (aDisplayLocale, DateFormat.MEDIUM);
  }

  /**
   * Get the long time formatter for the passed locale.
   *
   * @param aDisplayLocale
   *        The display locale to be used. May be <code>null</code>.
   * @return The created date time formatter. Never <code>null</code>.
   */
  @Nonnull
  public static DateTimeFormatter getLongFormatterTime (@Nullable final Locale aDisplayLocale)
  {
    return _getFormatterTime (aDisplayLocale, DateFormat.LONG);
  }

  /**
   * Get the full time formatter for the passed locale.
   *
   * @param aDisplayLocale
   *        The display locale to be used. May be <code>null</code>.
   * @return The created date time formatter. Never <code>null</code>.
   */
  @Nonnull
  public static DateTimeFormatter getFullFormatterTime (@Nullable final Locale aDisplayLocale)
  {
    return _getFormatterTime (aDisplayLocale, DateFormat.FULL);
  }

  @Nonnull
  private static DateTimeFormatter _getFormatterDateTime (@Nullable final Locale aDisplayLocale, final int nStyle)
  {
    return getWithLocale (s_aCache.getFromCache (new CacheKey (EDTType.LOCAL_DATE_TIME, aDisplayLocale, nStyle)),
                          aDisplayLocale);
  }

  /**
   * Get the default date time formatter for the passed locale. The default
   * style is medium.
   *
   * @param aDisplayLocale
   *        The display locale to be used. May be <code>null</code>.
   * @return The created date time formatter. Never <code>null</code>.
   */
  @Nonnull
  public static DateTimeFormatter getDefaultFormatterDateTime (@Nullable final Locale aDisplayLocale)
  {
    return _getFormatterDateTime (aDisplayLocale, DEFAULT_STYLE);
  }

  /**
   * Get the short date time formatter for the passed locale.
   *
   * @param aDisplayLocale
   *        The display locale to be used. May be <code>null</code>.
   * @return The created date time formatter. Never <code>null</code>.
   */
  @Nonnull
  public static DateTimeFormatter getShortFormatterDateTime (@Nullable final Locale aDisplayLocale)
  {
    return _getFormatterDateTime (aDisplayLocale, DateFormat.SHORT);
  }

  /**
   * Get the medium date time formatter for the passed locale.
   *
   * @param aDisplayLocale
   *        The display locale to be used. May be <code>null</code>.
   * @return The created date time formatter. Never <code>null</code>.
   */
  @Nonnull
  public static DateTimeFormatter getMediumFormatterDateTime (@Nullable final Locale aDisplayLocale)
  {
    return _getFormatterDateTime (aDisplayLocale, DateFormat.MEDIUM);
  }

  /**
   * Get the long date time formatter for the passed locale.
   *
   * @param aDisplayLocale
   *        The display locale to be used. May be <code>null</code>.
   * @return The created date time formatter. Never <code>null</code>.
   */
  @Nonnull
  public static DateTimeFormatter getLongFormatterDateTime (@Nullable final Locale aDisplayLocale)
  {
    return _getFormatterDateTime (aDisplayLocale, DateFormat.LONG);
  }

  /**
   * Get the full date time formatter for the passed locale.
   *
   * @param aDisplayLocale
   *        The display locale to be used. May be <code>null</code>.
   * @return The created date time formatter. Never <code>null</code>.
   */
  @Nonnull
  public static DateTimeFormatter getFullFormatterDateTime (@Nullable final Locale aDisplayLocale)
  {
    return _getFormatterDateTime (aDisplayLocale, DateFormat.FULL);
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
  public static DateTimeFormatter getForPattern (@Nonnull final String sPattern) throws IllegalArgumentException
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
  public static DateTimeFormatter getForPattern (@Nonnull final String sPattern,
                                                 @Nullable final Locale aDisplayLocale) throws IllegalArgumentException
  {
    final DateTimeFormatter aDTF = DateTimeFormatterCache.getDateTimeFormatterStrict (sPattern);
    return getWithLocale (aDTF, aDisplayLocale);
  }
}
