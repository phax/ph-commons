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
package com.helger.commons.string;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.PresentForCodeCoverage;

/**
 * This class contains the methods to parse String objects to numeric values.
 * Before v3.7.0 this was contained in class {@link StringHelper}.
 *
 * @author Philip Helger
 */
@Immutable
public final class StringParser
{
  /** The default radix used to convert string values to numeric values */
  public static final int DEFAULT_RADIX = 10;

  @PresentForCodeCoverage
  private static final StringParser s_aInstance = new StringParser ();

  private StringParser ()
  {}

  /**
   * Get the unified decimal string for parsing by the runtime library. This is
   * done by replacing ',' with '.'.
   *
   * @param sStr
   *        The string to unified. Never <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nonnull
  @Nonempty
  private static String _getUnifiedDecimal (@Nonnull @Nonempty final String sStr)
  {
    return sStr.replace (',', '.');
  }

  // ---[boolean]---

  /**
   * Try to interpret the passed object as boolean. This works only if the
   * passed object is either a {@link String} or a {@link Boolean}.
   *
   * @param aObject
   *        The object to be interpreted. May be <code>null</code>.
   * @return <code>false</code> if the passed object cannot be interpreted as a
   *         boolean.
   */
  public static boolean parseBool (@Nullable final Object aObject)
  {
    return parseBool (aObject, false);
  }

  /**
   * Try to interpret the passed object as boolean. This works only if the
   * passed object is either a {@link String} or a {@link Boolean}.
   *
   * @param aObject
   *        The object to be interpreted. May be <code>null</code>.
   * @param bDefault
   *        The default value to be returned, if the object cannot be
   *        interpreted.
   * @return The boolean representation or the default value if the passed
   *         object cannot be interpreted as a boolean.
   */
  public static boolean parseBool (@Nullable final Object aObject, final boolean bDefault)
  {
    if (aObject instanceof Boolean)
      return ((Boolean) aObject).booleanValue ();
    if (aObject instanceof String)
      return parseBool ((String) aObject);
    return bDefault;
  }

  /**
   * Parse the given {@link String} as boolean value. All values that are equal
   * to "true" (ignoring case) will result in <code>true</code> return values.
   * All other values result in <code>false</code> return values. This method is
   * equal to {@link Boolean#parseBoolean(String)}
   *
   * @param sStr
   *        The string to be interpreted. May be <code>null</code>.
   * @return <code>true</code> if the passed string matches "true" (ignoring
   *         case), <code>false</code> otherwise.
   * @see Boolean#parseBoolean(String)
   */
  public static boolean parseBool (@Nullable final String sStr)
  {
    return Boolean.parseBoolean (sStr);
  }

  /**
   * Parse the given {@link String} as boolean value. All values that are equal
   * to "true" (ignoring case) will result in <code>true</code> return values.
   * All values that are equal to "false" (ignoring case) will result in
   * <code>false</code> return values. All other values will return the default.
   *
   * @param sStr
   *        The string to be interpreted. May be <code>null</code>.
   * @param bDefault
   *        The default value to be returned if the passed string is neither
   *        "true" nor "false".
   * @return <code>true</code> or <code>false</code> :)
   */
  public static boolean parseBool (@Nullable final String sStr, final boolean bDefault)
  {
    // Do we need to start thinking at all?
    if (sStr != null && sStr.length () > 0)
    {
      // Is it true?
      if (sStr.equalsIgnoreCase ("true"))
        return true;

      // Is it false?
      if (sStr.equalsIgnoreCase ("false"))
        return false;
    }

    // Neither true nor false
    return bDefault;
  }

  /**
   * Try to interpret the passed object as boolean. This works only if the
   * passed object is either a {@link String} or a {@link Boolean}.
   *
   * @param aObject
   *        The object to be interpreted. May be <code>null</code>.
   * @return <code>null</code> if the passed object cannot be interpreted as a
   *         boolean, any other {@link Boolean} otherwise.
   */
  @Nullable
  public static Boolean parseBoolObj (@Nullable final Object aObject)
  {
    return parseBoolObj (aObject, null);
  }

  /**
   * Try to interpret the passed {@link Object} as {@link Boolean}.
   *
   * @param aObject
   *        The object to be interpreted. May be <code>null</code>.
   * @param aDefault
   *        The default value to be returned, if the passed object is
   *        <code>null</code>.
   * @return The passed default value if the passed object is <code>null</code>,
   *         the matching {@link Boolean} otherwise.
   */
  @Nullable
  public static Boolean parseBoolObj (@Nullable final Object aObject, @Nullable final Boolean aDefault)
  {
    return aObject == null ? aDefault : parseBoolObj (aObject.toString ());
  }

  /**
   * Returns a <code>Boolean</code> with a value represented by the specified
   * string. The <code>Boolean</code> returned represents a <code>true</code>
   * value if the string argument is not <code>null</code> and is equal,
   * ignoring case, to the string {@code "true"}. This method is equal to
   * {@link Boolean#valueOf(String)}
   *
   * @param sStr
   *        The string to be parsed. May be <code>null</code>.
   * @return the <code>Boolean</code> value represented by the string. Never
   *         <code>null</code>.
   */
  @Nonnull
  public static Boolean parseBoolObj (@Nullable final String sStr)
  {
    return Boolean.valueOf (sStr);
  }

  /**
   * Returns a <code>Boolean</code> with a value represented by the specified
   * string. The <code>Boolean</code> returned represents a <code>true</code>
   * value if the string argument is not <code>null</code> and is equal,
   * ignoring case, to the string {@code "true"}, and it will return
   * <code>false</code> if the string argument is not <code>null</code> and is
   * equal, ignoring case, to the string {@code "false"}. In all other cases
   * <code>null</code> is returned.
   *
   * @param sStr
   *        The string to be parsed. May be <code>null</code>.
   * @return the <code>Boolean</code> value represented by the string. Never
   *         <code>null</code>.
   */
  @Nullable
  public static Boolean parseBoolObjExact (@Nullable final String sStr)
  {
    return parseBoolObjExact (sStr, null);
  }

