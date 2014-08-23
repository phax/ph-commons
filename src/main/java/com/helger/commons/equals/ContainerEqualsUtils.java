/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.equals;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.lang.ClassHelper;

/**
 * A special equals helper class that handles collections and container classes.
 * The different to the reqular {@link EqualsUtils} and
 * {@link EqualsImplementationRegistry} methods is, that the methods contained
 * in here are not strictly obeying to the equals contract, as the
 * implementation classes must not match - only the content is relevant.
 *
 * @author Philip Helger
 */
public final class ContainerEqualsUtils
{
  /**
   * This enum differentiates the different meta container types.
   *
   * @author Philip Helger
   */
  public static enum EContainerType
  {
    /**
     * The collection type applies to all Collection objects that are not Sets,
     * and will be compared in their regular order.
     */
    COLLECTION,
    /** Sets represent unordered container */
    SET,
    /** Maps are key-value-containers */
    MAP,
    /** Arrays */
    ARRAY,
    /** Iterator */
    ITERATOR,
    /** Iterable */
    ITERABLE,
    /** Enumeration */
    ENUMERATION
  }

  private ContainerEqualsUtils ()
  {}

  @Nullable
  public static EContainerType getContainerTypeOfClass (@Nullable final Class <?> aClass)
  {
    if (aClass != null)
    {
      // Query Set before Collection, because Set is derived from Collection!
      if (Set.class.isAssignableFrom (aClass))
        return EContainerType.SET;
      if (Collection.class.isAssignableFrom (aClass))
        return EContainerType.COLLECTION;
      if (Map.class.isAssignableFrom (aClass))
        return EContainerType.MAP;
      if (ClassHelper.isArrayClass (aClass))
        return EContainerType.ARRAY;
      if (Iterator.class.isAssignableFrom (aClass))
        return EContainerType.ITERATOR;
      if (Iterable.class.isAssignableFrom (aClass))
        return EContainerType.ITERABLE;
      if (Enumeration.class.isAssignableFrom (aClass))
        return EContainerType.ENUMERATION;
    }
    return null;
  }

  @Nullable
  public static EContainerType getContainerTypeOfObject (@Nullable final Object aObj)
  {
    return aObj == null ? null : getContainerTypeOfClass (aObj.getClass ());
  }

  public static boolean isContainerClass (@Nullable final Class <?> aClass)
  {
    return getContainerTypeOfClass (aClass) != null;
  }

  public static boolean isContainerObject (@Nullable final Object aObj)
  {
    return getContainerTypeOfObject (aObj) != null;
  }

  private static boolean _areChildrenEqual (@Nullable final Object aObj1, @Nullable final Object aObj2)
  {
    if (isContainerObject (aObj1) && isContainerObject (aObj2))
    {
      // It's a nested collection!
      // -> indirect recursive call
      return equals (aObj1, aObj2);
    }

    // Not collections
    return EqualsImplementationRegistry.areEqual (aObj1, aObj2);
  }

