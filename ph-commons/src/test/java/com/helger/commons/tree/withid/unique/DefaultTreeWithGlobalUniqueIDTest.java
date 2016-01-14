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
package com.helger.commons.tree.withid.unique;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.mock.CommonsTestHelper;
import com.helger.commons.tree.withid.DefaultTreeItemWithID;

/**
 * Test class for class {@link DefaultTreeWithGlobalUniqueID}.
 *
 * @author Philip Helger
 */
public final class DefaultTreeWithGlobalUniqueIDTest
{
  @Test
  public void testUniqueness ()
  {
    final DefaultTreeWithGlobalUniqueID <String, String> aTestTree = new DefaultTreeWithGlobalUniqueID <String, String> ();
    final DefaultTreeItemWithID <String, String> x1 = aTestTree.getRootItem ().createChildItem ("x1", "1");
    x1.createChildItem ("x2", "a");
    x1.createChildItem ("x3", "b");
    x1.createChildItem ("x4", "c");
    final DefaultTreeItemWithID <String, String> x5 = aTestTree.getRootItem ().createChildItem ("x5", "2");
    x5.createChildItem ("x6", "a");
    final DefaultTreeItemWithID <String, String> x7 = x5.createChildItem ("x7", "b");
    x5.createChildItem ("x8", "c");
    final DefaultTreeItemWithID <String, String> x9 = aTestTree.getRootItem ().createChildItem ("x9", "3");
    x9.createChildItem ("x10", "a");
    x9.createChildItem ("x11", "b");
    x9.createChildItem ("x12", "c");
    try
    {
      // ID already in use
      x1.createChildItem ("x12", "any");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    assertSame (x1, aTestTree.getItemWithID ("x1"));
    assertSame ("1", aTestTree.getItemWithID ("x1").getData ());
    assertSame (x5, aTestTree.getItemWithID ("x5"));
    assertSame ("2", aTestTree.getItemWithID ("x5").getData ());
    assertSame (x7, aTestTree.getItemWithID ("x7"));
    assertSame ("b", aTestTree.getItemWithID ("x7").getData ());
    assertNull (aTestTree.getItemWithID ("abc"));
  }

  @Test
  public void testRest ()
  {
    final DefaultTreeWithGlobalUniqueID <String, String> aTestTree = new DefaultTreeWithGlobalUniqueID <String, String> ();
    final DefaultTreeItemWithID <String, String> x1 = aTestTree.getRootItem ().createChildItem ("x1", "1");
    x1.createChildItem ("x2", "a");
    final DefaultTreeItemWithID <String, String> x3 = x1.createChildItem ("x3", "b");
    x1.createChildItem ("x4", "c");

    // getFactory
    assertNotNull (aTestTree.getFactory ());
    assertTrue (aTestTree.getFactory () instanceof DefaultTreeItemWithUniqueIDFactory);

    // getChildWithID
    assertSame (x3, aTestTree.getChildWithID (x1, "x3"));
    assertNull (aTestTree.getChildWithID (null, "x3"));

    // getAllItems
    assertEquals (4, aTestTree.getAllItems ().size ());

    // hasChildren
    assertTrue (aTestTree.hasChildren (null));
    assertTrue (aTestTree.hasChildren (x1));
    assertFalse (aTestTree.hasChildren (x3));

    // getChildCount
    assertEquals (1, aTestTree.getChildCount (null));
    assertEquals (3, aTestTree.getChildCount (x1));
    assertEquals (0, aTestTree.getChildCount (x3));

    // getChildren
    assertEquals (1, aTestTree.getAllChildren (null).size ());
    assertEquals (3, aTestTree.getAllChildren (x1).size ());
    assertNull (aTestTree.getAllChildren (x3));

    // isItemSameOrDescendant
    assertFalse (aTestTree.isItemSameOrDescendant (null, "x1"));
    assertTrue (aTestTree.isItemSameOrDescendant ("x1", "x3"));
    assertFalse (aTestTree.isItemSameOrDescendant ("x3", null));
    assertFalse (aTestTree.isItemSameOrDescendant ("x3", "x1"));

    // other tree
    final DefaultTreeWithGlobalUniqueID <String, String> aTestTree2 = new DefaultTreeWithGlobalUniqueID <String, String> ();
    final DefaultTreeItemWithID <String, String> x12 = aTestTree2.getRootItem ().createChildItem ("x1", "1");
    x12.createChildItem ("x2", "a");
    x12.createChildItem ("x3", "b");
    x12.createChildItem ("x4", "c");

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (aTestTree, aTestTree2);
    x12.createChildItem ("x5", "d");
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (aTestTree, aTestTree2);

    // test remove
    assertTrue (x12.removeChild ("x5").isChanged ());
    assertFalse (x12.removeChild ("x5").isChanged ());
  }

  @Test
  public void testChildProvder ()
  {
    final DefaultTreeWithGlobalUniqueID <String, String> aTestTree2 = new DefaultTreeWithGlobalUniqueID <String, String> ();

    assertEquals (0, aTestTree2.getChildCount (null));
    assertFalse (aTestTree2.hasChildren (null));
    assertTrue (CollectionHelper.isEmpty (aTestTree2.getAllChildren (null)));

    final DefaultTreeItemWithID <String, String> x12 = aTestTree2.getRootItem ().createChildItem ("x1", "1");
    x12.createChildItem ("x2", "a");
    x12.createChildItem ("x3", "b");
    x12.createChildItem ("x4", "c");

    assertEquals (1, aTestTree2.getChildCount (null));
    assertTrue (aTestTree2.hasChildren (null));
    assertEquals (1, aTestTree2.getAllChildren (null).size ());

    assertEquals (3, aTestTree2.getChildCount (x12));
    assertTrue (aTestTree2.hasChildren (x12));
    assertEquals (3, aTestTree2.getAllChildren (x12).size ());
  }

  @Test
  public void testChangeParent ()
  {
    final DefaultTreeWithGlobalUniqueID <String, String> aTestTree = new DefaultTreeWithGlobalUniqueID <String, String> ();
    final DefaultTreeItemWithID <String, String> x1 = aTestTree.getRootItem ().createChildItem ("x1", "1");
    final DefaultTreeItemWithID <String, String> x2 = x1.createChildItem ("x2", "a");
    final DefaultTreeItemWithID <String, String> x3 = x1.createChildItem ("x3", "b");

    assertNotNull (aTestTree.getItemWithID ("x1"));
    assertNotNull (aTestTree.getItemWithID ("x2"));
    assertNotNull (aTestTree.getItemWithID ("x3"));

    // Check parent hierarchy
    assertSame (x1, x2.getParent ());
    assertSame (x1, x3.getParent ());

    // Change parent and check
    assertTrue (x3.changeParent (x2).isSuccess ());
    assertSame (x1, x2.getParent ());
    assertSame (x2, x3.getParent ());

    // No change
    assertTrue (x3.changeParent (x2).isSuccess ());
    assertSame (x1, x2.getParent ());
    assertSame (x2, x3.getParent ());

    // Still all present?
    assertNotNull (aTestTree.getItemWithID ("x1"));
    assertNotNull (aTestTree.getItemWithID ("x2"));
    assertNotNull (aTestTree.getItemWithID ("x3"));
  }
}
