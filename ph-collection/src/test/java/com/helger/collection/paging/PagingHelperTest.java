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
import static org.junit.Assert.assertTrue;

import java.util.Comparator;

import org.junit.Test;

import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;

/**
 * Test class for class {@link PagingHelper}.
 *
 * @author Philip Helger
 */
public final class PagingHelperTest
{
  private static final ICommonsList <String> LIST = new CommonsArrayList <> ("d", "b", "e", "a", "c");

  @Test
  public void testSorted ()
  {
    final Comparator <String> aComp = Comparator.naturalOrder ();

    assertEquals (new CommonsArrayList <> ("a", "b"), PagingHelper.getPage (LIST, new PagingSpec (0, 2), aComp));
    assertEquals (new CommonsArrayList <> ("c", "d"), PagingHelper.getPage (LIST, new PagingSpec (2, 2), aComp));

    // Last page is not full
    assertEquals (new CommonsArrayList <> ("e"), PagingHelper.getPage (LIST, new PagingSpec (4, 2), aComp));

    // Start index beyond the end
    assertTrue (PagingHelper.getPage (LIST, new PagingSpec (5, 2), aComp).isEmpty ());
    assertTrue (PagingHelper.getPage (LIST, new PagingSpec (99, 2), aComp).isEmpty ());

    // Max count 0
    assertTrue (PagingHelper.getPage (LIST, new PagingSpec (0, 0), aComp).isEmpty ());

    // Unlimited
    assertEquals (new CommonsArrayList <> ("a", "b", "c", "d", "e"),
                  PagingHelper.getPage (LIST, PagingSpec.UNLIMITED, aComp));
    assertEquals (new CommonsArrayList <> ("c", "d", "e"),
                  PagingHelper.getPage (LIST, new PagingSpec (2, -1), aComp));

    // Descending
    assertEquals (new CommonsArrayList <> ("e", "d"),
                  PagingHelper.getPage (LIST, new PagingSpec (0, 2), aComp.reversed ()));
  }

  @Test
  public void testUnsorted ()
  {
    // No comparator - existing order is kept
    assertEquals (new CommonsArrayList <> ("d", "b"), PagingHelper.getPage (LIST, new PagingSpec (0, 2)));
    assertEquals (new CommonsArrayList <> ("e", "a"), PagingHelper.getPage (LIST, new PagingSpec (2, 2)));
  }

  @Test
  public void testSourceIsNotModified ()
  {
    final ICommonsList <String> aSource = new CommonsArrayList <> ("d", "b", "e", "a", "c");
    PagingHelper.getPage (aSource, new PagingSpec (0, 2), Comparator.naturalOrder ());
    assertEquals (new CommonsArrayList <> ("d", "b", "e", "a", "c"), aSource);
  }

  @Test
  public void testEmptySource ()
  {
    assertTrue (PagingHelper.getPage (new CommonsArrayList <String> (), new PagingSpec (0, 25)).isEmpty ());
  }
}
