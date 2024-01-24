/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.security.SecureRandom;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.ICommonsSet;

/**
 * Test class for class {@link VerySecureRandom}.
 *
 * @author Philip Helger
 */
public final class VerySecureRandomTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (VerySecureRandomTest.class);

  private static final int MAX_RUNS = 4000;
  private static final int MAX_DUP = 2;

  @Test
  public void testRandomGen ()
  {
    final SecureRandom aRandom = VerySecureRandom.getInstance ();
    for (int j = 0; j < 2; ++j)
    {
      LOGGER.info ("Round " + (j + 1) + " with int");
      final ICommonsSet <Integer> aInts = new CommonsHashSet <> ();
      int nDuplicates = 0;
      for (int i = 0; i < MAX_RUNS; ++i)
      {
        final int n1 = aRandom.nextInt ();
        final int n2 = aRandom.nextInt ();
        // should be different
        assertNotEquals (n1, n2);
        // should not be contained!
        if (!aInts.add (Integer.valueOf (n1)))
          nDuplicates++;
        if (!aInts.add (Integer.valueOf (n2)))
          nDuplicates++;
      }
      assertTrue (nDuplicates <= MAX_DUP);

      LOGGER.info ("Round " + (j + 1) + " with long");
      nDuplicates = 0;
      final ICommonsSet <Long> aLongs = new CommonsHashSet <> ();
      for (int i = 0; i < MAX_RUNS; ++i)
      {
        final long n1 = aRandom.nextLong ();
        final long n2 = aRandom.nextLong ();
        // should be different
        assertNotEquals (n1, n2);
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
