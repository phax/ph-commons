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
package com.helger.collection.commons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.Test;

/**
 * Test class for the default methods of {@link ICommonsList}.
 *
 * @author Philip Helger
 */
public final class ICommonsListTest
{
  private static ICommonsList <String> _createList ()
  {
    return new CommonsArrayList <> ("a", "b", "c");
  }

  @Test
  public void testGetAll ()
  {
    final ICommonsList <String> aList = _createList ();

    assertEquals (3, aList.getAll (null).size ());
    assertEquals (1, aList.getAll (x -> x.equals ("a")).size ());

    final Function <String, String> aMapper = x -> "#" + x;
    assertEquals (new CommonsArrayList <> ("#a", "#b", "#c"), aList.getAllMapped (aMapper));
    assertEquals (new CommonsArrayList <> ("#a"), aList.getAllMapped (x -> x.equals ("a"), aMapper));
    assertEquals (3, aList.getAllMapped ((Predicate <String>) null, aMapper).size ());

    final ICommonsList <Object> aObjects = new CommonsArrayList <> ("a", Integer.valueOf (1), "b");
    assertEquals (2, aObjects.getAllInstanceOf (String.class).size ());
    assertEquals (1, aObjects.getAllInstanceOf (Integer.class).size ());

    assertNotNull (aList.createInstance ());
    assertTrue (aList.<Integer> createInstance ().isEmpty ());
  }

  @Test
  public void testFirstAndLast ()
  {
    final ICommonsList <String> aList = _createList ();
    assertEquals ("a", aList.getFirstOrNull ());
    assertEquals ("a", aList.getFirst ("x"));
    assertEquals ("c", aList.getLastOrNull ());
    assertEquals ("c", aList.getLast ("x"));
    assertEquals ("b", aList.getAtIndex (1, "x"));
    assertEquals ("x", aList.getAtIndex (17, "x"));

    final ICommonsList <String> aEmpty = new CommonsArrayList <> ();
    assertNull (aEmpty.getFirstOrNull ());
    assertEquals ("x", aEmpty.getFirst ("x"));
    assertNull (aEmpty.getLastOrNull ());
    assertEquals ("x", aEmpty.getLast ("x"));
  }

  @Test
  public void testSetFirstAndLast ()
  {
    final ICommonsList <String> aList = _createList ();
    assertEquals ("a", aList.setFirst ("x"));
    assertEquals ("x", aList.getFirstOrNull ());
    assertEquals ("c", aList.setLast ("y"));
    assertEquals ("y", aList.getLastOrNull ());

    final ICommonsList <String> aEmpty = new CommonsArrayList <> ();
    try
    {
      aEmpty.setFirst ("x");
      fail ();
    }
    catch (final IndexOutOfBoundsException ex)
    {
      // expected
    }
    try
    {
      aEmpty.setLast ("x");
      fail ();
    }
    catch (final IndexOutOfBoundsException ex)
    {
      // expected
    }
  }

  @Test
  public void testRemove ()
  {
    final ICommonsList <String> aList = _createList ();

    assertTrue (aList.removeAtIndex (0).isChanged ());
    assertFalse (aList.removeAtIndex (-1).isChanged ());
    assertFalse (aList.removeAtIndex (17).isChanged ());
    assertEquals (2, aList.size ());

    assertEquals ("b", aList.removeAndReturnElementAtIndex (0));
    assertNull (aList.removeAndReturnElementAtIndex (-1));
    assertNull (aList.removeAndReturnElementAtIndex (17));

    final ICommonsList <String> aList2 = _createList ();
    assertEquals ("a", aList2.removeFirstOrNull ());
    assertEquals ("c", aList2.removeLastOrNull ());
    assertEquals (1, aList2.size ());

    final ICommonsList <String> aEmpty = new CommonsArrayList <> ();
    assertNull (aEmpty.removeFirstOrNull ());
    assertNull (aEmpty.removeLastOrNull ());
  }

  @Test
  public void testSortAndReverse ()
  {
    final ICommonsList <String> aList = _createList ();

    assertSame (aList, aList.getSortedInline (Comparator.reverseOrder ()));
    assertEquals (new CommonsArrayList <> ("c", "b", "a"), aList);

    assertSame (aList, aList.reverse ());
    assertEquals (new CommonsArrayList <> ("a", "b", "c"), aList);

    assertSame (aList, aList.swapItems (0, 2));
    assertEquals (new CommonsArrayList <> ("c", "b", "a"), aList);
    // Swapping the same index changes nothing
    assertSame (aList, aList.swapItems (1, 1));
    assertEquals (new CommonsArrayList <> ("c", "b", "a"), aList);
  }

  @Test
  public void testUnmodifiable ()
  {
    final List <String> aUnmodifiable = _createList ().getAsUnmodifiable ();
    assertEquals (3, aUnmodifiable.size ());
    try
    {
      aUnmodifiable.add ("d");
      fail ();
    }
    catch (final UnsupportedOperationException ex)
    {
      // expected
    }
  }
}
