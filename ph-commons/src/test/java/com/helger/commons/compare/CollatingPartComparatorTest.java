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

import static org.junit.Assert.assertArrayEquals;

import java.util.List;
import java.util.Locale;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;

import com.helger.commons.collection.CollectionHelper;

/**
 * Test class for {@link CollatingPartComparator}
 *
 * @author Philip Helger
 */
public final class CollatingPartComparatorTest
{
  @NotThreadSafe
  private static final class MockCollatingComparatorString extends CollatingPartComparator <String>
  {
    MockCollatingComparatorString (final Locale aSortLocale)
    {
      super (aSortLocale, aObject -> aObject);
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
    List <String> l = CollectionHelper.getSorted (x, new MockCollatingComparatorString (Locale.US));
    assertArrayEquals (new String [] { S3, S1, S2 }, l.toArray ());

    // Explicitly sort descending
    l = CollectionHelper.getSorted (x, new MockCollatingComparatorString (Locale.US).reversed ());
    assertArrayEquals (new String [] { S2, S1, S3 }, l.toArray ());

    // change dynamically
    final AbstractComparator <String> c = new MockCollatingComparatorString (Locale.US);
    l = CollectionHelper.getSorted (x, c);
    assertArrayEquals (new String [] { S3, S1, S2 }, l.toArray ());

    // change to descending
    l = CollectionHelper.getSorted (x, c.reversed ());
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
    List <String> l = CollectionHelper.getSorted (x, new MockCollatingComparatorString (Locale.GERMAN));
    assertArrayEquals (new String [] { S2, S3, S1 }, l.toArray ());

    // sort ascending manually
    l = CollectionHelper.getSorted (x, new MockCollatingComparatorString (Locale.GERMAN));
    assertArrayEquals (new String [] { S2, S3, S1 }, l.toArray ());

    // sort descending manually
    l = CollectionHelper.getSorted (x, new MockCollatingComparatorString (Locale.GERMAN).reversed ());
    assertArrayEquals (new String [] { S1, S3, S2 }, l.toArray ());

    // null locale allowed
    new MockCollatingComparatorString ((Locale) null);
    assertArrayEquals (new String [] { S1, S3, S2 }, l.toArray ());

    // null locale allowed
  }
}
