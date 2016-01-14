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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.helger.commons.charset.CCharset;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.locale.LocaleFormatter;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.util.LevenshteinDistance;

/**
 * Check different levenshtein impolementations.<br>
 *
 * <pre>
 * [   1] Info    12.08.2010 12:58:19 [AbstractBenchmarkTask] Runtime: Date=Thu Aug 12 12:58:19 CEST 2010; Java=1.6.0_21; OS=Windows Vista [6.0]; User=philip; Procs=4
 * [   2] Info    12.08.2010 12:58:19 [AbstractBenchmarkTask] Comparing 100 strings from word list; The longest string is 17 chars!
 * [   3] Info    12.08.2010 12:58:24 [AbstractBenchmarkTask] *LevDist0: 3,780,023.369 ns
 * [   4] Info    12.08.2010 12:58:32 [AbstractBenchmarkTask] LevDist1a: 4,850,148.202 ns
 * [   5] Info    12.08.2010 12:58:39 [AbstractBenchmarkTask] LevDist1b: 4,791,247.561 ns
 * [   6] Info    12.08.2010 12:58:43 [AbstractBenchmarkTask] LevDist2a: 12,323,263.15 ns
 * [   7] Info    12.08.2010 12:58:46 [AbstractBenchmarkTask] LevDist2b: 10,900,994.4 ns
 *
 * [   1] Info    12.08.2010 12:59:43 [AbstractBenchmarkTask] Runtime: Date=Thu Aug 12 12:59:43 CEST 2010; Java=1.6.0_21; OS=Windows Vista [6.0]; User=philip; Procs=4
 * [   2] Info    12.08.2010 12:59:43 [AbstractBenchmarkTask] Comparing 100 strings from word list; The longest string is 1024 chars!
 * [   3] Info    12.08.2010 12:59:48 [AbstractBenchmarkTask] *LevDist0: 18,640,355.13 ns
 * [   4] Info    12.08.2010 12:59:55 [AbstractBenchmarkTask] LevDist1a: 22,973,917.075 ns
 * [   5] Info    12.08.2010 13:00:01 [AbstractBenchmarkTask] LevDist1b: 19,392,826.465 ns
 * [   6] Info    12.08.2010 13:00:14 [AbstractBenchmarkTask] LevDist2a: 41,738,856.475 ns
 * [   7] Info    12.08.2010 13:00:24 [AbstractBenchmarkTask] LevDist2b: 32,816,656.865 ns
 * </pre>
 */
public final class BenchmarkLevenshteinDistance extends AbstractBenchmarkTask
{
  private BenchmarkLevenshteinDistance ()
  {}

  public static void main (final String [] aArgs) throws Exception
  {
    logSystemInfo ();
    findWhetherSynchronizedOrLockAreFaster ();
  }

  private static List <String> _readWordList (final IReadableResource aRes, final Charset aCharset) throws IOException
  {
    final List <String> ret = new ArrayList <String> ();
    final BufferedReader aBR = new BufferedReader (new InputStreamReader (aRes.getInputStream (), aCharset));
    String sLine;
    int nIdx = 0;
    while ((sLine = aBR.readLine ()) != null)
    {
      if ((nIdx++ % 3) == 0)
      {
        ret.add (sLine);
        if (ret.size () >= 100)
          break;
      }
    }
    StreamHelper.close (aBR);
    return ret;
  }

  private static int _getMaxStringLength (final List <String> l)
  {
    int m = 0;
    for (final String s : l)
      m = Math.max (m, s.length ());
    return m;
  }

  private static final int LV_COST_INSERT = 2;
  private static final int LV_COST_DELETE = 2;
  private static final int LV_COST_SUBSTITUTE = 2;

