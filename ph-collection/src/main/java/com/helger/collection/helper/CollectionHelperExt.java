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

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.CodingStyleguideUnaware;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.array.ArrayHelper;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.collection.CollectionHelper;
import com.helger.collection.ECollectionBaseType;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.CommonsHashSet;
import com.helger.collection.commons.CommonsLinkedHashMap;
import com.helger.collection.commons.CommonsLinkedHashSet;
import com.helger.collection.commons.CommonsTreeMap;
import com.helger.collection.commons.CommonsTreeSet;
import com.helger.collection.commons.ICommonsIterableIterator;
import com.helger.collection.commons.ICommonsMap;
import com.helger.collection.commons.ICommonsSet;

/**
 * Provides various helper methods to handle collections like {@link List}, {@link Set} and
 * {@link Map}.
 *
 * @author Philip Helger
 */
@Immutable
public final class CollectionHelperExt extends CollectionHelper
{
  @PresentForCodeCoverage
  private static final CollectionHelperExt INSTANCE = new CollectionHelperExt ();

  private CollectionHelperExt ()
  {}

  /**
   * Get the passed object as a {@link CommonsArrayList} object. This is helpful in case you want to
   * compare the String array ["a", "b"] with the List&lt;String&gt; ("a", "b") If the passed object
   * is not a recognized. container type, than a new list with one element is created!
   *
   * @param aObj
   *        The object to be converted. May not be <code>null</code>.
   * @return The object as a collection. Never <code>null</code>.
   */
  @NonNull
  public static CommonsArrayList <?> getAsList (@NonNull final Object aObj)
  {
    ValueEnforcer.notNull (aObj, "Object");

    final ECollectionBaseType eType = getCollectionBaseTypeOfObject (aObj);
    if (eType == null)
    {
      // It's not a supported container -> create a new list with one element
      return createList (aObj);
    }

    switch (eType)
    {
      case COLLECTION:
        // It's already a collection
        if (aObj instanceof final CommonsArrayList <?> aList)
          return aList;
        return createList ((Collection <?>) aObj);
      case SET:
        // Convert to list
        return createList ((Set <?>) aObj);
      case MAP:
        // Use the entry set of the map as list
        return createList (((Map <?, ?>) aObj).entrySet ());
      case ARRAY:
        // Convert the array to a list
        return createList ((Object []) aObj);
      case ITERATOR:
        // Convert the iterator to a list
        return createList ((Iterator <?>) aObj);
      case ITERABLE:
        // Convert the iterable to a list
        return createList ((Iterable <?>) aObj);
      case ENUMERATION:
        // Convert the enumeration to a list
        return createList ((Enumeration <?>) aObj);
      default:
        throw new IllegalStateException ("Unhandled collection type " + eType + "!");
    }
  }

  /**
   * Get all elements that are only contained in the first contained, and not in the second. This
   * method implements <code>aCont1 - aCont2</code>.
   *
   * @param <ELEMENTTYPE>
   *        Set element type
   * @param aCollection1
   *        The first container. May be <code>null</code> or empty.
   * @param aCollection2
   *        The second container. May be <code>null</code> or empty.
   * @return The difference and never <code>null</code>. Returns an empty set, if the first
   *         container is empty. Returns a copy of the first container, if the second container is
   *         empty. Returns <code>aCont1 - aCont2</code> if both containers are non-empty.
   */
  @Nullable
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> ICommonsSet <ELEMENTTYPE> getDifference (@Nullable final Collection <? extends ELEMENTTYPE> aCollection1,
                                                                       @Nullable final Collection <? extends ELEMENTTYPE> aCollection2)
  {
    if (isEmpty (aCollection1))
      return createSet (0);
    if (isEmpty (aCollection2))
      return createSet (aCollection1);

    final ICommonsSet <ELEMENTTYPE> ret = createSet (aCollection1);
    ret.removeAll (aCollection2);
    return ret;
  }

  /**
   * Get all elements that are contained in the first AND in the second container.
   *
   * @param <ELEMENTTYPE>
   *        Collection element type
   * @param aCollection1
   *        The first container. May be <code>null</code> or empty.
   * @param aCollection2
   *        The second container. May be <code>null</code> or empty.
   * @return An empty set, if either the first or the second container are empty. Returns a set of
   *         elements that are contained in both containers, if both containers are non-empty. The
   *         return value is never <code>null</code>.
   */
  @Nullable
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> ICommonsSet <ELEMENTTYPE> getIntersected (@Nullable final Collection <? extends ELEMENTTYPE> aCollection1,
                                                                        @Nullable final Collection <? extends ELEMENTTYPE> aCollection2)
  {
    if (isEmpty (aCollection1))
      return createSet (0);
    if (isEmpty (aCollection2))
      return createSet (0);

    final ICommonsSet <ELEMENTTYPE> ret = createSet (aCollection1);
    ret.retainAll (aCollection2);
    return ret;
  }

  /**
   * Create a new empty map with the given initial capacity.
   *
   * @param <KEYTYPE>
   *        The key type.
   * @param <VALUETYPE>
   *        The value type.
   * @param nInitialCapacity
   *        The initial capacity. Must be &ge; 0.
   * @return A new empty {@link CommonsHashMap}. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsHashMap <KEYTYPE, VALUETYPE> createMap (@Nonnegative final int nInitialCapacity)
  {
    return new CommonsHashMap <> (nInitialCapacity);
  }

  /** @see #createMap(int) */
  @NonNull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsHashMap <KEYTYPE, VALUETYPE> createMap ()
  {
    return new CommonsHashMap <> ();
  }

