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
package com.helger.base.numeric.mutable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.base.state.EChange;

/**
 * Additional test class for the {@link Number} based overloads and the comparison helpers of the
 * mutable numeric types.
 *
 * @author Philip Helger
 */
public final class MutableNumericExtTest
{
  @Test
  public void testMutableIntNumberOverloads ()
  {
    final MutableInt a = new MutableInt (Integer.valueOf (10));
    assertEquals (10, a.intValue ());
    assertEquals (10f, a.floatValue (), 0.0001f);
    assertEquals (10d, a.doubleValue (), 0.0001);
    assertEquals (10L, a.longValue ());

    assertEquals (11, a.inc ());
    assertEquals (16, a.inc (Integer.valueOf (5)));
    assertEquals (15, a.dec ());
    assertEquals (10, a.dec (Integer.valueOf (5)));
    assertEquals (5, a.divide (Integer.valueOf (2)));
    assertEquals (10, a.multiply (Integer.valueOf (2)));

    assertSame (EChange.CHANGED, a.set (Integer.valueOf (20)));
    assertSame (EChange.UNCHANGED, a.set (Integer.valueOf (20)));
    assertEquals (20, a.intValue ());

    assertTrue (a.isEven ());
    assertFalse (a.isOdd ());
    assertNotNull (a.toString ());
  }

  @Test
  public void testMutableIntComparisons ()
  {
    assertTrue (new MutableInt (0).is0 ());
    assertTrue (new MutableInt (1).isNot0 ());
    assertTrue (new MutableInt (-1).isLT0 ());
    assertTrue (new MutableInt (0).isLE0 ());
    assertTrue (new MutableInt (1).isGT0 ());
    assertTrue (new MutableInt (0).isGE0 ());
    assertFalse (new MutableInt (1).is0 ());
    assertFalse (new MutableInt (0).isGT0 ());

    final MutableInt a = new MutableInt (5);
    assertEquals (5, a.getAndInc ());
    assertEquals (7, a.incAndGet ());
    assertEquals (0, a.compareTo (new MutableInt (7)));
    assertNotNull (a.getClone ());
  }

  @Test
  public void testMutableLongNumberOverloads ()
  {
    final MutableLong a = new MutableLong (Long.valueOf (10));
    assertEquals (10, a.intValue ());
    assertEquals (10f, a.floatValue (), 0.0001f);
    assertEquals (10d, a.doubleValue (), 0.0001);
    assertEquals (10L, a.longValue ());

    assertEquals (11L, a.inc ());
    assertEquals (16L, a.inc (Long.valueOf (5)));
    assertEquals (15L, a.dec ());
    assertEquals (10L, a.dec (Long.valueOf (5)));
    assertEquals (5L, a.divide (Long.valueOf (2)));
    assertEquals (10L, a.multiply (Long.valueOf (2)));

    assertSame (EChange.CHANGED, a.set (Long.valueOf (20)));
    assertSame (EChange.UNCHANGED, a.set (Long.valueOf (20)));
    assertTrue (a.isEven ());

    assertEquals (20L, a.getAndInc ());
    assertEquals (22L, a.incAndGet ());
    assertNotNull (a.getClone ());
  }

  @Test
  public void testMutableDoubleNumberOverloads ()
  {
    final MutableDouble a = new MutableDouble (Double.valueOf (10));
    assertEquals (10, a.intValue ());
    assertEquals (10f, a.floatValue (), 0.0001f);
    assertEquals (10d, a.doubleValue (), 0.0001);
    assertEquals (10L, a.longValue ());

    assertEquals (11d, a.inc (), 0.0001);
    assertEquals (16d, a.inc (Double.valueOf (5)), 0.0001);
    assertEquals (15d, a.dec (), 0.0001);
    assertEquals (10d, a.dec (Double.valueOf (5)), 0.0001);
    assertEquals (5d, a.divide (Double.valueOf (2)), 0.0001);
    assertEquals (10d, a.multiply (Double.valueOf (2)), 0.0001);

    assertSame (EChange.CHANGED, a.set (Double.valueOf (20)));
    assertSame (EChange.UNCHANGED, a.set (Double.valueOf (20)));

    assertTrue (new MutableDouble (0).is0 ());
    assertTrue (new MutableDouble (-1).isLT0 ());
    assertTrue (new MutableDouble (0).isLE0 ());
    assertTrue (new MutableDouble (1).isGT0 ());
    assertTrue (new MutableDouble (0).isGE0 ());
    assertNotNull (a.getClone ());
  }

