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
package com.helger.commons.error.list;

import java.util.Collection;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.RegEx;

import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.error.IError;
import com.helger.commons.error.level.EErrorLevel;
import com.helger.commons.error.level.IErrorLevel;
import com.helger.commons.error.level.IHasErrorLevels;
import com.helger.commons.regex.RegExHelper;

/**
 * Interface for a list of {@link IError} objects.
 *
 * @author Philip Helger
 * @since 8.5.0
 */
@MustImplementEqualsAndHashcode
public interface IErrorList extends ICommonsList <IError>, IHasErrorLevels <IError>
{
  /**
   * Check if no entry for the specified field is present
   *
   * @param sSearchFieldName
   *        The field name to search.
   * @return <code>true</code> if no entry for the specified field is present
   */
  default boolean hasNoEntryForField (@Nullable final String sSearchFieldName)
  {
    return containsNone (x -> x.hasErrorFieldName (sSearchFieldName));
  }

  /**
   * Check if no entry for the specified fields are present
   *
   * @param aSearchFieldNames
   *        The field names to search.
   * @return <code>true</code> if no entry for any of the specified fields is
   *         present
   */
  default boolean hasNoEntryForFields (@Nullable final String... aSearchFieldNames)
  {
    if (aSearchFieldNames != null)
      for (final String sSearchFieldName : aSearchFieldNames)
        if (hasEntryForField (sSearchFieldName))
          return false;
    return true;
  }

  /**
   * Check if any entry for the specified field is present
   *
   * @param sSearchFieldName
   *        The field name to search.
   * @return <code>true</code> if an entry for the specified field is present
   */
  default boolean hasEntryForField (@Nullable final String sSearchFieldName)
  {
    return containsAny (x -> x.hasErrorFieldName (sSearchFieldName));
  }

  /**
   * Check if any entry for the specified field and the specified error level is
   * present
   *
   * @param sSearchFieldName
   *        The field name to search.
   * @param aErrorLevel
   *        The exact form error level to search. May not be <code>null</code>
   * @return <code>true</code> if an entry for the specified field is present
   *         that has exactly the specified form error level
   */
  default boolean hasEntryForField (@Nullable final String sSearchFieldName, @Nullable final IErrorLevel aErrorLevel)
  {
    return aErrorLevel != null &&
           containsAny (x -> x.hasErrorFieldName (sSearchFieldName) && x.hasErrorLevel (aErrorLevel));
  }

  /**
   * Check if any error entry for the specified field is present
   *
   * @param sSearchFieldName
   *        The field name to search.
   * @return <code>true</code> if an error entry for the specified field is
   *         present
   */
  default boolean hasErrorForField (@Nullable final String sSearchFieldName)
  {
    return containsAny (x -> x.hasErrorFieldName (sSearchFieldName) && x.isError ());
  }

  /**
   * Check if any entry for the specified fields are present
   *
   * @param aSearchFieldNames
   *        The field names to search.
   * @return <code>true</code> if an entry for at least one of the specified
   *         fields is present
   */
  default boolean hasEntryForFields (@Nullable final String... aSearchFieldNames)
  {
    if (aSearchFieldNames != null)
      for (final String sSearchFieldName : aSearchFieldNames)
        if (hasEntryForField (sSearchFieldName))
          return true;
    return false;
  }

