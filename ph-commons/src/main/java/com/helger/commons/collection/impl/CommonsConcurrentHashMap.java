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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;

/**
 * A special {@link ConcurrentHashMap} implementation based on
 * {@link ICommonsMap}.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Map key type
 * @param <VALUETYPE>
 *        Map value type
 */
public class CommonsConcurrentHashMap <KEYTYPE, VALUETYPE> extends ConcurrentHashMap <KEYTYPE, VALUETYPE> implements
                                      ICommonsMap <KEYTYPE, VALUETYPE>
{
  public CommonsConcurrentHashMap ()
  {}

  public CommonsConcurrentHashMap (final int nInitialCapacity)
  {
    super (nInitialCapacity);
  }

  public CommonsConcurrentHashMap (final int nInitialCapacity, @Nonnegative final float fLoadFactor)
  {
    super (nInitialCapacity, fLoadFactor);
  }

  public CommonsConcurrentHashMap (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    super (CollectionHelper.getSize (aMap));
    if (aMap != null)
      putAll (aMap);
  }

  public <COLLTYPE> CommonsConcurrentHashMap (@Nullable final Collection <? extends COLLTYPE> aValues,
                                              @Nonnull final Function <? super COLLTYPE, ? extends KEYTYPE> aKeyMapper,
                                              @Nonnull final Function <? super COLLTYPE, ? extends VALUETYPE> aValueMapper)
  {
    super (CollectionHelper.getSize (aValues));
    putAllMapped (aValues, aKeyMapper, aValueMapper);
  }

  @Override
  @Nonnull
  @ReturnsMutableCopy
  public <K, V> CommonsConcurrentHashMap <K, V> createInstance ()
  {
    return new CommonsConcurrentHashMap <> ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public CommonsConcurrentHashMap <KEYTYPE, VALUETYPE> getClone ()
  {
    return new CommonsConcurrentHashMap <> (this);
  }
}
