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
package com.helger.commons.deadlock;

import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.CGlobal;
import com.helger.commons.state.EChange;

/**
 * A dead lock detection timer that checks for dead locks in a certain interval.
 * Uses {@link ThreadDeadlockDetector} internally.
 * 
 * @author Philip Helger
 */
@NotThreadSafe
public final class ThreadDeadlockDetectionTimer
{
  /**
   * The number of milliseconds between checking for deadlocks. It may be
   * expensive to check for deadlocks, and it is not critical to know so
   * quickly.
   */
  private static final long DEFAULT_DEADLOCK_CHECK_PERIOD = 10 * CGlobal.MILLISECONDS_PER_SECOND;
  private static final long INITIAL_DELAY_MS = 10;
  private static final Logger s_aLogger = LoggerFactory.getLogger (ThreadDeadlockDetectionTimer.class);

  private final ThreadDeadlockDetector m_aTLD = new ThreadDeadlockDetector ();
  private final TimerTask m_aTimerTask;
  private final Timer m_aThreadCheck = new Timer ("ThreadDeadlockDetector", true);

  public ThreadDeadlockDetectionTimer ()
  {
    this (DEFAULT_DEADLOCK_CHECK_PERIOD);
  }

  public ThreadDeadlockDetectionTimer (@Nonnegative final long nDeadlockCheckPeriod)
  {
    m_aTimerTask = new TimerTask ()
    {
      @Override
      public void run ()
      {
        m_aTLD.run ();
      }
    };
    m_aThreadCheck.schedule (m_aTimerTask, INITIAL_DELAY_MS, nDeadlockCheckPeriod);
    s_aLogger.info ("Deadlock detector started!");
  }

  /**
   * Cancel the deadlock detection task
   */
  public void stop ()
  {
    if (m_aTimerTask.cancel ())
    {
      s_aLogger.info ("Deadlock detector stopped!");
    }
  }

  @Nonnull
  public EChange addListener (@Nonnull final IThreadDeadlockListener aListener)
  {
    return m_aTLD.addListener (aListener);
  }

  @Nonnull
  public EChange removeListener (@Nullable final IThreadDeadlockListener aListener)
  {
    return m_aTLD.removeListener (aListener);
  }

  @Nonnull
  public EChange removeAllListeners ()
  {
    return m_aTLD.removeAllListeners ();
  }

  @Nonnegative
  public int getListenerCount ()
  {
    return m_aTLD.getListenerCount ();
  }
}
