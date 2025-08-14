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
package com.helger.collection.helper;

import static com.helger.collection.CollectionHelper.isEmpty;
import static com.helger.collection.CollectionHelper.isNotEmpty;
import static com.helger.collection.helper.CollectionHelperExt.newList;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.CodingStyleguideUnaware;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.equals.ValueEnforcer;
import com.helger.collection.commons.CollectionCommonsHelper;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsIterableIterator;
import com.helger.collection.commons.ICommonsOrderedMap;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

@Immutable
public class CollectionSort
{
  private CollectionSort ()
  {}

  @Nullable
  @ReturnsMutableObject ("design")
  @CodingStyleguideUnaware
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>, LISTTYPE extends List <ELEMENTTYPE>> LISTTYPE getSortedInline (@Nullable final LISTTYPE aList)
  {
    if (isNotEmpty (aList))
      aList.sort (null);
    return aList;
  }

  @Nullable
  @ReturnsMutableObject ("design")
  @CodingStyleguideUnaware
  public static <ELEMENTTYPE, LISTTYPE extends List <ELEMENTTYPE>> LISTTYPE getSortedInline (@Nullable final LISTTYPE aList,
                                                                                             @Nonnull final Comparator <? super ELEMENTTYPE> aComparator)
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
  @Nonnull
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
  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> ICommonsOrderedMap <KEYTYPE, VALUETYPE> getSortedByKey (@Nullable final Map <KEYTYPE, VALUETYPE> aMap,
                                                                                             @Nonnull final Comparator <? super KEYTYPE> aKeyComparator)
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
  @Nonnull
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
  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> ICommonsOrderedMap <KEYTYPE, VALUETYPE> getSortedByValue (@Nullable final Map <KEYTYPE, VALUETYPE> aMap,
                                                                                               @Nonnull final Comparator <? super VALUETYPE> aValueComparator)
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
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> CommonsArrayList <ELEMENTTYPE> getSorted (@Nullable final ICommonsIterableIterator <? extends ELEMENTTYPE> aIter)
  {
    return getSortedInline (newList (aIter));
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
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> CommonsArrayList <ELEMENTTYPE> getSorted (@Nullable final ICommonsIterableIterator <? extends ELEMENTTYPE> aIter,
                                                                                                                 @Nonnull final Comparator <? super ELEMENTTYPE> aComparator)
  {
    return getSortedInline (newList (aIter), aComparator);
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
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> CommonsArrayList <ELEMENTTYPE> getSorted (@Nullable final Iterator <? extends ELEMENTTYPE> aIter)
  {
    return getSortedInline (newList (aIter));
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
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> getSorted (@Nullable final Iterator <? extends ELEMENTTYPE> aIter,
                                                                        @Nonnull final Comparator <? super ELEMENTTYPE> aComparator)
  {
    return getSortedInline (newList (aIter), aComparator);
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
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> CommonsArrayList <ELEMENTTYPE> getSorted (@Nullable final Iterable <? extends ELEMENTTYPE> aCont)
  {
    return getSortedInline (newList (aCont));
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
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> getSorted (@Nullable final Iterable <? extends ELEMENTTYPE> aCont,
                                                                        @Nonnull final Comparator <? super ELEMENTTYPE> aComparator)
  {
    return getSortedInline (newList (aCont), aComparator);
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
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> CommonsArrayList <ELEMENTTYPE> getSorted (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    return getSortedInline (newList (aCont));
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
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> getSorted (@Nullable final Collection <? extends ELEMENTTYPE> aCont,
                                                                        @Nonnull final Comparator <? super ELEMENTTYPE> aComparator)
  {
    return getSortedInline (newList (aCont), aComparator);
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
  @Nonnull
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> CommonsArrayList <ELEMENTTYPE> getSorted (@Nullable final ELEMENTTYPE... aCont)
  {
    return getSortedInline (newList (aCont));
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
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> getSorted (@Nullable final ELEMENTTYPE [] aCont,
                                                                        @Nonnull final Comparator <? super ELEMENTTYPE> aComparator)
  {
    return getSortedInline (newList (aCont), aComparator);
  }

  @Nullable
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> getReverseList (@Nullable final Collection <? extends ELEMENTTYPE> aCollection)
  {
    if (isEmpty (aCollection))
      return new CommonsArrayList <> (0);

    final CommonsArrayList <ELEMENTTYPE> ret = new CommonsArrayList <> (aCollection);
    ret.reverse ();
    return ret;
  }

  @Nullable
  @ReturnsMutableObject ("semantics of this method")
  @CodingStyleguideUnaware
  public static <ELEMENTTYPE, LISTTYPE extends List <ELEMENTTYPE>> LISTTYPE getReverseInlineList (@Nullable final LISTTYPE aList)
  {
    if (aList == null)
      return null;

    Collections.reverse (aList);
    return aList;
  }
}
