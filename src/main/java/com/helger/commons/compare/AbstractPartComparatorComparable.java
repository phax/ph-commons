/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.compare;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * This class is an {@link AbstractComparator} that extracts a certain data
 * element from the main object to compare.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The data type to be compared
 * @param <PARTTYPE>
 *        The part type that is extracted from the data element and compared
 */
@NotThreadSafe
public abstract class AbstractPartComparatorComparable <DATATYPE, PARTTYPE extends Comparable <? super PARTTYPE>> extends AbstractComparator <DATATYPE>
{
  /**
   * Comparator with default sort order.
   */
  public AbstractPartComparatorComparable ()
  {}

  /**
   * Implement this method to extract the part to compare from the original
   * object.
   *
   * @param aObject
   *        The object to be compared. Never <code>null</code>.
   * @return The part of the source object to be compared with the part
   *         comparator provided in the constructor. May be <code>null</code>.
   */
  @Nullable
  protected abstract PARTTYPE getPart (@Nonnull DATATYPE aObject);

  @Override
  protected final int mainCompare (@Nonnull final DATATYPE aElement1, @Nonnull final DATATYPE aElement2)
  {
    final PARTTYPE aPart1 = getPart (aElement1);
    final PARTTYPE aPart2 = getPart (aElement2);

    // The extracted parts may be null again so use check order of null values
    return CompareHelper.nullSafeCompare (aPart1, aPart2, isNullValuesComeFirst ());
  }
}
