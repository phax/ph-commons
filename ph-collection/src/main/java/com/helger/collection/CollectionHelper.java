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
package com.helger.collection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.CodingStyleguideUnaware;
import com.helger.annotation.style.ReturnsImmutableObject;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.clone.CloneHelper;
import com.helger.base.clone.ICloneable;
import com.helger.base.lang.clazz.ClassHelper;
import com.helger.base.state.EChange;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

@Immutable
public class CollectionHelper
{
  protected CollectionHelper ()
  {}

  @Nullable
  public static ECollectionBaseType getCollectionBaseTypeOfClass (@Nullable final Class <?> aClass)
  {
    if (aClass != null)
    {
      // Query Set before Collection, because Set is derived from Collection!
      if (Set.class.isAssignableFrom (aClass))
        return ECollectionBaseType.SET;
      if (Collection.class.isAssignableFrom (aClass))
        return ECollectionBaseType.COLLECTION;
      if (Map.class.isAssignableFrom (aClass))
        return ECollectionBaseType.MAP;
      if (ClassHelper.isArrayClass (aClass))
        return ECollectionBaseType.ARRAY;
      if (Iterator.class.isAssignableFrom (aClass))
        return ECollectionBaseType.ITERATOR;
      if (Iterable.class.isAssignableFrom (aClass))
        return ECollectionBaseType.ITERABLE;
      if (Enumeration.class.isAssignableFrom (aClass))
        return ECollectionBaseType.ENUMERATION;
    }
    return null;
  }

  @Nullable
  public static ECollectionBaseType getCollectionBaseTypeOfObject (@Nullable final Object aObj)
  {
    return aObj == null ? null : getCollectionBaseTypeOfClass (aObj.getClass ());
  }

  public static boolean isCollectionClass (@Nullable final Class <?> aClass)
  {
    return getCollectionBaseTypeOfClass (aClass) != null;
  }

  public static boolean isCollectionObject (@Nullable final Object aObj)
  {
    return getCollectionBaseTypeOfObject (aObj) != null;
  }

  public static boolean isEmpty (@Nullable final Iterable <?> aCont)
  {
    return aCont == null || !aCont.iterator ().hasNext ();
  }

  public static boolean isEmpty (@Nullable final Collection <?> aCont)
  {
    return aCont == null || aCont.isEmpty ();
  }

  public static boolean isEmpty (@Nullable final Map <?, ?> aCont)
  {
    return aCont == null || aCont.isEmpty ();
  }

  public static boolean isNotEmpty (@Nullable final Iterable <?> aCont)
  {
    return aCont != null && aCont.iterator ().hasNext ();
  }

  public static boolean isNotEmpty (@Nullable final Collection <?> aCont)
  {
    return aCont != null && !aCont.isEmpty ();
  }

  public static boolean isNotEmpty (@Nullable final Map <?, ?> aCont)
  {
    return aCont != null && !aCont.isEmpty ();
  }

  /**
   * Retrieve the size of the passed {@link Collection}. This method handles <code>null</code>
   * containers.
   *
   * @param aCollection
   *        Object to check. May be <code>null</code>.
   * @return The size of the object or 0 if the passed parameter is <code>null</code>.
   */
  @Nonnegative
  public static int getSize (@Nullable final Collection <?> aCollection)
  {
    return aCollection == null ? 0 : aCollection.size ();
  }

  /**
   * Retrieve the size of the passed {@link Map}. This method handles <code>null</code> containers.
   *
   * @param aMap
   *        Object to check. May be <code>null</code>.
   * @return The size of the object or 0 if the passed parameter is <code>null</code>.
   */
  @Nonnegative
  public static int getSize (@Nullable final Map <?, ?> aMap)
  {
    return aMap == null ? 0 : aMap.size ();
  }

  /**
   * Retrieve the size of the passed {@link Iterator}.
   *
   * @param aIterator
   *        Iterator to check. May be <code>null</code>.
   * @return The number objects or 0 if the passed parameter is <code>null</code>.
   */
  @Nonnegative
  public static int getSizeIterator (@Nullable final Iterator <?> aIterator)
  {
    int ret = 0;
    if (aIterator != null)
      while (aIterator.hasNext ())
      {
        aIterator.next ();
        ++ret;
      }
    return ret;
  }

