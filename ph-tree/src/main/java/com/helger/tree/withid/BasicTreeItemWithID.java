/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.tree.withid;

import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.OverrideOnDemand;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.reflection.GenericReflection;
import com.helger.base.state.EChange;
import com.helger.base.state.ESuccess;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.ICommonsIterable;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsMap;
import com.helger.collection.commons.ICommonsSet;

/**
 * Basic tree item with ID implementation, independent of the implementation type.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        tree item key type
 * @param <DATATYPE>
 *        tree item value type
 * @param <ITEMTYPE>
 *        tree item implementation type
 */
@NotThreadSafe
public class BasicTreeItemWithID <KEYTYPE, DATATYPE, ITEMTYPE extends ITreeItemWithID <KEYTYPE, DATATYPE, ITEMTYPE>>
                                 implements
                                 ITreeItemWithID <KEYTYPE, DATATYPE, ITEMTYPE>
{
  // item factory
  private final ITreeItemWithIDFactory <KEYTYPE, DATATYPE, ITEMTYPE> m_aFactory;

  // parent tree item
  private ITEMTYPE m_aParent;

  // ID of this item
  private final KEYTYPE m_aDataID;

  // the data to be stored
  private DATATYPE m_aData;

  // child map & list
  private ICommonsMap <KEYTYPE, ITEMTYPE> m_aChildMap = null;
  private ICommonsList <ITEMTYPE> m_aChildren = null;

  /**
   * Constructor for root object with a <code>null</code> data ID
   *
   * @param aFactory
   *        The tree item factory to use. May not be <code>null</code>.
   */
  public BasicTreeItemWithID (@NonNull final ITreeItemWithIDFactory <KEYTYPE, DATATYPE, ITEMTYPE> aFactory)
  {
    this (aFactory, null);
  }

  /**
   * Constructor for root object
   *
   * @param aFactory
   *        The tree item factory to use. May not be <code>null</code>.
   * @param aDataID
   *        The data ID to use for the root item. May be <code>null</code>.
   */
  public BasicTreeItemWithID (@NonNull final ITreeItemWithIDFactory <KEYTYPE, DATATYPE, ITEMTYPE> aFactory,
                              @Nullable final KEYTYPE aDataID)
  {
    m_aFactory = ValueEnforcer.notNull (aFactory, "Factory");
    m_aDataID = aDataID;
  }

  /**
   * Constructor for normal elements
   *
   * @param aParent
   *        Parent item. May never be <code>null</code> since only the root has no parent.
   * @param aDataID
   *        The ID of the new item. May not be <code>null</code>.
   */
  public BasicTreeItemWithID (@NonNull final ITEMTYPE aParent, @NonNull final KEYTYPE aDataID)
  {
    ValueEnforcer.notNull (aParent, "Parent");
    if (!(aParent instanceof BasicTreeItemWithID <?, ?, ?>))
      throw new IllegalArgumentException ("Parent is no BasicTreeItemWithID");
    if (aParent.getFactory () == null)
      throw new IllegalStateException ("Parent item has no factory!");
    ValueEnforcer.notNull (aDataID, "DataID");
    m_aParent = aParent;
    m_aFactory = m_aParent.getFactory ();
    m_aDataID = aDataID;
  }

  /**
   * @return The tree item factory used for creating new tree items.
   *         Never <code>null</code>.
   */
  @NonNull
  public final ITreeItemWithIDFactory <KEYTYPE, DATATYPE, ITEMTYPE> getFactory ()
  {
    return m_aFactory;
  }

  /**
   * This method is called to validate a data ID object. This method may be overloaded in derived
   * classes. The default implementation accepts all values.
   *
   * @param aDataID
   *        The value to validate.
   * @return <code>true</code> if the ID is valid, <code>false</code> otherwise.
   */
  @OverrideOnDemand
  protected boolean isValidDataID (final KEYTYPE aDataID)
  {
    return true;
  }

  /**
   * This method is called to validate a data object. This method may be overloaded in derived
   * classes. The default implementation accepts all values.
   *
   * @param aData
   *        The value to validate.
   * @return <code>true</code> if the ID is valid, <code>false</code> otherwise.
   */
  @OverrideOnDemand
  protected boolean isValidData (final DATATYPE aData)
  {
    return true;
  }

  /**
   * @return <code>true</code> if this is the root item (i.e. has no parent),
   *         <code>false</code> otherwise.
   */
  public final boolean isRootItem ()
  {
    return m_aParent == null;
  }

  @NonNull
  private ITEMTYPE _asT (@NonNull final BasicTreeItemWithID <KEYTYPE, DATATYPE, ITEMTYPE> aItem)
  {
    return GenericReflection.uncheckedCast (aItem);
  }

  /**
   * @return The parent tree item or <code>null</code> if this is the root item.
   */
  @Nullable
  public final ITEMTYPE getParent ()
  {
    return m_aParent;
  }

  /**
   * @return The ID of the parent tree item or <code>null</code> if this is the
   *         root item.
   */
  @Nullable
  public final KEYTYPE getParentID ()
  {
    return m_aParent == null ? null : m_aParent.getID ();
  }

  /**
   * @return The data of the parent tree item or <code>null</code> if this is
   *         the root item.
   */
  @Nullable
  public final DATATYPE getParentData ()
  {
    return m_aParent == null ? null : m_aParent.getData ();
  }

  /**
   * @return The nesting level of this item. The root item has level 0, its
   *         children have level 1 etc.
   */
  @Nonnegative
  public final int getLevel ()
  {
    int ret = 0;
    ITEMTYPE aItem = thisAsT ();
    while (aItem.getParent () != null)
    {
      ++ret;
      aItem = aItem.getParent ();
    }
    return ret;
  }

  /**
   * @return The ID of this tree item. May be <code>null</code> for root items.
   */
  @Nullable
  public final KEYTYPE getID ()
  {
    return m_aDataID;
  }

  /**
   * @return The data associated with this tree item. May be <code>null</code>.
   */
  @Nullable
  public final DATATYPE getData ()
  {
    return m_aData;
  }

  @Override
  public final boolean hasChildren ()
  {
    return m_aChildMap != null && m_aChildMap.isNotEmpty ();
  }

  /**
   * @return The number of direct children of this item. Always &ge; 0.
   */
  @Nonnegative
  public final int getChildCount ()
  {
    return m_aChildMap == null ? 0 : m_aChildMap.size ();
  }

  /**
   * @return A mutable copy of all children of this item, or <code>null</code>
   *         if this item has no children.
   */
  @Nullable
  @ReturnsMutableCopy
  public final ICommonsList <ITEMTYPE> getAllChildren ()
  {
    return m_aChildren == null ? null : m_aChildren.getClone ();
  }

  /**
   * @return An iterable over the direct children of this item, or
   *         <code>null</code> if this item has no children.
   */
  @Nullable
  public final ICommonsIterable <ITEMTYPE> getChildren ()
  {
    return m_aChildren;
  }

  @Override
  public final void forAllChildren (@NonNull final Consumer <? super ITEMTYPE> aConsumer)
  {
    if (m_aChildren != null)
      m_aChildren.forEach (aConsumer);
  }

  @Override
  public final void forAllChildren (@NonNull final Predicate <? super ITEMTYPE> aFilter,
                                    @NonNull final Consumer <? super ITEMTYPE> aConsumer)
  {
    if (m_aChildren != null)
      m_aChildren.findAll (aFilter, aConsumer);
  }

  @Override
  public final <DSTTYPE> void forAllChildrenMapped (@NonNull final Predicate <? super ITEMTYPE> aFilter,
                                                    @NonNull final Function <? super ITEMTYPE, ? extends DSTTYPE> aMapper,
                                                    @NonNull final Consumer <? super DSTTYPE> aConsumer)
  {
    if (m_aChildren != null)
      m_aChildren.findAllMapped (aFilter, aMapper, aConsumer);
  }

  /**
   * @return A mutable copy of the IDs of all direct children, or
   *         <code>null</code> if this item has no children.
   */
  @Nullable
  @ReturnsMutableCopy
  public final ICommonsSet <KEYTYPE> getAllChildDataIDs ()
  {
    if (m_aChildMap == null)
      return null;
    return m_aChildMap.copyOfKeySet ();
  }

  /**
   * @return A mutable copy of the data of all direct children, or
   *         <code>null</code> if this item has no children.
   */
  @Nullable
  @ReturnsMutableCopy
  public final ICommonsList <DATATYPE> getAllChildDatas ()
  {
    if (m_aChildren == null)
      return null;
    return m_aChildren.getAllMapped (ITEMTYPE::getData);
  }

  /**
   * Get the child at the specified index.
   *
   * @param nIndex
   *        The index of the child to retrieve. Must be &ge; 0.
   * @return The child item at the given index. May be <code>null</code>.
   * @throws IndexOutOfBoundsException
   *         if the tree item has no children or the index is out of bounds.
   */
  @Nullable
  public final ITEMTYPE getChildAtIndex (@Nonnegative final int nIndex)
  {
    if (m_aChildren == null)
      throw new IndexOutOfBoundsException ("Tree item has no children!");
    return m_aChildren.get (nIndex);
  }

  @Override
  @Nullable
  public final ITEMTYPE getFirstChild ()
  {
    return m_aChildren == null ? null : m_aChildren.getFirstOrNull ();
  }

  @Override
  @Nullable
  public final ITEMTYPE findFirstChild (@NonNull final Predicate <? super ITEMTYPE> aFilter)
  {
    return m_aChildren == null ? null : m_aChildren.findFirst (aFilter);
  }

  @Override
  @Nullable
  public final <DSTTYPE> DSTTYPE findFirstChildMapped (@NonNull final Predicate <? super ITEMTYPE> aFilter,
                                                       @NonNull final Function <? super ITEMTYPE, ? extends DSTTYPE> aMapper)
  {
    return m_aChildren == null ? null : m_aChildren.findFirstMapped (aFilter, aMapper);
  }

  @Override
  @Nullable
  public final ITEMTYPE getLastChild ()
  {
    return m_aChildren == null ? null : m_aChildren.getLastOrNull ();
  }

  /**
   * Set the data associated with this tree item.
   *
   * @param aData
   *        The data to be set. May be <code>null</code>.
   * @throws IllegalArgumentException
   *         if the data is invalid according to {@link #isValidData(Object)}.
   */
  public final void setData (@Nullable final DATATYPE aData)
  {
    if (!isValidData (aData))
      throw new IllegalArgumentException ("The passed data object is invalid!");
    m_aData = aData;
  }

  /**
   * Create a child item with the specified data ID and data. If a child with
   * the same data ID already exists, its data is overwritten.
   *
   * @param aDataID
   *        The data ID of the new child. May be <code>null</code>.
   * @param aData
   *        The data of the new child. May be <code>null</code>.
   * @return The created or existing child item. May be <code>null</code> if
   *         overwrite is not allowed and the ID already exists.
   */
  @Nullable
  public final ITEMTYPE createChildItem (@Nullable final KEYTYPE aDataID, @Nullable final DATATYPE aData)
  {
    return createChildItem (aDataID, aData, true);
  }

  /**
   * Create a child item with the specified data ID and data.
   *
   * @param aDataID
   *        The data ID of the new child. May be <code>null</code>.
   * @param aData
   *        The data of the new child. May be <code>null</code>.
   * @param bAllowOverwrite
   *        <code>true</code> to allow overwriting existing children with the
   *        same ID, <code>false</code> to return <code>null</code> if a child
   *        with the same ID already exists.
   * @return The created or existing child item, or <code>null</code> if
   *         overwrite is not allowed and the ID already exists.
   */
  @Nullable
  public final ITEMTYPE createChildItem (@Nullable final KEYTYPE aDataID,
                                         @Nullable final DATATYPE aData,
                                         final boolean bAllowOverwrite)
  {
    if (!isValidDataID (aDataID))
      throw new IllegalArgumentException ("Illegal data ID provided");

    ITEMTYPE aItem = getChildItemOfDataID (aDataID);
    if (aItem != null)
    {
      // ID already exists
      if (!bAllowOverwrite)
        return null;

      // just change data of existing item
      aItem.setData (aData);
    }
    else
    {
      // create new item
      aItem = m_aFactory.create (_asT (this), aDataID);
      if (aItem == null)
        throw new IllegalStateException ("null item created!");
      aItem.setData (aData);
      if (m_aChildMap == null)
      {
        m_aChildMap = new CommonsHashMap <> ();
        m_aChildren = new CommonsArrayList <> ();
      }
      m_aChildMap.put (aDataID, aItem);
      m_aChildren.add (aItem);
    }
    return aItem;
  }

  /**
   * Check if a direct child item with the given data ID exists.
   *
   * @param aDataID
   *        The data ID to check. May be <code>null</code>.
   * @return <code>true</code> if a child with the given data ID exists,
   *         <code>false</code> otherwise.
   */
  public final boolean containsChildItemWithDataID (@Nullable final KEYTYPE aDataID)
  {
    return m_aChildMap != null && m_aChildMap.containsKey (aDataID);
  }

  /**
   * Get the direct child item with the given data ID.
   *
   * @param aDataID
   *        The data ID to search. May be <code>null</code>.
   * @return The child item with the given data ID, or <code>null</code> if no
   *         such child exists.
   */
  @Nullable
  public final ITEMTYPE getChildItemOfDataID (@Nullable final KEYTYPE aDataID)
  {
    return m_aChildMap == null ? null : m_aChildMap.get (aDataID);
  }

  /**
   * Check if this item is the same as or a child of the passed parent item.
   *
   * @param aParent
   *        The parent item to check against. May not be <code>null</code>.
   * @return <code>true</code> if this item is the same as or a descendant of
   *         the passed parent item, <code>false</code> otherwise.
   */
  public final boolean isSameOrChildOf (@NonNull final ITEMTYPE aParent)
  {
    ValueEnforcer.notNull (aParent, "Parent");

    ITreeItemWithID <KEYTYPE, DATATYPE, ITEMTYPE> aCur = this;
    while (aCur != null)
    {
      // Do not use "equals" because it recursively compares all children!
      if (EqualsHelper.identityEqual (aCur, aParent))
        return true;
      aCur = aCur.getParent ();
    }
    return false;
  }

  /**
   * Change the parent of this item to the specified new parent.
   *
   * @param aNewParent
   *        The new parent item. May not be <code>null</code>.
   * @return {@link ESuccess#SUCCESS} if the parent was changed successfully,
   *         {@link ESuccess#FAILURE} if the new parent is the same as or a
   *         child of this item.
   */
  @NonNull
  public final ESuccess changeParent (@NonNull final ITEMTYPE aNewParent)
  {
    ValueEnforcer.notNull (aNewParent, "NewParent");

    // no change so far
    if (getParent () == aNewParent)
      return ESuccess.SUCCESS;

    // cannot make a child of this, this' new parent.
    final ITEMTYPE aThis = _asT (this);
    if (aNewParent.isSameOrChildOf (aThis))
      return ESuccess.FAILURE;

    // add this to the new parent
    if (m_aParent.removeChild (getID ()).isUnchanged ())
      throw new IllegalStateException ("Failed to remove this from parent!");

    // Remember new parent!
    m_aParent = aNewParent;
    return ESuccess.valueOfChange (aNewParent.internalAddChild (getID (), aThis, false));
  }

  /**
   * Internal method to add a child item with the specified data ID.
   *
   * @param aDataID
   *        The data ID of the child. May not be <code>null</code>.
   * @param aChild
   *        The child item to be added. May not be <code>null</code>.
   * @param bAllowOverwrite
   *        <code>true</code> to allow overwriting existing children,
   *        <code>false</code> to return {@link EChange#UNCHANGED} if a child
   *        with the same ID already exists.
   * @return {@link EChange#CHANGED} if the child was added successfully,
   *         {@link EChange#UNCHANGED} if overwrite is not allowed and the ID
   *         already exists.
   */
  @NonNull
  public final EChange internalAddChild (@NonNull final KEYTYPE aDataID,
                                         @NonNull final ITEMTYPE aChild,
                                         final boolean bAllowOverwrite)
  {
    ValueEnforcer.notNull (aChild, "Child");

    // Ensure children are present
    if (m_aChildMap != null)
    {
      if (!bAllowOverwrite && m_aChildMap.containsKey (aDataID))
        return EChange.UNCHANGED;
    }
    else
    {
      m_aChildMap = new CommonsHashMap <> ();
      m_aChildren = new CommonsArrayList <> ();
    }

    m_aChildMap.put (aDataID, aChild);
    m_aChildren.add (aChild);
    m_aFactory.onAddItem (aChild);
    return EChange.CHANGED;
  }

  private void _recursiveRemoveFromFactory (@NonNull final ITEMTYPE aItem)
  {
    // Recursively remove this node and all child nodes from the factory!
    if (aItem.hasChildren ())
      for (final ITEMTYPE aChild : aItem.getAllChildren ())
        _recursiveRemoveFromFactory (aChild);
    m_aFactory.onRemoveItem (aItem);
  }

  /**
   * Remove the child item with the specified data ID.
   *
   * @param aDataID
   *        The ID of the child to remove. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if the child was removed, {@link EChange#UNCHANGED} otherwise.
   */
  @NonNull
  public final EChange removeChild (@Nullable final KEYTYPE aDataID)
  {
    if (aDataID == null)
      return EChange.UNCHANGED;

    // Any children present
    if (m_aChildMap == null)
      return EChange.UNCHANGED;

    // Main removal
    final ITEMTYPE aItem = m_aChildMap.remove (aDataID);
    if (aItem == null)
      return EChange.UNCHANGED;
    if (!m_aChildren.remove (aItem))
      throw new IllegalStateException ("Failed to remove item from list: " + aItem);

    // Notify factory
    _recursiveRemoveFromFactory (aItem);
    return EChange.CHANGED;
  }

  /**
   * Remove all children from this tree item.
   *
   * @return {@link EChange#CHANGED} if at least one child was removed, {@link EChange#UNCHANGED} if
   *         there were no children.
   */
  @NonNull
  public final EChange removeAllChildren ()
  {
    if (m_aChildMap == null || m_aChildMap.isEmpty ())
      return EChange.UNCHANGED;

    // Remember all children
    final ICommonsList <ITEMTYPE> aAllChildren = m_aChildren.getClone ();

    // Remove all children
    m_aChildMap.clear ();
    m_aChildren.clear ();

    // Notify factory after removal
    for (final ITEMTYPE aChild : aAllChildren)
      _recursiveRemoveFromFactory (aChild);
    return EChange.CHANGED;
  }

  /**
   * Reorder the children of this tree item using the provided comparator.
   *
   * @param aComparator
   *        The comparator to use for sorting. May not be <code>null</code>.
   */
  public final void reorderChildrenByItems (@NonNull final Comparator <? super ITEMTYPE> aComparator)
  {
    if (m_aChildren != null)
      m_aChildren.sort (aComparator);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final BasicTreeItemWithID <?, ?, ?> rhs = (BasicTreeItemWithID <?, ?, ?>) o;
    final Object aObj1 = m_aData;
    final Object aObj11 = m_aChildMap;
    return EqualsHelper.equals (m_aDataID, rhs.m_aDataID) &&
           EqualsHelper.equals (aObj1, rhs.m_aData) &&
           EqualsHelper.equals (aObj11, rhs.m_aChildMap);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aData).append (m_aDataID).append (m_aChildMap).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("dataID", m_aDataID)
                                       .append ("data", m_aData)
                                       .append ("children", m_aChildMap)
                                       .getToString ();
  }
}
