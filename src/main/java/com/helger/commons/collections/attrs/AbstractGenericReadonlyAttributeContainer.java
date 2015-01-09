/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.collections.attrs;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.CGlobal;
import com.helger.commons.lang.GenericReflection;
import com.helger.commons.string.StringParser;
import com.helger.commons.typeconvert.TypeConverter;

/**
 * Abstract base class for all kind of string-object mapping container. This
 * implementation provides a default implementation for all things that can be
 * independently implemented from the underlying data structure.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Key type
 * @param <VALUETYPE>
 *        Value type
 */
@NotThreadSafe
public abstract class AbstractGenericReadonlyAttributeContainer <KEYTYPE, VALUETYPE> implements
                                                                                     IGenericReadonlyAttributeContainer <KEYTYPE, VALUETYPE>
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractGenericReadonlyAttributeContainer.class);

  @Nullable
  public final <DATATYPE> DATATYPE getCastedAttribute (@Nullable final KEYTYPE aName)
  {
    return GenericReflection.<Object, DATATYPE> uncheckedCast (getAttributeObject (aName));
  }

  @Nullable
  public final <DATATYPE> DATATYPE getCastedAttribute (@Nullable final KEYTYPE aName, @Nullable final DATATYPE aDefault)
  {
    final DATATYPE aValue = this.<DATATYPE> getCastedAttribute (aName);
    return aValue == null ? aDefault : aValue;
  }

  @Nullable
  public final <DATATYPE> DATATYPE getTypedAttribute (@Nullable final KEYTYPE aName,
                                                      @Nonnull final Class <DATATYPE> aDstClass)
  {
    return TypeConverter.convertIfNecessary (getAttributeObject (aName), aDstClass);
  }

  @Nullable
  public final <DATATYPE> DATATYPE getTypedAttribute (@Nullable final KEYTYPE aName,
                                                      @Nonnull final Class <DATATYPE> aDstClass,
                                                      @Nullable final DATATYPE aDefault)
  {
    final DATATYPE aValue = this.<DATATYPE> getTypedAttribute (aName, aDstClass);
    return aValue == null ? aDefault : aValue;
  }

  @Nullable
  public final String getAttributeAsString (@Nullable final KEYTYPE aName)
  {
    return getAttributeAsString (aName, null);
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
    {
      // expected a single string but got an array
      final String [] aArray = (String []) aValue;
      s_aLogger.warn ("The parameter '" +
                      String.valueOf (aParamName) +
                      "' is an array with " +
                      aArray.length +
                      " items; using the first one if possible: " +
                      Arrays.toString (aArray));
      return aArray.length > 0 ? aArray[0] : sDefault;
    }

    return aValue.toString ();
  }

  @Nullable
  public final String getAttributeAsString (@Nullable final KEYTYPE aName, @Nullable final String sDefault)
  {
    final Object aValue = getAttributeObject (aName);
    return getAsString (aName, aValue, sDefault);
  }

  public static int getAsInt (@Nullable final Object aParamName, @Nullable final Object aValue, final int nDefault)
  {
    if (aValue == null)
      return nDefault;
    if (aValue instanceof Number)
      return ((Number) aValue).intValue ();
    // Interpret as String
    final String sValue = getAsString (aParamName, aValue, null);
    return StringParser.parseInt (sValue, nDefault);
  }

  public final int getAttributeAsInt (@Nullable final KEYTYPE aName)
  {
    return getAttributeAsInt (aName, CGlobal.ILLEGAL_UINT);
  }

  public final int getAttributeAsInt (@Nullable final KEYTYPE aName, final int nDefault)
  {
    final Object aValue = getAttributeObject (aName);
    return getAsInt (aName, aValue, nDefault);
  }

  public static long getAsLong (@Nullable final Object aParamName, @Nullable final Object aValue, final long nDefault)
  {
    if (aValue == null)
      return nDefault;
    if (aValue instanceof Number)
      return ((Number) aValue).longValue ();
    // Interpret as String
    final String sValue = getAsString (aParamName, aValue, null);
    return StringParser.parseLong (sValue, nDefault);
  }

  public final long getAttributeAsLong (@Nullable final KEYTYPE aName)
  {
    return getAttributeAsLong (aName, CGlobal.ILLEGAL_ULONG);
  }

  public final long getAttributeAsLong (@Nullable final KEYTYPE aName, final long nDefault)
  {
    final Object aValue = getAttributeObject (aName);
    return getAsLong (aName, aValue, nDefault);
  }

  public static double getAsDouble (@Nullable final Object aParamName,
                                    @Nullable final Object aValue,
                                    final double dDefault)
  {
    if (aValue == null)
      return dDefault;
    if (aValue instanceof Number)
      return ((Number) aValue).doubleValue ();
    // Interpret as String
    final String sValue = getAsString (aParamName, aValue, null);
    return StringParser.parseDouble (sValue, dDefault);
  }

  public final double getAttributeAsDouble (@Nullable final KEYTYPE aName)
  {
    return getAttributeAsDouble (aName, CGlobal.ILLEGAL_UINT);
  }

  public final double getAttributeAsDouble (@Nullable final KEYTYPE aName, final double dDefault)
  {
    final Object aValue = getAttributeObject (aName);
    return getAsDouble (aName, aValue, dDefault);
  }

  public static boolean getAsBoolean (@Nullable final Object aParamName,
                                      @Nullable final Object aValue,
                                      final boolean bDefault)
  {
    if (aValue == null)
      return bDefault;
    if (aValue instanceof Boolean)
      return ((Boolean) aValue).booleanValue ();
    // Interpret as String
    final String sValue = getAsString (aParamName, aValue, null);
    return StringParser.parseBool (sValue, bDefault);
  }

  public final boolean getAttributeAsBoolean (@Nullable final KEYTYPE aName)
  {
    return getAttributeAsBoolean (aName, false);
  }

  public final boolean getAttributeAsBoolean (@Nullable final KEYTYPE aName, final boolean bDefault)
  {
    final Object aValue = getAttributeObject (aName);
    return getAsBoolean (aName, aValue, bDefault);
  }

  @Nullable
  public static BigInteger getAsBigInteger (@Nullable final Object aParamName,
                                            @Nullable final Object aValue,
                                            @Nullable final BigInteger aDefault)
  {
    if (aValue == null)
      return aDefault;
    if (aValue instanceof Number)
      return BigInteger.valueOf (((Number) aValue).longValue ());
    // Interpret as String
    final String sValue = getAsString (aParamName, aValue, null);
    return StringParser.parseBigInteger (sValue, aDefault);
  }

  @Nullable
  public final BigInteger getAttributeAsBigInteger (@Nullable final KEYTYPE aName)
  {
    return getAttributeAsBigInteger (aName, null);
  }

  @Nullable
  public final BigInteger getAttributeAsBigInteger (@Nullable final KEYTYPE aName, @Nullable final BigInteger aDefault)
  {
    final Object aValue = getAttributeObject (aName);
    return getAsBigInteger (aName, aValue, aDefault);
  }

  @Nullable
  public static BigDecimal getAsBigDecimal (@Nullable final Object aParamName,
                                            @Nullable final Object aValue,
                                            @Nullable final BigDecimal aDefault)
  {
    if (aValue == null)
      return aDefault;
    if (aValue instanceof Number)
      return BigDecimal.valueOf (((Number) aValue).longValue ());
    // Interpret as String
    final String sValue = getAsString (aParamName, aValue, null);
    return StringParser.parseBigDecimal (sValue, aDefault);
  }

  @Nullable
  public final BigDecimal getAttributeAsBigDecimal (@Nullable final KEYTYPE aName)
  {
    return getAttributeAsBigDecimal (aName, null);
  }

  @Nullable
  public final BigDecimal getAttributeAsBigDecimal (@Nullable final KEYTYPE aName, @Nullable final BigDecimal aDefault)
  {
    final Object aValue = getAttributeObject (aName);
    return getAsBigDecimal (aName, aValue, aDefault);
  }
}
