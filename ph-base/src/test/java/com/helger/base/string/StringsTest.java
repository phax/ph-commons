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
package com.helger.base.string;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Locale;

import org.junit.Test;

import com.helger.base.CGlobal;
import com.helger.base.functional.ICharPredicate;

/**
 * Test class for class {@link Strings}.
 *
 * @author Philip Helger
 */
public final class StringsTest
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
  public void testGetReverse ()
  {
    assertNull (Strings.getReverse (null));
    assertEquals ("", Strings.getReverse (""));
    assertEquals ("a", Strings.getReverse ("a"));
    assertEquals ("ba", Strings.getReverse ("ab"));
    assertEquals (" ba", Strings.getReverse ("ab "));
    assertEquals ("cba", Strings.getReverse ("abc"));
  }

  @Test
  public void testGetFirstChar ()
  {
    assertEquals ('a', Strings.getFirstChar ("abc"));
    assertEquals ('a', Strings.getFirstChar ("a"));
    assertEquals (CGlobal.ILLEGAL_CHAR, Strings.getFirstChar (""));
    assertEquals (CGlobal.ILLEGAL_CHAR, Strings.getFirstChar ((CharSequence) null));
    assertEquals (CGlobal.ILLEGAL_CHAR, Strings.getFirstChar ((char []) null));
  }

  @Test
  public void testGetLastChar ()
  {
    assertEquals ('c', Strings.getLastChar ("abc"));
    assertEquals ('a', Strings.getLastChar ("a"));
    assertEquals (CGlobal.ILLEGAL_CHAR, Strings.getLastChar (""));
    assertEquals (CGlobal.ILLEGAL_CHAR, Strings.getLastChar ((CharSequence) null));
    assertEquals (CGlobal.ILLEGAL_CHAR, Strings.getLastChar ((char []) null));
  }

  @Test
  public void testGetIndexOfString ()
  {
    assertEquals (-1, Strings.getIndexOf (null, null));
    assertEquals (-1, Strings.getIndexOf (null, "a"));
    assertEquals (-1, Strings.getIndexOf ("b", null));
    assertEquals (-1, Strings.getIndexOf ("b", "cd"));
    assertEquals (-1, Strings.getIndexOf ("bla fob", "z"));
    assertEquals (0, Strings.getIndexOf ("bla fob", "b"));
    assertEquals (2, Strings.getIndexOf ("bla fob", "a"));
  }

  @Test
  public void testGetLastIndexOfString ()
  {
    assertEquals (-1, Strings.getLastIndexOf (null, null));
    assertEquals (-1, Strings.getLastIndexOf (null, "a"));
    assertEquals (-1, Strings.getLastIndexOf ("b", null));
    assertEquals (-1, Strings.getLastIndexOf ("b", "cd"));
    assertEquals (-1, Strings.getLastIndexOf ("bla fob", "z"));
    assertEquals (6, Strings.getLastIndexOf ("bla fob", "b"));
    assertEquals (2, Strings.getLastIndexOf ("bla fob", "a"));
  }

  @Test
  public void testGetIndexOfChar ()
  {
    assertEquals (-1, Strings.getIndexOf (null, '\0'));
    assertEquals (-1, Strings.getIndexOf (null, 'a'));
    assertEquals (-1, Strings.getIndexOf ("b", '\0'));
    assertEquals (-1, Strings.getIndexOf ("b", 'c'));
    assertEquals (-1, Strings.getIndexOf ("bla fob", 'z'));
    assertEquals (0, Strings.getIndexOf ("bla fob", 'b'));
    assertEquals (2, Strings.getIndexOf ("bla fob", 'a'));
  }

  @Test
  public void testGetLastIndexOfChar ()
  {
    assertEquals (-1, Strings.getLastIndexOf (null, '\0'));
    assertEquals (-1, Strings.getLastIndexOf (null, 'a'));
    assertEquals (-1, Strings.getLastIndexOf ("b", '\0'));
    assertEquals (-1, Strings.getLastIndexOf ("b", 'c'));
    assertEquals (-1, Strings.getLastIndexOf ("bla fob", 'z'));
    assertEquals (6, Strings.getLastIndexOf ("bla fob", 'b'));
    assertEquals (2, Strings.getLastIndexOf ("bla fob", 'a'));
  }

  @Test
  public void testContainsString ()
  {
    assertTrue (Strings.contains ("Test", "Test"));
    assertTrue (Strings.contains ("Test", "est"));
    assertTrue (Strings.contains ("Test", "Tes"));
    assertTrue (Strings.contains ("Test", "es"));
    assertTrue (Strings.contains ("Test", ""));

    assertFalse (Strings.contains ("Test", null));
    assertFalse (Strings.contains (null, "Test"));
    assertFalse (Strings.contains ("Tes", "Test"));
    assertFalse (Strings.contains ("est", "Test"));
    assertFalse (Strings.contains ("es", "Test"));
    assertFalse (Strings.contains ("", "Test"));

    assertFalse (Strings.contains ("Test", "TEST"));
    assertFalse (Strings.contains ("Test", "EST"));
    assertFalse (Strings.contains ("Test", "TES"));
    assertFalse (Strings.contains ("Test", "ES"));
  }

  @Test
  public void testContainsChar ()
  {
    assertTrue (Strings.contains ("Test", 'T'));
    assertTrue (Strings.contains ("Test", 'e'));
    assertTrue (Strings.contains ("Test", 's'));
    assertTrue (Strings.contains ("Test", 't'));
    assertFalse (Strings.contains ("Test", '\0'));

    assertFalse (Strings.contains ("Test", null));
    assertFalse (Strings.contains (null, 'T'));
  }

  @Test
  public void testMultiContains ()
  {
    final char [] aIn = "abcde".toCharArray ();
    assertTrue (Strings.containsAny (aIn, "a".toCharArray ()));
    assertFalse (Strings.containsAny (aIn, "z".toCharArray ()));
    assertFalse (Strings.containsAny (aIn, new char [0]));
    assertFalse (Strings.containsAny (new char [0], "a".toCharArray ()));
  }

  @Test
  public void testIndexOfIgnoreCaseString ()
  {
    assertEquals (-1, Strings.getIndexOfIgnoreCase (null, null, L_DE));
    assertEquals (-1, Strings.getIndexOfIgnoreCase (null, "a", L_DE));
    assertEquals (-1, Strings.getIndexOfIgnoreCase ("b", null, L_DE));
    assertEquals (-1, Strings.getIndexOfIgnoreCase ("b", "cd", L_DE));
    assertEquals (-1, Strings.getIndexOfIgnoreCase ("bla foo", "z", L_DE));
    assertEquals (0, Strings.getIndexOfIgnoreCase ("bla foo", "b", L_DE));
    assertEquals (2, Strings.getIndexOfIgnoreCase ("bla foo", "a", L_DE));
    assertEquals (0, Strings.getIndexOfIgnoreCase ("bla foo", "B", L_DE));
    assertEquals (2, Strings.getIndexOfIgnoreCase ("bla foo", "A", L_DE));
    assertEquals (0, Strings.getIndexOfIgnoreCase ("BLA FOO", "b", L_DE));
    assertEquals (2, Strings.getIndexOfIgnoreCase ("BLA FOO", "a", L_DE));
  }

  @Test
  public void testContainsIgnoreCaseString ()
  {
    final Locale aLocale = Locale.ENGLISH;
    assertTrue (Strings.containsIgnoreCase ("Test", "Test", aLocale));
    assertTrue (Strings.containsIgnoreCase ("Test", "est", aLocale));
    assertTrue (Strings.containsIgnoreCase ("Test", "Tes", aLocale));
    assertTrue (Strings.containsIgnoreCase ("Test", "es", aLocale));
    assertTrue (Strings.containsIgnoreCase ("Test", "", aLocale));

    assertFalse (Strings.containsIgnoreCase ("Test", null, aLocale));
    assertFalse (Strings.containsIgnoreCase (null, "Test", aLocale));
    assertFalse (Strings.containsIgnoreCase ("Tes", "Test", aLocale));
    assertFalse (Strings.containsIgnoreCase ("est", "Test", aLocale));
    assertFalse (Strings.containsIgnoreCase ("es", "Test", aLocale));
    assertFalse (Strings.containsIgnoreCase ("", "Test", aLocale));

    assertTrue (Strings.containsIgnoreCase ("Test", "TEST", aLocale));
    assertTrue (Strings.containsIgnoreCase ("Test", "EST", aLocale));
    assertTrue (Strings.containsIgnoreCase ("Test", "TES", aLocale));
    assertTrue (Strings.containsIgnoreCase ("Test", "ES", aLocale));
  }

  @Test
  public void testContainsAnyOnlyNoneString ()
  {
    assertTrue (Strings.containsAny ("aa", x -> x == 'a'));
    assertTrue (Strings.containsAny ("abc", x -> x == 'a'));
    assertTrue (Strings.containsAny ("abc", x -> x == 'b'));
    assertTrue (Strings.containsAny ("abc", (ICharPredicate) null));
    assertFalse (Strings.containsAny ("", (ICharPredicate) null));
    assertFalse (Strings.containsAny ((String) null, (ICharPredicate) null));
    assertFalse (Strings.containsAny ("", x -> x == 'a'));
    assertFalse (Strings.containsAny ((String) null, x -> x == 'a'));
    assertFalse (Strings.containsAny ("abc", x -> x == 'd'));

    assertTrue (Strings.containsOnly ("aa", x -> x == 'a'));
    assertFalse (Strings.containsOnly ("abc", x -> x == 'a'));
    assertFalse (Strings.containsOnly ("abc", x -> x == 'b'));
    assertTrue (Strings.containsOnly ("abc", (ICharPredicate) null));
    assertFalse (Strings.containsOnly ("", (ICharPredicate) null));
    assertFalse (Strings.containsOnly ((String) null, (ICharPredicate) null));
    assertFalse (Strings.containsOnly ("", x -> x == 'a'));
    assertFalse (Strings.containsOnly ((String) null, x -> x == 'a'));
    assertFalse (Strings.containsOnly ("abc", x -> x == 'd'));

    assertFalse (Strings.containsNone ("aa", x -> x == 'a'));
    assertFalse (Strings.containsNone ("abc", x -> x == 'a'));
    assertFalse (Strings.containsNone ("abc", x -> x == 'b'));
    assertFalse (Strings.containsNone ("abc", (ICharPredicate) null));
    assertTrue (Strings.containsNone ("", (ICharPredicate) null));
    assertTrue (Strings.containsNone ((String) null, (ICharPredicate) null));
    assertTrue (Strings.containsNone ("", x -> x == 'a'));
    assertTrue (Strings.containsNone ((String) null, x -> x == 'a'));
    assertTrue (Strings.containsNone ("abc", x -> x == 'd'));
  }

  @Test
  public void testContainsAnyOnlyNoneCharSequence ()
  {
    assertTrue (Strings.containsAny ((CharSequence) "aa", x -> x == 'a'));
    assertTrue (Strings.containsAny ((CharSequence) "abc", x -> x == 'a'));
    assertTrue (Strings.containsAny ((CharSequence) "abc", x -> x == 'b'));
    assertTrue (Strings.containsAny ((CharSequence) "abc", (ICharPredicate) null));
    assertFalse (Strings.containsAny ((CharSequence) "", (ICharPredicate) null));
    assertFalse (Strings.containsAny ((CharSequence) null, (ICharPredicate) null));
    assertFalse (Strings.containsAny ((CharSequence) "", x -> x == 'a'));
    assertFalse (Strings.containsAny ((CharSequence) null, x -> x == 'a'));
    assertFalse (Strings.containsAny ((CharSequence) "abc", x -> x == 'd'));

    assertTrue (Strings.containsOnly ((CharSequence) "aa", x -> x == 'a'));
    assertFalse (Strings.containsOnly ((CharSequence) "abc", x -> x == 'a'));
    assertFalse (Strings.containsOnly ((CharSequence) "abc", x -> x == 'b'));
    assertTrue (Strings.containsOnly ((CharSequence) "abc", (ICharPredicate) null));
    assertFalse (Strings.containsOnly ((CharSequence) "", (ICharPredicate) null));
    assertFalse (Strings.containsOnly ((CharSequence) null, (ICharPredicate) null));
    assertFalse (Strings.containsOnly ((CharSequence) "", x -> x == 'a'));
    assertFalse (Strings.containsOnly ((CharSequence) null, x -> x == 'a'));
    assertFalse (Strings.containsOnly ((CharSequence) "abc", x -> x == 'd'));

    assertFalse (Strings.containsNone ((CharSequence) "aa", x -> x == 'a'));
    assertFalse (Strings.containsNone ((CharSequence) "abc", x -> x == 'a'));
    assertFalse (Strings.containsNone ((CharSequence) "abc", x -> x == 'b'));
    assertFalse (Strings.containsNone ((CharSequence) "abc", (ICharPredicate) null));
    assertTrue (Strings.containsNone ((CharSequence) "", (ICharPredicate) null));
    assertTrue (Strings.containsNone ((CharSequence) null, (ICharPredicate) null));
    assertTrue (Strings.containsNone ((CharSequence) "", x -> x == 'a'));
    assertTrue (Strings.containsNone ((CharSequence) null, x -> x == 'a'));
    assertTrue (Strings.containsNone ((CharSequence) "abc", x -> x == 'd'));
  }

  @Test
  public void testIsAllWhitespace ()
  {
    assertTrue (Strings.isAllWhitespace ("   "));
    assertTrue (Strings.isAllWhitespace (" \t\r\n"));
    assertTrue (Strings.isAllWhitespace ("\n"));

    assertFalse (Strings.isAllWhitespace (""));
    assertFalse (Strings.isAllWhitespace (null));
    assertFalse (Strings.isAllWhitespace ("a"));
    assertFalse (Strings.isAllWhitespace ("abc"));
    assertFalse (Strings.isAllWhitespace ("ab c"));
    assertFalse (Strings.isAllWhitespace (" a"));
  }

  @Test
  public void testStartsWithChar ()
  {
    assertTrue (Strings.startsWith ("abc", 'a'));
    assertFalse (Strings.startsWith ("abc", 'b'));
    assertTrue (Strings.startsWith ("a", 'a'));
    assertFalse (Strings.startsWith ("", 'a'));
    assertFalse (Strings.startsWith (null, 'a'));

    final char [] aStart = { 'a', 'b', 'c' };
    assertTrue (Strings.startsWithAny ("abc", aStart));
    assertTrue (Strings.startsWithAny ("bbc", aStart));
    assertTrue (Strings.startsWithAny ("ccc", aStart));
    assertFalse (Strings.startsWithAny ("def", aStart));
    assertFalse (Strings.startsWithAny ("daabbcc", aStart));
    assertTrue (Strings.startsWithAny ("a", aStart));
    assertFalse (Strings.startsWithAny ("", aStart));
    assertFalse (Strings.startsWithAny (null, aStart));
    assertFalse (Strings.startsWithAny ("a", (char []) null));
    assertFalse (Strings.startsWithAny ("a", new char [0]));

    assertTrue (Strings.startsWithIgnoreCase ("abc", 'a'));
    assertFalse (Strings.startsWithIgnoreCase ("abc", 'b'));
    assertTrue (Strings.startsWithIgnoreCase ("a", 'a'));
    assertFalse (Strings.startsWithIgnoreCase ("", 'a'));
    assertFalse (Strings.startsWithIgnoreCase (null, 'a'));

    assertTrue (Strings.startsWithIgnoreCase ("ABC", 'a'));
    assertFalse (Strings.startsWithIgnoreCase ("ABC", 'b'));
    assertTrue (Strings.startsWithIgnoreCase ("A", 'a'));
    assertFalse (Strings.startsWithIgnoreCase ("", 'a'));
    assertFalse (Strings.startsWithIgnoreCase (null, 'a'));

    assertTrue (Strings.startsWithIgnoreCase ("abc", 'A'));
    assertFalse (Strings.startsWithIgnoreCase ("abc", 'B'));
    assertTrue (Strings.startsWithIgnoreCase ("a", 'A'));
    assertFalse (Strings.startsWithIgnoreCase ("", 'A'));
    assertFalse (Strings.startsWithIgnoreCase (null, 'A'));
  }

  @Test
  public void testStartsWithString ()
  {
    assertTrue (Strings.startsWith ("abc", "a"));
    assertTrue (Strings.startsWith ("abc", "ab"));
    assertTrue (Strings.startsWith ("abc", "abc"));
    assertFalse (Strings.startsWith ("abc", "b"));
    assertTrue (Strings.startsWith ("a", "a"));
    assertFalse (Strings.startsWith ("", "a"));
    assertFalse (Strings.startsWith (null, "a"));
    assertFalse (Strings.startsWith ("a", null));

    assertTrue (Strings.startsWith ("abc", ""));
    assertTrue (Strings.startsWith ("", ""));
    assertFalse (Strings.startsWith (null, ""));

    assertFalse (Strings.startsWith (null, null));
    assertTrue (Strings.startsWith ("", ""));

    assertFalse (Strings.startsWithIgnoreCase (null, null));
    assertTrue (Strings.startsWithIgnoreCase ("", ""));

    assertTrue (Strings.startsWithIgnoreCase ("abc", "a"));
    assertTrue (Strings.startsWithIgnoreCase ("abc", "ab"));
    assertTrue (Strings.startsWithIgnoreCase ("abc", "abc"));
    assertFalse (Strings.startsWithIgnoreCase ("abc", "b"));
    assertTrue (Strings.startsWithIgnoreCase ("a", "a"));
    assertFalse (Strings.startsWithIgnoreCase ("", "a"));
    assertFalse (Strings.startsWithIgnoreCase (null, "a"));
    assertFalse (Strings.startsWithIgnoreCase ("a", null));

    assertTrue (Strings.startsWithIgnoreCase ("ABC", "a"));
    assertTrue (Strings.startsWithIgnoreCase ("ABC", "ab"));
    assertTrue (Strings.startsWithIgnoreCase ("ABC", "abc"));
    assertFalse (Strings.startsWithIgnoreCase ("ABC", "b"));
    assertTrue (Strings.startsWithIgnoreCase ("A", "a"));
    assertFalse (Strings.startsWithIgnoreCase ("", "a"));
    assertFalse (Strings.startsWithIgnoreCase (null, "a"));
    assertFalse (Strings.startsWithIgnoreCase ("A", null));

    assertTrue (Strings.startsWithIgnoreCase ("abc", "A"));
    assertTrue (Strings.startsWithIgnoreCase ("abc", "AB"));
    assertTrue (Strings.startsWithIgnoreCase ("abc", "ABC"));
    assertFalse (Strings.startsWithIgnoreCase ("abc", "B"));
    assertTrue (Strings.startsWithIgnoreCase ("a", "A"));
    assertFalse (Strings.startsWithIgnoreCase ("", "A"));
    assertFalse (Strings.startsWithIgnoreCase (null, "A"));
    assertFalse (Strings.startsWithIgnoreCase ("a", null));
  }

  @Test
  public void testEndsWithChar ()
  {
    assertTrue (Strings.endsWith ("abc", 'c'));
    assertFalse (Strings.endsWith ("abc", 'b'));
    assertTrue (Strings.endsWith ("a", 'a'));
    assertFalse (Strings.endsWith ("", 'a'));
    assertFalse (Strings.endsWith (null, 'a'));
    assertFalse (Strings.endsWith (null, null));
    assertTrue (Strings.endsWith ("", ""));

    final char [] aEnd = { 'a', 'b', 'c' };
    assertTrue (Strings.endsWithAny ("abc", aEnd));
    assertTrue (Strings.endsWithAny ("aab", aEnd));
    assertTrue (Strings.endsWithAny ("aaa", aEnd));
    assertFalse (Strings.endsWithAny ("aad", aEnd));
    assertTrue (Strings.endsWithAny ("a", aEnd));
    assertFalse (Strings.endsWithAny ("", aEnd));
    assertFalse (Strings.endsWithAny (null, aEnd));
    assertFalse (Strings.endsWithAny ("a", (char []) null));
    assertFalse (Strings.endsWithAny ("a", new char [0]));

    assertFalse (Strings.endsWithIgnoreCase (null, null));
    assertTrue (Strings.endsWithIgnoreCase ("", ""));

    assertTrue (Strings.endsWithIgnoreCase ("abc", 'c'));
    assertFalse (Strings.endsWithIgnoreCase ("abc", 'b'));
    assertTrue (Strings.endsWithIgnoreCase ("a", 'a'));
    assertFalse (Strings.endsWithIgnoreCase ("", 'a'));
    assertFalse (Strings.endsWithIgnoreCase (null, 'a'));

    assertTrue (Strings.endsWithIgnoreCase ("ABC", 'c'));
    assertFalse (Strings.endsWithIgnoreCase ("ABC", 'b'));
    assertTrue (Strings.endsWithIgnoreCase ("A", 'a'));
    assertFalse (Strings.endsWithIgnoreCase ("", 'a'));
    assertFalse (Strings.endsWithIgnoreCase (null, 'a'));

    assertTrue (Strings.endsWithIgnoreCase ("abc", 'C'));
    assertFalse (Strings.endsWithIgnoreCase ("abc", 'B'));
    assertTrue (Strings.endsWithIgnoreCase ("a", 'A'));
    assertFalse (Strings.endsWithIgnoreCase ("", 'A'));
    assertFalse (Strings.endsWithIgnoreCase (null, 'A'));
  }

  @Test
  public void testEndsWithString ()
  {
    assertTrue (Strings.endsWith ("abc", "c"));
    assertTrue (Strings.endsWith ("abc", "bc"));
    assertTrue (Strings.endsWith ("abc", "abc"));
    assertFalse (Strings.endsWith ("abc", "b"));
    assertTrue (Strings.endsWith ("a", "a"));
    assertFalse (Strings.endsWith ("", "a"));
    assertFalse (Strings.endsWith (null, "a"));
    assertFalse (Strings.endsWith ("a", null));

    assertTrue (Strings.endsWithIgnoreCase ("abc", "c"));
    assertTrue (Strings.endsWithIgnoreCase ("abc", "bc"));
    assertTrue (Strings.endsWithIgnoreCase ("abc", "abc"));
    assertFalse (Strings.endsWithIgnoreCase ("abc", "b"));
    assertTrue (Strings.endsWithIgnoreCase ("a", "a"));
    assertFalse (Strings.endsWithIgnoreCase ("", "a"));
    assertFalse (Strings.endsWithIgnoreCase (null, "a"));
    assertFalse (Strings.endsWithIgnoreCase ("a", null));

    assertTrue (Strings.endsWithIgnoreCase ("ABC", "c"));
    assertTrue (Strings.endsWithIgnoreCase ("ABC", "bc"));
    assertTrue (Strings.endsWithIgnoreCase ("ABC", "abc"));
    assertFalse (Strings.endsWithIgnoreCase ("ABC", "b"));
    assertTrue (Strings.endsWithIgnoreCase ("A", "a"));
    assertFalse (Strings.endsWithIgnoreCase ("", "a"));
    assertFalse (Strings.endsWithIgnoreCase (null, "a"));
    assertFalse (Strings.endsWithIgnoreCase ("A", null));

    assertTrue (Strings.endsWithIgnoreCase ("abc", "C"));
    assertTrue (Strings.endsWithIgnoreCase ("abc", "BC"));
    assertTrue (Strings.endsWithIgnoreCase ("abc", "ABC"));
    assertFalse (Strings.endsWithIgnoreCase ("abc", "B"));
    assertTrue (Strings.endsWithIgnoreCase ("a", "A"));
    assertFalse (Strings.endsWithIgnoreCase ("", "A"));
    assertFalse (Strings.endsWithIgnoreCase (null, "A"));
    assertFalse (Strings.endsWithIgnoreCase ("a", null));
  }
}
