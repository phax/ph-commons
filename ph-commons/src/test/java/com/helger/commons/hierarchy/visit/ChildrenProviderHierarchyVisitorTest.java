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
package com.helger.commons.hierarchy.visit;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.commons.hierarchy.IChildrenProvider;
import com.helger.commons.hierarchy.MockChildrenProvider;
import com.helger.commons.hierarchy.MockHasChildren;

/**
 * Test class for class {@link ChildrenProviderHierarchyVisitor}.
 *
 * @author Philip Helger
 */
public final class ChildrenProviderHierarchyVisitorTest
{
  @Test
  public void testAll ()
  {
    final MockHasChildren hca = new MockHasChildren ("a");
    final MockHasChildren hcb = new MockHasChildren ("b");
    final MockHasChildren hc1 = new MockHasChildren ("1", hca, hcb);
    final IChildrenProvider <MockHasChildren> cp = new MockChildrenProvider (hc1);

    // Having children
    ChildrenProviderHierarchyVisitor.visitAll (cp, new DefaultHierarchyVisitorCallback <MockHasChildren> (), false);

    // Not having children
    ChildrenProviderHierarchyVisitor.visitAll (new MockChildrenProvider (hca),
                                               new DefaultHierarchyVisitorCallback <MockHasChildren> (),
                                               false);

    // Start explicitly at object
    ChildrenProviderHierarchyVisitor.visitFrom (hc1,
                                                cp,
                                                new DefaultHierarchyVisitorCallback <MockHasChildren> (),
                                                true);
    // no provider
    try
    {
      ChildrenProviderHierarchyVisitor.visitAll (null, new DefaultHierarchyVisitorCallback <MockHasChildren> (), true);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    // no callback
    try
    {
      ChildrenProviderHierarchyVisitor.visitAll (cp, null, false);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    // no callback
    try
    {
      ChildrenProviderHierarchyVisitor.visitFrom (null, cp, null, true);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
