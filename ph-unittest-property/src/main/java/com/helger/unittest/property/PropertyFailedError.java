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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;

/**
 * Thrown if a property does not hold. The message contains the shrunk sample, the original sample
 * and the seed to reproduce the run. The cause is the failure of the shrunk sample.
 *
 * @author Philip Helger
 * @since 12.8.0
 */
public class PropertyFailedError extends AssertionError
{
  private final long m_nSeed;
  private final int m_nTry;
  private final int m_nShrinkSteps;
  private final transient Object m_aOriginalSample;
  private final transient Object m_aShrunkSample;

  /**
   * Constructor
   *
   * @param nSeed
   *        The seed of the run
   * @param nTry
   *        The 1-based index of the failed try
   * @param nShrinkSteps
   *        The number of accepted shrinking steps
   * @param aOriginalSample
   *        The sample that failed first. May be <code>null</code>.
   * @param aShrunkSample
   *        The smallest failing sample found. May be <code>null</code>.
   * @param aCause
   *        The failure of the shrunk sample. May not be <code>null</code>.
   */
  public PropertyFailedError (final long nSeed,
                              @Nonnegative final int nTry,
                              @Nonnegative final int nShrinkSteps,
                              @Nullable final Object aOriginalSample,
                              @Nullable final Object aShrunkSample,
                              @NonNull final Throwable aCause)
  {
    super ("Property failed on try " +
           nTry +
           " (seed " +
           nSeed +
           ")\n" +
           "  Shrunk sample (" +
           nShrinkSteps +
           " steps): " +
           SampleFormatter.format (aShrunkSample) +
           "\n" +
           "  Original sample: " +
           SampleFormatter.format (aOriginalSample) +
           "\n" +
           "  Failure: " +
           aCause +
           "\n" +
           "  Reproduce with -D" +
           PropertyChecker.SYSTEM_PROPERTY_SEED +
           "=" +
           nSeed +
           " or setSeed (" +
           nSeed +
           "L)",
           aCause);
    m_nSeed = nSeed;
    m_nTry = nTry;
    m_nShrinkSteps = nShrinkSteps;
    m_aOriginalSample = aOriginalSample;
    m_aShrunkSample = aShrunkSample;
  }

  /**
   * @return The seed to reproduce the run.
   */
  public final long getSeed ()
  {
    return m_nSeed;
  }

  /**
   * @return The 1-based index of the failed try.
   */
  @Nonnegative
  public final int getTry ()
  {
    return m_nTry;
  }

  /**
   * @return The number of accepted shrinking steps.
   */
  @Nonnegative
  public final int getShrinkSteps ()
  {
    return m_nShrinkSteps;
  }

  /**
   * @return The sample that failed first. May be <code>null</code>. For properties with several
   *         arguments, this is an <code>Object[]</code>.
   */
  @Nullable
  public final Object getOriginalSample ()
  {
    return m_aOriginalSample;
  }

  /**
   * @return The smallest failing sample found. May be <code>null</code>. For properties with several
   *         arguments, this is an <code>Object[]</code>.
   */
  @Nullable
  public final Object getShrunkSample ()
  {
    return m_aShrunkSample;
  }
}
