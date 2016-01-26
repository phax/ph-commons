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

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.collection.iterate.ArrayIterator;
import com.helger.commons.collection.iterate.CombinedIterator;
import com.helger.commons.collection.iterate.EmptyEnumeration;
import com.helger.commons.collection.iterate.EmptyIterator;
import com.helger.commons.collection.iterate.EnumerationFromIterator;
import com.helger.commons.collection.iterate.IIterableIterator;
import com.helger.commons.collection.iterate.IterableIteratorFromEnumeration;
import com.helger.commons.collection.iterate.ReverseListIterator;

/**
 * Helper class containing several {@link Iterator}, {@link Iterable},
 * {@link Enumeration} etc. helper methods.
 *
 * @author Philip Helger
 */
public final class IteratorHelper
{
  private IteratorHelper ()
  {}

  public static boolean isEmpty (@Nullable final Iterator <?> aIter)
  {
    return aIter == null || !aIter.hasNext ();
  }

  public static boolean isEmpty (@Nullable final IIterableIterator <?> aIter)
  {
    return aIter == null || !aIter.hasNext ();
  }

  public static boolean isEmpty (@Nullable final Enumeration <?> aEnum)
  {
    return aEnum == null || !aEnum.hasMoreElements ();
  }

  public static boolean isNotEmpty (@Nullable final Iterator <?> aIter)
  {
    return aIter != null && aIter.hasNext ();
  }

  public static boolean isNotEmpty (@Nullable final IIterableIterator <?> aIter)
  {
    return aIter != null && aIter.hasNext ();
  }

  public static boolean isNotEmpty (@Nullable final Enumeration <?> aEnum)
  {
    return aEnum != null && aEnum.hasMoreElements ();
  }

  /**
   * Retrieve the size of the passed {@link Iterable}.
   *
   * @param aIterator
   *        Iterable iterator to check. May be <code>null</code>.
   * @return The number objects or 0 if the passed parameter is
   *         <code>null</code>.
   */
  @Nonnegative
  public static int getSize (@Nullable final IIterableIterator <?> aIterator)
  {
    return aIterator == null ? 0 : getSize (aIterator.iterator ());
  }

  /**
   * Retrieve the size of the passed {@link Iterator}.
   *
   * @param aIterator
   *        Iterator to check. May be <code>null</code>.
   * @return The number objects or 0 if the passed parameter is
   *         <code>null</code>.
   */
  @Nonnegative
  public static int getSize (@Nullable final Iterator <?> aIterator)
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

  @Nonnull
  public static <ELEMENTTYPE> IIterableIterator <ELEMENTTYPE> getIterator (@Nullable final Enumeration <? extends ELEMENTTYPE> aEnum)
  {
    return new IterableIteratorFromEnumeration <> (aEnum);
  }

  @Nonnull
  public static <ELEMENTTYPE> Iterator <ELEMENTTYPE> getIterator (@Nullable final Iterable <ELEMENTTYPE> aCont)
  {
    return aCont == null ? new EmptyIterator <> () : getIterator (aCont.iterator ());
  }

  @Nonnull
  public static <ELEMENTTYPE> Iterator <ELEMENTTYPE> getIterator (@Nullable final Iterator <ELEMENTTYPE> aIter)
  {
    return aIter == null ? new EmptyIterator <> () : aIter;
  }

  @Nonnull
  @SafeVarargs
  public static <ELEMENTTYPE> Iterator <ELEMENTTYPE> getIterator (@Nullable final ELEMENTTYPE... aArray)
  {
    return ArrayHelper.isEmpty (aArray) ? new EmptyIterator <> () : new ArrayIterator <> (aArray);
  }

  @Nonnull
  public static <ELEMENTTYPE> Iterator <ELEMENTTYPE> getReverseIterator (@Nullable final List <? extends ELEMENTTYPE> aCont)
  {
    if (CollectionHelper.isEmpty (aCont))
      return new EmptyIterator <> ();

    /**
     * Performance note: this implementation is much faster than building a
     * temporary list in reverse order and returning a forward iterator!
     */
    return new ReverseListIterator <> (aCont);
  }

