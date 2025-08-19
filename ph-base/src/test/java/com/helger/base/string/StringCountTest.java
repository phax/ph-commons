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

import java.util.Locale;

import org.junit.Test;

/**
 * Test class for class {@link StringCount}
 *
 * @author Philip Helger
 */
public final class StringCountTest
{
  @Test
  public void testGetOccurrenceCountString ()
  {
    assertEquals (0, StringCount.getOccurrenceCount ("Test", null));
    assertEquals (0, StringCount.getOccurrenceCount (null, "Test"));
    assertEquals (1, StringCount.getOccurrenceCount ("Test", "Test"));
    assertEquals (1, StringCount.getOccurrenceCount ("Test", "Tes"));
    assertEquals (1, StringCount.getOccurrenceCount ("Test", "est"));
    assertEquals (1, StringCount.getOccurrenceCount ("Test", "es"));
    assertEquals (2, StringCount.getOccurrenceCount ("Testen", "e"));
    assertEquals (0, StringCount.getOccurrenceCount ("Testen", ""));
    assertEquals (4, StringCount.getOccurrenceCount ("eeee", "e"));
    assertEquals (2, StringCount.getOccurrenceCount ("eeee", "ee"));
    assertEquals (1, StringCount.getOccurrenceCount ("eeee", "eee"));

    // Invalid case
    assertEquals (0, StringCount.getOccurrenceCount ("eeee", "E"));
    assertEquals (0, StringCount.getOccurrenceCount ("eeee", "EE"));
    assertEquals (0, StringCount.getOccurrenceCount ("eeee", "EEE"));
  }

  @Test
  public void testGetOccurrenceCountChar ()
  {
    assertEquals (0, StringCount.getOccurrenceCount (null, 'x'));
    assertEquals (0, StringCount.getOccurrenceCount ("e", 'f'));
    assertEquals (0, StringCount.getOccurrenceCount ("e", '\u0000'));
    assertEquals (0, StringCount.getOccurrenceCount ("eeee", 'f'));
    assertEquals (0, StringCount.getOccurrenceCount ("eeee", '\u0000'));

    assertEquals (1, StringCount.getOccurrenceCount ("e", 'e'));
    assertEquals (4, StringCount.getOccurrenceCount ("eeee", 'e'));
    assertEquals (1, StringCount.getOccurrenceCount ("abc", 'a'));
    assertEquals (2, StringCount.getOccurrenceCount ("aabc", 'a'));
    assertEquals (1, StringCount.getOccurrenceCount ("abc", 'b'));
    assertEquals (1, StringCount.getOccurrenceCount ("abc", 'c'));
    assertEquals (2, StringCount.getOccurrenceCount ("abcc", 'c'));
  }

  @Test
  public void testGetOccurrenceCountIgnoreCaseString ()
  {
    final Locale aLocale = Locale.ENGLISH;
    assertEquals (0, StringCount.getOccurrenceCountIgnoreCase ("Test", null, aLocale));
    assertEquals (0, StringCount.getOccurrenceCountIgnoreCase (null, "Test", aLocale));
    assertEquals (1, StringCount.getOccurrenceCountIgnoreCase ("Test", "Test", aLocale));
    assertEquals (1, StringCount.getOccurrenceCountIgnoreCase ("Test", "Tes", aLocale));
    assertEquals (1, StringCount.getOccurrenceCountIgnoreCase ("Test", "est", aLocale));
    assertEquals (1, StringCount.getOccurrenceCountIgnoreCase ("Test", "es", aLocale));
    assertEquals (2, StringCount.getOccurrenceCountIgnoreCase ("Testen", "e", aLocale));
    assertEquals (0, StringCount.getOccurrenceCountIgnoreCase ("Testen", "", aLocale));
    assertEquals (4, StringCount.getOccurrenceCountIgnoreCase ("eeee", "e", aLocale));
    assertEquals (2, StringCount.getOccurrenceCountIgnoreCase ("eeee", "ee", aLocale));
    assertEquals (1, StringCount.getOccurrenceCountIgnoreCase ("eeee", "eee", aLocale));

    // Ignoring case
    assertEquals (2, StringCount.getOccurrenceCountIgnoreCase ("Test", "t", aLocale));
    assertEquals (4, StringCount.getOccurrenceCountIgnoreCase ("eeee", "E", aLocale));
    assertEquals (2, StringCount.getOccurrenceCountIgnoreCase ("eeee", "EE", aLocale));
    assertEquals (1, StringCount.getOccurrenceCountIgnoreCase ("eeee", "EEE", aLocale));
  }

