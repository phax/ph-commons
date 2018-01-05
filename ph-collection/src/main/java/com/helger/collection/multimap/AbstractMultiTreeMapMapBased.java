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
package com.helger.collection.multimap;

import java.util.Comparator;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.collection.impl.CommonsTreeMap;
import com.helger.commons.collection.impl.ICommonsMap;

/**
 * Abstract multi map based on {@link CommonsTreeMap}.
 *
 * @author Philip Helger
 * @param <KEYTYPE1>
 *        outer key type
 * @param <KEYTYPE2>
 *        inner key type
 * @param <VALUETYPE>
 *        value type
 * @param <MAPTYPE>
 *        Inner map type
 */
@NotThreadSafe
public abstract class AbstractMultiTreeMapMapBased <KEYTYPE1, KEYTYPE2, VALUETYPE, MAPTYPE extends ICommonsMap <KEYTYPE2, VALUETYPE>>
                                                   extends CommonsTreeMap <KEYTYPE1, MAPTYPE>
                                                   implements IMultiMapMapBased <KEYTYPE1, KEYTYPE2, VALUETYPE, MAPTYPE>
{
  public AbstractMultiTreeMapMapBased ()
  {}

  public AbstractMultiTreeMapMapBased (@Nullable final Comparator <? super KEYTYPE1> aComparator)
  {
    super (aComparator);
  }

  public AbstractMultiTreeMapMapBased (@Nonnull final KEYTYPE1 aKey,
                                       @Nonnull final KEYTYPE2 aInnerKey,
                                       @Nullable final VALUETYPE aValue)
  {
    putSingle (aKey, aInnerKey, aValue);
  }

  public AbstractMultiTreeMapMapBased (@Nullable final KEYTYPE1 aKey,
                                       @Nullable final Map <? extends KEYTYPE2, ? extends VALUETYPE> aValue)
  {
    final MAPTYPE aMap = createNewInnerMap ();
    aMap.putAll (aValue);
    put (aKey, aMap);
  }

  public AbstractMultiTreeMapMapBased (@Nullable final Map <? extends KEYTYPE1, ? extends Map <? extends KEYTYPE2, ? extends VALUETYPE>> aCont)
  {
    if (aCont != null)
      for (final Map.Entry <? extends KEYTYPE1, ? extends Map <? extends KEYTYPE2, ? extends VALUETYPE>> aEntry : aCont.entrySet ())
      {
        final MAPTYPE aMap = createNewInnerMap ();
        aMap.putAll (aEntry.getValue ());
        put (aEntry.getKey (), aMap);
      }
  }

  @Nonnull
  protected abstract MAPTYPE createNewInnerMap ();

  @Nonnull
  public MAPTYPE getOrCreate (@Nullable final KEYTYPE1 aKey)
  {
    return computeIfAbsent (aKey, k -> createNewInnerMap ());
  }
}
