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
package com.helger.commons.charset;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.base.string.StringHelper;

/**
 * Test class for class {@link StringEncoder}.
 *
 * @author Philip Helger
 */
public final class StringEncoderTest
{
  private static final String UMLAUTS = "äöüÄÖÜ";

  private static byte [] _toArray (final ByteBuffer aBB)
  {
    final byte [] ret = new byte [aBB.remaining ()];
    aBB.get (ret);
    return ret;
  }

  @Test
  public void testGetAsNewArray ()
  {
    final StringEncoder aEncoder = new StringEncoder (StandardCharsets.UTF_8);

    // Empty String
    assertArrayEquals (new byte [0], aEncoder.getAsNewArray (""));

    // Simple ASCII
    assertArrayEquals ("abc".getBytes (StandardCharsets.UTF_8), aEncoder.getAsNewArray ("abc"));

    // Multi-byte characters
    assertArrayEquals (UMLAUTS.getBytes (StandardCharsets.UTF_8), aEncoder.getAsNewArray (UMLAUTS));

    // Surrogate pairs
    final String sSurrogate = "a😀b";
    assertArrayEquals (sSurrogate.getBytes (StandardCharsets.UTF_8), aEncoder.getAsNewArray (sSurrogate));

    // Larger than the internal array buffer, so the "worst case" path is used
    final String sLong = StringHelper.getRepeated ("abcdefghij", 1000);
    assertArrayEquals (sLong.getBytes (StandardCharsets.UTF_8), aEncoder.getAsNewArray (sLong));

    // Larger than the internal char buffer with multi-byte characters
    final String sLongUmlauts = StringHelper.getRepeated (UMLAUTS, 1000);
    assertArrayEquals (sLongUmlauts.getBytes (StandardCharsets.UTF_8), aEncoder.getAsNewArray (sLongUmlauts));
  }

  @Test
  public void testGetAsNewByteBuffer ()
  {
    final StringEncoder aEncoder = new StringEncoder (StandardCharsets.UTF_8);

    assertArrayEquals (new byte [0], _toArray (aEncoder.getAsNewByteBuffer ("")));
    assertArrayEquals ("abc".getBytes (StandardCharsets.UTF_8), _toArray (aEncoder.getAsNewByteBuffer ("abc")));
    assertArrayEquals (UMLAUTS.getBytes (StandardCharsets.UTF_8), _toArray (aEncoder.getAsNewByteBuffer (UMLAUTS)));

    // Enforce a buffer resize
    final String sLongUmlauts = StringHelper.getRepeated (UMLAUTS, 2000);
    assertArrayEquals (sLongUmlauts.getBytes (StandardCharsets.UTF_8),
                       _toArray (aEncoder.getAsNewByteBuffer (sLongUmlauts)));
  }

  @Test
  public void testEncodeToSmallBuffer ()
  {
    final StringEncoder aEncoder = new StringEncoder (StandardCharsets.UTF_8);

    // An empty String never needs a second round
    assertTrue (aEncoder.encode ("", ByteBuffer.allocate (10)).isBreak ());

    // The buffer is too small - "continue" is returned
    final String sSource = StringHelper.getRepeated ("abcdefghij", 100);
    final ByteBuffer aBB = ByteBuffer.allocate (10);
    assertTrue (aEncoder.encode (sSource, aBB).isContinue ());
    assertEquals (10, aBB.position ());

    // Encode the rest
    final ByteBuffer aBB2 = ByteBuffer.allocate (sSource.length ());
    assertTrue (aEncoder.encode (sSource, aBB2).isBreak ());
    assertEquals (sSource.length () - 10, aBB2.position ());

    // After a reset, everything starts from scratch
    aEncoder.reset ();
    assertArrayEquals (sSource.getBytes (StandardCharsets.UTF_8), aEncoder.getAsNewArray (sSource));
  }

  @Test
  public void testMaxBytesPerChar ()
  {
    // The JDK claims 4 bytes per char, but it is only 3 per char (4 per code
    // point, which requires 2 chars)
    assertEquals (3, StringEncoder.UTF8_MAX_BYTES_PER_CHAR);
    for (int i = Character.MIN_VALUE; i <= Character.MAX_VALUE; ++i)
    {
      final char c = (char) i;
      if (Character.isSurrogate (c))
        continue;
      final int nBytes = Character.toString (c).getBytes (StandardCharsets.UTF_8).length;
      if (nBytes > StringEncoder.UTF8_MAX_BYTES_PER_CHAR)
        fail ("Character " + i + " needs " + nBytes + " bytes");
    }
  }
}
