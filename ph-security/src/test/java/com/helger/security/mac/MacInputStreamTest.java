/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.charset.StandardCharsets;

import javax.crypto.Mac;

import org.junit.Test;

import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.io.stream.StreamHelper;

/**
 * Test class for class {@link MacInputStream}.
 *
 * @author Philip Helger
 */
public final class MacInputStreamTest
{
  private static final byte [] KEY = "secret key".getBytes (StandardCharsets.ISO_8859_1);
  private static final byte [] PAYLOAD = "Hello World - this is the payload".getBytes (StandardCharsets.ISO_8859_1);

  private static Mac _createMac ()
  {
    final EMacAlgorithm eAlgo = EMacAlgorithm.HMAC_SHA256;
    final Mac aMac = eAlgo.createMac ();
    try
    {
      aMac.init (eAlgo.createSecretKey (KEY));
    }
    catch (final java.security.InvalidKeyException ex)
    {
      throw new IllegalStateException (ex);
    }
    return aMac;
  }

  @Test
  public void testReadSingleBytes ()
  {
    final Mac aMac = _createMac ();
    try (final MacInputStream aIS = new MacInputStream (new NonBlockingByteArrayInputStream (PAYLOAD), aMac))
    {
      assertSame (aMac, aIS.getMac ());
      assertTrue (aIS.isOn ());
      assertEquals (Boolean.valueOf (MacInputStream.DEFAULT_ON), Boolean.valueOf (aIS.isOn ()));
      assertNotNull (aIS.toString ());

      int n;
      int nCount = 0;
      while ((n = aIS.read ()) != -1)
      {
        assertEquals (PAYLOAD[nCount] & 0xff, n);
        nCount++;
      }
      assertEquals (PAYLOAD.length, nCount);
    }
    catch (final java.io.IOException ex)
    {
      fail (ex.getMessage ());
    }

    // The same payload must lead to the same MAC
    final Mac aMac2 = _createMac ();
    aMac2.update (PAYLOAD);
    assertArrayEquals (aMac2.doFinal (), aMac.doFinal ());
  }

  @Test
  public void testReadByteArray ()
  {
    final Mac aMac = _createMac ();
    try (final MacInputStream aIS = new MacInputStream (new NonBlockingByteArrayInputStream (PAYLOAD), aMac))
    {
      assertArrayEquals (PAYLOAD, StreamHelper.getAllBytes (aIS));
    }
    catch (final java.io.IOException ex)
    {
      fail (ex.getMessage ());
    }

    final Mac aMac2 = _createMac ();
    aMac2.update (PAYLOAD);
    assertArrayEquals (aMac2.doFinal (), aMac.doFinal ());
  }

  @Test
  public void testSwitchedOff ()
  {
    final Mac aMac = _createMac ();
    try (final MacInputStream aIS = new MacInputStream (new NonBlockingByteArrayInputStream (PAYLOAD), aMac))
    {
      aIS.setOn (false);
      assertFalse (aIS.isOn ());
      assertArrayEquals (PAYLOAD, StreamHelper.getAllBytes (aIS));
    }
    catch (final java.io.IOException ex)
    {
      fail (ex.getMessage ());
    }

    // Nothing was fed to the MAC
    final Mac aMac2 = _createMac ();
    assertArrayEquals (aMac2.doFinal (), aMac.doFinal ());
  }

  @Test
  public void testSetMac ()
  {
    final Mac aMac = _createMac ();
    try (final MacInputStream aIS = new MacInputStream (new NonBlockingByteArrayInputStream (PAYLOAD), aMac))
    {
      final Mac aMac2 = _createMac ();
      aIS.setMac (aMac2);
      assertSame (aMac2, aIS.getMac ());

      try
      {
        aIS.setMac (null);
        fail ();
      }
      catch (final NullPointerException | IllegalArgumentException ex)
      {
        // expected
      }
    }
    catch (final java.io.IOException ex)
    {
      fail (ex.getMessage ());
    }
  }
}
