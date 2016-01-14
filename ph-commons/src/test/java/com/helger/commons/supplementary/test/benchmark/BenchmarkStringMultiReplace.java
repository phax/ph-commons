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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.helger.commons.string.StringHelper;

/**
 * This code benchmarks the performance of copying an array purely in Java
 * versus copying it with System.arraycopy.
 * <p>
 * If available on the executing platform, it may be very useful to perform
 * benchmarks with the server JVM as well as the default client JVM.
 */
public final class BenchmarkStringMultiReplace extends AbstractBenchmarkTask
{
  private BenchmarkStringMultiReplace ()
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
    s_aLogger.info ("Time purely in Java:        " + javaTime + " µs");

    final double systemTime = benchmarkTask (new PatternReplace (nRuns));
    s_aLogger.info ("Time using pattern replace: " + systemTime + " µs");

    final double self1Time = benchmarkTask (new SelfReplaceMultiple1 (nRuns));
    s_aLogger.info ("Time using self replace1:   " + self1Time + " µs");

    final double self2Time = benchmarkTask (new SelfReplaceMultiple2 (nRuns));
    s_aLogger.info ("Time using self replace2:   " + self2Time + " µs");

    final double self3Time = benchmarkTask (new SelfReplaceMultiple3 (nRuns));
    s_aLogger.info ("Time using self replace3:   " + self3Time + " µs");
  }

  private static final String SRC = "This is <<a> text";
  private static final String [] RSRC = new String [] { "&", "<", ">" };
  private static final String [] RDST = new String [] { "&amp;", "&lt;", "&gt;" };
  private static final String DST = "This is &lt;&lt;a&gt; text";

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
      {
        s = SRC;
        for (int j = 0; j < RSRC.length; ++j)
          s = s.replace (RSRC[j], RDST[j]);
      }
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
      final Pattern [] p = new Pattern [RSRC.length];
      for (int i = 0; i < RSRC.length; ++i)
        p[i] = Pattern.compile (RSRC[i], Pattern.LITERAL);

      String s = "";
      for (int i = 0; i < m_nRuns; i++)
      {
        s = SRC;
        for (int j = 0; j < p.length; ++j)
          s = p[j].matcher (s).replaceAll (RDST[j]);
      }
      if (!s.equals (DST))
        throw new IllegalStateException (s);
    }
  }

  private static final class SelfReplaceMultiple1 implements Runnable
  {
    private final int m_nRuns;

    public SelfReplaceMultiple1 (final int runs)
    {
      m_nRuns = runs;
    }

    public void run ()
    {
      final char [] aSrc = new char [RSRC.length];
      for (int i = 0; i < RSRC.length; ++i)
        aSrc[i] = RSRC[i].charAt (0);
      final char [] [] aDst = new char [RDST.length] [];
      for (int i = 0; i < RDST.length; ++i)
        aDst[i] = RDST[i].toCharArray ();

      String s = "";
      for (int i = 0; i < m_nRuns; i++)
        s = new String (StringHelper.replaceMultiple (SRC, aSrc, aDst));
      if (!s.equals (DST))
        throw new IllegalStateException (s);
    }
  }

  private static final class SelfReplaceMultiple2 implements Runnable
  {
    private final int m_nRuns;

    public SelfReplaceMultiple2 (final int runs)
    {
      m_nRuns = runs;
    }

    public void run ()
    {
      final Map <String, String> aMap = new LinkedHashMap <String, String> ();
      for (int i = 0; i < RSRC.length; ++i)
        aMap.put (RSRC[i], RDST[i]);

      String s = "";
      for (int i = 0; i < m_nRuns; i++)
        s = StringHelper.replaceMultiple (SRC, aMap);
      if (!s.equals (DST))
        throw new IllegalStateException (s);
    }
  }

  private static final class SelfReplaceMultiple3 implements Runnable
  {
    private final int m_nRuns;

    public SelfReplaceMultiple3 (final int runs)
    {
      m_nRuns = runs;
    }

    public void run ()
    {
      String s = "";
      for (int i = 0; i < m_nRuns; i++)
        s = StringHelper.replaceMultiple (SRC, RSRC, RDST);
      if (!s.equals (DST))
        throw new IllegalStateException (s);
    }
  }
}
