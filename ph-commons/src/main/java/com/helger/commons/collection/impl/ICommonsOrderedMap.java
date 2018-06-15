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

import java.util.Map;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;

/**
 * A special {@link Map} interface for ordered maps (like
 * {@link CommonsLinkedHashMap}) based on {@link ICommonsMap}.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        May key type
 * @param <VALUETYPE>
 *        Map value type
 */
public interface ICommonsOrderedMap <KEYTYPE, VALUETYPE> extends ICommonsMap <KEYTYPE, VALUETYPE>
{
  @Override
  @Nonnull
  @ReturnsMutableCopy
  default <K, V> ICommonsOrderedMap <K, V> createInstance ()
  {
    return new CommonsLinkedHashMap <> ();
  }

  @Override
  @Nonnull
  @ReturnsMutableCopy
  default ICommonsOrderedSet <KEYTYPE> copyOfKeySet ()
  {
    return new CommonsLinkedHashSet <> (keySet ());
  }

  @Override
  @Nonnull
  @ReturnsMutableCopy
  default ICommonsOrderedSet <KEYTYPE> copyOfKeySet (@Nullable final Predicate <? super KEYTYPE> aFilter)
  {
    if (aFilter == null)
      return copyOfKeySet ();
    return CollectionHelper.newOrderedSet (keySet (), aFilter);
  }

  @Override
  @Nonnull
  @ReturnsMutableCopy
  default ICommonsOrderedSet <Map.Entry <KEYTYPE, VALUETYPE>> copyOfEntrySet ()
  {
    return new CommonsLinkedHashSet <> (entrySet ());
  }

  @Nullable
  default KEYTYPE getLastKey ()
  {
    return getLastKey (null);
  }

  @Nullable
  default KEYTYPE getLastKey (@Nullable final KEYTYPE aDefault)
  {
    return isEmpty () ? aDefault : CollectionHelper.getLastElement (keySet ());
  }

  @Nullable
  default VALUETYPE getLastValue ()
  {
    return getLastValue (null);
  }

  @Nullable
  default VALUETYPE getLastValue (@Nullable final VALUETYPE aDefault)
  {
    final KEYTYPE aKey = getLastKey ();
    return aKey == null ? aDefault : get (aKey);
  }

  @Nonnull
  @ReturnsMutableCopy
  ICommonsOrderedMap <KEYTYPE, VALUETYPE> getClone ();
}