  /**
   * Returns a <code>Boolean</code> with a value represented by the specified
   * string. The <code>Boolean</code> returned represents a <code>true</code>
   * value if the string argument is not <code>null</code> and is equal,
   * ignoring case, to the string {@code "true"}, and it will return
   * <code>false</code> if the string argument is not <code>null</code> and is
   * equal, ignoring case, to the string {@code "false"}. In all other cases
   * <code>aDefault</code> is returned.
   *
   * @param sStr
   *        The string to be parsed. May be <code>null</code>.
   * @param aDefault
   *        The default value to be returned if the value is neither "true" nor
   *        "false". May be <code>null</code>.
   * @return the <code>Boolean</code> value represented by the string. Never
   *         <code>null</code>.
   */
  @Nullable
  public static Boolean parseBoolObjExact (@Nullable final String sStr, @Nullable final Boolean aDefault)
  {
    if (Boolean.TRUE.toString ().equalsIgnoreCase (sStr))
      return Boolean.TRUE;
    if (Boolean.FALSE.toString ().equalsIgnoreCase (sStr))
      return Boolean.FALSE;
    return aDefault;
  }

  // ---[byte]---

  /**
   * Parse the given {@link Object} as byte with radix {@value #DEFAULT_RADIX}.
   *
   * @param aObject
   *        The Object to parse. May be <code>null</code>.
   * @param nDefault
   *        The default value to be returned if the passed object could not be
   *        converted to a valid value.
   * @return The default value if the string does not represent a valid value.
   */
  public static byte parseByte (@Nullable final Object aObject, final byte nDefault)
  {
    return parseByte (aObject, DEFAULT_RADIX, nDefault);
  }

  /**
   * Parse the given {@link Object} as byte with the specified radix.
   *
   * @param aObject
   *        The Object to parse. May be <code>null</code>.
   * @param nRadix
   *        The radix to use. Must be &ge; {@link Character#MIN_RADIX} and &le;
   *        {@link Character#MAX_RADIX}.
   * @param nDefault
   *        The default value to be returned if the passed object could not be
   *        converted to a valid value.
   * @return The default value if the string does not represent a valid value.
   */
  public static byte parseByte (@Nullable final Object aObject, @Nonnegative final int nRadix, final byte nDefault)
  {
    if (aObject == null)
      return nDefault;
    if (aObject instanceof Number)
      return ((Number) aObject).byteValue ();
    return parseByte (aObject.toString (), nRadix, nDefault);
  }

  /**
   * Parse the given {@link String} as byte with radix {@value #DEFAULT_RADIX}.
   *
   * @param sStr
   *        The String to parse. May be <code>null</code>.
   * @param nDefault
   *        The value to be returned if the string cannot be converted to a
   *        valid value.
   * @return The passed default parameter if the string does not represent a
   *         valid value.
   */
  public static byte parseByte (@Nullable final String sStr, final byte nDefault)
  {
    return parseByte (sStr, DEFAULT_RADIX, nDefault);
  }

  /**
   * Parse the given {@link String} as byte with the specified radix.
   *
   * @param sStr
   *        The String to parse. May be <code>null</code>.
   * @param nRadix
   *        The radix to use. Must be &ge; {@link Character#MIN_RADIX} and &le;
   *        {@link Character#MAX_RADIX}.
   * @param nDefault
   *        The value to be returned if the string cannot be converted to a
   *        valid value.
   * @return The passed default parameter if the string does not represent a
   *         valid value.
   */
  public static byte parseByte (@Nullable final String sStr, @Nonnegative final int nRadix, final byte nDefault)
  {
    if (sStr != null && sStr.length () > 0)
      try
      {
        return Byte.parseByte (sStr, nRadix);
      }
      catch (final NumberFormatException ex)
      {
        // Fall through
      }
    return nDefault;
  }

  /**
   * Parse the given {@link Object} as {@link Byte} with radix
   * {@value #DEFAULT_RADIX}.
   *
   * @param aObject
   *        The object to parse. May be <code>null</code>.
   * @return <code>null</code> if the object does not represent a valid value.
   */
  @Nullable
  public static Byte parseByteObj (@Nullable final Object aObject)
  {
    return parseByteObj (aObject, DEFAULT_RADIX, null);
  }

  /**
   * Parse the given {@link Object} as {@link Byte} with radix
   * {@value #DEFAULT_RADIX}.
   *
   * @param aObject
   *        The object to parse. May be <code>null</code>.
   * @param aDefault
   *        The default value to be returned, if the passed object cannot be
   *        converted. May be <code>null</code>.
   * @return the passed default value if the object does not represent a valid
   *         value.
   */
  @Nullable
  public static Byte parseByteObj (@Nullable final Object aObject, @Nullable final Byte aDefault)
  {
    return parseByteObj (aObject, DEFAULT_RADIX, aDefault);
  }

  /**
   * Parse the given {@link Object} as {@link Byte} with the specified radix.
   *
   * @param aObject
   *        The object to parse. May be <code>null</code>.
   * @param nRadix
   *        The radix to use. Must be &ge; {@link Character#MIN_RADIX} and &le;
   *        {@link Character#MAX_RADIX}.
   * @param aDefault
   *        The default value to be returned, if the passed object cannot be
   *        converted. May be <code>null</code>.
   * @return the passed default value if the object does not represent a valid
   *         value.
   */
  @Nullable
  public static Byte parseByteObj (@Nullable final Object aObject,
                                   @Nonnegative final int nRadix,
                                   @Nullable final Byte aDefault)
  {
    if (aObject == null)
      return aDefault;
    if (aObject instanceof Number)
      return Byte.valueOf (((Number) aObject).byteValue ());
    return parseByteObj (aObject.toString (), nRadix, aDefault);
  }

  /**
   * Parse the given {@link String} as {@link Byte} with radix
   * {@value #DEFAULT_RADIX}.
   *
   * @param sStr
   *        The String to parse. May be <code>null</code>.
   * @return <code>null</code> if the string does not represent a valid value.
   */
  @Nullable
  public static Byte parseByteObj (@Nullable final String sStr)
  {
    return parseByteObj (sStr, DEFAULT_RADIX, null);
  }

