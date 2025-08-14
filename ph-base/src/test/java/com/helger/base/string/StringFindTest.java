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
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Test;

import com.helger.base.CGlobal;
import com.helger.base.functional.ICharPredicate;

/**
 * Test class for class {@link StringFind}
 *
 * @author Philip Helger
 */
public final class StringFindTest
{
  private static final Locale L_DE = new Locale ("de");

  @Test
  public void testGetFirstChar ()
  {
    assertEquals ('a', StringFind.getFirstChar ("abc"));
    assertEquals ('a', StringFind.getFirstChar ("a"));
    assertEquals (CGlobal.ILLEGAL_CHAR, StringFind.getFirstChar (""));
    assertEquals (CGlobal.ILLEGAL_CHAR, StringFind.getFirstChar ((CharSequence) null));
    assertEquals (CGlobal.ILLEGAL_CHAR, StringFind.getFirstChar ((char []) null));
  }

  @Test
  public void testGetLastChar ()
  {
    assertEquals ('c', StringFind.getLastChar ("abc"));
    assertEquals ('a', StringFind.getLastChar ("a"));
    assertEquals (CGlobal.ILLEGAL_CHAR, StringFind.getLastChar (""));
    assertEquals (CGlobal.ILLEGAL_CHAR, StringFind.getLastChar ((CharSequence) null));
    assertEquals (CGlobal.ILLEGAL_CHAR, StringFind.getLastChar ((char []) null));
  }

  @Test
  public void testGetIndexOfString ()
  {
    assertEquals (-1, StringFind.getIndexOf (null, null));
    assertEquals (-1, StringFind.getIndexOf (null, "a"));
    assertEquals (-1, StringFind.getIndexOf ("b", null));
    assertEquals (-1, StringFind.getIndexOf ("b", "cd"));
    assertEquals (-1, StringFind.getIndexOf ("bla fob", "z"));
    assertEquals (0, StringFind.getIndexOf ("bla fob", "b"));
    assertEquals (2, StringFind.getIndexOf ("bla fob", "a"));
  }

  @Test
  public void testGetLastIndexOfString ()
  {
    assertEquals (-1, StringFind.getLastIndexOf (null, null));
    assertEquals (-1, StringFind.getLastIndexOf (null, "a"));
    assertEquals (-1, StringFind.getLastIndexOf ("b", null));
    assertEquals (-1, StringFind.getLastIndexOf ("b", "cd"));
    assertEquals (-1, StringFind.getLastIndexOf ("bla fob", "z"));
    assertEquals (6, StringFind.getLastIndexOf ("bla fob", "b"));
    assertEquals (2, StringFind.getLastIndexOf ("bla fob", "a"));
  }

  @Test
  public void testGetIndexOfChar ()
  {
    assertEquals (-1, StringFind.getIndexOf (null, '\0'));
    assertEquals (-1, StringFind.getIndexOf (null, 'a'));
    assertEquals (-1, StringFind.getIndexOf ("b", '\0'));
    assertEquals (-1, StringFind.getIndexOf ("b", 'c'));
    assertEquals (-1, StringFind.getIndexOf ("bla fob", 'z'));
    assertEquals (0, StringFind.getIndexOf ("bla fob", 'b'));
    assertEquals (2, StringFind.getIndexOf ("bla fob", 'a'));
  }

  @Test
  public void testGetLastIndexOfChar ()
  {
    assertEquals (-1, StringFind.getLastIndexOf (null, '\0'));
    assertEquals (-1, StringFind.getLastIndexOf (null, 'a'));
    assertEquals (-1, StringFind.getLastIndexOf ("b", '\0'));
    assertEquals (-1, StringFind.getLastIndexOf ("b", 'c'));
    assertEquals (-1, StringFind.getLastIndexOf ("bla fob", 'z'));
    assertEquals (6, StringFind.getLastIndexOf ("bla fob", 'b'));
    assertEquals (2, StringFind.getLastIndexOf ("bla fob", 'a'));
  }

  @Test
  public void testContainsString ()
  {
    assertTrue (StringFind.contains ("Test", "Test"));
    assertTrue (StringFind.contains ("Test", "est"));
    assertTrue (StringFind.contains ("Test", "Tes"));
    assertTrue (StringFind.contains ("Test", "es"));
    assertTrue (StringFind.contains ("Test", ""));

    assertFalse (StringFind.contains ("Test", null));
    assertFalse (StringFind.contains (null, "Test"));
    assertFalse (StringFind.contains ("Tes", "Test"));
    assertFalse (StringFind.contains ("est", "Test"));
    assertFalse (StringFind.contains ("es", "Test"));
    assertFalse (StringFind.contains ("", "Test"));

    assertFalse (StringFind.contains ("Test", "TEST"));
    assertFalse (StringFind.contains ("Test", "EST"));
    assertFalse (StringFind.contains ("Test", "TES"));
    assertFalse (StringFind.contains ("Test", "ES"));
  }

