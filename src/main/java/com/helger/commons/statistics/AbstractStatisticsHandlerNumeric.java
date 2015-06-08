/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.CheckForSigned;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.CGlobal;
import com.helger.commons.annotation.Nonempty;

/**
 * Abstract base class for numeric statistic handler
 *
 * @author Philip Helger
 */
@ThreadSafe
public abstract class AbstractStatisticsHandlerNumeric implements IStatisticsHandlerNumeric
{
  private final ReadWriteLock m_aRWLock = new ReentrantReadWriteLock ();
  private int m_nInvocationCount = 0;
  private long m_nMin = CGlobal.ILLEGAL_ULONG;
  private long m_nMax = CGlobal.ILLEGAL_ULONG;
  private BigInteger m_aSum = BigInteger.ZERO;

  @Nonnegative
  public final int getInvocationCount ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_nInvocationCount;
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  protected final void addValue (final long nValue)
  {
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
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_aSum;
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @CheckForSigned
  public final long getMin ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_nMin;
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @CheckForSigned
  public final long getAverage ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      if (m_nInvocationCount == 0)
        return CGlobal.ILLEGAL_ULONG;
      return m_aSum.divide (BigInteger.valueOf (m_nInvocationCount)).longValue ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @CheckForSigned
  public long getMax ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_nMax;
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nonnull
  @Nonempty
  public String getAsString ()
  {
    return "invocations=" +
           getInvocationCount () +
           ";sum=" +
           getSum () +
           ";min=" +
           getMin () +
           ";avg=" +
           getAverage () +
           ";max=" +
           getMax ();
  }
}