  /**
   * Create a new map from the given map, mapping keys and values with the provided functions.
   *
   * @param <SRCKEYTYPE>
   *        The source key type.
   * @param <SRCVALUETYPE>
   *        The source value type.
   * @param <DSTKEYTYPE>
   *        The destination key type.
   * @param <DSTVALUETYPE>
   *        The destination value type.
   * @param aMap
   *        The source map. May be <code>null</code>.
   * @param aKeyMapper
   *        The key mapping function. May not be <code>null</code>.
   * @param aValueMapper
   *        The value mapping function. May not be <code>null</code>.
   * @return A new {@link CommonsHashMap}. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <SRCKEYTYPE, SRCVALUETYPE, DSTKEYTYPE, DSTVALUETYPE> CommonsHashMap <DSTKEYTYPE, DSTVALUETYPE> createMapMapped (@Nullable final Map <? extends SRCKEYTYPE, ? extends SRCVALUETYPE> aMap,
                                                                                                                                @NonNull final Function <? super SRCKEYTYPE, ? extends DSTKEYTYPE> aKeyMapper,
                                                                                                                                @NonNull final Function <? super SRCVALUETYPE, ? extends DSTVALUETYPE> aValueMapper)
  {
    if (isEmpty (aMap))
      return createMap (0);
    return new CommonsHashMap <> (aMap, aKeyMapper, aValueMapper);
  }

  /**
   * Create a new map from the given collection, mapping each element to a key and a value.
   *
   * @param <SRCTYPE>
   *        The source element type.
   * @param <DSTKEYTYPE>
   *        The destination key type.
   * @param <DSTVALUETYPE>
   *        The destination value type.
   * @param aCollection
   *        The source collection. May be <code>null</code>.
   * @param aKeyMapper
   *        The key mapping function. May not be <code>null</code>.
   * @param aValueMapper
   *        The value mapping function. May not be <code>null</code>.
   * @return A new {@link CommonsHashMap}. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTKEYTYPE, DSTVALUETYPE> CommonsHashMap <DSTKEYTYPE, DSTVALUETYPE> createMapMapped (@Nullable final Collection <? extends SRCTYPE> aCollection,
                                                                                                               @NonNull final Function <? super SRCTYPE, ? extends DSTKEYTYPE> aKeyMapper,
                                                                                                               @NonNull final Function <? super SRCTYPE, ? extends DSTVALUETYPE> aValueMapper)
  {
    if (isEmpty (aCollection))
      return createMap (0);
    return new CommonsHashMap <> (aCollection, aKeyMapper, aValueMapper);
  }

  /**
   * Create a new map from the given map, applying a filter to the entries.
   *
   * @param <KEYTYPE>
   *        The key type.
   * @param <VALUETYPE>
   *        The value type.
   * @param aMap
   *        The source map. May be <code>null</code>.
   * @param aFilter
   *        The filter to apply. May not be <code>null</code>.
   * @return A new {@link CommonsHashMap}. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsHashMap <KEYTYPE, VALUETYPE> createMap (@Nullable final Map <KEYTYPE, VALUETYPE> aMap,
                                                                                    @NonNull final Predicate <? super Map.Entry <? extends KEYTYPE, ? extends VALUETYPE>> aFilter)
  {
    if (isEmpty (aMap))
      return createMap (0);
    final CommonsHashMap <KEYTYPE, VALUETYPE> ret = createMap (aMap.size ());
    ret.putAll (aMap, aFilter);
    return ret;
  }

  /**
   * Create a new map with a single key-value pair.
   *
   * @param <KEYTYPE>
   *        The key type.
   * @param <VALUETYPE>
   *        The value type.
   * @param aKey
   *        The key. May be <code>null</code>.
   * @param aValue
   *        The value. May be <code>null</code>.
   * @return A new {@link CommonsHashMap} with one entry. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsHashMap <KEYTYPE, VALUETYPE> createMap (@Nullable final KEYTYPE aKey,
                                                                                    @Nullable final VALUETYPE aValue)
  {
    final CommonsHashMap <KEYTYPE, VALUETYPE> ret = createMap (1);
    ret.put (aKey, aValue);
    return ret;
  }

  /**
   * Create a new map from the given varargs array of alternating key-value pairs.
   *
   * @param <ELEMENTTYPE>
   *        The key and value type.
   * @param aValues
   *        The values in the order key, value, key, value, ... Must have an even number of
   *        elements. May be <code>null</code>.
   * @return A new {@link CommonsHashMap}. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE> CommonsHashMap <ELEMENTTYPE, ELEMENTTYPE> createMap (@Nullable final ELEMENTTYPE... aValues)
  {
    if (ArrayHelper.isEmpty (aValues))
      return createMap (0);

    if ((aValues.length % 2) != 0)
      throw new IllegalArgumentException ("The passed array needs an even number of elements!");

    final CommonsHashMap <ELEMENTTYPE, ELEMENTTYPE> ret = createMap (aValues.length / 2);
    for (int i = 0; i < aValues.length; i += 2)
      ret.put (aValues[i], aValues[i + 1]);
    return ret;
  }

  /**
   * Create a new map from the given key and value arrays.
   *
   * @param <KEYTYPE>
   *        The key type.
   * @param <VALUETYPE>
   *        The value type.
   * @param aKeys
   *        The key array. May be <code>null</code>.
   * @param aValues
   *        The value array. May be <code>null</code>. Must have the same length as the key array.
   * @return A new {@link CommonsHashMap}. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsHashMap <KEYTYPE, VALUETYPE> createMap (@Nullable final KEYTYPE [] aKeys,
                                                                                    @Nullable final VALUETYPE [] aValues)
  {
    final int nKeys = ArrayHelper.getSize (aKeys);
    final int nValues = ArrayHelper.getSize (aValues);

    // Check for identical size
    if (nKeys != nValues)
      throw new IllegalArgumentException ("The passed arrays have different length (" +
                                          nKeys +
                                          " keys and " +
                                          nValues +
                                          " values)!");

    // Are both empty?
    if (nKeys == 0)
      return createMap (0);

    final CommonsHashMap <KEYTYPE, VALUETYPE> ret = createMap (nKeys);
    for (int i = 0; i < aKeys.length; ++i)
      ret.put (aKeys[i], aValues[i]);
    return ret;
  }

  /** @see #createMap(Object[], Object[]) */
  @NonNull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsHashMap <KEYTYPE, VALUETYPE> createMap (@Nullable final Collection <? extends KEYTYPE> aKeys,
                                                                                    @Nullable final Collection <? extends VALUETYPE> aValues)
  {
    final int nKeys = getSize (aKeys);
    final int nValues = getSize (aValues);

    // Check for identical size
    if (nKeys != nValues)
      throw new IllegalArgumentException ("The passed arrays have different length (" +
                                          nKeys +
                                          " keys and " +
                                          nValues +
                                          " values)!");

    // Are both empty?
    if (nKeys == 0)
      return createMap (0);

    final CommonsHashMap <KEYTYPE, VALUETYPE> ret = createMap (nKeys);
    final Iterator <? extends KEYTYPE> itk = aKeys.iterator ();
    final Iterator <? extends VALUETYPE> itv = aValues.iterator ();
    while (itk.hasNext ())
      ret.put (itk.next (), itv.next ());
    return ret;
  }

  /**
   * Create a new map as a copy of the given map.
   *
   * @param <KEYTYPE>
   *        The key type.
   * @param <VALUETYPE>
   *        The value type.
   * @param aMap
   *        The source map. May be <code>null</code>.
   * @return A new {@link CommonsHashMap}. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsHashMap <KEYTYPE, VALUETYPE> createMap (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    if (isEmpty (aMap))
      return createMap (0);

    return new CommonsHashMap <> (aMap);
  }

  /**
   * Create a new map from the given array of maps, combining all entries.
   *
   * @param <KEYTYPE>
   *        The key type.
   * @param <VALUETYPE>
   *        The value type.
   * @param aMaps
   *        The source maps. May be <code>null</code>.
   * @return A new {@link CommonsHashMap}. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsHashMap <KEYTYPE, VALUETYPE> createMap (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> [] aMaps)
  {
    if (aMaps == null || aMaps.length == 0)
      return createMap (0);

    final CommonsHashMap <KEYTYPE, VALUETYPE> ret = createMap ();
    for (final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap : aMaps)
      ret.putAll (aMap);
    return ret;
  }

  /**
   * Create a new map from the given collection of map entries.
   *
   * @param <KEYTYPE>
   *        The key type.
   * @param <VALUETYPE>
   *        The value type.
   * @param aCollection
   *        The source collection of entries. May be <code>null</code>.
   * @return A new {@link CommonsHashMap}. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsHashMap <KEYTYPE, VALUETYPE> createMap (@Nullable final Collection <? extends Map.Entry <KEYTYPE, VALUETYPE>> aCollection)
  {
    if (isEmpty (aCollection))
      return createMap (0);

    final CommonsHashMap <KEYTYPE, VALUETYPE> ret = createMap (aCollection.size ());
    ret.putAll (aCollection);
    return ret;
  }

  /** @see #createMap(Collection) */
  @NonNull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsHashMap <KEYTYPE, VALUETYPE> createMap (@Nullable final Iterable <? extends Map.Entry <KEYTYPE, VALUETYPE>> aCollection)
  {
    if (isEmpty (aCollection))
      return createMap (0);

    final CommonsHashMap <KEYTYPE, VALUETYPE> ret = createMap ();
    ret.putAll (aCollection);
    return ret;
  }

  /**
   * Create a new empty ordered map with the given initial capacity.
   *
   * @param <KEYTYPE>
   *        The key type.
   * @param <VALUETYPE>
   *        The value type.
   * @param nInitialCapacity
   *        The initial capacity. Must be &ge; 0.
   * @return A new empty {@link CommonsLinkedHashMap}. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsLinkedHashMap <KEYTYPE, VALUETYPE> createOrderedMap (@Nonnegative final int nInitialCapacity)
  {
    return new CommonsLinkedHashMap <> (nInitialCapacity);
  }

  /** @see #createOrderedMap(int) */
  @NonNull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsLinkedHashMap <KEYTYPE, VALUETYPE> createOrderedMap ()
  {
    return new CommonsLinkedHashMap <> ();
  }

