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
package com.helger.commons.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.CodingStyleguideUnaware;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.annotation.ReturnsImmutableObject;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.CommonsLinkedHashSet;
import com.helger.commons.collection.impl.CommonsTreeMap;
import com.helger.commons.collection.impl.CommonsTreeSet;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.collection.iterate.IIterableIterator;
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
   * Get the passed object as a {@link CommonsArrayList} object. This is helpful
   * in case you want to compare the String array ["a", "b"] with the
   * List&lt;String&gt; ("a", "b") If the passed object is not a recognized.
   * container type, than a new list with one element is created!
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
        if (aObj instanceof CommonsArrayList <?>)
          return (CommonsArrayList <?>) aObj;
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

  @SafeVarargs
  @Nullable
  @ReturnsImmutableObject
  @CodingStyleguideUnaware
  public static <ELEMENTTYPE> List <ELEMENTTYPE> makeUnmodifiable (@Nullable final ELEMENTTYPE... aArray)
  {
    return aArray == null ? null : Collections.unmodifiableList (newList (aArray));
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
    return aNavigableSet == null ? Collections.emptyNavigableSet ()
                                 : Collections.unmodifiableNavigableSet (aNavigableSet);
  }

  @Nonnull
  @ReturnsImmutableObject
  @CodingStyleguideUnaware
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> NavigableMap <KEYTYPE, VALUETYPE> makeUnmodifiableNotNull (@Nullable final NavigableMap <KEYTYPE, VALUETYPE> aNavigableMap)
  {
    return aNavigableMap == null ? Collections.emptyNavigableMap ()
                                 : Collections.unmodifiableNavigableMap (aNavigableMap);
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
    final CommonsHashMap <DSTKEYTYPE, DSTVALUETYPE> ret = newMap (aMap.size ());
    ret.putAllMapped (aMap, aKeyMapper, aValueMapper);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTKEYTYPE, DSTVALUETYPE> CommonsHashMap <DSTKEYTYPE, DSTVALUETYPE> newMapMapped (@Nullable final Collection <? extends SRCTYPE> aCollection,
                                                                                                            @Nonnull final Function <? super SRCTYPE, ? extends DSTKEYTYPE> aKeyMapper,
                                                                                                            @Nonnull final Function <? super SRCTYPE, ? extends DSTVALUETYPE> aValueMapper)
  {
    if (isEmpty (aCollection))
      return newMap (0);
    final CommonsHashMap <DSTKEYTYPE, DSTVALUETYPE> ret = newMap (aCollection.size ());
    ret.putAllMapped (aCollection, aKeyMapper, aValueMapper);
    return ret;
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
    final CommonsLinkedHashMap <DSTKEYTYPE, DSTVALUETYPE> ret = newOrderedMap (aMap.size ());
    ret.putAllMapped (aMap, aKeyMapper, aValueMapper);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTKEYTYPE, DSTVALUETYPE> CommonsLinkedHashMap <DSTKEYTYPE, DSTVALUETYPE> newOrderedMapMapped (@Nullable final Collection <? extends SRCTYPE> aCollection,
                                                                                                                         @Nonnull final Function <? super SRCTYPE, ? extends DSTKEYTYPE> aKeyMapper,
                                                                                                                         @Nonnull final Function <? super SRCTYPE, ? extends DSTVALUETYPE> aValueMapper)
  {
    if (isEmpty (aCollection))
      return newOrderedMap (0);
    final CommonsLinkedHashMap <DSTKEYTYPE, DSTVALUETYPE> ret = newOrderedMap (aCollection.size ());
    ret.putAllMapped (aCollection, aKeyMapper, aValueMapper);
    return ret;
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
   * @return A {@link CommonsLinkedHashMap} containing the passed key-value
   *         entries. Never <code>null</code>.
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
    final CommonsTreeMap <DSTKEYTYPE, DSTVALUETYPE> ret = newSortedMap ();
    ret.putAllMapped (aMap, aKeyMapper, aValueMapper);
    return ret;
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
  public static <ELEMENTTYPE> CommonsHashSet <ELEMENTTYPE> newSet (@Nullable final IIterableIterator <? extends ELEMENTTYPE> aIter)
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
  @SuppressFBWarnings (value = { "NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE" },
                       justification = "When using the constructor with the Comparator it works with null values!")
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
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> CommonsTreeSet <ELEMENTTYPE> newSortedSet (@Nullable final IIterableIterator <? extends ELEMENTTYPE> aIter)
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
  public static <ELEMENTTYPE> CommonsLinkedHashSet <ELEMENTTYPE> newOrderedSet (@Nullable final IIterableIterator <? extends ELEMENTTYPE> aIter)
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
   * Compared to {@link Collections#list(Enumeration)} this method is more
   * flexible in Generics parameter.
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
    final CommonsArrayList <ELEMENTTYPE> ret = newList ();
    ret.addAll (aEnum);
    return ret;
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
    final CommonsArrayList <ELEMENTTYPE> ret = newList ();
    ret.addAll (aIter);
    return ret;
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
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> newList (@Nullable final IIterableIterator <? extends ELEMENTTYPE> aIter)
  {
    if (aIter == null)
      return newList (0);
    return newList (aIter.iterator ());
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
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> CommonsArrayList <ELEMENTTYPE> getSorted (@Nullable final IIterableIterator <? extends ELEMENTTYPE> aIter)
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
  public static <ELEMENTTYPE extends Comparable <? super ELEMENTTYPE>> CommonsArrayList <ELEMENTTYPE> getSorted (@Nullable final IIterableIterator <? extends ELEMENTTYPE> aIter,
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
   * @return A {@link CommonsArrayList} based on the results of
   *         {@link Collections#sort(List)}.
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
   * @return A {@link CommonsArrayList} based on the results of
   *         {@link Collections#sort(List)}.
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
   * @return A {@link CommonsArrayList} based on the results of
   *         {@link Collections#sort(List)}.
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
   * Get a map sorted by aIter's keys. Because no comparator is defined, the key
   * type needs to implement the {@link java.lang.Comparable} interface.
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
    if (isEmpty (aMap))
      return newOrderedMap (0);

    // get sorted entry list
    final ICommonsList <Map.Entry <KEYTYPE, VALUETYPE>> aList = newList (aMap.entrySet ());
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
   * @return the sorted map and never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> ICommonsOrderedMap <KEYTYPE, VALUETYPE> getSortedByKey (@Nullable final Map <KEYTYPE, VALUETYPE> aMap,
                                                                                             @Nonnull final Comparator <? super KEYTYPE> aKeyComparator)
  {
    ValueEnforcer.notNull (aKeyComparator, "KeyComparator");

    if (isEmpty (aMap))
      return newOrderedMap (0);

    // get sorted Map.Entry list by Entry.getValue ()
    final ICommonsList <Map.Entry <KEYTYPE, VALUETYPE>> aList = newList (aMap.entrySet ());
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
   * @return the sorted map and never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE extends Comparable <? super VALUETYPE>> ICommonsOrderedMap <KEYTYPE, VALUETYPE> getSortedByValue (@Nullable final Map <KEYTYPE, VALUETYPE> aMap)
  {
    if (isEmpty (aMap))
      return newOrderedMap (0);

    // get sorted entry list
    final ICommonsList <Map.Entry <KEYTYPE, VALUETYPE>> aList = newList (aMap.entrySet ());
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
   * @return the sorted map and never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> ICommonsOrderedMap <KEYTYPE, VALUETYPE> getSortedByValue (@Nullable final Map <KEYTYPE, VALUETYPE> aMap,
                                                                                               @Nonnull final Comparator <? super VALUETYPE> aValueComparator)
  {
    ValueEnforcer.notNull (aValueComparator, "ValueComparator");

    if (isEmpty (aMap))
      return newOrderedMap (0);

    // get sorted Map.Entry list by Entry.getValue ()
    final ICommonsList <Map.Entry <KEYTYPE, VALUETYPE>> aList = newList (aMap.entrySet ());
    aList.sort (Comparator.comparing (Map.Entry::getValue, aValueComparator));
    return newOrderedMap (aList);
  }

  @Nullable
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> getReverseList (@Nullable final Collection <? extends ELEMENTTYPE> aCollection)
  {
    if (isEmpty (aCollection))
      return newList (0);

    final CommonsArrayList <ELEMENTTYPE> ret = newList (aCollection);
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
  public static EChange removeAtIndex (@Nullable final List <?> aList, final int nIndex)
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

  /**
   * Retrieve the size of the passed {@link Enumeration}.
   *
   * @param aEnumeration
   *        Enumeration to check. May be <code>null</code>.
   * @return The number objects or 0 if the passed parameter is
   *         <code>null</code>.
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
   *        the length of the desired subset. If list is shorter than that,
   *        aIter will return a shorter section
   * @return The specified section of the passed list, or a shorter list if
   *         nStartIndex + nSectionLength is an invalid index. Never
   *         <code>null</code>.
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

  public static <ELEMENTTYPE> boolean contains (@Nullable final Collection <? extends ELEMENTTYPE> aCollection,
                                                @Nullable final ELEMENTTYPE aSearch)
  {
    if (isEmpty (aCollection))
      return false;

    return aCollection.contains (aSearch);
  }

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

  @Nullable
  public static <ELEMENTTYPE, DSTTYPE> DSTTYPE findFirstMapped (@Nullable final Iterable <? extends ELEMENTTYPE> aCollection,
                                                                @Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                                                @Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper)
  {
    return findFirstMapped (aCollection, aFilter, aMapper, (DSTTYPE) null);
  }

  @Nullable
  public static <ELEMENTTYPE, DSTTYPE> DSTTYPE findFirstMapped (@Nullable final Iterable <? extends ELEMENTTYPE> aCollection,
                                                                @Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                                                @Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper,
                                                                @Nullable final DSTTYPE aDefault)
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

  public static <ELEMENTTYPE> void findAll (@Nullable final Iterable <? extends ELEMENTTYPE> aSrc,
                                            @Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                            @Nonnull final Consumer <? super ELEMENTTYPE> aConsumer)
  {
    ValueEnforcer.notNull (aConsumer, "Consumer");

    if (isNotEmpty (aSrc))
      if (aFilter == null)
        aSrc.forEach (aConsumer);
      else
        for (final ELEMENTTYPE aElement : aSrc)
          if (aFilter.test (aElement))
            aConsumer.accept (aElement);
  }

  public static <SRCTYPE, DSTTYPE> void findAllMapped (@Nullable final Iterable <? extends SRCTYPE> aSrc,
                                                       @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aMapper,
                                                       @Nonnull final Consumer <? super DSTTYPE> aConsumer)
  {
    ValueEnforcer.notNull (aMapper, "Mapper");
    ValueEnforcer.notNull (aConsumer, "Consumer");

    if (isNotEmpty (aSrc))
      for (final SRCTYPE aElement : aSrc)
        aConsumer.accept (aMapper.apply (aElement));
  }

  /**
   * Iterate, filter, map, add.
   *
   * @param aSrc
   *        Source collection. May be <code>null</code>.
   * @param aFilter
   *        Filter on source object to use. May be <code>null</code>.
   * @param aMapper
   *        Mapping function. May not be <code>null</code>.
   * @param aConsumer
   *        Destination consumer. May not be <code>null</code>.
   * @param <SRCTYPE>
   *        Source object type
   * @param <DSTTYPE>
   *        Destination object type
   */
  public static <SRCTYPE, DSTTYPE> void findAllMapped (@Nullable final Iterable <? extends SRCTYPE> aSrc,
                                                       @Nullable final Predicate <? super SRCTYPE> aFilter,
                                                       @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aMapper,
                                                       @Nonnull final Consumer <? super DSTTYPE> aConsumer)
  {
    ValueEnforcer.notNull (aMapper, "Mapper");
    ValueEnforcer.notNull (aConsumer, "Consumer");

    if (aFilter == null)
      findAllMapped (aSrc, aMapper, aConsumer);
    else
      if (isNotEmpty (aSrc))
        for (final SRCTYPE aElement : aSrc)
          if (aFilter.test (aElement))
            aConsumer.accept (aMapper.apply (aElement));
  }

  /**
   * Iterate, map, filter, add.
   *
   * @param aSrc
   *        Source collection. May be <code>null</code>.
   * @param aMapper
   *        Mapping function. May not be <code>null</code>.
   * @param aFilter
   *        Filter on mapped object to use. May be <code>null</code>.
   * @param aConsumer
   *        Destination consumer. May not be <code>null</code>.
   * @param <SRCTYPE>
   *        Source object type
   * @param <DSTTYPE>
   *        Destination object type
   * @since 8.5.2
   */
  public static <SRCTYPE, DSTTYPE> void findAllMapped (@Nullable final Iterable <? extends SRCTYPE> aSrc,
                                                       @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aMapper,
                                                       @Nullable final Predicate <? super DSTTYPE> aFilter,
                                                       @Nonnull final Consumer <? super DSTTYPE> aConsumer)
  {
    ValueEnforcer.notNull (aMapper, "Mapper");
    ValueEnforcer.notNull (aConsumer, "Consumer");

    if (aFilter == null)
      findAllMapped (aSrc, aMapper, aConsumer);
    else
      if (isNotEmpty (aSrc))
        for (final SRCTYPE aElement : aSrc)
        {
          final DSTTYPE aMapped = aMapper.apply (aElement);
          if (aFilter.test (aMapped))
            aConsumer.accept (aMapped);
        }
  }

  /**
   * Count the number of elements in the passed iterable (collection) matching
   * the provided filter.
   *
   * @param aCollection
   *        The collection to count. May not be <code>null</code>.
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @return The number of matching elements. Always &ge; 0. If no filter is
   *         provided this is the same as {@link #getSize(Iterable)}.
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
   * Count the number of elements in the passed collection matching the provided
   * filter.
   *
   * @param aCollection
   *        The collection to count. May not be <code>null</code>.
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @return The number of matching elements. Always &ge; 0. If no filter is
   *         provided this is the same as {@link #getSize(Collection)}.
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

  public static <ELEMENTTYPE> boolean containsNone (@Nullable final Iterable <? extends ELEMENTTYPE> aCollection,
                                                    @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (aFilter == null)
      return isEmpty (aCollection);

    for (final ELEMENTTYPE aElement : aCollection)
      if (aFilter.test (aElement))
        return false;
    return true;
  }

  /**
   * Check if the passed collection contains only elements matching the
   * predicate. An empty collection does not fulfill this requirement! If no
   * filter is provided the return value is identical to
   * {@link #isNotEmpty(Iterable)}
   *
   * @param aCollection
   *        The collection to check. May be <code>null</code>.
   * @param aFilter
   *        Predicate to check against all elements. May not be
   *        <code>null</code>.
   * @return <code>true</code> only if the passed collection is neither
   *         <code>null</code> nor empty and if only matching elements are
   *         contained, or if no filter is provided and the collection is not
   *         empty.
   * @param <ELEMENTTYPE>
   *        Collection data type
   */
  public static <ELEMENTTYPE> boolean containsOnly (@Nullable final Iterable <? extends ELEMENTTYPE> aCollection,
                                                    @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (isEmpty (aCollection))
      return false;

    if (aFilter == null)
      return isNotEmpty (aCollection);

    for (final ELEMENTTYPE aElement : aCollection)
      if (!aFilter.test (aElement))
        return false;
    return true;
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
    return containsOnly (aCont, Objects::isNull);
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
    return getAtIndex (aList, nIndex, null);
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
    return getAtIndex (aCollection, nIndex, null);
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
    return getAtIndex (aCollection, aFilter, nIndex, null);
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
    return getAtIndexMapped (aCollection, nIndex, aMapper, null);
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
    return getAtIndexMapped (aCollection, aFilter, nIndex, aMapper, null);
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
}
