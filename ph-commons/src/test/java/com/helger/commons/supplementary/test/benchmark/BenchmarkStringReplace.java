/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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

import java.util.Locale;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import com.helger.base.string.StringReplace;

/**
 * This code benchmarks {@link String#replace(CharSequence, CharSequence)} and
 * {@link String#replace(char, char)} against a literal {@link Pattern} and against the own
 * implementations in {@link StringReplace}, for several input shapes.
 */
public final class BenchmarkStringReplace extends AbstractBenchmarkTask
{
  private static final int RUNS = 1000;

  private BenchmarkStringReplace ()
  {}

  private static final class Task implements Runnable
  {
    private final Supplier <String> m_aSupplier;
    private final String m_sExpected;

    public Task (final Supplier <String> aSupplier, final String sExpected)
    {
      m_aSupplier = aSupplier;
      m_sExpected = sExpected;
    }

    public void run ()
    {
      String s = "";
      for (int i = 0; i < RUNS; i++)
        s = m_aSupplier.get ();
      if (!s.equals (m_sExpected))
        throw new IllegalStateException (s);
    }
  }

  private static void _log (final String sName, final double dJDK, final double dSelf)
  {
    // benchmarkTask returns the nanoseconds of one task run, which has RUNS calls
    LOGGER.info (sName +
                 ": JDK=" +
                 String.format (Locale.ROOT, "%10.2f", Double.valueOf (dJDK / RUNS)) +
                 " ns; self=" +
                 String.format (Locale.ROOT, "%10.2f", Double.valueOf (dSelf / RUNS)) +
                 " ns; self/JDK=" +
                 String.format (Locale.ROOT, "%.2f", Double.valueOf (dSelf / dJDK)));
  }

  private static void _runString (final String sName, final String sSrc, final String sSearch, final String sReplace)
  {
    final String sExpected = Pattern.compile (sSearch, Pattern.LITERAL).matcher (sSrc).replaceAll (sReplace);
    final double dJDK = benchmarkTask (new Task ( () -> sSrc.replace (sSearch, sReplace), sExpected));
    final double dSelf = benchmarkTask (new Task ( () -> StringReplace.replaceAll (sSrc, sSearch, sReplace),
                                                   sExpected));
    _log (sName, dJDK, dSelf);
  }

  private static void _runChar (final String sName, final String sSrc, final char cSearch, final char cReplace)
  {
    final String sExpected = sSrc.replace (cSearch, cReplace);
    final double dJDK = benchmarkTask (new Task ( () -> sSrc.replace (cSearch, cReplace), sExpected));
    final double dSelf = benchmarkTask (new Task ( () -> StringReplace.replaceAll (sSrc, cSearch, cReplace),
                                                   sExpected));
    _log (sName, dJDK, dSelf);
  }

  public static void main (final String [] aArgs)
  {
    logSystemInfo ();

    final String sLongSparse = "This is a text without many special characters <a> ".repeat (200);
    final String sLongDense = "<<>><<>>".repeat (1000);
    final String sLongNone = "This is a text without any special characters ".repeat (200);
    final String sLongUTF16 = "Dies ist ein Text mit Umlauten äöü € <a> ".repeat (200);

    _runString ("String short     ", "This is <<a> text", "<", "&lt;");
    _runString ("String long-spars", sLongSparse, "<", "&lt;");
    _runString ("String long-dense", sLongDense, "<", "&lt;");
    _runString ("String long-none ", sLongNone, "<", "&lt;");
    _runString ("String long-utf16", sLongUTF16, "<", "&lt;");
    _runString ("String multi-char", sLongSparse, "<a>", "<b>");
    _runString ("String shrinking ", sLongSparse, "special", "x");

    _runChar ("Char short       ", "This is <<a> text", '<', '[');
    _runChar ("Char long-sparse ", sLongSparse, '<', '[');
    _runChar ("Char long-dense  ", sLongDense, '<', '[');
    _runChar ("Char long-none   ", sLongNone, '<', '[');
    _runChar ("Char long-utf16  ", sLongUTF16, '<', '[');
  }
}