  /** @see #createMapMapped(Map, Function, Function) */
  @NonNull
  @ReturnsMutableCopy
  public static <SRCKEYTYPE, SRCVALUETYPE, DSTKEYTYPE, DSTVALUETYPE> CommonsLinkedHashMap <DSTKEYTYPE, DSTVALUETYPE> createOrderedMapMapped (@Nullable final Map <? extends SRCKEYTYPE, ? extends SRCVALUETYPE> aMap,
                                                                                                                                             @NonNull final Function <? super SRCKEYTYPE, ? extends DSTKEYTYPE> aKeyMapper,
                                                                                                                                             @NonNull final Function <? super SRCVALUETYPE, ? extends DSTVALUETYPE> aValueMapper)
  {
    if (isEmpty (aMap))
      return createOrderedMap (0);
    return new CommonsLinkedHashMap <> (aMap, aKeyMapper, aValueMapper);
  }

  /** @see #createMapMapped(Collection, Function, Function) */
  @NonNull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTKEYTYPE, DSTVALUETYPE> CommonsLinkedHashMap <DSTKEYTYPE, DSTVALUETYPE> createOrderedMapMapped (@Nullable final Collection <? extends SRCTYPE> aCollection,
                                                                                                                            @NonNull final Function <? super SRCTYPE, ? extends DSTKEYTYPE> aKeyMapper,
                                                                                                                            @NonNull final Function <? super SRCTYPE, ? extends DSTVALUETYPE> aValueMapper)
  {
    if (isEmpty (aCollection))
      return createOrderedMap (0);
    return new CommonsLinkedHashMap <> (aCollection, aKeyMapper, aValueMapper);
  }

  /** @see #createMap(Map, Predicate) */
  @NonNull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsLinkedHashMap <KEYTYPE, VALUETYPE> createOrderedMap (@Nullable final Map <KEYTYPE, VALUETYPE> aMap,
                                                                                                 @NonNull final Predicate <? super Map.Entry <? extends KEYTYPE, ? extends VALUETYPE>> aFilter)
  {
    if (isEmpty (aMap))
      return createOrderedMap (0);
    final CommonsLinkedHashMap <KEYTYPE, VALUETYPE> ret = createOrderedMap (aMap.size ());
    ret.putAll (aMap, aFilter);
    return ret;
  }

  /** @see #createMap(Object, Object) */
  @NonNull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsLinkedHashMap <KEYTYPE, VALUETYPE> createOrderedMap (@Nullable final KEYTYPE aKey,
                                                                                                 @Nullable final VALUETYPE aValue)
  {
    final CommonsLinkedHashMap <KEYTYPE, VALUETYPE> ret = createOrderedMap (1);
    ret.put (aKey, aValue);
    return ret;
  }

  /** @see #createMap(Object[]) */
  @NonNull
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE> CommonsLinkedHashMap <ELEMENTTYPE, ELEMENTTYPE> createOrderedMap (@Nullable final ELEMENTTYPE... aValues)
  {
    if (ArrayHelper.isEmpty (aValues))
      return createOrderedMap (0);

    if ((aValues.length % 2) != 0)
      throw new IllegalArgumentException ("The passed array needs an even number of elements!");

    final CommonsLinkedHashMap <ELEMENTTYPE, ELEMENTTYPE> ret = createOrderedMap (aValues.length / 2);
    for (int i = 0; i < aValues.length; i += 2)
      ret.put (aValues[i], aValues[i + 1]);
    return ret;
  }

  /**
   * Retrieve a map that is ordered in the way the parameter arrays are passed in. Note that key and
   * value arrays need to have the same length.
   *
   * @param <KEYTYPE>
   *        The key type.
   * @param <VALUETYPE>
   *        The value type.
   * @param aKeys
   *        The key array to use. May not be <code>null</code>.
   * @param aValues
   *        The value array to use. May not be <code>null</code>.
   * @return A {@link CommonsLinkedHashMap} containing the passed key-value entries. Never
   *         <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsLinkedHashMap <KEYTYPE, VALUETYPE> createOrderedMap (@Nullable final KEYTYPE [] aKeys,
                                                                                                 @Nullable final VALUETYPE [] aValues)
  {
    final int nKeys = ArrayHelper.getSize (aKeys);
    final int nValues = ArrayHelper.getSize (aValues);

    // Check for identical size
    if (nKeys != nValues)
      throw new IllegalArgumentException ("The passed arrays have different length (" +
                                          nKeys +
                                          " keys and " +
                                          nValues +
                                          " values)!");

    // Are both empty?
    if (nKeys == 0)
      return createOrderedMap (0);

    final CommonsLinkedHashMap <KEYTYPE, VALUETYPE> ret = createOrderedMap (nKeys);
    for (int i = 0; i < aKeys.length; ++i)
      ret.put (aKeys[i], aValues[i]);
    return ret;
  }

  /** @see #createOrderedMap(Object[], Object[]) */
  @NonNull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsLinkedHashMap <KEYTYPE, VALUETYPE> createOrderedMap (@Nullable final Collection <? extends KEYTYPE> aKeys,
                                                                                                 @Nullable final Collection <? extends VALUETYPE> aValues)
  {
    final int nKeys = getSize (aKeys);
    final int nValues = getSize (aValues);

    // Check for identical size
    if (nKeys != nValues)
      throw new IllegalArgumentException ("The passed arrays have different length (" +
                                          nKeys +
                                          " keys and " +
                                          nValues +
                                          " values)!");

    // Are both empty?
    if (nKeys == 0)
      return createOrderedMap (0);

    final CommonsLinkedHashMap <KEYTYPE, VALUETYPE> ret = createOrderedMap (nKeys);
    final Iterator <? extends KEYTYPE> itk = aKeys.iterator ();
    final Iterator <? extends VALUETYPE> itv = aValues.iterator ();
    while (itk.hasNext ())
      ret.put (itk.next (), itv.next ());
    return ret;
  }

  /** @see #createMap(Map) */
  @NonNull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsLinkedHashMap <KEYTYPE, VALUETYPE> createOrderedMap (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    if (isEmpty (aMap))
      return createOrderedMap (0);
    return new CommonsLinkedHashMap <> (aMap);
  }

  /** @see #createMap(Map[]) */
  @NonNull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsLinkedHashMap <KEYTYPE, VALUETYPE> createOrderedMap (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> [] aMaps)
  {
    if (ArrayHelper.isEmpty (aMaps))
      return createOrderedMap (0);

    final CommonsLinkedHashMap <KEYTYPE, VALUETYPE> ret = createOrderedMap ();
    for (final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap : aMaps)
      ret.putAll (aMap);
    return ret;
  }

  /** @see #createMap(Collection) */
  @NonNull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsLinkedHashMap <KEYTYPE, VALUETYPE> createOrderedMap (@Nullable final Collection <? extends Map.Entry <KEYTYPE, VALUETYPE>> aCollection)
  {
    if (isEmpty (aCollection))
      return createOrderedMap (0);

    final CommonsLinkedHashMap <KEYTYPE, VALUETYPE> ret = createOrderedMap (aCollection.size ());
    ret.putAll (aCollection);
    return ret;
  }

  /** @see #createMap(Iterable) */
  @NonNull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsLinkedHashMap <KEYTYPE, VALUETYPE> createOrderedMap (@Nullable final Iterable <? extends Map.Entry <KEYTYPE, VALUETYPE>> aCollection)
  {
    if (isEmpty (aCollection))
      return createOrderedMap (0);

    final CommonsLinkedHashMap <KEYTYPE, VALUETYPE> ret = createOrderedMap ();
    ret.putAll (aCollection);
    return ret;
  }

  /**
   * Create a new empty sorted map.
   *
   * @param <KEYTYPE>
   *        The key type.
   * @param <VALUETYPE>
   *        The value type.
   * @return A new empty {@link CommonsTreeMap}. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> CommonsTreeMap <KEYTYPE, VALUETYPE> createSortedMap ()
  {
    return new CommonsTreeMap <> (Comparator.nullsFirst (Comparator.naturalOrder ()));
  }

  /** @see #createMapMapped(Map, Function, Function) */
  @NonNull
  @ReturnsMutableCopy
  public static <SRCKEYTYPE, SRCVALUETYPE, DSTKEYTYPE extends Comparable <? super DSTKEYTYPE>, DSTVALUETYPE> CommonsTreeMap <DSTKEYTYPE, DSTVALUETYPE> createSortedMapMapped (@Nullable final Map <? extends SRCKEYTYPE, ? extends SRCVALUETYPE> aMap,
                                                                                                                                                                              @NonNull final Function <? super SRCKEYTYPE, DSTKEYTYPE> aKeyMapper,
                                                                                                                                                                              @NonNull final Function <? super SRCVALUETYPE, DSTVALUETYPE> aValueMapper)
  {
    return new CommonsTreeMap <> (aMap, aKeyMapper, aValueMapper);
  }

