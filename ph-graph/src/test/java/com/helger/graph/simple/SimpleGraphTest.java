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
package com.helger.graph.simple;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.PrintWriter;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.io.stream.NonBlockingStringWriter;
import com.helger.commons.mock.CommonsTestHelper;
import com.helger.graph.AbstractGraphTestCase;
import com.helger.graph.IMutableGraphNode;
import com.helger.graph.impl.GraphNode;
import com.helger.matrix.Matrix;

/**
 * Test class for class {@link SimpleGraph}.
 *
 * @author Philip Helger
 */
public final class SimpleGraphTest extends AbstractGraphTestCase
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (SimpleGraphTest.class);

  @Test
  public void testCtor ()
  {
    final SimpleGraph sg = new SimpleGraph ();
    assertTrue (sg.getAllNodes ().isEmpty ());
    assertTrue (sg.getAllRelations ().isEmpty ());
    assertNotNull (sg.toString ());

    try
    {
      // No nodes
      sg.createIncidenceMatrix ();
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testAddNode ()
  {
    final SimpleGraph sg = new SimpleGraph ();
    assertEquals (0, sg.getNodeCount ());
    try
    {
      // null node not allowed
      sg.addNode ((GraphNode) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    final GraphNode n = new GraphNode ();
    assertTrue (sg.addNode (n).isChanged ());
    assertEquals (1, sg.getNodeCount ());
    assertFalse (n.hasRelations ());

    // node already contained
    assertFalse (sg.addNode (n).isChanged ());
    assertTrue (sg.getAllRelations ().isEmpty ());

    // Add a second node
    final GraphNode n2 = new GraphNode ();
    assertTrue (sg.addNode (n2).isChanged ());
    assertFalse (n2.hasRelations ());

    // node already contained
    assertFalse (sg.addNode (n2).isChanged ());
    assertTrue (sg.getAllRelations ().isEmpty ());

    assertNotNull (sg.createNode ());
    assertNotNull (sg.createNode ("id4711"));
    assertNull (sg.createNode ("id4711"));

    // No relations
    assertEquals (sg.createIncidenceMatrix (), new Matrix (4, 4, 0));
  }

  @Test
  public void testClear ()
  {
    final SimpleGraph sg = new SimpleGraph ();

    final GraphNode n = new GraphNode ();
    assertTrue (sg.addNode (n).isChanged ());

    final GraphNode n2 = new GraphNode ();
    assertTrue (sg.addNode (n2).isChanged ());

    assertTrue (sg.removeAll ().isChanged ());
    assertFalse (sg.removeAll ().isChanged ());
  }

  @Test
  public void testCycles ()
  {
    SimpleGraph sg = _buildGraph ();
    assertTrue (sg.isSelfContained ());

    sg = new SimpleGraph ();
    final IMutableGraphNode n1 = sg.createNode (null);
    final IMutableGraphNode n2 = sg.createNode (null);
    sg.createRelation (n1, n2);
    sg.createRelation (n2, n1);

    assertTrue (n1.hasRelations ());
    assertTrue (n1.isConnectedWith (n2));

    assertTrue (n2.hasRelations ());
    assertTrue (n2.isConnectedWith (n1));

    assertTrue (sg.isSelfContained ());

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (_buildGraph (), _buildGraph ());
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new SimpleGraph (), new SimpleGraph ());
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (_buildGraph (), new SimpleGraph ());
  }

  @Test
  public void testCycles2 ()
  {
    final SimpleGraph sg = new SimpleGraph ();
    for (int i = 1; i <= 6; ++i)
      sg.createNode (Integer.toString (i));

    // first cycle
    sg.createRelation ("1", "2");
    assertFalse (sg.containsCycles ());
    sg.createRelation ("2", "3");
    assertFalse (sg.containsCycles ());
    sg.createRelation ("3", "1");
    assertTrue (sg.containsCycles ());

    // Second cycle
    sg.createRelation ("4", "5");
    assertTrue (sg.containsCycles ());
    sg.createRelation ("5", "6");
    assertTrue (sg.containsCycles ());
    sg.createRelation ("6", "4");
    assertTrue (sg.containsCycles ());

    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    sg.createIncidenceMatrix ().print (new PrintWriter (aSW), 2, 0);
    s_aLogger.info (aSW.getAsString ());
  }

  @Test
  public void testSelfContained ()
  {
    final ISimpleGraph sg = new SimpleGraph ();
    assertTrue (sg.isSelfContained ());

    // n1 belongs to the graph
    IMutableGraphNode n1 = sg.createNode ("any");
    assertTrue (sg.isSelfContained ());

    // n2 does not belong to the graph
    IMutableGraphNode n2 = new GraphNode ("other");
    sg.createRelation (n1, n2);
    assertFalse (sg.isSelfContained ());

    // Get all relations
    assertEquals (1, sg.getAllRelations ().size ());

    // Remove nodes
    assertTrue (sg.removeNode (n1).isChanged ());
    assertFalse (sg.removeNode (n2).isChanged ());
    assertFalse (sg.removeAll ().isChanged ());

    // n1 does not belongs to the graph
    n1 = new GraphNode ("any");
    assertTrue (sg.isSelfContained ());

    // n2 belongs to the graph
    n2 = sg.createNode ("other");
    sg.createRelation (n1, n2);
    assertFalse (sg.isSelfContained ());
  }
}
