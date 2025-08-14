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
package com.helger.commons.string;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import org.junit.Test;

import com.helger.base.CGlobal;
import com.helger.base.functional.ICharPredicate;
import com.helger.base.functional.IThrowingFunction;
import com.helger.base.nonblocking.NonBlockingStringWriter;
import com.helger.base.string.StringHex;
import com.helger.base.string.Strings;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.collection.impl.ICommonsOrderedSet;
import com.helger.commons.collection.impl.ICommonsSet;

/**
 * Test class for class {@link StringHelper}.
 *
 * @author Philip Helger
 */
public final class StringHelperTest
{
  private static final Locale L_DE = new Locale ("de");

  @Test
  public void testHasTextAndHasNoText ()
  {
    assertTrue (Strings.isNotEmpty ("any"));
    assertTrue (Strings.isNotEmpty (" "));
    assertFalse (Strings.isNotEmpty (""));
    assertFalse (Strings.isNotEmpty ((String) null));

    assertTrue (Strings.isNotEmptyAfterTrim ("any"));
    assertFalse (Strings.isNotEmptyAfterTrim (" "));
    assertFalse (Strings.isNotEmptyAfterTrim (""));
    assertFalse (Strings.isNotEmptyAfterTrim (null));

    assertFalse (Strings.isEmpty ("any"));
    assertFalse (Strings.isEmpty (" "));
    assertTrue (Strings.isEmpty (""));
    assertTrue (Strings.isEmpty (null));

    assertFalse (Strings.isEmptyAfterTrim ("any"));
    assertTrue (Strings.isEmptyAfterTrim (" "));
    assertTrue (Strings.isEmptyAfterTrim (""));
    assertTrue (Strings.isEmptyAfterTrim (null));
  }

  @Test
  public void testLeadingZero ()
  {
    assertEquals ("005", Strings.getLeadingZero (5, 3));
    assertEquals ("0005", Strings.getLeadingZero (5, 4));
    assertEquals ("5", Strings.getLeadingZero (5, 1));
    assertEquals ("56", Strings.getLeadingZero (56, 1));
    assertEquals ("56", Strings.getLeadingZero (56, 2));
    assertEquals ("056", Strings.getLeadingZero (56, 3));
    assertEquals ("0000056", Strings.getLeadingZero (56, 7));
    assertEquals ("0005678", Strings.getLeadingZero (5678, 7));
    assertEquals ("-5", Strings.getLeadingZero (-5, 1));
    assertEquals ("-05", Strings.getLeadingZero (-5, 2));
    assertEquals ("-005", Strings.getLeadingZero (-5, 3));

    assertEquals ("005", Strings.getLeadingZero (5L, 3));
    assertEquals ("0005", Strings.getLeadingZero (5L, 4));
    assertEquals ("5", Strings.getLeadingZero (5L, 1));
    assertEquals ("56", Strings.getLeadingZero (56L, 1));
    assertEquals ("56", Strings.getLeadingZero (56L, 2));
    assertEquals ("056", Strings.getLeadingZero (56L, 3));
    assertEquals ("0000056", Strings.getLeadingZero (56L, 7));
    assertEquals ("0005678", Strings.getLeadingZero (5678L, 7));
    assertEquals ("-5", Strings.getLeadingZero (-5L, 1));
    assertEquals ("-05", Strings.getLeadingZero (-5L, 2));
    assertEquals ("-005", Strings.getLeadingZero (-5L, 3));

    assertNull (Strings.getLeadingZero ((Byte) null, 5));
    assertEquals ("00013", Strings.getLeadingZero (Byte.valueOf ((byte) 13), 5));
    assertNull (Strings.getLeadingZero ((Integer) null, 5));
    assertEquals ("00013", Strings.getLeadingZero (Integer.valueOf (13), 5));
    assertNull (Strings.getLeadingZero ((Long) null, 5));
    assertEquals ("00013", Strings.getLeadingZero (Long.valueOf (13), 5));
    assertNull (Strings.getLeadingZero ((Short) null, 5));
    assertEquals ("00013", Strings.getLeadingZero (Short.valueOf ((short) 13), 5));
  }

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
    final ICommonsList <String> aList = new CommonsArrayList <> ("a", "b", "c");
    assertEquals ("", StringHelper.getImploded (".", (String []) null));
    assertEquals ("", StringHelper.getImploded (".", (List <String>) null));
    assertEquals ("a.b.c", StringHelper.getImploded (".", aList));
    assertEquals ("abc", StringHelper.getImploded ("", aList));
    assertEquals ("abc", StringHelper.getImploded (aList));
    assertEquals ("a.b.c", StringHelper.getImploded (".", aList.toArray (new String [3])));
    assertEquals ("abc", StringHelper.getImploded ("", aList.toArray (new String [3])));
    assertEquals ("abc", StringHelper.getImploded (aList.toArray (new String [3])));

