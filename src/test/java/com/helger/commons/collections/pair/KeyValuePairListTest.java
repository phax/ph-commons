/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.collections.pair;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test class for class {@link KeyValuePairList}.
 * 
 * @author Philip Helger
 */
public final class KeyValuePairListTest
{
  @Test
  public void testAll ()
  {
    final KeyValuePairList <Integer, String> x = new KeyValuePairList <Integer, String> ();
    assertTrue (x.isEmpty ());
    x.add (Integer.valueOf (4), "Hi there");
    assertEquals (1, x.size ());
    final IReadonlyPair <Integer, String> p = x.get (0);
    assertNotNull (p);
    assertEquals (Integer.valueOf (4), p.getFirst ());
    assertEquals ("Hi there", p.getSecond ());

    x.add (Integer.valueOf (5), null);
    assertEquals (2, x.size ());
    assertNull (x.get (1).getSecond ());

    x.addNonNullValue (Integer.valueOf (6), null);
    assertEquals (2, x.size ());

    x.addNonNullValue (Integer.valueOf (7), "any");
    assertEquals (3, x.size ());
  }
}
