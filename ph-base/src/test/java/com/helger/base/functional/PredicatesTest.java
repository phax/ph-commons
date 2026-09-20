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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.function.DoublePredicate;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;
import java.util.function.Predicate;

import org.junit.Test;

/**
 * Test class for class {@link Predicates}.
 *
 * @author Philip Helger
 */
public final class PredicatesTest
{
  @Test
  public void testAllAndNone ()
  {
    final Predicate <String> aAll = Predicates.all ();
    assertTrue (aAll.test ("any"));
    assertTrue (aAll.test (null));

    final Predicate <String> aNone = Predicates.none ();
    assertFalse (aNone.test ("any"));
    assertFalse (aNone.test (null));

    assertTrue (Predicates.notNull ().test ("any"));
    assertFalse (Predicates.notNull ().test (null));

    assertFalse (Predicates.isNull ().test ("any"));
    assertTrue (Predicates.isNull ().test (null));
  }

  @Test
  public void testCharPredicates ()
  {
    assertTrue (Predicates.charIsEQ0 ().test ((char) 0));
    assertFalse (Predicates.charIsEQ0 ().test ('a'));

    assertFalse (Predicates.charIsNE0 ().test ((char) 0));
    assertTrue (Predicates.charIsNE0 ().test ('a'));

    assertFalse (Predicates.charIsGT0 ().test ((char) 0));
    assertTrue (Predicates.charIsGT0 ().test ('a'));
  }

  @Test
  public void testDoublePredicates ()
  {
    final DoublePredicate [] aAll = { Predicates.doubleIsLT0 (),
                                      Predicates.doubleIsLE0 (),
                                      Predicates.doubleIsEQ0 (),
                                      Predicates.doubleIsNE0 (),
                                      Predicates.doubleIsGE0 (),
                                      Predicates.doubleIsGT0 () };
    for (final DoublePredicate a : aAll)
      assertNotNull (a);

    assertTrue (Predicates.doubleIsLT0 ().test (-1));
    assertFalse (Predicates.doubleIsLT0 ().test (0));
    assertTrue (Predicates.doubleIsLE0 ().test (0));
    assertFalse (Predicates.doubleIsLE0 ().test (1));
    assertTrue (Predicates.doubleIsEQ0 ().test (0));
    assertFalse (Predicates.doubleIsEQ0 ().test (1));
    assertTrue (Predicates.doubleIsNE0 ().test (1));
    assertFalse (Predicates.doubleIsNE0 ().test (0));
    assertTrue (Predicates.doubleIsGE0 ().test (0));
    assertFalse (Predicates.doubleIsGE0 ().test (-1));
    assertTrue (Predicates.doubleIsGT0 ().test (1));
    assertFalse (Predicates.doubleIsGT0 ().test (0));
  }

  @Test
  public void testIntPredicates ()
  {
    final IntPredicate [] aAll = { Predicates.intIsLT0 (),
                                   Predicates.intIsLE0 (),
                                   Predicates.intIsEQ0 (),
                                   Predicates.intIsNE0 (),
                                   Predicates.intIsGE0 (),
                                   Predicates.intIsGT0 () };
    for (final IntPredicate a : aAll)
      assertNotNull (a);

    assertTrue (Predicates.intIsLT0 ().test (-1));
    assertFalse (Predicates.intIsLT0 ().test (0));
    assertTrue (Predicates.intIsLE0 ().test (0));
    assertFalse (Predicates.intIsLE0 ().test (1));
    assertTrue (Predicates.intIsEQ0 ().test (0));
    assertFalse (Predicates.intIsEQ0 ().test (1));
    assertTrue (Predicates.intIsNE0 ().test (1));
    assertFalse (Predicates.intIsNE0 ().test (0));
    assertTrue (Predicates.intIsGE0 ().test (0));
    assertFalse (Predicates.intIsGE0 ().test (-1));
    assertTrue (Predicates.intIsGT0 ().test (1));
    assertFalse (Predicates.intIsGT0 ().test (0));
  }

  @Test
  public void testLongPredicates ()
  {
    final LongPredicate [] aAll = { Predicates.longIsLT0 (),
                                    Predicates.longIsLE0 (),
                                    Predicates.longIsEQ0 (),
                                    Predicates.longIsNE0 (),
                                    Predicates.longIsGE0 (),
                                    Predicates.longIsGT0 () };
    for (final LongPredicate a : aAll)
      assertNotNull (a);

    assertTrue (Predicates.longIsLT0 ().test (-1L));
    assertFalse (Predicates.longIsLT0 ().test (0L));
    assertTrue (Predicates.longIsLE0 ().test (0L));
    assertFalse (Predicates.longIsLE0 ().test (1L));
    assertTrue (Predicates.longIsEQ0 ().test (0L));
    assertFalse (Predicates.longIsEQ0 ().test (1L));
    assertTrue (Predicates.longIsNE0 ().test (1L));
    assertFalse (Predicates.longIsNE0 ().test (0L));
    assertTrue (Predicates.longIsGE0 ().test (0L));
    assertFalse (Predicates.longIsGE0 ().test (-1L));
    assertTrue (Predicates.longIsGT0 ().test (1L));
    assertFalse (Predicates.longIsGT0 ().test (0L));
  }

  @Test
  public void testAnd ()
  {
    final Predicate <String> aNotNull = Predicates.notNull ();
    final Predicate <String> aLong = x -> x != null && x.length () > 3;

    // Both null returns null
    assertNull (Predicates.and (null, null));
    // Only one present delegates to that one
    assertTrue (Predicates.and (aNotNull, null).test ("any"));
    assertFalse (Predicates.and (aNotNull, null).test (null));
    assertTrue (Predicates.and (null, aLong).test ("abcd"));
    assertFalse (Predicates.and (null, aLong).test ("ab"));

    final Predicate <String> aBoth = Predicates.and (aNotNull, aLong);
    assertNotNull (aBoth);
    assertTrue (aBoth.test ("abcd"));
    assertFalse (aBoth.test ("ab"));
    assertFalse (aBoth.test (null));
  }

  @Test
  public void testOr ()
  {
    final Predicate <String> aIsA = "a"::equals;
    final Predicate <String> aIsB = "b"::equals;

    assertNull (Predicates.or (null, null));
    assertTrue (Predicates.or (aIsA, null).test ("a"));
    assertFalse (Predicates.or (aIsA, null).test ("b"));
    assertTrue (Predicates.or (null, aIsB).test ("b"));
    assertFalse (Predicates.or (null, aIsB).test ("a"));

    final Predicate <String> aEither = Predicates.or (aIsA, aIsB);
    assertNotNull (aEither);
    assertTrue (aEither.test ("a"));
    assertTrue (aEither.test ("b"));
    assertFalse (aEither.test ("c"));
  }
}
