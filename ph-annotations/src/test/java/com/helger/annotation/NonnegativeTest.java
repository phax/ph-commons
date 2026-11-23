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
package com.helger.annotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.annotation.meta.When;

/**
 * Test class for class {@link Nonnegative}.
 *
 * @author Philip Helger
 */
public final class NonnegativeTest
{
  @Nonnegative
  private int m_nZeroAllowed;

  @Nonnegative (zeroAllowed = false)
  private int m_nZeroNotAllowed;

  private static Nonnegative _get (final String sFieldName) throws NoSuchFieldException
  {
    return NonnegativeTest.class.getDeclaredField (sFieldName).getAnnotation (Nonnegative.class);
  }

  @Test
  public void testZeroAllowed () throws NoSuchFieldException
  {
    assertTrue (_get ("m_nZeroAllowed").zeroAllowed ());
    assertFalse (_get ("m_nZeroNotAllowed").zeroAllowed ());
  }

  @Test
  public void testChecker () throws NoSuchFieldException
  {
    final Nonnegative.Checker aChecker = new Nonnegative.Checker ();
    final Nonnegative aGE0 = _get ("m_nZeroAllowed");
    final Nonnegative aGT0 = _get ("m_nZeroNotAllowed");

    for (final Number aValue : new Number [] { Integer.valueOf (0),
                                               Long.valueOf (0),
                                               Double.valueOf (0),
                                               Float.valueOf (0) })
    {
      assertEquals (When.ALWAYS, aChecker.forConstantValue (aGE0, aValue));
      assertEquals (When.NEVER, aChecker.forConstantValue (aGT0, aValue));
    }
    for (final Number aValue : new Number [] { Integer.valueOf (1),
                                               Long.valueOf (1),
                                               Double.valueOf (0.5),
                                               Float.valueOf (0.5f) })
    {
      assertEquals (When.ALWAYS, aChecker.forConstantValue (aGE0, aValue));
      assertEquals (When.ALWAYS, aChecker.forConstantValue (aGT0, aValue));
    }
    for (final Number aValue : new Number [] { Integer.valueOf (-1),
                                               Long.valueOf (-1),
                                               Double.valueOf (-0.5),
                                               Float.valueOf (-0.5f) })
    {
      assertEquals (When.NEVER, aChecker.forConstantValue (aGE0, aValue));
      assertEquals (When.NEVER, aChecker.forConstantValue (aGT0, aValue));
    }
    assertEquals (When.NEVER, aChecker.forConstantValue (aGE0, "1"));
  }
}
