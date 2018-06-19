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

import java.util.Iterator;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;
import com.helger.commons.string.StringHelper;
import com.helger.graph.IMutableDirectedGraphRelation;

/**
 * Test class for class {@link DirectedGraphNode}.
 *
 * @author Philip Helger
 */
public final class DirectedGraphNodeTest
{
  @Test
  public void testCtor ()
  {
    final DirectedGraphNode n = new DirectedGraphNode ();
    assertNotNull (n.getID ());
    assertTrue (StringHelper.hasText (n.getID ()));

    final DirectedGraphNode n1 = new DirectedGraphNode ("");
    assertNotNull (n1.getID ());
    assertTrue (StringHelper.hasText (n1.getID ()));
    assertEquals (0, n1.getAllFromNodes ().size ());
    assertEquals (0, n1.getIncomingRelationCount ());
    assertEquals (0, n1.getAllToNodes ().size ());
    assertEquals (0, n1.getOutgoingRelationCount ());
    assertFalse (n1.hasIncomingRelations ());
    assertFalse (n1.hasOutgoingRelations ());
    assertFalse (n1.hasRelations ());
    assertFalse (n1.hasIncomingAndOutgoingRelations ());

    final DirectedGraphNode n3 = new DirectedGraphNode ("id1");
    assertNotNull (n3.getID ());
    assertEquals ("id1", n3.getID ());
  }

  @Test
  public void testRelationsOutgoing1 ()
  {
    final DirectedGraphNode nf = new DirectedGraphNode ();
    final DirectedGraphNode nt = new DirectedGraphNode ();
    final IMutableDirectedGraphRelation gr = new DirectedGraphRelation (nf, nt);
    nf.addOutgoingRelation (gr);

    assertFalse (nf.isIncomingRelation (gr));
    assertTrue (nf.isOutgoingRelation (gr));
    assertFalse (nt.isIncomingRelation (gr));
    assertFalse (nt.isOutgoingRelation (gr));
    assertTrue (nf.isConnectedWith (nt));
    assertFalse (nt.isConnectedWith (nf));
    assertFalse (nf.hasIncomingRelations ());
    assertTrue (nf.hasOutgoingRelations ());
    assertTrue (nf.hasRelations ());
    assertFalse (nf.hasIncomingAndOutgoingRelations ());

    assertEquals (0, nf.getAllFromNodes ().size ());
    assertEquals (1, nf.getAllToNodes ().size ());
    assertEquals (0, nt.getAllFromNodes ().size ());
    assertEquals (0, nt.getAllToNodes ().size ());

    assertTrue (nf.isConnectedWith (nt));
    assertFalse (nf.isConnectedWith (nf));
    assertFalse (nt.isConnectedWith (nf));
    assertFalse (nt.isConnectedWith (nt));

    Iterator <IMutableDirectedGraphRelation> it = nf.getAllOutgoingRelations ().iterator ();
    assertNotNull (it);
    assertTrue (it.hasNext ());
    assertTrue (it.hasNext ());
    assertEquals (it.next ().getTo (), nt);
    assertFalse (it.hasNext ());
    assertFalse (it.hasNext ());

    it = nf.getAllIncomingRelations ().iterator ();
    assertNotNull (it);
    assertFalse (it.hasNext ());
    assertFalse (it.hasNext ());

    it = nt.getAllIncomingRelations ().iterator ();
    assertNotNull (it);
    assertFalse (it.hasNext ());
    assertFalse (it.hasNext ());

    it = nt.getAllOutgoingRelations ().iterator ();
    assertNotNull (it);
    assertFalse (it.hasNext ());
    assertFalse (it.hasNext ());
  }

  @Test
  public void testRelationsOutgoing2 ()
  {
    final DirectedGraphNode nf = new DirectedGraphNode ();
    final DirectedGraphNode nt = new DirectedGraphNode ();
    nf.addOutgoingRelation (new DirectedGraphRelation (nf, nt));
    Iterator <IMutableDirectedGraphRelation> it = nf.getAllOutgoingRelations ().iterator ();
    assertNotNull (it);
    assertTrue (it.hasNext ());
    assertTrue (it.hasNext ());
    assertEquals (it.next ().getTo (), nt);
    assertFalse (it.hasNext ());
    assertFalse (it.hasNext ());

    it = nf.getAllIncomingRelations ().iterator ();
    assertNotNull (it);
    assertFalse (it.hasNext ());
    assertFalse (it.hasNext ());

    it = nt.getAllIncomingRelations ().iterator ();
    assertNotNull (it);
    assertFalse (it.hasNext ());
    assertFalse (it.hasNext ());

    it = nt.getAllOutgoingRelations ().iterator ();
    assertNotNull (it);
    assertFalse (it.hasNext ());
    assertFalse (it.hasNext ());
  }

  @Test
  public void testRelationsIncoming1 ()
  {
    final DirectedGraphNode nf = new DirectedGraphNode ();
    final DirectedGraphNode nt = new DirectedGraphNode ();
    nt.addIncomingRelation (new DirectedGraphRelation (nf, nt));
    Iterator <IMutableDirectedGraphRelation> it = nf.getAllOutgoingRelations ().iterator ();
    assertNotNull (it);
    assertFalse (it.hasNext ());
    assertFalse (it.hasNext ());

    it = nf.getAllIncomingRelations ().iterator ();
    assertNotNull (it);
    assertFalse (it.hasNext ());
    assertFalse (it.hasNext ());

    it = nt.getAllIncomingRelations ().iterator ();
    assertNotNull (it);
    assertTrue (it.hasNext ());
    assertTrue (it.hasNext ());
    assertEquals (it.next ().getFrom (), nf);
    assertFalse (it.hasNext ());
    assertFalse (it.hasNext ());

    it = nt.getAllOutgoingRelations ().iterator ();
    assertNotNull (it);
    assertFalse (it.hasNext ());
    assertFalse (it.hasNext ());
  }