  /** @see #createMap(Map, Predicate) */
  @NonNull
  @ReturnsMutableCopy
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> CommonsTreeMap <KEYTYPE, VALUETYPE> createSortedMap (@Nullable final Map <KEYTYPE, VALUETYPE> aMap,
                                                                                                                               @NonNull final Predicate <? super Map.Entry <? extends KEYTYPE, ? extends VALUETYPE>> aFilter)
  {
    final CommonsTreeMap <KEYTYPE, VALUETYPE> ret = createSortedMap ();
    ret.putAll (aMap, aFilter);
    return ret;
  }

  /** @see #createMap(Object, Object) */
  @NonNull
  @ReturnsMutableCopy
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> CommonsTreeMap <KEYTYPE, VALUETYPE> createSortedMap (@Nullable final KEYTYPE aKey,
                                                                                                                               @Nullable final VALUETYPE aValue)
  {
    final CommonsTreeMap <KEYTYPE, VALUETYPE> ret = createSortedMap ();
    ret.put (aKey, aValue);
    return ret;
  }

  /** @see #createMap(Object[]) */
  @NonNull
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> CommonsTreeMap <ELEMENTTYPE, ELEMENTTYPE> createSortedMap (@Nullable final ELEMENTTYPE... aValues)
  {
    if (ArrayHelper.isEmpty (aValues))
      return createSortedMap ();

    if ((aValues.length % 2) != 0)
      throw new IllegalArgumentException ("The passed array needs an even number of elements!");

    final CommonsTreeMap <ELEMENTTYPE, ELEMENTTYPE> ret = createSortedMap ();
    for (int i = 0; i < aValues.length; i += 2)
      ret.put (aValues[i], aValues[i + 1]);
    return ret;
  }

  /** @see #createMap(Object[], Object[]) */
  @NonNull
  @ReturnsMutableCopy
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> CommonsTreeMap <KEYTYPE, VALUETYPE> createSortedMap (@Nullable final KEYTYPE [] aKeys,
                                                                                                                               @Nullable final VALUETYPE [] aValues)
  {
    // Are both empty?
    if (ArrayHelper.isEmpty (aKeys) && ArrayHelper.isEmpty (aValues))
      return createSortedMap ();

    // keys OR values may be null here
    if (ArrayHelper.getSize (aKeys) != ArrayHelper.getSize (aValues))
      throw new IllegalArgumentException ("The passed arrays have different length!");

    final CommonsTreeMap <KEYTYPE, VALUETYPE> ret = createSortedMap ();
    for (int i = 0; i < aKeys.length; ++i)
      ret.put (aKeys[i], aValues[i]);
    return ret;
  }

  /** @see #createMap(Object[], Object[]) */
  @NonNull
  @ReturnsMutableCopy
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> CommonsTreeMap <KEYTYPE, VALUETYPE> createSortedMap (@Nullable final Collection <? extends KEYTYPE> aKeys,
                                                                                                                               @Nullable final Collection <? extends VALUETYPE> aValues)
  {
    // Are both empty?
    if (isEmpty (aKeys) && isEmpty (aValues))
      return createSortedMap ();

    // keys OR values may be null here
    if (getSize (aKeys) != getSize (aValues))
      throw new IllegalArgumentException ("Number of keys is different from number of values");

    final CommonsTreeMap <KEYTYPE, VALUETYPE> ret = createSortedMap ();
    final Iterator <? extends KEYTYPE> itk = aKeys.iterator ();
    final Iterator <? extends VALUETYPE> itv = aValues.iterator ();
    while (itk.hasNext ())
      ret.put (itk.next (), itv.next ());
    return ret;
  }

  /** @see #createMap(Map) */
  @NonNull
  @ReturnsMutableCopy
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> CommonsTreeMap <KEYTYPE, VALUETYPE> createSortedMap (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    if (isEmpty (aMap))
      return createSortedMap ();

    final CommonsTreeMap <KEYTYPE, VALUETYPE> ret = createSortedMap ();
    ret.putAll (aMap);
    return ret;
  }

  /** @see #createMap(Map[]) */
  @NonNull
  @ReturnsMutableCopy
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> CommonsTreeMap <KEYTYPE, VALUETYPE> createSortedMap (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> [] aMaps)
  {
    if (aMaps == null || aMaps.length == 0)
      return createSortedMap ();

    final CommonsTreeMap <KEYTYPE, VALUETYPE> ret = createSortedMap ();
    for (final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap : aMaps)
      ret.putAll (aMap);
    return ret;
  }

  /** @see #createMap(Collection) */
  @NonNull
  @ReturnsMutableCopy
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> CommonsTreeMap <KEYTYPE, VALUETYPE> createSortedMap (@Nullable final Collection <? extends Map.Entry <KEYTYPE, VALUETYPE>> aCollection)
  {
    if (isEmpty (aCollection))
      return createSortedMap ();

    final CommonsTreeMap <KEYTYPE, VALUETYPE> ret = createSortedMap ();
    ret.putAll (aCollection);
    return ret;
  }

  /** @see #createMap(Iterable) */
  @NonNull
  @ReturnsMutableCopy
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> CommonsTreeMap <KEYTYPE, VALUETYPE> createSortedMap (@Nullable final Iterable <? extends Map.Entry <KEYTYPE, VALUETYPE>> aCollection)
  {
    if (isEmpty (aCollection))
      return createSortedMap ();

    final CommonsTreeMap <KEYTYPE, VALUETYPE> ret = createSortedMap ();
    ret.putAll (aCollection);
    return ret;
  }

  /**
   * Create a new empty set with the given initial capacity.
   *
   * @param <ELEMENTTYPE>
   *        The element type.
   * @param nInitialCapacity
   *        The initial capacity. Must be &ge; 0.
   * @return A new empty {@link CommonsHashSet}. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsHashSet <ELEMENTTYPE> createSet (@Nonnegative final int nInitialCapacity)
  {
    return new CommonsHashSet <> (nInitialCapacity);
  }

  /** @see #createSet(int) */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsHashSet <ELEMENTTYPE> createSet ()
  {
    return new CommonsHashSet <> ();
  }

  /**
   * Create a new set by mapping elements from the given collection.
   *
   * @param <SRCTYPE>
   *        The source element type.
   * @param <DSTTYPE>
   *        The destination element type.
   * @param aCollection
   *        The source collection. May be <code>null</code>.
   * @param aMapper
   *        The mapping function. May not be <code>null</code>.
   * @return A new {@link CommonsHashSet}. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> CommonsHashSet <DSTTYPE> createSetMapped (@Nullable final Collection <? extends SRCTYPE> aCollection,
                                                                             @NonNull final Function <? super SRCTYPE, DSTTYPE> aMapper)
  {
    if (isEmpty (aCollection))
      return createSet (0);
    final CommonsHashSet <DSTTYPE> ret = createSet (aCollection.size ());
    ret.addAllMapped (aCollection, aMapper);
    return ret;
  }

  /** @see #createSetMapped(Collection, Function) */
  @NonNull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> CommonsHashSet <DSTTYPE> createSetMapped (@Nullable final SRCTYPE [] aArray,
                                                                             @NonNull final Function <? super SRCTYPE, DSTTYPE> aMapper)
  {
    if (ArrayHelper.isEmpty (aArray))
      return createSet (0);

    final CommonsHashSet <DSTTYPE> ret = createSet (aArray.length);
    ret.addAllMapped (aArray, aMapper);
    return ret;
  }

