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
import java.util.List;
import java.util.function.Function;

import org.junit.Test;

import com.helger.base.CGlobal;
import com.helger.base.functional.IThrowingFunction;
import com.helger.base.io.nonblocking.NonBlockingStringWriter;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsHashSet;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsOrderedMap;
import com.helger.collection.commons.ICommonsOrderedSet;
import com.helger.collection.commons.ICommonsSet;
import com.helger.collection.helper.CollectionHelperExt;

/**
 * Test class for class {@link StringHelper}.
 *
 * @author Philip Helger
 */
public final class StringHelperTest
{

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
    final ICommonsOrderedMap <String, String> aMap = CollectionHelperExt.newOrderedMap ("a",
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