  @Test
  public void testRelationsIncoming2 ()
  {
    final DirectedGraphNode nf = new DirectedGraphNode ();
    final DirectedGraphNode nt = new DirectedGraphNode ();
    nt.addIncomingRelation (new DirectedGraphRelation (nf, nt));
    Iterator <IMutableDirectedGraphRelation> it = nf.getAllOutgoingRelations ().iterator ();
    assertNotNull (it);
    assertFalse (it.hasNext ());
    assertFalse (it.hasNext ());

    it = nf.getAllIncomingRelations ().iterator ();
    assertNotNull (it);
    assertFalse (it.hasNext ());
    assertFalse (it.hasNext ());

    it = nt.getAllIncomingRelations ().iterator ();
    assertNotNull (it);
    assertTrue (it.hasNext ());
    assertTrue (it.hasNext ());
    assertEquals (it.next ().getFrom (), nf);
    assertFalse (it.hasNext ());
    assertFalse (it.hasNext ());

    it = nt.getAllOutgoingRelations ().iterator ();
    assertNotNull (it);
    assertFalse (it.hasNext ());
    assertFalse (it.hasNext ());
  }

  @Test
  public void testSelfRelations ()
  {
    final DirectedGraphNode nf = new DirectedGraphNode ();
    nf.addOutgoingRelation (new DirectedGraphRelation (nf, nf));
    Iterator <IMutableDirectedGraphRelation> it = nf.getAllOutgoingRelations ().iterator ();
    assertNotNull (it);
    assertTrue (it.hasNext ());
    assertTrue (it.hasNext ());
    assertEquals (it.next ().getTo (), nf);
    assertFalse (it.hasNext ());
    assertFalse (it.hasNext ());

    it = nf.getAllIncomingRelations ().iterator ();
    assertNotNull (it);
    assertFalse (it.hasNext ());
    assertFalse (it.hasNext ());
  }

  @Test
  public void testStdMethods ()
  {
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new DirectedGraphNode ("id0"),
                                                                       new DirectedGraphNode ("id0"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new DirectedGraphNode ("id0"),
                                                                           new DirectedGraphNode ("id1"));
    final DirectedGraphNode n1 = new DirectedGraphNode ("id0");
    n1.attrs ().putIn ("a", "b");
    final DirectedGraphNode n2 = new DirectedGraphNode ("id0");
    n2.attrs ().putIn ("a", "c");
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (n1, n2);
  }

  @Test
  public void testRelationsInvalid ()
  {
    try
    {
      final DirectedGraphNode n = new DirectedGraphNode ();
      // null not allowed
      n.addOutgoingRelation ((IMutableDirectedGraphRelation) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      final DirectedGraphNode n = new DirectedGraphNode ();
      // null not allowed
      n.addIncomingRelation ((IMutableDirectedGraphRelation) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      final DirectedGraphNode n = new DirectedGraphNode ();
      // relation does not match node
      n.addOutgoingRelation (new DirectedGraphRelation (new DirectedGraphNode (), new DirectedGraphNode ()));
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      final DirectedGraphNode n = new DirectedGraphNode ();
      // relation does not match node
      n.addIncomingRelation (new DirectedGraphRelation (new DirectedGraphNode (), new DirectedGraphNode ()));
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      final DirectedGraphNode n = new DirectedGraphNode ();
      final DirectedGraphRelation r = new DirectedGraphRelation (n, new DirectedGraphNode ());
      n.addOutgoingRelation (r);
      // exactly the same relation already added
      n.addOutgoingRelation (r);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      final DirectedGraphNode n = new DirectedGraphNode ();
      final DirectedGraphRelation r = new DirectedGraphRelation (new DirectedGraphNode (), n);
      n.addIncomingRelation (r);
      // exactly the same relation already added
      n.addIncomingRelation (r);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      final DirectedGraphNode nf = new DirectedGraphNode ();
      final DirectedGraphNode nt = new DirectedGraphNode ();
      final IMutableDirectedGraphRelation r = new DirectedGraphRelation (nf, nt);
      nf.addOutgoingRelation (r);
      assertFalse (nf.hasIncomingRelations ());
      assertTrue (nf.hasOutgoingRelations ());
      assertFalse (nt.hasIncomingRelations ());
      assertFalse (nt.hasOutgoingRelations ());
      // relation already contained
      nf.addOutgoingRelation (r);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      final DirectedGraphNode nf = new DirectedGraphNode ();
      final DirectedGraphNode nt = new DirectedGraphNode ();
      final IMutableDirectedGraphRelation r = new DirectedGraphRelation (nf, nt);
      nt.addIncomingRelation (r);
      assertFalse (nf.hasIncomingRelations ());
      assertFalse (nf.hasOutgoingRelations ());
      assertTrue (nt.hasIncomingRelations ());
      assertFalse (nt.hasOutgoingRelations ());
      // relation already contained
      nt.addIncomingRelation (r);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }
}
