/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.collections.triple;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test class for class {@link ReadonlyTriple}.
 * 
 * @author Philip Helger
 */
public final class ReadonlyTripleTest
{
  @Test
  public void testAll ()
  {
    final ReadonlyTriple <String, String, String> rt = new ReadonlyTriple <String, String, String> (null, null, null);
    assertNull (rt.getFirst ());
    assertNull (rt.getSecond ());
    assertNull (rt.getThird ());
    final ReadonlyTriple <String, String, String> rt2 = new ReadonlyTriple <String, String, String> (rt);
    assertNull (rt2.getFirst ());
    assertNull (rt2.getSecond ());
    assertNull (rt2.getThird ());
    assertNotNull (rt2.toString ());

    assertEquals (rt, rt);
    assertEquals (rt, rt2);
    assertFalse (rt.equals (null));
    assertFalse (rt.equals ("any"));

    final ReadonlyTriple <String, String, String> rt3a = new ReadonlyTriple <String, String, String> ("a", null, null);
    final ReadonlyTriple <String, String, String> rt3b = new ReadonlyTriple <String, String, String> (null, "a", null);
    final ReadonlyTriple <String, String, String> rt3c = new ReadonlyTriple <String, String, String> (null, null, "a");
    assertFalse (rt.equals (rt3a));
    assertFalse (rt.equals (rt3b));
    assertFalse (rt.equals (rt3c));
    assertFalse (rt3a.equals (rt));
    assertFalse (rt3a.equals (rt3b));
    assertFalse (rt3a.equals (rt3c));
    assertFalse (rt3b.equals (rt));
    assertFalse (rt3b.equals (rt3a));
    assertFalse (rt3b.equals (rt3c));
    assertFalse (rt3c.equals (rt));
    assertFalse (rt3c.equals (rt3a));
    assertFalse (rt3c.equals (rt3b));

    assertTrue (rt.hashCode () == rt2.hashCode ());
    assertFalse (rt.hashCode () == rt3a.hashCode ());
    assertFalse (rt.hashCode () == rt3b.hashCode ());
    assertFalse (rt.hashCode () == rt3c.hashCode ());
    assertFalse (rt3a.hashCode () == rt3b.hashCode ());
    assertFalse (rt3a.hashCode () == rt3c.hashCode ());
    assertFalse (rt3b.hashCode () == rt3c.hashCode ());

    final IReadonlyTriple <String, String, String> rt4 = ReadonlyTriple.create ("a", "b", "c");
    assertEquals ("a", rt4.getFirst ());
    assertEquals ("b", rt4.getSecond ());
    assertEquals ("c", rt4.getThird ());
  }
}
