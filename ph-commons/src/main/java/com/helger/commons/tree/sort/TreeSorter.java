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
package com.helger.commons.tree.sort;

import java.util.Comparator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.hierarchy.visit.ChildrenProviderHierarchyVisitor;
import com.helger.commons.hierarchy.visit.DefaultHierarchyVisitorCallback;
import com.helger.commons.hierarchy.visit.EHierarchyVisitorReturn;
import com.helger.commons.tree.IBasicTree;
import com.helger.commons.tree.IBasicTreeItem;
import com.helger.commons.tree.ITreeItem;

/**
 * Sort {@link com.helger.commons.tree.ITree} instances recursively.
 *
 * @author Philip Helger
 */
@Immutable
public final class TreeSorter
{
  @PresentForCodeCoverage
  private static final TreeSorter s_aInstance = new TreeSorter ();

  private TreeSorter ()
  {}

  private static <DATATYPE, ITEMTYPE extends ITreeItem <DATATYPE, ITEMTYPE>> void _sort (@Nonnull final IBasicTree <? extends DATATYPE, ITEMTYPE> aTree,
                                                                                         @Nonnull final Comparator <? super ITEMTYPE> aComparator)
  {
    ValueEnforcer.notNull (aTree, "Tree");
    ValueEnforcer.notNull (aComparator, "Comparator");

    // and now start iterating (including the root item)
    ChildrenProviderHierarchyVisitor.visitFrom (aTree.getRootItem (), new DefaultHierarchyVisitorCallback <ITEMTYPE> ()
    {
      @Override
      @Nonnull
      public EHierarchyVisitorReturn onItemBeforeChildren (@Nullable final ITEMTYPE aTreeItem)
      {
        if (aTreeItem != null)
          aTreeItem.reorderChildItems (aComparator);
        return EHierarchyVisitorReturn.CONTINUE;
      }
    }, true);
  }

  /**
   * Sort each level of the passed tree with the specified comparator.
   *
   * @param aTree
   *        The tree to be sorted.
   * @param aValueComparator
   *        The comparator to be used for sorting the tree items on each level.
   * @param <DATATYPE>
   *        The tree item data type
   * @param <ITEMTYPE>
   *        The tree item type
   */
  public static <DATATYPE, ITEMTYPE extends ITreeItem <DATATYPE, ITEMTYPE>> void sort (@Nonnull final IBasicTree <? extends DATATYPE, ITEMTYPE> aTree,
                                                                                       @Nonnull final Comparator <? super DATATYPE> aValueComparator)
  {
    _sort (aTree, Comparator.comparing (IBasicTreeItem::getData, aValueComparator));
  }

  /**
   * Sort each level of the passed tree with the specified comparator. This
   * method assumes that the values in the tree item implement the
   * {@link Comparable} interface.
   *
   * @param aTree
   *        The tree to be sorted.
   * @param <DATATYPE>
   *        The tree item data type
   * @param <ITEMTYPE>
   *        The tree item type
   */
  public static <DATATYPE extends Comparable <? super DATATYPE>, ITEMTYPE extends ITreeItem <DATATYPE, ITEMTYPE>> void sort (@Nonnull final IBasicTree <DATATYPE, ITEMTYPE> aTree)
  {
    _sort (aTree, Comparator.comparing (IBasicTreeItem::getData));
  }
}
