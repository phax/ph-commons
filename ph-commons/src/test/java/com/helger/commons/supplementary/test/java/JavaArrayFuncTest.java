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
package com.helger.commons.supplementary.test.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Array;

import org.junit.Test;

import com.helger.commons.mock.CommonsAssert;

public final class JavaArrayFuncTest
{
  @Test
  public void testReflectionIntArray ()
  {
    final int [] aIntArray = new int [] { 1, 2, 3, 4 };
    assertEquals (4, Array.getLength (aIntArray));
    // Check type conversions from Array class
    try
    {
      Array.getBoolean (aIntArray, 0);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      Array.getByte (aIntArray, 0);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      Array.getChar (aIntArray, 0);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    CommonsAssert.assertEquals (1, Array.getDouble (aIntArray, 0));
    CommonsAssert.assertEquals (1, Array.getFloat (aIntArray, 0));
    assertEquals (1, Array.getInt (aIntArray, 0));
    assertEquals (1, Array.getLong (aIntArray, 0));
    try
    {
      Array.getShort (aIntArray, 0);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    // Test as Object
    final Object o = Array.get (aIntArray, 0);
    assertNotNull (o);
    assertTrue (o instanceof Integer);
    assertEquals (1, ((Integer) o).intValue ());
  }
}
