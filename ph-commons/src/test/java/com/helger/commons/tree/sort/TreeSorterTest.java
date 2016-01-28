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

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

import com.helger.commons.compare.ISerializableComparator;
import com.helger.commons.tree.DefaultTree;
import com.helger.commons.tree.DefaultTreeItem;

/**
 * Test class for class {@link TreeSorter}
 *
 * @author Philip Helger
 */
public final class TreeSorterTest
{
  @Test
  public void testTreeString ()
  {
    final DefaultTree <String> aTree = new DefaultTree <String> ();
    assertNotNull (aTree.getRootItem ());
    final DefaultTreeItem <String> i1 = aTree.getRootItem ().createChildItem ("Windows");
    i1.createChildItem ("sxs");
    i1.createChildItem ("temp");
    i1.createChildItem ("System32");
    final DefaultTreeItem <String> i2 = aTree.getRootItem ().createChildItem ("Program Files");
    i2.createChildItem ("Eclipse");
    i2.createChildItem ("Apache Software Foundation");

    // Sort all items by String
    TreeSorter.sort (aTree, ISerializableComparator.getComparatorCollating (Locale.US));

    assertEquals (2, aTree.getRootItem ().getChildCount ());
    final List <? extends DefaultTreeItem <String>> aChildren = aTree.getRootItem ().getAllChildren ();
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
  }

  @Test
  public void testTreeNumeric ()
  {
    final DefaultTree <BigDecimal> aTree = new DefaultTree <BigDecimal> ();
    final DefaultTreeItem <BigDecimal> i1 = aTree.getRootItem ().createChildItem (BigDecimal.valueOf (2));
    i1.createChildItem (BigDecimal.valueOf (20));
    i1.createChildItem (BigDecimal.valueOf (22));
    i1.createChildItem (BigDecimal.valueOf (21));
    final DefaultTreeItem <BigDecimal> i2 = aTree.getRootItem ().createChildItem (BigDecimal.valueOf (1));
    i2.createChildItem (BigDecimal.valueOf (12));
    i2.createChildItem (BigDecimal.valueOf (11));

    // Sort all items by String
    TreeSorter.sort (aTree, Comparator.comparingInt (Number::intValue));

    assertEquals (2, aTree.getRootItem ().getChildCount ());
    final List <? extends DefaultTreeItem <BigDecimal>> aChildren = aTree.getRootItem ().getAllChildren ();
    assertSame (i2, aChildren.get (0));
    assertSame (i1, aChildren.get (1));
    // Test Apache (children must also be sorted)
    assertEquals (2, i2.getChildCount ());
    assertEquals (BigDecimal.valueOf (11), i2.getChildAtIndex (0).getData ());
    assertEquals (BigDecimal.valueOf (12), i2.getChildAtIndex (1).getData ());
    // Test Windows
    assertEquals (3, i1.getChildCount ());
    assertEquals (BigDecimal.valueOf (20), i1.getChildAtIndex (0).getData ());
    assertEquals (BigDecimal.valueOf (21), i1.getChildAtIndex (1).getData ());
    assertEquals (BigDecimal.valueOf (22), i1.getChildAtIndex (2).getData ());
  }
}
