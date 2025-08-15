/*
 * Copyright (C) 2015-2025 Philip Helger (www.helger.com)
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

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.statistics.api.IMutableStatisticsHandlerCache;

/**
 * Default implementation of {@link IMutableStatisticsHandlerCache}
 *
 * @author Philip Helger
 */
@ThreadSafe
public class StatisticsHandlerCache implements IMutableStatisticsHandlerCache
{
  private final AtomicInteger m_aHits = new AtomicInteger ();
  private final AtomicInteger m_aMisses = new AtomicInteger ();

  @Nonnegative
  public int getInvocationCount ()
  {
    return getHits () + getMisses ();
  }

  public void cacheHit ()
  {
    m_aHits.incrementAndGet ();
  }

  public void cacheMiss ()
  {
    m_aMisses.incrementAndGet ();
  }

  @Nonnegative
  public int getHits ()
  {
    return m_aHits.intValue ();
  }

  @Nonnegative
  public int getMisses ()
  {
    return m_aMisses.intValue ();
  }
}
