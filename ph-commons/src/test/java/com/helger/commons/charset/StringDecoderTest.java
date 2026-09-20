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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.base.string.StringHelper;

/**
 * Test class for class {@link StringDecoder}.
 *
 * @author Philip Helger
 */
public final class StringDecoderTest
{
  private static final String UMLAUTS = "äöüÄÖÜ";

  @Test
  public void testFinishByteArray ()
  {
    final StringDecoder aDecoder = new StringDecoder (StandardCharsets.UTF_8);

    final byte [] aBytes = "abc".getBytes (StandardCharsets.UTF_8);
    assertEquals ("abc", aDecoder.finish (aBytes, 0, aBytes.length));
    // Partial
    assertEquals ("b", aDecoder.finish (aBytes, 1, 1));
    // Empty
    assertEquals ("", aDecoder.finish (aBytes, 0, 0));

    final byte [] aUmlauts = UMLAUTS.getBytes (StandardCharsets.UTF_8);
    assertEquals (UMLAUTS, aDecoder.finish (aUmlauts, 0, aUmlauts.length));
  }

  @Test
  public void testFinishByteBuffer ()
  {
    final StringDecoder aDecoder = new StringDecoder (StandardCharsets.UTF_8);
    assertEquals ("abc", aDecoder.finish (ByteBuffer.wrap ("abc".getBytes (StandardCharsets.UTF_8))));
  }

  @Test
  public void testDecodeChunks ()
  {
    final StringDecoder aDecoder = new StringDecoder (StandardCharsets.UTF_8);

    final byte [] aBytes = "abcdef".getBytes (StandardCharsets.UTF_8);
    aDecoder.decode (aBytes, 0, 3);
    aDecoder.decode (ByteBuffer.wrap (aBytes, 3, 2));
    assertEquals ("abcdef", aDecoder.finish (aBytes, 5, 1));

    // The decoder can be reused after finish
    aDecoder.decode ("abc".getBytes (StandardCharsets.UTF_8));
    assertEquals ("abc", aDecoder.finish (new byte [0], 0, 0));
  }

  @Test
  public void testLargeInput ()
  {
    final StringDecoder aDecoder = new StringDecoder (StandardCharsets.UTF_8);

    // Larger than the initial buffer, so the buffer must grow
    final String sLong = StringHelper.getRepeated ("abcdefghij", 1000);
    final byte [] aBytes = sLong.getBytes (StandardCharsets.UTF_8);
    assertEquals (sLong, aDecoder.finish (aBytes, 0, aBytes.length));
  }

  @Test
  public void testReserve ()
  {
    final StringDecoder aDecoder = new StringDecoder (StandardCharsets.UTF_8);

    // Smaller than the initial buffer
    aDecoder.reserve (10);
    // Larger than the initial buffer - and not aligned
    aDecoder.reserve (StringDecoder.INITIAL_BUFFER_SIZE * 2 + 1);
    assertEquals ("abc", aDecoder.finish ("abc".getBytes (StandardCharsets.UTF_8), 0, 3));

    try
    {
      aDecoder.reserve (-1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }

    // Reserve is only allowed if nothing was decoded yet
    aDecoder.decode ("abc".getBytes (StandardCharsets.UTF_8));
    try
    {
      aDecoder.reserve (10);
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
    }
  }
}
