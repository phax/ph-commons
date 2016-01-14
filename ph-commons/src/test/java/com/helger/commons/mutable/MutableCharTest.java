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
 * Test class for class {@link MutableChar}.
 *
 * @author Philip Helger
 */
public final class MutableCharTest
{
  @Test
  public void testMutableChar ()
  {
    final MutableChar x = new MutableChar ();
    assertEquals (x.charValue (), 0);
    assertEquals (x.getAsCharacter (), Character.valueOf ((char) 0));
    assertFalse (x.isNot0 ());
    assertTrue (x.is0 ());

    x.inc ();
    assertEquals (x.charValue (), 1);
    assertFalse (x.hashCode () == x.charValue ());

    x.inc (5);
    assertEquals (x.charValue (), 6);
    assertFalse (x.hashCode () == x.charValue ());

    x.inc (-2);
    assertEquals (x.charValue (), 4);
    assertFalse (x.hashCode () == x.charValue ());

    x.dec ();
    assertEquals (x.charValue (), 3);
    assertFalse (x.isEven ());
    assertFalse (x.hashCode () == x.charValue ());

    x.dec (2);
    assertEquals (x.charValue (), 1);
    assertTrue (x.isNot0 ());
    assertFalse (x.is0 ());
    assertFalse (x.isEven ());
    assertFalse (x.hashCode () == x.charValue ());

    assertTrue (x.set (65535).isChanged ());
    assertFalse (x.set (65535).isChanged ());
    assertFalse (x.isEven ());
    assertEquals (x.charValue (), 65535);

    assertTrue (x.set (65536).isChanged ());
    assertTrue (x.isEven ());
    assertEquals (x.charValue (), 0);

    assertTrue (x.set (65537).isChanged ());
    assertFalse (x.isEven ());
    assertEquals (x.charValue (), 1);

    assertTrue (x.set (0).isChanged ());
    assertEquals (x.charValue (), 0);

    assertTrue (x.set (1).isChanged ());
    assertEquals (x.charValue (), 1);

    assertTrue (x.set (-1).isChanged ());
    assertEquals (x.charValue (), 65535);

    assertTrue (x.set (-2).isChanged ());
    assertEquals (x.charValue (), 65534);

    assertEquals (-1, new MutableChar (4).compareTo (new MutableChar (5)));
    assertEquals (0, new MutableChar (5).compareTo (new MutableChar (5)));
    assertEquals (+1, new MutableChar (6).compareTo (new MutableChar (5)));

    assertNotNull (x.toString ());
    assertTrue (x.toString ().contains (Character.toString (x.charValue ())));

    x.set (-1);
    assertTrue (x.isGreater0 ());
    x.set (0);
    assertFalse (x.isGreater0 ());
    x.set (1);
    assertTrue (x.isGreater0 ());

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new MutableChar (-7), new MutableChar (-7));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new MutableChar (6), new MutableChar (7));
    CommonsTestHelper.testGetClone (new MutableChar (Integer.MAX_VALUE));
  }
}
