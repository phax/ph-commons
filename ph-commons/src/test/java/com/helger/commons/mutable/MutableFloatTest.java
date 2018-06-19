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
 * Test class for class {@link MutableFloat}.
 *
 * @author Philip Helger
 */
public final class MutableFloatTest
{
  private static final double DELTA = 0.00001;

  @Test
  public void testMutableFloat ()
  {
    final MutableFloat x = new MutableFloat (0f);
    assertEquals (0, x.floatValue (), DELTA);
    assertEquals (Float.valueOf (0), x.getAsFloat ());
    assertFalse (x.isNot0 ());
    assertTrue (x.is0 ());

    x.inc ();
    assertEquals (1, x.floatValue (), DELTA);
    assertNotEquals (x.hashCode (), x.floatValue (), DELTA);

    x.inc (5);
    assertEquals (6, x.floatValue (), DELTA);
    assertNotEquals (x.hashCode (), x.floatValue (), DELTA);

    x.inc (-2);
    assertEquals (4, x.floatValue (), DELTA);
    assertNotEquals (x.hashCode (), x.floatValue (), DELTA);

    x.dec ();
    assertEquals (3, x.floatValue (), DELTA);
    assertNotEquals (x.hashCode (), x.floatValue (), DELTA);

    x.dec (5);
    assertEquals (-2, x.floatValue (), DELTA);
    assertTrue (x.isNot0 ());
    assertFalse (x.is0 ());
    assertNotEquals (x.hashCode (), x.floatValue (), DELTA);

    assertTrue (x.set (4711).isChanged ());
    assertFalse (x.set (4711).isChanged ());
    assertEquals (4711, x.floatValue (), DELTA);

    assertEquals (-1, new MutableFloat (4).compareTo (new MutableFloat (5)));
    assertEquals (0, new MutableFloat (5).compareTo (new MutableFloat (5)));
    assertEquals (+1, new MutableFloat (6).compareTo (new MutableFloat (5)));

    assertNotNull (x.toString ());
    assertTrue (x.toString ().contains (Float.toString (x.floatValue ())));

    x.set (-1);
    assertFalse (x.isGT0 ());
    x.set (0);
    assertFalse (x.isGT0 ());
    x.set (1);
    assertTrue (x.isGT0 ());

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new MutableFloat (3.1234f),
                                                                       new MutableFloat (3.1234f));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new MutableFloat (3.1234f),
                                                                           new MutableFloat (3.123f));
    CommonsTestHelper.testGetClone (new MutableFloat (47.11f));
  }
}
