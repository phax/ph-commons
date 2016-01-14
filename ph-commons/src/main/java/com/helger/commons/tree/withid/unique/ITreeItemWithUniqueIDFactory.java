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

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.tree.withid.ITreeItemWithID;
import com.helger.commons.tree.withid.ITreeItemWithIDFactory;

/**
 * An abstract tree item factory that maintains a unique ID over all items!
 *
 * @param <KEYTYPE>
 *        The key type.
 * @param <DATATYPE>
 *        The value type to be contained in tree items.
 * @param <ITEMTYPE>
 *        tree item type
 * @author Philip Helger
 */
public interface ITreeItemWithUniqueIDFactory <KEYTYPE, DATATYPE, ITEMTYPE extends ITreeItemWithID <KEYTYPE, DATATYPE, ITEMTYPE>>
                                              extends ITreeItemWithIDFactory <KEYTYPE, DATATYPE, ITEMTYPE>
{
  /**
   * Check if an item with the given ID is contained.
   *
   * @param aDataID
   *        The data ID to look up.
   * @return <code>true</code> if such an item is contained, <code>false</code>
   *         otherwise.
   */
  boolean containsItemWithDataID (@Nullable KEYTYPE aDataID);

  /**
   * Try to retrieve the stored item with the given ID.
   *
   * @param aDataID
   *        The data ID to look up.
   * @return <code>null</code> if no such item is contained, the item otherwise.
   */
  @Nullable
  ITEMTYPE getItemOfDataID (@Nullable KEYTYPE aDataID);

  /**
   * @return The number of all contained items. Always &ge; 0.
   */
  @Nonnegative
  int getItemCount ();

  /**
   * @return A collection that contains all items created by this factory
   *         instance.
   */
  @Nonnull
  @ReturnsMutableCopy
  Collection <ITEMTYPE> getAllItems ();

  /**
   * @return A collection that contains all item datas created by this factory
   *         instance.
   */
  @Nonnull
  @ReturnsMutableCopy
  Collection <DATATYPE> getAllItemDatas ();
}
