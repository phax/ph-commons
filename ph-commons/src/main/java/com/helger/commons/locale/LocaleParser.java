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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;

/**
 * Helper class to safely parse formatted numbers.
 *
 * @author Philip Helger
 */
@Immutable
public final class LocaleParser
{
  @PresentForCodeCoverage
  private static final LocaleParser s_aInstance = new LocaleParser ();

  private LocaleParser ()
  {}

  @Nullable
  public static Number parse (final String sStr, @Nonnull final Locale aParseLocale)
  {
    return parse (sStr, aParseLocale, (Number) null);
  }

  @Nullable
  public static Number parse (final String sStr, @Nonnull final Locale aParseLocale, @Nullable final Number aDefault)
  {
    return parse (sStr, NumberFormat.getInstance (aParseLocale), aDefault);
  }

  @Nullable
  public static Number parse (@Nullable final String sStr, @Nonnull final NumberFormat aNF)
  {
    return parse (sStr, aNF, (Number) null);
  }

  @Nullable
  public static Number parse (@Nullable final String sStr,
                              @Nonnull final NumberFormat aNF,
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

  public static byte parseByte (@Nullable final String sStr, @Nonnull final Locale aParseLocale, final byte nDefault)
  {
    return parseByte (sStr, NumberFormat.getIntegerInstance (aParseLocale), nDefault);
  }

  public static byte parseByte (@Nullable final String sStr, @Nonnull final NumberFormat aNF, final byte nDefault)
  {
    final Number aNum = parse (sStr, aNF);
    return aNum == null ? nDefault : aNum.byteValue ();
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

  public static short parseShort (@Nullable final String sStr, @Nonnull final Locale aParseLocale, final short nDefault)
  {
    return parseShort (sStr, NumberFormat.getIntegerInstance (aParseLocale), nDefault);
  }

  public static short parseShort (@Nullable final String sStr, @Nonnull final NumberFormat aNF, final short nDefault)
  {
    final Number aNum = parse (sStr, aNF);
    return aNum == null ? nDefault : aNum.shortValue ();
  }

  @Nullable
  public static BigDecimal parseBigDecimal (@Nullable final String sStr, @Nonnull final Locale aParseLocale)
  {
    return parseBigDecimal (sStr, aParseLocale, (BigDecimal) null);
  }

  @Nullable
  public static BigDecimal parseBigDecimal (@Nullable final String sStr,
                                            @Nonnull final Locale aParseLocale,
                                            @Nullable final BigDecimal aDefault)
  {
    return parseBigDecimal (sStr, NumberFormat.getInstance (aParseLocale), aDefault);
  }

  @Nullable
  public static BigDecimal parseBigDecimal (@Nullable final String sStr, @Nonnull final DecimalFormat aNF)
  {
    return parseBigDecimal (sStr, aNF, (BigDecimal) null);
  }

  @Nullable
  public static BigDecimal parseBigDecimal (@Nullable final String sStr,
                                            @Nonnull final DecimalFormat aNF,
                                            @Nullable final BigDecimal aDefault)
  {
    ValueEnforcer.notNull (aNF, "NumberFormat");

    aNF.setParseBigDecimal (true);
    return (BigDecimal) parse (sStr, aNF, aDefault);
  }

  @Nullable
  public static BigDecimal parseBigDecimal (@Nullable final String sStr, @Nonnull final NumberFormat aNF)
  {
    return parseBigDecimal (sStr, aNF, (BigDecimal) null);
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
