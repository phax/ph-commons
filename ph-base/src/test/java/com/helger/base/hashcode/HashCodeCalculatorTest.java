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
package com.helger.base.hashcode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

/**
 * Test class for class {@link HashCodeCalculator}.
 *
 * @author Philip Helger
 */
public final class HashCodeCalculatorTest
{
  private static final int P = HashCodeCalculator.INITIAL_HASHCODE;

  @Test
  public void testHashCodePrimitives ()
  {
    assertNotEquals (HashCodeCalculator.hashCode (true), HashCodeCalculator.hashCode (false));
    assertEquals (HashCodeCalculator.hashCode ((byte) 5), HashCodeCalculator.hashCode ((byte) 5));
    assertEquals (HashCodeCalculator.hashCode ('a'), HashCodeCalculator.hashCode ('a'));
    assertEquals (HashCodeCalculator.hashCode (1.5d), HashCodeCalculator.hashCode (1.5d));
    assertEquals (HashCodeCalculator.hashCode (1.5f), HashCodeCalculator.hashCode (1.5f));
    assertEquals (HashCodeCalculator.hashCode ((short) 5), HashCodeCalculator.hashCode ((short) 5));

    assertEquals (HashCodeCalculator.HASHCODE_NULL, HashCodeCalculator.hashCode ((Object) null));
    final Object o = "abc";
    assertEquals ("abc".hashCode (), HashCodeCalculator.hashCode (o));
  }

  @Test
  public void testAppendPrimitives ()
  {
    assertNotEquals (HashCodeCalculator.append (P, true), HashCodeCalculator.append (P, false));
    assertEquals (HashCodeCalculator.append (P, (byte) 5), HashCodeCalculator.append (P, (byte) 5));
    assertEquals (HashCodeCalculator.append (P, 'a'), HashCodeCalculator.append (P, 'a'));
    assertEquals (HashCodeCalculator.append (P, 1.5d), HashCodeCalculator.append (P, 1.5d));
    assertEquals (HashCodeCalculator.append (P, 1.5f), HashCodeCalculator.append (P, 1.5f));
    assertEquals (HashCodeCalculator.append (P, 5), HashCodeCalculator.append (P, 5));
    assertEquals (HashCodeCalculator.append (P, 5L), HashCodeCalculator.append (P, 5L));
    assertEquals (HashCodeCalculator.append (P, (short) 5), HashCodeCalculator.append (P, (short) 5));
    assertEquals (HashCodeCalculator.append (P, "abc"), HashCodeCalculator.append (P, "abc"));
    assertEquals (HashCodeCalculator.append (P, (Object) null), HashCodeCalculator.append (P, (Object) null));
  }

  @Test
  public void testHashCodeArraysWithRange ()
  {
    assertEquals (HashCodeCalculator.hashCode (new boolean [] { true, false }, 0, 2),
                  HashCodeCalculator.hashCode (new boolean [] { true, false }, 0, 2));
    assertEquals (HashCodeCalculator.hashCode (new byte [] { 1, 2 }, 0, 2),
                  HashCodeCalculator.hashCode (new byte [] { 1, 2 }, 0, 2));
    assertEquals (HashCodeCalculator.hashCode (new char [] { 'a', 'b' }, 0, 2),
                  HashCodeCalculator.hashCode (new char [] { 'a', 'b' }, 0, 2));
    assertEquals (HashCodeCalculator.hashCode (new double [] { 1, 2 }, 0, 2),
                  HashCodeCalculator.hashCode (new double [] { 1, 2 }, 0, 2));
    assertEquals (HashCodeCalculator.hashCode (new float [] { 1, 2 }, 0, 2),
                  HashCodeCalculator.hashCode (new float [] { 1, 2 }, 0, 2));
    assertEquals (HashCodeCalculator.hashCode (new int [] { 1, 2 }, 0, 2),
                  HashCodeCalculator.hashCode (new int [] { 1, 2 }, 0, 2));
    assertEquals (HashCodeCalculator.hashCode (new long [] { 1, 2 }, 0, 2),
                  HashCodeCalculator.hashCode (new long [] { 1, 2 }, 0, 2));
    assertEquals (HashCodeCalculator.hashCode (new short [] { 1, 2 }, 0, 2),
                  HashCodeCalculator.hashCode (new short [] { 1, 2 }, 0, 2));
    assertEquals (HashCodeCalculator.hashCode (new Object [] { "a", null }, 0, 2),
                  HashCodeCalculator.hashCode (new Object [] { "a", null }, 0, 2));

    // A partial range differs from the full one
    assertNotEquals (HashCodeCalculator.hashCode (new int [] { 1, 2 }, 0, 1),
                     HashCodeCalculator.hashCode (new int [] { 1, 2 }, 0, 2));
  }

