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
package com.helger.commons.string;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.helger.commons.CGlobal;
import com.helger.commons.charset.CCharset;
import com.helger.commons.charset.CharsetManager;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.io.stream.NonBlockingStringWriter;
import com.helger.commons.mock.AbstractCommonsTestCase;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link StringHelper}.
 *
 * @author Philip Helger
 */
public final class StringHelperTest extends AbstractCommonsTestCase
{
  @Test
  public void testHasTextAndHasNoText ()
  {
    assertTrue (StringHelper.hasText ("any"));
    assertTrue (StringHelper.hasText (" "));
    assertFalse (StringHelper.hasText (""));
    assertFalse (StringHelper.hasText (null));

    assertTrue (StringHelper.hasTextAfterTrim ("any"));
    assertFalse (StringHelper.hasTextAfterTrim (" "));
    assertFalse (StringHelper.hasTextAfterTrim (""));
    assertFalse (StringHelper.hasTextAfterTrim (null));

    assertFalse (StringHelper.hasNoText ("any"));
    assertFalse (StringHelper.hasNoText (" "));
    assertTrue (StringHelper.hasNoText (""));
    assertTrue (StringHelper.hasNoText (null));

    assertFalse (StringHelper.hasNoTextAfterTrim ("any"));
    assertTrue (StringHelper.hasNoTextAfterTrim (" "));
    assertTrue (StringHelper.hasNoTextAfterTrim (""));
    assertTrue (StringHelper.hasNoTextAfterTrim (null));
  }

  @Test
  public void testLeadingZero ()
  {
    assertEquals ("005", StringHelper.getLeadingZero (5, 3));
    assertEquals ("0005", StringHelper.getLeadingZero (5, 4));
    assertEquals ("5", StringHelper.getLeadingZero (5, 1));
    assertEquals ("56", StringHelper.getLeadingZero (56, 1));
    assertEquals ("56", StringHelper.getLeadingZero (56, 2));
    assertEquals ("056", StringHelper.getLeadingZero (56, 3));
    assertEquals ("0000056", StringHelper.getLeadingZero (56, 7));
    assertEquals ("0005678", StringHelper.getLeadingZero (5678, 7));
    assertEquals ("-5", StringHelper.getLeadingZero (-5, 1));
    assertEquals ("-05", StringHelper.getLeadingZero (-5, 2));
    assertEquals ("-005", StringHelper.getLeadingZero (-5, 3));

    assertEquals ("005", StringHelper.getLeadingZero (5L, 3));
    assertEquals ("0005", StringHelper.getLeadingZero (5L, 4));
    assertEquals ("5", StringHelper.getLeadingZero (5L, 1));
    assertEquals ("56", StringHelper.getLeadingZero (56L, 1));
    assertEquals ("56", StringHelper.getLeadingZero (56L, 2));
    assertEquals ("056", StringHelper.getLeadingZero (56L, 3));
    assertEquals ("0000056", StringHelper.getLeadingZero (56L, 7));
    assertEquals ("0005678", StringHelper.getLeadingZero (5678L, 7));
    assertEquals ("-5", StringHelper.getLeadingZero (-5L, 1));
    assertEquals ("-05", StringHelper.getLeadingZero (-5L, 2));
    assertEquals ("-005", StringHelper.getLeadingZero (-5L, 3));

    assertNull (StringHelper.getLeadingZero ((Byte) null, 5));
    assertEquals ("00013", StringHelper.getLeadingZero (Byte.valueOf ((byte) 13), 5));
    assertNull (StringHelper.getLeadingZero ((Integer) null, 5));
    assertEquals ("00013", StringHelper.getLeadingZero (Integer.valueOf (13), 5));
    assertNull (StringHelper.getLeadingZero ((Long) null, 5));
    assertEquals ("00013", StringHelper.getLeadingZero (Long.valueOf (13), 5));
    assertNull (StringHelper.getLeadingZero ((Short) null, 5));
    assertEquals ("00013", StringHelper.getLeadingZero (Short.valueOf ((short) 13), 5));
  }

