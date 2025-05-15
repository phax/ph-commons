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
package com.helger.commons.supplementary.test.benchmark;

import java.math.BigDecimal;

/**
 * http://stackoverflow.com/questions/421280/how-do-i-find-the-caller-of-a-method-using-stacktrace-or-reflection?noredirect=1&lq=1
 * Output:
 *
 * <pre>
Runtime: Date=2016-07-18T11:37:34.278; Java=1.8.0_66; OS=Windows 7 [6.1]; User=helger; Procs=4
Time using Reflection:                                102814.426484375 us
Time using Thread.currentThread ().getStackTrace (): 5930035.365 us
Time using new Throwable ().getStackTrace ()         5367559.035 us
Time using mySecurityManager.getCallerClassName       431699.17125 us
 * </pre>
 */
public final class BenchmarkGetCallerClass extends AbstractBenchmarkTask
{
  private BenchmarkGetCallerClass ()
  {}

  public static void main (final String [] aArgs)
  {
    logSystemInfo ();
    _run ();
  }

  private static void _run ()
  {
    final int nRuns = 1000;

    // Uncomment this and the class below to use it
    // final double t1 = benchmarkTask (new ReflectionMethod (nRuns));
    // LOGGER.info ("Time using Reflection: " +
    // BigDecimal.valueOf (t1).toString () +
    // " us");

    final double t2 = benchmarkTask (new ThreadStackTraceMethod (nRuns));
    LOGGER.info ("Time using Thread.currentThread ().getStackTrace (): " + BigDecimal.valueOf (t2).toString () + " us");

    final double t3 = benchmarkTask (new ThrowableStackTraceMethod (nRuns));
    LOGGER.info ("Time using new Throwable ().getStackTrace ()         " + BigDecimal.valueOf (t3).toString () + " us");

    final double t4 = benchmarkTask (new StackWalkerMethod (nRuns));
    LOGGER.info ("Time using StackWalker.walk                          " + BigDecimal.valueOf (t4).toString () + " us");
  }

  /**
   * Abstract class for testing different methods of getting the caller class name
   */
  private abstract static class AbstractDoIt implements Runnable
  {
    private final int m_nRuns;
    private boolean m_bShow = false;

    protected AbstractDoIt (final int runs)
    {
      m_nRuns = runs;
    }

    public abstract String getCallerClassName (int nCallStackDepth);

    public void run ()
    {
      for (int i = 0; i < m_nRuns; ++i)
      {
        final String s = getCallerClassName (2);
        if (!m_bShow)
        {
          LOGGER.info (" ==> " + s);
          m_bShow = true;
        }
      }
    }
  }

  /**
   * Uses the internal Reflection class.<br>
   * Uncomment to use it.
   */
  // private static class ReflectionMethod extends AbstractDoIt
  // {
  // public ReflectionMethod (final int runs)
  // {
  // super (runs);
  // }
  //
  // @Override
  // public String getCallerClassName (final int nCallStackDepth)
  // {
  // return sun.reflect.Reflection.getCallerClass (nCallStackDepth).getName ();
  // }
  // }

  /**
   * Get a stack trace from the current thread
   */
  private static class ThreadStackTraceMethod extends AbstractDoIt
  {
    public ThreadStackTraceMethod (final int runs)
    {
      super (runs);
    }

    // Returns: com.helger.commons.supplementary.test.benchmark.AbstractBenchmarkTask
    @Override
    public String getCallerClassName (final int nCallStackDepth)
    {
      return Thread.currentThread ().getStackTrace ()[nCallStackDepth + 1].getClassName ();
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

    // Returns: com.helger.commons.supplementary.test.benchmark.AbstractBenchmarkTask
    @Override
    public String getCallerClassName (final int nCallStackDepth)
    {
      return new Throwable ().getStackTrace ()[nCallStackDepth].getClassName ();
    }
  }

  /**
   * Use the StackWalker.walk() <br>
   * This is the fastest version
   */
  static class StackWalkerMethod extends AbstractDoIt
  {
    public StackWalkerMethod (final int runs)
    {
      super (runs);
    }

    // Returns: com.helger.commons.supplementary.test.benchmark.AbstractBenchmarkTask
    @Override
    public String getCallerClassName (final int nCallStackDepth)
    {
      final StackWalker aWalker = StackWalker.getInstance (StackWalker.Option.RETAIN_CLASS_REFERENCE);
      return aWalker.walk (s -> s.map (x -> x.getDeclaringClass ().getName ())
                                 .skip (nCallStackDepth)
                                 .findFirst ()
                                 .orElse (null));
    }
  }
}
