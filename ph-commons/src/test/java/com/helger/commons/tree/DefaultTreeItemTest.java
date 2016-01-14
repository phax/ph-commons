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
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link DefaultTreeItem}.
 *
 * @author Philip Helger
 */
public final class DefaultTreeItemTest
{
  @Test
  public void testCtor ()
  {
    final DefaultTreeItemFactory <String> tif = new DefaultTreeItemFactory <String> ();
    final DefaultTreeItem <String> t = tif.createRoot ();
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (t, tif.createRoot ());
    final DefaultTreeItem <String> c = t.createChildItem ("any");
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (t, tif.createRoot ());

    assertTrue (t.isRootItem ());
    assertFalse (c.isRootItem ());
    assertTrue (t.isSameOrChildOf (t));
    assertFalse (t.isSameOrChildOf (c));
    assertTrue (c.isSameOrChildOf (t));
    assertTrue (c.isSameOrChildOf (c));
  }

  @Test
  public void testLevel ()
  {
    final DefaultTreeItemFactory <String> tif = new DefaultTreeItemFactory <String> ();
    final DefaultTreeItem <String> t = tif.createRoot ();
    assertEquals (0, t.getLevel ());
    final DefaultTreeItem <String> c = t.createChildItem ("any");
    assertEquals (0, t.getLevel ());
    assertEquals (1, c.getLevel ());
    final DefaultTreeItem <String> c2 = c.createChildItem ("any2");
    assertEquals (0, t.getLevel ());
    assertEquals (1, c.getLevel ());
    assertEquals (2, c2.getLevel ());
    final DefaultTreeItem <String> c2b = c.createChildItem ("any2");
    assertEquals (0, t.getLevel ());
    assertEquals (1, c.getLevel ());
    assertEquals (2, c2.getLevel ());
    assertEquals (2, c2b.getLevel ());
    c2b.changeParent (t);
    assertEquals (0, t.getLevel ());
    assertEquals (1, c.getLevel ());
    assertEquals (2, c2.getLevel ());
    assertEquals (1, c2b.getLevel ());
  }
}
