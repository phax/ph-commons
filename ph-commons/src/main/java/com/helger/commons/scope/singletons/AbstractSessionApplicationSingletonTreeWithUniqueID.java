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
package com.helger.commons.scope.singletons;

import java.util.Collection;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.scope.singleton.AbstractSessionApplicationSingleton;
import com.helger.commons.state.EChange;
import com.helger.commons.tree.withid.DefaultTreeItemWithID;
import com.helger.commons.tree.withid.unique.DefaultTreeWithGlobalUniqueID;
import com.helger.commons.tree.withid.unique.ITreeWithGlobalUniqueID;

/**
 * Represents a global singleton tree with a unique ID. It basically is a
 * {@link AbstractSessionApplicationSingleton} wrapping a
 * {@link DefaultTreeWithGlobalUniqueID} with the same API.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Tree key type
 * @param <VALUETYPE>
 *        Tree value type
 */
public abstract class AbstractSessionApplicationSingletonTreeWithUniqueID <KEYTYPE, VALUETYPE> extends
                                                                          AbstractSessionApplicationSingleton implements
                                                                          ITreeWithGlobalUniqueID <KEYTYPE, VALUETYPE, DefaultTreeItemWithID <KEYTYPE, VALUETYPE>>
{
  protected final DefaultTreeWithGlobalUniqueID <KEYTYPE, VALUETYPE> m_aTree = new DefaultTreeWithGlobalUniqueID <> ();

  public AbstractSessionApplicationSingletonTreeWithUniqueID ()
  {}

  public final boolean hasChildren ()
  {
    return m_aTree.hasChildren ();
  }

  @Nonnegative
  public int getChildCount ()
  {
    return m_aTree.getChildCount ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public final Collection <? extends DefaultTreeItemWithID <KEYTYPE, VALUETYPE>> getAllChildren ()
  {
    return m_aTree.getAllChildren ();
  }

  @Nonnull
  public DefaultTreeItemWithID <KEYTYPE, VALUETYPE> getRootItem ()
  {
    return m_aTree.getRootItem ();
  }

  @Nullable
  public DefaultTreeItemWithID <KEYTYPE, VALUETYPE> getChildWithID (@Nullable final DefaultTreeItemWithID <KEYTYPE, VALUETYPE> aCurrent,
                                                                    @Nullable final KEYTYPE aID)
  {
    return m_aTree.getChildWithID (aCurrent, aID);
  }

  public boolean hasChildren (@Nullable final DefaultTreeItemWithID <KEYTYPE, VALUETYPE> aCurrent)
  {
    return m_aTree.hasChildren (aCurrent);
  }

  @Nonnegative
  public int getChildCount (@Nullable final DefaultTreeItemWithID <KEYTYPE, VALUETYPE> aCurrent)
  {
    return m_aTree.getChildCount (aCurrent);
  }

  @Nullable
  public Collection <? extends DefaultTreeItemWithID <KEYTYPE, VALUETYPE>> getAllChildren (@Nullable final DefaultTreeItemWithID <KEYTYPE, VALUETYPE> aCurrent)
  {
    return m_aTree.getAllChildren (aCurrent);
  }

  @Nullable
  public DefaultTreeItemWithID <KEYTYPE, VALUETYPE> getItemWithID (@Nullable final KEYTYPE aDataID)
  {
    return m_aTree.getItemWithID (aDataID);
  }

  @Nonnegative
  public int getItemCount ()
  {
    return m_aTree.getItemCount ();
  }

  @Nonnull
  public Collection <DefaultTreeItemWithID <KEYTYPE, VALUETYPE>> getAllItems ()
  {
    return m_aTree.getAllItems ();
  }

  public boolean isItemSameOrDescendant (@Nullable final KEYTYPE aParentItemID, @Nullable final KEYTYPE aChildItemID)
  {
    return m_aTree.isItemSameOrDescendant (aParentItemID, aChildItemID);
  }

  public boolean containsItemWithID (@Nullable final KEYTYPE aDataID)
  {
    return m_aTree.containsItemWithID (aDataID);
  }

  @Nullable
  public VALUETYPE getItemDataWithID (@Nullable final KEYTYPE aDataID)
  {
    return m_aTree.getItemDataWithID (aDataID);
  }

  @Nonnull
  @ReturnsMutableCopy
  public Collection <VALUETYPE> getAllItemDatas ()
  {
    return m_aTree.getAllItemDatas ();
  }

  @Nonnull
  public EChange removeItemWithID (@Nullable final KEYTYPE aDataID)
  {
    return m_aTree.removeItemWithID (aDataID);
  }
}
