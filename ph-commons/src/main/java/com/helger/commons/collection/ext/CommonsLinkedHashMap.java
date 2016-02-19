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

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;

public class CommonsLinkedHashMap <KEYTYPE, VALUETYPE> extends LinkedHashMap <KEYTYPE, VALUETYPE>
                                  implements ICommonsMap <KEYTYPE, VALUETYPE>
{
  public CommonsLinkedHashMap ()
  {}

  public CommonsLinkedHashMap (final int nInitialCapacity)
  {
    super (nInitialCapacity);
  }

  public CommonsLinkedHashMap (final int nInitialCapacity, final float fLoadFactor)
  {
    super (nInitialCapacity, fLoadFactor);
  }

  public CommonsLinkedHashMap (@Nonnull final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    super (aMap);
  }

  public CommonsLinkedHashMap (final int nInitialCapacity, final float fLoadFactor, final boolean bAccessOrder)
  {
    super (nInitialCapacity, fLoadFactor, bAccessOrder);
  }

  @Nonnull
  @ReturnsMutableCopy
  public CommonsLinkedHashMap <KEYTYPE, VALUETYPE> getClone ()
  {
    return new CommonsLinkedHashMap <> (this);
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsSet <KEYTYPE> copyOfKeySet ()
  {
    // Must be ordered here!
    return CollectionHelper.newOrderedSet (keySet ());
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsSet <Map.Entry <KEYTYPE, VALUETYPE>> copyOfEntrySet ()
  {
    // Must be ordered here!
    return CollectionHelper.newOrderedSet (entrySet ());
  }
}
