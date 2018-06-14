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

import java.util.Collections;
import java.util.NavigableMap;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.CodingStyleguideUnaware;
import com.helger.commons.annotation.ReturnsMutableCopy;

/**
 * A special {@link NavigableMap} interface based on {@link ICommonsSortedMap}.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Map key type
 * @param <VALUETYPE>
 *        Map value type
 */
public interface ICommonsNavigableMap <KEYTYPE, VALUETYPE> extends
                                      NavigableMap <KEYTYPE, VALUETYPE>,
                                      ICommonsSortedMap <KEYTYPE, VALUETYPE>
{
  @Override
  @Nonnull
  @CodingStyleguideUnaware
  default NavigableMap <KEYTYPE, VALUETYPE> getAsUnmodifiable ()
  {
    return Collections.unmodifiableNavigableMap (this);
  }

  @Nonnull
  @ReturnsMutableCopy
  ICommonsNavigableMap <KEYTYPE, VALUETYPE> getClone ();
}
