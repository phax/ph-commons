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
package com.helger.commons.locale;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.Locale;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;

/**
 * Helper class to safely print numbers in a formatted way.
 *
 * @author Philip Helger
 */
@Immutable
public final class LocaleFormatter
{
  @PresentForCodeCoverage
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
   *        The number of fractional digits to use. Must be &ge; 0.
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
   *        The number of fractional digits to use. Must be &ge; 0.
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
}
