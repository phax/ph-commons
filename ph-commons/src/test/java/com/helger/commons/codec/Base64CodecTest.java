package com.helger.commons.codec;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import com.helger.commons.base64.Base64;

/**
 * Test class for class {@link Base64Codec}
 *
 * @author Philip Helger
 */
public final class Base64CodecTest
{
  @Test
  public void testGetEncodedLength ()
  {
    final Base64Codec aBase64 = new Base64Codec ();
    for (int i = 0; i < 256; ++i)
    {
      final byte [] aBuf = new byte [i];
      Arrays.fill (aBuf, (byte) i);
      assertEquals (Base64.safeEncodeBytesToBytes (aBuf).length, aBase64.getEncodedLength (i));
    }
  }
}
