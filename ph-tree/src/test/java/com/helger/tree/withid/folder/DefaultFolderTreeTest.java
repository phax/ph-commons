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
package com.helger.tree.withid.folder;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.helger.base.aggregate.IAggregator;
import com.helger.base.string.StringImplode;
import com.helger.collection.commons.ICommonsSet;
import com.helger.collection.helper.PrimitiveCollectionHelper;
import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link DefaultFolderTree}.
 *
 * @author Philip Helger
 */
public final class DefaultFolderTreeTest
{
  @Test
  public void testBasic ()
  {
    final IAggregator <String, String> aCombinator = x -> StringImplode.getImploded ('/', x);
    final DefaultFolderTree <String, Integer, ICommonsSet <Integer>> ft = DefaultFolderTree.createForSet (aCombinator);
    assertNotNull (ft.getRootItem ());

    final DefaultFolderTreeItem <String, Integer, ICommonsSet <Integer>> i1 = ft.getRootItem ()
                                                                                .createChildItem ("id1",
                                                                                                  PrimitiveCollectionHelper.newPrimitiveSet (1,
                                                                                                                                             2,
                                                                                                                                             3));
    assertNotNull (i1);

    final DefaultFolderTreeItem <String, Integer, ICommonsSet <Integer>> i2 = ft.getRootItem ()
                                                                                .createChildItem ("id1",
                                                                                                  PrimitiveCollectionHelper.newPrimitiveSet (1,
                                                                                                                                             2,
                                                                                                                                             3));
    assertNotNull (i2);
    assertSame (i1, i2);

    final DefaultFolderTreeItem <String, Integer, ICommonsSet <Integer>> i3 = ft.getRootItem ()
                                                                                .createChildItem ("id3",
                                                                                                  PrimitiveCollectionHelper.newPrimitiveSet (1,
                                                                                                                                             3));
    assertNotNull (i3);
    assertNotSame (i1, i3);

    TestHelper.testDefaultImplementationWithDifferentContentObject (i1, i3);
  }
}
