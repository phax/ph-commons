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
package com.helger.tree;

import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsIterable;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.state.EChange;
import com.helger.commons.state.EContinue;
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
   * @return the created TreeItem object or <code>null</code> if the ID is
   *         already in use and bAllowOverwrite is false
   */
  @Nonnull
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

  @Nullable
  @ReturnsMutableCopy
  public final ICommonsList <ITEMTYPE> getAllChildren ()
  {
    return m_aChildren == null ? null : m_aChildren.getClone ();
  }

  @Nullable
  public final ICommonsIterable <ITEMTYPE> getChildren ()
  {
    return m_aChildren;
  }

  @Override
  public final void forAllChildren (@Nonnull final Consumer <? super ITEMTYPE> aConsumer)
  {
    if (m_aChildren != null)
      m_aChildren.forEach (aConsumer);
  }

  @Override
  @Nonnull
  public final EContinue forAllChildrenBreakable (@Nonnull final Function <? super ITEMTYPE, EContinue> aConsumer)
  {
    if (m_aChildren != null)
      return m_aChildren.forEachBreakable (aConsumer);
    return EContinue.CONTINUE;
  }

  @Override
  public final void forAllChildren (@Nonnull final Predicate <? super ITEMTYPE> aFilter,
                                    @Nonnull final Consumer <? super ITEMTYPE> aConsumer)
  {
    if (m_aChildren != null)
      m_aChildren.findAll (aFilter, aConsumer);
  }

  @Override
  public final <DSTTYPE> void forAllChildrenMapped (@Nonnull final Predicate <? super ITEMTYPE> aFilter,
                                                    @Nonnull final Function <? super ITEMTYPE, ? extends DSTTYPE> aMapper,
                                                    @Nonnull final Consumer <? super DSTTYPE> aConsumer)
  {
    if (m_aChildren != null)
      m_aChildren.findAllMapped (aFilter, aMapper, aConsumer);
  }

  @Nullable
  @ReturnsMutableCopy
  public final ICommonsList <DATATYPE> getAllChildDatas ()
  {
    if (m_aChildren == null)
      return null;
    return m_aChildren.getAllMapped (ITEMTYPE::getData);
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
    return m_aChildren == null ? 0 : m_aChildren.size ();
  }

  @Override
  @Nullable
  public final ITEMTYPE getFirstChild ()
  {
    return m_aChildren == null ? null : m_aChildren.getFirst ();
  }

  @Override
  @Nullable
  public final ITEMTYPE findFirstChild (@Nonnull final Predicate <? super ITEMTYPE> aFilter)
  {
    return m_aChildren == null ? null : m_aChildren.findFirst (aFilter);
  }

  @Override
  @Nullable
  public final <DSTTYPE> DSTTYPE findFirstChildMapped (@Nonnull final Predicate <? super ITEMTYPE> aFilter,
                                                       @Nonnull final Function <? super ITEMTYPE, ? extends DSTTYPE> aMapper)
  {
    return m_aChildren == null ? null : m_aChildren.findFirstMapped (aFilter, aMapper);
  }

  @Override
  @Nullable
  public final ITEMTYPE getLastChild ()
  {
    return m_aChildren == null ? null : m_aChildren.getLast ();
  }

  @SuppressFBWarnings ("IL_INFINITE_LOOP")
  public final boolean isSameOrChildOf (@Nonnull final ITEMTYPE aParent)
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

  @Nonnull
  public final ESuccess changeParent (@Nonnull final ITEMTYPE aNewParent)
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

  @Nonnull
  public final EChange internalAddChild (@Nonnull final ITEMTYPE aChild)
  {
    ValueEnforcer.notNull (aChild, "Child");

    // Ensure children are present
    if (m_aChildren == null)
      m_aChildren = new CommonsArrayList <> ();

    return m_aChildren.addObject (aChild);
  }

  @Nonnull
  public final EChange removeChild (@Nonnull final ITEMTYPE aChild)
  {
    ValueEnforcer.notNull (aChild, "Child");

    if (m_aChildren == null)
      return EChange.UNCHANGED;
    return m_aChildren.removeObject (aChild);
  }

  public final void reorderChildItems (@Nonnull final Comparator <? super ITEMTYPE> aComparator)
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
    return new ToStringGenerator (this).append ("data", m_aData).append ("children", m_aChildren).getToString ();
  }
}
