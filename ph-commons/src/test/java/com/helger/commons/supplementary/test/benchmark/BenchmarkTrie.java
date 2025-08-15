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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import com.helger.base.io.stream.StreamHelper;
import com.helger.base.string.StringHelper;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.CommonsTreeMap;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsMap;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.locale.LocaleFormatter;
import com.helger.commons.supplementary.test.code.StringTrieFuncTest;

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

  private static ICommonsList <String> _readWordList (final IReadableResource aRes, final Charset aCharset)
                                                                                                            throws IOException
  {
    final ICommonsList <String> ret = new CommonsArrayList <> ();
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

  private static int _getMaxStringLength (final Iterable <String> l)
  {
    int m = 0;
    for (final String s : l)
      m = Math.max (m, s.length ());
    return m;
  }

  private static void execute () throws IOException
  {
    final ICommonsList <String> aStrings = _readWordList (new ClassPathResource ("wordlist/english-words.95"),
                                                          StandardCharsets.ISO_8859_1);
    if (true)
    {
      // 309 chars
      aStrings.add ("Very long string!! Very long string!! Very long string!! Very long string!! Very long string!! Very long string!! Very long string!! Very long string!! Very long string!! Very long string!! Very long string!! Very long string!! Very long string!! Very long string!! Very long string!! Very long string!! Very ");
      // 1024 chars
      aStrings.add (StringHelper.getRepeated ("a", 1024));
    }
    final String [] aStringArray = aStrings.toArray (new String [aStrings.size ()]);
    LOGGER.info ("Comparing " +
                 aStrings.size () +
                 " strings from word list; The longest string is " +
                 _getMaxStringLength (aStrings) +
                 " chars!");

    final AbstractStringMapBase aL1a = new StringMapHashMap (aStringArray);
    final AbstractStringMapBase aL1b = new StringMapTreeMap (aStringArray);
    final AbstractStringMapBase aL2a = new StringMapTST (aStringArray);
    LOGGER.info ("Initial check done!");

    double dTime = benchmarkTask (aL2a);
    LOGGER.info ("StringMapTST: " + LocaleFormatter.getFormatted (dTime, Locale.ENGLISH) + " ns");
    LOGGER.info (aL2a.size () + " entries");

    dTime = benchmarkTask (aL1a);
    LOGGER.info ("StringMapHashMap: " + LocaleFormatter.getFormatted (dTime, Locale.ENGLISH) + " ns");
    LOGGER.info (aL1a.size () + " entries");

    dTime = benchmarkTask (aL1b);
    LOGGER.info ("StringMapTreeMap: " + LocaleFormatter.getFormatted (dTime, Locale.ENGLISH) + " ns");
    LOGGER.info (aL1b.size () + " entries");
  }

  private abstract static class AbstractStringMapBase implements Runnable
  {
    private final String [] m_aStrings;

    protected AbstractStringMapBase (final String [] aStrings)
    {
      m_aStrings = aStrings;
    }

    public abstract int size ();

    public abstract void add (String sKey, String sValue);

    public abstract String get (String sKey);

    public final void run ()
    {
      if (false)
        LOGGER.info ("run " + getClass ());
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

  private static final class StringMapHashMap extends AbstractStringMapBase
  {
    private final ICommonsMap <String, String> m_aMap;

    public StringMapHashMap (final String [] aStrings)
    {
      super (aStrings);
      m_aMap = new CommonsHashMap <> ();
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

  private static final class StringMapTreeMap extends AbstractStringMapBase
  {
    private final ICommonsMap <String, String> m_aMap;

    public StringMapTreeMap (final String [] aStrings)
    {
      super (aStrings);
      m_aMap = new CommonsTreeMap <> ();
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

  private static final class StringMapTST extends AbstractStringMapBase
  {
    private final StringTrieFuncTest <String> m_aMap;

    public StringMapTST (final String [] aStrings)
    {
      super (aStrings);
      m_aMap = new StringTrieFuncTest <> ();
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
