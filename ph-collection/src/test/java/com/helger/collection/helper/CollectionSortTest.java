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
package com.helger.collection.helper;

import static com.helger.collection.helper.CollectionHelperExt.createList;
import static com.helger.collection.helper.CollectionSort.getSorted;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Iterator;

import org.junit.Test;

import com.helger.collection.commons.CommonsIterableIterator;
import com.helger.collection.commons.ICommonsIterableIterator;
import com.helger.collection.commons.ICommonsList;

/**
 * Test class for class {@link CollectionSort}
 *
 * @author Philip Helger
 */
public final class CollectionSortTest
{
  @Test
  public void testGetSortedIterator ()
  {
    assertNotNull (getSorted ((Iterator <String>) null));

    final ICommonsList <String> aList = createList ("d", "c", "b", "a");
    final ICommonsList <String> aSorted = getSorted (aList.iterator ());
    assertEquals (4, aSorted.size ());
    assertEquals ("a", aSorted.get (0));
    assertEquals ("b", aSorted.get (1));
    assertEquals ("c", aSorted.get (2));
    assertEquals ("d", aSorted.get (3));
  }

  @Test
  public void testGetSortedIterable ()
  {
    assertNotNull (getSorted ((Iterable <String>) null));

    final ICommonsList <String> aList = createList ("d", "c", "b", "a");
    final ICommonsList <String> aSorted = getSorted (aList);
    assertEquals (4, aSorted.size ());
    assertEquals ("a", aSorted.get (0));
    assertEquals ("b", aSorted.get (1));
    assertEquals ("c", aSorted.get (2));
    assertEquals ("d", aSorted.get (3));
  }

  @Test
  public void testGetSortedIIterableIterator ()
  {
    assertNotNull (getSorted ((ICommonsIterableIterator <String>) null));

    final ICommonsList <String> aList = createList ("d", "c", "b", "a");
    final ICommonsList <String> aSorted = getSorted (new CommonsIterableIterator <> (aList));
    assertEquals (4, aSorted.size ());
    assertEquals ("a", aSorted.get (0));
    assertEquals ("b", aSorted.get (1));
    assertEquals ("c", aSorted.get (2));
    assertEquals ("d", aSorted.get (3));
  }

  @Test
  public void testGetSortedArray ()
  {
    assertNotNull (getSorted ((String []) null));

    final ICommonsList <String> aSorted = getSorted ("d", "c", "b", "a");
    assertEquals (4, aSorted.size ());
    assertEquals ("a", aSorted.get (0));
    assertEquals ("b", aSorted.get (1));
    assertEquals ("c", aSorted.get (2));
    assertEquals ("d", aSorted.get (3));
  }
}
