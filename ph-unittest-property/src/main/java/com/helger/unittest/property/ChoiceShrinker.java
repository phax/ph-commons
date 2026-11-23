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
import java.util.function.Function;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;

/**
 * Minimises a failing choice sequence. It knows nothing about the generated types; it only tries
 * shorter sequences and smaller values and keeps every candidate that still fails.
 * <p>
 * Candidates are compared in "shortlex" order: a shorter sequence is smaller, and sequences of the
 * same length are compared value by value. Each accepted candidate is strictly smaller than the
 * previous best, so shrinking always terminates.
 *
 * @author Philip Helger
 */
@NotThreadSafe
final class ChoiceShrinker
{
  /**
   * Chunk sizes for deleting and zeroing. A collection element needs one "continue" choice plus 1
   * (non-negative number) or 2 (signed number: sign and magnitude) choices.
   */
  private static final int [] CHUNK_SIZES = { 8, 4, 3, 2, 1 };
  /** How far apart two choices may be to be considered by the redistribute pass */
  private static final int REDISTRIBUTE_WINDOW = 8;

  private final Function <long [], long []> m_aTest;
  private final int m_nMaxAttempts;
  private long [] m_aBest;
  private int m_nAttempts = 0;
  private int m_nSteps = 0;

  /**
   * Constructor
   *
   * @param aTest
   *        Replays a candidate. Returns the choices actually used, if the candidate still fails in the
   *        same way, or <code>null</code> otherwise. May not be <code>null</code>.
   * @param nMaxAttempts
   *        The maximum number of candidates to test.
   */
  ChoiceShrinker (@NonNull final Function <long [], long []> aTest, @Nonnegative final int nMaxAttempts)
  {
    m_aTest = aTest;
    m_nMaxAttempts = nMaxAttempts;
  }

  private static boolean _isSmaller (final long @NonNull [] aCandidate, final long @NonNull [] aReference)
  {
    if (aCandidate.length != aReference.length)
      return aCandidate.length < aReference.length;
    return Arrays.compare (aCandidate, aReference) < 0;
  }

  private boolean _tryCandidate (final long @NonNull [] aCandidate)
  {
    if (m_nAttempts >= m_nMaxAttempts || !_isSmaller (aCandidate, m_aBest))
      return false;

    m_nAttempts++;
    final long [] aUsed = m_aTest.apply (aCandidate);
    if (aUsed == null || !_isSmaller (aUsed, m_aBest))
      return false;

    m_aBest = aUsed;
    m_nSteps++;
    return true;
  }

  @NonNull
  private static long [] _withoutChunk (final long @NonNull [] aSrc, final int nIndex, final int nSize)
  {
    final long [] ret = new long [aSrc.length - nSize];
    System.arraycopy (aSrc, 0, ret, 0, nIndex);
    System.arraycopy (aSrc, nIndex + nSize, ret, nIndex, aSrc.length - nIndex - nSize);
    return ret;
  }

  /**
   * Remove chunks of choices. As collections use one "continue" choice per element, removing a
   * chunk usually removes one or more elements.
   */
  private boolean _passDeleteChunks ()
  {
    boolean bImproved = false;
    for (final int nSize : CHUNK_SIZES)
    {
      int nIndex = m_aBest.length - nSize;
      while (nIndex >= 0 && m_nAttempts < m_nMaxAttempts)
      {
        if (_tryCandidate (_withoutChunk (m_aBest, nIndex, nSize)))
        {
          bImproved = true;
          // Retry at the same position, the sequence may now be shorter
          nIndex = Math.min (nIndex, m_aBest.length - nSize);
        }
        else
          nIndex--;
      }
    }
    return bImproved;
  }

  /**
   * Set chunks of choices to 0, which makes several values the simplest value at once.
   */
  private boolean _passZeroChunks ()
  {
    boolean bImproved = false;
    for (final int nSize : CHUNK_SIZES)
      for (int nIndex = 0; nIndex + nSize <= m_aBest.length && m_nAttempts < m_nMaxAttempts; ++nIndex)
      {
        boolean bAllZero = true;
        for (int i = nIndex; i < nIndex + nSize; ++i)
          if (m_aBest[i] != 0)
          {
            bAllZero = false;
            break;
          }
        if (!bAllZero)
        {
          final long [] aCandidate = m_aBest.clone ();
          Arrays.fill (aCandidate, nIndex, nIndex + nSize, 0);
          if (_tryCandidate (aCandidate))
            bImproved = true;
        }
      }
    return bImproved;
  }