  @Test
  public void testContainsChar ()
  {
    assertTrue (StringFind.contains ("Test", 'T'));
    assertTrue (StringFind.contains ("Test", 'e'));
    assertTrue (StringFind.contains ("Test", 's'));
    assertTrue (StringFind.contains ("Test", 't'));
    assertFalse (StringFind.contains ("Test", '\0'));

    assertFalse (StringFind.contains ("Test", null));
    assertFalse (StringFind.contains (null, 'T'));
  }

  @Test
  public void testMultiContains ()
  {
    final char [] aIn = "abcde".toCharArray ();
    assertTrue (StringFind.containsAny (aIn, "a".toCharArray ()));
    assertFalse (StringFind.containsAny (aIn, "z".toCharArray ()));
    assertFalse (StringFind.containsAny (aIn, new char [0]));
    assertFalse (StringFind.containsAny (new char [0], "a".toCharArray ()));
  }

  @Test
  public void testIndexOfIgnoreCaseString ()
  {
    assertEquals (-1, StringFind.getIndexOfIgnoreCase (null, null, L_DE));
    assertEquals (-1, StringFind.getIndexOfIgnoreCase (null, "a", L_DE));
    assertEquals (-1, StringFind.getIndexOfIgnoreCase ("b", null, L_DE));
    assertEquals (-1, StringFind.getIndexOfIgnoreCase ("b", "cd", L_DE));
    assertEquals (-1, StringFind.getIndexOfIgnoreCase ("bla foo", "z", L_DE));
    assertEquals (0, StringFind.getIndexOfIgnoreCase ("bla foo", "b", L_DE));
    assertEquals (2, StringFind.getIndexOfIgnoreCase ("bla foo", "a", L_DE));
    assertEquals (0, StringFind.getIndexOfIgnoreCase ("bla foo", "B", L_DE));
    assertEquals (2, StringFind.getIndexOfIgnoreCase ("bla foo", "A", L_DE));
    assertEquals (0, StringFind.getIndexOfIgnoreCase ("BLA FOO", "b", L_DE));
    assertEquals (2, StringFind.getIndexOfIgnoreCase ("BLA FOO", "a", L_DE));
  }

  @Test
  public void testContainsIgnoreCaseString ()
  {
    final Locale aLocale = Locale.ENGLISH;
    assertTrue (StringFind.containsIgnoreCase ("Test", "Test", aLocale));
    assertTrue (StringFind.containsIgnoreCase ("Test", "est", aLocale));
    assertTrue (StringFind.containsIgnoreCase ("Test", "Tes", aLocale));
    assertTrue (StringFind.containsIgnoreCase ("Test", "es", aLocale));
    assertTrue (StringFind.containsIgnoreCase ("Test", "", aLocale));

    assertFalse (StringFind.containsIgnoreCase ("Test", null, aLocale));
    assertFalse (StringFind.containsIgnoreCase (null, "Test", aLocale));
    assertFalse (StringFind.containsIgnoreCase ("Tes", "Test", aLocale));
    assertFalse (StringFind.containsIgnoreCase ("est", "Test", aLocale));
    assertFalse (StringFind.containsIgnoreCase ("es", "Test", aLocale));
    assertFalse (StringFind.containsIgnoreCase ("", "Test", aLocale));

    assertTrue (StringFind.containsIgnoreCase ("Test", "TEST", aLocale));
    assertTrue (StringFind.containsIgnoreCase ("Test", "EST", aLocale));
    assertTrue (StringFind.containsIgnoreCase ("Test", "TES", aLocale));
    assertTrue (StringFind.containsIgnoreCase ("Test", "ES", aLocale));
  }

