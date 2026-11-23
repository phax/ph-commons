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
package com.helger.collection.ring;

import static org.junit.Assert.assertEquals;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import org.junit.Test;

import com.helger.base.state.EChange;
import com.helger.unittest.property.Generators;
import com.helger.unittest.property.IGenerator;
import com.helger.unittest.property.PropertyChecker;

/**
 * Model based property tests for {@link RingBufferFifo} and {@link RingBufferLifo} with an
 * {@link ArrayDeque} as the model.
 *
 * @author Philip Helger
 */
public final class RingBufferPropertyTest
{
  /** Operations: values &ge; 0 are put, -1 is take */
  private static final IGenerator <List <Integer>> OPERATIONS = Generators.lists (Generators.ints (-1, 100), 0, 100);

  @Test
  public void testFifo ()
  {
    new PropertyChecker ().setTries (1000)
                          .forAll (Generators.ints (1, 8), Generators.booleans (), OPERATIONS, (aCapacity, aOverwrite, aOps) -> {
                            final int nCapacity = aCapacity.intValue ();
                            final boolean bOverwrite = aOverwrite.booleanValue ();
                            final RingBufferFifo <Integer> aRing = new RingBufferFifo <> (nCapacity, bOverwrite);
                            final Deque <Integer> aModel = new ArrayDeque <> ();
                            int nStep = 0;
                            for (final Integer aOp : aOps)
                            {
                              final String sMsg = "Step " + nStep++ + ": " + aOp;
                              if (aOp.intValue () < 0)
                                assertEquals (sMsg, aModel.pollFirst (), aRing.take ());
                              else
                              {
                                final EChange eExpected;
                                if (aModel.size () < nCapacity)
                                {
                                  aModel.addLast (aOp);
                                  eExpected = EChange.CHANGED;
                                }
                                else
                                  if (bOverwrite)
                                  {
                                    // The oldest element is overwritten
                                    aModel.pollFirst ();
                                    aModel.addLast (aOp);
                                    eExpected = EChange.CHANGED;
                                  }
                                  else
                                    eExpected = EChange.UNCHANGED;
                                assertEquals (sMsg, eExpected, aRing.put (aOp));
                              }
                              assertEquals (sMsg, aModel.size (), aRing.getAvailable ());
                              assertEquals (sMsg, nCapacity - aModel.size (), aRing.getRemainingCapacity ());
                            }
                          });
  }

  @Test
  public void testLifo ()
  {
    new PropertyChecker ().setTries (1000)
                          .forAll (Generators.ints (1, 8), Generators.booleans (), OPERATIONS, (aCapacity, aOverwrite, aOps) -> {
                            final int nCapacity = aCapacity.intValue ();
                            final boolean bOverwrite = aOverwrite.booleanValue ();
                            final RingBufferLifo <Integer> aRing = new RingBufferLifo <> (nCapacity, bOverwrite);
                            final Deque <Integer> aModel = new ArrayDeque <> ();
                            int nStep = 0;
                            for (final Integer aOp : aOps)
                            {
                              final String sMsg = "Step " + nStep++ + ": " + aOp;
                              if (aOp.intValue () < 0)
                                assertEquals (sMsg, aModel.pollLast (), aRing.take ());
                              else
                              {
                                final EChange eExpected;
                                if (aModel.size () < nCapacity)
                                {
                                  aModel.addLast (aOp);
                                  eExpected = EChange.CHANGED;
                                }
                                else
                                  if (bOverwrite)
                                  {
                                    // The oldest element is overwritten
                                    aModel.pollFirst ();
                                    aModel.addLast (aOp);
                                    eExpected = EChange.CHANGED;
                                  }
                                  else
                                    eExpected = EChange.UNCHANGED;
                                assertEquals (sMsg, eExpected, aRing.put (aOp));
                              }
                              assertEquals (sMsg, aModel.size (), aRing.getAvailable ());
                            }
                          });
  }
}
