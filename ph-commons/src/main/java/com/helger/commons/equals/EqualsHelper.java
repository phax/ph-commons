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
package com.helger.commons.equals;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.ECollectionBaseType;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * A small helper class that provides helper methods for easy
 * <code>equals</code> method generation
 *
 * @author Philip Helger
 */
@Immutable
public final class EqualsHelper
{
  @PresentForCodeCoverage
  private static final EqualsHelper s_aInstance = new EqualsHelper ();

  private EqualsHelper ()
  {}

  /**
   * Check if two values are equal. This method only exists, so that no type
   * differentiation is needed.
   *
   * @param aObj1
   *        First value
   * @param aObj2
   *        Second value
   * @return <code>true</code> if they are equal, <code>false</code> otherwise.
   */
  public static boolean equals (final boolean aObj1, final boolean aObj2)
  {
    return aObj1 == aObj2;
  }

  /**
   * Check if two values are equal. This method only exists, so that no type
   * differentiation is needed.
   *
   * @param aObj1
   *        First value
   * @param aObj2
   *        Second value
   * @return <code>true</code> if they are equal, <code>false</code> otherwise.
   */
  public static boolean equals (final byte aObj1, final byte aObj2)
  {
    return aObj1 == aObj2;
  }

  /**
   * Check if two values are equal. This method only exists, so that no type
   * differentiation is needed.
   *
   * @param aObj1
   *        First value
   * @param aObj2
   *        Second value
   * @return <code>true</code> if they are equal, <code>false</code> otherwise.
   */
  public static boolean equals (final char aObj1, final char aObj2)
  {
    return aObj1 == aObj2;
  }

  /**
   * Check if two double values are equal. This is necessary, because in some
   * cases, the "==" operator returns wrong results.
   *
   * @param aObj1
   *        First double
   * @param aObj2
   *        Second double
   * @return <code>true</code> if they are equal, <code>false</code> otherwise.
   */
  public static boolean equals (final double aObj1, final double aObj2)
  {
    // Special overload for "double" required!
    return (aObj1 == aObj2) || (Double.doubleToLongBits (aObj1) == Double.doubleToLongBits (aObj2));
  }

  /**
   * Check if two float values are equal. This is necessary, because in some
   * cases, the "==" operator returns wrong results.
   *
   * @param aObj1
   *        First float
   * @param aObj2
   *        Second float
   * @return <code>true</code> if they are equal, <code>false</code> otherwise.
   */
  public static boolean equals (final float aObj1, final float aObj2)
  {
    // Special overload for "float" required!
    return (aObj1 == aObj2) || (Float.floatToIntBits (aObj1) == Float.floatToIntBits (aObj2));
  }

  /**
   * Check if two values are equal. This method only exists, so that no type
   * differentiation is needed.
   *
   * @param aObj1
   *        First value
   * @param aObj2
   *        Second value
   * @return <code>true</code> if they are equal, <code>false</code> otherwise.
   */
  public static boolean equals (final int aObj1, final int aObj2)
  {
    return aObj1 == aObj2;
  }

  /**
   * Check if two values are equal. This method only exists, so that no type
   * differentiation is needed.
   *
   * @param aObj1
   *        First value
   * @param aObj2
   *        Second value
   * @return <code>true</code> if they are equal, <code>false</code> otherwise.
   */
  public static boolean equals (final long aObj1, final long aObj2)
  {
    return aObj1 == aObj2;
  }

  /**
   * Check if two values are equal. This method only exists, so that no type
   * differentiation is needed.
   *
   * @param aObj1
   *        First value
   * @param aObj2
   *        Second value
   * @return <code>true</code> if they are equal, <code>false</code> otherwise.
   */
  public static boolean equals (final short aObj1, final short aObj2)
  {
    return aObj1 == aObj2;
  }

  /**
   * Check if two values are equal. This method only exists, so that no type
   * differentiation is needed.
   *
   * @param aObj1
   *        First value
   * @param aObj2
   *        Second value
   * @return <code>true</code> if they are equal, <code>false</code> otherwise.
   * @see EqualsImplementationRegistry#areEqual(Object, Object)
   */
  public static boolean equals (@Nullable final Object aObj1, @Nullable final Object aObj2)
  {
    return EqualsImplementationRegistry.areEqual (aObj1, aObj2);
  }

  /**
   * Check if the passed strings are equals case insensitive handling
   * <code>null</code> appropriately.
   *
   * @param sObj1
   *        First object to compare
   * @param sObj2
   *        Second object to compare
   * @return <code>true</code> if they are equal case insensitive,
   *         <code>false</code> otherwise.
   */
  @SuppressFBWarnings ({ "ES_COMPARING_PARAMETER_STRING_WITH_EQ" })
  public static boolean equalsIgnoreCase (@Nullable final String sObj1, @Nullable final String sObj2)
  {
    return sObj1 == null ? sObj2 == null : sObj1.equalsIgnoreCase (sObj2);
  }

  private static boolean _areChildrenEqual (@Nullable final Object aObj1, @Nullable final Object aObj2)
  {
    if (CollectionHelper.isCollectionObject (aObj1) && CollectionHelper.isCollectionObject (aObj2))
    {
      // It's a nested collection!
      // -> indirect recursive call
      return equalsCollection (aObj1, aObj2);
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
  public static boolean equalsCollection (@Nullable final Object aObj1, @Nullable final Object aObj2)
  {
    // Same object - check first
    if (aObj1 == aObj2)
      return true;

    // Is only one value null?
    if (aObj1 == null || aObj2 == null)
      return false;

    final ECollectionBaseType eType1 = CollectionHelper.getCollectionBaseTypeOfObject (aObj1);
    final ECollectionBaseType eType2 = CollectionHelper.getCollectionBaseTypeOfObject (aObj2);
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
        return equalsCollection (aIterable1.iterator (), aIterable2.iterator ());
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
   * This is a sanity method that first calls
   * {@link CollectionHelper#getAsList(Object)} on both objects an than calls
   * {@link #equalsCollection(Object, Object)} on the collections. This means
   * that calling this method with the String array ["a", "b"] and the
   * List&lt;String&gt; ("a", "b") will result in a return value of true.
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
    final List <?> aCollection1 = CollectionHelper.getAsList (aObj1);
    final List <?> aCollection2 = CollectionHelper.getAsList (aObj2);

    // And compare
    return equalsCollection (aCollection1, aCollection2);
  }
}
