/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.collection.paging;

import java.util.function.Consumer;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.collection.commons.ICommonsList;

/**
 * Read-only interface for a "paging specification": the description of a single page of a larger
 * result set, together with the fields it is to be sorted by. It is meant to be used as a single
 * parameter object for querying data stores, so that the query API does not need to be extended
 * whenever another paging or sorting aspect is needed.<br>
 * The sort fields are relevant for paging, because only a stable and reproducible order guarantees
 * that consecutive page requests return disjunct results.
 *
 * @author Philip Helger
 * @since 12.4.0
 * @see PagingSpec
 */
public interface IPagingSpec
{
  /**
   * @return The 0-based index of the first element to be returned. Always &ge; 0.
   */
  @Nonnegative
  long getStartIndex ();

  /**
   * @return The maximum number of elements to be returned. A value &lt; 0 means "no limit", a value
   *         of 0 means "no elements".
   * @see #isUnlimited()
   */
  long getMaxCount ();

  /**
   * @return <code>true</code> if all elements starting from the start index are to be returned,
   *         <code>false</code> if the number of elements is limited.
   */
  default boolean isUnlimited ()
  {
    return getMaxCount () < 0;
  }

  /**
   * @return <code>true</code> if this specification can never return any element, because the
   *         maximum count is 0.
   */
  default boolean isEmptyPage ()
  {
    return getMaxCount () == 0;
  }

  /**
   * @return A copy of the list of all fields to sort by, in the order of precedence. Never
   *         <code>null</code> but maybe empty.
   */
  @NonNull
  @ReturnsMutableCopy
  ICommonsList <SortField> getAllSortFields ();

  /**
   * @return The number of sort fields present. Always &ge; 0.
   */
  @Nonnegative
  int getSortFieldCount ();

  /**
   * @return <code>true</code> if at least one sort field is present, <code>false</code> otherwise.
   */
  default boolean hasSortFields ()
  {
    return getSortFieldCount () > 0;
  }

  /**
   * Invoke the provided consumer for all contained sort fields, in the order of precedence.
   *
   * @param aConsumer
   *        The consumer to be invoked. May not be <code>null</code>.
   */
  void forEachSortField (@NonNull Consumer <? super SortField> aConsumer);
}
