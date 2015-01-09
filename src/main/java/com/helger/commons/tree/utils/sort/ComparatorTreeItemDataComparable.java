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
package com.helger.commons.tree.utils.sort;

import java.util.Comparator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.compare.AbstractPartComparatorComparable;
import com.helger.commons.compare.ESortOrder;
import com.helger.commons.tree.IBasicTreeItem;

/**
 * Comparator for sorting {@link IBasicTreeItem} items by their value using an
 * comparable value types.<br>
 * Works for {@link com.helger.commons.tree.simple.ITreeItem} and
 * {@link com.helger.commons.tree.withid.ITreeItemWithID}.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        tree item value type
 * @param <ITEMTYPE>
 *        tree item implementation type
 */
public class ComparatorTreeItemDataComparable <DATATYPE extends Comparable <? super DATATYPE>, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> extends AbstractPartComparatorComparable <ITEMTYPE, DATATYPE>
{
  /**
   * Comparator with default sort order.
   */
  public ComparatorTreeItemDataComparable ()
  {
    super ();
  }

  /**
   * Constructor with sort order.
   *
   * @param eSortOrder
   *        The sort order to use. May not be <code>null</code>.
   */
  public ComparatorTreeItemDataComparable (@Nonnull final ESortOrder eSortOrder)
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
  public ComparatorTreeItemDataComparable (@Nullable final Comparator <? super ITEMTYPE> aNestedComparator)
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
  public ComparatorTreeItemDataComparable (@Nonnull final ESortOrder eSortOrder,
                                           @Nullable final Comparator <? super ITEMTYPE> aNestedComparator)
  {
    super (eSortOrder, aNestedComparator);
  }

  @Override
  @Nullable
  protected DATATYPE getPart (@Nonnull final ITEMTYPE aTreeItem)
  {
    return aTreeItem.getData ();
  }
}