  @Test
  public void testHexEncode ()
  {
    try
    {
      // null not allowed
      StringHelper.getHexEncoded (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      // null not allowed
      StringHelper.getHexEncoded (null, 0, 5);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StringHelper.getHexEncoded (new byte [0], -1, 5);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      StringHelper.getHexEncoded (new byte [0], 0, -1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      StringHelper.getHexEncoded (new byte [0], 0, 1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    assertEquals (StringHelper.getHexEncoded (new byte [] {}), "");
    assertEquals (StringHelper.getHexEncoded (new byte [] { 1 }), "01");
    assertEquals (StringHelper.getHexEncoded (new byte [] { 1, 10 }), "010a");
    assertEquals (StringHelper.getHexEncoded (new byte [] { 0, 1, 10, (byte) 255 }), "00010aff");

    // Byte offset
    assertEquals (StringHelper.getHexEncoded (new byte [] { 1, 10 }, 0, 2), "010a");
    assertEquals (StringHelper.getHexEncoded (new byte [] { 1, 10 }, 0, 1), "01");
    assertEquals (StringHelper.getHexEncoded (new byte [] { 1, 10 }, 1, 1), "0a");
    try
    {
      // length is too large
      StringHelper.getHexEncoded (new byte [] { 1, 10 }, 1, 2);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testGetHexValue ()
  {
    assertEquals (0, StringHelper.getHexValue ('0'));
    assertEquals (9, StringHelper.getHexValue ('9'));
    assertEquals (10, StringHelper.getHexValue ('a'));
    assertEquals (10, StringHelper.getHexValue ('A'));
    assertEquals (15, StringHelper.getHexValue ('f'));
    assertEquals (15, StringHelper.getHexValue ('F'));
    assertEquals (-1, StringHelper.getHexValue ('g'));
    assertEquals (-1, StringHelper.getHexValue ('z'));
  }

  @Test
  public void testGetHexChar ()
  {
    assertEquals ('0', StringHelper.getHexChar (0));
    assertEquals ('9', StringHelper.getHexChar (9));
    assertEquals ('a', StringHelper.getHexChar (10));
    assertEquals ('f', StringHelper.getHexChar (15));
    assertEquals ('\0', StringHelper.getHexChar (-1));
    assertEquals ('\0', StringHelper.getHexChar (16));
    assertEquals ('\0', StringHelper.getHexChar (999));
  }

  @Test
  public void testHexDecode ()
  {
    try
    {
      // null not allowed
      StringHelper.getHexDecoded ((String) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      // null not allowed
      StringHelper.getHexDecoded ((char []) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      // odd length
      StringHelper.getHexDecoded ("000");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      // No valid hex char 'g'
      StringHelper.getHexDecoded ("0g");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      // No valid hex char 'g'
      StringHelper.getHexDecoded ("g0");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    for (final String sString : new String [] { "Super", "Hallo", "", "Welt!", "fff" })
      assertEquals (sString,
                    CharsetManager.getAsString (StringHelper.getHexDecoded (StringHelper.getHexEncoded (CharsetManager.getAsBytes (sString,
                                                                                                                                   CCharset.CHARSET_ISO_8859_1_OBJ))),
                                                CCharset.CHARSET_ISO_8859_1_OBJ));

    assertArrayEquals (new byte [] { 0 }, StringHelper.getHexDecoded ("00"));
    assertArrayEquals (new byte [] { 0, 1 }, StringHelper.getHexDecoded ("0001"));
    assertArrayEquals (new byte [] { 0 }, StringHelper.getHexDecoded ("0001".toCharArray (), 0, 2));
    assertArrayEquals (new byte [] { 1 }, StringHelper.getHexDecoded ("0001".toCharArray (), 2, 2));
  }

  @Test
  public void testHexStringByte ()
  {
    assertEquals ("ff", StringHelper.getHexString ((byte) -1));
    assertEquals ("0", StringHelper.getHexString ((byte) 0));
    assertEquals ("9", StringHelper.getHexString ((byte) 9));
    assertEquals ("a", StringHelper.getHexString ((byte) 10));
    assertEquals ("10", StringHelper.getHexString ((byte) 16));
    assertEquals ("ff", StringHelper.getHexString ((byte) 255));
  }

  @Test
  public void testHexStringLeadingZeroByte ()
  {
    assertEquals ("00ff", StringHelper.getHexStringLeadingZero ((byte) -1, 4));
    assertEquals ("0000", StringHelper.getHexStringLeadingZero ((byte) 0, 4));
    assertEquals ("0009", StringHelper.getHexStringLeadingZero ((byte) 9, 4));
    assertEquals ("000a", StringHelper.getHexStringLeadingZero ((byte) 10, 4));
    assertEquals ("0010", StringHelper.getHexStringLeadingZero ((byte) 16, 4));
    assertEquals ("00ff", StringHelper.getHexStringLeadingZero ((byte) 255, 4));
  }

  @Test
  public void testHexStringLeadingZero2Byte ()
  {
    assertEquals ("ff", StringHelper.getHexStringLeadingZero2 ((byte) -1));
    assertEquals ("00", StringHelper.getHexStringLeadingZero2 ((byte) 0));
    assertEquals ("09", StringHelper.getHexStringLeadingZero2 ((byte) 9));
    assertEquals ("0a", StringHelper.getHexStringLeadingZero2 ((byte) 10));
    assertEquals ("10", StringHelper.getHexStringLeadingZero2 ((byte) 16));
    assertEquals ("ff", StringHelper.getHexStringLeadingZero2 ((byte) 255));
  }

  @Test
  public void testHexStringInt ()
  {
    assertEquals ("-10", StringHelper.getHexString (-16));
    assertEquals ("-1", StringHelper.getHexString (-1));
    assertEquals ("0", StringHelper.getHexString (0));
    assertEquals ("9", StringHelper.getHexString (9));
    assertEquals ("a", StringHelper.getHexString (10));
    assertEquals ("10", StringHelper.getHexString (16));
    assertEquals ("ff", StringHelper.getHexString (255));
    assertEquals ("ffff", StringHelper.getHexString (65535));
  }

  @Test
  public void testHexStringLeadingZeroInt ()
  {
    assertEquals ("-10", StringHelper.getHexStringLeadingZero (-16, 2));
    assertEquals ("-1", StringHelper.getHexStringLeadingZero (-1, 2));
    assertEquals ("00", StringHelper.getHexStringLeadingZero (0, 2));
    assertEquals ("09", StringHelper.getHexStringLeadingZero (9, 2));
    assertEquals ("00a", StringHelper.getHexStringLeadingZero (10, 3));
    assertEquals ("010", StringHelper.getHexStringLeadingZero (16, 3));
    assertEquals ("00ff", StringHelper.getHexStringLeadingZero (255, 4));
    assertEquals ("ffff", StringHelper.getHexStringLeadingZero (65535, 4));
    assertEquals ("ffff", StringHelper.getHexStringLeadingZero (65535, 0));
  }

  @Test
  public void testHexStringLong ()
  {
    assertEquals ("-10", StringHelper.getHexString (-16L));
    assertEquals ("-1", StringHelper.getHexString (-1L));
    assertEquals ("0", StringHelper.getHexString (0L));
    assertEquals ("9", StringHelper.getHexString (9L));
    assertEquals ("a", StringHelper.getHexString (10L));
    assertEquals ("10", StringHelper.getHexString (16L));
    assertEquals ("ff", StringHelper.getHexString (255L));
    assertEquals ("ffff", StringHelper.getHexString (65535L));
    assertEquals ("ffff0000", StringHelper.getHexString (65536L * 65535L));
  }

  @Test
  public void testHexStringLeadingZeroLong ()
  {
    assertEquals ("-10", StringHelper.getHexStringLeadingZero (-16L, 2));
    assertEquals ("-01", StringHelper.getHexStringLeadingZero (-1L, 3));
    assertEquals ("000", StringHelper.getHexStringLeadingZero (0L, 3));
    assertEquals ("09", StringHelper.getHexStringLeadingZero (9L, 2));
    assertEquals ("00a", StringHelper.getHexStringLeadingZero (10L, 3));
    assertEquals ("00ff", StringHelper.getHexStringLeadingZero (255L, 4));
    assertEquals ("ffff", StringHelper.getHexStringLeadingZero (65535L, 4));
    assertEquals ("0000ffff", StringHelper.getHexStringLeadingZero (65535L, 8));
    assertEquals ("ffff0000", StringHelper.getHexStringLeadingZero (65536L * 65535L, 5));
  }

  @Test
  public void testHexStringShort ()
  {
    assertEquals ("fffe", StringHelper.getHexString ((short) -2));
    assertEquals ("ffff", StringHelper.getHexString ((short) -1));
    assertEquals ("0", StringHelper.getHexString ((short) 0));
    assertEquals ("9", StringHelper.getHexString ((short) 9));
    assertEquals ("a", StringHelper.getHexString ((short) 10));
    assertEquals ("10", StringHelper.getHexString ((short) 16));
    assertEquals ("ff", StringHelper.getHexString ((short) 255));
  }

  @Test
  public void testHexStringLeadingZeroShort ()
  {
    assertEquals ("0ffff", StringHelper.getHexStringLeadingZero ((short) -1, 5));
    assertEquals ("0000", StringHelper.getHexStringLeadingZero ((short) 0, 4));
    assertEquals ("0009", StringHelper.getHexStringLeadingZero ((short) 9, 4));
    assertEquals ("000a", StringHelper.getHexStringLeadingZero ((short) 10, 4));
    assertEquals ("0010", StringHelper.getHexStringLeadingZero ((short) 16, 4));
    assertEquals ("00ff", StringHelper.getHexStringLeadingZero ((short) 255, 4));
  }

  @Test
  public void testGetLeadingWhitespaceCount ()
  {
    assertEquals (0, StringHelper.getLeadingWhitespaceCount ("Hallo Welt"));
    assertEquals (1, StringHelper.getLeadingWhitespaceCount (" Hallo Welt"));
    assertEquals (2, StringHelper.getLeadingWhitespaceCount ("  Hallo Welt"));
    assertEquals (2, StringHelper.getLeadingWhitespaceCount ("\t\tHallo Welt"));
    assertEquals (2, StringHelper.getLeadingWhitespaceCount ("  "));
    assertEquals (0, StringHelper.getLeadingWhitespaceCount (""));
    assertEquals (0, StringHelper.getLeadingWhitespaceCount (null));
  }

  @Test
  public void testGetTrailingWhitespaceCount ()
  {
    assertEquals (0, StringHelper.getTrailingWhitespaceCount ("Hallo Welt"));
    assertEquals (1, StringHelper.getTrailingWhitespaceCount (" Hallo Welt "));
    assertEquals (2, StringHelper.getTrailingWhitespaceCount ("  Hallo Welt  "));
    assertEquals (2, StringHelper.getTrailingWhitespaceCount ("\t\tHallo Welt\t\t"));
    assertEquals (2, StringHelper.getTrailingWhitespaceCount ("  "));
    assertEquals (0, StringHelper.getTrailingWhitespaceCount (""));
    assertEquals (0, StringHelper.getTrailingWhitespaceCount (null));
  }

  @Test
  public void testGetLeadingCharCount ()
  {
    assertEquals (0, StringHelper.getLeadingCharCount ("Hallo Welt", 'x'));
    assertEquals (1, StringHelper.getLeadingCharCount ("xHallo Welt", 'x'));
    assertEquals (2, StringHelper.getLeadingCharCount ("xxHallo Welt", 'x'));
    assertEquals (2, StringHelper.getLeadingCharCount ("xx", 'x'));
    assertEquals (0, StringHelper.getLeadingCharCount ("", 'x'));
    assertEquals (0, StringHelper.getLeadingCharCount (null, 'x'));
  }

  @Test
  public void testGetTrailingCharCount ()
  {
    assertEquals (0, StringHelper.getTrailingCharCount ("Hallo Welt", 'x'));
    assertEquals (1, StringHelper.getTrailingCharCount (" Hallo Weltx", 'x'));
    assertEquals (2, StringHelper.getTrailingCharCount ("  Hallo Weltxx", 'x'));
    assertEquals (2, StringHelper.getTrailingCharCount ("xx", 'x'));
    assertEquals (0, StringHelper.getTrailingCharCount ("", 'x'));
    assertEquals (0, StringHelper.getTrailingCharCount (null, 'x'));
  }

  @Test
  public void testImplodeIterable ()
  {
    final List <String> aList = CollectionHelper.newList ("a", "b", "c");
    assertEquals ("", StringHelper.getImploded (".", (String []) null));
    assertEquals ("", StringHelper.getImploded (".", (List <String>) null));
    assertEquals ("a.b.c", StringHelper.getImploded (".", aList));
    assertEquals ("abc", StringHelper.getImploded ("", aList));
    assertEquals ("abc", StringHelper.getImploded (aList));
    assertEquals ("a.b.c", StringHelper.getImploded (".", aList.toArray (new String [3])));
    assertEquals ("abc", StringHelper.getImploded ("", aList.toArray (new String [3])));
    assertEquals ("abc", StringHelper.getImploded (aList.toArray (new String [3])));

    try
    {
      StringHelper.getImploded (null, aList);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  @SuppressFBWarnings ("TQ_NEVER_VALUE_USED_WHERE_ALWAYS_REQUIRED")
  public void testImplodeArray ()
  {
    final String [] aArray = new String [] { "a", "b", "c" };
    assertEquals ("a.b", StringHelper.getImploded (".", aArray, 0, 2));
    assertEquals ("ab", StringHelper.getImploded (aArray, 0, 2));
    assertEquals ("b.c", StringHelper.getImploded (".", aArray, 1, 2));
    assertEquals ("bc", StringHelper.getImploded (aArray, 1, 2));
    assertEquals ("", StringHelper.getImploded (".", aArray, 0, 0));
    assertEquals ("", StringHelper.getImploded (aArray, 0, 0));
    assertEquals ("", StringHelper.getImploded (".", aArray, 2, 0));
    assertEquals ("", StringHelper.getImploded (aArray, 2, 0));
    assertEquals ("", StringHelper.getImploded (".", null, 2, 0));
    assertEquals ("", StringHelper.getImploded (null, 2, 0));

    try
    {
      // null separator
      StringHelper.getImploded (null, aArray);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      // null separator
      StringHelper.getImploded (null, aArray, 2, 2);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StringHelper.getImploded (".", aArray, -1, 2);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      StringHelper.getImploded (aArray, -1, 2);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      StringHelper.getImploded (".", aArray, 0, -1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      StringHelper.getImploded (aArray, 0, -1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      // too long
      StringHelper.getImploded (".", aArray, 2, 2);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      // too long
      StringHelper.getImploded (aArray, 2, 2);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      // too long
      StringHelper.getImploded (".", aArray, 0, 4);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      // too long
      StringHelper.getImploded (aArray, 0, 4);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testImplodeMap ()
  {
    final Map <String, String> aMap = CollectionHelper.newOrderedMap ("a", "true", "b", "true", "c", "false");
    assertEquals ("atruebtruecfalse", StringHelper.getImploded ("", "", aMap));
    assertEquals ("atrue,btrue,cfalse", StringHelper.getImploded (",", "", aMap));
    assertEquals ("a,trueb,truec,false", StringHelper.getImploded ("", ",", aMap));
    assertEquals ("a,true,b,true,c,false", StringHelper.getImploded (",", ",", aMap));
    assertEquals ("a:true,b:true,c:false", StringHelper.getImploded (",", ":", aMap));

    try
    {
      StringHelper.getImploded (null, ":", aMap);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StringHelper.getImploded (",", null, aMap);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testGetImplodedNonEmptyIterable ()
  {
    final List <String> aList = CollectionHelper.newList (null, "a", "", "b", null, "c", "");
    assertEquals ("", StringHelper.getImplodedNonEmpty (".", (String []) null));
    assertEquals ("", StringHelper.getImplodedNonEmpty (".", (List <String>) null));
    assertEquals ("a.b.c", StringHelper.getImplodedNonEmpty (".", aList));
    assertEquals ("abc", StringHelper.getImplodedNonEmpty ("", aList));
    assertEquals ("a.b.c", StringHelper.getImplodedNonEmpty (".", aList.toArray (new String [3])));
    assertEquals ("abc", StringHelper.getImplodedNonEmpty ("", aList.toArray (new String [3])));

    try
    {
      StringHelper.getImplodedNonEmpty (null, aList);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  @SuppressFBWarnings ("TQ_NEVER_VALUE_USED_WHERE_ALWAYS_REQUIRED")
  public void testGetImplodedNonEmptyArray ()
  {
    final String [] aArray = new String [] { null, "a", "", "b", null, "c", "" };
    assertEquals ("a.b", StringHelper.getImplodedNonEmpty (".", aArray, 0, 4));
    assertEquals ("b.c", StringHelper.getImplodedNonEmpty (".", aArray, 2, 4));
    assertEquals ("", StringHelper.getImplodedNonEmpty (".", aArray, 0, 0));
    assertEquals ("", StringHelper.getImplodedNonEmpty (".", aArray, 4, 0));
    assertEquals ("", StringHelper.getImplodedNonEmpty (".", null, 4, 0));

    try
    {
      StringHelper.getImplodedNonEmpty (null, aArray, 2, 2);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StringHelper.getImplodedNonEmpty (".", aArray, -1, 2);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      StringHelper.getImplodedNonEmpty (".", aArray, 0, -1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      // too long
      StringHelper.getImplodedNonEmpty (".", aArray, 6, 2);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      // too long
      StringHelper.getImplodedNonEmpty (".", aArray, 0, 8);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      StringHelper.getImplodedNonEmpty (null, aArray);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testGetExplodedToList ()
  {
    List <String> ret = StringHelper.getExploded ("@", "a@b@@c");
    assertEquals (CollectionHelper.newList ("a", "b", "", "c"), ret);
    ret = StringHelper.getExploded ("uu", "auubuuuuuuc");
    assertEquals (CollectionHelper.newList ("a", "b", "", "", "c"), ret);
    ret = StringHelper.getExploded (".", "a.b...c");
    assertEquals (CollectionHelper.newList ("a", "b", "", "", "c"), ret);
    ret = StringHelper.getExploded ("o", "boo:and:foo");
    assertEquals (CollectionHelper.newList ("b", "", ":and:f", "", ""), ret);
    ret = StringHelper.getExploded ("@", "@a@b@@c");
    assertEquals (CollectionHelper.newList ("", "a", "b", "", "c"), ret);
    ret = StringHelper.getExploded ("@", "a@b@@c@");
    assertEquals (CollectionHelper.newList ("a", "b", "", "c", ""), ret);
    ret = StringHelper.getExploded ("@", "@a@b@@c@");
    assertEquals (CollectionHelper.newList ("", "a", "b", "", "c", ""), ret);
    assertTrue (StringHelper.getExploded ("@", null).isEmpty ());

    try
    {
      StringHelper.getExploded (null, "@a@b@@c@");
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testGetExplodedToListWithMax ()
  {
    assertEquals (CollectionHelper.newList ("a", "b", "", "c"), StringHelper.getExploded ("@", "a@b@@c", 5));
    assertEquals (CollectionHelper.newList ("a", "b", "", "c"), StringHelper.getExploded ("@", "a@b@@c", 4));
    assertEquals (CollectionHelper.newList ("a", "b", "@c"), StringHelper.getExploded ("@", "a@b@@c", 3));
    assertEquals (CollectionHelper.newList ("a", "b@@c"), StringHelper.getExploded ("@", "a@b@@c", 2));
    assertEquals (CollectionHelper.newList ("a@b@@c"), StringHelper.getExploded ("@", "a@b@@c", 1));
    assertEquals (CollectionHelper.newList ("a", "b", "", "c"), StringHelper.getExploded ("@", "a@b@@c", 0));
    assertEquals (CollectionHelper.newList ("a", "b", "", "c"), StringHelper.getExploded ("@", "a@b@@c", -1));
    assertEquals (CollectionHelper.newList ("a", "b", "", "c"), StringHelper.getExploded ("@", "a@b@@c", -2));
    assertTrue (StringHelper.getExploded ("@", null, 5).isEmpty ());
  }

  @Test
  public void testGetExplodedArray ()
  {
    assertArrayEquals (new String [] { "a", "b", "", "c" }, StringHelper.getExplodedArray ('@', "a@b@@c", 5));
    assertArrayEquals (new String [] { "a", "b", "", "c" }, StringHelper.getExplodedArray ('@', "a@b@@c", 4));
    assertArrayEquals (new String [] { "a", "b", "@c" }, StringHelper.getExplodedArray ('@', "a@b@@c", 3));
    assertArrayEquals (new String [] { "a", "b@@c" }, StringHelper.getExplodedArray ('@', "a@b@@c", 2));
    assertArrayEquals (new String [] { "a@b@@c" }, StringHelper.getExplodedArray ('@', "a@b@@c", 1));
    assertArrayEquals (new String [] { "a", "b", "", "c" }, StringHelper.getExplodedArray ('@', "a@b@@c", 0));
    assertArrayEquals (new String [] { "a", "b", "", "c" }, StringHelper.getExplodedArray ('@', "a@b@@c", -1));
    assertArrayEquals (new String [] { "a", "b", "", "c" }, StringHelper.getExplodedArray ('@', "a@b@@c", -2));
    assertTrue (StringHelper.getExplodedArray ('@', null, 5).length == 0);
  }

  @Test
  public void testExplodeToSet ()
  {
    Set <String> ret = StringHelper.getExplodedToSet ("@", "a@b@@c");
    assertEquals (CollectionHelper.newSet ("a", "b", "", "c"), ret);
    ret = StringHelper.getExplodedToSet ("uu", "auubuuuuuuc");
    assertEquals (CollectionHelper.newSet ("a", "b", "", "", "c"), ret);
    ret = StringHelper.getExplodedToSet (".", "a.b...c");
    assertEquals (CollectionHelper.newSet ("a", "b", "", "", "c"), ret);
    ret = StringHelper.getExplodedToSet ("o", "boo:and:foo");
    assertEquals (CollectionHelper.newSet ("b", "", ":and:f", "", ""), ret);
    ret = StringHelper.getExplodedToSet ("@", "@a@b@@c");
    assertEquals (CollectionHelper.newSet ("", "a", "b", "", "c"), ret);
    ret = StringHelper.getExplodedToSet ("@", "a@b@@c@");
    assertEquals (CollectionHelper.newSet ("a", "b", "", "c", ""), ret);
    ret = StringHelper.getExplodedToSet ("@", "@a@b@@c@");
    assertEquals (CollectionHelper.newSet ("", "a", "b", "", "c", ""), ret);
    assertTrue (StringHelper.getExplodedToSet ("@", null).isEmpty ());

    try
    {
      StringHelper.getExplodedToSet (null, "@a@b@@c@");
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testExplodeToOrderedSet ()
  {
    Set <String> ret = StringHelper.getExplodedToOrderedSet ("@", "a@b@@c");
    assertEquals (CollectionHelper.newSet ("a", "b", "", "c"), ret);
    ret = StringHelper.getExplodedToOrderedSet ("uu", "auubuuuuuuc");
    assertEquals (CollectionHelper.newSet ("a", "b", "", "", "c"), ret);
    ret = StringHelper.getExplodedToOrderedSet (".", "a.b...c");
    assertEquals (CollectionHelper.newSet ("a", "b", "", "", "c"), ret);
    ret = StringHelper.getExplodedToOrderedSet ("o", "boo:and:foo");
    assertEquals (CollectionHelper.newSet ("b", "", ":and:f", "", ""), ret);
    ret = StringHelper.getExplodedToOrderedSet ("@", "@a@b@@c");
    assertEquals (CollectionHelper.newSet ("", "a", "b", "", "c"), ret);
    ret = StringHelper.getExplodedToOrderedSet ("@", "a@b@@c@");
    assertEquals (CollectionHelper.newSet ("a", "b", "", "c", ""), ret);
    ret = StringHelper.getExplodedToOrderedSet ("@", "@a@b@@c@");
    assertEquals (CollectionHelper.newSet ("", "a", "b", "", "c", ""), ret);
    assertTrue (StringHelper.getExplodedToOrderedSet ("@", null).isEmpty ());

    try
    {
      StringHelper.getExplodedToOrderedSet (null, "@a@b@@c@");
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  @SuppressFBWarnings ("TQ_NEVER_VALUE_USED_WHERE_ALWAYS_REQUIRED")
  public void testGetRepeated ()
  {
    assertEquals ("", StringHelper.getRepeated ('a', 0));
    assertEquals ("a", StringHelper.getRepeated ('a', 1));
    assertEquals ("aaa", StringHelper.getRepeated ('a', 3));
    assertEquals ("  ", StringHelper.getRepeated (' ', 2));
    try
    {
      StringHelper.getRepeated (' ', -1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    assertEquals ("", StringHelper.getRepeated ("a", 0));
    assertEquals ("a", StringHelper.getRepeated ("a", 1));
    assertEquals ("aaa", StringHelper.getRepeated ("a", 3));
    assertEquals ("ababab", StringHelper.getRepeated ("ab", 3));
    assertEquals ("  ", StringHelper.getRepeated (" ", 2));
    try
    {
      StringHelper.getRepeated (null, 5);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StringHelper.getRepeated (" ", -1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    // Check overflow
    try
    {
      StringHelper.getRepeated ("  ", Integer.MAX_VALUE);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testConcatenateOnDemand ()
  {
    assertEquals ("abc", StringHelper.getConcatenatedOnDemand ("a", "b", "c"));
    assertEquals ("a", StringHelper.getConcatenatedOnDemand ("a", "b", null));
    assertEquals ("a", StringHelper.getConcatenatedOnDemand ("a", "b", ""));
    assertEquals ("c", StringHelper.getConcatenatedOnDemand (null, "b", "c"));
    assertEquals ("c", StringHelper.getConcatenatedOnDemand ("", "b", "c"));
    assertEquals ("ac", StringHelper.getConcatenatedOnDemand ("a", "", "c"));
    assertEquals ("ac", StringHelper.getConcatenatedOnDemand ("a", null, "c"));
    assertEquals ("bc", StringHelper.getConcatenatedOnDemand (null, null, "bc"));
    assertEquals ("", StringHelper.getConcatenatedOnDemand (null, null, null));
    assertEquals ("", StringHelper.getConcatenatedOnDemand ("", "", ""));

    assertEquals ("a", StringHelper.getConcatenatedOnDemand ("a", null));
    assertEquals ("a", StringHelper.getConcatenatedOnDemand ("a", ""));
    assertEquals ("b", StringHelper.getConcatenatedOnDemand (null, "b"));
    assertEquals ("b", StringHelper.getConcatenatedOnDemand ("", "b"));
    assertEquals ("ab", StringHelper.getConcatenatedOnDemand ("a", "b"));
    assertEquals ("", StringHelper.getConcatenatedOnDemand (null, null));
    assertEquals ("", StringHelper.getConcatenatedOnDemand ("", ""));
  }

  @Test
  public void testStartsWithChar ()
  {
    assertTrue (StringHelper.startsWith ("abc", 'a'));
    assertFalse (StringHelper.startsWith ("abc", 'b'));
    assertTrue (StringHelper.startsWith ("a", 'a'));
    assertFalse (StringHelper.startsWith ("", 'a'));
    assertFalse (StringHelper.startsWith (null, 'a'));

    final char [] aStart = new char [] { 'a', 'b', 'c' };
    assertTrue (StringHelper.startsWithAny ("abc", aStart));
    assertTrue (StringHelper.startsWithAny ("bbc", aStart));
    assertTrue (StringHelper.startsWithAny ("ccc", aStart));
    assertFalse (StringHelper.startsWithAny ("def", aStart));
    assertFalse (StringHelper.startsWithAny ("daabbcc", aStart));
    assertTrue (StringHelper.startsWithAny ("a", aStart));
    assertFalse (StringHelper.startsWithAny ("", aStart));
    assertFalse (StringHelper.startsWithAny (null, aStart));
    assertFalse (StringHelper.startsWithAny ("a", (char []) null));
    assertFalse (StringHelper.startsWithAny ("a", new char [0]));

    assertTrue (StringHelper.startsWithIgnoreCase ("abc", 'a'));
    assertFalse (StringHelper.startsWithIgnoreCase ("abc", 'b'));
    assertTrue (StringHelper.startsWithIgnoreCase ("a", 'a'));
    assertFalse (StringHelper.startsWithIgnoreCase ("", 'a'));
    assertFalse (StringHelper.startsWithIgnoreCase (null, 'a'));

    assertTrue (StringHelper.startsWithIgnoreCase ("ABC", 'a'));
    assertFalse (StringHelper.startsWithIgnoreCase ("ABC", 'b'));
    assertTrue (StringHelper.startsWithIgnoreCase ("A", 'a'));
    assertFalse (StringHelper.startsWithIgnoreCase ("", 'a'));
    assertFalse (StringHelper.startsWithIgnoreCase (null, 'a'));

    assertTrue (StringHelper.startsWithIgnoreCase ("abc", 'A'));
    assertFalse (StringHelper.startsWithIgnoreCase ("abc", 'B'));
    assertTrue (StringHelper.startsWithIgnoreCase ("a", 'A'));
    assertFalse (StringHelper.startsWithIgnoreCase ("", 'A'));
    assertFalse (StringHelper.startsWithIgnoreCase (null, 'A'));
  }

  @Test
  public void testStartsWithString ()
  {
    assertTrue (StringHelper.startsWith ("abc", "a"));
    assertTrue (StringHelper.startsWith ("abc", "ab"));
    assertTrue (StringHelper.startsWith ("abc", "abc"));
    assertFalse (StringHelper.startsWith ("abc", "b"));
    assertTrue (StringHelper.startsWith ("a", "a"));
    assertFalse (StringHelper.startsWith ("", "a"));
    assertFalse (StringHelper.startsWith (null, "a"));
    assertFalse (StringHelper.startsWith ("a", null));

    assertTrue (StringHelper.startsWith ("abc", ""));
    assertTrue (StringHelper.startsWith ("", ""));
    assertFalse (StringHelper.startsWith (null, ""));

    assertFalse (StringHelper.startsWith (null, null));
    assertTrue (StringHelper.startsWith ("", ""));

    assertFalse (StringHelper.startsWithIgnoreCase (null, null));
    assertTrue (StringHelper.startsWithIgnoreCase ("", ""));

    assertTrue (StringHelper.startsWithIgnoreCase ("abc", "a"));
    assertTrue (StringHelper.startsWithIgnoreCase ("abc", "ab"));
    assertTrue (StringHelper.startsWithIgnoreCase ("abc", "abc"));
    assertFalse (StringHelper.startsWithIgnoreCase ("abc", "b"));
    assertTrue (StringHelper.startsWithIgnoreCase ("a", "a"));
    assertFalse (StringHelper.startsWithIgnoreCase ("", "a"));
    assertFalse (StringHelper.startsWithIgnoreCase (null, "a"));
    assertFalse (StringHelper.startsWithIgnoreCase ("a", null));

    assertTrue (StringHelper.startsWithIgnoreCase ("ABC", "a"));
    assertTrue (StringHelper.startsWithIgnoreCase ("ABC", "ab"));
    assertTrue (StringHelper.startsWithIgnoreCase ("ABC", "abc"));
    assertFalse (StringHelper.startsWithIgnoreCase ("ABC", "b"));
    assertTrue (StringHelper.startsWithIgnoreCase ("A", "a"));
    assertFalse (StringHelper.startsWithIgnoreCase ("", "a"));
    assertFalse (StringHelper.startsWithIgnoreCase (null, "a"));
    assertFalse (StringHelper.startsWithIgnoreCase ("A", null));

    assertTrue (StringHelper.startsWithIgnoreCase ("abc", "A"));
    assertTrue (StringHelper.startsWithIgnoreCase ("abc", "AB"));
    assertTrue (StringHelper.startsWithIgnoreCase ("abc", "ABC"));
    assertFalse (StringHelper.startsWithIgnoreCase ("abc", "B"));
    assertTrue (StringHelper.startsWithIgnoreCase ("a", "A"));
    assertFalse (StringHelper.startsWithIgnoreCase ("", "A"));
    assertFalse (StringHelper.startsWithIgnoreCase (null, "A"));
    assertFalse (StringHelper.startsWithIgnoreCase ("a", null));
  }

  @Test
  public void testEndsWithChar ()
  {
    assertTrue (StringHelper.endsWith ("abc", 'c'));
    assertFalse (StringHelper.endsWith ("abc", 'b'));
    assertTrue (StringHelper.endsWith ("a", 'a'));
    assertFalse (StringHelper.endsWith ("", 'a'));
    assertFalse (StringHelper.endsWith (null, 'a'));
    assertFalse (StringHelper.endsWith (null, null));
    assertTrue (StringHelper.endsWith ("", ""));

    final char [] aEnd = new char [] { 'a', 'b', 'c' };
    assertTrue (StringHelper.endsWithAny ("abc", aEnd));
    assertTrue (StringHelper.endsWithAny ("aab", aEnd));
    assertTrue (StringHelper.endsWithAny ("aaa", aEnd));
    assertFalse (StringHelper.endsWithAny ("aad", aEnd));
    assertTrue (StringHelper.endsWithAny ("a", aEnd));
    assertFalse (StringHelper.endsWithAny ("", aEnd));
    assertFalse (StringHelper.endsWithAny (null, aEnd));
    assertFalse (StringHelper.endsWithAny ("a", (char []) null));
    assertFalse (StringHelper.endsWithAny ("a", new char [0]));

    assertFalse (StringHelper.endsWithIgnoreCase (null, null));
    assertTrue (StringHelper.endsWithIgnoreCase ("", ""));

    assertTrue (StringHelper.endsWithIgnoreCase ("abc", 'c'));
    assertFalse (StringHelper.endsWithIgnoreCase ("abc", 'b'));
    assertTrue (StringHelper.endsWithIgnoreCase ("a", 'a'));
    assertFalse (StringHelper.endsWithIgnoreCase ("", 'a'));
    assertFalse (StringHelper.endsWithIgnoreCase (null, 'a'));

    assertTrue (StringHelper.endsWithIgnoreCase ("ABC", 'c'));
    assertFalse (StringHelper.endsWithIgnoreCase ("ABC", 'b'));
    assertTrue (StringHelper.endsWithIgnoreCase ("A", 'a'));
    assertFalse (StringHelper.endsWithIgnoreCase ("", 'a'));
    assertFalse (StringHelper.endsWithIgnoreCase (null, 'a'));

    assertTrue (StringHelper.endsWithIgnoreCase ("abc", 'C'));
    assertFalse (StringHelper.endsWithIgnoreCase ("abc", 'B'));
    assertTrue (StringHelper.endsWithIgnoreCase ("a", 'A'));
    assertFalse (StringHelper.endsWithIgnoreCase ("", 'A'));
    assertFalse (StringHelper.endsWithIgnoreCase (null, 'A'));
  }

  @Test
  public void testEndsWithString ()
  {
    assertTrue (StringHelper.endsWith ("abc", "c"));
    assertTrue (StringHelper.endsWith ("abc", "bc"));
    assertTrue (StringHelper.endsWith ("abc", "abc"));
    assertFalse (StringHelper.endsWith ("abc", "b"));
    assertTrue (StringHelper.endsWith ("a", "a"));
    assertFalse (StringHelper.endsWith ("", "a"));
    assertFalse (StringHelper.endsWith (null, "a"));
    assertFalse (StringHelper.endsWith ("a", null));

    assertTrue (StringHelper.endsWithIgnoreCase ("abc", "c"));
    assertTrue (StringHelper.endsWithIgnoreCase ("abc", "bc"));
    assertTrue (StringHelper.endsWithIgnoreCase ("abc", "abc"));
    assertFalse (StringHelper.endsWithIgnoreCase ("abc", "b"));
    assertTrue (StringHelper.endsWithIgnoreCase ("a", "a"));
    assertFalse (StringHelper.endsWithIgnoreCase ("", "a"));
    assertFalse (StringHelper.endsWithIgnoreCase (null, "a"));
    assertFalse (StringHelper.endsWithIgnoreCase ("a", null));

    assertTrue (StringHelper.endsWithIgnoreCase ("ABC", "c"));
    assertTrue (StringHelper.endsWithIgnoreCase ("ABC", "bc"));
    assertTrue (StringHelper.endsWithIgnoreCase ("ABC", "abc"));
    assertFalse (StringHelper.endsWithIgnoreCase ("ABC", "b"));
    assertTrue (StringHelper.endsWithIgnoreCase ("A", "a"));
    assertFalse (StringHelper.endsWithIgnoreCase ("", "a"));
    assertFalse (StringHelper.endsWithIgnoreCase (null, "a"));
    assertFalse (StringHelper.endsWithIgnoreCase ("A", null));

    assertTrue (StringHelper.endsWithIgnoreCase ("abc", "C"));
    assertTrue (StringHelper.endsWithIgnoreCase ("abc", "BC"));
    assertTrue (StringHelper.endsWithIgnoreCase ("abc", "ABC"));
    assertFalse (StringHelper.endsWithIgnoreCase ("abc", "B"));
    assertTrue (StringHelper.endsWithIgnoreCase ("a", "A"));
    assertFalse (StringHelper.endsWithIgnoreCase ("", "A"));
    assertFalse (StringHelper.endsWithIgnoreCase (null, "A"));
    assertFalse (StringHelper.endsWithIgnoreCase ("a", null));
  }

  @Test
  public void testGetIndexOfString ()
  {
    assertEquals (-1, StringHelper.getIndexOf (null, null));
    assertEquals (-1, StringHelper.getIndexOf (null, "a"));
    assertEquals (-1, StringHelper.getIndexOf ("b", null));
    assertEquals (-1, StringHelper.getIndexOf ("b", "cd"));
    assertEquals (-1, StringHelper.getIndexOf ("bla fob", "z"));
    assertEquals (0, StringHelper.getIndexOf ("bla fob", "b"));
    assertEquals (2, StringHelper.getIndexOf ("bla fob", "a"));
  }

  @Test
  public void testGetLastIndexOfString ()
  {
    assertEquals (-1, StringHelper.getLastIndexOf (null, null));
    assertEquals (-1, StringHelper.getLastIndexOf (null, "a"));
    assertEquals (-1, StringHelper.getLastIndexOf ("b", null));
    assertEquals (-1, StringHelper.getLastIndexOf ("b", "cd"));
    assertEquals (-1, StringHelper.getLastIndexOf ("bla fob", "z"));
    assertEquals (6, StringHelper.getLastIndexOf ("bla fob", "b"));
    assertEquals (2, StringHelper.getLastIndexOf ("bla fob", "a"));
  }

  @Test
  public void testGetIndexOfChar ()
  {
    assertEquals (-1, StringHelper.getIndexOf (null, '\0'));
    assertEquals (-1, StringHelper.getIndexOf (null, 'a'));
    assertEquals (-1, StringHelper.getIndexOf ("b", '\0'));
    assertEquals (-1, StringHelper.getIndexOf ("b", 'c'));
    assertEquals (-1, StringHelper.getIndexOf ("bla fob", 'z'));
    assertEquals (0, StringHelper.getIndexOf ("bla fob", 'b'));
    assertEquals (2, StringHelper.getIndexOf ("bla fob", 'a'));
  }

  @Test
  public void testGetLastIndexOfChar ()
  {
    assertEquals (-1, StringHelper.getLastIndexOf (null, '\0'));
    assertEquals (-1, StringHelper.getLastIndexOf (null, 'a'));
    assertEquals (-1, StringHelper.getLastIndexOf ("b", '\0'));
    assertEquals (-1, StringHelper.getLastIndexOf ("b", 'c'));
    assertEquals (-1, StringHelper.getLastIndexOf ("bla fob", 'z'));
    assertEquals (6, StringHelper.getLastIndexOf ("bla fob", 'b'));
    assertEquals (2, StringHelper.getLastIndexOf ("bla fob", 'a'));
  }

  @Test
  public void testContainsString ()
  {
    assertTrue (StringHelper.contains ("Test", "Test"));
    assertTrue (StringHelper.contains ("Test", "est"));
    assertTrue (StringHelper.contains ("Test", "Tes"));
    assertTrue (StringHelper.contains ("Test", "es"));
    assertTrue (StringHelper.contains ("Test", ""));

    assertFalse (StringHelper.contains ("Test", null));
    assertFalse (StringHelper.contains (null, "Test"));
    assertFalse (StringHelper.contains ("Tes", "Test"));
    assertFalse (StringHelper.contains ("est", "Test"));
    assertFalse (StringHelper.contains ("es", "Test"));
    assertFalse (StringHelper.contains ("", "Test"));

    assertFalse (StringHelper.contains ("Test", "TEST"));
    assertFalse (StringHelper.contains ("Test", "EST"));
    assertFalse (StringHelper.contains ("Test", "TES"));
    assertFalse (StringHelper.contains ("Test", "ES"));
  }

  @Test
  public void testContainsChar ()
  {
    assertTrue (StringHelper.contains ("Test", 'T'));
    assertTrue (StringHelper.contains ("Test", 'e'));
    assertTrue (StringHelper.contains ("Test", 's'));
    assertTrue (StringHelper.contains ("Test", 't'));
    assertFalse (StringHelper.contains ("Test", '\0'));

    assertFalse (StringHelper.contains ("Test", null));
    assertFalse (StringHelper.contains (null, 'T'));
  }

  @Test
  public void testIndexOfIgnoreCaseString ()
  {
    assertEquals (-1, StringHelper.getIndexOfIgnoreCase (null, null, L_DE));
    assertEquals (-1, StringHelper.getIndexOfIgnoreCase (null, "a", L_DE));
    assertEquals (-1, StringHelper.getIndexOfIgnoreCase ("b", null, L_DE));
    assertEquals (-1, StringHelper.getIndexOfIgnoreCase ("b", "cd", L_DE));
    assertEquals (-1, StringHelper.getIndexOfIgnoreCase ("bla foo", "z", L_DE));
    assertEquals (0, StringHelper.getIndexOfIgnoreCase ("bla foo", "b", L_DE));
    assertEquals (2, StringHelper.getIndexOfIgnoreCase ("bla foo", "a", L_DE));
    assertEquals (0, StringHelper.getIndexOfIgnoreCase ("bla foo", "B", L_DE));
    assertEquals (2, StringHelper.getIndexOfIgnoreCase ("bla foo", "A", L_DE));
    assertEquals (0, StringHelper.getIndexOfIgnoreCase ("BLA FOO", "b", L_DE));
    assertEquals (2, StringHelper.getIndexOfIgnoreCase ("BLA FOO", "a", L_DE));
  }

  @Test
  public void testContainsIgnoreCaseString ()
  {
    final Locale aLocale = Locale.ENGLISH;
    assertTrue (StringHelper.containsIgnoreCase ("Test", "Test", aLocale));
    assertTrue (StringHelper.containsIgnoreCase ("Test", "est", aLocale));
    assertTrue (StringHelper.containsIgnoreCase ("Test", "Tes", aLocale));
    assertTrue (StringHelper.containsIgnoreCase ("Test", "es", aLocale));
    assertTrue (StringHelper.containsIgnoreCase ("Test", "", aLocale));

    assertFalse (StringHelper.containsIgnoreCase ("Test", null, aLocale));
    assertFalse (StringHelper.containsIgnoreCase (null, "Test", aLocale));
    assertFalse (StringHelper.containsIgnoreCase ("Tes", "Test", aLocale));
    assertFalse (StringHelper.containsIgnoreCase ("est", "Test", aLocale));
    assertFalse (StringHelper.containsIgnoreCase ("es", "Test", aLocale));
    assertFalse (StringHelper.containsIgnoreCase ("", "Test", aLocale));

    assertTrue (StringHelper.containsIgnoreCase ("Test", "TEST", aLocale));
    assertTrue (StringHelper.containsIgnoreCase ("Test", "EST", aLocale));
    assertTrue (StringHelper.containsIgnoreCase ("Test", "TES", aLocale));
    assertTrue (StringHelper.containsIgnoreCase ("Test", "ES", aLocale));
  }

  @Test
  public void testGetOccurrenceCountString ()
  {
    assertEquals (0, StringHelper.getOccurrenceCount ("Test", null));
    assertEquals (0, StringHelper.getOccurrenceCount (null, "Test"));
    assertEquals (1, StringHelper.getOccurrenceCount ("Test", "Test"));
    assertEquals (1, StringHelper.getOccurrenceCount ("Test", "Tes"));
    assertEquals (1, StringHelper.getOccurrenceCount ("Test", "est"));
    assertEquals (1, StringHelper.getOccurrenceCount ("Test", "es"));
    assertEquals (2, StringHelper.getOccurrenceCount ("Testen", "e"));
    assertEquals (0, StringHelper.getOccurrenceCount ("Testen", ""));
    assertEquals (4, StringHelper.getOccurrenceCount ("eeee", "e"));
    assertEquals (2, StringHelper.getOccurrenceCount ("eeee", "ee"));
    assertEquals (1, StringHelper.getOccurrenceCount ("eeee", "eee"));

    // Invalid case
    assertEquals (0, StringHelper.getOccurrenceCount ("eeee", "E"));
    assertEquals (0, StringHelper.getOccurrenceCount ("eeee", "EE"));
    assertEquals (0, StringHelper.getOccurrenceCount ("eeee", "EEE"));
  }

  @Test
  public void testGetOccurrenceCountChar ()
  {
    assertEquals (0, StringHelper.getOccurrenceCount (null, 'x'));
    assertEquals (0, StringHelper.getOccurrenceCount ("e", 'f'));
    assertEquals (0, StringHelper.getOccurrenceCount ("e", '\u0000'));
    assertEquals (0, StringHelper.getOccurrenceCount ("eeee", 'f'));
    assertEquals (0, StringHelper.getOccurrenceCount ("eeee", '\u0000'));

    assertEquals (1, StringHelper.getOccurrenceCount ("e", 'e'));
    assertEquals (4, StringHelper.getOccurrenceCount ("eeee", 'e'));
    assertEquals (1, StringHelper.getOccurrenceCount ("abc", 'a'));
    assertEquals (2, StringHelper.getOccurrenceCount ("aabc", 'a'));
    assertEquals (1, StringHelper.getOccurrenceCount ("abc", 'b'));
    assertEquals (1, StringHelper.getOccurrenceCount ("abc", 'c'));
    assertEquals (2, StringHelper.getOccurrenceCount ("abcc", 'c'));
  }

  @Test
  public void testGetOccurrenceCountIgnoreCaseString ()
  {
    final Locale aLocale = Locale.ENGLISH;
    assertEquals (0, StringHelper.getOccurrenceCountIgnoreCase ("Test", null, aLocale));
    assertEquals (0, StringHelper.getOccurrenceCountIgnoreCase (null, "Test", aLocale));
    assertEquals (1, StringHelper.getOccurrenceCountIgnoreCase ("Test", "Test", aLocale));
    assertEquals (1, StringHelper.getOccurrenceCountIgnoreCase ("Test", "Tes", aLocale));
    assertEquals (1, StringHelper.getOccurrenceCountIgnoreCase ("Test", "est", aLocale));
    assertEquals (1, StringHelper.getOccurrenceCountIgnoreCase ("Test", "es", aLocale));
    assertEquals (2, StringHelper.getOccurrenceCountIgnoreCase ("Testen", "e", aLocale));
    assertEquals (0, StringHelper.getOccurrenceCountIgnoreCase ("Testen", "", aLocale));
    assertEquals (4, StringHelper.getOccurrenceCountIgnoreCase ("eeee", "e", aLocale));
    assertEquals (2, StringHelper.getOccurrenceCountIgnoreCase ("eeee", "ee", aLocale));
    assertEquals (1, StringHelper.getOccurrenceCountIgnoreCase ("eeee", "eee", aLocale));

    // Ignoring case
    assertEquals (2, StringHelper.getOccurrenceCountIgnoreCase ("Test", "t", aLocale));
    assertEquals (4, StringHelper.getOccurrenceCountIgnoreCase ("eeee", "E", aLocale));
    assertEquals (2, StringHelper.getOccurrenceCountIgnoreCase ("eeee", "EE", aLocale));
    assertEquals (1, StringHelper.getOccurrenceCountIgnoreCase ("eeee", "EEE", aLocale));
  }

  @Test
  public void testGetOccurrenceCountIgnoreCaseChar ()
  {
    final Locale aLocale = Locale.ENGLISH;
    assertEquals (0, StringHelper.getOccurrenceCountIgnoreCase (null, 'x', aLocale));
    assertEquals (0, StringHelper.getOccurrenceCountIgnoreCase ("e", 'f', aLocale));
    assertEquals (0, StringHelper.getOccurrenceCountIgnoreCase ("e", '\u0000', aLocale));
    assertEquals (0, StringHelper.getOccurrenceCountIgnoreCase ("eeee", 'f', aLocale));
    assertEquals (0, StringHelper.getOccurrenceCountIgnoreCase ("eeee", '\u0000', aLocale));

    assertEquals (1, StringHelper.getOccurrenceCountIgnoreCase ("e", 'e', aLocale));
    assertEquals (1, StringHelper.getOccurrenceCountIgnoreCase ("E", 'e', aLocale));
    assertEquals (1, StringHelper.getOccurrenceCountIgnoreCase ("e", 'E', aLocale));
    assertEquals (4, StringHelper.getOccurrenceCountIgnoreCase ("eeee", 'e', aLocale));
    assertEquals (4, StringHelper.getOccurrenceCountIgnoreCase ("EEEE", 'e', aLocale));
    assertEquals (4, StringHelper.getOccurrenceCountIgnoreCase ("eeee", 'E', aLocale));
    assertEquals (1, StringHelper.getOccurrenceCountIgnoreCase ("abc", 'a', aLocale));
    assertEquals (1, StringHelper.getOccurrenceCountIgnoreCase ("ABC", 'a', aLocale));
    assertEquals (1, StringHelper.getOccurrenceCountIgnoreCase ("abc", 'A', aLocale));
    assertEquals (2, StringHelper.getOccurrenceCountIgnoreCase ("aabc", 'a', aLocale));
    assertEquals (2, StringHelper.getOccurrenceCountIgnoreCase ("AABC", 'a', aLocale));
    assertEquals (2, StringHelper.getOccurrenceCountIgnoreCase ("aabc", 'A', aLocale));
    assertEquals (1, StringHelper.getOccurrenceCountIgnoreCase ("abc", 'b', aLocale));
    assertEquals (1, StringHelper.getOccurrenceCountIgnoreCase ("ABC", 'b', aLocale));
    assertEquals (1, StringHelper.getOccurrenceCountIgnoreCase ("abc", 'B', aLocale));
    assertEquals (1, StringHelper.getOccurrenceCountIgnoreCase ("abc", 'c', aLocale));
    assertEquals (1, StringHelper.getOccurrenceCountIgnoreCase ("ABC", 'c', aLocale));
    assertEquals (1, StringHelper.getOccurrenceCountIgnoreCase ("abc", 'C', aLocale));
    assertEquals (2, StringHelper.getOccurrenceCountIgnoreCase ("abcc", 'c', aLocale));
    assertEquals (2, StringHelper.getOccurrenceCountIgnoreCase ("ABCC", 'c', aLocale));
    assertEquals (2, StringHelper.getOccurrenceCountIgnoreCase ("abcc", 'C', aLocale));
  }

  @Test
  public void testTrimLeadingWhitespaces ()
  {
    assertEquals ("Hallo Welt", StringHelper.trimLeadingWhitespaces ("Hallo Welt"));
    assertEquals ("Hallo Welt ", StringHelper.trimLeadingWhitespaces (" Hallo Welt "));
    assertEquals ("Hallo Welt  ", StringHelper.trimLeadingWhitespaces ("  Hallo Welt  "));
    assertEquals ("", StringHelper.trimLeadingWhitespaces ("  "));
    assertEquals ("", StringHelper.trimLeadingWhitespaces (""));
    assertSame (null, StringHelper.trimLeadingWhitespaces (null));
  }

  @Test
  public void testTrimTrailingWhitespaces ()
  {
    assertEquals ("Hallo Welt", StringHelper.trimTrailingWhitespaces ("Hallo Welt"));
    assertEquals (" Hallo Welt", StringHelper.trimTrailingWhitespaces (" Hallo Welt "));
    assertEquals ("  Hallo Welt", StringHelper.trimTrailingWhitespaces ("  Hallo Welt  "));
    assertEquals ("", StringHelper.trimTrailingWhitespaces ("  "));
    assertEquals ("", StringHelper.trimTrailingWhitespaces (""));
    assertSame (null, StringHelper.trimTrailingWhitespaces (null));
  }

  @Test
  public void testTrimEnd ()
  {
    assertEquals ("Hallo Welt", StringHelper.trimEnd ("Hallo Welt", ""));
    assertEquals ("Hallo Welt", StringHelper.trimEnd ("Hallo Welt", "asd"));
    assertEquals (" Hallo We", StringHelper.trimEnd (" Hallo Welt", "lt"));
    assertEquals ("Hallo Wel", StringHelper.trimEnd ("Hallo Welt", "t"));
    assertEquals ("", StringHelper.trimEnd ("", "lt"));
    assertEquals ("", StringHelper.trimEnd ("", ""));
    assertSame (null, StringHelper.trimEnd (null, null));
  }

  @Test
  public void testTrimEndRepeatedlyString ()
  {
    assertEquals ("Hallo Welt", StringHelper.trimEndRepeatedly ("Hallo Welt", ""));
    assertEquals ("Hallo Welt", StringHelper.trimEndRepeatedly ("Hallo Welt", "asd"));
    assertEquals (" Hallo We", StringHelper.trimEndRepeatedly (" Hallo Welt", "lt"));
    assertEquals (" Hallo We", StringHelper.trimEndRepeatedly (" Hallo Weltltltlt", "lt"));
    assertEquals ("Hallo Wel", StringHelper.trimEndRepeatedly ("Hallo Welt", "t"));
    assertEquals ("Hallo Wel", StringHelper.trimEndRepeatedly ("Hallo Welttttttttttt", "t"));
    assertEquals ("", StringHelper.trimEndRepeatedly ("", "lt"));
    assertEquals ("", StringHelper.trimEndRepeatedly ("", ""));
    assertSame (null, StringHelper.trimEndRepeatedly (null, null));
  }

  @Test
  public void testTrimEndRepeatedlyChar ()
  {
    assertEquals ("Hallo Welt", StringHelper.trimEndRepeatedly ("Hallo Welt", 'a'));
    assertEquals ("Hallo Wel", StringHelper.trimEndRepeatedly ("Hallo Welt", 't'));
    assertEquals ("Hallo Wel", StringHelper.trimEndRepeatedly ("Hallo Welttttttttttt", 't'));
    assertEquals ("", StringHelper.trimEndRepeatedly ("", 'a'));
    assertSame (null, StringHelper.trimEndRepeatedly (null, 'a'));
  }

  @Test
  public void testTrimEndInt ()
  {
    assertEquals ("Hallo Welt", StringHelper.trimEnd ("Hallo Welt", 0));
    assertEquals ("Hallo Welt", StringHelper.trimEnd ("Hallo Welt", -3));
    assertEquals (" Hallo We", StringHelper.trimEnd (" Hallo Welt", 2));
    assertEquals ("Hallo Wel", StringHelper.trimEnd ("Hallo Welt", 1));
    assertEquals ("H", StringHelper.trimEnd ("Hallo Welt", 9));
    assertEquals ("", StringHelper.trimEnd ("Hallo Welt", 10));
    assertEquals ("", StringHelper.trimEnd ("Hallo Welt", 12));
    assertEquals ("", StringHelper.trimEnd ("Hallo Welt", 9999));
    assertEquals ("", StringHelper.trimEnd ("", 2));
    assertEquals ("", StringHelper.trimEnd ("", 0));
    assertSame (null, StringHelper.trimEnd (null, null));
  }

  @Test
  public void testTrimStart ()
  {
    assertEquals ("Hallo Welt", StringHelper.trimStart ("Hallo Welt", ""));
    assertEquals ("Hallo Welt", StringHelper.trimStart ("Hallo Welt", "asd"));
    assertEquals ("allo Welt", StringHelper.trimStart (" Hallo Welt", " H"));
    assertEquals ("allo Welt", StringHelper.trimStart ("Hallo Welt", "H"));
    assertEquals ("", StringHelper.trimStart ("", "lt"));
    assertEquals ("", StringHelper.trimStart ("", ""));
    assertSame (null, StringHelper.trimStart (null, null));
  }

  @Test
  public void testTrimStartRepeatedlyString ()
  {
    assertEquals ("Hallo Welt", StringHelper.trimStartRepeatedly ("Hallo Welt", ""));
    assertEquals ("Hallo Welt", StringHelper.trimStartRepeatedly ("Hallo Welt", "asd"));
    assertEquals ("allo Welt", StringHelper.trimStartRepeatedly (" Hallo Welt", " H"));
    assertEquals ("allo Welt", StringHelper.trimStartRepeatedly ("HHHHHHHHHallo Welt", "H"));
    assertEquals ("", StringHelper.trimStartRepeatedly ("", "lt"));
    assertEquals ("", StringHelper.trimStartRepeatedly ("", ""));
    assertSame (null, StringHelper.trimStartRepeatedly (null, null));
  }

  @Test
  public void testTrimStartRepeatedlyChar ()
  {
    assertEquals ("Hallo Welt", StringHelper.trimStartRepeatedly ("Hallo Welt", 'a'));
    assertEquals ("allo Welt", StringHelper.trimStartRepeatedly ("Hallo Welt", 'H'));
    assertEquals ("allo Welt", StringHelper.trimStartRepeatedly ("HHHHHHHHHallo Welt", 'H'));
    assertEquals ("", StringHelper.trimStartRepeatedly ("", 'a'));
    assertSame (null, StringHelper.trimStartRepeatedly (null, 'a'));
  }

  @Test
  public void testTrimStartInt ()
  {
    assertEquals ("Hallo Welt", StringHelper.trimStart ("Hallo Welt", 0));
    assertEquals ("Hallo Welt", StringHelper.trimStart ("Hallo Welt", -3));
    assertEquals ("allo Welt ", StringHelper.trimStart (" Hallo Welt ", 2));
    assertEquals ("allo Welt", StringHelper.trimStart ("Hallo Welt", 1));
    assertEquals ("t", StringHelper.trimStart ("Hallo Welt", 9));
    assertEquals ("", StringHelper.trimStart ("Hallo Welt", 10));
    assertEquals ("", StringHelper.trimStart ("Hallo Welt", 12));
    assertEquals ("", StringHelper.trimStart ("Hallo Welt", 9999));
    assertEquals ("", StringHelper.trimStart ("", 2));
    assertEquals ("", StringHelper.trimStart ("", 0));
    assertSame (null, StringHelper.trimStart (null, null));
  }

  @Test
  public void testTrim ()
  {
    assertEquals ("Hallo Welt", StringHelper.trim ("Hallo Welt"));
    assertEquals ("Hallo Welt", StringHelper.trim (" Hallo Welt"));
    assertEquals ("Hallo Welt", StringHelper.trim ("Hallo Welt "));
    assertEquals ("Hallo Welt", StringHelper.trim (" Hallo Welt "));
    assertEquals ("Hallo Welt", StringHelper.trim ("   Hallo Welt   "));
    assertEquals ("", StringHelper.trim (""));
    assertEquals ("", StringHelper.trim (""));
    assertSame (null, StringHelper.trim (null));
  }

  @Test
  public void testGetFirstChar ()
  {
    assertEquals ('a', StringHelper.getFirstChar ("abc"));
    assertEquals ('a', StringHelper.getFirstChar ("a"));
    assertEquals (CGlobal.ILLEGAL_CHAR, StringHelper.getFirstChar (""));
    assertEquals (CGlobal.ILLEGAL_CHAR, StringHelper.getFirstChar ((CharSequence) null));
    assertEquals (CGlobal.ILLEGAL_CHAR, StringHelper.getFirstChar ((char []) null));
  }

  @Test
  public void testGetLastChar ()
  {
    assertEquals ('c', StringHelper.getLastChar ("abc"));
    assertEquals ('a', StringHelper.getLastChar ("a"));
    assertEquals (CGlobal.ILLEGAL_CHAR, StringHelper.getLastChar (""));
    assertEquals (CGlobal.ILLEGAL_CHAR, StringHelper.getLastChar ((CharSequence) null));
    assertEquals (CGlobal.ILLEGAL_CHAR, StringHelper.getLastChar ((char []) null));
  }

  @Test
  public void testGetCharCount ()
  {
    assertEquals (0, StringHelper.getCharCount ("abc", 'x'));
    assertEquals (1, StringHelper.getCharCount ("xabc", 'x'));
    assertEquals (1, StringHelper.getCharCount ("abxc", 'x'));
    assertEquals (1, StringHelper.getCharCount ("abcx", 'x'));
    assertEquals (0, StringHelper.getCharCount ((String) null, 'x'));
    assertEquals (0, StringHelper.getCharCount ((char []) null, 'x'));
    assertEquals (0, StringHelper.getCharCount ("", 'x'));
    for (int i = 0; i < 1000; ++i)
      assertEquals (i, StringHelper.getCharCount (StringHelper.getRepeated ('x', i), 'x'));
  }

  @Test
  public void testGetLineCount ()
  {
    assertEquals (1, StringHelper.getLineCount ("abc"));
    assertEquals (2, StringHelper.getLineCount ("ab\nc"));
    assertEquals (2, StringHelper.getLineCount ("ab\r\nc"));
    assertEquals (1, StringHelper.getLineCount ("ab\rc"));
  }

  @Test
  public void testGetCharacterCountInt ()
  {
    int iVal = 1;
    for (int i = 1; i <= 10; ++i)
    {
      assertEquals (i, StringHelper.getCharacterCount (iVal));
      iVal *= 10;
    }
    iVal = -1;
    for (int i = 1; i <= 10; ++i)
    {
      assertEquals (1 + i, StringHelper.getCharacterCount (iVal));
      iVal *= 10;
    }
    assertEquals (11, StringHelper.getCharacterCount (Integer.MIN_VALUE + 1));
    assertEquals (10, StringHelper.getCharacterCount (Integer.MAX_VALUE));
  }

  @Test
  public void testGetCharacterCountLong ()
  {
    long lVal = 1;
    for (int i = 1; i <= 19; ++i)
    {
      assertEquals (i, StringHelper.getCharacterCount (lVal));
      lVal *= 10;
    }
    lVal = -1;
    for (int i = 1; i <= 19; ++i)
    {
      assertEquals (1 + i, StringHelper.getCharacterCount (lVal));
      lVal *= 10;
    }
    assertEquals (20, StringHelper.getCharacterCount (Long.MIN_VALUE + 1));
    assertEquals (19, StringHelper.getCharacterCount (Long.MAX_VALUE));
  }

  @Test
  @SuppressFBWarnings ("TQ_NEVER_VALUE_USED_WHERE_ALWAYS_REQUIRED")
  public void testCutAfterLength ()
  {
    assertEquals ("abc...", StringHelper.getCutAfterLength ("abc die Katze lief im Schnee", 3, "..."));
    assertEquals ("ab", StringHelper.getCutAfterLength ("ab", 3, "..."));
    assertEquals ("abc", StringHelper.getCutAfterLength ("abc", 3, "..."));
    assertEquals ("", StringHelper.getCutAfterLength ("", 3, "..."));
    assertEquals ("abc", StringHelper.getCutAfterLength ("abcdef", 3, ""));
    assertEquals ("abc", StringHelper.getCutAfterLength ("abcdef", 3, null));

    try
    {
      StringHelper.getCutAfterLength (null, 3, "...");
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      StringHelper.getCutAfterLength ("abc", -1, "...");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testGetNotNullString ()
  {
    assertEquals ("abc", StringHelper.getNotNull ("abc"));
    assertEquals ("", StringHelper.getNotNull (""));
    assertEquals ("", StringHelper.getNotNull (null));
    assertEquals ("xy", StringHelper.getNotNull (null, "xy"));
  }

  @Test
  public void testGetNotNullCharSeq ()
  {
    assertEquals ("abc", StringHelper.getNotNull (new StringBuilder ("abc")).toString ());
    assertEquals ("", StringHelper.getNotNull (new StringBuilder ()).toString ());
    assertEquals ("", StringHelper.getNotNull ((StringBuilder) null));
  }

  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testReplaceAllString ()
  {
    assertEquals ("abc", StringHelper.replaceAll ("abc", "d", "e"));
    assertEquals ("abd", StringHelper.replaceAll ("abc", "c", "d"));
    assertEquals ("adc", StringHelper.replaceAll ("abc", "b", "d"));
    assertEquals ("dbc", StringHelper.replaceAll ("abc", "a", "d"));
    assertEquals ("ddd", StringHelper.replaceAll ("aaa", "a", "d"));
    assertEquals ("xyxyxy", StringHelper.replaceAll ("aaa", "a", "xy"));
    assertEquals ("", StringHelper.replaceAll ("", "anything", "nothing"));
    assertEquals ("", StringHelper.replaceAll ("aaa", "a", ""));
    assertEquals ("bb", StringHelper.replaceAll ("ababa", "a", ""));
    assertEquals ("acd", StringHelper.replaceAll ("abcd", "ab", "a"));
    assertEquals ("abd", StringHelper.replaceAll ("abcd", "bc", "b"));
    assertEquals ("abc", StringHelper.replaceAll ("abcd", "cd", "c"));
    assertEquals ("abc", StringHelper.replaceAll ("abcd", "d", ""));
    assertEquals ("bcbc", StringHelper.replaceAll ("bcbcbc", "bcbc", "bc"));
    assertEquals ("aa", StringHelper.replaceAll ("aaaa", "aa", "a"));
    assertEquals ("a  a b ", StringHelper.replaceAll ("a    a  b ", "  ", " "));
    assertNull (StringHelper.replaceAll (null, "aa", "a"));
    assertEquals ("aaaa", StringHelper.replaceAll ("aaaa", "aa", "aa"));

    try
    {
      StringHelper.replaceAll ("aaaaach", null, "a");
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StringHelper.replaceAll ("aaaaach", "aa", null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testReplaceAllChar ()
  {
    assertEquals ("abc", StringHelper.replaceAll ("abc", 'd', 'e'));
    assertEquals ("abd", StringHelper.replaceAll ("abc", 'c', 'd'));
    assertEquals ("adc", StringHelper.replaceAll ("abc", 'b', 'd'));
    assertEquals ("dbc", StringHelper.replaceAll ("abc", 'a', 'd'));
    assertEquals ("ddd", StringHelper.replaceAll ("aaa", 'a', 'd'));
    assertEquals ("", StringHelper.replaceAll ("", 'a', 'b'));
    assertEquals ("aaa", StringHelper.replaceAll ("aaa", 'a', 'a'));
    assertEquals ("aaa", StringHelper.replaceAll ("aaa", 'b', 'b'));
    assertEquals ("bbbbb", StringHelper.replaceAll ("ababa", 'a', 'b'));
    assertEquals ("\0b\0b\0", StringHelper.replaceAll ("ababa", 'a', '\0'));
  }

  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testReplaceAllSafe ()
  {
    assertEquals ("abc", StringHelper.replaceAllSafe ("abc", "d", "e"));
    assertEquals ("abd", StringHelper.replaceAllSafe ("abc", "c", "d"));
    assertEquals ("adc", StringHelper.replaceAllSafe ("abc", "b", "d"));
    assertEquals ("dbc", StringHelper.replaceAllSafe ("abc", "a", "d"));
    assertEquals ("ddd", StringHelper.replaceAllSafe ("aaa", "a", "d"));
    assertEquals ("xyxyxy", StringHelper.replaceAllSafe ("aaa", "a", "xy"));
    assertEquals ("", StringHelper.replaceAllSafe ("", "anything", "nothing"));
    assertEquals ("", StringHelper.replaceAllSafe ("aaa", "a", ""));
    assertEquals ("bb", StringHelper.replaceAllSafe ("ababa", "a", ""));
    assertEquals ("acd", StringHelper.replaceAllSafe ("abcd", "ab", "a"));
    assertEquals ("abd", StringHelper.replaceAllSafe ("abcd", "bc", "b"));
    assertEquals ("abc", StringHelper.replaceAllSafe ("abcd", "cd", "c"));
    assertEquals ("abc", StringHelper.replaceAllSafe ("abcd", "d", ""));
    assertEquals ("bcbc", StringHelper.replaceAllSafe ("bcbcbc", "bcbc", "bc"));
    assertEquals ("aa", StringHelper.replaceAllSafe ("aaaa", "aa", "a"));
    assertEquals ("a  a b ", StringHelper.replaceAllSafe ("a    a  b ", "  ", " "));
    assertNull (StringHelper.replaceAllSafe (null, "aa", "a"));
    assertEquals ("aaaa", StringHelper.replaceAllSafe ("aaaa", "aa", "aa"));

    try
    {
      StringHelper.replaceAllSafe ("aaaaach", null, "a");
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    assertEquals ("ach", StringHelper.replaceAllSafe ("aaaaach", "aa", null));
  }

  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testReplaceAllRepeatedly ()
  {
    assertEquals ("abc", StringHelper.replaceAllRepeatedly ("abc", "d", "e"));
    assertEquals ("dbc", StringHelper.replaceAllRepeatedly ("abc", "a", "d"));
    assertEquals ("ddd", StringHelper.replaceAllRepeatedly ("aaa", "a", "d"));
    assertEquals ("a a b ", StringHelper.replaceAllRepeatedly ("a    a  b ", "  ", " "));
    assertEquals ("", StringHelper.replaceAllRepeatedly ("", " a", "b"));
    assertNull (StringHelper.replaceAllRepeatedly (null, " a", "b"));

    try
    {
      StringHelper.replaceAllRepeatedly ("aaaaach", null, "a");
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StringHelper.replaceAllRepeatedly ("aaaaach", "aa", null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StringHelper.replaceAllRepeatedly ("aaaaach", "a", "aa");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testReplaceMultipleMap ()
  {
    final Map <String, String> aMap = new HashMap <String, String> ();
    aMap.put ("Hallo", "Hi");
    aMap.put ("Welt", "world");
    aMap.put ("!", "???");
    assertEquals ("Abc die Katze lief im Schnee", StringHelper.replaceMultiple ("Abc die Katze lief im Schnee", aMap));
    assertEquals ("Hi Katze", StringHelper.replaceMultiple ("Hallo Katze", aMap));
    assertEquals ("Moin world", StringHelper.replaceMultiple ("Moin Welt", aMap));
    assertEquals ("Moin welt", StringHelper.replaceMultiple ("Moin welt", aMap));
    assertEquals ("Hi", StringHelper.replaceMultiple ("Hallo", aMap));
    assertEquals ("Hi Hi", StringHelper.replaceMultiple ("Hallo Hallo", aMap));
    assertEquals ("HiHiHi", StringHelper.replaceMultiple ("HalloHalloHallo", aMap));
    assertEquals ("Hi world???", StringHelper.replaceMultiple ("Hallo Welt!", aMap));
    assertEquals ("Hi world???Hi world???", StringHelper.replaceMultiple ("Hallo Welt!Hallo Welt!", aMap));
  }

  @Test
  public void testReplaceMultipleCharArrays ()
  {
    assertArrayEquals ("bb".toCharArray (),
                       StringHelper.replaceMultiple ("a", new char [] { 'a' }, new char [] [] { "bb".toCharArray () }));
    assertArrayEquals ("bbbb".toCharArray (),
                       StringHelper.replaceMultiple ("aa",
                                                     new char [] { 'a' },
                                                     new char [] [] { "bb".toCharArray () }));
    assertArrayEquals ("cdc".toCharArray (),
                       StringHelper.replaceMultiple ("cdc",
                                                     new char [] { 'a' },
                                                     new char [] [] { "bb".toCharArray () }));
    assertArrayEquals ("cbbc".toCharArray (),
                       StringHelper.replaceMultiple ("cac",
                                                     new char [] { 'a' },
                                                     new char [] [] { "bb".toCharArray () }));
    assertArrayEquals ("ddbbdd".toCharArray (),
                       StringHelper.replaceMultiple ("cac",
                                                     new char [] { 'a', 'c' },
                                                     new char [] [] { "bb".toCharArray (), "dd".toCharArray () }));
    assertArrayEquals ("<ddbbdd>".toCharArray (),
                       StringHelper.replaceMultiple ("<cac>",
                                                     new char [] { 'a', 'c' },
                                                     new char [] [] { "bb".toCharArray (), "dd".toCharArray () }));
    assertArrayEquals (new char [0],
                       StringHelper.replaceMultiple ("",
                                                     new char [] { 'a', 'c' },
                                                     new char [] [] { "bb".toCharArray (), "dd".toCharArray () }));
    assertArrayEquals ("any".toCharArray (), StringHelper.replaceMultiple ("any", new char [0], new char [0] []));
    try
    {
      StringHelper.replaceMultiple ("any", (char []) null, new char [] [] { "bb".toCharArray (), "dd".toCharArray () });
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StringHelper.replaceMultiple ("any", "an".toCharArray (), null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StringHelper.replaceMultiple ("any", new char [1], new char [2] []);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testReplaceMultipleTo () throws IOException
  {
    NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    StringHelper.replaceMultipleTo ("a", new char [] { 'a' }, new char [] [] { "bb".toCharArray () }, aSW);
    assertEquals ("bb", aSW.getAsString ());

    aSW = new NonBlockingStringWriter ();
    StringHelper.replaceMultipleTo ("aa", new char [] { 'a' }, new char [] [] { "bb".toCharArray () }, aSW);
    assertEquals ("bbbb", aSW.getAsString ());

    aSW = new NonBlockingStringWriter ();
    StringHelper.replaceMultipleTo ("cdc", new char [] { 'a' }, new char [] [] { "bb".toCharArray () }, aSW);
    assertEquals ("cdc", aSW.getAsString ());

    aSW = new NonBlockingStringWriter ();
    StringHelper.replaceMultipleTo ("cac", new char [] { 'a' }, new char [] [] { "bb".toCharArray () }, aSW);
    assertEquals ("cbbc", aSW.getAsString ());

    aSW = new NonBlockingStringWriter ();
    StringHelper.replaceMultipleTo ("cac",
                                    new char [] { 'a', 'c' },
                                    new char [] [] { "bb".toCharArray (), "dd".toCharArray () },
                                    aSW);
    assertEquals ("ddbbdd", aSW.getAsString ());

    aSW = new NonBlockingStringWriter ();
    StringHelper.replaceMultipleTo ("<cac>",
                                    new char [] { 'a', 'c' },
                                    new char [] [] { "bb".toCharArray (), "dd".toCharArray () },
                                    aSW);
    assertEquals ("<ddbbdd>", aSW.getAsString ());

    aSW = new NonBlockingStringWriter ();
    StringHelper.replaceMultipleTo ("",
                                    new char [] { 'a', 'c' },
                                    new char [] [] { "bb".toCharArray (), "dd".toCharArray () },
                                    aSW);
    assertEquals ("", aSW.getAsString ());

    aSW = new NonBlockingStringWriter ();
    StringHelper.replaceMultipleTo ("any", new char [0], new char [0] [], aSW);
    assertEquals ("any", aSW.getAsString ());

    try
    {
      StringHelper.replaceMultipleTo ("any", null, new char [0] [], aSW);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StringHelper.replaceMultipleTo ("any", new char [0], null, aSW);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StringHelper.replaceMultipleTo ("any", new char [0], new char [1] [], aSW);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      StringHelper.replaceMultipleTo ("any", new char [0], new char [0] [], null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testReplaceMultipleCharArrayToChar ()
  {
    assertArrayEquals ("abc".toCharArray (), StringHelper.replaceMultiple ("abc", "def".toCharArray (), ' '));
    assertArrayEquals ("   ".toCharArray (), StringHelper.replaceMultiple ("abc", "abc".toCharArray (), ' '));
    assertArrayEquals ("   ".toCharArray (), StringHelper.replaceMultiple ("abc", "abcabc".toCharArray (), ' '));
    assertArrayEquals ("   ".toCharArray (), StringHelper.replaceMultiple ("abc", "aabbcc".toCharArray (), ' '));
    assertArrayEquals ("      ".toCharArray (), StringHelper.replaceMultiple ("abcabc", "abc".toCharArray (), ' '));
    assertArrayEquals ("      ".toCharArray (), StringHelper.replaceMultiple ("aabbcc", "abc".toCharArray (), ' '));
    assertArrayEquals ("a  ".toCharArray (), StringHelper.replaceMultiple ("abc", "bc".toCharArray (), ' '));
    assertArrayEquals (" b ".toCharArray (), StringHelper.replaceMultiple ("abc", "ac".toCharArray (), ' '));
    assertArrayEquals ("  c".toCharArray (), StringHelper.replaceMultiple ("abc", "ab".toCharArray (), ' '));
    assertArrayEquals ("abc".toCharArray (), StringHelper.replaceMultiple ("abc", "".toCharArray (), ' '));
    assertArrayEquals ("".toCharArray (), StringHelper.replaceMultiple ("", "abc".toCharArray (), ' '));
    assertArrayEquals ("".toCharArray (), StringHelper.replaceMultiple ("", "".toCharArray (), ' '));
    assertArrayEquals ("".toCharArray (), StringHelper.replaceMultiple (null, "abc".toCharArray (), ' '));
    assertArrayEquals ("".toCharArray (), StringHelper.replaceMultiple (null, "".toCharArray (), ' '));
  }

  @Test
  public void testRemoveAllChar ()
  {
    assertEquals ("abc", StringHelper.removeAll ("abc", 'd'));
    assertEquals ("ab", StringHelper.removeAll ("abc", 'c'));
    assertEquals ("ac", StringHelper.removeAll ("abc", 'b'));
    assertEquals ("bc", StringHelper.removeAll ("abc", 'a'));
    assertEquals ("", StringHelper.removeAll ("aaa", 'a'));
    assertEquals ("", StringHelper.removeAll ("", 'a'));
    assertEquals ("bb", StringHelper.removeAll ("ababa", 'a'));
    assertEquals ("abc", StringHelper.removeAll ("abcd", 'd'));
    assertNull (StringHelper.removeAll (null, 'a'));
  }

  @Test
  public void testRemoveAllString ()
  {
    assertEquals ("abc", StringHelper.removeAll ("abc", "d"));
    assertEquals ("abc", StringHelper.removeAll ("abc", "ac"));
    assertEquals ("ab", StringHelper.removeAll ("abc", "c"));
    assertEquals ("a", StringHelper.removeAll ("abc", "bc"));
    assertEquals ("ac", StringHelper.removeAll ("abc", "b"));
    assertEquals ("c", StringHelper.removeAll ("abc", "ab"));
    assertEquals ("bc", StringHelper.removeAll ("abc", "a"));
    assertEquals ("", StringHelper.removeAll ("aaa", "a"));
    assertEquals ("a", StringHelper.removeAll ("aaa", "aa"));
    assertEquals ("", StringHelper.removeAll ("", "a"));
    assertEquals ("bb", StringHelper.removeAll ("ababa", "a"));
    assertEquals ("a", StringHelper.removeAll ("ababa", "ab"));
    assertEquals ("abc", StringHelper.removeAll ("abcd", "d"));
    assertNull (StringHelper.removeAll (null, "a"));
  }

  @Test
  public void testToString ()
  {
    assertEquals ("1", StringHelper.getToString (I1));
    assertEquals ("any", StringHelper.getToString ("any"));
    assertEquals ("", StringHelper.getToString (null));

    assertEquals ("1", StringHelper.getToString (I1, "default"));
    assertEquals ("any", StringHelper.getToString ("any", "default"));
    assertEquals ("default", StringHelper.getToString (null, "default"));

    assertEquals ("1", StringHelper.getToString (I1, null));
    assertEquals ("any", StringHelper.getToString ("any", null));
    assertNull (StringHelper.getToString (null, null));
  }

  @Test
  public void testMultiContains ()
  {
    final char [] aIn = "abcde".toCharArray ();
    assertTrue (StringHelper.containsAny (aIn, "a".toCharArray ()));
    assertFalse (StringHelper.containsAny (aIn, "z".toCharArray ()));
    assertFalse (StringHelper.containsAny (aIn, new char [0]));
    assertFalse (StringHelper.containsAny (new char [0], "a".toCharArray ()));
  }

  @Test
  public void testGetWithoutLeadingChar ()
  {
    assertEquals ("bcd", StringHelper.getWithoutLeadingChar ("abcd"));
    assertEquals ("b", StringHelper.getWithoutLeadingChar ("ab"));
    assertEquals ("", StringHelper.getWithoutLeadingChar ("a"));
    assertEquals ("", StringHelper.getWithoutLeadingChar (""));
    assertEquals ("", StringHelper.getWithoutLeadingChar (null));
  }

  @Test
  public void testGetWithoutLeadingChars ()
  {
    assertEquals ("cd", StringHelper.getWithoutLeadingChars ("abcd", 2));
    assertEquals ("d", StringHelper.getWithoutLeadingChars ("abcd", 3));
    assertEquals ("c", StringHelper.getWithoutLeadingChars ("abc", 2));
    assertEquals ("", StringHelper.getWithoutLeadingChars ("ab", 2));
    assertEquals ("", StringHelper.getWithoutLeadingChars ("a", 2));
    assertEquals ("", StringHelper.getWithoutLeadingChars ("", 2));
    assertEquals ("", StringHelper.getWithoutLeadingChars (null, 2));
  }

  @Test
  public void testGetWithoutTrailingChar ()
  {
    assertEquals ("abc", StringHelper.getWithoutTrailingChar ("abcd"));
    assertEquals ("a", StringHelper.getWithoutTrailingChar ("ab"));
    assertEquals ("", StringHelper.getWithoutTrailingChar ("a"));
    assertEquals ("", StringHelper.getWithoutTrailingChar (""));
    assertEquals ("", StringHelper.getWithoutTrailingChar (null));
  }

  @Test
  public void testGetWithoutTrailingChars ()
  {
    assertEquals ("ab", StringHelper.getWithoutTrailingChars ("abcd", 2));
    assertEquals ("a", StringHelper.getWithoutTrailingChars ("abcd", 3));
    assertEquals ("a", StringHelper.getWithoutTrailingChars ("abc", 2));
    assertEquals ("", StringHelper.getWithoutTrailingChars ("ab", 2));
    assertEquals ("", StringHelper.getWithoutTrailingChars ("a", 2));
    assertEquals ("", StringHelper.getWithoutTrailingChars ("", 2));
    assertEquals ("", StringHelper.getWithoutTrailingChars (null, 2));
  }

  @Test
  public void testGetUntilFirstExcl ()
  {
    assertEquals ("", StringHelper.getUntilFirstExcl ("abc@def.com", 'a'));
    assertEquals ("ab", StringHelper.getUntilFirstExcl ("abc@def.com", 'c'));
    assertEquals ("abc", StringHelper.getUntilFirstExcl ("abc@def.com", '@'));
    assertEquals ("abc@def", StringHelper.getUntilFirstExcl ("abc@def.com", '.'));
    assertEquals ("abc@def.co", StringHelper.getUntilFirstExcl ("abc@def.com", 'm'));
    assertNull (StringHelper.getUntilFirstExcl ("abc@def.com", 'X'));
    assertNull (StringHelper.getUntilFirstExcl ("", 'X'));
    assertNull (StringHelper.getUntilFirstExcl (null, 'X'));

    assertEquals ("", StringHelper.getUntilFirstExcl ("abc@def.com", "a"));
    assertEquals ("", StringHelper.getUntilFirstExcl ("abc@def.com", "ab"));
    assertEquals ("abc", StringHelper.getUntilFirstExcl ("abc@def.com", "@"));
    assertEquals ("abc", StringHelper.getUntilFirstExcl ("abc@def.com", "@d"));
    assertEquals ("abc@def", StringHelper.getUntilFirstExcl ("abc@def.com", "."));
    assertEquals ("abc@def", StringHelper.getUntilFirstExcl ("abc@def.com", ".c"));
    assertEquals ("abc@def.co", StringHelper.getUntilFirstExcl ("abc@def.com", "m"));
    assertNull (StringHelper.getUntilFirstExcl ("abc@def.com", "fg"));
    assertNull (StringHelper.getUntilFirstExcl ("abc@def.com", "X"));
    assertNull (StringHelper.getUntilFirstExcl ("", "X"));
    assertNull (StringHelper.getUntilFirstExcl (null, "X"));
    assertEquals ("", StringHelper.getUntilFirstExcl ("abc@def.com", ""));
  }

  @Test
  public void testGetUntilLastExcl ()
  {
    assertEquals ("", StringHelper.getUntilLastExcl ("abc@def.com", 'a'));
    assertEquals ("abc@def.", StringHelper.getUntilLastExcl ("abc@def.com", 'c'));
    assertEquals ("abc", StringHelper.getUntilLastExcl ("abc@def.com", '@'));
    assertEquals ("abc@def", StringHelper.getUntilLastExcl ("abc@def.com", '.'));
    assertEquals ("abc@def.co", StringHelper.getUntilLastExcl ("abc@def.com", 'm'));
    assertNull (StringHelper.getUntilLastExcl ("abc@def.com", 'X'));
    assertNull (StringHelper.getUntilLastExcl ("", 'X'));
    assertNull (StringHelper.getUntilLastExcl (null, 'X'));

    assertEquals ("", StringHelper.getUntilLastExcl ("abc@def.com", "a"));
    assertEquals ("", StringHelper.getUntilLastExcl ("abc@def.com", "ab"));
    assertEquals ("abc", StringHelper.getUntilLastExcl ("abc@def.com", "@"));
    assertEquals ("abc", StringHelper.getUntilLastExcl ("abc@def.com", "@d"));
    assertEquals ("abc@def", StringHelper.getUntilLastExcl ("abc@def.com", "."));
    assertEquals ("abc@def", StringHelper.getUntilLastExcl ("abc@def.com", ".c"));
    assertEquals ("abc@def.co", StringHelper.getUntilLastExcl ("abc@def.com", "m"));
    assertNull (StringHelper.getUntilLastExcl ("abc@def.com", "fg"));
    assertNull (StringHelper.getUntilLastExcl ("abc@def.com", "X"));
    assertNull (StringHelper.getUntilLastExcl ("", "X"));
    assertNull (StringHelper.getUntilLastExcl (null, "X"));
    assertEquals ("", StringHelper.getUntilLastExcl ("abc@def.com", ""));
  }

  @Test
  public void testGetUntilFirstIncl ()
  {
    assertEquals ("a", StringHelper.getUntilFirstIncl ("abc@def.com", 'a'));
    assertEquals ("abc", StringHelper.getUntilFirstIncl ("abc@def.com", 'c'));
    assertEquals ("abc@", StringHelper.getUntilFirstIncl ("abc@def.com", '@'));
    assertEquals ("abc@def.", StringHelper.getUntilFirstIncl ("abc@def.com", '.'));
    assertEquals ("abc@def.com", StringHelper.getUntilFirstIncl ("abc@def.com", 'm'));
    assertNull (StringHelper.getUntilFirstIncl ("abc@def.com", 'X'));
    assertNull (StringHelper.getUntilFirstIncl ("", 'X'));
    assertNull (StringHelper.getUntilFirstIncl (null, 'X'));

    assertEquals ("a", StringHelper.getUntilFirstIncl ("abc@def.com", "a"));
    assertEquals ("ab", StringHelper.getUntilFirstIncl ("abc@def.com", "ab"));
    assertEquals ("abc@", StringHelper.getUntilFirstIncl ("abc@def.com", "@"));
    assertEquals ("abc@d", StringHelper.getUntilFirstIncl ("abc@def.com", "@d"));
    assertEquals ("abc@def.", StringHelper.getUntilFirstIncl ("abc@def.com", "."));
    assertEquals ("abc@def.c", StringHelper.getUntilFirstIncl ("abc@def.com", ".c"));
    assertEquals ("abc@def.com", StringHelper.getUntilFirstIncl ("abc@def.com", "m"));
    assertNull (StringHelper.getUntilFirstIncl ("abc@def.com", "fg"));
    assertNull (StringHelper.getUntilFirstIncl ("abc@def.com", "X"));
    assertNull (StringHelper.getUntilFirstIncl ("", "X"));
    assertNull (StringHelper.getUntilFirstIncl (null, "X"));
    assertEquals ("", StringHelper.getUntilFirstIncl ("abc@def.com", ""));
  }

  @Test
  public void testGetUntilLastIncl ()
  {
    assertEquals ("a", StringHelper.getUntilLastIncl ("abc@def.com", 'a'));
    assertEquals ("abc@def.c", StringHelper.getUntilLastIncl ("abc@def.com", 'c'));
    assertEquals ("abc@", StringHelper.getUntilLastIncl ("abc@def.com", '@'));
    assertEquals ("abc@def.", StringHelper.getUntilLastIncl ("abc@def.com", '.'));
    assertEquals ("abc@def.com", StringHelper.getUntilLastIncl ("abc@def.com", 'm'));
    assertNull (StringHelper.getUntilLastIncl ("abc@def.com", 'X'));
    assertNull (StringHelper.getUntilLastIncl ("", 'X'));
    assertNull (StringHelper.getUntilLastIncl (null, 'X'));

    assertEquals ("a", StringHelper.getUntilLastIncl ("abc@def.com", "a"));
    assertEquals ("ab", StringHelper.getUntilLastIncl ("abc@def.com", "ab"));
    assertEquals ("abc@", StringHelper.getUntilLastIncl ("abc@def.com", "@"));
    assertEquals ("abc@d", StringHelper.getUntilLastIncl ("abc@def.com", "@d"));
    assertEquals ("abc@def.", StringHelper.getUntilLastIncl ("abc@def.com", "."));
    assertEquals ("abc@def.c", StringHelper.getUntilLastIncl ("abc@def.com", ".c"));
    assertEquals ("abc@def.com", StringHelper.getUntilLastIncl ("abc@def.com", "m"));
    assertNull (StringHelper.getUntilLastIncl ("abc@def.com", "fg"));
    assertNull (StringHelper.getUntilLastIncl ("abc@def.com", "X"));
    assertNull (StringHelper.getUntilLastIncl ("", "X"));
    assertNull (StringHelper.getUntilLastIncl (null, "X"));
    assertEquals ("", StringHelper.getUntilLastIncl ("abc@def.com", ""));
  }

  @Test
  public void testGetFromFirstExcl ()
  {
    assertEquals ("bc@def.com", StringHelper.getFromFirstExcl ("abc@def.com", 'a'));
    assertEquals ("@def.com", StringHelper.getFromFirstExcl ("abc@def.com", 'c'));
    assertEquals ("def.com", StringHelper.getFromFirstExcl ("abc@def.com", '@'));
    assertEquals ("com", StringHelper.getFromFirstExcl ("abc@def.com", '.'));
    assertEquals ("", StringHelper.getFromFirstExcl ("abc@def.com", 'm'));
    assertNull (StringHelper.getFromFirstExcl ("abc@def.com", 'X'));
    assertNull (StringHelper.getFromFirstExcl ("", 'X'));
    assertNull (StringHelper.getFromFirstExcl (null, 'X'));

    assertEquals ("bc@def.com", StringHelper.getFromFirstExcl ("abc@def.com", "a"));
    assertEquals ("c@def.com", StringHelper.getFromFirstExcl ("abc@def.com", "ab"));
    assertEquals ("def.com", StringHelper.getFromFirstExcl ("abc@def.com", "@"));
    assertEquals ("ef.com", StringHelper.getFromFirstExcl ("abc@def.com", "@d"));
    assertEquals ("com", StringHelper.getFromFirstExcl ("abc@def.com", "."));
    assertEquals ("om", StringHelper.getFromFirstExcl ("abc@def.com", ".c"));
    assertEquals ("", StringHelper.getFromFirstExcl ("abc@def.com", "m"));
    assertNull (StringHelper.getFromFirstExcl ("abc@def.com", "fg"));
    assertNull (StringHelper.getFromFirstExcl ("abc@def.com", "X"));
    assertNull (StringHelper.getFromFirstExcl ("", "X"));
    assertNull (StringHelper.getFromFirstExcl (null, "X"));
    assertEquals ("abc@def.com", StringHelper.getFromFirstExcl ("abc@def.com", ""));
  }

  @Test
  public void testGetFromLastExcl ()
  {
    assertEquals ("bc@def.com", StringHelper.getFromLastExcl ("abc@def.com", 'a'));
    assertEquals ("om", StringHelper.getFromLastExcl ("abc@def.com", 'c'));
    assertEquals ("def.com", StringHelper.getFromLastExcl ("abc@def.com", '@'));
    assertEquals ("com", StringHelper.getFromLastExcl ("abc@def.com", '.'));
    assertEquals ("", StringHelper.getFromLastExcl ("abc@def.com", 'm'));
    assertNull (StringHelper.getFromLastExcl ("abc@def.com", 'X'));
    assertNull (StringHelper.getFromLastExcl ("", 'X'));
    assertNull (StringHelper.getFromLastExcl (null, 'X'));

    assertEquals ("bc@def.com", StringHelper.getFromLastExcl ("abc@def.com", "a"));
    assertEquals ("c@def.com", StringHelper.getFromLastExcl ("abc@def.com", "ab"));
    assertEquals ("def.com", StringHelper.getFromLastExcl ("abc@def.com", "@"));
    assertEquals ("ef.com", StringHelper.getFromLastExcl ("abc@def.com", "@d"));
    assertEquals ("com", StringHelper.getFromLastExcl ("abc@def.com", "."));
    assertEquals ("om", StringHelper.getFromLastExcl ("abc@def.com", ".c"));
    assertEquals ("", StringHelper.getFromLastExcl ("abc@def.com", "m"));
    assertNull (StringHelper.getFromLastExcl ("abc@def.com", "fg"));
    assertNull (StringHelper.getFromLastExcl ("abc@def.com", "X"));
    assertNull (StringHelper.getFromLastExcl ("", "X"));
    assertNull (StringHelper.getFromLastExcl (null, "X"));
    assertEquals ("abc@def.com", StringHelper.getFromLastExcl ("abc@def.com", ""));
  }

  @Test
  public void testGetFromFirstIncl ()
  {
    assertEquals ("abc@def.com", StringHelper.getFromFirstIncl ("abc@def.com", 'a'));
    assertEquals ("c@def.com", StringHelper.getFromFirstIncl ("abc@def.com", 'c'));
    assertEquals ("@def.com", StringHelper.getFromFirstIncl ("abc@def.com", '@'));
    assertEquals (".com", StringHelper.getFromFirstIncl ("abc@def.com", '.'));
    assertEquals ("m", StringHelper.getFromFirstIncl ("abc@def.com", 'm'));
    assertNull (StringHelper.getFromFirstIncl ("abc@def.com", 'X'));
    assertNull (StringHelper.getFromFirstIncl ("", 'X'));
    assertNull (StringHelper.getFromFirstIncl (null, 'X'));

    assertEquals ("abc@def.com", StringHelper.getFromFirstIncl ("abc@def.com", "a"));
    assertEquals ("abc@def.com", StringHelper.getFromFirstIncl ("abc@def.com", "ab"));
    assertEquals ("@def.com", StringHelper.getFromFirstIncl ("abc@def.com", "@"));
    assertEquals ("@def.com", StringHelper.getFromFirstIncl ("abc@def.com", "@d"));
    assertEquals (".com", StringHelper.getFromFirstIncl ("abc@def.com", "."));
    assertEquals (".com", StringHelper.getFromFirstIncl ("abc@def.com", ".c"));
    assertEquals ("m", StringHelper.getFromFirstIncl ("abc@def.com", "m"));
    assertNull (StringHelper.getFromFirstIncl ("abc@def.com", "fg"));
    assertNull (StringHelper.getFromFirstIncl ("abc@def.com", "X"));
    assertNull (StringHelper.getFromFirstIncl ("", "X"));
    assertNull (StringHelper.getFromFirstIncl (null, "X"));
    assertEquals ("abc@def.com", StringHelper.getFromFirstIncl ("abc@def.com", ""));
  }

  @Test
  public void testGetFromLastIncl ()
  {
    assertEquals ("abc@def.com", StringHelper.getFromLastIncl ("abc@def.com", 'a'));
    assertEquals ("com", StringHelper.getFromLastIncl ("abc@def.com", 'c'));
    assertEquals ("@def.com", StringHelper.getFromLastIncl ("abc@def.com", '@'));
    assertEquals (".com", StringHelper.getFromLastIncl ("abc@def.com", '.'));
    assertEquals ("m", StringHelper.getFromLastIncl ("abc@def.com", 'm'));
    assertNull (StringHelper.getFromLastIncl ("abc@def.com", 'X'));
    assertNull (StringHelper.getFromLastIncl ("", 'X'));
    assertNull (StringHelper.getFromLastIncl (null, 'X'));

    assertEquals ("abc@def.com", StringHelper.getFromLastIncl ("abc@def.com", "a"));
    assertEquals ("abc@def.com", StringHelper.getFromLastIncl ("abc@def.com", "ab"));
    assertEquals ("@def.com", StringHelper.getFromLastIncl ("abc@def.com", "@"));
    assertEquals ("@def.com", StringHelper.getFromLastIncl ("abc@def.com", "@d"));
    assertEquals (".com", StringHelper.getFromLastIncl ("abc@def.com", "."));
    assertEquals (".com", StringHelper.getFromLastIncl ("abc@def.com", ".c"));
    assertEquals ("m", StringHelper.getFromLastIncl ("abc@def.com", "m"));
    assertNull (StringHelper.getFromLastIncl ("abc@def.com", "fg"));
    assertNull (StringHelper.getFromLastIncl ("abc@def.com", "X"));
    assertNull (StringHelper.getFromLastIncl ("", "X"));
    assertNull (StringHelper.getFromLastIncl (null, "X"));
    assertEquals ("abc@def.com", StringHelper.getFromLastIncl ("abc@def.com", ""));
  }

  @Test
  public void testGetWithoutWhiteSpaces ()
  {
    assertEquals ("12345", StringHelper.getWithoutAnySpaces (" 1  2\t3\n4\r5"));
    assertEquals ("12345", StringHelper.getWithoutAnySpaces ("12345"));
    // test unicode space
    assertEquals ("7650", StringHelper.getWithoutAnySpaces ("7 650"));
    assertEquals ("", StringHelper.getWithoutAnySpaces ("      \t\r\n  \t\t\t"));
    assertEquals ("", StringHelper.getWithoutAnySpaces (""));
    assertEquals ("", StringHelper.getWithoutAnySpaces (null));
  }

  @Test
  public void testGetFirstTokenChar ()
  {
    assertEquals ("abc", StringHelper.getFirstToken ("abc", ' '));
    assertEquals ("", StringHelper.getFirstToken ("abc", 'a'));
    assertEquals ("a", StringHelper.getFirstToken ("abc", 'b'));
    assertEquals ("a", StringHelper.getFirstToken ("abcdcba", 'b'));
    assertEquals ("ab", StringHelper.getFirstToken ("abc", 'c'));
    assertEquals ("", StringHelper.getFirstToken ("", 'a'));
    assertNull (StringHelper.getFirstToken (null, 'c'));
  }

  @Test
  public void testGetFirstTokenString ()
  {
    assertEquals ("abc", StringHelper.getFirstToken ("abc", " "));
    assertEquals ("", StringHelper.getFirstToken ("abc", "a"));
    assertEquals ("a", StringHelper.getFirstToken ("abc", "b"));
    assertEquals ("a", StringHelper.getFirstToken ("abcdcba", "b"));
    assertEquals ("abcd", StringHelper.getFirstToken ("abcdcba", "cb"));
    assertEquals ("ab", StringHelper.getFirstToken ("abc", "c"));
    assertEquals ("", StringHelper.getFirstToken ("", "a"));
    assertNull (StringHelper.getFirstToken (null, "c"));
    assertNull (StringHelper.getFirstToken (null, ""));
    assertNull (StringHelper.getFirstToken (null, null));
    assertEquals ("abc", StringHelper.getFirstToken ("abc", null));
    assertEquals ("abc", StringHelper.getFirstToken ("abc", ""));
    assertEquals ("abc", StringHelper.getFirstToken ("abc", "        "));
  }

  @Test
  public void testGetLastTokenChar ()
  {
    assertEquals ("abc", StringHelper.getLastToken ("abc", ' '));
    assertEquals ("bc", StringHelper.getLastToken ("abc", 'a'));
    assertEquals ("c", StringHelper.getLastToken ("abc", 'b'));
    assertEquals ("a", StringHelper.getLastToken ("abcdcba", 'b'));
    assertEquals ("", StringHelper.getLastToken ("abc", 'c'));
    assertEquals ("", StringHelper.getLastToken ("", 'a'));
    assertNull (StringHelper.getLastToken (null, 'c'));
  }

  @Test
  public void testGetLastTokenString ()
  {
    assertEquals ("abc", StringHelper.getLastToken ("abc", " "));
    assertEquals ("bc", StringHelper.getLastToken ("abc", "a"));
    assertEquals ("c", StringHelper.getLastToken ("abc", "b"));
    assertEquals ("a", StringHelper.getLastToken ("abcdcba", "b"));
    assertEquals ("a", StringHelper.getLastToken ("abcdcba", "cb"));
    assertEquals ("", StringHelper.getLastToken ("abc", "c"));
    assertEquals ("", StringHelper.getLastToken ("", "a"));
    assertNull (StringHelper.getLastToken (null, "c"));
    assertNull (StringHelper.getLastToken (null, ""));
    assertNull (StringHelper.getLastToken (null, null));
    assertEquals ("abc", StringHelper.getLastToken ("abc", null));
    assertEquals ("abc", StringHelper.getLastToken ("abc", ""));
    assertEquals ("abc", StringHelper.getLastToken ("abc", "        "));
  }

  @Test
  public void testGetReverse ()
  {
    assertNull (StringHelper.getReverse (null));
    assertEquals ("", StringHelper.getReverse (""));
    assertEquals ("a", StringHelper.getReverse ("a"));
    assertEquals ("ba", StringHelper.getReverse ("ab"));
    assertEquals (" ba", StringHelper.getReverse ("ab "));
    assertEquals ("cba", StringHelper.getReverse ("abc"));
  }
}