  /**
   * Retrieve the size of the passed {@link Iterable}.
   *
   * @param aIterable
   *        Iterator to check. May be <code>null</code>.
   * @return The number objects or 0 if the passed parameter is <code>null</code>.
   */
  @Nonnegative
  public static int getSize (@Nullable final Iterable <?> aIterable)
  {
    return aIterable == null ? 0 : getSizeIterator (aIterable.iterator ());
  }

  /**
   * Retrieve the size of the passed {@link Enumeration}.
   *
   * @param aEnumeration
   *        Enumeration to check. May be <code>null</code>.
   * @return The number objects or 0 if the passed parameter is <code>null</code>.
   */
  @Nonnegative
  public static int getSize (@Nullable final Enumeration <?> aEnumeration)
  {
    int ret = 0;
    if (aEnumeration != null)
      while (aEnumeration.hasMoreElements ())
      {
        aEnumeration.nextElement ();
        ++ret;
      }
    return ret;
  }

  /**
   * Count the number of elements in the passed iterable (collection) matching the provided filter.
   *
   * @param aCollection
   *        The collection to count. May not be <code>null</code>.
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @return The number of matching elements. Always &ge; 0. If no filter is provided this is the
   *         same as {@link #getSize(Iterable)}.
   * @param <ELEMENTTYPE>
   *        The element type to count
   */
  @Nonnegative
  public static <ELEMENTTYPE> int getCount (@Nullable final Iterable <? extends ELEMENTTYPE> aCollection,
                                            @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (aFilter == null)
      return getSize (aCollection);

    int ret = 0;
    if (isNotEmpty (aCollection))
      for (final ELEMENTTYPE aElement : aCollection)
        if (aFilter.test (aElement))
          ret++;
    return ret;
  }

  /**
   * Count the number of elements in the passed collection matching the provided filter.
   *
   * @param aCollection
   *        The collection to count. May not be <code>null</code>.
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @return The number of matching elements. Always &ge; 0. If no filter is provided this is the
   *         same as {@link #getSize(Collection)}.
   * @param <ELEMENTTYPE>
   *        The element type to count
   */
  @Nonnegative
  public static <ELEMENTTYPE> int getCount (@Nullable final Collection <? extends ELEMENTTYPE> aCollection,
                                            @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (aFilter == null)
      return getSize (aCollection);

    int ret = 0;
    if (isNotEmpty (aCollection))
      for (final ELEMENTTYPE aElement : aCollection)
        if (aFilter.test (aElement))
          ret++;
    return ret;
  }

  /**
   * Safe list element accessor method.
   *
   * @param aList
   *        The list to extract from. May be <code>null</code>.
   * @param nIndex
   *        The index to access. Should be &ge; 0.
   * @return <code>null</code> if the element cannot be accessed.
   * @param <ELEMENTTYPE>
   *        The type of elements on the list.
   */
  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE getAtIndex (@Nullable final List <? extends ELEMENTTYPE> aList,
                                                      final int nIndex)
  {
    return getAtIndex (aList, nIndex, (ELEMENTTYPE) null);
  }

  /**
   * Safe list element accessor method.
   *
   * @param aList
   *        The list to extract from. May be <code>null</code>.
   * @param nIndex
   *        The index to access. Should be &ge; 0.
   * @param aDefault
   *        The value to be returned, if the index is out of bounds.
   * @return The default parameter if the element cannot be accessed.
   * @param <ELEMENTTYPE>
   *        The type of elements on the list.
   */
  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE getAtIndex (@Nullable final List <? extends ELEMENTTYPE> aList,
                                                      final int nIndex,
                                                      @Nullable final ELEMENTTYPE aDefault)
  {
    return aList != null && nIndex >= 0 && nIndex < aList.size () ? aList.get (nIndex) : aDefault;
  }

  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE getAtIndex (@Nullable final Iterable <? extends ELEMENTTYPE> aCollection,
                                                      @Nonnegative final int nIndex)
  {
    return getAtIndex (aCollection, nIndex, (ELEMENTTYPE) null);
  }

  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE getAtIndex (@Nullable final Iterable <? extends ELEMENTTYPE> aCollection,
                                                      @Nonnegative final int nIndex,
                                                      @Nullable final ELEMENTTYPE aDefault)
  {
    if (nIndex >= 0)
    {
      int nCurIndex = 0;
      for (final ELEMENTTYPE aElement : aCollection)
      {
        if (nCurIndex == nIndex)
          return aElement;
        ++nCurIndex;
      }
    }
    return aDefault;
  }

  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE getAtIndex (@Nullable final Iterable <? extends ELEMENTTYPE> aCollection,
                                                      @Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                                      @Nonnegative final int nIndex)
  {
    return getAtIndex (aCollection, aFilter, nIndex, (ELEMENTTYPE) null);
  }

  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE getAtIndex (@Nullable final Iterable <? extends ELEMENTTYPE> aCollection,
                                                      @Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                                      @Nonnegative final int nIndex,
                                                      @Nullable final ELEMENTTYPE aDefault)
  {
    if (aFilter == null)
      return getAtIndex (aCollection, nIndex, aDefault);

    if (nIndex >= 0)
    {
      int nCurIndex = 0;
      for (final ELEMENTTYPE aElement : aCollection)
        if (aFilter.test (aElement))
        {
          if (nCurIndex == nIndex)
            return aElement;
          ++nCurIndex;
        }
    }
    return aDefault;
  }

