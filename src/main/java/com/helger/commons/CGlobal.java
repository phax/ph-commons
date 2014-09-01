/**
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
package com.helger.commons;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Locale;

import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotations.PresentForCodeCoverage;

/**
 * A class declaring system wide constants. Its main use is giving a semantic
 * meaning to constant values.
 * 
 * @author Philip Helger
 */
@Immutable
public final class CGlobal
{
  /**
   * Number of bits in a byte.
   */
  public static final int BITS_PER_BYTE = 8;

  /**
   * Number of bits in a short.
   */
  public static final int BITS_PER_SHORT = 2 * BITS_PER_BYTE;

  /**
   * Number of bits in an int.
   */
  public static final int BITS_PER_INT = 4 * BITS_PER_BYTE;

  /**
   * Number of bits in a long.
   */
  public static final int BITS_PER_LONG = 8 * BITS_PER_BYTE;

  /**
   * Value if a bit is set.
   */
  public static final int BIT_SET = 1;

  /**
   * Value if a bit is not set.
   */
  public static final int BIT_NOT_SET = 0;

  /**
   * The maximum number that can be represented by a single byte.
   */
  public static final int MAX_BYTE_VALUE = 0xFF;

  /**
   * Radix for hexadecimal values.
   */
  public static final int HEX_RADIX = 16;

  /**
   * Represents an illegal unsigned short.
   */
  public static final short ILLEGAL_USHORT = -1;

  /**
   * Represents an illegal unsigned integer.
   */
  public static final int ILLEGAL_UINT = -1;

  /**
   * Represents an illegal unsigned long.
   */
  public static final long ILLEGAL_ULONG = -1L;

  /**
   * Represents an illegal float.
   */
  public static final float ILLEGAL_FLOAT = Float.NaN;

  /**
   * Represents an illegal double.
   */
  public static final double ILLEGAL_DOUBLE = Double.NaN;

  /**
   * Represents an illegal character.
   */
  public static final char ILLEGAL_CHAR = '\0';

  /**
   * constant for an indefinite number of entries.
   */
  public static final int INDEFINITE_ENTRIES = ILLEGAL_UINT;

  /**
   * Bytes per kilobyte.
   */
  public static final int BYTES_PER_KILOBYTE = 1024;

  /**
   * Bytes per kilobyte.
   */
  public static final long BYTES_PER_KILOBYTE_LONG = BYTES_PER_KILOBYTE;

  /**
   * Bytes per megabyte.
   */
  public static final int BYTES_PER_MEGABYTE = BYTES_PER_KILOBYTE * BYTES_PER_KILOBYTE;

  /**
   * Bytes per gigabyte.
   */
  public static final long BYTES_PER_GIGABYTE = BYTES_PER_MEGABYTE * BYTES_PER_KILOBYTE_LONG;

  /**
   * Bytes per terabyte. Needs to be a long and not an int!
   */
  public static final long BYTES_PER_TERABYTE = BYTES_PER_GIGABYTE * BYTES_PER_KILOBYTE;

  /**
   * Bytes per petabyte. Needs to be a long and not an int!
   */
  public static final long BYTES_PER_PETABYTE = BYTES_PER_TERABYTE * BYTES_PER_KILOBYTE;

  /** Hours in a day. */
  public static final int HOURS_PER_DAY = 24;

  /** Minutes in an hour. */
  public static final int MINUTES_PER_HOUR = 60;

  /** Minutes in a day. */
  public static final int MINUTES_PER_DAY = HOURS_PER_DAY * MINUTES_PER_HOUR;

  /** Seconds in a minute. */
  public static final int SECONDS_PER_MINUTE = 60;

  /** Seconds in an hour. */
  public static final int SECONDS_PER_HOUR = MINUTES_PER_HOUR * SECONDS_PER_MINUTE;

  /** Seconds in a day. */
  public static final int SECONDS_PER_DAY = MINUTES_PER_DAY * SECONDS_PER_MINUTE;

