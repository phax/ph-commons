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
package com.helger.graph.iterate;

import java.util.Iterator;

import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.equals.ValueEnforcer;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsHashSet;
import com.helger.collection.commons.ICommonsIterableIterator;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsSet;
import com.helger.graph.IMutableGraphNode;
import com.helger.graph.IMutableGraphRelation;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * A simple iterator for undirected graphs.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public final class GraphIterator implements ICommonsIterableIterator <IMutableGraphNode>
{
  /**
   * Maps node IDs to node states
   */
  private final ICommonsSet <String> m_aHandledObjects = new CommonsHashSet <> ();

  private final Iterator <IMutableGraphNode> m_aIter;

  /**
   * Does the graph have cycles?
   */
  private boolean m_bHasCycles = false;

  public GraphIterator (@Nonnull final IMutableGraphNode aStartNode)
  {
    ValueEnforcer.notNull (aStartNode, "startNode");

    // Collect all nodes, depth first
    final ICommonsList <IMutableGraphNode> aList = new CommonsArrayList <> ();
    _traverseDFS (aStartNode, aList);
    m_aIter = aList.iterator ();
  }

  private void _traverseDFS (@Nonnull final IMutableGraphNode aStartNode,
                             @Nonnull final ICommonsList <IMutableGraphNode> aList)
  {
    m_aHandledObjects.add (aStartNode.getID ());
    aList.add (aStartNode);
    for (final IMutableGraphRelation aRelation : aStartNode.getAllRelations ())
    {
      final boolean bNewRelation = m_aHandledObjects.add (aRelation.getID ());
      for (final IMutableGraphNode aNode : aRelation.getAllConnectedNodes ())
        if (EqualsHelper.identityDifferent (aNode, aStartNode))
        {
          if (!m_aHandledObjects.contains (aNode.getID ()))
            _traverseDFS (aNode, aList);
          else
          {
            // If an unexplored edge leads to a node visited before, then the
            // graph contains a cycle.
            if (bNewRelation)
              m_bHasCycles = true;
          }
        }
    }
  }

  public boolean hasNext ()
  {
    return m_aIter.hasNext ();
  }

  @Nullable
  public IMutableGraphNode next ()
  {
    return m_aIter.next ();
  }

  /**
   * @return <code>true</code> if the iterator determined a cycle while iterating the graph
   */
  public boolean hasCycles ()
  {
    return m_bHasCycles;
  }
}
