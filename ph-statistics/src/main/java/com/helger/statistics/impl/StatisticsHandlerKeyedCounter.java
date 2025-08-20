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
package com.helger.statistics.impl;

import java.util.concurrent.atomic.AtomicInteger;

import com.helger.annotation.CheckForSigned;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.CGlobal;
import com.helger.base.concurrent.SimpleReadWriteLock;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.ICommonsMap;
import com.helger.collection.commons.ICommonsSet;
import com.helger.statistics.api.IMutableStatisticsHandlerKeyedCounter;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Default implementation of {@link IMutableStatisticsHandlerKeyedCounter}
 *
 * @author Philip Helger
 */
@ThreadSafe
public class StatisticsHandlerKeyedCounter implements IMutableStatisticsHandlerKeyedCounter
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
      return new ToStringGenerator (null).append ("invocations", m_nInvocationCount).append ("count", m_nCount).getToString ();
    }
  }

  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
  private final AtomicInteger m_aInvocationCount = new AtomicInteger ();
  private final ICommonsMap <String, Value> m_aMap = new CommonsHashMap <> ();

  @Nonnegative
  public int getInvocationCount ()
  {
    return m_aInvocationCount.intValue ();
  }

  public void increment (@Nullable final String sKey, final long nByHowMany)
  {
    m_aInvocationCount.incrementAndGet ();
    m_aRWLock.writeLocked ( () -> {
      final Value aPerKey = m_aMap.get (sKey);
      if (aPerKey == null)
        m_aMap.put (sKey, new Value (nByHowMany));
      else
        aPerKey.increment (nByHowMany);
    });
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsSet <String> getAllKeys ()
  {
    return m_aRWLock.readLockedGet (m_aMap::copyOfKeySet);
  }

  @CheckForSigned
  public long getCount (@Nullable final String sKey)
  {
    return m_aRWLock.readLockedLong ( () -> {
      final Value aCount = m_aMap.get (sKey);
      return aCount == null ? CGlobal.ILLEGAL_ULONG : aCount.getCount ();
    });
  }

  @CheckForSigned
  public int getInvocationCount (@Nullable final String sKey)
  {
    return m_aRWLock.readLockedInt ( () -> {
      final Value aCount = m_aMap.get (sKey);
      return aCount == null ? CGlobal.ILLEGAL_UINT : aCount.getInvocationCount ();
    });
  }
}
