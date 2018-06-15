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
package com.helger.commons.collection.impl;

import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.CodingStyleguideUnaware;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;

/**
 * A special {@link SortedMap} based interface with extended functionality based
 * on {@link ICommonsMap}.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Map key type
 * @param <VALUETYPE>
 *        Map value type
 */
public interface ICommonsSortedMap <KEYTYPE, VALUETYPE> extends
                                   SortedMap <KEYTYPE, VALUETYPE>,
                                   ICommonsMap <KEYTYPE, VALUETYPE>
{
  /**
   * Create a new {@link CommonsTreeMap}.
   */
  @Override
  @Nonnull
  @ReturnsMutableCopy
  default <K, V> ICommonsSortedMap <K, V> createInstance ()
  {
    return new CommonsTreeMap <> ();
  }

  @Override
  @Nonnull
  @ReturnsMutableCopy
  default ICommonsSortedSet <KEYTYPE> copyOfKeySet ()
  {
    return new CommonsTreeSet <> (keySet ());
  }

  @Override
  @Nonnull
  @ReturnsMutableCopy
  default ICommonsSortedSet <KEYTYPE> copyOfKeySet (@Nullable final Predicate <? super KEYTYPE> aFilter)
  {
    if (aFilter == null)
      return copyOfKeySet ();
    final CommonsTreeSet <KEYTYPE> ret = new CommonsTreeSet <> ();
    CollectionHelper.findAll (keySet (), aFilter, ret::add);
    return ret;
  }

  @Override
  @Nonnull
  @ReturnsMutableCopy
  default ICommonsOrderedSet <Map.Entry <KEYTYPE, VALUETYPE>> copyOfEntrySet ()
  {
    return new CommonsLinkedHashSet <> (entrySet ());
  }

  @Override
  @Nullable
  default KEYTYPE getFirstKey (@Nullable final KEYTYPE aDefault)
  {
    return isEmpty () ? aDefault : firstKey ();
  }

  @Override
  @Nullable
  default VALUETYPE getFirstValue (@Nullable final VALUETYPE aDefault)
  {
    final KEYTYPE aKey = getFirstKey (null);
    return aKey == null ? aDefault : get (aKey);
  }

  @Nullable
  default KEYTYPE getLastKey ()
  {
    return getLastKey (null);
  }

  @Nullable
  default KEYTYPE getLastKey (@Nullable final KEYTYPE aDefault)
  {
    return isEmpty () ? aDefault : lastKey ();
  }

  @Nullable
  default VALUETYPE getLastValue ()
  {
    return getLastValue (null);
  }

  @Nullable
  default VALUETYPE getLastValue (@Nullable final VALUETYPE aDefault)
  {
    final KEYTYPE aKey = getLastKey (null);
    return aKey == null ? aDefault : get (aKey);
  }

  @Override
  @Nonnull
  @CodingStyleguideUnaware
  default SortedMap <KEYTYPE, VALUETYPE> getAsUnmodifiable ()
  {
    return Collections.unmodifiableSortedMap (this);
  }

  @Nonnull
  @ReturnsMutableCopy
  ICommonsSortedMap <KEYTYPE, VALUETYPE> getClone ();
}
