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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.base.state.EContinue;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsCollection;
import com.helger.collection.commons.ICommonsList;

/**
 * Test class for class {@link BasicTree}.
 *
 * @author Philip Helger
 */
public final class BasicTreeTest
{
  @Test
  public void testChildrenHandling ()
  {
    final DefaultTree <String> aTree = new DefaultTree <> ();
    final DefaultTreeItem <String> aRootItem = aTree.getRootItem ();
    assertNotNull (aRootItem);

    // The root item is the one and only child of the tree itself
    assertTrue (aTree.hasChildren ());
    assertEquals (1, aTree.getChildCount ());

    final ICommonsCollection <DefaultTreeItem <String>> aChildren = aTree.getAllChildren ();
    assertEquals (1, aChildren.size ());
    assertTrue (aChildren.contains (aRootItem));
    assertNotNull (aTree.getChildren ());
  }

  @Test
  public void testForAllChildren ()
  {
    final DefaultTree <String> aTree = new DefaultTree <> ();
    final DefaultTreeItem <String> aRootItem = aTree.getRootItem ();

    final ICommonsList <DefaultTreeItem <String>> aVisited = new CommonsArrayList <> ();
    aTree.forAllChildren (aVisited::add);
    assertEquals (new CommonsArrayList <> (aRootItem), aVisited);

    aVisited.clear ();
    assertSame (EContinue.BREAK, aTree.forAllChildrenBreakable (x -> {
      aVisited.add (x);
      return EContinue.BREAK;
    }));
    assertEquals (1, aVisited.size ());
  }

  @Test
  public void testForAllChildrenFiltered ()
  {
    final DefaultTree <String> aTree = new DefaultTree <> ();

    final ICommonsList <DefaultTreeItem <String>> aVisited = new CommonsArrayList <> ();
    aTree.forAllChildren (x -> false, aVisited::add);
    assertTrue (aVisited.isEmpty ());

    aTree.forAllChildren (x -> true, aVisited::add);
    assertEquals (1, aVisited.size ());
  }

  @Test
  public void testForAllChildrenMapped ()
  {
    final DefaultTree <String> aTree = new DefaultTree <> ();
    aTree.getRootItem ().createChildItem ("child");

    final ICommonsList <Integer> aVisited = new CommonsArrayList <> ();
    aTree.forAllChildrenMapped (x -> false, x -> Integer.valueOf (x.getChildCount ()), aVisited::add);
    assertTrue (aVisited.isEmpty ());

    aTree.forAllChildrenMapped (x -> true, x -> Integer.valueOf (x.getChildCount ()), aVisited::add);
    assertEquals (new CommonsArrayList <> (Integer.valueOf (1)), aVisited);
  }

  @Test
  public void testEqualsHashcodeToString ()
  {
    final DefaultTree <String> aTree = new DefaultTree <> ();
    assertEquals (aTree, aTree);
    assertEquals (aTree.hashCode (), aTree.hashCode ());
    assertFalse (aTree.equals (null));
    assertFalse (aTree.equals ("any other type"));
    assertNotNull (aTree.toString ());

    // Two empty trees are equal
    assertEquals (aTree, new DefaultTree <> ());

    aTree.getRootItem ().createChildItem ("child");
    assertFalse (aTree.equals (new DefaultTree <> ()));
  }

  @SuppressWarnings ("unused")
  @Test
  public void testInvalidParams ()
  {
    try
    {
      new BasicTree <String, DefaultTreeItem <String>> (null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      // The factory returns null for the root item
      new BasicTree <String, DefaultTreeItem <String>> (new ITreeItemFactory <> ()
      {
        public DefaultTreeItem <String> createRoot ()
        {
          return null;
        }

        public DefaultTreeItem <String> create (final DefaultTreeItem <String> aParent)
        {
          return null;
        }
      });
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
    }
  }
}
