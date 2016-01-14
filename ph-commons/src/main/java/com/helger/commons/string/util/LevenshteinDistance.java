/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.string.util;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.string.StringHelper;

/**
 * Utility class for calculating the Levenshtein distance of 2 strings.
 *
 * @author Philip Helger
 */
@Immutable
public final class LevenshteinDistance
{
  @PresentForCodeCoverage
  private static final LevenshteinDistance s_aInstance = new LevenshteinDistance ();

  private LevenshteinDistance ()
  {}

  /**
   * Main generic Levenshtein implementation that uses "1" for all costs!
   *
   * @param aStr1
   *        Not null
   * @param nLen1
   *        &gt; 0
   * @param aStr2
   *        Not null
   * @param nLen2
   *        &gt; 0
   * @return non negative distance
   */
  private static int _getDistance111 (@Nonnull final char [] aStr1,
                                      @Nonnegative final int nLen1,
                                      @Nonnull final char [] aStr2,
                                      @Nonnegative final int nLen2)
  {
    // previous cost array, horizontally
    int [] aPrevRow = new int [nLen1 + 1];

    // cost array, horizontally
    int [] aCurRow = new int [nLen1 + 1];

    // temp vars
    int i, j, nSubstVal;
    int ch2;
    int [] tmp;

    // init
    for (i = 0; i <= nLen1; i++)
      aPrevRow[i] = i;

    for (j = 0; j < nLen2; j++)
    {
      ch2 = aStr2[j];
      aCurRow[0] = j + 1;

      for (i = 0; i < nLen1; i++)
      {
        nSubstVal = aStr1[i] == ch2 ? 0 : 1;
        aCurRow[i + 1] = Math.min (Math.min (aCurRow[i] + 1, aPrevRow[i + 1] + 1), aPrevRow[i] + nSubstVal);
      }

      // swap current distance counts to 'previous row' distance counts
      tmp = aPrevRow;
      aPrevRow = aCurRow;
      aCurRow = tmp;
    }

    // our last action in the above loop was to switch d and p, so p now
    // actually has the most recent cost counts
    return aPrevRow[nLen1];
  }

  /**
   * Main generic Levenshtein implementation. Assume all preconditions are
   * checked.
   *
   * @param aStr1
   *        Not null
   * @param nLen1
   *        &gt; 0
   * @param aStr2
   *        Not null
   * @param nLen2
   *        &gt; 0
   * @param nCostInsert
   *        &ge; 0
   * @param nCostDelete
   *        &ge; 0
   * @param nCostSubstitution
   *        &ge; 0
   * @return non negative distance
   */
  private static int _getDistance (@Nonnull final char [] aStr1,
                                   @Nonnegative final int nLen1,
                                   @Nonnull final char [] aStr2,
                                   @Nonnegative final int nLen2,
                                   @Nonnegative final int nCostInsert,
                                   @Nonnegative final int nCostDelete,
                                   @Nonnegative final int nCostSubstitution)
  {
    // previous cost array, horizontally
    int [] aPrevRow = new int [nLen1 + 1];

    // cost array, horizontally
    int [] aCurRow = new int [nLen1 + 1];

    // temp vars
    int i, j, ch2, nSubstCost;
    int [] tmp;

    // init
    for (i = 0; i <= nLen1; i++)
      aPrevRow[i] = i * nCostInsert;

    for (j = 0; j < nLen2; j++)
    {
      ch2 = aStr2[j];
      aCurRow[0] = (j + 1) * nCostDelete;

      for (i = 0; i < nLen1; i++)
      {
        nSubstCost = aStr1[i] == ch2 ? 0 : nCostSubstitution;
        aCurRow[i + 1] = Math.min (Math.min (aCurRow[i] + nCostInsert, aPrevRow[i + 1] + nCostDelete),
                                   aPrevRow[i] + nSubstCost);
      }

      // swap current distance counts to 'previous row' distance counts
      tmp = aPrevRow;
      aPrevRow = aCurRow;
      aCurRow = tmp;
    }

    // our last action in the above loop was to switch d and p, so p now
    // actually has the most recent cost counts
    return aPrevRow[nLen1];
  }

