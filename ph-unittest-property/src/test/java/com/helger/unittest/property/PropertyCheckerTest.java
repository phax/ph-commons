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
package com.helger.unittest.property;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.junit.Test;

/**
 * Test class for class {@link PropertyChecker}. The shrinking tests run with many seeds, as a single
 * seed may hide weaknesses of the shrinker.
 *
 * @author Philip Helger
 */
public final class PropertyCheckerTest
{
  private static final int SEED_COUNT = 50;

  @FunctionalInterface
  private interface IRunnable
  {
    void run () throws Exception;
  }

  private static PropertyFailedError _expectFailure (final IRunnable aRunnable) throws Exception
  {
    try
    {
      aRunnable.run ();
    }
    catch (final PropertyFailedError ex)
    {
      return ex;
    }
    fail ("Expected the property to fail");
    return null;
  }

  /**
   * Run the failing property with {@link #SEED_COUNT} different seeds and check each shrunk sample.
   */
  private static <T> void _checkShrinkingForManySeeds (final IGenerator <T> aGenerator,
                                                       final IProperty1 <? super T> aProperty,
                                                       final Consumer <PropertyFailedError> aCheck) throws Exception
  {
    for (int nSeed = 1; nSeed <= SEED_COUNT; ++nSeed)
    {
      final PropertyChecker aChecker = new PropertyChecker ().setTries (1000).setSeed (nSeed);
      final PropertyFailedError ex = _expectFailure ( () -> aChecker.forAll (aGenerator, aProperty));
      try
      {
        aCheck.accept (ex);
      }
      catch (final AssertionError ex2)
      {
        throw new AssertionError ("Seed " + nSeed + ": " + ex.getMessage (), ex2);
      }
    }
  }

  @Test
  public void testPassingPropertyRunsAllTries ()
  {
    final AtomicInteger aCounter = new AtomicInteger ();
    new PropertyChecker ().setTries (123).forAll (Generators.ints (), aValue -> aCounter.incrementAndGet ());
    assertEquals (123, aCounter.get ());
  }

  @Test
  public void testShrinkIntToBoundary () throws Exception
  {
    _checkShrinkingForManySeeds (Generators.ints (),
                                 aValue -> assertTrue (aValue.intValue () < 1000),
                                 ex -> assertEquals (Integer.valueOf (1000), ex.getShrunkSample ()));
    _checkShrinkingForManySeeds (Generators.ints (),
                                 aValue -> assertTrue (aValue.intValue () > -50),
                                 ex -> assertEquals (Integer.valueOf (-50), ex.getShrunkSample ()));
  }

  @Test
  public void testShrinkThroughMap () throws Exception
  {
    // Shrinking works on the choices, so it also works for mapped values
    final IGenerator <Integer> aEven = Generators.ints (0, 1_000_000)
                                                 .map (aValue -> Integer.valueOf (aValue.intValue () * 2));
    _checkShrinkingForManySeeds (aEven,
                                 aValue -> assertTrue (aValue.intValue () < 777),
                                 ex -> assertEquals (Integer.valueOf (778), ex.getShrunkSample ()));
  }

  @Test
  public void testShrinkByteArray () throws Exception
  {
    // Fails for any negative byte - the minimal counterexample is a single -1
    _checkShrinkingForManySeeds (Generators.byteArrays (), aBytes -> {
      for (final byte b : aBytes)
        assertTrue (b >= 0);
    }, ex -> assertArrayEquals (new byte [] { -1 }, (byte []) ex.getShrunkSample ()));
  }

  @Test
  public void testShrinkString () throws Exception
  {
    _checkShrinkingForManySeeds (Generators.strings ("abcxyz", 0, 100),
                                 sText -> assertTrue (sText.indexOf ("xy") < 0),
                                 ex -> assertEquals ("xy", ex.getShrunkSample ()));
  }

  @Test
  public void testShrinkListSum () throws Exception
  {
    // The redistribute pass moves value to later elements: the minimum is [1, 100]
    _checkShrinkingForManySeeds (Generators.lists (Generators.ints (0, 100), 0, 50), aList -> {
      int nSum = 0;
      for (final Integer aValue : aList)
        nSum += aValue.intValue ();
      assertTrue (nSum <= 100);
    }, ex -> assertEquals (List.of (Integer.valueOf (1), Integer.valueOf (100)), ex.getShrunkSample ()));
  }

  @Test
  public void testShrinkTwoArguments () throws Exception
  {
    for (int nSeed = 1; nSeed <= SEED_COUNT; ++nSeed)
    {
      final PropertyChecker aChecker = new PropertyChecker ().setSeed (nSeed);
      final PropertyFailedError ex = _expectFailure ( () -> aChecker.forAll (Generators.ints (0, 1000),
                                                                             Generators.ints (0, 1000),
                                                                             (aA, aB) -> assertTrue (aA.intValue () +
                                                                                                     aB.intValue () < 50)));
      assertArrayEquals (ex.getMessage (),
                         new Object [] { Integer.valueOf (0), Integer.valueOf (50) },
                         (Object []) ex.getShrunkSample ());
    }
  }

  @Test
  public void testShrinkFourArguments () throws Exception
  {
    final IGenerator <Integer> aGen = Generators.ints (0, 1000);
    for (int nSeed = 1; nSeed <= SEED_COUNT; ++nSeed)
    {
      final PropertyChecker aChecker = new PropertyChecker ().setSeed (nSeed);
      final PropertyFailedError ex = _expectFailure ( () -> aChecker.forAll (aGen,
                                                                             aGen,
                                                                             aGen,
                                                                             aGen,
                                                                             (aA, aB, aC, aD) -> assertTrue (aA.intValue () +
                                                                                                             aB.intValue () +
                                                                                                             aC.intValue () +
                                                                                                             aD.intValue () < 50)));
      assertArrayEquals (ex.getMessage (),
                         new Object [] { Integer.valueOf (0), Integer.valueOf (0), Integer.valueOf (0), Integer.valueOf (50) },
                         (Object []) ex.getShrunkSample ());
    }
  }