  /** Milli seconds per second. */
  public static final long MILLISECONDS_PER_SECOND = 1000;

  /** Milli seconds per second. */
  public static final long MILLISECONDS_PER_MINUTE = SECONDS_PER_MINUTE * MILLISECONDS_PER_SECOND;

  /** Milli seconds per hour. */
  public static final long MILLISECONDS_PER_HOUR = MINUTES_PER_HOUR * MILLISECONDS_PER_MINUTE;

  /** Micro seconds per milli second. */
  public static final long MICROSECONDS_PER_MILLISECOND = 1000L;

  /** Micro seconds per second. */
  public static final long MICROSECONDS_PER_SECOND = 1000000L;

  /** Nano seconds per micro second. */
  public static final long NANOSECONDS_PER_MICROSECOND = 1000L;

  /** Nano seconds per milli second. */
  public static final long NANOSECONDS_PER_MILLISECOND = 1000000L;

  /** Nano seconds per second. */
  public static final long NANOSECONDS_PER_SECOND = 1000000000L;

  /** The BigInteger for the largest possible byte value */
  public static final BigInteger BIGINT_MAX_BYTE = BigInteger.valueOf (Byte.MAX_VALUE);

  /** The BigInteger for the smallest possible byte value */
  public static final BigInteger BIGINT_MIN_BYTE = BigInteger.valueOf (Byte.MIN_VALUE);

  /** The BigInteger for the largest possible character value */
  public static final BigInteger BIGINT_MAX_CHAR = BigInteger.valueOf (Character.MAX_VALUE);

  /** The BigInteger for the smallest possible character value */
  public static final BigInteger BIGINT_MIN_CHAR = BigInteger.valueOf (Character.MIN_VALUE);

  /** The BigInteger for the largest possible short value */
  public static final BigInteger BIGINT_MAX_SHORT = BigInteger.valueOf (Short.MAX_VALUE);

  /** The BigInteger for the smallest possible short value */
  public static final BigInteger BIGINT_MIN_SHORT = BigInteger.valueOf (Short.MIN_VALUE);

  /** The BigInteger for the largest possible int value */
  public static final BigInteger BIGINT_MAX_INT = BigInteger.valueOf (Integer.MAX_VALUE);

  /** The BigInteger for the smallest possible int value */
  public static final BigInteger BIGINT_MIN_INT = BigInteger.valueOf (Integer.MIN_VALUE);

  /** The BigInteger for the largest possible long value */
  public static final BigInteger BIGINT_MAX_LONG = BigInteger.valueOf (Long.MAX_VALUE);

  /** The BigInteger for the smallest possible long value */
  public static final BigInteger BIGINT_MIN_LONG = BigInteger.valueOf (Long.MIN_VALUE);

  /** The BigInteger representation of -1 */
  public static final BigInteger BIGINT_MINUS_ONE = BigInteger.ONE.negate ();

  /** The BigInteger representation of 10 */
  public static final BigInteger BIGINT_10 = BigInteger.TEN;

  /** The BigInteger representation of 100 */
  public static final BigInteger BIGINT_100 = BigInteger.valueOf (100);

  /** The BigInteger representation of 1000 */
  public static final BigInteger BIGINT_1000 = BigInteger.valueOf (1000);

  /** The BigDecimal for the largest possible byte value */
  public static final BigDecimal BIGDEC_MAX_BYTE = BigDecimal.valueOf (Byte.MAX_VALUE);

  /** The BigDecimal for the smallest possible byte value */
  public static final BigDecimal BIGDEC_MIN_BYTE = BigDecimal.valueOf (Byte.MIN_VALUE);

  /** The BigDecimal for the largest possible character value */
  public static final BigDecimal BIGDEC_MAX_CHAR = BigDecimal.valueOf (Character.MAX_VALUE);

