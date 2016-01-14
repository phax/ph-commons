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
package com.helger.commons.io.stream;

import static org.junit.Assert.assertArrayEquals;

import java.io.IOException;
import java.util.Random;

import org.junit.Test;

import com.helger.commons.charset.CCharset;
import com.helger.commons.messagedigest.EMessageDigestAlgorithm;
import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link MessageDigestOutputStream}.
 *
 * @author Philip Helger
 */
public final class MessageDigestOutputStreamTest
{
  private static final Random s_aRandom = new Random ();

  @Test
  public void testAll () throws IOException
  {
    // For all algorithms
    for (final EMessageDigestAlgorithm eMDAlgo : EMessageDigestAlgorithm.values ())
    {
      final String sTestString = "test" + eMDAlgo.getAlgorithm () + "-xxx" + s_aRandom.nextDouble ();

      // First hash
      try (MessageDigestOutputStream aHIS1 = new MessageDigestOutputStream (new NonBlockingByteArrayOutputStream (),
                                                                            eMDAlgo))
      {
        StreamHelper.copyInputStreamToOutputStreamAndCloseOS (new StringInputStream (sTestString,
                                                                                     CCharset.CHARSET_ISO_8859_1_OBJ),
                                                              new NonBlockingByteArrayOutputStream ());
        final byte [] aDigest1 = aHIS1.getDigest ();

        // Second hash
        try (MessageDigestOutputStream aHIS2 = new MessageDigestOutputStream (new NonBlockingByteArrayOutputStream (),
                                                                              eMDAlgo))
        {
          StreamHelper.copyInputStreamToOutputStreamAndCloseOS (new StringInputStream (sTestString,
                                                                                       CCharset.CHARSET_ISO_8859_1_OBJ),
                                                                new NonBlockingByteArrayOutputStream ());
          final byte [] aDigest2 = aHIS2.getDigest ();

          // Must be equal
          assertArrayEquals (aDigest1, aDigest2);

          CommonsTestHelper.testToStringImplementation (aHIS1);
        }
      }
    }
  }
}
