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
package com.helger.commons.supplementary.test.benchmark;

import java.math.BigDecimal;
import java.util.Random;

import com.helger.commons.random.RandomHelper;
import com.helger.commons.string.StringHelper;

/**
 * This code benchmarks the performance of copying an array purely in Java
 * versus copying it with System.arraycopy.
 * <p>
 * If available on the executing platform, it may be very useful to perform
 * benchmarks with the server JVM as well as the default client JVM.
 */
public final class BenchmarkStringCodePoints extends AbstractBenchmarkTask
{
  private static final String s_sText;

  static
  {
    final StringBuilder aSB = new StringBuilder ();
    final Random r = RandomHelper.getRandom ();
    for (int i = 0; i < 10_000; ++i)
      aSB.append ((char) r.nextInt (Character.MAX_VALUE));
    s_sText = aSB.toString ();
  }

  private BenchmarkStringCodePoints ()
  {}

  public static void main (final String [] aArgs) throws Exception
  {
    logSystemInfo ();
    _run ();
  }

  private static void _run ()
  {
    final int nRuns = 1000;
    final double t1 = benchmarkTask (new StringHelperIterate (nRuns));
    s_aLogger.info ("Time using StringHelper:                " + BigDecimal.valueOf (t1).toString () + " us");

    final double t2 = benchmarkTask (new CodePointsForEachOrdered (nRuns));
    s_aLogger.info ("Time using codePoints().forEachOrdered: " + BigDecimal.valueOf (t2).toString () + " us");

    final double t3 = benchmarkTask (new CodePointsCount (nRuns));
    s_aLogger.info ("Time using codePoints().count:          " + BigDecimal.valueOf (t3).toString () + " us");
  }

  private static final class StringHelperIterate implements Runnable
  {
    private final int m_nRuns;

    public StringHelperIterate (final int runs)
    {
      m_nRuns = runs;
    }

    public void run ()
    {
      for (int i = 0; i < m_nRuns; i++)
        StringHelper.iterateCodePoints (s_sText, c -> {});
    }
  }

  private static final class CodePointsForEachOrdered implements Runnable
  {
    private final int m_nRuns;

    public CodePointsForEachOrdered (final int runs)
    {
      m_nRuns = runs;
    }

    public void run ()
    {
      for (int i = 0; i < m_nRuns; i++)
        s_sText.codePoints ().forEachOrdered (c -> {});
    }
  }

  private static final class CodePointsCount implements Runnable
  {
    private final int m_nRuns;

    public CodePointsCount (final int runs)
    {
      m_nRuns = runs;
    }

    public void run ()
    {
      for (int i = 0; i < m_nRuns; i++)
        s_sText.codePoints ().count ();
    }
  }
}
