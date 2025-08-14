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
package com.helger.commons.equals;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.equals.EqualsHelper;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.ECollectionBaseType;
import com.helger.commons.collection.impl.ICommonsList;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * A small helper class that provides helper methods for easy <code>equals</code> method generation
 * especially around collections. These methods were previously directly contained in class
 * EqualsHelper.
 *
 * @author Philip Helger
 * @since 12.0.0
 */
@Immutable
public final class CollectionEqualsHelper
{
  @PresentForCodeCoverage
  private static final CollectionEqualsHelper INSTANCE = new CollectionEqualsHelper ();

  private CollectionEqualsHelper ()
  {}

  private static boolean _areChildrenEqual (@Nullable final Object aObj1, @Nullable final Object aObj2)
  {
    if (CollectionHelper.isCollectionObject (aObj1) && CollectionHelper.isCollectionObject (aObj2))
    {
      // It's a nested collection!
      // -> indirect recursive call
      return equalsCollection (aObj1, aObj2);
    }

    // Not collections
    return EqualsHelper.equals (aObj1, aObj2);
  }

  public static <T> boolean equalsCollectionOnly (@Nonnull final Collection <T> aCont1,
                                                  @Nonnull final Collection <?> aCont2)
  {
    if (aCont1.isEmpty () && aCont2.isEmpty ())
      return true;
    if (aCont1.size () != aCont2.size ())
      return false;
    final Iterator <?> aIter2 = aCont2.iterator ();
    for (final T aChildObj1 : aCont1)
    {
      final Object aChildObj2 = aIter2.next ();
      if (!_areChildrenEqual (aChildObj1, aChildObj2))
        return false;
    }
    return true;
  }

  public static <K, V> boolean equalsMap (@Nonnull final Map <K, V> aCont1, @Nonnull final Map <?, ?> aCont2)
  {
    if (aCont1.size () != aCont2.size ())
      return false;

    for (final Entry <K, V> aEntry : aCont1.entrySet ())
    {
      final K aKey = aEntry.getKey ();
      final V aValue = aEntry.getValue ();
      if (aValue == null)
      {
        if (aCont2.get (aKey) == null && aCont2.containsKey (aKey))
        {
          // Also contains a null value
        }
        else
          return false;
      }
      else
      {
        if (!_areChildrenEqual (aValue, aCont2.get (aKey)))
          return false;
      }
    }
    return true;
  }

  public static <T> boolean equalsSet (@Nonnull final Set <T> aCont1, @Nonnull final Set <?> aCont2)
  {
    if (aCont1.size () != aCont2.size ())
      return false;
    for (final T aChildObj1 : aCont1)
      if (!aCont2.contains (aChildObj1))
        return false;
    return true;
  }

  public static <T> boolean equalsIterator (@Nonnull final Iterator <T> aIter1, final Iterator <?> aIter2)
  {
    while (aIter1.hasNext ())
    {
      if (!aIter2.hasNext ())
      {
        // Second iterator is shorter
        return false;
      }
      final T aChildObj1 = aIter1.next ();
      final Object aChildObj2 = aIter2.next ();
      if (!_areChildrenEqual (aChildObj1, aChildObj2))
        return false;
    }
    // Second iterator is longer?
    return !aIter2.hasNext ();
  }

  public static <T> boolean equalsEumeration (@Nonnull final Enumeration <T> aEnum1, final Enumeration <?> aEnum2)
  {
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
    // Second enumeration is longer?
    return !aEnum2.hasMoreElements ();
  }

