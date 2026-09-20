/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.hierarchy.visit.DefaultHierarchyVisitorCallback;
import com.helger.collection.hierarchy.visit.EHierarchyVisitorReturn;
import com.helger.tree.DefaultTree;
import com.helger.tree.DefaultTreeItem;

/**
 * Test class for class {@link TreeVisitor.HierarchyVisitorCallbackWithConversion}.
 *
 * @author Philip Helger
 */
public final class HierarchyVisitorCallbackWithConversionTest
{
  /**
   * Records all callback invocations in invocation order.
   *
   * @author Philip Helger
   */
  private static final class MockDataCallback extends DefaultHierarchyVisitorCallback <String>
  {
    private final ICommonsList <String> m_aEvents = new CommonsArrayList <> ();

    @Override
    public void begin ()
    {
      super.begin ();
      m_aEvents.add ("begin");
    }

    @Override
    public void onLevelDown ()
    {
      super.onLevelDown ();
      m_aEvents.add ("down");
    }

    @Override
    public void onLevelUp ()
    {
      super.onLevelUp ();
      m_aEvents.add ("up");
    }

    @Override
    public EHierarchyVisitorReturn onItemBeforeChildren (final String sItem)
    {
      m_aEvents.add ("before:" + sItem);
      return EHierarchyVisitorReturn.CONTINUE;
    }

    @Override
    public EHierarchyVisitorReturn onItemAfterChildren (final String sItem)
    {
      m_aEvents.add ("after:" + sItem);
      return EHierarchyVisitorReturn.CONTINUE;
    }

    @Override
    public void end ()
    {
      m_aEvents.add ("end");
      super.end ();
    }
  }

  @Test
  public void testVisitTreeData ()
  {
    final DefaultTree <String> aTree = new DefaultTree <> ();
    final DefaultTreeItem <String> aChild = aTree.getRootItem ().createChildItem ("child");
    aChild.createChildItem ("grandchild");

    final MockDataCallback aDataCB = new MockDataCallback ();
    TreeVisitor.visitTreeData (aTree, aDataCB);

    // The conversion callback delegates every event to the data callback
    assertEquals ("begin", aDataCB.m_aEvents.getFirstOrNull ());
    assertEquals ("end", aDataCB.m_aEvents.getLastOrNull ());
    assertTrue (aDataCB.m_aEvents.contains ("before:child"));
    assertTrue (aDataCB.m_aEvents.contains ("after:child"));
    assertTrue (aDataCB.m_aEvents.contains ("before:grandchild"));
    assertTrue (aDataCB.m_aEvents.contains ("down"));
    assertTrue (aDataCB.m_aEvents.contains ("up"));
  }

  @Test
  public void testInvalidParams ()
  {
    try
    {
      new TreeVisitor.HierarchyVisitorCallbackWithConversion <DefaultTreeItem <String>, String> (null,
                                                                                                  DefaultTreeItem::getData);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      new TreeVisitor.HierarchyVisitorCallbackWithConversion <DefaultTreeItem <String>, String> (new MockDataCallback (),
                                                                                                  null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }
}
