/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;

import org.junit.Test;

import com.helger.commons.charset.CCharset;
import com.helger.commons.charset.CharsetManager;
import com.helger.commons.mock.AbstractPHTestCase;
import com.helger.commons.random.VerySecureRandom;

/**
 * Abstract test base class to test arbitrary codec encode and decode operations
 * 
 * @author Philip Helger
 */
public abstract class AbstractCodecTest extends AbstractPHTestCase
{
  @Nonnull
  protected abstract ICodec createCodec ();

  protected final void testEncodeDecode (@Nonnull final byte [] aOriginalBytes)
  {
    final ICodec aCodec = createCodec ();
    assertNotNull (aCodec);

    // bytes encode + decode
    {
      final byte [] aEncoded = aCodec.encode (aOriginalBytes);
      assertNotNull (aEncoded);

      final byte [] aDecoded = aCodec.decode (aEncoded);
      assertNotNull (aDecoded);
      assertArrayEquals (aOriginalBytes, aDecoded);
    }
  }

  @Test
  public final void testEncodeAndDecodeBasicCases ()
  {
    testEncodeDecode (new byte [0]);
    testEncodeDecode (CharsetManager.getAsBytes ("Hallo JÜnit", CCharset.CHARSET_ISO_8859_1_OBJ));
    testEncodeDecode (CharsetManager.getAsBytes ("Hallo JÜnit", CCharset.CHARSET_UTF_8_OBJ));

    for (int i = 0; i < 256; ++i)
    {
      final byte [] aBuf = new byte [i];

      // build ascending identity field
      for (int j = 0; j < i; ++j)
        aBuf[j] = (byte) j;
      testEncodeDecode (aBuf);

      // build constant field with all the same byte
      for (int j = 0; j < i; ++j)
        aBuf[j] = (byte) i;
      testEncodeDecode (aBuf);
    }
  }

  @Test
  public final void testArbitraryRandomBytes ()
  {
    for (int i = 0; i < 500; ++i)
    {
      final byte [] buf = new byte [i];
      VerySecureRandom.getInstance ().nextBytes (buf);
      testEncodeDecode (buf);
    }

    final byte [] aAny = new byte [1024];
    VerySecureRandom.getInstance ().nextBytes (aAny);
    testEncodeDecode (aAny);
  }
}
