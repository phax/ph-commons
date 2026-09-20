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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.Test;

import com.helger.base.numeric.mutable.MutableInt;
import com.helger.base.state.EContinue;

/**
 * Test class for the default methods of {@link ICommonsCollection} and {@link ICommonsIterable}.
 *
 * @author Philip Helger
 */
public final class ICommonsCollectionTest
{
  private static ICommonsList <String> _createList ()
  {
    return new CommonsArrayList <> ("a", "b", "c");
  }

  @Test
  public void testGetters ()
  {
    final ICommonsCollection <String> aColl = _createList ();

    assertEquals (_createList (), aColl.getCopyAsList ());
    assertEquals (3, aColl.getCount (null));
    assertEquals (1, aColl.getCount (x -> x.equals ("a")));

    assertEquals ("a", aColl.getAtIndex (0));
    assertNull (aColl.getAtIndex (17));
    assertEquals ("x", aColl.getAtIndex (17, "x"));
    assertEquals ("a", aColl.getAtIndex (0, "x"));

    assertEquals ("b", aColl.getAtIndex (x -> !x.equals ("a"), 0));
    assertNull (aColl.getAtIndex (x -> !x.equals ("a"), 17));
    assertEquals ("b", aColl.getAtIndex (x -> !x.equals ("a"), 0, "x"));
    assertEquals ("x", aColl.getAtIndex (x -> !x.equals ("a"), 17, "x"));
    assertEquals ("a", aColl.getAtIndex (null, 0, "x"));

    assertEquals ("#a", aColl.getAtIndexMapped (0, x -> "#" + x));
    assertNull (aColl.getAtIndexMapped (17, x -> "#" + x));
    assertEquals ("#a", aColl.getAtIndexMapped (0, x -> "#" + x, "x"));
    assertEquals ("x", aColl.getAtIndexMapped (17, x -> "#" + x, "x"));
    assertEquals ("#b", aColl.getAtIndexMapped (x -> !x.equals ("a"), 0, x -> "#" + x));
    assertNull (aColl.getAtIndexMapped (x -> !x.equals ("a"), 17, x -> "#" + x));
    assertEquals ("#b", aColl.getAtIndexMapped (x -> !x.equals ("a"), 0, x -> "#" + x, "x"));
    assertEquals ("x", aColl.getAtIndexMapped (x -> !x.equals ("a"), 17, x -> "#" + x, "x"));

    assertEquals (new CommonsArrayList <> ("c", "b", "a"), aColl.getSorted (Comparator.reverseOrder ()));
  }

  @Test
  public void testAdd ()
  {
    final ICommonsCollection <String> aColl = new CommonsArrayList <> ();

    assertTrue (aColl.addObject ("a").isChanged ());
    assertTrue (aColl.addIf ("b", x -> true).isChanged ());
    assertFalse (aColl.addIf ("c", x -> false).isChanged ());
    assertTrue (aColl.addIf ("c", null).isChanged ());
    assertTrue (aColl.addIfNotNull ("d").isChanged ());
    assertFalse (aColl.addIfNotNull (null).isChanged ());
    assertEquals (4, aColl.size ());
  }

  @Test
  public void testAddAll ()
  {
    final ICommonsCollection <String> aColl = new CommonsArrayList <> ();

    assertTrue (aColl.addAll ("a", "b").isChanged ());
    assertFalse (aColl.addAll ((String []) null).isChanged ());
    assertTrue (aColl.addAll ((Iterable <String>) _createList ()).isChanged ());
    assertFalse (aColl.addAll ((Iterable <String>) null).isChanged ());
    assertTrue (aColl.addAll (_createList ().iterator ()).isChanged ());
    assertFalse (aColl.addAll ((Iterator <String>) null).isChanged ());

    final Enumeration <String> aEnum = new CommonsVector <> (_createList ()).elements ();
    assertTrue (aColl.addAll (aEnum).isChanged ());
    assertFalse (aColl.addAll ((Enumeration <String>) null).isChanged ());

    aColl.clear ();
    assertTrue (aColl.addAll (new String [] { "a", "b" }, x -> x.equals ("a")).isChanged ());
    assertEquals (1, aColl.size ());
    assertTrue (aColl.addAll (new String [] { "a", "b" }, (Predicate <String>) null).isChanged ());
    assertEquals (3, aColl.size ());
    assertFalse (aColl.addAll ((String []) null, x -> true).isChanged ());

    aColl.clear ();
    assertTrue (aColl.addAll (_createList (), x -> x.equals ("a")).isChanged ());
    assertEquals (1, aColl.size ());
    assertFalse (aColl.addAll ((Iterable <String>) null, x -> true).isChanged ());

    aColl.clear ();
    assertTrue (aColl.addAll (_createList ().iterator (), x -> x.equals ("a")).isChanged ());
    assertEquals (1, aColl.size ());
    assertFalse (aColl.addAll ((Iterator <String>) null, x -> true).isChanged ());

    aColl.clear ();
    assertTrue (aColl.addAll (new CommonsVector <> (_createList ()).elements (), x -> x.equals ("a")).isChanged ());
    assertEquals (1, aColl.size ());
    assertFalse (aColl.addAll ((Enumeration <String>) null, x -> true).isChanged ());
  }

