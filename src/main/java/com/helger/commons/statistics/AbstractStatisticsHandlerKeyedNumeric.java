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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.CheckForSigned;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.CGlobal;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * Abstract base class for a keyed numeric statistic handler
 *
 * @author Philip Helger
 */
@ThreadSafe
public abstract class AbstractStatisticsHandlerKeyedNumeric implements IStatisticsHandlerKeyedNumeric
{
  private static final class Value
  {
    private int m_nInvocationCount;
    private long m_nMin;
    private long m_nMax;
    private BigInteger m_aSum;

    public Value (final long nValue)
    {
      m_nInvocationCount = 1;
      m_nMin = nValue;
      m_nMax = nValue;
      m_aSum = BigInteger.valueOf (nValue);
    }

    public void add (final long nValue)
    {
      m_nInvocationCount++;
      if (nValue < m_nMin)
        m_nMin = nValue;
      if (nValue > m_nMax)
        m_nMax = nValue;
      m_aSum = m_aSum.add (BigInteger.valueOf (nValue));
    }

    @Nonnegative
    public int getInvocationCount ()
    {
      return m_nInvocationCount;
    }

    @CheckForSigned
    public long getMin ()
    {
      return m_nMin;
    }

    @CheckForSigned
    public long getMax ()
    {
      return m_nMax;
    }

    @Nonnull
    public BigInteger getSum ()
    {
      return m_aSum;
    }

    @CheckForSigned
    public long getAverage ()
    {
      return m_aSum.divide (BigInteger.valueOf (m_nInvocationCount)).longValue ();
    }

    @Override
    public String toString ()
    {
      // No object ID needed for toString
      return new ToStringGenerator (null).append ("invocations", m_nInvocationCount)
                                         .append ("min", m_nMin)
                                         .append ("max", m_nMax)
                                         .append ("sum", m_aSum)
                                         .toString ();
    }
  }

  private final ReadWriteLock m_aRWLock = new ReentrantReadWriteLock ();
  private final AtomicInteger m_aInvocationCount = new AtomicInteger (0);
  private final Map <String, Value> m_aMap = new HashMap <String, Value> ();

  @Nonnegative
  public final int getInvocationCount ()
  {
    return m_aInvocationCount.get ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public Set <String> getAllKeys ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return CollectionHelper.newSet (m_aMap.keySet ());
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  protected final void addValue (@Nullable final String sKey, final long nValue)
  {
    m_aInvocationCount.incrementAndGet ();
    m_aRWLock.writeLock ().lock ();
    try
    {
      final Value aValue = m_aMap.get (sKey);
      if (aValue == null)
        m_aMap.put (sKey, new Value (nValue));
      else
        aValue.add (nValue);
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @CheckForSigned
  public final int getInvocationCount (@Nullable final String sKey)
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      final Value aValue = m_aMap.get (sKey);
      return aValue == null ? CGlobal.ILLEGAL_UINT : aValue.getInvocationCount ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nullable
  public final BigInteger getSum (@Nullable final String sKey)
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      final Value aValue = m_aMap.get (sKey);
      return aValue == null ? null : aValue.getSum ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @CheckForSigned
  public final long getMin (@Nullable final String sKey)
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      final Value aValue = m_aMap.get (sKey);
      return aValue == null ? CGlobal.ILLEGAL_ULONG : aValue.getMin ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @CheckForSigned
  public final long getAverage (@Nullable final String sKey)
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      final Value aValue = m_aMap.get (sKey);
      return aValue == null ? CGlobal.ILLEGAL_ULONG : aValue.getAverage ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @CheckForSigned
  public long getMax (@Nullable final String sKey)
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      final Value aValue = m_aMap.get (sKey);
      return aValue == null ? CGlobal.ILLEGAL_ULONG : aValue.getMax ();
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
    return "invocations=" + getInvocationCount () + ";map=" + m_aMap.entrySet ();
  }
}