  /**
   * Get the distance of the 2 strings, using the costs 1 for insertion,
   * deletion and substitution.
   *
   * @param aStr1
   *        First string.
   * @param aStr2
   *        Second string.
   * @return The Levenshtein distance.
   */
  public static int getDistance (@Nullable final char [] aStr1, @Nullable final char [] aStr2)
  {
    final int nLen1 = aStr1 == null ? 0 : aStr1.length;
    final int nLen2 = aStr2 == null ? 0 : aStr2.length;

    if (nLen1 == 0)
      return nLen2;
    if (nLen2 == 0)
      return nLen1;

    return _getDistance111 (aStr1, nLen1, aStr2, nLen2);
  }

  public static int getDistance (@Nullable final char [] aStr1,
                                 @Nullable final char [] aStr2,
                                 @Nonnegative final int nCostInsert,
                                 @Nonnegative final int nCostDelete,
                                 @Nonnegative final int nCostSubstitution)
  {
    ValueEnforcer.isGE0 (nCostInsert, "InsertionCost");
    ValueEnforcer.isGE0 (nCostDelete, "DeletionCost");
    ValueEnforcer.isGE0 (nCostSubstitution, "SubstitutionCost");

    final int nLen1 = aStr1 == null ? 0 : aStr1.length;
    final int nLen2 = aStr2 == null ? 0 : aStr2.length;

    if (nLen1 == 0)
      return nLen2 * nCostInsert;
    if (nLen2 == 0)
      return nLen1 * nCostInsert;

    // Fallback to more efficient variant?
    if (nCostInsert == 1 && nCostDelete == 1 && nCostSubstitution == 1)
      return _getDistance111 (aStr1, nLen1, aStr2, nLen2);

    return _getDistance (aStr1, nLen1, aStr2, nLen2, nCostInsert, nCostDelete, nCostSubstitution);
  }

  /**
   * Get the distance of the 2 strings, using the costs 1 for insertion,
   * deletion and substitution.
   *
   * @param sStr1
   *        First string.
   * @param sStr2
   *        second string.
   * @return The Levenshtein distance.
   */
  public static int getDistance (@Nullable final String sStr1, @Nullable final String sStr2)
  {
    final int nLen1 = StringHelper.getLength (sStr1);
    final int nLen2 = StringHelper.getLength (sStr2);

    if (nLen1 == 0)
      return nLen2;
    if (nLen2 == 0)
      return nLen1;

    return _getDistance111 (sStr1.toCharArray (), nLen1, sStr2.toCharArray (), nLen2);
  }

  public static int getDistance (@Nullable final String sStr1,
                                 @Nullable final String sStr2,
                                 @Nonnegative final int nCostInsert,
                                 @Nonnegative final int nCostDelete,
                                 @Nonnegative final int nCostSubstitution)
  {
    ValueEnforcer.isGE0 (nCostInsert, "InsertionCost");
    ValueEnforcer.isGE0 (nCostDelete, "DeletionCost");
    ValueEnforcer.isGE0 (nCostSubstitution, "SubstitutionCost");

    final int nLen1 = StringHelper.getLength (sStr1);
    final int nLen2 = StringHelper.getLength (sStr2);

    if (nLen1 == 0)
      return nLen2 * nCostInsert;
    if (nLen2 == 0)
      return nLen1 * nCostInsert;

    // Fallback to more efficient variant?
    if (nCostInsert == 1 && nCostDelete == 1 && nCostSubstitution == 1)
      return _getDistance111 (sStr1.toCharArray (), nLen1, sStr2.toCharArray (), nLen2);

    return _getDistance (sStr1.toCharArray (),
                         nLen1,
                         sStr2.toCharArray (),
                         nLen2,
                         nCostInsert,
                         nCostDelete,
                         nCostSubstitution);
  }
}