  @Test
  public void testAddAllMapped ()
  {
    final ICommonsCollection <String> aColl = new CommonsArrayList <> ();
    final Function <String, String> aMapper = x -> "#" + x;
    final Predicate <String> aSrcFilter = x -> x.equals ("a");
    final Predicate <String> aDstFilter = x -> x.equals ("#a");
    final Predicate <String> aAllFilter = x -> true;

    assertTrue (aColl.addAllMapped (_createList (), aMapper).isChanged ());
    assertEquals ("#a", aColl.getAtIndex (0));
    assertFalse (aColl.addAllMapped ((Iterable <String>) null, aMapper).isChanged ());

    aColl.clear ();
    assertTrue (aColl.addAllMapped (new String [] { "a", "b" }, aMapper).isChanged ());
    assertFalse (aColl.addAllMapped ((String []) null, aMapper).isChanged ());

    aColl.clear ();
    assertTrue (aColl.addAllMapped (_createList (), aSrcFilter, aMapper).isChanged ());
    assertEquals (1, aColl.size ());
    assertFalse (aColl.addAllMapped ((Iterable <String>) null, aAllFilter, aMapper).isChanged ());

    aColl.clear ();
    assertTrue (aColl.addAllMapped (new String [] { "a", "b" }, aSrcFilter, aMapper).isChanged ());
    assertEquals (1, aColl.size ());
    assertFalse (aColl.addAllMapped ((String []) null, aAllFilter, aMapper).isChanged ());

    aColl.clear ();
    assertTrue (aColl.addAllMapped (_createList (), aMapper, aDstFilter).isChanged ());
    assertEquals (1, aColl.size ());
    assertFalse (aColl.addAllMapped ((Iterable <String>) null, aMapper, aAllFilter).isChanged ());

    aColl.clear ();
    assertTrue (aColl.addAllMapped (new String [] { "a", "b" }, aMapper, aDstFilter).isChanged ());
    assertEquals (1, aColl.size ());
    assertFalse (aColl.addAllMapped ((String []) null, aMapper, aAllFilter).isChanged ());
  }

  @Test
  public void testSetAndRemove ()
  {
    final ICommonsCollection <String> aColl = new CommonsArrayList <> ();

    assertTrue (aColl.set ("a").isChanged ());
    assertEquals (1, aColl.size ());
    // "set" always adds the provided value - even if it is null
    assertTrue (aColl.set (null).isChanged ());
    assertEquals (1, aColl.size ());
    assertNull (aColl.getAtIndex (0));

    assertTrue (aColl.setAll (_createList ()).isChanged ());
    assertEquals (3, aColl.size ());
    assertTrue (aColl.setAll ((Iterable <String>) null).isChanged ());
    assertTrue (aColl.isEmpty ());

    assertTrue (aColl.setAll ("a", "b").isChanged ());
    assertEquals (2, aColl.size ());
    assertTrue (aColl.setAll ((String []) null).isChanged ());

    assertTrue (aColl.setAllMapped (_createList (), x -> "#" + x).isChanged ());
    assertEquals (3, aColl.size ());
    assertTrue (aColl.setAllMapped ((Iterable <String>) null, x -> "#" + x).isChanged ());

    assertTrue (aColl.setAllMapped (new String [] { "a", "b" }, x -> "#" + x).isChanged ());
    assertEquals (2, aColl.size ());
    assertTrue (aColl.setAllMapped ((String []) null, x -> "#" + x).isChanged ());

    aColl.setAll (_createList ());
    assertTrue (aColl.removeObject ("a").isChanged ());
    assertFalse (aColl.removeObject ("a").isChanged ());
    assertFalse (aColl.removeObject (null).isChanged ());
    assertTrue (aColl.removeAll ().isChanged ());
    assertFalse (aColl.removeAll ().isChanged ());
  }

  @Test
  public void testUnmodifiableAndIterator ()
  {
    final ICommonsCollection <String> aColl = _createList ();

    final Collection <String> aUnmodifiable = aColl.getAsUnmodifiable ();
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

    assertNotNull (aColl.iterator2 ());
    int nCount = 0;
    for (final String s : aColl.iterator2 ())
    {
      assertNotNull (s);
      nCount++;
    }
    assertEquals (3, nCount);
  }

