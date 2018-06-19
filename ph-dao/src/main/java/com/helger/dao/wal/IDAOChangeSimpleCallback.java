/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.dao.wal;

import java.io.Serializable;

import javax.annotation.Nonnull;

import com.helger.commons.id.IHasID;

/**
 * Simple version of the DAO callback that calls {@link #onChange(IHasID)} for
 * all items.
 *
 * @author Philip Helger
 * @param <INTERFACETYPE>
 *        The interface typed used by the DAO.
 */
public interface IDAOChangeSimpleCallback <INTERFACETYPE extends IHasID <String> & Serializable> extends
                                          IDAOChangeCallback <INTERFACETYPE>
{
  /**
   * Called after an item was created, deleted or changed.
   *
   * @param aItem
   *        The effected item. Never <code>null</code>.
   */
  void onChange (@Nonnull final INTERFACETYPE aItem);

  /**
   * Called after a new item was created.
   *
   * @param aNewItem
   *        The newly created item. Never <code>null</code>.
   */
  @Override
  default void onCreateItem (@Nonnull final INTERFACETYPE aNewItem)
  {
    onChange (aNewItem);
  }

  /**
   * Called after an item was updated.
   *
   * @param aItem
   *        The updated item. Never <code>null</code>.
   */
  @Override
  default void onUpdateItem (@Nonnull final INTERFACETYPE aItem)
  {
    onChange (aItem);
  }

  /**
   * Called after an item was removed.
   *
   * @param aItem
   *        The removed item. Never <code>null</code>.
   */
  @Override
  default void onDeleteItem (@Nonnull final INTERFACETYPE aItem)
  {
    onChange (aItem);
  }

  /**
   * Called after an item was marked as deleted (but is still present - special
   * case of update!).
   *
   * @param aItem
   *        The item that was marked as deleted. Never <code>null</code>.
   */
  @Override
  default void onMarkItemDeleted (@Nonnull final INTERFACETYPE aItem)
  {
    onChange (aItem);
  }

  /**
   * Called after an item was marked as undeleted.
   *
   * @param aItem
   *        The item that was marked as not deleted anymore. Never
   *        <code>null</code>.
   */
  @Override
  default void onMarkItemUndeleted (@Nonnull final INTERFACETYPE aItem)
  {
    onChange (aItem);
  }
}