  /**
   * Get a sub-list with all entries for the specified field name
   *
   * @param sSearchFieldName
   *        The field name to search.
   * @return Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  default IErrorList getListOfField (@Nullable final String sSearchFieldName)
  {
    return getSubList (x -> x.hasErrorFieldName (sSearchFieldName));
  }

  /**
   * Get a sub-list with all entries for the specified field names
   *
   * @param aSearchFieldNames
   *        The field names to search.
   * @return Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  default IErrorList getListOfFields (@Nullable final String... aSearchFieldNames)
  {
    if (ArrayHelper.isEmpty (aSearchFieldNames))
    {
      // Empty sublist
      return getSubList (x -> false);
    }
    return getSubList (x -> x.hasErrorFieldName () && ArrayHelper.contains (aSearchFieldNames, x.getErrorFieldName ()));
  }

  /**
   * Get a sub-list with all entries for the specified field names
   *
   * @param aSearchFieldNames
   *        The field names to search.
   * @return Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  default IErrorList getListOfFields (@Nullable final Collection <String> aSearchFieldNames)
  {
    if (CollectionHelper.isEmpty (aSearchFieldNames))
    {
      // Empty sublist
      return getSubList (x -> false);
    }
    return getSubList (x -> x.hasErrorFieldName () && aSearchFieldNames.contains (x.getErrorFieldName ()));
  }

  /**
   * Get a sub-list with all entries that have field names starting with one of
   * the supplied names.
   *
   * @param aSearchFieldNames
   *        The field names to search.
   * @return Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  default IErrorList getListOfFieldsStartingWith (@Nullable final String... aSearchFieldNames)
  {
    if (ArrayHelper.isEmpty (aSearchFieldNames))
    {
      // Empty sublist
      return getSubList (x -> false);
    }
    return getSubList (x -> x.hasErrorFieldName () &&
                            ArrayHelper.containsAny (aSearchFieldNames, y -> x.getErrorFieldName ().startsWith (y)));
  }

  /**
   * Get a sub-list with all entries that have field names matching the passed
   * regular expression.
   *
   * @param sRegExp
   *        The regular expression to compare the entries against.
   * @return Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  default IErrorList getListOfFieldsRegExp (@Nonnull @Nonempty @RegEx final String sRegExp)
  {
    return getSubList (x -> x.hasErrorFieldName () &&
                            RegExHelper.stringMatchesPattern (sRegExp, x.getErrorFieldName ()));
  }

  /**
   * Get a sub-list with all entries that does not contain an error field name.
   *
   * @return Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  default IErrorList getListWithoutField ()
  {
    return getSubList (IError::hasNoErrorFieldName);
  }

  /**
   * Get a resource error group containing only the failure elements. All error
   * levels except {@link EErrorLevel#SUCCESS} are considered to be a failure!
   *
   * @return A non-<code>null</code> error list containing only the failures.
   */
  @Nonnull
  default IErrorList getAllFailures ()
  {
    return getSubList (IError::isFailure);
  }

  /**
   * Get a resource error group containing only the error elements. All error
   * levels &ge; {@link EErrorLevel#ERROR} are considered to be an error!
   *
   * @return A non-<code>null</code> error list containing only the errors.
   */
  @Nonnull
  default IErrorList getAllErrors ()
  {
    return getSubList (IError::isError);
  }

  /**
   * Get a sub-list with all entries that match the provided predicate.
   *
   * @param aFilter
   *        The filter to be used. May be <code>null</code> in which a copy is
   *        returned.
   * @return Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  IErrorList getSubList (@Nullable Predicate <? super IError> aFilter);

  /**
   * Get a list with all contained texts.
   *
   * @param aContentLocale
   *        The content locale to used. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  default ICommonsList <String> getAllTexts (@Nonnull final Locale aContentLocale)
  {
    return getAllDataItems (x -> x.getErrorText (aContentLocale));
  }

  /**
   * Get a list with only a single data element.
   *
   * @param aExtractor
   *        The data extractor to be used. May not be <code>null</code>.
   * @return Never <code>null</code>.
   * @param <T>
   *        Type to extract from {@link IError}.
   */
  @Nonnull
  @ReturnsMutableCopy
  default <T> ICommonsList <T> getAllDataItems (@Nonnull final Function <? super IError, ? extends T> aExtractor)
  {
    return new CommonsArrayList <> (this, aExtractor);
  }

  /**
   * @return A map with all items mapped from error ID to its occurrences.
   */
  @Nonnull
  @ReturnsMutableCopy
  default ICommonsMap <String, ICommonsList <IError>> getGroupedByID ()
  {
    return getGrouped (IError::getErrorID);
  }

  /**
   * @return A map with all items mapped from error field name to its
   *         occurrences.
   */
  @Nonnull
  @ReturnsMutableCopy
  default ICommonsMap <String, ICommonsList <IError>> getGroupedByFieldName ()
  {
    return getGrouped (IError::getErrorFieldName);
  }

  /**
   * @param aKeyExtractor
   *        the key extractor by which the result is grouped.
   * @return A map with all items mapped from a key to its occurrences.
   * @param <T>
   *        Return list key type
   */
  @Nonnull
  @ReturnsMutableCopy
  default <T> ICommonsOrderedMap <T, ICommonsList <IError>> getGrouped (@Nonnull final Function <? super IError, T> aKeyExtractor)
  {
    final ICommonsOrderedMap <T, ICommonsList <IError>> ret = new CommonsLinkedHashMap <> ();
    // create a list for each key, and add the respective entry
    forEach (x -> ret.computeIfAbsent (aKeyExtractor.apply (x), k -> new CommonsArrayList <> ()).add (x));
    return ret;
  }
}
