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
package com.helger.commons.typeconvert;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.PresentForCodeCoverage;
import com.helger.commons.lang.ClassHelper;
import com.helger.commons.typeconvert.TypeConverterException.EReason;

/**
 * Helper class for converting base types likes "boolean" to object types like
 * "Boolean".<br>
 * Uses {@link TypeConverterRegistry#getFuzzyConverter(Class, Class)} for
 * retrieving a registered converter. If no converter is found, it is checked
 * whether a mapping from a primitive type to an object type exists.
 *
 * @author Philip Helger
 */
@SuppressWarnings ("javadoc")
@Immutable
public final class TypeConverter
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (TypeConverter.class);

  @PresentForCodeCoverage
  @SuppressWarnings ("unused")
  private static final TypeConverter s_aInstance = new TypeConverter ();

  private TypeConverter ()
  {}

  @Nullable
  public static <DSTTYPE> DSTTYPE convert (final boolean aSrcValue, @Nonnull final Class <DSTTYPE> aDstClass)
  {
    return convertIfNecessary (Boolean.valueOf (aSrcValue), aDstClass);
  }

  @Nullable
  public static <DSTTYPE> DSTTYPE convert (final byte aSrcValue, @Nonnull final Class <DSTTYPE> aDstClass)
  {
    return convertIfNecessary (Byte.valueOf (aSrcValue), aDstClass);
  }

  @Nullable
  public static <DSTTYPE> DSTTYPE convert (final char aSrcValue, @Nonnull final Class <DSTTYPE> aDstClass)
  {
    return convertIfNecessary (Character.valueOf (aSrcValue), aDstClass);
  }

  @Nullable
  public static <DSTTYPE> DSTTYPE convert (final double aSrcValue, @Nonnull final Class <DSTTYPE> aDstClass)
  {
    return convertIfNecessary (Double.valueOf (aSrcValue), aDstClass);
  }

  @Nullable
  public static <DSTTYPE> DSTTYPE convert (final float aSrcValue, @Nonnull final Class <DSTTYPE> aDstClass)
  {
    return convertIfNecessary (Float.valueOf (aSrcValue), aDstClass);
  }

  @Nullable
  public static <DSTTYPE> DSTTYPE convert (final int aSrcValue, @Nonnull final Class <DSTTYPE> aDstClass)
  {
    return convertIfNecessary (Integer.valueOf (aSrcValue), aDstClass);
  }

  @Nullable
  public static <DSTTYPE> DSTTYPE convert (final long aSrcValue, @Nonnull final Class <DSTTYPE> aDstClass)
  {
    return convertIfNecessary (Long.valueOf (aSrcValue), aDstClass);
  }

  @Nullable
  public static <DSTTYPE> DSTTYPE convert (final short aSrcValue, @Nonnull final Class <DSTTYPE> aDstClass)
  {
    return convertIfNecessary (Short.valueOf (aSrcValue), aDstClass);
  }

  /**
   * Convert the passed source value to boolean
   *
   * @param aSrcValue
   *        The source value. May not be <code>null</code>.
   * @return <code>null</code> if the source value was <code>null</code>.
   * @throws IllegalArgumentException
   *         if the conversion process fails because either the conversion
   *         failed, or no converter was found.
   * @see TypeConverterProviderBestMatch
   * @throws TypeConverterException
   *         if the source value is <code>null</code>
   */
  public static boolean convertToBoolean (@Nonnull final Object aSrcValue)
  {
    if (aSrcValue == null)
      throw new TypeConverterException (boolean.class, EReason.NULL_SOURCE_NOT_ALLOWED);
    final Boolean aValue = convertIfNecessary (aSrcValue, Boolean.class);
    return aValue.booleanValue ();
  }

  /**
   * Convert the passed source value to byte
   *
   * @param aSrcValue
   *        The source value. May not be <code>null</code>.
   * @return <code>null</code> if the source value was <code>null</code>.
   * @throws IllegalArgumentException
   *         if the conversion process fails because either the conversion
   *         failed, or no converter was found.
   * @see TypeConverterProviderBestMatch
   * @throws TypeConverterException
   *         if the source value is <code>null</code>
   */
  public static byte convertToByte (@Nonnull final Object aSrcValue)
  {
    if (aSrcValue == null)
      throw new TypeConverterException (byte.class, EReason.NULL_SOURCE_NOT_ALLOWED);
    final Byte aValue = convertIfNecessary (aSrcValue, Byte.class);
    return aValue.byteValue ();
  }

  /**
   * Convert the passed source value to char
   *
   * @param aSrcValue
   *        The source value. May not be <code>null</code>.
   * @return <code>null</code> if the source value was <code>null</code>.
   * @throws IllegalArgumentException
   *         if the conversion process fails because either the conversion
   *         failed, or no converter was found.
   * @see TypeConverterProviderBestMatch
   * @throws TypeConverterException
   *         if the source value is <code>null</code>
   */
  public static char convertToChar (@Nonnull final Object aSrcValue)
  {
    if (aSrcValue == null)
      throw new TypeConverterException (char.class, EReason.NULL_SOURCE_NOT_ALLOWED);
    final Character aValue = convertIfNecessary (aSrcValue, Character.class);
    return aValue.charValue ();
  }

  /**
   * Convert the passed source value to double
   *
   * @param aSrcValue
   *        The source value. May not be <code>null</code>.
   * @return <code>null</code> if the source value was <code>null</code>.
   * @throws IllegalArgumentException
   *         if the conversion process fails because either the conversion
   *         failed, or no converter was found.
   * @see TypeConverterProviderBestMatch
   * @throws TypeConverterException
   *         if the source value is <code>null</code>
   */
  public static double convertToDouble (@Nonnull final Object aSrcValue)
  {
    if (aSrcValue == null)
      throw new TypeConverterException (double.class, EReason.NULL_SOURCE_NOT_ALLOWED);
    final Double aValue = convertIfNecessary (aSrcValue, Double.class);
    return aValue.doubleValue ();
  }

  /**
   * Convert the passed source value to float
   *
   * @param aSrcValue
   *        The source value. May not be <code>null</code>.
   * @return <code>null</code> if the source value was <code>null</code>.
   * @throws IllegalArgumentException
   *         if the conversion process fails because either the conversion
   *         failed, or no converter was found.
   * @see TypeConverterProviderBestMatch
   * @throws TypeConverterException
   *         if the source value is <code>null</code>
   */
  public static float convertToFloat (@Nonnull final Object aSrcValue)
  {
    if (aSrcValue == null)
      throw new TypeConverterException (float.class, EReason.NULL_SOURCE_NOT_ALLOWED);
    final Float aValue = convertIfNecessary (aSrcValue, Float.class);
    return aValue.floatValue ();
  }

  /**
   * Convert the passed source value to int
   *
   * @param aSrcValue
   *        The source value. May not be <code>null</code>.
   * @return <code>null</code> if the source value was <code>null</code>.
   * @throws IllegalArgumentException
   *         if the conversion process fails because either the conversion
   *         failed, or no converter was found.
   * @see TypeConverterProviderBestMatch
   * @throws TypeConverterException
   *         if the source value is <code>null</code>
   */
  public static int convertToInt (@Nonnull final Object aSrcValue)
  {
    if (aSrcValue == null)
      throw new TypeConverterException (int.class, EReason.NULL_SOURCE_NOT_ALLOWED);
    final Integer aValue = convertIfNecessary (aSrcValue, Integer.class);
    return aValue.intValue ();
  }

  /**
   * Convert the passed source value to long
   *
   * @param aSrcValue
   *        The source value. May not be <code>null</code>.
   * @return <code>null</code> if the source value was <code>null</code>.
   * @throws IllegalArgumentException
   *         if the conversion process fails because either the conversion
   *         failed, or no converter was found.
   * @see TypeConverterProviderBestMatch
   * @throws TypeConverterException
   *         if the source value is <code>null</code>
   */
  public static long convertToLong (@Nonnull final Object aSrcValue)
  {
    if (aSrcValue == null)
      throw new TypeConverterException (long.class, EReason.NULL_SOURCE_NOT_ALLOWED);
    final Long aValue = convertIfNecessary (aSrcValue, Long.class);
    return aValue.longValue ();
  }

  /**
   * Convert the passed source value to short
   *
   * @param aSrcValue
   *        The source value. May not be <code>null</code>.
   * @return <code>null</code> if the source value was <code>null</code>.
   * @throws IllegalArgumentException
   *         if the conversion process fails because either the conversion
   *         failed, or no converter was found.
   * @see TypeConverterProviderBestMatch
   * @throws TypeConverterException
   *         if the source value is <code>null</code>
   */
  public static short convertToShort (@Nonnull final Object aSrcValue)
  {
    if (aSrcValue == null)
      throw new TypeConverterException (short.class, EReason.NULL_SOURCE_NOT_ALLOWED);
    final Short aValue = convertIfNecessary (aSrcValue, Short.class);
    return aValue.shortValue ();
  }

  /**
   * Convert the passed source value to the destination class, if a conversion
   * is necessary. By default the fuzzy type converter provider is used.
   *
   * @param <DSTTYPE>
   *        The destination type.
   * @param aSrcValue
   *        The source value. May be <code>null</code>.
   * @param aDstClass
   *        The destination class to use.
   * @return <code>null</code> if the source value was <code>null</code>.
   * @throws IllegalArgumentException
   *         if the conversion process fails because either the conversion
   *         failed, or no converter was found.
   * @throws TypeConverterException
   *         If either no converter could be found, or if the conversion failed!
   * @see TypeConverterProviderBestMatch
   */
  @Nullable
  public static <DSTTYPE> DSTTYPE convertIfNecessary (@Nullable final Object aSrcValue,
                                                      @Nonnull final Class <DSTTYPE> aDstClass)
  {
    return convertIfNecessary (TypeConverterProviderBestMatch.getInstance (), aSrcValue, aDstClass);
  }

  /**
   * Get the class to use. In case the passed class is a primitive type, the
   * corresponding wrapper class is used.
   *
   * @param aClass
   *        The class to check. Can be <code>null</code> but should not be
   *        <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  private static Class <?> _getUsableClass (@Nullable final Class <?> aClass)
  {
    final Class <?> aPrimitiveWrapperType = ClassHelper.getPrimitiveWrapperClass (aClass);
    return aPrimitiveWrapperType != null ? aPrimitiveWrapperType : aClass;
  }

  @Nonnull
  private static Object _performConversion (@Nonnull final ITypeConverterProvider aTypeConverterProvider,
                                            @Nonnull final Class <?> aSrcClass,
                                            @Nonnull final Class <?> aUsableDstClass,
                                            @Nonnull final Object aSrcValue) throws TypeConverterException
  {
    // try to find matching converter
    final ITypeConverter aConverter = aTypeConverterProvider.getTypeConverter (aSrcClass, aUsableDstClass);
    if (aConverter == null)
    {
      s_aLogger.warn ("No type converter from '" +
                      aSrcClass.getName () +
                      "' to '" +
                      aUsableDstClass.getName () +
                      "' was found");
      throw new TypeConverterException (aSrcClass, aUsableDstClass, EReason.NO_CONVERTER_FOUND);
    }

    // Okay, converter was found -> invoke it
    final Object aRetVal = aConverter.convert (aSrcValue);
    if (aRetVal == null)
    {
      s_aLogger.warn ("Type conversion from '" +
                      aSrcValue +
                      "' of class '" +
                      aSrcClass.getName () +
                      "' to '" +
                      aUsableDstClass.getName () +
                      "' with converter " +
                      aConverter +
                      " failed");
      throw new TypeConverterException (aSrcClass, aUsableDstClass, EReason.CONVERSION_FAILED);
    }
    return aRetVal;
  }

  /**
   * Convert the passed source value to the destination class, if a conversion
   * is necessary.
   *
   * @param <DSTTYPE>
   *        The destination type.
   * @param aTypeConverterProvider
   *        The type converter provider. May not be <code>null</code>. .
   * @param aSrcValue
   *        The source value. May be <code>null</code>.
   * @param aDstClass
   *        The destination class to use.
   * @return <code>null</code> if the source value was <code>null</code>.
   * @throws IllegalArgumentException
   *         if the conversion process fails because either the conversion
   *         failed, or no converter was found.
   * @throws TypeConverterException
   *         If either no converter could be found, or if the conversion failed!
   */
  @Nullable
  public static <DSTTYPE> DSTTYPE convertIfNecessary (@Nonnull final ITypeConverterProvider aTypeConverterProvider,
                                                      @Nullable final Object aSrcValue,
                                                      @Nonnull final Class <DSTTYPE> aDstClass) throws TypeConverterException
  {
    ValueEnforcer.notNull (aTypeConverterProvider, "TypeConverterProvider");
    ValueEnforcer.notNull (aDstClass, "DstClass");

    // Nothing to convert for null
    if (aSrcValue == null)
      return null;

    final Class <?> aSrcClass = aSrcValue.getClass ();
    final Class <?> aUsableDstClass = _getUsableClass (aDstClass);

    // First check if a direct cast is possible
    Object aRetVal;
    if (ClassHelper.areConvertibleClasses (aSrcClass, aUsableDstClass))
      aRetVal = aSrcValue;
    else
      aRetVal = _performConversion (aTypeConverterProvider, aSrcClass, aUsableDstClass, aSrcValue);

    // Done :)
    // Note: aUsableDstClass.cast (aRetValue) does not work on conversion from
    // "boolean" to "Boolean" whereas casting works
    @SuppressWarnings ("unchecked")
    final DSTTYPE ret = (DSTTYPE) aRetVal;
    return ret;
  }
}