  @Test
  public void testIterableForEach ()
  {
    final ICommonsList <String> aList = _createList ();

    final MutableInt aCount = new MutableInt (0);
    aList.forEachByIndex ((nIndex, sElement) -> aCount.inc ());
    assertEquals (3, aCount.intValue ());

    aCount.set (0);
    assertTrue (aList.forEachBreakable (x -> {
      aCount.inc ();
      return EContinue.CONTINUE;
    }).isContinue ());
    assertEquals (3, aCount.intValue ());

    aCount.set (0);
    assertTrue (aList.forEachBreakable (x -> {
      aCount.inc ();
      return EContinue.BREAK;
    }).isBreak ());
    assertEquals (1, aCount.intValue ());

    aCount.set (0);
    aList.forEachThrowing (x -> aCount.inc ());
    assertEquals (3, aCount.intValue ());
  }

  @Test
  public void testIterableFind ()
  {
    final ICommonsList <String> aList = _createList ();
    final Function <String, String> aMapper = x -> "#" + x;
    final Predicate <String> aSrcFilter = x -> x.equals ("a");
    final Predicate <String> aDstFilter = x -> x.equals ("#a");

    final ICommonsList <String> aDst = new CommonsArrayList <> ();
    aList.findAll (x -> x.equals ("a"), aDst::add);
    assertEquals (1, aDst.size ());
    aDst.clear ();
    aList.findAll (null, aDst::add);
    assertEquals (3, aDst.size ());

    aDst.clear ();
    aList.findAllMapped (aMapper, aDst::add);
    assertEquals (new CommonsArrayList <> ("#a", "#b", "#c"), aDst);

    aDst.clear ();
    aList.findAllMapped (aSrcFilter, aMapper, aDst::add);
    assertEquals (new CommonsArrayList <> ("#a"), aDst);
    aDst.clear ();
    aList.findAllMapped ((Predicate <String>) null, aMapper, aDst::add);
    assertEquals (3, aDst.size ());

    aDst.clear ();
    aList.findAllMapped (aMapper, aDstFilter, aDst::add);
    assertEquals (new CommonsArrayList <> ("#a"), aDst);
    aDst.clear ();
    aList.findAllMapped (aMapper, (Predicate <String>) null, aDst::add);
    assertEquals (3, aDst.size ());

    final ICommonsList <Object> aObjects = new CommonsArrayList <> ("a", Integer.valueOf (1), "b");
    final ICommonsList <String> aStrings = new CommonsArrayList <> ();
    aObjects.findAllInstanceOf (String.class, aStrings::add);
    assertEquals (2, aStrings.size ());

    assertEquals ("a", aList.findFirst (x -> x.equals ("a")));
    assertNull (aList.findFirst (x -> false));
    assertEquals ("a", aList.findFirst (null));
    assertEquals ("x", aList.findFirst (x -> false, "x"));
    assertEquals ("a", aList.findFirst (x -> true, "x"));

    assertEquals ("#a", aList.findFirstMapped (aSrcFilter, aMapper));
    assertNull (aList.findFirstMapped (x -> false, aMapper));
    assertEquals ("x", aList.findFirstMapped (x -> false, aMapper, "x"));
    assertEquals ("#a", aList.findFirstMapped (x -> true, aMapper, "x"));
  }

  @Test
  public void testIterableContains ()
  {
    final ICommonsList <String> aList = _createList ();

    assertTrue (aList.containsAny (x -> x.equals ("a")));
    assertFalse (aList.containsAny (x -> false));
    assertTrue (aList.containsAny (null));
    assertFalse (aList.containsNone (x -> x.equals ("a")));
    assertTrue (aList.containsNone (x -> false));
    assertFalse (aList.containsNone (null));
    assertTrue (aList.containsOnly (x -> true));
    assertFalse (aList.containsOnly (x -> x.equals ("a")));

    assertEquals (3, aList.size ());
    assertEquals (3, aList.getCount (null));
    assertEquals (1, aList.getCount (x -> x.equals ("a")));

    assertEquals (0, aList.findFirstIndex (x -> x.equals ("a")));
    assertEquals (-1, aList.findFirstIndex (x -> false));
    assertEquals (2, aList.findLastIndex (x -> x.equals ("c")));
    assertEquals (-1, aList.findLastIndex (x -> false));

    // Empty list
    final ICommonsList <String> aEmpty = new CommonsArrayList <> ();
    assertTrue (aEmpty.isEmpty ());
    assertFalse (aEmpty.containsAny (x -> true));
    assertTrue (aEmpty.containsNone (x -> true));
    assertFalse (aEmpty.containsOnly (x -> true));
    assertEquals (0, aEmpty.getCount (x -> true));
  }
}
