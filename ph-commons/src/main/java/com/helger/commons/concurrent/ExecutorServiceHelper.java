/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.state.EInterrupt;

/**
 * Wrapper around an {@link ExecutorService} with additional helper methods.
 *
 * @author Philip Helger
 */
@Immutable
public final class ExecutorServiceHelper
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (ExecutorServiceHelper.class);

  private ExecutorServiceHelper ()
  {}

  /**
   * Wait indefinitely on the {@link ExecutorService} until it terminates.
   *
   * @param aES
   *        The {@link ExecutorService} to operate on. May not be
   *        <code>null</code>.
   * @return {@link EInterrupt#INTERRUPTED} if the executor service was
   *         interrupted while awaiting termination. Never <code>null</code>.
   */
  @Nonnull
  public static EInterrupt waitUntilAllTasksAreFinished (@Nonnull final ExecutorService aES)
  {
    return waitUntilAllTasksAreFinished (aES, 1, TimeUnit.SECONDS);
  }

  /**
   * Wait indefinitely on the {@link ExecutorService} until it terminates.
   *
   * @param aES
   *        The {@link ExecutorService} to operate on. May not be
   *        <code>null</code>.
   * @param nTimeout
   *        the maximum time to wait. Must be &gt; 0.
   * @param eUnit
   *        the time unit of the timeout argument. Must not be <code>null</code>
   *        .
   * @return {@link EInterrupt#INTERRUPTED} if the executor service was
   *         interrupted while awaiting termination. Never <code>null</code>.
   */
  @Nonnull
  public static EInterrupt waitUntilAllTasksAreFinished (@Nonnull final ExecutorService aES,
                                                         @Nonnegative final long nTimeout,
                                                         @Nonnull final TimeUnit eUnit)
  {
    ValueEnforcer.notNull (aES, "ExecutorService");

    try
    {
      while (!aES.awaitTermination (nTimeout, eUnit))
      {
        // wait until completion
      }
    }
    catch (final InterruptedException ex)
    {
      if (s_aLogger.isErrorEnabled ())
        s_aLogger.error ("Error waiting for Executor service " + aES + " to end", ex);
      Thread.currentThread ().interrupt ();
      return EInterrupt.INTERRUPTED;
    }
    return EInterrupt.NOT_INTERRUPTED;
  }

  /**
   * Call shutdown on the {@link ExecutorService} and wait indefinitely until it
   * terminated.
   *
   * @param aES
   *        The {@link ExecutorService} to operate on. May not be
   *        <code>null</code>.
   * @return {@link EInterrupt#INTERRUPTED} if the executor service was
   *         interrupted while awaiting termination. Never <code>null</code>.
   */
  @Nonnull
  public static EInterrupt shutdownAndWaitUntilAllTasksAreFinished (@Nonnull final ExecutorService aES)
  {
    return shutdownAndWaitUntilAllTasksAreFinished (aES, 1, TimeUnit.SECONDS);
  }

  /**
   * Call shutdown on the {@link ExecutorService} and wait indefinitely until it
   * terminated.
   *
   * @param aES
   *        The {@link ExecutorService} to operate on. May not be
   *        <code>null</code>.
   * @param nTimeout
   *        the maximum time to wait. Must be &gt; 0.
   * @param eUnit
   *        the time unit of the timeout argument. Must not be <code>null</code>
   *        .
   * @return {@link EInterrupt#INTERRUPTED} if the executor service was
   *         interrupted while awaiting termination. Never <code>null</code>.
   */
  @Nonnull
  public static EInterrupt shutdownAndWaitUntilAllTasksAreFinished (@Nonnull final ExecutorService aES,
                                                                    @Nonnegative final long nTimeout,
                                                                    @Nonnull final TimeUnit eUnit)
  {
    ValueEnforcer.notNull (aES, "ExecutorService");

    if (aES.isShutdown ())
      return EInterrupt.NOT_INTERRUPTED;

    // accept no further requests
    aES.shutdown ();

    // Wait...
    return waitUntilAllTasksAreFinished (aES, nTimeout, eUnit);
  }
}
