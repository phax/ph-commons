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
package com.helger.commons.collection.iterate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;

import org.junit.Test;

import com.helger.base.functional.Predicates;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.helper.CollectionHelperExt;
import com.helger.collection.iterator.FilterIterator;
import com.helger.collection.iterator.IterableIterator;
import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for {@link FilterIterator}
 *
 * @author Philip Helger
 */
public final class FilterIteratorTest
{
  /**
   * Test constructor with Iterator
   */
  @Test
  public void testConstructorIterator ()
  {
    final List <String> aList = CollectionHelperExt.newList ("i1", "i2");
    final Iterator <String> it = aList.iterator ();
    final Predicate <String> aFilter = Predicates.notNull ();

    try
    {
      // both null
      new FilterIterator <> ((Iterator <String>) null, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // filter is null
      new FilterIterator <> (it, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // iterator is null
      new FilterIterator <> ((Iterator <String>) null, aFilter);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  /**
   * Test constructor with Iterable
   */
  @Test
  public void testConstructorIterable ()
  {
    final ICommonsList <String> aList = new CommonsArrayList <> ("i1", "i2");
    final Predicate <String> aFilter = Predicates.notNull ();

    try
    {
      // both null
      new FilterIterator <> ((Iterable <String>) null, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // filter is null
      new FilterIterator <> (aList, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // iterator is null
      new FilterIterator <> ((Iterable <String>) null, aFilter);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testIteration1 ()
  {
    final List <String> aList = CollectionHelperExt.newList ("s1", "s2", null, "s3");
    new FilterIterator <> (new IterableIterator <> (aList), Objects::nonNull);
    final FilterIterator <String> it = new FilterIterator <> (aList, Objects::nonNull);
    assertNotNull (it);
    assertSame (it, it.iterator ());

    assertTrue (it.hasNext ());
    assertEquals ("s1", it.next ());
    assertTrue (it.hasNext ());
    assertEquals ("s2", it.next ());
    assertTrue (it.hasNext ());
    assertEquals ("s3", it.next ());
    assertFalse (it.hasNext ());

    try
    {
      // can't call next if hasNext failed
      it.next ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {}
  }

  @Test
  public void testIteration2 ()
  {
    final List <String> aList = CollectionHelperExt.newList ("s1", "s2", null, "s3");
    final FilterIterator <String> it = new FilterIterator <> (aList.iterator (), Objects::nonNull);
    assertNotNull (it);
    assertSame (it, it.iterator ());

    assertTrue (it.hasNext ());
    assertTrue (it.hasNext ());
    assertTrue (it.hasNext ());
    assertEquals ("s1", it.next ());
    assertTrue (it.hasNext ());
    assertTrue (it.hasNext ());
    assertEquals ("s2", it.next ());
    assertTrue (it.hasNext ());
    assertTrue (it.hasNext ());
    assertEquals ("s3", it.next ());
    assertFalse (it.hasNext ());
    assertFalse (it.hasNext ());

    try
    {
      // can't call next if hasNext failed
      it.next ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {}
  }

  @Test
  public void testIteration3 ()
  {
    // filtered element in the middle
    final List <String> aList = CollectionHelperExt.newList ("s1", null, "s2", null, "s3");
    final FilterIterator <String> it = new FilterIterator <> (aList, Objects::nonNull);
    assertNotNull (it);

    assertEquals ("s1", it.next ());
    assertEquals ("s2", it.next ());
    assertEquals ("s3", it.next ());

    try
    {
      // can't call next if hasNext failed
      it.next ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {}
  }

  @Test
  public void testIteration4 ()
  {
    // filtered elements at the end
    final List <String> aList = CollectionHelperExt.newList ("s1", "s2", "s3", null, null);
    final FilterIterator <String> it = new FilterIterator <> (aList, Objects::nonNull);
    assertNotNull (it);

    assertEquals ("s1", it.next ());
    assertEquals ("s2", it.next ());
    assertEquals ("s3", it.next ());

    try
    {
      // can't call next if hasNext failed
      it.next ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {}
  }

  @Test
  public void testIteration5 ()
  {
    // filtered elements at the beginning
    final List <String> aList = CollectionHelperExt.newList (null, null, "s1", "s2", "s3");
    final FilterIterator <String> it = new FilterIterator <> (aList, Objects::nonNull);
    assertNotNull (it);

    assertEquals ("s1", it.next ());
    assertEquals ("s2", it.next ());
    assertEquals ("s3", it.next ());

    CommonsTestHelper.testToStringImplementation (it);

    try
    {
      // can't call next if hasNext failed
      it.next ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {}
  }
}
