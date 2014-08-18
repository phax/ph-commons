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
package com.helger.commons.tree.utils.sort;

import java.util.Comparator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.compare.ESortOrder;
import com.helger.commons.tree.simple.DefaultTreeItem;

/**
 * Comparator for sorting {@link DefaultTreeItem} items by their value using an
 * explicit {@link Comparator}.
 * 
 * @author Philip Helger
 * @param <DATATYPE>
 *        tree item value type
 */
public final class ComparatorDefaultTreeItem <DATATYPE> extends
                                                        ComparatorTreeItemData <DATATYPE, DefaultTreeItem <DATATYPE>>
{
  /**
   * Constructor with default sort order.
   * 
   * @param aDataComparator
   *        Comparator for the data elements. May not be <code>null</code>.
   */
  public ComparatorDefaultTreeItem (@Nonnull final Comparator <? super DATATYPE> aDataComparator)
  {
    super (aDataComparator);
  }

  /**
   * Constructor with sort order.
   * 
   * @param aDataComparator
   *        Comparator for the data elements. May not be <code>null</code>.
   * @param eSortOrder
   *        The sort order to use. May not be <code>null</code>.
   */
  public ComparatorDefaultTreeItem (@Nonnull final ESortOrder eSortOrder,
                                    @Nonnull final Comparator <? super DATATYPE> aDataComparator)
  {
    super (eSortOrder, aDataComparator);
  }

  /**
   * Comparator with default sort order and a nested comparator.
   * 
   * @param aNestedComparator
   *        The nested comparator to be invoked, when the main comparison
   *        resulted in 0.
   * @param aDataComparator
   *        The comparator for comparing the IDs. May not be <code>null</code>.
   */
  public ComparatorDefaultTreeItem (@Nullable final Comparator <? super DefaultTreeItem <DATATYPE>> aNestedComparator,
                                    @Nonnull final Comparator <? super DATATYPE> aDataComparator)
  {
    super (aNestedComparator, aDataComparator);
  }

  /**
   * Constructor with sort order and a nested comparator.
   * 
   * @param eSortOrder
   *        The sort order to use. May not be <code>null</code>.
   * @param aNestedComparator
   *        The nested comparator to be invoked, when the main comparison
   *        resulted in 0.
   * @param aDataComparator
   *        The comparator for comparing the IDs. May not be <code>null</code>.
   */
  public ComparatorDefaultTreeItem (@Nonnull final ESortOrder eSortOrder,
                                    @Nullable final Comparator <? super DefaultTreeItem <DATATYPE>> aNestedComparator,
                                    @Nonnull final Comparator <? super DATATYPE> aDataComparator)
  {
    super (eSortOrder, aNestedComparator, aDataComparator);
  }
}
