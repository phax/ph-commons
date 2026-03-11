/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.collection.commons;

import java.util.EnumMap;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.style.ReturnsMutableCopy;

/**
 * A special {@link EnumMap} implementation based on {@link ICommonsMap}.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Map key type
 * @param <VALUETYPE>
 *        Map value type
 */
public class CommonsEnumMap <KEYTYPE extends Enum <KEYTYPE>, VALUETYPE> extends EnumMap <KEYTYPE, VALUETYPE> implements
                            ICommonsMap <KEYTYPE, VALUETYPE>
{
  /**
   * Create a new enum map with the specified key class.
   *
   * @param aKeyClass
   *        The enum class used as the key type. May not be <code>null</code>.
   */
  public CommonsEnumMap (@NonNull final Class <KEYTYPE> aKeyClass)
  {
    super (aKeyClass);
  }

  /**
   * Create a new enum map with the same entries as the provided enum map.
   *
   * @param aMap
   *        The enum map to copy entries from. May not be <code>null</code>.
   */
  public CommonsEnumMap (@NonNull final EnumMap <KEYTYPE, ? extends VALUETYPE> aMap)
  {
    super (aMap);
  }

  // Cannot overwrite createInstance because of the special key type generics
  // constraints

  /**
   * @return A mutable copy of this map. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public CommonsEnumMap <KEYTYPE, VALUETYPE> getClone ()
  {
    return new CommonsEnumMap <> (this);
  }
}
