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
package com.helger.commons.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.graph.IMutableDirectedGraphNode;
import com.helger.graph.IMutableGraphNode;
import com.helger.graph.simple.SimpleDirectedGraph;
import com.helger.graph.simple.SimpleGraph;

/**
 * Test class for class {@link GraphVizHelper}.
 *
 * @author Philip Helger
 */
public final class GraphVizHelperTest
{
  @Test
  public void testGetAttribute ()
  {
    assertEquals ("label=<abc>", GraphVizHelper.getAttribute ("label", "abc"));
    // The value is masked as an XML element name, so "&" is kept as is
    assertEquals ("label=<a&b>", GraphVizHelper.getAttribute ("label", "a&b"));
  }

  @Test
  public void testUndirectedGraph ()
  {
    final SimpleGraph aGraph = new SimpleGraph ();
    final IMutableGraphNode n1 = aGraph.createNode ("n1");
    final IMutableGraphNode n2 = aGraph.createNode ("n2");
    n1.attrs ().putIn ("text", "Node 1");
    n2.attrs ().putIn ("text", "Node 2");
    aGraph.createRelation ("r1", n1, n2).attrs ().putIn ("text", "Relation 1");

    // Without labels
    final String sDot = GraphVizHelper.getAsGraphVizDot (aGraph, null, null);
    assertTrue (sDot, sDot.startsWith ("graph " + aGraph.getID () + "{"));
    assertTrue (sDot, sDot.contains ("n1;"));
    assertTrue (sDot, sDot.contains ("n1--n2;"));
    assertTrue (sDot, sDot.endsWith ("}"));

    // With labels
    final String sDot2 = GraphVizHelper.getAsGraphVizDot (aGraph, "text", "text");
    assertTrue (sDot2, sDot2.contains ("n1[label=<Node 1>];"));
    assertTrue (sDot2, sDot2.contains ("n1--n2[label=<Relation 1>];"));
  }

  @Test
  public void testDirectedGraph ()
  {
    final SimpleDirectedGraph aGraph = new SimpleDirectedGraph ();
    final IMutableDirectedGraphNode n1 = aGraph.createNode ("n1");
    final IMutableDirectedGraphNode n2 = aGraph.createNode ("n2");
    n1.attrs ().putIn ("text", "Node 1");
    n2.attrs ().putIn ("text", "Node 2");
    aGraph.createRelation ("r1", n1, n2).attrs ().putIn ("text", "Relation 1");

    // Without labels
    final String sDot = GraphVizHelper.getAsGraphVizDot (aGraph, null, null);
    assertTrue (sDot, sDot.startsWith ("digraph " + aGraph.getID () + "{"));
    assertTrue (sDot, sDot.contains ("n1;"));
    assertTrue (sDot, sDot.contains ("n1->n2;"));
    assertTrue (sDot, sDot.endsWith ("}"));

    // With labels
    final String sDot2 = GraphVizHelper.getAsGraphVizDot (aGraph, "text", "text");
    assertTrue (sDot2, sDot2.contains ("n1[label=<Node 1>];"));
    assertTrue (sDot2, sDot2.contains ("n1->n2[label=<Relation 1>];"));
  }
}
