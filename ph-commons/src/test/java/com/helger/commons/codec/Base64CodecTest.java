package com.helger.commons.codec;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

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
      assertEquals (aBase64.getEncoded (aBuf).length, aBase64.getEncodedLength (i));
    }
  }

  @Test
  public void testGetDecodedLength ()
  {
    final Base64Codec aBase64 = new Base64Codec ();
    assertEquals (0, aBase64.getDecodedLength (0));
    for (int i = 1; i <= 4; ++i)
      assertEquals (3, aBase64.getDecodedLength (i));
    for (int i = 5; i <= 8; ++i)
      assertEquals (6, aBase64.getDecodedLength (i));
  }
}
