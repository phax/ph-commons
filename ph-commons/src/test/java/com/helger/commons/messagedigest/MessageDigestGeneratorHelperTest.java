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
package com.helger.commons.messagedigest;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.helger.commons.charset.CCharset;
import com.helger.commons.charset.CharsetManager;

/**
 * Test class for class {@link MessageDigestGeneratorHelper}.
 *
 * @author Philip Helger
 */
public final class MessageDigestGeneratorHelperTest
{
  @Test
  public void testAll ()
  {
    // For all algorithms
    for (final EMessageDigestAlgorithm eAlgo : EMessageDigestAlgorithm.values ())
    {
      // Create 2 MDGens
      final IMessageDigestGenerator aMD1 = new MessageDigestGenerator (eAlgo);
      final IMessageDigestGenerator aMD2 = new NonBlockingMessageDigestGenerator (eAlgo);
      for (int i = 0; i < 255; ++i)
      {
        final byte [] aBytes = CharsetManager.getAsBytes ("abc" + i + "def", CCharset.CHARSET_ISO_8859_1_OBJ);
        aMD1.update ((byte) i);
        aMD1.update (aBytes);
        aMD2.update ((byte) i);
        aMD2.update (aBytes);
      }

      // Results must be equal
      assertArrayEquals (aMD1.getAllDigestBytes (), aMD2.getAllDigestBytes ());
      assertEquals (MessageDigestGeneratorHelper.getLongFromDigest (aMD1.getAllDigestBytes ()),
                    MessageDigestGeneratorHelper.getLongFromDigest (aMD2.getAllDigestBytes ()));
      assertEquals (MessageDigestGeneratorHelper.getHexValueFromDigest (aMD1.getAllDigestBytes ()),
                    MessageDigestGeneratorHelper.getHexValueFromDigest (aMD2.getAllDigestBytes ()));

      final String s = "ph-commons is great";
      final byte [] aBytes = CharsetManager.getAsBytes (s, CCharset.CHARSET_ISO_8859_1_OBJ);
      assertArrayEquals (MessageDigestGeneratorHelper.getAllDigestBytes (eAlgo, aBytes),
                         MessageDigestGeneratorHelper.getAllDigestBytes (eAlgo, aBytes));
      assertArrayEquals (MessageDigestGeneratorHelper.getAllDigestBytes (eAlgo, aBytes, 5, 10),
                         MessageDigestGeneratorHelper.getAllDigestBytes (eAlgo, aBytes, 5, 10));
      assertArrayEquals (MessageDigestGeneratorHelper.getAllDigestBytes (eAlgo, s, CCharset.CHARSET_UTF_8_OBJ),
                         MessageDigestGeneratorHelper.getAllDigestBytes (eAlgo, s, CCharset.CHARSET_UTF_8_OBJ));
    }
  }
}