  /**
   * Parse the given {@link String} as {@link Byte} with radix
   * {@value #DEFAULT_RADIX}.
   *
   * @param sStr
   *        The String to parse. May be <code>null</code>.
   * @param aDefault
   *        The default value to be returned if the passed string could not be
   *        converted to a valid value. May be <code>null</code>.
   * @return <code>aDefault</code> if the string does not represent a valid
   *         value.
   */
  @Nullable
  public static Byte parseByteObj (@Nullable final String sStr, @Nullable final Byte aDefault)
  {
    return parseByteObj (sStr, DEFAULT_RADIX, aDefault);
  }

  /**
   * Parse the given {@link String} as {@link Byte} with the specified radix.
   *
   * @param sStr
   *        The String to parse. May be <code>null</code>.
   * @param nRadix
   *        The radix to use. Must be &ge; {@link Character#MIN_RADIX} and &le;
   *        {@link Character#MAX_RADIX}.
   * @param aDefault
   *        The default value to be returned if the passed string could not be
   *        converted to a valid value. May be <code>null</code>.
   * @return <code>aDefault</code> if the string does not represent a valid
   *         value.
   */
  @Nullable
  public static Byte parseByteObj (@Nullable final String sStr,
                                   @Nonnegative final int nRadix,
                                   @Nullable final Byte aDefault)
  {
    if (sStr != null && sStr.length () > 0)
      try
      {
        return Byte.valueOf (sStr, nRadix);
      }
      catch (final NumberFormatException ex)
      {
        // Fall through
      }
    return aDefault;
  }

  // ---[double]---

  /**
   * Parse the given {@link Object} as double. Note: both the locale independent
   * form of a double can be parsed here (e.g. 4.523) as well as a localized
   * form using the comma as the decimal separator (e.g. the German 4,523).
   *
   * @param aObject
   *        The object to parse. May be <code>null</code>.
   * @param dDefault
   *        The default value to be returned if the passed object could not be
   *        converted to a valid value.
   * @return The default value if the object does not represent a valid value.
   */
  public static double parseDouble (@Nullable final Object aObject, final double dDefault)
  {
    if (aObject == null)
      return dDefault;
    if (aObject instanceof Number)
      return ((Number) aObject).doubleValue ();
    return parseDouble (aObject.toString (), dDefault);
  }

  /**
   * Parse the given {@link String} as double.
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @param dDefault
   *        The default value to be returned if the passed object could not be
   *        converted to a valid value.
   * @return The default value if the string does not represent a valid value.
   */
  public static double parseDouble (@Nullable final String sStr, final double dDefault)
  {
    // parseDouble throws a NPE if parameter is null
    if (sStr != null && sStr.length () > 0)
      try
      {
        // Single point where we replace "," with "." for parsing!
        return Double.parseDouble (_getUnifiedDecimal (sStr));
      }
      catch (final NumberFormatException ex)
      {
        // Fall through
      }
    return dDefault;
  }

  /**
   * Parse the given {@link Object} as {@link Double}. Note: both the locale
   * independent form of a double can be parsed here (e.g. 4.523) as well as a
   * localized form using the comma as the decimal separator (e.g. the German
   * 4,523).
   *
   * @param aObject
   *        The object to parse. May be <code>null</code>.
   * @return <code>null</code> if the object does not represent a valid value.
   */
  @Nullable
  public static Double parseDoubleObj (@Nullable final Object aObject)
  {
    return parseDoubleObj (aObject, null);
  }

  /**
   * Parse the given {@link Object} as {@link Double}. Note: both the locale
   * independent form of a double can be parsed here (e.g. 4.523) as well as a
   * localized form using the comma as the decimal separator (e.g. the German
   * 4,523).
   *
   * @param aObject
   *        The object to parse. May be <code>null</code>.
   * @param aDefault
   *        The default value to be returned if the parsed object cannot be
   *        converted to a double. May be <code>null</code>.
   * @return <code>aDefault</code> if the object does not represent a valid
   *         value.
   */
  @Nullable
  public static Double parseDoubleObj (@Nullable final Object aObject, @Nullable final Double aDefault)
  {
    final double dValue = parseDouble (aObject, Double.NaN);
    return Double.isNaN (dValue) ? aDefault : Double.valueOf (dValue);
  }

  /**
   * Parse the given {@link String} as {@link Double}. Note: both the locale
   * independent form of a double can be parsed here (e.g. 4.523) as well as a
   * localized form using the comma as the decimal separator (e.g. the German
   * 4,523).
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @return <code>null</code> if the object does not represent a valid value.
   */
  @Nullable
  public static Double parseDoubleObj (@Nullable final String sStr)
  {
    return parseDoubleObj (sStr, null);
  }

  /**
   * Parse the given {@link String} as {@link Double}. Note: both the locale
   * independent form of a double can be parsed here (e.g. 4.523) as well as a
   * localized form using the comma as the decimal separator (e.g. the German
   * 4,523).
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @param aDefault
   *        The default value to be returned if the parsed string cannot be
   *        converted to a double. May be <code>null</code>.
   * @return <code>aDefault</code> if the object does not represent a valid
   *         value.
   */
  @Nullable
  public static Double parseDoubleObj (@Nullable final String sStr, @Nullable final Double aDefault)
  {
    final double dValue = parseDouble (sStr, Double.NaN);
    return Double.isNaN (dValue) ? aDefault : Double.valueOf (dValue);
  }

  // ---[float]---

  /**
   * Parse the given {@link Object} as float. Note: both the locale independent
   * form of a float can be parsed here (e.g. 4.523) as well as a localized form
   * using the comma as the decimal separator (e.g. the German 4,523).
   *
   * @param aObject
   *        The object to parse. May be <code>null</code>.
   * @param fDefault
   *        The default value to be returned if the passed object could not be
   *        converted to a valid value.
   * @return The default value if the object does not represent a valid value.
   */
  public static float parseFloat (@Nullable final Object aObject, final float fDefault)
  {
    if (aObject == null)
      return fDefault;
    if (aObject instanceof Number)
      return ((Number) aObject).floatValue ();
    return parseFloat (aObject.toString (), fDefault);
  }

