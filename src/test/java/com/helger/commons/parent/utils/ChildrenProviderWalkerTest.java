/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.parent.utils;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.commons.hierarchy.DefaultHierarchyWalkerCallback;
import com.helger.commons.parent.IChildrenProvider;
import com.helger.commons.parent.MockChildrenProvider;
import com.helger.commons.parent.MockHasChildren;

/**
 * Test class for class {@link ChildrenProviderWalker}.
 * 
 * @author Philip Helger
 */
public final class ChildrenProviderWalkerTest
{
  @Test
  public void testAll ()
  {
    final MockHasChildren hca = new MockHasChildren ("a");
    final MockHasChildren hcb = new MockHasChildren ("b");
    final MockHasChildren hc1 = new MockHasChildren ("1", hca, hcb);
    final IChildrenProvider <MockHasChildren> cp = new MockChildrenProvider (hc1);

    // Having children
    ChildrenProviderWalker.walkProvider (cp, new DefaultHierarchyWalkerCallback <MockHasChildren> ());

    // Not having children
    ChildrenProviderWalker.walkProvider (new MockChildrenProvider (hca),
                                         new DefaultHierarchyWalkerCallback <MockHasChildren> ());

    // Start explicitly at object
    ChildrenProviderWalker.walkSubProvider (hc1, cp, new DefaultHierarchyWalkerCallback <MockHasChildren> ());

    // no provider
    try
    {
      ChildrenProviderWalker.walkProvider (null, new DefaultHierarchyWalkerCallback <MockHasChildren> ());
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    // no callback
    try
    {
      ChildrenProviderWalker.walkProvider (cp, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    // Nothing to walk
    try
    {
      ChildrenProviderWalker.walkSubProvider (null, cp, new DefaultHierarchyWalkerCallback <MockHasChildren> ());
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
