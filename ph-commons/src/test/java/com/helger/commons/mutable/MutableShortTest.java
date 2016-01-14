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
 * Test class for class {@link MutableShort}.
 *
 * @author Philip Helger
 */
public final class MutableShortTest
{
  @Test
  public void testMutableByte ()
  {
    final MutableShort x = new MutableShort ();
    assertEquals (x.shortValue (), 0);
    assertEquals (x.getAsShort (), Short.valueOf ((short) 0));
    assertFalse (x.isNot0 ());
    assertTrue (x.is0 ());

    x.inc ();
    assertEquals (x.shortValue (), 1);
    assertFalse (x.hashCode () == x.shortValue ());

    x.inc (5);
    assertEquals (x.shortValue (), 6);
    assertFalse (x.hashCode () == x.shortValue ());

    x.inc (-2);
    assertEquals (x.shortValue (), 4);
    assertFalse (x.hashCode () == x.shortValue ());

    x.dec ();
    assertEquals (x.shortValue (), 3);
    assertFalse (x.isEven ());
    assertFalse (x.hashCode () == x.shortValue ());

    x.dec (5);
    assertEquals (x.shortValue (), -2);
    assertTrue (x.isNot0 ());
    assertFalse (x.is0 ());
    assertTrue (x.isEven ());
    assertFalse (x.hashCode () == x.shortValue ());

    assertTrue (x.set (255).isChanged ());
    assertFalse (x.set (255).isChanged ());
    assertFalse (x.isEven ());
    assertEquals (x.shortValue (), 255);

    assertTrue (x.set (0).isChanged ());
    assertEquals (x.shortValue (), 0);

    assertTrue (x.set (32767).isChanged ());
    assertEquals (x.shortValue (), 32767);

    assertTrue (x.set (32768).isChanged ());
    assertEquals (x.shortValue (), -32768);

    assertTrue (x.set (65535).isChanged ());
    assertEquals (x.shortValue (), -1);

    assertTrue (x.set (65536).isChanged ());
    assertEquals (x.shortValue (), 0);

    assertTrue (x.set (65537).isChanged ());
    assertEquals (x.shortValue (), 1);

    assertEquals (-1, new MutableShort (4).compareTo (new MutableShort (5)));
    assertEquals (0, new MutableShort (5).compareTo (new MutableShort (5)));
    assertEquals (+1, new MutableShort (6).compareTo (new MutableShort (5)));

    assertNotNull (x.toString ());
    assertTrue (x.toString ().contains (Integer.toString (x.shortValue ())));

    x.set (-1);
    assertFalse (x.isGreater0 ());
    x.set (0);
    assertFalse (x.isGreater0 ());
    x.set (1);
    assertTrue (x.isGreater0 ());

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new MutableShort (-7), new MutableShort (-7));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new MutableShort (6), new MutableShort (7));
    CommonsTestHelper.testGetClone (new MutableShort (Integer.MAX_VALUE));
  }
}