  /**
   * Create a new set from the given collection, applying a filter.
   *
   * @param <ELEMENTTYPE>
   *        The element type.
   * @param aCollection
   *        The source collection. May be <code>null</code>.
   * @param aFilter
   *        The filter to apply. May not be <code>null</code>.
   * @return A new {@link CommonsHashSet}. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsHashSet <ELEMENTTYPE> createSet (@Nullable final Collection <? extends ELEMENTTYPE> aCollection,
                                                                      @NonNull final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (isEmpty (aCollection))
      return createSet (0);
    final CommonsHashSet <ELEMENTTYPE> ret = createSet (aCollection.size ());
    ret.addAll (aCollection, aFilter);
    return ret;
  }

  /**
   * Create a new set with a single element.
   *
   * @param <ELEMENTTYPE>
   *        The element type.
   * @param aValue
   *        The value to add. May be <code>null</code>.
   * @return A new {@link CommonsHashSet} with one element. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsHashSet <ELEMENTTYPE> createSet (@Nullable final ELEMENTTYPE aValue)
  {
    final CommonsHashSet <ELEMENTTYPE> ret = createSet (1);
    ret.add (aValue);
    return ret;
  }

  /**
   * Create a new set from the given varargs array.
   *
   * @param <ELEMENTTYPE>
   *        The element type.
   * @param aValues
   *        The values to add. May be <code>null</code>.
   * @return A new {@link CommonsHashSet}. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE> CommonsHashSet <ELEMENTTYPE> createSet (@Nullable final ELEMENTTYPE... aValues)
  {
    if (ArrayHelper.isEmpty (aValues))
      return createSet (0);

    final CommonsHashSet <ELEMENTTYPE> ret = createSet (aValues.length);
    ret.addAll (aValues);
    return ret;
  }

  /** @see #createSet(Object[]) */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsHashSet <ELEMENTTYPE> createSet (@Nullable final Iterable <? extends ELEMENTTYPE> aCont)
  {
    final CommonsHashSet <ELEMENTTYPE> ret = createSet ();
    ret.addAll (aCont);
    return ret;
  }

  /** @see #createSet(Object[]) */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsHashSet <ELEMENTTYPE> createSet (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    if (isEmpty (aCont))
      return createSet (0);

    return new CommonsHashSet <> (aCont);
  }

  /** @see #createSet(Object[]) */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsHashSet <ELEMENTTYPE> createSet (@Nullable final Iterator <? extends ELEMENTTYPE> aIter)
  {
    final CommonsHashSet <ELEMENTTYPE> ret = createSet ();
    ret.addAll (aIter);
    return ret;
  }

  /** @see #createSet(Object[]) */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsHashSet <ELEMENTTYPE> createSet (@Nullable final ICommonsIterableIterator <? extends ELEMENTTYPE> aIter)
  {
    if (aIter == null)
      return createSet (0);
    return createSet (aIter.iterator ());
  }

  /** @see #createSet(Object[]) */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsHashSet <ELEMENTTYPE> createSet (@Nullable final Enumeration <? extends ELEMENTTYPE> aEnum)
  {
    final CommonsHashSet <ELEMENTTYPE> ret = createSet ();
    ret.addAll (aEnum);
    return ret;
  }

  /**
   * Create a new {@link EnumSet} from the given enum values.
   *
   * @param <ELEMENTTYPE>
   *        The enum type.
   * @param aEnumClass
   *        The enum class. May not be <code>null</code>.
   * @param aValues
   *        The values to add. May be <code>null</code>.
   * @return A new {@link EnumSet}. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE extends Enum <ELEMENTTYPE>> EnumSet <ELEMENTTYPE> createEnumSet (@NonNull final Class <ELEMENTTYPE> aEnumClass,
                                                                                              @Nullable final ELEMENTTYPE... aValues)
  {
    final EnumSet <ELEMENTTYPE> ret = EnumSet.noneOf (aEnumClass);
    if (aValues != null)
      Collections.addAll (ret, aValues);
    return ret;
  }

  /** @see #createEnumSet(Class, Enum[]) */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Enum <ELEMENTTYPE>> EnumSet <ELEMENTTYPE> createEnumSet (@NonNull final Class <ELEMENTTYPE> aEnumClass,
                                                                                              @Nullable final Collection <ELEMENTTYPE> aValues)
  {
    if (isEmpty (aValues))
      return EnumSet.noneOf (aEnumClass);
    return EnumSet.copyOf (aValues);
  }

  /** @see #createEnumSet(Class, Enum[]) */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Enum <ELEMENTTYPE>> EnumSet <ELEMENTTYPE> createEnumSet (@NonNull final Class <ELEMENTTYPE> aEnumClass,
                                                                                              @Nullable final EnumSet <ELEMENTTYPE> aValues)
  {
    if (aValues == null)
      return EnumSet.noneOf (aEnumClass);
    return EnumSet.copyOf (aValues);
  }

  /**
   * Create a new empty sorted set.
   *
   * @param <ELEMENTTYPE>
   *        The element type.
   * @return A new empty {@link CommonsTreeSet}. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> CommonsTreeSet <ELEMENTTYPE> createSortedSet ()
  {
    return new CommonsTreeSet <> (Comparator.nullsFirst (Comparator.naturalOrder ()));
  }

  /** @see #createSetMapped(Collection, Function) */
  @NonNull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE extends Comparable <? super DSTTYPE>> CommonsTreeSet <DSTTYPE> createSortedSetMapped (@Nullable final Collection <? extends SRCTYPE> aCollection,
                                                                                                                        @NonNull final Function <? super SRCTYPE, DSTTYPE> aMapper)
  {
    final CommonsTreeSet <DSTTYPE> ret = createSortedSet ();
    ret.addAllMapped (aCollection, aMapper);
    return ret;
  }

  /** @see #createSetMapped(Object[], Function) */
  @NonNull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE extends Comparable <? super DSTTYPE>> CommonsTreeSet <DSTTYPE> createSortedSetMapped (@Nullable final SRCTYPE [] aArray,
                                                                                                                        @NonNull final Function <? super SRCTYPE, DSTTYPE> aMapper)
  {
    final CommonsTreeSet <DSTTYPE> ret = createSortedSet ();
    ret.addAllMapped (aArray, aMapper);
    return ret;
  }

  /** @see #createSet(Collection, Predicate) */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> CommonsTreeSet <ELEMENTTYPE> createSortedSet (@Nullable final Collection <? extends ELEMENTTYPE> aCollection,
                                                                                                                     @NonNull final Predicate <? super ELEMENTTYPE> aFilter)
  {
    final CommonsTreeSet <ELEMENTTYPE> ret = createSortedSet ();
    ret.addAll (aCollection, aFilter);
    return ret;
  }

  /** @see #createSet(Object) */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> CommonsTreeSet <ELEMENTTYPE> createSortedSet (@Nullable final ELEMENTTYPE aValue)
  {
    final CommonsTreeSet <ELEMENTTYPE> ret = createSortedSet ();
    ret.add (aValue);
    return ret;
  }

  /** @see #createSet(Object[]) */
  @NonNull
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> CommonsTreeSet <ELEMENTTYPE> createSortedSet (@Nullable final ELEMENTTYPE... aValues)
  {
    final CommonsTreeSet <ELEMENTTYPE> ret = createSortedSet ();
    ret.addAll (aValues);
    return ret;
  }

  /** @see #createSet(Object[]) */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> CommonsTreeSet <ELEMENTTYPE> createSortedSet (@Nullable final Iterable <? extends ELEMENTTYPE> aCont)
  {
    final CommonsTreeSet <ELEMENTTYPE> ret = createSortedSet ();
    ret.addAll (aCont);
    return ret;
  }

  /** @see #createSet(Object[]) */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> CommonsTreeSet <ELEMENTTYPE> createSortedSet (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    final CommonsTreeSet <ELEMENTTYPE> ret = createSortedSet ();
    if (isNotEmpty (aCont))
      ret.addAll (aCont);
    return ret;
  }

  /** @see #createSet(Object[]) */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> CommonsTreeSet <ELEMENTTYPE> createSortedSet (@Nullable final Iterator <? extends ELEMENTTYPE> aIter)
  {
    final CommonsTreeSet <ELEMENTTYPE> ret = createSortedSet ();
    ret.addAll (aIter);
    return ret;
  }

