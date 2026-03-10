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
package com.helger.text.locale;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.enforce.ValueEnforcer;

/**
 * Helper class to safely parse formatted numbers.
 *
 * @author Philip Helger
 */
@Immutable
public final class LocaleParser
{
  @PresentForCodeCoverage
  private static final LocaleParser INSTANCE = new LocaleParser ();

  private LocaleParser ()
  {}

  /**
   * Parse the given string as a {@link Number} using the specified locale.
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @param aParseLocale
   *        The locale to use for parsing. May not be <code>null</code>.
   * @return The parsed number or <code>null</code> if parsing failed.
   */
  @Nullable
  public static Number parse (final String sStr, @NonNull final Locale aParseLocale)
  {
    return parse (sStr, aParseLocale, (Number) null);
  }

  /**
   * Parse the given string as a {@link Number} using the specified locale and default value.
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @param aParseLocale
   *        The locale to use for parsing. May not be <code>null</code>.
   * @param aDefault
   *        The default value to return if parsing fails. May be <code>null</code>.
   * @return The parsed number or the default value if parsing failed.
   */
  @Nullable
  public static Number parse (final String sStr, @NonNull final Locale aParseLocale, @Nullable final Number aDefault)
  {
    return parse (sStr, NumberFormat.getInstance (aParseLocale), aDefault);
  }

  /**
   * Parse the given string as a {@link Number} using the specified number format.
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @param aNF
   *        The number format to use for parsing. May not be <code>null</code>.
   * @return The parsed number or <code>null</code> if parsing failed.
   */
  @Nullable
  public static Number parse (@Nullable final String sStr, @NonNull final NumberFormat aNF)
  {
    return parse (sStr, aNF, (Number) null);
  }

  /**
   * Parse the given string as a {@link Number} using the specified number format and default value.
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @param aNF
   *        The number format to use for parsing. May not be <code>null</code>.
   * @param aDefault
   *        The default value to return if parsing fails. May be <code>null</code>.
   * @return The parsed number or the default value if parsing failed.
   */
  @Nullable
  public static Number parse (@Nullable final String sStr,
                              @NonNull final NumberFormat aNF,
                              @Nullable final Number aDefault)
  {
    ValueEnforcer.notNull (aNF, "NumberFormat");

    if (sStr != null)
      try
      {
        // parse throws a NPE if parameter is null
        return aNF.parse (sStr);
      }
      catch (final ParseException ex)
      {
        // fall through
      }

    // Return the provided default value
    return aDefault;
  }

  /**
   * Parse the given string as a float value using the specified locale.
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @param aParseLocale
   *        The locale to use for parsing. May not be <code>null</code>.
   * @param fDefault
   *        The default value to return if parsing fails.
   * @return The parsed float value or the default value if parsing failed.
   */
  public static float parseFloat (@Nullable final String sStr, @NonNull final Locale aParseLocale, final float fDefault)
  {
    return parseFloat (sStr, NumberFormat.getInstance (aParseLocale), fDefault);
  }

  /**
   * Parse the given string as a float value using the specified number format.
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @param aNF
   *        The number format to use for parsing. May not be <code>null</code>.
   * @param fDefault
   *        The default value to return if parsing fails.
   * @return The parsed float value or the default value if parsing failed.
   */
  public static float parseFloat (@Nullable final String sStr, @NonNull final NumberFormat aNF, final float fDefault)
  {
    final Number aNum = parse (sStr, aNF);
    return aNum == null ? fDefault : aNum.floatValue ();
  }

  /**
   * Parse the given string as a double value using the specified locale.
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @param aParseLocale
   *        The locale to use for parsing. May not be <code>null</code>.
   * @param dDefault
   *        The default value to return if parsing fails.
   * @return The parsed double value or the default value if parsing failed.
   */
  public static double parseDouble (@Nullable final String sStr,
                                    @NonNull final Locale aParseLocale,
                                    final double dDefault)
  {
    return parseDouble (sStr, NumberFormat.getInstance (aParseLocale), dDefault);
  }

