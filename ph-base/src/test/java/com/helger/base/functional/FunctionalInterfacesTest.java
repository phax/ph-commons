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
package com.helger.base.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

/**
 * Test class for the functional interfaces {@link IBooleanPredicate}, {@link ICharPredicate},
 * {@link IBooleanConsumer}, {@link ICharConsumer}, {@link ITriConsumer}, {@link IThrowingConsumer}
 * and {@link IThrowingFunction}.
 *
 * @author Philip Helger
 */
public final class FunctionalInterfacesTest
{
  @Test
  public void testBooleanPredicate ()
  {
    assertTrue (IBooleanPredicate.all ().test (true));
    assertTrue (IBooleanPredicate.all ().test (false));
    assertFalse (IBooleanPredicate.none ().test (true));
    assertFalse (IBooleanPredicate.none ().test (false));

    assertTrue (IBooleanPredicate.ifTrue ().test (true));
    assertFalse (IBooleanPredicate.ifTrue ().test (false));
    assertFalse (IBooleanPredicate.ifFalse ().test (true));
    assertTrue (IBooleanPredicate.ifFalse ().test (false));

    // negate
    assertFalse (IBooleanPredicate.ifTrue ().negate ().test (true));
    assertTrue (IBooleanPredicate.ifTrue ().negate ().test (false));

    // and
    assertTrue (IBooleanPredicate.ifTrue ().and (IBooleanPredicate.all ()).test (true));
    assertFalse (IBooleanPredicate.ifTrue ().and (IBooleanPredicate.none ()).test (true));

    // or
    assertTrue (IBooleanPredicate.ifTrue ().or (IBooleanPredicate.none ()).test (true));
    assertTrue (IBooleanPredicate.none ().or (IBooleanPredicate.all ()).test (false));
    assertFalse (IBooleanPredicate.none ().or (IBooleanPredicate.none ()).test (false));
  }

  @Test
  public void testCharPredicate ()
  {
    final ICharPredicate aIsA = x -> x == 'a';

    assertTrue (aIsA.test ('a'));
    assertFalse (aIsA.test ('b'));

    assertFalse (aIsA.negate ().test ('a'));
    assertTrue (aIsA.negate ().test ('b'));

    final ICharPredicate aIsLower = x -> Character.isLowerCase (x);
    assertTrue (aIsA.and (aIsLower).test ('a'));
    assertFalse (aIsA.and (aIsLower).test ('A'));

    assertTrue (aIsA.or (aIsLower).test ('b'));
    assertFalse (aIsA.or (aIsLower).test ('B'));
  }

  @Test
  public void testBooleanConsumer ()
  {
    final List <String> aCalls = new ArrayList <> ();
    final IBooleanConsumer a1 = x -> aCalls.add ("1:" + x);
    final IBooleanConsumer a2 = x -> aCalls.add ("2:" + x);

    a1.accept (true);
    assertEquals (1, aCalls.size ());

    aCalls.clear ();
    a1.andThen (a2).accept (false);
    assertEquals (2, aCalls.size ());
    assertEquals ("1:false", aCalls.get (0));
    assertEquals ("2:false", aCalls.get (1));

    // A null "after" returns the first one
    assertSame (a1, a1.andThen (null));

    // The static combinator handles all null combinations
    assertNull (IBooleanConsumer.and (null, null));
    assertSame (a1, IBooleanConsumer.and (a1, null));
    assertSame (a2, IBooleanConsumer.and (null, a2));
    assertNotNull (IBooleanConsumer.and (a1, a2));
  }

  @Test
  public void testCharConsumer ()
  {
    final List <String> aCalls = new ArrayList <> ();
    final ICharConsumer a1 = x -> aCalls.add ("1:" + x);
    final ICharConsumer a2 = x -> aCalls.add ("2:" + x);

    a1.accept ('a');
    assertEquals (1, aCalls.size ());

    aCalls.clear ();
    a1.andThen (a2).accept ('b');
    assertEquals (2, aCalls.size ());

    assertSame (a1, a1.andThen (null));
    assertNull (ICharConsumer.and (null, null));
    assertSame (a1, ICharConsumer.and (a1, null));
    assertSame (a2, ICharConsumer.and (null, a2));
    assertNotNull (ICharConsumer.and (a1, a2));
  }

  @Test
  public void testTriConsumer ()
  {
    final AtomicInteger aCount = new AtomicInteger (0);
    final ITriConsumer <String, String, String> a1 = (x, y, z) -> aCount.incrementAndGet ();
    final ITriConsumer <String, String, String> a2 = (x, y, z) -> aCount.addAndGet (10);

    a1.accept ("a", "b", "c");
    assertEquals (1, aCount.get ());

    a1.andThen (a2).accept ("a", "b", "c");
    assertEquals (12, aCount.get ());

    // A null "after" returns the first one
    assertSame (a1, a1.andThen (null));
  }

  @Test
  public void testThrowingConsumer () throws Exception
  {
    final List <String> aCalls = new ArrayList <> ();
    final IThrowingConsumer <String, Exception> a1 = x -> aCalls.add ("1:" + x);
    final IThrowingConsumer <String, Exception> a2 = x -> aCalls.add ("2:" + x);

    a1.accept ("v");
    assertEquals (1, aCalls.size ());

    aCalls.clear ();
    a1.andThen (a2).accept ("v");
    assertEquals (2, aCalls.size ());

    assertSame (a1, a1.andThen (null));

    // An exception is propagated
    final IThrowingConsumer <String, Exception> aEx = x -> { throw new IllegalStateException ("mock"); };
    try
    {
      aEx.accept ("v");
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
    }
  }

  @Test
  public void testThrowingFunction () throws Exception
  {
    final IThrowingFunction <String, Integer, Exception> aLength = x -> Integer.valueOf (x.length ());
    final IThrowingFunction <Integer, String, Exception> aToString = x -> "len=" + x;

    assertEquals (Integer.valueOf (3), aLength.apply ("abc"));
    assertEquals ("len=3", aLength.andThen (aToString).apply ("abc"));
    assertEquals (Integer.valueOf (5), aLength.compose ((String x) -> x + "xx").apply ("abc"));

    try
    {
      aLength.andThen (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {
      // expected
    }
    try
    {
      aLength.compose (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {
      // expected
    }
  }
}
