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
package com.helger.security.mac;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.SecretKey;

import org.junit.Test;

import com.helger.commons.string.StringHelper;

/**
 * Test class for class {@link EMacAlgorithm}.
 *
 * @author Philip Helger
 */
public final class EMacAlgorithmTest
{
  @Test
  public void testAll ()
  {
    for (final EMacAlgorithm eAlgo : EMacAlgorithm.values ())
    {
      assertTrue (StringHelper.hasText (eAlgo.getAlgorithm ()));
      assertNotNull (eAlgo.createMac ());
      assertSame (eAlgo, EMacAlgorithm.getFromStringIgnoreCase (eAlgo.getAlgorithm ()));
      assertSame (eAlgo, EMacAlgorithm.getFromStringIgnoreCase (eAlgo.getAlgorithm ().toLowerCase (Locale.US)));
      assertSame (eAlgo, EMacAlgorithm.getFromStringIgnoreCase (eAlgo.getAlgorithm ().toUpperCase (Locale.US)));
      assertSame (eAlgo, EMacAlgorithm.valueOf (eAlgo.name ()));
    }
    assertNull (EMacAlgorithm.getFromStringIgnoreCase (null));
    assertNull (EMacAlgorithm.getFromStringIgnoreCase ("bla"));
  }

  @Test
  public void testConsistency () throws InvalidKeyException
  {
    // For all algorithms
    for (final EMacAlgorithm eAlgo : EMacAlgorithm.values ())
    {
      final SecretKey aSigningKey = eAlgo.createSecretKey ("keyForTestingPurposesOnly".getBytes (StandardCharsets.ISO_8859_1));

      // Create 2 Macs
      final Mac aMD1 = eAlgo.createMac ();
      aMD1.init (aSigningKey);
      final Mac aMD2 = eAlgo.createMac ();
      aMD2.init (aSigningKey);
      for (int i = 0; i < 255; ++i)
      {
        final byte [] aBytes = ("abc" + i + "def").getBytes (StandardCharsets.ISO_8859_1);
        aMD1.update ((byte) i);
        aMD1.update (aBytes);
        aMD2.update ((byte) i);
        aMD2.update (aBytes);
      }

      // Results must be equal
      assertArrayEquals (aMD1.doFinal (), aMD2.doFinal ());
    }
  }
}
