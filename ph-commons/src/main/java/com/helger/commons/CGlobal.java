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
package com.helger.commons;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Year;
import java.util.Locale;

import javax.annotation.concurrent.Immutable;

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

  /** Milliseconds per second. */
  public static final long MILLISECONDS_PER_SECOND = 1000L;

  /** Milliseconds per second. */
  public static final long MILLISECONDS_PER_MINUTE = SECONDS_PER_MINUTE * MILLISECONDS_PER_SECOND;

  /** Milliseconds per hour. */
  public static final long MILLISECONDS_PER_HOUR = MINUTES_PER_HOUR * MILLISECONDS_PER_MINUTE;

  /** Micro seconds per millisecond. */
  public static final long MICROSECONDS_PER_MILLISECOND = 1000L;

  /** Micro seconds per second. */
  public static final long MICROSECONDS_PER_SECOND = MICROSECONDS_PER_MILLISECOND * MILLISECONDS_PER_SECOND;

  /** Nanoseconds per microsecond. */
  public static final long NANOSECONDS_PER_MICROSECOND = 1000L;

  /** Nanoseconds per millisecond. */
  public static final long NANOSECONDS_PER_MILLISECOND = NANOSECONDS_PER_MICROSECOND * MICROSECONDS_PER_MILLISECOND;

  /** Nano seconds per second. */
  public static final long NANOSECONDS_PER_SECOND = NANOSECONDS_PER_MICROSECOND * MICROSECONDS_PER_SECOND;

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

  /** The BigInteger representation of 2 */
  public static final BigInteger BIGINT_2 = BigInteger.valueOf (2);

  /** The BigInteger representation of 3 */
  public static final BigInteger BIGINT_3 = BigInteger.valueOf (3);

  /** The BigInteger representation of 4 */
  public static final BigInteger BIGINT_4 = BigInteger.valueOf (4);

  /** The BigInteger representation of 5 */
  public static final BigInteger BIGINT_5 = BigInteger.valueOf (5);

  /** The BigInteger representation of 6 */
  public static final BigInteger BIGINT_6 = BigInteger.valueOf (6);

  /** The BigInteger representation of 7 */
  public static final BigInteger BIGINT_7 = BigInteger.valueOf (7);

  /** The BigInteger representation of 8 */
  public static final BigInteger BIGINT_8 = BigInteger.valueOf (8);

  /** The BigInteger representation of 9 */
  public static final BigInteger BIGINT_9 = BigInteger.valueOf (9);

  /** The BigInteger representation of 10 */
  public static final BigInteger BIGINT_10 = BigInteger.TEN;

  /** The BigInteger representation of 50 */
  public static final BigInteger BIGINT_50 = BigInteger.valueOf (50);

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

  /** The BigDecimal representation of 2 */
  public static final BigDecimal BIGDEC_2 = new BigDecimal ("2");

  /** The BigDecimal representation of 3 */
  public static final BigDecimal BIGDEC_3 = new BigDecimal ("3");

  /** The BigDecimal representation of 4 */
  public static final BigDecimal BIGDEC_4 = new BigDecimal ("4");

  /** The BigDecimal representation of 5 */
  public static final BigDecimal BIGDEC_5 = new BigDecimal ("5");

  /** The BigDecimal representation of 6 */
  public static final BigDecimal BIGDEC_6 = new BigDecimal ("6");

  /** The BigDecimal representation of 7 */
  public static final BigDecimal BIGDEC_7 = new BigDecimal ("7");

  /** The BigDecimal representation of 8 */
  public static final BigDecimal BIGDEC_8 = new BigDecimal ("8");

  /** The BigDecimal representation of 9 */
  public static final BigDecimal BIGDEC_9 = new BigDecimal ("9");

  /** The BigDecimal representation of 10 */
  public static final BigDecimal BIGDEC_10 = BigDecimal.TEN;

  /** The BigDecimal representation of 50 */
  public static final BigDecimal BIGDEC_50 = new BigDecimal ("50");

  /** The BigDecimal representation of 100 */
  public static final BigDecimal BIGDEC_100 = new BigDecimal ("100");

  /** The BigDecimal representation of 1000 */
  public static final BigDecimal BIGDEC_1000 = new BigDecimal ("1000");

  /**
   * Separates language and country in a locale string representation.
   */
  public static final char LOCALE_SEPARATOR = '_';

  /**
   * Separates language and country in a locale string representation.
   */
  public static final String LOCALE_SEPARATOR_STR = Character.toString (LOCALE_SEPARATOR);

  /**
   * The very hardcoded default locale that cannot be changed and contains a
   * language and a country. Equals {@link Locale#US}.
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
  public static final int CURRENT_YEAR = Year.now ().getValue ();

  /** Centimeter per inch (2.54) */
  public static final float CM_PER_INCH = 2.54f;

  /** Millimeter per inch (25.4) */
  public static final float MM_PER_INCH = 25.4f;

  // Default values for primitive values
  /** Default boolean value */
  public static final boolean DEFAULT_BOOLEAN = false;
  /** Default byte value */
  public static final byte DEFAULT_BYTE = (byte) 0;
  /** Default char value */
  public static final char DEFAULT_CHAR = '\0';
  /** Default double value */
  public static final double DEFAULT_DOUBLE = 0D;
  /** Default float value */
  public static final float DEFAULT_FLOAT = 0F;
  /** Default int value */
  public static final int DEFAULT_INT = 0;
  /** Default long value */
  public static final long DEFAULT_LONG = 0L;
  /** Default short value */
  public static final short DEFAULT_SHORT = (short) 0;

  // Default values for primitive wrapper values
  /** Boolean value of {@link #DEFAULT_BOOLEAN} */
  public static final Boolean DEFAULT_BOOLEAN_OBJ = Boolean.valueOf (DEFAULT_BOOLEAN);
  /** Byte value of {@link #DEFAULT_BYTE} */
  public static final Byte DEFAULT_BYTE_OBJ = Byte.valueOf (DEFAULT_BYTE);
  /** Character value of {@link #DEFAULT_CHAR} */
  public static final Character DEFAULT_CHAR_OBJ = Character.valueOf (DEFAULT_CHAR);
  /** Double value of {@link #DEFAULT_DOUBLE} */
  public static final Double DEFAULT_DOUBLE_OBJ = Double.valueOf (DEFAULT_DOUBLE);
  /** Float value of {@link #DEFAULT_FLOAT} */
  public static final Float DEFAULT_FLOAT_OBJ = Float.valueOf (DEFAULT_FLOAT);
  /** Integer value of {@link #DEFAULT_INT} */
  public static final Integer DEFAULT_INT_OBJ = Integer.valueOf (DEFAULT_INT);
  /** Long value of {@link #DEFAULT_LONG} */
  public static final Long DEFAULT_LONG_OBJ = Long.valueOf (DEFAULT_LONG);
  /** Short value of {@link #DEFAULT_SHORT} */
  public static final Short DEFAULT_SHORT_OBJ = Short.valueOf (DEFAULT_SHORT);

  private CGlobal ()
  {}
}