  @Test
  public void testContainsAnyOnlyNoneString ()
  {
    assertTrue (StringFind.containsAny ("aa", x -> x == 'a'));
    assertTrue (StringFind.containsAny ("abc", x -> x == 'a'));
    assertTrue (StringFind.containsAny ("abc", x -> x == 'b'));
    assertTrue (StringFind.containsAny ("abc", (ICharPredicate) null));
    assertFalse (StringFind.containsAny ("", (ICharPredicate) null));
    assertFalse (StringFind.containsAny ((String) null, (ICharPredicate) null));
    assertFalse (StringFind.containsAny ("", x -> x == 'a'));
    assertFalse (StringFind.containsAny ((String) null, x -> x == 'a'));
    assertFalse (StringFind.containsAny ("abc", x -> x == 'd'));

    assertTrue (StringFind.containsOnly ("aa", x -> x == 'a'));
    assertFalse (StringFind.containsOnly ("abc", x -> x == 'a'));
    assertFalse (StringFind.containsOnly ("abc", x -> x == 'b'));
    assertTrue (StringFind.containsOnly ("abc", (ICharPredicate) null));
    assertFalse (StringFind.containsOnly ("", (ICharPredicate) null));
    assertFalse (StringFind.containsOnly ((String) null, (ICharPredicate) null));
    assertFalse (StringFind.containsOnly ("", x -> x == 'a'));
    assertFalse (StringFind.containsOnly ((String) null, x -> x == 'a'));
    assertFalse (StringFind.containsOnly ("abc", x -> x == 'd'));

    assertFalse (StringFind.containsNone ("aa", x -> x == 'a'));
    assertFalse (StringFind.containsNone ("abc", x -> x == 'a'));
    assertFalse (StringFind.containsNone ("abc", x -> x == 'b'));
    assertFalse (StringFind.containsNone ("abc", (ICharPredicate) null));
    assertTrue (StringFind.containsNone ("", (ICharPredicate) null));
    assertTrue (StringFind.containsNone ((String) null, (ICharPredicate) null));
    assertTrue (StringFind.containsNone ("", x -> x == 'a'));
    assertTrue (StringFind.containsNone ((String) null, x -> x == 'a'));
    assertTrue (StringFind.containsNone ("abc", x -> x == 'd'));
  }

  @Test
  public void testContainsAnyOnlyNoneCharSequence ()
  {
    assertTrue (StringFind.containsAny ((CharSequence) "aa", x -> x == 'a'));
    assertTrue (StringFind.containsAny ((CharSequence) "abc", x -> x == 'a'));
    assertTrue (StringFind.containsAny ((CharSequence) "abc", x -> x == 'b'));
    assertTrue (StringFind.containsAny ((CharSequence) "abc", (ICharPredicate) null));
    assertFalse (StringFind.containsAny ((CharSequence) "", (ICharPredicate) null));
    assertFalse (StringFind.containsAny ((CharSequence) null, (ICharPredicate) null));
    assertFalse (StringFind.containsAny ((CharSequence) "", x -> x == 'a'));
    assertFalse (StringFind.containsAny ((CharSequence) null, x -> x == 'a'));
    assertFalse (StringFind.containsAny ((CharSequence) "abc", x -> x == 'd'));

    assertTrue (StringFind.containsOnly ((CharSequence) "aa", x -> x == 'a'));
    assertFalse (StringFind.containsOnly ((CharSequence) "abc", x -> x == 'a'));
    assertFalse (StringFind.containsOnly ((CharSequence) "abc", x -> x == 'b'));
    assertTrue (StringFind.containsOnly ((CharSequence) "abc", (ICharPredicate) null));
    assertFalse (StringFind.containsOnly ((CharSequence) "", (ICharPredicate) null));
    assertFalse (StringFind.containsOnly ((CharSequence) null, (ICharPredicate) null));
    assertFalse (StringFind.containsOnly ((CharSequence) "", x -> x == 'a'));
    assertFalse (StringFind.containsOnly ((CharSequence) null, x -> x == 'a'));
    assertFalse (StringFind.containsOnly ((CharSequence) "abc", x -> x == 'd'));

    assertFalse (StringFind.containsNone ((CharSequence) "aa", x -> x == 'a'));
    assertFalse (StringFind.containsNone ((CharSequence) "abc", x -> x == 'a'));
    assertFalse (StringFind.containsNone ((CharSequence) "abc", x -> x == 'b'));
    assertFalse (StringFind.containsNone ((CharSequence) "abc", (ICharPredicate) null));
    assertTrue (StringFind.containsNone ((CharSequence) "", (ICharPredicate) null));
    assertTrue (StringFind.containsNone ((CharSequence) null, (ICharPredicate) null));
    assertTrue (StringFind.containsNone ((CharSequence) "", x -> x == 'a'));
    assertTrue (StringFind.containsNone ((CharSequence) null, x -> x == 'a'));
    assertTrue (StringFind.containsNone ((CharSequence) "abc", x -> x == 'd'));
  }

  @Test
  public void testIsAllWhitespace ()
  {
    assertTrue (StringFind.isAllWhitespace ("   "));
    assertTrue (StringFind.isAllWhitespace (" \t\r\n"));
    assertTrue (StringFind.isAllWhitespace ("\n"));

    assertFalse (StringFind.isAllWhitespace (""));
    assertFalse (StringFind.isAllWhitespace (null));
    assertFalse (StringFind.isAllWhitespace ("a"));
    assertFalse (StringFind.isAllWhitespace ("abc"));
    assertFalse (StringFind.isAllWhitespace ("ab c"));
    assertFalse (StringFind.isAllWhitespace (" a"));
  }
}
