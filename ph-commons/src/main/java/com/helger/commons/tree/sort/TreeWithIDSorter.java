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
import com.helger.commons.hierarchy.visit.DefaultHierarchyVisitorCallback;
import com.helger.commons.hierarchy.visit.EHierarchyVisitorReturn;
import com.helger.commons.id.IHasID;
import com.helger.commons.tree.IBasicTree;
import com.helger.commons.tree.IBasicTreeItem;
import com.helger.commons.tree.util.TreeVisitor;
import com.helger.commons.tree.withid.ITreeItemWithID;

/**
 * Sort {@link com.helger.commons.tree.withid.ITreeWithID} instances recursively
 * - either by ID or by value
 *
 * @author Philip Helger
 */
@Immutable
public final class TreeWithIDSorter
{
  @PresentForCodeCoverage
  private static final TreeWithIDSorter s_aInstance = new TreeWithIDSorter ();

  private TreeWithIDSorter ()
  {}

  private static <KEYTYPE, DATATYPE, ITEMTYPE extends ITreeItemWithID <KEYTYPE, DATATYPE, ITEMTYPE>> void _sort (@Nonnull final IBasicTree <DATATYPE, ITEMTYPE> aTree,
                                                                                                                 @Nonnull final Comparator <? super ITEMTYPE> aComparator)
  {
    ValueEnforcer.notNull (aTree, "Tree");
    ValueEnforcer.notNull (aComparator, "Comparator");

    // sort root manually
    aTree.getRootItem ().reorderChildrenByItems (aComparator);

    // and now start iterating
    TreeVisitor.visitTree (aTree, new DefaultHierarchyVisitorCallback <ITEMTYPE> ()
    {
      @Override
      public EHierarchyVisitorReturn onItemBeforeChildren (@Nullable final ITEMTYPE aTreeItem)
      {
        if (aTreeItem != null)
          aTreeItem.reorderChildrenByItems (aComparator);
        return EHierarchyVisitorReturn.CONTINUE;
      }
    });
  }

  /**
   * Sort each level of the passed tree on the ID with the specified comparator.
   *
   * @param <KEYTYPE>
   *        Tree item key type
   * @param <DATATYPE>
   *        Tree item data type
   * @param <ITEMTYPE>
   *        Tree item type
   * @param aTree
   *        The tree to be sorted.
   * @param aKeyComparator
   *        The comparator to be used for sorting the tree item keys on each
   *        level.
   */
  public static <KEYTYPE, DATATYPE, ITEMTYPE extends ITreeItemWithID <KEYTYPE, DATATYPE, ITEMTYPE>> void sortByID (@Nonnull final IBasicTree <DATATYPE, ITEMTYPE> aTree,
                                                                                                                   @Nonnull final Comparator <? super KEYTYPE> aKeyComparator)
  {
    _sort (aTree, Comparator.comparing (IHasID::getID, aKeyComparator));
  }

  /**
   * Sort each level of the passed tree on the ID with the specified comparator.
   * This method assumes that the IDs in the tree item implement the
   * {@link Comparable} interface.
   *
   * @param <KEYTYPE>
   *        Tree item key type
   * @param <DATATYPE>
   *        Tree item data type
   * @param <ITEMTYPE>
   *        Tree item type
   * @param aTree
   *        The tree to be sorted.
   */
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, DATATYPE, ITEMTYPE extends ITreeItemWithID <KEYTYPE, DATATYPE, ITEMTYPE>> void sortByID (@Nonnull final IBasicTree <DATATYPE, ITEMTYPE> aTree)
  {
    _sort (aTree, IHasID.getComparatorID ());
  }

  /**
   * Sort each level of the passed tree on the value with the specified
   * comparator.
   *
   * @param <KEYTYPE>
   *        Tree item key type
   * @param <DATATYPE>
   *        Tree item data type
   * @param <ITEMTYPE>
   *        Tree item type
   * @param aTree
   *        The tree to be sorted.
   * @param aValueComparator
   *        The comparator to be used for sorting the tree item keys on each
   *        level.
   */
  public static <KEYTYPE, DATATYPE, ITEMTYPE extends ITreeItemWithID <KEYTYPE, DATATYPE, ITEMTYPE>> void sortByValue (@Nonnull final IBasicTree <DATATYPE, ITEMTYPE> aTree,
                                                                                                                      @Nonnull final Comparator <? super DATATYPE> aValueComparator)
  {
    _sort (aTree, Comparator.comparing (IBasicTreeItem::getData, aValueComparator));
  }

  /**
   * Sort each level of the passed tree on the value with the specified
   * comparator. This method assumes that the values in the tree item implement
   * the {@link Comparable} interface.
   *
   * @param <KEYTYPE>
   *        Tree item key type
   * @param <DATATYPE>
   *        Tree item data type
   * @param <ITEMTYPE>
   *        Tree item type
   * @param aTree
   *        The tree to be sorted.
   */
  public static <KEYTYPE, DATATYPE extends Comparable <? super DATATYPE>, ITEMTYPE extends ITreeItemWithID <KEYTYPE, DATATYPE, ITEMTYPE>> void sortByValue (@Nonnull final IBasicTree <DATATYPE, ITEMTYPE> aTree)
  {
    _sort (aTree, Comparator.comparing (IBasicTreeItem::getData));
  }
}