  @NonNull
  private static long [] _withValue (final long @NonNull [] aSrc, final int nIndex, final long nValue)
  {
    final long [] ret = aSrc.clone ();
    ret[nIndex] = nValue;
    return ret;
  }

  /**
   * Make each single choice as small as possible with a binary search. That finds boundary values
   * like the smallest number for which a property fails.
   */
  private boolean _passMinimiseValues ()
  {
    boolean bImproved = false;
    for (int nIndex = 0; nIndex < m_aBest.length && m_nAttempts < m_nMaxAttempts; ++nIndex)
    {
      if (m_aBest[nIndex] == 0)
        continue;
      if (_tryCandidate (_withValue (m_aBest, nIndex, 0)))
      {
        bImproved = true;
        continue;
      }

      // 0 passes, the current value fails - search for the smallest failing value in between
      long nLo = 0;
      long nHi = m_aBest[nIndex];
      while (nLo + 1 < nHi && nIndex < m_aBest.length && m_nAttempts < m_nMaxAttempts)
      {
        final long nMid = nLo + (nHi - nLo) / 2;
        if (_tryCandidate (_withValue (m_aBest, nIndex, nMid)))
        {
          bImproved = true;
          nHi = nIndex < m_aBest.length ? m_aBest[nIndex] : 0;
        }
        else
          nLo = nMid;
      }
    }
    return bImproved;
  }

  @NonNull
  private static long [] _withMovedAmount (final long @NonNull [] aSrc,
                                           final int nFrom,
                                           final int nTo,
                                           final long nAmount)
  {
    final long [] ret = aSrc.clone ();
    ret[nFrom] -= nAmount;
    ret[nTo] += nAmount;
    return ret;
  }

  /**
   * Move value from an earlier choice to a later one. That is lexicographically smaller and finds
   * e.g. [1, 100] instead of [52, 49] for "the sum must not exceed 100".
   */
  private boolean _passRedistribute ()
  {
    boolean bImproved = false;
    for (int nFrom = 0; nFrom < m_aBest.length && m_nAttempts < m_nMaxAttempts; ++nFrom)
      for (int nTo = nFrom + 1; nTo < Math.min (m_aBest.length, nFrom + 1 + REDISTRIBUTE_WINDOW) &&
                               m_nAttempts < m_nMaxAttempts; ++nTo)
      {
        // Greedy: try to move as much as possible, halve the amount on failure
        long nAmount = Math.min (m_aBest[nFrom], Long.MAX_VALUE - m_aBest[nTo]);
        while (nAmount > 0 && nTo < m_aBest.length && m_nAttempts < m_nMaxAttempts)
        {
          if (_tryCandidate (_withMovedAmount (m_aBest, nFrom, nTo, nAmount)))
          {
            bImproved = true;
            if (nTo >= m_aBest.length)
              break;
            nAmount = Math.min (nAmount, Math.min (m_aBest[nFrom], Long.MAX_VALUE - m_aBest[nTo]));
          }
          else
            nAmount /= 2;
        }
      }
    return bImproved;
  }

  /**
   * Shrink the passed choices.
   *
   * @param aFailingChoices
   *        The choices of the original failure. May not be <code>null</code>.
   * @return The smallest failing choices found. Never <code>null</code>.
   */
  @NonNull
  long [] shrink (final long @NonNull [] aFailingChoices)
  {
    m_aBest = aFailingChoices;
    boolean bImproved = true;
    while (bImproved && m_nAttempts < m_nMaxAttempts)
    {
      bImproved = false;
      if (_passDeleteChunks ())
        bImproved = true;
      if (_passZeroChunks ())
        bImproved = true;
      if (_passMinimiseValues ())
        bImproved = true;
      if (!bImproved && _passRedistribute ())
        bImproved = true;
    }
    return m_aBest;
  }

  /**
   * @return The number of accepted shrinking steps.
   */
  @Nonnegative
  int getSteps ()
  {
    return m_nSteps;
  }

  /**
   * @return The number of candidates tested.
   */
  @Nonnegative
  int getAttempts ()
  {
    return m_nAttempts;
  }
}
