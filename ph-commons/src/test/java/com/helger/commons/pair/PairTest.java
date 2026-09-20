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
package com.helger.commons.pair;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test class for class {@link Pair}.
 *
 * @author Philip Helger
 */
public final class PairTest
{
  @Test
  public void testCtor ()
  {
    final Pair <String, Integer> aEmpty = new Pair <> ();
    assertNull (aEmpty.getFirst ());
    assertNull (aEmpty.getSecond ());

    final Pair <String, Integer> aPair = new Pair <> ("abc", Integer.valueOf (17));
    assertEquals ("abc", aPair.getFirst ());
    assertEquals (Integer.valueOf (17), aPair.getSecond ());

    final Pair <String, Integer> aCopy = new Pair <> (aPair);
    assertEquals ("abc", aCopy.getFirst ());
    assertEquals (Integer.valueOf (17), aCopy.getSecond ());

    assertEquals (aPair, Pair.create ("abc", Integer.valueOf (17)));
  }

  @Test
  public void testSetter ()
  {
    final Pair <String, Integer> aPair = new Pair <> ();
    assertTrue (aPair.setFirst ("abc").isChanged ());
    assertFalse (aPair.setFirst ("abc").isChanged ());
    assertEquals ("abc", aPair.getFirst ());
    assertTrue (aPair.setFirst (null).isChanged ());
    assertNull (aPair.getFirst ());

    assertTrue (aPair.setSecond (Integer.valueOf (17)).isChanged ());
    assertFalse (aPair.setSecond (Integer.valueOf (17)).isChanged ());
    assertEquals (Integer.valueOf (17), aPair.getSecond ());
    assertTrue (aPair.setSecond (null).isChanged ());
    assertNull (aPair.getSecond ());
  }

  @Test
  public void testCloneAndEquals ()
  {
    final Pair <String, Integer> aPair = Pair.create ("abc", Integer.valueOf (17));

    final Pair <String, Integer> aClone = aPair.getClone ();
    assertNotSame (aPair, aClone);
    assertEquals (aPair, aClone);
    assertEquals (aPair.hashCode (), aClone.hashCode ());
    assertNotNull (aPair.toString ());

    assertEquals (aPair, aPair);
    assertNotEquals (aPair, null);
    assertNotEquals (aPair, "any other type");
    assertNotEquals (aPair, Pair.create ("def", Integer.valueOf (17)));
    assertNotEquals (aPair, Pair.create ("abc", Integer.valueOf (18)));
    assertEquals (new Pair <> (), new Pair <> ());

    // Modifying the clone does not modify the original
    aClone.setFirst ("def");
    assertEquals ("abc", aPair.getFirst ());
  }
}
