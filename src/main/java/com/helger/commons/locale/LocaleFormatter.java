/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.PresentForCodeCoverage;

/**
 * Helper class to safely print and parse numbers in a formatted way.
 * 
 * @author Philip Helger
 */
@Immutable
public final class LocaleFormatter
{
  @PresentForCodeCoverage
  @SuppressWarnings ("unused")
  private static final LocaleFormatter s_aInstance = new LocaleFormatter ();

  private LocaleFormatter ()
  {}

  /**
   * Format the passed value according to the rules specified by the given
   * locale. All calls to {@link Double#toString(double)} that are displayed to
   * the user should instead use this method.
   * 
   * @param dValue
   *        The value to be formatted.
   * @param aDisplayLocale
   *        The locale to be used. May not be <code>null</code>.
   * @return The formatted string.
   */
  @Nonnull
  public static String getFormatted (final double dValue, @Nonnull final Locale aDisplayLocale)
  {
    ValueEnforcer.notNull (aDisplayLocale, "DisplayLocale");

    return NumberFormat.getNumberInstance (aDisplayLocale).format (dValue);
  }

  /**
   * Format the passed value according to the rules specified by the given
   * locale. All calls to {@link Integer#toString(int)} that are displayed to
   * the user should instead use this method.
   * 
   * @param nValue
   *        The value to be formatted.
   * @param aDisplayLocale
   *        The locale to be used. May not be <code>null</code>.
   * @return The formatted string.
   */
  @Nonnull
  public static String getFormatted (final int nValue, @Nonnull final Locale aDisplayLocale)
  {
    ValueEnforcer.notNull (aDisplayLocale, "DisplayLocale");

    return NumberFormat.getIntegerInstance (aDisplayLocale).format (nValue);
  }

  /**
   * Format the passed value according to the rules specified by the given
   * locale. All calls to {@link Long#toString(long)} that are displayed to the
   * user should instead use this method.
   * 
   * @param nValue
   *        The value to be formatted.
   * @param aDisplayLocale
   *        The locale to be used. May not be <code>null</code>.
   * @return The formatted string.
   */
  @Nonnull
  public static String getFormatted (final long nValue, @Nonnull final Locale aDisplayLocale)
  {
    ValueEnforcer.notNull (aDisplayLocale, "DisplayLocale");

    return NumberFormat.getIntegerInstance (aDisplayLocale).format (nValue);
  }

  /**
   * Format the passed value according to the rules specified by the given
   * locale. All calls to {@link BigInteger#toString()} that are displayed to
   * the user should instead use this method.
   * 
   * @param aValue
   *        The value to be formatted. May not be <code>null</code>.
   * @param aDisplayLocale
   *        The locale to be used. May not be <code>null</code>.
   * @return The formatted string.
   */
  @Nonnull
  public static String getFormatted (@Nonnull final BigInteger aValue, @Nonnull final Locale aDisplayLocale)
  {
    ValueEnforcer.notNull (aValue, "Value");
    ValueEnforcer.notNull (aDisplayLocale, "DisplayLocale");

    return NumberFormat.getIntegerInstance (aDisplayLocale).format (aValue);
  }

  /**
   * Format the passed value according to the rules specified by the given
   * locale. All calls to {@link BigDecimal#toString()} that are displayed to
   * the user should instead use this method. By default a maximum of 3 fraction
   * digits are shown.
   * 
   * @param aValue
   *        The value to be formatted. May not be <code>null</code>.
   * @param aDisplayLocale
   *        The locale to be used. May not be <code>null</code>.
   * @return The formatted string.
   */
  @Nonnull
  public static String getFormatted (@Nonnull final BigDecimal aValue, @Nonnull final Locale aDisplayLocale)
  {
    ValueEnforcer.notNull (aValue, "Value");
    ValueEnforcer.notNull (aDisplayLocale, "DisplayLocale");

    return NumberFormat.getInstance (aDisplayLocale).format (aValue);
  }

  /**
   * Format the passed value according to the rules specified by the given
   * locale.
   * 
   * @param aValue
   *        The value to be formatted. May not be <code>null</code>.
   * @param nFractionDigits
   *        The number of fractional digits to use. Must be >= 0.
   * @param aDisplayLocale
   *        The locale to be used. May not be <code>null</code>.
   * @return The formatted string.
   */
  @Nonnull
  public static String getFormatted (@Nonnull final BigDecimal aValue,
                                     @Nonnegative final int nFractionDigits,
                                     @Nonnull final Locale aDisplayLocale)
  {
    ValueEnforcer.notNull (aValue, "Value");
    ValueEnforcer.notNull (aDisplayLocale, "DisplayLocale");

    final NumberFormat aNF = NumberFormat.getInstance (aDisplayLocale);
    aNF.setMinimumFractionDigits (nFractionDigits);
    aNF.setMaximumFractionDigits (nFractionDigits);
    return aNF.format (aValue);
  }

  /**
   * Format the passed value according to the rules specified by the given
   * locale. All fraction digits of the passed value are displayed.
   * 
   * @param aValue
   *        The value to be formatted. May not be <code>null</code>.
   * @param aDisplayLocale
   *        The locale to be used. May not be <code>null</code>.
   * @return The formatted string.
   */
  @Nonnull
  public static String getFormattedWithAllFractionDigits (@Nonnull final BigDecimal aValue,
                                                          @Nonnull final Locale aDisplayLocale)
  {
    ValueEnforcer.notNull (aValue, "Value");
    ValueEnforcer.notNull (aDisplayLocale, "DisplayLocale");

    final NumberFormat aNF = NumberFormat.getInstance (aDisplayLocale);
    aNF.setMaximumFractionDigits (aValue.scale ());
    return aNF.format (aValue);
  }

