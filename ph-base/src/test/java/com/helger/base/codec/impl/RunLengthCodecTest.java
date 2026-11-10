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
package com.helger.base.codec.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import org.junit.Test;

import com.helger.base.codec.DecodeException;

/**
 * Test class for class {@link RunLengthCodec}
 *
 * @author Philip Helger
 */
public final class RunLengthCodecTest
{
  private static final Charset CHARSET = StandardCharsets.UTF_8;

  private static byte [] _bytes (final int... aValues)
  {
    final byte [] ret = new byte [aValues.length];
    for (int i = 0; i < aValues.length; ++i)
      ret[i] = (byte) aValues[i];
    return ret;
  }

  private static void _testRoundTrip (final byte [] aDecoded)
  {
    final RunLengthCodec c = new RunLengthCodec ();
    final byte [] aEncoded = c.getEncoded (aDecoded);
    assertTrue (aEncoded.length <= c.getMaximumEncodedLength (aDecoded.length));
    // Always terminated with the end of data marker
    assertEquals (RunLengthCodec.RUN_LENGTH_EOD, aEncoded[aEncoded.length - 1] & 0xff);
    assertArrayEquals (aDecoded, c.getDecoded (aEncoded));
  }

  @Test
  public void testDecode ()
  {
    final RunLengthCodec c = new RunLengthCodec ();
    // A literal run of length byte n copies n + 1 bytes
    assertEquals ("WWWWWWWWWWWWTFF",
                  c.getDecodedAsString (new byte [] { (byte) (257 - 12), 'W', 0x02, 'T', 'F', 'F' }, CHARSET));
    assertEquals ("WWWWWWWWWWTFF",
                  c.getDecodedAsString (new byte [] { (byte) (257 - 10), 'W', 0x02, 'T', 'F', 'F', (byte) 0x80 },
                                        CHARSET));
    // A single literal byte
    assertEquals ("XY", c.getDecodedAsString (new byte [] { 0x00, 'X', 0x00, 'Y', (byte) 0x80 }, CHARSET));
    // Everything after the end of data marker is ignored
    assertEquals ("X", c.getDecodedAsString (new byte [] { 0x00, 'X', (byte) 0x80, 0x00, 'Y' }, CHARSET));
  }

  @Test
  public void testDecodeMaxLiteralRun ()
  {
    final byte [] aEncoded = new byte [1 + 128];
    aEncoded[0] = 0x7f;
    for (int i = 0; i < 128; ++i)
      aEncoded[1 + i] = (byte) i;
    final byte [] aDecoded = new RunLengthCodec ().getDecoded (aEncoded);
    assertEquals (128, aDecoded.length);
    for (int i = 0; i < 128; ++i)
      assertEquals (i, aDecoded[i]);
  }

  @Test (expected = DecodeException.class)
  public void testDecodeTruncatedLiteral ()
  {
    new RunLengthCodec ().getDecoded (new byte [] { 0x03, 'T', 'F', 'F' });
  }

  @Test
  public void testEncodePackBitsExample ()
  {
    // The example from the Apple PackBits technical note, plus the end of data marker
    final byte [] aDecoded = _bytes (0xAA,
                                     0xAA,
                                     0xAA,
                                     0x80,
                                     0x00,
                                     0x2A,
                                     0xAA,
                                     0xAA,
                                     0xAA,
                                     0xAA,
                                     0x80,
                                     0x00,
                                     0x2A,
                                     0x22,
                                     0xAA,
                                     0xAA,
                                     0xAA,
                                     0xAA,
                                     0xAA,
                                     0xAA,
                                     0xAA,
                                     0xAA,
                                     0xAA,
                                     0xAA);
    final byte [] aExpected = _bytes (0xFE,
                                      0xAA,
                                      0x02,
                                      0x80,
                                      0x00,
                                      0x2A,
                                      0xFD,
                                      0xAA,
                                      0x03,
                                      0x80,
                                      0x00,
                                      0x2A,
                                      0x22,
                                      0xF7,
                                      0xAA,
                                      0x80);
    assertArrayEquals (aExpected, new RunLengthCodec ().getEncoded (aDecoded));
  }

  @Test
  public void testEncodeEmpty ()
  {
    assertArrayEquals (new byte [] { (byte) 0x80 }, new RunLengthCodec ().getEncoded (new byte [0]));
    _testRoundTrip (new byte [0]);
  }

  @Test
  public void testRoundTrip ()
  {
    _testRoundTrip ("WWWWWWWWWWWWTFF".getBytes (CHARSET));
    _testRoundTrip ("a".getBytes (CHARSET));
    _testRoundTrip ("aa".getBytes (CHARSET));
    _testRoundTrip ("aab".getBytes (CHARSET));
    _testRoundTrip ("aaab".getBytes (CHARSET));
    _testRoundTrip ("abbbbc".getBytes (CHARSET));

    // Runs and literals around the maximum run length of 128
    for (final int nLen : new int [] { 1, 2, 3, 127, 128, 129, 130, 255, 256, 257, 1000 })
    {
      final byte [] aSame = new byte [nLen];
      _testRoundTrip (aSame);

      final byte [] aDifferent = new byte [nLen];
      for (int i = 0; i < nLen; ++i)
        aDifferent[i] = (byte) i;
      _testRoundTrip (aDifferent);

      // Pairs of equal bytes stay in literal runs
      final byte [] aPairs = new byte [nLen];
      for (int i = 0; i < nLen; ++i)
        aPairs[i] = (byte) (i / 2);
      _testRoundTrip (aPairs);
    }

    // Random data with few and with many repetitions
    final Random aRandom = new Random (20261110);
    for (int i = 0; i < 200; ++i)
    {
      final byte [] aData = new byte [aRandom.nextInt (2000)];
      final int nValueRange = 1 + aRandom.nextInt (i % 2 == 0 ? 3 : 256);
      for (int j = 0; j < aData.length; ++j)
        aData[j] = (byte) aRandom.nextInt (nValueRange);
      _testRoundTrip (aData);
    }
  }

  @Test
  public void testEncodeWithOffset ()
  {
    final RunLengthCodec c = new RunLengthCodec ();
    final byte [] aBuf = "xxaaaabcyy".getBytes (CHARSET);
    final byte [] aEncoded = c.getEncoded (aBuf, 2, 6);
    assertEquals ("aaaabc", c.getDecodedAsString (aEncoded, CHARSET));
  }
}
