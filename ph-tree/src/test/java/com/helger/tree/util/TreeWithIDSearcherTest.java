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

import java.util.List;

import org.junit.Test;

import com.helger.commons.collection.CollectionHelper;
import com.helger.tree.mock.MockHasParent;
import com.helger.tree.withid.DefaultTreeItemWithID;
import com.helger.tree.withid.DefaultTreeWithID;

/**
 * Test class for class {@link TreeWithIDSearcher}.
 *
 * @author Philip Helger
 */
public final class TreeWithIDSearcherTest
{
  @Test
  public void testSearch ()
  {
    final DefaultTreeWithID <String, MockHasParent> aTree = TreeWithIDBuilder.buildTree (CollectionHelper.newList (new MockHasParent ("a"),
                                                                                                                   new MockHasParent ("ab"),
                                                                                                                   new MockHasParent ("abc"),
                                                                                                                   new MockHasParent ("abd")));
    List <DefaultTreeItemWithID <String, MockHasParent>> aList = TreeWithIDSearcher.findAllItemsWithIDRecursive (aTree, "abc");
    assertNotNull (aList);
    assertEquals (1, aList.size ());
    assertEquals ("abc", aList.get (0).getID ());

    aList = TreeWithIDSearcher.findAllItemsWithIDRecursive (aTree, "gibtsned");
    assertNotNull (aList);
    assertEquals (0, aList.size ());
  }
}
