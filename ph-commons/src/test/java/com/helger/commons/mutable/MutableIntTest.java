/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.mutable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link MutableInt}.
 *
 * @author Philip Helger
 */
public final class MutableIntTest
{
  @Test
  public void testMutableInt ()
  {
    final MutableInt x = new MutableInt ();
    assertEquals (x.intValue (), 0);
    assertEquals (x.getAsInteger (), Integer.valueOf (0));
    assertFalse (x.isNot0 ());
    assertTrue (x.is0 ());

    x.inc ();
    assertEquals (x.intValue (), 1);
    assertFalse (x.hashCode () == x.intValue ());

    x.inc (5);
    assertEquals (x.intValue (), 6);
    assertFalse (x.hashCode () == x.intValue ());

    x.inc (-2);
    assertEquals (x.intValue (), 4);
    assertFalse (x.hashCode () == x.intValue ());

    x.dec ();
    assertEquals (x.intValue (), 3);
    assertFalse (x.isEven ());
    assertFalse (x.hashCode () == x.intValue ());

    x.dec (5);
    assertEquals (x.intValue (), -2);
    assertTrue (x.isNot0 ());
    assertFalse (x.is0 ());
    assertTrue (x.isEven ());
    assertFalse (x.hashCode () == x.intValue ());

    assertTrue (x.set (4711).isChanged ());
    assertFalse (x.set (4711).isChanged ());
    assertFalse (x.isEven ());
    assertEquals (x.intValue (), 4711);

    assertEquals (-1, new MutableInt (4).compareTo (new MutableInt (5)));
    assertEquals (0, new MutableInt (5).compareTo (new MutableInt (5)));
    assertEquals (+1, new MutableInt (6).compareTo (new MutableInt (5)));

    assertNotNull (x.toString ());
    assertTrue (x.toString ().contains (Integer.toString (x.intValue ())));

    x.set (-1);
    assertFalse (x.isGreater0 ());
    x.set (0);
    assertFalse (x.isGreater0 ());
    x.set (1);
    assertTrue (x.isGreater0 ());

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new MutableInt (-7), new MutableInt (-7));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new MutableInt (6), new MutableInt (7));
    CommonsTestHelper.testGetClone (new MutableInt (Integer.MAX_VALUE));
  }
}
