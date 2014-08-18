/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.random;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/**
 * Test class for class {@link VerySecureRandom}.
 * 
 * @author Philip Helger
 */
public final class VerySecureRandomTest
{
  private static final int MAX_RUNS = 10000;
  private static final int MAX_DUP = 2;

  @Test
  public void testRandomGen ()
  {
    for (int j = 0; j < 2; ++j)
    {
      final Set <Integer> aInts = new HashSet <Integer> ();
      int nDuplicates = 0;
      for (int i = 0; i < MAX_RUNS; ++i)
      {
        final int n1 = VerySecureRandom.getInstance ().nextInt ();
        final int n2 = VerySecureRandom.getInstance ().nextInt ();
        // should be different
        assertTrue (n1 != n2);
        // should not be contained!
        if (!aInts.add (Integer.valueOf (n1)))
          nDuplicates++;
        if (!aInts.add (Integer.valueOf (n2)))
          nDuplicates++;
      }
      assertTrue (nDuplicates <= MAX_DUP);

      nDuplicates = 0;
      final Set <Long> aLongs = new HashSet <Long> ();
      for (int i = 0; i < MAX_RUNS; ++i)
      {
        final long n1 = VerySecureRandom.getInstance ().nextLong ();
        final long n2 = VerySecureRandom.getInstance ().nextLong ();
        // should be different
        assertTrue (n1 != n2);
        // should not be contained!
        if (!aLongs.add (Long.valueOf (n1)))
          nDuplicates++;
        if (!aLongs.add (Long.valueOf (n2)))
          nDuplicates++;
      }
      assertTrue (nDuplicates <= MAX_DUP);
    }
  }
}
