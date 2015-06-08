/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.tree.util.walk;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.hierarchy.DefaultHierarchyWalkerCallback;
import com.helger.commons.hierarchy.IHierarchyWalkerCallback;
import com.helger.commons.parent.ChildrenProviderHasChildren;
import com.helger.commons.parent.IChildrenProvider;
import com.helger.commons.tree.IBasicTree;
import com.helger.commons.tree.IBasicTreeItem;

/**
 * Iterate all nodes of a tree, or a tree element using a custom callback
 * mechanism.
 *
 * @author Philip Helger
 */
@Immutable
public final class TreeWalker
{
  @PresentForCodeCoverage
  private static final TreeWalker s_aInstance = new TreeWalker ();

  private TreeWalker ()
  {}

  private static <DATATYPE, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> void _walkTree (@Nonnull final ITEMTYPE aTreeItem,
                                                                                                  @Nonnull final IChildrenProvider <ITEMTYPE> aChildrenProvider,
                                                                                                  @Nonnull final IHierarchyWalkerCallback <? super ITEMTYPE> aCallback)
  {
    aCallback.onItemBeforeChildren (aTreeItem);
    if (aChildrenProvider.hasChildren (aTreeItem))
      for (final ITEMTYPE aChildItem : aChildrenProvider.getAllChildren (aTreeItem))
      {
        aCallback.onLevelDown ();
        // recursive call
        _walkTree (aChildItem, aChildrenProvider, aCallback);
        aCallback.onLevelUp ();
      }
    aCallback.onItemAfterChildren (aTreeItem);
  }

  public static <DATATYPE, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> void walkTree (@Nonnull final IBasicTree <DATATYPE, ITEMTYPE> aTree,
                                                                                                @Nonnull final IHierarchyWalkerCallback <? super ITEMTYPE> aCallback)
  {
    walkTree (aTree, new ChildrenProviderHasChildren <ITEMTYPE> (), aCallback);
  }

  public static <DATATYPE, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> void walkTree (@Nonnull final IBasicTree <DATATYPE, ITEMTYPE> aTree,
                                                                                                @Nonnull final IChildrenProvider <ITEMTYPE> aChildrenProvider,
                                                                                                @Nonnull final IHierarchyWalkerCallback <? super ITEMTYPE> aCallback)
  {
    ValueEnforcer.notNull (aTree, "Tree");

    walkSubTree (aTree.getRootItem (), aChildrenProvider, aCallback);
  }

  public static <DATATYPE, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> void walkTreeData (@Nonnull final IBasicTree <DATATYPE, ITEMTYPE> aTree,
                                                                                                    @Nonnull final IHierarchyWalkerCallback <? super DATATYPE> aDataCallback)
  {
    walkTreeData (aTree, new ChildrenProviderHasChildren <ITEMTYPE> (), aDataCallback);
  }

  public static <DATATYPE, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> void walkTreeData (@Nonnull final IBasicTree <DATATYPE, ITEMTYPE> aTree,
                                                                                                    @Nonnull final IChildrenProvider <ITEMTYPE> aChildrenProvider,
                                                                                                    @Nonnull final IHierarchyWalkerCallback <? super DATATYPE> aDataCallback)
  {
    ValueEnforcer.notNull (aTree, "Tree");

    walkSubTreeData (aTree.getRootItem (), aChildrenProvider, aDataCallback);
  }

  public static <DATATYPE, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> void walkSubTree (@Nonnull final ITEMTYPE aTreeItem,
                                                                                                   @Nonnull final IHierarchyWalkerCallback <? super ITEMTYPE> aCallback)
  {
    walkSubTree (aTreeItem, new ChildrenProviderHasChildren <ITEMTYPE> (), aCallback);
  }

  public static <DATATYPE, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> void walkSubTree (@Nonnull final ITEMTYPE aTreeItem,
                                                                                                   @Nonnull final IChildrenProvider <ITEMTYPE> aChildrenProvider,
                                                                                                   @Nonnull final IHierarchyWalkerCallback <? super ITEMTYPE> aCallback)
  {
    ValueEnforcer.notNull (aTreeItem, "TreeItem");
    ValueEnforcer.notNull (aChildrenProvider, "ChildrenProvider");
    ValueEnforcer.notNull (aCallback, "Callback");

    aCallback.begin ();
    try
    {
      if (aChildrenProvider.hasChildren (aTreeItem))
        for (final ITEMTYPE aChildItem : aChildrenProvider.getAllChildren (aTreeItem))
          _walkTree (aChildItem, aChildrenProvider, aCallback);
    }
    finally
    {
      aCallback.end ();
    }
  }

  public static <DATATYPE, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> void walkSubTreeData (@Nonnull final ITEMTYPE aTreeItem,
                                                                                                       @Nonnull final IHierarchyWalkerCallback <? super DATATYPE> aDataCallback)
  {
    walkSubTreeData (aTreeItem, new ChildrenProviderHasChildren <ITEMTYPE> (), aDataCallback);
  }

  public static <DATATYPE, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> void walkSubTreeData (@Nonnull final ITEMTYPE aTreeItem,
                                                                                                       @Nonnull final IChildrenProvider <ITEMTYPE> aChildrenProvider,
                                                                                                       @Nonnull final IHierarchyWalkerCallback <? super DATATYPE> aDataCallback)
  {
    ValueEnforcer.notNull (aDataCallback, "DataCallback");

    // Wrap callback
    walkSubTree (aTreeItem, aChildrenProvider, new DefaultHierarchyWalkerCallback <ITEMTYPE> ()
    {
      @Override
      public void begin ()
      {
        super.begin ();
        aDataCallback.begin ();
      }

      @Override
      public void onLevelDown ()
      {
        super.onLevelDown ();
        aDataCallback.onLevelDown ();
      }

      @Override
      public void onLevelUp ()
      {
        aDataCallback.onLevelUp ();
        super.onLevelUp ();
      }

      @Override
      public void end ()
      {
        aDataCallback.end ();
        super.end ();
      }

      @Override
      public void onItemBeforeChildren (@Nonnull final ITEMTYPE aItem)
      {
        aDataCallback.onItemBeforeChildren (aItem.getData ());
      }

      @Override
      public void onItemAfterChildren (@Nonnull final ITEMTYPE aItem)
      {
        aDataCallback.onItemAfterChildren (aItem.getData ());
      }
    });
  }
}
