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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;

import com.helger.commons.charset.CSpecialChars;
import com.helger.commons.collections.CollectionHelper;

/**
 * Test class for {@link AbstractCollatingComparator}
 *
 * @author Philip Helger
 */
public final class AbstractCollatingComparatorTest
{
  @NotThreadSafe
  private static final class MockCollatingComparatorString extends AbstractCollatingComparator <String>
  {
    MockCollatingComparatorString (final Locale aSortLocale)
    {
      super (aSortLocale);
    }

    @Override
    @Nonnull
    protected String getPart (@Nonnull final String sObject)
    {
      return sObject;
    }
  }

  @Test
  public void testAll ()
  {
    final String S1 = "abc";
    final String S2 = "ABC";
    final String S3 = "ab";
    final String [] x = new String [] { S1, S2, S3 };

    // Explicitly sort ascending
    List <String> l = CollectionHelper.getSorted (x,
                                                  new MockCollatingComparatorString (Locale.US).setSortOrder (ESortOrder.ASCENDING));
    assertArrayEquals (new String [] { S3, S1, S2 }, l.toArray ());

    // Explicitly sort descending
    l = CollectionHelper.getSorted (x,
                                    new MockCollatingComparatorString (Locale.US).setSortOrder (ESortOrder.DESCENDING));
    assertArrayEquals (new String [] { S2, S1, S3 }, l.toArray ());

    // change dynamically
    final AbstractComparator <String> c = new MockCollatingComparatorString (Locale.US).setSortOrder (ESortOrder.ASCENDING);
    l = CollectionHelper.getSorted (x, c);
    assertArrayEquals (new String [] { S3, S1, S2 }, l.toArray ());

    // change to descending
    l = CollectionHelper.getSorted (x, c.setSortOrder (ESortOrder.DESCENDING));
    assertArrayEquals (new String [] { S2, S1, S3 }, l.toArray ());
  }

  /**
   * Test for constructors using a locale
   */
  @Test
  public void testLocaleGerman ()
  {
    final String S1 = "bbc";
    final String S2 = "abc";
    final String S3 = CSpecialChars.AUML_LC_STR + "bc";
    final String [] x = new String [] { S1, S2, S3 };

    // default: sort ascending
    List <String> l = CollectionHelper.getSorted (x, new MockCollatingComparatorString (Locale.GERMAN));
    assertArrayEquals (new String [] { S2, S3, S1 }, l.toArray ());

    // sort ascending manually
    l = CollectionHelper.getSorted (x,
                                    new MockCollatingComparatorString (Locale.GERMAN).setSortOrder (ESortOrder.ASCENDING));
    assertArrayEquals (new String [] { S2, S3, S1 }, l.toArray ());

    // sort descending manually
    l = CollectionHelper.getSorted (x,
                                    new MockCollatingComparatorString (Locale.GERMAN).setSortOrder (ESortOrder.DESCENDING));
    assertArrayEquals (new String [] { S1, S3, S2 }, l.toArray ());

    // null locale allowed
    new MockCollatingComparatorString ((Locale) null);
    assertArrayEquals (new String [] { S1, S3, S2 }, l.toArray ());

    // null locale allowed
  }

  /**
   * Test for method isAscending
   */
  @Test
  public void testIsAscending ()
  {
    assertTrue (new MockCollatingComparatorString (Locale.CANADA_FRENCH).getSortOrder ().isAscending ());
    assertFalse (new MockCollatingComparatorString (Locale.CANADA_FRENCH).setSortOrder (ESortOrder.DESCENDING)
                                                                         .getSortOrder ()
                                                                         .isAscending ());
  }
}
