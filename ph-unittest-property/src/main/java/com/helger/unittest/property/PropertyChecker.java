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

import java.util.Random;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;

/**
 * Runs properties: generates samples, checks the property for each of them and, on failure, shrinks
 * the sample and throws a {@link PropertyFailedError}. It does not depend on a test framework, so it
 * can be called from a plain JUnit 4 test method:
 *
 * <pre>
 * &#64;Test
 * public void testRoundTrip ()
 * {
 *   new PropertyChecker ().forAll (Generators.byteArrays (),
 *                                  aBytes -&gt; assertArrayEquals (aBytes, decode (encode (aBytes))));
 * }
 * </pre>
 * <p>
 * The number of tries and the seed can be set globally with the system properties
 * {@value #SYSTEM_PROPERTY_TRIES} and {@value #SYSTEM_PROPERTY_SEED}. The seed system property
 * overrides any seed set in code, so that a failure can be reproduced without changing the code. The
 * tries system property sets the default, which {@link #setTries(int)} overrides.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class PropertyChecker
{
  /** System property to set the default number of tries */
  public static final String SYSTEM_PROPERTY_TRIES = "ph.property.tries";
  /** System property to set the seed of all runs */
  public static final String SYSTEM_PROPERTY_SEED = "ph.property.seed";

  /** The default number of tries per property */
  public static final int DEFAULT_TRIES = 200;
  /** The default maximum number of candidates tested while shrinking */
  public static final int DEFAULT_MAX_SHRINK_ATTEMPTS = 10_000;
  /** The default number of rejected samples allowed per try */
  public static final int DEFAULT_MAX_REJECTS_PER_TRY = 5;

  /**
   * The outcome of checking one sample.
   */
  private static final class Outcome
  {
    private final long [] m_aChoices;
    private final Throwable m_aFailure;

    Outcome (final long @NonNull [] aChoices, @Nullable final Throwable aFailure)
    {
      m_aChoices = aChoices;
      m_aFailure = aFailure;
    }
  }

  private int m_nTries;
  private Long m_aSeed;
  private int m_nMaxShrinkAttempts = DEFAULT_MAX_SHRINK_ATTEMPTS;
  private int m_nMaxChoices = ChoiceSequence.DEFAULT_MAX_CHOICES;

  /**
   * Constructor using the system properties or the defaults.
   */
  public PropertyChecker ()
  {
    final String sTries = System.getProperty (SYSTEM_PROPERTY_TRIES);
    m_nTries = sTries != null ? Integer.parseInt (sTries.trim ()) : DEFAULT_TRIES;
  }

  /**
   * @return The number of samples checked per property.
   */
  @Nonnegative
  public final int getTries ()
  {
    return m_nTries;
  }

  /**
   * @param nTries
   *        The number of samples to check per property. Must be &gt; 0.
   * @return this for chaining
   */
  @NonNull
  public final PropertyChecker setTries (final int nTries)
  {
    if (nTries <= 0)
      throw new IllegalArgumentException ("Tries must be > 0: " + nTries);
    m_nTries = nTries;
    return this;
  }

  /**
   * @param nSeed
   *        The seed to use, e.g. to reproduce a failure. The system property
   *        {@value #SYSTEM_PROPERTY_SEED} takes precedence.
   * @return this for chaining
   */
  @NonNull
  public final PropertyChecker setSeed (final long nSeed)
  {
    m_aSeed = Long.valueOf (nSeed);
    return this;
  }

  /**
   * @param nMaxShrinkAttempts
   *        The maximum number of candidates tested while shrinking. 0 disables shrinking.
   * @return this for chaining
   */
  @NonNull
  public final PropertyChecker setMaxShrinkAttempts (@Nonnegative final int nMaxShrinkAttempts)
  {
    if (nMaxShrinkAttempts < 0)
      throw new IllegalArgumentException ("MaxShrinkAttempts must be >= 0: " + nMaxShrinkAttempts);
    m_nMaxShrinkAttempts = nMaxShrinkAttempts;
    return this;
  }

  /**
   * @param nMaxChoices
   *        The maximum number of choices a generator may draw for one sample. Must be &gt; 0.
   * @return this for chaining
   */
  @NonNull
  public final PropertyChecker setMaxChoices (final int nMaxChoices)
  {
    if (nMaxChoices <= 0)
      throw new IllegalArgumentException ("MaxChoices must be > 0: " + nMaxChoices);
    m_nMaxChoices = nMaxChoices;
    return this;
  }

  private long _getEffectiveSeed ()
  {
    final String sSeed = System.getProperty (SYSTEM_PROPERTY_SEED);
    if (sSeed != null)
      return Long.parseLong (sSeed.trim ());
    if (m_aSeed != null)
      return m_aSeed.longValue ();
    return new Random ().nextLong ();
  }

  /**
   * Generate a sample and check the property.
   *
   * @return <code>null</code> if the sample was rejected.
   */
  @Nullable
  private static <T> Outcome _check (@NonNull final IGenerator <T> aGenerator,
                                     @NonNull final IProperty1 <? super T> aProperty,
                                     @NonNull final ChoiceSequence aChoices)
  {
    final T aSample;
    try
    {
      aSample = aGenerator.generate (aChoices);
    }
    catch (final SampleRejectedException ex)
    {
      return null;
    }

    final long [] aUsedChoices = aChoices.getAllChoices ();
    try
    {
      aProperty.check (aSample);
      return new Outcome (aUsedChoices, null);
    }
    catch (final SampleRejectedException ex)
    {
      return null;
    }
    catch (final Throwable t)
    {
      return new Outcome (aUsedChoices, t);
    }
  }

  @NonNull
  private <T> PropertyFailedError _createFailure (@NonNull final IGenerator <T> aGenerator,
                                                  @NonNull final IProperty1 <? super T> aProperty,
                                                  final long nSeed,
                                                  final int nTry,
                                                  @NonNull final Outcome aFailedOutcome)
  {
    // Only accept candidates that fail with the same exception class, so that shrinking does not slip
    // over to a different bug
    final Class <?> aFailureClass = aFailedOutcome.m_aFailure.getClass ();
    final ChoiceShrinker aShrinker = new ChoiceShrinker (aCandidate -> {
      final Outcome aOutcome = _check (aGenerator, aProperty, ChoiceSequence.createReplay (aCandidate));
      if (aOutcome != null && aOutcome.m_aFailure != null && aOutcome.m_aFailure.getClass () == aFailureClass)
        return aOutcome.m_aChoices;
      return null;
    }, m_nMaxShrinkAttempts);
    final long [] aShrunkChoices = aShrinker.shrink (aFailedOutcome.m_aChoices);

    // Generate the samples again, because the property may have modified them
    final Object aOriginalSample = aGenerator.generate (ChoiceSequence.createReplay (aFailedOutcome.m_aChoices));
    final Object aShrunkSample = aGenerator.generate (ChoiceSequence.createReplay (aShrunkChoices));
    final Outcome aShrunkOutcome = _check (aGenerator, aProperty, ChoiceSequence.createReplay (aShrunkChoices));
    final Throwable aCause = aShrunkOutcome != null && aShrunkOutcome.m_aFailure != null ? aShrunkOutcome.m_aFailure
                                                                                         : aFailedOutcome.m_aFailure;
    return new PropertyFailedError (nSeed, nTry, aShrinker.getSteps (), aOriginalSample, aShrunkSample, aCause);
  }

  /**
   * Check that the property holds for all generated values.
   *
   * @param <A>
   *        The type of the generated values
   * @param aGenerator
   *        The generator for the values. May not be <code>null</code>.
   * @param aProperty
   *        The property to check. May not be <code>null</code>.
   * @throws PropertyFailedError
   *         If the property does not hold, or if too many samples were rejected
   */
  public <A> void forAll (@NonNull final IGenerator <A> aGenerator, @NonNull final IProperty1 <? super A> aProperty)
  {
    final long nSeed = _getEffectiveSeed ();
    final Random aRandom = new Random (nSeed);
    final int nMaxRejects = m_nTries * DEFAULT_MAX_REJECTS_PER_TRY;
    int nRejects = 0;
    int nTry = 0;
    while (nTry < m_nTries)
    {
      final Outcome aOutcome = _check (aGenerator, aProperty, ChoiceSequence.createRandom (aRandom, m_nMaxChoices));
      if (aOutcome == null)
      {
        if (++nRejects > nMaxRejects)
          throw new AssertionError ("Too many rejected samples: " +
                                    nRejects +
                                    " rejected, " +
                                    nTry +
                                    " checked (seed " +
                                    nSeed +
                                    ")");
        continue;
      }
      nTry++;
      if (aOutcome.m_aFailure != null)
        throw _createFailure (aGenerator, aProperty, nSeed, nTry, aOutcome);
    }
  }

  /**
   * Check that the property holds for all combinations of generated values. On failure, the samples
   * of {@link PropertyFailedError} are <code>Object[]</code>.
   *
   * @param <A>
   *        The type of the first value
   * @param <B>
   *        The type of the second value
   * @param aGeneratorA
   *        The generator for the first value. May not be <code>null</code>.
   * @param aGeneratorB
   *        The generator for the second value. May not be <code>null</code>.
   * @param aProperty
   *        The property to check. May not be <code>null</code>.
   * @throws PropertyFailedError
   *         If the property does not hold, or if too many samples were rejected
   */
  @SuppressWarnings ("unchecked")
  public <A, B> void forAll (@NonNull final IGenerator <A> aGeneratorA,
                             @NonNull final IGenerator <B> aGeneratorB,
                             @NonNull final IProperty2 <? super A, ? super B> aProperty)
  {
    final IGenerator <Object []> aTupleGenerator = aChoices -> new Object [] { aGeneratorA.generate (aChoices),
                                                                              aGeneratorB.generate (aChoices) };
    forAll (aTupleGenerator, aArgs -> aProperty.check ((A) aArgs[0], (B) aArgs[1]));
  }

  /**
   * Check that the property holds for all combinations of generated values. On failure, the samples
   * of {@link PropertyFailedError} are <code>Object[]</code>.
   *
   * @param <A>
   *        The type of the first value
   * @param <B>
   *        The type of the second value
   * @param <C>
   *        The type of the third value
   * @param aGeneratorA
   *        The generator for the first value. May not be <code>null</code>.
   * @param aGeneratorB
   *        The generator for the second value. May not be <code>null</code>.
   * @param aGeneratorC
   *        The generator for the third value. May not be <code>null</code>.
   * @param aProperty
   *        The property to check. May not be <code>null</code>.
   * @throws PropertyFailedError
   *         If the property does not hold, or if too many samples were rejected
   */
  @SuppressWarnings ("unchecked")
  public <A, B, C> void forAll (@NonNull final IGenerator <A> aGeneratorA,
                                @NonNull final IGenerator <B> aGeneratorB,
                                @NonNull final IGenerator <C> aGeneratorC,
                                @NonNull final IProperty3 <? super A, ? super B, ? super C> aProperty)
  {
    final IGenerator <Object []> aTupleGenerator = aChoices -> new Object [] { aGeneratorA.generate (aChoices),
                                                                              aGeneratorB.generate (aChoices),
                                                                              aGeneratorC.generate (aChoices) };
    forAll (aTupleGenerator, aArgs -> aProperty.check ((A) aArgs[0], (B) aArgs[1], (C) aArgs[2]));
  }
  /**
   * Check that the property holds for all combinations of generated values. On failure, the samples
   * of {@link PropertyFailedError} are <code>Object[]</code>.
   *
   * @param <A>
   *        The type of the first value
   * @param <B>
   *        The type of the second value
   * @param <C>
   *        The type of the third value
   * @param <D>
   *        The type of the fourth value
   * @param aGeneratorA
   *        The generator for the first value. May not be <code>null</code>.
   * @param aGeneratorB
   *        The generator for the second value. May not be <code>null</code>.
   * @param aGeneratorC
   *        The generator for the third value. May not be <code>null</code>.
   * @param aGeneratorD
   *        The generator for the fourth value. May not be <code>null</code>.
   * @param aProperty
   *        The property to check. May not be <code>null</code>.
   * @throws PropertyFailedError
   *         If the property does not hold, or if too many samples were rejected
   */
  @SuppressWarnings ("unchecked")
  public <A, B, C, D> void forAll (@NonNull final IGenerator <A> aGeneratorA,
                                   @NonNull final IGenerator <B> aGeneratorB,
                                   @NonNull final IGenerator <C> aGeneratorC,
                                   @NonNull final IGenerator <D> aGeneratorD,
                                   @NonNull final IProperty4 <? super A, ? super B, ? super C, ? super D> aProperty)
  {
    final IGenerator <Object []> aTupleGenerator = aChoices -> new Object [] { aGeneratorA.generate (aChoices),
                                                                              aGeneratorB.generate (aChoices),
                                                                              aGeneratorC.generate (aChoices),
                                                                              aGeneratorD.generate (aChoices) };
    forAll (aTupleGenerator, aArgs -> aProperty.check ((A) aArgs[0], (B) aArgs[1], (C) aArgs[2], (D) aArgs[3]));
  }
}