  /** @see #createSet(Object[]) */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> CommonsTreeSet <ELEMENTTYPE> createSortedSet (@Nullable final ICommonsIterableIterator <? extends ELEMENTTYPE> aIter)
  {
    if (aIter == null)
      return createSortedSet ();
    return createSortedSet (aIter.iterator ());
  }

  /** @see #createSet(Object[]) */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> CommonsTreeSet <ELEMENTTYPE> createSortedSet (@Nullable final Enumeration <? extends ELEMENTTYPE> aEnum)
  {
    final CommonsTreeSet <ELEMENTTYPE> ret = createSortedSet ();
    ret.addAll (aEnum);
    return ret;
  }

  /**
   * Create a new empty ordered set with the given initial capacity.
   *
   * @param <ELEMENTTYPE>
   *        The element type.
   * @param nInitialCapacity
   *        The initial capacity. Must be &ge; 0.
   * @return A new empty {@link CommonsLinkedHashSet}. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsLinkedHashSet <ELEMENTTYPE> createOrderedSet (@Nonnegative final int nInitialCapacity)
  {
    return new CommonsLinkedHashSet <> (nInitialCapacity);
  }

  /** @see #createOrderedSet(int) */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsLinkedHashSet <ELEMENTTYPE> createOrderedSet ()
  {
    return new CommonsLinkedHashSet <> ();
  }

  /** @see #createSetMapped(Collection, Function) */
  @NonNull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> CommonsLinkedHashSet <DSTTYPE> createOrderedSetMapped (@Nullable final Collection <? extends SRCTYPE> aCollection,
                                                                                          @NonNull final Function <? super SRCTYPE, DSTTYPE> aMapper)
  {
    if (isEmpty (aCollection))
      return createOrderedSet (0);
    final CommonsLinkedHashSet <DSTTYPE> ret = createOrderedSet (aCollection.size ());
    ret.addAllMapped (aCollection, aMapper);
    return ret;
  }

  /** @see #createSetMapped(Object[], Function) */
  @NonNull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> CommonsLinkedHashSet <DSTTYPE> createOrderedSetMapped (@Nullable final SRCTYPE [] aArray,
                                                                                          @NonNull final Function <? super SRCTYPE, DSTTYPE> aMapper)
  {
    if (ArrayHelper.isEmpty (aArray))
      return createOrderedSet (0);
    final CommonsLinkedHashSet <DSTTYPE> ret = createOrderedSet (aArray.length);
    ret.addAllMapped (aArray, aMapper);
    return ret;
  }

  /** @see #createSet(Collection, Predicate) */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsLinkedHashSet <ELEMENTTYPE> createOrderedSet (@Nullable final Collection <? extends ELEMENTTYPE> aCollection,
                                                                                   @NonNull final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (isEmpty (aCollection))
      return createOrderedSet (0);
    final CommonsLinkedHashSet <ELEMENTTYPE> ret = createOrderedSet (aCollection.size ());
    ret.addAll (aCollection, aFilter);
    return ret;
  }

  /** @see #createSet(Object) */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsLinkedHashSet <ELEMENTTYPE> createOrderedSet (@Nullable final ELEMENTTYPE aValue)
  {
    final CommonsLinkedHashSet <ELEMENTTYPE> ret = createOrderedSet (1);
    ret.add (aValue);
    return ret;
  }

  /** @see #createSet(Object[]) */
  @NonNull
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE> CommonsLinkedHashSet <ELEMENTTYPE> createOrderedSet (@Nullable final ELEMENTTYPE... aValues)
  {
    if (ArrayHelper.isEmpty (aValues))
      return createOrderedSet (0);

    final CommonsLinkedHashSet <ELEMENTTYPE> ret = createOrderedSet (aValues.length);
    Collections.addAll (ret, aValues);
    return ret;
  }

  /** @see #createSet(Object[]) */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsLinkedHashSet <ELEMENTTYPE> createOrderedSet (@Nullable final Iterable <? extends ELEMENTTYPE> aCont)
  {
    final CommonsLinkedHashSet <ELEMENTTYPE> ret = createOrderedSet ();
    ret.addAll (aCont);
    return ret;
  }

  /** @see #createSet(Object[]) */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsLinkedHashSet <ELEMENTTYPE> createOrderedSet (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    if (isEmpty (aCont))
      return createOrderedSet (0);
    return new CommonsLinkedHashSet <> (aCont);
  }

  /** @see #createSet(Object[]) */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsLinkedHashSet <ELEMENTTYPE> createOrderedSet (@NonNull final Iterator <? extends ELEMENTTYPE> aIter)
  {
    final CommonsLinkedHashSet <ELEMENTTYPE> ret = createOrderedSet ();
    ret.addAll (aIter);
    return ret;
  }

  /** @see #createSet(Object[]) */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsLinkedHashSet <ELEMENTTYPE> createOrderedSet (@Nullable final ICommonsIterableIterator <? extends ELEMENTTYPE> aIter)
  {
    if (aIter == null)
      return createOrderedSet (0);
    return createOrderedSet (aIter.iterator ());
  }

  /** @see #createSet(Object[]) */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsLinkedHashSet <ELEMENTTYPE> createOrderedSet (@Nullable final Enumeration <? extends ELEMENTTYPE> aEnum)
  {
    final CommonsLinkedHashSet <ELEMENTTYPE> ret = createOrderedSet ();
    ret.addAll (aEnum);
    return ret;
  }

  /**
   * Create a new list prefilled with the given value.
   *
   * @param <ELEMENTTYPE>
   *        The element type.
   * @param aValue
   *        The value to fill the list with. May be <code>null</code>.
   * @param nElements
   *        The number of elements. Must be &ge; 0.
   * @return A new {@link CommonsArrayList}. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> createListPrefilled (@Nullable final ELEMENTTYPE aValue,
                                                                                  @Nonnegative final int nElements)
  {
    ValueEnforcer.isGE0 (nElements, "Elements");

    final CommonsArrayList <ELEMENTTYPE> ret = new CommonsArrayList <> (nElements);
    for (int i = 0; i < nElements; ++i)
      ret.add (aValue);
    return ret;
  }

  /**
   * Create a new empty list with the given initial capacity.
   *
   * @param <ELEMENTTYPE>
   *        The element type.
   * @param nInitialCapacity
   *        The initial capacity. Must be &ge; 0.
   * @return A new empty {@link CommonsArrayList}. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> createList (@Nonnegative final int nInitialCapacity)
  {
    return new CommonsArrayList <> (nInitialCapacity);
  }

  /** @see #createList(int) */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> createList ()
  {
    return new CommonsArrayList <> ();
  }

  /**
   * Create a new list from the given collection, applying a filter.
   *
   * @param <ELEMENTTYPE>
   *        The element type.
   * @param aCollection
   *        The source collection. May be <code>null</code>.
   * @param aFilter
   *        The filter to apply. May not be <code>null</code>.
   * @return A new {@link CommonsArrayList}. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> createList (@Nullable final Collection <? extends ELEMENTTYPE> aCollection,
                                                                         @NonNull final Predicate <? super ELEMENTTYPE> aFilter)
  {
    return CommonsArrayList.createFiltered (aCollection, aFilter);
  }

  /**
   * Create a new list with a single element.
   *
   * @param <ELEMENTTYPE>
   *        The element type.
   * @param aValue
   *        The value to add. May be <code>null</code>.
   * @return A new {@link CommonsArrayList} with one element. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> createList (@Nullable final ELEMENTTYPE aValue)
  {
    final CommonsArrayList <ELEMENTTYPE> ret = createList (1);
    ret.add (aValue);
    return ret;
  }

  /**
   * Create a new list from the given varargs array.
   *
   * @param <ELEMENTTYPE>
   *        The element type.
   * @param aValues
   *        The values to add. May be <code>null</code>.
   * @return A new {@link CommonsArrayList}. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> createList (@Nullable final ELEMENTTYPE... aValues)
  {
    // Don't user Arrays.asList since aIter returns an unmodifiable list!
    if (ArrayHelper.isEmpty (aValues))
      return createList (0);

    final CommonsArrayList <ELEMENTTYPE> ret = createList (aValues.length);
    ret.addAll (aValues);
    return ret;
  }

  /**
   * Create a new list by mapping elements from the given array.
   *
   * @param <SRCTYPE>
   *        The source element type.
   * @param <DSTTYPE>
   *        The destination element type.
   * @param aValues
   *        The source array. May be <code>null</code>.
   * @param aMapper
   *        The mapping function. May not be <code>null</code>.
   * @return A new {@link CommonsArrayList}. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> CommonsArrayList <DSTTYPE> createListMapped (@Nullable final SRCTYPE [] aValues,
                                                                                @NonNull final Function <? super SRCTYPE, ? extends DSTTYPE> aMapper)
  {
    // Don't user Arrays.asList since aIter returns an unmodifiable list!
    if (ArrayHelper.isEmpty (aValues))
      return createList (0);

    final CommonsArrayList <DSTTYPE> ret = createList (aValues.length);
    ret.addAllMapped (aValues, aMapper);
    return ret;
  }

  /** @see #createListMapped(Object[], Function) */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE, DSTTYPE> CommonsArrayList <DSTTYPE> createListMapped (@Nullable final Iterable <? extends ELEMENTTYPE> aIter,
                                                                                    @NonNull final Function <? super ELEMENTTYPE, ? extends DSTTYPE> aMapper)
  {
    final CommonsArrayList <DSTTYPE> ret = createList ();
    ret.addAllMapped (aIter, aMapper);
    return ret;
  }

