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
package com.helger.commons.compare;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.mock.CommonsAssert;

/**
 * Test class for {@link DoubleComparator}
 *
 * @author Philip Helger
 */
public final class AbstractDoubleComparatorFuncTest
{
  @NotThreadSafe
  private static final class MockComparator extends DoubleComparator <Double>
  {
    MockComparator ()
    {
      super (aObject -> aObject.doubleValue ());
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
    CommonsAssert.assertEquals (-56, l.get (0).doubleValue ());
    CommonsAssert.assertEquals (1, l.get (1).doubleValue ());
    CommonsAssert.assertEquals (3, l.get (2).doubleValue ());
    CommonsAssert.assertEquals (3, l.get (3).doubleValue ());

    // Explicitly sort ascending
    l = CollectionHelper.getSorted (x, new MockComparator ().setSortOrder (ESortOrder.ASCENDING));
    assertNotNull (l);
    CommonsAssert.assertEquals (-56, l.get (0).doubleValue ());
    CommonsAssert.assertEquals (1, l.get (1).doubleValue ());
    CommonsAssert.assertEquals (3, l.get (2).doubleValue ());
    CommonsAssert.assertEquals (3, l.get (3).doubleValue ());

    // Explicitly sort descending
    l = CollectionHelper.getSorted (x, new MockComparator ().setSortOrder (ESortOrder.DESCENDING));
    assertNotNull (l);
    CommonsAssert.assertEquals (3, l.get (0).doubleValue ());
    CommonsAssert.assertEquals (3, l.get (1).doubleValue ());
    CommonsAssert.assertEquals (1, l.get (2).doubleValue ());
    CommonsAssert.assertEquals (-56, l.get (3).doubleValue ());

    // change dynamically
    final AbstractComparator <Double> c = new MockComparator ().setSortOrder (ESortOrder.ASCENDING);
    l = CollectionHelper.getSorted (x, c);
    CommonsAssert.assertEquals (-56, l.get (0).doubleValue ());
    CommonsAssert.assertEquals (1, l.get (1).doubleValue ());
    CommonsAssert.assertEquals (3, l.get (2).doubleValue ());
    CommonsAssert.assertEquals (3, l.get (3).doubleValue ());

    // change to descending
    l = CollectionHelper.getSorted (x, c.setSortOrder (ESortOrder.DESCENDING));
    CommonsAssert.assertEquals (3, l.get (0).doubleValue ());
    CommonsAssert.assertEquals (3, l.get (1).doubleValue ());
    CommonsAssert.assertEquals (1, l.get (2).doubleValue ());
    CommonsAssert.assertEquals (-56, l.get (3).doubleValue ());
  }

  /**
   * Test for method isAscending
   */
  @Test
  public void testIsAscending ()
  {
    assertTrue (new MockComparator ().getSortOrder ().isAscending ());
    assertFalse (new MockComparator ().setSortOrder (ESortOrder.DESCENDING).getSortOrder ().isAscending ());
  }
}
