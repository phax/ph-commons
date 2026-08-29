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
package com.helger.collection.paging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link PagingSpec}.
 *
 * @author Philip Helger
 */
public final class PagingSpecTest
{
  @Test
  public void testBasic ()
  {
    final PagingSpec aSpec = new PagingSpec (25, 25);
    assertEquals (25, aSpec.getStartIndex ());
    assertEquals (25, aSpec.getMaxCount ());
    assertFalse (aSpec.isUnlimited ());
    assertFalse (aSpec.isEmptyPage ());
    assertFalse (aSpec.hasSortFields ());
    assertEquals (0, aSpec.getSortFieldCount ());
    assertTrue (aSpec.getAllSortFields ().isEmpty ());

    TestHelper.testDefaultImplementationWithEqualContentObject (aSpec, new PagingSpec (25, 25));
    TestHelper.testDefaultImplementationWithDifferentContentObject (aSpec, new PagingSpec (0, 25));
    TestHelper.testDefaultImplementationWithDifferentContentObject (aSpec, new PagingSpec (25, 50));
    TestHelper.testDefaultImplementationWithDifferentContentObject (aSpec,
                                                                    new PagingSpec (25,
                                                                                    25,
                                                                                    SortField.ascending ("name")));
  }

  @Test
  public void testUnlimited ()
  {
    assertTrue (PagingSpec.UNLIMITED.isUnlimited ());
    assertFalse (PagingSpec.UNLIMITED.isEmptyPage ());
    assertEquals (0, PagingSpec.UNLIMITED.getStartIndex ());

    // All negative values must be treated the same
    assertEquals (new PagingSpec (0, -1), new PagingSpec (0, -999));
    assertEquals (PagingSpec.UNLIMITED, PagingSpec.createUnlimited ());

    final PagingSpec aSpec = PagingSpec.createUnlimited (SortField.descending ("name"));
    assertTrue (aSpec.isUnlimited ());
    assertEquals (1, aSpec.getSortFieldCount ());
  }

  @Test
  public void testEmptyPage ()
  {
    final PagingSpec aSpec = new PagingSpec (0, 0);
    assertTrue (aSpec.isEmptyPage ());
    assertFalse (aSpec.isUnlimited ());
  }

  @Test
  public void testSortFields ()
  {
    final SortField aSF1 = SortField.ascending ("name");
    final SortField aSF2 = SortField.descending ("id");
    final PagingSpec aSpec = new PagingSpec (0, 10, aSF1, aSF2);
    assertEquals (2, aSpec.getSortFieldCount ());
    assertTrue (aSpec.hasSortFields ());

    // Order of precedence must be retained
    final ICommonsList <SortField> aAll = aSpec.getAllSortFields ();
    assertEquals (new CommonsArrayList <> (aSF1, aSF2), aAll);

    // Must be a copy
    aAll.clear ();
    assertEquals (2, aSpec.getSortFieldCount ());

    // Iterable based constructor must lead to the same result
    assertEquals (aSpec, new PagingSpec (0, 10, new CommonsArrayList <> (aSF1, aSF2)));

    // withSortFields keeps the paging but replaces the sorting
    final PagingSpec aSpec2 = aSpec.withSortFields (aSF2);
    assertEquals (aSpec.getStartIndex (), aSpec2.getStartIndex ());
    assertEquals (aSpec.getMaxCount (), aSpec2.getMaxCount ());
    assertEquals (new CommonsArrayList <> (aSF2), aSpec2.getAllSortFields ());
  }

  @Test
  public void testCreateForPage ()
  {
    assertEquals (new PagingSpec (0, 25), PagingSpec.createForPage (0, 25));
    assertEquals (new PagingSpec (25, 25), PagingSpec.createForPage (1, 25));
    assertEquals (new PagingSpec (250, 25), PagingSpec.createForPage (10, 25));

    // Must be able to handle values beyond the int range
    assertEquals (new PagingSpec (2L * Integer.MAX_VALUE, 2), PagingSpec.createForPage (Integer.MAX_VALUE, 2));

    // Must not silently overflow
    try
    {
      PagingSpec.createForPage (Long.MAX_VALUE, 25);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testInvalid ()
  {
    try
    {
      new PagingSpec (-1, 25);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }

    try
    {
      new PagingSpec (0, 25, SortField.ascending ("name"), null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }

    try
    {
      PagingSpec.createForPage (0, 0);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }
}
