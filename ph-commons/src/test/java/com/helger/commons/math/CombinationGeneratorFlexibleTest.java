/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.timing.StopWatch;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link CombinationGeneratorFlexible}.
 *
 * @author Philip Helger
 */
public final class CombinationGeneratorFlexibleTest extends AbstractCombinationGeneratorTestCase
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (CombinationGeneratorFlexibleTest.class);

  @Test
  @SuppressFBWarnings ("TQ_NEVER_VALUE_USED_WHERE_ALWAYS_REQUIRED")
  public void testStringCombination ()
  {
    final ICommonsList <String> aElements = new CommonsArrayList <> ("A", "B", "B");

    // Allow empty
    CombinationGeneratorFlexible <String> aGenerator = new CombinationGeneratorFlexible <> (3, true);

    Set <ICommonsList <String>> aResults = aGenerator.getCombinations (aElements);
    assertEquals (6, aResults.size ());
    assertTrue (aResults.contains (new CommonsArrayList <> ()));

    aResults = aGenerator.getCombinations (new CommonsArrayList <> ());
    assertEquals (1, aResults.size ());
    assertTrue (aResults.contains (new CommonsArrayList <> ()));

    // Not allowing empty
    aGenerator = new CombinationGeneratorFlexible <> (3, false);

    aResults = aGenerator.getCombinations (aElements);
    assertEquals (5, aResults.size ());
    assertFalse (aResults.contains (new CommonsArrayList <> ()));

    aResults = aGenerator.getCombinations (new CommonsArrayList <> ());
    assertEquals (1, aResults.size ());
    assertTrue (aResults.contains (new CommonsArrayList <> ()));

    try
    {
      aGenerator.getCombinations (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  @SuppressFBWarnings ("TQ_NEVER_VALUE_USED_WHERE_ALWAYS_REQUIRED")
  public void testCtor ()
  {
    try
    {
      new CombinationGeneratorFlexible <String> (-1, true);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Ignore ("Takes too long")
  @Test
  public void testHugeDataSet ()
  {
    final StopWatch aSW = StopWatch.createdStarted ();
    try
    {
      // Takes approx. 490ms on PH main machine (2012-01-21)
      // With one element more the time is at approx. 1500ms
      final Set <ICommonsList <String>> aResult = CombinationGeneratorFlexible.getCombinations (HUGE_LIST, true);
      s_aLogger.info ("Regular: " + aSW.stopAndGetMillis () + " ms with " + aResult.size () + " elements");
    }
    catch (final OutOfMemoryError ex)
    {
      // Happens e.g. on Jenkins
      s_aLogger.error ("Out of memory on test!", ex);
    }
  }

  @Ignore ("Simply wrong assumption")
  @Test
  public void testRedundancy ()
  {
    final ICommonsList <String> aInputList = new CommonsArrayList <> ("a", "b", "c", "d", "e", "f", "g", "h");

    // Build all permutations of the input list, using all available slots
    final ICommonsSet <ICommonsList <String>> aSimplePermutations = new CommonsHashSet <> ();
    CombinationGenerator.addAllPermutations (aInputList, aInputList.size (), aSimplePermutations);

    // Flexible combination generator
    final ICommonsSet <ICommonsList <String>> aFlexiblePermutations = CombinationGeneratorFlexible.getCombinations (aInputList,
                                                                                                                    true);
    assertTrue (aFlexiblePermutations.size () >= aSimplePermutations.size ());

    // Now the assumptions: I assume that all permutations from the flexible
    // generator are also contained in all permutations
    for (final ICommonsList <String> aList : aFlexiblePermutations)
      assertTrue (aList.toString (), aSimplePermutations.contains (aList));
  }
}
