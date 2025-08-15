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
import java.util.concurrent.atomic.AtomicLong;

import com.helger.annotation.CheckForSigned;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.statistics.api.IMutableStatisticsHandlerCounter;

/**
 * Default implementation of {@link IMutableStatisticsHandlerCounter}
 *
 * @author Philip Helger
 */
@ThreadSafe
public class StatisticsHandlerCounter implements IMutableStatisticsHandlerCounter
{
  private final AtomicInteger m_aInvocationCount = new AtomicInteger ();
  private final AtomicLong m_aCount = new AtomicLong ();

  @Nonnegative
  public int getInvocationCount ()
  {
    return m_aInvocationCount.intValue ();
  }

  @CheckForSigned
  public long getCount ()
  {
    return m_aCount.longValue ();
  }

  public void increment (final long nByHowMany)
  {
    m_aInvocationCount.incrementAndGet ();
    m_aCount.addAndGet (nByHowMany);
  }
}
