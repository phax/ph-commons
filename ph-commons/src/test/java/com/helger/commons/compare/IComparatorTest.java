/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import static com.helger.collection.helper.CollectionHelperExt.newList;
import static com.helger.collection.helper.CollectionHelperExt.newMap;
import static com.helger.collection.helper.CollectionSort.getSorted;
import static com.helger.collection.helper.CollectionSort.getSortedByKey;
import static com.helger.collection.helper.CollectionSort.getSortedByValue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.text.Collator;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;

import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.CommonsIterableIterator;
import com.helger.collection.helper.CollectionHelperExt;
import com.helger.commons.mock.CommonsTestHelper;

import jakarta.annotation.Nonnull;

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
    final List <String> l = CollectionHelperExt.newList ("a", null, "c");
    assertEquals (3, getSorted (l, IComparator.getComparatorCollating (Locale.US)).size ());
    assertEquals (3, getSorted (l, IComparator.getComparatorCollating (Locale.US).reversed ()).size ());
    assertEquals (3, getSorted (l, IComparator.getComparatorCollating (L_EN)).size ());
    assertEquals (3, getSorted (l, IComparator.getComparatorCollating (L_FR).reversed ()).size ());
    assertEquals (3, getSorted (l, IComparator.getComparatorCollating (Collator.getInstance (L_FR))).size ());
    assertEquals (3,
                  getSorted (l, IComparator.getComparatorCollating (Collator.getInstance (L_FR)).reversed ()).size ());
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
    final String [] x = { S1, S2, S3 };

    // Explicitly sort ascending
    List <String> l = getSorted (x, IComparator.getComparatorCollating (Locale.US));
    assertArrayEquals (new String [] { S3, S1, S2 }, l.toArray ());

    // Explicitly sort descending
    l = getSorted (x, IComparator.getComparatorCollating (Locale.US).reversed ());
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
    final String [] x = { S1, S2, S3 };

    // default: sort ascending
    ICommonsList <String> l = getSorted (x, IComparator.getComparatorCollating (Locale.GERMAN));
    assertArrayEquals (new String [] { S2, S3, S1 }, l.toArray ());

    // sort ascending manually
    l = getSorted (x, IComparator.getComparatorCollating (Locale.GERMAN));
    assertArrayEquals (new String [] { S2, S3, S1 }, l.toArray ());

    // sort descending manually
    l = getSorted (x, IComparator.getComparatorCollating (Locale.GERMAN).reversed ());
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
    final String [] x = { S1, S2, S3, S4 };

    // Null values first
    ICommonsList <String> l = getSorted (x, IComparator.getComparatorStringLongestFirst ());
    assertArrayEquals (new String [] { S4, S1, S3, S2 }, l.toArray ());

    // Null values last
    l = getSorted (x, IComparator.getComparatorStringLongestFirst (false));
    assertArrayEquals (new String [] { S1, S3, S2, S4 }, l.toArray ());

    // Null values first
    l = getSorted (x, IComparator.getComparatorStringShortestFirst ());
    assertArrayEquals (new String [] { S4, S3, S2, S1 }, l.toArray ());

    // Null values last
    l = getSorted (x, IComparator.getComparatorStringShortestFirst (false));
    assertArrayEquals (new String [] { S3, S2, S1, S4 }, l.toArray ());
  }

  @Test
  public void testGetSortedIIterableIteratorWithLocale ()
  {
    final ICommonsList <String> aList = newList ("d", "c", "b", "a");
    final ICommonsList <String> aSorted = getSorted (new CommonsIterableIterator <> (aList),
                                                     IComparator.getComparatorCollating (Locale.US));
    assertEquals (4, aSorted.size ());
    assertEquals ("a", aSorted.get (0));
    assertEquals ("b", aSorted.get (1));
    assertEquals ("c", aSorted.get (2));
    assertEquals ("d", aSorted.get (3));
  }

  private static final class MyStringCompi implements IComparator <String>
  {
    public int compare (@Nonnull final String sStr1, @Nonnull final String sStr2)
    {
      if (sStr1.equals ("b"))
        return -1;
      if (sStr2.equals ("b"))
        return +1;
      return sStr1.compareTo (sStr2);
    }
  }

  @Test
  public void testGetSortedFromIteratorWithCompi ()
  {
    assertNotNull (getSorted ((Iterator <String>) null, new MyStringCompi ()));

    final ICommonsList <String> aList = newList ("d", "c", "b", "a");

    try
    {
      // null comparator not allowed
      getSorted (aList.iterator (), (Comparator <String>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    final ICommonsList <String> aSorted = getSorted (aList.iterator (), new MyStringCompi ());
    assertEquals (4, aSorted.size ());
    assertEquals ("b", aSorted.get (0));
    assertEquals ("a", aSorted.get (1));
    assertEquals ("c", aSorted.get (2));
    assertEquals ("d", aSorted.get (3));
  }

  @Test
  public void testGetSortedIterableWithCompi ()
  {
    assertNotNull (getSorted ((Iterable <String>) null, new MyStringCompi ()));

    final ICommonsList <String> aList = newList ("d", "c", "b", "a");

    try
    {
      // null comparator not allowed
      getSorted (aList, (Comparator <String>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    final ICommonsList <String> aSorted = getSorted (aList, new MyStringCompi ());
    assertEquals (4, aSorted.size ());
    assertEquals ("b", aSorted.get (0));
    assertEquals ("a", aSorted.get (1));
    assertEquals ("c", aSorted.get (2));
    assertEquals ("d", aSorted.get (3));
  }

  @Test
  public void testGetSortedArrayWithCompi ()
  {
    assertNotNull (getSorted ((String []) null, new MyStringCompi ()));

    try
    {
      // null comparator not allowed
      getSorted (new String [0], null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    final ICommonsList <String> aSorted = getSorted (new String [] { "d", "c", "b", "a" }, new MyStringCompi ());
    assertEquals (4, aSorted.size ());
    assertEquals ("b", aSorted.get (0));
    assertEquals ("a", aSorted.get (1));
    assertEquals ("c", aSorted.get (2));
    assertEquals ("d", aSorted.get (3));
  }

  /**
   * Test for method getSortedByKey
   */
  @Test
  public void testGetSortedByKey ()
  {
    assertNotNull (getSortedByKey ((Map <String, ?>) null));
    assertNotNull (getSortedByKey (null, IComparator.getComparatorCollating (Locale.US).reversed ()));

    try
    {
      // null Comparator
      getSortedByKey (newMap (), (Comparator <String>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    final Map <String, String> aMap = newMap ("K2", "ValueA", "K3", "ValueB", "K1", "ValueC");
    assertNotNull (aMap);
    assertEquals (3, aMap.size ());

    Iterator <Map.Entry <String, String>> it = getSortedByKey (aMap).entrySet ().iterator ();
    assertEquals ("K1", it.next ().getKey ());
    assertEquals ("K2", it.next ().getKey ());
    assertEquals ("K3", it.next ().getKey ());

    // reverse sort
    it = getSortedByKey (aMap, IComparator.getComparatorCollating (Locale.US).reversed ()).entrySet ().iterator ();
    assertEquals ("K3", it.next ().getKey ());
    assertEquals ("K2", it.next ().getKey ());
    assertEquals ("K1", it.next ().getKey ());
  }

  /**
   * Test for method getSortedByValue
   */
  @Test
  public void testGetSortedByValue ()
  {
    assertNotNull (getSortedByValue ((Map <?, String>) null));
    assertNotNull (getSortedByValue (null, IComparator.getComparatorCollating (Locale.US).reversed ()));

    try
    {
      // null Comparator
      getSortedByValue (newMap (), (Comparator <String>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    final Map <String, String> aMap = newMap ("K1", "ValueB", "K2", "ValueC", "K3", "ValueA");
    assertNotNull (aMap);
    assertEquals (3, aMap.size ());

    Iterator <Map.Entry <String, String>> it = getSortedByValue (aMap).entrySet ().iterator ();
    assertEquals ("ValueA", it.next ().getValue ());
    assertEquals ("ValueB", it.next ().getValue ());
    assertEquals ("ValueC", it.next ().getValue ());

    // reverse sort
    it = getSortedByValue (aMap, IComparator.getComparatorCollating (Locale.US).reversed ()).entrySet ().iterator ();
    assertEquals ("ValueC", it.next ().getValue ());
    assertEquals ("ValueB", it.next ().getValue ());
    assertEquals ("ValueA", it.next ().getValue ());
  }
}