  /**
   * Create an empty iterator.
   *
   * @param <ELEMENTTYPE>
   *        The type the iterator's elements.
   * @return A non-<code>null</code> object.
   */
  @Nonnull
  public static <ELEMENTTYPE> Iterator <ELEMENTTYPE> getEmptyIterator ()
  {
    return new EmptyIterator <> ();
  }

  /**
   * Get a merged iterator of both iterators. The first iterator is iterated
   * first, the second one afterwards.
   *
   * @param <ELEMENTTYPE>
   *        The type of elements to be enumerated.
   * @param aIter1
   *        First iterator. May be <code>null</code>.
   * @param aIter2
   *        Second iterator. May be <code>null</code>.
   * @return The merged iterator. Never <code>null</code>.
   */
  @Nonnull
  public static <ELEMENTTYPE> Iterator <ELEMENTTYPE> getCombinedIterator (@Nullable final Iterator <? extends ELEMENTTYPE> aIter1,
                                                                          @Nullable final Iterator <? extends ELEMENTTYPE> aIter2)
  {
    return new CombinedIterator <> (aIter1, aIter2);
  }

  /**
   * Get an {@link Enumeration} object based on a {@link Collection} object.
   *
   * @param <ELEMENTTYPE>
   *        the type of the elements in the container
   * @param aCont
   *        The container to enumerate.
   * @return an Enumeration object
   */
  @Nonnull
  public static <ELEMENTTYPE> Enumeration <ELEMENTTYPE> getEnumeration (@Nullable final Iterable <ELEMENTTYPE> aCont)
  {
    return CollectionHelper.isEmpty (aCont) ? new EmptyEnumeration <> () : getEnumeration (aCont.iterator ());
  }

  /**
   * Get an {@link Enumeration} object based on the passed array.
   *
   * @param <ELEMENTTYPE>
   *        the type of the elements in the container
   * @param aArray
   *        The array to enumerate.
   * @return an Enumeration object
   */
  @Nonnull
  @SafeVarargs
  public static <ELEMENTTYPE> Enumeration <ELEMENTTYPE> getEnumeration (@Nullable final ELEMENTTYPE... aArray)
  {
    return getEnumeration (getIterator (aArray));
  }

  /**
   * Get an Enumeration object based on an Iterator object.
   *
   * @param <ELEMENTTYPE>
   *        the type of the elements in the container
   * @param aIter
   *        iterator object to use
   * @return an Enumeration object
   */
  @Nonnull
  public static <ELEMENTTYPE> Enumeration <ELEMENTTYPE> getEnumeration (@Nullable final Iterator <ELEMENTTYPE> aIter)
  {
    if (aIter == null)
      return new EmptyEnumeration <> ();

    return new EnumerationFromIterator <> (aIter);
  }

  /**
   * Get an Enumeration object based on a Map object.
   *
   * @param <KEYTYPE>
   *        map key type
   * @param <VALUETYPE>
   *        map value type
   * @param aMap
   *        map object to use
   * @return an Enumeration object
   */
  @Nonnull
  public static <KEYTYPE, VALUETYPE> Enumeration <Map.Entry <KEYTYPE, VALUETYPE>> getEnumeration (@Nullable final Map <KEYTYPE, VALUETYPE> aMap)
  {
    if (aMap == null)
      return new EmptyEnumeration <> ();
    return getEnumeration (aMap.entrySet ());
  }

  /**
   * Create an empty enumeration.
   *
   * @param <ELEMENTTYPE>
   *        The type the enumeration's elements.
   * @return A non-<code>null</code> object.
   */
  @Nonnull
  public static <ELEMENTTYPE> Enumeration <ELEMENTTYPE> getEmptyEnumeration ()
  {
    return new EmptyEnumeration <> ();
  }

}
