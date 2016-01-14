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
package com.helger.commons.collection.iterate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.NoSuchElementException;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link ArrayEnumeration}.
 *
 * @author Philip Helger
 */
public final class ArrayEnumerationTest
{
  @Test
  @SuppressFBWarnings ("TQ_NEVER_VALUE_USED_WHERE_ALWAYS_REQUIRED")
  public void testAll ()
  {
    ArrayEnumeration <String> ae = new ArrayEnumeration <String> ("Hallo", "Welt", "from", "Copenhagen");
    for (int i = 0; i < 10; ++i)
      assertTrue (ae.hasMoreElements ());
    assertEquals ("Hallo", ae.nextElement ());
    for (int i = 0; i < 10; ++i)
      assertTrue (ae.hasMoreElements ());
    assertEquals ("Welt", ae.nextElement ());
    for (int i = 0; i < 10; ++i)
      assertTrue (ae.hasMoreElements ());
    assertEquals ("from", ae.nextElement ());
    for (int i = 0; i < 10; ++i)
      assertTrue (ae.hasMoreElements ());
    assertEquals ("Copenhagen", ae.nextElement ());
    for (int i = 0; i < 10; ++i)
      assertFalse (ae.hasMoreElements ());
    assertNotNull (ae.toString ());

    try
    {
      ae.nextElement ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {}

    try
    {
      new ArrayEnumeration <String> ((String []) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      new ArrayEnumeration <String> ((String []) null, -1, 5);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      new ArrayEnumeration <String> (new String [] { "x", "y" }, -1, 5);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      new ArrayEnumeration <String> (new String [] { "x", "y" }, 0, -1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      new ArrayEnumeration <String> (new String [] { "x", "y" }, 5, 2);
      fail ();
    }
    catch (final ArrayIndexOutOfBoundsException ex)
    {}

    // equals...
    ae = new ArrayEnumeration <String> ("Hallo", "Welt", "from", "Copenhagen");
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (ae,
                                                                       new ArrayEnumeration <String> ("Hallo",
                                                                                                      "Welt",
                                                                                                      "from",
                                                                                                      "Copenhagen"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (ae,
                                                                           new ArrayEnumeration <String> ("Hallo",
                                                                                                          "Welt",
                                                                                                          "from"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (ae,
                                                                           new ArrayEnumeration <Integer> (Integer.valueOf (5)));
    // Different index
    final ArrayEnumeration <String> ae2 = new ArrayEnumeration <String> ("Hallo", "Welt", "from", "Copenhagen");
    ae2.nextElement ();
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (ae, ae2);
  }
}
