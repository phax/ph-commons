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
package com.helger.commons.statistics;

import java.math.BigInteger;

import javax.annotation.CheckForSigned;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.CGlobal;
import com.helger.commons.concurrent.SimpleReadWriteLock;

/**
 * Abstract base class for numeric statistic handler
 *
 * @author Philip Helger
 */
@ThreadSafe
public abstract class AbstractStatisticsHandlerNumeric implements IStatisticsHandlerNumeric
{
  private final transient SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
  private int m_nInvocationCount = 0;
  private long m_nMin = CGlobal.ILLEGAL_ULONG;
  private long m_nMax = CGlobal.ILLEGAL_ULONG;
  private BigInteger m_aSum = BigInteger.ZERO;

  @Nonnegative
  public final int getInvocationCount ()
  {
    return m_aRWLock.readLocked ( () -> m_nInvocationCount);
  }

  protected final void addValue (final long nValue)
  {
    m_aRWLock.writeLocked ( () -> {
      m_nInvocationCount++;
      if (m_nMin == CGlobal.ILLEGAL_ULONG || nValue < m_nMin)
        m_nMin = nValue;
      if (m_nMax == CGlobal.ILLEGAL_ULONG || nValue > m_nMax)
        m_nMax = nValue;
      m_aSum = m_aSum.add (BigInteger.valueOf (nValue));
    });
  }

  @Nonnull
  public final BigInteger getSum ()
  {
    return m_aRWLock.readLocked ( () -> m_aSum);
  }

  @CheckForSigned
  public final long getMin ()
  {
    return m_aRWLock.readLocked ( () -> m_nMin);
  }

  @CheckForSigned
  public final long getAverage ()
  {
    return m_aRWLock.readLocked ( () -> {
      if (m_nInvocationCount == 0)
        return CGlobal.ILLEGAL_ULONG;
      return m_aSum.divide (BigInteger.valueOf (m_nInvocationCount)).longValue ();
    });
  }

  @CheckForSigned
  public long getMax ()
  {
    return m_aRWLock.readLocked ( () -> m_nMax);
  }
}
