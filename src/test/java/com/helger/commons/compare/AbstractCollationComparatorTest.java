/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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

import org.junit.Test;

import com.helger.commons.charset.CSpecialChars;
import com.helger.commons.collections.ContainerHelper;

/**
 * Test class for {@link AbstractCollationComparator}
 *
 * @author Philip Helger
 */
public final class AbstractCollationComparatorTest
{
  private static final class ComparatorString extends AbstractCollationComparator <String>
  {
    ComparatorString (final ESortOrder eSortOrder)
    {
      super (eSortOrder);
    }

    ComparatorString (final Locale aSortLocale)
    {
      super (aSortLocale);
    }

    ComparatorString (final Locale aSortLocale, final ESortOrder eSortOrder)
    {
      super (aSortLocale, eSortOrder);
    }

    @Override
    protected String asString (final String sObject)
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
    List <String> l = ContainerHelper.getSorted (x, new ComparatorString (ESortOrder.ASCENDING));
    assertArrayEquals (new String [] { S3, S1, S2 }, l.toArray ());

    // Explicitly sort descending
    l = ContainerHelper.getSorted (x, new ComparatorString (ESortOrder.DESCENDING));
    assertArrayEquals (new String [] { S2, S1, S3 }, l.toArray ());

    // change dynamically
    final ComparatorString c = new ComparatorString (ESortOrder.ASCENDING);
    l = ContainerHelper.getSorted (x, c);
    assertArrayEquals (new String [] { S3, S1, S2 }, l.toArray ());

    // change to descending
    l = ContainerHelper.getSorted (x, c.setSortOrder (ESortOrder.DESCENDING));
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
    List <String> l = ContainerHelper.getSorted (x, new ComparatorString (Locale.GERMAN));
    assertArrayEquals (new String [] { S2, S3, S1 }, l.toArray ());

    // sort ascending manually
    l = ContainerHelper.getSorted (x, new ComparatorString (Locale.GERMAN, ESortOrder.ASCENDING));
    assertArrayEquals (new String [] { S2, S3, S1 }, l.toArray ());

    // sort descending manually
    l = ContainerHelper.getSorted (x, new ComparatorString (Locale.GERMAN, ESortOrder.DESCENDING));
    assertArrayEquals (new String [] { S1, S3, S2 }, l.toArray ());

    // null locale allowed
    new ComparatorString ((Locale) null);
    assertArrayEquals (new String [] { S1, S3, S2 }, l.toArray ());

    // null locale allowed
  }

  /**
   * Test for method isAscending
   */
  @Test
  public void testIsAscending ()
  {
    assertTrue (new ComparatorString (Locale.CANADA_FRENCH).getSortOrder ().isAscending ());
    assertFalse (new ComparatorString (Locale.CANADA_FRENCH, ESortOrder.DESCENDING).getSortOrder ().isAscending ());
    assertTrue (new ComparatorString (ESortOrder.ASCENDING).getSortOrder ().isAscending ());
    assertFalse (new ComparatorString (ESortOrder.DESCENDING).getSortOrder ().isAscending ());
  }
}
