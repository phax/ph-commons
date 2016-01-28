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
package com.helger.commons.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Comparator;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link DefaultTree}.
 *
 * @author Philip Helger
 */
public final class DefaultTreeTest
{
  @Test
  public void testBasic ()
  {
    final DefaultTree <String> t = new DefaultTree <String> ();

    // root item
    final DefaultTreeItem <String> ti = t.getRootItem ().createChildItem ("Hallo");
    assertNotNull (ti);
    assertEquals ("Hallo", ti.getData ());
    assertFalse (ti.hasChildren ());
    assertEquals (0, ti.getChildCount ());
    assertNull (ti.getAllChildren ());
    assertSame (t.getRootItem (), ti.getParent ());

    // level 1
    final DefaultTreeItem <String> ti1 = ti.createChildItem ("Welt");
    assertNotNull (ti1);
    assertEquals ("Welt", ti1.getData ());
    assertFalse (ti1.hasChildren ());
    assertEquals (0, ti1.getChildCount ());
    assertNull (ti1.getAllChildren ());
    assertSame (ti, ti1.getParent ());

    assertTrue (ti.hasChildren ());
    assertEquals (1, ti.getChildCount ());
    assertTrue (ti.getAllChildren ().contains (ti1));
  }

  @Test
  public void testEqualsHashCode ()
  {
    final DefaultTree <String> t = new DefaultTree <String> ();
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (t, new DefaultTree <String> ());
    t.getRootItem ().createChildItem ("data");
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (t, new DefaultTree <String> ());
  }

  @Test
  public void testDelete ()
  {
    final DefaultTree <String> t = new DefaultTree <String> ();

    // root item
    final DefaultTreeItem <String> ti = t.getRootItem ().createChildItem ("Hallo");
    final DefaultTreeItem <String> ti1 = ti.createChildItem ("Welt");

    // item not present
    assertTrue (ti.removeChild (ti).isUnchanged ());

    // delete child first
    assertTrue (ti.removeChild (ti1).isChanged ());
    assertTrue (t.getRootItem ().removeChild (ti).isChanged ());
  }

  @Test
  public void testReorder ()
  {
    final DefaultTree <String> t = new DefaultTree <String> ();

    // root item
    final DefaultTreeItem <String> ti = t.getRootItem ().createChildItem ("Hallo");

    // no items yet....
    assertFalse (ti.hasChildren ());
    ti.reorderChildItems (Comparator.comparing (IBasicTreeItem::getData));
    assertFalse (ti.hasChildren ());

    // add 2 items
    assertNotNull (ti.createChildItem ("Welt2"));
    assertNotNull (ti.createChildItem ("Welt1"));
    assertTrue (ti.hasChildren ());

    // check current order
    assertEquals (2, ti.getChildCount ());
    assertEquals ("Welt2", ti.getAllChildren ().get (0).getData ());
    assertEquals ("Welt1", ti.getAllChildren ().get (1).getData ());

    // reorder
    ti.reorderChildItems (Comparator.comparing (IBasicTreeItem::getData));

    // check new order
    assertEquals (2, ti.getChildCount ());
    assertEquals ("Welt1", ti.getAllChildren ().get (0).getData ());
    assertEquals ("Welt2", ti.getAllChildren ().get (1).getData ());
  }

  @Test
  public void testChangeParent ()
  {
    final DefaultTree <String> t1 = new DefaultTree <String> ();
    final DefaultTreeItem <String> t1i1 = t1.getRootItem ().createChildItem ("Hallo");
    final DefaultTreeItem <String> t1i2 = t1i1.createChildItem ("Hallo");
    final DefaultTree <String> t2 = new DefaultTree <String> ();
    final DefaultTreeItem <String> t2i1 = t2.getRootItem ().createChildItem ("Hallo");
    final DefaultTreeItem <String> t2i2 = t2i1.createChildItem ("Hallo");

    // cannot work
    assertTrue (t2i1.changeParent (t2i1).isFailure ());
    assertTrue (t2i1.changeParent (t2i2).isFailure ());

    // preconditions
    assertFalse (t1i2.hasChildren ());
    assertTrue (t2i1.hasChildren ());

    // perform
    assertTrue (t2i1.getAllChildren ().contains (t2i2));
    assertTrue (t2i2.changeParent (t1i2).isSuccess ());
    assertTrue (t1i2.getAllChildren ().contains (t2i2));
    assertSame (t1i2, t2i2.getParent ());
    assertFalse (t2i1.getAllChildren ().contains (t2i2));

    // postconditions
    assertTrue (t1i2.hasChildren ());
    assertFalse (t2i1.hasChildren ());
  }

  @Test
  public void testCtor ()
  {
    final DefaultTree <String> t = new DefaultTree <String> ();
    assertNotNull (t.getRootItem ());
    assertTrue (t.toString ().length () > 0);
  }
}
