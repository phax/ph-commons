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
package com.helger.commons.stats;

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
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.CGlobal;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * Default implementation of {@link IStatisticsHandlerKeyedCounter}
 * 
 * @author Philip Helger
 */
@ThreadSafe
final class StatisticsHandlerKeyedCounter implements IStatisticsHandlerKeyedCounter
{
  @NotThreadSafe
  private static final class Value
  {
    private int m_nInvocationCount;
    private long m_nCount;

    public Value (final long nInitialValue)
    {
      m_nInvocationCount = 1;
      m_nCount = nInitialValue;
    }

    public void increment (final long nByHowMany)
    {
      m_nInvocationCount++;
      m_nCount += nByHowMany;
    }

    @Nonnegative
    public int getInvocationCount ()
    {
      return m_nInvocationCount;
    }

    public long getCount ()
    {
      return m_nCount;
    }

    @Override
    public String toString ()
    {
      // No object needed for ctor
      return new ToStringGenerator (null).append ("invocations", m_nInvocationCount)
                                         .append ("count", m_nCount)
                                         .toString ();
    }
  }

  private final ReadWriteLock m_aRWLock = new ReentrantReadWriteLock ();
  private final AtomicInteger m_aInvocationCount = new AtomicInteger ();
  private final Map <String, Value> m_aMap = new HashMap <String, Value> ();

  @Nonnegative
  public int getInvocationCount ()
  {
    return m_aInvocationCount.intValue ();
  }

  public void increment (@Nullable final String sKey)
  {
    increment (sKey, 1L);
  }

  public void increment (@Nullable final String sKey, final long nByHowMany)
  {
    m_aInvocationCount.incrementAndGet ();
    m_aRWLock.writeLock ().lock ();
    try
    {
      final Value aPerKey = m_aMap.get (sKey);
      if (aPerKey == null)
        m_aMap.put (sKey, new Value (nByHowMany));
      else
        aPerKey.increment (nByHowMany);
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Nonnull
  @ReturnsMutableCopy
  public Set <String> getAllKeys ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return ContainerHelper.newSet (m_aMap.keySet ());
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @CheckForSigned
  public long getCount (@Nullable final String sKey)
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      final Value aCount = m_aMap.get (sKey);
      return aCount == null ? CGlobal.ILLEGAL_ULONG : aCount.getCount ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @CheckForSigned
  public int getInvocationCount (@Nullable final String sKey)
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      final Value aCount = m_aMap.get (sKey);
      return aCount == null ? CGlobal.ILLEGAL_UINT : aCount.getInvocationCount ();
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
    m_aRWLock.readLock ().lock ();
    try
    {
      return "invocations=" + getInvocationCount () + "; keyed=" + m_aMap.entrySet ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }
}