  /**
   * Check if the content of the passed containers is equal. If the container
   * itself contains nested containers, this method is invoked recursively. For
   * non-container elements, the
   * {@link EqualsImplementationRegistry#areEqual(Object, Object)} method is
   * invoked to test for equality!
   *
   * @param aObj1
   *        The first container
   * @param aObj2
   *        The second container
   * @return <code>true</code> if both objects are the same, or if they have the
   *         same meta type and have the same content.
   * @throws IllegalArgumentException
   *         if one of the arguments is not a container!
   */
  public static boolean equals (@Nullable final Object aObj1, @Nullable final Object aObj2)
  {
    // Same object - check first
    if (aObj1 == aObj2)
      return true;

    // Is only one value null?
    if (aObj1 == null || aObj2 == null)
      return false;

    final EContainerType eType1 = getContainerTypeOfObject (aObj1);
    final EContainerType eType2 = getContainerTypeOfObject (aObj2);
    if (eType1 == null)
      throw new IllegalArgumentException ("The first parameter is not a container type: " + aObj1);
    if (eType2 == null)
      throw new IllegalArgumentException ("The second parameter is not a container type: " + aObj2);

    if (!eType1.equals (eType2))
    {
      // Different container types!
      return false;
    }

    switch (eType1)
    {
      case COLLECTION:
      {
        // Valid for non-Set collections
        final Collection <?> aCont1 = (Collection <?>) aObj1;
        final Collection <?> aCont2 = (Collection <?>) aObj2;
        if (aCont1.isEmpty () && aCont2.isEmpty ())
          return true;
        if (aCont1.size () != aCont2.size ())
          return false;
        final Iterator <?> aIter1 = aCont1.iterator ();
        final Iterator <?> aIter2 = aCont2.iterator ();
        while (aIter1.hasNext ())
        {
          final Object aChildObj1 = aIter1.next ();
          final Object aChildObj2 = aIter2.next ();
          if (!_areChildrenEqual (aChildObj1, aChildObj2))
            return false;
        }
        return true;
      }
      case SET:
      {
        // Valid for Set
        final Set <?> aCont1 = (Set <?>) aObj1;
        final Set <?> aCont2 = (Set <?>) aObj2;
        if (aCont1.isEmpty () && aCont2.isEmpty ())
          return true;
        if (aCont1.size () != aCont2.size ())
          return false;
        for (final Object aChildObj1 : aCont1)
          if (!aCont2.contains (aChildObj1))
            return false;
        return true;
      }
      case MAP:
      {
        // Valid for Map
        final Map <?, ?> aCont1 = (Map <?, ?>) aObj1;
        final Map <?, ?> aCont2 = (Map <?, ?>) aObj2;
        if (aCont1.isEmpty () && aCont2.isEmpty ())
          return true;
        if (aCont1.size () != aCont2.size ())
          return false;
        for (final Map.Entry <?, ?> aEntry : aCont1.entrySet ())
        {
          final Object aChildObj1 = aEntry.getValue ();
          final Object aChildObj2 = aCont2.get (aEntry.getKey ());
          if (!_areChildrenEqual (aChildObj1, aChildObj2))
            return false;
        }
        return true;
      }
      case ARRAY:
      {
        // Check if it is an array of collections (e.g. List<String>[])
        final Class <?> aComponentClass1 = aObj1.getClass ().getComponentType ();
        final Class <?> aComponentClass2 = aObj2.getClass ().getComponentType ();
        if (isContainerClass (aComponentClass1) && isContainerClass (aComponentClass2))
        {
          // Special handling for arrays of containers
          final Object [] aArray1 = (Object []) aObj1;
          final Object [] aArray2 = (Object []) aObj2;
          if (aArray1.length != aArray2.length)
            return false;
          for (int i = 0; i < aArray1.length; ++i)
          {
            final Object aChildObj1 = aArray1[i];
            final Object aChildObj2 = aArray2[i];
            if (!_areChildrenEqual (aChildObj1, aChildObj2))
              return false;
          }
          return true;
        }

        // No different types possible -> use EqualsImplementationRegistry
        // directly
        return EqualsImplementationRegistry.areEqual (aObj1, aObj2);
      }
      case ITERATOR:
      {
        final Iterator <?> aIter1 = (Iterator <?>) aObj1;
        final Iterator <?> aIter2 = (Iterator <?>) aObj2;
        while (aIter1.hasNext ())
        {
          if (!aIter2.hasNext ())
          {
            // Second iterator is shorter
            return false;
          }
          final Object aChildObj1 = aIter1.next ();
          final Object aChildObj2 = aIter2.next ();
          if (!_areChildrenEqual (aChildObj1, aChildObj2))
            return false;
        }
        if (aIter2.hasNext ())
        {
          // Second iterator is longer
          return false;
        }
        return true;
      }
      case ITERABLE:
      {
        final Iterable <?> aIterable1 = (Iterable <?>) aObj1;
        final Iterable <?> aIterable2 = (Iterable <?>) aObj2;
        return equals (aIterable1.iterator (), aIterable2.iterator ());
      }
      case ENUMERATION:
      {
        final Enumeration <?> aEnum1 = (Enumeration <?>) aObj1;
        final Enumeration <?> aEnum2 = (Enumeration <?>) aObj2;
        while (aEnum1.hasMoreElements ())
        {
          if (!aEnum2.hasMoreElements ())
          {
            // Second enumeration is shorter
            return false;
          }
          final Object aChildObj1 = aEnum1.nextElement ();
          final Object aChildObj2 = aEnum2.nextElement ();
          if (!_areChildrenEqual (aChildObj1, aChildObj2))
            return false;
        }
        if (aEnum2.hasMoreElements ())
        {
          // Second enumeration is longer
          return false;
        }
        return true;
      }
      default:
        throw new IllegalStateException ("Unhandled container type " + eType1 + "!");
    }
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

    final EContainerType eType = getContainerTypeOfObject (aObj);
    if (eType == null)
    {
      // It's not a supported container -> create a new list with one element
      return ContainerHelper.newList (aObj);
    }

    switch (eType)
    {
      case COLLECTION:
        // It's already a collection
        if (aObj instanceof List <?>)
          return (List <?>) aObj;
        return ContainerHelper.newList ((Collection <?>) aObj);
      case SET:
        // Convert to list
        return ContainerHelper.newList ((Set <?>) aObj);
      case MAP:
        // Use the entry set of the map as list
        return ContainerHelper.newList (((Map <?, ?>) aObj).entrySet ());
      case ARRAY:
        // Convert the array to a list
        return ContainerHelper.newList ((Object []) aObj);
      case ITERATOR:
        // Convert the iterator to a list
        return ContainerHelper.newList ((Iterator <?>) aObj);
      case ITERABLE:
        // Convert the iterable to a list
        return ContainerHelper.newList ((Iterable <?>) aObj);
      case ENUMERATION:
        // Convert the enumeration to a list
        return ContainerHelper.newList ((Enumeration <?>) aObj);
      default:
        throw new IllegalStateException ("Unhandled container type " + eType + "!");
    }
  }

  /**
   * This is a sanity method that first calls {@link #getAsList(Object)} on both
   * objects an than calls {@link #equals(Object, Object)} on the collections.
   * This means that calling this method with the String array ["a", "b"] and
   * the List&lt;String&gt; ("a", "b") will result in a return value of true.
   *
   * @param aObj1
   *        The first object to be compared
   * @param aObj2
   *        The second object to be compared
   * @return <code>true</code> if the contents are equal, <code>false</code>
   *         otherwise
   */
  public static boolean equalsAsList (@Nullable final Object aObj1, @Nullable final Object aObj2)
  {
    // Same object - check first
    if (aObj1 == aObj2)
      return true;

    // Is only one value null?
    if (aObj1 == null || aObj2 == null)
      return false;

    // Convert to a collection
    final List <?> aCollection1 = getAsList (aObj1);
    final List <?> aCollection2 = getAsList (aObj2);

    // And compare
    return equals (aCollection1, aCollection2);
  }
}
