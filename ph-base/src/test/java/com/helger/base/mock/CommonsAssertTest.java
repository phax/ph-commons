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
package com.helger.base.mock;

import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Test class for class {@link CommonsAssert}.
 *
 * @author Philip Helger
 */
public final class CommonsAssertTest
{
  /**
   * Note: CommonsAssert signals a failure with an {@link IllegalArgumentException}, not with an
   * {@link AssertionError}.
   *
   * @param aRunnable
   *        The assertion that is expected to fail
   */
  private static void _assertFails (final Runnable aRunnable)
  {
    try
    {
      aRunnable.run ();
      fail ("The assertion should have failed");
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testFail ()
  {
    _assertFails (() -> CommonsAssert.fail ());
    _assertFails (() -> CommonsAssert.fail ("any message"));
  }

  @Test
  public void testBoolean ()
  {
    CommonsAssert.assertEquals (true, true);
    CommonsAssert.assertEquals (true, Boolean.TRUE);
    CommonsAssert.assertEquals ("msg", true, true);
    CommonsAssert.assertNotEquals (true, false);
    CommonsAssert.assertNotEquals (true, Boolean.FALSE);
    CommonsAssert.assertNotEquals ("msg", true, false);

    _assertFails (() -> CommonsAssert.assertEquals (true, false));
    _assertFails (() -> CommonsAssert.assertEquals (true, Boolean.FALSE));
    _assertFails (() -> CommonsAssert.assertEquals ("msg", true, false));
    _assertFails (() -> CommonsAssert.assertNotEquals (true, true));
    _assertFails (() -> CommonsAssert.assertNotEquals (true, Boolean.TRUE));
    _assertFails (() -> CommonsAssert.assertNotEquals ("msg", true, true));
  }

  @Test
  public void testDouble ()
  {
    CommonsAssert.assertEquals (1.0, 1.0);
    CommonsAssert.assertEquals (1.0, Double.valueOf (1.0));
    CommonsAssert.assertEquals ("msg", 1.0, 1.0);
    // Within the allowed rounding difference
    CommonsAssert.assertEquals (1.0, 1.0 + CommonsAssert.DOUBLE_ALLOWED_ROUNDING_DIFFERENCE / 2);

    _assertFails (() -> CommonsAssert.assertEquals (1.0, 2.0));
    _assertFails (() -> CommonsAssert.assertEquals (1.0, Double.valueOf (2.0)));
    _assertFails (() -> CommonsAssert.assertEquals ("msg", 1.0, 2.0));
  }

  @Test
  public void testFloat ()
  {
    CommonsAssert.assertEquals (1.0f, 1.0f);
    CommonsAssert.assertEquals (1.0f, Float.valueOf (1.0f));
    CommonsAssert.assertEquals ("msg", 1.0f, 1.0f);
    CommonsAssert.assertEquals (1.0f, 1.0f + CommonsAssert.FLOAT_ALLOWED_ROUNDING_DIFFERENCE / 2);

    _assertFails (() -> CommonsAssert.assertEquals (1.0f, 2.0f));
    _assertFails (() -> CommonsAssert.assertEquals (1.0f, Float.valueOf (2.0f)));
    _assertFails (() -> CommonsAssert.assertEquals ("msg", 1.0f, 2.0f));
  }

  @Test
  public void testObject ()
  {
    CommonsAssert.assertEquals ("a", "a");
    CommonsAssert.assertEquals ((String) null, (String) null);
    CommonsAssert.assertEquals ("msg", "a", "a");
    CommonsAssert.assertNotEquals ("a", "b");
    CommonsAssert.assertNotEquals ("msg", "a", "b");

    _assertFails (() -> CommonsAssert.assertEquals ("a", "b"));
    _assertFails (() -> CommonsAssert.assertEquals ("msg", "a", "b"));
    _assertFails (() -> CommonsAssert.assertNotEquals ("a", "a"));
    _assertFails (() -> CommonsAssert.assertNotEquals ("msg", "a", "a"));
  }

  @Test
  public void testFloatArray ()
  {
    CommonsAssert.assertEquals ((float []) null, (float []) null);
    CommonsAssert.assertEquals (new float [] { 1f, 2f }, new float [] { 1f, 2f });

    _assertFails (() -> CommonsAssert.assertEquals (new float [] { 1f }, (float []) null));
    _assertFails (() -> CommonsAssert.assertEquals ((float []) null, new float [] { 1f }));
    _assertFails (() -> CommonsAssert.assertEquals (new float [] { 1f }, new float [] { 1f, 2f }));
    _assertFails (() -> CommonsAssert.assertEquals (new float [] { 1f }, new float [] { 2f }));
  }

  @Test
  public void testDoubleArray ()
  {
    CommonsAssert.assertEquals ((double []) null, (double []) null);
    CommonsAssert.assertEquals (new double [] { 1, 2 }, new double [] { 1, 2 });

    _assertFails (() -> CommonsAssert.assertEquals (new double [] { 1 }, (double []) null));
    _assertFails (() -> CommonsAssert.assertEquals ((double []) null, new double [] { 1 }));
    _assertFails (() -> CommonsAssert.assertEquals (new double [] { 1 }, new double [] { 1, 2 }));
    _assertFails (() -> CommonsAssert.assertEquals (new double [] { 1 }, new double [] { 2 }));
  }
}
