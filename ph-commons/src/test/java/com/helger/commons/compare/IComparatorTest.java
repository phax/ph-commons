/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.text.Collator;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link IComparator}.
 *
 * @author Philip Helger
 */
public final class IComparatorTest
{
  private static final Locale L_FR = new Locale ("fr");
  private static final Locale L_EN = new Locale ("en");

  @Test
  public void testCollating ()
  {
    final List <String> l = CollectionHelper.newList ("a", null, "c");
    assertEquals (3, CollectionHelper.getSorted (l, IComparator.getComparatorCollating (Locale.US)).size ());
    assertEquals (3,
                  CollectionHelper.getSorted (l, IComparator.getComparatorCollating (Locale.US).reversed ()).size ());
    assertEquals (3, CollectionHelper.getSorted (l, IComparator.getComparatorCollating (L_EN)).size ());
    assertEquals (3, CollectionHelper.getSorted (l, IComparator.getComparatorCollating (L_FR).reversed ()).size ());
    assertEquals (3,
                  CollectionHelper.getSorted (l, IComparator.getComparatorCollating (Collator.getInstance (L_FR)))
                                  .size ());
    assertEquals (3,
                  CollectionHelper.getSorted (l,
                                              IComparator.getComparatorCollating (Collator.getInstance (L_FR))
                                                         .reversed ())
                                  .size ());
    CommonsTestHelper.testToStringImplementation (IComparator.getComparatorCollating (Locale.US));

    try
    {
      IComparator.getComparatorCollating ((Collator) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testCollatingOrder ()
  {
    final String S1 = "abc";
    final String S2 = "ABC";
    final String S3 = "ab";
    final String [] x = new String [] { S1, S2, S3 };

    // Explicitly sort ascending
    List <String> l = CollectionHelper.getSorted (x, IComparator.getComparatorCollating (Locale.US));
    assertArrayEquals (new String [] { S3, S1, S2 }, l.toArray ());

    // Explicitly sort descending
    l = CollectionHelper.getSorted (x, IComparator.getComparatorCollating (Locale.US).reversed ());
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
    final String S3 = "äbc";
    final String [] x = new String [] { S1, S2, S3 };

    // default: sort ascending
    ICommonsList <String> l = CollectionHelper.getSorted (x, IComparator.getComparatorCollating (Locale.GERMAN));
    assertArrayEquals (new String [] { S2, S3, S1 }, l.toArray ());

    // sort ascending manually
    l = CollectionHelper.getSorted (x, IComparator.getComparatorCollating (Locale.GERMAN));
    assertArrayEquals (new String [] { S2, S3, S1 }, l.toArray ());

    // sort descending manually
    l = CollectionHelper.getSorted (x, IComparator.getComparatorCollating (Locale.GERMAN).reversed ());
    assertArrayEquals (new String [] { S1, S3, S2 }, l.toArray ());

    // null locale allowed
    IComparator.getComparatorCollating ((Locale) null);
    assertArrayEquals (new String [] { S1, S3, S2 }, l.toArray ());
  }

  @Test
  public void testComparatorStringLongestFirst ()
  {
    final String S1 = "zzz";
    final String S2 = "zz";
    final String S3 = "aa";
    final String S4 = null;
    final String [] x = new String [] { S1, S2, S3, S4 };

    // Null values first
    ICommonsList <String> l = CollectionHelper.getSorted (x, IComparator.getComparatorStringLongestFirst ());
    assertArrayEquals (new String [] { S4, S1, S3, S2 }, l.toArray ());

    // Null values last
    l = CollectionHelper.getSorted (x, IComparator.getComparatorStringLongestFirst (false));
    assertArrayEquals (new String [] { S1, S3, S2, S4 }, l.toArray ());

    // Null values first
    l = CollectionHelper.getSorted (x, IComparator.getComparatorStringShortestFirst ());
    assertArrayEquals (new String [] { S4, S3, S2, S1 }, l.toArray ());

    // Null values last
    l = CollectionHelper.getSorted (x, IComparator.getComparatorStringShortestFirst (false));
    assertArrayEquals (new String [] { S3, S2, S1, S4 }, l.toArray ());
  }
}
