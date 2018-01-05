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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link GraphRelation}.
 *
 * @author Philip Helger
 */
public final class GraphRelationTest
{
  @Test
  @SuppressWarnings ("unused")
  public void testCtor ()
  {
    new GraphRelation (new GraphNode (), new GraphNode ());

    try
    {
      new GraphRelation (new GraphNode (), null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      new GraphRelation (null, new GraphNode ());
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      new GraphRelation (null, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testGet ()
  {
    final GraphNode nf = new GraphNode ();
    final GraphNode nt = new GraphNode ();
    final GraphRelation gr = new GraphRelation (nf, nt);
    assertEquals (2, gr.getAllConnectedNodes ().size ());
    assertTrue (gr.getAllConnectedNodes ().contains (nf));
    assertTrue (gr.getAllConnectedNodes ().contains (nt));

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new GraphRelation ("id1", nf, nt),
                                                                       new GraphRelation ("id1", nf, nt));
    // different IDs
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new GraphRelation (nf, nt),
                                                                           new GraphRelation (nf, nt));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new GraphRelation ("id1", nf, nt),
                                                                           new GraphRelation ("id1",
                                                                                              nf,
                                                                                              new GraphNode ()));
  }
}