  /**
   * Parse the given string as a double value using the specified number format.
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @param aNF
   *        The number format to use for parsing. May not be <code>null</code>.
   * @param dDefault
   *        The default value to return if parsing fails.
   * @return The parsed double value or the default value if parsing failed.
   */
  public static double parseDouble (@Nullable final String sStr, @NonNull final NumberFormat aNF, final double dDefault)
  {
    final Number aNum = parse (sStr, aNF);
    return aNum == null ? dDefault : aNum.doubleValue ();
  }

  /**
   * Parse the given string as a byte value using the specified locale.
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @param aParseLocale
   *        The locale to use for parsing. May not be <code>null</code>.
   * @param nDefault
   *        The default value to return if parsing fails.
   * @return The parsed byte value or the default value if parsing failed.
   */
  public static byte parseByte (@Nullable final String sStr, @NonNull final Locale aParseLocale, final byte nDefault)
  {
    return parseByte (sStr, NumberFormat.getIntegerInstance (aParseLocale), nDefault);
  }

  /**
   * Parse the given string as a byte value using the specified number format.
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @param aNF
   *        The number format to use for parsing. May not be <code>null</code>.
   * @param nDefault
   *        The default value to return if parsing fails.
   * @return The parsed byte value or the default value if parsing failed.
   */
  public static byte parseByte (@Nullable final String sStr, @NonNull final NumberFormat aNF, final byte nDefault)
  {
    final Number aNum = parse (sStr, aNF);
    return aNum == null ? nDefault : aNum.byteValue ();
  }

  /**
   * Parse the given string as an int value using the specified locale.
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @param aParseLocale
   *        The locale to use for parsing. May not be <code>null</code>.
   * @param nDefault
   *        The default value to return if parsing fails.
   * @return The parsed int value or the default value if parsing failed.
   */
  public static int parseInt (@Nullable final String sStr, @NonNull final Locale aParseLocale, final int nDefault)
  {
    return parseInt (sStr, NumberFormat.getIntegerInstance (aParseLocale), nDefault);
  }

  /**
   * Parse the given string as an int value using the specified number format.
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @param aNF
   *        The number format to use for parsing. May not be <code>null</code>.
   * @param nDefault
   *        The default value to return if parsing fails.
   * @return The parsed int value or the default value if parsing failed.
   */
  public static int parseInt (@Nullable final String sStr, @NonNull final NumberFormat aNF, final int nDefault)
  {
    final Number aNum = parse (sStr, aNF);
    return aNum == null ? nDefault : aNum.intValue ();
  }

  /**
   * Parse the given string as a long value using the specified locale.
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @param aParseLocale
   *        The locale to use for parsing. May not be <code>null</code>.
   * @param nDefault
   *        The default value to return if parsing fails.
   * @return The parsed long value or the default value if parsing failed.
   */
  public static long parseLong (@Nullable final String sStr, @NonNull final Locale aParseLocale, final long nDefault)
  {
    return parseLong (sStr, NumberFormat.getIntegerInstance (aParseLocale), nDefault);
  }

  /**
   * Parse the given string as a long value using the specified number format.
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @param aNF
   *        The number format to use for parsing. May not be <code>null</code>.
   * @param nDefault
   *        The default value to return if parsing fails.
   * @return The parsed long value or the default value if parsing failed.
   */
  public static long parseLong (@Nullable final String sStr, @NonNull final NumberFormat aNF, final long nDefault)
  {
    final Number aNum = parse (sStr, aNF);
    return aNum == null ? nDefault : aNum.longValue ();
  }

  /**
   * Parse the given string as a short value using the specified locale.
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @param aParseLocale
   *        The locale to use for parsing. May not be <code>null</code>.
   * @param nDefault
   *        The default value to return if parsing fails.
   * @return The parsed short value or the default value if parsing failed.
   */
  public static short parseShort (@Nullable final String sStr, @NonNull final Locale aParseLocale, final short nDefault)
  {
    return parseShort (sStr, NumberFormat.getIntegerInstance (aParseLocale), nDefault);
  }

