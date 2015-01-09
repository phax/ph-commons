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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.annotation.Nullable;

import org.junit.Test;

import com.helger.commons.CGlobal;
import com.helger.commons.collections.ContainerHelper;

/**
 * Test class for {@link AbstractIntegerComparator}
 *
 * @author Philip Helger
 */
public final class AbstractIntegerComparatorTest
{
  private static final class ComparatorMockNumeric extends AbstractIntegerComparator <Integer>
  {
    ComparatorMockNumeric ()
    {}

    ComparatorMockNumeric (final ESortOrder eSortOrder)
    {
      super (eSortOrder);
    }

    @Override
    protected long asLong (@Nullable final Integer aInt)
    {
      return aInt == null ? CGlobal.ILLEGAL_ULONG : aInt.longValue ();
    }
  }

  @Test
  public void testAll ()
  {
    final Integer [] x = new Integer [] { Integer.valueOf (3),
                                         Integer.valueOf (3),
                                         Integer.valueOf (-56),
                                         Integer.valueOf (1) };

    // default: sort ascending
    List <Integer> l = ContainerHelper.getSorted (x, new ComparatorMockNumeric ());
    assertNotNull (l);
    assertEquals (-56, l.get (0).intValue ());
    assertEquals (1, l.get (1).intValue ());
    assertEquals (3, l.get (2).intValue ());
    assertEquals (3, l.get (3).intValue ());

    // Explicitly sort ascending
    l = ContainerHelper.getSorted (x, new ComparatorMockNumeric (ESortOrder.ASCENDING));
    assertNotNull (l);
    assertEquals (-56, l.get (0).intValue ());
    assertEquals (1, l.get (1).intValue ());
    assertEquals (3, l.get (2).intValue ());
    assertEquals (3, l.get (3).intValue ());

    // Explicitly sort descending
    l = ContainerHelper.getSorted (x, new ComparatorMockNumeric (ESortOrder.DESCENDING));
    assertNotNull (l);
    assertEquals (3, l.get (0).intValue ());
    assertEquals (3, l.get (1).intValue ());
    assertEquals (1, l.get (2).intValue ());
    assertEquals (-56, l.get (3).intValue ());

    // change dynamically
    final ComparatorMockNumeric c = new ComparatorMockNumeric (ESortOrder.ASCENDING);
    l = ContainerHelper.getSorted (x, c);
    assertEquals (-56, l.get (0).intValue ());
    assertEquals (1, l.get (1).intValue ());
    assertEquals (3, l.get (2).intValue ());
    assertEquals (3, l.get (3).intValue ());

    // change to descending
    l = ContainerHelper.getSorted (x, c.setSortOrder (ESortOrder.DESCENDING));
    assertEquals (3, l.get (0).intValue ());
    assertEquals (3, l.get (1).intValue ());
    assertEquals (1, l.get (2).intValue ());
    assertEquals (-56, l.get (3).intValue ());
  }

  /**
   * Test for method isAscending
   */
  @Test
  public void testIsAscending ()
  {
    assertTrue (new ComparatorMockNumeric ().getSortOrder ().isAscending ());
    assertFalse (new ComparatorMockNumeric (ESortOrder.DESCENDING).getSortOrder ().isAscending ());
  }
}
