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

import com.helger.base.compare.ESortOrder;
import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link SortField}.
 *
 * @author Philip Helger
 */
public final class SortFieldTest
{
  @Test
  public void testBasic ()
  {
    final SortField aAsc = SortField.ascending ("name");
    assertEquals ("name", aAsc.getFieldName ());
    assertEquals (ESortOrder.ASCENDING, aAsc.getSortOrder ());
    assertTrue (aAsc.isAscending ());
    assertTrue (aAsc.hasFieldName ("name"));
    assertFalse (aAsc.hasFieldName ("Name"));
    assertFalse (aAsc.hasFieldName (null));

    final SortField aDesc = SortField.descending ("name");
    assertEquals (ESortOrder.DESCENDING, aDesc.getSortOrder ());
    assertFalse (aDesc.isAscending ());

    assertEquals (aDesc, aAsc.getAlternate ());
    assertEquals (aAsc, aDesc.getAlternate ());

    TestHelper.testDefaultImplementationWithEqualContentObject (aAsc, SortField.ascending ("name"));
    TestHelper.testDefaultImplementationWithDifferentContentObject (aAsc, SortField.descending ("name"));
    TestHelper.testDefaultImplementationWithDifferentContentObject (aAsc, SortField.ascending ("other"));
  }

  @Test
  public void testInvalid ()
  {
    try
    {
      new SortField (null, ESortOrder.ASCENDING);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }

    try
    {
      new SortField ("", ESortOrder.ASCENDING);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }

    try
    {
      new SortField ("name", null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }
}
