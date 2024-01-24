/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
package com.helger.commons.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigInteger;

import org.junit.Test;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsSet;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link CombinationGenerator}.
 *
 * @author Philip Helger
 */
public final class CombinationGeneratorTest
{
  private static final String A = "A";
  private static final String B = "B";
  private static final String C = "C";

  @Test
  public void testStringCombination ()
  {
    final ICommonsList <String> aElements = new CommonsArrayList <> (A, B, B, C);
    final CombinationGenerator <String> x = new CombinationGenerator <> (aElements, 3);
    assertEquals (BigInteger.valueOf (4), x.getTotalCombinations ());
    assertEquals (BigInteger.valueOf (4), x.getCombinationsLeft ());

    final ICommonsList <ICommonsList <String>> aResultsWithDuplicates = new CommonsArrayList <> ();
    final ICommonsSet <ICommonsList <String>> aResultsWithoutDuplicates = new CommonsHashSet <> ();
    while (x.hasNext ())
    {
      final ICommonsList <String> aResult = x.next ();
      aResultsWithDuplicates.add (aResult);
      aResultsWithoutDuplicates.add (aResult);
    }
    assertEquals (4, aResultsWithDuplicates.size ());
    assertEquals (3, aResultsWithoutDuplicates.size ());

    try
    {
      x.remove ();
      fail ();
    }
    catch (final UnsupportedOperationException ex)
    {}
  }

  @Test
  public void testStringCombination2 ()
  {
    final ICommonsList <String> aElements = new CommonsArrayList <> (A, B, B, C);
    final CombinationGenerator <String> x = new CombinationGenerator <> (aElements, 0);
    assertEquals (BigInteger.ONE, x.getTotalCombinations ());
    assertEquals (BigInteger.ONE, x.getCombinationsLeft ());

    final ICommonsList <ICommonsList <String>> aResultsWithDuplicates = new CommonsArrayList <> ();
    final ICommonsSet <ICommonsList <String>> aResultsWithoutDuplicates = new CommonsHashSet <> ();
    while (x.hasNext ())
    {
      final ICommonsList <String> aResult = x.next ();
      aResultsWithDuplicates.add (aResult);
      aResultsWithoutDuplicates.add (aResult);
    }
    assertEquals (1, aResultsWithDuplicates.size ());
    assertEquals (1, aResultsWithoutDuplicates.size ());
  }

  @Test
  @SuppressFBWarnings ({ "NP_NONNULL_PARAM_VIOLATION", "TQ_NEVER_VALUE_USED_WHERE_ALWAYS_REQUIRED" })
  public void testCtor ()
  {
    try
    {
      new CombinationGenerator <String> (null, 3);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      new CombinationGenerator <> (new CommonsArrayList <> (), 3);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      new CombinationGenerator <> (new CommonsArrayList <> ("a"), 3);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      new CombinationGenerator <> (new CommonsArrayList <> ("a", "b"), -1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }
}
