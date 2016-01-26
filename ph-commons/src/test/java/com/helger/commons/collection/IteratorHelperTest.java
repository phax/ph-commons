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
package com.helger.commons.collection;

import static com.helger.commons.collection.CollectionHelper.newList;
import static com.helger.commons.collection.IteratorHelper.getEmptyEnumeration;
import static com.helger.commons.collection.IteratorHelper.getEmptyIterator;
import static com.helger.commons.collection.IteratorHelper.getEnumeration;
import static com.helger.commons.collection.IteratorHelper.getIterator;
import static com.helger.commons.collection.IteratorHelper.getReverseIterator;
import static com.helger.commons.collection.IteratorHelper.getSize;
import static com.helger.commons.collection.IteratorHelper.isEmpty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Test;

import com.helger.commons.collection.iterate.EmptyEnumeration;
import com.helger.commons.collection.iterate.EmptyIterator;
import com.helger.commons.collection.iterate.IIterableIterator;
import com.helger.commons.collection.iterate.IterableIterator;

/**
 * Test class for class {@link IteratorHelper}.
 *
 * @author Philip Helger
 */
public final class IteratorHelperTest
{
  @Test
  public void testIsEmpty_Enumeration ()
  {
    assertTrue (isEmpty ((Enumeration <?>) null));
    assertTrue (isEmpty (getEnumeration (new ArrayList <> ())));
    assertTrue (isEmpty (new EmptyEnumeration <> ()));
    assertFalse (isEmpty (getEnumeration (newList ("any"))));
  }

  @Test
  public void testIsEmpty_Iterator ()
  {
    assertTrue (isEmpty ((Iterator <?>) null));
    assertTrue (isEmpty (new ArrayList <> ().iterator ()));
    assertTrue (isEmpty (new EmptyIterator <> ()));
    assertFalse (isEmpty (newList ("any").iterator ()));
    assertTrue (isEmpty (new EmptyIterator <> ()));
  }

  @Test
  public void testIsEmpty_IIterableIterator ()
  {
    assertTrue (isEmpty ((IIterableIterator <?>) null));
    assertTrue (isEmpty (IterableIterator.create (new ArrayList <> ())));
    assertTrue (isEmpty (IterableIterator.<String> createEmpty ()));
    assertFalse (isEmpty (IterableIterator.create (newList ("any"))));
  }

  @Test
  public void testGetSize_IterableIterator ()
  {
    assertEquals (0, getSize ((IIterableIterator <?>) null));
    assertEquals (0, getSize (IterableIterator.createEmpty ()));
    assertEquals (1, getSize (IterableIterator.create (newList ("any"))));
  }

  @Test
  public void testGetSize_Iterator ()
  {
    assertEquals (0, getSize ((Iterator <?>) null));
    assertEquals (0, getSize (new ArrayList <String> ().iterator ()));
    assertEquals (1, getSize (newList ("any").iterator ()));
  }

  @Test
  public void testGetEnumeratorFromIterator ()
  {
    final List <String> aList = newList ("d", "c", "b", "a");
    Enumeration <String> aEnum = getEnumeration (aList.iterator ());
    assertTrue (aEnum.hasMoreElements ());
    assertEquals (aEnum.nextElement (), "d");
    assertTrue (aEnum.hasMoreElements ());
    assertEquals (aEnum.nextElement (), "c");
    assertTrue (aEnum.hasMoreElements ());
    assertEquals (aEnum.nextElement (), "b");
    assertTrue (aEnum.hasMoreElements ());
    assertEquals (aEnum.nextElement (), "a");
    assertFalse (aEnum.hasMoreElements ());
    assertFalse (aEnum.hasMoreElements ());

    aEnum = getEnumeration ((Iterator <String>) null);
    assertFalse (aEnum.hasMoreElements ());
  }

  @Test
  public void testGetReverseIterator ()
  {
    assertNotNull (getReverseIterator (null));
    assertFalse (getReverseIterator (null).hasNext ());

    final List <String> aList = newList ("d", "c", "b", "a");
    final Iterator <String> it = getReverseIterator (aList);
    assertTrue (it.hasNext ());
    assertEquals (it.next (), "a");
    assertTrue (it.hasNext ());
    assertEquals (it.next (), "b");
    assertTrue (it.hasNext ());
    assertEquals (it.next (), "c");
    assertTrue (it.hasNext ());
    assertEquals (it.next (), "d");
    assertFalse (it.hasNext ());
    assertFalse (it.hasNext ());
    try
    {
      it.next ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {}

    try
    {
      it.remove ();
      fail ();
    }
    catch (final UnsupportedOperationException ex)
    {}
  }

  @Test
  public void testEmptyIterator ()
  {
    final Iterator <String> it = getEmptyIterator ();
    assertNotNull (it);
    assertFalse (it.hasNext ());

    try
    {
      it.next ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {}

    try
    {
      it.remove ();
      fail ();
    }
    catch (final UnsupportedOperationException ex)
    {}
  }

  @Test
  public void testEmptyEnumeration ()
  {
    final Enumeration <String> aEnum = getEmptyEnumeration ();
    assertNotNull (aEnum);
    assertFalse (aEnum.hasMoreElements ());

    try
    {
      aEnum.nextElement ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {}
  }

  /**
   * Test for method getIterator
   */
  @Test
  public void testGetIterator_Iterable ()
  {
    assertNotNull (getIterator ((ArrayList <?>) null));
    assertFalse (getIterator ((ArrayList <?>) null).hasNext ());
    assertTrue (getIterator (newList ("abc")).hasNext ());
  }

  /**
   * Test for method getIterator
   */
  @Test
  public void testGetIterator_Iterator ()
  {
    assertNotNull (getIterator ((Iterator <?>) null));
    assertFalse (getIterator ((Iterator <?>) null).hasNext ());
    assertTrue (getIterator (newList ("abc").iterator ()).hasNext ());
  }

  /**
   * Test for method getIterator
   */
  @Test
  public void testGetIterator_Array ()
  {
    assertNotNull (getIterator (new String [0]));
    assertFalse (getIterator (new String [0]).hasNext ());
    assertTrue (getIterator ("a").hasNext ());
  }

  @Test
  public void testGetIteratorFromEnumeration ()
  {
    final Enumeration <String> aSourceEnum = Collections.enumeration (newList ("a", "b", "c", "d"));
    IIterableIterator <String> it = getIterator (aSourceEnum);
    assertNotNull (it);
    assertSame (it, it.iterator ());
    assertTrue (it.hasNext ());
    assertEquals ("a", it.next ());
    assertTrue (it.hasNext ());
    assertEquals ("b", it.next ());
    assertTrue (it.hasNext ());
    assertEquals ("c", it.next ());
    assertTrue (it.hasNext ());
    assertEquals ("d", it.next ());
    assertFalse (it.hasNext ());

    try
    {
      it.next ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {}

    try
    {
      it.remove ();
      fail ();
    }
    catch (final UnsupportedOperationException ex)
    {}

    it = getIterator ((Enumeration <String>) null);
    assertNotNull (it);
    assertFalse (it.hasNext ());
  }
}
