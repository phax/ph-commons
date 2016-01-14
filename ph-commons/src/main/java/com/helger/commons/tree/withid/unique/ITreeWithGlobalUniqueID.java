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
package com.helger.commons.tree.withid.unique;

import java.util.Collection;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.hierarchy.IChildrenProviderWithID;
import com.helger.commons.state.EChange;
import com.helger.commons.tree.withid.ITreeItemWithID;
import com.helger.commons.tree.withid.ITreeWithID;

/**
 * A specialized version of the tree, where each item is required to have a
 * unique ID so that item searching can be performed with little runtime effort.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        The type of the key elements for the tree. This is typically String.
 * @param <DATATYPE>
 *        The type of the elements contained in the tree. This is the generic
 *        type to be stored in the tree.
 * @param <ITEMTYPE>
 *        The type of the tree item that will be stored in this tree.
 */
public interface ITreeWithGlobalUniqueID <KEYTYPE, DATATYPE, ITEMTYPE extends ITreeItemWithID <KEYTYPE, DATATYPE, ITEMTYPE>>
                                         extends
                                         ITreeWithID <KEYTYPE, DATATYPE, ITEMTYPE>,
                                         IChildrenProviderWithID <KEYTYPE, ITEMTYPE>
{
  /**
   * Check if a tree item corresponding to the given ID is present.
   *
   * @param aDataID
   *        The ID of the tree item to search.
   * @return <code>true</code> if such an item is present
   */
  boolean containsItemWithID (@Nullable KEYTYPE aDataID);

  /**
   * Get the {@link ITreeItemWithID} that corresponds to the given ID.
   *
   * @param aDataID
   *        The ID of the tree item to search.
   * @return <code>null</code> if no such tree item exists.
   */
  @Nullable
  ITEMTYPE getItemWithID (@Nullable KEYTYPE aDataID);

  /**
   * Get the data of the tree item that corresponds to the given ID.
   *
   * @param aDataID
   *        The ID of the tree item to search.
   * @return <code>null</code> if no such tree item exists.
   */
  @Nullable
  DATATYPE getItemDataWithID (@Nullable KEYTYPE aDataID);

  /**
   * @return The number of all contained items. Always &ge; 0.
   */
  @Nonnegative
  int getItemCount ();

  /**
   * @return A non-<code>null</code> collection of all items.
   */
  @Nonnull
  Collection <ITEMTYPE> getAllItems ();

  /**
   * @return A non-<code>null</code> collection of all item datas.
   */
  @Nonnull
  Collection <DATATYPE> getAllItemDatas ();

  /**
   * Remove the item with the specified ID
   *
   * @param aDataID
   *        The ID of the item to be removed
   * @return {@link EChange#CHANGED} if the item was removed,
   *         {@link EChange#UNCHANGED} otherwise. Never <code>null</code>.
   */
  @Nonnull
  EChange removeItemWithID (@Nullable KEYTYPE aDataID);

  /**
   * Check if one item is equal or a child of the other item. This relationship
   * is checked not only for direct children but for all levels.
   *
   * @param aParentItemID
   *        The parent item ID to a validate.
   * @param aChildItemID
   *        The item ID to check whether it is a child of the passed parent
   *        folder.
   * @return <code>true</code> if the child item is the same or a child of the
   *         parent item, <code>false</code> if one of the IDs could not be
   *         resolved or they are not in a parent-child-relationship.
   */
  boolean isItemSameOrDescendant (@Nullable KEYTYPE aParentItemID, @Nullable KEYTYPE aChildItemID);
}
