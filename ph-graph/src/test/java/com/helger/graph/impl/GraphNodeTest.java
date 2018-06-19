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
package com.helger.graph.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;
import com.helger.commons.string.StringHelper;
import com.helger.graph.IMutableGraphRelation;

/**
 * Test class for class {@link GraphNode}.
 *
 * @author Philip Helger
 */
public final class GraphNodeTest
{
  @Test
  public void testCtor ()
  {
    final GraphNode n = new GraphNode ();
    assertNotNull (n.getID ());
    assertTrue (StringHelper.hasText (n.getID ()));

    final GraphNode n1 = new GraphNode ("");
    assertNotNull (n1.getID ());
    assertTrue (StringHelper.hasText (n1.getID ()));
    assertFalse (n1.hasRelations ());

    final GraphNode n3 = new GraphNode ("id1");
    assertNotNull (n3.getID ());
    assertEquals ("id1", n3.getID ());
  }

  @Test
  public void testRelationsOutgoing1 ()
  {
    final GraphNode nf = new GraphNode ();
    final GraphNode nt = new GraphNode ();
    final IMutableGraphRelation gr = new GraphRelation (nf, nt);
    nf.addRelation (gr);

    assertTrue (nf.isConnectedWith (nt));
    assertFalse (nt.isConnectedWith (nf));
    assertTrue (nf.hasRelations ());
    assertEquals (1, nf.getAllRelations ().size ());
    assertEquals (0, nt.getAllRelations ().size ());

    assertTrue (nf.isConnectedWith (nt));
    assertFalse (nf.isConnectedWith (nf));
    assertFalse (nt.isConnectedWith (nf));
    assertFalse (nt.isConnectedWith (nt));
  }

  @Test
  public void testSelfRelations ()
  {
    final GraphNode nf = new GraphNode ();
    nf.addRelation (new GraphRelation (nf, nf));
  }

  @Test
  public void testStdMethods ()
  {
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new GraphNode ("id0"), new GraphNode ("id0"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new GraphNode ("id0"),
                                                                           new GraphNode ("id1"));
    final GraphNode n1 = new GraphNode ("id0");
    n1.attrs ().putIn ("a", "b");
    final GraphNode n2 = new GraphNode ("id0");
    n2.attrs ().putIn ("a", "c");
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (n1, n2);
  }

  @Test
  public void testRelationsInvalid ()
  {
    GraphNode n = new GraphNode ();
    // null not allowed
    assertFalse (n.addRelation ((IMutableGraphRelation) null).isChanged ());

    try
    {
      n = new GraphNode ();
      // relation does not match node
      n.addRelation (new GraphRelation (new GraphNode (), new GraphNode ()));
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    n = new GraphNode ();
    GraphRelation r = new GraphRelation (n, new GraphNode ());
    assertTrue (n.addRelation (r).isChanged ());
    // exactly the same relation already added
    assertFalse (n.addRelation (r).isChanged ());

    final GraphNode nf = new GraphNode ();
    final GraphNode nt = new GraphNode ();
    r = new GraphRelation (nf, nt);
    nf.addRelation (r);
    assertTrue (nf.hasRelations ());
    assertTrue (nf.hasRelations ());
    // relation already contained
    assertFalse (nf.addRelation (r).isChanged ());
  }
}
