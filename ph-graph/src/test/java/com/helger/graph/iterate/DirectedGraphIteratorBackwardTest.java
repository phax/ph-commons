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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.NoSuchElementException;

import org.junit.Test;

import com.helger.graph.AbstractGraphTestCase;
import com.helger.graph.IMutableDirectedGraph;
import com.helger.graph.IMutableDirectedGraphNode;

/**
 * Test class for class {@link DirectedGraphIteratorBackward}.
 *
 * @author Philip Helger
 */
@SuppressWarnings ("unused")
public final class DirectedGraphIteratorBackwardTest extends AbstractGraphTestCase
{
  @Test
  public void testGraphIterator ()
  {
    try
    {
      // null node not allowed
      new DirectedGraphIteratorBackward (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    final IMutableDirectedGraph aGraph = _buildDirectedGraph ();
    final IMutableDirectedGraphNode aEnd = aGraph.getSingleEndNode ();
    assertEquals ("4", aEnd.getID ());
    final DirectedGraphIteratorBackward it = new DirectedGraphIteratorBackward (aEnd);

    try
    {
      it.remove ();
      fail ();
    }
    catch (final UnsupportedOperationException ex)
    {
      // expected exception
    }

    // one of the following assertEquals may fail if the hashmap is sorted in a
    // different way
    for (int i = 0; i < 100; ++i)
      assertTrue (it.hasNext ());
    assertEquals (5, _getNodeValue (it.next ()));
    for (int i = 0; i < 100; ++i)
      assertTrue (it.hasNext ());
    assertEquals (4, _getNodeValue (it.next ()));
    for (int i = 0; i < 100; ++i)
      assertTrue (it.hasNext ());
    assertEquals (3, _getNodeValue (it.next ()));
    assertTrue (it.hasNext ());
    assertEquals (2, _getNodeValue (it.next ()));
    assertTrue (it.hasNext ());
    assertEquals (1, _getNodeValue (it.next ()));
    assertTrue (it.hasNext ());
    assertEquals (6, _getNodeValue (it.next ()));
    assertTrue (it.hasNext ());
    assertEquals (7, _getNodeValue (it.next ()));
    assertFalse (it.hasNext ());
  }

  @Test
  public void testStartIteratingInTheMiddleOneWay ()
  {
    final IMutableDirectedGraph aGraph = _buildDirectedGraph ();
    final IMutableDirectedGraphNode aEndNode = aGraph.getNodeOfID ("5");
    final DirectedGraphIteratorBackward it = new DirectedGraphIteratorBackward (aEndNode);
    assertTrue (it.hasNext ());
    assertEquals (6, _getNodeValue (it.next ()));
    assertTrue (it.hasNext ());
    assertEquals (1, _getNodeValue (it.next ()));
    assertFalse (it.hasNext ());
  }

  @Test
  public void testStartIteratingInTheMiddleTwoWays ()
  {
    final IMutableDirectedGraph aGraph = _buildDirectedGraph ();
    final IMutableDirectedGraphNode aStartNode = aGraph.getNodeOfID ("3");
    final DirectedGraphIteratorBackward it = new DirectedGraphIteratorBackward (aStartNode);
    assertTrue (it.hasNext ());
    assertEquals (4, _getNodeValue (it.next ()));
    assertTrue (it.hasNext ());
    assertEquals (3, _getNodeValue (it.next ()));
    assertTrue (it.hasNext ());
    assertEquals (2, _getNodeValue (it.next ()));
    assertTrue (it.hasNext ());
    assertEquals (1, _getNodeValue (it.next ()));
    assertTrue (it.hasNext ());
    assertEquals (6, _getNodeValue (it.next ()));
    assertTrue (it.hasNext ());
    assertEquals (7, _getNodeValue (it.next ()));
    assertFalse (it.hasNext ());
  }

  @Test
  public void testCycleIterate1 ()
  {
    final IMutableDirectedGraph aGraph = _buildSimpleDirectedGraphCycle ();
    final DirectedGraphIteratorBackward it = new DirectedGraphIteratorBackward (aGraph.getNodeOfID ("0"));
    assertTrue (it.hasNext ());
    // first item has ID 0 and value 1
    assertEquals (1, _getNodeValue (it.next ()));
    assertTrue (it.hasNext ());
    // second item has ID 1 and value 2
    assertEquals (2, _getNodeValue (it.next ()));
    assertFalse (it.hasNext ());
  }

  @Test
  public void testCycleIterate2 ()
  {
    final IMutableDirectedGraph aGraph = _buildSimpleDirectedGraphCycle2 ();
    final DirectedGraphIteratorBackward it = new DirectedGraphIteratorBackward (aGraph.getNodeOfID ("3"));
    assertNotNull (it.iterator ());
    assertTrue (it.hasNext ());
    assertEquals (4, _getNodeValue (it.next ()));
    assertTrue (it.hasNext ());
    assertEquals (3, _getNodeValue (it.next ()));
    assertTrue (it.hasNext ());
    assertEquals (2, _getNodeValue (it.next ()));
    assertTrue (it.hasNext ());
    assertEquals (1, _getNodeValue (it.next ()));
    assertFalse (it.hasNext ());

    try
    {
      it.next ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {}
  }
}
