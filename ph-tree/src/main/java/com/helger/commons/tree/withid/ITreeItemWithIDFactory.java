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
package com.helger.commons.tree.withid;

import javax.annotation.Nonnull;

import com.helger.commons.factory.IHierarchicalFactoryWithParameter;
import com.helger.commons.factory.IHierarchicalRootFactory;

/**
 * A factory interface that creates tree items.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        The key type.
 * @param <DATATYPE>
 *        The value type to be contained in tree items.
 * @param <ITEMTYPE>
 *        tree item type
 */
public interface ITreeItemWithIDFactory <KEYTYPE, DATATYPE, ITEMTYPE extends ITreeItemWithID <KEYTYPE, DATATYPE, ITEMTYPE>>
                                        extends
                                        IHierarchicalFactoryWithParameter <ITEMTYPE, KEYTYPE>,
                                        IHierarchicalRootFactory <ITEMTYPE>
{
  /**
   * {@inheritDoc}
   */
  @Nonnull
  ITEMTYPE createRoot ();

  /**
   * {@inheritDoc}
   */
  ITEMTYPE create (@Nonnull ITEMTYPE aParent, @Nonnull KEYTYPE aDataID);

  /**
   * To be called once a tree item is removed from the owning tree. This method
   * is mainly important for the tree with globally unique IDs.
   *
   * @param aItem
   *        The item that was removed.
   */
  void onRemoveItem (@Nonnull ITEMTYPE aItem);

  /**
   * To be called once a tree item is added to the owning tree. This method is
   * mainly important for the tree with globally unique IDs.
   *
   * @param aItem
   *        The item that was added.
   */
  void onAddItem (@Nonnull ITEMTYPE aItem);
}
