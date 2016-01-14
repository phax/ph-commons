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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.lang.GenericReflection;
import com.helger.commons.state.EChange;
import com.helger.commons.state.ESuccess;
import com.helger.commons.string.ToStringGenerator;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

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
public class BasicTreeItem <DATATYPE, ITEMTYPE extends ITreeItem <DATATYPE, ITEMTYPE>>
                           implements ITreeItem <DATATYPE, ITEMTYPE>
{
  // item factory
  private final ITreeItemFactory <DATATYPE, ITEMTYPE> m_aFactory;

  // parent tree item
  private ITEMTYPE m_aParent;

  // the data to be stored
  private DATATYPE m_aData;

  // child list
  private List <ITEMTYPE> m_aChildren = null;

  /**
   * Constructor for root object.
   *
   * @param aFactory
   *        The factory to use for creating tree items. May not be
   *        <code>null</code>.
   */
  public BasicTreeItem (@Nonnull final ITreeItemFactory <DATATYPE, ITEMTYPE> aFactory)
  {
    m_aFactory = ValueEnforcer.notNull (aFactory, "Factory");
    m_aParent = null;
    m_aData = null;
  }

  /**
   * Constructor for normal elements.
   *
   * @param aParent
   *        Parent item to use. May never be <code>null</code> since only the
   *        root has no parent and for the root item a special no-argument
   *        constructor is present.
   */
  public BasicTreeItem (@Nonnull final ITEMTYPE aParent)
  {
    ValueEnforcer.notNull (aParent, "Parent");
    ValueEnforcer.isTrue (aParent instanceof BasicTreeItem <?, ?>, "Parent is no BasicTreeItem");
    ValueEnforcer.notNull (aParent.getFactory (), "parent item factory");
    m_aParent = aParent;
    m_aFactory = aParent.getFactory ();
    m_aData = null;
  }

  @Nonnull
  public final ITreeItemFactory <DATATYPE, ITEMTYPE> getFactory ()
  {
    return m_aFactory;
  }

  /**
   * This method is called to validate a data object. This method may be
   * overloaded in derived classes. The default implementation accepts all
   * values.
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

  @Nullable
  public final DATATYPE getData ()
  {
    return m_aData;
  }

  public final void setData (@Nullable final DATATYPE aData)
  {
    ValueEnforcer.isTrue (isValidData (aData), "The passed data object is invalid!");
    m_aData = aData;
  }

  public final boolean isRootItem ()
  {
    return m_aParent == null;
  }

  @Nullable
  public final ITEMTYPE getParent ()
  {
    return m_aParent;
  }

  @Nullable
  public final DATATYPE getParentData ()
  {
    return m_aParent == null ? null : m_aParent.getData ();
  }

  @Nonnegative
  public final int getLevel ()
  {
    int ret = 0;
    @SuppressWarnings ("unchecked")
    ITEMTYPE aItem = (ITEMTYPE) this;
    while (aItem.getParent () != null)
    {
      ++ret;
      aItem = aItem.getParent ();
    }
    return ret;
  }

  @Nonnull
  private ITEMTYPE _asT (@Nonnull final BasicTreeItem <DATATYPE, ITEMTYPE> aItem)
  {
    return GenericReflection.<BasicTreeItem <DATATYPE, ITEMTYPE>, ITEMTYPE> uncheckedCast (aItem);
  }

  /**
   * Add a child item to this item.
   *
   * @param aData
   *        the data associated with this item
   * @return the created TreeItem object or <code>null</code> if the ID is
   *         already in use and bAllowOverwrite is false
   */
  @Nonnull
  public final ITEMTYPE createChildItem (@Nullable final DATATYPE aData)
  {
    // create new item
    final ITEMTYPE aItem = m_aFactory.create (_asT (this));
    if (aItem == null)
      throw new IllegalStateException ("null item created!");
    aItem.setData (aData);
    internalAddChild (aItem);
    return aItem;
  }

  public final boolean hasChildren ()
  {
    return m_aChildren != null && !m_aChildren.isEmpty ();
  }

  @Nullable
  @ReturnsMutableCopy
  public final List <ITEMTYPE> getAllChildren ()
  {
    return m_aChildren == null ? null : CollectionHelper.newList (m_aChildren);
  }

  @Nullable
  @ReturnsMutableCopy
  public final List <DATATYPE> getAllChildDatas ()
  {
    if (m_aChildren == null)
      return null;
    final List <DATATYPE> ret = new ArrayList <DATATYPE> ();
    for (final ITEMTYPE aChild : m_aChildren)
      ret.add (aChild.getData ());
    return ret;
  }

  @Nullable
  public final ITEMTYPE getChildAtIndex (@Nonnegative final int nIndex)
  {
    if (m_aChildren == null)
      throw new IndexOutOfBoundsException ("Tree item has no children!");
    return m_aChildren.get (nIndex);
  }

  @Nonnegative
  public final int getChildCount ()
  {
    return m_aChildren != null ? m_aChildren.size () : 0;
  }

  @Nullable
  public ITEMTYPE getFirstChild ()
  {
    return CollectionHelper.getFirstElement (m_aChildren);
  }

  @Nullable
  public ITEMTYPE getLastChild ()
  {
    return CollectionHelper.getLastElement (m_aChildren);
  }

  @SuppressFBWarnings ("IL_INFINITE_LOOP")
  public final boolean isSameOrChildOf (@Nonnull final ITEMTYPE aParent)
  {
    ValueEnforcer.notNull (aParent, "Parent");

    ITreeItem <DATATYPE, ITEMTYPE> aCur = this;
    while (aCur != null)
    {
      // Do not use "equals" because it recursively compares all children!
      if (aCur == aParent)
        return true;
      aCur = aCur.getParent ();
    }
    return false;
  }

  @Nonnull
  public final ESuccess changeParent (@Nonnull final ITEMTYPE aNewParent)
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
    if (getParent ().removeChild (aThis).isUnchanged ())
      throw new IllegalStateException ("Failed to remove this from parent!");

    // Remember new parent!
    m_aParent = aNewParent;
    return ESuccess.valueOfChange (aNewParent.internalAddChild (aThis));
  }

  @Nonnull
  public final EChange internalAddChild (@Nonnull final ITEMTYPE aChild)
  {
    ValueEnforcer.notNull (aChild, "Child");

    // Ensure children are present
    if (m_aChildren == null)
      m_aChildren = new ArrayList <> ();

    return EChange.valueOf (m_aChildren.add (aChild));
  }

  @Nonnull
  public final EChange removeChild (@Nonnull final ITEMTYPE aChild)
  {
    ValueEnforcer.notNull (aChild, "Child");

    return EChange.valueOf (m_aChildren != null && m_aChildren.remove (aChild));
  }

  public final void reorderChildItems (@Nonnull final Comparator <? super ITEMTYPE> aComparator)
  {
    if (m_aChildren != null)
      m_aChildren = CollectionHelper.getSorted (m_aChildren, aComparator);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final BasicTreeItem <?, ?> rhs = (BasicTreeItem <?, ?>) o;
    return EqualsHelper.equals (m_aData, rhs.m_aData) && EqualsHelper.equals (m_aChildren, rhs.m_aChildren);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aData).append (m_aChildren).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("data", m_aData).append ("children", m_aChildren).toString ();
  }
}