  @Test
  public void testGetOccurrenceCountIgnoreCaseChar ()
  {
    final Locale aLocale = Locale.ENGLISH;
    assertEquals (0, StringCount.getOccurrenceCountIgnoreCase (null, 'x', aLocale));
    assertEquals (0, StringCount.getOccurrenceCountIgnoreCase ("e", 'f', aLocale));
    assertEquals (0, StringCount.getOccurrenceCountIgnoreCase ("e", '\u0000', aLocale));
    assertEquals (0, StringCount.getOccurrenceCountIgnoreCase ("eeee", 'f', aLocale));
    assertEquals (0, StringCount.getOccurrenceCountIgnoreCase ("eeee", '\u0000', aLocale));

    assertEquals (1, StringCount.getOccurrenceCountIgnoreCase ("e", 'e', aLocale));
    assertEquals (1, StringCount.getOccurrenceCountIgnoreCase ("E", 'e', aLocale));
    assertEquals (1, StringCount.getOccurrenceCountIgnoreCase ("e", 'E', aLocale));
    assertEquals (4, StringCount.getOccurrenceCountIgnoreCase ("eeee", 'e', aLocale));
    assertEquals (4, StringCount.getOccurrenceCountIgnoreCase ("EEEE", 'e', aLocale));
    assertEquals (4, StringCount.getOccurrenceCountIgnoreCase ("eeee", 'E', aLocale));
    assertEquals (1, StringCount.getOccurrenceCountIgnoreCase ("abc", 'a', aLocale));
    assertEquals (1, StringCount.getOccurrenceCountIgnoreCase ("ABC", 'a', aLocale));
    assertEquals (1, StringCount.getOccurrenceCountIgnoreCase ("abc", 'A', aLocale));
    assertEquals (2, StringCount.getOccurrenceCountIgnoreCase ("aabc", 'a', aLocale));
    assertEquals (2, StringCount.getOccurrenceCountIgnoreCase ("AABC", 'a', aLocale));
    assertEquals (2, StringCount.getOccurrenceCountIgnoreCase ("aabc", 'A', aLocale));
    assertEquals (1, StringCount.getOccurrenceCountIgnoreCase ("abc", 'b', aLocale));
    assertEquals (1, StringCount.getOccurrenceCountIgnoreCase ("ABC", 'b', aLocale));
    assertEquals (1, StringCount.getOccurrenceCountIgnoreCase ("abc", 'B', aLocale));
    assertEquals (1, StringCount.getOccurrenceCountIgnoreCase ("abc", 'c', aLocale));
    assertEquals (1, StringCount.getOccurrenceCountIgnoreCase ("ABC", 'c', aLocale));
    assertEquals (1, StringCount.getOccurrenceCountIgnoreCase ("abc", 'C', aLocale));
    assertEquals (2, StringCount.getOccurrenceCountIgnoreCase ("abcc", 'c', aLocale));
    assertEquals (2, StringCount.getOccurrenceCountIgnoreCase ("ABCC", 'c', aLocale));
    assertEquals (2, StringCount.getOccurrenceCountIgnoreCase ("abcc", 'C', aLocale));
  }

  @Test
  public void testGetCharCount ()
  {
    assertEquals (0, StringCount.getCharCount ("abc", 'x'));
    assertEquals (1, StringCount.getCharCount ("xabc", 'x'));
    assertEquals (1, StringCount.getCharCount ("abxc", 'x'));
    assertEquals (1, StringCount.getCharCount ("abcx", 'x'));
    assertEquals (0, StringCount.getCharCount ((String) null, 'x'));
    assertEquals (0, StringCount.getCharCount ((char []) null, 'x'));
    assertEquals (0, StringCount.getCharCount ("", 'x'));
    for (int i = 0; i < 1000; ++i)
      assertEquals (i, StringCount.getCharCount (StringHelper.getRepeated ('x', i), 'x'));
  }

