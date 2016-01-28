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
package com.helger.commons.tree.withid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Comparator;

import org.junit.Test;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.id.IHasID;
import com.helger.commons.mock.CommonsTestHelper;
import com.helger.commons.tree.IBasicTreeItem;

/**
 * Test class for class {@link DefaultTreeItemWithID}.
 *
 * @author Philip Helger
 */
public final class DefaultTreeItemWithIDTest
{
  @Test
  public void testBasic ()
  {
    final DefaultTreeWithID <String, String> t = new DefaultTreeWithID <String, String> ();

    // tree root
    assertNotNull (t.getRootItem ());
    assertTrue (t.getRootItem ().isRootItem ());
    assertTrue (t.getRootItem ().isSameOrChildOf (t.getRootItem ()));

    // root item
    final DefaultTreeItemWithID <String, String> ti = t.getRootItem ().createChildItem ("root", "Hallo");
    assertNotNull (ti);
    assertEquals ("root", ti.getID ());
    assertEquals ("Hallo", ti.getData ());
    assertFalse (ti.hasChildren ());
    assertEquals (0, ti.getChildCount ());
    assertTrue (CollectionHelper.isEmpty (ti.getAllChildren ()));
    assertSame (t.getRootItem (), ti.getParent ());
    assertFalse (ti.isRootItem ());
    assertTrue (ti.isSameOrChildOf (t.getRootItem ()));
    assertFalse (t.getRootItem ().isSameOrChildOf (ti));

    // level 1
    final DefaultTreeItemWithID <String, String> ti1 = ti.createChildItem ("root1", "Welt");
    assertNotNull (ti1);
    assertEquals ("root1", ti1.getID ());
    assertEquals ("Welt", ti1.getData ());
    assertFalse (ti1.hasChildren ());
    assertEquals (0, ti1.getChildCount ());
    assertTrue (CollectionHelper.isEmpty (ti1.getAllChildren ()));
    assertSame (ti, ti1.getParent ());

    assertTrue (ti.hasChildren ());
    assertEquals (1, ti.getChildCount ());
    assertTrue (ti.getAllChildren ().contains (ti1));

    // test get child of ID
    assertNotNull (ti.getChildItemOfDataID ("root1"));
    assertNull (ti.getChildItemOfDataID ("any"));
    assertNull (ti.getChildItemOfDataID (""));
    assertNull (ti.getChildItemOfDataID (null));
    assertEquals ("Welt", ti.getChildItemOfDataID ("root1").getData ());
  }

