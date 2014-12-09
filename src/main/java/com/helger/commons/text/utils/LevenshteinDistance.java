/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.text.utils;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotations.PresentForCodeCoverage;

/**
 * Utility class for calculating the Levenshtein distance of 2 strings.
 *
 * @author Philip Helger
 * @deprecated Use {@link com.helger.commons.string.utils.LevenshteinDistance}
 *             instead.
 */
@Immutable
@Deprecated
public final class LevenshteinDistance
{
  @PresentForCodeCoverage
  @SuppressWarnings ("unused")
  private static final LevenshteinDistance s_aInstance = new LevenshteinDistance ();

  private LevenshteinDistance ()
  {}

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
    return com.helger.commons.string.utils.LevenshteinDistance.getDistance (aStr1, aStr2);
  }

  public static int getDistance (@Nullable final char [] aStr1,
                                 @Nullable final char [] aStr2,
                                 @Nonnegative final int nCostInsert,
                                 @Nonnegative final int nCostDelete,
                                 @Nonnegative final int nCostSubstitution)
  {
    return com.helger.commons.string.utils.LevenshteinDistance.getDistance (aStr1,
                                                                            aStr2,
                                                                            nCostInsert,
                                                                            nCostDelete,
                                                                            nCostSubstitution);
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
    return com.helger.commons.string.utils.LevenshteinDistance.getDistance (sStr1, sStr2);
  }

  public static int getDistance (@Nullable final String sStr1,
                                 @Nullable final String sStr2,
                                 @Nonnegative final int nCostInsert,
                                 @Nonnegative final int nCostDelete,
                                 @Nonnegative final int nCostSubstitution)
  {
    return com.helger.commons.string.utils.LevenshteinDistance.getDistance (sStr1,
                                                                            sStr2,
                                                                            nCostInsert,
                                                                            nCostDelete,
                                                                            nCostSubstitution);
  }
}
