/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.tree.util;

import com.helger.annotation.Nonnull;
import com.helger.annotation.Nullable;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.misc.PresentForCodeCoverage;
import com.helger.annotation.misc.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.hierarchy.visit.DefaultHierarchyVisitorCallback;
import com.helger.commons.hierarchy.visit.EHierarchyVisitorReturn;
import com.helger.tree.IBasicTree;
import com.helger.tree.withid.ITreeItemWithID;

/**
 * A utility class that helps searching items within trees.
 *
 * @author Philip Helger
 */
@Immutable
public final class TreeWithIDSearcher
{
  @PresentForCodeCoverage
  private static final TreeWithIDSearcher INSTANCE = new TreeWithIDSearcher ();

  private TreeWithIDSearcher ()
  {}

  /**
   * Fill all items with the same ID by linearly scanning of the tree.
   *
   * @param <KEYTYPE>
   *        tree ID type
   * @param <DATATYPE>
   *        tree data type
   * @param <ITEMTYPE>
   *        tree item type
   * @param aTree
   *        The tree to search. May not be <code>null</code>.
   * @param aSearchID
   *        The ID to search. May not be <code>null</code>.
   * @return A non-<code>null</code> list with all matching items.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, DATATYPE, ITEMTYPE extends ITreeItemWithID <KEYTYPE, DATATYPE, ITEMTYPE>> ICommonsList <ITEMTYPE> findAllItemsWithIDRecursive (@Nonnull final IBasicTree <DATATYPE, ITEMTYPE> aTree,
                                                                                                                                                         @Nullable final KEYTYPE aSearchID)
  {
    return findAllItemsWithIDRecursive (aTree.getRootItem (), aSearchID);
  }

  /**
   * Fill all items with the same ID by linearly scanning the tree.
   *
   * @param <KEYTYPE>
   *        tree ID type
   * @param <DATATYPE>
   *        tree data type
   * @param <ITEMTYPE>
   *        tree item type
   * @param aTreeItem
   *        The tree item to search. May not be <code>null</code>.
   * @param aSearchID
   *        The ID to search. May not be <code>null</code>.
   * @return A non-<code>null</code> list with all matching items.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <KEYTYPE, DATATYPE, ITEMTYPE extends ITreeItemWithID <KEYTYPE, DATATYPE, ITEMTYPE>> ICommonsList <ITEMTYPE> findAllItemsWithIDRecursive (@Nonnull final ITEMTYPE aTreeItem,
                                                                                                                                                         @Nullable final KEYTYPE aSearchID)
  {
    final ICommonsList <ITEMTYPE> aRetList = new CommonsArrayList <> ();
    TreeVisitor.visitTreeItem (aTreeItem, new DefaultHierarchyVisitorCallback <ITEMTYPE> ()
    {
      @Override
      @Nonnull
      public EHierarchyVisitorReturn onItemBeforeChildren (@Nullable final ITEMTYPE aItem)
      {
        if (aItem != null && aItem.getID ().equals (aSearchID))
          aRetList.add (aItem);
        return EHierarchyVisitorReturn.CONTINUE;
      }
    });
    return aRetList;
  }
}
