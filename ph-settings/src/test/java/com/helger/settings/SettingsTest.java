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
package com.helger.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public final class SettingsTest
{
  private static final String FIELD1 = "field1";
  private static final String FIELD2 = "field2";
  private static final String FIELD3 = "field3";
  private static final String FIELD4 = "field4";
  private static final String FIELD5 = "field5";

  private void _testSettings ()
  {
    final Settings s = new Settings ("My first settings");
    assertEquals ("My first settings", s.getName ());
    assertTrue (s.keySet ().isEmpty ());

    {
      final BigDecimal v = BigDecimal.valueOf (1.1);
      assertNull (s.getValue (FIELD1));
      assertTrue (s.putIn (FIELD1, v).isChanged ());
      assertFalse (s.putIn (FIELD1, v).isChanged ());
      assertNotNull (s.getValue (FIELD1));
      assertEquals (v, s.getValue (FIELD1));
    }

    {
      final BigInteger v = BigInteger.valueOf (4711);
      assertNull (s.getValue (FIELD2));
      assertTrue (s.putIn (FIELD2, v).isChanged ());
      assertFalse (s.putIn (FIELD2, v).isChanged ());
      assertNotNull (s.getValue (FIELD2));
      assertEquals (v, s.getValue (FIELD2));
    }

    {
      final String v = "Hallo settings";
      assertNull (s.getValue (FIELD3));
      assertTrue (s.putIn (FIELD3, v).isChanged ());
      assertFalse (s.putIn (FIELD3, v).isChanged ());
      assertNotNull (s.getValue (FIELD3));
      assertEquals (v, s.getValue (FIELD3));
    }

    assertTrue (s.containsKey (FIELD1));
    assertTrue (s.containsKey (FIELD2));
    assertTrue (s.containsKey (FIELD3));
    assertFalse (s.containsKey (FIELD4));
    assertFalse (s.containsKey (FIELD5));

    assertTrue (s.putIn (FIELD4, Boolean.TRUE).isChanged ());
    assertTrue (s.putIn (FIELD5, Boolean.TRUE).isChanged ());

    assertEquals (5, s.keySet ().size ());
    assertTrue (s.containsKey (FIELD1));
    assertTrue (s.containsKey (FIELD2));
    assertTrue (s.containsKey (FIELD3));
    assertTrue (s.containsKey (FIELD4));
    assertTrue (s.containsKey (FIELD5));
    assertTrue (s.keySet ().contains (FIELD1));
    assertTrue (s.keySet ().contains (FIELD2));
    assertTrue (s.keySet ().contains (FIELD3));
    assertTrue (s.keySet ().contains (FIELD4));
    assertTrue (s.keySet ().contains (FIELD5));
  }

  @Test
  public void testSettingsImpl ()
  {
    _testSettings ();
  }

  @SuppressWarnings ("unused")
  @Test
  @SuppressFBWarnings ("NP_NONNULL_PARAM_VIOLATION")
  public void testIllegal ()
  {
    try
    {
      // null name not allowed
      new Settings ((String) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // empty name not allowed
      new Settings ("");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      // illegal field name
      new Settings ("xxx").putIn (null, "my value");
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // illegal field name
      new Settings ("xxx").putIn ("", "my value");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testStdMethods ()
  {
    final Settings s = new Settings ("s1");
    assertTrue (s.putIn (FIELD1, "My value").isChanged ());

    {
      final Settings t = new Settings ("s1");
      assertTrue (t.putIn (FIELD1, "My value").isChanged ());
      CommonsTestHelper.testDefaultImplementationWithEqualContentObject (s, t);
    }

    {
      final Settings t = new Settings ("s1");
      t.putIn (FIELD1, "My other value");
      CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (s, t);
    }

    {
      final Settings t = new Settings ("other name");
      t.putIn (FIELD1, "My value");
      CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (s, t);
    }
  }

  @Test
  public void testNull ()
  {
    final Settings s = new Settings ("s1");
    assertTrue (s.putIn (FIELD1, "My value").isChanged ());
    assertTrue (s.keySet ().contains (FIELD1));
    assertFalse (s.keySet ().contains (FIELD2));

    assertFalse (s.putIn (FIELD2, null).isChanged ());
    assertTrue (s.keySet ().contains (FIELD1));
    assertTrue (s.keySet ().contains (FIELD2));
  }

  @Test
  public void testCopy ()
  {
    final Settings s = new Settings ("s1");
    assertTrue (s.putIn (FIELD1, "My value").isChanged ());

    final Settings s2 = new Settings (s);
    assertTrue (s2.keySet ().contains (FIELD1));
    assertFalse (s2.keySet ().contains (FIELD2));

    assertTrue (s2.putIn (FIELD2, "Other value").isChanged ());
    assertTrue (s.keySet ().contains (FIELD1));
    assertFalse (s.keySet ().contains (FIELD2));
    assertTrue (s2.keySet ().contains (FIELD1));
    assertTrue (s2.keySet ().contains (FIELD2));
  }
}
