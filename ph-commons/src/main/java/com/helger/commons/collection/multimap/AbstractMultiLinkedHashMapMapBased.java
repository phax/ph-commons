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
package com.helger.commons.collection.multimap;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.collection.ext.CommonsLinkedHashMap;
import com.helger.commons.collection.ext.ICommonsMap;

/**
 * Abstract multi map based on {@link CommonsLinkedHashMap}.
 *
 * @author Philip Helger
 * @param <KEYTYPE1>
 *        outer key type
 * @param <KEYTYPE2>
 *        inner key type
 * @param <VALUETYPE>
 *        value type
 */
@NotThreadSafe
public abstract class AbstractMultiLinkedHashMapMapBased <KEYTYPE1, KEYTYPE2, VALUETYPE> extends
                                                         CommonsLinkedHashMap <KEYTYPE1, ICommonsMap <KEYTYPE2, VALUETYPE>>
                                                         implements IMultiMapMapBased <KEYTYPE1, KEYTYPE2, VALUETYPE>
{
  public AbstractMultiLinkedHashMapMapBased ()
  {}

  public AbstractMultiLinkedHashMapMapBased (@Nonnull final KEYTYPE1 aKey,
                                             @Nonnull final KEYTYPE2 aInnerKey,
                                             @Nullable final VALUETYPE aValue)
  {
    putSingle (aKey, aInnerKey, aValue);
  }

  public AbstractMultiLinkedHashMapMapBased (@Nullable final KEYTYPE1 aKey,
                                             @Nullable final ICommonsMap <KEYTYPE2, VALUETYPE> aValue)
  {
    put (aKey, aValue);
  }

  public AbstractMultiLinkedHashMapMapBased (@Nullable final Map <? extends KEYTYPE1, ? extends ICommonsMap <KEYTYPE2, VALUETYPE>> aCont)
  {
    if (aCont != null)
      putAll (aCont);
  }

  @Nonnull
  protected abstract ICommonsMap <KEYTYPE2, VALUETYPE> createNewInnerMap ();

  @Nonnull
  public ICommonsMap <KEYTYPE2, VALUETYPE> getOrCreate (@Nullable final KEYTYPE1 aKey)
  {
    return computeIfAbsent (aKey, k -> createNewInnerMap ());
  }
}
