/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.equals.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.state.EContinue;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsCollection;
import com.helger.collection.commons.ICommonsIterable;

import jakarta.annotation.Nonnull;

/**
 * Root class for a simple tree. The elements of the tree are not sorted by any
 * means.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        tree item value type
 * @param <ITEMTYPE>
 *        tree item implementation type
 */
@NotThreadSafe
public class BasicTree <DATATYPE, ITEMTYPE extends ITreeItem <DATATYPE, ITEMTYPE>> implements ITree <DATATYPE, ITEMTYPE>
{
  // Root item.
  private final ITEMTYPE m_aRootItem;

  public BasicTree (@Nonnull final ITreeItemFactory <DATATYPE, ITEMTYPE> aFactory)
  {
    ValueEnforcer.notNull (aFactory, "Factory");
    m_aRootItem = aFactory.createRoot ();
    if (m_aRootItem == null)
      throw new IllegalStateException ("Failed to create root item!");
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
    final BasicTree <?, ?> rhs = (BasicTree <?, ?>) o;
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
    return new ToStringGenerator (this).append ("RootItem", m_aRootItem).getToString ();
  }
}
