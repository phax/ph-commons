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
package com.helger.commons.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.annotation.ReturnsImmutableObject;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.collection.impl.EmptySortedSet;
import com.helger.commons.collection.impl.NonBlockingStack;
import com.helger.commons.collection.iterate.IIterableIterator;
import com.helger.commons.collection.multimap.IMultiMap;
import com.helger.commons.collection.multimap.IMultiMapSetBased;
import com.helger.commons.collection.multimap.MultiHashMapHashSetBased;
import com.helger.commons.lang.ClassHelper;
import com.helger.commons.state.EChange;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Provides various helper methods to handle collections like {@link List},
 * {@link Set} and {@link Map}.
 *
 * @author Philip Helger
 */
@Immutable
public final class CollectionHelper
{
  @PresentForCodeCoverage
  private static final CollectionHelper s_aInstance = new CollectionHelper ();

  private CollectionHelper ()
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

  /**
   * Get the passed object as a {@link List} object. This is helpful in case you
   * want to compare the String array ["a", "b"] with the List&lt;String&gt;
   * ("a", "b") If the passed object is not a recognized. container type, than a
   * new list with one element is created!
   *
   * @param aObj
   *        The object to be converted. May not be <code>null</code>.
   * @return The object as a collection. Never <code>null</code>.
   */
  @Nonnull
  public static List <?> getAsList (@Nonnull final Object aObj)
  {
    ValueEnforcer.notNull (aObj, "Object");

    final ECollectionBaseType eType = getCollectionBaseTypeOfObject (aObj);
    if (eType == null)
    {
      // It's not a supported container -> create a new list with one element
      return newList (aObj);
    }

    switch (eType)
    {
      case COLLECTION:
        // It's already a collection
        if (aObj instanceof List <?>)
          return (List <?>) aObj;
        return newList ((Collection <?>) aObj);
      case SET:
        // Convert to list
        return newList ((Set <?>) aObj);
      case MAP:
        // Use the entry set of the map as list
        return newList (((Map <?, ?>) aObj).entrySet ());
      case ARRAY:
        // Convert the array to a list
        return newList ((Object []) aObj);
      case ITERATOR:
        // Convert the iterator to a list
        return newList ((Iterator <?>) aObj);
      case ITERABLE:
        // Convert the iterable to a list
        return newList ((Iterable <?>) aObj);
      case ENUMERATION:
        // Convert the enumeration to a list
        return newList ((Enumeration <?>) aObj);
      default:
        throw new IllegalStateException ("Unhandled collection type " + eType + "!");
    }
  }

  @Nonnull
  public static <ELEMENTTYPE> List <? extends ELEMENTTYPE> getNotNull (@Nullable final List <? extends ELEMENTTYPE> aList)
  {
    return aList == null ? newList (0) : aList;
  }

  @Nonnull
  public static <ELEMENTTYPE> Set <? extends ELEMENTTYPE> getNotNull (@Nullable final Set <? extends ELEMENTTYPE> aSet)
  {
    return aSet == null ? newSet (0) : aSet;
  }

  @Nonnull
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> SortedSet <? extends ELEMENTTYPE> getNotNull (@Nullable final SortedSet <? extends ELEMENTTYPE> aSortedSet)
  {
    return aSortedSet == null ? newSortedSet () : aSortedSet;
  }

  @Nonnull
  public static <KEYTYPE, VALUETYPE> Map <? extends KEYTYPE, ? extends VALUETYPE> getNotNull (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    return aMap == null ? newMap (0) : aMap;
  }

  @Nonnull
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> SortedMap <? extends KEYTYPE, ? extends VALUETYPE> getNotNull (@Nullable final SortedMap <? extends KEYTYPE, ? extends VALUETYPE> aSortedMap)
  {
    return aSortedMap == null ? newSortedMap () : aSortedMap;
  }

  @Nullable
  @ReturnsImmutableObject
  public static <ELEMENTTYPE> Collection <ELEMENTTYPE> makeUnmodifiable (@Nullable final Collection <? extends ELEMENTTYPE> aCollection)
  {
    return aCollection == null ? null : Collections.unmodifiableCollection (aCollection);
  }

  @Nullable
  @ReturnsImmutableObject
  public static <ELEMENTTYPE> List <ELEMENTTYPE> makeUnmodifiable (@Nullable final List <? extends ELEMENTTYPE> aList)
  {
    return aList == null ? null : Collections.unmodifiableList (aList);
  }

  @Nullable
  @ReturnsImmutableObject
  public static <ELEMENTTYPE> Set <ELEMENTTYPE> makeUnmodifiable (@Nullable final Set <? extends ELEMENTTYPE> aSet)
  {
    return aSet == null ? null : Collections.unmodifiableSet (aSet);
  }

  @Nullable
  @ReturnsImmutableObject
  public static <KEYTYPE, VALUETYPE> Map <KEYTYPE, VALUETYPE> makeUnmodifiable (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    return aMap == null ? null : Collections.unmodifiableMap (aMap);
  }

  @Nullable
  @ReturnsImmutableObject
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> SortedSet <ELEMENTTYPE> makeUnmodifiable (@Nullable final SortedSet <ELEMENTTYPE> aSortedSet)
  {
    return aSortedSet == null ? null : Collections.unmodifiableSortedSet (aSortedSet);
  }

