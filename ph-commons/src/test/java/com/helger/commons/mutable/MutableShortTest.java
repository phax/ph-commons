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
 * Test class for class {@link MutableShort}.
 *
 * @author Philip Helger
 */
public final class MutableShortTest
{
  @Test
  public void testMutableByte ()
  {
    final MutableShort x = new MutableShort (0);
    assertEquals (0, x.shortValue ());
    assertEquals (Short.valueOf ((short) 0), x.getAsShort ());
    assertFalse (x.isNot0 ());
    assertTrue (x.is0 ());

    x.inc ();
    assertEquals (1, x.shortValue ());
    assertNotEquals (x.hashCode (), x.shortValue ());

    x.inc (5);
    assertEquals (6, x.shortValue ());
    assertNotEquals (x.hashCode (), x.shortValue ());

    x.inc (-2);
    assertEquals (4, x.shortValue ());
    assertNotEquals (x.hashCode (), x.shortValue ());

    x.dec ();
    assertEquals (3, x.shortValue ());
    assertFalse (x.isEven ());
    assertNotEquals (x.hashCode (), x.shortValue ());

    x.dec (5);
    assertEquals (-2, x.shortValue ());
    assertTrue (x.isNot0 ());
    assertFalse (x.is0 ());
    assertTrue (x.isEven ());
    assertNotEquals (x.hashCode (), x.shortValue ());

    assertTrue (x.set (255).isChanged ());
    assertFalse (x.set (255).isChanged ());
    assertFalse (x.isEven ());
    assertEquals (255, x.shortValue ());

    assertTrue (x.set (0).isChanged ());
    assertEquals (0, x.shortValue ());

    assertTrue (x.set (32767).isChanged ());
    assertEquals (32767, x.shortValue ());

    assertTrue (x.set (32768).isChanged ());
    assertEquals (-32768, x.shortValue ());

    assertTrue (x.set (65535).isChanged ());
    assertEquals (-1, x.shortValue ());

    assertTrue (x.set (65536).isChanged ());
    assertEquals (0, x.shortValue ());

    assertTrue (x.set (65537).isChanged ());
    assertEquals (1, x.shortValue ());

    assertEquals (-1, new MutableShort (4).compareTo (new MutableShort (5)));
    assertEquals (0, new MutableShort (5).compareTo (new MutableShort (5)));
    assertEquals (+1, new MutableShort (6).compareTo (new MutableShort (5)));

    assertNotNull (x.toString ());
    assertTrue (x.toString ().contains (Integer.toString (x.shortValue ())));

    x.set (-1);
    assertFalse (x.isGT0 ());
    x.set (0);
    assertFalse (x.isGT0 ());
    x.set (1);
    assertTrue (x.isGT0 ());

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new MutableShort (-7), new MutableShort (-7));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new MutableShort (6), new MutableShort (7));
    CommonsTestHelper.testGetClone (new MutableShort (Integer.MAX_VALUE));
  }
}
