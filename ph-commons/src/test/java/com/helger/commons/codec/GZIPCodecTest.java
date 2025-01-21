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
package com.helger.commons.codec;

import static org.junit.Assert.assertArrayEquals;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

/**
 * Test class for class {@link GZIPCodec}
 *
 * @author Philip Helger
 */
public final class GZIPCodecTest
{
  private static final Charset CHARSET = StandardCharsets.UTF_8;

  @Test
  public void testDecode ()
  {
    final GZIPCodec aCodec = new GZIPCodec ();
    for (final String s : new String [] { "abc", "", "test blank", "hällö special chars" })
    {
      final byte [] aDecoded = s.getBytes (CHARSET);
      final byte [] aEncoded = aCodec.getEncoded (aDecoded);
      final byte [] aDecoded2 = aCodec.getDecoded (aEncoded);
      assertArrayEquals (aDecoded, aDecoded2);
    }
  }
}
