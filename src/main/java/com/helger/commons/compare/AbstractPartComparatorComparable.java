/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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

import java.util.Comparator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
public abstract class AbstractPartComparatorComparable <DATATYPE, PARTTYPE extends Comparable <? super PARTTYPE>> extends
                                                                                                                  AbstractComparator <DATATYPE>
{
  /**
   * Comparator with default sort order.
   */
  public AbstractPartComparatorComparable ()
  {
    super ();
  }

  /**
   * Constructor with sort order.
   * 
   * @param eSortOrder
   *        The sort order to use. May not be <code>null</code>.
   */
  public AbstractPartComparatorComparable (@Nonnull final ESortOrder eSortOrder)
  {
    super (eSortOrder);
  }

  /**
   * Comparator with default sort order and a nested comparator.
   * 
   * @param aNestedComparator
   *        The nested comparator to be invoked, when the main comparison
   *        resulted in 0.
   */
  public AbstractPartComparatorComparable (@Nullable final Comparator <? super DATATYPE> aNestedComparator)
  {
    super (aNestedComparator);
  }

  /**
   * Comparator with sort order and a nested comparator.
   * 
   * @param eSortOrder
   *        The sort order to use. May not be <code>null</code>.
   * @param aNestedComparator
   *        The nested comparator to be invoked, when the main comparison
   *        resulted in 0.
   */
  public AbstractPartComparatorComparable (@Nonnull final ESortOrder eSortOrder,
                                           @Nullable final Comparator <? super DATATYPE> aNestedComparator)
  {
    super (eSortOrder, aNestedComparator);
  }

  @Nullable
  protected abstract PARTTYPE getPart (DATATYPE aObject);

  @Override
  protected final int mainCompare (final DATATYPE aElement1, final DATATYPE aElement2)
  {
    final PARTTYPE aPart1 = getPart (aElement1);
    final PARTTYPE aPart2 = getPart (aElement2);
    return CompareUtils.nullSafeCompare (aPart1, aPart2);
  }
}
