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

import com.helger.annotation.Nonnegative;
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
 */
@FunctionalInterface
public interface IGetterByIndexExtTrait extends IGetterByIndexTrait
{
  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (nIndex,null,Blob.class)</code>
   * @see #getConvertedValue(int,Object,Class)
   */
  @Nullable
  default java.sql.Blob getAsSqlBlob (@Nonnegative final int nIndex)
  {
    return getConvertedValue (nIndex, null, java.sql.Blob.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (nIndex,null,Clob.class)</code>
   * @see #getConvertedValue(int,Object,Class)
   */
  @Nullable
  default java.sql.Clob getAsSqlClob (@Nonnegative final int nIndex)
  {
    return getConvertedValue (nIndex, null, java.sql.Clob.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (nIndex,null,Date.class)</code>
   * @see #getConvertedValue(int,Object,Class)
   */
  @Nullable
  default java.sql.Date getAsSqlDate (@Nonnegative final int nIndex)
  {
    return getConvertedValue (nIndex, null, java.sql.Date.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (nIndex,null,NClob.class)</code>
   * @see #getConvertedValue(int,Object,Class)
   */
  @Nullable
  default java.sql.NClob getAsSqlNClob (@Nonnegative final int nIndex)
  {
    return getConvertedValue (nIndex, null, java.sql.NClob.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (nIndex,null,RowId.class)</code>
   * @see #getConvertedValue(int,Object,Class)
   */
  @Nullable
  default java.sql.RowId getAsSqlRowId (@Nonnegative final int nIndex)
  {
    return getConvertedValue (nIndex, null, java.sql.RowId.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (nIndex,null,Time.class)</code>
   * @see #getConvertedValue(int,Object,Class)
   */
  @Nullable
  default java.sql.Time getAsSqlTime (@Nonnegative final int nIndex)
  {
    return getConvertedValue (nIndex, null, java.sql.Time.class);
  }

  /**
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @return <code>getConvertedValue (nIndex,null,Timestamp.class)</code>
   * @see #getConvertedValue(int,Object,Class)
   */
  @Nullable
  default java.sql.Timestamp getAsSqlTimestamp (@Nonnegative final int nIndex)
  {
    return getConvertedValue (nIndex, null, java.sql.Timestamp.class);
  }

  /**
   * Get the value as a String, interpreted as a {@link LocalDate}.
   *
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @param aContentLocale
   *        Locale to use for conversion.
   * @return <code>null</code> if either the conversion to String or the parsing of the String
   *         failed.
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
   * @return <code>null</code> if either the conversion to String or the parsing of the String
   *         failed.
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
   * @return <code>null</code> if either the conversion to String or the parsing of the String
   *         failed.
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
