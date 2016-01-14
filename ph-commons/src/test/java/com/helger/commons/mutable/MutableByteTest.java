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
 * Test class for class {@link MutableByte}.
 *
 * @author Philip Helger
 */
public final class MutableByteTest
{
  @Test
  public void testMutableByte ()
  {
    final MutableByte x = new MutableByte ();
    assertEquals (x.byteValue (), 0);
    assertEquals (x.getAsByte (), Byte.valueOf ((byte) 0));
    assertFalse (x.isNot0 ());
    assertTrue (x.is0 ());

    x.inc ();
    assertEquals (x.byteValue (), 1);
    assertFalse (x.hashCode () == x.byteValue ());

    x.inc (5);
    assertEquals (x.byteValue (), 6);
    assertFalse (x.hashCode () == x.byteValue ());

    x.inc (-2);
    assertEquals (x.byteValue (), 4);
    assertFalse (x.hashCode () == x.byteValue ());

    x.dec ();
    assertEquals (x.byteValue (), 3);
    assertFalse (x.isEven ());
    assertFalse (x.hashCode () == x.byteValue ());

    x.dec (5);
    assertEquals (x.byteValue (), -2);
    assertTrue (x.isNot0 ());
    assertFalse (x.is0 ());
    assertTrue (x.isEven ());
    assertFalse (x.hashCode () == x.byteValue ());

    assertTrue (x.set (255).isChanged ());
    assertFalse (x.set (255).isChanged ());
    assertFalse (x.isEven ());
    assertEquals (x.byteValue (), -1);

    assertTrue (x.set (0).isChanged ());
    assertEquals (x.byteValue (), 0);

    assertTrue (x.set (127).isChanged ());
    assertEquals (x.byteValue (), 127);

    assertTrue (x.set (128).isChanged ());
    assertEquals (x.byteValue (), -128);

    assertTrue (x.set (255).isChanged ());
    assertEquals (x.byteValue (), -1);

    assertTrue (x.set (256).isChanged ());
    assertEquals (x.byteValue (), 0);

    assertTrue (x.set (257).isChanged ());
    assertEquals (x.byteValue (), 1);

    assertEquals (-1, new MutableByte (4).compareTo (new MutableByte (5)));
    assertEquals (0, new MutableByte (5).compareTo (new MutableByte (5)));
    assertEquals (+1, new MutableByte (6).compareTo (new MutableByte (5)));

    assertNotNull (x.toString ());
    assertTrue (x.toString ().contains (Integer.toString (x.byteValue ())));

    x.set (-1);
    assertFalse (x.isGreater0 ());
    x.set (0);
    assertFalse (x.isGreater0 ());
    x.set (1);
    assertTrue (x.isGreater0 ());

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new MutableByte (-7), new MutableByte (-7));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new MutableByte (6), new MutableByte (7));
    CommonsTestHelper.testGetClone (new MutableByte (Integer.MAX_VALUE));
  }
}