  /** @see #createListMapped(Object[], Function) */
  @NonNull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> CommonsArrayList <DSTTYPE> createListMapped (@Nullable final Collection <? extends SRCTYPE> aCollection,
                                                                                @NonNull final Function <? super SRCTYPE, ? extends DSTTYPE> aMapper)
  {
    if (isEmpty (aCollection))
      return createList (0);
    final CommonsArrayList <DSTTYPE> ret = createList (aCollection.size ());
    ret.addAllMapped (aCollection, aMapper);
    return ret;
  }

  /**
   * Create a new list by filtering and then mapping elements from the given iterable.
   *
   * @param <ELEMENTTYPE>
   *        The source element type.
   * @param <DSTTYPE>
   *        The destination element type.
   * @param aCollection
   *        The source iterable. May be <code>null</code>.
   * @param aFilter
   *        The filter to apply before mapping. May be <code>null</code>.
   * @param aMapper
   *        The mapping function. May not be <code>null</code>.
   * @return A new {@link CommonsArrayList}. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE, DSTTYPE> CommonsArrayList <DSTTYPE> createListMapped (@Nullable final Iterable <? extends ELEMENTTYPE> aCollection,
                                                                                    @Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                                                                    @NonNull final Function <? super ELEMENTTYPE, ? extends DSTTYPE> aMapper)
  {
    final CommonsArrayList <DSTTYPE> ret = createList ();
    ret.addAllMapped (aCollection, aFilter, aMapper);
    return ret;
  }

  /**
   * Compared to {@link Collections#list(Enumeration)} this method is more flexible in generics
   * parameter.
   *
   * @param <ELEMENTTYPE>
   *        Type of the elements
   * @param aEnum
   *        The enumeration to be converted
   * @return The non-<code>null</code> created {@link CommonsArrayList}.
   * @see Collections#list(Enumeration)
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> createList (@Nullable final Enumeration <? extends ELEMENTTYPE> aEnum)
  {
    return new CommonsArrayList <> (aEnum);
  }

  /** @see #createList(Object[]) */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> createList (@Nullable final Iterator <? extends ELEMENTTYPE> aIter)
  {
    final CommonsArrayList <ELEMENTTYPE> ret = createList ();
    ret.addAll (aIter);
    return ret;
  }

  /** @see #createList(Object[]) */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> createList (@Nullable final Iterable <? extends ELEMENTTYPE> aIter)
  {
    return new CommonsArrayList <> (aIter);
  }

  /** @see #createList(Object[]) */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> createList (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    if (isEmpty (aCont))
      return createList (0);

    return new CommonsArrayList <> (aCont);
  }

  /** @see #createList(Object[]) */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> createList (@Nullable final ICommonsIterableIterator <? extends ELEMENTTYPE> aIter)
  {
    if (aIter == null)
      return createList (0);
    return createList (aIter.iterator ());
  }

  /**
   * Get a map consisting only of a set of specified keys. If an element from the key set is not
   * contained in the original map, the key is ignored.
   *
   * @param <KEY>
   *        Source map key type.
   * @param <VALUE>
   *        Source map value type.
   * @param aValues
   *        Source map to filter. May not be <code>null</code>.
   * @param aKeys
   *        The filter set to filter the entries from. May not be <code>null</code>.
   * @return A non-<code>null</code> map containing only the elements from the specified key set.
   */
  @Nullable
  @ReturnsMutableCopy
  public static <KEY, VALUE> ICommonsMap <KEY, VALUE> getFilteredMap (@Nullable final ICommonsMap <KEY, VALUE> aValues,
                                                                      @Nullable final Collection <? extends KEY> aKeys)
  {
    if (isEmpty (aValues) || isEmpty (aKeys))
      return null;

    final ICommonsMap <KEY, VALUE> ret = createMap ();
    for (final KEY aKey : aKeys)
      if (aValues.containsKey (aKey))
        ret.put (aKey, aValues.get (aKey));
    return ret;
  }

  /**
   * Create a new list that is the concatenation of the two passed collections.
   *
   * @param <ELEMENTTYPE>
   *        The element type.
   * @param aCollection1
   *        The first collection. May be <code>null</code>.
   * @param aCollection2
   *        The second collection. May be <code>null</code>.
   * @return A new {@link CommonsArrayList} containing all elements. May be <code>null</code> if
   *         both parameters are <code>null</code>.
   */
  @Nullable
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> getConcatenatedList (@Nullable final Collection <? extends ELEMENTTYPE> aCollection1,
                                                                                  @Nullable final Collection <? extends ELEMENTTYPE> aCollection2)
  {
    final int nSize1 = getSize (aCollection1);
    if (nSize1 == 0)
      return createList (aCollection2);

    final int nSize2 = getSize (aCollection2);
    if (nSize2 == 0)
      return createList (aCollection1);

    final CommonsArrayList <ELEMENTTYPE> ret = createList (nSize1 + nSize2);
    ret.addAll (aCollection1);
    ret.addAll (aCollection2);
    return ret;
  }

  /** @see #getConcatenatedList(Collection, Collection) */
  @Nullable
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> getConcatenatedList (@Nullable final Collection <? extends ELEMENTTYPE> aCont1,
                                                                                  @Nullable final ELEMENTTYPE... aCont2)
  {
    final int nSize1 = getSize (aCont1);
    if (nSize1 == 0)
      return createList (aCont2);

    final int nSize2 = ArrayHelper.getSize (aCont2);
    if (nSize2 == 0)
      return createList (aCont1);

    final CommonsArrayList <ELEMENTTYPE> ret = createList (nSize1 + nSize2);
    ret.addAll (aCont1);
    Collections.addAll (ret, aCont2);
    return ret;
  }

  /** @see #getConcatenatedList(Collection, Collection) */
  @Nullable
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> getConcatenatedList (@Nullable final ELEMENTTYPE [] aCont1,
                                                                                  @Nullable final Collection <? extends ELEMENTTYPE> aCont2)
  {
    final int nSize1 = ArrayHelper.getSize (aCont1);
    if (nSize1 == 0)
      return createList (aCont2);

    final int nSize2 = getSize (aCont2);
    if (nSize2 == 0)
      return createList (aCont1);

    final CommonsArrayList <ELEMENTTYPE> ret = createList (nSize1 + nSize2);
    Collections.addAll (ret, aCont1);
    ret.addAll (aCont2);
    return ret;
  }

