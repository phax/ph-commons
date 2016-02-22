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
package com.helger.commons.traits;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.typeconvert.TypeConverter;
import com.helger.commons.typeconvert.TypeConverterException;

/**
 * A generic convert Object to anything with convenience API.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IConvertibleByIndexTrait
{
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
  default <T> T getCastedValue (@Nonnegative final int nIndex,
                                @Nonnull final Class <T> aClass) throws ClassCastException
  {
    return aClass.cast (getValue (nIndex));
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
    return TypeConverter.convertIfNecessary (getValue (nIndex), aClass);
  }

  default boolean getAsBoolean (@Nonnegative final int nIndex) throws TypeConverterException
  {
    return TypeConverter.convertToBoolean (getValue (nIndex));
  }

  default boolean getAsBoolean (@Nonnegative final int nIndex, final boolean bDefault)
  {
    return TypeConverter.convertToBoolean (getValue (nIndex), bDefault);
  }

  default byte getAsByte (@Nonnegative final int nIndex) throws TypeConverterException
  {
    return TypeConverter.convertToByte (getValue (nIndex));
  }

  default byte getAsByte (@Nonnegative final int nIndex, final byte nDefault)
  {
    return TypeConverter.convertToByte (getValue (nIndex), nDefault);
  }

  default char getAsChar (@Nonnegative final int nIndex) throws TypeConverterException
  {
    return TypeConverter.convertToChar (getValue (nIndex));
  }

  default char getAsChar (@Nonnegative final int nIndex, final char cDefault)
  {
    return TypeConverter.convertToChar (getValue (nIndex), cDefault);
  }

  default double getAsDouble (@Nonnegative final int nIndex) throws TypeConverterException
  {
    return TypeConverter.convertToDouble (getValue (nIndex));
  }

  default double getAsDouble (@Nonnegative final int nIndex, final double dDefault)
  {
    return TypeConverter.convertToDouble (getValue (nIndex), dDefault);
  }

  default float getAsFloat (@Nonnegative final int nIndex) throws TypeConverterException
  {
    return TypeConverter.convertToFloat (getValue (nIndex));
  }

  default float getAsFloat (@Nonnegative final int nIndex, final float fDefault)
  {
    return TypeConverter.convertToFloat (getValue (nIndex), fDefault);
  }

  default int getAsInt (@Nonnegative final int nIndex) throws TypeConverterException
  {
    return TypeConverter.convertToInt (getValue (nIndex));
  }

  default int getAsInt (@Nonnegative final int nIndex, final int nDefault)
  {
    return TypeConverter.convertToInt (getValue (nIndex), nDefault);
  }

  default long getAsLong (@Nonnegative final int nIndex) throws TypeConverterException
  {
    return TypeConverter.convertToLong (getValue (nIndex));
  }

  default long getAsLong (@Nonnegative final int nIndex, final long nDefault)
  {
    return TypeConverter.convertToLong (getValue (nIndex), nDefault);
  }

  default short getAsShort (@Nonnegative final int nIndex) throws TypeConverterException
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
   * @return <code>getConvertedValue (String.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default String getAsString (@Nonnegative final int nIndex) throws TypeConverterException
  {
    return getConvertedValue (nIndex, String.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (BigDecimal.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default BigDecimal getAsBigDecimal (@Nonnegative final int nIndex) throws TypeConverterException
  {
    return getConvertedValue (nIndex, BigDecimal.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (BigInteger.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default BigInteger getAsBigInteger (@Nonnegative final int nIndex) throws TypeConverterException
  {
    return getConvertedValue (nIndex, BigInteger.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (LocalDate.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default LocalDate getAsLocalDate (@Nonnegative final int nIndex) throws TypeConverterException
  {
    return getConvertedValue (nIndex, LocalDate.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (LocalTime.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default LocalTime getAsLocalTime (@Nonnegative final int nIndex) throws TypeConverterException
  {
    return getConvertedValue (nIndex, LocalTime.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (LocalDateTime.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default LocalDateTime getAsLocalDateTime (@Nonnegative final int nIndex) throws TypeConverterException
  {
    return getConvertedValue (nIndex, LocalDateTime.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (byte[].class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default byte [] getAsByteArray (@Nonnegative final int nIndex) throws TypeConverterException
  {
    return getConvertedValue (nIndex, byte [].class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (Boolean.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default Boolean getAsBooleanObj (@Nonnegative final int nIndex) throws TypeConverterException
  {
    return getConvertedValue (nIndex, Boolean.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (Byte.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default Byte getAsByteObj (@Nonnegative final int nIndex) throws TypeConverterException
  {
    return getConvertedValue (nIndex, Byte.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (Character.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default Character getAsCharObj (@Nonnegative final int nIndex) throws TypeConverterException
  {
    return getConvertedValue (nIndex, Character.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (Double.class)</code>
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
   * @return <code>getConvertedValue (Float.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default Float getAsFloatObj (@Nonnegative final int nIndex) throws TypeConverterException
  {
    return getConvertedValue (nIndex, Float.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (Integer.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default Integer getAsIntObj (@Nonnegative final int nIndex) throws TypeConverterException
  {
    return getConvertedValue (nIndex, Integer.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (Long.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default Long getAsLongObj (@Nonnegative final int nIndex) throws TypeConverterException
  {
    return getConvertedValue (nIndex, Long.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (Short.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default Short getAsShortObj (@Nonnegative final int nIndex) throws TypeConverterException
  {
    return getConvertedValue (nIndex, Short.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (Blob.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default java.sql.Blob getAsSqlBlob (@Nonnegative final int nIndex) throws TypeConverterException
  {
    return getConvertedValue (nIndex, java.sql.Blob.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (Clob.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default java.sql.Clob getAsSqlClob (@Nonnegative final int nIndex) throws TypeConverterException
  {
    return getConvertedValue (nIndex, java.sql.Clob.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (Date.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default java.sql.Date getAsSqlDate (@Nonnegative final int nIndex) throws TypeConverterException
  {
    return getConvertedValue (nIndex, java.sql.Date.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (NClob.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default java.sql.NClob getAsSqlNClob (@Nonnegative final int nIndex) throws TypeConverterException
  {
    return getConvertedValue (nIndex, java.sql.NClob.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (RowId.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default java.sql.RowId getAsSqlRowId (@Nonnegative final int nIndex) throws TypeConverterException
  {
    return getConvertedValue (nIndex, java.sql.RowId.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (Time.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default java.sql.Time getAsSqlTime (@Nonnegative final int nIndex) throws TypeConverterException
  {
    return getConvertedValue (nIndex, java.sql.Time.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (Timestamp.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(int,Class)
   */
  @Nullable
  default java.sql.Timestamp getAsSqlTimestamp (@Nonnegative final int nIndex) throws TypeConverterException
  {
    return getConvertedValue (nIndex, java.sql.Timestamp.class);
  }
}
