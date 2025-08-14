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

import org.junit.Test;

/**
 * Test class for class {@link Strings}.
 *
 * @author Philip Helger
 */
public final class StringsTest
{
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
}
