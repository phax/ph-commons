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
package com.helger.graph.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.graph.IMutableGraphRelation;

/**
 * Test class for the relation handling of {@link GraphNode}.
 *
 * @author Philip Helger
 */
public final class GraphNodeRelationsTest
{
  @Test
  public void testRelations ()
  {
    final GraphNode aNode1 = new GraphNode ("node1");
    final GraphNode aNode2 = new GraphNode ("node2");
    final IMutableGraphRelation aRel = new GraphRelation (aNode1, aNode2);

    assertFalse (aNode1.hasRelations ());
    assertEquals (0, aNode1.getRelationCount ());
    assertTrue (aNode1.getAllRelations ().isEmpty ());
    assertTrue (aNode1.getAllRelationIDs ().isEmpty ());
    assertTrue (aNode1.getAllRelatedNodes ().isEmpty ());
    assertTrue (aNode1.getAllRelatedNodeIDs ().isEmpty ());
    assertFalse (aNode1.isConnectedWith (aNode2));
    assertNull (aNode1.getRelation (aNode2));
    assertNull (aNode1.getRelation (null));

    // A null relation is not added
    assertTrue (aNode1.addRelation (null).isUnchanged ());

    assertTrue (aNode1.addRelation (aRel).isChanged ());
    assertTrue (aNode2.addRelation (aRel).isChanged ());
    // Adding the same relation twice does not change anything
    assertTrue (aNode1.addRelation (aRel).isUnchanged ());

    assertTrue (aNode1.hasRelations ());
    assertEquals (1, aNode1.getRelationCount ());
    assertEquals (1, aNode1.getAllRelations ().size ());
    assertTrue (aNode1.getAllRelationIDs ().contains (aRel.getID ()));
    assertTrue (aNode1.getAllRelatedNodes ().contains (aNode2));
    assertTrue (aNode1.getAllRelatedNodeIDs ().contains ("node2"));
    assertTrue (aNode1.isConnectedWith (aNode2));
    assertSame (aRel, aNode1.getRelation (aNode2));

    final ICommonsList <IMutableGraphRelation> aVisited = new CommonsArrayList <> ();
    aNode1.forEachRelation (aVisited::add);
    assertEquals (new CommonsArrayList <> (aRel), aVisited);

    assertTrue (aNode1.removeRelation (null).isUnchanged ());
    assertTrue (aNode1.removeRelation (aRel).isChanged ());
    assertTrue (aNode1.removeRelation (aRel).isUnchanged ());
    assertFalse (aNode1.hasRelations ());

    aNode1.addRelation (aRel);
    assertTrue (aNode1.removeAllRelations ().isChanged ());
    assertTrue (aNode1.removeAllRelations ().isUnchanged ());
  }

  @Test
  public void testDefaultCtorAndBasics ()
  {
    final GraphNode aNode = new GraphNode ();
    assertNotNull (aNode.getID ());
    assertFalse (aNode.isDirected ());
    assertNotNull (aNode.attrs ());
    assertNotNull (aNode.toString ());
    assertEquals (aNode, aNode);
    assertEquals (aNode.hashCode (), aNode.hashCode ());
    assertFalse (aNode.equals (null));
    assertFalse (aNode.equals ("any other type"));
  }
}
