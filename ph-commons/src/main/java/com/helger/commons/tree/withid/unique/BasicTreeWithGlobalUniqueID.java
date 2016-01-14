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
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.tree.withid.BasicTreeWithID;
import com.helger.commons.tree.withid.ITreeItemWithID;

/**
 * A managed tree is a specialized version of the tree, where each item is
 * required to have a unique ID so that item searching can be performed quite
 * easily.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        The type of the key elements for the tree.
 * @param <DATATYPE>
 *        The type of the elements contained in the tree
 * @param <ITEMTYPE>
 *        tree item type
 */
@NotThreadSafe
public class BasicTreeWithGlobalUniqueID <KEYTYPE, DATATYPE, ITEMTYPE extends ITreeItemWithID <KEYTYPE, DATATYPE, ITEMTYPE>>
                                         extends BasicTreeWithID <KEYTYPE, DATATYPE, ITEMTYPE>
                                         implements ITreeWithGlobalUniqueID <KEYTYPE, DATATYPE, ITEMTYPE>
{
  private final ITreeItemWithUniqueIDFactory <KEYTYPE, DATATYPE, ITEMTYPE> m_aFactory;

  public BasicTreeWithGlobalUniqueID (@Nonnull final ITreeItemWithUniqueIDFactory <KEYTYPE, DATATYPE, ITEMTYPE> aFactory)
  {
    super (aFactory);
    m_aFactory = aFactory;
  }

  /**
   * @return The factory used for creation. For internal use only.
   */
  @Nonnull
  protected final ITreeItemWithUniqueIDFactory <KEYTYPE, DATATYPE, ITEMTYPE> getFactory ()
  {
    return m_aFactory;
  }

  public final boolean containsItemWithID (@Nullable final KEYTYPE aDataID)
  {
    return m_aFactory.containsItemWithDataID (aDataID);
  }

  @Nullable
  public final ITEMTYPE getItemWithID (@Nullable final KEYTYPE aDataID)
  {
    return m_aFactory.getItemOfDataID (aDataID);
  }

  @Nullable
  public final DATATYPE getItemDataWithID (@Nullable final KEYTYPE aDataID)
  {
    final ITEMTYPE aItem = getItemWithID (aDataID);
    return aItem == null ? null : aItem.getData ();
  }

  @Nullable
  public final ITEMTYPE getChildWithID (@Nullable final ITEMTYPE aCurrentItem, @Nullable final KEYTYPE aDataID)
  {
    final ITEMTYPE aItem = aCurrentItem != null ? aCurrentItem : getRootItem ();
    return aItem.getChildItemOfDataID (aDataID);
  }

  @Nonnegative
  public final int getItemCount ()
  {
    return m_aFactory.getItemCount ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public final Collection <ITEMTYPE> getAllItems ()
  {
    return m_aFactory.getAllItems ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public final Collection <DATATYPE> getAllItemDatas ()
  {
    return m_aFactory.getAllItemDatas ();
  }

  @Nonnull
  public final EChange removeItemWithID (@Nullable final KEYTYPE aDataID)
  {
    final ITEMTYPE aItem = getItemWithID (aDataID);
    if (aItem == null)
      return EChange.UNCHANGED;
    final ITEMTYPE aParent = aItem.getParent ();
    if (aParent == null)
      throw new IllegalArgumentException ("Cannot remove the root item!");
    if (aParent.removeChild (aDataID).isUnchanged ())
      throw new IllegalStateException ("Failed to remove child " + aItem + " from parent " + aParent);
    return EChange.CHANGED;
  }

  public final boolean isItemSameOrDescendant (@Nullable final KEYTYPE aParentItemID,
                                               @Nullable final KEYTYPE aChildItemID)
  {
    final ITEMTYPE aSearchParent = getItemWithID (aParentItemID);
    if (aSearchParent == null)
      return false;

    final ITEMTYPE aChild = getItemWithID (aChildItemID);
    return aChild != null && aChild.isSameOrChildOf (aSearchParent);

  }

  public boolean hasChildren (@Nullable final ITEMTYPE aItem)
  {
    return aItem == null ? getRootItem ().hasChildren () : aItem.hasChildren ();
  }

  @Nonnegative
  public int getChildCount (@Nullable final ITEMTYPE aItem)
  {
    return aItem == null ? getRootItem ().getChildCount () : aItem.getChildCount ();
  }

  @Nullable
  public List <? extends ITEMTYPE> getAllChildren (@Nullable final ITEMTYPE aItem)
  {
    return aItem == null ? getRootItem ().getAllChildren () : aItem.getAllChildren ();
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!super.equals (o))
      return false;
    final BasicTreeWithGlobalUniqueID <?, ?, ?> rhs = (BasicTreeWithGlobalUniqueID <?, ?, ?>) o;
    return m_aFactory.equals (rhs.m_aFactory);
  }

  @Override
  public int hashCode ()
  {
    return HashCodeGenerator.getDerived (super.hashCode ()).append (m_aFactory).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("factory", m_aFactory).toString ();
  }
}
