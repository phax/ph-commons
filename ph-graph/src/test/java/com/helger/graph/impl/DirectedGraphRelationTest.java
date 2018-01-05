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

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link DirectedGraphRelation}.
 *
 * @author Philip Helger
 */
public final class DirectedGraphRelationTest
{
  @Test
  @SuppressWarnings ("unused")
  public void testCtor ()
  {
    new DirectedGraphRelation (new DirectedGraphNode (), new DirectedGraphNode ());

    try
    {
      new DirectedGraphRelation (new DirectedGraphNode (), null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      new DirectedGraphRelation (null, new DirectedGraphNode ());
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      new DirectedGraphRelation (null, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testGet ()
  {
    final DirectedGraphNode nf = new DirectedGraphNode ();
    final DirectedGraphNode nt = new DirectedGraphNode ();
    final DirectedGraphRelation gr = new DirectedGraphRelation (nf, nt);
    assertSame (gr.getFrom (), nf);
    assertSame (gr.getTo (), nt);

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new DirectedGraphRelation ("id1", nf, nt),
                                                                       new DirectedGraphRelation ("id1", nf, nt));
    // different IDs
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new DirectedGraphRelation (nf, nt),
                                                                           new DirectedGraphRelation (nf, nt));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new DirectedGraphRelation ("id1", nf, nt),
                                                                           new DirectedGraphRelation ("id1",
                                                                                                      nf,
                                                                                                      new DirectedGraphNode ()));
  }
}
