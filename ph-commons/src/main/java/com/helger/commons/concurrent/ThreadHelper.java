/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
package com.helger.commons.concurrent;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.CGlobal;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.state.ESuccess;

/**
 * Some thread utility methods.
 *
 * @author Philip Helger
 */
@Immutable
public final class ThreadHelper
{
  private ThreadHelper ()
  {}

  /**
   * Sleep the current thread for a certain amount of time
   *
   * @param nMinutes
   *        The minutes to sleep. Must be &ge; 0.
   * @return {@link ESuccess#SUCCESS} if sleeping was not interrupted,
   *         {@link ESuccess#FAILURE} if sleeping was interrupted
   */
  @Nonnull
  public static ESuccess sleepMinutes (@Nonnegative final long nMinutes)
  {
    ValueEnforcer.isGE0 (nMinutes, "Minutes");
    return sleep (nMinutes * CGlobal.MILLISECONDS_PER_MINUTE);
  }

  /**
   * Sleep the current thread for a certain amount of time
   *
   * @param nSeconds
   *        The seconds to sleep. Must be &ge; 0.
   * @return {@link ESuccess#SUCCESS} if sleeping was not interrupted,
   *         {@link ESuccess#FAILURE} if sleeping was interrupted
   */
  @Nonnull
  public static ESuccess sleepSeconds (@Nonnegative final long nSeconds)
  {
    ValueEnforcer.isGE0 (nSeconds, "Seconds");
    return sleep (nSeconds * CGlobal.MILLISECONDS_PER_SECOND);
  }

  /**
   * Sleep the current thread for a certain amount of time
   *
   * @param aDuration
   *        The time value to use. May not be <code>null</code>.
   * @return {@link ESuccess#SUCCESS} if sleeping was not interrupted,
   *         {@link ESuccess#FAILURE} if sleeping was interrupted
   */
  @Nonnull
  public static ESuccess sleep (@Nonnull final Duration aDuration)
  {
    ValueEnforcer.notNull (aDuration, "Duration");
    return sleep (aDuration.toMillis ());
  }

  /**
   * Sleep the current thread for a certain amount of time
   *
   * @param nDuration
   *        The duration to sleep. Must be &ge; 0.
   * @param aTimeUnit
   *        The time unit to use. May not be <code>null</code>.
   * @return {@link ESuccess#SUCCESS} if sleeping was not interrupted,
   *         {@link ESuccess#FAILURE} if sleeping was interrupted
   */
  @Nonnull
  public static ESuccess sleep (@Nonnegative final long nDuration, @Nonnull final TimeUnit aTimeUnit)
  {
    ValueEnforcer.isGE0 (nDuration, "Duration");
    ValueEnforcer.notNull (aTimeUnit, "TimeUnit");

    return sleep (aTimeUnit.toMillis (nDuration));
  }

  /**
   * Sleep the current thread for a certain amount of time
   *
   * @param nMilliseconds
   *        The milliseconds to sleep. Must be &ge; 0.
   * @return {@link ESuccess#SUCCESS} if sleeping was not interrupted,
   *         {@link ESuccess#FAILURE} if sleeping was interrupted
   */
  @Nonnull
  public static ESuccess sleep (@Nonnegative final long nMilliseconds)
  {
    ValueEnforcer.isGE0 (nMilliseconds, "MilliSeconds");

    try
    {
      Thread.sleep (nMilliseconds);
      return ESuccess.SUCCESS;
    }
    catch (final InterruptedException ex)
    {
      Thread.currentThread ().interrupt ();
      return ESuccess.FAILURE;
    }
  }
}
