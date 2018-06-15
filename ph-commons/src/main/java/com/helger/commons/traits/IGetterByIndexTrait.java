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

import javax.annotation.Nonnegative;
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
 */
@FunctionalInterface
public interface IGetterByIndexTrait
{
  /**
   * Get the value at the specified index.
   *
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return The value at the specified index. No <code>null</code> constraints
   *         applicable.
   */
  @Nullable
  Object getValue (@Nonnegative int nIndex);

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return The class of the value or <code>null</code> if no value is
   *         contained.
   */
  @Nullable
  default Class <?> getValueClass (@Nonnegative final int nIndex)
  {
    final Object aValue = getValue (nIndex);
    return aValue == null ? null : aValue.getClass ();
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>true</code> if the value is not <code>null</code>. Same as
   *         <code>getValue()!=null</code>.
   */
  default boolean hasValue (@Nonnegative final int nIndex)
  {
    return getValue (nIndex) != null;
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>true</code> if the value is <code>null</code>. Same as
   *         <code>getValue()==null</code>.
   */
  default boolean hasNoValue (@Nonnegative final int nIndex)
  {
    return getValue (nIndex) == null;
  }

  /**
   * Get the contained value casted to the return type.
   *
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return The object value casted to the passed class. May be
   *         <code>null</code> if the contained value is <code>null</code>.
   * @throws ClassCastException
   *         in case the value types are not convertible
   * @param <T>
   *        Destination type
   */
  @Nullable
  default <T> T getCastedValue (@Nonnegative final int nIndex)
  {
    return GenericReflection.uncheckedCast (getValue (nIndex));
  }

  /**
   * Get the contained value casted to the return type.
   *
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
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
  default <T> T getCastedValue (@Nonnegative final int nIndex, @Nullable final T aDefault)
  {
    final Object aValue = getValue (nIndex);
    return aValue == null ? aDefault : GenericReflection.uncheckedCast (aValue);
  }

  /**
   * Get the contained value casted to the specified class.
   *
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
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
  default <T> T getCastedValue (@Nonnegative final int nIndex, @Nonnull final Class <T> aClass)
  {
    return aClass.cast (getValue (nIndex));
  }

  /**
   * Get the contained value casted to the specified class.
   *
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
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
  default <T> T getCastedValue (@Nonnegative final int nIndex,
                                @Nullable final T aDefault,
                                @Nonnull final Class <T> aClass)
  {
    final Object aValue = getValue (nIndex);
    return aValue == null ? aDefault : aClass.cast (aValue);
  }

  /**
   * Get the contained value casted to the specified class, but only if the cast
   * is possible.
   *
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
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
  default <T> T getSafeCastedValue (@Nonnegative final int nIndex, @Nonnull final Class <T> aClass)
  {
    return getSafeCastedValue (nIndex, null, aClass);
  }

  /**
   * Implement this method to handle cases of
   * {@link #getSafeCastedValue(int, Object, Class)} that failed because invalid
   * provided class.
   *
   * <pre>
   * "Index '" + nIndex + "' is present, but not as a " + aClass + " but as a " + aValue.getClass ()
   * </pre>
   *
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @param aClass
   *        The class that was desired
   * @param aValue
   *        The value that was retrieved and cannot be casted to the class
   * @since 9.0.1
   */
  default void onSafeCastError (@Nonnegative final int nIndex,
                                @Nonnull final Class <?> aClass,
                                @Nonnull final Object aValue)
  {
    // empty
  }

  /**
   * Get the contained value casted to the specified class, but only if the cast
   * is possible.
   *
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
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
  default <T> T getSafeCastedValue (@Nonnegative final int nIndex,
                                    @Nullable final T aDefault,
                                    @Nonnull final Class <T> aClass)
  {
    final Object aValue = getValue (nIndex);
    final T ret = aValue != null && aClass.isAssignableFrom (aValue.getClass ()) ? aClass.cast (aValue) : aDefault;
    if (ret == null && aValue != null)
      onSafeCastError (nIndex, aClass, aValue);
    return ret;
  }

  /**
   * Get the contained value converted using TypeConverter to the passed class.
   *
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
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
  default <T> T getConvertedValue (@Nonnegative final int nIndex, @Nonnull final Class <T> aClass)
  {
    return TypeConverter.convert (getValue (nIndex), aClass);
  }

  /**
   * Get the contained value converted using TypeConverter to the passed class.
   *
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
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
  default <T> T getConvertedValue (@Nonnegative final int nIndex,
                                   @Nullable final T aDefault,
                                   @Nonnull final Class <T> aClass)
  {
    final Object aValue = getValue (nIndex);
    return aValue == null ? aDefault : TypeConverter.convert (aValue, aClass);
  }

  default boolean getAsBoolean (@Nonnegative final int nIndex)
  {
    return TypeConverter.convertToBoolean (getValue (nIndex));
  }

  default boolean getAsBoolean (@Nonnegative final int nIndex, final boolean bDefault)
  {
    return TypeConverter.convertToBoolean (getValue (nIndex), bDefault);
  }

  default byte getAsByte (@Nonnegative final int nIndex)
  {
    return TypeConverter.convertToByte (getValue (nIndex));
  }

  default byte getAsByte (@Nonnegative final int nIndex, final byte nDefault)
  {
    return TypeConverter.convertToByte (getValue (nIndex), nDefault);
  }

  default char getAsChar (@Nonnegative final int nIndex)
  {
    return TypeConverter.convertToChar (getValue (nIndex));
  }

  default char getAsChar (@Nonnegative final int nIndex, final char cDefault)
  {
    return TypeConverter.convertToChar (getValue (nIndex), cDefault);
  }

  default double getAsDouble (@Nonnegative final int nIndex)
  {
    return TypeConverter.convertToDouble (getValue (nIndex));
  }

  default double getAsDouble (@Nonnegative final int nIndex, final double dDefault)
  {
    return TypeConverter.convertToDouble (getValue (nIndex), dDefault);
  }

  default float getAsFloat (@Nonnegative final int nIndex)
  {
    return TypeConverter.convertToFloat (getValue (nIndex));
  }

  default float getAsFloat (@Nonnegative final int nIndex, final float fDefault)
  {
    return TypeConverter.convertToFloat (getValue (nIndex), fDefault);
  }

  default int getAsInt (@Nonnegative final int nIndex)
  {
    return TypeConverter.convertToInt (getValue (nIndex));
  }

  default int getAsInt (@Nonnegative final int nIndex, final int nDefault)
  {
    return TypeConverter.convertToInt (getValue (nIndex), nDefault);
  }

  default long getAsLong (@Nonnegative final int nIndex)
  {
    return TypeConverter.convertToLong (getValue (nIndex));
  }

  default long getAsLong (@Nonnegative final int nIndex, final long nDefault)
  {
    return TypeConverter.convertToLong (getValue (nIndex), nDefault);
  }

  default short getAsShort (@Nonnegative final int nIndex)
  {
    return TypeConverter.convertToShort (getValue (nIndex));
  }

  default short getAsShort (@Nonnegative final int nIndex, final short nDefault)
  {
    return TypeConverter.convertToShort (getValue (nIndex), nDefault);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (nIndex,String.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default String getAsString (@Nonnegative final int nIndex)
  {
    return getConvertedValue (nIndex, String.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @param sDefault
   *        The value to be returned if the retrieved value is <code>null</code>
   *        .
   * @return <code>getConvertedValue (nIndex,sDefault, String.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Object,Class)
   */
  @Nullable
  default String getAsString (@Nonnegative final int nIndex, @Nullable final String sDefault)
  {
    return getConvertedValue (nIndex, sDefault, String.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (nIndex,char[].class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default char [] getAsCharArray (@Nonnegative final int nIndex)
  {
    return getConvertedValue (nIndex, char [].class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @param aDefault
   *        The value to be returned if the retrieved value is <code>null</code>
   *        .
   * @return <code>getConvertedValue (nIndex,aDefault, char[].class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Object,Class)
   */
  @Nullable
  default char [] getAsCharArray (@Nonnegative final int nIndex, @Nullable final char [] aDefault)
  {
    return getConvertedValue (nIndex, aDefault, char [].class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (nIndex,BigDecimal.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default BigDecimal getAsBigDecimal (@Nonnegative final int nIndex)
  {
    return getConvertedValue (nIndex, BigDecimal.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @param aDefault
   *        The value to be returned if the retrieved value is <code>null</code>
   *        .
   * @return <code>getConvertedValue (nIndex,aDefault,BigDecimal.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Object,Class)
   */
  @Nullable
  default BigDecimal getAsBigDecimal (@Nonnegative final int nIndex, @Nullable final BigDecimal aDefault)
  {
    return getConvertedValue (nIndex, aDefault, BigDecimal.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (nIndex,BigInteger.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default BigInteger getAsBigInteger (@Nonnegative final int nIndex)
  {
    return getConvertedValue (nIndex, BigInteger.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @param aDefault
   *        The value to be returned if the retrieved value is <code>null</code>
   *        .
   * @return <code>getConvertedValue (nIndex,aDefault,BigInteger.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Object,Class)
   */
  @Nullable
  default BigInteger getAsBigInteger (@Nonnegative final int nIndex, @Nullable final BigInteger aDefault)
  {
    return getConvertedValue (nIndex, aDefault, BigInteger.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (nIndex,LocalDate.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default LocalDate getAsLocalDate (@Nonnegative final int nIndex)
  {
    return getConvertedValue (nIndex, LocalDate.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @param aDefault
   *        The value to be returned if the retrieved value is <code>null</code>
   *        .
   * @return <code>getConvertedValue (nIndex,aDefault,LocalDate.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Object,Class)
   */
  @Nullable
  default LocalDate getAsLocalDate (@Nonnegative final int nIndex, @Nullable final LocalDate aDefault)
  {
    return getConvertedValue (nIndex, aDefault, LocalDate.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (nIndex,LocalTime.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default LocalTime getAsLocalTime (@Nonnegative final int nIndex)
  {
    return getConvertedValue (nIndex, LocalTime.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @param aDefault
   *        The value to be returned if the retrieved value is <code>null</code>
   *        .
   * @return <code>getConvertedValue (nIndex,aDefault,LocalTime.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Object,Class)
   */
  @Nullable
  default LocalTime getAsLocalTime (@Nonnegative final int nIndex, @Nullable final LocalTime aDefault)
  {
    return getConvertedValue (nIndex, aDefault, LocalTime.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (nIndex,LocalDateTime.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default LocalDateTime getAsLocalDateTime (@Nonnegative final int nIndex)
  {
    return getConvertedValue (nIndex, LocalDateTime.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @param aDefault
   *        The value to be returned if the retrieved value is <code>null</code>
   *        .
   * @return <code>getConvertedValue (nIndex,aDefault,LocalDateTime.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Object,Class)
   */
  @Nullable
  default LocalDateTime getAsLocalDateTime (@Nonnegative final int nIndex, @Nullable final LocalDateTime aDefault)
  {
    return getConvertedValue (nIndex, aDefault, LocalDateTime.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (nIndex,byte[].class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default byte [] getAsByteArray (@Nonnegative final int nIndex)
  {
    return getConvertedValue (nIndex, byte [].class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (nIndex,Boolean.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default Boolean getAsBooleanObj (@Nonnegative final int nIndex)
  {
    return getConvertedValue (nIndex, Boolean.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (nIndex,Byte.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default Byte getAsByteObj (@Nonnegative final int nIndex)
  {
    return getConvertedValue (nIndex, Byte.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (nIndex,Character.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default Character getAsCharObj (@Nonnegative final int nIndex)
  {
    return getConvertedValue (nIndex, Character.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (nIndex,Double.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default Double getAsDoubleObj (@Nonnegative final int nIndex)
  {
    return getConvertedValue (nIndex, Double.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (nIndex,Float.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default Float getAsFloatObj (@Nonnegative final int nIndex)
  {
    return getConvertedValue (nIndex, Float.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (nIndex,Integer.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default Integer getAsIntObj (@Nonnegative final int nIndex)
  {
    return getConvertedValue (nIndex, Integer.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (nIndex,Long.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default Long getAsLongObj (@Nonnegative final int nIndex)
  {
    return getConvertedValue (nIndex, Long.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (nIndex,Short.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default Short getAsShortObj (@Nonnegative final int nIndex)
  {
    return getConvertedValue (nIndex, Short.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (nIndex,Blob.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default java.sql.Blob getAsSqlBlob (@Nonnegative final int nIndex)
  {
    return getConvertedValue (nIndex, java.sql.Blob.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (nIndex,Clob.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default java.sql.Clob getAsSqlClob (@Nonnegative final int nIndex)
  {
    return getConvertedValue (nIndex, java.sql.Clob.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (nIndex,Date.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default java.sql.Date getAsSqlDate (@Nonnegative final int nIndex)
  {
    return getConvertedValue (nIndex, java.sql.Date.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (nIndex,NClob.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default java.sql.NClob getAsSqlNClob (@Nonnegative final int nIndex)
  {
    return getConvertedValue (nIndex, java.sql.NClob.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (nIndex,RowId.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default java.sql.RowId getAsSqlRowId (@Nonnegative final int nIndex)
  {
    return getConvertedValue (nIndex, java.sql.RowId.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (nIndex,Time.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default java.sql.Time getAsSqlTime (@Nonnegative final int nIndex)
  {
    return getConvertedValue (nIndex, java.sql.Time.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (nIndex,Timestamp.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default java.sql.Timestamp getAsSqlTimestamp (@Nonnegative final int nIndex)
  {
    return getConvertedValue (nIndex, java.sql.Timestamp.class);
  }

  /**
   * Get the value as a String, interpreted as a {@link LocalDate}.
   *
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @param aContentLocale
   *        Locale to use for conversion.
   * @return <code>null</code> if either the conversion to String or the parsing
   *         of the String failed.
   */
  @Nullable
  default LocalDate getAsLocalDate (@Nonnegative final int nIndex, @Nonnull final Locale aContentLocale)
  {
    final String sStartDate = getAsString (nIndex);
    return PDTFromString.getLocalDateFromString (sStartDate, aContentLocale);
  }

  /**
   * Get the value as a String, interpreted as a {@link LocalTime}.
   *
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @param aContentLocale
   *        Locale to use for conversion.
   * @return <code>null</code> if either the conversion to String or the parsing
   *         of the String failed.
   */
  @Nullable
  default LocalTime getAsLocalTime (@Nonnegative final int nIndex, @Nonnull final Locale aContentLocale)
  {
    final String sStartDate = getAsString (nIndex);
    return PDTFromString.getLocalTimeFromString (sStartDate, aContentLocale);
  }

  /**
   * Get the value as a String, interpreted as a {@link LocalDateTime}.
   *
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @param aContentLocale
   *        Locale to use for conversion.
   * @return <code>null</code> if either the conversion to String or the parsing
   *         of the String failed.
   */
  @Nullable
  default LocalDateTime getAsLocalDateTime (@Nonnegative final int nIndex, @Nonnull final Locale aContentLocale)
  {
    final String sStartDate = getAsString (nIndex);
    return PDTFromString.getLocalDateTimeFromString (sStartDate, aContentLocale);
  }

  /**
   * Get a list of all attribute values with the same name.
   *
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>null</code> if no such attribute value exists
   */
  @Nullable
  default ICommonsList <String> getAsStringList (@Nonnegative final int nIndex)
  {
    return getAsStringList (nIndex, null);
  }

  /**
   * Get a list of all attribute values with the same name.
   *
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @param aDefault
   *        The default value to be returned, if no such attribute is present.
   * @return <code>aDefault</code> if no such attribute value exists
   */
  @Nullable
  default ICommonsList <String> getAsStringList (@Nonnegative final int nIndex,
                                                 @Nullable final ICommonsList <String> aDefault)
  {
    final Object aValue = getValue (nIndex);
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
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>null</code> if no such attribute value exists
   */
  @Nullable
  default ICommonsOrderedSet <String> getAsStringSet (@Nonnegative final int nIndex)
  {
    return getAsStringSet (nIndex, null);
  }

  /**
   * Get a set of all attribute values with the same name.
   *
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @param aDefault
   *        The default value to be returned, if no such attribute is present.
   * @return <code>aDefault</code> if no such attribute value exists
   */
  @Nullable
  default ICommonsOrderedSet <String> getAsStringSet (@Nonnegative final int nIndex,
                                                      @Nullable final ICommonsOrderedSet <String> aDefault)
  {
    final Object aValue = getValue (nIndex);
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
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @param sDesiredValue
   *        The value to be matched
   * @return <code>true</code> if an attribute with the given name is present
   *         and has the desired value
   */
  default boolean hasStringValue (@Nonnegative final int nIndex, @Nullable final String sDesiredValue)
  {
    return hasStringValue (nIndex, sDesiredValue, false);
  }

  /**
   * Check if a attribute with the given name is present in the request and has
   * the specified value. If no such attribute is present, the passed default
   * value is returned.
   *
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
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
  default boolean hasStringValue (@Nonnegative final int nIndex,
                                  @Nullable final String sDesiredValue,
                                  final boolean bDefault)
  {
    final String sValue = getAsString (nIndex);
    return sValue == null ? bDefault : sValue.equals (sDesiredValue);
  }
}
