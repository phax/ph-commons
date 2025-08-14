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
package com.helger.scope;

import com.helger.annotation.concurrent.GuardedBy;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.base.concurrent.SimpleReadWriteLock;

import jakarta.annotation.Nullable;

/**
 * Global scope utility methods that don't nicely fit somewhere else.
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class ScopeHelper
{
  public static final boolean DEFAULT_DEBUG_LIFE_CYCLE = false;
  public static final boolean DEFAULT_DEBUG_GLOBAL_SCOPE = false;
  public static final boolean DEFAULT_DEBUG_SESSION_SCOPE = false;
  public static final boolean DEFAULT_DEBUG_REQUEST_SCOPE = false;
  public static final boolean DEFAULT_DEBUG_WITH_STACK_TRACE = false;

  private static final SimpleReadWriteLock RW_LOCK = new SimpleReadWriteLock ();
  @GuardedBy ("RW_LOCK")
  private static boolean s_bDebugLifeCycle = DEFAULT_DEBUG_LIFE_CYCLE;
  @GuardedBy ("RW_LOCK")
  private static boolean s_bDebugGlobalScope = DEFAULT_DEBUG_GLOBAL_SCOPE;
  @GuardedBy ("RW_LOCK")
  private static boolean s_bDebugSessionScope = DEFAULT_DEBUG_SESSION_SCOPE;
  @GuardedBy ("RW_LOCK")
  private static boolean s_bDebugRequestScope = DEFAULT_DEBUG_REQUEST_SCOPE;
  @GuardedBy ("RW_LOCK")
  private static boolean s_bDebugWithStackTrace = DEFAULT_DEBUG_WITH_STACK_TRACE;

  private ScopeHelper ()
  {}

  /**
   * Enable or disable scope life cycle debugging for all scopes.
   *
   * @param bDebugLifeCycle
   *        <code>true</code> if the scope life cycle should be debugged, <code>false</code> to
   *        disable it. By default is is disabled.
   */
  public static void setLifeCycleDebuggingEnabled (final boolean bDebugLifeCycle)
  {
    RW_LOCK.writeLocked ( () -> s_bDebugLifeCycle = bDebugLifeCycle);
  }

  /**
   * @return <code>true</code> if life cycle debugging for all scopes is enabled, <code>false</code>
   *         if it is disabled. The default value is disabled.
   */
  public static boolean isLifeCycleDebuggingEnabled ()
  {
    return RW_LOCK.readLockedBoolean ( () -> s_bDebugLifeCycle);
  }

  /**
   * Enable or disable global scope life cycle debugging.
   *
   * @param bDebugScope
   *        <code>true</code> if the global scope life cycle should be debugged, <code>false</code>
   *        to disable it. By default is is disabled.
   */
  public static void setDebugGlobalScopeEnabled (final boolean bDebugScope)
  {
    RW_LOCK.writeLocked ( () -> s_bDebugGlobalScope = bDebugScope);
  }

  /**
   * @return <code>true</code> if global scope life cycle debugging is enabled, <code>false</code>
   *         if it is disabled. The default value is disabled.
   */
  public static boolean isDebugGlobalScopeEnabled ()
  {
    return RW_LOCK.readLockedBoolean ( () -> s_bDebugGlobalScope);
  }

  /**
   * Enable or disable session scope life cycle debugging.
   *
   * @param bDebugScope
   *        <code>true</code> if the session scope life cycle should be debugged, <code>false</code>
   *        to disable it. By default is is disabled.
   */
  public static void setDebugSessionScopeEnabled (final boolean bDebugScope)
  {
    RW_LOCK.writeLocked ( () -> s_bDebugSessionScope = bDebugScope);
  }

  /**
   * @return <code>true</code> if session scope life cycle debugging is enabled, <code>false</code>
   *         if it is disabled. The default value is disabled.
   */
  public static boolean isDebugSessionScopeEnabled ()
  {
    return RW_LOCK.readLockedBoolean ( () -> s_bDebugSessionScope);
  }

  /**
   * Enable or disable request scope life cycle debugging.
   *
   * @param bDebugScope
   *        <code>true</code> if the request scope life cycle should be debugged, <code>false</code>
   *        to disable it. By default is is disabled.
   */
  public static void setDebugRequestScopeEnabled (final boolean bDebugScope)
  {
    RW_LOCK.writeLocked ( () -> s_bDebugRequestScope = bDebugScope);
  }

  /**
   * @return <code>true</code> if request scope life cycle debugging is enabled, <code>false</code>
   *         if it is disabled. The default value is disabled.
   */
  public static boolean isDebugRequestScopeEnabled ()
  {
    // Called very often - performance improvement
    RW_LOCK.readLock ().lock ();
    try
    {
      return s_bDebugRequestScope;
    }
    finally
    {
      RW_LOCK.readLock ().unlock ();
    }
  }

  /**
   * Enable or disable stack traces when debugging scopes.
   *
   * @param bDebugWithStackTrace
   *        <code>true</code> to enable stack traces, <code>false</code> to disable them. By default
   *        is is disabled.
   */
  public static void setDebugWithStackTrace (final boolean bDebugWithStackTrace)
  {
    RW_LOCK.writeLocked ( () -> s_bDebugWithStackTrace = bDebugWithStackTrace);
  }

  /**
   * @return <code>true</code> if stack traces should be logged, <code>false</code> if not. The
   *         default value is disabled.
   */
  public static boolean isDebugWithStackTrace ()
  {
    return RW_LOCK.readLockedBoolean ( () -> s_bDebugWithStackTrace);
  }

  /**
   * This is a just a helper method to determine whether global scope creation/deletion issues
   * should be logged or not.
   *
   * @return <code>true</code> if global scope creation/deletion should be logged,
   *         <code>false</code> otherwise.
   * @since 11.0.4
   */
  public static boolean isDebugGlobalScopeLifeCycle ()
  {
    return isLifeCycleDebuggingEnabled () || isDebugGlobalScopeEnabled ();
  }

  /**
   * This is a just a helper method to determine whether session scope creation/deletion issues
   * should be logged or not.
   *
   * @return <code>true</code> if session scope creation/deletion should be logged,
   *         <code>false</code> otherwise.
   * @since 11.0.4
   */
  public static boolean isDebugSessionScopeLifeCycle ()
  {
    return isLifeCycleDebuggingEnabled () || isDebugSessionScopeEnabled ();
  }

  /**
   * This is a just a helper method to determine whether request scope creation/deletion issues
   * should be logged or not.
   *
   * @return <code>true</code> if request scope creation/deletion should be logged,
   *         <code>false</code> otherwise.
   * @since 11.0.4
   */
  public static boolean isDebugRequestScopeLifeCycle ()
  {
    return isLifeCycleDebuggingEnabled () || isDebugRequestScopeEnabled ();
  }

  /**
   * The exception used for receiving scope debug stack traces
   *
   * @author Philip Helger
   * @since 9.4.7
   */
  public static class DebugScopeException extends Exception
  {}

  /**
   * @return An exception with the current stack trace or <code>null</code> if
   *         {@link #isDebugWithStackTrace()} is <code>false</code>
   * @see #isDebugWithStackTrace()
   * @since 11.0.5
   */
  @Nullable
  public static Exception getDebugException ()
  {
    return isDebugWithStackTrace () ? new DebugScopeException () : null;
  }
}