  @Test
  public void testAdd ()
  {
    final DefaultTreeWithID <String, String> t = new DefaultTreeWithID <String, String> ();

    // root item
    final DefaultTreeItemWithID <String, String> ti = t.getRootItem ().createChildItem ("root", "Hallo");
    assertNotNull (ti);

    // level 1
    final DefaultTreeItemWithID <String, String> ti1 = ti.createChildItem ("root1", "Welt");
    assertNotNull (ti1);
    assertEquals ("Welt", ti.getChildItemOfDataID ("root1").getData ());

    // do not overwrite
    assertNull (ti.createChildItem ("root1", "Hudriwudri", false));
    assertEquals ("Welt", ti.getChildItemOfDataID ("root1").getData ());

    // overwrite
    assertNotNull (ti.createChildItem ("root1", "Hudriwudri", true));
    assertEquals ("Hudriwudri", ti.getChildItemOfDataID ("root1").getData ());

    try
    {
      // null ID not allowed
      ti.createChildItem (null, "Data!!");
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testDelete ()
  {
    final DefaultTreeWithID <String, String> t = new DefaultTreeWithID <String, String> ();
    // No children present
    assertFalse (t.getRootItem ().removeChild ("root1").isChanged ());

    // root item
    final DefaultTreeItemWithID <String, String> ti = t.getRootItem ().createChildItem ("root", "Hallo");
    assertNotNull (ti.createChildItem ("root1", "Welt"));

    // item not present
    assertTrue (ti.removeChild ("root").isUnchanged ());

    // delete child first
    assertTrue (ti.removeChild ("root1").isChanged ());
    assertTrue (t.getRootItem ().removeChild ("root").isChanged ());
    assertFalse (t.getRootItem ().hasChildren ());

    t.getRootItem ().createChildItem ("x2", "y2");
    assertTrue (t.getRootItem ().hasChildren ());
    assertTrue (t.getRootItem ().removeAllChildren ().isChanged ());
    assertFalse (t.getRootItem ().hasChildren ());
    assertFalse (t.getRootItem ().removeAllChildren ().isChanged ());
  }

  @Test
  public void testReorderByItem ()
  {
    final DefaultTreeWithID <String, String> t = new DefaultTreeWithID <String, String> ();

    // root item
    final DefaultTreeItemWithID <String, String> ti = t.getRootItem ().createChildItem ("root", "Hallo");

    // no items yet....
    assertFalse (ti.hasChildren ());
    ti.reorderChildrenByItems (Comparator.comparing (IBasicTreeItem::getData));
    assertFalse (ti.hasChildren ());

    // add 2 items
    assertNotNull (ti.createChildItem ("id2", "Welt2"));
    assertNotNull (ti.createChildItem ("id1", "Welt1"));
    assertTrue (ti.hasChildren ());

    // check current order
    assertEquals (2, ti.getChildCount ());
    assertEquals ("id2", ti.getAllChildren ().get (0).getID ());
    assertEquals ("Welt2", ti.getAllChildren ().get (0).getData ());
    assertEquals ("id1", ti.getAllChildren ().get (1).getID ());
    assertEquals ("Welt1", ti.getAllChildren ().get (1).getData ());

    // reorder
    ti.reorderChildrenByItems (Comparator.comparing (IBasicTreeItem::getData));

    // check new order
    assertEquals (2, ti.getChildCount ());
    assertEquals ("id1", ti.getAllChildren ().get (0).getID ());
    assertEquals ("Welt1", ti.getAllChildren ().get (0).getData ());
    assertEquals ("id2", ti.getAllChildren ().get (1).getID ());
    assertEquals ("Welt2", ti.getAllChildren ().get (1).getData ());
  }

  @Test
  public void testReorderByKey ()
  {
    final DefaultTreeWithID <String, String> t = new DefaultTreeWithID <String, String> ();

    // root item
    final DefaultTreeItemWithID <String, String> ti = t.getRootItem ().createChildItem ("root", "Hallo");

    // no items yet....
    assertFalse (ti.hasChildren ());

    // add 2 items
    assertNotNull (ti.createChildItem ("id2", "Welt2"));
    assertNotNull (ti.createChildItem ("id1", "Welt1"));
    assertTrue (ti.hasChildren ());

    // check current order
    assertEquals (2, ti.getChildCount ());
    assertEquals ("id2", ti.getAllChildren ().get (0).getID ());
    assertEquals ("Welt2", ti.getAllChildren ().get (0).getData ());
    assertEquals ("id1", ti.getAllChildren ().get (1).getID ());
    assertEquals ("Welt1", ti.getAllChildren ().get (1).getData ());

    // reorder
    ti.reorderChildrenByItems (IHasID.getComparatorID ());

    // check new order
    assertEquals (2, ti.getChildCount ());
    assertEquals ("id1", ti.getAllChildren ().get (0).getID ());
    assertEquals ("Welt1", ti.getAllChildren ().get (0).getData ());
    assertEquals ("id2", ti.getAllChildren ().get (1).getID ());
    assertEquals ("Welt2", ti.getAllChildren ().get (1).getData ());
  }

  @Test
  public void testCtor ()
  {
    final DefaultTreeWithID <String, Object> t = new DefaultTreeWithID <String, Object> ();
    assertNotNull (t.getRootItem ());
    assertTrue (t.toString ().length () > 0);

    try
    {
      new DefaultTreeItemWithID <Object, Object> (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      new DefaultTreeItemWithID <Object, Object> (null, "dataid");
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testStdMethods ()
  {
    final DefaultTreeWithID <String, String> t = new DefaultTreeWithID <String, String> ();
    final DefaultTreeWithID <String, String> t2 = new DefaultTreeWithID <String, String> ();
    t2.getRootItem ().createChildItem ("dataid", "Data");

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (t.getRootItem (),
                                                                       new DefaultTreeWithID <String, String> ().getRootItem ());
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (t.getRootItem (), t2.getRootItem ());
  }

  @Test
  public void testInvalid ()
  {
    final DefaultTreeWithID <String, String> t = new DefaultTreeWithID <String, String> ();

    try
    {
      t.getRootItem ().getChildAtIndex (0);
      fail ();
    }
    catch (final IndexOutOfBoundsException ex)
    {}
    try
    {
      t.getRootItem ().isSameOrChildOf (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      t.getRootItem ().changeParent (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    assertFalse (t.getRootItem ().removeChild (null).isChanged ());
  }

  @Test
  public void testChangeParent ()
  {
    final DefaultTreeItemWithID <String, String> root = new DefaultTreeWithID <String, String> ().getRootItem ();
    final DefaultTreeItemWithID <String, String> root2 = root.createChildItem ("root", "root");
    final DefaultTreeItemWithID <String, String> child1 = root2.createChildItem ("child1", "child1");

    // same parent
    assertTrue (child1.changeParent (root2).isSuccess ());

    // Cannot invert hierarchy
    assertTrue (root2.changeParent (child1).isFailure ());

    // success
    assertTrue (child1.changeParent (root).isSuccess ());

    assertEquals (2, root.getChildCount ());
    assertEquals (0, root2.getChildCount ());
    assertEquals (0, child1.getChildCount ());

    assertNull (root.getParent ());
    assertSame (root, root2.getParent ());
    assertSame (root, child1.getParent ());
  }
}
