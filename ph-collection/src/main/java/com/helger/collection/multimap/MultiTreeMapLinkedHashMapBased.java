/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsOrderedMap;

public class MultiTreeMapLinkedHashMapBased <KEYTYPE1, KEYTYPE2 extends Comparable <? super KEYTYPE2>, VALUETYPE>
                                            extends
                                            AbstractMultiTreeMapMapBased <KEYTYPE1, KEYTYPE2, VALUETYPE, ICommonsOrderedMap <KEYTYPE2, VALUETYPE>>
{
  public MultiTreeMapLinkedHashMapBased ()
  {}

  public MultiTreeMapLinkedHashMapBased (@Nullable final Comparator <? super KEYTYPE1> aComparator)
  {
    super (aComparator);
  }

  public MultiTreeMapLinkedHashMapBased (@Nonnull final KEYTYPE1 aKey,
                                         @Nonnull final KEYTYPE2 aInnerKey,
                                         @Nullable final VALUETYPE aValue)
  {
    super (aKey, aInnerKey, aValue);
  }

  public MultiTreeMapLinkedHashMapBased (@Nullable final KEYTYPE1 aKey,
                                         @Nullable final Map <? extends KEYTYPE2, ? extends VALUETYPE> aValue)
  {
    super (aKey, aValue);
  }

  public MultiTreeMapLinkedHashMapBased (@Nullable final Map <? extends KEYTYPE1, ? extends Map <? extends KEYTYPE2, ? extends VALUETYPE>> aCont)
  {
    super (aCont);
  }

  @Override
  @Nonnull
  @ReturnsMutableCopy
  protected CommonsLinkedHashMap <KEYTYPE2, VALUETYPE> createNewInnerMap ()
  {
    return new CommonsLinkedHashMap<> ();
  }
}