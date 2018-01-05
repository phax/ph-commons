/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

/**
 * Test class for class {@link Base64Codec}
 *
 * @author Philip Helger
 */
public final class Base64CodecTest
{
  @Test
  public void testGetEncodedLength ()
  {
    final Base64Codec aBase64 = new Base64Codec ();
    for (int i = 0; i < 256; ++i)
    {
      final byte [] aBuf = new byte [i];
      Arrays.fill (aBuf, (byte) i);
      assertEquals (aBase64.getEncoded (aBuf).length, aBase64.getEncodedLength (i));
    }
  }

  @Test
  public void testGetDecodedLength ()
  {
    final Base64Codec aBase64 = new Base64Codec ();
    assertEquals (0, aBase64.getDecodedLength (0));
    for (int i = 1; i <= 4; ++i)
      assertEquals (3, aBase64.getDecodedLength (i));
    for (int i = 5; i <= 8; ++i)
      assertEquals (6, aBase64.getDecodedLength (i));
  }
}
