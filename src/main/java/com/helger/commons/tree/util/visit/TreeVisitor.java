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
package com.helger.commons.tree.util.visit;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.w3c.dom.traversal.TreeWalker;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.hierarchy.ChildrenProviderHasChildren;
import com.helger.commons.hierarchy.IChildrenProvider;
import com.helger.commons.hierarchy.visit.ChildrenProviderHierarchyVisitor;
import com.helger.commons.hierarchy.visit.DefaultHierarchyVisitorCallback;
import com.helger.commons.hierarchy.visit.EHierarchyVisitorReturn;
import com.helger.commons.hierarchy.visit.IHierarchyVisitorCallback;
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
public final class TreeVisitor
{
  @PresentForCodeCoverage
  private static final TreeVisitor s_aInstance = new TreeVisitor ();

  private TreeVisitor ()
  {}

  public static <DATATYPE, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> void visitTree (@Nonnull final IBasicTree <DATATYPE, ITEMTYPE> aTree,
                                                                                                 @Nonnull final IHierarchyVisitorCallback <? super ITEMTYPE> aCallback)
  {
    visitTree (aTree, new ChildrenProviderHasChildren <ITEMTYPE> (), aCallback);
  }

  public static <DATATYPE, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> void visitTree (@Nonnull final IBasicTree <DATATYPE, ITEMTYPE> aTree,
                                                                                                 @Nonnull final IChildrenProvider <ITEMTYPE> aChildrenResolver,
                                                                                                 @Nonnull final IHierarchyVisitorCallback <? super ITEMTYPE> aCallback)
  {
    ValueEnforcer.notNull (aTree, "Tree");
    visitTreeItem (aTree.getRootItem (), aChildrenResolver, aCallback);
  }

  public static <DATATYPE, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> void visitTreeData (@Nonnull final IBasicTree <DATATYPE, ITEMTYPE> aTree,
                                                                                                     @Nonnull final IHierarchyVisitorCallback <? super DATATYPE> aDataCallback)
  {
    visitTreeData (aTree, new ChildrenProviderHasChildren <ITEMTYPE> (), aDataCallback);
  }

  public static <DATATYPE, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> void visitTreeData (@Nonnull final IBasicTree <DATATYPE, ITEMTYPE> aTree,
                                                                                                     @Nonnull final IChildrenProvider <ITEMTYPE> aChildrenProvider,
                                                                                                     @Nonnull final IHierarchyVisitorCallback <? super DATATYPE> aDataCallback)
  {
    ValueEnforcer.notNull (aTree, "Tree");

    visitTreeItemData (aTree.getRootItem (), aChildrenProvider, aDataCallback);
  }

  public static <DATATYPE, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> void visitTreeItem (@Nonnull final ITEMTYPE aTreeItem,
                                                                                                     @Nonnull final IHierarchyVisitorCallback <? super ITEMTYPE> aCallback)
  {
    ValueEnforcer.notNull (aTreeItem, "TreeItem");
    ChildrenProviderHierarchyVisitor.visitFrom (aTreeItem, aCallback, false);
  }

  public static <DATATYPE, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> void visitTreeItem (@Nonnull final ITEMTYPE aTreeItem,
                                                                                                     @Nonnull final IChildrenProvider <ITEMTYPE> aChildrenProvider,
                                                                                                     @Nonnull final IHierarchyVisitorCallback <? super ITEMTYPE> aCallback)
  {
    ValueEnforcer.notNull (aTreeItem, "TreeItem");
    ChildrenProviderHierarchyVisitor.visitFrom (aTreeItem, aChildrenProvider, aCallback, false);
  }

  public static <DATATYPE, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> void visitTreeItemData (@Nonnull final ITEMTYPE aTreeItem,
                                                                                                         @Nonnull final IHierarchyVisitorCallback <? super DATATYPE> aDataCallback)
  {
    visitTreeItemData (aTreeItem, new ChildrenProviderHasChildren <ITEMTYPE> (), aDataCallback);
  }

  public static <DATATYPE, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> void visitTreeItemData (@Nonnull final ITEMTYPE aTreeItem,
                                                                                                         @Nonnull final IChildrenProvider <ITEMTYPE> aChildrenProvider,
                                                                                                         @Nonnull final IHierarchyVisitorCallback <? super DATATYPE> aDataCallback)
  {
    ValueEnforcer.notNull (aDataCallback, "DataCallback");

    // Wrap callback
    visitTreeItem (aTreeItem, aChildrenProvider, new DefaultHierarchyVisitorCallback <ITEMTYPE> ()
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
      @Nonnull
      public EHierarchyVisitorReturn onItemBeforeChildren (@Nonnull final ITEMTYPE aItem)
      {
        return aDataCallback.onItemBeforeChildren (aItem.getData ());
      }

      @Override
      @Nonnull
      public EHierarchyVisitorReturn onItemAfterChildren (@Nonnull final ITEMTYPE aItem)
      {
        return aDataCallback.onItemAfterChildren (aItem.getData ());
      }

      @Override
      public void end ()
      {
        aDataCallback.end ();
        super.end ();
      }
    });
  }
}
