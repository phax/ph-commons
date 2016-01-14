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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import com.helger.commons.charset.CCharset;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.locale.LocaleFormatter;
import com.helger.commons.string.StringHelper;

/**
 * Check different Maps with String keys.<br>
 *
 * <pre>
 * </pre>
 */
public final class BenchmarkTrie extends AbstractBenchmarkTask
{
  private BenchmarkTrie ()
  {}

  public static void main (final String [] aArgs) throws Exception
  {
    logSystemInfo ();
    execute ();
  }

  private static List <String> _readWordList (final IReadableResource aRes, final Charset aCharset) throws IOException
  {
    final List <String> ret = new ArrayList <String> ();
    final BufferedReader aBR = new BufferedReader (new InputStreamReader (aRes.getInputStream (), aCharset));
    String sLine;
    while ((sLine = aBR.readLine ()) != null)
    {
      ret.add (sLine);
      if (ret.size () > 999)
        break;
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

  private static void execute () throws IOException
  {
    final List <String> aStrings = _readWordList (new ClassPathResource ("wordlist/english-words.95"),
                                                  CCharset.CHARSET_ISO_8859_1_OBJ);
    if (true)
    {
      // 309 chars
      aStrings.add ("Very long string!! Very long string!! Very long string!! Very long string!! Very long string!! Very long string!! Very long string!! Very long string!! Very long string!! Very long string!! Very long string!! Very long string!! Very long string!! Very long string!! Very long string!! Very long string!! Very ");
      // 1024 chars
      aStrings.add (StringHelper.getRepeated ("a", 1024));
    }
    final String [] aStringArray = aStrings.toArray (new String [aStrings.size ()]);
    s_aLogger.info ("Comparing " +
                    aStrings.size () +
                    " strings from word list; The longest string is " +
                    _getMaxStringLength (aStrings) +
                    " chars!");

    final StringMapBase aL1a = new StringMapHashMap (aStringArray);
    final StringMapBase aL1b = new StringMapTreeMap (aStringArray);
    final StringMapBase aL2a = new StringMapTST (aStringArray);
    s_aLogger.info ("Initial check done!");

    double dTime;

    dTime = benchmarkTask (aL2a);
    s_aLogger.info ("StringMapTST: " + LocaleFormatter.getFormatted (dTime, Locale.ENGLISH) + " ns");
    s_aLogger.info (aL2a.size () + " entries");

    dTime = benchmarkTask (aL1a);
    s_aLogger.info ("StringMapHashMap: " + LocaleFormatter.getFormatted (dTime, Locale.ENGLISH) + " ns");
    s_aLogger.info (aL1a.size () + " entries");

    dTime = benchmarkTask (aL1b);
    s_aLogger.info ("StringMapTreeMap: " + LocaleFormatter.getFormatted (dTime, Locale.ENGLISH) + " ns");
    s_aLogger.info (aL1b.size () + " entries");
  }

  private abstract static class StringMapBase implements Runnable
  {
    private final String [] m_aStrings;

    public StringMapBase (final String [] aStrings)
    {
      m_aStrings = aStrings;
    }

    public abstract int size ();

    public abstract void add (String sKey, String sValue);

    public abstract String get (String sKey);

    public final void run ()
    {
      if (false)
        s_aLogger.info ("run " + getClass ());
      final int n = m_aStrings.length;
      for (int i = 0; i < n; ++i)
      {
        final String s1 = m_aStrings[i];
        add (s1, s1);
      }
      for (int i = 0; i < n; ++i)
      {
        final String s1 = m_aStrings[i];
        if (!s1.equals (get (s1)))
          throw new IllegalStateException ("Not found: " + s1);
      }
    }
  }

  private static final class StringMapHashMap extends StringMapBase
  {
    private final Map <String, String> m_aMap;

    public StringMapHashMap (final String [] aStrings)
    {
      super (aStrings);
      m_aMap = new HashMap <String, String> ();
    }

    @Override
    public int size ()
    {
      return m_aMap.size ();
    }

    @Override
    public void add (final String sKey, final String sValue)
    {
      m_aMap.put (sKey, sValue);
    }

    @Override
    public String get (final String sKey)
    {
      return m_aMap.get (sKey);
    }
  }

  private static final class StringMapTreeMap extends StringMapBase
  {
    private final Map <String, String> m_aMap;

    public StringMapTreeMap (final String [] aStrings)
    {
      super (aStrings);
      m_aMap = new TreeMap <String, String> ();
    }

    @Override
    public int size ()
    {
      return m_aMap.size ();
    }

    @Override
    public void add (final String sKey, final String sValue)
    {
      m_aMap.put (sKey, sValue);
    }

    @Override
    public String get (final String sKey)
    {
      return m_aMap.get (sKey);
    }
  }

  private static final class StringMapTST extends StringMapBase
  {
    private final StringTrieFuncTest <String> m_aMap;

    public StringMapTST (final String [] aStrings)
    {
      super (aStrings);
      m_aMap = new StringTrieFuncTest <String> ();
    }

    @Override
    public int size ()
    {
      return m_aMap.size ();
    }

    @Override
    public void add (final String sKey, final String sValue)
    {
      m_aMap.put (sKey, sValue);
    }

    @Override
    public String get (final String sKey)
    {
      return m_aMap.get (sKey);
    }
  }
}
