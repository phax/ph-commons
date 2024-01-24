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
package com.helger.commons.codec;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nonnull;

import org.junit.Test;

import com.helger.commons.collection.ArrayHelper;

/**
 * Test class for class {@link ICharArrayCodec}
 *
 * @author Philip Helger
 */
public final class ICharArrayCodecTest
{
  private void _testCodec (@Nonnull final ICharArrayCodec c, @Nonnull final char [] aSrcChars)
  {
    // all
    {
      // Encode
      final char [] aEncoded = c.getEncoded (aSrcChars);
      assertNotNull (c.getClass ().getName (), aEncoded);

      // Decode
      final char [] aDecoded = c.getDecoded (aEncoded);
      assertNotNull (c.getClass ().getName (), aDecoded);

      // Compare with source
      assertArrayEquals (c.getClass ().getName (), aSrcChars, aDecoded);
    }

    // as string
    {
      // Encode
      final String sEncoded = c.getEncodedAsString (aSrcChars);
      assertNotNull (c.getClass ().getName (), sEncoded);

      // Decode
      final String sDecoded = c.getDecodedAsString (sEncoded);
      assertNotNull (c.getClass ().getName (), sDecoded);

      // Compare with source
      assertArrayEquals (c.getClass ().getName (), aSrcChars, sDecoded.toCharArray ());
    }

    // Encode partial
    final int nSrcCount = aSrcChars.length;
    if (nSrcCount >= 4)
    {
      // Encode
      final char [] aEncoded = c.getEncoded (aSrcChars, 1, nSrcCount - 2);
      assertNotNull (c.getClass ().getName (), aEncoded);

      // Decode all (of partial)
      final char [] aDecoded = c.getDecoded (aEncoded);
      assertNotNull (c.getClass ().getName (), aDecoded);

      // Compare all with source
      assertArrayEquals (c.getClass ().getName (), ArrayHelper.getCopy (aSrcChars, 1, nSrcCount - 2), aDecoded);
    }
  }

  private void _testCodec (@Nonnull final ICharArrayCodec c)
  {
    _testCodec (c, ArrayHelper.EMPTY_CHAR_ARRAY);
    _testCodec (c, "Hallo JÜnit".toCharArray ());

    // Get random bytes
    final char [] aRandomBytes = new char [256];
    final ThreadLocalRandom aRandom = ThreadLocalRandom.current ();
    for (int i = 0; i < 256; ++i)
      aRandomBytes[i] = (char) aRandom.nextInt (Character.MIN_CODE_POINT, Character.MAX_CODE_POINT);
    _testCodec (c, aRandomBytes);

    for (int i = 0; i < 256; ++i)
    {
      final char [] aBuf = new char [i];

      // build ascending identity field
      for (int j = 0; j < i; ++j)
        aBuf[j] = (char) j;
      _testCodec (c, aBuf);

      // build constant field with all the same byte
      for (int j = 0; j < i; ++j)
        aBuf[j] = (char) i;
      _testCodec (c, aBuf);
    }
  }

  @Test
  public void testArbitraryCodecs ()
  {
    _testCodec (new RFC2616Codec ());
  }
}
