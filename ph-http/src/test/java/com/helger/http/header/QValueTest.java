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
package com.helger.http.header;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.base.mock.CommonsAssert;

/**
 * Test class for class {@link QValue}.
 *
 * @author Philip Helger
 */
public final class QValueTest
{
  @Test
  public void testBasic ()
  {
    final QValue aQ = new QValue (0.3);
    CommonsAssert.assertEquals (0.3, aQ.getQuality ());
    assertNotNull (aQ.toString ());

    assertEquals (aQ, aQ);
    assertEquals (aQ, new QValue (0.3));
    assertEquals (aQ.hashCode (), new QValue (0.3).hashCode ());
    assertNotEquals (aQ, null);
    assertNotEquals (aQ, "any other type");
    assertNotEquals (aQ, new QValue (0.4));
  }

  @Test
  public void testValueInRange ()
  {
    CommonsAssert.assertEquals (QValue.MIN_QUALITY, QValue.getValueInRange (-1));
    CommonsAssert.assertEquals (QValue.MAX_QUALITY, QValue.getValueInRange (2));
    CommonsAssert.assertEquals (0.5, QValue.getValueInRange (0.5));

    // Values out of range are cut
    CommonsAssert.assertEquals (QValue.MIN_QUALITY, new QValue (-1).getQuality ());
    CommonsAssert.assertEquals (QValue.MAX_QUALITY, new QValue (2).getQuality ());
  }

  @Test
  public void testStates ()
  {
    assertTrue (QValue.MIN_QVALUE.isMinimumQuality ());
    assertFalse (QValue.MIN_QVALUE.isAboveMinimumQuality ());
    assertTrue (QValue.MIN_QVALUE.isLowValue ());
    assertFalse (QValue.MIN_QVALUE.isHighValue ());
    assertTrue (QValue.MIN_QVALUE.isBelowMaximumQuality ());
    assertFalse (QValue.MIN_QVALUE.isMaximumQuality ());
    assertFalse (QValue.MIN_QVALUE.isBetweenMinimumAndMaximum ());

    assertFalse (QValue.MAX_QVALUE.isMinimumQuality ());
    assertTrue (QValue.MAX_QVALUE.isAboveMinimumQuality ());
    assertFalse (QValue.MAX_QVALUE.isLowValue ());
    assertTrue (QValue.MAX_QVALUE.isHighValue ());
    assertFalse (QValue.MAX_QVALUE.isBelowMaximumQuality ());
    assertTrue (QValue.MAX_QVALUE.isMaximumQuality ());
    assertFalse (QValue.MAX_QVALUE.isBetweenMinimumAndMaximum ());

    final QValue aHalf = new QValue (QValue.HALF_QUALITY);
    assertFalse (aHalf.isMinimumQuality ());
    assertTrue (aHalf.isAboveMinimumQuality ());
    assertTrue (aHalf.isLowValue ());
    assertFalse (aHalf.isHighValue ());
    assertTrue (aHalf.isBelowMaximumQuality ());
    assertFalse (aHalf.isMaximumQuality ());
    assertTrue (aHalf.isBetweenMinimumAndMaximum ());
  }

  @Test
  public void testCompareTo ()
  {
    assertTrue (QValue.MIN_QVALUE.compareTo (QValue.MAX_QVALUE) < 0);
    assertTrue (QValue.MAX_QVALUE.compareTo (QValue.MIN_QVALUE) > 0);
    assertEquals (0, QValue.MAX_QVALUE.compareTo (new QValue (QValue.MAX_QUALITY)));
  }
}
