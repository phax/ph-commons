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
package com.helger.commons.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.helger.base.clone.CloneHelper;

/**
 * Test class for class {@link CloneHelper}.
 *
 * @author Philip Helger
 */
public final class CloneHelperTest
{
  @Test
  public void testGetClonedValue () throws CloneNotSupportedException
  {
    assertNull (CloneHelper.getClonedValue (null));
    assertEquals ("Hallo", CloneHelper.getClonedValue ("Hallo"));
    assertEquals (Double.valueOf (3.1234), CloneHelper.getClonedValue (Double.valueOf (3.1234)));

    // java.lang.Cloneable
    final MockCloneable d0 = new MockCloneable ();
    assertEquals (0, d0.getI ());
    assertEquals (1, d0.clone ().getI ());
    assertEquals (2, d0.clone ().clone ().getI ());
    assertEquals (1, CloneHelper.getClonedValue (d0).getI ());

    // ICloneable
    final MockICloneable d1 = new MockICloneable ();
    assertEquals (0, d1.getI ());
    assertEquals (1, d1.getClone ().getI ());
    assertEquals (2, d1.getClone ().getClone ().getI ());
    assertEquals (1, CloneHelper.getClonedValue (d1).getI ());

    final MockWithCopyCtor d2 = new MockWithCopyCtor ();
    // If you get a NullPointerException here, the test class is not in the same
    // package as the class to test!
    assertEquals (1, CloneHelper.getClonedValue (d2).getI ());

    final MockWithPrivateCopyCtor d3 = new MockWithPrivateCopyCtor ();
    assertNull (CloneHelper.getClonedValue (d3));

    final MockNoPublicCtor d4 = MockNoPublicCtor.getInstance ();
    assertNull (CloneHelper.getClonedValue (d4));

    final MockCloneNotSupported d5 = new MockCloneNotSupported ();
    assertNull (CloneHelper.getClonedValue (d5));

    // Clone the most basic object :)
    assertNull (CloneHelper.getClonedValue (new Object ()));
  }
}
