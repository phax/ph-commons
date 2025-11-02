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
package com.helger.collection.iterator;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.base.array.ArrayHelper;
import com.helger.collection.CollectionHelper;
import com.helger.collection.base.EmptyIterator;
import com.helger.collection.base.IIterableIterator;
import com.helger.collection.commons.CommonsIterableEnumeration;

@Immutable
public class IteratorHelper
{
  protected IteratorHelper ()
  {}

  public static boolean isEmpty (@Nullable final Iterator <?> aIter)
  {
    return aIter == null || !aIter.hasNext ();
  }

  public static boolean isNotEmpty (@Nullable final Iterator <?> aIter)
  {
    return aIter != null && aIter.hasNext ();
  }

  /**
   * Retrieve the size of the passed {@link Iterator}.
   *
   * @param aIterator
   *        Iterator to check. May be <code>null</code>.
   * @return The number objects or 0 if the passed parameter is <code>null</code>.
   */
  @Nonnegative
  public static int getSize (@Nullable final Iterator <?> aIterator)
  {
    // This way to keep package dependencies clean
    return CollectionHelper.getSizeIterator (aIterator);
  }

  @NonNull
  public static <ELEMENTTYPE> Iterator <ELEMENTTYPE> getIterator (@Nullable final Iterable <ELEMENTTYPE> aCont)
  {
    return aCont == null ? new EmptyIterator <> () : getIterator (aCont.iterator ());
  }

  @NonNull
  public static <ELEMENTTYPE> Iterator <ELEMENTTYPE> getIterator (@Nullable final Iterator <ELEMENTTYPE> aIter)
  {
    return aIter == null ? new EmptyIterator <> () : aIter;
  }

  @NonNull
  public static <ELEMENTTYPE> IIterableIterator <ELEMENTTYPE> getIterator (@Nullable final Enumeration <? extends ELEMENTTYPE> aEnum)
  {
    return new CommonsIterableEnumeration <> (aEnum);
  }

  @NonNull
  @SafeVarargs
  public static <ELEMENTTYPE> Iterator <ELEMENTTYPE> getIterator (@Nullable final ELEMENTTYPE... aArray)
  {
    return ArrayHelper.isEmpty (aArray) ? new EmptyIterator <> () : new ArrayIterator <> (aArray);
  }

  @NonNull
  public static <ELEMENTTYPE> Iterator <ELEMENTTYPE> getReverseIterator (@Nullable final List <? extends ELEMENTTYPE> aCont)
  {
    if (CollectionHelper.isEmpty (aCont))
      return new EmptyIterator <> ();

    /**
     * Performance note: this implementation is much faster than building a temporary list in
     * reverse order and returning a forward iterator!
     */
    return new ReverseListIterator <> (aCont);
  }

  /**
   * Get a merged iterator of both iterators. The first iterator is iterated first, the second one
   * afterwards.
   *
   * @param <ELEMENTTYPE>
   *        The type of elements to be enumerated.
   * @param aIter1
   *        First iterator. May be <code>null</code>.
   * @param aIter2
   *        Second iterator. May be <code>null</code>.
   * @return The merged iterator. Never <code>null</code>.
   */
  @NonNull
  public static <ELEMENTTYPE> Iterator <ELEMENTTYPE> getCombinedIterator (@Nullable final Iterator <? extends ELEMENTTYPE> aIter1,
                                                                          @Nullable final Iterator <? extends ELEMENTTYPE> aIter2)
  {
    return new CombinedIterator <> (aIter1, aIter2);
  }
}
