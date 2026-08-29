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

import java.util.Collection;
import java.util.Comparator;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;

/**
 * Helper class to apply an {@link IPagingSpec} onto an in-memory collection. This is the fallback
 * for all data stores that cannot apply the paging natively, and it is the reference behaviour all
 * native implementations must comply to.
 *
 * @author Philip Helger
 * @since 12.4.0
 */
@Immutable
public final class PagingHelper
{
  private PagingHelper ()
  {}

  /**
   * Extract a single page out of the provided collection. The source collection is never modified.
   *
   * @param <T>
   *        The collection element type
   * @param aCollection
   *        The collection to take the page from. May not be <code>null</code>.
   * @param aPagingSpec
   *        The paging specification to be applied. May not be <code>null</code>.
   * @param aComparator
   *        The comparator matching the sort fields of the paging specification. May be
   *        <code>null</code> in which case the order of the source collection is used as is. It is
   *        up to the caller to resolve the sort fields onto a comparator, because only the caller
   *        knows the data model.
   * @return A non-<code>null</code> but maybe empty list.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <T> ICommonsList <T> getPage (@NonNull final Collection <? extends T> aCollection,
                                              @NonNull final IPagingSpec aPagingSpec,
                                              @Nullable final Comparator <? super T> aComparator)
  {
    ValueEnforcer.notNull (aCollection, "Collection");
    ValueEnforcer.notNull (aPagingSpec, "PagingSpec");

    final long nStartIndex = aPagingSpec.getStartIndex ();
    if (aPagingSpec.isEmptyPage () || nStartIndex >= aCollection.size ())
      return new CommonsArrayList <> ();

    // Always work on a copy, so that the source collection is not modified
    final ICommonsList <T> aSorted = new CommonsArrayList <> (aCollection);
    if (aComparator != null)
      aSorted.sort (aComparator);

    // The start index is smaller than the size and therefore always fits into an int
    final int nRealStartIndex = (int) nStartIndex;
    final int nEndIndex;
    if (aPagingSpec.isUnlimited ())
      nEndIndex = aSorted.size ();
    else
    {
      // Limit the count to the size first, so that the addition can never overflow
      final long nMaxCount = Math.min (aPagingSpec.getMaxCount (), aSorted.size ());
      nEndIndex = (int) Math.min (nStartIndex + nMaxCount, aSorted.size ());
    }

    return new CommonsArrayList <> (aSorted.subList (nRealStartIndex, nEndIndex));
  }

  /**
   * Extract a single page out of the provided collection, keeping the existing order.
   *
   * @param <T>
   *        The collection element type
   * @param aCollection
   *        The collection to take the page from. May not be <code>null</code>.
   * @param aPagingSpec
   *        The paging specification to be applied. May not be <code>null</code>.
   * @return A non-<code>null</code> but maybe empty list.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <T> ICommonsList <T> getPage (@NonNull final Collection <? extends T> aCollection,
                                              @NonNull final IPagingSpec aPagingSpec)
  {
    return getPage (aCollection, aPagingSpec, (Comparator <? super T>) null);
  }
}