  /**
   * Parse the given {@link String} as float.
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @param fDefault
   *        The default value to be returned if the passed object could not be
   *        converted to a valid value.
   * @return The default value if the string does not represent a valid value.
   */
  public static float parseFloat (@Nullable final String sStr, final float fDefault)
  {
    // parseDouble throws a NPE if parameter is null
    if (sStr != null && sStr.length () > 0)
      try
      {
        // Single point where we replace "," with "." for parsing!
        return Float.parseFloat (_getUnifiedDecimal (sStr));
      }
      catch (final NumberFormatException ex)
      {
        // Fall through
      }
    return fDefault;
  }

  /**
   * Parse the given {@link Object} as {@link Float}. Note: both the locale
   * independent form of a double can be parsed here (e.g. 4.523) as well as a
   * localized form using the comma as the decimal separator (e.g. the German
   * 4,523).
   *
   * @param aObject
   *        The object to parse. May be <code>null</code>.
   * @return <code>null</code> if the object does not represent a valid value.
   */
  @Nullable
  public static Float parseFloatObj (@Nullable final Object aObject)
  {
    return parseFloatObj (aObject, null);
  }

  /**
   * Parse the given {@link Object} as {@link Float}. Note: both the locale
   * independent form of a double can be parsed here (e.g. 4.523) as well as a
   * localized form using the comma as the decimal separator (e.g. the German
   * 4,523).
   *
   * @param aObject
   *        The object to parse. May be <code>null</code>.
   * @param aDefault
   *        The default value to be returned if the parsed object cannot be
   *        converted to a float. May be <code>null</code>.
   * @return <code>aDefault</code> if the object does not represent a valid
   *         value.
   */
  @Nullable
  public static Float parseFloatObj (@Nullable final Object aObject, @Nullable final Float aDefault)
  {
    final float fValue = parseFloat (aObject, Float.NaN);
    return Float.isNaN (fValue) ? aDefault : Float.valueOf (fValue);
  }

  /**
   * Parse the given {@link String} as {@link Float}. Note: both the locale
   * independent form of a double can be parsed here (e.g. 4.523) as well as a
   * localized form using the comma as the decimal separator (e.g. the German
   * 4,523).
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @return <code>null</code> if the object does not represent a valid value.
   */
  @Nullable
  public static Float parseFloatObj (@Nullable final String sStr)
  {
    return parseFloatObj (sStr, null);
  }

  /**
   * Parse the given {@link String} as {@link Float}. Note: both the locale
   * independent form of a double can be parsed here (e.g. 4.523) as well as a
   * localized form using the comma as the decimal separator (e.g. the German
   * 4,523).
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @param aDefault
   *        The default value to be returned if the parsed string cannot be
   *        converted to a float. May be <code>null</code>.
   * @return <code>aDefault</code> if the object does not represent a valid
   *         value.
   */
  @Nullable
  public static Float parseFloatObj (@Nullable final String sStr, @Nullable final Float aDefault)
  {
    final float fValue = parseFloat (sStr, Float.NaN);
    return Float.isNaN (fValue) ? aDefault : Float.valueOf (fValue);
  }

  // ---[int]---

  /**
   * Parse the given {@link Object} as int with radix {@link #DEFAULT_RADIX}.
   *
   * @param aObject
   *        The Object to parse. May be <code>null</code>.
   * @param nDefault
   *        The default value to be returned if the passed object could not be
   *        converted to a valid value.
   * @return The default value if the string does not represent a valid value.
   */
  public static int parseInt (@Nullable final Object aObject, final int nDefault)
  {
    return parseInt (aObject, DEFAULT_RADIX, nDefault);
  }

  /**
   * Parse the given {@link Object} as int with the specified radix.
   *
   * @param aObject
   *        The object to parse. May be <code>null</code>.
   * @param nRadix
   *        The radix to use. Must be &ge; {@link Character#MIN_RADIX} and &le;
   *        {@link Character#MAX_RADIX}.
   * @param nDefault
   *        The default value to be returned if the passed object could not be
   *        converted to a valid value.
   * @return The default value if the string does not represent a valid value.
   */
  public static int parseInt (@Nullable final Object aObject, @Nonnegative final int nRadix, final int nDefault)
  {
    if (aObject == null)
      return nDefault;
    if (aObject instanceof Number)
      return ((Number) aObject).intValue ();
    return parseInt (aObject.toString (), nRadix, nDefault);
  }

  /**
   * Parse the given {@link String} as int with radix {@value #DEFAULT_RADIX}.
   *
   * @param sStr
   *        The String to parse. May be <code>null</code>.
   * @param nDefault
   *        The value to be returned if the string cannot be converted to a
   *        valid value.
   * @return The passed default parameter if the string does not represent a
   *         valid value.
   */
  public static int parseInt (@Nullable final String sStr, final int nDefault)
  {
    return parseInt (sStr, DEFAULT_RADIX, nDefault);
  }

  /**
   * Parse the given {@link String} as int with the specified radix.
   *
   * @param sStr
   *        The String to parse. May be <code>null</code>.
   * @param nRadix
   *        The radix to use. Must be &ge; {@link Character#MIN_RADIX} and &le;
   *        {@link Character#MAX_RADIX}.
   * @param nDefault
   *        The value to be returned if the string cannot be converted to a
   *        valid value.
   * @return The passed default parameter if the string does not represent a
   *         valid value.
   */
  public static int parseInt (@Nullable final String sStr, @Nonnegative final int nRadix, final int nDefault)
  {
    if (sStr != null && sStr.length () > 0)
      try
      {
        return Integer.parseInt (sStr, nRadix);
      }
      catch (final NumberFormatException ex)
      {
        // Fall through
      }
    return nDefault;
  }

