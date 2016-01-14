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

import java.util.ArrayList;
import java.util.List;

import com.helger.commons.string.StringHelper;
import com.helger.commons.timing.StopWatch;

public final class BenchmarkCharContains
{
  interface IDoIt
  {
    boolean containsPathSep (String s);
  }

  public static final IDoIt s_a1 = s -> {
    if (s != null)
      for (final char c : s.toCharArray ())
        if (c == '/' || c == '\\')
          return true;
    return false;
  };

  public static final IDoIt s_a2 = s -> s.indexOf ('/') >= 0 || s.indexOf ('\\') >= 0;

  public static void main (final String [] args)
  {
    final List <String> aStrs = new ArrayList <String> ();

    for (int j = 0; j < 100; ++j)
      for (int i = 'a'; i <= 'z'; ++i)
      {
        final char c = (char) i;
        String sc = Character.toString (c);
        aStrs.add (sc);
        aStrs.add (sc + "/" + sc);
        aStrs.add (sc + "\\" + sc);
        sc = StringHelper.getRepeated (c, 100);
        aStrs.add (sc);
        aStrs.add (sc + "/" + sc);
        aStrs.add (sc + "\\" + sc);
      }

    final StopWatch aSW1 = StopWatch.createdStarted ();
    int nSum1 = 0;
    for (final String s : aStrs)
      nSum1 += s_a1.containsPathSep (s) ? 1 : 0;
    aSW1.stop ();
    System.out.println ("Version 1 took " + aSW1.getMillis ());

    final StopWatch aSW2 = StopWatch.createdStarted ();
    int nSum2 = 0;
    for (final String s : aStrs)
      nSum2 += s_a2.containsPathSep (s) ? 1 : 0;
    aSW2.stop ();
    System.out.println ("Version 2 took " + aSW2.getMillis ());

    if (nSum1 != nSum2)
      throw new RuntimeException ("Dont match! " + nSum1 + " -- " + nSum2);
  }
}
