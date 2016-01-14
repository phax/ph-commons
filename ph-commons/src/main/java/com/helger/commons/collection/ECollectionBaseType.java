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
package com.helger.commons.collection;

/**
 * This enum differentiates the different meta container types.
 *
 * @author Philip Helger
 */
public enum ECollectionBaseType
{
  /**
   * The collection type applies to all Collection objects that are not Sets,
   * and will be compared in their regular order.
   */
  COLLECTION,
  /** Sets represent unordered container */
  SET,
  /** Maps are key-value-containers */
  MAP,
  /** Arrays */
  ARRAY,
  /** Iterator */
  ITERATOR,
  /** Iterable */
  ITERABLE,
  /** Enumeration */
  ENUMERATION
}