  @Test
  public void testAppendArrays ()
  {
    assertEquals (HashCodeCalculator.append (P, new boolean [] { true }),
                  HashCodeCalculator.append (P, new boolean [] { true }));
    assertEquals (HashCodeCalculator.append (P, new byte [] { 1 }), HashCodeCalculator.append (P, new byte [] { 1 }));
    assertEquals (HashCodeCalculator.append (P, new char [] { 'a' }),
                  HashCodeCalculator.append (P, new char [] { 'a' }));
    assertEquals (HashCodeCalculator.append (P, new double [] { 1 }),
                  HashCodeCalculator.append (P, new double [] { 1 }));
    assertEquals (HashCodeCalculator.append (P, new float [] { 1 }), HashCodeCalculator.append (P, new float [] { 1 }));
    assertEquals (HashCodeCalculator.append (P, new int [] { 1 }), HashCodeCalculator.append (P, new int [] { 1 }));
    assertEquals (HashCodeCalculator.append (P, new long [] { 1 }), HashCodeCalculator.append (P, new long [] { 1 }));
    assertEquals (HashCodeCalculator.append (P, new short [] { 1 }), HashCodeCalculator.append (P, new short [] { 1 }));
    assertEquals (HashCodeCalculator.append (P, new Object [] { "a" }),
                  HashCodeCalculator.append (P, new Object [] { "a" }));

    // null arrays are handled
    assertEquals (HashCodeCalculator.append (P, (boolean []) null), HashCodeCalculator.append (P, (boolean []) null));
    assertEquals (HashCodeCalculator.append (P, (byte []) null), HashCodeCalculator.append (P, (byte []) null));
    assertEquals (HashCodeCalculator.append (P, (char []) null), HashCodeCalculator.append (P, (char []) null));
    assertEquals (HashCodeCalculator.append (P, (double []) null), HashCodeCalculator.append (P, (double []) null));
    assertEquals (HashCodeCalculator.append (P, (float []) null), HashCodeCalculator.append (P, (float []) null));
    assertEquals (HashCodeCalculator.append (P, (int []) null), HashCodeCalculator.append (P, (int []) null));
    assertEquals (HashCodeCalculator.append (P, (long []) null), HashCodeCalculator.append (P, (long []) null));
    assertEquals (HashCodeCalculator.append (P, (short []) null), HashCodeCalculator.append (P, (short []) null));
    assertEquals (HashCodeCalculator.append (P, (Object []) null), HashCodeCalculator.append (P, (Object []) null));
  }

  @Test
  public void testAppendArraysWithRange ()
  {
    assertEquals (HashCodeCalculator.append (P, new boolean [] { true, false }, 0, 1),
                  HashCodeCalculator.append (P, new boolean [] { true, false }, 0, 1));
    assertEquals (HashCodeCalculator.append (P, new byte [] { 1, 2 }, 0, 1),
                  HashCodeCalculator.append (P, new byte [] { 1, 2 }, 0, 1));
    assertEquals (HashCodeCalculator.append (P, new char [] { 'a', 'b' }, 0, 1),
                  HashCodeCalculator.append (P, new char [] { 'a', 'b' }, 0, 1));
    assertEquals (HashCodeCalculator.append (P, new double [] { 1, 2 }, 0, 1),
                  HashCodeCalculator.append (P, new double [] { 1, 2 }, 0, 1));
    assertEquals (HashCodeCalculator.append (P, new float [] { 1, 2 }, 0, 1),
                  HashCodeCalculator.append (P, new float [] { 1, 2 }, 0, 1));
    assertEquals (HashCodeCalculator.append (P, new int [] { 1, 2 }, 0, 1),
                  HashCodeCalculator.append (P, new int [] { 1, 2 }, 0, 1));
    assertEquals (HashCodeCalculator.append (P, new long [] { 1, 2 }, 0, 1),
                  HashCodeCalculator.append (P, new long [] { 1, 2 }, 0, 1));
    assertEquals (HashCodeCalculator.append (P, new short [] { 1, 2 }, 0, 1),
                  HashCodeCalculator.append (P, new short [] { 1, 2 }, 0, 1));
    assertEquals (HashCodeCalculator.append (P, new Object [] { "a", "b" }, 0, 1),
                  HashCodeCalculator.append (P, new Object [] { "a", "b" }, 0, 1));
  }
}
