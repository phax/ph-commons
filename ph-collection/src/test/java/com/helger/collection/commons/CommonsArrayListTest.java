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
package com.helger.collection.commons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import com.helger.base.numeric.mutable.MutableInt;
import com.helger.base.state.EContinue;
import com.helger.collection.CollectionTestHelper;

/**
 * Test class for class {@link CommonsArrayList}.
 *
 * @author Philip Helger
 */
public final class CommonsArrayListTest
{
  @Test
  public void testBasic ()
  {
    final ICommonsList <String> aTest = new CommonsArrayList <> ();
    assertTrue (aTest.isEmpty ());
    aTest.add ("aaa");
    aTest.add ("bbb");
    aTest.add ("ccc");

    CollectionTestHelper.testGetClone (aTest);
  }

  @Test
  public void testGetFirstAndLast ()
  {
    final ICommonsList <String> aTest = new CommonsArrayList <> ();
    assertTrue (aTest.isEmpty ());
    assertNull (aTest.getFirstOrNull ());
    assertNull (aTest.getLastOrNull ());
    assertNull (aTest.removeFirstOrNull ());
    assertNull (aTest.removeLastOrNull ());
  }

  @Test
  public void testCtor ()
  {
    CommonsArrayList <String> aTest = new CommonsArrayList <> ();
    assertEquals (0, aTest.size ());

    aTest = new CommonsArrayList <> (5);
    assertEquals (0, aTest.size ());

    aTest = new CommonsArrayList <> ("a", "b", "c");
    assertEquals (3, aTest.size ());

    aTest = new CommonsArrayList <> ("only");
    assertEquals (1, aTest.size ());

    aTest = new CommonsArrayList <> (new CommonsArrayList <> ("a", "b", "c"));
    assertEquals (3, aTest.size ());

    aTest = new CommonsArrayList <> ((Iterable <String>) new CommonsArrayList <> ("a", "b", "c", "d"));
    assertEquals (4, aTest.size ());

    aTest = new CommonsArrayList <> (new CommonsArrayList <> (Integer.valueOf (1), Integer.valueOf (2)),
                                     x -> x.toString ());
    assertEquals (2, aTest.size ());

    aTest = new CommonsArrayList <> ((Iterable <Integer>) new CommonsArrayList <> (Integer.valueOf (1),
                                                                                   Integer.valueOf (2),
                                                                                   Integer.valueOf (4)),
                                     x -> x.toString ());
    assertEquals (3, aTest.size ());
  }

  @Test
  public void testForEachBreakable ()
  {
    final CommonsArrayList <String> aTest = new CommonsArrayList <> ("a", "b", "c");
    final MutableInt aCounter = new MutableInt (0);
    aTest.forEachBreakable (x -> {
      aCounter.inc ();
      return x.equals ("b") ? EContinue.BREAK : EContinue.CONTINUE;
    });
    assertEquals (2, aCounter.intValue ());
  }

  @Test
  public void testFindIndex ()
  {
    final ICommonsList <String> aTest = new CommonsArrayList <> ();
    aTest.add ("a");
    aTest.add ("b");
    aTest.add ("c");
    aTest.add ("b");
    aTest.add ("a");

    assertEquals (0, aTest.findFirstIndex (x -> x.equals ("a")));
    assertEquals (1, aTest.findFirstIndex (x -> x.equals ("b")));
    assertEquals (2, aTest.findFirstIndex (x -> x.equals ("c")));
    assertEquals (-1, aTest.findFirstIndex (x -> x.equals ("d")));

    assertEquals (4, aTest.findLastIndex (x -> x.equals ("a")));
    assertEquals (3, aTest.findLastIndex (x -> x.equals ("b")));
    assertEquals (2, aTest.findLastIndex (x -> x.equals ("c")));
    assertEquals (-1, aTest.findLastIndex (x -> x.equals ("d")));
  }

  @Test
  public void testForEachThrowing ()
  {
    final ICommonsList <String> aTest = new CommonsArrayList <> ("a");

    try
    {
      aTest.forEachThrowing (s -> { throw new IllegalStateException (s); });
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      assertEquals ("a", ex.getMessage ());
    }

    try
    {
      aTest.forEachThrowing (s -> { throw new IOException (s); });
      fail ();
    }
    catch (final IOException ex)
    {
      assertEquals ("a", ex.getMessage ());
    }
  }
}
