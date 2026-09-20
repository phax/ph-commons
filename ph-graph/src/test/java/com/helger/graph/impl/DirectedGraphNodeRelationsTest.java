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
import com.helger.graph.IMutableDirectedGraphRelation;

/**
 * Test class for the relation handling of {@link DirectedGraphNode}.
 *
 * @author Philip Helger
 */
public final class DirectedGraphNodeRelationsTest
{
  @Test
  public void testIncomingRelations ()
  {
    final DirectedGraphNode aFrom = new DirectedGraphNode ("from");
    final DirectedGraphNode aTo = new DirectedGraphNode ("to");
    final IMutableDirectedGraphRelation aRel = new DirectedGraphRelation (aFrom, aTo);

    assertFalse (aTo.hasIncomingRelations ());
    assertEquals (0, aTo.getIncomingRelationCount ());
    assertFalse (aTo.isIncomingRelation (aRel));
    assertFalse (aTo.isIncomingRelation (null));
    assertTrue (aTo.getAllIncomingRelations ().isEmpty ());
    assertTrue (aTo.getAllFromNodes ().isEmpty ());
    assertFalse (aTo.isFromNode (aFrom));
    assertNull (aTo.getIncomingRelationFrom (aFrom));

    aTo.addIncomingRelation (aRel);
    assertTrue (aTo.hasIncomingRelations ());
    assertEquals (1, aTo.getIncomingRelationCount ());
    assertTrue (aTo.isIncomingRelation (aRel));
    assertEquals (new CommonsArrayList <> (aRel), aTo.getAllIncomingRelations ());
    assertTrue (aTo.isFromNode (aFrom));
    assertTrue (aTo.getAllFromNodes ().contains (aFrom));
    assertSame (aRel, aTo.getIncomingRelationFrom (aFrom));

    final ICommonsList <IMutableDirectedGraphRelation> aVisited = new CommonsArrayList <> ();
    aTo.forEachIncomingRelation (aVisited::add);
    assertEquals (new CommonsArrayList <> (aRel), aVisited);

    assertTrue (aTo.removeIncomingRelation (aRel).isChanged ());
    assertTrue (aTo.removeIncomingRelation (aRel).isUnchanged ());
    assertFalse (aTo.hasIncomingRelations ());

    aTo.addIncomingRelation (aRel);
    assertTrue (aTo.removeAllIncomingRelations ().isChanged ());
    assertTrue (aTo.removeAllIncomingRelations ().isUnchanged ());
  }

  @Test
  public void testOutgoingRelations ()
  {
    final DirectedGraphNode aFrom = new DirectedGraphNode ("from");
    final DirectedGraphNode aTo = new DirectedGraphNode ("to");
    final IMutableDirectedGraphRelation aRel = new DirectedGraphRelation (aFrom, aTo);

    assertFalse (aFrom.hasOutgoingRelations ());
    assertEquals (0, aFrom.getOutgoingRelationCount ());
    assertFalse (aFrom.isOutgoingRelation (aRel));
    assertFalse (aFrom.isOutgoingRelation (null));
    assertTrue (aFrom.getAllOutgoingRelations ().isEmpty ());
    assertTrue (aFrom.getAllToNodes ().isEmpty ());
    assertFalse (aFrom.isToNode (aTo));
    assertNull (aFrom.getOutgoingRelationTo (aTo));

    aFrom.addOutgoingRelation (aRel);
    assertTrue (aFrom.hasOutgoingRelations ());
    assertEquals (1, aFrom.getOutgoingRelationCount ());
    assertTrue (aFrom.isOutgoingRelation (aRel));
    assertEquals (new CommonsArrayList <> (aRel), aFrom.getAllOutgoingRelations ());
    assertTrue (aFrom.isToNode (aTo));
    assertTrue (aFrom.getAllToNodes ().contains (aTo));
    assertSame (aRel, aFrom.getOutgoingRelationTo (aTo));

    final ICommonsList <IMutableDirectedGraphRelation> aVisited = new CommonsArrayList <> ();
    aFrom.forEachOutgoingRelation (aVisited::add);
    assertEquals (new CommonsArrayList <> (aRel), aVisited);

    assertTrue (aFrom.removeOutgoingRelation (aRel).isChanged ());
    assertTrue (aFrom.removeOutgoingRelation (aRel).isUnchanged ());
    assertFalse (aFrom.hasOutgoingRelations ());

    aFrom.addOutgoingRelation (aRel);
    assertTrue (aFrom.removeAllOutgoingRelations ().isChanged ());
    assertTrue (aFrom.removeAllOutgoingRelations ().isUnchanged ());
  }

  @Test
  public void testCombinedRelations ()
  {
    final DirectedGraphNode aFrom = new DirectedGraphNode ("from");
    final DirectedGraphNode aTo = new DirectedGraphNode ("to");
    final IMutableDirectedGraphRelation aRel = new DirectedGraphRelation (aFrom, aTo);
    aFrom.addOutgoingRelation (aRel);
    aTo.addIncomingRelation (aRel);

    assertTrue (aFrom.isConnectedWith (aTo));
    assertFalse (aFrom.isConnectedWith (null));
    assertSame (aRel, aFrom.getRelation (aTo));
    assertNull (aFrom.getRelation (null));

    assertEquals (1, aFrom.getAllRelations ().size ());
    assertEquals (1, aFrom.getAllRelationIDs ().size ());
    assertTrue (aFrom.getAllRelationIDs ().contains (aRel.getID ()));
    assertEquals (1, aFrom.getAllRelatedNodes ().size ());
    assertTrue (aFrom.getAllRelatedNodes ().contains (aTo));
    assertTrue (aFrom.getAllRelatedNodeIDs ().contains ("to"));

    assertTrue (aTo.isConnectedWith (aFrom));
    assertSame (aRel, aTo.getRelation (aFrom));
    assertTrue (aTo.getAllRelatedNodeIDs ().contains ("from"));
  }

  @Test
  public void testDefaultCtorAndBasics ()
  {
    final DirectedGraphNode aNode = new DirectedGraphNode ();
    assertNotNull (aNode.getID ());
    assertTrue (aNode.isDirected ());
    assertNotNull (aNode.attrs ());
    assertNotNull (aNode.toString ());
    assertEquals (aNode, aNode);
    assertEquals (aNode.hashCode (), aNode.hashCode ());
    assertFalse (aNode.equals (null));
    assertFalse (aNode.equals ("any other type"));
  }
}
