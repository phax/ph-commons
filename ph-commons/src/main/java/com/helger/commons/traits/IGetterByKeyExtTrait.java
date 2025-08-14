/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;

import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsLinkedHashSet;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsOrderedSet;
import com.helger.commons.datetime.PDTFromString;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * A generic convert Object to anything with convenience API.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        The key type. E.g. String etc.
 */
@FunctionalInterface
public interface IGetterByKeyExtTrait <KEYTYPE> extends IGetterByKeyTrait <KEYTYPE>
{
  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @return <code>getConvertedValue (aKey,null,Blob.class)</code>
   * @see #getConvertedValue(Object,Object,Class)
   */
  @Nullable
  default java.sql.Blob getAsSqlBlob (@Nullable final KEYTYPE aKey)
  {
    return getConvertedValue (aKey, null, java.sql.Blob.class);
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @return <code>getConvertedValue (aKey,null,Clob.class)</code>
   * @see #getConvertedValue(Object,Object,Class)
   */
  @Nullable
  default java.sql.Clob getAsSqlClob (@Nullable final KEYTYPE aKey)
  {
    return getConvertedValue (aKey, null, java.sql.Clob.class);
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @return <code>getConvertedValue (aKey,null,Date.class)</code>
   * @see #getConvertedValue(Object,Object,Class)
   */
  @Nullable
  default java.sql.Date getAsSqlDate (@Nullable final KEYTYPE aKey)
  {
    return getConvertedValue (aKey, null, java.sql.Date.class);
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @return <code>getConvertedValue (aKey,null,NClob.class)</code>
   * @see #getConvertedValue(Object,Object,Class)
   */
  @Nullable
  default java.sql.NClob getAsSqlNClob (@Nullable final KEYTYPE aKey)
  {
    return getConvertedValue (aKey, null, java.sql.NClob.class);
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @return <code>getConvertedValue (aKey,null,RowId.class)</code>
   * @see #getConvertedValue(Object,Object,Class)
   */
  @Nullable
  default java.sql.RowId getAsSqlRowId (@Nullable final KEYTYPE aKey)
  {
    return getConvertedValue (aKey, null, java.sql.RowId.class);
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @return <code>getConvertedValue (aKey,null,Time.class)</code>
   * @see #getConvertedValue(Object,Object,Class)
   */
  @Nullable
  default java.sql.Time getAsSqlTime (@Nullable final KEYTYPE aKey)
  {
    return getConvertedValue (aKey, null, java.sql.Time.class);
  }

  /**
   * @param aKey
   *        The key to be accessed. May be <code>null</code>.
   * @return <code>getConvertedValue (aKey,null,Timestamp.class)</code>
   * @see #getConvertedValue(Object,Object,Class)
   */
  @Nullable
  default java.sql.Timestamp getAsSqlTimestamp (@Nullable final KEYTYPE aKey)
  {
    return getConvertedValue (aKey, null, java.sql.Timestamp.class);
  }

  /**
   * Get the value as a String, interpreted as a {@link LocalDate}.
   *
   * @param aKey
   *        The key to check. May be <code>null</code>.
   * @param aContentLocale
   *        Locale to use for conversion.
   * @return <code>null</code> if either the conversion to String or the parsing of the String
   *         failed.
   */
  @Nullable
  default LocalDate getAsLocalDate (@Nullable final KEYTYPE aKey, @Nonnull final Locale aContentLocale)
  {
    return getConvertedValue (aKey, null, LocalDate.class);
  }

  /**
   * Get the value as a String, interpreted as a {@link LocalTime}.
   *
   * @param aKey
   *        The key to check. May be <code>null</code>.
   * @param aContentLocale
   *        Locale to use for conversion.
   * @return <code>null</code> if either the conversion to String or the parsing of the String
   *         failed.
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
   * @return <code>null</code> if either the conversion to String or the parsing of the String
   *         failed.
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
      if (aValue instanceof final String [] aArray)
      {
        // multiple values passed in the request
        return new CommonsArrayList <> (aArray);
      }
      if (aValue instanceof final String sValue)
      {
        // single value passed in the request
        return new CommonsArrayList <> (sValue);
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
      if (aValue instanceof final String [] aArray)
      {
        // multiple values passed in the request
        return new CommonsLinkedHashSet <> (aArray);
      }
      if (aValue instanceof final String sValue)
      {
        // single value passed in the request
        return new CommonsLinkedHashSet <> (sValue);
      }
    }
    return aDefault;
  }
}
