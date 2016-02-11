package com.helger.commons.codec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.helger.commons.charset.CCharset;

/**
 * Test class for class {@link Base32Codec}
 *
 * @author Philip Helger
 */
public final class Base32CodecTest
{
  @Test
  public void testGetEncodingLength ()
  {
    assertEquals (0, Base32Codec.getEncodedLength (0));
    for (int i = 1; i <= 5; ++i)
      assertEquals (8, Base32Codec.getEncodedLength (i));
    for (int i = 6; i <= 10; ++i)
      assertEquals (16, Base32Codec.getEncodedLength (i));
    for (int i = 11; i <= 15; ++i)
      assertEquals (24, Base32Codec.getEncodedLength (i));
  }

  @Test
  public void testEncode ()
  {
    final Base32Codec aBase32 = new Base32Codec ();
    assertNull (aBase32.getEncodedAsString (null));
    assertEquals ("", aBase32.getEncodedAsString (new byte [0]));
    assertEquals ("AA======", aBase32.getEncodedAsString (new byte [] { 0 }));
    assertEquals ("IE======", aBase32.getEncodedAsString ("A".getBytes (CCharset.DEFAULT_CHARSET_OBJ)));
    assertEquals ("MY======", aBase32.getEncodedAsString ("f".getBytes (CCharset.DEFAULT_CHARSET_OBJ)));
    assertEquals ("IFBA====", aBase32.getEncodedAsString ("AB".getBytes (CCharset.DEFAULT_CHARSET_OBJ)));
    assertEquals ("MZXW6===", aBase32.getEncodedAsString ("foo".getBytes (CCharset.DEFAULT_CHARSET_OBJ)));
    assertEquals ("MZXW6YQ=", aBase32.getEncodedAsString ("foob".getBytes (CCharset.DEFAULT_CHARSET_OBJ)));
    assertEquals ("ORSXG5A=", aBase32.getEncodedAsString ("test".getBytes (CCharset.DEFAULT_CHARSET_OBJ)));
    assertEquals ("IFBEGRCF", aBase32.getEncodedAsString ("ABCDE".getBytes (CCharset.DEFAULT_CHARSET_OBJ)));
    assertEquals ("MZXW6YTB", aBase32.getEncodedAsString ("fooba".getBytes (CCharset.DEFAULT_CHARSET_OBJ)));
    assertEquals ("MZXW6YTBOI======", aBase32.getEncodedAsString ("foobar".getBytes (CCharset.DEFAULT_CHARSET_OBJ)));
  }
}
