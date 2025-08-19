/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.base.BaseTestHelper;

/**
 * Test class for class {@link MutableLong}.
 *
 * @author Philip Helger
 */
public final class MutableLongTest
{
  @Test
  public void testMutableLong ()
  {
    final MutableLong x = new MutableLong (0);
    assertEquals (0, x.longValue ());
    assertEquals (Long.valueOf (0), x.getAsLong ());
    assertFalse (x.isNot0 ());
    assertTrue (x.is0 ());

    x.inc ();
    assertEquals (1, x.longValue ());
    assertNotEquals (x.hashCode (), x.longValue ());

    x.inc (5);
    assertEquals (6, x.longValue ());
    assertNotEquals (x.hashCode (), x.longValue ());

    x.inc (-2);
    assertEquals (4, x.longValue ());
    assertNotEquals (x.hashCode (), x.longValue ());

    x.dec ();
    assertEquals (3, x.longValue ());
    assertFalse (x.isEven ());
    assertNotEquals (x.hashCode (), x.longValue ());

    x.dec (5);
    assertEquals (-2, x.longValue ());
    assertTrue (x.isNot0 ());
    assertFalse (x.is0 ());
    assertTrue (x.isEven ());
    assertNotEquals (x.hashCode (), x.longValue ());

    assertTrue (x.set (4711).isChanged ());
    assertFalse (x.set (4711).isChanged ());
    assertFalse (x.isEven ());
    assertEquals (4711, x.longValue ());

    assertEquals (4711, x.getAndInc ());
    assertEquals (4712, x.shortValue ());
    assertEquals (4713, x.incAndGet ());
    assertEquals (4713, x.shortValue ());

    assertEquals (-1, new MutableLong (4).compareTo (new MutableLong (5)));
    assertEquals (0, new MutableLong (5).compareTo (new MutableLong (5)));
    assertEquals (+1, new MutableLong (6).compareTo (new MutableLong (5)));

    assertNotNull (x.toString ());
    assertTrue (x.toString ().contains (Long.toString (x.longValue ())));

    x.set (-1);
    assertFalse (x.isGT0 ());
    x.set (0);
    assertFalse (x.isGT0 ());
    x.set (1);
    assertTrue (x.isGT0 ());

    BaseTestHelper.testDefaultImplementationWithEqualContentObject (new MutableLong (-7000), new MutableLong (-7000));
    BaseTestHelper.testDefaultImplementationWithDifferentContentObject (new MutableLong (600), new MutableLong (700));
    BaseTestHelper.testGetClone (new MutableLong (Long.MIN_VALUE));
  }
}
