/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.helger.base.io.nonblocking.NonBlockingStringWriter;

/**
 * Test class for the "multiple" methods of {@link StringReplace} and
 * {@link StringRemove}.
 *
 * @author Philip Helger
 */
public final class StringReplaceMultipleTest
{
  private static final char [] SEARCH = new char [] { '<', '>', '&' };
  private static final char [] [] REPLACEMENT = new char [] [] { "&lt;".toCharArray (),
                                                                 "&gt;".toCharArray (),
                                                                 "&amp;".toCharArray () };

  @Test
  public void testReplaceMultipleString ()
  {
    assertArrayEquals ("&lt;a&gt;&amp;".toCharArray (), StringReplace.replaceMultiple ("<a>&", SEARCH, REPLACEMENT));
    // Nothing to replace
    assertArrayEquals ("abc".toCharArray (), StringReplace.replaceMultiple ("abc", SEARCH, REPLACEMENT));
    // Empty input
    assertArrayEquals (new char [0], StringReplace.replaceMultiple ("", SEARCH, REPLACEMENT));
    assertArrayEquals (new char [0], StringReplace.replaceMultiple ((String) null, SEARCH, REPLACEMENT));
    // Nothing to search for
    assertArrayEquals ("<a>".toCharArray (),
                       StringReplace.replaceMultiple ("<a>", new char [0], new char [0] []));
  }

  @Test
  public void testReplaceMultipleCharArray ()
  {
    assertArrayEquals ("&lt;a&gt;&amp;".toCharArray (),
                       StringReplace.replaceMultiple ("<a>&".toCharArray (), SEARCH, REPLACEMENT));
    assertArrayEquals ("abc".toCharArray (),
                       StringReplace.replaceMultiple ("abc".toCharArray (), SEARCH, REPLACEMENT));
    assertArrayEquals (new char [0], StringReplace.replaceMultiple (new char [0], SEARCH, REPLACEMENT));
    assertArrayEquals (new char [0], StringReplace.replaceMultiple ((char []) null, SEARCH, REPLACEMENT));
  }

  @Test
  public void testReplaceMultipleTo () throws IOException
  {
    try (final NonBlockingStringWriter aSW = new NonBlockingStringWriter ())
    {
      assertEquals (3, StringReplace.replaceMultipleTo ("<a>&", SEARCH, REPLACEMENT, aSW));
      assertEquals ("&lt;a&gt;&amp;", aSW.getAsString ());
    }

    // Nothing to replace
    try (final NonBlockingStringWriter aSW = new NonBlockingStringWriter ())
    {
      assertEquals (0, StringReplace.replaceMultipleTo ("abc", SEARCH, REPLACEMENT, aSW));
      assertEquals ("abc", aSW.getAsString ());
    }

    // Empty input
    try (final NonBlockingStringWriter aSW = new NonBlockingStringWriter ())
    {
      assertEquals (0, StringReplace.replaceMultipleTo ((String) null, SEARCH, REPLACEMENT, aSW));
      assertEquals (0, StringReplace.replaceMultipleTo ("", SEARCH, REPLACEMENT, aSW));
      assertEquals (0, StringReplace.replaceMultipleTo ((char []) null, SEARCH, REPLACEMENT, aSW));
      assertEquals ("", aSW.getAsString ());
    }

    // char array with offset and length
    try (final NonBlockingStringWriter aSW = new NonBlockingStringWriter ())
    {
      assertEquals (2, StringReplace.replaceMultipleTo ("x<a>x".toCharArray (), 1, 3, SEARCH, REPLACEMENT, aSW));
      assertEquals ("&lt;a&gt;", aSW.getAsString ());
    }

    // Nothing to search for
    try (final NonBlockingStringWriter aSW = new NonBlockingStringWriter ())
    {
      assertEquals (0, StringReplace.replaceMultipleTo ("<a>", new char [0], new char [0] [], aSW));
      assertEquals ("<a>", aSW.getAsString ());
    }
  }

  @Test
  public void testRemoveMultiple ()
  {
    assertEquals ("a", StringRemove.removeMultiple ("<a>", SEARCH));
    assertEquals ("abc", StringRemove.removeMultiple ("abc", SEARCH));
    assertEquals ("", StringRemove.removeMultiple ("", SEARCH));
    assertEquals ("", StringRemove.removeMultiple (null, SEARCH));
    // Nothing to remove
    assertEquals ("<a>", StringRemove.removeMultiple ("<a>", new char [0]));
  }
}