  /**
   * Parse the given {@link Object} as {@link Integer} with radix
   * {@value #DEFAULT_RADIX}.
   *
   * @param aObject
   *        The object to parse. May be <code>null</code>.
   * @return <code>null</code> if the object does not represent a valid value.
   */
  @Nullable
  public static Integer parseIntObj (@Nullable final Object aObject)
  {
    return parseIntObj (aObject, DEFAULT_RADIX, null);
  }

  /**
   * Parse the given {@link Object} as {@link Integer} with radix
   * {@value #DEFAULT_RADIX}.
   *
   * @param aObject
   *        The object to parse. May be <code>null</code>.
   * @param aDefault
   *        The default value to be returned if the passed object cannot be
   *        converted to an Integer. May be <code>null</code>.
   * @return <code>aDefault</code> if the object does not represent a valid
   *         value.
   */
  @Nullable
  public static Integer parseIntObj (@Nullable final Object aObject, @Nullable final Integer aDefault)
  {
    return parseIntObj (aObject, DEFAULT_RADIX, aDefault);
  }

  /**
   * Parse the given {@link Object} as {@link Integer} with the specified radix.
   *
   * @param aObject
   *        The object to parse. May be <code>null</code>.
   * @param nRadix
   *        The radix to use. Must be &ge; {@link Character#MIN_RADIX} and &le;
   *        {@link Character#MAX_RADIX}.
   * @param aDefault
   *        The default value to be returned if the passed object cannot be
   *        converted to an Integer. May be <code>null</code>.
   * @return <code>aDefault</code> if the object does not represent a valid
   *         value.
   */
  @Nullable
  public static Integer parseIntObj (@Nullable final Object aObject,
                                     @Nonnegative final int nRadix,
                                     @Nullable final Integer aDefault)
  {
    if (aObject == null)
      return aDefault;
    if (aObject instanceof Number)
      return Integer.valueOf (((Number) aObject).intValue ());
    return parseIntObj (aObject.toString (), nRadix, aDefault);
  }

  /**
   * Parse the given {@link String} as {@link Integer} with radix
   * {@value #DEFAULT_RADIX}.
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @return <code>null</code> if the string does not represent a valid value.
   */
  @Nullable
  public static Integer parseIntObj (@Nullable final String sStr)
  {
    return parseIntObj (sStr, DEFAULT_RADIX, null);
  }

  /**
   * Parse the given {@link String} as {@link Integer} with radix
   * {@link #DEFAULT_RADIX}.
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @param aDefault
   *        The default value to be returned if the passed string could not be
   *        converted to a valid value. May be <code>null</code>.
   * @return <code>aDefault</code> if the string does not represent a valid
   *         value.
   */
  @Nullable
  public static Integer parseIntObj (@Nullable final String sStr, @Nullable final Integer aDefault)
  {
    return parseIntObj (sStr, DEFAULT_RADIX, aDefault);
  }

  /**
   * Parse the given {@link String} as {@link Integer} with the specified radix.
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @param nRadix
   *        The radix to use. Must be &ge; {@link Character#MIN_RADIX} and &le;
   *        {@link Character#MAX_RADIX}.
   * @param aDefault
   *        The default value to be returned if the passed string could not be
   *        converted to a valid value. May be <code>null</code>.
   * @return <code>aDefault</code> if the string does not represent a valid
   *         value.
   */
  @Nullable
  public static Integer parseIntObj (@Nullable final String sStr,
                                     @Nonnegative final int nRadix,
                                     @Nullable final Integer aDefault)
  {
    if (sStr != null && sStr.length () > 0)
      try
      {
        return Integer.valueOf (sStr, nRadix);
      }
      catch (final NumberFormatException ex)
      {
        // Fall through
      }
    return aDefault;
  }

  // ---[long]---

  /**
   * Parse the given {@link Object} as long with radix {@value #DEFAULT_RADIX}.
   *
   * @param aObject
   *        The object to parse. May be <code>null</code>.
   * @param nDefault
   *        The default value to be returned if the passed object could not be
   *        converted to a valid value.
   * @return The default value if the object does not represent a valid value.
   */
  public static long parseLong (@Nullable final Object aObject, final long nDefault)
  {
    return parseLong (aObject, DEFAULT_RADIX, nDefault);
  }

  /**
   * Parse the given {@link Object} as long with the specified radix.
   *
   * @param aObject
   *        The object to parse. May be <code>null</code>.
   * @param nRadix
   *        The radix to use. Must be &ge; {@link Character#MIN_RADIX} and &le;
   *        {@link Character#MAX_RADIX}.
   * @param nDefault
   *        The default value to be returned if the passed object could not be
   *        converted to a valid value.
   * @return The default value if the object does not represent a valid value.
   */
  public static long parseLong (@Nullable final Object aObject, @Nonnegative final int nRadix, final long nDefault)
  {
    if (aObject == null)
      return nDefault;
    if (aObject instanceof Number)
      return ((Number) aObject).longValue ();
    return parseLong (aObject.toString (), nRadix, nDefault);
  }

  /**
   * Parse the given {@link String} as long with radix {@value #DEFAULT_RADIX}.
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @param nDefault
   *        The default value to be returned if the passed object could not be
   *        converted to a valid value.
   * @return The default if the string does not represent a valid value.
   */
  public static long parseLong (@Nullable final String sStr, final long nDefault)
  {
    return parseLong (sStr, DEFAULT_RADIX, nDefault);
  }

  /**
   * Parse the given {@link String} as long with the specified radix.
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @param nRadix
   *        The radix to use. Must be &ge; {@link Character#MIN_RADIX} and &le;
   *        {@link Character#MAX_RADIX}.
   * @param nDefault
   *        The default value to be returned if the passed object could not be
   *        converted to a valid value.
   * @return The default if the string does not represent a valid value.
   */
  public static long parseLong (@Nullable final String sStr, @Nonnegative final int nRadix, final long nDefault)
  {
    if (sStr != null && sStr.length () > 0)
      try
      {
        return Long.parseLong (sStr, nRadix);
      }
      catch (final NumberFormatException ex)
      {
        // Fall through
      }
    return nDefault;
  }