  @Nullable
  public static <SRCTYPE, DSTTYPE> DSTTYPE getAtIndexMapped (@Nullable final Iterable <? extends SRCTYPE> aCollection,
                                                             @Nonnegative final int nIndex,
                                                             @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aMapper)
  {
    return getAtIndexMapped (aCollection, nIndex, aMapper, (DSTTYPE) null);
  }

  @Nullable
  public static <SRCTYPE, DSTTYPE> DSTTYPE getAtIndexMapped (@Nullable final Iterable <? extends SRCTYPE> aCollection,
                                                             @Nonnegative final int nIndex,
                                                             @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aMapper,
                                                             @Nullable final DSTTYPE aDefault)
  {
    if (nIndex >= 0)
    {
      int nCurIndex = 0;
      for (final SRCTYPE aElement : aCollection)
      {
        if (nCurIndex == nIndex)
          return aMapper.apply (aElement);
        ++nCurIndex;
      }
    }
    return aDefault;
  }

  @Nullable
  public static <SRCTYPE, DSTTYPE> DSTTYPE getAtIndexMapped (@Nullable final Iterable <? extends SRCTYPE> aCollection,
                                                             @Nullable final Predicate <? super SRCTYPE> aFilter,
                                                             @Nonnegative final int nIndex,
                                                             @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aMapper)
  {
    return getAtIndexMapped (aCollection, aFilter, nIndex, aMapper, (DSTTYPE) null);
  }

  @Nullable
  public static <SRCTYPE, DSTTYPE> DSTTYPE getAtIndexMapped (@Nullable final Iterable <? extends SRCTYPE> aCollection,
                                                             @Nullable final Predicate <? super SRCTYPE> aFilter,
                                                             @Nonnegative final int nIndex,
                                                             @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aMapper,
                                                             @Nullable final DSTTYPE aDefault)
  {
    if (aFilter == null)
      return getAtIndexMapped (aCollection, nIndex, aMapper, aDefault);

    if (nIndex >= 0)
    {
      int nCurIndex = 0;
      for (final SRCTYPE aElement : aCollection)
        if (aFilter.test (aElement))
        {
          if (nCurIndex == nIndex)
            return aMapper.apply (aElement);
          ++nCurIndex;
        }
    }
    return aDefault;
  }

  @SafeVarargs
  @Nullable
  @ReturnsImmutableObject
  @CodingStyleguideUnaware
  public static <ELEMENTTYPE> List <ELEMENTTYPE> makeUnmodifiable (@Nullable final ELEMENTTYPE... aArray)
  {
    return aArray == null ? null : Collections.unmodifiableList (Arrays.asList (aArray));
  }

  @Nullable
  @ReturnsImmutableObject
  @CodingStyleguideUnaware
  public static <ELEMENTTYPE> Collection <ELEMENTTYPE> makeUnmodifiable (@Nullable final Collection <ELEMENTTYPE> aCollection)
  {
    return aCollection == null ? null : Collections.unmodifiableCollection (aCollection);
  }

  @Nullable
  @ReturnsImmutableObject
  @CodingStyleguideUnaware
  public static <ELEMENTTYPE> List <ELEMENTTYPE> makeUnmodifiable (@Nullable final List <ELEMENTTYPE> aList)
  {
    return aList == null ? null : Collections.unmodifiableList (aList);
  }

  @Nullable
  @ReturnsImmutableObject
  @CodingStyleguideUnaware
  public static <ELEMENTTYPE> Set <ELEMENTTYPE> makeUnmodifiable (@Nullable final Set <ELEMENTTYPE> aSet)
  {
    return aSet == null ? null : Collections.unmodifiableSet (aSet);
  }

