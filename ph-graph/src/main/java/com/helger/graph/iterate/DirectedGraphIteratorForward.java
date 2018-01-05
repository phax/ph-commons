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
package com.helger.graph.iterate;

import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.NonBlockingStack;
import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.collection.iterate.IIterableIterator;
import com.helger.commons.functional.IPredicate;
import com.helger.graph.IMutableDirectedGraphNode;
import com.helger.graph.IMutableDirectedGraphRelation;

/**
 * A simple forward iterator for directed graphs (following the outgoing nodes).
 *
 * @author Philip Helger
 */
@NotThreadSafe
public final class DirectedGraphIteratorForward implements IIterableIterator <IMutableDirectedGraphNode>
{
  /**
   * This class represents a node in the current iteration process. It is
   * relevant to easily keep the current iterator status and the node together.
   *
   * @author Philip Helger
   */
  private static final class IterationNode
  {
    private final IMutableDirectedGraphNode m_aNode;
    private final Iterator <IMutableDirectedGraphRelation> m_aOutgoingIt;

    private IterationNode (@Nonnull final IMutableDirectedGraphNode aNode)
    {
      m_aNode = ValueEnforcer.notNull (aNode, "Node");
      m_aOutgoingIt = aNode.getAllOutgoingRelations ().iterator ();
    }

    @Nonnull
    public IMutableDirectedGraphNode getNode ()
    {
      return m_aNode;
    }

    @Nonnull
    public Iterator <IMutableDirectedGraphRelation> getOutgoingRelationIterator ()
    {
      return m_aOutgoingIt;
    }
  }

  /**
   * Current stack. It contains the current node plus an iterator of the
   * outgoing relations of the node
   */
  private final NonBlockingStack <IterationNode> m_aNodeStack = new NonBlockingStack <> ();

  /**
   * Optional filter for graph relations to defined whether thy should be
   * followed or not. May be <code>null</code>.
   */
  private final IPredicate <? super IMutableDirectedGraphRelation> m_aRelationFilter;

  /**
   * This set keeps track of all the nodes we already visited. This is important
   * for cyclic dependencies.
   */
  private final ICommonsSet <String> m_aHandledNodes = new CommonsHashSet <> ();

  /**
   * Does the graph have cycles?
   */
  private boolean m_bHasCycles = false;

  public DirectedGraphIteratorForward (@Nonnull final IMutableDirectedGraphNode aStartNode)
  {
    this (aStartNode, null);
  }

  public DirectedGraphIteratorForward (@Nonnull final IMutableDirectedGraphNode aStartNode,
                                       @Nullable final IPredicate <? super IMutableDirectedGraphRelation> aRelationFilter)
  {
    ValueEnforcer.notNull (aStartNode, "StartNode");

    m_aRelationFilter = aRelationFilter;

    // Ensure that the start node is present
    m_aNodeStack.push (new IterationNode (aStartNode));
  }

  public boolean hasNext ()
  {
    return !m_aNodeStack.isEmpty ();
  }

  @Nullable
  public IMutableDirectedGraphNode next ()
  {
    // If no nodes are left, there ain't no next!
    if (!hasNext ())
      throw new NoSuchElementException ();

    // get the node to return
    final IMutableDirectedGraphNode ret = m_aNodeStack.peek ().getNode ();
    m_aHandledNodes.add (ret.getID ());

    // find next node
    {
      boolean bFoundNewNode = false;
      while (!m_aNodeStack.isEmpty () && !bFoundNewNode)
      {
        // check all outgoing relations
        final Iterator <IMutableDirectedGraphRelation> itPeek = m_aNodeStack.peek ().getOutgoingRelationIterator ();
        while (itPeek.hasNext ())
        {
          final IMutableDirectedGraphRelation aCurrentRelation = itPeek.next ();

          // Callback to check whether the current relation should be followed
          // or not
          if (m_aRelationFilter != null && !m_aRelationFilter.test (aCurrentRelation))
            continue;

          // to-node of the current relation
          final IMutableDirectedGraphNode aCurrentOutgoingNode = aCurrentRelation.getTo ();

          // check if the current node is already contained in the stack
          // If so, we have a cycle
          for (final IterationNode aStackElement : m_aNodeStack)
            if (aStackElement.getNode () == aCurrentOutgoingNode)
            {
              // we found a cycle!
              m_bHasCycles = true;
              break;
            }

          // Ensure that each node is returned only once!
          if (!m_aHandledNodes.contains (aCurrentOutgoingNode.getID ()))
          {
            // Okay, we have a new node
            m_aNodeStack.push (new IterationNode (aCurrentOutgoingNode));
            bFoundNewNode = true;
            break;
          }
        }

        // if we followed all relations of the current node, go to previous node
        if (!bFoundNewNode)
          m_aNodeStack.pop ();
      }
    }

    return ret;
  }

  /**
   * @return <code>true</code> if the iterator determined a cycle while
   *         iterating the graph
   */
  public boolean hasCycles ()
  {
    return m_bHasCycles;
  }
}