  @Test
  public void testShrinkKeepsExceptionClass () throws Exception
  {
    // 0 is simpler, but fails with a different exception, so it must not be accepted
    _checkShrinkingForManySeeds (Generators.ints (1, 1000), aValue -> {
      if (aValue.intValue () > 10)
        throw new IllegalArgumentException ("big");
    }, ex -> {
      assertEquals (Integer.valueOf (11), ex.getShrunkSample ());
      assertTrue (ex.getCause () instanceof IllegalArgumentException);
    });
    _checkShrinkingForManySeeds (Generators.ints (0, 1000), aValue -> {
      if (aValue.intValue () == 0)
        throw new IllegalStateException ("zero");
      if (aValue.intValue () > 10)
        throw new IllegalArgumentException ("big");
    }, ex -> {
      if (ex.getCause () instanceof IllegalArgumentException)
        assertEquals (Integer.valueOf (11), ex.getShrunkSample ());
      else
        assertEquals (Integer.valueOf (0), ex.getShrunkSample ());
    });
  }

  @Test
  public void testEdgeCasesAreFound () throws Exception
  {
    // A uniform distribution would practically never hit the maximum
    _checkShrinkingForManySeeds (Generators.ints (),
                                 aValue -> assertTrue (aValue.intValue () != Integer.MAX_VALUE),
                                 ex -> assertEquals (Integer.valueOf (Integer.MAX_VALUE), ex.getShrunkSample ()));
  }

  @Test
  public void testSameSeedSameResult () throws Exception
  {
    final IRunnable aRun = () -> new PropertyChecker ().setSeed (4711)
                                                       .forAll (Generators.byteArrays (),
                                                                aBytes -> assertTrue (aBytes.length < 20));
    final PropertyFailedError ex1 = _expectFailure (aRun);
    final PropertyFailedError ex2 = _expectFailure (aRun);
    assertEquals (ex1.getTry (), ex2.getTry ());
    assertArrayEquals ((byte []) ex1.getOriginalSample (), (byte []) ex2.getOriginalSample ());
    assertArrayEquals ((byte []) ex1.getShrunkSample (), (byte []) ex2.getShrunkSample ());
    assertArrayEquals (new byte [20], (byte []) ex1.getShrunkSample ());
  }

  @Test
  public void testAssumeAndFilter ()
  {
    final AtomicInteger aCounter = new AtomicInteger ();
    new PropertyChecker ().forAll (Generators.ints (0, 100).filter (aValue -> aValue.intValue () % 2 == 0), aValue -> {
      PropertyAssume.that (aValue.intValue () != 42);
      assertEquals (0, aValue.intValue () % 2);
      aCounter.incrementAndGet ();
    });
    assertEquals (PropertyChecker.DEFAULT_TRIES, aCounter.get ());
  }

  @Test
  public void testTooManyRejects ()
  {
    try
    {
      new PropertyChecker ().forAll (Generators.ints (), aValue -> PropertyAssume.that (false));
      fail ();
    }
    catch (final PropertyFailedError ex)
    {
      fail ("Rejects are no property failure");
    }
    catch (final AssertionError ex)
    {
      assertTrue (ex.getMessage ().startsWith ("Too many rejected samples"));
    }
  }

  @Test
  public void testFlatMap () throws Exception
  {
    // The array length is chosen first, then the content
    final IGenerator <byte []> aGen = Generators.ints (1, 10)
                                                .flatMap (aSize -> Generators.byteArrays (aSize.intValue (),
                                                                                          aSize.intValue ()));
    new PropertyChecker ().forAll (aGen, aBytes -> assertTrue (aBytes.length >= 1 && aBytes.length <= 10));

    _checkShrinkingForManySeeds (aGen,
                                 aBytes -> assertTrue (aBytes.length < 5),
                                 ex -> assertArrayEquals (new byte [5], (byte []) ex.getShrunkSample ()));
  }

  @Test
  public void testUnicodeStringsHaveNoLoneSurrogates ()
  {
    new PropertyChecker ().forAll (Generators.unicodeStrings (), sText -> {
      for (int i = 0; i < sText.length (); ++i)
      {
        final char c = sText.charAt (i);
        if (Character.isHighSurrogate (c))
        {
          assertTrue (i + 1 < sText.length ());
          assertTrue (Character.isLowSurrogate (sText.charAt (i + 1)));
          ++i;
        }
        else
          assertFalse (Character.isLowSurrogate (c));
      }
    });
  }

  @Test
  public void testFailureMessage () throws Exception
  {
    final PropertyFailedError ex = _expectFailure ( () -> new PropertyChecker ().setSeed (42)
                                                                                 .forAll (Generators.strings ("ab\u00e4",
                                                                                                              1,
                                                                                                              10),
                                                                                          sText -> assertTrue (sText.indexOf ('\u00e4') <
                                                                                                               0)));
    assertEquals ("\u00e4", ex.getShrunkSample ());
    assertEquals (42, ex.getSeed ());
    assertTrue (ex.getMessage (), ex.getMessage ().contains ("Shrunk sample"));
    assertTrue (ex.getMessage (), ex.getMessage ().contains ("\"\\u00e4\""));
    assertTrue (ex.getMessage (), ex.getMessage ().contains ("-D" + PropertyChecker.SYSTEM_PROPERTY_SEED + "=42"));
  }
}