  @Nullable
  @ReturnsImmutableObject
  public static <KEYTYPE, VALUETYPE> SortedMap <KEYTYPE, VALUETYPE> makeUnmodifiable (@Nullable final SortedMap <KEYTYPE, ? extends VALUETYPE> aSortedMap)
  {
    return aSortedMap == null ? null : Collections.unmodifiableSortedMap (aSortedMap);
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <ELEMENTTYPE> Collection <ELEMENTTYPE> makeUnmodifiableNotNull (@Nullable final Collection <? extends ELEMENTTYPE> aCollection)
  {
    return aCollection == null ? newUnmodifiableList () : Collections.unmodifiableCollection (aCollection);
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <ELEMENTTYPE> List <ELEMENTTYPE> makeUnmodifiableNotNull (@Nullable final List <? extends ELEMENTTYPE> aList)
  {
    return aList == null ? newUnmodifiableList () : Collections.unmodifiableList (aList);
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <ELEMENTTYPE> Set <ELEMENTTYPE> makeUnmodifiableNotNull (@Nullable final Set <? extends ELEMENTTYPE> aSet)
  {
    return aSet == null ? newUnmodifiableSet () : Collections.unmodifiableSet (aSet);
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <KEYTYPE, VALUETYPE> Map <KEYTYPE, VALUETYPE> makeUnmodifiableNotNull (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    return aMap == null ? newUnmodifiableMap () : Collections.unmodifiableMap (aMap);
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> SortedSet <ELEMENTTYPE> makeUnmodifiableNotNull (@Nullable final SortedSet <ELEMENTTYPE> aSortedSet)
  {
    return aSortedSet == null ? newUnmodifiableSortedSet () : Collections.unmodifiableSortedSet (aSortedSet);
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> SortedMap <KEYTYPE, VALUETYPE> makeUnmodifiableNotNull (@Nullable final SortedMap <KEYTYPE, ? extends VALUETYPE> aSortedMap)
  {
    return Collections.unmodifiableSortedMap (aSortedMap == null ? newSortedMap () : aSortedMap);
  }

  /**
   * Get all elements that are only contained in the first contained, and not in
   * the second. This method implements <code>aCont1 - aCont2</code>.
   *
   * @param <ELEMENTTYPE>
   *        Set element type
   * @param aCollection1
   *        The first container. May be <code>null</code> or empty.
   * @param aCollection2
   *        The second container. May be <code>null</code> or empty.
   * @return The difference and never <code>null</code>. Returns an empty set,
   *         if the first container is empty. Returns a copy of the first
   *         container, if the second container is empty. Returns
   *         <code>aCont1 - aCont2</code> if both containers are non-empty.
   */
  @Nullable
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> Set <ELEMENTTYPE> getDifference (@Nullable final Collection <? extends ELEMENTTYPE> aCollection1,
                                                               @Nullable final Collection <? extends ELEMENTTYPE> aCollection2)
  {
    if (isEmpty (aCollection1))
      return newSet (0);
    if (isEmpty (aCollection2))
      return newSet (aCollection1);

    final Set <ELEMENTTYPE> ret = newSet (aCollection1);
    ret.removeAll (aCollection2);
    return ret;
  }

  /**
   * Get all elements that are contained in the first AND in the second
   * container.
   *
   * @param <ELEMENTTYPE>
   *        Collection element type
   * @param aCollection1
   *        The first container. May be <code>null</code> or empty.
   * @param aCollection2
   *        The second container. May be <code>null</code> or empty.
   * @return An empty set, if either the first or the second container are
   *         empty. Returns a set of elements that are contained in both
   *         containers, if both containers are non-empty. The return value is
   *         never <code>null</code>.
   */
  @Nullable
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> Set <ELEMENTTYPE> getIntersected (@Nullable final Collection <? extends ELEMENTTYPE> aCollection1,
                                                                @Nullable final Collection <? extends ELEMENTTYPE> aCollection2)
  {
    if (isEmpty (aCollection1))
      return newSet (0);
    if (isEmpty (aCollection2))
      return newSet (0);

    final Set <ELEMENTTYPE> ret = newSet (aCollection1);
    ret.retainAll (aCollection2);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> HashMap <KEYTYPE, VALUETYPE> newMap (@Nonnegative final int nInitialCapacity)
  {
    return new HashMap <> (nInitialCapacity);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> HashMap <KEYTYPE, VALUETYPE> newMap ()
  {
    return new HashMap <> ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCKEYTYPE, SRCVALUETYPE, DSTKEYTYPE, DSTVALUETYPE> HashMap <DSTKEYTYPE, DSTVALUETYPE> newMapMapped (@Nullable final Map <? extends SRCKEYTYPE, ? extends SRCVALUETYPE> aMap,
                                                                                                                      @Nonnull final Function <? super SRCKEYTYPE, DSTKEYTYPE> aKeyMapper,
                                                                                                                      @Nonnull final Function <? super SRCVALUETYPE, DSTVALUETYPE> aValueMapper)
  {
    if (isEmpty (aMap))
      return newMap (0);
    final HashMap <DSTKEYTYPE, DSTVALUETYPE> ret = newMap (aMap.size ());
    for (final Map.Entry <? extends SRCKEYTYPE, ? extends SRCVALUETYPE> aEntry : aMap.entrySet ())
      ret.put (aKeyMapper.apply (aEntry.getKey ()), aValueMapper.apply (aEntry.getValue ()));
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> HashMap <KEYTYPE, VALUETYPE> newMap (@Nullable final Map <KEYTYPE, VALUETYPE> aMap,
                                                                          @Nonnull final Predicate <Map.Entry <? super KEYTYPE, ? super VALUETYPE>> aFilter)
  {
    if (isEmpty (aMap))
      return newMap (0);
    final HashMap <KEYTYPE, VALUETYPE> ret = newMap (aMap.size ());
    for (final Map.Entry <KEYTYPE, VALUETYPE> aEntry : aMap.entrySet ())
      if (aFilter.test (aEntry))
        ret.put (aEntry.getKey (), aEntry.getValue ());
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> HashMap <KEYTYPE, VALUETYPE> newMap (@Nullable final KEYTYPE aKey,
                                                                          @Nullable final VALUETYPE aValue)
  {
    final HashMap <KEYTYPE, VALUETYPE> ret = newMap (1);
    ret.put (aKey, aValue);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE> HashMap <ELEMENTTYPE, ELEMENTTYPE> newMap (@Nullable final ELEMENTTYPE... aValues)
  {
    if (ArrayHelper.isEmpty (aValues))
      return newMap (0);

    if ((aValues.length % 2) != 0)
      throw new IllegalArgumentException ("The passed array needs an even number of elements!");

    final HashMap <ELEMENTTYPE, ELEMENTTYPE> ret = newMap (aValues.length / 2);
    for (int i = 0; i < aValues.length; i += 2)
      ret.put (aValues[i], aValues[i + 1]);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> HashMap <KEYTYPE, VALUETYPE> newMap (@Nullable final KEYTYPE [] aKeys,
                                                                          @Nullable final VALUETYPE [] aValues)
  {
    // Are both empty?
    if (ArrayHelper.isEmpty (aKeys) && ArrayHelper.isEmpty (aValues))
      return newMap (0);

    // keys OR values may be null here
    if (ArrayHelper.getSize (aKeys) != ArrayHelper.getSize (aValues))
      throw new IllegalArgumentException ("The passed arrays have different length!");

    final HashMap <KEYTYPE, VALUETYPE> ret = newMap (aKeys.length);
    for (int i = 0; i < aKeys.length; ++i)
      ret.put (aKeys[i], aValues[i]);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> HashMap <KEYTYPE, VALUETYPE> newMap (@Nullable final Collection <? extends KEYTYPE> aKeys,
                                                                          @Nullable final Collection <? extends VALUETYPE> aValues)
  {
    // Are both empty?
    if (isEmpty (aKeys) && isEmpty (aValues))
      return newMap (0);

    // keys OR values may be null here
    if (getSize (aKeys) != getSize (aValues))
      throw new IllegalArgumentException ("Number of keys is different from number of values");

    final HashMap <KEYTYPE, VALUETYPE> ret = newMap (aKeys.size ());
    final Iterator <? extends KEYTYPE> itk = aKeys.iterator ();
    final Iterator <? extends VALUETYPE> itv = aValues.iterator ();
    while (itk.hasNext ())
      ret.put (itk.next (), itv.next ());
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> HashMap <KEYTYPE, VALUETYPE> newMap (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    if (isEmpty (aMap))
      return newMap (0);

    return new HashMap <> (aMap);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> HashMap <KEYTYPE, VALUETYPE> newMap (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> [] aMaps)
  {
    if (aMaps == null || aMaps.length == 0)
      return newMap (0);

    final HashMap <KEYTYPE, VALUETYPE> ret = newMap ();
    for (final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap : aMaps)
      ret.putAll (aMap);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> HashMap <KEYTYPE, VALUETYPE> newMap (@Nullable final Collection <? extends Map.Entry <KEYTYPE, VALUETYPE>> aCollection)
  {
    if (isEmpty (aCollection))
      return newMap (0);

    final HashMap <KEYTYPE, VALUETYPE> ret = newMap (aCollection.size ());
    for (final Map.Entry <KEYTYPE, VALUETYPE> aEntry : aCollection)
      ret.put (aEntry.getKey (), aEntry.getValue ());
    return ret;
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <KEYTYPE, VALUETYPE> Map <KEYTYPE, VALUETYPE> newUnmodifiableMap ()
  {
    return Collections.emptyMap ();
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <KEYTYPE, VALUETYPE> Map <KEYTYPE, VALUETYPE> newUnmodifiableMap (@Nullable final KEYTYPE aKey,
                                                                                  @Nullable final VALUETYPE aValue)
  {
    return Collections.singletonMap (aKey, aValue);
  }

  @Nonnull
  @ReturnsImmutableObject
  @SafeVarargs
  public static <ELEMENTTYPE> Map <ELEMENTTYPE, ELEMENTTYPE> newUnmodifiableMap (@Nullable final ELEMENTTYPE... aValues)
  {
    return makeUnmodifiable (newMap (aValues));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <KEYTYPE, VALUETYPE> Map <KEYTYPE, VALUETYPE> newUnmodifiableMap (@Nullable final KEYTYPE [] aKeys,
                                                                                  @Nullable final VALUETYPE [] aValues)
  {
    return makeUnmodifiable (newMap (aKeys, aValues));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <KEYTYPE, VALUETYPE> Map <KEYTYPE, VALUETYPE> newUnmodifiableMap (@Nullable final Collection <? extends KEYTYPE> aKeys,
                                                                                  @Nullable final Collection <? extends VALUETYPE> aValues)
  {
    return makeUnmodifiable (newMap (aKeys, aValues));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <KEYTYPE, VALUETYPE> Map <KEYTYPE, VALUETYPE> newUnmodifiableMap (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    return makeUnmodifiable (aMap);
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <KEYTYPE, VALUETYPE> Map <KEYTYPE, VALUETYPE> newUnmodifiableMap (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> [] aMaps)
  {
    return makeUnmodifiable (newMap (aMaps));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <KEYTYPE, VALUETYPE> Map <KEYTYPE, VALUETYPE> newUnmodifiableMap (@Nullable final Collection <? extends Map.Entry <KEYTYPE, VALUETYPE>> aCollection)
  {
    return makeUnmodifiable (newMap (aCollection));
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> LinkedHashMap <KEYTYPE, VALUETYPE> newOrderedMap (@Nonnegative final int nInitialCapacity)
  {
    return new LinkedHashMap <> (nInitialCapacity);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> LinkedHashMap <KEYTYPE, VALUETYPE> newOrderedMap ()
  {
    return new LinkedHashMap <> ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCKEYTYPE, SRCVALUETYPE, DSTKEYTYPE, DSTVALUETYPE> LinkedHashMap <DSTKEYTYPE, DSTVALUETYPE> newOrderedMapMapped (@Nullable final Map <? extends SRCKEYTYPE, ? extends SRCVALUETYPE> aMap,
                                                                                                                                   @Nonnull final Function <? super SRCKEYTYPE, DSTKEYTYPE> aKeyMapper,
                                                                                                                                   @Nonnull final Function <? super SRCVALUETYPE, DSTVALUETYPE> aValueMapper)
  {
    if (isEmpty (aMap))
      return newOrderedMap (0);
    final LinkedHashMap <DSTKEYTYPE, DSTVALUETYPE> ret = newOrderedMap (aMap.size ());
    for (final Map.Entry <? extends SRCKEYTYPE, ? extends SRCVALUETYPE> aEntry : aMap.entrySet ())
      ret.put (aKeyMapper.apply (aEntry.getKey ()), aValueMapper.apply (aEntry.getValue ()));
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> LinkedHashMap <KEYTYPE, VALUETYPE> newOrderedMap (@Nullable final Map <KEYTYPE, VALUETYPE> aMap,
                                                                                       @Nonnull final Predicate <Map.Entry <? super KEYTYPE, ? super VALUETYPE>> aFilter)
  {
    if (isEmpty (aMap))
      return newOrderedMap (0);
    final LinkedHashMap <KEYTYPE, VALUETYPE> ret = newOrderedMap (aMap.size ());
    for (final Map.Entry <KEYTYPE, VALUETYPE> aEntry : aMap.entrySet ())
      if (aFilter.test (aEntry))
        ret.put (aEntry.getKey (), aEntry.getValue ());
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> LinkedHashMap <KEYTYPE, VALUETYPE> newOrderedMap (@Nullable final KEYTYPE aKey,
                                                                                       @Nullable final VALUETYPE aValue)
  {
    final LinkedHashMap <KEYTYPE, VALUETYPE> ret = newOrderedMap (1);
    ret.put (aKey, aValue);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE> LinkedHashMap <ELEMENTTYPE, ELEMENTTYPE> newOrderedMap (@Nullable final ELEMENTTYPE... aValues)
  {
    if (ArrayHelper.isEmpty (aValues))
      return newOrderedMap (0);

    if ((aValues.length % 2) != 0)
      throw new IllegalArgumentException ("The passed array needs an even number of elements!");

    final LinkedHashMap <ELEMENTTYPE, ELEMENTTYPE> ret = newOrderedMap (aValues.length / 2);
    for (int i = 0; i < aValues.length; i += 2)
      ret.put (aValues[i], aValues[i + 1]);
    return ret;
  }

  /**
   * Retrieve a map that is ordered in the way the parameter arrays are passed
   * in. Note that key and value arrays need to have the same length.
   *
   * @param <KEYTYPE>
   *        The key type.
   * @param <VALUETYPE>
   *        The value type.
   * @param aKeys
   *        The key array to use. May not be <code>null</code>.
   * @param aValues
   *        The value array to use. May not be <code>null</code>.
   * @return A {@link java.util.LinkedHashMap} containing the passed key-value
   *         entries. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> LinkedHashMap <KEYTYPE, VALUETYPE> newOrderedMap (@Nullable final KEYTYPE [] aKeys,
                                                                                       @Nullable final VALUETYPE [] aValues)
  {
    // Are both empty?
    if (ArrayHelper.isEmpty (aKeys) && ArrayHelper.isEmpty (aValues))
      return newOrderedMap (0);

    // keys OR values may be null here
    if (ArrayHelper.getSize (aKeys) != ArrayHelper.getSize (aValues))
      throw new IllegalArgumentException ("The passed arrays have different length!");

    final LinkedHashMap <KEYTYPE, VALUETYPE> ret = newOrderedMap (aKeys.length);
    for (int i = 0; i < aKeys.length; ++i)
      ret.put (aKeys[i], aValues[i]);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> LinkedHashMap <KEYTYPE, VALUETYPE> newOrderedMap (@Nullable final Collection <? extends KEYTYPE> aKeys,
                                                                                       @Nullable final Collection <? extends VALUETYPE> aValues)
  {
    // Are both empty?
    if (isEmpty (aKeys) && isEmpty (aValues))
      return newOrderedMap (0);

    // keys OR values may be null here
    if (getSize (aKeys) != getSize (aValues))
      throw new IllegalArgumentException ("Number of keys is different from number of values");

    final LinkedHashMap <KEYTYPE, VALUETYPE> ret = newOrderedMap (aKeys.size ());
    final Iterator <? extends KEYTYPE> itk = aKeys.iterator ();
    final Iterator <? extends VALUETYPE> itv = aValues.iterator ();
    while (itk.hasNext ())
      ret.put (itk.next (), itv.next ());
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> LinkedHashMap <KEYTYPE, VALUETYPE> newOrderedMap (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    if (isEmpty (aMap))
      return newOrderedMap (0);

    return new LinkedHashMap <> (aMap);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> LinkedHashMap <KEYTYPE, VALUETYPE> newOrderedMap (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> [] aMaps)
  {
    if (aMaps == null || aMaps.length == 0)
      return newOrderedMap (0);

    final LinkedHashMap <KEYTYPE, VALUETYPE> ret = newOrderedMap ();
    for (final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap : aMaps)
      ret.putAll (aMap);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> LinkedHashMap <KEYTYPE, VALUETYPE> newOrderedMap (@Nullable final Collection <? extends Map.Entry <KEYTYPE, VALUETYPE>> aCollection)
  {
    if (isEmpty (aCollection))
      return newOrderedMap (0);

    final LinkedHashMap <KEYTYPE, VALUETYPE> ret = newOrderedMap (aCollection.size ());
    for (final Map.Entry <KEYTYPE, VALUETYPE> aEntry : aCollection)
      ret.put (aEntry.getKey (), aEntry.getValue ());
    return ret;
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <KEYTYPE, VALUETYPE> Map <KEYTYPE, VALUETYPE> newUnmodifiableOrderedMap ()
  {
    return Collections.emptyMap ();
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <KEYTYPE, VALUETYPE> Map <KEYTYPE, VALUETYPE> newUnmodifiableOrderedMap (@Nullable final KEYTYPE aKey,
                                                                                         @Nullable final VALUETYPE aValue)
  {
    return Collections.singletonMap (aKey, aValue);
  }

  @Nonnull
  @ReturnsImmutableObject
  @SafeVarargs
  public static <ELEMENTTYPE> Map <ELEMENTTYPE, ELEMENTTYPE> newUnmodifiableOrderedMap (@Nullable final ELEMENTTYPE... aValues)
  {
    return makeUnmodifiable (newOrderedMap (aValues));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <KEYTYPE, VALUETYPE> Map <KEYTYPE, VALUETYPE> newUnmodifiableOrderedMap (@Nullable final KEYTYPE [] aKeys,
                                                                                         @Nullable final VALUETYPE [] aValues)
  {
    return makeUnmodifiable (newOrderedMap (aKeys, aValues));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <KEYTYPE, VALUETYPE> Map <KEYTYPE, VALUETYPE> newUnmodifiableOrderedMap (@Nullable final Collection <? extends KEYTYPE> aKeys,
                                                                                         @Nullable final Collection <? extends VALUETYPE> aValues)
  {
    return makeUnmodifiable (newOrderedMap (aKeys, aValues));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <KEYTYPE, VALUETYPE> Map <KEYTYPE, VALUETYPE> newUnmodifiableOrderedMap (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> aOrderedMap)
  {
    return makeUnmodifiable (aOrderedMap);
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <KEYTYPE, VALUETYPE> Map <KEYTYPE, VALUETYPE> newUnmodifiableOrderedMap (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> [] aOrderedMaps)
  {
    return makeUnmodifiable (newOrderedMap (aOrderedMaps));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <KEYTYPE, VALUETYPE> Map <KEYTYPE, VALUETYPE> newUnmodifiableOrderedMap (@Nullable final Collection <? extends Map.Entry <KEYTYPE, VALUETYPE>> aCollection)
  {
    return makeUnmodifiable (newOrderedMap (aCollection));
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> TreeMap <KEYTYPE, VALUETYPE> newSortedMap ()
  {
    return new TreeMap <> (Comparator.nullsFirst (Comparator.naturalOrder ()));
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCKEYTYPE, SRCVALUETYPE, DSTKEYTYPE extends Comparable <? super DSTKEYTYPE>, DSTVALUETYPE> TreeMap <DSTKEYTYPE, DSTVALUETYPE> newSortedMapMapped (@Nullable final Map <? extends SRCKEYTYPE, ? extends SRCVALUETYPE> aMap,
                                                                                                                                                                    @Nonnull final Function <? super SRCKEYTYPE, DSTKEYTYPE> aKeyMapper,
                                                                                                                                                                    @Nonnull final Function <? super SRCVALUETYPE, DSTVALUETYPE> aValueMapper)
  {
    final TreeMap <DSTKEYTYPE, DSTVALUETYPE> ret = newSortedMap ();
    if (isNotEmpty (aMap))
      for (final Map.Entry <? extends SRCKEYTYPE, ? extends SRCVALUETYPE> aEntry : aMap.entrySet ())
        ret.put (aKeyMapper.apply (aEntry.getKey ()), aValueMapper.apply (aEntry.getValue ()));
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> TreeMap <KEYTYPE, VALUETYPE> newSortedMap (@Nullable final Map <KEYTYPE, VALUETYPE> aMap,
                                                                                                                     @Nonnull final Predicate <Map.Entry <? super KEYTYPE, ? super VALUETYPE>> aFilter)
  {
    final TreeMap <KEYTYPE, VALUETYPE> ret = newSortedMap ();
    if (isNotEmpty (aMap))
      for (final Map.Entry <KEYTYPE, VALUETYPE> aEntry : aMap.entrySet ())
        if (aFilter.test (aEntry))
          ret.put (aEntry.getKey (), aEntry.getValue ());
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> TreeMap <KEYTYPE, VALUETYPE> newSortedMap (@Nullable final KEYTYPE aKey,
                                                                                                                     @Nullable final VALUETYPE aValue)
  {
    final TreeMap <KEYTYPE, VALUETYPE> ret = newSortedMap ();
    ret.put (aKey, aValue);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> TreeMap <ELEMENTTYPE, ELEMENTTYPE> newSortedMap (@Nullable final ELEMENTTYPE... aValues)
  {
    if (ArrayHelper.isEmpty (aValues))
      return newSortedMap ();

    if ((aValues.length % 2) != 0)
      throw new IllegalArgumentException ("The passed array needs an even number of elements!");

    final TreeMap <ELEMENTTYPE, ELEMENTTYPE> ret = newSortedMap ();
    for (int i = 0; i < aValues.length; i += 2)
      ret.put (aValues[i], aValues[i + 1]);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> TreeMap <KEYTYPE, VALUETYPE> newSortedMap (@Nullable final KEYTYPE [] aKeys,
                                                                                                                     @Nullable final VALUETYPE [] aValues)
  {
    // Are both empty?
    if (ArrayHelper.isEmpty (aKeys) && ArrayHelper.isEmpty (aValues))
      return newSortedMap ();

    // keys OR values may be null here
    if (ArrayHelper.getSize (aKeys) != ArrayHelper.getSize (aValues))
      throw new IllegalArgumentException ("The passed arrays have different length!");

    final TreeMap <KEYTYPE, VALUETYPE> ret = newSortedMap ();
    for (int i = 0; i < aKeys.length; ++i)
      ret.put (aKeys[i], aValues[i]);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> TreeMap <KEYTYPE, VALUETYPE> newSortedMap (@Nullable final Collection <? extends KEYTYPE> aKeys,
                                                                                                                     @Nullable final Collection <? extends VALUETYPE> aValues)
  {
    // Are both empty?
    if (isEmpty (aKeys) && isEmpty (aValues))
      return newSortedMap ();

    // keys OR values may be null here
    if (getSize (aKeys) != getSize (aValues))
      throw new IllegalArgumentException ("Number of keys is different from number of values");

    final TreeMap <KEYTYPE, VALUETYPE> ret = newSortedMap ();
    final Iterator <? extends KEYTYPE> itk = aKeys.iterator ();
    final Iterator <? extends VALUETYPE> itv = aValues.iterator ();
    while (itk.hasNext ())
      ret.put (itk.next (), itv.next ());
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> TreeMap <KEYTYPE, VALUETYPE> newSortedMap (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    if (isEmpty (aMap))
      return newSortedMap ();

    final TreeMap <KEYTYPE, VALUETYPE> ret = newSortedMap ();
    ret.putAll (aMap);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> TreeMap <KEYTYPE, VALUETYPE> newSortedMap (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> [] aMaps)
  {
    if (aMaps == null || aMaps.length == 0)
      return newSortedMap ();

    final TreeMap <KEYTYPE, VALUETYPE> ret = newSortedMap ();
    for (final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap : aMaps)
      ret.putAll (aMap);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> TreeMap <KEYTYPE, VALUETYPE> newSortedMap (@Nullable final Collection <? extends Map.Entry <KEYTYPE, VALUETYPE>> aCollection)
  {
    if (isEmpty (aCollection))
      return newSortedMap ();

    final TreeMap <KEYTYPE, VALUETYPE> ret = newSortedMap ();
    for (final Map.Entry <KEYTYPE, VALUETYPE> aEntry : aCollection)
      ret.put (aEntry.getKey (), aEntry.getValue ());
    return ret;
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> SortedMap <KEYTYPE, VALUETYPE> newUnmodifiableSortedMap ()
  {
    return makeUnmodifiable (CollectionHelper.<KEYTYPE, VALUETYPE> newSortedMap ());
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> SortedMap <KEYTYPE, VALUETYPE> newUnmodifiableSortedMap (@Nullable final KEYTYPE aKey,
                                                                                                                                   @Nullable final VALUETYPE aValue)
  {
    return makeUnmodifiable (newSortedMap (aKey, aValue));
  }

  @Nonnull
  @ReturnsImmutableObject
  @SafeVarargs
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> SortedMap <ELEMENTTYPE, ELEMENTTYPE> newUnmodifiableSortedMap (@Nullable final ELEMENTTYPE... aValues)
  {
    return makeUnmodifiable (newSortedMap (aValues));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> SortedMap <KEYTYPE, VALUETYPE> newUnmodifiableSortedMap (@Nullable final KEYTYPE [] aKeys,
                                                                                                                                   @Nullable final VALUETYPE [] aValues)
  {
    return makeUnmodifiable (newSortedMap (aKeys, aValues));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> SortedMap <KEYTYPE, VALUETYPE> newUnmodifiableSortedMap (@Nullable final Collection <? extends KEYTYPE> aKeys,
                                                                                                                                   @Nullable final Collection <? extends VALUETYPE> aValues)
  {
    return makeUnmodifiable (newSortedMap (aKeys, aValues));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> SortedMap <KEYTYPE, VALUETYPE> newUnmodifiableSortedMap (@Nullable final SortedMap <KEYTYPE, ? extends VALUETYPE> aMap)
  {
    return makeUnmodifiable (aMap);
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> SortedMap <KEYTYPE, VALUETYPE> newUnmodifiableSortedMap (@Nullable final Map <KEYTYPE, ? extends VALUETYPE> [] aMaps)
  {
    return makeUnmodifiable (newSortedMap (aMaps));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> SortedMap <KEYTYPE, VALUETYPE> newUnmodifiableSortedMap (@Nullable final Collection <? extends Map.Entry <KEYTYPE, VALUETYPE>> aCollection)
  {
    return makeUnmodifiable (newSortedMap (aCollection));
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> HashSet <ELEMENTTYPE> newSet (@Nonnegative final int nInitialCapacity)
  {
    return new HashSet <> (nInitialCapacity);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> HashSet <ELEMENTTYPE> newSet ()
  {
    return new HashSet <> ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> HashSet <DSTTYPE> newSetMapped (@Nullable final Collection <? extends SRCTYPE> aCollection,
                                                                   @Nonnull final Function <? super SRCTYPE, DSTTYPE> aMapper)
  {
    if (isEmpty (aCollection))
      return newSet (0);
    final HashSet <DSTTYPE> ret = newSet (aCollection.size ());
    for (final SRCTYPE aValue : aCollection)
      ret.add (aMapper.apply (aValue));
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> HashSet <ELEMENTTYPE> newSet (@Nullable final Collection <? extends ELEMENTTYPE> aCollection,
                                                            @Nonnull final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (isEmpty (aCollection))
      return newSet (0);
    final HashSet <ELEMENTTYPE> ret = newSet (aCollection.size ());
    for (final ELEMENTTYPE aValue : aCollection)
      if (aFilter.test (aValue))
        ret.add (aValue);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> HashSet <ELEMENTTYPE> newSet (@Nullable final ELEMENTTYPE aValue)
  {
    final HashSet <ELEMENTTYPE> ret = newSet (1);
    ret.add (aValue);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE> HashSet <ELEMENTTYPE> newSet (@Nullable final ELEMENTTYPE... aValues)
  {
    if (ArrayHelper.isEmpty (aValues))
      return newSet (0);

    final HashSet <ELEMENTTYPE> ret = newSet (aValues.length);
    Collections.addAll (ret, aValues);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> HashSet <ELEMENTTYPE> newSet (@Nullable final Iterable <? extends ELEMENTTYPE> aCont)
  {
    final HashSet <ELEMENTTYPE> ret = newSet ();
    if (aCont != null)
      for (final ELEMENTTYPE aValue : aCont)
        ret.add (aValue);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> HashSet <ELEMENTTYPE> newSet (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    if (isEmpty (aCont))
      return newSet (0);

    return new HashSet <> (aCont);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> HashSet <ELEMENTTYPE> newSet (@Nullable final Iterator <? extends ELEMENTTYPE> aIter)
  {
    final HashSet <ELEMENTTYPE> ret = newSet ();
    if (aIter != null)
      while (aIter.hasNext ())
        ret.add (aIter.next ());
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> HashSet <ELEMENTTYPE> newSet (@Nullable final IIterableIterator <? extends ELEMENTTYPE> aIter)
  {
    if (aIter == null)
      return newSet (0);
    return newSet (aIter.iterator ());
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> HashSet <ELEMENTTYPE> newSet (@Nullable final Enumeration <? extends ELEMENTTYPE> aEnum)
  {
    final HashSet <ELEMENTTYPE> ret = newSet ();
    if (aEnum != null)
      while (aEnum.hasMoreElements ())
        ret.add (aEnum.nextElement ());
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE extends Enum <ELEMENTTYPE>> EnumSet <ELEMENTTYPE> newEnumSet (@Nonnull final Class <ELEMENTTYPE> aEnumClass,
                                                                                           @Nullable final ELEMENTTYPE... aValues)
  {
    final EnumSet <ELEMENTTYPE> ret = EnumSet.noneOf (aEnumClass);
    if (aValues != null)
      for (final ELEMENTTYPE aValue : aValues)
        ret.add (aValue);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Enum <ELEMENTTYPE>> EnumSet <ELEMENTTYPE> newEnumSet (@Nonnull final Class <ELEMENTTYPE> aEnumClass,
                                                                                           @Nullable final Collection <ELEMENTTYPE> aValues)
  {
    if (isEmpty (aValues))
      return EnumSet.noneOf (aEnumClass);
    return EnumSet.copyOf (aValues);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Enum <ELEMENTTYPE>> EnumSet <ELEMENTTYPE> newEnumSet (@Nonnull final Class <ELEMENTTYPE> aEnumClass,
                                                                                           @Nullable final EnumSet <ELEMENTTYPE> aValues)
  {
    if (aValues == null)
      return EnumSet.noneOf (aEnumClass);
    return EnumSet.copyOf (aValues);
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <ELEMENTTYPE> Set <ELEMENTTYPE> newUnmodifiableSet ()
  {
    return Collections.emptySet ();
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <ELEMENTTYPE> Set <ELEMENTTYPE> newUnmodifiableSet (@Nullable final ELEMENTTYPE aValue)
  {
    return Collections.singleton (aValue);
  }

  @Nonnull
  @ReturnsImmutableObject
  @SafeVarargs
  public static <ELEMENTTYPE> Set <ELEMENTTYPE> newUnmodifiableSet (@Nullable final ELEMENTTYPE... aValues)
  {
    return makeUnmodifiable (newSet (aValues));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <ELEMENTTYPE> Set <ELEMENTTYPE> newUnmodifiableSet (@Nullable final Iterable <? extends ELEMENTTYPE> aCont)
  {
    return makeUnmodifiable (newSet (aCont));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <ELEMENTTYPE> Set <ELEMENTTYPE> newUnmodifiableSet (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    return makeUnmodifiable (newSet (aCont));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <ELEMENTTYPE> Set <ELEMENTTYPE> newUnmodifiableSet (@Nullable final Iterator <? extends ELEMENTTYPE> aIter)
  {
    return makeUnmodifiable (newSet (aIter));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <ELEMENTTYPE> Set <ELEMENTTYPE> newUnmodifiableSet (@Nullable final IIterableIterator <? extends ELEMENTTYPE> aIter)
  {
    return makeUnmodifiable (newSet (aIter));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <ELEMENTTYPE> Set <ELEMENTTYPE> newUnmodifiableSet (@Nullable final Enumeration <? extends ELEMENTTYPE> aEnum)
  {
    return makeUnmodifiable (newSet (aEnum));
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> TreeSet <ELEMENTTYPE> newSortedSet ()
  {
    return new TreeSet <> (Comparator.nullsFirst (Comparator.naturalOrder ()));
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE extends Comparable <? super DSTTYPE>> TreeSet <DSTTYPE> newSortedSetMapped (@Nullable final Collection <? extends SRCTYPE> aCollection,
                                                                                                              @Nonnull final Function <? super SRCTYPE, DSTTYPE> aMapper)
  {
    final TreeSet <DSTTYPE> ret = newSortedSet ();
    if (isNotEmpty (aCollection))
      for (final SRCTYPE aValue : aCollection)
        ret.add (aMapper.apply (aValue));
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> TreeSet <ELEMENTTYPE> newSortedSet (@Nullable final Collection <? extends ELEMENTTYPE> aCollection,
                                                                                                           @Nonnull final Predicate <? super ELEMENTTYPE> aFilter)
  {
    final TreeSet <ELEMENTTYPE> ret = newSortedSet ();
    if (isNotEmpty (aCollection))
      for (final ELEMENTTYPE aValue : aCollection)
        if (aFilter.test (aValue))
          ret.add (aValue);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  @SuppressFBWarnings (value = { "NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE" }, justification = "When using the constructor with the Comparator it works with null values!")
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> TreeSet <ELEMENTTYPE> newSortedSet (@Nullable final ELEMENTTYPE aValue)
  {
    final TreeSet <ELEMENTTYPE> ret = newSortedSet ();
    ret.add (aValue);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> TreeSet <ELEMENTTYPE> newSortedSet (@Nullable final ELEMENTTYPE... aValues)
  {
    final TreeSet <ELEMENTTYPE> ret = newSortedSet ();
    if (ArrayHelper.isNotEmpty (aValues))
      Collections.addAll (ret, aValues);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> TreeSet <ELEMENTTYPE> newSortedSet (@Nullable final Iterable <? extends ELEMENTTYPE> aCont)
  {
    final TreeSet <ELEMENTTYPE> ret = newSortedSet ();
    if (aCont != null)
      for (final ELEMENTTYPE aValue : aCont)
        ret.add (aValue);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> TreeSet <ELEMENTTYPE> newSortedSet (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    final TreeSet <ELEMENTTYPE> ret = newSortedSet ();
    if (isNotEmpty (aCont))
      ret.addAll (aCont);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> TreeSet <ELEMENTTYPE> newSortedSet (@Nullable final Iterator <? extends ELEMENTTYPE> aIter)
  {
    final TreeSet <ELEMENTTYPE> ret = newSortedSet ();
    if (aIter != null)
      while (aIter.hasNext ())
        ret.add (aIter.next ());
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> TreeSet <ELEMENTTYPE> newSortedSet (@Nullable final IIterableIterator <? extends ELEMENTTYPE> aIter)
  {
    if (aIter == null)
      return newSortedSet ();
    return newSortedSet (aIter.iterator ());
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> TreeSet <ELEMENTTYPE> newSortedSet (@Nullable final Enumeration <? extends ELEMENTTYPE> aEnum)
  {
    final TreeSet <ELEMENTTYPE> ret = newSortedSet ();
    if (aEnum != null)
      while (aEnum.hasMoreElements ())
        ret.add (aEnum.nextElement ());
    return ret;
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <ELEMENTTYPE> SortedSet <ELEMENTTYPE> newUnmodifiableSortedSet ()
  {
    return new EmptySortedSet <ELEMENTTYPE> ();
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> SortedSet <ELEMENTTYPE> newUnmodifiableSortedSet (@Nullable final ELEMENTTYPE aValue)
  {
    return makeUnmodifiable (newSortedSet (aValue));
  }

  @Nonnull
  @ReturnsImmutableObject
  @SafeVarargs
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> SortedSet <ELEMENTTYPE> newUnmodifiableSortedSet (@Nullable final ELEMENTTYPE... aValues)
  {
    return makeUnmodifiable (newSortedSet (aValues));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> SortedSet <ELEMENTTYPE> newUnmodifiableSortedSet (@Nullable final Iterable <? extends ELEMENTTYPE> aCont)
  {
    return makeUnmodifiable (newSortedSet (aCont));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> SortedSet <ELEMENTTYPE> newUnmodifiableSortedSet (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    return makeUnmodifiable (newSortedSet (aCont));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> SortedSet <ELEMENTTYPE> newUnmodifiableSortedSet (@Nullable final Iterator <? extends ELEMENTTYPE> aIter)
  {
    return makeUnmodifiable (newSortedSet (aIter));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> SortedSet <ELEMENTTYPE> newUnmodifiableSortedSet (@Nullable final IIterableIterator <? extends ELEMENTTYPE> aIter)
  {
    return makeUnmodifiable (newSortedSet (aIter));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> SortedSet <ELEMENTTYPE> newUnmodifiableSortedSet (@Nullable final Enumeration <? extends ELEMENTTYPE> aEnum)
  {
    return makeUnmodifiable (newSortedSet (aEnum));
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> LinkedHashSet <ELEMENTTYPE> newOrderedSet (@Nonnegative final int nInitialCapacity)
  {
    return new LinkedHashSet <> (nInitialCapacity);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> LinkedHashSet <ELEMENTTYPE> newOrderedSet ()
  {
    return new LinkedHashSet <> ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> LinkedHashSet <DSTTYPE> newOrderedSetMapped (@Nullable final Collection <? extends SRCTYPE> aCollection,
                                                                                @Nonnull final Function <? super SRCTYPE, DSTTYPE> aMapper)
  {
    if (isEmpty (aCollection))
      return newOrderedSet (0);
    final LinkedHashSet <DSTTYPE> ret = newOrderedSet (aCollection.size ());
    for (final SRCTYPE aValue : aCollection)
      ret.add (aMapper.apply (aValue));
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> LinkedHashSet <ELEMENTTYPE> newOrderedSet (@Nullable final Collection <? extends ELEMENTTYPE> aCollection,
                                                                         @Nonnull final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (isEmpty (aCollection))
      return newOrderedSet (0);
    final LinkedHashSet <ELEMENTTYPE> ret = newOrderedSet (aCollection.size ());
    for (final ELEMENTTYPE aValue : aCollection)
      if (aFilter.test (aValue))
        ret.add (aValue);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> LinkedHashSet <ELEMENTTYPE> newOrderedSet (@Nullable final ELEMENTTYPE aValue)
  {
    final LinkedHashSet <ELEMENTTYPE> ret = newOrderedSet (1);
    ret.add (aValue);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE> LinkedHashSet <ELEMENTTYPE> newOrderedSet (@Nullable final ELEMENTTYPE... aValues)
  {
    if (ArrayHelper.isEmpty (aValues))
      return newOrderedSet (0);

    final LinkedHashSet <ELEMENTTYPE> ret = newOrderedSet (aValues.length);
    Collections.addAll (ret, aValues);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> LinkedHashSet <ELEMENTTYPE> newOrderedSet (@Nullable final Iterable <? extends ELEMENTTYPE> aCont)
  {
    final LinkedHashSet <ELEMENTTYPE> ret = newOrderedSet ();
    if (aCont != null)
      for (final ELEMENTTYPE aValue : aCont)
        ret.add (aValue);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> LinkedHashSet <ELEMENTTYPE> newOrderedSet (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    if (isEmpty (aCont))
      return newOrderedSet (0);

    return new LinkedHashSet <> (aCont);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> LinkedHashSet <ELEMENTTYPE> newOrderedSet (@Nonnull final Iterator <? extends ELEMENTTYPE> aIter)
  {
    final LinkedHashSet <ELEMENTTYPE> ret = newOrderedSet ();
    if (aIter != null)
      while (aIter.hasNext ())
        ret.add (aIter.next ());
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> LinkedHashSet <ELEMENTTYPE> newOrderedSet (@Nullable final IIterableIterator <? extends ELEMENTTYPE> aIter)
  {
    if (aIter == null)
      return newOrderedSet (0);
    return newOrderedSet (aIter.iterator ());
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> LinkedHashSet <ELEMENTTYPE> newOrderedSet (@Nullable final Enumeration <? extends ELEMENTTYPE> aEnum)
  {
    final LinkedHashSet <ELEMENTTYPE> ret = newOrderedSet ();
    if (aEnum != null)
      while (aEnum.hasMoreElements ())
        ret.add (aEnum.nextElement ());
    return ret;
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <ELEMENTTYPE> Set <ELEMENTTYPE> newUnmodifiableOrderedSet ()
  {
    return Collections.emptySet ();
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <ELEMENTTYPE> Set <ELEMENTTYPE> newUnmodifiableOrderedSet (@Nullable final ELEMENTTYPE aValue)
  {
    return Collections.singleton (aValue);
  }

  @Nonnull
  @ReturnsImmutableObject
  @SafeVarargs
  public static <ELEMENTTYPE> Set <ELEMENTTYPE> newUnmodifiableOrderedSet (@Nullable final ELEMENTTYPE... aValues)
  {
    return makeUnmodifiable (newOrderedSet (aValues));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <ELEMENTTYPE> Set <ELEMENTTYPE> newUnmodifiableOrderedSet (@Nonnull final Iterable <? extends ELEMENTTYPE> aCont)
  {
    return makeUnmodifiable (newOrderedSet (aCont));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <ELEMENTTYPE> Set <ELEMENTTYPE> newUnmodifiableOrderedSet (@Nonnull final Collection <? extends ELEMENTTYPE> aCont)
  {
    return makeUnmodifiable (newOrderedSet (aCont));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <ELEMENTTYPE> Set <ELEMENTTYPE> newUnmodifiableOrderedSet (@Nonnull final Iterator <? extends ELEMENTTYPE> aIter)
  {
    return makeUnmodifiable (newOrderedSet (aIter));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <ELEMENTTYPE> Set <ELEMENTTYPE> newUnmodifiableOrderedSet (@Nonnull final IIterableIterator <? extends ELEMENTTYPE> aIter)
  {
    return makeUnmodifiable (newOrderedSet (aIter));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <ELEMENTTYPE> Set <ELEMENTTYPE> newUnmodifiableOrderedSet (@Nullable final Enumeration <? extends ELEMENTTYPE> aEnum)
  {
    return makeUnmodifiable (newOrderedSet (aEnum));
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> ArrayList <ELEMENTTYPE> newListPrefilled (@Nullable final ELEMENTTYPE aValue,
                                                                        @Nonnegative final int nElements)
  {
    ValueEnforcer.isGE0 (nElements, "Elements");

    final ArrayList <ELEMENTTYPE> ret = new ArrayList <> (nElements);
    for (int i = 0; i < nElements; ++i)
      ret.add (aValue);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> ArrayList <ELEMENTTYPE> newList (@Nonnegative final int nInitialCapacity)
  {
    return new ArrayList <> (nInitialCapacity);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> ArrayList <ELEMENTTYPE> newList ()
  {
    return new ArrayList <> ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> ArrayList <DSTTYPE> newListMapped (@Nullable final Collection <? extends SRCTYPE> aCollection,
                                                                      @Nonnull final Function <? super SRCTYPE, DSTTYPE> aMapper)
  {
    if (isEmpty (aCollection))
      return newList (0);
    final ArrayList <DSTTYPE> ret = newList (aCollection.size ());
    for (final SRCTYPE aValue : aCollection)
      ret.add (aMapper.apply (aValue));
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> ArrayList <ELEMENTTYPE> newList (@Nullable final Collection <? extends ELEMENTTYPE> aCollection,
                                                               @Nonnull final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (isEmpty (aCollection))
      return newList (0);
    final ArrayList <ELEMENTTYPE> ret = newList (aCollection.size ());
    for (final ELEMENTTYPE aValue : aCollection)
      if (aFilter.test (aValue))
        ret.add (aValue);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> ArrayList <ELEMENTTYPE> newList (@Nullable final ELEMENTTYPE aValue)
  {
    final ArrayList <ELEMENTTYPE> ret = newList (1);
    ret.add (aValue);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE> ArrayList <ELEMENTTYPE> newList (@Nullable final ELEMENTTYPE... aValues)
  {
    // Don't user Arrays.asList since aIter returns an unmodifiable list!
    if (ArrayHelper.isEmpty (aValues))
      return newList (0);

    final ArrayList <ELEMENTTYPE> ret = newList (aValues.length);
    Collections.addAll (ret, aValues);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> ArrayList <DSTTYPE> newListMapped (@Nullable final SRCTYPE [] aValues,
                                                                      @Nonnull final Function <? super SRCTYPE, DSTTYPE> aMapper)
  {
    // Don't user Arrays.asList since aIter returns an unmodifiable list!
    if (ArrayHelper.isEmpty (aValues))
      return newList (0);

    final ArrayList <DSTTYPE> ret = newList (aValues.length);
    for (final SRCTYPE aValue : aValues)
      ret.add (aMapper.apply (aValue));
    return ret;
  }

  /**
   * Compared to {@link Collections#list(Enumeration)} this method is more
   * flexible in Generics parameter.
   *
   * @param <ELEMENTTYPE>
   *        Type of the elements
   * @param aEnum
   *        The enumeration to be converted
   * @return The non-<code>null</code> created {@link ArrayList}.
   * @see Collections#list(Enumeration)
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> ArrayList <ELEMENTTYPE> newList (@Nullable final Enumeration <? extends ELEMENTTYPE> aEnum)
  {
    final ArrayList <ELEMENTTYPE> ret = newList ();
    if (aEnum != null)
      while (aEnum.hasMoreElements ())
        ret.add (aEnum.nextElement ());
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> ArrayList <ELEMENTTYPE> newList (@Nullable final Iterator <? extends ELEMENTTYPE> aIter)
  {
    final ArrayList <ELEMENTTYPE> ret = newList ();
    if (aIter != null)
      while (aIter.hasNext ())
        ret.add (aIter.next ());
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> ArrayList <ELEMENTTYPE> newList (@Nullable final Iterable <? extends ELEMENTTYPE> aIter)
  {
    final ArrayList <ELEMENTTYPE> ret = newList ();
    if (aIter != null)
      for (final ELEMENTTYPE aObj : aIter)
        ret.add (aObj);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE, RETTYPE> ArrayList <RETTYPE> newListMapped (@Nullable final Iterable <? extends ELEMENTTYPE> aIter,
                                                                          @Nonnull final Function <? super ELEMENTTYPE, RETTYPE> aMapper)
  {
    final ArrayList <RETTYPE> ret = newList ();
    if (aIter != null)
      for (final ELEMENTTYPE aObj : aIter)
        ret.add (aMapper.apply (aObj));
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> ArrayList <ELEMENTTYPE> newList (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    if (isEmpty (aCont))
      return newList (0);

    return new ArrayList <> (aCont);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> ArrayList <ELEMENTTYPE> newList (@Nullable final IIterableIterator <? extends ELEMENTTYPE> aIter)
  {
    if (aIter == null)
      return newList (0);
    return newList (aIter.iterator ());
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> Vector <ELEMENTTYPE> newVector (@Nonnegative final int nInitialCapacity)
  {
    return new Vector <> (nInitialCapacity);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> Vector <ELEMENTTYPE> newVector ()
  {
    return new Vector <> ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> Vector <DSTTYPE> newVectorMapped (@Nullable final Collection <? extends SRCTYPE> aCollection,
                                                                     @Nonnull final Function <? super SRCTYPE, DSTTYPE> aMapper)
  {
    if (isEmpty (aCollection))
      return newVector (0);
    final Vector <DSTTYPE> ret = newVector (aCollection.size ());
    for (final SRCTYPE aValue : aCollection)
      ret.add (aMapper.apply (aValue));
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> Vector <ELEMENTTYPE> newVector (@Nullable final Collection <? extends ELEMENTTYPE> aCollection,
                                                              @Nonnull final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (isEmpty (aCollection))
      return newVector (0);
    final Vector <ELEMENTTYPE> ret = newVector (aCollection.size ());
    for (final ELEMENTTYPE aValue : aCollection)
      if (aFilter.test (aValue))
        ret.add (aValue);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> Vector <ELEMENTTYPE> newVectorPrefilled (@Nullable final ELEMENTTYPE aValue,
                                                                       @Nonnegative final int nElements)
  {
    ValueEnforcer.isGE0 (nElements, "Elements");

    final Vector <ELEMENTTYPE> ret = newVector (nElements);
    for (int i = 0; i < nElements; ++i)
      ret.add (aValue);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> Vector <ELEMENTTYPE> newVector (@Nullable final ELEMENTTYPE aValue)
  {
    final Vector <ELEMENTTYPE> ret = newVector (1);
    ret.add (aValue);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE> Vector <ELEMENTTYPE> newVector (@Nullable final ELEMENTTYPE... aValues)
  {
    // Don't user Arrays.asVector since aIter returns an unmodifiable list!
    if (ArrayHelper.isEmpty (aValues))
      return newVector (0);

    final Vector <ELEMENTTYPE> ret = newVector (aValues.length);
    Collections.addAll (ret, aValues);
    return ret;
  }

  /**
   * Compared to {@link Collections#list(Enumeration)} this method is more
   * flexible in Generics parameter.
   *
   * @param <ELEMENTTYPE>
   *        Type of the elements
   * @param aEnum
   *        The enumeration to be converted
   * @return The non-<code>null</code> created {@link Vector}.
   * @see Collections#list(Enumeration)
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> Vector <ELEMENTTYPE> newVector (@Nullable final Enumeration <? extends ELEMENTTYPE> aEnum)
  {
    final Vector <ELEMENTTYPE> ret = newVector ();
    if (aEnum != null)
      while (aEnum.hasMoreElements ())
        ret.add (aEnum.nextElement ());
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> Vector <ELEMENTTYPE> newVector (@Nullable final Iterator <? extends ELEMENTTYPE> aIter)
  {
    final Vector <ELEMENTTYPE> ret = newVector ();
    if (aIter != null)
      while (aIter.hasNext ())
        ret.add (aIter.next ());
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> Vector <ELEMENTTYPE> newVector (@Nullable final Iterable <? extends ELEMENTTYPE> aIter)
  {
    final Vector <ELEMENTTYPE> ret = newVector ();
    if (aIter != null)
      for (final ELEMENTTYPE aObj : aIter)
        ret.add (aObj);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> Vector <ELEMENTTYPE> newVector (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    if (isEmpty (aCont))
      return newVector (0);

    return newVector (aCont);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> Vector <ELEMENTTYPE> newVector (@Nullable final IIterableIterator <? extends ELEMENTTYPE> aIter)
  {
    if (aIter == null)
      return newVector (0);
    return newVector (aIter.iterator ());
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <ELEMENTTYPE> List <ELEMENTTYPE> newUnmodifiableList ()
  {
    return Collections.emptyList ();
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <ELEMENTTYPE> List <ELEMENTTYPE> newUnmodifiableList (@Nullable final ELEMENTTYPE aValue)
  {
    return Collections.singletonList (aValue);
  }

  @Nonnull
  @ReturnsImmutableObject
  @SafeVarargs
  public static <ELEMENTTYPE> List <ELEMENTTYPE> newUnmodifiableList (@Nullable final ELEMENTTYPE... aValues)
  {
    return makeUnmodifiable (newList (aValues));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <ELEMENTTYPE> List <ELEMENTTYPE> newUnmodifiableList (@Nullable final Enumeration <? extends ELEMENTTYPE> aIter)
  {
    return makeUnmodifiable (newList (aIter));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <ELEMENTTYPE> List <ELEMENTTYPE> newUnmodifiableList (@Nullable final Iterator <? extends ELEMENTTYPE> aIter)
  {
    return makeUnmodifiable (newList (aIter));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <ELEMENTTYPE> List <ELEMENTTYPE> newUnmodifiableList (@Nullable final Iterable <? extends ELEMENTTYPE> aCont)
  {
    return makeUnmodifiable (newList (aCont));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <ELEMENTTYPE> List <ELEMENTTYPE> newUnmodifiableList (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    return makeUnmodifiable (newList (aCont));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <ELEMENTTYPE> List <ELEMENTTYPE> newUnmodifiableList (@Nullable final IIterableIterator <? extends ELEMENTTYPE> aIter)
  {
    return makeUnmodifiable (newList (aIter));
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> NonBlockingStack <ELEMENTTYPE> newStack (@Nonnegative final int nInitialCapacity)
  {
    return new NonBlockingStack <> (nInitialCapacity);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> NonBlockingStack <ELEMENTTYPE> newStack ()
  {
    return new NonBlockingStack <> ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> NonBlockingStack <DSTTYPE> newStackMapped (@Nullable final Collection <? extends SRCTYPE> aCollection,
                                                                              @Nonnull final Function <? super SRCTYPE, DSTTYPE> aMapper)
  {
    if (isEmpty (aCollection))
      return newStack (0);
    final NonBlockingStack <DSTTYPE> ret = newStack (aCollection.size ());
    for (final SRCTYPE aValue : aCollection)
      ret.push (aMapper.apply (aValue));
    return ret;
  }

  /**
   * Create a new stack with a single element.
   *
   * @param <ELEMENTTYPE>
   *        The type of elements contained in the stack.
   * @param aValue
   *        The value to push. Maybe <code>null</code>.
   * @return A non-<code>null</code> stack.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> NonBlockingStack <ELEMENTTYPE> newStack (@Nullable final ELEMENTTYPE aValue)
  {
    final NonBlockingStack <ELEMENTTYPE> ret = newStack ();
    ret.push (aValue);
    return ret;
  }

  /**
   * Create a new stack from the given array.
   *
   * @param <ELEMENTTYPE>
   *        The type of elements contained in the stack.
   * @param aValues
   *        The values that are to be pushed on the stack. The last element will
   *        be the top element on the stack. May not be <code>null</code> .
   * @return A non-<code>null</code> stack object.
   */
  @Nonnull
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE> NonBlockingStack <ELEMENTTYPE> newStack (@Nullable final ELEMENTTYPE... aValues)
  {
    return new NonBlockingStack <> (aValues);
  }

  /**
   * Create a new stack from the given collection.
   *
   * @param <ELEMENTTYPE>
   *        The type of elements contained in the stack.
   * @param aValues
   *        The values that are to be pushed on the stack. The last element will
   *        be the top element on the stack. May not be <code>null</code> .
   * @return A non-<code>null</code> stack object.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> NonBlockingStack <ELEMENTTYPE> newStack (@Nullable final Collection <? extends ELEMENTTYPE> aValues)
  {
    return new NonBlockingStack <> (aValues);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> PriorityQueue <ELEMENTTYPE> newQueue (@Nonnegative final int nInitialCapacity)
  {
    return new PriorityQueue <> (nInitialCapacity);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> PriorityQueue <ELEMENTTYPE> newQueue ()
  {
    return new PriorityQueue <> ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> PriorityQueue <DSTTYPE> newQueueMapped (@Nullable final Collection <? extends SRCTYPE> aCollection,
                                                                           @Nonnull final Function <? super SRCTYPE, DSTTYPE> aMapper)
  {
    if (isEmpty (aCollection))
      return newQueue (0);
    final PriorityQueue <DSTTYPE> ret = newQueue (aCollection.size ());
    for (final SRCTYPE aValue : aCollection)
      ret.add (aMapper.apply (aValue));
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> PriorityQueue <ELEMENTTYPE> newQueue (@Nonnull final ELEMENTTYPE aValue)
  {
    final PriorityQueue <ELEMENTTYPE> ret = newQueue (1);
    ret.add (aValue);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE> PriorityQueue <ELEMENTTYPE> newQueue (@Nullable final ELEMENTTYPE... aValues)
  {
    // Don't user Arrays.asQueue since aIter returns an unmodifiable list!
    if (ArrayHelper.isEmpty (aValues))
      return newQueue (0);

    final PriorityQueue <ELEMENTTYPE> ret = newQueue (aValues.length);
    Collections.addAll (ret, aValues);
    return ret;
  }

  /**
   * Compared to {@link Collections#list(Enumeration)} this method is more
   * flexible in Generics parameter.
   *
   * @param <ELEMENTTYPE>
   *        Type of the elements
   * @param aEnum
   *        The enumeration to be converted
   * @return The non-<code>null</code> created {@link PriorityQueue}.
   * @see Collections#list(Enumeration)
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> PriorityQueue <ELEMENTTYPE> newQueue (@Nullable final Enumeration <? extends ELEMENTTYPE> aEnum)
  {
    final PriorityQueue <ELEMENTTYPE> ret = newQueue ();
    if (aEnum != null)
      while (aEnum.hasMoreElements ())
        ret.add (aEnum.nextElement ());
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> PriorityQueue <ELEMENTTYPE> newQueue (@Nullable final Iterator <? extends ELEMENTTYPE> aIter)
  {
    final PriorityQueue <ELEMENTTYPE> ret = newQueue ();
    if (aIter != null)
      while (aIter.hasNext ())
        ret.add (aIter.next ());
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> PriorityQueue <ELEMENTTYPE> newQueue (@Nullable final Iterable <? extends ELEMENTTYPE> aIter)
  {
    final PriorityQueue <ELEMENTTYPE> ret = newQueue ();
    if (aIter != null)
      for (final ELEMENTTYPE aObj : aIter)
        ret.add (aObj);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> PriorityQueue <ELEMENTTYPE> newQueue (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    if (isEmpty (aCont))
      return newQueue (0);
    return new PriorityQueue <> (aCont);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> PriorityQueue <ELEMENTTYPE> newQueue (@Nullable final IIterableIterator <? extends ELEMENTTYPE> aIter)
  {
    if (aIter == null)
      return newQueue (0);
    return newQueue (aIter.iterator ());
  }

  /**
   * Convert the given iterator to a sorted list.
   *
   * @param <ELEMENTTYPE>
   *        The type of elements to iterate. May not be <code>null</code>.
   * @param aIter
   *        Input iterator. May be <code>null</code>.
   * @return a non-null {@link ArrayList} based on the results of
   *         {@link Collections#sort(List)}.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> List <ELEMENTTYPE> getSorted (@Nullable final IIterableIterator <? extends ELEMENTTYPE> aIter)
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
   * @return a non-null {@link ArrayList} based on the results of
   *         {@link Collections#sort(List)}.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> List <ELEMENTTYPE> getSorted (@Nullable final IIterableIterator <? extends ELEMENTTYPE> aIter,
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
   * @return a non-null {@link ArrayList} based on the results of
   *         {@link Collections#sort(List)}.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> List <ELEMENTTYPE> getSorted (@Nullable final Iterator <? extends ELEMENTTYPE> aIter)
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
   * @return a non-null {@link ArrayList} based on the results of
   *         {@link Collections#sort(List, Comparator)}.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> List <ELEMENTTYPE> getSorted (@Nullable final Iterator <? extends ELEMENTTYPE> aIter,
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
   * @return A {@link ArrayList} based on the results of
   *         {@link Collections#sort(List)}.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> List <ELEMENTTYPE> getSorted (@Nullable final Iterable <? extends ELEMENTTYPE> aCont)
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
   * @return A {@link ArrayList} based on the results of
   *         {@link Collections#sort(List, Comparator)}.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> List <ELEMENTTYPE> getSorted (@Nullable final Iterable <? extends ELEMENTTYPE> aCont,
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
   * @return A {@link ArrayList} based on the results of
   *         {@link Collections#sort(List)}.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> List <ELEMENTTYPE> getSorted (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
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
   * @return A {@link ArrayList} based on the results of
   *         {@link Collections#sort(List, Comparator)}.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> List <ELEMENTTYPE> getSorted (@Nullable final Collection <? extends ELEMENTTYPE> aCont,
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
   * @return A {@link ArrayList} based on the results of
   *         {@link Collections#sort(List)}.
   */
  @Nonnull
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> List <ELEMENTTYPE> getSorted (@Nullable final ELEMENTTYPE... aCont)
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
   * @return A {@link ArrayList} based on the results of
   *         {@link Collections#sort(List, Comparator)}.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> List <ELEMENTTYPE> getSorted (@Nullable final ELEMENTTYPE [] aCont,
                                                            @Nonnull final Comparator <? super ELEMENTTYPE> aComparator)
  {
    return getSortedInline (newList (aCont), aComparator);
  }

  @Nullable
  @ReturnsMutableObject ("design")
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> List <ELEMENTTYPE> getSortedInline (@Nullable final List <ELEMENTTYPE> aList)
  {
    if (isNotEmpty (aList))
      aList.sort (null);
    return aList;
  }

  @Nullable
  @ReturnsMutableObject ("design")
  public static <ELEMENTTYPE> List <ELEMENTTYPE> getSortedInline (@Nullable final List <ELEMENTTYPE> aList,
                                                                  @Nonnull final Comparator <? super ELEMENTTYPE> aComparator)
  {
    ValueEnforcer.notNull (aComparator, "Comparator");

    if (isNotEmpty (aList))
      aList.sort (aComparator);
    return aList;
  }

  /**
   * Get a map sorted by aIter's keys. Because no comparator is defined, the key
   * type needs to implement the {@link java.lang.Comparable} interface.
   *
   * @param <KEYTYPE>
   *        map key type
   * @param <VALUETYPE>
   *        map value type
   * @param aMap
   *        the map to sort
   * @return the sorted map or the original map, if it was empty
   */
  @Nullable
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> Map <KEYTYPE, VALUETYPE> getSortedByKey (@Nullable final Map <KEYTYPE, VALUETYPE> aMap)
  {
    if (isEmpty (aMap))
      return aMap;

    // get sorted entry list
    final List <Map.Entry <KEYTYPE, VALUETYPE>> aList = newList (aMap.entrySet ());
    aList.sort (Comparator.comparing (Map.Entry::getKey));
    return newOrderedMap (aList);
  }

  /**
   * Get a map sorted by its keys. The comparison order is defined by the passed
   * comparator object.
   *
   * @param <KEYTYPE>
   *        map key type
   * @param <VALUETYPE>
   *        map value type
   * @param aMap
   *        The map to sort. May not be <code>null</code>.
   * @param aKeyComparator
   *        The comparator to be used. May not be <code>null</code>.
   * @return the sorted map or the original map, if it was empty
   */
  @Nullable
  public static <KEYTYPE, VALUETYPE> Map <KEYTYPE, VALUETYPE> getSortedByKey (@Nullable final Map <KEYTYPE, VALUETYPE> aMap,
                                                                              @Nonnull final Comparator <? super KEYTYPE> aKeyComparator)
  {
    ValueEnforcer.notNull (aKeyComparator, "KeyComparator");

    if (isEmpty (aMap))
      return aMap;

    // get sorted Map.Entry list by Entry.getValue ()
    final List <Map.Entry <KEYTYPE, VALUETYPE>> aList = newList (aMap.entrySet ());
    aList.sort (Comparator.comparing (Map.Entry::getKey, aKeyComparator));
    return newOrderedMap (aList);
  }

  /**
   * Get a map sorted by its values. Because no comparator is defined, the value
   * type needs to implement the {@link java.lang.Comparable} interface.
   *
   * @param <KEYTYPE>
   *        map key type
   * @param <VALUETYPE>
   *        map value type
   * @param aMap
   *        The map to sort. May not be <code>null</code>.
   * @return the sorted map or the original map, if it was empty
   */
  @Nullable
  public static <KEYTYPE, VALUETYPE extends Comparable <? super VALUETYPE>> Map <KEYTYPE, VALUETYPE> getSortedByValue (@Nullable final Map <KEYTYPE, VALUETYPE> aMap)
  {
    if (isEmpty (aMap))
      return aMap;

    // get sorted entry list
    final List <Map.Entry <KEYTYPE, VALUETYPE>> aList = newList (aMap.entrySet ());
    aList.sort (Comparator.comparing (Map.Entry::getValue));
    return newOrderedMap (aList);
  }

  /**
   * Get a map sorted by aIter's values. The comparison order is defined by the
   * passed comparator object.
   *
   * @param <KEYTYPE>
   *        map key type
   * @param <VALUETYPE>
   *        map value type
   * @param aMap
   *        The map to sort. May not be <code>null</code>.
   * @param aValueComparator
   *        The comparator to be used. May not be <code>null</code>.
   * @return the sorted map or the original map, if it was empty
   */
  @Nullable
  public static <KEYTYPE, VALUETYPE> Map <KEYTYPE, VALUETYPE> getSortedByValue (@Nullable final Map <KEYTYPE, VALUETYPE> aMap,
                                                                                @Nonnull final Comparator <? super VALUETYPE> aValueComparator)
  {
    ValueEnforcer.notNull (aValueComparator, "ValueComparator");

    if (isEmpty (aMap))
      return aMap;

    // get sorted Map.Entry list by Entry.getValue ()
    final List <Map.Entry <KEYTYPE, VALUETYPE>> aList = newList (aMap.entrySet ());
    aList.sort (Comparator.comparing (Map.Entry::getValue, aValueComparator));
    return newOrderedMap (aList);
  }

  @Nullable
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> List <ELEMENTTYPE> getReverseList (@Nullable final Collection <? extends ELEMENTTYPE> aCollection)
  {
    if (isEmpty (aCollection))
      return newList (0);

    final List <ELEMENTTYPE> ret = newList (aCollection);
    Collections.reverse (ret);
    return ret;
  }

  @Nullable
  @ReturnsMutableObject ("semantics of this method")
  public static <ELEMENTTYPE> List <ELEMENTTYPE> getReverseInlineList (@Nullable final List <ELEMENTTYPE> aList)
  {
    if (aList == null)
      return null;

    Collections.reverse (aList);
    return aList;
  }

  @Nullable
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> NonBlockingStack <ELEMENTTYPE> getStackCopyWithoutTop (@Nullable final NonBlockingStack <ELEMENTTYPE> aStack)
  {
    if (isEmpty (aStack))
      return null;

    final NonBlockingStack <ELEMENTTYPE> ret = new NonBlockingStack <> (aStack);
    ret.pop ();
    return ret;
  }

  /**
   * Get a map consisting only of a set of specified keys. If an element from
   * the key set is not contained in the original map, the key is ignored.
   *
   * @param <KEY>
   *        Source map key type.
   * @param <VALUE>
   *        Source map value type.
   * @param aValues
   *        Source map to filter. May not be <code>null</code>.
   * @param aKeys
   *        The filter set to filter the entries from. May not be
   *        <code>null</code>.
   * @return A non-<code>null</code> map containing only the elements from the
   *         specified key set.
   */
  @Nullable
  @ReturnsMutableCopy
  public static <KEY, VALUE> Map <KEY, VALUE> getFilteredMap (@Nullable final Map <KEY, VALUE> aValues,
                                                              @Nullable final Collection <KEY> aKeys)
  {
    if (isEmpty (aValues) || isEmpty (aKeys))
      return null;

    final Map <KEY, VALUE> ret = newMap ();
    for (final KEY aKey : aKeys)
      if (aValues.containsKey (aKey))
        ret.put (aKey, aValues.get (aKey));
    return ret;
  }

  /**
   * Get the first element of the passed list.
   *
   * @param <ELEMENTTYPE>
   *        List element type
   * @param aList
   *        The list. May be <code>null</code>.
   * @return <code>null</code> if the list is <code>null</code> or empty, the
   *         first element otherwise.
   */
  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE getFirstElement (@Nullable final List <ELEMENTTYPE> aList)
  {
    return isEmpty (aList) ? null : aList.get (0);
  }

  /**
   * Get the first element of the passed sorted set.
   *
   * @param <ELEMENTTYPE>
   *        Set element type
   * @param aSortedSet
   *        The sorted set. May be <code>null</code>.
   * @return <code>null</code> if the list is <code>null</code> or empty, the
   *         first element otherwise.
   */
  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE getFirstElement (@Nullable final SortedSet <ELEMENTTYPE> aSortedSet)
  {
    return isEmpty (aSortedSet) ? null : aSortedSet.first ();
  }

  /**
   * Get the first element of the passed collection.
   *
   * @param <ELEMENTTYPE>
   *        Collection element type
   * @param aCollection
   *        The collection. May be <code>null</code>.
   * @return <code>null</code> if the collection is <code>null</code> or empty,
   *         the first element otherwise.
   */
  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE getFirstElement (@Nullable final Collection <ELEMENTTYPE> aCollection)
  {
    return isEmpty (aCollection) ? null : aCollection.iterator ().next ();
  }

  /**
   * Get the first element of the passed iterable.
   *
   * @param <ELEMENTTYPE>
   *        Iterable element type
   * @param aIterable
   *        The iterable. May be <code>null</code>.
   * @return <code>null</code> if the iterable is <code>null</code> or empty,
   *         the first element otherwise.
   */
  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE getFirstElement (@Nullable final Iterable <ELEMENTTYPE> aIterable)
  {
    if (aIterable == null)
      return null;
    final Iterator <ELEMENTTYPE> it = aIterable.iterator ();
    return it.hasNext () ? it.next () : null;
  }

  /**
   * Get the first element of the passed map.
   *
   * @param <KEYTYPE>
   *        Map key type
   * @param <VALUETYPE>
   *        Map value type
   * @param aMap
   *        The map. May be <code>null</code>.
   * @return <code>null</code> if the map is <code>null</code> or empty, the
   *         first element otherwise.
   */
  @Nullable
  public static <KEYTYPE, VALUETYPE> Map.Entry <KEYTYPE, VALUETYPE> getFirstElement (@Nullable final Map <KEYTYPE, VALUETYPE> aMap)
  {
    return isEmpty (aMap) ? null : aMap.entrySet ().iterator ().next ();
  }

  /**
   * Get the first key of the passed map.
   *
   * @param <KEYTYPE>
   *        Map key type
   * @param <VALUETYPE>
   *        Map value type
   * @param aMap
   *        The map. May be <code>null</code>.
   * @return <code>null</code> if the map is <code>null</code> or empty, the
   *         first key otherwise.
   */
  @Nullable
  public static <KEYTYPE, VALUETYPE> KEYTYPE getFirstKey (@Nullable final Map <KEYTYPE, VALUETYPE> aMap)
  {
    return isEmpty (aMap) ? null : aMap.keySet ().iterator ().next ();
  }

  /**
   * Get the first key of the passed sorted map.
   *
   * @param <KEYTYPE>
   *        Map key type
   * @param <VALUETYPE>
   *        Map value type
   * @param aSortedMap
   *        The sorted map. May be <code>null</code>.
   * @return <code>null</code> if the map is <code>null</code> or empty, the
   *         first key otherwise.
   */
  @Nullable
  public static <KEYTYPE, VALUETYPE> KEYTYPE getFirstKey (@Nullable final SortedMap <KEYTYPE, VALUETYPE> aSortedMap)
  {
    return isEmpty (aSortedMap) ? null : aSortedMap.firstKey ();
  }

  /**
   * Get the first value of the passed map.
   *
   * @param <KEYTYPE>
   *        Map key type
   * @param <VALUETYPE>
   *        Map value type
   * @param aMap
   *        The map. May be <code>null</code>.
   * @return <code>null</code> if the map is <code>null</code> or empty, the
   *         first value otherwise.
   */
  @Nullable
  public static <KEYTYPE, VALUETYPE> VALUETYPE getFirstValue (@Nullable final Map <KEYTYPE, VALUETYPE> aMap)
  {
    return isEmpty (aMap) ? null : aMap.values ().iterator ().next ();
  }

  /**
   * Get the first value of the passed map.
   *
   * @param <KEYTYPE>
   *        Map key type
   * @param <VALUETYPE>
   *        Map value type
   * @param aSortedMap
   *        The map. May be <code>null</code>.
   * @return <code>null</code> if the map is <code>null</code> or empty, the
   *         first value otherwise.
   */
  @Nullable
  public static <KEYTYPE, VALUETYPE> VALUETYPE getFirstValue (@Nullable final SortedMap <KEYTYPE, VALUETYPE> aSortedMap)
  {
    final KEYTYPE aKey = getFirstKey (aSortedMap);
    return aKey == null ? null : aSortedMap.get (aKey);
  }

  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE removeFirstElement (@Nullable final List <ELEMENTTYPE> aList)
  {
    return isEmpty (aList) ? null : aList.remove (0);
  }

  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE getLastElement (@Nullable final List <ELEMENTTYPE> aList)
  {
    final int nSize = getSize (aList);
    return nSize == 0 ? null : aList.get (nSize - 1);
  }

  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE getLastElement (@Nullable final SortedSet <ELEMENTTYPE> aSortedSet)
  {
    return isEmpty (aSortedSet) ? null : aSortedSet.last ();
  }

  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE getLastElement (@Nullable final Collection <ELEMENTTYPE> aCollection)
  {
    if (isEmpty (aCollection))
      return null;

    // Slow but shouldn't matter
    ELEMENTTYPE aLast = null;
    for (final ELEMENTTYPE aElement : aCollection)
      aLast = aElement;
    return aLast;
  }

  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE getLastElement (@Nullable final Iterable <ELEMENTTYPE> aIterable)
  {
    if (aIterable == null)
      return null;

    // Slow but shouldn't matter
    ELEMENTTYPE aLast = null;
    for (final ELEMENTTYPE aElement : aIterable)
      aLast = aElement;
    return aLast;
  }

  /**
   * Remove the element at the specified index from the passed list. This works
   * if the list is not <code>null</code> and the index is &ge; 0 and &lt;
   * <code>list.size()</code>
   *
   * @param aList
   *        The list to remove an element from. May be <code>null</code>.
   * @param nIndex
   *        The index to be removed. May be arbitrary.
   * @return {@link EChange#CHANGED} if removal was successful
   * @see #removeAndReturnElementAtIndex(List, int)
   */
  @Nonnull
  public static EChange removeElementAtIndex (@Nullable final List <?> aList, final int nIndex)
  {
    if (aList == null || nIndex < 0 || nIndex >= aList.size ())
      return EChange.UNCHANGED;
    aList.remove (nIndex);
    return EChange.CHANGED;
  }

  /**
   * Remove the element at the specified index from the passed list. This works
   * if the list is not <code>null</code> and the index is &ge; 0 and &lt;
   * <code>list.size()</code>
   *
   * @param <ELEMENTTYPE>
   *        List element type
   * @param aList
   *        The list to remove an element from. May be <code>null</code>.
   * @param nIndex
   *        The index to be removed. May be arbitrary.
   * @return <code>null</code> if removal failed or the removed element. Note:
   *         the removed element may also be <code>null</code> so it may be
   *         tricky to determine if removal succeeded or not!
   * @see #removeElementAtIndex(List, int)
   */
  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE removeAndReturnElementAtIndex (@Nullable final List <ELEMENTTYPE> aList,
                                                                         final int nIndex)
  {
    if (aList == null || nIndex < 0 || nIndex >= aList.size ())
      return null;
    return aList.remove (nIndex);
  }

  /**
   * Get the last key of the passed sorted map.
   *
   * @param <KEYTYPE>
   *        Map key type
   * @param <VALUETYPE>
   *        Map value type
   * @param aSortedMap
   *        The sorted map. May be <code>null</code>.
   * @return <code>null</code> if the map is <code>null</code> or empty, the
   *         last key otherwise.
   */
  @Nullable
  public static <KEYTYPE, VALUETYPE> KEYTYPE getLastKey (@Nullable final SortedMap <KEYTYPE, VALUETYPE> aSortedMap)
  {
    return isEmpty (aSortedMap) ? null : aSortedMap.lastKey ();
  }

  /**
   * Get the last value of the passed map.
   *
   * @param <KEYTYPE>
   *        Map key type
   * @param <VALUETYPE>
   *        Map value type
   * @param aSortedMap
   *        The map. May be <code>null</code>.
   * @return <code>null</code> if the map is <code>null</code> or empty, the
   *         last value otherwise.
   */
  @Nullable
  public static <KEYTYPE, VALUETYPE> VALUETYPE getLastValue (@Nullable final SortedMap <KEYTYPE, VALUETYPE> aSortedMap)
  {
    final KEYTYPE aKey = getLastKey (aSortedMap);
    return aKey == null ? null : aSortedMap.get (aKey);
  }

  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE removeLastElement (@Nullable final List <ELEMENTTYPE> aList)
  {
    final int nSize = getSize (aList);
    return nSize == 0 ? null : aList.remove (nSize - 1);
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
   * Retrieve the size of the passed {@link Collection}. This method handles
   * <code>null</code> containers.
   *
   * @param aCollection
   *        Object to check. May be <code>null</code>.
   * @return The size of the object or 0 if the passed parameter is
   *         <code>null</code>.
   */
  @Nonnegative
  public static int getSize (@Nullable final Collection <?> aCollection)
  {
    return aCollection == null ? 0 : aCollection.size ();
  }

  /**
   * Retrieve the size of the passed {@link Map}. This method handles
   * <code>null</code> containers.
   *
   * @param aMap
   *        Object to check. May be <code>null</code>.
   * @return The size of the object or 0 if the passed parameter is
   *         <code>null</code>.
   */
  @Nonnegative
  public static int getSize (@Nullable final Map <?, ?> aMap)
  {
    return aMap == null ? 0 : aMap.size ();
  }

  /**
   * Retrieve the size of the passed {@link Iterable}.
   *
   * @param aIterable
   *        Iterator to check. May be <code>null</code>.
   * @return The number objects or 0 if the passed parameter is
   *         <code>null</code>.
   */
  @Nonnegative
  public static int getSize (@Nullable final Iterable <?> aIterable)
  {
    return aIterable == null ? 0 : IteratorHelper.getSize (aIterable.iterator ());
  }

  @Nullable
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> List <ELEMENTTYPE> getConcatenatedList (@Nullable final Collection <? extends ELEMENTTYPE> aCollection1,
                                                                      @Nullable final Collection <? extends ELEMENTTYPE> aCollection2)
  {
    final int nSize1 = getSize (aCollection1);
    if (nSize1 == 0)
      return newList (aCollection2);

    final int nSize2 = getSize (aCollection2);
    if (nSize2 == 0)
      return newList (aCollection1);

    final List <ELEMENTTYPE> ret = newList (nSize1 + nSize2);
    ret.addAll (aCollection1);
    ret.addAll (aCollection2);
    return ret;
  }

  @Nullable
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE> List <ELEMENTTYPE> getConcatenatedList (@Nullable final Collection <? extends ELEMENTTYPE> aCont1,
                                                                      @Nullable final ELEMENTTYPE... aCont2)
  {
    final int nSize1 = getSize (aCont1);
    if (nSize1 == 0)
      return newList (aCont2);

    final int nSize2 = ArrayHelper.getSize (aCont2);
    if (nSize2 == 0)
      return newList (aCont1);

    final List <ELEMENTTYPE> ret = newList (nSize1 + nSize2);
    ret.addAll (aCont1);
    Collections.addAll (ret, aCont2);
    return ret;
  }

  @Nullable
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> List <ELEMENTTYPE> getConcatenatedList (@Nullable final ELEMENTTYPE [] aCont1,
                                                                      @Nullable final Collection <? extends ELEMENTTYPE> aCont2)
  {
    final int nSize1 = ArrayHelper.getSize (aCont1);
    if (nSize1 == 0)
      return newList (aCont2);

    final int nSize2 = getSize (aCont2);
    if (nSize2 == 0)
      return newList (aCont1);

    final List <ELEMENTTYPE> ret = newList (nSize1 + nSize2);
    Collections.addAll (ret, aCont1);
    ret.addAll (aCont2);
    return ret;
  }

  @Nullable
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> Set <ELEMENTTYPE> getConcatenatedSet (@Nullable final Collection <? extends ELEMENTTYPE> aCont1,
                                                                    @Nullable final Collection <? extends ELEMENTTYPE> aCont2)
  {
    final int nSize1 = getSize (aCont1);
    if (nSize1 == 0)
      return newSet (aCont2);

    final int nSize2 = getSize (aCont2);
    if (nSize2 == 0)
      return newSet (aCont1);

    final Set <ELEMENTTYPE> ret = newSet (nSize1 + nSize2);
    ret.addAll (aCont1);
    ret.addAll (aCont2);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE> Set <ELEMENTTYPE> getConcatenatedSet (@Nullable final Collection <? extends ELEMENTTYPE> aCont1,
                                                                    @Nullable final ELEMENTTYPE... aCont2)
  {
    final int nSize1 = getSize (aCont1);
    if (nSize1 == 0)
      return newSet (aCont2);

    final int nSize2 = ArrayHelper.getSize (aCont2);
    if (nSize2 == 0)
      return newSet (aCont1);

    final Set <ELEMENTTYPE> ret = newSet (nSize1 + nSize2);
    ret.addAll (aCont1);
    Collections.addAll (ret, aCont2);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> Set <ELEMENTTYPE> getConcatenatedSet (@Nullable final ELEMENTTYPE [] aCont1,
                                                                    @Nullable final Collection <? extends ELEMENTTYPE> aCont2)
  {
    final int nSize1 = ArrayHelper.getSize (aCont1);
    if (nSize1 == 0)
      return newSet (aCont2);

    final int nSize2 = getSize (aCont2);
    if (nSize2 == 0)
      return newSet (aCont1);

    final Set <ELEMENTTYPE> ret = newSet (nSize1 + nSize2);
    Collections.addAll (ret, aCont1);
    ret.addAll (aCont2);
    return ret;
  }

  @Nonnull
  @ReturnsMutableObject ("design")
  @SafeVarargs
  public static <ELEMENTTYPE, COLLTYPE extends Collection <? super ELEMENTTYPE>> COLLTYPE getConcatenatedInline (@Nonnull final COLLTYPE aCont,
                                                                                                                 @Nullable final ELEMENTTYPE... aElementsToAdd)
  {
    ValueEnforcer.notNull (aCont, "Container");

    if (aElementsToAdd != null)
      Collections.addAll (aCont, aElementsToAdd);
    return aCont;
  }

  @Nonnull
  @ReturnsMutableObject ("design")
  public static <ELEMENTTYPE, COLLTYPE extends Collection <? super ELEMENTTYPE>> COLLTYPE getConcatenatedInline (@Nonnull final COLLTYPE aCont,
                                                                                                                 @Nullable final Collection <? extends ELEMENTTYPE> aElementsToAdd)
  {
    ValueEnforcer.notNull (aCont, "Container");

    if (aElementsToAdd != null)
      aCont.addAll (aElementsToAdd);
    return aCont;
  }

  /**
   * Create a map that contains the combination of the other 2 maps. Both maps
   * need to have the same key and value type.
   *
   * @param <KEY>
   *        The map key type.
   * @param <VALUE>
   *        The map value type.
   * @param aMap1
   *        The first map. May be <code>null</code>.
   * @param aMap2
   *        The second map. May be <code>null</code>.
   * @return Never <code>null</code> and always a new object. If both parameters
   *         are not <code>null</code> a new map is created, initially
   *         containing the entries from the first parameter, afterwards
   *         extended by the parameters of the second map potentially
   *         overwriting elements from the first map.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <KEY, VALUE> Map <KEY, VALUE> getCombinedMap (@Nullable final Map <KEY, VALUE> aMap1,
                                                              @Nullable final Map <KEY, VALUE> aMap2)
  {
    if (isEmpty (aMap1))
      return newMap (aMap2);
    if (isEmpty (aMap2))
      return newMap (aMap1);

    // create and fill result map
    final Map <KEY, VALUE> ret = new HashMap <> (aMap1);
    ret.putAll (aMap2);
    return ret;
  }

  @Nullable
  @ReturnsMutableCopy
  public static List <?> newObjectListFromArray (@Nullable final Object aValue, @Nonnull final Class <?> aComponentType)
  {
    if (aValue == null)
      return null;

    if (aComponentType == boolean.class)
    {
      // get as List<Boolean>
      return newList ((boolean []) aValue);
    }
    if (aComponentType == byte.class)
    {
      // get as List<Byte>
      return newList ((byte []) aValue);
    }
    if (aComponentType == char.class)
    {
      // get as List<Character>
      return newList ((char []) aValue);
    }
    if (aComponentType == double.class)
    {
      // get as List<Double>
      return newList ((double []) aValue);
    }
    if (aComponentType == float.class)
    {
      // get as List<Float>
      return newList ((float []) aValue);
    }
    if (aComponentType == int.class)
    {
      // get as List<Integer>
      return newList ((int []) aValue);
    }
    if (aComponentType == long.class)
    {
      // get as List<Long>
      return newList ((long []) aValue);
    }
    if (aComponentType == short.class)
    {
      // get as List<Short>
      return newList ((short []) aValue);
    }

    // the rest
    final Object [] aArray = (Object []) aValue;
    if (ArrayHelper.isEmpty (aArray))
      return null;

    final List <Object> ret = new ArrayList <Object> (aArray.length);
    Collections.addAll (ret, aArray);
    return ret;
  }

  /**
   * Gets a sublist excerpt of the passed list.
   *
   * @param <ELEMENTTYPE>
   *        Type of elements in list
   * @param aCont
   *        The backing list. May not be <code>null</code>.
   * @param nStartIndex
   *        The start index to use. Needs to be &ge; 0.
   * @param nSectionLength
   *        the length of the desired subset. If list is shorter than that,
   *        aIter will return a shorter section
   * @return The specified section of the passed list, or a shorter list if
   *         nStartIndex + nSectionLength is an invalid index. Never
   *         <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> List <ELEMENTTYPE> getSubList (@Nullable final List <ELEMENTTYPE> aCont,
                                                             @Nonnegative final int nStartIndex,
                                                             @Nonnegative final int nSectionLength)
  {
    ValueEnforcer.isGE0 (nStartIndex, "StartIndex");
    ValueEnforcer.isGE0 (nSectionLength, "SectionLength");

    final int nSize = getSize (aCont);
    if (nSize == 0 || nStartIndex >= nSize)
      return newList (0);

    int nEndIndex = nStartIndex + nSectionLength;
    if (nEndIndex > nSize)
      nEndIndex = nSize;

    // Create a copy of the list because "subList" only returns a view of the
    // original list!
    return newList (aCont.subList (nStartIndex, nEndIndex));
  }

  /**
   * Get a map where keys and values are exchanged.
   *
   * @param <KEYTYPE>
   *        Original key type.
   * @param <VALUETYPE>
   *        Original value type.
   * @param aMap
   *        The input map to convert. May not be <code>null</code>.
   * @return The swapped hash map (unsorted!)
   */
  @Nullable
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> Map <VALUETYPE, KEYTYPE> getSwappedKeyValues (@Nullable final Map <KEYTYPE, VALUETYPE> aMap)
  {
    if (isEmpty (aMap))
      return null;

    final Map <VALUETYPE, KEYTYPE> ret = newMap (aMap.size ());
    for (final Map.Entry <KEYTYPE, VALUETYPE> aEntry : aMap.entrySet ())
      ret.put (aEntry.getValue (), aEntry.getKey ());
    return ret;
  }

  /**
   * Get a map where the lookup (1K..nV) has been reversed to (1V..nK)
   *
   * @param <KEYTYPE>
   *        Original key type
   * @param <VALUETYPE>
   *        Original value type
   * @param aMap
   *        The input map to convert. May not be <code>null</code>
   * @return A swapped {@link IMultiMapSetBased}
   */
  @Nullable
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> IMultiMapSetBased <VALUETYPE, KEYTYPE> getReverseLookupSet (@Nullable final IMultiMap <KEYTYPE, VALUETYPE, ? extends Collection <VALUETYPE>> aMap)
  {
    if (isEmpty (aMap))
      return null;

    final IMultiMapSetBased <VALUETYPE, KEYTYPE> ret = new MultiHashMapHashSetBased <VALUETYPE, KEYTYPE> ();
    for (final Map.Entry <KEYTYPE, ? extends Collection <VALUETYPE>> aEntry : aMap.entrySet ())
      for (final VALUETYPE aValue : aEntry.getValue ())
        ret.putSingle (aValue, aEntry.getKey ());
    return ret;
  }

  /**
   * Get a map where the lookup (1K..nV) has been reversed to (1V..nK)
   *
   * @param <KEYTYPE>
   *        Original key type
   * @param <VALUETYPE>
   *        Original value type
   * @param aMap
   *        The input map to convert. May not be <code>null</code>
   * @return A swapped {@link HashMap}
   */
  @Nullable
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> IMultiMapSetBased <VALUETYPE, KEYTYPE> getReverseLookup (@Nullable final IMultiMapSetBased <KEYTYPE, VALUETYPE> aMap)
  {
    if (isEmpty (aMap))
      return null;

    final IMultiMapSetBased <VALUETYPE, KEYTYPE> aRet = new MultiHashMapHashSetBased <VALUETYPE, KEYTYPE> ();
    for (final Map.Entry <KEYTYPE, Set <VALUETYPE>> aEntry : aMap.entrySet ())
      for (final VALUETYPE aValue : aEntry.getValue ())
        aRet.putSingle (aValue, aEntry.getKey ());
    return aRet;
  }

  /**
   * Safe list element accessor method.
   *
   * @param <ELEMENTTYPE>
   *        The type of elements on the list.
   * @param aList
   *        The list to extract from. May be <code>null</code>.
   * @param nIndex
   *        The index to access. Should be &ge; 0.
   * @return <code>null</code> if the element cannot be accessed.
   */
  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE getSafe (@Nullable final List <ELEMENTTYPE> aList, final int nIndex)
  {
    return getSafe (aList, nIndex, null);
  }

  /**
   * Safe list element accessor method.
   *
   * @param <ELEMENTTYPE>
   *        The type of elements on the list.
   * @param aList
   *        The list to extract from. May be <code>null</code>.
   * @param nIndex
   *        The index to access. Should be &ge; 0.
   * @param aDefault
   *        The value to be returned, if the index is out of bounds.
   * @return The default parameter if the element cannot be accessed.
   */
  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE getSafe (@Nullable final List <ELEMENTTYPE> aList,
                                                   final int nIndex,
                                                   @Nullable final ELEMENTTYPE aDefault)
  {
    return aList != null && nIndex >= 0 && nIndex < aList.size () ? aList.get (nIndex) : aDefault;
  }

  @SuppressWarnings ("null")
  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE findFirst (@Nullable final Iterable <? extends ELEMENTTYPE> aCollection,
                                                     @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    return findFirst (aCollection, aFilter, (ELEMENTTYPE) null);
  }

  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE findFirst (@Nullable final Iterable <? extends ELEMENTTYPE> aCollection,
                                                     @Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                                     @Nullable final ELEMENTTYPE aDefault)
  {
    if (aFilter == null)
      return getFirstElement (aCollection);

    if (isNotEmpty (aCollection))
      for (final ELEMENTTYPE aElement : aCollection)
        if (aFilter.test (aElement))
          return aElement;

    return aDefault;
  }

  @SuppressWarnings ("null")
  @Nullable
  public static <ELEMENTTYPE, RETTYPE> RETTYPE findFirst (@Nullable final Iterable <? extends ELEMENTTYPE> aCollection,
                                                          @Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                                          @Nonnull final Function <? super ELEMENTTYPE, RETTYPE> aMapper)
  {
    return findFirst (aCollection, aFilter, aMapper, (RETTYPE) null);
  }

  @Nullable
  public static <ELEMENTTYPE, RETTYPE> RETTYPE findFirst (@Nullable final Iterable <? extends ELEMENTTYPE> aCollection,
                                                          @Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                                          @Nonnull final Function <? super ELEMENTTYPE, RETTYPE> aMapper,
                                                          @Nullable final RETTYPE aDefault)
  {
    ValueEnforcer.notNull (aMapper, "Mapper");

    if (isNotEmpty (aCollection))
    {
      if (aFilter == null)
        return aMapper.apply (getFirstElement (aCollection));

      for (final ELEMENTTYPE aElement : aCollection)
        if (aFilter.test (aElement))
          return aMapper.apply (aElement);
    }

    return aDefault;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> List <ELEMENTTYPE> getAll (@Nullable final Iterable <? extends ELEMENTTYPE> aCollection,
                                                         @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    final List <ELEMENTTYPE> ret = new ArrayList <> ();
    findAll (aCollection, aFilter, ret);
    return ret;
  }

  public static <ELEMENTTYPE> void findAll (@Nullable final Iterable <? extends ELEMENTTYPE> aSrc,
                                            @Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                            @Nonnull final Collection <? super ELEMENTTYPE> aDst)
  {
    ValueEnforcer.notNull (aDst, "Dst");

    if (isNotEmpty (aSrc))
      for (final ELEMENTTYPE aElement : aSrc)
        if (aFilter == null || aFilter.test (aElement))
          aDst.add (aElement);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE, RETTYPE> List <RETTYPE> getAllMapped (@Nullable final Iterable <? extends ELEMENTTYPE> aCollection,
                                                                    @Nonnull final Function <? super ELEMENTTYPE, RETTYPE> aMapper)
  {
    final List <RETTYPE> ret = new ArrayList <> ();
    findAllMapped (aCollection, aMapper, ret);
    return ret;
  }

  public static <SRCTYPE, DSTTYPE> void findAllMapped (@Nullable final Iterable <? extends SRCTYPE> aSrc,
                                                       @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aMapper,
                                                       @Nonnull final Collection <? super DSTTYPE> aDst)
  {
    ValueEnforcer.notNull (aMapper, "Mapper");
    ValueEnforcer.notNull (aDst, "Dst");

    if (isNotEmpty (aSrc))
      for (final SRCTYPE aElement : aSrc)
        aDst.add (aMapper.apply (aElement));
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE, RETTYPE> List <RETTYPE> getAll (@Nullable final Iterable <? extends ELEMENTTYPE> aCollection,
                                                              @Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                                              @Nonnull final Function <? super ELEMENTTYPE, RETTYPE> aMapper)
  {
    final List <RETTYPE> ret = new ArrayList <> ();
    findAll (aCollection, aFilter, aMapper, ret);
    return ret;
  }

  public static <SRCTYPE, DSTTYPE> void findAll (@Nullable final Iterable <? extends SRCTYPE> aSrc,
                                                 @Nullable final Predicate <? super SRCTYPE> aFilter,
                                                 @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aMapper,
                                                 @Nonnull final Collection <? super DSTTYPE> aDst)
  {
    ValueEnforcer.notNull (aMapper, "Mapper");
    ValueEnforcer.notNull (aDst, "Dst");

    if (isNotEmpty (aSrc))
      for (final SRCTYPE aElement : aSrc)
        if (aFilter == null || aFilter.test (aElement))
          aDst.add (aMapper.apply (aElement));
  }

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

  public static <ELEMENTTYPE> boolean containsAny (@Nullable final Iterable <? extends ELEMENTTYPE> aCollection,
                                                   @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (aFilter == null)
      return isNotEmpty (aCollection);

    if (isNotEmpty (aCollection))
      for (final ELEMENTTYPE aElement : aCollection)
        if (aFilter.test (aElement))
          return true;
    return false;
  }

  /**
   * Check if the passed collection contains at least one <code>null</code>
   * element.
   *
   * @param aCont
   *        The collection to check. May be <code>null</code>.
   * @return <code>true</code> only if the passed collection is neither
   *         <code>null</code> nor empty and if at least one <code>null</code>
   *         element is contained.
   */
  public static boolean containsAnyNullElement (@Nullable final Iterable <?> aCont)
  {
    return containsAny (aCont, Objects::isNull);
  }

  /**
   * Check if the passed collection contains only <code>null</code> element.
   *
   * @param aCont
   *        The collection to check. May be <code>null</code>.
   * @return <code>true</code> only if the passed collection is neither
   *         <code>null</code> nor empty and if at least one <code>null</code>
   *         element is contained.
   */
  public static boolean containsOnlyNullElements (@Nullable final Iterable <?> aCont)
  {
    if (isEmpty (aCont))
      return false;

    for (final Object aObj : aCont)
      if (aObj != null)
        return false;
    return true;
  }
}
