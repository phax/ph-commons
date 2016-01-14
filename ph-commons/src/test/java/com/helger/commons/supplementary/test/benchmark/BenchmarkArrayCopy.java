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

/**
 * This code benchmarks the performance of copying an array purely in Java
 * versus copying it with System.arraycopy.
 * <p>
 * If available on the executing platform, it may be very useful to perform
 * benchmarks with the server JVM as well as the default client JVM.
 */
public final class BenchmarkArrayCopy extends AbstractBenchmarkTask
{
  private BenchmarkArrayCopy ()
  {}

  public static void main (final String [] aArgs) throws Exception
  {
    logSystemInfo ();
    findWhenSystemBeatsJava ();
    exploreScalingOfSystemArrayCopy ();
  }

  private static void findWhenSystemBeatsJava ()
  {
    for (int i = 1; true; i++)
    {
      final double javaTime = benchmarkTask (new JavaArrayCopy (i));
      s_aLogger.info ("Time to copy an int[" + i + "] purely in Java: " + javaTime + " us");

      final double systemTime = benchmarkTask (new SystemArrayCopy (i));
      s_aLogger.info ("Time to copy an int[" + i + "] using System.arraycopy: " + systemTime + " us");

      if (systemTime < javaTime)
      {
        if (i == 1)
          throw new IllegalStateException (" found that System.arraycopy beats a pure Java copy even for an array of length 1");
        s_aLogger.info ("System.arraycopy first beats a pure Java copy for int arrays of length = " + i);
        break;
      }
    }
    // +++ the above algorithm could be more efficient (e.g. first find an upper
    // bound using doubling or array length, and then use bisection to find
    // exact crossover)
  }

  // 2002/11/10 result: System.arraycopy first beats pure Java array copy for
  // array length around 32, give or take a factor of 2

  /**
   * This method finds how the execution time of
   * {@link System#arraycopy(Object, int, Object, int, int)} scales versus array
   * length. (The execution time for small arrays is dominated by the overhead
   * of making a native call, not array length. For large arrays, this overhead
   * is minimal and the scaling should become linear.)
   */
  private static void exploreScalingOfSystemArrayCopy ()
  {
    for (int i = 1; i < 1024 * 1024; i *= 2)
    {
      final double systemTime = benchmarkTask (new SystemArrayCopy (i));
      s_aLogger.info ("Time to copy an int[" + i + "] using System.arraycopy: " + systemTime + " us");
    }
  }

  // 2002/11/10 result: linear behavior starts for array lengths around 512,
  // give or take a factor of 2

  private static final class JavaArrayCopy implements Runnable
  {
    private final int [] m_aSource;
    private final int [] m_aTarget;

    JavaArrayCopy (final int nArrayLength)
    {
      m_aSource = new int [nArrayLength];
      m_aTarget = new int [nArrayLength];
    }

    public void run ()
    {
      for (int i = 0; i < m_aTarget.length; i++)
      {
        m_aTarget[i] = m_aSource[i];
      }
    }
  }

  private static final class SystemArrayCopy implements Runnable
  {
    private final int [] m_aSource;
    private final int [] m_aTarget;

    SystemArrayCopy (final int nArrayLength)
    {
      m_aSource = new int [nArrayLength];
      m_aTarget = new int [nArrayLength];
    }

    public void run ()
    {
      System.arraycopy (m_aSource, 0, m_aTarget, 0, m_aTarget.length);
    }
  }
}
