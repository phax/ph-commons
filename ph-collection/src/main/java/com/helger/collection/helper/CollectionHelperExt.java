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

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

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
  @Nonnull
  public static CommonsArrayList <?> getAsList (@Nonnull final Object aObj)
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
        if (aObj instanceof final CommonsArrayList <?> aList)
          return aList;
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
      return newSet (0);
    if (isEmpty (aCollection2))
      return newSet (aCollection1);

    final ICommonsSet <ELEMENTTYPE> ret = newSet (aCollection1);
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
      return newSet (0);
    if (isEmpty (aCollection2))
      return newSet (0);

    final ICommonsSet <ELEMENTTYPE> ret = newSet (aCollection1);
    ret.retainAll (aCollection2);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsHashMap <KEYTYPE, VALUETYPE> newMap (@Nonnegative final int nInitialCapacity)
  {
    return new CommonsHashMap <> (nInitialCapacity);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsHashMap <KEYTYPE, VALUETYPE> newMap ()
  {
    return new CommonsHashMap <> ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCKEYTYPE, SRCVALUETYPE, DSTKEYTYPE, DSTVALUETYPE> CommonsHashMap <DSTKEYTYPE, DSTVALUETYPE> newMapMapped (@Nullable final Map <? extends SRCKEYTYPE, ? extends SRCVALUETYPE> aMap,
                                                                                                                             @Nonnull final Function <? super SRCKEYTYPE, ? extends DSTKEYTYPE> aKeyMapper,
                                                                                                                             @Nonnull final Function <? super SRCVALUETYPE, ? extends DSTVALUETYPE> aValueMapper)
  {
    if (isEmpty (aMap))
      return newMap (0);
    return new CommonsHashMap <> (aMap, aKeyMapper, aValueMapper);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTKEYTYPE, DSTVALUETYPE> CommonsHashMap <DSTKEYTYPE, DSTVALUETYPE> newMapMapped (@Nullable final Collection <? extends SRCTYPE> aCollection,
                                                                                                            @Nonnull final Function <? super SRCTYPE, ? extends DSTKEYTYPE> aKeyMapper,
                                                                                                            @Nonnull final Function <? super SRCTYPE, ? extends DSTVALUETYPE> aValueMapper)
  {
    if (isEmpty (aCollection))
      return newMap (0);
    return new CommonsHashMap <> (aCollection, aKeyMapper, aValueMapper);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsHashMap <KEYTYPE, VALUETYPE> newMap (@Nullable final Map <KEYTYPE, VALUETYPE> aMap,
                                                                                 @Nonnull final Predicate <? super Map.Entry <? extends KEYTYPE, ? extends VALUETYPE>> aFilter)
  {
    if (isEmpty (aMap))
      return newMap (0);
    final CommonsHashMap <KEYTYPE, VALUETYPE> ret = newMap (aMap.size ());
    ret.putAll (aMap, aFilter);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsHashMap <KEYTYPE, VALUETYPE> newMap (@Nullable final KEYTYPE aKey,
                                                                                 @Nullable final VALUETYPE aValue)
  {
    final CommonsHashMap <KEYTYPE, VALUETYPE> ret = newMap (1);
    ret.put (aKey, aValue);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE> CommonsHashMap <ELEMENTTYPE, ELEMENTTYPE> newMap (@Nullable final ELEMENTTYPE... aValues)
  {
    if (ArrayHelper.isEmpty (aValues))
      return newMap (0);

    if ((aValues.length % 2) != 0)
      throw new IllegalArgumentException ("The passed array needs an even number of elements!");

    final CommonsHashMap <ELEMENTTYPE, ELEMENTTYPE> ret = newMap (aValues.length / 2);
    for (int i = 0; i < aValues.length; i += 2)
      ret.put (aValues[i], aValues[i + 1]);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsHashMap <KEYTYPE, VALUETYPE> newMap (@Nullable final KEYTYPE [] aKeys,
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
      return newMap (0);

    final CommonsHashMap <KEYTYPE, VALUETYPE> ret = newMap (nKeys);
    for (int i = 0; i < aKeys.length; ++i)
      ret.put (aKeys[i], aValues[i]);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsHashMap <KEYTYPE, VALUETYPE> newMap (@Nullable final Collection <? extends KEYTYPE> aKeys,
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
      return newMap (0);

    final CommonsHashMap <KEYTYPE, VALUETYPE> ret = newMap (nKeys);
    final Iterator <? extends KEYTYPE> itk = aKeys.iterator ();
    final Iterator <? extends VALUETYPE> itv = aValues.iterator ();
    while (itk.hasNext ())
      ret.put (itk.next (), itv.next ());
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsHashMap <KEYTYPE, VALUETYPE> newMap (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    if (isEmpty (aMap))
      return newMap (0);

    return new CommonsHashMap <> (aMap);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsHashMap <KEYTYPE, VALUETYPE> newMap (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> [] aMaps)
  {
    if (aMaps == null || aMaps.length == 0)
      return newMap (0);

    final CommonsHashMap <KEYTYPE, VALUETYPE> ret = newMap ();
    for (final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap : aMaps)
      ret.putAll (aMap);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsHashMap <KEYTYPE, VALUETYPE> newMap (@Nullable final Collection <? extends Map.Entry <KEYTYPE, VALUETYPE>> aCollection)
  {
    if (isEmpty (aCollection))
      return newMap (0);

    final CommonsHashMap <KEYTYPE, VALUETYPE> ret = newMap (aCollection.size ());
    ret.putAll (aCollection);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsHashMap <KEYTYPE, VALUETYPE> newMap (@Nullable final Iterable <? extends Map.Entry <KEYTYPE, VALUETYPE>> aCollection)
  {
    if (isEmpty (aCollection))
      return newMap (0);

    final CommonsHashMap <KEYTYPE, VALUETYPE> ret = newMap ();
    ret.putAll (aCollection);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsLinkedHashMap <KEYTYPE, VALUETYPE> newOrderedMap (@Nonnegative final int nInitialCapacity)
  {
    return new CommonsLinkedHashMap <> (nInitialCapacity);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsLinkedHashMap <KEYTYPE, VALUETYPE> newOrderedMap ()
  {
    return new CommonsLinkedHashMap <> ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCKEYTYPE, SRCVALUETYPE, DSTKEYTYPE, DSTVALUETYPE> CommonsLinkedHashMap <DSTKEYTYPE, DSTVALUETYPE> newOrderedMapMapped (@Nullable final Map <? extends SRCKEYTYPE, ? extends SRCVALUETYPE> aMap,
                                                                                                                                          @Nonnull final Function <? super SRCKEYTYPE, ? extends DSTKEYTYPE> aKeyMapper,
                                                                                                                                          @Nonnull final Function <? super SRCVALUETYPE, ? extends DSTVALUETYPE> aValueMapper)
  {
    if (isEmpty (aMap))
      return newOrderedMap (0);
    return new CommonsLinkedHashMap <> (aMap, aKeyMapper, aValueMapper);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTKEYTYPE, DSTVALUETYPE> CommonsLinkedHashMap <DSTKEYTYPE, DSTVALUETYPE> newOrderedMapMapped (@Nullable final Collection <? extends SRCTYPE> aCollection,
                                                                                                                         @Nonnull final Function <? super SRCTYPE, ? extends DSTKEYTYPE> aKeyMapper,
                                                                                                                         @Nonnull final Function <? super SRCTYPE, ? extends DSTVALUETYPE> aValueMapper)
  {
    if (isEmpty (aCollection))
      return newOrderedMap (0);
    return new CommonsLinkedHashMap <> (aCollection, aKeyMapper, aValueMapper);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsLinkedHashMap <KEYTYPE, VALUETYPE> newOrderedMap (@Nullable final Map <KEYTYPE, VALUETYPE> aMap,
                                                                                              @Nonnull final Predicate <? super Map.Entry <? extends KEYTYPE, ? extends VALUETYPE>> aFilter)
  {
    if (isEmpty (aMap))
      return newOrderedMap (0);
    final CommonsLinkedHashMap <KEYTYPE, VALUETYPE> ret = newOrderedMap (aMap.size ());
    ret.putAll (aMap, aFilter);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsLinkedHashMap <KEYTYPE, VALUETYPE> newOrderedMap (@Nullable final KEYTYPE aKey,
                                                                                              @Nullable final VALUETYPE aValue)
  {
    final CommonsLinkedHashMap <KEYTYPE, VALUETYPE> ret = newOrderedMap (1);
    ret.put (aKey, aValue);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE> CommonsLinkedHashMap <ELEMENTTYPE, ELEMENTTYPE> newOrderedMap (@Nullable final ELEMENTTYPE... aValues)
  {
    if (ArrayHelper.isEmpty (aValues))
      return newOrderedMap (0);

    if ((aValues.length % 2) != 0)
      throw new IllegalArgumentException ("The passed array needs an even number of elements!");

    final CommonsLinkedHashMap <ELEMENTTYPE, ELEMENTTYPE> ret = newOrderedMap (aValues.length / 2);
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
  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsLinkedHashMap <KEYTYPE, VALUETYPE> newOrderedMap (@Nullable final KEYTYPE [] aKeys,
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
      return newOrderedMap (0);

    final CommonsLinkedHashMap <KEYTYPE, VALUETYPE> ret = newOrderedMap (nKeys);
    for (int i = 0; i < aKeys.length; ++i)
      ret.put (aKeys[i], aValues[i]);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsLinkedHashMap <KEYTYPE, VALUETYPE> newOrderedMap (@Nullable final Collection <? extends KEYTYPE> aKeys,
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
      return newOrderedMap (0);

    final CommonsLinkedHashMap <KEYTYPE, VALUETYPE> ret = newOrderedMap (nKeys);
    final Iterator <? extends KEYTYPE> itk = aKeys.iterator ();
    final Iterator <? extends VALUETYPE> itv = aValues.iterator ();
    while (itk.hasNext ())
      ret.put (itk.next (), itv.next ());
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsLinkedHashMap <KEYTYPE, VALUETYPE> newOrderedMap (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    if (isEmpty (aMap))
      return newOrderedMap (0);
    return new CommonsLinkedHashMap <> (aMap);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsLinkedHashMap <KEYTYPE, VALUETYPE> newOrderedMap (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> [] aMaps)
  {
    if (ArrayHelper.isEmpty (aMaps))
      return newOrderedMap (0);

    final CommonsLinkedHashMap <KEYTYPE, VALUETYPE> ret = newOrderedMap ();
    for (final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap : aMaps)
      ret.putAll (aMap);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsLinkedHashMap <KEYTYPE, VALUETYPE> newOrderedMap (@Nullable final Collection <? extends Map.Entry <KEYTYPE, VALUETYPE>> aCollection)
  {
    if (isEmpty (aCollection))
      return newOrderedMap (0);

    final CommonsLinkedHashMap <KEYTYPE, VALUETYPE> ret = newOrderedMap (aCollection.size ());
    ret.putAll (aCollection);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> CommonsLinkedHashMap <KEYTYPE, VALUETYPE> newOrderedMap (@Nullable final Iterable <? extends Map.Entry <KEYTYPE, VALUETYPE>> aCollection)
  {
    if (isEmpty (aCollection))
      return newOrderedMap (0);

    final CommonsLinkedHashMap <KEYTYPE, VALUETYPE> ret = newOrderedMap ();
    ret.putAll (aCollection);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> CommonsTreeMap <KEYTYPE, VALUETYPE> newSortedMap ()
  {
    return new CommonsTreeMap <> (Comparator.nullsFirst (Comparator.naturalOrder ()));
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCKEYTYPE, SRCVALUETYPE, DSTKEYTYPE extends Comparable <? super DSTKEYTYPE>, DSTVALUETYPE> CommonsTreeMap <DSTKEYTYPE, DSTVALUETYPE> newSortedMapMapped (@Nullable final Map <? extends SRCKEYTYPE, ? extends SRCVALUETYPE> aMap,
                                                                                                                                                                           @Nonnull final Function <? super SRCKEYTYPE, DSTKEYTYPE> aKeyMapper,
                                                                                                                                                                           @Nonnull final Function <? super SRCVALUETYPE, DSTVALUETYPE> aValueMapper)
  {
    return new CommonsTreeMap <> (aMap, aKeyMapper, aValueMapper);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> CommonsTreeMap <KEYTYPE, VALUETYPE> newSortedMap (@Nullable final Map <KEYTYPE, VALUETYPE> aMap,
                                                                                                                            @Nonnull final Predicate <? super Map.Entry <? extends KEYTYPE, ? extends VALUETYPE>> aFilter)
  {
    final CommonsTreeMap <KEYTYPE, VALUETYPE> ret = newSortedMap ();
    ret.putAll (aMap, aFilter);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> CommonsTreeMap <KEYTYPE, VALUETYPE> newSortedMap (@Nullable final KEYTYPE aKey,
                                                                                                                            @Nullable final VALUETYPE aValue)
  {
    final CommonsTreeMap <KEYTYPE, VALUETYPE> ret = newSortedMap ();
    ret.put (aKey, aValue);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> CommonsTreeMap <ELEMENTTYPE, ELEMENTTYPE> newSortedMap (@Nullable final ELEMENTTYPE... aValues)
  {
    if (ArrayHelper.isEmpty (aValues))
      return newSortedMap ();

    if ((aValues.length % 2) != 0)
      throw new IllegalArgumentException ("The passed array needs an even number of elements!");

    final CommonsTreeMap <ELEMENTTYPE, ELEMENTTYPE> ret = newSortedMap ();
    for (int i = 0; i < aValues.length; i += 2)
      ret.put (aValues[i], aValues[i + 1]);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> CommonsTreeMap <KEYTYPE, VALUETYPE> newSortedMap (@Nullable final KEYTYPE [] aKeys,
                                                                                                                            @Nullable final VALUETYPE [] aValues)
  {
    // Are both empty?
    if (ArrayHelper.isEmpty (aKeys) && ArrayHelper.isEmpty (aValues))
      return newSortedMap ();

    // keys OR values may be null here
    if (ArrayHelper.getSize (aKeys) != ArrayHelper.getSize (aValues))
      throw new IllegalArgumentException ("The passed arrays have different length!");

    final CommonsTreeMap <KEYTYPE, VALUETYPE> ret = newSortedMap ();
    for (int i = 0; i < aKeys.length; ++i)
      ret.put (aKeys[i], aValues[i]);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> CommonsTreeMap <KEYTYPE, VALUETYPE> newSortedMap (@Nullable final Collection <? extends KEYTYPE> aKeys,
                                                                                                                            @Nullable final Collection <? extends VALUETYPE> aValues)
  {
    // Are both empty?
    if (isEmpty (aKeys) && isEmpty (aValues))
      return newSortedMap ();

    // keys OR values may be null here
    if (getSize (aKeys) != getSize (aValues))
      throw new IllegalArgumentException ("Number of keys is different from number of values");

    final CommonsTreeMap <KEYTYPE, VALUETYPE> ret = newSortedMap ();
    final Iterator <? extends KEYTYPE> itk = aKeys.iterator ();
    final Iterator <? extends VALUETYPE> itv = aValues.iterator ();
    while (itk.hasNext ())
      ret.put (itk.next (), itv.next ());
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> CommonsTreeMap <KEYTYPE, VALUETYPE> newSortedMap (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    if (isEmpty (aMap))
      return newSortedMap ();

    final CommonsTreeMap <KEYTYPE, VALUETYPE> ret = newSortedMap ();
    ret.putAll (aMap);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> CommonsTreeMap <KEYTYPE, VALUETYPE> newSortedMap (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> [] aMaps)
  {
    if (aMaps == null || aMaps.length == 0)
      return newSortedMap ();

    final CommonsTreeMap <KEYTYPE, VALUETYPE> ret = newSortedMap ();
    for (final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap : aMaps)
      ret.putAll (aMap);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> CommonsTreeMap <KEYTYPE, VALUETYPE> newSortedMap (@Nullable final Collection <? extends Map.Entry <KEYTYPE, VALUETYPE>> aCollection)
  {
    if (isEmpty (aCollection))
      return newSortedMap ();

    final CommonsTreeMap <KEYTYPE, VALUETYPE> ret = newSortedMap ();
    ret.putAll (aCollection);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> CommonsTreeMap <KEYTYPE, VALUETYPE> newSortedMap (@Nullable final Iterable <? extends Map.Entry <KEYTYPE, VALUETYPE>> aCollection)
  {
    if (isEmpty (aCollection))
      return newSortedMap ();

    final CommonsTreeMap <KEYTYPE, VALUETYPE> ret = newSortedMap ();
    ret.putAll (aCollection);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsHashSet <ELEMENTTYPE> newSet (@Nonnegative final int nInitialCapacity)
  {
    return new CommonsHashSet <> (nInitialCapacity);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsHashSet <ELEMENTTYPE> newSet ()
  {
    return new CommonsHashSet <> ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> CommonsHashSet <DSTTYPE> newSetMapped (@Nullable final Collection <? extends SRCTYPE> aCollection,
                                                                          @Nonnull final Function <? super SRCTYPE, DSTTYPE> aMapper)
  {
    if (isEmpty (aCollection))
      return newSet (0);
    final CommonsHashSet <DSTTYPE> ret = newSet (aCollection.size ());
    ret.addAllMapped (aCollection, aMapper);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> CommonsHashSet <DSTTYPE> newSetMapped (@Nullable final SRCTYPE [] aArray,
                                                                          @Nonnull final Function <? super SRCTYPE, DSTTYPE> aMapper)
  {
    if (ArrayHelper.isEmpty (aArray))
      return newSet (0);

    final CommonsHashSet <DSTTYPE> ret = newSet (aArray.length);
    ret.addAllMapped (aArray, aMapper);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsHashSet <ELEMENTTYPE> newSet (@Nullable final Collection <? extends ELEMENTTYPE> aCollection,
                                                                   @Nonnull final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (isEmpty (aCollection))
      return newSet (0);
    final CommonsHashSet <ELEMENTTYPE> ret = newSet (aCollection.size ());
    ret.addAll (aCollection, aFilter);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsHashSet <ELEMENTTYPE> newSet (@Nullable final ELEMENTTYPE aValue)
  {
    final CommonsHashSet <ELEMENTTYPE> ret = newSet (1);
    ret.add (aValue);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE> CommonsHashSet <ELEMENTTYPE> newSet (@Nullable final ELEMENTTYPE... aValues)
  {
    if (ArrayHelper.isEmpty (aValues))
      return newSet (0);

    final CommonsHashSet <ELEMENTTYPE> ret = newSet (aValues.length);
    ret.addAll (aValues);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsHashSet <ELEMENTTYPE> newSet (@Nullable final Iterable <? extends ELEMENTTYPE> aCont)
  {
    final CommonsHashSet <ELEMENTTYPE> ret = newSet ();
    ret.addAll (aCont);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsHashSet <ELEMENTTYPE> newSet (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    if (isEmpty (aCont))
      return newSet (0);

    return new CommonsHashSet <> (aCont);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsHashSet <ELEMENTTYPE> newSet (@Nullable final Iterator <? extends ELEMENTTYPE> aIter)
  {
    final CommonsHashSet <ELEMENTTYPE> ret = newSet ();
    ret.addAll (aIter);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsHashSet <ELEMENTTYPE> newSet (@Nullable final ICommonsIterableIterator <? extends ELEMENTTYPE> aIter)
  {
    if (aIter == null)
      return newSet (0);
    return newSet (aIter.iterator ());
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsHashSet <ELEMENTTYPE> newSet (@Nullable final Enumeration <? extends ELEMENTTYPE> aEnum)
  {
    final CommonsHashSet <ELEMENTTYPE> ret = newSet ();
    ret.addAll (aEnum);
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
      Collections.addAll (ret, aValues);
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
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> CommonsTreeSet <ELEMENTTYPE> newSortedSet ()
  {
    return new CommonsTreeSet <> (Comparator.nullsFirst (Comparator.naturalOrder ()));
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE extends Comparable <? super DSTTYPE>> CommonsTreeSet <DSTTYPE> newSortedSetMapped (@Nullable final Collection <? extends SRCTYPE> aCollection,
                                                                                                                     @Nonnull final Function <? super SRCTYPE, DSTTYPE> aMapper)
  {
    final CommonsTreeSet <DSTTYPE> ret = newSortedSet ();
    ret.addAllMapped (aCollection, aMapper);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE extends Comparable <? super DSTTYPE>> CommonsTreeSet <DSTTYPE> newSortedSetMapped (@Nullable final SRCTYPE [] aArray,
                                                                                                                     @Nonnull final Function <? super SRCTYPE, DSTTYPE> aMapper)
  {
    final CommonsTreeSet <DSTTYPE> ret = newSortedSet ();
    ret.addAllMapped (aArray, aMapper);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> CommonsTreeSet <ELEMENTTYPE> newSortedSet (@Nullable final Collection <? extends ELEMENTTYPE> aCollection,
                                                                                                                  @Nonnull final Predicate <? super ELEMENTTYPE> aFilter)
  {
    final CommonsTreeSet <ELEMENTTYPE> ret = newSortedSet ();
    ret.addAll (aCollection, aFilter);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> CommonsTreeSet <ELEMENTTYPE> newSortedSet (@Nullable final ELEMENTTYPE aValue)
  {
    final CommonsTreeSet <ELEMENTTYPE> ret = newSortedSet ();
    ret.add (aValue);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> CommonsTreeSet <ELEMENTTYPE> newSortedSet (@Nullable final ELEMENTTYPE... aValues)
  {
    final CommonsTreeSet <ELEMENTTYPE> ret = newSortedSet ();
    ret.addAll (aValues);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> CommonsTreeSet <ELEMENTTYPE> newSortedSet (@Nullable final Iterable <? extends ELEMENTTYPE> aCont)
  {
    final CommonsTreeSet <ELEMENTTYPE> ret = newSortedSet ();
    ret.addAll (aCont);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> CommonsTreeSet <ELEMENTTYPE> newSortedSet (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    final CommonsTreeSet <ELEMENTTYPE> ret = newSortedSet ();
    if (isNotEmpty (aCont))
      ret.addAll (aCont);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> CommonsTreeSet <ELEMENTTYPE> newSortedSet (@Nullable final Iterator <? extends ELEMENTTYPE> aIter)
  {
    final CommonsTreeSet <ELEMENTTYPE> ret = newSortedSet ();
    ret.addAll (aIter);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> CommonsTreeSet <ELEMENTTYPE> newSortedSet (@Nullable final ICommonsIterableIterator <? extends ELEMENTTYPE> aIter)
  {
    if (aIter == null)
      return newSortedSet ();
    return newSortedSet (aIter.iterator ());
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> CommonsTreeSet <ELEMENTTYPE> newSortedSet (@Nullable final Enumeration <? extends ELEMENTTYPE> aEnum)
  {
    final CommonsTreeSet <ELEMENTTYPE> ret = newSortedSet ();
    ret.addAll (aEnum);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsLinkedHashSet <ELEMENTTYPE> newOrderedSet (@Nonnegative final int nInitialCapacity)
  {
    return new CommonsLinkedHashSet <> (nInitialCapacity);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsLinkedHashSet <ELEMENTTYPE> newOrderedSet ()
  {
    return new CommonsLinkedHashSet <> ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> CommonsLinkedHashSet <DSTTYPE> newOrderedSetMapped (@Nullable final Collection <? extends SRCTYPE> aCollection,
                                                                                       @Nonnull final Function <? super SRCTYPE, DSTTYPE> aMapper)
  {
    if (isEmpty (aCollection))
      return newOrderedSet (0);
    final CommonsLinkedHashSet <DSTTYPE> ret = newOrderedSet (aCollection.size ());
    ret.addAllMapped (aCollection, aMapper);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> CommonsLinkedHashSet <DSTTYPE> newOrderedSetMapped (@Nullable final SRCTYPE [] aArray,
                                                                                       @Nonnull final Function <? super SRCTYPE, DSTTYPE> aMapper)
  {
    if (ArrayHelper.isEmpty (aArray))
      return newOrderedSet (0);
    final CommonsLinkedHashSet <DSTTYPE> ret = newOrderedSet (aArray.length);
    ret.addAllMapped (aArray, aMapper);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsLinkedHashSet <ELEMENTTYPE> newOrderedSet (@Nullable final Collection <? extends ELEMENTTYPE> aCollection,
                                                                                @Nonnull final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (isEmpty (aCollection))
      return newOrderedSet (0);
    final CommonsLinkedHashSet <ELEMENTTYPE> ret = newOrderedSet (aCollection.size ());
    ret.addAll (aCollection, aFilter);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsLinkedHashSet <ELEMENTTYPE> newOrderedSet (@Nullable final ELEMENTTYPE aValue)
  {
    final CommonsLinkedHashSet <ELEMENTTYPE> ret = newOrderedSet (1);
    ret.add (aValue);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE> CommonsLinkedHashSet <ELEMENTTYPE> newOrderedSet (@Nullable final ELEMENTTYPE... aValues)
  {
    if (ArrayHelper.isEmpty (aValues))
      return newOrderedSet (0);

    final CommonsLinkedHashSet <ELEMENTTYPE> ret = newOrderedSet (aValues.length);
    Collections.addAll (ret, aValues);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsLinkedHashSet <ELEMENTTYPE> newOrderedSet (@Nullable final Iterable <? extends ELEMENTTYPE> aCont)
  {
    final CommonsLinkedHashSet <ELEMENTTYPE> ret = newOrderedSet ();
    ret.addAll (aCont);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsLinkedHashSet <ELEMENTTYPE> newOrderedSet (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    if (isEmpty (aCont))
      return newOrderedSet (0);
    return new CommonsLinkedHashSet <> (aCont);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsLinkedHashSet <ELEMENTTYPE> newOrderedSet (@Nonnull final Iterator <? extends ELEMENTTYPE> aIter)
  {
    final CommonsLinkedHashSet <ELEMENTTYPE> ret = newOrderedSet ();
    ret.addAll (aIter);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsLinkedHashSet <ELEMENTTYPE> newOrderedSet (@Nullable final ICommonsIterableIterator <? extends ELEMENTTYPE> aIter)
  {
    if (aIter == null)
      return newOrderedSet (0);
    return newOrderedSet (aIter.iterator ());
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsLinkedHashSet <ELEMENTTYPE> newOrderedSet (@Nullable final Enumeration <? extends ELEMENTTYPE> aEnum)
  {
    final CommonsLinkedHashSet <ELEMENTTYPE> ret = newOrderedSet ();
    ret.addAll (aEnum);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> newListPrefilled (@Nullable final ELEMENTTYPE aValue,
                                                                               @Nonnegative final int nElements)
  {
    ValueEnforcer.isGE0 (nElements, "Elements");

    final CommonsArrayList <ELEMENTTYPE> ret = new CommonsArrayList <> (nElements);
    for (int i = 0; i < nElements; ++i)
      ret.add (aValue);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> newList (@Nonnegative final int nInitialCapacity)
  {
    return new CommonsArrayList <> (nInitialCapacity);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> newList ()
  {
    return new CommonsArrayList <> ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> newList (@Nullable final Collection <? extends ELEMENTTYPE> aCollection,
                                                                      @Nonnull final Predicate <? super ELEMENTTYPE> aFilter)
  {
    return CommonsArrayList.createFiltered (aCollection, aFilter);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> newList (@Nullable final ELEMENTTYPE aValue)
  {
    final CommonsArrayList <ELEMENTTYPE> ret = newList (1);
    ret.add (aValue);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> newList (@Nullable final ELEMENTTYPE... aValues)
  {
    // Don't user Arrays.asList since aIter returns an unmodifiable list!
    if (ArrayHelper.isEmpty (aValues))
      return newList (0);

    final CommonsArrayList <ELEMENTTYPE> ret = newList (aValues.length);
    ret.addAll (aValues);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> CommonsArrayList <DSTTYPE> newListMapped (@Nullable final SRCTYPE [] aValues,
                                                                             @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aMapper)
  {
    // Don't user Arrays.asList since aIter returns an unmodifiable list!
    if (ArrayHelper.isEmpty (aValues))
      return newList (0);

    final CommonsArrayList <DSTTYPE> ret = newList (aValues.length);
    ret.addAllMapped (aValues, aMapper);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE, DSTTYPE> CommonsArrayList <DSTTYPE> newListMapped (@Nullable final Iterable <? extends ELEMENTTYPE> aIter,
                                                                                 @Nonnull final Function <? super ELEMENTTYPE, ? extends DSTTYPE> aMapper)
  {
    final CommonsArrayList <DSTTYPE> ret = newList ();
    ret.addAllMapped (aIter, aMapper);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> CommonsArrayList <DSTTYPE> newListMapped (@Nullable final Collection <? extends SRCTYPE> aCollection,
                                                                             @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aMapper)
  {
    if (isEmpty (aCollection))
      return newList (0);
    final CommonsArrayList <DSTTYPE> ret = newList (aCollection.size ());
    ret.addAllMapped (aCollection, aMapper);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE, DSTTYPE> CommonsArrayList <DSTTYPE> newListMapped (@Nullable final Iterable <? extends ELEMENTTYPE> aCollection,
                                                                                 @Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                                                                 @Nonnull final Function <? super ELEMENTTYPE, ? extends DSTTYPE> aMapper)
  {
    final CommonsArrayList <DSTTYPE> ret = newList ();
    ret.addAllMapped (aCollection, aFilter, aMapper);
    return ret;
  }

  /**
   * Compared to {@link Collections#list(Enumeration)} this method is more flexible in Generics
   * parameter.
   *
   * @param <ELEMENTTYPE>
   *        Type of the elements
   * @param aEnum
   *        The enumeration to be converted
   * @return The non-<code>null</code> created {@link CommonsArrayList}.
   * @see Collections#list(Enumeration)
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> newList (@Nullable final Enumeration <? extends ELEMENTTYPE> aEnum)
  {
    return new CommonsArrayList <> (aEnum);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> newList (@Nullable final Iterator <? extends ELEMENTTYPE> aIter)
  {
    final CommonsArrayList <ELEMENTTYPE> ret = newList ();
    ret.addAll (aIter);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> newList (@Nullable final Iterable <? extends ELEMENTTYPE> aIter)
  {
    return new CommonsArrayList <> (aIter);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> newList (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    if (isEmpty (aCont))
      return newList (0);

    return new CommonsArrayList <> (aCont);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> newList (@Nullable final ICommonsIterableIterator <? extends ELEMENTTYPE> aIter)
  {
    if (aIter == null)
      return newList (0);
    return newList (aIter.iterator ());
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

    final ICommonsMap <KEY, VALUE> ret = newMap ();
    for (final KEY aKey : aKeys)
      if (aValues.containsKey (aKey))
        ret.put (aKey, aValues.get (aKey));
    return ret;
  }

  @Nullable
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> getConcatenatedList (@Nullable final Collection <? extends ELEMENTTYPE> aCollection1,
                                                                                  @Nullable final Collection <? extends ELEMENTTYPE> aCollection2)
  {
    final int nSize1 = getSize (aCollection1);
    if (nSize1 == 0)
      return newList (aCollection2);

    final int nSize2 = getSize (aCollection2);
    if (nSize2 == 0)
      return newList (aCollection1);

    final CommonsArrayList <ELEMENTTYPE> ret = newList (nSize1 + nSize2);
    ret.addAll (aCollection1);
    ret.addAll (aCollection2);
    return ret;
  }

  @Nullable
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> getConcatenatedList (@Nullable final Collection <? extends ELEMENTTYPE> aCont1,
                                                                                  @Nullable final ELEMENTTYPE... aCont2)
  {
    final int nSize1 = getSize (aCont1);
    if (nSize1 == 0)
      return newList (aCont2);

    final int nSize2 = ArrayHelper.getSize (aCont2);
    if (nSize2 == 0)
      return newList (aCont1);

    final CommonsArrayList <ELEMENTTYPE> ret = newList (nSize1 + nSize2);
    ret.addAll (aCont1);
    Collections.addAll (ret, aCont2);
    return ret;
  }

  @Nullable
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> getConcatenatedList (@Nullable final ELEMENTTYPE [] aCont1,
                                                                                  @Nullable final Collection <? extends ELEMENTTYPE> aCont2)
  {
    final int nSize1 = ArrayHelper.getSize (aCont1);
    if (nSize1 == 0)
      return newList (aCont2);

    final int nSize2 = getSize (aCont2);
    if (nSize2 == 0)
      return newList (aCont1);

    final CommonsArrayList <ELEMENTTYPE> ret = newList (nSize1 + nSize2);
    Collections.addAll (ret, aCont1);
    ret.addAll (aCont2);
    return ret;
  }

  @Nullable
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> ICommonsSet <ELEMENTTYPE> getConcatenatedSet (@Nullable final Collection <? extends ELEMENTTYPE> aCont1,
                                                                            @Nullable final Collection <? extends ELEMENTTYPE> aCont2)
  {
    final int nSize1 = getSize (aCont1);
    if (nSize1 == 0)
      return newSet (aCont2);

    final int nSize2 = getSize (aCont2);
    if (nSize2 == 0)
      return newSet (aCont1);

    final ICommonsSet <ELEMENTTYPE> ret = newSet (nSize1 + nSize2);
    ret.addAll (aCont1);
    ret.addAll (aCont2);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE> ICommonsSet <ELEMENTTYPE> getConcatenatedSet (@Nullable final Collection <? extends ELEMENTTYPE> aCont1,
                                                                            @Nullable final ELEMENTTYPE... aCont2)
  {
    final int nSize1 = getSize (aCont1);
    if (nSize1 == 0)
      return newSet (aCont2);

    final int nSize2 = ArrayHelper.getSize (aCont2);
    if (nSize2 == 0)
      return newSet (aCont1);

    final ICommonsSet <ELEMENTTYPE> ret = newSet (nSize1 + nSize2);
    ret.addAll (aCont1);
    Collections.addAll (ret, aCont2);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> ICommonsSet <ELEMENTTYPE> getConcatenatedSet (@Nullable final ELEMENTTYPE [] aCont1,
                                                                            @Nullable final Collection <? extends ELEMENTTYPE> aCont2)
  {
    final int nSize1 = ArrayHelper.getSize (aCont1);
    if (nSize1 == 0)
      return newSet (aCont2);

    final int nSize2 = getSize (aCont2);
    if (nSize2 == 0)
      return newSet (aCont1);

    final ICommonsSet <ELEMENTTYPE> ret = newSet (nSize1 + nSize2);
    Collections.addAll (ret, aCont1);
    ret.addAll (aCont2);
    return ret;
  }

  @Nonnull
  @ReturnsMutableObject ("design")
  @SafeVarargs
  @CodingStyleguideUnaware
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
  @CodingStyleguideUnaware
  public static <ELEMENTTYPE, COLLTYPE extends Collection <? super ELEMENTTYPE>> COLLTYPE getConcatenatedInline (@Nonnull final COLLTYPE aCont,
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
  @Nonnull
  @ReturnsMutableCopy
  public static <KEY, VALUE> ICommonsMap <KEY, VALUE> getCombinedMap (@Nullable final Map <KEY, VALUE> aMap1,
                                                                      @Nullable final Map <KEY, VALUE> aMap2)
  {
    if (isEmpty (aMap1))
      return newMap (aMap2);
    if (isEmpty (aMap2))
      return newMap (aMap1);

    // create and fill result map
    final ICommonsMap <KEY, VALUE> ret = new CommonsHashMap <> (aMap1);
    ret.putAll (aMap2);
    return ret;
  }

  @Nullable
  @ReturnsMutableCopy
  public static CommonsArrayList <?> newObjectListFromArray (@Nullable final Object aValue,
                                                             @Nonnull final Class <?> aComponentType)
  {
    if (aValue == null)
      return null;

    if (aComponentType == boolean.class)
    {
      // get as CommonsList<Boolean>
      return PrimitiveCollectionHelper.newPrimitiveList ((boolean []) aValue);
    }
    if (aComponentType == byte.class)
    {
      // get as CommonsList<Byte>
      return PrimitiveCollectionHelper.newPrimitiveList ((byte []) aValue);
    }
    if (aComponentType == char.class)
    {
      // get as CommonsList<Character>
      return PrimitiveCollectionHelper.newPrimitiveList ((char []) aValue);
    }
    if (aComponentType == double.class)
    {
      // get as CommonsList<Double>
      return PrimitiveCollectionHelper.newPrimitiveList ((double []) aValue);
    }
    if (aComponentType == float.class)
    {
      // get as CommonsList<Float>
      return PrimitiveCollectionHelper.newPrimitiveList ((float []) aValue);
    }
    if (aComponentType == int.class)
    {
      // get as CommonsList<Integer>
      return PrimitiveCollectionHelper.newPrimitiveList ((int []) aValue);
    }
    if (aComponentType == long.class)
    {
      // get as CommonsList<Long>
      return PrimitiveCollectionHelper.newPrimitiveList ((long []) aValue);
    }
    if (aComponentType == short.class)
    {
      // get as CommonsList<Short>
      return PrimitiveCollectionHelper.newPrimitiveList ((short []) aValue);
    }

    // the rest
    final Object [] aArray = (Object []) aValue;
    if (ArrayHelper.isEmpty (aArray))
      return null;

    final CommonsArrayList <Object> ret = newList (aArray.length);
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
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> getSubList (@Nullable final List <ELEMENTTYPE> aCont,
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
}
