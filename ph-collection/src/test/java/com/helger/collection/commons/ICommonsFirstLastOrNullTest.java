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
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Test class for the <code>getFirstOrNull ()</code> and <code>getLastOrNull ()</code> methods of
 * {@link ICommonsList}, {@link ICommonsOrderedSet} and {@link ICommonsSortedSet}.<br>
 * These methods exist, because the JDK 21 <code>SequencedCollection.getFirst ()</code> of the
 * implementation classes shadows any interface default method of the same name, and throws a
 * {@link java.util.NoSuchElementException} instead of returning <code>null</code>.
 *
 * @author Philip Helger
 */
public final class ICommonsFirstLastOrNullTest
{
  @Test
  public void testList ()
  {
    final ICommonsList <String> aEmpty = new CommonsArrayList <> ();
    assertNull (aEmpty.getFirstOrNull ());
    assertNull (aEmpty.getLastOrNull ());
    assertEquals ("def", aEmpty.getFirst ("def"));
    assertEquals ("def", aEmpty.getLast ("def"));

    final ICommonsList <String> aFilled = new CommonsArrayList <> ("a", "b", "c");
    assertEquals ("a", aFilled.getFirstOrNull ());
    assertEquals ("c", aFilled.getLastOrNull ());
    assertEquals ("a", aFilled.getFirst ("def"));
    assertEquals ("c", aFilled.getLast ("def"));
  }

  @Test
  public void testOrderedSet ()
  {
    final ICommonsOrderedSet <String> aEmpty = new CommonsLinkedHashSet <> ();
    assertNull (aEmpty.getFirstOrNull ());
    assertNull (aEmpty.getLastOrNull ());
    assertEquals ("def", aEmpty.getFirst ("def"));
    assertEquals ("def", aEmpty.getLast ("def"));

    final ICommonsOrderedSet <String> aFilled = new CommonsLinkedHashSet <> ("b", "a", "c");
    // Insertion ordered
    assertEquals ("b", aFilled.getFirstOrNull ());
    assertEquals ("c", aFilled.getLastOrNull ());
    assertEquals ("b", aFilled.getFirst ("def"));
    assertEquals ("c", aFilled.getLast ("def"));
  }

  @Test
  public void testSortedSet ()
  {
    final ICommonsNavigableSet <String> aEmpty = new CommonsTreeSet <> ();
    assertNull (aEmpty.getFirstOrNull ());
    assertNull (aEmpty.getLastOrNull ());
    assertEquals ("def", aEmpty.getFirst ("def"));
    assertEquals ("def", aEmpty.getLast ("def"));

    final ICommonsNavigableSet <String> aFilled = new CommonsTreeSet <> ("b", "a", "c");
    // Sorted
    assertEquals ("a", aFilled.getFirstOrNull ());
    assertEquals ("c", aFilled.getLastOrNull ());
    assertEquals ("a", aFilled.getFirst ("def"));
    assertEquals ("c", aFilled.getLast ("def"));
  }
}
