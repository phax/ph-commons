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
package com.helger.commons.tree.sort;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.List;
import java.util.Locale;

import org.junit.Test;

import com.helger.commons.compare.ISerializableComparator;
import com.helger.commons.tree.withid.DefaultTreeItemWithID;
import com.helger.commons.tree.withid.DefaultTreeWithID;

/**
 * Test class for class {@link TreeWithIDSorter}
 *
 * @author Philip Helger
 */
public final class TreeWithIDSorterTest
{
  @Test
  public void testTreeWithIDValue ()
  {
    final DefaultTreeWithID <String, String> aTree = new DefaultTreeWithID <String, String> ();
    assertNotNull (aTree.getRootItem ());
    final DefaultTreeItemWithID <String, String> i1 = aTree.getRootItem ().createChildItem ("r1", "Windows");
    i1.createChildItem ("w1", "sxs");
    i1.createChildItem ("w2", "temp");
    i1.createChildItem ("w3", "System32");
    final DefaultTreeItemWithID <String, String> i2 = aTree.getRootItem ().createChildItem ("r2", "Program Files");
    i2.createChildItem ("p1", "Eclipse");
    i2.createChildItem ("p2", "Apache Software Foundation");

    // Sort all items by value
    TreeWithIDSorter.sortByValue (aTree, ISerializableComparator.getComparatorCollating (Locale.US));

    assertEquals (2, aTree.getRootItem ().getChildCount ());
    List <? extends DefaultTreeItemWithID <String, String>> aChildren = aTree.getRootItem ().getAllChildren ();
    assertSame (i2, aChildren.get (0));
    assertSame (i1, aChildren.get (1));
    // Test Apache (children must also be sorted)
    assertEquals (2, i2.getChildCount ());
    assertEquals ("Apache Software Foundation", i2.getChildAtIndex (0).getData ());
    assertEquals ("Eclipse", i2.getChildAtIndex (1).getData ());
    // Test Windows
    assertEquals (3, i1.getChildCount ());
    assertEquals ("sxs", i1.getChildAtIndex (0).getData ());
    assertEquals ("System32", i1.getChildAtIndex (1).getData ());
    assertEquals ("temp", i1.getChildAtIndex (2).getData ());

    // Sort all items by keys
    TreeWithIDSorter.sortByID (aTree, ISerializableComparator.getComparatorCollating (Locale.US));

    assertEquals (2, aTree.getRootItem ().getChildCount ());
    aChildren = aTree.getRootItem ().getAllChildren ();
    assertSame (i1, aChildren.get (0));
    assertSame (i2, aChildren.get (1));
    // Test Windows
    assertEquals (3, i1.getChildCount ());
    assertEquals ("sxs", i1.getChildAtIndex (0).getData ());
    assertEquals ("temp", i1.getChildAtIndex (1).getData ());
    assertEquals ("System32", i1.getChildAtIndex (2).getData ());
    // Test Apache (children must also be sorted)
    assertEquals (2, i2.getChildCount ());
    assertEquals ("Eclipse", i2.getChildAtIndex (0).getData ());
    assertEquals ("Apache Software Foundation", i2.getChildAtIndex (1).getData ());
  }
}
