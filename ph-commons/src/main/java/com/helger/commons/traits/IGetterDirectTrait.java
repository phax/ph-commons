/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.lang.GenericReflection;
import com.helger.commons.typeconvert.TypeConverter;
import com.helger.commons.typeconvert.TypeConverterException;

/**
 * A generic convert Object to anything with convenience API.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IGetterDirectTrait
{
  /**
   * @return The value of interest. No <code>null</code> constraints applicable.
   */
  @Nullable
  Object getValue ();

  /**
   * @return The class of the value or <code>null</code> if no value is
   *         contained.
   */
  @Nullable
  default Class <?> getValueClass ()
  {
    final Object aValue = getValue ();
    return aValue == null ? null : aValue.getClass ();
  }

  /**
   * @return <code>true</code> if the value is not <code>null</code>. Same as
   *         <code>getValue()!=null</code>.
   */
  default boolean hasValue ()
  {
    return getValue () != null;
  }

  /**
   * @return <code>true</code> if the value is <code>null</code>. Same as
   *         <code>getValue()==null</code>.
   */
  default boolean hasNoValue ()
  {
    return getValue () == null;
  }

  /**
   * Get the contained value casted to the return type.
   *
   * @return The object value casted to the passed class. May be
   *         <code>null</code> if the contained value is <code>null</code>.
   * @throws ClassCastException
   *         in case the value types are not convertible
   * @param <T>
   *        Destination type
   */
  @Nullable
  default <T> T getCastedValue ()
  {
    return GenericReflection.uncheckedCast (getValue ());
  }

  /**
   * Get the contained value casted to the return type.
   *
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
  default <T> T getCastedValue (@Nullable final T aDefault)
  {
    final Object aValue = getValue ();
    return aValue == null ? aDefault : GenericReflection.uncheckedCast (aValue);
  }

  /**
   * Get the contained value casted to the specified class.
   *
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
  default <T> T getCastedValue (@Nonnull final Class <T> aClass)
  {
    return aClass.cast (getValue ());
  }

  /**
   * Get the contained value casted to the specified class.
   *
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
  default <T> T getCastedValue (@Nullable final T aDefault, @Nonnull final Class <T> aClass)
  {
    final Object aValue = getValue ();
    return aValue == null ? aDefault : aClass.cast (aValue);
  }

  /**
   * Get the contained value converted using TypeConverter to the passed class.
   *
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
  default <T> T getConvertedValue (@Nonnull final Class <T> aClass)
  {
    return TypeConverter.convert (getValue (), aClass);
  }

  /**
   * Get the contained value converted using TypeConverter to the passed class.
   *
   * @param aDefault
   *        The value to be returned if the retrieved value is <code>null</code>
   *        or if type conversion fails.
   * @param aClass
   *        The class to convert to. May not be <code>null</code>.
   * @return The object value casted to the passed class. May be
   *         <code>null</code> if the contained value is <code>null</code>.
   * @param <T>
   *        Destination type
   */
  @Nullable
  default <T> T getConvertedValue (@Nullable final T aDefault, @Nonnull final Class <T> aClass)
  {
    final Object aValue = getValue ();
    return aValue == null ? aDefault : TypeConverter.convert (aValue, aClass, aDefault);
  }

  default boolean getAsBoolean ()
  {
    // throws TypeConverterException if key is null
    return TypeConverter.convertToBoolean (getValue ());
  }

  default boolean getAsBoolean (final boolean bDefault)
  {
    return TypeConverter.convertToBoolean (getValue (), bDefault);
  }

  default byte getAsByte ()
  {
    // throws TypeConverterException if value is null
    return TypeConverter.convertToByte (getValue ());
  }

  default byte getAsByte (final byte nDefault)
  {
    return TypeConverter.convertToByte (getValue (), nDefault);
  }

  default char getAsChar ()
  {
    // throws TypeConverterException if value is null
    return TypeConverter.convertToChar (getValue ());
  }

  default char getAsChar (final char cDefault)
  {
    return TypeConverter.convertToChar (getValue (), cDefault);
  }

  default double getAsDouble ()
  {
    // throws TypeConverterException if value is null
    return TypeConverter.convertToDouble (getValue ());
  }

  default double getAsDouble (final double dDefault)
  {
    return TypeConverter.convertToDouble (getValue (), dDefault);
  }

  default float getAsFloat ()
  {
    // throws TypeConverterException if value is null
    return TypeConverter.convertToFloat (getValue ());
  }

  default float getAsFloat (final float fDefault)
  {
    return TypeConverter.convertToFloat (getValue (), fDefault);
  }

  default int getAsInt ()
  {
    // throws TypeConverterException if value is null
    return TypeConverter.convertToInt (getValue ());
  }

  default int getAsInt (final int nDefault)
  {
    return TypeConverter.convertToInt (getValue (), nDefault);
  }

  default long getAsLong ()
  {
    // throws TypeConverterException if value is null
    return TypeConverter.convertToLong (getValue ());
  }

  default long getAsLong (final long nDefault)
  {
    return TypeConverter.convertToLong (getValue (), nDefault);
  }

  default short getAsShort ()
  {
    // throws TypeConverterException if value is null
    return TypeConverter.convertToShort (getValue ());
  }

  default short getAsShort (final short nDefault)
  {
    return TypeConverter.convertToShort (getValue (), nDefault);
  }

  /**
   * @return <code>getConvertedValue (null, String.class)</code>
   * @see #getConvertedValue(Object, Class)
   */
  @Nullable
  default String getAsString ()
  {
    return getConvertedValue (null, String.class);
  }

  /**
   * @param sDefault
   *        The value to be returned if the retrieved value is <code>null</code>
   *        .
   * @return <code>getConvertedValue (sDefault, String.class)</code>
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default String getAsString (@Nullable final String sDefault)
  {
    return getConvertedValue (sDefault, String.class);
  }

  /**
   * @return <code>getConvertedValue (null, char[].class)</code>
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default char [] getAsCharArray ()
  {
    return getConvertedValue (null, char [].class);
  }

  /**
   * @param aDefault
   *        The value to be returned if the retrieved value is <code>null</code>
   *        .
   * @return <code>getConvertedValue (aDefault, char[].class)</code>
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default char [] getAsCharArray (@Nullable final char [] aDefault)
  {
    return getConvertedValue (aDefault, char [].class);
  }

  /**
   * @return <code>getConvertedValue (null, BigDecimal.class)</code>
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default BigDecimal getAsBigDecimal ()
  {
    return getConvertedValue (null, BigDecimal.class);
  }

  /**
   * @param aDefault
   *        The value to be returned if the retrieved value is <code>null</code>
   *        .
   * @return <code>getConvertedValue (sDefault, BigDecimal.class)</code>
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default BigDecimal getAsBigDecimal (@Nullable final BigDecimal aDefault)
  {
    return getConvertedValue (aDefault, BigDecimal.class);
  }

  /**
   * @return <code>getConvertedValue (null, BigInteger.class)</code>
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default BigInteger getAsBigInteger ()
  {
    return getConvertedValue (null, BigInteger.class);
  }

  /**
   * @param aDefault
   *        The value to be returned if the retrieved value is <code>null</code>
   *        .
   * @return <code>getConvertedValue (sDefault, BigInteger.class)</code>
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default BigInteger getAsBigInteger (@Nullable final BigInteger aDefault)
  {
    return getConvertedValue (aDefault, BigInteger.class);
  }

  /**
   * @return <code>getConvertedValue (null, LocalDate.class)</code>
   * @see #getConvertedValue(Object, Class)
   */
  @Nullable
  default LocalDate getAsLocalDate ()
  {
    return getConvertedValue (null, LocalDate.class);
  }

  /**
   * @param aDefault
   *        The value to be returned if the retrieved value is <code>null</code>
   *        .
   * @return <code>getConvertedValue (aDefault, LocalDate.class)</code>
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default LocalDate getAsLocalDate (@Nullable final LocalDate aDefault)
  {
    return getConvertedValue (aDefault, LocalDate.class);
  }

  /**
   * @return <code>getConvertedValue (null, LocalTime.class)</code>
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default LocalTime getAsLocalTime ()
  {
    return getConvertedValue (null, LocalTime.class);
  }

  /**
   * @param aDefault
   *        The value to be returned if the retrieved value is <code>null</code>
   *        .
   * @return <code>getConvertedValue (aDefault, LocalTime.class)</code>
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default LocalTime getAsLocalTime (@Nullable final LocalTime aDefault)
  {
    return getConvertedValue (aDefault, LocalTime.class);
  }

  /**
   * @return <code>getConvertedValue (null, LocalDateTime.class)</code>
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default LocalDateTime getAsLocalDateTime ()
  {
    return getConvertedValue (null, LocalDateTime.class);
  }

  /**
   * @param aDefault
   *        The value to be returned if the retrieved value is <code>null</code>
   *        .
   * @return <code>getConvertedValue (aDefault, LocalDateTime.class)</code>
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default LocalDateTime getAsLocalDateTime (@Nullable final LocalDateTime aDefault)
  {
    return getConvertedValue (aDefault, LocalDateTime.class);
  }

  /**
   * @return <code>getConvertedValue (null, byte[].class)</code>
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default byte [] getAsByteArray ()
  {
    return getConvertedValue (null, byte [].class);
  }

  /**
   * @return <code>getConvertedValue (null, Boolean.class)</code>
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default Boolean getAsBooleanObj ()
  {
    return getConvertedValue (null, Boolean.class);
  }

  /**
   * @return <code>getConvertedValue (null, Byte.class)</code>
   * @see #getConvertedValue(Object, Class)
   */
  @Nullable
  default Byte getAsByteObj ()
  {
    return getConvertedValue (null, Byte.class);
  }

  /**
   * @return <code>getConvertedValue (null, Character.class)</code>
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default Character getAsCharObj ()
  {
    return getConvertedValue (null, Character.class);
  }

  /**
   * @return <code>getConvertedValue (null, Double.class)</code>
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default Double getAsDoubleObj ()
  {
    return getConvertedValue (null, Double.class);
  }

  /**
   * @return <code>getConvertedValue (null, Float.class)</code>
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default Float getAsFloatObj ()
  {
    return getConvertedValue (null, Float.class);
  }

  /**
   * @return <code>getConvertedValue (null, Integer.class)</code>
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default Integer getAsIntObj ()
  {
    return getConvertedValue (null, Integer.class);
  }

  /**
   * @return <code>getConvertedValue (null, Long.class)</code>
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default Long getAsLongObj ()
  {
    return getConvertedValue (null, Long.class);
  }

  /**
   * @return <code>getConvertedValue (null, Short.class)</code>
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default Short getAsShortObj ()
  {
    return getConvertedValue (null, Short.class);
  }

  /**
   * @return <code>getConvertedValue (null, Blob.class)</code>
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default java.sql.Blob getAsSqlBlob ()
  {
    return getConvertedValue (null, java.sql.Blob.class);
  }

  /**
   * @return <code>getConvertedValue (null, Clob.class)</code>
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default java.sql.Clob getAsSqlClob ()
  {
    return getConvertedValue (null, java.sql.Clob.class);
  }

  /**
   * @return <code>getConvertedValue (null, Date.class)</code>
   * @see #getConvertedValue(Object, Class)
   */
  @Nullable
  default java.sql.Date getAsSqlDate ()
  {
    return getConvertedValue (null, java.sql.Date.class);
  }

  /**
   * @return <code>getConvertedValue (null, NClob.class)</code>
   * @see #getConvertedValue(Object, Class)
   */
  @Nullable
  default java.sql.NClob getAsSqlNClob ()
  {
    return getConvertedValue (null, java.sql.NClob.class);
  }

  /**
   * @return <code>getConvertedValue (null, RowId.class)</code>
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default java.sql.RowId getAsSqlRowId ()
  {
    return getConvertedValue (null, java.sql.RowId.class);
  }

  /**
   * @return <code>getConvertedValue (null, Time.class)</code>
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default java.sql.Time getAsSqlTime ()
  {
    return getConvertedValue (null, java.sql.Time.class);
  }

  /**
   * @return <code>getConvertedValue (null, Timestamp.class)</code>
   * @see #getConvertedValue(Object, Class)
   */
  @Nullable
  default java.sql.Timestamp getAsSqlTimestamp ()
  {
    return getConvertedValue (null, java.sql.Timestamp.class);
  }
}
