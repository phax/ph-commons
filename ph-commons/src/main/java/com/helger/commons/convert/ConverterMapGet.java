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
package com.helger.commons.convert;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;

/**
 * An implementation of {@link IConverter} that converts from a map key to a map
 * value
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Map key type
 * @param <VALUETYPE>
 *        Map value type
 */
public class ConverterMapGet <KEYTYPE, VALUETYPE> implements IConverter <KEYTYPE, VALUETYPE>
{
  private final Map <KEYTYPE, VALUETYPE> m_aMap;

  public ConverterMapGet (@Nonnull final Map <KEYTYPE, VALUETYPE> aMap)
  {
    m_aMap = ValueEnforcer.notNull (aMap, "Map");
  }

  @Nonnull
  @ReturnsMutableCopy
  public Map <KEYTYPE, VALUETYPE> getMap ()
  {
    return CollectionHelper.newMap (m_aMap);
  }

  @Nullable
  public VALUETYPE apply (@Nullable final KEYTYPE aKey)
  {
    return m_aMap.get (aKey);
  }
}
