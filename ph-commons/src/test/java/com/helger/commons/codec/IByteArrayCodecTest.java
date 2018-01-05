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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;

import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;

import org.junit.Test;

import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.random.RandomHelper;

/**
 * Test class for class {@link ICodec}
 *
 * @author Philip Helger
 */
public final class IByteArrayCodecTest
{
  private void _testCodec (@Nonnull final IByteArrayCodec c, @Nonnull final byte [] aSrcBytes)
  {
    // all
    {
      // Encode
      final byte [] aEncoded = c.getEncoded (aSrcBytes);
      assertNotNull (c.getClass ().getName (), aEncoded);

      // Decode
      final byte [] aDecoded = c.getDecoded (aEncoded);
      assertNotNull (c.getClass ().getName (), aDecoded);

      // Compare with source
      assertArrayEquals (c.getClass ().getName (), aSrcBytes, aDecoded);
    }

    // Encode partial
    final int nBytes = aSrcBytes.length;
    if (nBytes >= 4)
    {
      // Encode
      final byte [] aEncoded = c.getEncoded (aSrcBytes, 1, nBytes - 2);
      assertNotNull (c.getClass ().getName (), aEncoded);

      // Decode all (of partial)
      final byte [] aDecoded = c.getDecoded (aEncoded);
      assertNotNull (c.getClass ().getName (), aDecoded);

      // Compare all with source
      assertArrayEquals (c.getClass ().getName (), ArrayHelper.getCopy (aSrcBytes, 1, nBytes - 2), aDecoded);
    }
  }

  private void _testCodec (@Nonnull final IByteArrayCodec c)
  {
    _testCodec (c, new byte [0]);
    _testCodec (c, "Hallo JÜnit".getBytes (StandardCharsets.ISO_8859_1));
    _testCodec (c, "Hallo JÜnit".getBytes (StandardCharsets.UTF_8));

    // Get random bytes
    final byte [] aRandomBytes = new byte [256];
    RandomHelper.getRandom ().nextBytes (aRandomBytes);
    _testCodec (c, aRandomBytes);

    for (int i = 0; i < 256; ++i)
    {
      final byte [] aBuf = new byte [i];

      // build ascending identity field
      for (int j = 0; j < i; ++j)
        aBuf[j] = (byte) j;
      _testCodec (c, aBuf);

      // build constant field with all the same byte
      for (int j = 0; j < i; ++j)
        aBuf[j] = (byte) i;
      _testCodec (c, aBuf);
    }
  }

  @Test
  public void testArbitraryCodecs ()
  {
    _testCodec (new Base16Codec ());
    _testCodec (new Base32Codec (true));
    _testCodec (new Base32Codec (false));
    _testCodec (new Base64Codec ());
    _testCodec (new FlateCodec ());
    _testCodec (new LZWCodec ());
    _testCodec (new QuotedPrintableCodec ());
    _testCodec (new URLCodec ());
  }
}