  /**
   * Parse the given {@link Object} as {@link Long} with radix
   * {@value #DEFAULT_RADIX}.
   *
   * @param aObject
   *        The object to parse. May be <code>null</code>.
   * @return <code>null</code> if the object does not represent a valid value.
   */
  @Nullable
  public static Long parseLongObj (@Nullable final Object aObject)
  {
    return parseLongObj (aObject, DEFAULT_RADIX, null);
  }

  /**
   * Parse the given {@link Object} as {@link Long} with radix
   * {@value #DEFAULT_RADIX}.
   *
   * @param aObject
   *        The object to parse. May be <code>null</code>.
   * @param aDefault
   *        The default value to be returned if the passed object cannot be
   *        converted to a Long. May be <code>null</code>.
   * @return <code>aDefault</code> if the object does not represent a valid
   *         value.
   */
  @Nullable
  public static Long parseLongObj (@Nullable final Object aObject, @Nullable final Long aDefault)
  {
    return parseLongObj (aObject, DEFAULT_RADIX, aDefault);
  }

  /**
   * Parse the given {@link Object} as {@link Long} with the specified radix.
   *
   * @param aObject
   *        The object to parse. May be <code>null</code>.
   * @param nRadix
   *        The radix to use. Must be &ge; {@link Character#MIN_RADIX} and &le;
   *        {@link Character#MAX_RADIX}.
   * @param aDefault
   *        The default value to be returned if the passed object cannot be
   *        converted to a Long. May be <code>null</code>.
   * @return <code>aDefault</code> if the object does not represent a valid
   *         value.
   */
  @Nullable
  public static Long parseLongObj (@Nullable final Object aObject,
                                   @Nonnegative final int nRadix,
                                   @Nullable final Long aDefault)
  {
    if (aObject == null)
      return aDefault;
    if (aObject instanceof Number)
      return Long.valueOf (((Number) aObject).longValue ());
    return parseLongObj (aObject.toString (), nRadix, aDefault);
  }

  /**
   * Parse the given {@link String} as {@link Long} with radix
   * {@value #DEFAULT_RADIX}.
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @return <code>null</code> if the string does not represent a valid value.
   */
  @Nullable
  public static Long parseLongObj (@Nullable final String sStr)
  {
    return parseLongObj (sStr, DEFAULT_RADIX, null);
  }

  /**
   * Parse the given {@link String} as {@link Long} with radix
   * {@link #DEFAULT_RADIX}.
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @param aDefault
   *        The default value to be returned if the passed string could not be
   *        converted to a valid value. May be <code>null</code>.
   * @return <code>aDefault</code> if the string does not represent a valid
   *         value.
   */
  @Nullable
  public static Long parseLongObj (@Nullable final String sStr, @Nullable final Long aDefault)
  {
    return parseLongObj (sStr, DEFAULT_RADIX, aDefault);
  }

  /**
   * Parse the given {@link String} as {@link Long} with the specified radix.
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @param nRadix
   *        The radix to use. Must be &ge; {@link Character#MIN_RADIX} and &le;
   *        {@link Character#MAX_RADIX}.
   * @param aDefault
   *        The default value to be returned if the passed string could not be
   *        converted to a valid value. May be <code>null</code>.
   * @return <code>aDefault</code> if the string does not represent a valid
   *         value.
   */
  @Nullable
  public static Long parseLongObj (@Nullable final String sStr,
                                   @Nonnegative final int nRadix,
                                   @Nullable final Long aDefault)
  {
    if (sStr != null && sStr.length () > 0)
      try
      {
        return Long.valueOf (sStr, nRadix);
      }
      catch (final NumberFormatException ex)
      {
        // Fall through
      }
    return aDefault;
  }

  // ---[short]---

  /**
   * Parse the given {@link Object} as short with radix {@value #DEFAULT_RADIX}.
   *
   * @param aObject
   *        The object to parse. May be <code>null</code>.
   * @param nDefault
   *        The default value to be returned if the passed object could not be
   *        converted to a valid value.
   * @return The default value if the object does not represent a valid value.
   */
  public static short parseShort (@Nullable final Object aObject, final short nDefault)
  {
    return parseShort (aObject, DEFAULT_RADIX, nDefault);
  }

  /**
   * Parse the given {@link Object} as short with the specified radix.
   *
   * @param aObject
   *        The object to parse. May be <code>null</code>.
   * @param nRadix
   *        The radix to use. Must be &ge; {@link Character#MIN_RADIX} and &le;
   *        {@link Character#MAX_RADIX}.
   * @param nDefault
   *        The default value to be returned if the passed object could not be
   *        converted to a valid value.
   * @return The default value if the object does not represent a valid value.
   */
  public static short parseShort (@Nullable final Object aObject, @Nonnegative final int nRadix, final short nDefault)
  {
    if (aObject == null)
      return nDefault;
    if (aObject instanceof Number)
      return ((Number) aObject).shortValue ();
    return parseShort (aObject.toString (), nRadix, nDefault);
  }

  /**
   * Parse the given {@link String} as short with radix {@value #DEFAULT_RADIX}.
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @param nDefault
   *        The default value to be returned if the passed object could not be
   *        converted to a valid value.
   * @return The default if the string does not represent a valid value.
   */
  public static short parseShort (@Nullable final String sStr, final short nDefault)
  {
    return parseShort (sStr, DEFAULT_RADIX, nDefault);
  }

  /**
   * Parse the given {@link String} as short with the specified radix.
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @param nRadix
   *        The radix to use. Must be &ge; {@link Character#MIN_RADIX} and &le;
   *        {@link Character#MAX_RADIX}.
   * @param nDefault
   *        The default value to be returned if the passed object could not be
   *        converted to a valid value.
   * @return The default if the string does not represent a valid value.
   */
  public static short parseShort (@Nullable final String sStr, @Nonnegative final int nRadix, final short nDefault)
  {
    if (sStr != null && sStr.length () > 0)
      try
      {
        return Short.parseShort (sStr, nRadix);
      }
      catch (final NumberFormatException ex)
      {
        // Fall through
      }
    return nDefault;
  }

