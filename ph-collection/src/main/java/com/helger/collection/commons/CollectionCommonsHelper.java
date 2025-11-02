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
package com.helger.collection.commons;

import static com.helger.collection.CollectionHelper.isEmpty;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;

/**
 * Package utility class to make sure the package dependencies stay clean
 *
 * @author Philip Helger
 */
public final class CollectionCommonsHelper
{
  private CollectionCommonsHelper ()
  {}

  @NonNull
  @ReturnsMutableCopy
  private static <KEYTYPE, VALUETYPE> CommonsLinkedHashMap <KEYTYPE, VALUETYPE> _newOrderedMap (@Nullable final List <? extends Map.Entry <KEYTYPE, VALUETYPE>> aList)
  {
    final CommonsLinkedHashMap <KEYTYPE, VALUETYPE> ret = new CommonsLinkedHashMap <> (aList.size ());
    ret.putAll (aList);
    return ret;
  }

  /**
   * Get a map sorted by aIter's keys. Because no comparator is defined, the key type needs to
   * implement the {@link java.lang.Comparable} interface.
   *
   * @param <KEYTYPE>
   *        map key type
   * @param <VALUETYPE>
   *        map value type
   * @param aMap
   *        the map to sort
   * @return the sorted map and never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> ICommonsOrderedMap <KEYTYPE, VALUETYPE> getSortedByKey (@Nullable final Map <KEYTYPE, VALUETYPE> aMap)
  {
    if (isEmpty (aMap))
      return new CommonsLinkedHashMap <> (0);

    // get sorted entry list
    final ICommonsList <Map.Entry <KEYTYPE, VALUETYPE>> aList = new CommonsArrayList <> (aMap.entrySet ());
    aList.sort (Comparator.comparing (Map.Entry::getKey));
    return _newOrderedMap (aList);
  }

  /**
   * Get a map sorted by its keys. The comparison order is defined by the passed comparator object.
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
  @NonNull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> ICommonsOrderedMap <KEYTYPE, VALUETYPE> getSortedByKey (@Nullable final Map <KEYTYPE, VALUETYPE> aMap,
                                                                                             @NonNull final Comparator <? super KEYTYPE> aKeyComparator)
  {
    ValueEnforcer.notNull (aKeyComparator, "KeyComparator");

    if (isEmpty (aMap))
      return new CommonsLinkedHashMap <> (0);

    // get sorted Map.Entry list by Entry.getValue ()
    final ICommonsList <Map.Entry <KEYTYPE, VALUETYPE>> aList = new CommonsArrayList <> (aMap.entrySet ());
    aList.sort (Comparator.comparing (Map.Entry::getKey, aKeyComparator));
    return _newOrderedMap (aList);
  }

  /**
   * Get a map sorted by its values. Because no comparator is defined, the value type needs to
   * implement the {@link java.lang.Comparable} interface.
   *
   * @param <KEYTYPE>
   *        map key type
   * @param <VALUETYPE>
   *        map value type
   * @param aMap
   *        The map to sort. May not be <code>null</code>.
   * @return the sorted map and never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE extends Comparable <? super VALUETYPE>> ICommonsOrderedMap <KEYTYPE, VALUETYPE> getSortedByValue (@Nullable final Map <KEYTYPE, VALUETYPE> aMap)
  {
    if (isEmpty (aMap))
      return new CommonsLinkedHashMap <> (0);

    // get sorted entry list
    final ICommonsList <Map.Entry <KEYTYPE, VALUETYPE>> aList = new CommonsArrayList <> (aMap.entrySet ());
    aList.sort (Comparator.comparing (Map.Entry::getValue));
    return _newOrderedMap (aList);
  }

  /**
   * Get a map sorted by aIter's values. The comparison order is defined by the passed comparator
   * object.
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
  @NonNull
  @ReturnsMutableCopy
  public static <KEYTYPE, VALUETYPE> ICommonsOrderedMap <KEYTYPE, VALUETYPE> getSortedByValue (@Nullable final Map <KEYTYPE, VALUETYPE> aMap,
                                                                                               @NonNull final Comparator <? super VALUETYPE> aValueComparator)
  {
    ValueEnforcer.notNull (aValueComparator, "ValueComparator");

    if (isEmpty (aMap))
      return new CommonsLinkedHashMap <> (0);

    // get sorted Map.Entry list by Entry.getValue ()
    final ICommonsList <Map.Entry <KEYTYPE, VALUETYPE>> aList = new CommonsArrayList <> (aMap.entrySet ());
    aList.sort (Comparator.comparing (Map.Entry::getValue, aValueComparator));
    return _newOrderedMap (aList);
  }
}
