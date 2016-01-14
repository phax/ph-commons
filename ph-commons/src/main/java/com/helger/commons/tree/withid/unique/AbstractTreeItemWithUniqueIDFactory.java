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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.tree.withid.ITreeItemWithID;

/**
 * An abstract tree item factory that maintains a unique ID over all items. It
 * does not put the root item in the data store!
 *
 * @param <KEYTYPE>
 *        The key type.
 * @param <DATATYPE>
 *        The value type to be contained in tree items.
 * @param <ITEMTYPE>
 *        tree item type
 * @author Philip Helger
 */
@NotThreadSafe
public abstract class AbstractTreeItemWithUniqueIDFactory <KEYTYPE, DATATYPE, ITEMTYPE extends ITreeItemWithID <KEYTYPE, DATATYPE, ITEMTYPE>>
                                                          implements
                                                          ITreeItemWithUniqueIDFactory <KEYTYPE, DATATYPE, ITEMTYPE>
{
  private final Map <KEYTYPE, ITEMTYPE> m_aItemStore;

  public AbstractTreeItemWithUniqueIDFactory ()
  {
    this (new HashMap <KEYTYPE, ITEMTYPE> ());
  }

  public AbstractTreeItemWithUniqueIDFactory (@Nonnull final Map <KEYTYPE, ITEMTYPE> aItemStore)
  {
    m_aItemStore = ValueEnforcer.notNull (aItemStore, "ItemStore");
  }

  @Nonnull
  protected final ITEMTYPE addToItemStore (@Nonnull final KEYTYPE aDataID, @Nonnull final ITEMTYPE aItem)
  {
    // Is the ID already in use?
    if (m_aItemStore.containsKey (aDataID))
      throw new IllegalArgumentException ("An item with ID '" + aDataID + "' is already contained!");
    m_aItemStore.put (aDataID, aItem);
    return aItem;
  }

  @Nonnull
  protected abstract ITEMTYPE internalCreate (@Nonnull final ITEMTYPE aParent, @Nonnull final KEYTYPE aDataID);

  /**
   * Get the ID of the passed tree item to use for internal storage.
   *
   * @param aItem
   *        The item who's ID is to be resolved.
   * @return The ID of the item
   */
  @Nonnull
  @OverrideOnDemand
  protected KEYTYPE internalGetItemID (@Nonnull final ITEMTYPE aItem)
  {
    return aItem.getID ();
  }

  @Nonnull
  public final ITEMTYPE create (@Nonnull final ITEMTYPE aParent, @Nonnull final KEYTYPE aDataID)
  {
    ValueEnforcer.notNull (aParent, "Parent");

    // Create and store the item via the default factory
    final ITEMTYPE aTreeItem = internalCreate (aParent, aDataID);
    return addToItemStore (internalGetItemID (aTreeItem), aTreeItem);
  }

  public final void onRemoveItem (@Nonnull final ITEMTYPE aTreeItem)
  {
    // Remove item from item store
    m_aItemStore.remove (internalGetItemID (aTreeItem));
  }

  public final void onAddItem (@Nonnull final ITEMTYPE aTreeItem)
  {
    // Add item to item store
    addToItemStore (internalGetItemID (aTreeItem), aTreeItem);
  }

  public final boolean containsItemWithDataID (@Nullable final KEYTYPE aDataID)
  {
    return m_aItemStore.containsKey (aDataID);
  }

  @Nullable
  public final ITEMTYPE getItemOfDataID (@Nullable final KEYTYPE aDataID)
  {
    return m_aItemStore.get (aDataID);
  }

  @Nonnegative
  public final int getItemCount ()
  {
    return m_aItemStore.size ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public final List <ITEMTYPE> getAllItems ()
  {
    return CollectionHelper.newList (m_aItemStore.values ());
  }

  @Nonnull
  @ReturnsMutableCopy
  public final List <DATATYPE> getAllItemDatas ()
  {
    final List <DATATYPE> ret = new ArrayList <DATATYPE> ();
    for (final ITEMTYPE aItem : m_aItemStore.values ())
      ret.add (aItem.getData ());
    return ret;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final AbstractTreeItemWithUniqueIDFactory <?, ?, ?> rhs = (AbstractTreeItemWithUniqueIDFactory <?, ?, ?>) o;
    return m_aItemStore.equals (rhs.m_aItemStore);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aItemStore).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("itemStoreKeys", m_aItemStore.keySet ()).toString ();
  }
}
