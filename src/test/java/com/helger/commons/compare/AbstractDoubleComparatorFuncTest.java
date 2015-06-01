/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.compare;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;

import com.helger.commons.CGlobal;
import com.helger.commons.collections.CollectionHelper;
import com.helger.commons.mock.PHAssert;

/**
 * Test class for {@link AbstractDoubleComparator}
 *
 * @author Philip Helger
 */
public final class AbstractDoubleComparatorFuncTest
{
  @NotThreadSafe
  private static final class MockComparator extends AbstractDoubleComparator <Double>
  {
    MockComparator ()
    {}

    MockComparator (final ESortOrder eSortOrder)
    {
      super (eSortOrder);
    }

    @Override
    protected double asDouble (@Nullable final Double aValue)
    {
      return aValue == null ? CGlobal.ILLEGAL_DOUBLE : aValue.doubleValue ();
    }
  }

  @Test
  public void testAll ()
  {
    final Double [] x = new Double [] { Double.valueOf (3),
                                       Double.valueOf (3),
                                       Double.valueOf (-56),
                                       Double.valueOf (1) };

    // default: sort ascending
    List <Double> l = CollectionHelper.getSorted (x, new MockComparator ());
    assertNotNull (l);
    PHAssert.assertEquals (-56, l.get (0).doubleValue ());
    PHAssert.assertEquals (1, l.get (1).doubleValue ());
    PHAssert.assertEquals (3, l.get (2).doubleValue ());
    PHAssert.assertEquals (3, l.get (3).doubleValue ());

    // Explicitly sort ascending
    l = CollectionHelper.getSorted (x, new MockComparator (ESortOrder.ASCENDING));
    assertNotNull (l);
    PHAssert.assertEquals (-56, l.get (0).doubleValue ());
    PHAssert.assertEquals (1, l.get (1).doubleValue ());
    PHAssert.assertEquals (3, l.get (2).doubleValue ());
    PHAssert.assertEquals (3, l.get (3).doubleValue ());

    // Explicitly sort descending
    l = CollectionHelper.getSorted (x, new MockComparator (ESortOrder.DESCENDING));
    assertNotNull (l);
    PHAssert.assertEquals (3, l.get (0).doubleValue ());
    PHAssert.assertEquals (3, l.get (1).doubleValue ());
    PHAssert.assertEquals (1, l.get (2).doubleValue ());
    PHAssert.assertEquals (-56, l.get (3).doubleValue ());

    // change dynamically
    final MockComparator c = new MockComparator (ESortOrder.ASCENDING);
    l = CollectionHelper.getSorted (x, c);
    PHAssert.assertEquals (-56, l.get (0).doubleValue ());
    PHAssert.assertEquals (1, l.get (1).doubleValue ());
    PHAssert.assertEquals (3, l.get (2).doubleValue ());
    PHAssert.assertEquals (3, l.get (3).doubleValue ());

    // change to descending
    l = CollectionHelper.getSorted (x, c.setSortOrder (ESortOrder.DESCENDING));
    PHAssert.assertEquals (3, l.get (0).doubleValue ());
    PHAssert.assertEquals (3, l.get (1).doubleValue ());
    PHAssert.assertEquals (1, l.get (2).doubleValue ());
    PHAssert.assertEquals (-56, l.get (3).doubleValue ());
  }

  /**
   * Test for method isAscending
   */
  @Test
  public void testIsAscending ()
  {
    assertTrue (new MockComparator ().getSortOrder ().isAscending ());
    assertFalse (new MockComparator (ESortOrder.DESCENDING).getSortOrder ().isAscending ());
  }
}
