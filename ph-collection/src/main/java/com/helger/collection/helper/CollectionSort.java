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
package com.helger.collection.helper;

import static com.helger.collection.CollectionHelper.isNotEmpty;
import static com.helger.collection.helper.CollectionHelperExt.createList;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.IntBinaryOperator;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.CodingStyleguideUnaware;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.collection.commons.CollectionCommonsHelper;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsIterableIterator;
import com.helger.collection.commons.ICommonsOrderedMap;

/**
 * Helper class for sorting lists, iterators, and maps, both inline and by creating sorted copies.
 *
 * @author Philip Helger
 */
@Immutable
public class CollectionSort
{
  /** Ranges up to this length are sorted with insertion sort */
  private static final int INSERTION_SORT_THRESHOLD = 32;

  private CollectionSort ()
  {}

  private static void _insertionSortStable (final int @NonNull [] aArray,
                                            @Nonnegative final int nFromIndex,
                                            @Nonnegative final int nToIndex,
                                            @NonNull final IntBinaryOperator aCmp)
  {
    for (int i = nFromIndex + 1; i < nToIndex; ++i)
    {
      final int nValue = aArray[i];
      int j = i - 1;
      // Strictly greater keeps equal elements in their original order
      while (j >= nFromIndex && aCmp.applyAsInt (aArray[j], nValue) > 0)
      {
        aArray[j + 1] = aArray[j];
        j--;
      }
      aArray[j + 1] = nValue;
    }
  }

  private static void _mergeStable (final int @NonNull [] aSrc,
                                    final int @NonNull [] aDst,
                                    @Nonnegative final int nFromIndex,
                                    @Nonnegative final int nMidIndex,
                                    @Nonnegative final int nToIndex,
                                    @NonNull final IntBinaryOperator aCmp)
  {
    if (nMidIndex >= nToIndex || aCmp.applyAsInt (aSrc[nMidIndex - 1], aSrc[nMidIndex]) <= 0)
    {
      // Already in order (or no right part) - just copy
      System.arraycopy (aSrc, nFromIndex, aDst, nFromIndex, nToIndex - nFromIndex);
      return;
    }

    int nLeft = nFromIndex;
    int nRight = nMidIndex;
    int nDst = nFromIndex;
    while (nLeft < nMidIndex && nRight < nToIndex)
    {
      // Take from the right part only if it is strictly smaller to keep the sort stable
      if (aCmp.applyAsInt (aSrc[nRight], aSrc[nLeft]) < 0)
        aDst[nDst++] = aSrc[nRight++];
      else
        aDst[nDst++] = aSrc[nLeft++];
    }
    System.arraycopy (aSrc, nLeft, aDst, nDst, nMidIndex - nLeft);
    nDst += nMidIndex - nLeft;
    System.arraycopy (aSrc, nRight, aDst, nDst, nToIndex - nRight);
  }

  private static void _sortStable (final int @NonNull [] aArray,
                                   @Nonnegative final int nLength,
                                   @NonNull final IntBinaryOperator aCmp)
  {
    // Sort small runs with insertion sort
    for (int nStart = 0; nStart < nLength; nStart += INSERTION_SORT_THRESHOLD)
      _insertionSortStable (aArray, nStart, Math.min (nStart + INSERTION_SORT_THRESHOLD, nLength), aCmp);
    if (nLength <= INSERTION_SORT_THRESHOLD)
      return;

    // Bottom-up merge of the runs, alternating between the two arrays
    int [] aSrc = aArray;
    int [] aDst = new int [nLength];
    // Use long to avoid overflows for very large arrays
    for (long nWidth = INSERTION_SORT_THRESHOLD; nWidth < nLength; nWidth *= 2)
    {
      for (long nLeft = 0; nLeft < nLength; nLeft += 2 * nWidth)
      {
        final int nMid = (int) Math.min (nLeft + nWidth, nLength);
        final int nRight = (int) Math.min (nLeft + 2 * nWidth, nLength);
        _mergeStable (aSrc, aDst, (int) nLeft, nMid, nRight, aCmp);
      }
      final int [] aTmp = aSrc;
      aSrc = aDst;
      aDst = aTmp;
    }
    if (aSrc != aArray)
      System.arraycopy (aSrc, 0, aArray, 0, nLength);
  }

