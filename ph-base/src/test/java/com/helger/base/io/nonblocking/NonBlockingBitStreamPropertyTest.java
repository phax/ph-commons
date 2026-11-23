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
package com.helger.base.io.nonblocking;

import static org.junit.Assert.assertEquals;

import java.nio.ByteOrder;
import java.util.List;

import org.junit.Test;

import com.helger.unittest.property.Generators;
import com.helger.unittest.property.IGenerator;
import com.helger.unittest.property.PropertyChecker;

/**
 * Property based tests for {@link NonBlockingBitOutputStream} and {@link NonBlockingBitInputStream}:
 * any sequence of values with any bit widths reads back unchanged.
 *
 * @author Philip Helger
 */
public final class NonBlockingBitStreamPropertyTest
{
  /** Pairs of bit width (1-32) and value */
  private static final IGenerator <List <Integer>> WIDTH_AND_VALUE = Generators.lists (Generators.ints (), 2, 2)
                                                                               .map (aList -> List.of (Integer.valueOf (1 +
                                                                                                                        Math.floorMod (aList.get (0)
                                                                                                                                            .intValue (),
                                                                                                                                       32)),
                                                                                                      aList.get (1)));

  private static int _getLowBits (final int nValue, final int nBits)
  {
    return nBits == 32 ? nValue : nValue & ((1 << nBits) - 1);
  }

  @Test
  public void testWriteReadRoundTrip ()
  {
    new PropertyChecker ().setTries (1000)
                          .forAll (Generators.lists (WIDTH_AND_VALUE, 0, 50),
                                   Generators.of (ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN),
                                   (aEntries, aByteOrder) -> {
                                     final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
                                     try (final NonBlockingBitOutputStream aBOS = new NonBlockingBitOutputStream (aBAOS,
                                                                                                                  aByteOrder))
                                     {
                                       for (final List <Integer> aEntry : aEntries)
                                         aBOS.writeBits (aEntry.get (1).intValue (), aEntry.get (0).intValue ());
                                     }

                                     try (final NonBlockingBitInputStream aBIS = new NonBlockingBitInputStream (new NonBlockingByteArrayInputStream (aBAOS.toByteArray ()),
                                                                                                                aByteOrder))
                                     {
                                       for (final List <Integer> aEntry : aEntries)
                                       {
                                         final int nBits = aEntry.get (0).intValue ();
                                         assertEquals (_getLowBits (aEntry.get (1).intValue (), nBits), aBIS.readBits (nBits));
                                       }
                                     }
                                   });
  }
}
