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
package com.helger.tree.withid;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsCollection;
import com.helger.commons.collection.impl.ICommonsIterable;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.state.EContinue;
import com.helger.commons.string.ToStringGenerator;

/**
 * Base class for a tree having items with IDs. This implementation is
 * independent of the item implementation class. The elements of the tree are
 * not sorted by any means.
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
public class BasicTreeWithID <KEYTYPE, DATATYPE, ITEMTYPE extends ITreeItemWithID <KEYTYPE, DATATYPE, ITEMTYPE>>
                             implements
                             ITreeWithID <KEYTYPE, DATATYPE, ITEMTYPE>
{
  // Root item.
  private final ITEMTYPE m_aRootItem;

  public BasicTreeWithID (@Nonnull final ITreeItemWithIDFactory <KEYTYPE, DATATYPE, ITEMTYPE> aFactory)
  {
    ValueEnforcer.notNull (aFactory, "Factory");

    m_aRootItem = aFactory.createRoot ();
    if (m_aRootItem == null)
      throw new IllegalStateException ("Failed to create root item!");
    if (m_aRootItem.getParent () != null)
      throw new IllegalStateException ("Create root item has a non-null parent!!!");
  }

  @Override
  public final boolean hasChildren ()
  {
    // root item is always present
    return true;
  }

  @Nonnegative
  public final int getChildCount ()
  {
    // Exactly 1 root item is present
    return 1;
  }

  @Nonnull
  @ReturnsMutableCopy
  public final ICommonsCollection <ITEMTYPE> getAllChildren ()
  {
    return new CommonsArrayList <> (m_aRootItem);
  }

  @Nonnull
  public final ICommonsIterable <ITEMTYPE> getChildren ()
  {
    return getAllChildren ();
  }

  @Override
  public final void forAllChildren (@Nonnull final Consumer <? super ITEMTYPE> aConsumer)
  {
    aConsumer.accept (m_aRootItem);
  }

  @Override
  @Nonnull
  public final EContinue forAllChildrenBreakable (@Nonnull final Function <? super ITEMTYPE, EContinue> aConsumer)
  {
    return aConsumer.apply (m_aRootItem);
  }

  @Override
  public final void forAllChildren (@Nonnull final Predicate <? super ITEMTYPE> aFilter,
                                    @Nonnull final Consumer <? super ITEMTYPE> aConsumer)
  {
    if (aFilter.test (m_aRootItem))
      aConsumer.accept (m_aRootItem);
  }

  @Override
  public final <DSTTYPE> void forAllChildrenMapped (@Nonnull final Predicate <? super ITEMTYPE> aFilter,
                                                    @Nonnull final Function <? super ITEMTYPE, ? extends DSTTYPE> aMapper,
                                                    @Nonnull final Consumer <? super DSTTYPE> aConsumer)
  {
    if (aFilter.test (m_aRootItem))
      aConsumer.accept (aMapper.apply (m_aRootItem));
  }

  @Nonnull
  public final ITEMTYPE getRootItem ()
  {
    return m_aRootItem;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final BasicTreeWithID <?, ?, ?> rhs = (BasicTreeWithID <?, ?, ?>) o;
    return m_aRootItem.equals (rhs.m_aRootItem);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aRootItem).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("root", m_aRootItem).getToString ();
  }
}
