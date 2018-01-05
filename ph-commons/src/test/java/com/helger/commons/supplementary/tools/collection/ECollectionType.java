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
package com.helger.commons.supplementary.tools.collection;

import javax.annotation.Nonnull;

enum ECollectionType
{
  LIST ("CommonsArrayList", "List", "List"),
  VECTOR ("CommonsVector", "List", "Vector"),
  SET ("CommonsHashSet", "Set", "Set"),
  ORDERED_SET ("CommonsLinkedHashSet", "Set", "OrderedSet"),
  SORTED_SET ("CommonsTreeSet", "Set", "SortedSet"),
  MAP ("CommonsHashMap", "Map", "Map"),
  ORDERED_MAP ("CommonsLinkedHashMap", "Map", "OrderedMap"),
  SORTED_MAP ("CommonsTreeMap", "Map", "SortedMap"),
  STACK ("NonBlockingStack", "List", "Stack"),
  QUEUE ("PriorityQueue", "Collection", "Queue");

  final String m_sClassName;
  final String m_sSuffix;
  final String m_sUnmodifiableClassName;

  private ECollectionType (@Nonnull final String sClassName,
                           @Nonnull final String sUnmodifiableClassName,
                           @Nonnull final String sSuffix)
  {
    m_sClassName = sClassName;
    m_sSuffix = sSuffix;
    m_sUnmodifiableClassName = sUnmodifiableClassName;
  }

  public boolean isMap ()
  {
    return this == MAP || this == ORDERED_MAP || this == SORTED_MAP;
  }

  public boolean isSorted ()
  {
    return this == SORTED_SET || this == SORTED_MAP;
  }
}
