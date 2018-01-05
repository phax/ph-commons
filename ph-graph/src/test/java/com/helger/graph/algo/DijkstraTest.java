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
package com.helger.graph.algo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.graph.IMutableDirectedGraphNode;
import com.helger.graph.IMutableGraphNode;
import com.helger.graph.simple.SimpleDirectedGraph;
import com.helger.graph.simple.SimpleDirectedGraphObjectFastFactory;
import com.helger.graph.simple.SimpleGraph;
import com.helger.graph.simple.SimpleGraphObjectFastFactory;

public final class DijkstraTest
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (DijkstraTest.class);
  private static final String ATTR_WEIGHT = "weight";

  @Test
  public void testBasic ()
  {
    final SimpleDirectedGraph g = new SimpleDirectedGraph (new SimpleDirectedGraphObjectFastFactory ());
    g.createNode ("O");
    g.createNode ("A");
    g.createNode ("B");
    g.createNode ("C");
    g.createNode ("D");
    g.createNode ("E");
    g.createNode ("T");
    g.createRelation ("O", "A").attrs ().putIn (ATTR_WEIGHT, 2);
    g.createRelation ("O", "B").attrs ().putIn (ATTR_WEIGHT, 5);
    g.createRelation ("O", "C").attrs ().putIn (ATTR_WEIGHT, 4);
    g.createRelation ("A", "D").attrs ().putIn (ATTR_WEIGHT, 7);
    g.createRelation ("A", "B").attrs ().putIn (ATTR_WEIGHT, 2);
    g.createRelation ("C", "B").attrs ().putIn (ATTR_WEIGHT, 1);
    g.createRelation ("C", "E").attrs ().putIn (ATTR_WEIGHT, 4);
    g.createRelation ("B", "D").attrs ().putIn (ATTR_WEIGHT, 4);
    g.createRelation ("B", "E").attrs ().putIn (ATTR_WEIGHT, 3);
    g.createRelation ("D", "E").attrs ().putIn (ATTR_WEIGHT, 1);
    g.createRelation ("D", "T").attrs ().putIn (ATTR_WEIGHT, 5);
    g.createRelation ("E", "T").attrs ().putIn (ATTR_WEIGHT, 7);
    assertEquals ("O", g.getSingleStartNode ().getID ());
    assertEquals ("T", g.getSingleEndNode ().getID ());

    final Dijkstra.Result <IMutableDirectedGraphNode> r = Dijkstra.applyDijkstra (g,
                                                                                  "O",
                                                                                  "T",
                                                                                  x -> x.attrs ()
                                                                                        .getAsInt (ATTR_WEIGHT,
                                                                                                   Integer.MIN_VALUE));
    assertNotNull (r);
    s_aLogger.info (r.getAsString ());
    assertEquals (13, r.getResultDistance ());
  }

  @Test
  public void example1aDirected ()
  {
    final SimpleDirectedGraph g = new SimpleDirectedGraph (new SimpleDirectedGraphObjectFastFactory ());
    for (int i = 1; i <= 6; ++i)
      g.createNode (Integer.toString (i));
    g.createRelation ("1", "2").attrs ().putIn (ATTR_WEIGHT, 3);
    g.createRelation ("1", "3").attrs ().putIn (ATTR_WEIGHT, 1);
    g.createRelation ("2", "5").attrs ().putIn (ATTR_WEIGHT, 1);
    g.createRelation ("3", "4").attrs ().putIn (ATTR_WEIGHT, 1);
    g.createRelation ("3", "6").attrs ().putIn (ATTR_WEIGHT, 4);
    g.createRelation ("4", "5").attrs ().putIn (ATTR_WEIGHT, 5);
    g.createRelation ("4", "6").attrs ().putIn (ATTR_WEIGHT, 5);
    g.createRelation ("5", "6").attrs ().putIn (ATTR_WEIGHT, 2);
    assertEquals ("1", g.getSingleStartNode ().getID ());
    assertEquals ("6", g.getSingleEndNode ().getID ());

    final Dijkstra.Result <IMutableDirectedGraphNode> r = Dijkstra.applyDijkstra (g,
                                                                                  "1",
                                                                                  "6",
                                                                                  x -> x.attrs ()
                                                                                        .getAsInt (ATTR_WEIGHT,
                                                                                                   Integer.MIN_VALUE));
    assertNotNull (r);
    s_aLogger.info (r.getAsString ());
    assertEquals (5, r.getResultDistance ());
  }

  @Test
  public void example1aUndirected ()
  {
    final SimpleGraph g = new SimpleGraph (new SimpleGraphObjectFastFactory ());
    for (int i = 1; i <= 6; ++i)
      g.createNode (Integer.toString (i));
    g.createRelation ("1", "2").attrs ().putIn (ATTR_WEIGHT, 3);
    g.createRelation ("1", "3").attrs ().putIn (ATTR_WEIGHT, 1);
    g.createRelation ("2", "5").attrs ().putIn (ATTR_WEIGHT, 1);
    g.createRelation ("3", "4").attrs ().putIn (ATTR_WEIGHT, 1);
    g.createRelation ("3", "6").attrs ().putIn (ATTR_WEIGHT, 4);
    g.createRelation ("4", "5").attrs ().putIn (ATTR_WEIGHT, 5);
    g.createRelation ("4", "6").attrs ().putIn (ATTR_WEIGHT, 5);
    g.createRelation ("5", "6").attrs ().putIn (ATTR_WEIGHT, 2);

    final Dijkstra.Result <IMutableGraphNode> r = Dijkstra.applyDijkstra (g,
                                                                          "1",
                                                                          "6",
                                                                          x -> x.attrs ().getAsInt (ATTR_WEIGHT,
                                                                                                    Integer.MIN_VALUE));
    assertNotNull (r);
    s_aLogger.info (r.getAsString ());
    assertEquals (5, r.getResultDistance ());
  }

  @Test
  public void example2a ()
  {
    final SimpleDirectedGraph g = new SimpleDirectedGraph (new SimpleDirectedGraphObjectFastFactory ());
    g.createNode ("O");
    g.createNode ("A");
    g.createNode ("B");
    g.createNode ("C");
    g.createNode ("D");
    g.createNode ("E");
    g.createNode ("F");
    g.createNode ("H");
    g.createNode ("I");
    g.createNode ("T");
    g.createRelation ("O", "A").attrs ().putIn (ATTR_WEIGHT, 12);
    g.createRelation ("O", "B").attrs ().putIn (ATTR_WEIGHT, 5);
    g.createRelation ("O", "C").attrs ().putIn (ATTR_WEIGHT, 8);
    g.createRelation ("C", "F").attrs ().putIn (ATTR_WEIGHT, 1);
    g.createRelation ("A", "B").attrs ().putIn (ATTR_WEIGHT, 4);
    g.createRelation ("A", "D").attrs ().putIn (ATTR_WEIGHT, 4);
    g.createRelation ("A", "E").attrs ().putIn (ATTR_WEIGHT, 6);
    g.createRelation ("A", "I").attrs ().putIn (ATTR_WEIGHT, 3);
    g.createRelation ("B", "F").attrs ().putIn (ATTR_WEIGHT, 5);
    g.createRelation ("B", "E").attrs ().putIn (ATTR_WEIGHT, 7);
    g.createRelation ("F", "E").attrs ().putIn (ATTR_WEIGHT, 4);
    g.createRelation ("E", "D").attrs ().putIn (ATTR_WEIGHT, 2);
    g.createRelation ("F", "I").attrs ().putIn (ATTR_WEIGHT, 7);
    g.createRelation ("E", "H").attrs ().putIn (ATTR_WEIGHT, 5);
    g.createRelation ("D", "T").attrs ().putIn (ATTR_WEIGHT, 10);
    g.createRelation ("H", "T").attrs ().putIn (ATTR_WEIGHT, 10);
    g.createRelation ("I", "H").attrs ().putIn (ATTR_WEIGHT, 11);
    assertEquals ("O", g.getSingleStartNode ().getID ());
    assertEquals ("T", g.getSingleEndNode ().getID ());

    final Dijkstra.Result <IMutableDirectedGraphNode> r = Dijkstra.applyDijkstra (g,
                                                                                  "O",
                                                                                  "T",
                                                                                  x -> x.attrs ()
                                                                                        .getAsInt (ATTR_WEIGHT,
                                                                                                   Integer.MIN_VALUE));
    assertNotNull (r);
    s_aLogger.info (r.getAsString ());
    assertEquals (24, r.getResultDistance ());
  }

  @Test
  public void testCities ()
  {
    final SimpleGraph g = new SimpleGraph (new SimpleGraphObjectFastFactory ());
    g.createNode ("Barcelona");
    g.createNode ("Narbonne");
    g.createNode ("Marseille");
    g.createNode ("Toulouse");
    g.createNode ("Geneve");
    g.createNode ("Paris");
    g.createNode ("Lausanne");
    g.createRelation ("Barcelona", "Narbonne").attrs ().putIn (ATTR_WEIGHT, 250);
    g.createRelation ("Narbonne", "Marseille").attrs ().putIn (ATTR_WEIGHT, 260);
    g.createRelation ("Narbonne", "Toulouse").attrs ().putIn (ATTR_WEIGHT, 150);
    g.createRelation ("Narbonne", "Geneve").attrs ().putIn (ATTR_WEIGHT, 550);
    g.createRelation ("Marseille", "Geneve").attrs ().putIn (ATTR_WEIGHT, 470);
    g.createRelation ("Toulouse", "Paris").attrs ().putIn (ATTR_WEIGHT, 680);
    g.createRelation ("Toulouse", "Geneve").attrs ().putIn (ATTR_WEIGHT, 700);
    g.createRelation ("Geneve", "Paris").attrs ().putIn (ATTR_WEIGHT, 540);
    g.createRelation ("Geneve", "Lausanne").attrs ().putIn (ATTR_WEIGHT, 64);
    g.createRelation ("Lausanne", "Paris").attrs ().putIn (ATTR_WEIGHT, 536);

    Dijkstra.Result <IMutableGraphNode> r = Dijkstra.applyDijkstra (g,
                                                                    "Barcelona",
                                                                    "Lausanne",
                                                                    x -> x.attrs ().getAsInt (ATTR_WEIGHT,
                                                                                              Integer.MIN_VALUE));
    assertNotNull (r);
    s_aLogger.info (r.getAsString ());
    assertEquals (864, r.getResultDistance ());

    r = Dijkstra.applyDijkstra (g, "Lausanne", "Barcelona", x -> x.attrs ().getAsInt (ATTR_WEIGHT, Integer.MIN_VALUE));
    assertNotNull (r);
    s_aLogger.info (r.getAsString ());
    assertEquals (864, r.getResultDistance ());
  }
}
