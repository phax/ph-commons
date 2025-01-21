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
package com.helger.security.messagedigest;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.codec.Base32Codec;
import com.helger.commons.string.StringHelper;

/**
 * Test class for class {@link EMessageDigestAlgorithm}.
 *
 * @author Philip Helger
 */
public final class EMessageDigestAlgorithmTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (EMessageDigestAlgorithmTest.class);
  private static final Charset CHARSET = StandardCharsets.UTF_8;

  @Test
  public void testAll ()
  {
    for (final EMessageDigestAlgorithm eAlgo : EMessageDigestAlgorithm.values ())
    {
      assertTrue (StringHelper.hasText (eAlgo.getAlgorithm ()));
      assertNotNull (eAlgo.createMessageDigest ());
      assertSame (eAlgo, EMessageDigestAlgorithm.getFromStringIgnoreCase (eAlgo.getAlgorithm ()));
      assertSame (eAlgo, EMessageDigestAlgorithm.getFromStringIgnoreCase (eAlgo.getAlgorithm ().toLowerCase (Locale.US)));
      assertSame (eAlgo, EMessageDigestAlgorithm.getFromStringIgnoreCase (eAlgo.getAlgorithm ().toUpperCase (Locale.US)));
      assertSame (eAlgo, EMessageDigestAlgorithm.valueOf (eAlgo.name ()));
    }
    assertNull (EMessageDigestAlgorithm.getFromStringIgnoreCase (null));
    assertNull (EMessageDigestAlgorithm.getFromStringIgnoreCase ("bla"));
  }

  @Test
  public void testConsistency ()
  {
    // For all algorithms
    for (final EMessageDigestAlgorithm eAlgo : EMessageDigestAlgorithm.values ())
    {
      // Create 2 MDGens
      final MessageDigest aMD1 = eAlgo.createMessageDigest ();
      final MessageDigest aMD2 = eAlgo.createMessageDigest ();
      for (int i = 0; i < 255; ++i)
      {
        final byte [] aBytes = ("abc" + i + "def").getBytes (StandardCharsets.ISO_8859_1);
        aMD1.update ((byte) i);
        aMD1.update (aBytes);
        aMD2.update ((byte) i);
        aMD2.update (aBytes);
      }

      // Results must be equal
      assertArrayEquals (aMD1.digest (), aMD2.digest ());
    }
  }

  @Test
  public void testBase32 ()
  {
    final Base32Codec aBase32 = new Base32Codec ().setAddPaddding (false);
    assertEquals ("4444WYPIXHSTJGGABKB7QMG63KJNR7IFMXRALGPORDXI6ZF64HUA",
                  aBase32.getEncodedAsString (MessageDigestValue.create ("urn:oasis:names:tc:ebcore:partyid-type:iso6523:0060:1234567890128".getBytes (CHARSET),
                                                                         EMessageDigestAlgorithm.SHA_256)
                                                                .getAllBytes (),
                                              CHARSET));
    assertEquals ("XJ4BNP4PAHH6UQKBIDPF3LRCEOYAGYNDSYLXVHFUCD7WD4QACWWQ",
                  aBase32.getEncodedAsString (MessageDigestValue.create ("abc".getBytes (CHARSET), EMessageDigestAlgorithm.SHA_256)
                                                                .getAllBytes (),
                                              CHARSET));
    assertEquals ("WXKAIXB7IZX2SH7CZRVL46JDFINFPTPRAT32E3TRNYFB4J4J354A",
                  aBase32.getEncodedAsString (MessageDigestValue.create ("ABC".getBytes (CHARSET), EMessageDigestAlgorithm.SHA_256)
                                                                .getAllBytes (),
                                              CHARSET));
  }

  @Test
  @Ignore ("takes 5 seconds and is only valuable with the output")
  public void testDistribution ()
  {
    final EMessageDigestAlgorithm eAlgo = EMessageDigestAlgorithm.SHA_256;
    final ThreadLocalRandom aRandom = ThreadLocalRandom.current ();
    final int nBuckets = 256;
    final int [] aCount = new int [nBuckets];
    for (int i = 0; i < nBuckets * 10_000; ++i)
    {
      // Get random bytes
      final byte [] aBytes = new byte [128];
      aRandom.nextBytes (aBytes);

      // Get message digest
      final MessageDigest aMD1 = eAlgo.createMessageDigest ();
      final byte [] aDigest = aMD1.digest (aBytes);

      // Calc bucket index
      int nSum = 0;
      for (final byte b : aDigest)
        nSum += (b & 0xff);
      final int nBucket = nSum % nBuckets;

      // Count
      aCount[nBucket]++;
    }
    final StringBuilder aSB = new StringBuilder ();
    for (int i = 0; i < nBuckets; i++)
      aSB.append ("bucket[" + i + "]: " + aCount[i]).append ('\n');
    LOGGER.info ("Result:\n" + aSB.toString ());
  }
}
