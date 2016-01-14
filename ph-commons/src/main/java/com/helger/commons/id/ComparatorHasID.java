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
package com.helger.commons.id;

import java.util.Comparator;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.compare.PartComparator;

/**
 * This is a {@link java.util.Comparator} for objects that implement the
 * {@link IHasID} interface with a class that does NOT implements
 * {@link Comparable}.
 *
 * @author Philip Helger
 * @param <IDTYPE>
 *        The type of the ID
 * @param <DATATYPE>
 *        The type of elements to be compared.
 */
@NotThreadSafe
public class ComparatorHasID <IDTYPE, DATATYPE extends IHasID <IDTYPE>> extends PartComparator <DATATYPE, IDTYPE>
{
  /**
   * Comparator with default sort order and no nested comparator.
   *
   * @param aIDComparator
   *        The comparator for comparing the IDs. May not be <code>null</code>.
   */
  public ComparatorHasID (@Nonnull final Comparator <? super IDTYPE> aIDComparator)
  {
    super (aIDComparator, aObject -> aObject.getID ());
  }
}
