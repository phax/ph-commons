/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.base.timing;

import java.time.Duration;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.CGlobal;
import com.helger.base.state.EChange;
import com.helger.base.state.IStoppable;
import com.helger.base.tostring.ToStringGenerator;

/**
 * Simple stop watch based on {@link System#nanoTime()}.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class StopWatch implements IStoppable
{
  private long m_nStartDT = 0;
  private long m_nDurationNanos = 0;

  /**
   * Constructor.
   *
   * @param bStart
   *        if <code>true</code> the stop watch is directly started!
   */
  protected StopWatch (final boolean bStart)
  {
    if (bStart)
      start ();
  }

  /**
   * Reset all saved durations, in case this stop watch is to be used in a loop.
   * Does not change the start/stop state.
   *
   * @return {@link EChange}.
   */
  @NonNull
  public EChange reset ()
  {
    if (m_nDurationNanos == 0)
      return EChange.UNCHANGED;
    m_nDurationNanos = 0;
    return EChange.CHANGED;
  }

  /**
   * @return The current time in nano seconds.
   */
  protected static final long getCurrentNanoTime ()
  {
    return System.nanoTime ();
  }

  /**
   * Start the stop watch.
   *
   * @return {@link EChange}.
   */
  @NonNull
  public final EChange start ()
  {
    // Already started?
    if (m_nStartDT > 0)
      return EChange.UNCHANGED;
    m_nStartDT = getCurrentNanoTime ();
    return EChange.CHANGED;
  }

  /**
   * Stop the stop watch.
   *
   * @return {@link EChange#CHANGED} if the stop watch was previously running
   *         and is now stopped, and {@link EChange#UNCHANGED} if the stop watch
   *         was already stopped.
   */
  @NonNull
  public EChange stop ()
  {
    // Already stopped?
    if (m_nStartDT == 0)
      return EChange.UNCHANGED;

    final long nCurrentNanoTime = getCurrentNanoTime ();
    m_nDurationNanos += (nCurrentNanoTime - m_nStartDT);
    m_nStartDT = 0;
    return EChange.CHANGED;
  }

  /**
   * Stops, resets and starts the stop watch.
   *
   * @see #stop()
   * @see #reset()
   * @see #start()
   */
  public void restart ()
  {
    stop ();
    reset ();
    start ();
  }

  /**
   * @return <code>true</code> if the stop watch is currently started (running),
   *         <code>false</code> otherwise.
   */
  public boolean isStarted ()
  {
    return m_nStartDT > 0;
  }

  /**
   * @return <code>true</code> if the stop watch is currently stopped (not
   *         running), <code>false</code> otherwise.
   */
  public boolean isStopped ()
  {
    return m_nStartDT == 0;
  }

  /**
   * @return The elapsed nano seconds (1000 nano seconds = 1 milli second).
   */
  @Nonnegative
  public long getNanos ()
  {
    return m_nDurationNanos;
  }

  /**
   * @return The elapsed milli seconds.
   */
  @Nonnegative
  public long getMillis ()
  {
    return m_nDurationNanos / CGlobal.NANOSECONDS_PER_MILLISECOND;
  }

  /**
   * @return The elapsed duration. Never <code>null</code>.
   */
  @NonNull
  public Duration getDuration ()
  {
    return Duration.ofNanos (m_nDurationNanos);
  }

  /**
   * Stop the stop watch and get the elapsed milliseconds since the start. If
   * the stop watch was started and stopped multiple times, the duration is
   * added.
   *
   * @return The elapsed milli seconds or 0 if the stop watch was never started.
   */
  @Nonnegative
  public long stopAndGetMillis ()
  {
    stop ();
    return getMillis ();
  }

  /**
   * Stop the stop watch and get the duration since the start. If the stop watch
   * was started and stopped multiple times, the duration is added.
   *
   * @return The elapsed duration or 0 if the stop watch was never started.
   */
  @NonNull
  public Duration stopAndGetDuration ()
  {
    stop ();
    return getDuration ();
  }

  /**
   * Create an intermediate time (lap time). It internally stops, takes the
   * duration and restarts.
   *
   * @return The elapsed duration or 0 if the stop watch was never started.
   * @since 10.0.0
   */
  @NonNull
  public Duration getLapDuration ()
  {
    stop ();
    final Duration ret = getDuration ();
    start ();
    return ret;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("StartDT", m_nStartDT)
                                       .append ("DurationNanos", m_nDurationNanos)
                                       .getToString ();
  }

  /**
   * @return A new {@link StopWatch} object that is started. Never
   *         <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static StopWatch createdStarted ()
  {
    return new StopWatch (true);
  }

  /**
   * @return A new {@link StopWatch} object that is NOT started. Never
   *         <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static StopWatch createdStopped ()
  {
    return new StopWatch (false);
  }

  /**
   * Run the passed runnable and measure the time. In v10 this was changed to
   * return Duration instead of TimeValue.
   *
   * @param aRunnable
   *        The runnable to be executed. May not be <code>null</code>.
   * @return The elapsed Duration. Never <code>null</code>.
   */
  @NonNull
  public static Duration runMeasured (@NonNull final Runnable aRunnable)
  {
    final StopWatch aSW = createdStarted ();
    aRunnable.run ();
    return aSW.stopAndGetDuration ();
  }
}
