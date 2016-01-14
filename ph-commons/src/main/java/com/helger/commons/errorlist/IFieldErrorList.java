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
package com.helger.commons.errorlist;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.RegEx;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.multimap.IMultiMapListBased;
import com.helger.commons.error.IErrorLevel;

/**
 * A subset of {@link IErrorList} containing only the methods relevant for
 * fields
 *
 * @author Philip Helger
 */
public interface IFieldErrorList
{
  /**
   * Check if no entry for the specified field is present
   *
   * @param sSearchFieldName
   *        The field name to search.
   * @return <code>true</code> if no entry for the specified field is present
   */
  boolean hasNoEntryForField (@Nullable String sSearchFieldName);

  /**
   * Check if no entry for the specified fields are present
   *
   * @param aSearchFieldNames
   *        The field names to search.
   * @return <code>true</code> if no entry for any of the specified fields is
   *         present
   */
  boolean hasNoEntryForFields (@Nullable String... aSearchFieldNames);

  /**
   * Check if any entry for the specified field is present
   *
   * @param sSearchFieldName
   *        The field name to search.
   * @return <code>true</code> if an entry for the specified field is present
   */
  boolean hasEntryForField (@Nullable String sSearchFieldName);

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
  boolean hasEntryForField (@Nullable String sSearchFieldName, @Nullable IErrorLevel aErrorLevel);

  /**
   * Check if any entry for the specified fields are present
   *
   * @param aSearchFieldNames
   *        The field names to search.
   * @return <code>true</code> if an entry for at least one of the specified
   *         fields is present
   */
  boolean hasEntryForFields (@Nullable String... aSearchFieldNames);

  /**
   * Get a sub-list with all entries for the specified field name
   *
   * @param sSearchFieldName
   *        The field name to search.
   * @return Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  IFieldErrorList getListOfField (@Nullable String sSearchFieldName);

  /**
   * Get a sub-list with all entries for the specified field names
   *
   * @param aSearchFieldNames
   *        The field names to search.
   * @return Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  IFieldErrorList getListOfFields (@Nullable String... aSearchFieldNames);

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
  IFieldErrorList getListOfFieldsStartingWith (@Nullable String... aSearchFieldNames);

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
  IFieldErrorList getListOfFieldsRegExp (@Nonnull @Nonempty @RegEx String sRegExp);

  /**
   * Get a list with all texts for the specified field name.
   *
   * @param sSearchFieldName
   *        The field name to search.
   * @return Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  List <String> getAllItemTextsOfField (@Nullable String sSearchFieldName);

  /**
   * Get a list with all texts for the specified field names
   *
   * @param aSearchFieldNames
   *        The field names to search.
   * @return Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  List <String> getAllItemTextsOfFields (@Nullable String... aSearchFieldNames);

  /**
   * Get a list with all texts of entries that have field names starting with
   * one of the supplied names.
   *
   * @param aSearchFieldNames
   *        The field names to search.
   * @return Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  List <String> getAllItemTextsOfFieldsStartingWith (@Nullable String... aSearchFieldNames);

  /**
   * Get a list with all texts of entries that have field names matching the
   * passed regular expression.
   *
   * @param sRegExp
   *        The regular expression to compare the entries against.
   * @return Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  List <String> getAllItemTextsOfFieldsRegExp (@Nonnull @Nonempty @RegEx String sRegExp);

  /**
   * @return A map with all items mapped from error ID to its occurrences.
   */
  @Nonnull
  @ReturnsMutableCopy
  IMultiMapListBased <String, IError> getStructuredByID ();

  /**
   * @return A map with all items mapped from error field name to its
   *         occurrences.
   */
  @Nonnull
  @ReturnsMutableCopy
  IMultiMapListBased <String, IError> getStructuredByFieldName ();
}
