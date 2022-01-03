/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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
package com.helger.graph.algo;

import java.util.function.ToIntFunction;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.CommonsLinkedHashSet;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.collection.impl.ICommonsOrderedSet;
import com.helger.commons.debug.GlobalDebug;
import com.helger.commons.lang.GenericReflection;
import com.helger.graph.IMutableBaseGraph;
import com.helger.graph.IMutableBaseGraphNode;
import com.helger.graph.IMutableBaseGraphRelation;
import com.helger.graph.IMutableDirectedGraphNode;
import com.helger.graph.IMutableDirectedGraphRelation;

/**
 * Find the shortest path between 2 graph nodes, using Dijsktra's algorithm
 *
 * @author Philip Helger
 */
public final class Dijkstra
{
  private static final Logger LOGGER = LoggerFactory.getLogger (Dijkstra.class);

  private static final class WorkElement <N extends IMutableBaseGraphNode <N, ?>>
  {
    private final N m_aFromNode;
    private final int m_nDistance;
    private final N m_aToNode;

    /**
     * Special constructor for the initial state
     *
     * @param nDistance
     *        Distance to use
     * @param aToNode
     *        to-node to use
     */
    public WorkElement (@Nonnegative final int nDistance, @Nonnull final N aToNode)
    {
      this (null, nDistance, aToNode);
    }

    public WorkElement (@Nullable final N aFromNode, @Nonnegative final int nDistance, @Nonnull final N aToNode)
    {
      ValueEnforcer.isGE0 (nDistance, "Distance");
      ValueEnforcer.notNull (aToNode, "ToNode");
      m_aFromNode = aFromNode;
      m_nDistance = nDistance;
      m_aToNode = aToNode;
    }

    @Nullable
    public N getFromNode ()
    {
      return m_aFromNode;
    }

    @Nullable
    public String getFromNodeID ()
    {
      return m_aFromNode == null ? null : m_aFromNode.getID ();
    }

    @Nonnegative
    public int getDistance ()
    {
      return m_nDistance;
    }

    @Nonnull
    public N getToNode ()
    {
      return m_aToNode;
    }

    @Nonnull
    public String getToNodeID ()
    {
      return m_aToNode.getID ();
    }

    @Nonnull
    @Nonempty
    public String getAsString ()
    {
      return "{" +
             (m_aFromNode == null ? "" : "'" + m_aFromNode.getID () + "',") +
             (m_nDistance == Integer.MAX_VALUE ? "Inf" : Integer.toString (m_nDistance)) +
             ",'" +
             m_aToNode.getID () +
             "'}";
    }
  }

  private static final class WorkRow <N extends IMutableBaseGraphNode <N, ?>>
  {
    private final ICommonsOrderedMap <String, WorkElement <N>> m_aElements;

    public WorkRow (@Nonnegative final int nElements)
    {
      ValueEnforcer.isGT0 (nElements, "Elements");
      m_aElements = new CommonsLinkedHashMap <> (nElements);
    }

    public void add (@Nonnull final WorkElement <N> aElement)
    {
      ValueEnforcer.notNull (aElement, "Element");

      m_aElements.put (aElement.getToNodeID (), aElement);
    }

    @Nullable
    public WorkElement <N> getElement (@Nullable final String sNodeID)
    {
      return m_aElements.get (sNodeID);
    }

    /**
     * @return The element with the smallest distance!
     */
    @Nonnull
    public WorkElement <N> getClosestElement ()
    {
      WorkElement <N> ret = null;
      for (final WorkElement <N> aElement : m_aElements.values ())
        if (ret == null || aElement.getDistance () < ret.getDistance ())
          ret = aElement;

      if (ret == null)
        throw new IllegalStateException ("Cannot call this method without an element!");
      return ret;
    }

    @Nonnull
    @ReturnsMutableCopy
    public ICommonsList <WorkElement <N>> getAllElements ()
    {
      return m_aElements.copyOfValues ();
    }
  }

  @Immutable
  public static final class Result <N extends IMutableBaseGraphNode <N, ?>>
  {
    private final ICommonsList <N> m_aResultNodes;
    private final int m_nResultDistance;

    public Result (@Nonnull @Nonempty final ICommonsList <N> aResultNodes, @Nonnegative final int nResultDistance)
    {
      ValueEnforcer.notEmpty (aResultNodes, "EesultNodes");
      ValueEnforcer.isGE0 (nResultDistance, "Result Distance");
      m_aResultNodes = aResultNodes;
      m_nResultDistance = nResultDistance;
    }

    @Nonnull
    @ReturnsMutableCopy
    public ICommonsList <N> getAllResultNodes ()
    {
      return m_aResultNodes.getClone ();
    }

    @Nonnegative
    public int getResultNodeCount ()
    {
      return m_aResultNodes.size ();
    }

    @Nonnegative
    public int getResultDistance ()
    {
      return m_nResultDistance;
    }

    @Nonnull
    @Nonempty
    public String getAsString ()
    {
      final StringBuilder aSB = new StringBuilder ();
      aSB.append ("Distance ").append (m_nResultDistance).append (" for route {");
      int nIndex = 0;
      for (final N aNode : m_aResultNodes)
      {
        if (nIndex > 0)
          aSB.append (',');
        aSB.append ('\'').append (aNode.getID ()).append ('\'');
        nIndex++;
      }
      return aSB.append ('}').toString ();
    }
  }

  private Dijkstra ()
  {}

