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
  default <T> T getConvertedValue (@Nullable final T aDefault, @Nonnull final Class <T> aClass)
  {
    final Object aValue = getValue ();
    return aValue == null ? aDefault : TypeConverter.convert (aValue, aClass);
  }

  default boolean getAsBoolean ()
  {
    return TypeConverter.convertToBoolean (getValue ());
  }

  default boolean getAsBoolean (final boolean bDefault)
  {
    return TypeConverter.convertToBoolean (getValue (), bDefault);
  }

  default byte getAsByte ()
  {
    return TypeConverter.convertToByte (getValue ());
  }

  default byte getAsByte (final byte nDefault)
  {
    return TypeConverter.convertToByte (getValue (), nDefault);
  }

  default char getAsChar ()
  {
    return TypeConverter.convertToChar (getValue ());
  }

  default char getAsChar (final char cDefault)
  {
    return TypeConverter.convertToChar (getValue (), cDefault);
  }

  default double getAsDouble ()
  {
    return TypeConverter.convertToDouble (getValue ());
  }

  default double getAsDouble (final double dDefault)
  {
    return TypeConverter.convertToDouble (getValue (), dDefault);
  }

  default float getAsFloat ()
  {
    return TypeConverter.convertToFloat (getValue ());
  }

  default float getAsFloat (final float fDefault)
  {
    return TypeConverter.convertToFloat (getValue (), fDefault);
  }

  default int getAsInt ()
  {
    return TypeConverter.convertToInt (getValue ());
  }

  default int getAsInt (final int nDefault)
  {
    return TypeConverter.convertToInt (getValue (), nDefault);
  }

  default long getAsLong ()
  {
    return TypeConverter.convertToLong (getValue ());
  }

  default long getAsLong (final long nDefault)
  {
    return TypeConverter.convertToLong (getValue (), nDefault);
  }

  default short getAsShort ()
  {
    return TypeConverter.convertToShort (getValue ());
  }

  default short getAsShort (final short nDefault)
  {
    return TypeConverter.convertToShort (getValue (), nDefault);
  }

  /**
   * @return <code>getConvertedValue (String.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Class)
   */
  @Nullable
  default String getAsString ()
  {
    return getConvertedValue (String.class);
  }

  /**
   * @param sDefault
   *        The value to be returned if the retrieved value is <code>null</code>
   *        .
   * @return <code>getConvertedValue (sDefault, String.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default String getAsString (@Nullable final String sDefault)
  {
    return getConvertedValue (sDefault, String.class);
  }

  /**
   * @return <code>getConvertedValue (char[].class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Class)
   */
  @Nullable
  default char [] getAsCharArray ()
  {
    return getConvertedValue (char [].class);
  }

  /**
   * @param aDefault
   *        The value to be returned if the retrieved value is <code>null</code>
   *        .
   * @return <code>getConvertedValue (aDefault, char[].class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default char [] getAsCharArray (@Nullable final char [] aDefault)
  {
    return getConvertedValue (aDefault, char [].class);
  }

  /**
   * @return <code>getConvertedValue (BigDecimal.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Class)
   */
  @Nullable
  default BigDecimal getAsBigDecimal ()
  {
    return getConvertedValue (BigDecimal.class);
  }

  /**
   * @param aDefault
   *        The value to be returned if the retrieved value is <code>null</code>
   *        .
   * @return <code>getConvertedValue (sDefault, BigDecimal.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default BigDecimal getAsBigDecimal (@Nullable final BigDecimal aDefault)
  {
    return getConvertedValue (aDefault, BigDecimal.class);
  }

  /**
   * @return <code>getConvertedValue (BigInteger.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Class)
   */
  @Nullable
  default BigInteger getAsBigInteger ()
  {
    return getConvertedValue (BigInteger.class);
  }

  /**
   * @param aDefault
   *        The value to be returned if the retrieved value is <code>null</code>
   *        .
   * @return <code>getConvertedValue (sDefault, BigInteger.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default BigInteger getAsBigInteger (@Nullable final BigInteger aDefault)
  {
    return getConvertedValue (aDefault, BigInteger.class);
  }

  /**
   * @return <code>getConvertedValue (LocalDate.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Class)
   */
  @Nullable
  default LocalDate getAsLocalDate ()
  {
    return getConvertedValue (LocalDate.class);
  }

  /**
   * @param aDefault
   *        The value to be returned if the retrieved value is <code>null</code>
   *        .
   * @return <code>getConvertedValue (aDefault, LocalDate.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default LocalDate getAsLocalDate (@Nullable final LocalDate aDefault)
  {
    return getConvertedValue (aDefault, LocalDate.class);
  }

  /**
   * @return <code>getConvertedValue (LocalTime.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Class)
   */
  @Nullable
  default LocalTime getAsLocalTime ()
  {
    return getConvertedValue (LocalTime.class);
  }

  /**
   * @param aDefault
   *        The value to be returned if the retrieved value is <code>null</code>
   *        .
   * @return <code>getConvertedValue (aDefault, LocalTime.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default LocalTime getAsLocalTime (@Nullable final LocalTime aDefault)
  {
    return getConvertedValue (aDefault, LocalTime.class);
  }

  /**
   * @return <code>getConvertedValue (LocalDateTime.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Class)
   */
  @Nullable
  default LocalDateTime getAsLocalDateTime ()
  {
    return getConvertedValue (LocalDateTime.class);
  }

  /**
   * @param aDefault
   *        The value to be returned if the retrieved value is <code>null</code>
   *        .
   * @return <code>getConvertedValue (aDefault, LocalDateTime.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Object,Class)
   */
  @Nullable
  default LocalDateTime getAsLocalDateTime (@Nullable final LocalDateTime aDefault)
  {
    return getConvertedValue (LocalDateTime.class);
  }

  /**
   * @return <code>getConvertedValue (byte[].class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Class)
   */
  @Nullable
  default byte [] getAsByteArray ()
  {
    return getConvertedValue (byte [].class);
  }

  /**
   * @return <code>getConvertedValue (Boolean.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Class)
   */
  @Nullable
  default Boolean getAsBooleanObj ()
  {
    return getConvertedValue (Boolean.class);
  }

  /**
   * @return <code>getConvertedValue (Byte.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Class)
   */
  @Nullable
  default Byte getAsByteObj ()
  {
    return getConvertedValue (Byte.class);
  }

  /**
   * @return <code>getConvertedValue (Character.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Class)
   */
  @Nullable
  default Character getAsCharObj ()
  {
    return getConvertedValue (Character.class);
  }

  /**
   * @return <code>getConvertedValue (Double.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Class)
   */
  @Nullable
  default Double getAsDoubleObj ()
  {
    return getConvertedValue (Double.class);
  }

  /**
   * @return <code>getConvertedValue (Float.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Class)
   */
  @Nullable
  default Float getAsFloatObj ()
  {
    return getConvertedValue (Float.class);
  }

  /**
   * @return <code>getConvertedValue (Integer.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Class)
   */
  @Nullable
  default Integer getAsIntObj ()
  {
    return getConvertedValue (Integer.class);
  }

  /**
   * @return <code>getConvertedValue (Long.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Class)
   */
  @Nullable
  default Long getAsLongObj ()
  {
    return getConvertedValue (Long.class);
  }

  /**
   * @return <code>getConvertedValue (Short.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Class)
   */
  @Nullable
  default Short getAsShortObj ()
  {
    return getConvertedValue (Short.class);
  }

  /**
   * @return <code>getConvertedValue (Blob.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Class)
   */
  @Nullable
  default java.sql.Blob getAsSqlBlob ()
  {
    return getConvertedValue (java.sql.Blob.class);
  }

  /**
   * @return <code>getConvertedValue (Clob.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Class)
   */
  @Nullable
  default java.sql.Clob getAsSqlClob ()
  {
    return getConvertedValue (java.sql.Clob.class);
  }

  /**
   * @return <code>getConvertedValue (Date.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Class)
   */
  @Nullable
  default java.sql.Date getAsSqlDate ()
  {
    return getConvertedValue (java.sql.Date.class);
  }

  /**
   * @return <code>getConvertedValue (NClob.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Class)
   */
  @Nullable
  default java.sql.NClob getAsSqlNClob ()
  {
    return getConvertedValue (java.sql.NClob.class);
  }

  /**
   * @return <code>getConvertedValue (RowId.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Class)
   */
  @Nullable
  default java.sql.RowId getAsSqlRowId ()
  {
    return getConvertedValue (java.sql.RowId.class);
  }

  /**
   * @return <code>getConvertedValue (Time.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Class)
   */
  @Nullable
  default java.sql.Time getAsSqlTime ()
  {
    return getConvertedValue (java.sql.Time.class);
  }

  /**
   * @return <code>getConvertedValue (Timestamp.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Class)
   */
  @Nullable
  default java.sql.Timestamp getAsSqlTimestamp ()
  {
    return getConvertedValue (java.sql.Timestamp.class);
  }
}
