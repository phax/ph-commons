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

import java.util.Arrays;
import java.util.Random;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;

/**
 * The source of all randomness for a generator. Every decision a generator makes is a number drawn
 * from this sequence, and every drawn number is recorded.
 * <p>
 * This is the basis for shrinking: a failing sample is fully described by its recorded choices. The
 * shrinker makes the choice sequence shorter and its values smaller and replays it through the same
 * generator. As generators are written so that smaller choices produce "simpler" values (shorter
 * collections, numbers closer to 0, earlier elements of a list), a minimal choice sequence yields a
 * minimal sample. This works through <code>map</code>, <code>filter</code> and <code>flatMap</code>
 * without any type specific shrinking code. The idea is taken from the Python library Hypothesis.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public final class ChoiceSequence
{
  /** The default maximum number of choices per sample, to stop runaway generators */
  public static final int DEFAULT_MAX_CHOICES = 64 * 1024;

  private final Random m_aRandom;
  private final long [] m_aReplay;
  private final int m_nMaxChoices;
  private long [] m_aRecorded = new long [32];
  private int m_nCount = 0;

  private ChoiceSequence (@Nullable final Random aRandom, final long @Nullable [] aReplay, final int nMaxChoices)
  {
    m_aRandom = aRandom;
    m_aReplay = aReplay;
    m_nMaxChoices = nMaxChoices;
  }

  /**
   * Create a new sequence that draws random choices.
   *
   * @param aRandom
   *        The random source. May not be <code>null</code>.
   * @param nMaxChoices
   *        The maximum number of choices that may be drawn. Must be &gt; 0.
   * @return The new sequence. Never <code>null</code>.
   */
  @NonNull
  public static ChoiceSequence createRandom (@NonNull final Random aRandom, final int nMaxChoices)
  {
    if (nMaxChoices <= 0)
      throw new IllegalArgumentException ("MaxChoices must be > 0: " + nMaxChoices);
    return new ChoiceSequence (aRandom, null, nMaxChoices);
  }

  /**
   * Create a new sequence that replays previously recorded choices. A drawn value that is larger
   * than the requested maximum is reduced to that maximum. Drawing beyond the end of the recorded
   * choices rejects the sample.
   *
   * @param aChoices
   *        The choices to replay. May not be <code>null</code>.
   * @return The new sequence. Never <code>null</code>.
   */
  @NonNull
  public static ChoiceSequence createReplay (final long @NonNull [] aChoices)
  {
    return new ChoiceSequence (null, aChoices, aChoices.length);
  }

  private void _record (final long nValue)
  {
    if (m_nCount == m_aRecorded.length)
      m_aRecorded = Arrays.copyOf (m_aRecorded, m_nCount * 2);
    m_aRecorded[m_nCount++] = nValue;
  }

  /**
   * Draw the next choice. Generators must map smaller choices to simpler values, with 0 being the
   * simplest.
   *
   * @param nMaxInclusive
   *        The maximum value to be returned. Must be &ge; 0.
   * @return A value between 0 and <code>nMaxInclusive</code>, both inclusive.
   * @throws SampleRejectedException
   *         If the maximum number of choices was reached
   */
  @Nonnegative
  public long draw (@Nonnegative final long nMaxInclusive)
  {
    if (nMaxInclusive < 0)
      throw new IllegalArgumentException ("MaxInclusive must be >= 0: " + nMaxInclusive);
    if (m_nCount >= m_nMaxChoices)
      throw new SampleRejectedException ("The generator needs more than " + m_nMaxChoices + " choices");

    final long nValue;
    if (m_aReplay != null)
      nValue = Math.min (m_aReplay[m_nCount], nMaxInclusive);
    else
      if (nMaxInclusive == Long.MAX_VALUE)
        nValue = m_aRandom.nextLong () & Long.MAX_VALUE;
      else
        nValue = m_aRandom.nextLong (nMaxInclusive + 1);
    _record (nValue);
    return nValue;
  }

  /**
   * Draw the next choice, with a preferred value. While generating, the hint is recorded as the
   * choice, which lets generators use any distribution (e.g. prefer edge cases) without recording
   * how they made the decision. While replaying, the hint is ignored and the recorded choice is
   * returned, so shrinking only sees the plain value.
   *
   * @param nMaxInclusive
   *        The maximum value to be returned. Must be &ge; 0.
   * @param nHint
   *        The value to record while generating. It is limited to the range 0 to
   *        <code>nMaxInclusive</code>.
   * @return A value between 0 and <code>nMaxInclusive</code>, both inclusive.
   * @throws SampleRejectedException
   *         If the maximum number of choices was reached
   */
  @Nonnegative
  public long draw (@Nonnegative final long nMaxInclusive, final long nHint)
  {
    if (nMaxInclusive < 0)
      throw new IllegalArgumentException ("MaxInclusive must be >= 0: " + nMaxInclusive);
    if (m_nCount >= m_nMaxChoices)
      throw new SampleRejectedException ("The generator needs more than " + m_nMaxChoices + " choices");

    final long nValue;
    if (m_aReplay != null)
      nValue = Math.min (m_aReplay[m_nCount], nMaxInclusive);
    else
      nValue = Math.max (0, Math.min (nHint, nMaxInclusive));
    _record (nValue);
    return nValue;
  }

  /**
   * @return <code>true</code> if new random choices are generated, <code>false</code> if recorded
   *         choices are replayed.
   */
  public boolean isGenerating ()
  {
    return m_aReplay == null;
  }

  /**
   * Get a random number that is <b>not</b> recorded. Use it only to compute the hint for
   * {@link #draw(long, long)} while {@link #isGenerating()} is <code>true</code>.
   *
   * @param nMaxInclusive
   *        The maximum value to be returned. Must be &ge; 0 and &lt; {@link Long#MAX_VALUE}.
   * @return A random value between 0 and <code>nMaxInclusive</code>, both inclusive.
   * @throws IllegalStateException
   *         If choices are replayed
   */
  @Nonnegative
  public long nextUnrecordedRandom (@Nonnegative final long nMaxInclusive)
  {
    if (m_aRandom == null)
      throw new IllegalStateException ("No random numbers available while replaying");
    return m_aRandom.nextLong (nMaxInclusive + 1);
  }

  /**
   * @return The number of choices drawn so far.
   */
  @Nonnegative
  public int getCount ()
  {
    return m_nCount;
  }

  /**
   * @return A copy of all choices drawn so far. Never <code>null</code>.
   */
  @ReturnsMutableCopy
  public long @NonNull [] getAllChoices ()
  {
    return Arrays.copyOf (m_aRecorded, m_nCount);
  }
}