  /**
   * Check if the content of the passed containers is equal. If the container itself contains nested
   * containers, this method is invoked recursively. For non-container elements, the
   * {@link EqualsImplementationRegistry#areEqual(Object, Object)} method is invoked to test for
   * equality!
   *
   * @param aObj1
   *        The first container. May be <code>null</code>.
   * @param aObj2
   *        The second container. May be <code>null</code>.
   * @return <code>true</code> if both objects are the same, or if they have the same meta type and
   *         have the same content.
   * @throws IllegalArgumentException
   *         if one of the arguments is not a container!
   */
  public static boolean equalsCollection (@Nullable final Object aObj1, @Nullable final Object aObj2)
  {
    // Same object - check first
    if (EqualsHelper.identityEqual (aObj1, aObj2))
      return true;

    // Is only one value null?
    if (aObj1 == null || aObj2 == null)
      return false;

    final ECollectionBaseType eType1 = CollectionHelper.getCollectionBaseTypeOfClass (aObj1.getClass ());
    final ECollectionBaseType eType2 = CollectionHelper.getCollectionBaseTypeOfClass (aObj2.getClass ());
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
        return equalsCollectionOnly (aCont1, aCont2);
      }
      case SET:
      {
        // Valid for Set
        final Set <?> aCont1 = (Set <?>) aObj1;
        final Set <?> aCont2 = (Set <?>) aObj2;
        return equalsSet (aCont1, aCont2);
      }
      case MAP:
      {
        // Valid for Map
        final Map <?, ?> aCont1 = (Map <?, ?>) aObj1;
        final Map <?, ?> aCont2 = (Map <?, ?>) aObj2;
        return equalsMap (aCont1, aCont2);
      }
      case ARRAY:
      {
        // Check if it is an array of collections (e.g. List<String>[])
        final Class <?> aComponentClass1 = aObj1.getClass ().getComponentType ();
        final Class <?> aComponentClass2 = aObj2.getClass ().getComponentType ();
        if (CollectionHelper.isCollectionClass (aComponentClass1) &&
            CollectionHelper.isCollectionClass (aComponentClass2))
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
        return EqualsHelper.equals (aObj1, aObj2);
      }
      case ITERATOR:
      {
        final Iterator <?> aIter1 = (Iterator <?>) aObj1;
        final Iterator <?> aIter2 = (Iterator <?>) aObj2;
        return equalsIterator (aIter1, aIter2);
      }
      case ITERABLE:
      {
        final Iterable <?> aIterable1 = (Iterable <?>) aObj1;
        final Iterable <?> aIterable2 = (Iterable <?>) aObj2;
        return equalsIterator (aIterable1.iterator (), aIterable2.iterator ());
      }
      case ENUMERATION:
      {
        final Enumeration <?> aEnum1 = (Enumeration <?>) aObj1;
        final Enumeration <?> aEnum2 = (Enumeration <?>) aObj2;
        return equalsEumeration (aEnum1, aEnum2);
      }
      default:
        throw new IllegalStateException ("Unhandled container type " + eType1 + "!");
    }
  }

  /**
   * This is a sanity method that first calls {@link CollectionHelper#getAsList(Object)} on both
   * objects an than calls {@link #equalsCollectionOnly(Collection, Collection)} on the collections.
   * This means that calling this method with the String array ["a", "b"] and the List&lt;String&gt;
   * ("a", "b") will result in a return value of true.
   *
   * @param aObj1
   *        The first object to be compared. May be <code>null</code>.
   * @param aObj2
   *        The second object to be compared. May be <code>null</code>.
   * @return <code>true</code> if the contents are equal, <code>false</code> otherwise
   */
  public static boolean equalsAsList (@Nullable final Object aObj1, @Nullable final Object aObj2)
  {
    // Same object - check first
    if (EqualsHelper.identityEqual (aObj1, aObj2))
      return true;

    // Is only one value null?
    if (aObj1 == null || aObj2 == null)
      return false;

    // Convert to a collection
    final ICommonsList <?> aCollection1 = CollectionHelper.getAsList (aObj1);
    final ICommonsList <?> aCollection2 = CollectionHelper.getAsList (aObj2);

    // And compare
    return equalsCollectionOnly (aCollection1, aCollection2);
  }
}