  private static void findWhetherSynchronizedOrLockAreFaster () throws IOException
  {
    final List <String> aStrings = _readWordList (new ClassPathResource ("wordlist/english-words.95"),
                                                  CCharset.CHARSET_ISO_8859_1_OBJ);
    if (true)
    {
      aStrings.remove (0);
      aStrings.remove (0);
      // 309 chars
      aStrings.add ("Very long string!! Very long string!! Very long string!! Very long string!! Very long string!! Very long string!! Very long string!! Very long string!! Very long string!! Very long string!! Very long string!! Very long string!! Very long string!! Very long string!! Very long string!! Very long string!! Very ");
      // 1024 chars
      aStrings.add (StringHelper.getRepeated ("a", 1024));
    }
    final String [] aStringArray = aStrings.toArray (new String [0]);
    s_aLogger.info ("Comparing " +
                    aStrings.size () +
                    " strings from word list; The longest string is " +
                    _getMaxStringLength (aStrings) +
                    " chars!");

    final LevDist0StringHelper aL0 = new LevDist0StringHelper (aStringArray);
    final LevDistBase aL1a = new LevDist1a (aStringArray);
    final LevDistBase aL1b = new LevDist1b (aStringArray);
    final LevDistBase aL2a = new LevDist2a (aStringArray);
    final LevDistBase aL2b = new LevDist2b (aStringArray);

    // Check algo once for all texts!
    for (int i = 0; i < aStringArray.length - 1; ++i)
    {
      final String s1 = aStringArray[i];
      for (int j = i + 1; j < aStringArray.length; ++j)
      {
        final String s2 = aStringArray[j];
        final int n0 = LevenshteinDistance.getDistance (s1, s2, LV_COST_INSERT, LV_COST_DELETE, LV_COST_SUBSTITUTE);
        final int n1a = aL1a.levDist (s1, s2);
        final int n1b = aL1b.levDist (s1, s2);
        final int n2a = aL2a.levDist (s1, s2);
        final int n2b = aL2b.levDist (s1, s2);
        if (!(n0 == n1a && n1a == n1b && n1b == n2a && n2a == n2b))
          throw new IllegalArgumentException ("Implementation differences!");
      }
    }
    s_aLogger.info ("Initial check done!");

    double dTime = benchmarkTask (aL0);
    s_aLogger.info ("*LevDist0: " + LocaleFormatter.getFormatted (dTime, Locale.ENGLISH) + " ns");

    dTime = benchmarkTask (aL1a);
    s_aLogger.info ("LevDist1a: " + LocaleFormatter.getFormatted (dTime, Locale.ENGLISH) + " ns");

    dTime = benchmarkTask (aL1b);
    s_aLogger.info ("LevDist1b: " + LocaleFormatter.getFormatted (dTime, Locale.ENGLISH) + " ns");

    dTime = benchmarkTask (aL2a);
    s_aLogger.info ("LevDist2a: " + LocaleFormatter.getFormatted (dTime, Locale.ENGLISH) + " ns");

    dTime = benchmarkTask (aL2b);
    s_aLogger.info ("LevDist2b: " + LocaleFormatter.getFormatted (dTime, Locale.ENGLISH) + " ns");
  }

  private static final class LevDist0StringHelper implements Runnable
  {
    private final char [] [] m_aStrings;
    private final boolean m_bSimple;

    public LevDist0StringHelper (final String [] aStrings)
    {
      // Convert String[] to char[][] ;-)
      m_aStrings = new char [aStrings.length] [];
      for (int i = 0; i < aStrings.length; ++i)
        m_aStrings[i] = aStrings[i].toCharArray ();
      m_bSimple = (LV_COST_INSERT == 1 && LV_COST_DELETE == 1 && LV_COST_SUBSTITUTE == 1);
    }

    public void run ()
    {
      final int n = m_aStrings.length;
      for (int i = 0; i < n - 1; ++i)
      {
        final char [] s1 = m_aStrings[i];
        for (int j = i + 1; j < n; j++)
          if (m_bSimple)
            LevenshteinDistance.getDistance (s1, m_aStrings[j]);
          else
            LevenshteinDistance.getDistance (s1, m_aStrings[j], LV_COST_INSERT, LV_COST_DELETE, LV_COST_SUBSTITUTE);
      }
    }
  }

  private abstract static class LevDistBase implements Runnable
  {
    private final String [] m_aStrings;

    public LevDistBase (final String [] aStrings)
    {
      m_aStrings = aStrings;
    }

    public abstract int levDist (String s1, String s2);

    public final void run ()
    {
      if (false)
        s_aLogger.info ("run " + getClass ());
      final int n = m_aStrings.length;
      for (int i = 0; i < n - 1; ++i)
      {
        final String s1 = m_aStrings[i];
        for (int j = i + 1; j < n; j++)
          levDist (s1, m_aStrings[j]);
      }
    }
  }

  private static final class LevDist1a extends LevDistBase
  {
    public LevDist1a (final String [] aStrings)
    {
      super (aStrings);
    }

    @Override
    public int levDist (final String sStr1, final String sStr2)
    {
      final int nLen1 = sStr1.length ();
      final int nLen2 = sStr2.length ();

      if (nLen1 == 0)
        return nLen2;
      if (nLen2 == 0)
        return nLen1;

      // 'previous' cost array, horizontally
      int [] aPrevRow = new int [nLen1 + 1];

      // cost array, horizontally
      int [] aCurRow = new int [nLen1 + 1];

      for (int i = 0; i <= nLen1; i++)
        aPrevRow[i] = i * LV_COST_INSERT;

      for (int j = 1; j <= nLen2; j++)
      {
        final char ch2 = sStr2.charAt (j - 1);
        aCurRow[0] = j * LV_COST_DELETE;

        for (int i = 1; i <= nLen1; i++)
        {
          final int cost = sStr1.charAt (i - 1) == ch2 ? 0 : LV_COST_SUBSTITUTE;
          // minimum of cell to the left+1, to the top+1, diagonally left and up
          // +cost
          aCurRow[i] = Math.min (Math.min (aCurRow[i - 1] + LV_COST_INSERT, aPrevRow[i] + LV_COST_DELETE),
                                 aPrevRow[i - 1] + cost);
        }

        // copy current distance counts to 'previous row' distance counts
        final int [] tmp = aPrevRow;
        aPrevRow = aCurRow;
        aCurRow = tmp;
      }

      // our last action in the above loop was to switch d and p, so p now
      // actually has the most recent cost counts
      return aPrevRow[nLen1];
    }
  }

