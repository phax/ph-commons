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
package com.helger.graph;

import javax.annotation.Nonnull;

import com.helger.graph.simple.SimpleDirectedGraph;
import com.helger.graph.simple.SimpleGraph;

public abstract class AbstractGraphTestCase
{
  protected static final String ATTR_VALUE = "value";

  @Nonnull
  private static final IMutableDirectedGraphNode _createDGN (final SimpleDirectedGraph aGraph, final int i)
  {
    final IMutableDirectedGraphNode aNode = aGraph.createNode (Integer.toString (i));
    aNode.attrs ().putIn (ATTR_VALUE, Integer.valueOf (i + 1));
    return aNode;
  }

  @Nonnull
  private static final IMutableGraphNode _createGN (final SimpleGraph aGraph, final int i)
  {
    final IMutableGraphNode aNode = aGraph.createNode (Integer.toString (i));
    aNode.attrs ().putIn (ATTR_VALUE, Integer.valueOf (i + 1));
    return aNode;
  }

  protected static final int _getNodeValue (@Nonnull final IMutableBaseGraphNode <?, ?> aGN)
  {
    return aGN.attrs ().getAsInt (ATTR_VALUE);
  }

  @Nonnull
  protected SimpleDirectedGraph _buildDirectedGraph ()
  {
    final SimpleDirectedGraph aGraph = new SimpleDirectedGraph ();

    final IMutableDirectedGraphNode node0 = _createDGN (aGraph, 0);
    final IMutableDirectedGraphNode node1 = _createDGN (aGraph, 1);
    final IMutableDirectedGraphNode node2 = _createDGN (aGraph, 2);
    final IMutableDirectedGraphNode node3 = _createDGN (aGraph, 3);
    final IMutableDirectedGraphNode node4 = _createDGN (aGraph, 4);
    final IMutableDirectedGraphNode node5 = _createDGN (aGraph, 5);
    final IMutableDirectedGraphNode node6 = _createDGN (aGraph, 6);
    aGraph.createRelation (node0.getID (), node1.getID ());
    aGraph.createRelation (node1, node2);
    aGraph.createRelation (node2, node3);
    aGraph.createRelation (node3, node4);
    aGraph.createRelation (node0, node5);
    aGraph.createRelation (node5, node3);
    aGraph.createRelation (node5, node6);
    aGraph.createRelation (node6, node3);

    return aGraph;
  }

  @Nonnull
  protected SimpleGraph _buildGraph ()
  {
    final SimpleGraph aGraph = new SimpleGraph ();

    final IMutableGraphNode node0 = _createGN (aGraph, 0);
    final IMutableGraphNode node1 = _createGN (aGraph, 1);
    final IMutableGraphNode node2 = _createGN (aGraph, 2);
    final IMutableGraphNode node3 = _createGN (aGraph, 3);
    final IMutableGraphNode node4 = _createGN (aGraph, 4);
    final IMutableGraphNode node5 = _createGN (aGraph, 5);
    final IMutableGraphNode node6 = _createGN (aGraph, 6);
    aGraph.createRelation (node0.getID (), node1.getID ());
    aGraph.createRelation (node0, node5);
    aGraph.createRelation (node1, node2);
    aGraph.createRelation (node2, node3);
    aGraph.createRelation (node3, node4);
    aGraph.createRelation (node3, node5);
    aGraph.createRelation (node3, node6);
    aGraph.createRelation (node5, node6);

    return aGraph;
  }

  @Nonnull
  protected SimpleDirectedGraph _buildSimpleDirectedGraphCycle ()
  {
    final SimpleDirectedGraph aGraph = new SimpleDirectedGraph ();
    final IMutableDirectedGraphNode node0 = _createDGN (aGraph, 0);
    final IMutableDirectedGraphNode node1 = _createDGN (aGraph, 1);
    aGraph.createRelation (node0, node1);
    aGraph.createRelation (node1, node0);
    return aGraph;
  }

  @Nonnull
  protected SimpleDirectedGraph _buildSimpleDirectedGraphCycle2 ()
  {
    final SimpleDirectedGraph aGraph = new SimpleDirectedGraph ();
    final IMutableDirectedGraphNode node0 = _createDGN (aGraph, 0);
    final IMutableDirectedGraphNode node1 = _createDGN (aGraph, 1);
    final IMutableDirectedGraphNode node2 = _createDGN (aGraph, 2);
    final IMutableDirectedGraphNode node3 = _createDGN (aGraph, 3);
    aGraph.createRelation (node0, node1);
    aGraph.createRelation (node1, node2);
    aGraph.createRelation (node2, node3);
    aGraph.createRelation (node3, node0);
    return aGraph;
  }

  @Nonnull
  protected SimpleGraph _buildSimpleGraphCycle ()
  {
    final SimpleGraph aGraph = new SimpleGraph ();
    final IMutableGraphNode node0 = _createGN (aGraph, 0);
    final IMutableGraphNode node1 = _createGN (aGraph, 1);
    aGraph.createRelation (node0, node1);
    aGraph.createRelation (node1, node0);
    return aGraph;
  }

  @Nonnull
  protected SimpleGraph _buildSimpleGraphCycle2 ()
  {
    final SimpleGraph aGraph = new SimpleGraph ();
    final IMutableGraphNode node0 = _createGN (aGraph, 0);
    final IMutableGraphNode node1 = _createGN (aGraph, 1);
    final IMutableGraphNode node2 = _createGN (aGraph, 2);
    final IMutableGraphNode node3 = _createGN (aGraph, 3);
    aGraph.createRelation (node0, node1);
    aGraph.createRelation (node1, node2);
    aGraph.createRelation (node2, node3);
    aGraph.createRelation (node3, node0);
    return aGraph;
  }
}