  /**
   * Sort a part of the passed int array inline with the provided comparator. The sort is stable,
   * so elements that compare equal keep their relative order. A merge sort is used, with insertion
   * sort for small runs. In contrast to {@link java.util.Arrays#sort(int[])} this allows a custom
   * order without boxing the values.
   *
   * @param aArray
   *        The array to be sorted. May not be <code>null</code>.
   * @param nOfs
   *        The offset of the first element to sort. Must be &ge; 0.
   * @param nLen
   *        The number of elements to sort. Must be &ge; 0.
   * @param aCmp
   *        The comparator to use. It must return a negative value, 0 or a positive value, like
   *        {@link Comparator#compare(Object, Object)}. May not be <code>null</code>.
   * @since 12.5.4
   */
  public static void sortInlineStable (final int @NonNull [] aArray,
                                       @Nonnegative final int nOfs,
                                       @Nonnegative final int nLen,
                                       @NonNull final IntBinaryOperator aCmp)
  {
    ValueEnforcer.isArrayOfsLen (aArray, nOfs, nLen);
    ValueEnforcer.notNull (aCmp, "Comparator");

    if (nLen < 2)
      return;

    if (nOfs == 0)
      _sortStable (aArray, nLen, aCmp);
    else
    {
      // Sort a copy of the range and copy it back
      final int [] aRange = new int [nLen];
      System.arraycopy (aArray, nOfs, aRange, 0, nLen);
      _sortStable (aRange, nLen, aCmp);
      System.arraycopy (aRange, 0, aArray, nOfs, nLen);
    }
  }

  /**
   * Get the indices <code>0</code> to <code>nCount - 1</code> sorted by the provided comparator.
   * The comparator is invoked with two indices and must compare the elements at these indices.
   * The sort is stable, so indices of elements that compare equal stay in ascending order. This
   * allows to sort parallel arrays or lists without boxing.
   *
   * @param nCount
   *        The number of indices to sort. Must be &ge; 0.
   * @param aCmp
   *        The comparator that compares the elements at the two passed indices. May not be
   *        <code>null</code>.
   * @return A new array of length <code>nCount</code> with the sorted indices. Never
   *         <code>null</code>.
   * @since 12.5.4
   */
  @ReturnsMutableCopy
  public static int @NonNull [] getSortedIndices (@Nonnegative final int nCount, @NonNull final IntBinaryOperator aCmp)
  {
    ValueEnforcer.isGE0 (nCount, "Count");
    ValueEnforcer.notNull (aCmp, "Comparator");

    final int [] ret = new int [nCount];
    for (int i = 0; i < nCount; ++i)
      ret[i] = i;
    if (nCount > 1)
      _sortStable (ret, nCount, aCmp);
    return ret;
  }

  /**
   * Sort the provided list inline using the natural ordering of its elements.
   *
   * @param aList
   *        The list to be sorted inline. May be <code>null</code>.
   * @return The same list instance, sorted. May be <code>null</code> if the input list was
   *         <code>null</code>.
   * @param <ELEMENTTYPE>
   *        The type of elements in the list. Must implement {@link Comparable}.
   * @param <LISTTYPE>
   *        The type of the list.
   */
  @Nullable
  @ReturnsMutableObject ("design")
  @CodingStyleguideUnaware
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>, LISTTYPE extends List <ELEMENTTYPE>> LISTTYPE getSortedInline (@Nullable final LISTTYPE aList)
  {
    if (isNotEmpty (aList))
      aList.sort (null);
    return aList;
  }

  /**
   * Sort the provided list inline using the specified comparator.
   *
   * @param aList
   *        The list to be sorted inline. May be <code>null</code>.
   * @param aComparator
   *        The comparator to use for sorting. May not be <code>null</code>.
   * @return The same list instance, sorted. May be <code>null</code> if the input list was
   *         <code>null</code>.
   * @param <ELEMENTTYPE>
   *        The type of elements in the list.
   * @param <LISTTYPE>
   *        The type of the list.
   */
  @Nullable
  @ReturnsMutableObject ("design")
  @CodingStyleguideUnaware
  public static <ELEMENTTYPE, LISTTYPE extends List <ELEMENTTYPE>> LISTTYPE getSortedInline (@Nullable final LISTTYPE aList,
                                                                                             @NonNull final Comparator <? super ELEMENTTYPE> aComparator)
  {
    ValueEnforcer.notNull (aComparator, "Comparator");

    if (isNotEmpty (aList))
      aList.sort (aComparator);
    return aList;
  }

