/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.annotation.Nonnull;

import org.junit.Test;

import com.helger.commons.random.VerySecureRandom;

/**
 * Abstract test base class to test arbitrary String codec encode and decode
 * operations
 * 
 * @author Philip Helger
 */
public abstract class AbstractStringCodecTest extends AbstractCodecTest
{
  @Nonnull
  protected abstract IStringCodec createStringCodec ();

  protected final void testEncodeDecodeString (@Nonnull final String sOriginal)
  {
    final IStringCodec aCodec = createStringCodec ();
    assertNotNull (aCodec);

    // String encode + decode
    {
      final String sEncoded = aCodec.encodeText (sOriginal);
      assertNotNull (sEncoded);

      final String sDecoded = aCodec.decodeText (sEncoded);
      assertNotNull (sDecoded);
      assertEquals (sOriginal, sDecoded);
    }
  }

  @Test
  public final void testEncodeAndDecodeBasicCasesString ()
  {
    testEncodeDecodeString ("");
    testEncodeDecodeString ("Hallo JÜnit");

    for (int i = 0; i < 256; ++i)
    {
      final char [] aBuf = new char [i];

      // build ascending identity field
      for (int j = 0; j < i; ++j)
        aBuf[j] = (char) j;
      testEncodeDecodeString (new String (aBuf));

      // build constant field with all the same byte
      for (int j = 0; j < i; ++j)
        aBuf[j] = (char) i;
      testEncodeDecodeString (new String (aBuf));
    }
  }

  @Test
  public final void testArbitraryRandomString ()
  {
    for (int i = 0; i < 500; ++i)
    {
      final byte [] buf = new byte [i];
      VerySecureRandom.getInstance ().nextBytes (buf);
      final char [] c = new char [i];
      for (int j = 0; j < i; ++j)
        c[j] = (char) buf[j];
      testEncodeDecodeString (new String (c));
    }
  }
}
