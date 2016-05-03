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
package com.helger.commons.collection.ext;

import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;

public interface ICommonsSortedMap <KEYTYPE, VALUETYPE>
                                   extends SortedMap <KEYTYPE, VALUETYPE>, ICommonsMap <KEYTYPE, VALUETYPE>
{
  @Nonnull
  @ReturnsMutableCopy
  default ICommonsOrderedSet <KEYTYPE> copyOfKeySet ()
  {
    return new CommonsLinkedHashSet<> (keySet ());
  }

  @Nonnull
  @ReturnsMutableCopy
  default ICommonsOrderedSet <Map.Entry <KEYTYPE, VALUETYPE>> copyOfEntrySet ()
  {
    return new CommonsLinkedHashSet<> (entrySet ());
  }

  @Nullable
  default KEYTYPE getFirstKey ()
  {
    return isEmpty () ? null : firstKey ();
  }

  @Nullable
  default VALUETYPE getFirstValue ()
  {
    final KEYTYPE aKey = getFirstKey ();
    return aKey == null ? null : get (aKey);
  }

  @Nullable
  default KEYTYPE getLastKey ()
  {
    return isEmpty () ? null : lastKey ();
  }

  @Nullable
  default VALUETYPE getLastValue ()
  {
    final KEYTYPE aKey = getLastKey ();
    return aKey == null ? null : get (aKey);
  }

  @Nonnull
  default SortedMap <KEYTYPE, VALUETYPE> getAsUnmodifiable ()
  {
    return Collections.unmodifiableSortedMap (this);
  }

  @Nonnull
  @ReturnsMutableCopy
  ICommonsSortedMap <KEYTYPE, VALUETYPE> getClone ();
}