  /**
   * Parse the given {@link Object} as {@link Short} with radix
   * {@value #DEFAULT_RADIX}.
   *
   * @param aObject
   *        The object to parse. May be <code>null</code>.
   * @return <code>null</code> if the object does not represent a valid value.
   */
  @Nullable
  public static Short parseShortObj (@Nullable final Object aObject)
  {
    return parseShortObj (aObject, DEFAULT_RADIX, null);
  }

  /**
   * Parse the given {@link Object} as {@link Short} with radix
   * {@value #DEFAULT_RADIX}.
   *
   * @param aObject
   *        The object to parse. May be <code>null</code>.
   * @param aDefault
   *        The default value to be returned if the passed object cannot be
   *        converted to a Short. May be <code>null</code>.
   * @return <code>aDefault</code> if the object does not represent a valid
   *         value.
   */
  @Nullable
  public static Short parseShortObj (@Nullable final Object aObject, @Nullable final Short aDefault)
  {
    return parseShortObj (aObject, DEFAULT_RADIX, aDefault);
  }

  /**
   * Parse the given {@link Object} as {@link Short} with the specified radix.
   *
   * @param aObject
   *        The object to parse. May be <code>null</code>.
   * @param nRadix
   *        The radix to use. Must be &ge; {@link Character#MIN_RADIX} and &le;
   *        {@link Character#MAX_RADIX}.
   * @param aDefault
   *        The default value to be returned if the passed object cannot be
   *        converted to a Short. May be <code>null</code>.
   * @return <code>aDefault</code> if the object does not represent a valid
   *         value.
   */
  @Nullable
  public static Short parseShortObj (@Nullable final Object aObject,
                                     @Nonnegative final int nRadix,
                                     @Nullable final Short aDefault)
  {
    if (aObject == null)
      return aDefault;
    if (aObject instanceof Number)
      return Short.valueOf (((Number) aObject).shortValue ());
    return parseShortObj (aObject.toString (), nRadix, aDefault);
  }

  /**
   * Parse the given {@link String} as {@link Short} with radix
   * {@value #DEFAULT_RADIX}.
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @return <code>null</code> if the string does not represent a valid value.
   */
  @Nullable
  public static Short parseShortObj (@Nullable final String sStr)
  {
    return parseShortObj (sStr, DEFAULT_RADIX, null);
  }

  /**
   * Parse the given {@link String} as {@link Short} with radix
   * {@link #DEFAULT_RADIX}.
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @param aDefault
   *        The default value to be returned if the passed string could not be
   *        converted to a valid value. May be <code>null</code>.
   * @return <code>aDefault</code> if the string does not represent a valid
   *         value.
   */
  @Nullable
  public static Short parseShortObj (@Nullable final String sStr, @Nullable final Short aDefault)
  {
    return parseShortObj (sStr, DEFAULT_RADIX, aDefault);
  }

  /**
   * Parse the given {@link String} as {@link Short} with the specified radix.
   *
   * @param sStr
   *        The string to parse. May be <code>null</code>.
   * @param nRadix
   *        The radix to use. Must be &ge; {@link Character#MIN_RADIX} and &le;
   *        {@link Character#MAX_RADIX}.
   * @param aDefault
   *        The default value to be returned if the passed string could not be
   *        converted to a valid value. May be <code>null</code>.
   * @return <code>aDefault</code> if the string does not represent a valid
   *         value.
   */
  @Nullable
  public static Short parseShortObj (@Nullable final String sStr,
                                     @Nonnegative final int nRadix,
                                     @Nullable final Short aDefault)
  {
    if (sStr != null && sStr.length () > 0)
      try
      {
        return Short.valueOf (sStr, nRadix);
      }
      catch (final NumberFormatException ex)
      {
        // Fall through
      }
    return aDefault;
  }

  // ---[BigInteger]---

  /**
   * Parse the given {@link String} as {@link BigInteger} with radix
   * {@value #DEFAULT_RADIX}.
   *
   * @param sStr
   *        The String to parse. May be <code>null</code>.
   * @return <code>null</code> if the string does not represent a valid value.
   */
  @Nullable
  public static BigInteger parseBigInteger (@Nullable final String sStr)
  {
    return parseBigInteger (sStr, DEFAULT_RADIX, null);
  }

  /**
   * Parse the given {@link String} as {@link BigInteger} with the specified
   * radix.
   *
   * @param sStr
   *        The String to parse. May be <code>null</code>.
   * @param nRadix
   *        The radix to use. Must be &ge; {@link Character#MIN_RADIX} and &le;
   *        {@link Character#MAX_RADIX}.
   * @return <code>null</code> if the string does not represent a valid value.
   */
  @Nullable
  public static BigInteger parseBigInteger (@Nullable final String sStr, @Nonnegative final int nRadix)
  {
    return parseBigInteger (sStr, nRadix, null);
  }

  /**
   * Parse the given {@link String} as {@link BigInteger} with radix
   * {@value #DEFAULT_RADIX}.
   *
   * @param sStr
   *        The String to parse. May be <code>null</code>.
   * @param aDefault
   *        The default value to be returned if the passed string could not be
   *        converted to a valid value. May be <code>null</code>.
   * @return <code>aDefault</code> if the string does not represent a valid
   *         value.
   */
  @Nullable
  public static BigInteger parseBigInteger (@Nullable final String sStr, @Nullable final BigInteger aDefault)
  {
    return parseBigInteger (sStr, DEFAULT_RADIX, aDefault);
  }

  /**
   * Parse the given {@link String} as {@link BigInteger} with the specified
   * radix.
   *
   * @param sStr
   *        The String to parse. May be <code>null</code>.
   * @param nRadix
   *        The radix to use. Must be &ge; {@link Character#MIN_RADIX} and &le;
   *        {@link Character#MAX_RADIX}.
   * @param aDefault
   *        The default value to be returned if the passed string could not be
   *        converted to a valid value. May be <code>null</code>.
   * @return <code>aDefault</code> if the string does not represent a valid
   *         value.
   */
  @Nullable
  public static BigInteger parseBigInteger (@Nullable final String sStr,
                                            @Nonnegative final int nRadix,
                                            @Nullable final BigInteger aDefault)
  {
    if (sStr != null && sStr.length () > 0)
      try
      {
        return new BigInteger (sStr, nRadix);
      }
      catch (final NumberFormatException ex)
      {
        // Fall through
      }
    return aDefault;
  }

