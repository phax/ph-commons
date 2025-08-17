/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.tree.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Collection;

import org.junit.Test;

import com.helger.collection.helper.CollectionHelperExt;
import com.helger.collection.hierarchy.IParentProvider;
import com.helger.tree.mock.MockChildrenProvider;
import com.helger.tree.mock.MockHasChildren;
import com.helger.tree.mock.MockHasIDString;
import com.helger.tree.mock.MockHasParent;
import com.helger.tree.withid.DefaultTreeWithID;

/**
 * Test class for class {@link TreeWithIDBuilder}.
 *
 * @author Philip Helger
 */
public final class TreeWithIDBuilderTest
{
  @Test
  public void testBuildFromParent ()
  {
    final DefaultTreeWithID <String, MockHasParent> aTree = TreeWithIDBuilder.buildTree (CollectionHelperExt.newList (new MockHasParent ("a"),
                                                                                                                   new MockHasParent ("ab"),
                                                                                                                   new MockHasParent ("abc"),
                                                                                                                   new MockHasParent ("abd")));
    assertNotNull (aTree.getRootItem ());
    assertNull (aTree.getRootItem ().getID ());
    assertEquals (1, aTree.getRootItem ().getChildCount ());
    assertEquals ("a", aTree.getRootItem ().getChildAtIndex (0).getID ());
    assertEquals ("ab", aTree.getRootItem ().getChildAtIndex (0).getChildAtIndex (0).getID ());
    assertEquals ("abc", aTree.getRootItem ().getChildAtIndex (0).getChildAtIndex (0).getChildAtIndex (0).getID ());
    assertEquals ("abd", aTree.getRootItem ().getChildAtIndex (0).getChildAtIndex (0).getChildAtIndex (1).getID ());

    assertNotNull (TreeWithIDBuilder.buildTree (CollectionHelperExt.newList (new MockHasParent ("abc"),
                                                                          new MockHasParent ("abd"),
                                                                          new MockHasParent ("a"),
                                                                          new MockHasParent ("ab"))));

    try
    {
      // parent "ab" and "a" missing
      TreeWithIDBuilder.buildTree (CollectionHelperExt.newList (new MockHasParent ("abc")));
      fail ();
    }
    catch (final IllegalStateException ex)
    {}
    try
    {
      TreeWithIDBuilder.buildTree ((Collection <MockHasParent>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      TreeWithIDBuilder.buildTree (CollectionHelperExt.newList (new MockHasParent ("abc")), null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      TreeWithIDBuilder.buildTree ((Collection <MockHasParent>) null, IParentProvider.parentProviderHasParent ());
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testBuildFromChildren ()
  {
    final DefaultTreeWithID <String, MockHasChildren> aTree = TreeWithIDBuilder.buildTree (new MockChildrenProvider (new MockHasChildren ("",
                                                                                                                                          new MockHasChildren ("a",
                                                                                                                                                               new MockHasChildren ("b",
                                                                                                                                                                                    new MockHasChildren ("c"),
                                                                                                                                                                                    new MockHasChildren ("d"))))));
    assertNotNull (aTree.getRootItem ());
    assertNull (aTree.getRootItem ().getID ());
    assertEquals (1, aTree.getRootItem ().getChildCount ());
    assertEquals ("a", aTree.getRootItem ().getChildAtIndex (0).getID ());
    assertEquals ("b", aTree.getRootItem ().getChildAtIndex (0).getChildAtIndex (0).getID ());
    assertEquals ("c", aTree.getRootItem ().getChildAtIndex (0).getChildAtIndex (0).getChildAtIndex (0).getID ());
    assertEquals ("d", aTree.getRootItem ().getChildAtIndex (0).getChildAtIndex (0).getChildAtIndex (1).getID ());

    try
    {
      TreeWithIDBuilder.buildTree ((MockChildrenProvider) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testBuildTreeFromIHasID ()
  {
    final MockHasIDString [] x = new MockHasIDString [0];
    final IParentProvider <MockHasIDString> pp = o -> null;
    final DefaultTreeWithID <String, MockHasIDString> aTree = TreeWithIDBuilder.buildTree (x, pp);
    assertNotNull (aTree);

    try
    {
      TreeWithIDBuilder.buildTree ((MockHasIDString []) null, pp);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      TreeWithIDBuilder.buildTree (x, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
