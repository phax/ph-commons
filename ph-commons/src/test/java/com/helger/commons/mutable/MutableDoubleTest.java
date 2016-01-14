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
 * Test class for class {@link MutableDouble}.
 *
 * @author Philip Helger
 */
public final class MutableDoubleTest
{
  private static final double DELTA = 0.00001;

  @Test
  public void testMutableDouble ()
  {
    final MutableDouble x = new MutableDouble ();
    assertEquals (x.doubleValue (), 0, DELTA);
    assertEquals (x.getAsDouble (), Double.valueOf (0));
    assertFalse (x.isNot0 ());
    assertTrue (x.is0 ());

    x.inc ();
    assertEquals (x.doubleValue (), 1, DELTA);
    assertFalse (x.hashCode () == x.doubleValue ());

    x.inc (5);
    assertEquals (x.doubleValue (), 6, DELTA);
    assertFalse (x.hashCode () == x.doubleValue ());

    x.inc (-2);
    assertEquals (x.doubleValue (), 4, DELTA);
    assertFalse (x.hashCode () == x.doubleValue ());

    x.dec ();
    assertEquals (x.doubleValue (), 3, DELTA);
    assertFalse (x.hashCode () == x.doubleValue ());

    x.dec (5);
    assertEquals (x.doubleValue (), -2, DELTA);
    assertTrue (x.isNot0 ());
    assertFalse (x.is0 ());
    assertFalse (x.hashCode () == x.doubleValue ());

    assertTrue (x.set (4711).isChanged ());
    assertFalse (x.set (4711).isChanged ());
    assertEquals (x.doubleValue (), 4711, DELTA);

    assertEquals (-1, new MutableDouble (4).compareTo (new MutableDouble (5)));
    assertEquals (0, new MutableDouble (5).compareTo (new MutableDouble (5)));
    assertEquals (+1, new MutableDouble (6).compareTo (new MutableDouble (5)));

    assertNotNull (x.toString ());
    assertTrue (x.toString ().contains (Double.toString (x.doubleValue ())));

    x.set (-1);
    assertFalse (x.isGreater0 ());
    x.set (0);
    assertFalse (x.isGreater0 ());
    x.set (1);
    assertTrue (x.isGreater0 ());

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new MutableDouble (3.1234),
                                                                       new MutableDouble (3.1234));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new MutableDouble (3.1234),
                                                                           new MutableDouble (3.123));
    CommonsTestHelper.testGetClone (new MutableDouble (47.11));
  }
}