    assertEquals ("abc", StringHelper.getImploded (null, aList));
  }

  @Test
  public void testImplodeArray ()
  {
    final String [] aArray = { "a", "b", "c" };
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

    // null separator
    assertEquals ("abc", StringHelper.getImploded (null, aArray));
    // null separator
    assertEquals ("c", StringHelper.getImploded (null, aArray, 2, 2));
    assertEquals ("a.b", StringHelper.getImploded (".", aArray, -1, 2));
    assertEquals ("ab", StringHelper.getImploded (aArray, -1, 2));
    assertEquals ("a.b.c", StringHelper.getImploded (".", aArray, 0, -1));
    assertEquals ("abc", StringHelper.getImploded (aArray, 0, -1));
    // too long
    assertEquals ("c", StringHelper.getImploded (".", aArray, 2, 2));
    // too long
    assertEquals ("c", StringHelper.getImploded (aArray, 2, 2));
    // too long
    assertEquals ("a.b.c", StringHelper.getImploded (".", aArray, 0, 4));
    // too long
    assertEquals ("abc", StringHelper.getImploded (aArray, 0, 4));
  }

  @Test
  public void testImplodeMap ()
  {
    final ICommonsOrderedMap <String, String> aMap = CollectionHelper.newOrderedMap ("a",
                                                                                     "true",
                                                                                     "b",
                                                                                     "true",
                                                                                     "c",
                                                                                     "false");
    assertEquals ("atruebtruecfalse", StringHelper.getImploded ("", "", aMap));
    assertEquals ("atrue,btrue,cfalse", StringHelper.getImploded (",", "", aMap));
    assertEquals ("a,trueb,truec,false", StringHelper.getImploded ("", ",", aMap));
    assertEquals ("a,true,b,true,c,false", StringHelper.getImploded (",", ",", aMap));
    assertEquals ("a:true,b:true,c:false", StringHelper.getImploded (",", ":", aMap));

    assertEquals ("a:trueb:truec:false", StringHelper.getImploded (null, ":", aMap));
    assertEquals ("atrue,btrue,cfalse", StringHelper.getImploded (",", null, aMap));
  }

  @Test
  public void testGetImplodedNonEmptyIterable ()
  {
    final ICommonsList <String> aList = new CommonsArrayList <> (null, "a", "", "b", null, "c", "");
    assertEquals ("", StringHelper.getImplodedNonEmpty (".", (String []) null));
    assertEquals ("", StringHelper.getImplodedNonEmpty (".", (List <String>) null));
    assertEquals ("a.b.c", StringHelper.getImplodedNonEmpty (".", aList));
    assertEquals ("abc", StringHelper.getImplodedNonEmpty ("", aList));
    assertEquals ("a.b.c", StringHelper.getImplodedNonEmpty (".", aList.toArray (new String [3])));
    assertEquals ("abc", StringHelper.getImplodedNonEmpty ("", aList.toArray (new String [3])));

    StringHelper.getImplodedNonEmpty (null, aList);
  }

  @Test
  public void testGetImplodedNonEmptyArray ()
  {
    final String [] aArray = { null, "a", "", "b", null, "c", "" };
    assertEquals ("a.b", StringHelper.getImplodedNonEmpty (".", aArray, 0, 4));
    assertEquals ("b.c", StringHelper.getImplodedNonEmpty (".", aArray, 2, 4));
    assertEquals ("", StringHelper.getImplodedNonEmpty (".", aArray, 0, 0));
    assertEquals ("", StringHelper.getImplodedNonEmpty (".", aArray, 4, 0));
    assertEquals ("", StringHelper.getImplodedNonEmpty (".", null, 4, 0));

    StringHelper.getImplodedNonEmpty (null, aArray, 2, 2);
    StringHelper.getImplodedNonEmpty (".", aArray, -1, 2);
    StringHelper.getImplodedNonEmpty (".", aArray, 0, -1);
    // too long
    StringHelper.getImplodedNonEmpty (".", aArray, 6, 2);
    // too long
    StringHelper.getImplodedNonEmpty (".", aArray, 0, 8);
    StringHelper.getImplodedNonEmpty (null, aArray);
  }

  @Test
  public void testGetExplodedToList ()
  {
    ICommonsList <String> ret = StringHelper.getExploded ("@", "a@b@@c");
    assertEquals (new CommonsArrayList <> ("a", "b", "", "c"), ret);
    ret = StringHelper.getExploded ("uu", "auubuuuuuuc");
    assertEquals (new CommonsArrayList <> ("a", "b", "", "", "c"), ret);
    ret = StringHelper.getExploded (".", "a.b...c");
    assertEquals (new CommonsArrayList <> ("a", "b", "", "", "c"), ret);
    ret = StringHelper.getExploded ("o", "boo:and:foo");
    assertEquals (new CommonsArrayList <> ("b", "", ":and:f", "", ""), ret);
    ret = StringHelper.getExploded ("@", "@a@b@@c");
    assertEquals (new CommonsArrayList <> ("", "a", "b", "", "c"), ret);
    ret = StringHelper.getExploded ("@", "a@b@@c@");
    assertEquals (new CommonsArrayList <> ("a", "b", "", "c", ""), ret);
    ret = StringHelper.getExploded ("@", "@a@b@@c@");
    assertEquals (new CommonsArrayList <> ("", "a", "b", "", "c", ""), ret);
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
    assertEquals (new CommonsArrayList <> ("a", "b", "", "c"), StringHelper.getExploded ("@", "a@b@@c", 5));
    assertEquals (new CommonsArrayList <> ("a", "b", "", "c"), StringHelper.getExploded ("@", "a@b@@c", 4));
    assertEquals (new CommonsArrayList <> ("a", "b", "@c"), StringHelper.getExploded ("@", "a@b@@c", 3));
    assertEquals (new CommonsArrayList <> ("a", "b@@c"), StringHelper.getExploded ("@", "a@b@@c", 2));
    assertEquals (new CommonsArrayList <> ("a@b@@c"), StringHelper.getExploded ("@", "a@b@@c", 1));
    assertEquals (new CommonsArrayList <> ("a", "b", "", "c"), StringHelper.getExploded ("@", "a@b@@c", 0));
    assertEquals (new CommonsArrayList <> ("a", "b", "", "c"), StringHelper.getExploded ("@", "a@b@@c", -1));
    assertEquals (new CommonsArrayList <> ("a", "b", "", "c"), StringHelper.getExploded ("@", "a@b@@c", -2));
    assertEquals (new CommonsArrayList <> ("", "b", ""), StringHelper.getExploded ("@", "@b@", -2));
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
    assertArrayEquals (new String [] { "", "b", "" }, StringHelper.getExplodedArray ('@', "@b@", -2));
    assertTrue (StringHelper.getExplodedArray ('@', null, 5).length == 0);
  }

  @Test
  public void testExplodeToSet ()
  {
    ICommonsSet <String> ret = StringHelper.getExplodedToSet ("@", "a@b@@c");
    assertEquals (new CommonsHashSet <> ("a", "b", "", "c"), ret);
    ret = StringHelper.getExplodedToSet ("uu", "auubuuuuuuc");
    assertEquals (new CommonsHashSet <> ("a", "b", "", "", "c"), ret);
    ret = StringHelper.getExplodedToSet (".", "a.b...c");
    assertEquals (new CommonsHashSet <> ("a", "b", "", "", "c"), ret);
    ret = StringHelper.getExplodedToSet ("o", "boo:and:foo");
    assertEquals (new CommonsHashSet <> ("b", "", ":and:f", "", ""), ret);
    ret = StringHelper.getExplodedToSet ("@", "@a@b@@c");
    assertEquals (new CommonsHashSet <> ("", "a", "b", "", "c"), ret);
    ret = StringHelper.getExplodedToSet ("@", "a@b@@c@");
    assertEquals (new CommonsHashSet <> ("a", "b", "", "c", ""), ret);
    ret = StringHelper.getExplodedToSet ("@", "@a@b@@c@");
    assertEquals (new CommonsHashSet <> ("", "a", "b", "", "c", ""), ret);
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
    ICommonsOrderedSet <String> ret = StringHelper.getExplodedToOrderedSet ("@", "a@b@@c");
    assertEquals (new CommonsHashSet <> ("a", "b", "", "c"), ret);
    ret = StringHelper.getExplodedToOrderedSet ("uu", "auubuuuuuuc");
    assertEquals (new CommonsHashSet <> ("a", "b", "", "", "c"), ret);
    ret = StringHelper.getExplodedToOrderedSet (".", "a.b...c");
    assertEquals (new CommonsHashSet <> ("a", "b", "", "", "c"), ret);
    ret = StringHelper.getExplodedToOrderedSet ("o", "boo:and:foo");
    assertEquals (new CommonsHashSet <> ("b", "", ":and:f", "", ""), ret);
    ret = StringHelper.getExplodedToOrderedSet ("@", "@a@b@@c");
    assertEquals (new CommonsHashSet <> ("", "a", "b", "", "c"), ret);
    ret = StringHelper.getExplodedToOrderedSet ("@", "a@b@@c@");
    assertEquals (new CommonsHashSet <> ("a", "b", "", "c", ""), ret);
    ret = StringHelper.getExplodedToOrderedSet ("@", "@a@b@@c@");
    assertEquals (new CommonsHashSet <> ("", "a", "b", "", "c", ""), ret);
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
  public void testGetRepeated ()
  {
    assertEquals ("", Strings.getRepeated ('a', 0));
    assertEquals ("a", Strings.getRepeated ('a', 1));
    assertEquals ("aaa", Strings.getRepeated ('a', 3));
    assertEquals ("  ", Strings.getRepeated (' ', 2));
    try
    {
      Strings.getRepeated (' ', -1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    assertEquals ("", Strings.getRepeated ("a", 0));
    assertEquals ("a", Strings.getRepeated ("a", 1));
    assertEquals ("aaa", Strings.getRepeated ("a", 3));
    assertEquals ("ababab", Strings.getRepeated ("ab", 3));
    assertEquals ("  ", Strings.getRepeated (" ", 2));
    try
    {
      Strings.getRepeated (null, 5);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      Strings.getRepeated (" ", -1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    // Check overflow
    try
    {
      Strings.getRepeated ("  ", Integer.MAX_VALUE);
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

    final char [] aStart = { 'a', 'b', 'c' };
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

    final char [] aEnd = { 'a', 'b', 'c' };
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
      assertEquals (i, StringHelper.getCharCount (Strings.getRepeated ('x', i), 'x'));
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
    assertEquals ("abc", Strings.getNotNull ("abc"));
    assertEquals ("", Strings.getNotNull (""));
    assertEquals ("", Strings.getNotNull (null));
    assertEquals ("bla", Strings.getNotNull (null, "bla"));
    assertEquals ("bla", Strings.getNotNull (null, () -> "bla"));
  }

  @Test
  public void testGetNotEmptyString ()
  {
    assertEquals ("abc", Strings.getNotEmpty ("abc", "bla"));
    assertEquals ("bla", Strings.getNotEmpty ("", "bla"));
    assertEquals ("bla", Strings.getNotEmpty (null, "bla"));
    assertEquals ("bla", Strings.getNotEmpty (null, "bla"));
    assertEquals ("bla", Strings.getNotEmpty (null, () -> "bla"));
  }

  @Test
  public void testGetNotNullCharSeq ()
  {
    assertEquals ("abc", Strings.getNotNull (new StringBuilder ("abc")).toString ());
    assertEquals ("", Strings.getNotNull (new StringBuilder ()).toString ());
    assertEquals ("", Strings.getNotNull ((StringBuilder) null));
    assertEquals ("bla", Strings.getNotNull ((StringBuilder) null, () -> "bla"));
  }

  @Test
  public void testGetNotEmptyCharSeq ()
  {
    assertEquals ("abc", Strings.getNotEmpty (new StringBuilder ("abc"), "bla").toString ());
    assertEquals ("bla", Strings.getNotEmpty (new StringBuilder (), "bla").toString ());
    assertEquals ("bla", Strings.getNotEmpty ((StringBuilder) null, "bla"));
    assertEquals ("bla", Strings.getNotEmpty ((StringBuilder) null, () -> "bla"));
  }

  @Test
  public void testReplaceAllString ()
  {
    assertEquals ("abc", Strings.replaceAll ("abc", "d", "e"));
    assertEquals ("abd", Strings.replaceAll ("abc", "c", "d"));
    assertEquals ("adc", Strings.replaceAll ("abc", "b", "d"));
    assertEquals ("dbc", Strings.replaceAll ("abc", "a", "d"));
    assertEquals ("ddd", Strings.replaceAll ("aaa", "a", "d"));
    assertEquals ("xyxyxy", Strings.replaceAll ("aaa", "a", "xy"));
    assertEquals ("", Strings.replaceAll ("", "anything", "nothing"));
    assertEquals ("", Strings.replaceAll ("aaa", "a", ""));
    assertEquals ("bb", Strings.replaceAll ("ababa", "a", ""));
    assertEquals ("acd", Strings.replaceAll ("abcd", "ab", "a"));
    assertEquals ("abd", Strings.replaceAll ("abcd", "bc", "b"));
    assertEquals ("abc", Strings.replaceAll ("abcd", "cd", "c"));
    assertEquals ("abc", Strings.replaceAll ("abcd", "d", ""));
    assertEquals ("bcbc", Strings.replaceAll ("bcbcbc", "bcbc", "bc"));
    assertEquals ("aa", Strings.replaceAll ("aaaa", "aa", "a"));
    assertEquals ("a  a b ", Strings.replaceAll ("a    a  b ", "  ", " "));
    assertNull (Strings.replaceAll (null, "aa", "a"));
    assertEquals ("aaaa", Strings.replaceAll ("aaaa", "aa", "aa"));

    try
    {
      Strings.replaceAll ("aaaaach", null, "a");
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      Strings.replaceAll ("aaaaach", "aa", null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testReplaceAllChar ()
  {
    assertEquals ("abc", Strings.replaceAll ("abc", 'd', 'e'));
    assertEquals ("abd", Strings.replaceAll ("abc", 'c', 'd'));
    assertEquals ("adc", Strings.replaceAll ("abc", 'b', 'd'));
    assertEquals ("dbc", Strings.replaceAll ("abc", 'a', 'd'));
    assertEquals ("ddd", Strings.replaceAll ("aaa", 'a', 'd'));
    assertEquals ("", Strings.replaceAll ("", 'a', 'b'));
    assertEquals ("aaa", Strings.replaceAll ("aaa", 'a', 'a'));
    assertEquals ("aaa", Strings.replaceAll ("aaa", 'b', 'b'));
    assertEquals ("bbbbb", Strings.replaceAll ("ababa", 'a', 'b'));
    assertEquals ("\0b\0b\0", Strings.replaceAll ("ababa", 'a', '\0'));
  }

  @Test
  public void testReplaceAllSafe ()
  {
    assertEquals ("abc", Strings.replaceAllSafe ("abc", "d", "e"));
    assertEquals ("abd", Strings.replaceAllSafe ("abc", "c", "d"));
    assertEquals ("adc", Strings.replaceAllSafe ("abc", "b", "d"));
    assertEquals ("dbc", Strings.replaceAllSafe ("abc", "a", "d"));
    assertEquals ("ddd", Strings.replaceAllSafe ("aaa", "a", "d"));
    assertEquals ("xyxyxy", Strings.replaceAllSafe ("aaa", "a", "xy"));
    assertEquals ("", Strings.replaceAllSafe ("", "anything", "nothing"));
    assertEquals ("", Strings.replaceAllSafe ("aaa", "a", ""));
    assertEquals ("bb", Strings.replaceAllSafe ("ababa", "a", ""));
    assertEquals ("acd", Strings.replaceAllSafe ("abcd", "ab", "a"));
    assertEquals ("abd", Strings.replaceAllSafe ("abcd", "bc", "b"));
    assertEquals ("abc", Strings.replaceAllSafe ("abcd", "cd", "c"));
    assertEquals ("abc", Strings.replaceAllSafe ("abcd", "d", ""));
    assertEquals ("bcbc", Strings.replaceAllSafe ("bcbcbc", "bcbc", "bc"));
    assertEquals ("aa", Strings.replaceAllSafe ("aaaa", "aa", "a"));
    assertEquals ("a  a b ", Strings.replaceAllSafe ("a    a  b ", "  ", " "));
    assertNull (Strings.replaceAllSafe (null, "aa", "a"));
    assertEquals ("aaaa", Strings.replaceAllSafe ("aaaa", "aa", "aa"));

    try
    {
      Strings.replaceAllSafe ("aaaaach", null, "a");
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    assertEquals ("ach", Strings.replaceAllSafe ("aaaaach", "aa", null));
  }

  @Test
  public void testReplaceAllRepeatedly ()
  {
    assertEquals ("abc", Strings.replaceAllRepeatedly ("abc", "d", "e"));
    assertEquals ("dbc", Strings.replaceAllRepeatedly ("abc", "a", "d"));
    assertEquals ("ddd", Strings.replaceAllRepeatedly ("aaa", "a", "d"));
    assertEquals ("a a b ", Strings.replaceAllRepeatedly ("a    a  b ", "  ", " "));
    assertEquals ("", Strings.replaceAllRepeatedly ("", " a", "b"));
    assertNull (Strings.replaceAllRepeatedly (null, " a", "b"));

    try
    {
      Strings.replaceAllRepeatedly ("aaaaach", null, "a");
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      Strings.replaceAllRepeatedly ("aaaaach", "aa", null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      Strings.replaceAllRepeatedly ("aaaaach", "a", "aa");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testReplaceMultipleMap ()
  {
    final ICommonsMap <String, String> aMap = new CommonsHashMap <> ();
    aMap.put ("Hallo", "Hi");
    aMap.put ("Welt", "world");
    aMap.put ("!", "???");
    assertEquals ("Abc die Katze lief im Schnee", Strings.replaceMultiple ("Abc die Katze lief im Schnee", aMap));
    assertEquals ("Hi Katze", Strings.replaceMultiple ("Hallo Katze", aMap));
    assertEquals ("Moin world", Strings.replaceMultiple ("Moin Welt", aMap));
    assertEquals ("Moin welt", Strings.replaceMultiple ("Moin welt", aMap));
    assertEquals ("Hi", Strings.replaceMultiple ("Hallo", aMap));
    assertEquals ("Hi Hi", Strings.replaceMultiple ("Hallo Hallo", aMap));
    assertEquals ("HiHiHi", Strings.replaceMultiple ("HalloHalloHallo", aMap));
    assertEquals ("Hi world???", Strings.replaceMultiple ("Hallo Welt!", aMap));
    assertEquals ("Hi world???Hi world???", Strings.replaceMultiple ("Hallo Welt!Hallo Welt!", aMap));
  }

  @Test
  public void testReplaceMultipleCharArrays ()
  {
    assertArrayEquals ("bb".toCharArray (),
                       Strings.replaceMultiple ("a", new char [] { 'a' }, new char [] [] { "bb".toCharArray () }));
    assertArrayEquals ("bbbb".toCharArray (),
                       Strings.replaceMultiple ("aa", new char [] { 'a' }, new char [] [] { "bb".toCharArray () }));
    assertArrayEquals ("cdc".toCharArray (),
                       Strings.replaceMultiple ("cdc", new char [] { 'a' }, new char [] [] { "bb".toCharArray () }));
    assertArrayEquals ("cbbc".toCharArray (),
                       Strings.replaceMultiple ("cac", new char [] { 'a' }, new char [] [] { "bb".toCharArray () }));
    assertArrayEquals ("ddbbdd".toCharArray (),
                       Strings.replaceMultiple ("cac",
                                                new char [] { 'a', 'c' },
                                                new char [] [] { "bb".toCharArray (), "dd".toCharArray () }));
    assertArrayEquals ("<ddbbdd>".toCharArray (),
                       Strings.replaceMultiple ("<cac>",
                                                new char [] { 'a', 'c' },
                                                new char [] [] { "bb".toCharArray (), "dd".toCharArray () }));
    assertArrayEquals (new char [0],
                       Strings.replaceMultiple ("",
                                                new char [] { 'a', 'c' },
                                                new char [] [] { "bb".toCharArray (), "dd".toCharArray () }));
    assertArrayEquals ("any".toCharArray (), Strings.replaceMultiple ("any", new char [0], new char [0] []));
    try
    {
      Strings.replaceMultiple ("any", (char []) null, new char [] [] { "bb".toCharArray (), "dd".toCharArray () });
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      Strings.replaceMultiple ("any", "an".toCharArray (), null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      Strings.replaceMultiple ("any", new char [1], new char [2] []);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testReplaceMultipleTo () throws IOException
  {
    NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    Strings.replaceMultipleTo ("a", new char [] { 'a' }, new char [] [] { "bb".toCharArray () }, aSW);
    assertEquals ("bb", aSW.getAsString ());

    aSW = new NonBlockingStringWriter ();
    Strings.replaceMultipleTo ("aa", new char [] { 'a' }, new char [] [] { "bb".toCharArray () }, aSW);
    assertEquals ("bbbb", aSW.getAsString ());

    aSW = new NonBlockingStringWriter ();
    Strings.replaceMultipleTo ("cdc", new char [] { 'a' }, new char [] [] { "bb".toCharArray () }, aSW);
    assertEquals ("cdc", aSW.getAsString ());

    aSW = new NonBlockingStringWriter ();
    Strings.replaceMultipleTo ("cac", new char [] { 'a' }, new char [] [] { "bb".toCharArray () }, aSW);
    assertEquals ("cbbc", aSW.getAsString ());

    aSW = new NonBlockingStringWriter ();
    Strings.replaceMultipleTo ("cac",
                               new char [] { 'a', 'c' },
                               new char [] [] { "bb".toCharArray (), "dd".toCharArray () },
                               aSW);
    assertEquals ("ddbbdd", aSW.getAsString ());

    aSW = new NonBlockingStringWriter ();
    Strings.replaceMultipleTo ("<cac>",
                               new char [] { 'a', 'c' },
                               new char [] [] { "bb".toCharArray (), "dd".toCharArray () },
                               aSW);
    assertEquals ("<ddbbdd>", aSW.getAsString ());

    aSW = new NonBlockingStringWriter ();
    Strings.replaceMultipleTo ("",
                               new char [] { 'a', 'c' },
                               new char [] [] { "bb".toCharArray (), "dd".toCharArray () },
                               aSW);
    assertEquals ("", aSW.getAsString ());

    aSW = new NonBlockingStringWriter ();
    Strings.replaceMultipleTo ("any", new char [0], new char [0] [], aSW);
    assertEquals ("any", aSW.getAsString ());

    try
    {
      Strings.replaceMultipleTo ("any", null, new char [0] [], aSW);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      Strings.replaceMultipleTo ("any", new char [0], null, aSW);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      Strings.replaceMultipleTo ("any", new char [0], new char [1] [], aSW);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      Strings.replaceMultipleTo ("any", new char [0], new char [0] [], null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testReplaceMultipleCharArrayToChar ()
  {
    assertArrayEquals ("abc".toCharArray (), Strings.replaceMultiple ("abc", "def".toCharArray (), ' '));
    assertArrayEquals ("   ".toCharArray (), Strings.replaceMultiple ("abc", "abc".toCharArray (), ' '));
    assertArrayEquals ("   ".toCharArray (), Strings.replaceMultiple ("abc", "abcabc".toCharArray (), ' '));
    assertArrayEquals ("   ".toCharArray (), Strings.replaceMultiple ("abc", "aabbcc".toCharArray (), ' '));
    assertArrayEquals ("      ".toCharArray (), Strings.replaceMultiple ("abcabc", "abc".toCharArray (), ' '));
    assertArrayEquals ("      ".toCharArray (), Strings.replaceMultiple ("aabbcc", "abc".toCharArray (), ' '));
    assertArrayEquals ("a  ".toCharArray (), Strings.replaceMultiple ("abc", "bc".toCharArray (), ' '));
    assertArrayEquals (" b ".toCharArray (), Strings.replaceMultiple ("abc", "ac".toCharArray (), ' '));
    assertArrayEquals ("  c".toCharArray (), Strings.replaceMultiple ("abc", "ab".toCharArray (), ' '));
    assertArrayEquals ("abc".toCharArray (), Strings.replaceMultiple ("abc", "".toCharArray (), ' '));
    assertArrayEquals ("".toCharArray (), Strings.replaceMultiple ("", "abc".toCharArray (), ' '));
    assertArrayEquals ("".toCharArray (), Strings.replaceMultiple ("", "".toCharArray (), ' '));
    assertArrayEquals ("".toCharArray (), Strings.replaceMultiple (null, "abc".toCharArray (), ' '));
    assertArrayEquals ("".toCharArray (), Strings.replaceMultiple (null, "".toCharArray (), ' '));
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
    final Integer I1 = Integer.valueOf (1);
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

  @Test
  public void testContainsAnyOnlyNoneString ()
  {
    assertTrue (StringHelper.containsAny ("aa", x -> x == 'a'));
    assertTrue (StringHelper.containsAny ("abc", x -> x == 'a'));
    assertTrue (StringHelper.containsAny ("abc", x -> x == 'b'));
    assertTrue (StringHelper.containsAny ("abc", (ICharPredicate) null));
    assertFalse (StringHelper.containsAny ("", (ICharPredicate) null));
    assertFalse (StringHelper.containsAny ((String) null, (ICharPredicate) null));
    assertFalse (StringHelper.containsAny ("", x -> x == 'a'));
    assertFalse (StringHelper.containsAny ((String) null, x -> x == 'a'));
    assertFalse (StringHelper.containsAny ("abc", x -> x == 'd'));

    assertTrue (StringHelper.containsOnly ("aa", x -> x == 'a'));
    assertFalse (StringHelper.containsOnly ("abc", x -> x == 'a'));
    assertFalse (StringHelper.containsOnly ("abc", x -> x == 'b'));
    assertTrue (StringHelper.containsOnly ("abc", (ICharPredicate) null));
    assertFalse (StringHelper.containsOnly ("", (ICharPredicate) null));
    assertFalse (StringHelper.containsOnly ((String) null, (ICharPredicate) null));
    assertFalse (StringHelper.containsOnly ("", x -> x == 'a'));
    assertFalse (StringHelper.containsOnly ((String) null, x -> x == 'a'));
    assertFalse (StringHelper.containsOnly ("abc", x -> x == 'd'));

    assertFalse (StringHelper.containsNone ("aa", x -> x == 'a'));
    assertFalse (StringHelper.containsNone ("abc", x -> x == 'a'));
    assertFalse (StringHelper.containsNone ("abc", x -> x == 'b'));
    assertFalse (StringHelper.containsNone ("abc", (ICharPredicate) null));
    assertTrue (StringHelper.containsNone ("", (ICharPredicate) null));
    assertTrue (StringHelper.containsNone ((String) null, (ICharPredicate) null));
    assertTrue (StringHelper.containsNone ("", x -> x == 'a'));
    assertTrue (StringHelper.containsNone ((String) null, x -> x == 'a'));
    assertTrue (StringHelper.containsNone ("abc", x -> x == 'd'));
  }

  @Test
  public void testContainsAnyOnlyNoneCharSequence ()
  {
    assertTrue (StringHelper.containsAny ((CharSequence) "aa", x -> x == 'a'));
    assertTrue (StringHelper.containsAny ((CharSequence) "abc", x -> x == 'a'));
    assertTrue (StringHelper.containsAny ((CharSequence) "abc", x -> x == 'b'));
    assertTrue (StringHelper.containsAny ((CharSequence) "abc", (ICharPredicate) null));
    assertFalse (StringHelper.containsAny ((CharSequence) "", (ICharPredicate) null));
    assertFalse (StringHelper.containsAny ((CharSequence) null, (ICharPredicate) null));
    assertFalse (StringHelper.containsAny ((CharSequence) "", x -> x == 'a'));
    assertFalse (StringHelper.containsAny ((CharSequence) null, x -> x == 'a'));
    assertFalse (StringHelper.containsAny ((CharSequence) "abc", x -> x == 'd'));

    assertTrue (StringHelper.containsOnly ((CharSequence) "aa", x -> x == 'a'));
    assertFalse (StringHelper.containsOnly ((CharSequence) "abc", x -> x == 'a'));
    assertFalse (StringHelper.containsOnly ((CharSequence) "abc", x -> x == 'b'));
    assertTrue (StringHelper.containsOnly ((CharSequence) "abc", (ICharPredicate) null));
    assertFalse (StringHelper.containsOnly ((CharSequence) "", (ICharPredicate) null));
    assertFalse (StringHelper.containsOnly ((CharSequence) null, (ICharPredicate) null));
    assertFalse (StringHelper.containsOnly ((CharSequence) "", x -> x == 'a'));
    assertFalse (StringHelper.containsOnly ((CharSequence) null, x -> x == 'a'));
    assertFalse (StringHelper.containsOnly ((CharSequence) "abc", x -> x == 'd'));

    assertFalse (StringHelper.containsNone ((CharSequence) "aa", x -> x == 'a'));
    assertFalse (StringHelper.containsNone ((CharSequence) "abc", x -> x == 'a'));
    assertFalse (StringHelper.containsNone ((CharSequence) "abc", x -> x == 'b'));
    assertFalse (StringHelper.containsNone ((CharSequence) "abc", (ICharPredicate) null));
    assertTrue (StringHelper.containsNone ((CharSequence) "", (ICharPredicate) null));
    assertTrue (StringHelper.containsNone ((CharSequence) null, (ICharPredicate) null));
    assertTrue (StringHelper.containsNone ((CharSequence) "", x -> x == 'a'));
    assertTrue (StringHelper.containsNone ((CharSequence) null, x -> x == 'a'));
    assertTrue (StringHelper.containsNone ((CharSequence) "abc", x -> x == 'd'));
  }

  @Test
  public void testIsAllWhitespace ()
  {
    assertTrue (StringHelper.isAllWhitespace ("   "));
    assertTrue (StringHelper.isAllWhitespace (" \t\r\n"));
    assertTrue (StringHelper.isAllWhitespace ("\n"));

    assertFalse (StringHelper.isAllWhitespace (""));
    assertFalse (StringHelper.isAllWhitespace (null));
    assertFalse (StringHelper.isAllWhitespace ("a"));
    assertFalse (StringHelper.isAllWhitespace ("abc"));
    assertFalse (StringHelper.isAllWhitespace ("ab c"));
    assertFalse (StringHelper.isAllWhitespace (" a"));
  }

  @Test
  public void testGetQuoted ()
  {
    assertEquals ("null", StringHelper.getQuoted (null));
    assertEquals ("''", StringHelper.getQuoted (""));
    assertEquals ("'abc'", StringHelper.getQuoted ("abc"));
    assertEquals ("'aBc'", StringHelper.getQuoted ("aBc"));
  }

  @Test
  public void testAppendQuotedSB ()
  {
    final Function <String, String> quote = x -> {
      final StringBuilder aSB = new StringBuilder ();
      StringHelper.appendQuoted (aSB, x);
      return aSB.toString ();
    };
    assertEquals ("null", quote.apply (null));
    assertEquals ("''", quote.apply (""));
    assertEquals ("'abc'", quote.apply ("abc"));
    assertEquals ("'aBc'", quote.apply ("aBc"));
  }

  @Test
  public void testAppendQuotedWriter () throws IOException
  {
    final IThrowingFunction <String, String, IOException> quote = x -> {
      final NonBlockingStringWriter aSB = new NonBlockingStringWriter ();
      StringHelper.appendQuoted (aSB, x);
      return aSB.getAsString ();
    };
    assertEquals ("null", quote.apply (null));
    assertEquals ("''", quote.apply (""));
    assertEquals ("'abc'", quote.apply ("abc"));
    assertEquals ("'aBc'", quote.apply ("aBc"));
  }
}
