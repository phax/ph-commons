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
import java.util.concurrent.atomic.AtomicInteger;

import com.helger.annotation.CheckForSigned;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.GuardedBy;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.CGlobal;
import com.helger.base.concurrent.SimpleReadWriteLock;
import com.helger.base.statistics.IStatisticsHandlerKeyedNumeric;
import com.helger.base.string.ToStringGenerator;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.collection.impl.ICommonsSet;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

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
                                         .getToString ();
    }
  }

  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
  private final AtomicInteger m_aInvocationCount = new AtomicInteger (0);
  @GuardedBy ("m_aRWLock")
  private final ICommonsMap <String, Value> m_aMap = new CommonsHashMap <> ();

  @Nonnegative
  public final int getInvocationCount ()
  {
    return m_aInvocationCount.get ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsSet <String> getAllKeys ()
  {
    return m_aRWLock.readLockedGet (m_aMap::copyOfKeySet);
  }

  protected final void addValue (@Nullable final String sKey, final long nValue)
  {
    m_aInvocationCount.incrementAndGet ();

    // Better performance when done manually
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
    return m_aRWLock.readLockedInt ( () -> {
      final Value aValue = m_aMap.get (sKey);
      return aValue == null ? CGlobal.ILLEGAL_UINT : aValue.getInvocationCount ();
    });
  }

  @Nullable
  public final BigInteger getSum (@Nullable final String sKey)
  {
    return m_aRWLock.readLockedGet ( () -> {
      final Value aValue = m_aMap.get (sKey);
      return aValue == null ? null : aValue.getSum ();
    });
  }

  @CheckForSigned
  public final long getMin (@Nullable final String sKey)
  {
    return m_aRWLock.readLockedLong ( () -> {
      final Value aValue = m_aMap.get (sKey);
      return aValue == null ? CGlobal.ILLEGAL_ULONG : aValue.getMin ();
    });
  }

  @CheckForSigned
  public final long getAverage (@Nullable final String sKey)
  {
    return m_aRWLock.readLockedLong ( () -> {
      final Value aValue = m_aMap.get (sKey);
      return aValue == null ? CGlobal.ILLEGAL_ULONG : aValue.getAverage ();
    });
  }

  @CheckForSigned
  public long getMax (@Nullable final String sKey)
  {
    return m_aRWLock.readLockedLong ( () -> {
      final Value aValue = m_aMap.get (sKey);
      return aValue == null ? CGlobal.ILLEGAL_ULONG : aValue.getMax ();
    });
  }
}
