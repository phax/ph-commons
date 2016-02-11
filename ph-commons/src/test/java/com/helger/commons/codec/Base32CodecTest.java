package com.helger.commons.codec;

import static org.junit.Assert.assertArrayEquals;
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
  public void testGetEncodedLength ()
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
    assertEquals ("AAAAAAAA", aBase32.getEncodedAsString (new byte [] { 0, 0, 0, 0, 0 }));
  }

  @Test
  public void testGetDecodedLength ()
  {
    assertEquals (0, Base32Codec.getDecodedLength (0));
    for (int i = 1; i <= 8; ++i)
      assertEquals (5, Base32Codec.getDecodedLength (i));
    for (int i = 9; i <= 16; ++i)
      assertEquals (10, Base32Codec.getDecodedLength (i));
    for (int i = 17; i <= 24; ++i)
      assertEquals (15, Base32Codec.getDecodedLength (i));
  }

  @Test
  public void testDecode ()
  {
    final Base32Codec aBase32 = new Base32Codec ();
    assertNull (aBase32.getDecodedAsString (null));
    assertEquals ("", aBase32.getDecodedAsString (new byte [0]));
    assertArrayEquals (new byte [] { 0 }, aBase32.getDecoded ("AA".getBytes ()));
    assertArrayEquals (new byte [] { 0, 0, 0, 0, 0 }, aBase32.getDecoded ("AAAAAAAA".getBytes ()));
    assertEquals ("A", aBase32.getDecodedAsString ("IE".getBytes ()));
    assertEquals ("A", aBase32.getDecodedAsString ("IE======".getBytes ()));
    assertEquals ("f", aBase32.getDecodedAsString ("MY".getBytes ()));
    assertEquals ("f", aBase32.getDecodedAsString ("MY======".getBytes ()));
    assertEquals ("AB", aBase32.getDecodedAsString ("IFBA".getBytes ()));
    assertEquals ("AB", aBase32.getDecodedAsString ("IFBA====".getBytes ()));
    assertEquals ("foo", aBase32.getDecodedAsString ("MZXW6".getBytes ()));
    assertEquals ("foo", aBase32.getDecodedAsString ("MZXW6===".getBytes ()));
    assertEquals ("foob", aBase32.getDecodedAsString ("MZXW6YQ".getBytes ()));
    assertEquals ("foob", aBase32.getDecodedAsString ("MZXW6YQ==".getBytes ()));
    assertEquals ("test", aBase32.getDecodedAsString ("ORSXG5A".getBytes ()));
    assertEquals ("test", aBase32.getDecodedAsString ("ORSXG5A==".getBytes ()));
    assertEquals ("ABCDE", aBase32.getDecodedAsString ("IFBEGRCF".getBytes ()));
    assertEquals ("fooba", aBase32.getDecodedAsString ("MZXW6YTB".getBytes ()));
    assertEquals ("foobar", aBase32.getDecodedAsString ("MZXW6YTBOI".getBytes ()));
    assertEquals ("foobar", aBase32.getDecodedAsString ("MZXW6YTBOI======".getBytes ()));
    assertEquals ("foobar", aBase32.getDecodedAsString ("MZXW6YTBOI==================".getBytes ()));
  }
}
