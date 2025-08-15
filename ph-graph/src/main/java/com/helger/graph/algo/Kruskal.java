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
package com.helger.graph.algo;

import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonempty;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.debug.GlobalDebug;
import com.helger.base.equals.ValueEnforcer;
import com.helger.base.string.StringImplode;
import com.helger.collection.commons.CommonsTreeSet;
import com.helger.collection.commons.ICommonsList;
import com.helger.graph.IMutableGraphNode;
import com.helger.graph.IMutableGraphRelation;
import com.helger.graph.simple.ISimpleGraph;
import com.helger.graph.simple.SimpleGraph;
import com.helger.graph.simple.SimpleGraphObjectFastFactory;

import jakarta.annotation.Nonnull;

/**
 * Find the minimum spanning tree of a graph, using Kruskal's algorithm.
 *
 * @author Philip Helger
 */
public final class Kruskal
{
  public static final class Result
  {
    private final SimpleGraph m_aGraph;
    private final int m_nTotalWeight;

    public Result (@Nonnull final SimpleGraph aGraph, final int nTotalWeight)
    {
      ValueEnforcer.notNull (aGraph, "Graph");
      m_aGraph = aGraph;
      m_nTotalWeight = nTotalWeight;
    }

    @Nonnull
    public SimpleGraph getGraph ()
    {
      return m_aGraph;
    }

    public int getTotalWeight ()
    {
      return m_nTotalWeight;
    }

    @Nonnull
    @Nonempty
    public String getAsString ()
    {
      final StringBuilder aSB = new StringBuilder ();
      aSB.append ("Total weight ").append (m_nTotalWeight).append (" for nodes {");
      int nIndex = 0;
      for (final IMutableGraphNode aNode : m_aGraph.getAllNodes ().values ())
      {
        if (nIndex > 0)
          aSB.append (',');
        aSB.append ('\'').append (aNode.getID ()).append ('\'');
        nIndex++;
      }
      return aSB.append ('}').toString ();
    }
  }

  private static final Logger LOGGER = LoggerFactory.getLogger (Kruskal.class);

  @PresentForCodeCoverage
  private static final Kruskal INSTANCE = new Kruskal ();

  private Kruskal ()
  {}

  private static String _getWeightInfo (@Nonnull final IMutableGraphRelation aRel,
                                        @Nonnull @Nonempty final String sRelationCostAttr)
  {
    return "{" +
           StringImplode.getImploded (',', new CommonsTreeSet <> (aRel.getAllConnectedNodeIDs ())) +
           ":" +
           aRel.attrs ().getAsInt (sRelationCostAttr) +
           "}";
  }

  @Nonnull
  public static Kruskal.Result applyKruskal (@Nonnull final ISimpleGraph aGraph,
                                             @Nonnull @Nonempty final String sRelationCostAttr)
  {
    final ICommonsList <IMutableGraphRelation> aSortedRelations = aGraph.getAllRelationObjs ()
                                                                        .getSortedInline (Comparator.comparingInt (x -> x.attrs ()
                                                                                                                         .getAsInt (sRelationCostAttr)));
    if (GlobalDebug.isDebugMode ())
    {
      LOGGER.info ("Starting Kruskal on " + aSortedRelations.size () + " relations");
      LOGGER.info ("Sorted relations: " +
                   StringImplode.getImplodedMapped (';', aSortedRelations, x -> _getWeightInfo (x, sRelationCostAttr)));
    }

    final SimpleGraph ret = new SimpleGraph (new SimpleGraphObjectFastFactory ());
    // Duplicate all nodes from source graph
    for (final IMutableGraphNode aNode : aGraph.getAllNodes ().values ())
    {
      final IMutableGraphNode aNewNode = ret.createNode (aNode.getID ());
      aNewNode.attrs ().putAllIn (aNode.attrs ());
    }

    // Now start adding the relations (undirected!)
    int nRemainingRelations = aGraph.getNodeCount () - 1;
    int nTotalWeight = 0;
    for (final IMutableGraphRelation aRelation : aSortedRelations)
    {
      final int nWeight = aRelation.attrs ().getAsInt (sRelationCostAttr);
      final IMutableGraphRelation aNewRelation = ret.createRelation (aRelation.getNode1ID (), aRelation.getNode2ID ());
      aNewRelation.attrs ().putAllIn (aRelation.attrs ());
      if (ret.containsCycles ())
      {
        if (GlobalDebug.isDebugMode ())
          LOGGER.info ("Ignoring " +
                       _getWeightInfo (aNewRelation, sRelationCostAttr) +
                       " because it introduces a cycle!");
        ret.removeRelation (aNewRelation);
      }
      else
      {
        if (GlobalDebug.isDebugMode ())
          LOGGER.info ("Added " + _getWeightInfo (aNewRelation, sRelationCostAttr) + "!");
        nTotalWeight += nWeight;
        nRemainingRelations--;
        if (nRemainingRelations == 0)
          break;
      }
    }

    if (GlobalDebug.isDebugMode ())
      LOGGER.info ("Having a total weight of " + nTotalWeight);

    return new Kruskal.Result (ret, nTotalWeight);
  }
}