  /** The BigDecimal for the smallest possible character value */
  public static final BigDecimal BIGDEC_MIN_CHAR = BigDecimal.valueOf (Character.MIN_VALUE);

  /** The BigDecimal for the largest possible short value */
  public static final BigDecimal BIGDEC_MAX_SHORT = BigDecimal.valueOf (Short.MAX_VALUE);

  /** The BigDecimal for the smallest possible short value */
  public static final BigDecimal BIGDEC_MIN_SHORT = BigDecimal.valueOf (Short.MIN_VALUE);

  /** The BigDecimal for the largest possible int value */
  public static final BigDecimal BIGDEC_MAX_INT = BigDecimal.valueOf (Integer.MAX_VALUE);

  /** The BigDecimal for the smallest possible int value */
  public static final BigDecimal BIGDEC_MIN_INT = BigDecimal.valueOf (Integer.MIN_VALUE);

  /** The BigDecimal for the largest possible long value */
  public static final BigDecimal BIGDEC_MAX_LONG = BigDecimal.valueOf (Long.MAX_VALUE);

  /** The BigDecimal for the smallest possible long value */
  public static final BigDecimal BIGDEC_MIN_LONG = BigDecimal.valueOf (Long.MIN_VALUE);

  /** The BigDecimal for the largest possible float value */
  public static final BigDecimal BIGDEC_MAX_FLOAT = BigDecimal.valueOf (Float.MAX_VALUE);

  /** The BigDecimal for the smallest possible float value */
  public static final BigDecimal BIGDEC_MIN_FLOAT = BigDecimal.valueOf (Float.MIN_VALUE);

  /** The BigDecimal for the largest possible double value */
  public static final BigDecimal BIGDEC_MAX_DOUBLE = BigDecimal.valueOf (Double.MAX_VALUE);

  /** The BigDecimal for the smallest possible double value */
  public static final BigDecimal BIGDEC_MIN_DOUBLE = BigDecimal.valueOf (Double.MIN_VALUE);

  /** The BigDecimal representation of -1 */
  public static final BigDecimal BIGDEC_MINUS_ONE = BigDecimal.ONE.negate ();

  /** The BigDecimal representation of 10 */
  public static final BigDecimal BIGDEC_10 = BigDecimal.TEN;

  /** The BigDecimal representation of 100 */
  public static final BigDecimal BIGDEC_100 = new BigDecimal ("100");

  /** The BigDecimal representation of 1000 */
  public static final BigDecimal BIGDEC_1000 = new BigDecimal ("1000");

  /** The system line separator */
  public static final String LINE_SEPARATOR = SystemProperties.getLineSeparator ();

  /**
   * Separates language and country in a locale string representation.
   */
  public static final char LOCALE_SEPARATOR = '_';

  /**
   * Separates language and country in a locale string representation.
   */
  public static final String LOCALE_SEPARATOR_STR = Character.toString (LOCALE_SEPARATOR);

  /**
   * The very hardcoded default locale that cannot be changed. Equals
   * {@link Locale#US}.
   */
  public static final Locale DEFAULT_LOCALE = Locale.US;

  /**
   * The default language in case no other language is specified. Equals "en".
   */
  public static final String DEFAULT_LANGUAGE = DEFAULT_LOCALE.getLanguage ();

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

  /**
   * The number for formatting numbers in a machine readable way. This is
   * {@link Locale#ENGLISH} which has no country part!
   */
  public static final Locale LOCALE_FIXED_NUMBER_FORMAT = Locale.ENGLISH;

  /** The year when the application was started. */
  public static final int CURRENT_YEAR = Calendar.getInstance ().get (Calendar.YEAR);

  /** Centimeter per inch */
  public static final float CM_PER_INCH = 2.54f;

  /** Millimeter per inch */
  public static final float MM_PER_INCH = 25.4f;

  @PresentForCodeCoverage
  @SuppressWarnings ("unused")
  private static final CGlobal s_aInstance = new CGlobal ();

  private CGlobal ()
  {}
}
