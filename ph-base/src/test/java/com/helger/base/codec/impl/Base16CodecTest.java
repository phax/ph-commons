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
package com.helger.base.codec.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import org.junit.Test;

/**
 * Test class for class {@link Base16Codec}
 *
 * @author Philip Helger
 */
public final class Base16CodecTest
{
  @Test
  public void testDecode ()
  {
    final String sEncoded = "616263414243";
    assertEquals ("abcABC", new Base16Codec ().getDecodedAsString (sEncoded, StandardCharsets.US_ASCII));
  }

  @Test
  public void testRandomEncodeDecode ()
  {
    final Supplier <String> fRndString = () -> {
      final int nLen = ThreadLocalRandom.current ().nextInt (100);
      final StringBuilder ret = new StringBuilder ();
      for (int i = 0; i < nLen; ++i)
        ret.append (ThreadLocalRandom.current ().nextInt (Character.MIN_CODE_POINT, Character.MAX_CODE_POINT));
      return ret.toString ();
    };

    final Base16Codec aCodec = new Base16Codec ();
    for (int i = 0; i < 1_000; ++i)
    {
      final String sRandom = fRndString.get ();
      final byte [] aEncoded = aCodec.getEncoded (sRandom, StandardCharsets.UTF_8);
      assertNotNull (aEncoded);
      final String sDecoded = aCodec.getDecodedAsString (aEncoded, StandardCharsets.UTF_8);
      assertNotNull (sDecoded);
      assertEquals (sRandom, sDecoded);
    }
  }
}