  private static final class LevDist1b extends LevDistBase
  {
    public LevDist1b (final String [] aStrings)
    {
      super (aStrings);
    }

    @Override
    public int levDist (final String sStr1, final String sStr2)
    {
      final int nLen1 = sStr1.length ();
      final int nLen2 = sStr2.length ();

      if (nLen1 == 0)
        return nLen2;
      if (nLen2 == 0)
        return nLen1;

      final char [] chs1 = sStr1.toCharArray ();
      final char [] chs2 = sStr2.toCharArray ();

      // 'previous' cost array, horizontally
      int [] aPrevRow = new int [nLen1 + 1];
      for (int i = 0; i <= nLen1; i++)
        aPrevRow[i] = i * LV_COST_INSERT;

      // cost array, horizontally
      int [] aCurRow = new int [nLen1 + 1];

      for (int j = 0; j < nLen2; j++)
      {
        final int ch2 = chs2[j];
        aCurRow[0] = (j + 1) * LV_COST_DELETE;

        for (int i = 0; i < nLen1; i++)
        {
          final int cost = chs1[i] == ch2 ? 0 : LV_COST_SUBSTITUTE;
          // minimum of cell to the left+1, to the top+1, diagonally left and up
          // +cost
          aCurRow[i + 1] = Math.min (Math.min (aCurRow[i] + LV_COST_INSERT, aPrevRow[i + 1] + LV_COST_DELETE),
                                     aPrevRow[i] + cost);
        }

        // swap current distance counts to 'previous row' distance counts
        final int [] tmp = aPrevRow;
        aPrevRow = aCurRow;
        aCurRow = tmp;
      }

      // our last action in the above loop was to switch d and p, so p now
      // actually has the most recent cost counts
      return aPrevRow[nLen1];
    }
  }

  private static final class LevDist2a extends LevDistBase
  {
    public LevDist2a (final String [] aStrings)
    {
      super (aStrings);
    }

    @Override
    public int levDist (final String sStr1, final String sStr2)
    {
      final int nLen1 = sStr1.length ();
      final int nLen2 = sStr2.length ();

      if (nLen1 == 0)
        return nLen2;
      if (nLen2 == 0)
        return nLen1;

      final int [] [] T = new int [nLen1 + 1] [nLen2 + 1];

      T[0][0] = 0;
      for (int j = 0; j < nLen2; j++)
        T[0][j + 1] = T[0][j] + LV_COST_INSERT;

      for (int i = 0; i < nLen1; i++)
      {
        T[i + 1][0] = T[i][0] + LV_COST_DELETE;
        for (int j = 0; j < nLen2; j++)
        {
          final int cost = sStr1.charAt (i) == sStr2.charAt (j) ? 0 : LV_COST_SUBSTITUTE;
          T[i + 1][j + 1] = Math.min (Math.min (T[i][j] + cost, T[i][j + 1] + LV_COST_DELETE),
                                      T[i + 1][j] + LV_COST_INSERT);
        }
      }

      return T[nLen1][nLen2];
    }
  }

  private static final class LevDist2b extends LevDistBase
  {
    public LevDist2b (final String [] aStrings)
    {
      super (aStrings);
    }

    @Override
    public int levDist (final String sStr1, final String sStr2)
    {
      final int nLen1 = sStr1.length ();
      final int nLen2 = sStr2.length ();

      if (nLen1 == 0)
        return nLen2;
      if (nLen2 == 0)
        return nLen1;

      final char [] chs1 = sStr1.toCharArray ();
      final char [] chs2 = sStr2.toCharArray ();

      final int [] [] T = new int [nLen1 + 1] [nLen2 + 1];

      T[0][0] = 0;
      for (int j = 0; j < nLen2; j++)
        T[0][j + 1] = T[0][j] + LV_COST_INSERT;

      for (int i = 0; i < nLen1; i++)
      {
        T[i + 1][0] = T[i][0] + LV_COST_DELETE;
        final int ch1 = chs1[i];
        for (int j = 0; j < nLen2; j++)
        {
          final int cost = ch1 == chs2[j] ? 0 : LV_COST_SUBSTITUTE;
          T[i + 1][j + 1] = Math.min (Math.min (T[i][j] + cost, T[i][j + 1] + LV_COST_DELETE),
                                      T[i + 1][j] + LV_COST_INSERT);
        }
      }

      return T[nLen1][nLen2];
    }
  }
}
