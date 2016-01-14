/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
import javax.annotation.concurrent.NotThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.state.EInterrupt;

/**
 * Wrapper around an {@link ExecutorService} with additional helper methods.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public final class ManagedExecutorService
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (ManagedExecutorService.class);

  private final ExecutorService m_aES;

  public ManagedExecutorService (@Nonnull final ExecutorService aES)
  {
    m_aES = ValueEnforcer.notNull (aES, "ExecutorService");
  }

  /**
   * Wait indefinitely on the {@link ExecutorService} until it terminates.
   *
   * @return {@link EInterrupt#INTERRUPTED} if the executor service was
   *         interrupted while awaiting termination. Never <code>null</code>.
   */
  @Nonnull
  public EInterrupt waitUntilAllTasksAreFinished ()
  {
    return waitUntilAllTasksAreFinished (1, TimeUnit.SECONDS);
  }

  /**
   * Wait indefinitely on the {@link ExecutorService} until it terminates.
   *
   * @param nTimeout
   *        the maximum time to wait. Must be &gt; 0.
   * @param eUnit
   *        the time unit of the timeout argument. Must not be <code>null</code>
   *        .
   * @return {@link EInterrupt#INTERRUPTED} if the executor service was
   *         interrupted while awaiting termination. Never <code>null</code>.
   */
  @Nonnull
  public EInterrupt waitUntilAllTasksAreFinished (@Nonnegative final long nTimeout, @Nonnull final TimeUnit eUnit)
  {
    try
    {
      while (!m_aES.awaitTermination (nTimeout, eUnit))
      {
        // wait until completion
      }
    }
    catch (final InterruptedException ex)
    {
      s_aLogger.error ("Error waiting for Executor service " + m_aES + " to end", ex);
      return EInterrupt.INTERRUPTED;
    }
    return EInterrupt.NOT_INTERRUPTED;
  }

  /**
   * Call shutdown on the {@link ExecutorService} and wait indefinitely until it
   * terminated.
   *
   * @return {@link EInterrupt#INTERRUPTED} if the executor service was
   *         interrupted while awaiting termination. Never <code>null</code>.
   */
  @Nonnull
  public EInterrupt shutdownAndWaitUntilAllTasksAreFinished ()
  {
    return shutdownAndWaitUntilAllTasksAreFinished (1, TimeUnit.SECONDS);
  }

  /**
   * Call shutdown on the {@link ExecutorService} and wait indefinitely until it
   * terminated.
   *
   * @param nTimeout
   *        the maximum time to wait. Must be &gt; 0.
   * @param eUnit
   *        the time unit of the timeout argument. Must not be <code>null</code>
   *        .
   * @return {@link EInterrupt#INTERRUPTED} if the executor service was
   *         interrupted while awaiting termination. Never <code>null</code>.
   */
  @Nonnull
  public EInterrupt shutdownAndWaitUntilAllTasksAreFinished (@Nonnegative final long nTimeout,
                                                             @Nonnull final TimeUnit eUnit)
  {
    if (m_aES.isShutdown ())
      return EInterrupt.NOT_INTERRUPTED;

    // accept no further requests
    m_aES.shutdown ();

    // Wait...
    return waitUntilAllTasksAreFinished (nTimeout, eUnit);
  }

  @Nonnull
  public static EInterrupt shutdownAndWaitUntilAllTasksAreFinished (@Nonnull final ExecutorService aES)
  {
    return new ManagedExecutorService (aES).shutdownAndWaitUntilAllTasksAreFinished ();
  }

  @Nonnull
  public static EInterrupt shutdownAndWaitUntilAllTasksAreFinished (@Nonnull final ExecutorService aES,
                                                                    @Nonnegative final long nTimeout,
                                                                    @Nonnull final TimeUnit eUnit)
  {
    return new ManagedExecutorService (aES).shutdownAndWaitUntilAllTasksAreFinished (nTimeout, eUnit);
  }
}