  /**
   * Format the given value as percentage. The "%" sign is automatically
   * appended according to the requested locale. The number of fractional digits
   * depend on the locale.
   * 
   * @param dValue
   *        The value to be used. E.g. "0.125" will result in something like
   *        "12.5%"
   * @param aDisplayLocale
   *        The locale to use.
   * @return The non-<code>null</code> formatted string.
   */
  @Nonnull
  public static String getFormattedPercent (final double dValue, @Nonnull final Locale aDisplayLocale)
  {
    ValueEnforcer.notNull (aDisplayLocale, "DisplayLocale");

    return NumberFormat.getPercentInstance (aDisplayLocale).format (dValue);
  }

  /**
   * Format the given value as percentage. The "%" sign is automatically
   * appended according to the requested locale.
   * 
   * @param dValue
   *        The value to be used. E.g. "0.125" will result in something like
   *        "12.5%"
   * @param nFractionDigits
   *        The number of fractional digits to use. Must be >= 0.
   * @param aDisplayLocale
   *        The locale to use.
   * @return The non-<code>null</code> formatted string.
   */
  @Nonnull
  public static String getFormattedPercent (final double dValue,
                                            @Nonnegative final int nFractionDigits,
                                            @Nonnull final Locale aDisplayLocale)
  {
    ValueEnforcer.notNull (aDisplayLocale, "DisplayLocale");

    final NumberFormat aNF = NumberFormat.getPercentInstance (aDisplayLocale);
    aNF.setMinimumFractionDigits (nFractionDigits);
    aNF.setMaximumFractionDigits (nFractionDigits);
    return aNF.format (dValue);
  }

  @Nullable
  public static Number parse (final String sStr, @Nonnull final Locale aParseLocale)
  {
    return parse (sStr, NumberFormat.getInstance (aParseLocale));
  }

  @Nullable
  public static Number parse (@Nullable final String sStr, @Nonnull final NumberFormat aNF)
  {
    ValueEnforcer.notNull (aNF, "NumberFormat");

    if (sStr != null)
      try
      {
        // parse throws a NPE if parameter is null
        return aNF.parse (sStr);
      }
      catch (final ParseException ex)// NOPMD
      {
        // fall through
      }
    return null;
  }

  public static float parseFloat (@Nullable final String sStr, @Nonnull final Locale aParseLocale, final float fDefault)
  {
    return parseFloat (sStr, NumberFormat.getInstance (aParseLocale), fDefault);
  }

  public static float parseFloat (@Nullable final String sStr, @Nonnull final NumberFormat aNF, final float fDefault)
  {
    final Number aNum = parse (sStr, aNF);
    return aNum == null ? fDefault : aNum.floatValue ();
  }

  public static double parseDouble (@Nullable final String sStr,
                                    @Nonnull final Locale aParseLocale,
                                    final double dDefault)
  {
    return parseDouble (sStr, NumberFormat.getInstance (aParseLocale), dDefault);
  }

  public static double parseDouble (@Nullable final String sStr, @Nonnull final NumberFormat aNF, final double dDefault)
  {
    final Number aNum = parse (sStr, aNF);
    return aNum == null ? dDefault : aNum.doubleValue ();
  }

  public static int parseInt (@Nullable final String sStr, @Nonnull final Locale aParseLocale, final int nDefault)
  {
    return parseInt (sStr, NumberFormat.getIntegerInstance (aParseLocale), nDefault);
  }

  public static int parseInt (@Nullable final String sStr, @Nonnull final NumberFormat aNF, final int nDefault)
  {
    final Number aNum = parse (sStr, aNF);
    return aNum == null ? nDefault : aNum.intValue ();
  }

  public static long parseLong (@Nullable final String sStr, @Nonnull final Locale aParseLocale, final long nDefault)
  {
    return parseLong (sStr, NumberFormat.getIntegerInstance (aParseLocale), nDefault);
  }

  public static long parseLong (@Nullable final String sStr, @Nonnull final NumberFormat aNF, final long nDefault)
  {
    final Number aNum = parse (sStr, aNF);
    return aNum == null ? nDefault : aNum.longValue ();
  }

  @Nullable
  public static BigDecimal parseBigDecimal (@Nullable final String sStr, @Nonnull final DecimalFormat aNF)
  {
    ValueEnforcer.notNull (aNF, "NumberFormat");

    aNF.setParseBigDecimal (true);
    return (BigDecimal) parse (sStr, aNF);
  }

  @Nullable
  public static BigDecimal parseBigDecimal (@Nullable final String sStr,
                                            @Nonnull final Locale aParseLocale,
                                            @Nullable final BigDecimal aDefault)
  {
    return parseBigDecimal (sStr, NumberFormat.getIntegerInstance (aParseLocale), aDefault);
  }

  @Nullable
  public static BigDecimal parseBigDecimal (@Nullable final String sStr,
                                            @Nonnull final NumberFormat aNF,
                                            @Nullable final BigDecimal aDefault)
  {
    BigDecimal ret = null;
    if (aNF instanceof DecimalFormat)
    {
      // Use the simple version
      ret = parseBigDecimal (sStr, (DecimalFormat) aNF);
    }
    else
    {
      // Unsafe version!
      final Number aNum = parse (sStr, aNF);
      if (aNum != null)
        ret = BigDecimal.valueOf (aNum.doubleValue ());
    }
    return ret == null ? aDefault : ret;
  }
}
