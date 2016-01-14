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
package com.helger.commons.collection.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.commons.factory.FactoryNewInstance;
import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link SafeArrayList}.
 *
 * @author Philip Helger
 */
public final class SafeArrayListTest
{
  @Test
  public void testSafeArrayListNullFactory ()
  {
    // create with a "null"-creating factory
    final SafeArrayList <String> sv = new SafeArrayList <String> ();
    assertEquals (sv.size (), 0);

    // no such index 0 -> create using the factory
    assertNull (sv.get (0));
    assertEquals (sv.size (), 1);

    // no such index 10 -> create using the factory
    assertNull (sv.get (10));
    assertEquals (sv.size (), 11);

    // check if all other elements are also null
    for (final String sMember : sv)
      assertNull (sMember);
  }

  @Test
  public void testSafeArrayListNewInstanceFactory ()
  {
    try
    {
      new SafeArrayList <String> (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    // create with a "null"-creating factory
    final SafeArrayList <String> sv = new SafeArrayList <String> (FactoryNewInstance.create (String.class));
    assertEquals (sv.size (), 0);

    // no such index 0 -> create using the factory
    assertNotNull (sv.get (0));
    assertEquals (sv.size (), 1);

    // no such index 10 -> create using the factory
    assertNotNull (sv.get (10));
    assertEquals (sv.size (), 11);

    // check if all other elements are also not null
    for (final String sMember : sv)
      assertNotNull (sMember);
  }

  @Test
  public void testEqualsAndHashcode ()
  {
    final SafeArrayList <String> sl = new SafeArrayList <String> ();
    sl.set (10, "any");
    assertNull (sl.get (9));

    final SafeArrayList <String> sl2 = new SafeArrayList <String> ();
    sl2.set (10, "any");

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (sl, sl2);
    sl2.set (9, "x");
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (sl, sl2);
  }
}