  @Nullable
  @ReturnsImmutableObject
  @CodingStyleguideUnaware
  public static <KEYTYPE, VALUETYPE> Map <KEYTYPE, VALUETYPE> makeUnmodifiable (@Nullable final Map <KEYTYPE, VALUETYPE> aMap)
  {
    return aMap == null ? null : Collections.unmodifiableMap (aMap);
  }
  //
  // @Nullable
  // @ReturnsImmutableObject
  // public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>>
  // SortedSet <ELEMENTTYPE> makeUnmodifiable (@Nullable final SortedSet
  // <ELEMENTTYPE> aSortedSet)
  // {
  // return aSortedSet == null ? null : Collections.unmodifiableSortedSet
  // (aSortedSet);
  // }
  //
  // @Nullable
  // @ReturnsImmutableObject
  // public static <KEYTYPE, VALUETYPE> SortedMap <KEYTYPE, VALUETYPE>
  // makeUnmodifiable (@Nullable final SortedMap <KEYTYPE, VALUETYPE>
  // aSortedMap)
  // {
  // return aSortedMap == null ? null : Collections.unmodifiableSortedMap
  // (aSortedMap);
  // }

  @Nullable
  @ReturnsImmutableObject
  @CodingStyleguideUnaware
  public static <ELEMENTTYPE> NavigableSet <ELEMENTTYPE> makeUnmodifiable (@Nullable final NavigableSet <ELEMENTTYPE> aNavigableSet)
  {
    return aNavigableSet == null ? null : Collections.unmodifiableNavigableSet (aNavigableSet);
  }

  @Nullable
  @ReturnsImmutableObject
  @CodingStyleguideUnaware
  public static <KEYTYPE, VALUETYPE> NavigableMap <KEYTYPE, VALUETYPE> makeUnmodifiable (@Nullable final NavigableMap <KEYTYPE, VALUETYPE> aNavigableMap)
  {
    return aNavigableMap == null ? null : Collections.unmodifiableNavigableMap (aNavigableMap);
  }

  @Nonnull
  @ReturnsImmutableObject
  @CodingStyleguideUnaware
  public static <ELEMENTTYPE> Collection <ELEMENTTYPE> makeUnmodifiableNotNull (@Nullable final Collection <ELEMENTTYPE> aCollection)
  {
    return aCollection == null ? Collections.emptyList () : Collections.unmodifiableCollection (aCollection);
  }

  @Nonnull
  @ReturnsImmutableObject
  @CodingStyleguideUnaware
  public static <ELEMENTTYPE> List <ELEMENTTYPE> makeUnmodifiableNotNull (@Nullable final List <ELEMENTTYPE> aList)
  {
    return aList == null ? Collections.emptyList () : Collections.unmodifiableList (aList);
  }

  @Nonnull
  @ReturnsImmutableObject
  @CodingStyleguideUnaware
  public static <ELEMENTTYPE> Set <ELEMENTTYPE> makeUnmodifiableNotNull (@Nullable final Set <ELEMENTTYPE> aSet)
  {
    return aSet == null ? Collections.emptySet () : Collections.unmodifiableSet (aSet);
  }

  @Nonnull
  @ReturnsImmutableObject
  @CodingStyleguideUnaware
  public static <KEYTYPE, VALUETYPE> Map <KEYTYPE, VALUETYPE> makeUnmodifiableNotNull (@Nullable final Map <KEYTYPE, VALUETYPE> aMap)
  {
    return aMap == null ? Collections.emptyMap () : Collections.unmodifiableMap (aMap);
  }

  // @Nonnull
  // @ReturnsImmutableObject
  // public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>>
  // SortedSet <ELEMENTTYPE> makeUnmodifiableNotNull (@Nullable final SortedSet
  // <ELEMENTTYPE> aSortedSet)
  // {
  // return aSortedSet == null ? Collections.emptySortedSet () :
  // Collections.unmodifiableSortedSet (aSortedSet);
  // }
  //
  // @Nonnull
  // @ReturnsImmutableObject
  // public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE>
  // SortedMap <KEYTYPE, VALUETYPE> makeUnmodifiableNotNull (@Nullable final
  // SortedMap <KEYTYPE, VALUETYPE> aSortedMap)
  // {
  // return aSortedMap == null ? Collections.emptySortedMap () :
  // Collections.unmodifiableSortedMap (aSortedMap);
  // }

