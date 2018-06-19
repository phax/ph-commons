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
package com.helger.commons.mutable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
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
    final MutableInt x = new MutableInt (0);
    assertEquals (0, x.intValue ());
    assertEquals (Integer.valueOf (0), x.getAsInteger ());
    assertFalse (x.isNot0 ());
    assertTrue (x.is0 ());

    x.inc ();
    assertEquals (1, x.intValue ());
    assertNotEquals (x.hashCode (), x.intValue ());

    x.inc (5);
    assertEquals (6, x.intValue ());
    assertNotEquals (x.hashCode (), x.intValue ());

    x.inc (-2);
    assertEquals (4, x.intValue ());
    assertNotEquals (x.hashCode (), x.intValue ());

    x.dec ();
    assertEquals (3, x.intValue ());
    assertFalse (x.isEven ());
    assertNotEquals (x.hashCode (), x.intValue ());

    x.dec (5);
    assertEquals (-2, x.intValue ());
    assertTrue (x.isNot0 ());
    assertFalse (x.is0 ());
    assertTrue (x.isEven ());
    assertNotEquals (x.hashCode (), x.intValue ());

    assertTrue (x.set (4711).isChanged ());
    assertFalse (x.set (4711).isChanged ());
    assertFalse (x.isEven ());
    assertEquals (4711, x.intValue ());

    assertEquals (-1, new MutableInt (4).compareTo (new MutableInt (5)));
    assertEquals (0, new MutableInt (5).compareTo (new MutableInt (5)));
    assertEquals (+1, new MutableInt (6).compareTo (new MutableInt (5)));

    assertNotNull (x.toString ());
    assertTrue (x.toString ().contains (Integer.toString (x.intValue ())));

    x.set (-1);
    assertFalse (x.isGT0 ());
    x.set (0);
    assertFalse (x.isGT0 ());
    x.set (1);
    assertTrue (x.isGT0 ());

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new MutableInt (-7), new MutableInt (-7));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new MutableInt (6), new MutableInt (7));
    CommonsTestHelper.testGetClone (new MutableInt (Integer.MAX_VALUE));
  }
}
