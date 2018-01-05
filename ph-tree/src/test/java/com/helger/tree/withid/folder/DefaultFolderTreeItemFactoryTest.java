/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import com.helger.commons.aggregate.IAggregator;
import com.helger.commons.mock.CommonsTestHelper;
import com.helger.commons.string.StringHelper;

/**
 * Test class for class {@link DefaultFolderTreeItemFactory}.
 *
 * @author Philip Helger
 */
public final class DefaultFolderTreeItemFactoryTest
{
  @Test
  public void testBasic ()
  {
    final DefaultFolderTreeItemFactory <String, String, List <String>> ftif = new DefaultFolderTreeItemFactory <> (x -> StringHelper.getImploded ('/',
                                                                                                                                                  x));
    assertNotNull (ftif.createRoot ());
    try
    {
      ftif.createRoot ();
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testEquals ()
  {
    final IAggregator <String, String> aAggregator = x -> StringHelper.getImploded ('/', x);
    final DefaultFolderTreeItemFactory <String, String, List <String>> ftif = new DefaultFolderTreeItemFactory <> (aAggregator);
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (ftif,
                                                                       new DefaultFolderTreeItemFactory <> (aAggregator));
    // New aggregator - different object!
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (ftif,
                                                                           new DefaultFolderTreeItemFactory <> (x -> StringHelper.getImploded ('/',
                                                                                                                                               x)));
  }
}
