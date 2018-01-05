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
package com.helger.scope.singleton;

import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.debug.GlobalDebug;

/**
 * Global singleton utility methods that don't nicely fit somewhere else.
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class SingletonHelper
{
  public static final boolean DEFAULT_DEBUG_CONSISTENCY = GlobalDebug.isDebugMode ();
  public static final boolean DEFAULT_DEBUG_WITH_STACK_TRACE = false;

  private static final SimpleReadWriteLock s_aRWLock = new SimpleReadWriteLock ();
  @GuardedBy ("s_aRWLock")
  private static boolean s_bDebugConsistency = DEFAULT_DEBUG_CONSISTENCY;
  @GuardedBy ("s_aRWLock")
  private static boolean s_bDebugWithStackTrace = DEFAULT_DEBUG_WITH_STACK_TRACE;

  private SingletonHelper ()
  {}

  /**
   * Enable or disable singleton consistency debugging.
   *
   * @param bDebugConsistency
   *        <code>true</code> if the singleton consistency should be debugged,
   *        <code>false</code> to disable it.
   */
  public static void setDebugConsistency (final boolean bDebugConsistency)
  {
    s_aRWLock.writeLocked ( () -> s_bDebugConsistency = bDebugConsistency);
  }

  /**
   * @return <code>true</code> if singleton consistency debugging is enabled,
   *         <code>false</code> if it is disabled. The default value is
   *         {@link GlobalDebug#isDebugMode()}.
   */
  public static boolean isDebugConsistency ()
  {
    return s_aRWLock.readLocked ( () -> s_bDebugConsistency);
  }

  /**
   * Enable or disable stack traces when debugging singletons.
   *
   * @param bDebugWithStackTrace
   *        <code>true</code> to enable stack traces, <code>false</code> to
   *        disable them. By default is is disabled.
   */
  public static void setDebugWithStackTrace (final boolean bDebugWithStackTrace)
  {
    s_aRWLock.writeLocked ( () -> s_bDebugWithStackTrace = bDebugWithStackTrace);
  }

  /**
   * @return <code>true</code> if stack traces should be logged,
   *         <code>false</code> if not. The default value is disabled.
   */
  public static boolean isDebugWithStackTrace ()
  {
    return s_aRWLock.readLocked ( () -> s_bDebugWithStackTrace);
  }

  /**
   * @return An exception with the current stack trace or <code>null</code> if
   *         {@link #isDebugWithStackTrace()} is <code>false</code>
   * @see #isDebugWithStackTrace()
   */
  @Nullable
  public static Throwable getDebugStackTrace ()
  {
    return isDebugWithStackTrace () ? new Exception () : null;
  }
}
