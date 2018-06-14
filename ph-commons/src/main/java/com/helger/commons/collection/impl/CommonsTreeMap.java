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
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;

/**
 * A special {@link TreeMap} implementation based on
 * {@link ICommonsNavigableMap}.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Map key type
 * @param <VALUETYPE>
 *        Map value type
 */
public class CommonsTreeMap <KEYTYPE, VALUETYPE> extends TreeMap <KEYTYPE, VALUETYPE> implements
                            ICommonsNavigableMap <KEYTYPE, VALUETYPE>
{
  public CommonsTreeMap ()
  {}

  public CommonsTreeMap (@Nullable final Comparator <? super KEYTYPE> aComparator)
  {
    super (aComparator);
  }

  public CommonsTreeMap (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    if (aMap != null)
      putAll (aMap);
  }

  public <COLLTYPE> CommonsTreeMap (@Nullable final Collection <? extends COLLTYPE> aValues,
                                    @Nonnull final Function <? super COLLTYPE, ? extends KEYTYPE> aKeyMapper,
                                    @Nonnull final Function <? super COLLTYPE, ? extends VALUETYPE> aValueMapper)
  {
    putAllMapped (aValues, aKeyMapper, aValueMapper);
  }

  @Override
  @Nonnull
  @ReturnsMutableCopy
  public <K, V> CommonsTreeMap <K, V> createInstance ()
  {
    return new CommonsTreeMap <> ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public CommonsTreeMap <KEYTYPE, VALUETYPE> getClone ()
  {
    final CommonsTreeMap <KEYTYPE, VALUETYPE> ret = new CommonsTreeMap <> (comparator ());
    ret.putAll (this);
    return ret;
  }
}
