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

import com.helger.commons.string.StringHelper;
import com.helger.commons.timing.StopWatch;

public final class BenchmarkIntCharCount
{
  interface IDoIt
  {
    int getCharacterCount (int n);
  }

  public static final IDoIt s_a1 = n -> StringHelper.getCharacterCount (n);

  public static final IDoIt s_a2 = n -> Integer.toString (n).length ();

  public static void main (final String [] args)
  {
    final int nMinValue = -1000000;
    final int nMaxValue = nMinValue * -1;

    final StopWatch aSW1 = StopWatch.createdStarted ();
    int nSum1 = 0;
    for (int i = nMinValue; i <= nMaxValue; ++i)
      nSum1 += s_a1.getCharacterCount (i);
    aSW1.stop ();
    System.out.println ("Version 1 took " + aSW1.getMillis ());

    final StopWatch aSW2 = StopWatch.createdStarted ();
    int nSum2 = 0;
    for (int i = nMinValue; i <= nMaxValue; ++i)
      nSum2 += s_a2.getCharacterCount (i);
    aSW2.stop ();
    System.out.println ("Version 2 took " + aSW2.getMillis ());

    if (nSum1 != nSum2)
      throw new RuntimeException ("Dont match! " + nSum1 + " -- " + nSum2);
  }
}
