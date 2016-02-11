package com.helger.commons.traits;

import java.math.BigDecimal;
import java.math.BigInteger;

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
public interface IConvertibleTrait
{
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
   * @return <code>true</code> if the value is <code>null</code>. Same as
   *         <code>getValue()==null</code>.
   */
  default boolean isNullValue ()
  {
    return getValue () == null;
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
  default <T> T getCastedValue (@Nonnull final Class <T> aClass) throws ClassCastException
  {
    return aClass.cast (getValue ());
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
    return TypeConverter.convertIfNecessary (getValue (), aClass);
  }

  default boolean getAsBoolean () throws TypeConverterException
  {
    return TypeConverter.convertToBoolean (getValue ());
  }

  default boolean getAsBoolean (final boolean bDefault)
  {
    return TypeConverter.convertToBoolean (getValue (), bDefault);
  }

  default byte getAsByte () throws TypeConverterException
  {
    return TypeConverter.convertToByte (getValue ());
  }

  default byte getAsByte (final byte nDefault)
  {
    return TypeConverter.convertToByte (getValue (), nDefault);
  }

  default char getAsChar () throws TypeConverterException
  {
    return TypeConverter.convertToChar (getValue ());
  }

  default char getAsChar (final char cDefault)
  {
    return TypeConverter.convertToChar (getValue (), cDefault);
  }

  default double getAsDouble () throws TypeConverterException
  {
    return TypeConverter.convertToDouble (getValue ());
  }

  default double getAsDouble (final double dDefault)
  {
    return TypeConverter.convertToDouble (getValue (), dDefault);
  }

  default float getAsFloat () throws TypeConverterException
  {
    return TypeConverter.convertToFloat (getValue ());
  }

  default float getAsFloat (final float fDefault)
  {
    return TypeConverter.convertToFloat (getValue (), fDefault);
  }

  default int getAsInt () throws TypeConverterException
  {
    return TypeConverter.convertToInt (getValue ());
  }

  default int getAsInt (final int nDefault)
  {
    return TypeConverter.convertToInt (getValue (), nDefault);
  }

  default long getAsLong () throws TypeConverterException
  {
    return TypeConverter.convertToLong (getValue ());
  }

  default long getAsLong (final long nDefault)
  {
    return TypeConverter.convertToLong (getValue (), nDefault);
  }

  default short getAsShort () throws TypeConverterException
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
  default String getAsString () throws TypeConverterException
  {
    return getConvertedValue (String.class);
  }

  /**
   * @return <code>getConvertedValue (BigDecimal.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Class)
   */
  @Nullable
  default BigDecimal getAsBigDecimal () throws TypeConverterException
  {
    return getConvertedValue (BigDecimal.class);
  }

  /**
   * @return <code>getConvertedValue (BigInteger.class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Class)
   */
  @Nullable
  default BigInteger getAsBigInteger () throws TypeConverterException
  {
    return getConvertedValue (BigInteger.class);
  }

  /**
   * @return <code>getConvertedValue (byte[].class)</code>
   * @throws TypeConverterException
   *         in case of an error
   * @see #getConvertedValue(Class)
   */
  @Nullable
  default byte [] getAsByteArray () throws TypeConverterException
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
  default Boolean getAsBooleanObj () throws TypeConverterException
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
  default Byte getAsByteObj () throws TypeConverterException
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
  default Character getAsCharObj () throws TypeConverterException
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
  default Float getAsFloatObj () throws TypeConverterException
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
  default Integer getAsIntObj () throws TypeConverterException
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
  default Long getAsLongObj () throws TypeConverterException
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
  default Short getAsShortObj () throws TypeConverterException
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
  default java.sql.Blob getAsBlob () throws TypeConverterException
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
  default java.sql.Clob getAsClob () throws TypeConverterException
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
  default java.sql.Date getAsDate () throws TypeConverterException
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
  default java.sql.NClob getAsNClob () throws TypeConverterException
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
  default java.sql.RowId getAsRowId () throws TypeConverterException
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
  default java.sql.Time getAsTime () throws TypeConverterException
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
  default java.sql.Timestamp getAsTimestamp () throws TypeConverterException
  {
    return getConvertedValue (java.sql.Timestamp.class);
  }

  /**
   * Convert the passed object to a convertible object :)
   * 
   * @param o
   *        Source object.
   * @return The convertible object
   */
  @Nonnull
  static IConvertibleTrait wrap (final Object o)
  {
    return () -> o;
  }
}