  /**
   * Get a map sorted by aIter's keys. Because no comparator is defined, the key type needs to
   * implement the {@link java.lang.Comparable} interface.
   *
   * @param <KEYTYPE>
   *        map key type
   * @param <VALUETYPE>
   *        map value type
   * @param aMap
   *        the map to sort
   * @return the sorted map and never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> ICommonsOrderedMap <KEYTYPE, VALUETYPE> getSortedByKey (@Nullable final Map <KEYTYPE, VALUETYPE> aMap)
  {
    return CollectionCommonsHelper.getSortedByKey (aMap);
  }

  /**
   * Get a map sorted by its keys. The comparison order is defined by the passed comparator object.
   *
   * @param <KEYTYPE>
   *        map key type
   * @param <VALUETYPE>
   *        map value type
   * @param aMap
   *        The map to sort. May not be <code>null</code>.
   * @param aKeyComparator
   *        The comparator to be used. May not be <code>null</code>.
   * @return the sorted map and never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> ICommonsOrderedMap <KEYTYPE, VALUETYPE> getSortedByKey (@Nullable final Map <KEYTYPE, VALUETYPE> aMap,
                                                                                             @NonNull final Comparator <? super KEYTYPE> aKeyComparator)
  {
    return CollectionCommonsHelper.getSortedByKey (aMap, aKeyComparator);
  }

  /**
   * Get a map sorted by its values. Because no comparator is defined, the value type needs to
   * implement the {@link java.lang.Comparable} interface.
   *
   * @param <KEYTYPE>
   *        map key type
   * @param <VALUETYPE>
   *        map value type
   * @param aMap
   *        The map to sort. May not be <code>null</code>.
   * @return the sorted map and never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE extends Comparable <? super VALUETYPE>> ICommonsOrderedMap <KEYTYPE, VALUETYPE> getSortedByValue (@Nullable final Map <KEYTYPE, VALUETYPE> aMap)
  {
    return CollectionCommonsHelper.getSortedByValue (aMap);
  }

  /**
   * Get a map sorted by aIter's values. The comparison order is defined by the passed comparator
   * object.
   *
   * @param <KEYTYPE>
   *        map key type
   * @param <VALUETYPE>
   *        map value type
   * @param aMap
   *        The map to sort. May not be <code>null</code>.
   * @param aValueComparator
   *        The comparator to be used. May not be <code>null</code>.
   * @return the sorted map and never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> ICommonsOrderedMap <KEYTYPE, VALUETYPE> getSortedByValue (@Nullable final Map <KEYTYPE, VALUETYPE> aMap,
                                                                                               @NonNull final Comparator <? super VALUETYPE> aValueComparator)
  {
    return CollectionCommonsHelper.getSortedByValue (aMap, aValueComparator);
  }

  /**
   * Convert the given iterator to a sorted list.
   *
   * @param <ELEMENTTYPE>
   *        The type of elements to iterate. May not be <code>null</code>.
   * @param aIter
   *        Input iterator. May be <code>null</code>.
   * @return a non-null {@link CommonsArrayList} based on the results of
   *         {@link Collections#sort(List)}.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> CommonsArrayList <ELEMENTTYPE> getSorted (@Nullable final ICommonsIterableIterator <? extends ELEMENTTYPE> aIter)
  {
    return getSortedInline (createList (aIter));
  }

  /**
   * Convert the given iterator to a sorted list.
   *
   * @param <ELEMENTTYPE>
   *        The type of elements to iterate. May not be <code>null</code>.
   * @param aIter
   *        Input iterator. May be <code>null</code>.
   * @param aComparator
   *        The comparator to use. May not be <code>null</code>.
   * @return a non-null {@link CommonsArrayList} based on the results of
   *         {@link Collections#sort(List)}.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> CommonsArrayList <ELEMENTTYPE> getSorted (@Nullable final ICommonsIterableIterator <? extends ELEMENTTYPE> aIter,
                                                                                                                 @NonNull final Comparator <? super ELEMENTTYPE> aComparator)
  {
    return getSortedInline (createList (aIter), aComparator);
  }

  /**
   * Convert the given iterator to a sorted list.
   *
   * @param <ELEMENTTYPE>
   *        The type of elements to iterate. May not be <code>null</code>.
   * @param aIter
   *        Input iterator. May not be <code>null</code>.
   * @return a non-null {@link CommonsArrayList} based on the results of
   *         {@link Collections#sort(List)}.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> CommonsArrayList <ELEMENTTYPE> getSorted (@Nullable final Iterator <? extends ELEMENTTYPE> aIter)
  {
    return getSortedInline (createList (aIter));
  }

  /**
   * Convert the given iterator to a sorted list.
   *
   * @param <ELEMENTTYPE>
   *        The type of elements to iterate.
   * @param aIter
   *        Input iterator. May be <code>null</code>.
   * @param aComparator
   *        The comparator to use. May not be <code>null</code>.
   * @return a non-null {@link CommonsArrayList} based on the results of
   *         {@link Collections#sort(List, Comparator)}.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> getSorted (@Nullable final Iterator <? extends ELEMENTTYPE> aIter,
                                                                        @NonNull final Comparator <? super ELEMENTTYPE> aComparator)
  {
    return getSortedInline (createList (aIter), aComparator);
  }

  /**
   * Convert the given iterable object to a sorted list.
   *
   * @param <ELEMENTTYPE>
   *        The type of element to iterate.
   * @param aCont
   *        Iterable input object. May be <code>null</code>.
   * @return A {@link CommonsArrayList} based on the results of {@link Collections#sort(List)}.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> CommonsArrayList <ELEMENTTYPE> getSorted (@Nullable final Iterable <? extends ELEMENTTYPE> aCont)
  {
    return getSortedInline (createList (aCont));
  }

  /**
   * Convert the given iterable object to a sorted list.
   *
   * @param <ELEMENTTYPE>
   *        The type of element to iterate.
   * @param aCont
   *        Iterable input object. May be <code>null</code>.
   * @param aComparator
   *        The comparator to use. May not be <code>null</code>.
   * @return A {@link CommonsArrayList} based on the results of
   *         {@link Collections#sort(List, Comparator)}.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> getSorted (@Nullable final Iterable <? extends ELEMENTTYPE> aCont,
                                                                        @NonNull final Comparator <? super ELEMENTTYPE> aComparator)
  {
    return getSortedInline (createList (aCont), aComparator);
  }

  /**
   * Convert the given collection object to a sorted list.
   *
   * @param <ELEMENTTYPE>
   *        The type of element to iterate.
   * @param aCont
   *        Collection input object. May be <code>null</code>.
   * @return A {@link CommonsArrayList} based on the results of {@link Collections#sort(List)}.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> CommonsArrayList <ELEMENTTYPE> getSorted (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    return getSortedInline (createList (aCont));
  }

  /**
   * Convert the given collection object to a sorted list.
   *
   * @param <ELEMENTTYPE>
   *        The type of element to iterate.
   * @param aCont
   *        Collection input object. May be <code>null</code>.
   * @param aComparator
   *        The comparator to use. May not be <code>null</code>.
   * @return A {@link CommonsArrayList} based on the results of
   *         {@link Collections#sort(List, Comparator)}.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> getSorted (@Nullable final Collection <? extends ELEMENTTYPE> aCont,
                                                                        @NonNull final Comparator <? super ELEMENTTYPE> aComparator)
  {
    return getSortedInline (createList (aCont), aComparator);
  }

  /**
   * Convert the given iterable object to a sorted list.
   *
   * @param <ELEMENTTYPE>
   *        The type of element to iterate.
   * @param aCont
   *        Array input object. May be <code>null</code>.
   * @return A {@link CommonsArrayList} based on the results of {@link Collections#sort(List)}.
   */
  @NonNull
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> CommonsArrayList <ELEMENTTYPE> getSorted (@Nullable final ELEMENTTYPE... aCont)
  {
    return getSortedInline (createList (aCont));
  }

  /**
   * Convert the given iterable object to a sorted list.
   *
   * @param <ELEMENTTYPE>
   *        The type of element to iterate.
   * @param aCont
   *        Iterable input object. May be <code>null</code>.
   * @param aComparator
   *        The comparator to use. May not be <code>null</code>.
   * @return A {@link CommonsArrayList} based on the results of
   *         {@link Collections#sort(List, Comparator)}.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> getSorted (@Nullable final ELEMENTTYPE [] aCont,
                                                                        @NonNull final Comparator <? super ELEMENTTYPE> aComparator)
  {
    return getSortedInline (createList (aCont), aComparator);
  }
}
