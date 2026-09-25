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
package com.helger.statistics.impl;

import java.math.BigInteger;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.CheckForSigned;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.base.CGlobal;
import com.helger.base.concurrent.SimpleReadWriteLock;
import com.helger.statistics.api.IStatisticsHandlerNumeric;

/**
 * Abstract base class for numeric statistic handler
 *
 * @author Philip Helger
 */
@ThreadSafe
public abstract class AbstractStatisticsHandlerNumeric implements IStatisticsHandlerNumeric
{
  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
  private int m_nInvocationCount = 0;
  private long m_nMin = CGlobal.ILLEGAL_ULONG;
  private long m_nMax = CGlobal.ILLEGAL_ULONG;
  private long m_nSum = 0;
  // Only non-null if "m_nSum" overflowed at least once
  private BigInteger m_aSumOverflow;

  /** {@inheritDoc} */
  @Nonnegative
  public final int getInvocationCount ()
  {
    return m_aRWLock.readLockedInt (() -> m_nInvocationCount);
  }

  protected final void addValue (final long nValue)
  {
    // Better performance when done manually
    m_aRWLock.writeLock ().lock ();
    try
    {
      m_nInvocationCount++;
      if (m_nMin == CGlobal.ILLEGAL_ULONG || nValue < m_nMin)
        m_nMin = nValue;
      if (m_nMax == CGlobal.ILLEGAL_ULONG || nValue > m_nMax)
        m_nMax = nValue;
      if (m_aSumOverflow != null)
        m_aSumOverflow = m_aSumOverflow.add (BigInteger.valueOf (nValue));
      else
        try
        {
          // This is the common case, and it creates no object at all
          m_nSum = Math.addExact (m_nSum, nValue);
        }
        catch (final ArithmeticException ex)
        {
          // Happens at most once per handler
          m_aSumOverflow = BigInteger.valueOf (m_nSum).add (BigInteger.valueOf (nValue));
        }
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  /** {@inheritDoc} */
  @NonNull
  public final BigInteger getSum ()
  {
    return m_aRWLock.readLockedGet (() -> m_aSumOverflow != null ? m_aSumOverflow
                                                                  : BigInteger.valueOf (m_nSum));
  }

  /** {@inheritDoc} */
  @CheckForSigned
  public final long getMin ()
  {
    return m_aRWLock.readLockedLong (() -> m_nMin);
  }

  /** {@inheritDoc} */
  @CheckForSigned
  public final long getAverage ()
  {
    return m_aRWLock.readLockedLong (() -> {
      if (m_nInvocationCount == 0)
        return CGlobal.ILLEGAL_ULONG;
      if (m_aSumOverflow != null)
        return m_aSumOverflow.divide (BigInteger.valueOf (m_nInvocationCount)).longValue ();
      return m_nSum / m_nInvocationCount;
    });
  }

  /** {@inheritDoc} */
  @CheckForSigned
  public long getMax ()
  {
    return m_aRWLock.readLockedLong (() -> m_nMax);
  }
}
