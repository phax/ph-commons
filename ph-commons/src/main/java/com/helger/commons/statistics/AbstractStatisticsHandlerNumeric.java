/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.commons.statistics;

import java.math.BigInteger;

import com.helger.annotation.CheckForSigned;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.base.CGlobal;
import com.helger.commons.concurrent.SimpleReadWriteLock;

import jakarta.annotation.Nonnull;

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
  private BigInteger m_aSum = BigInteger.ZERO;

  @Nonnegative
  public final int getInvocationCount ()
  {
    return m_aRWLock.readLockedInt ( () -> m_nInvocationCount);
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
      m_aSum = m_aSum.add (BigInteger.valueOf (nValue));
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Nonnull
  public final BigInteger getSum ()
  {
    return m_aRWLock.readLockedGet ( () -> m_aSum);
  }

  @CheckForSigned
  public final long getMin ()
  {
    return m_aRWLock.readLockedLong ( () -> m_nMin);
  }

  @CheckForSigned
  public final long getAverage ()
  {
    return m_aRWLock.readLockedLong ( () -> {
      if (m_nInvocationCount == 0)
        return CGlobal.ILLEGAL_ULONG;
      return m_aSum.divide (BigInteger.valueOf (m_nInvocationCount)).longValue ();
    });
  }

  @CheckForSigned
  public long getMax ()
  {
    return m_aRWLock.readLockedLong ( () -> m_nMax);
  }
}