  /**
   * Parse the given string as a short value using the specified number format.
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @param aNF
   *        The number format to use for parsing. May not be <code>null</code>.
   * @param nDefault
   *        The default value to return if parsing fails.
   * @return The parsed short value or the default value if parsing failed.
   */
  public static short parseShort (@Nullable final String sStr, @NonNull final NumberFormat aNF, final short nDefault)
  {
    final Number aNum = parse (sStr, aNF);
    return aNum == null ? nDefault : aNum.shortValue ();
  }

  /**
   * Parse the given string as a {@link BigDecimal} using the specified locale.
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @param aParseLocale
   *        The locale to use for parsing. May not be <code>null</code>.
   * @return The parsed {@link BigDecimal} or <code>null</code> if parsing failed.
   */
  @Nullable
  public static BigDecimal parseBigDecimal (@Nullable final String sStr, @NonNull final Locale aParseLocale)
  {
    return parseBigDecimal (sStr, aParseLocale, (BigDecimal) null);
  }

  /**
   * Parse the given string as a {@link BigDecimal} using the specified locale and default value.
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @param aParseLocale
   *        The locale to use for parsing. May not be <code>null</code>.
   * @param aDefault
   *        The default value to return if parsing fails. May be <code>null</code>.
   * @return The parsed {@link BigDecimal} or the default value if parsing failed.
   */
  @Nullable
  public static BigDecimal parseBigDecimal (@Nullable final String sStr,
                                            @NonNull final Locale aParseLocale,
                                            @Nullable final BigDecimal aDefault)
  {
    return parseBigDecimal (sStr, NumberFormat.getInstance (aParseLocale), aDefault);
  }

  /**
   * Parse the given string as a {@link BigDecimal} using the specified decimal format.
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @param aNF
   *        The decimal format to use for parsing. May not be <code>null</code>.
   * @return The parsed {@link BigDecimal} or <code>null</code> if parsing failed.
   */
  @Nullable
  public static BigDecimal parseBigDecimal (@Nullable final String sStr, @NonNull final DecimalFormat aNF)
  {
    return parseBigDecimal (sStr, aNF, (BigDecimal) null);
  }

  /**
   * Parse the given string as a {@link BigDecimal} using the specified decimal format and default
   * value.
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @param aNF
   *        The decimal format to use for parsing. May not be <code>null</code>.
   * @param aDefault
   *        The default value to return if parsing fails. May be <code>null</code>.
   * @return The parsed {@link BigDecimal} or the default value if parsing failed.
   */
  @Nullable
  public static BigDecimal parseBigDecimal (@Nullable final String sStr,
                                            @NonNull final DecimalFormat aNF,
                                            @Nullable final BigDecimal aDefault)
  {
    ValueEnforcer.notNull (aNF, "NumberFormat");

    aNF.setParseBigDecimal (true);
    return (BigDecimal) parse (sStr, aNF, aDefault);
  }

  /**
   * Parse the given string as a {@link BigDecimal} using the specified number format.
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @param aNF
   *        The number format to use for parsing. May not be <code>null</code>.
   * @return The parsed {@link BigDecimal} or <code>null</code> if parsing failed.
   */
  @Nullable
  public static BigDecimal parseBigDecimal (@Nullable final String sStr, @NonNull final NumberFormat aNF)
  {
    return parseBigDecimal (sStr, aNF, (BigDecimal) null);
  }

  /**
   * Parse the given string as a {@link BigDecimal} using the specified number format and default
   * value. If the number format is a {@link DecimalFormat}, the precise parsing mode is used;
   * otherwise it falls back to converting through {@link Number#doubleValue()}.
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @param aNF
   *        The number format to use for parsing. May not be <code>null</code>.
   * @param aDefault
   *        The default value to return if parsing fails. May be <code>null</code>.
   * @return The parsed {@link BigDecimal} or the default value if parsing failed.
   */
  @Nullable
  public static BigDecimal parseBigDecimal (@Nullable final String sStr,
                                            @NonNull final NumberFormat aNF,
                                            @Nullable final BigDecimal aDefault)
  {
    BigDecimal ret = null;
    if (aNF instanceof final DecimalFormat aDecimalFormat)
    {
      // Use the simple version
      ret = parseBigDecimal (sStr, aDecimalFormat);
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
