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
package com.helger.commons.supplementary.test.benchmark;

import java.math.BigDecimal;

import com.helger.commons.lang.ReflectionSecurityManager;

/**
 * http://stackoverflow.com/questions/421280/how-do-i-find-the-caller-of-a-method-using-stacktrace-or-reflection?noredirect=1&lq=1
 */
public final class BenchmarkGetCallerClass extends AbstractBenchmarkTask
{
  private BenchmarkGetCallerClass ()
  {}

  public static void main (final String [] aArgs) throws Exception
  {
    logSystemInfo ();
    _run ();
  }

  private static void _run ()
  {
    final int nRuns = 1000;
    final double t1 = benchmarkTask (new ReflectionMethod (nRuns));
    s_aLogger.info ("Time using Reflection:                               " +
                    BigDecimal.valueOf (t1).toString () +
                    " us");

    final double t2 = benchmarkTask (new ThreadStackTraceMethod (nRuns));
    s_aLogger.info ("Time using Thread.currentThread ().getStackTrace (): " +
                    BigDecimal.valueOf (t2).toString () +
                    " us");

    final double t3 = benchmarkTask (new ThrowableStackTraceMethod (nRuns));
    s_aLogger.info ("Time using new Throwable ().getStackTrace ()         " +
                    BigDecimal.valueOf (t3).toString () +
                    " us");

    final double t4 = benchmarkTask (new SecurityManagerMethod (nRuns));
    s_aLogger.info ("Time using mySecurityManager.getCallerClassName      " +
                    BigDecimal.valueOf (t4).toString () +
                    " us");
  }

  /**
   * Abstract class for testing different methods of getting the caller class
   * name
   */
  private static abstract class AbstractDoIt implements Runnable
  {
    private final int m_nRuns;

    public AbstractDoIt (final int runs)
    {
      m_nRuns = runs;
    }

    public abstract String getCallerClassName (int nCallStackDepth);

    public void run ()
    {
      for (int i = 0; i < m_nRuns; ++i)
        getCallerClassName (2);
    }
  }

  /**
   * Uses the internal Reflection class
   */
  private static class ReflectionMethod extends AbstractDoIt
  {
    public ReflectionMethod (final int runs)
    {
      super (runs);
    }

    @SuppressWarnings ({ "deprecation", "restriction" })
    @Override
    public String getCallerClassName (final int nCallStackDepth)
    {
      return sun.reflect.Reflection.getCallerClass (nCallStackDepth).getName ();
    }
  }

  /**
   * Get a stack trace from the current thread
   */
  private static class ThreadStackTraceMethod extends AbstractDoIt
  {
    public ThreadStackTraceMethod (final int runs)
    {
      super (runs);
    }

    @Override
    public String getCallerClassName (final int nCallStackDepth)
    {
      return Thread.currentThread ().getStackTrace ()[nCallStackDepth].getClassName ();
    }
  }

  /**
   * Get a stack trace from a new Throwable
   */
  private static class ThrowableStackTraceMethod extends AbstractDoIt
  {
    public ThrowableStackTraceMethod (final int runs)
    {
      super (runs);
    }

    @Override
    public String getCallerClassName (final int nCallStackDepth)
    {
      return new Throwable ().getStackTrace ()[nCallStackDepth].getClassName ();
    }
  }

  /**
   * Use the SecurityManager.getClassContext()
   */
  static class SecurityManagerMethod extends AbstractDoIt
  {
    public SecurityManagerMethod (final int runs)
    {
      super (runs);
    }

    @Override
    public String getCallerClassName (final int nCallStackDepth)
    {
      return ReflectionSecurityManager.INSTANCE.getCallerClassName (nCallStackDepth);
    }
  }
}
