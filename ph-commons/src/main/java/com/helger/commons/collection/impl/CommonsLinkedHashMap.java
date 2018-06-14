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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;

/**
 * A special {@link LinkedHashMap} implementation based on
 * {@link ICommonsOrderedMap}.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Map key type
 * @param <VALUETYPE>
 *        Map value type
 */
public class CommonsLinkedHashMap <KEYTYPE, VALUETYPE> extends LinkedHashMap <KEYTYPE, VALUETYPE> implements
                                  ICommonsOrderedMap <KEYTYPE, VALUETYPE>
{
  public CommonsLinkedHashMap ()
  {}

  public CommonsLinkedHashMap (@Nonnegative final int nInitialCapacity)
  {
    super (nInitialCapacity);
  }

  public CommonsLinkedHashMap (@Nonnegative final int nInitialCapacity, final float fLoadFactor)
  {
    super (nInitialCapacity, fLoadFactor);
  }

  public CommonsLinkedHashMap (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    super (CollectionHelper.getSize (aMap));
    if (aMap != null)
      putAll (aMap);
  }

  public CommonsLinkedHashMap (@Nonnegative final int nInitialCapacity,
                               final float fLoadFactor,
                               final boolean bAccessOrder)
  {
    super (nInitialCapacity, fLoadFactor, bAccessOrder);
  }

  public <COLLTYPE> CommonsLinkedHashMap (@Nullable final Collection <? extends COLLTYPE> aValues,
                                          @Nonnull final Function <? super COLLTYPE, ? extends KEYTYPE> aKeyMapper,
                                          @Nonnull final Function <? super COLLTYPE, ? extends VALUETYPE> aValueMapper)
  {
    super (CollectionHelper.getSize (aValues));
    putAllMapped (aValues, aKeyMapper, aValueMapper);
  }

  @Override
  @Nonnull
  @ReturnsMutableCopy
  public <K, V> CommonsLinkedHashMap <K, V> createInstance ()
  {
    return new CommonsLinkedHashMap <> ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public CommonsLinkedHashMap <KEYTYPE, VALUETYPE> getClone ()
  {
    return new CommonsLinkedHashMap <> (this);
  }
}
