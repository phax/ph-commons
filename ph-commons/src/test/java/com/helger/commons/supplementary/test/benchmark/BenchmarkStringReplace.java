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

import java.util.regex.Pattern;

import com.helger.commons.string.StringHelper;

/**
 * This code benchmarks the performance of copying an array purely in Java
 * versus copying it with System.arraycopy.
 * <p>
 * If available on the executing platform, it may be very useful to perform
 * benchmarks with the server JVM as well as the default client JVM.
 */
public final class BenchmarkStringReplace extends AbstractBenchmarkTask
{
  private BenchmarkStringReplace ()
  {}

  public static void main (final String [] aArgs) throws Exception
  {
    logSystemInfo ();
    findWhenSystemBeatsJava ();
  }

  private static void findWhenSystemBeatsJava ()
  {
    final int nRuns = 1000;
    final double javaTime = benchmarkTask (new StringReplace (nRuns));
    s_aLogger.info ("Time purely in Java:        " + javaTime + " us");

    final double systemTime = benchmarkTask (new PatternReplace (nRuns));
    s_aLogger.info ("Time using pattern replace: " + systemTime + " us");

    final double selfTime = benchmarkTask (new SelfReplace (nRuns));
    s_aLogger.info ("Time using self replace:    " + selfTime + " us");
  }

  private static final String SRC = "This is <<a> text";
  private static final String RSRC = "<";
  private static final String RDST = "&lt;";
  private static final String DST = "This is &lt;&lt;a> text";

  private static final class StringReplace implements Runnable
  {
    private final int m_nRuns;

    public StringReplace (final int runs)
    {
      m_nRuns = runs;
    }

    public void run ()
    {
      String s = "";
      for (int i = 0; i < m_nRuns; i++)
        s = SRC.replace (RSRC, RDST);
      if (!s.equals (DST))
        throw new IllegalStateException (s);
    }
  }

  private static final class PatternReplace implements Runnable
  {
    private final int m_nRuns;

    public PatternReplace (final int runs)
    {
      m_nRuns = runs;
    }

    public void run ()
    {
      String s = "";
      final Pattern p = Pattern.compile (RSRC, Pattern.LITERAL);
      for (int i = 0; i < m_nRuns; i++)
        s = p.matcher (SRC).replaceAll (RDST);
      if (!s.equals (DST))
        throw new IllegalStateException (s);
    }
  }

  private static final class SelfReplace implements Runnable
  {
    private final int m_nRuns;

    public SelfReplace (final int runs)
    {
      m_nRuns = runs;
    }

    public void run ()
    {
      String s = "";
      for (int i = 0; i < m_nRuns; i++)
        s = StringHelper.replaceAll (SRC, RSRC, RDST);
      if (!s.equals (DST))
        throw new IllegalStateException (s);
    }
  }
}