  @Test
  public void testMutableFloatNumberOverloads ()
  {
    final MutableFloat a = new MutableFloat (Float.valueOf (10));
    assertEquals (10, a.intValue ());
    assertEquals (10f, a.floatValue (), 0.0001f);
    assertEquals (10d, a.doubleValue (), 0.0001);
    assertEquals (10L, a.longValue ());

    assertEquals (11f, a.inc (), 0.0001f);
    assertEquals (16f, a.inc (Float.valueOf (5)), 0.0001f);
    assertEquals (15f, a.dec (), 0.0001f);
    assertEquals (10f, a.dec (Float.valueOf (5)), 0.0001f);
    assertEquals (5f, a.divide (Float.valueOf (2)), 0.0001f);
    assertEquals (10f, a.multiply (Float.valueOf (2)), 0.0001f);

    assertSame (EChange.CHANGED, a.set (Float.valueOf (20)));
    assertSame (EChange.UNCHANGED, a.set (Float.valueOf (20)));

    assertTrue (new MutableFloat (0).is0 ());
    assertTrue (new MutableFloat (-1).isLT0 ());
    assertTrue (new MutableFloat (0).isLE0 ());
    assertTrue (new MutableFloat (1).isGT0 ());
    assertTrue (new MutableFloat (0).isGE0 ());
    assertNotNull (a.getClone ());
  }

  @Test
  public void testMutableByteAndShort ()
  {
    final MutableByte aByte = new MutableByte (Byte.valueOf ((byte) 10));
    assertEquals (10, aByte.intValue ());
    assertEquals (11, aByte.inc ());
    assertEquals (10, aByte.dec ());
    assertSame (EChange.CHANGED, aByte.set (Byte.valueOf ((byte) 20)));
    assertTrue (aByte.isEven ());
    assertTrue (new MutableByte ((byte) 0).is0 ());
    assertNotNull (aByte.getClone ());

    final MutableShort aShort = new MutableShort (Short.valueOf ((short) 10));
    assertEquals (10, aShort.intValue ());
    assertEquals (11, aShort.inc ());
    assertEquals (10, aShort.dec ());
    assertSame (EChange.CHANGED, aShort.set (Short.valueOf ((short) 20)));
    assertTrue (aShort.isEven ());
    assertTrue (new MutableShort ((short) 0).is0 ());
    assertNotNull (aShort.getClone ());
  }

  @Test
  public void testMutableCharAndBoolean ()
  {
    final MutableChar aChar = new MutableChar ('a');
    assertEquals ('a', aChar.charValue ());
    assertEquals ('b', aChar.inc ());
    assertEquals ('a', aChar.dec ());
    assertSame (EChange.CHANGED, aChar.set ('z'));
    assertSame (EChange.UNCHANGED, aChar.set ('z'));
    assertNotNull (aChar.getClone ());
    assertNotNull (aChar.toString ());

    final MutableBoolean aBool = new MutableBoolean (false);
    assertFalse (aBool.booleanValue ());
    assertSame (EChange.CHANGED, aBool.set (true));
    assertSame (EChange.UNCHANGED, aBool.set (true));
    assertTrue (aBool.booleanValue ());
    assertNotNull (aBool.getClone ());
    assertNotNull (aBool.toString ());

    // "false" sorts before "true", like Boolean.compareTo
    assertTrue (new MutableBoolean (false).compareTo (new MutableBoolean (true)) < 0);
    assertTrue (new MutableBoolean (true).compareTo (new MutableBoolean (false)) > 0);
    assertEquals (0, new MutableBoolean (true).compareTo (new MutableBoolean (true)));
    assertEquals (0, new MutableBoolean (false).compareTo (new MutableBoolean (false)));
  }

  @Test
  public void testInvalidParams ()
  {
    try
    {
      new MutableInt ((Number) null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }
}
