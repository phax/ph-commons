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
package com.helger.commons.collection.attr;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.typeconvert.TypeConverter;

/**
 * Contains static conversion routines used by the attribute container classes.
 *
 * @author Philip Helger
 */
@Immutable
public final class AttributeValueConverter
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (AttributeValueConverter.class);

  private AttributeValueConverter ()
  {}

  @Nullable
  private static String _getAsSingleString (@Nullable final Object aParamName,
                                            @Nonnull final String [] aArray,
                                            @Nullable final String sDefault)
  {
    // expected a single string but got an array
    s_aLogger.warn ("The attribute '" +
                    String.valueOf (aParamName) +
                    "' is an array with " +
                    aArray.length +
                    " items; using the first one if possible: " +
                    Arrays.toString (aArray));
    return aArray.length > 0 ? aArray[0] : sDefault;
  }

  /**
   * Get the string representation of the passed value, suitable for parameters.
   *
   * @param aParamName
   *        The name of the parameters. Has just informal character, for
   *        warnings. May be <code>null</code>.
   * @param aValue
   *        The value to be converted to a String. May be <code>null</code>.
   *        Explicitly supported data types are: String, String[]. All other
   *        data types are returned as "toString()".
   * @param sDefault
   *        The default value to be returned, if the passed value is
   *        <code>null</code> or an empty String array.
   * @return The default value if the value is <code>null</code> or an empty
   *         String array, the value as string otherwise. If the value is a
   *         String[] than the first element is returned, and the other elements
   *         are discarded.
   */
  @Nullable
  public static String getAsString (@Nullable final Object aParamName,
                                    @Nullable final Object aValue,
                                    @Nullable final String sDefault)
  {
    if (aValue == null)
      return sDefault;
    if (aValue instanceof String)
      return (String) aValue;
    if (aValue instanceof String [])
      return _getAsSingleString (aParamName, (String []) aValue, sDefault);

    return TypeConverter.convertIfNecessary (aValue, String.class, sDefault);
  }

  public static int getAsInt (@Nullable final Object aParamName, @Nullable final Object aValue, final int nDefault)
  {
    if (aValue == null)
      return nDefault;
    if (aValue instanceof Number)
      return ((Number) aValue).intValue ();
    if (aValue instanceof String [])
      return TypeConverter.convertToInt (_getAsSingleString (aParamName, (String []) aValue, null), nDefault);

    return TypeConverter.convertToInt (aValue, nDefault);
  }

  public static long getAsLong (@Nullable final Object aParamName, @Nullable final Object aValue, final long nDefault)
  {
    if (aValue == null)
      return nDefault;
    if (aValue instanceof Number)
      return ((Number) aValue).longValue ();
    if (aValue instanceof String [])
      return TypeConverter.convertToLong (_getAsSingleString (aParamName, (String []) aValue, null), nDefault);

    return TypeConverter.convertToLong (aValue, nDefault);
  }

  public static double getAsDouble (@Nullable final Object aParamName,
                                    @Nullable final Object aValue,
                                    final double dDefault)
  {
    if (aValue == null)
      return dDefault;
    if (aValue instanceof Number)
      return ((Number) aValue).doubleValue ();
    if (aValue instanceof String [])
      return TypeConverter.convertToDouble (_getAsSingleString (aParamName, (String []) aValue, null), dDefault);

    return TypeConverter.convertToDouble (aValue, dDefault);
  }

  public static float getAsFloat (@Nullable final Object aParamName,
                                  @Nullable final Object aValue,
                                  final float fDefault)
  {
    if (aValue == null)
      return fDefault;
    if (aValue instanceof Number)
      return ((Number) aValue).floatValue ();
    if (aValue instanceof String [])
      return TypeConverter.convertToFloat (_getAsSingleString (aParamName, (String []) aValue, null), fDefault);

    return TypeConverter.convertToFloat (aValue, fDefault);
  }

  public static boolean getAsBoolean (@Nullable final Object aParamName,
                                      @Nullable final Object aValue,
                                      final boolean bDefault)
  {
    if (aValue == null)
      return bDefault;
    if (aValue instanceof Boolean)
      return ((Boolean) aValue).booleanValue ();
    if (aValue instanceof String [])
      return TypeConverter.convertToBoolean (_getAsSingleString (aParamName, (String []) aValue, null), bDefault);

    return TypeConverter.convertToBoolean (aValue, bDefault);
  }

  @Nullable
  public static BigInteger getAsBigInteger (@Nullable final Object aParamName,
                                            @Nullable final Object aValue,
                                            @Nullable final BigInteger aDefault)
  {
    if (aValue instanceof String [])
      return TypeConverter.convertIfNecessary (_getAsSingleString (aParamName, (String []) aValue, null),
                                               BigInteger.class,
                                               aDefault);

    return TypeConverter.convertIfNecessary (aValue, BigInteger.class, aDefault);
  }

  @Nullable
  public static BigDecimal getAsBigDecimal (@Nullable final Object aParamName,
                                            @Nullable final Object aValue,
                                            @Nullable final BigDecimal aDefault)
  {
    if (aValue instanceof String [])
      return TypeConverter.convertIfNecessary (_getAsSingleString (aParamName, (String []) aValue, null),
                                               BigDecimal.class,
                                               aDefault);

    return TypeConverter.convertIfNecessary (aValue, BigDecimal.class, aDefault);
  }
}
