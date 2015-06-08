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
import com.helger.commons.hierarchy.DefaultHierarchyWalkerDynamicCallback;
import com.helger.commons.hierarchy.EHierarchyCallbackReturn;
import com.helger.commons.hierarchy.IHierarchyWalkerDynamicCallback;
import com.helger.commons.parent.ChildrenProviderHasChildren;
import com.helger.commons.parent.IChildrenProvider;
import com.helger.commons.tree.IBasicTree;
import com.helger.commons.tree.IBasicTreeItem;

/**
 * A specialized walker that iterates all elements in a tree and calls a
 * callback method. Compared to {@link TreeWalker} the callbacks used in this
 * class allow to stop iteration or to skip all siblings.
 *
 * @author Philip Helger
 */
@Immutable
public final class TreeWalkerDynamic
{
  @PresentForCodeCoverage
  private static final TreeWalkerDynamic s_aInstance = new TreeWalkerDynamic ();

  private TreeWalkerDynamic ()
  {}

  @Nonnull
  private static <DATATYPE, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> EHierarchyCallbackReturn _walkTree (@Nonnull final ITEMTYPE aTreeItem,
                                                                                                                      @Nonnull final IChildrenProvider <ITEMTYPE> aChildrenProvider,
                                                                                                                      @Nonnull final IHierarchyWalkerDynamicCallback <? super ITEMTYPE> aCallback)
  {
    // prefix insertion
    final EHierarchyCallbackReturn eRetPrefix = aCallback.onItemBeforeChildren (aTreeItem);

    // call children only if mode is continue
    EHierarchyCallbackReturn eRetChildren = EHierarchyCallbackReturn.CONTINUE;
    if (eRetPrefix == EHierarchyCallbackReturn.CONTINUE && aChildrenProvider.hasChildren (aTreeItem))
    {
      // iterate children
      aCallback.onLevelDown ();
      try
      {
        for (final ITEMTYPE aChildItem : aChildrenProvider.getAllChildren (aTreeItem))
        {
          // recursive call
          eRetChildren = _walkTree (aChildItem, aChildrenProvider, aCallback);
          if (eRetChildren == EHierarchyCallbackReturn.USE_PARENTS_NEXT_SIBLING)
          {
            // If we don't want the children to be enumerated, break this loop
            // and continue as normal
            eRetChildren = EHierarchyCallbackReturn.CONTINUE;
            break;
          }

          if (eRetChildren == EHierarchyCallbackReturn.STOP_ITERATION)
          {
            // stop iterating and propagate the return code to the root
            break;
          }
        }
      }
      finally
      {
        // callback
        aCallback.onLevelUp ();
      }
    }

    // postfix insertion even if prefix iteration failed
    final EHierarchyCallbackReturn eRetPostfix = aCallback.onItemAfterChildren (aTreeItem);

    // most stringent first
    if (eRetPrefix == EHierarchyCallbackReturn.STOP_ITERATION ||
        eRetChildren == EHierarchyCallbackReturn.STOP_ITERATION ||
        eRetPostfix == EHierarchyCallbackReturn.STOP_ITERATION)
    {
      // stop complete iteration
      return EHierarchyCallbackReturn.STOP_ITERATION;
    }
    if (eRetPrefix == EHierarchyCallbackReturn.USE_PARENTS_NEXT_SIBLING ||
        eRetChildren == EHierarchyCallbackReturn.USE_PARENTS_NEXT_SIBLING ||
        eRetPostfix == EHierarchyCallbackReturn.USE_PARENTS_NEXT_SIBLING)
    {
      // skip children and siblings
      return EHierarchyCallbackReturn.USE_PARENTS_NEXT_SIBLING;
    }

    // continue
    return EHierarchyCallbackReturn.CONTINUE;
  }

  public static <DATATYPE, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> void walkTree (@Nonnull final IBasicTree <DATATYPE, ITEMTYPE> aTree,
                                                                                                @Nonnull final IHierarchyWalkerDynamicCallback <? super ITEMTYPE> aCallback)
  {
    walkTree (aTree, new ChildrenProviderHasChildren <ITEMTYPE> (), aCallback);
  }

  public static <DATATYPE, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> void walkTree (@Nonnull final IBasicTree <DATATYPE, ITEMTYPE> aTree,
                                                                                                @Nonnull final IChildrenProvider <ITEMTYPE> aChildrenResolver,
                                                                                                @Nonnull final IHierarchyWalkerDynamicCallback <? super ITEMTYPE> aCallback)
  {
    ValueEnforcer.notNull (aTree, "Tree");

    walkSubTree (aTree.getRootItem (), aChildrenResolver, aCallback);
  }

  public static <DATATYPE, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> void walkTreeData (@Nonnull final IBasicTree <DATATYPE, ITEMTYPE> aTree,
                                                                                                    @Nonnull final IHierarchyWalkerDynamicCallback <? super DATATYPE> aDataCallback)
  {
    walkTreeData (aTree, new ChildrenProviderHasChildren <ITEMTYPE> (), aDataCallback);
  }

  public static <DATATYPE, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> void walkTreeData (@Nonnull final IBasicTree <DATATYPE, ITEMTYPE> aTree,
                                                                                                    @Nonnull final IChildrenProvider <ITEMTYPE> aChildrenProvider,
                                                                                                    @Nonnull final IHierarchyWalkerDynamicCallback <? super DATATYPE> aDataCallback)
  {
    ValueEnforcer.notNull (aTree, "Tree");

    walkSubTreeData (aTree.getRootItem (), aChildrenProvider, aDataCallback);
  }

  public static <DATATYPE, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> void walkSubTree (@Nonnull final ITEMTYPE aTreeItem,
                                                                                                   @Nonnull final IHierarchyWalkerDynamicCallback <? super ITEMTYPE> aCallback)
  {
    walkSubTree (aTreeItem, new ChildrenProviderHasChildren <ITEMTYPE> (), aCallback);
  }

  public static <DATATYPE, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> void walkSubTree (@Nonnull final ITEMTYPE aTreeItem,
                                                                                                   @Nonnull final IChildrenProvider <ITEMTYPE> aChildrenProvider,
                                                                                                   @Nonnull final IHierarchyWalkerDynamicCallback <? super ITEMTYPE> aCallback)
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
                                                                                                       @Nonnull final IHierarchyWalkerDynamicCallback <? super DATATYPE> aDataCallback)
  {
    walkSubTreeData (aTreeItem, new ChildrenProviderHasChildren <ITEMTYPE> (), aDataCallback);
  }

  public static <DATATYPE, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> void walkSubTreeData (@Nonnull final ITEMTYPE aTreeItem,
                                                                                                       @Nonnull final IChildrenProvider <ITEMTYPE> aChildrenProvider,
                                                                                                       @Nonnull final IHierarchyWalkerDynamicCallback <? super DATATYPE> aDataCallback)
  {
    ValueEnforcer.notNull (aDataCallback, "DataCallback");

    // Wrap callback
    walkSubTree (aTreeItem, aChildrenProvider, new DefaultHierarchyWalkerDynamicCallback <ITEMTYPE> ()
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
      @Nonnull
      public EHierarchyCallbackReturn onItemBeforeChildren (@Nonnull final ITEMTYPE aItem)
      {
        return aDataCallback.onItemBeforeChildren (aItem.getData ());
      }

      @Override
      @Nonnull
      public EHierarchyCallbackReturn onItemAfterChildren (@Nonnull final ITEMTYPE aItem)
      {
        return aDataCallback.onItemAfterChildren (aItem.getData ());
      }
    });
  }
}
