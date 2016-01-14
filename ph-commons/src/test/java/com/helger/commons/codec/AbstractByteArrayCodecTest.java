/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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

import java.security.SecureRandom;

import javax.annotation.Nonnull;

import org.junit.Test;

import com.helger.commons.charset.CCharset;
import com.helger.commons.charset.CharsetManager;
import com.helger.commons.mock.AbstractCommonsTestCase;
import com.helger.commons.random.VerySecureRandom;

/**
 * Abstract test base class to test arbitrary byte array codec encode and decode
 * operations
 *
 * @author Philip Helger
 */
public abstract class AbstractByteArrayCodecTest extends AbstractCommonsTestCase
{
  @Nonnull
  protected abstract IByteArrayCodec createByteArrayCodec ();

  protected final void testByteArrayEncodeDecode (@Nonnull final byte [] aOriginalBytes)
  {
    final IByteArrayCodec aCodec = createByteArrayCodec ();
    assertNotNull (aCodec);

    // bytes encode + decode
    {
      final byte [] aEncoded = aCodec.getEncoded (aOriginalBytes);
      assertNotNull (aEncoded);

      final byte [] aDecoded = aCodec.getDecoded (aEncoded);
      assertNotNull (aDecoded);
      assertArrayEquals (aOriginalBytes, aDecoded);
    }
  }

  @Test
  public final void testByteArrayEncodeAndDecodeBasicCases ()
  {
    testByteArrayEncodeDecode (new byte [0]);
    testByteArrayEncodeDecode (CharsetManager.getAsBytes ("Hallo JÜnit", CCharset.CHARSET_ISO_8859_1_OBJ));
    testByteArrayEncodeDecode (CharsetManager.getAsBytes ("Hallo JÜnit", CCharset.CHARSET_UTF_8_OBJ));

    for (int i = 0; i < 256; ++i)
    {
      final byte [] aBuf = new byte [i];

      // build ascending identity field
      for (int j = 0; j < i; ++j)
        aBuf[j] = (byte) j;
      testByteArrayEncodeDecode (aBuf);

      // build constant field with all the same byte
      for (int j = 0; j < i; ++j)
        aBuf[j] = (byte) i;
      testByteArrayEncodeDecode (aBuf);
    }
  }

  @Test
  public final void testByteArrayArbitraryRandomBytes ()
  {
    final SecureRandom aRandom = VerySecureRandom.getInstance ();
    for (int i = 0; i < 500; ++i)
    {
      final byte [] buf = new byte [i];
      aRandom.nextBytes (buf);
      testByteArrayEncodeDecode (buf);
    }

    final byte [] aAny = new byte [1024];
    aRandom.nextBytes (aAny);
    testByteArrayEncodeDecode (aAny);
  }
}