  @Nullable
  private static <N extends IMutableBaseGraphNode <N, R>, R extends IMutableBaseGraphRelation <N, R>> R _getRelationFromLastMatch (@Nonnull final WorkElement <N> aLastMatch,
                                                                                                                                   @Nonnull final N aNode)
  {
    if (aNode.isDirected ())
    {
      // Directed

      // Cast to Object required for JDK command line compiler
      final Object aDirectedFromNode = aLastMatch.getToNode ();
      final Object aDirectedToNode = aNode;
      final IMutableDirectedGraphRelation r = ((IMutableDirectedGraphNode) aDirectedFromNode).getOutgoingRelationTo ((IMutableDirectedGraphNode) aDirectedToNode);
      return GenericReflection.uncheckedCast (r);
    }

    // Undirected
    return aLastMatch.getToNode ().getRelation (aNode);
  }

  @Nonnull
  public static <N extends IMutableBaseGraphNode <N, R>, R extends IMutableBaseGraphRelation <N, R>> Dijkstra.Result <N> applyDijkstra (@Nonnull final IMutableBaseGraph <N, R> aGraph,
                                                                                                                                        @Nonnull @Nonempty final String sFromID,
                                                                                                                                        @Nonnull @Nonempty final String sToID,
                                                                                                                                        @Nonnull final ToIntFunction <? super R> aRelationCostProvider)
  {
    final N aStartNode = aGraph.getNodeOfID (sFromID);
    if (aStartNode == null)
      throw new IllegalArgumentException ("Invalid From ID: " + sFromID);
    final N aEndNode = aGraph.getNodeOfID (sToID);
    if (aEndNode == null)
      throw new IllegalArgumentException ("Invalid To ID: " + sToID);

    // Ordered set for deterministic results
    final ICommonsOrderedSet <N> aAllRemainingNodes = new CommonsLinkedHashSet <> (aGraph.getAllNodes ().values ());

    if (GlobalDebug.isDebugMode ())
      if (LOGGER.isInfoEnabled ())
        LOGGER.info ("Starting Dijkstra on directed graph with " +
                     aAllRemainingNodes.size () +
                     " nodes starting from '" +
                     sFromID +
                     "' and up to '" +
                     sToID +
                     "'");

    // Map from to-node-id to element
    final ICommonsOrderedMap <String, WorkElement <N>> aAllMatches = new CommonsLinkedHashMap <> ();
    WorkElement <N> aLastMatch = null;
    WorkRow <N> aLastRow = null;
    int nIteration = 0;
    do
    {
      final WorkRow <N> aRow = new WorkRow <> (aAllRemainingNodes.size ());
      if (aLastRow == null)
      {
        // Initial row - no from node
        for (final N aNode : aAllRemainingNodes)
          if (aNode.equals (aStartNode))
          {
            // Start node has distance 0 to itself
            aRow.add (new WorkElement <> (0, aNode));
          }
          else
          {
            // All other elements have infinite distance to the start node (for
            // now)
            aRow.add (new WorkElement <> (Integer.MAX_VALUE, aNode));
          }
      }
      else
      {
        // All following rows
        for (final N aNode : aAllRemainingNodes)
        {
          // Find distance to last match
          final WorkElement <N> aPrevElement = aLastRow.getElement (aNode.getID ());

          // Get the relation from the last match to this node (may be null if
          // nodes are not connected)
          final R aRelation = Dijkstra.<N, R> _getRelationFromLastMatch (aLastMatch, aNode);
          if (aRelation != null)
          {
            // Nodes are related - check weight
            final int nNewDistance = aLastMatch.getDistance () + aRelationCostProvider.applyAsInt (aRelation);

            // Use only, if distance is shorter (=better) than before!
            if (nNewDistance < aPrevElement.getDistance ())
              aRow.add (new WorkElement <> (aLastMatch.getToNode (), nNewDistance, aNode));
            else
              aRow.add (aPrevElement);
          }
          else
          {
            // Nodes are not related - use result from previous row
            aRow.add (aPrevElement);
          }
        }
      }

      // Get the closest element of the current row
      final WorkElement <N> aClosest = aRow.getClosestElement ();

      if (GlobalDebug.isDebugMode ())
      {
        final StringBuilder aSB = new StringBuilder ("Iteration[").append (nIteration).append ("]: ");
        for (final WorkElement <N> e : aRow.getAllElements ())
          aSB.append (e.getAsString ());
        aSB.append (" ==> ").append (aClosest.getAsString ());
        if (LOGGER.isInfoEnabled ())
          LOGGER.info (aSB.toString ());
      }

      aAllRemainingNodes.remove (aClosest.getToNode ());
      aAllMatches.put (aClosest.getToNodeID (), aClosest);
      aLastMatch = aClosest;
      aLastRow = aRow;
      ++nIteration;

      if (aClosest.getToNode ().equals (aEndNode))
      {
        // We found the shortest way to the end node!
        break;
      }
    } while (true);

    // Now get the result path from back to front
    final int nResultDistance = aLastMatch.getDistance ();
    final ICommonsList <N> aResultNodes = new CommonsArrayList <> ();
    while (true)
    {
      aResultNodes.add (0, aLastMatch.getToNode ());
      // Are we at the start node?
      if (aLastMatch.getFromNode () == null)
        break;
      aLastMatch = aAllMatches.get (aLastMatch.getFromNodeID ());
      if (aLastMatch == null)
        throw new IllegalStateException ("Inconsistency!");
    }

    // Results
    return new Dijkstra.Result <> (aResultNodes, nResultDistance);
  }
}
