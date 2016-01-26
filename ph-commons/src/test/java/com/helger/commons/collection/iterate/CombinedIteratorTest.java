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
package com.helger.commons.collection.iterate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Test;

import com.helger.commons.collection.IteratorHelper;

/**
 * Test class for class {@link CombinedIterator}.
 *
 * @author Philip Helger
 */
public final class CombinedIteratorTest
{
  @Test
  public void testAll ()
  {
    // both null
    Iterator <String> es = CombinedIterator.create (null, null);
    assertFalse (es.hasNext ());
    try
    {
      es.next ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {}

    // both empty
    es = CombinedIterator.create (new EmptyIterator <String> (), new EmptyIterator <String> ());
    assertFalse (es.hasNext ());
    try
    {
      es.next ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {}

    // one null
    es = CombinedIterator.create (IteratorHelper.getIterator ("a", "b", "c"), null);
    assertTrue (es.hasNext ());
    assertEquals ("a", es.next ());
    assertTrue (es.hasNext ());
    assertEquals ("b", es.next ());
    assertTrue (es.hasNext ());
    assertEquals ("c", es.next ());
    assertFalse (es.hasNext ());
    try
    {
      es.next ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {}

    // one empty
    es = CombinedIterator.create (IteratorHelper.getIterator ("a", "b", "c"), new EmptyIterator <> ());
    assertTrue (es.hasNext ());
    assertEquals ("a", es.next ());
    assertTrue (es.hasNext ());
    assertEquals ("b", es.next ());
    assertTrue (es.hasNext ());
    assertEquals ("c", es.next ());
    assertFalse (es.hasNext ());
    try
    {
      es.next ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {}

    // other one null
    es = CombinedIterator.create (null, IteratorHelper.getIterator ("a", "b", "c"));
    assertTrue (es.hasNext ());
    assertEquals ("a", es.next ());
    assertTrue (es.hasNext ());
    assertEquals ("b", es.next ());
    assertTrue (es.hasNext ());
    assertEquals ("c", es.next ());
    assertFalse (es.hasNext ());
    try
    {
      es.next ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {}

    // other one empty
    es = CombinedIterator.create (new EmptyIterator <String> (), IteratorHelper.getIterator ("a", "b", "c"));
    assertTrue (es.hasNext ());
    assertEquals ("a", es.next ());
    assertTrue (es.hasNext ());
    assertEquals ("b", es.next ());
    assertTrue (es.hasNext ());
    assertEquals ("c", es.next ());
    assertFalse (es.hasNext ());
    try
    {
      es.next ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {}

    // both not null
    es = CombinedIterator.create (IteratorHelper.getIterator ("a", "b"), IteratorHelper.getIterator ("c", "d"));
    assertTrue (es.hasNext ());
    assertEquals ("a", es.next ());
    assertTrue (es.hasNext ());
    assertEquals ("b", es.next ());
    assertTrue (es.hasNext ());
    assertEquals ("c", es.next ());
    assertTrue (es.hasNext ());
    assertEquals ("d", es.next ());
    assertFalse (es.hasNext ());
    try
    {
      es.next ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {}
  }
}