  /**
   * Create a new set that is the concatenation of the two passed collections.
   *
   * @param <ELEMENTTYPE>
   *        The element type.
   * @param aCont1
   *        The first collection. May be <code>null</code>.
   * @param aCont2
   *        The second collection. May be <code>null</code>.
   * @return A new {@link ICommonsSet} containing all elements. May be <code>null</code> if both
   *         parameters are <code>null</code>.
   */
  @Nullable
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> ICommonsSet <ELEMENTTYPE> getConcatenatedSet (@Nullable final Collection <? extends ELEMENTTYPE> aCont1,
                                                                            @Nullable final Collection <? extends ELEMENTTYPE> aCont2)
  {
    final int nSize1 = getSize (aCont1);
    if (nSize1 == 0)
      return createSet (aCont2);

    final int nSize2 = getSize (aCont2);
    if (nSize2 == 0)
      return createSet (aCont1);

    final ICommonsSet <ELEMENTTYPE> ret = createSet (nSize1 + nSize2);
    ret.addAll (aCont1);
    ret.addAll (aCont2);
    return ret;
  }

  /** @see #getConcatenatedSet(Collection, Collection) */
  @NonNull
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE> ICommonsSet <ELEMENTTYPE> getConcatenatedSet (@Nullable final Collection <? extends ELEMENTTYPE> aCont1,
                                                                            @Nullable final ELEMENTTYPE... aCont2)
  {
    final int nSize1 = getSize (aCont1);
    if (nSize1 == 0)
      return createSet (aCont2);

    final int nSize2 = ArrayHelper.getSize (aCont2);
    if (nSize2 == 0)
      return createSet (aCont1);

    final ICommonsSet <ELEMENTTYPE> ret = createSet (nSize1 + nSize2);
    ret.addAll (aCont1);
    Collections.addAll (ret, aCont2);
    return ret;
  }

  /** @see #getConcatenatedSet(Collection, Collection) */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> ICommonsSet <ELEMENTTYPE> getConcatenatedSet (@Nullable final ELEMENTTYPE [] aCont1,
                                                                            @Nullable final Collection <? extends ELEMENTTYPE> aCont2)
  {
    final int nSize1 = ArrayHelper.getSize (aCont1);
    if (nSize1 == 0)
      return createSet (aCont2);

    final int nSize2 = getSize (aCont2);
    if (nSize2 == 0)
      return createSet (aCont1);

    final ICommonsSet <ELEMENTTYPE> ret = createSet (nSize1 + nSize2);
    Collections.addAll (ret, aCont1);
    ret.addAll (aCont2);
    return ret;
  }

  /**
   * Add elements to the given collection inline and return the collection.
   *
   * @param <ELEMENTTYPE>
   *        The element type.
   * @param <COLLTYPE>
   *        The collection type.
   * @param aCont
   *        The collection to add elements to. May not be <code>null</code>.
   * @param aElementsToAdd
   *        The elements to add. May be <code>null</code>.
   * @return The passed collection. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableObject ("design")
  @SafeVarargs
  @CodingStyleguideUnaware
  public static <ELEMENTTYPE, COLLTYPE extends Collection <? super ELEMENTTYPE>> COLLTYPE getConcatenatedInline (@NonNull final COLLTYPE aCont,
                                                                                                                 @Nullable final ELEMENTTYPE... aElementsToAdd)
  {
    ValueEnforcer.notNull (aCont, "Container");

    if (aElementsToAdd != null)
      Collections.addAll (aCont, aElementsToAdd);
    return aCont;
  }

  /** @see #getConcatenatedInline(Collection, Object[]) */
  @NonNull
  @ReturnsMutableObject ("design")
  @CodingStyleguideUnaware
  public static <ELEMENTTYPE, COLLTYPE extends Collection <? super ELEMENTTYPE>> COLLTYPE getConcatenatedInline (@NonNull final COLLTYPE aCont,
                                                                                                                 @Nullable final Collection <? extends ELEMENTTYPE> aElementsToAdd)
  {
    ValueEnforcer.notNull (aCont, "Container");

    if (aElementsToAdd != null)
      aCont.addAll (aElementsToAdd);
    return aCont;
  }

  /**
   * Create a map that contains the combination of the other 2 maps. Both maps need to have the same
   * key and value type.
   *
   * @param <KEY>
   *        The map key type.
   * @param <VALUE>
   *        The map value type.
   * @param aMap1
   *        The first map. May be <code>null</code>.
   * @param aMap2
   *        The second map. May be <code>null</code>.
   * @return Never <code>null</code> and always a new object. If both parameters are not
   *         <code>null</code> a new map is created, initially containing the entries from the first
   *         parameter, afterwards extended by the parameters of the second map potentially
   *         overwriting elements from the first map.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <KEY, VALUE> ICommonsMap <KEY, VALUE> getCombinedMap (@Nullable final Map <KEY, VALUE> aMap1,
                                                                      @Nullable final Map <KEY, VALUE> aMap2)
  {
    if (isEmpty (aMap1))
      return createMap (aMap2);
    if (isEmpty (aMap2))
      return createMap (aMap1);

    // create and fill result map
    final ICommonsMap <KEY, VALUE> ret = new CommonsHashMap <> (aMap1);
    ret.putAll (aMap2);
    return ret;
  }

  /**
   * Convert an array (including primitive arrays) to a list of the corresponding wrapper type.
   *
   * @param aValue
   *        The array value. May be <code>null</code>.
   * @param aComponentType
   *        The component type of the array. May not be <code>null</code>.
   * @return A new list or <code>null</code> if the passed value is <code>null</code> or the array
   *         is empty.
   */
  @Nullable
  @ReturnsMutableCopy
  public static CommonsArrayList <?> createObjectListFromArray (@Nullable final Object aValue,
                                                                @NonNull final Class <?> aComponentType)
  {
    if (aValue == null)
      return null;

    if (aComponentType == boolean.class)
    {
      // get as CommonsList<Boolean>
      return PrimitiveCollectionHelper.createPrimitiveList ((boolean []) aValue);
    }
    if (aComponentType == byte.class)
    {
      // get as CommonsList<Byte>
      return PrimitiveCollectionHelper.createPrimitiveList ((byte []) aValue);
    }
    if (aComponentType == char.class)
    {
      // get as CommonsList<Character>
      return PrimitiveCollectionHelper.createPrimitiveList ((char []) aValue);
    }
    if (aComponentType == double.class)
    {
      // get as CommonsList<Double>
      return PrimitiveCollectionHelper.createPrimitiveList ((double []) aValue);
    }
    if (aComponentType == float.class)
    {
      // get as CommonsList<Float>
      return PrimitiveCollectionHelper.createPrimitiveList ((float []) aValue);
    }
    if (aComponentType == int.class)
    {
      // get as CommonsList<Integer>
      return PrimitiveCollectionHelper.createPrimitiveList ((int []) aValue);
    }
    if (aComponentType == long.class)
    {
      // get as CommonsList<Long>
      return PrimitiveCollectionHelper.createPrimitiveList ((long []) aValue);
    }
    if (aComponentType == short.class)
    {
      // get as CommonsList<Short>
      return PrimitiveCollectionHelper.createPrimitiveList ((short []) aValue);
    }

    // the rest
    final Object [] aArray = (Object []) aValue;
    if (ArrayHelper.isEmpty (aArray))
      return null;

    final CommonsArrayList <Object> ret = createList (aArray.length);
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
   *        the length of the desired subset. If list is shorter than that, aIter will return a
   *        shorter section
   * @return The specified section of the passed list, or a shorter list if nStartIndex +
   *         nSectionLength is an invalid index. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> getSubList (@Nullable final List <ELEMENTTYPE> aCont,
                                                                         @Nonnegative final int nStartIndex,
                                                                         @Nonnegative final int nSectionLength)
  {
    ValueEnforcer.isGE0 (nStartIndex, "StartIndex");
    ValueEnforcer.isGE0 (nSectionLength, "SectionLength");

    final int nSize = getSize (aCont);
    if (nSize == 0 || nStartIndex >= nSize)
      return createList (0);

    int nEndIndex = nStartIndex + nSectionLength;
    if (nEndIndex > nSize)
      nEndIndex = nSize;

    // Create a copy of the list because "subList" only returns a view of the
    // original list!
    return createList (aCont.subList (nStartIndex, nEndIndex));
  }
}
