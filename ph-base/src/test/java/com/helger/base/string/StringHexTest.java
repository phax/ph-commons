package com.helger.base.string;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

/**
 * Test class for class StringHex
 *
 * @author Philip Helger
 */
public final class StringHexTest
{

  @Test
  public void testHexEncode ()
  {
    try
    {
      // null not allowed
      StringHex.getHexEncoded (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      // null not allowed
      StringHex.getHexEncoded (null, 0, 5);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StringHex.getHexEncoded (new byte [0], -1, 5);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      StringHex.getHexEncoded (new byte [0], 0, -1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      StringHex.getHexEncoded (new byte [0], 0, 1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    assertEquals ("", StringHex.getHexEncoded (new byte [] {}));
    assertEquals ("01", StringHex.getHexEncoded (new byte [] { 1 }));
    assertEquals ("010a", StringHex.getHexEncoded (new byte [] { 1, 10 }));
    assertEquals ("00010aff", StringHex.getHexEncoded (new byte [] { 0, 1, 10, (byte) 255 }));

    // Byte offset
    assertEquals ("010a", StringHex.getHexEncoded (new byte [] { 1, 10 }, 0, 2));
    assertEquals ("01", StringHex.getHexEncoded (new byte [] { 1, 10 }, 0, 1));
    assertEquals ("0a", StringHex.getHexEncoded (new byte [] { 1, 10 }, 1, 1));
    try
    {
      // length is too large
      StringHex.getHexEncoded (new byte [] { 1, 10 }, 1, 2);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testGetHexValue ()
  {
    assertEquals (0, StringHex.getHexValue ('0'));
    assertEquals (9, StringHex.getHexValue ('9'));
    assertEquals (10, StringHex.getHexValue ('a'));
    assertEquals (10, StringHex.getHexValue ('A'));
    assertEquals (15, StringHex.getHexValue ('f'));
    assertEquals (15, StringHex.getHexValue ('F'));
    assertEquals (-1, StringHex.getHexValue ('g'));
    assertEquals (-1, StringHex.getHexValue ('z'));
  }

  @Test
  public void testGetHexChar ()
  {
    assertEquals ('0', StringHex.getHexChar (0));
    assertEquals ('9', StringHex.getHexChar (9));
    assertEquals ('a', StringHex.getHexChar (10));
    assertEquals ('f', StringHex.getHexChar (15));
    assertEquals ('\0', StringHex.getHexChar (-1));
    assertEquals ('\0', StringHex.getHexChar (16));
    assertEquals ('\0', StringHex.getHexChar (999));
  }

  @Test
  public void testHexDecode ()
  {
    try
    {
      // null not allowed
      StringHex.getHexDecoded ((String) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      // null not allowed
      StringHex.getHexDecoded ((char []) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      // odd length
      StringHex.getHexDecoded ("000");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      // No valid hex char 'g'
      StringHex.getHexDecoded ("0g");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      // No valid hex char 'g'
      StringHex.getHexDecoded ("g0");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    for (final String sString : new String [] { "Super", "Hallo", "", "Welt!", "fff" })
      assertEquals (sString,
                    new String (StringHex.getHexDecoded (StringHex.getHexEncoded (sString.getBytes (StandardCharsets.ISO_8859_1))),
                                StandardCharsets.ISO_8859_1));

    assertArrayEquals (new byte [] { 0 }, StringHex.getHexDecoded ("00"));
    assertArrayEquals (new byte [] { 0, 1 }, StringHex.getHexDecoded ("0001"));
    assertArrayEquals (new byte [] { 0 }, StringHex.getHexDecoded ("0001".toCharArray (), 0, 2));
    assertArrayEquals (new byte [] { 1 }, StringHex.getHexDecoded ("0001".toCharArray (), 2, 2));
  }

  @Test
  public void testHexStringByte ()
  {
    assertEquals ("ff", StringHex.getHexString ((byte) -1));
    assertEquals ("0", StringHex.getHexString ((byte) 0));
    assertEquals ("9", StringHex.getHexString ((byte) 9));
    assertEquals ("a", StringHex.getHexString ((byte) 10));
    assertEquals ("10", StringHex.getHexString ((byte) 16));
    assertEquals ("ff", StringHex.getHexString ((byte) 255));
  }

  @Test
  public void testHexStringLeadingZeroByte ()
  {
    assertEquals ("00ff", StringHex.getHexStringLeadingZero ((byte) -1, 4));
    assertEquals ("0000", StringHex.getHexStringLeadingZero ((byte) 0, 4));
    assertEquals ("0009", StringHex.getHexStringLeadingZero ((byte) 9, 4));
    assertEquals ("000a", StringHex.getHexStringLeadingZero ((byte) 10, 4));
    assertEquals ("0010", StringHex.getHexStringLeadingZero ((byte) 16, 4));
    assertEquals ("00ff", StringHex.getHexStringLeadingZero ((byte) 255, 4));
  }

  @Test
  public void testHexStringLeadingZero2Byte ()
  {
    assertEquals ("ff", StringHex.getHexStringLeadingZero2 ((byte) -1));
    assertEquals ("00", StringHex.getHexStringLeadingZero2 ((byte) 0));
    assertEquals ("09", StringHex.getHexStringLeadingZero2 ((byte) 9));
    assertEquals ("0a", StringHex.getHexStringLeadingZero2 ((byte) 10));
    assertEquals ("10", StringHex.getHexStringLeadingZero2 ((byte) 16));
    assertEquals ("ff", StringHex.getHexStringLeadingZero2 ((byte) 255));
  }

  @Test
  public void testHexStringInt ()
  {
    assertEquals ("-10", StringHex.getHexString (-16));
    assertEquals ("-1", StringHex.getHexString (-1));
    assertEquals ("0", StringHex.getHexString (0));
    assertEquals ("9", StringHex.getHexString (9));
    assertEquals ("a", StringHex.getHexString (10));
    assertEquals ("10", StringHex.getHexString (16));
    assertEquals ("ff", StringHex.getHexString (255));
    assertEquals ("ffff", StringHex.getHexString (65535));
  }

  @Test
  public void testHexStringLeadingZeroInt ()
  {
    assertEquals ("-10", StringHex.getHexStringLeadingZero (-16, 2));
    assertEquals ("-1", StringHex.getHexStringLeadingZero (-1, 2));
    assertEquals ("00", StringHex.getHexStringLeadingZero (0, 2));
    assertEquals ("09", StringHex.getHexStringLeadingZero (9, 2));
    assertEquals ("00a", StringHex.getHexStringLeadingZero (10, 3));
    assertEquals ("010", StringHex.getHexStringLeadingZero (16, 3));
    assertEquals ("00ff", StringHex.getHexStringLeadingZero (255, 4));
    assertEquals ("ffff", StringHex.getHexStringLeadingZero (65535, 4));
    assertEquals ("ffff", StringHex.getHexStringLeadingZero (65535, 0));
  }

  @Test
  public void testHexStringLong ()
  {
    assertEquals ("-10", StringHex.getHexString (-16L));
    assertEquals ("-1", StringHex.getHexString (-1L));
    assertEquals ("0", StringHex.getHexString (0L));
    assertEquals ("9", StringHex.getHexString (9L));
    assertEquals ("a", StringHex.getHexString (10L));
    assertEquals ("10", StringHex.getHexString (16L));
    assertEquals ("ff", StringHex.getHexString (255L));
    assertEquals ("ffff", StringHex.getHexString (65535L));
    assertEquals ("ffff0000", StringHex.getHexString (65536L * 65535L));
  }

  @Test
  public void testHexStringLeadingZeroLong ()
  {
    assertEquals ("-10", StringHex.getHexStringLeadingZero (-16L, 2));
    assertEquals ("-01", StringHex.getHexStringLeadingZero (-1L, 3));
    assertEquals ("000", StringHex.getHexStringLeadingZero (0L, 3));
    assertEquals ("09", StringHex.getHexStringLeadingZero (9L, 2));
    assertEquals ("00a", StringHex.getHexStringLeadingZero (10L, 3));
    assertEquals ("00ff", StringHex.getHexStringLeadingZero (255L, 4));
    assertEquals ("ffff", StringHex.getHexStringLeadingZero (65535L, 4));
    assertEquals ("0000ffff", StringHex.getHexStringLeadingZero (65535L, 8));
    assertEquals ("ffff0000", StringHex.getHexStringLeadingZero (65536L * 65535L, 5));
  }

  @Test
  public void testHexStringShort ()
  {
    assertEquals ("fffe", StringHex.getHexString ((short) -2));
    assertEquals ("ffff", StringHex.getHexString ((short) -1));
    assertEquals ("0", StringHex.getHexString ((short) 0));
    assertEquals ("9", StringHex.getHexString ((short) 9));
    assertEquals ("a", StringHex.getHexString ((short) 10));
    assertEquals ("10", StringHex.getHexString ((short) 16));
    assertEquals ("ff", StringHex.getHexString ((short) 255));
  }

  @Test
  public void testHexStringLeadingZeroShort ()
  {
    assertEquals ("0ffff", StringHex.getHexStringLeadingZero ((short) -1, 5));
    assertEquals ("0000", StringHex.getHexStringLeadingZero ((short) 0, 4));
    assertEquals ("0009", StringHex.getHexStringLeadingZero ((short) 9, 4));
    assertEquals ("000a", StringHex.getHexStringLeadingZero ((short) 10, 4));
    assertEquals ("0010", StringHex.getHexStringLeadingZero ((short) 16, 4));
    assertEquals ("00ff", StringHex.getHexStringLeadingZero ((short) 255, 4));
  }

}
