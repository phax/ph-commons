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

import org.slf4j.LoggerFactory;

import com.helger.commons.random.VerySecureRandom;

/**
 * This code benchmarks the performance of copying an array purely in Java
 * versus copying it with System.arraycopy.
 * <p>
 * If available on the executing platform, it may be very useful to perform
 * benchmarks with the server JVM as well as the default client JVM.
 */
public final class BenchmarkBigDecimalEquals extends AbstractBenchmarkTask
{
  private BenchmarkBigDecimalEquals ()
  {}

  public static void main (final String [] aArgs) throws Exception
  {
    logSystemInfo ();
    findWhenSystemBeatsJava ();
  }

  private static void findWhenSystemBeatsJava ()
  {
    final BigDecimal [] aNums = new BigDecimal [1000];
    for (int i = 0; i < aNums.length; ++i)
      aNums[i] = BigDecimal.valueOf (VerySecureRandom.getInstance ().nextDouble () *
                                     (50 + VerySecureRandom.getInstance ().nextDouble ()));
    s_aLogger.info ("Starting");

    final double dTime1 = benchmarkTask (new BigDecimalCompare (aNums));
    s_aLogger.info ("Time compare:  " + BigDecimal.valueOf (dTime1).toString () + " us");

    final double dTime2 = benchmarkTask (new BigDecimalSetScale (aNums));
    s_aLogger.info ("Time setScale: " + BigDecimal.valueOf (dTime2).toString () + " us");

    s_aLogger.info ("Time1 is " + BigDecimal.valueOf (dTime1 / dTime2 * 100).toString () + "% of time2");
  }

  private static final class BigDecimalCompare implements Runnable
  {
    private final BigDecimal [] m_aNums;

    public BigDecimalCompare (final BigDecimal [] aNums)
    {
      m_aNums = aNums;
    }

    public void run ()
    {
      boolean b = true;
      for (int i = 0; i < m_aNums.length - 1; i++)
        for (int j = i; j < m_aNums.length; j++)
        {
          final BigDecimal aBD1 = m_aNums[i];
          final BigDecimal aBD2 = m_aNums[j];
          b = aBD1.compareTo (aBD2) == 0;
        }
      if (b)
        LoggerFactory.getLogger ("test").info ("Equals: " + b);
    }
  }

  private static final class BigDecimalSetScale implements Runnable
  {
    private final BigDecimal [] m_aNums;

    public BigDecimalSetScale (final BigDecimal [] aNums)
    {
      m_aNums = aNums;
    }

    public void run ()
    {
      boolean b = true;
      for (int i = 0; i < m_aNums.length - 1; i++)
        for (int j = i; j < m_aNums.length; j++)
        {
          final BigDecimal aBD1 = m_aNums[i];
          final BigDecimal aBD2 = m_aNums[j];
          final int nMaxScale = Math.max (aBD1.scale (), aBD2.scale ());
          b = aBD1.setScale (nMaxScale).equals (aBD2.setScale (nMaxScale));
        }
      if (b)
        LoggerFactory.getLogger ("test").info ("Equals: " + b);
    }
  }
}