  // ---[BigDecimal]---

  /**
   * Parse the given {@link String} as {@link BigDecimal}.
   *
   * @param sStr
   *        The String to parse. May be <code>null</code>.
   * @return <code>null</code> if the string does not represent a valid value.
   */
  @Nullable
  public static BigDecimal parseBigDecimal (@Nullable final String sStr)
  {
    return parseBigDecimal (sStr, null);
  }

  /**
   * Parse the given {@link String} as {@link BigDecimal}.
   *
   * @param sStr
   *        The String to parse. May be <code>null</code>.
   * @param aDefault
   *        The default value to be returned if the passed string could not be
   *        converted to a valid value. May be <code>null</code>.
   * @return <code>aDefault</code> if the string does not represent a valid
   *         value.
   */
  @Nullable
  public static BigDecimal parseBigDecimal (@Nullable final String sStr, @Nullable final BigDecimal aDefault)
  {
    if (sStr != null && sStr.length () > 0)
      try
      {
        return new BigDecimal (_getUnifiedDecimal (sStr));
      }
      catch (final NumberFormatException ex)
      {
        // Fall through
      }
    return aDefault;
  }

  /**
   * Parse the given {@link String} as {@link BigDecimal}.
   *
   * @param sStr
   *        The String to parse. May be <code>null</code>.
   * @param nScale
   *        The scaling (decimal places) to be used for the result. Must be &ge;
   *        0!
   * @param eRoundingMode
   *        The rounding mode to be used to achieve the scale. May not be
   *        <code>null</code>.
   * @return <code>null</code> if the string does not represent a valid value.
   */
  @Nullable
  public static BigDecimal parseBigDecimal (@Nullable final String sStr,
                                            @Nonnegative final int nScale,
                                            @Nonnull final RoundingMode eRoundingMode)
  {
    return parseBigDecimal (sStr, nScale, eRoundingMode, null);
  }

  /**
   * Parse the given {@link String} as {@link BigDecimal}.
   *
   * @param sStr
   *        The String to parse. May be <code>null</code>.
   * @param nScale
   *        The scaling (decimal places) to be used for the result. Must be &ge;
   *        0!
   * @param eRoundingMode
   *        The rounding mode to be used to achieve the scale. May not be
   *        <code>null</code>.
   * @param aDefault
   *        The default value to be returned if the passed string could not be
   *        converted to a valid value. May be <code>null</code>.
   * @return <code>aDefault</code> if the string does not represent a valid
   *         value.
   */
  @Nullable
  public static BigDecimal parseBigDecimal (@Nullable final String sStr,
                                            @Nonnegative final int nScale,
                                            @Nonnull final RoundingMode eRoundingMode,
                                            @Nullable final BigDecimal aDefault)
  {
    ValueEnforcer.isGE0 (nScale, "Scale");
    ValueEnforcer.notNull (eRoundingMode, "RoundingMode");

    if (sStr != null && sStr.length () > 0)
      try
      {
        return new BigDecimal (_getUnifiedDecimal (sStr)).setScale (nScale, eRoundingMode);
      }
      catch (final NumberFormatException ex)
      {
        // Fall through
      }
    return aDefault;
  }

  // ---[check methods]---

  /**
   * Checks if the given string is a numeric string that can be converted to a
   * long value with radix {@value #DEFAULT_RADIX}.
   *
   * @param sStr
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the value can be converted to a valid value
   */
  public static boolean isInt (@Nullable final String sStr)
  {
    if (sStr != null)
      try
      {
        Integer.parseInt (sStr, DEFAULT_RADIX);
        return true;
      }
      catch (final NumberFormatException ex)
      {
        // fall through
      }
    return false;
  }

  /**
   * Checks if the given string is a numeric string that can be converted to a
   * long value with radix {@value #DEFAULT_RADIX}.
   *
   * @param sStr
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the value can be converted to a valid value
   */
  public static boolean isLong (@Nullable final String sStr)
  {
    if (sStr != null)
      try
      {
        Long.parseLong (sStr, DEFAULT_RADIX);
        return true;
      }
      catch (final NumberFormatException ex)
      {
        // fall through
      }
    return false;
  }

  /**
   * Checks if the given string is a numeric string that can be converted to an
   * unsigned long value with radix {@value #DEFAULT_RADIX}.
   *
   * @param sStr
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the value can be converted to a valid value
   */
  public static boolean isUnsignedInt (@Nullable final String sStr)
  {
    if (sStr != null)
      try
      {
        final int ret = Integer.parseInt (sStr, DEFAULT_RADIX);
        return ret >= 0;
      }
      catch (final NumberFormatException ex)
      {
        // fall through
      }
    return false;
  }

  /**
   * Checks if the given string is a numeric string that can be converted to an
   * unsigned long value with radix {@value #DEFAULT_RADIX}.
   *
   * @param sStr
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the value can be converted to a valid value
   */
  public static boolean isUnsignedLong (@Nullable final String sStr)
  {
    if (sStr != null)
      try
      {
        final long ret = Long.parseLong (sStr, DEFAULT_RADIX);
        return ret >= 0;
      }
      catch (final NumberFormatException ex)
      {
        // fall through
      }
    return false;
  }

  /**
   * Checks if the given string is a double string that can be converted to a
   * double value.
   *
   * @param sStr
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the value can be converted to a valid value
   */
  public static boolean isDouble (@Nullable final String sStr)
  {
    return !Double.isNaN (parseDouble (sStr, Double.NaN));
  }

  /**
   * Checks if the given string is a float string that can be converted to a
   * double value.
   *
   * @param sStr
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the value can be converted to a valid value
   */
  public static boolean isFloat (@Nullable final String sStr)
  {
    return !Float.isNaN (parseFloat (sStr, Float.NaN));
  }
}
