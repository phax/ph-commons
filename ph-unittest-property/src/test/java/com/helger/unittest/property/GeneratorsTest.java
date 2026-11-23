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
package com.helger.unittest.property;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.Test;

/**
 * Test class for class {@link Generators}.
 *
 * @author Philip Helger
 */
public final class GeneratorsTest
{
  @Test
  public void testBytesInRange ()
  {
    new PropertyChecker ().forAll (Generators.bytes ((byte) -3, (byte) 5),
                                   aValue -> assertTrue (aValue.byteValue () >= -3 && aValue.byteValue () <= 5));
  }

  @Test
  public void testLongs () throws Exception
  {
    final long nMax = (1L << 48) - 1;
    new PropertyChecker ().forAll (Generators.longs (-5, nMax),
                                   aValue -> assertTrue (aValue.longValue () >= -5 && aValue.longValue () <= nMax));

    // Values above the int range are found and shrunk to the boundary
    try
    {
      new PropertyChecker ().setSeed (1).forAll (Generators.longs (0, nMax),
                                                 aValue -> assertTrue (aValue.longValue () <= Integer.MAX_VALUE));
      fail ();
    }
    catch (final PropertyFailedError ex)
    {
      assertEquals (Long.valueOf (Integer.MAX_VALUE + 1L), ex.getShrunkSample ());
    }

    // The full range shrinks towards 0
    try
    {
      new PropertyChecker ().setSeed (1).forAll (Generators.longs (), aValue -> assertTrue (aValue.longValue () >= 0));
      fail ();
    }
    catch (final PropertyFailedError ex)
    {
      assertTrue (ex.getMessage (), ((Long) ex.getShrunkSample ()).longValue () < 0);
    }
  }

  @Test
  public void testStringsFromCharRange ()
  {
    new PropertyChecker ().forAll (Generators.strings ('\u00e0', '\u00ff', 3, 7), sText -> {
      assertTrue (sText, sText.length () >= 3 && sText.length () <= 7);
      for (int i = 0; i < sText.length (); ++i)
        assertTrue (sText, sText.charAt (i) >= '\u00e0' && sText.charAt (i) <= '\u00ff');
    });
  }

  @Test
  public void testMutatedByteArraysLength ()
  {
    // At most 3 edits, each changes the length by at most one - except truncation
    final IGenerator <byte []> aBase = Generators.byteArrays (10, 10);
    new PropertyChecker ().forAll (Generators.mutatedByteArrays (aBase),
                                   aBytes -> assertTrue (Arrays.toString (aBytes), aBytes.length <= 13));
  }

  @Test
  public void testMutatedByteArraysChangesInput ()
  {
    // With a single edit and replacement bytes that never occur in the input, every result differs
    // from the input. With several edits this does not hold, as an insert may be undone by a delete.
    final byte [] aValid = { 1, 2, 3, 4, 5 };
    new PropertyChecker ().forAll (Generators.mutatedByteArrays (Generators.constant (aValid),
                                                                 Generators.bytes ((byte) 10, (byte) 20),
                                                                 1),
                                   aBytes -> assertTrue (!Arrays.equals (aValid, aBytes)));
  }

  @Test
  public void testMutatedByteArraysShrinking ()
  {
    // A "decoder" that only accepts lower case letters - the minimal defect is a single 0 byte
    final IGenerator <byte []> aValid = Generators.strings ("abcdefghijklmnopqrstuvwxyz", 0, 50)
                                                  .map (sText -> sText.getBytes (StandardCharsets.US_ASCII));
    for (int nSeed = 1; nSeed <= 50; ++nSeed)
    {
      try
      {
        new PropertyChecker ().setSeed (nSeed).forAll (Generators.mutatedByteArrays (aValid), aBytes -> {
          for (final byte b : aBytes)
            assertTrue (b >= 'a' && b <= 'z');
        });
        fail ("Seed " + nSeed + ": expected a failure");
      }
      catch (final PropertyFailedError ex)
      {
        assertArrayEquals (ex.getMessage (), new byte [] { 0 }, (byte []) ex.getShrunkSample ());
      }
    }
  }
}