  @Test
  public void testGetLineCount ()
  {
    assertEquals (1, StringCount.getLineCount ("abc"));
    assertEquals (2, StringCount.getLineCount ("ab\nc"));
    assertEquals (2, StringCount.getLineCount ("ab\r\nc"));
    assertEquals (1, StringCount.getLineCount ("ab\rc"));
  }

  @Test
  public void testGetCharacterCountInt ()
  {
    int iVal = 1;
    for (int i = 1; i <= 10; ++i)
    {
      assertEquals (i, StringCount.getCharacterCount (iVal));
      iVal *= 10;
    }
    iVal = -1;
    for (int i = 1; i <= 10; ++i)
    {
      assertEquals (1 + i, StringCount.getCharacterCount (iVal));
      iVal *= 10;
    }
    assertEquals (11, StringCount.getCharacterCount (Integer.MIN_VALUE + 1));
    assertEquals (10, StringCount.getCharacterCount (Integer.MAX_VALUE));
  }

  @Test
  public void testGetCharacterCountLong ()
  {
    long lVal = 1;
    for (int i = 1; i <= 19; ++i)
    {
      assertEquals (i, StringCount.getCharacterCount (lVal));
      lVal *= 10;
    }
    lVal = -1;
    for (int i = 1; i <= 19; ++i)
    {
      assertEquals (1 + i, StringCount.getCharacterCount (lVal));
      lVal *= 10;
    }
    assertEquals (20, StringCount.getCharacterCount (Long.MIN_VALUE + 1));
    assertEquals (19, StringCount.getCharacterCount (Long.MAX_VALUE));
  }

  @Test
  public void testGetLeadingWhitespaceCount ()
  {
    assertEquals (0, StringCount.getLeadingWhitespaceCount ("Hallo Welt"));
    assertEquals (1, StringCount.getLeadingWhitespaceCount (" Hallo Welt"));
    assertEquals (2, StringCount.getLeadingWhitespaceCount ("  Hallo Welt"));
    assertEquals (2, StringCount.getLeadingWhitespaceCount ("\t\tHallo Welt"));
    assertEquals (2, StringCount.getLeadingWhitespaceCount ("  "));
    assertEquals (0, StringCount.getLeadingWhitespaceCount (""));
    assertEquals (0, StringCount.getLeadingWhitespaceCount (null));
  }

  @Test
  public void testGetTrailingWhitespaceCount ()
  {
    assertEquals (0, StringCount.getTrailingWhitespaceCount ("Hallo Welt"));
    assertEquals (1, StringCount.getTrailingWhitespaceCount (" Hallo Welt "));
    assertEquals (2, StringCount.getTrailingWhitespaceCount ("  Hallo Welt  "));
    assertEquals (2, StringCount.getTrailingWhitespaceCount ("\t\tHallo Welt\t\t"));
    assertEquals (2, StringCount.getTrailingWhitespaceCount ("  "));
    assertEquals (0, StringCount.getTrailingWhitespaceCount (""));
    assertEquals (0, StringCount.getTrailingWhitespaceCount (null));
  }

  @Test
  public void testGetLeadingCharCount ()
  {
    assertEquals (0, StringCount.getLeadingCharCount ("Hallo Welt", 'x'));
    assertEquals (1, StringCount.getLeadingCharCount ("xHallo Welt", 'x'));
    assertEquals (2, StringCount.getLeadingCharCount ("xxHallo Welt", 'x'));
    assertEquals (2, StringCount.getLeadingCharCount ("xx", 'x'));
    assertEquals (0, StringCount.getLeadingCharCount ("", 'x'));
    assertEquals (0, StringCount.getLeadingCharCount (null, 'x'));
  }

  @Test
  public void testGetTrailingCharCount ()
  {
    assertEquals (0, StringCount.getTrailingCharCount ("Hallo Welt", 'x'));
    assertEquals (1, StringCount.getTrailingCharCount (" Hallo Weltx", 'x'));
    assertEquals (2, StringCount.getTrailingCharCount ("  Hallo Weltxx", 'x'));
    assertEquals (2, StringCount.getTrailingCharCount ("xx", 'x'));
    assertEquals (0, StringCount.getTrailingCharCount ("", 'x'));
    assertEquals (0, StringCount.getTrailingCharCount (null, 'x'));
  }
}