  @Nonnull
  @ReturnsImmutableObject
  @CodingStyleguideUnaware
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> NavigableSet <ELEMENTTYPE> makeUnmodifiableNotNull (@Nullable final NavigableSet <ELEMENTTYPE> aNavigableSet)
  {
    return aNavigableSet == null ? Collections.emptyNavigableSet () : Collections.unmodifiableNavigableSet (
                                                                                                            aNavigableSet);
  }

  @Nonnull
  @ReturnsImmutableObject
  @CodingStyleguideUnaware
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> NavigableMap <KEYTYPE, VALUETYPE> makeUnmodifiableNotNull (@Nullable final NavigableMap <KEYTYPE, VALUETYPE> aNavigableMap)
  {
    return aNavigableMap == null ? Collections.emptyNavigableMap () : Collections.unmodifiableNavigableMap (
                                                                                                            aNavigableMap);
  }

  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE removeFirstElement (@Nullable final List <ELEMENTTYPE> aList)
  {
    return isEmpty (aList) ? null : aList.remove (0);
  }

  /**
   * Remove the element at the specified index from the passed list. This works if the list is not
   * <code>null</code> and the index is &ge; 0 and &lt; <code>list.size()</code>
   *
   * @param aList
   *        The list to remove an element from. May be <code>null</code>.
   * @param nIndex
   *        The index to be removed. May be arbitrary.
   * @return {@link EChange#CHANGED} if removal was successful
   * @see #removeAndReturnElementAtIndex(List, int)
   */
  @Nonnull
  public static EChange removeAtIndex (@Nullable final List <?> aList, final int nIndex)
  {
    if (aList == null || nIndex < 0 || nIndex >= aList.size ())
      return EChange.UNCHANGED;
    aList.remove (nIndex);
    return EChange.CHANGED;
  }

  /**
   * Remove the element at the specified index from the passed list. This works if the list is not
   * <code>null</code> and the index is &ge; 0 and &lt; <code>list.size()</code>
   *
   * @param <ELEMENTTYPE>
   *        List element type
   * @param aList
   *        The list to remove an element from. May be <code>null</code>.
   * @param nIndex
   *        The index to be removed. May be arbitrary.
   * @return <code>null</code> if removal failed or the removed element. Note: the removed element
   *         may also be <code>null</code> so it may be tricky to determine if removal succeeded or
   *         not!
   * @see #removeAtIndex(List, int)
   */
  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE removeAndReturnElementAtIndex (@Nullable final List <ELEMENTTYPE> aList,
                                                                         final int nIndex)
  {
    if (aList == null || nIndex < 0 || nIndex >= aList.size ())
      return null;
    return aList.remove (nIndex);
  }

  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE removeLastElement (@Nullable final List <ELEMENTTYPE> aList)
  {
    final int nSize = getSize (aList);
    return nSize == 0 ? null : aList.remove (nSize - 1);
  }

  /**
   * Get a list where each contained item is also cloned. Like a deep copy.
   *
   * @param aList
   *        Source list. May be <code>null</code>.
   * @return The cloned list. Never <code>null</code> but maybe empty if the source list is empty.
   * @param <DATATYPE>
   *        The list element type to be cloned
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <DATATYPE> ICommonsList <DATATYPE> getGenericClonedList (@Nullable final Iterable <DATATYPE> aList)
  {
    final ICommonsList <DATATYPE> ret = new CommonsArrayList <> ();
    if (aList != null)
      for (final DATATYPE aItem : aList)
        ret.add (CloneHelper.getClonedValue (aItem));
    return ret;
  }

  /**
   * Get a list where each contained item is also cloned. Like a deep copy.
   *
   * @param aList
   *        Source list. May be <code>null</code>.
   * @return The cloned list. Never <code>null</code> but maybe empty if the source list is empty.
   * @param <DATATYPE>
   *        The set element type to be cloned
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <DATATYPE extends ICloneable <DATATYPE>> ICommonsList <DATATYPE> getClonedList (@Nullable final Iterable <DATATYPE> aList)
  {
    final ICommonsList <DATATYPE> ret = new CommonsArrayList <> ();
    if (aList != null)
      for (final DATATYPE aItem : aList)
        ret.add (CloneHelper.getCloneIfNotNull (aItem));
    return ret;
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
