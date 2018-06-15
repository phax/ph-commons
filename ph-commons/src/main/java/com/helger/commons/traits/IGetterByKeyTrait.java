/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.commons.traits;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsLinkedHashSet;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsOrderedSet;
import com.helger.commons.datetime.PDTFromString;
import com.helger.commons.lang.GenericReflection;
import com.helger.commons.typeconvert.TypeConverter;
import com.helger.commons.typeconvert.TypeConverterException;

/**
 * A generic convert Object to anything with convenience API.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        The key type. E.g. String etc.
 */
@FunctionalInterface
public interface IGetterByKeyTrait <KEYTYPE>
{
  /**
   * Get the value matching the provided key.
   *
   * @param aKey
   *        The key to query. May be <code>null</code>.
   * @return The value matching the key. No <code>null</code> constraints
   *         applicable.
   */
  @Nullable
  Object getValue (@Nullable KEYTYPE aKey);

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @return The class of the value or <code>null</code> if no value is
   *         contained.
   */
  @Nullable
  default Class <?> getValueClass (@Nullable final KEYTYPE aKey)
  {
    final Object aValue = getValue (aKey);
    return aValue == null ? null : aValue.getClass ();
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @return <code>true</code> if the value is not <code>null</code>. Same as
   *         <code>getValue()!=null</code>.
   */
  default boolean containsNonNullValue (@Nullable final KEYTYPE aKey)
  {
    return getValue (aKey) != null;
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @return <code>true</code> if the value is <code>null</code>. Same as
   *         <code>getValue()==null</code>.
   */
  default boolean containsNullValue (@Nullable final KEYTYPE aKey)
  {
    return getValue (aKey) == null;
  }

  /**
   * Get the contained value casted to the return type.
   *
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @return The object value casted to the passed class. May be
   *         <code>null</code> if the contained value is <code>null</code>.
   * @throws ClassCastException
   *         in case the value types are not convertible
   * @param <T>
   *        Destination type
   */
  @Nullable
  default <T> T getCastedValue (@Nullable final KEYTYPE aKey)
  {
    return GenericReflection.uncheckedCast (getValue (aKey));
  }

  /**
   * Get the contained value casted to the return type.
   *
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @param aDefault
   *        The value to be returned if the retrieved value is <code>null</code>
   *        .
   * @return The object value casted to the passed class. May be
   *         <code>null</code> if the contained value is <code>null</code>.
   * @throws ClassCastException
   *         in case the value types are not convertible
   * @param <T>
   *        Destination type
   */
  @Nullable
  default <T> T getCastedValue (@Nullable final KEYTYPE aKey, @Nullable final T aDefault)
  {
    final Object aValue = getValue (aKey);
    return aValue == null ? aDefault : GenericReflection.uncheckedCast (aValue);
  }

  /**
   * Get the contained value casted to the specified class.
   *
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @param aClass
   *        The class to cast to.
   * @return The object value casted to the passed class. May be
   *         <code>null</code> if the contained value is <code>null</code>.
   * @throws ClassCastException
   *         in case the value types are not convertible
   * @param <T>
   *        Destination type
   */
  @Nullable
  default <T> T getCastedValue (@Nullable final KEYTYPE aKey, @Nonnull final Class <T> aClass)
  {
    return aClass.cast (getValue (aKey));
  }

  /**
   * Get the contained value casted to the specified class.
   *
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @param aDefault
   *        The value to be returned if the retrieved value is <code>null</code>
   *        .
   * @param aClass
   *        The class to cast to.
   * @return The object value casted to the passed class. May be
   *         <code>null</code> if the contained value is <code>null</code>.
   * @throws ClassCastException
   *         in case the value types are not convertible
   * @param <T>
   *        Destination type
   */
  @Nullable
  default <T> T getCastedValue (@Nullable final KEYTYPE aKey,
                                @Nullable final T aDefault,
                                @Nonnull final Class <T> aClass)
  {
    final Object aValue = getValue (aKey);
    return aValue == null ? aDefault : aClass.cast (aValue);
  }

  /**
   * Get the contained value casted to the specified class, but only if the cast
   * is possible.
   *
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @param aClass
   *        The class to cast to.
   * @return The object value casted to the passed class. May be
   *         <code>null</code> if the contained value is <code>null</code>.
   * @throws ClassCastException
   *         in case the value types are not convertible
   * @param <T>
   *        Destination type
   * @see #getSafeCastedValue(Object, Object, Class)
   */
  @Nullable
  default <T> T getSafeCastedValue (@Nullable final KEYTYPE aKey, @Nonnull final Class <T> aClass)
  {
    return getSafeCastedValue (aKey, null, aClass);
  }

  /**
   * Implement this method to handle cases of
   * {@link #getSafeCastedValue(Object, Object, Class)} that failed because
   * invalid provided class.
   *
   * <pre>
   * "Key '" + aKey + "' is present, but not as a " + aClass + " but as a " + aValue.getClass ()
   * </pre>
   *
   * @param aKey
   *        Key that was searched
   * @param aClass
   *        The class that was desired
   * @param aValue
   *        The value that was retrieved and cannot be casted to the class
   * @since 9.0.1
   */
  default void onSafeCastError (@Nullable final KEYTYPE aKey,
                                @Nonnull final Class <?> aClass,
                                @Nonnull final Object aValue)
  {
    // empty
  }

  /**
   * Get the contained value casted to the specified class, but only if the cast
   * is possible.
   *
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @param aDefault
   *        The value to be returned if the retrieved value is <code>null</code>
   *        .
   * @param aClass
   *        The class to cast to.
   * @return The object value casted to the passed class. May be
   *         <code>null</code> if the contained value is <code>null</code>.
   * @throws ClassCastException
   *         in case the value types are not convertible
   * @param <T>
   *        Destination type
   * @see #getSafeCastedValue(Object, Class)
   */
  @Nullable
  default <T> T getSafeCastedValue (@Nullable final KEYTYPE aKey,
                                    @Nullable final T aDefault,
                                    @Nonnull final Class <T> aClass)
  {
    final Object aValue = getValue (aKey);
    final T ret = aValue != null && aClass.isAssignableFrom (aValue.getClass ()) ? aClass.cast (aValue) : aDefault;
    if (ret == null && aValue != null)
      onSafeCastError (aKey, aClass, aValue);
    return ret;
  }

  /**
   * Get the contained value converted using TypeConverter to the passed class.
   *
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @param aClass
   *        The class to convert to.
   * @return The object value casted to the passed class. May be
   *         <code>null</code> if the contained value is <code>null</code>.
   * @throws TypeConverterException
   *         in case of an error
   * @param <T>
   *        Destination type
   */
  @Nullable
  default <T> T getConvertedValue (@Nullable final KEYTYPE aKey, @Nonnull final Class <T> aClass)
  {
    return TypeConverter.convert (getValue (aKey), aClass);
  }

  /**
   * Get the contained value converted using TypeConverter to the passed class.
   *
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @param aDefault
   *        The value to be returned if the retrieved value is <code>null</code>
   *        .
   * @param aClass
   *        The class to convert to.
   * @return The object value casted to the passed class. May be
   *         <code>null</code> if the contained value is <code>null</code>.
   * @throws TypeConverterException
   *         in case of an error
   * @param <T>
   *        Destination type
   */
  @Nullable
  default <T> T getConvertedValue (@Nullable final KEYTYPE aKey,
                                   @Nullable final T aDefault,
                                   @Nonnull final Class <T> aClass)
  {
    final Object aValue = getValue (aKey);
    return aValue == null ? aDefault : TypeConverter.convert (aValue, aClass);
  }

  default boolean getAsBoolean (@Nullable final KEYTYPE aKey)
  {
    return TypeConverter.convertToBoolean (getValue (aKey));
  }

  default boolean getAsBoolean (@Nullable final KEYTYPE aKey, final boolean bDefault)
  {
    return TypeConverter.convertToBoolean (getValue (aKey), bDefault);
  }

  default byte getAsByte (@Nullable final KEYTYPE aKey)
  {
    return TypeConverter.convertToByte (getValue (aKey));
  }

  default byte getAsByte (@Nullable final KEYTYPE aKey, final byte nDefault)
  {
    return TypeConverter.convertToByte (getValue (aKey), nDefault);
  }

  default char getAsChar (@Nullable final KEYTYPE aKey)
  {
    return TypeConverter.convertToChar (getValue (aKey));
  }

  default char getAsChar (@Nullable final KEYTYPE aKey, final char cDefault)
  {
    return TypeConverter.convertToChar (getValue (aKey), cDefault);
  }

  default double getAsDouble (@Nullable final KEYTYPE aKey)
  {
    return TypeConverter.convertToDouble (getValue (aKey));
  }

  default double getAsDouble (@Nullable final KEYTYPE aKey, final double dDefault)
  {
    return TypeConverter.convertToDouble (getValue (aKey), dDefault);
  }

  default float getAsFloat (@Nullable final KEYTYPE aKey)
  {
    return TypeConverter.convertToFloat (getValue (aKey));
  }

  default float getAsFloat (@Nullable final KEYTYPE aKey, final float fDefault)
  {
    return TypeConverter.convertToFloat (getValue (aKey), fDefault);
  }

  default int getAsInt (@Nullable final KEYTYPE aKey)
  {
    return TypeConverter.convertToInt (getValue (aKey));
  }

  default int getAsInt (@Nullable final KEYTYPE aKey, final int nDefault)
  {
    return TypeConverter.convertToInt (getValue (aKey), nDefault);
  }

  default long getAsLong (@Nullable final KEYTYPE aKey)
  {
    return TypeConverter.convertToLong (getValue (aKey));
  }

  default long getAsLong (@Nullable final KEYTYPE aKey, final long nDefault)
  {
    return TypeConverter.convertToLong (getValue (aKey), nDefault);
  }

  default short getAsShort (@Nullable final KEYTYPE aKey)
  {
    return TypeConverter.convertToShort (getValue (aKey));
  }

  default short getAsShort (@Nullable final KEYTYPE aKey, final short nDefault)
  {
    return TypeConverter.convertToShort (getValue (aKey), nDefault);
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @return <code>getConvertedValue (aKey,String.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default String getAsString (@Nullable final KEYTYPE aKey)
  {
    return getConvertedValue (aKey, String.class);
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @param sDefault
   *        The value to be returned if the retrieved value is <code>null</code>
   *        .
   * @return <code>getConvertedValue (aKey,sDefault, String.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Object,Object,Class)
   */
  @Nullable
  default String getAsString (@Nullable final KEYTYPE aKey, @Nullable final String sDefault)
  {
    return getConvertedValue (aKey, sDefault, String.class);
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @return <code>getConvertedValue (aKey,char [].class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default char [] getAsCharArray (@Nullable final KEYTYPE aKey)
  {
    return getConvertedValue (aKey, char [].class);
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @param aDefault
   *        The value to be returned if the retrieved value is <code>null</code>
   *        .
   * @return <code>getConvertedValue (aKey, aDefault, char[].class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Object,Object,Class)
   */
  @Nullable
  default char [] getAsCharArray (@Nullable final KEYTYPE aKey, @Nullable final char [] aDefault)
  {
    return getConvertedValue (aKey, aDefault, char [].class);
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @return <code>getConvertedValue (aKey,BigDecimal.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default BigDecimal getAsBigDecimal (@Nullable final KEYTYPE aKey)
  {
    return getConvertedValue (aKey, BigDecimal.class);
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @param aDefault
   *        The value to be returned if the retrieved value is <code>null</code>
   *        .
   * @return <code>getConvertedValue (aKey,aDefault,BigDecimal.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Object,Object,Class)
   */
  @Nullable
  default BigDecimal getAsBigDecimal (@Nullable final KEYTYPE aKey, @Nullable final BigDecimal aDefault)
  {
    return getConvertedValue (aKey, aDefault, BigDecimal.class);
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @return <code>getConvertedValue (aKey,BigInteger.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default BigInteger getAsBigInteger (@Nullable final KEYTYPE aKey)
  {
    return getConvertedValue (aKey, BigInteger.class);
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @param aDefault
   *        The value to be returned if the retrieved value is <code>null</code>
   *        .
   * @return <code>getConvertedValue (aKey,aDefault,BigInteger.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Object,Object, Class)
   */
  @Nullable
  default BigInteger getAsBigInteger (@Nullable final KEYTYPE aKey, @Nullable final BigInteger aDefault)
  {
    return getConvertedValue (aKey, aDefault, BigInteger.class);
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @return <code>getConvertedValue (aKey,LocalDate.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default LocalDate getAsLocalDate (@Nullable final KEYTYPE aKey)
  {
    return getConvertedValue (aKey, LocalDate.class);
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @param aDefault
   *        The value to be returned if the retrieved value is <code>null</code>
   *        .
   * @return <code>getConvertedValue (aKey,aDefault,LocalDate.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Object,Object,Class)
   */
  @Nullable
  default LocalDate getAsLocalDate (@Nullable final KEYTYPE aKey, @Nullable final LocalDate aDefault)
  {
    return getConvertedValue (aKey, aDefault, LocalDate.class);
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @return <code>getConvertedValue (aKey,LocalTime.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default LocalTime getAsLocalTime (@Nullable final KEYTYPE aKey)
  {
    return getConvertedValue (aKey, LocalTime.class);
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @param aDefault
   *        The value to be returned if the retrieved value is <code>null</code>
   *        .
   * @return <code>getConvertedValue (aKey,aDefault,LocalTime.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Object,Object,Class)
   */
  @Nullable
  default LocalTime getAsLocalTime (@Nullable final KEYTYPE aKey, @Nullable final LocalTime aDefault)
  {
    return getConvertedValue (aKey, aDefault, LocalTime.class);
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @return <code>getConvertedValue (aKey,LocalDateTime.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default LocalDateTime getAsLocalDateTime (@Nullable final KEYTYPE aKey)
  {
    return getConvertedValue (aKey, LocalDateTime.class);
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @param aDefault
   *        The value to be returned if the retrieved value is <code>null</code>
   *        .
   * @return <code>getConvertedValue (aKey,aDefault,LocalDateTime.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Object,Object,Class)
   */
  @Nullable
  default LocalDateTime getAsLocalDateTime (@Nullable final KEYTYPE aKey, @Nullable final LocalDateTime aDefault)
  {
    return getConvertedValue (aKey, aDefault, LocalDateTime.class);
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @return <code>getConvertedValue (aKey,byte[].class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default byte [] getAsByteArray (@Nullable final KEYTYPE aKey)
  {
    return getConvertedValue (aKey, byte [].class);
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @return <code>getConvertedValue (aKey,Boolean.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default Boolean getAsBooleanObj (@Nullable final KEYTYPE aKey)
  {
    return getConvertedValue (aKey, Boolean.class);
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @return <code>getConvertedValue (aKey,Byte.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default Byte getAsByteObj (@Nullable final KEYTYPE aKey)
  {
    return getConvertedValue (aKey, Byte.class);
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @return <code>getConvertedValue (aKey,Character.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default Character getAsCharObj (@Nullable final KEYTYPE aKey)
  {
    return getConvertedValue (aKey, Character.class);
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @return <code>getConvertedValue (aKey,Double.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default Double getAsDoubleObj (@Nullable final KEYTYPE aKey)
  {
    return getConvertedValue (aKey, Double.class);
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @return <code>getConvertedValue (aKey,Float.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default Float getAsFloatObj (@Nullable final KEYTYPE aKey)
  {
    return getConvertedValue (aKey, Float.class);
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @return <code>getConvertedValue (aKey,Integer.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default Integer getAsIntObj (@Nullable final KEYTYPE aKey)
  {
    return getConvertedValue (aKey, Integer.class);
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @return <code>getConvertedValue (aKey,Long.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default Long getAsLongObj (@Nullable final KEYTYPE aKey)
  {
    return getConvertedValue (aKey, Long.class);
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @return <code>getConvertedValue (aKey,Short.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default Short getAsShortObj (@Nullable final KEYTYPE aKey)
  {
    return getConvertedValue (aKey, Short.class);
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @return <code>getConvertedValue (aKey,Blob.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default java.sql.Blob getAsSqlBlob (@Nullable final KEYTYPE aKey)
  {
    return getConvertedValue (aKey, java.sql.Blob.class);
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @return <code>getConvertedValue (aKey,Clob.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default java.sql.Clob getAsSqlClob (@Nullable final KEYTYPE aKey)
  {
    return getConvertedValue (aKey, java.sql.Clob.class);
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @return <code>getConvertedValue (aKey,Date.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default java.sql.Date getAsSqlDate (@Nullable final KEYTYPE aKey)
  {
    return getConvertedValue (aKey, java.sql.Date.class);
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @return <code>getConvertedValue (aKey,NClob.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default java.sql.NClob getAsSqlNClob (@Nullable final KEYTYPE aKey)
  {
    return getConvertedValue (aKey, java.sql.NClob.class);
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @return <code>getConvertedValue (aKey,RowId.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default java.sql.RowId getAsSqlRowId (@Nullable final KEYTYPE aKey)
  {
    return getConvertedValue (aKey, java.sql.RowId.class);
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @return <code>getConvertedValue (aKey,Time.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default java.sql.Time getAsSqlTime (@Nullable final KEYTYPE aKey)
  {
    return getConvertedValue (aKey, java.sql.Time.class);
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @return <code>getConvertedValue (aKey,Timestamp.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default java.sql.Timestamp getAsSqlTimestamp (@Nullable final KEYTYPE aKey)
  {
    return getConvertedValue (aKey, java.sql.Timestamp.class);
  }

  /**
   * Get the value as a String, interpreted as a {@link LocalDate}.
   *
   * @param aKey
   *        The key to check. May be <code>null</code>.
   * @param aContentLocale
   *        Locale to use for conversion.
   * @return <code>null</code> if either the conversion to String or the parsing
   *         of the String failed.
   */
  @Nullable
  default LocalDate getAsLocalDate (@Nullable final KEYTYPE aKey, @Nonnull final Locale aContentLocale)
  {
    final String sStartDate = getAsString (aKey);
    return PDTFromString.getLocalDateFromString (sStartDate, aContentLocale);
  }

  /**
   * Get the value as a String, interpreted as a {@link LocalTime}.
   *
   * @param aKey
   *        The key to check. May be <code>null</code>.
   * @param aContentLocale
   *        Locale to use for conversion.
   * @return <code>null</code> if either the conversion to String or the parsing
   *         of the String failed.
   */
  @Nullable
  default LocalTime getAsLocalTime (@Nullable final KEYTYPE aKey, @Nonnull final Locale aContentLocale)
  {
    final String sStartDate = getAsString (aKey);
    return PDTFromString.getLocalTimeFromString (sStartDate, aContentLocale);
  }

  /**
   * Get the value as a String, interpreted as a {@link LocalDateTime}.
   *
   * @param aKey
   *        The key to check. May be <code>null</code>.
   * @param aContentLocale
   *        Locale to use for conversion.
   * @return <code>null</code> if either the conversion to String or the parsing
   *         of the String failed.
   */
  @Nullable
  default LocalDateTime getAsLocalDateTime (@Nullable final KEYTYPE aKey, @Nonnull final Locale aContentLocale)
  {
    final String sStartDate = getAsString (aKey);
    return PDTFromString.getLocalDateTimeFromString (sStartDate, aContentLocale);
  }

  /**
   * Get a list of all attribute values with the same name.
   *
   * @param aKey
   *        The key to check. May be <code>null</code>.
   * @return <code>null</code> if no such attribute value exists
   */
  @Nullable
  default ICommonsList <String> getAsStringList (@Nullable final KEYTYPE aKey)
  {
    return getAsStringList (aKey, null);
  }

  /**
   * Get a list of all attribute values with the same name.
   *
   * @param aKey
   *        The key to check. May be <code>null</code>.
   * @param aDefault
   *        The default value to be returned, if no such attribute is present.
   * @return <code>aDefault</code> if no such attribute value exists
   */
  @Nullable
  default ICommonsList <String> getAsStringList (@Nullable final KEYTYPE aKey,
                                                 @Nullable final ICommonsList <String> aDefault)
  {
    final Object aValue = getValue (aKey);
    if (aValue != null)
    {
      if (aValue instanceof String [])
      {
        // multiple values passed in the request
        return new CommonsArrayList <> ((String []) aValue);
      }
      if (aValue instanceof String)
      {
        // single value passed in the request
        return new CommonsArrayList <> ((String) aValue);
      }
    }
    return aDefault;
  }

  /**
   * Get a set of all attribute values with the same name.
   *
   * @param aKey
   *        The key to check. May be <code>null</code>.
   * @return <code>null</code> if no such attribute value exists
   */
  @Nullable
  default ICommonsOrderedSet <String> getAsStringSet (@Nullable final KEYTYPE aKey)
  {
    return getAsStringSet (aKey, null);
  }

  /**
   * Get a set of all attribute values with the same name.
   *
   * @param aKey
   *        The key to check. May be <code>null</code>.
   * @param aDefault
   *        The default value to be returned, if no such attribute is present.
   * @return <code>aDefault</code> if no such attribute value exists
   */
  @Nullable
  default ICommonsOrderedSet <String> getAsStringSet (@Nullable final KEYTYPE aKey,
                                                      @Nullable final ICommonsOrderedSet <String> aDefault)
  {
    final Object aValue = getValue (aKey);
    if (aValue != null)
    {
      if (aValue instanceof String [])
      {
        // multiple values passed in the request
        return new CommonsLinkedHashSet <> ((String []) aValue);
      }
      if (aValue instanceof String)
      {
        // single value passed in the request
        return new CommonsLinkedHashSet <> ((String) aValue);
      }
    }
    return aDefault;
  }

  /**
   * Check if a attribute with the given name is present in the request and has
   * the specified value.
   *
   * @param aKey
   *        The key to check. May be <code>null</code>.
   * @param sDesiredValue
   *        The value to be matched
   * @return <code>true</code> if an attribute with the given name is present
   *         and has the desired value
   */
  default boolean hasStringValue (@Nullable final KEYTYPE aKey, @Nullable final String sDesiredValue)
  {
    return hasStringValue (aKey, sDesiredValue, false);
  }

  /**
   * Check if a attribute with the given name is present in the request and has
   * the specified value. If no such attribute is present, the passed default
   * value is returned.
   *
   * @param aKey
   *        The key to check. May be <code>null</code>.
   * @param sDesiredValue
   *        The value to be matched
   * @param bDefault
   *        the default value to be returned, if the specified attribute is not
   *        present
   * @return <code>true</code> if an attribute with the given name is present
   *         and has the desired value, <code>false</code> if the attribute is
   *         present but has a different value. If the attribute is not present,
   *         the default value is returned.
   */
  default boolean hasStringValue (@Nullable final KEYTYPE aKey,
                                  @Nullable final String sDesiredValue,
                                  final boolean bDefault)
  {
    final String sValue = getAsString (aKey);
    return sValue == null ? bDefault : sValue.equals (sDesiredValue);
  }
}
