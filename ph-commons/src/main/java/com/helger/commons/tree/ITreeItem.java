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
package com.helger.commons.tree;

import java.util.Comparator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.state.EChange;

/**
 * Base interface for simple tree items
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        tree item value type
 * @param <ITEMTYPE>
 *        tree item implementation type
 */
public interface ITreeItem <DATATYPE, ITEMTYPE extends ITreeItem <DATATYPE, ITEMTYPE>>
                           extends IBasicTreeItem <DATATYPE, ITEMTYPE>
{
  /**
   * @return The factory used to create instances of this interface.
   */
  @Nonnull
  ITreeItemFactory <DATATYPE, ITEMTYPE> getFactory ();

  /**
   * Add an existing child to this tree item. Use only internally!
   *
   * @param aChild
   *        The child to be added. May not be <code>null</code>.
   * @return {@link EChange#UNCHANGED} if the child is already contained,
   *         {@link EChange#CHANGED} upon success.
   */
  @Nonnull
  EChange internalAddChild (@Nonnull ITEMTYPE aChild);

  /**
   * Add a child item to this item.
   *
   * @param aData
   *        the data associated with this item
   * @return the created TreeItem object
   */
  @Nonnull
  ITEMTYPE createChildItem (@Nullable DATATYPE aData);

  /**
   * Remove the passed node as a child node from this node.
   *
   * @param aChild
   *        The child to be removed. May not be <code>null</code>.
   * @return {@link EChange#CHANGED} if the removal succeeded,
   *         {@link EChange#UNCHANGED} otherwise
   */
  @Nonnull
  EChange removeChild (@Nonnull ITEMTYPE aChild);

  /**
   * Reorder the child items based on the item itself.
   *
   * @param aComparator
   *        The comparator use. May not be <code>null</code>.
   */
  void reorderChildItems (@Nonnull Comparator <? super ITEMTYPE> aComparator);
}
