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
package com.helger.commons.tree.util;

import com.helger.commons.hierarchy.visit.DefaultHierarchyVisitorCallback;
import com.helger.commons.hierarchy.visit.EHierarchyVisitorReturn;
import com.helger.commons.mutable.MutableInt;
import com.helger.commons.tree.withid.DefaultTreeItemWithID;

public final class MockTreeVisitorCallback extends DefaultHierarchyVisitorCallback <DefaultTreeItemWithID <String, Object>>
{
  private final MutableInt m_aMI;

  public MockTreeVisitorCallback (final MutableInt mi)
  {
    m_aMI = mi;
  }

  @Override
  public EHierarchyVisitorReturn onItemBeforeChildren (final DefaultTreeItemWithID <String, Object> aItem)
  {
    if (getLevel () < 0)
      throw new IllegalStateException ();
    m_aMI.inc ();
    return EHierarchyVisitorReturn.CONTINUE;
  }
}
