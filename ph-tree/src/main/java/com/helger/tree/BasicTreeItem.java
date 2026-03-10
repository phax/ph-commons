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
package com.helger.tree;

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
import com.helger.base.state.EChange;
import com.helger.base.state.EContinue;
import com.helger.base.state.ESuccess;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsIterable;
import com.helger.collection.commons.ICommonsList;

/**
 * Basic implementation of the {@link ITreeItem} interface
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        tree item value type
 * @param <ITEMTYPE>
 *        tree item implementation type
 */
@NotThreadSafe
public class BasicTreeItem <DATATYPE, ITEMTYPE extends ITreeItem <DATATYPE, ITEMTYPE>> implements
                           ITreeItem <DATATYPE, ITEMTYPE>
{
  // item factory
  private final ITreeItemFactory <DATATYPE, ITEMTYPE> m_aFactory;

  // parent tree item
  private ITEMTYPE m_aParent;

  // the data to be stored
  private DATATYPE m_aData;

  // child list
  private ICommonsList <ITEMTYPE> m_aChildren;

  /**
   * Constructor for root object.
   *
   * @param aFactory
   *        The factory to use for creating tree items. May not be <code>null</code>.
   */
  public BasicTreeItem (@NonNull final ITreeItemFactory <DATATYPE, ITEMTYPE> aFactory)
  {
    m_aFactory = ValueEnforcer.notNull (aFactory, "Factory");
    m_aParent = null;
    m_aData = null;
  }

  /**
   * Constructor for normal elements.
   *
   * @param aParent
   *        Parent item to use. May never be <code>null</code> since only the root has no parent and
   *        for the root item a special no-argument constructor is present.
   */
  public BasicTreeItem (@NonNull final ITEMTYPE aParent)
  {
    ValueEnforcer.notNull (aParent, "Parent");
    ValueEnforcer.isTrue (aParent instanceof BasicTreeItem <?, ?>, "Parent is no BasicTreeItem");
    ValueEnforcer.notNull (aParent.getFactory (), "parent item factory");
    m_aParent = aParent;
    m_aFactory = aParent.getFactory ();
    m_aData = null;
  }

  /**
   * @return The tree item factory used for creating new tree items.
   *         Never <code>null</code>.
   */
  @NonNull
  public final ITreeItemFactory <DATATYPE, ITEMTYPE> getFactory ()
  {
    return m_aFactory;
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
   * @return The data associated with this tree item. May be <code>null</code>.
   */
  @Nullable
  public final DATATYPE getData ()
  {
    return m_aData;
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
    ValueEnforcer.isTrue (isValidData (aData), "The passed data object is invalid!");
    m_aData = aData;
  }

  /**
   * @return <code>true</code> if this is the root item (i.e. has no parent),
   *         <code>false</code> otherwise.
   */
  public final boolean isRootItem ()
  {
    return m_aParent == null;
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
   * Add a child item to this item.
   *
   * @param aData
   *        the data associated with this item
   * @return the created TreeItem object or <code>null</code> if the ID is already in use and
   *         bAllowOverwrite is false
   */
  @NonNull
  public final ITEMTYPE createChildItem (@Nullable final DATATYPE aData)
  {
    // create new item
    final ITEMTYPE aItem = m_aFactory.create (thisAsT ());
    if (aItem == null)
      throw new IllegalStateException ("null item created!");
    aItem.setData (aData);
    internalAddChild (aItem);
    return aItem;
  }

  @Override
  public final boolean hasChildren ()
  {
    return m_aChildren != null && m_aChildren.isNotEmpty ();
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
  @NonNull
  public final EContinue forAllChildrenBreakable (@NonNull final Function <? super ITEMTYPE, EContinue> aConsumer)
  {
    if (m_aChildren != null)
      return m_aChildren.forEachBreakable (aConsumer);
    return EContinue.CONTINUE;
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

  /**
   * @return The number of direct children of this item. Always &ge; 0.
   */
  @Nonnegative
  public final int getChildCount ()
  {
    return m_aChildren == null ? 0 : m_aChildren.size ();
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

    ITreeItem <DATATYPE, ITEMTYPE> aCur = this;
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
    final ITEMTYPE aThis = thisAsT ();
    if (aNewParent.isSameOrChildOf (aThis))
      return ESuccess.FAILURE;

    // add this to the new parent
    if (getParent ().removeChild (aThis).isUnchanged ())
      throw new IllegalStateException ("Failed to remove this from parent!");

    // Remember new parent!
    m_aParent = aNewParent;
    return ESuccess.valueOfChange (aNewParent.internalAddChild (aThis));
  }

  /**
   * Internal method to add a child item to this item.
   *
   * @param aChild
   *        The child item to be added. May not be <code>null</code>.
   * @return {@link EChange#CHANGED} if the child was added successfully.
   */
  @NonNull
  public final EChange internalAddChild (@NonNull final ITEMTYPE aChild)
  {
    ValueEnforcer.notNull (aChild, "Child");

    // Ensure children are present
    if (m_aChildren == null)
      m_aChildren = new CommonsArrayList <> ();

    return m_aChildren.addObject (aChild);
  }

  /**
   * Remove the specified child item from this item.
   *
   * @param aChild
   *        The child item to be removed. May not be <code>null</code>.
   * @return {@link EChange#CHANGED} if the child was removed successfully,
   *         {@link EChange#UNCHANGED} if the child was not found.
   */
  @NonNull
  public final EChange removeChild (@NonNull final ITEMTYPE aChild)
  {
    ValueEnforcer.notNull (aChild, "Child");

    if (m_aChildren == null)
      return EChange.UNCHANGED;
    return m_aChildren.removeObject (aChild);
  }

  /**
   * Reorder the child items of this item using the specified comparator.
   *
   * @param aComparator
   *        The comparator to use for sorting. May not be <code>null</code>.
   */
  public final void reorderChildItems (@NonNull final Comparator <? super ITEMTYPE> aComparator)
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
    final BasicTreeItem <?, ?> rhs = (BasicTreeItem <?, ?>) o;
    final Object aObj1 = m_aData;
    final Object aObj11 = m_aChildren;
    return EqualsHelper.equals (aObj1, rhs.m_aData) && EqualsHelper.equals (aObj11, rhs.m_aChildren);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aData).append (m_aChildren).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("data", m_aData).append ("children", m_aChildren).getToString ();
  }
}
