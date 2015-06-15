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
package com.helger.commons.tree.sort;

import java.util.Comparator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.compare.AbstractPartComparator;
import com.helger.commons.tree.IBasicTreeItem;

/**
 * Comparator for sorting {@link IBasicTreeItem} items by their data using an
 * explicit {@link Comparator}.<br>
 * Works for {@link com.helger.commons.tree.ITreeItem} and
 * {@link com.helger.commons.tree.withid.ITreeItemWithID}.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        tree item value type
 * @param <ITEMTYPE>
 *        tree item implementation type
 */
@NotThreadSafe
public class ComparatorTreeItemData <DATATYPE, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> extends AbstractPartComparator <ITEMTYPE, DATATYPE>
{
  /**
   * Constructor with default sort order.
   *
   * @param aDataComparator
   *        Comparator for the data elements. May not be <code>null</code>.
   */
  public ComparatorTreeItemData (@Nonnull final Comparator <? super DATATYPE> aDataComparator)
  {
    super (aDataComparator);
  }

  @Override
  @Nullable
  protected DATATYPE getPart (@Nonnull final ITEMTYPE aTreeItem)
  {
    return aTreeItem.getData ();
  }
}
