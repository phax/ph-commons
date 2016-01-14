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
package com.helger.commons.regex;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import com.helger.commons.string.StringHelper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for {@link RegExHelper}.
 *
 * @author Philip Helger
 */
public final class RegExHelperTest
{
  /**
   * Test for method split
   */
  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testSplitNoLimit ()
  {
    String [] x = RegExHelper.getSplitToArray ("abc", "b");
    assertNotNull (x);
    assertEquals (2, x.length);
    assertEquals ("a", x[0]);
    assertEquals ("c", x[1]);

    x = RegExHelper.getSplitToArray ("aaacbccca", "b");
    assertNotNull (x);
    assertEquals (2, x.length);
    assertEquals ("aaac", x[0]);
    assertEquals ("ccca", x[1]);

    x = RegExHelper.getSplitToArray ("aaa", "b");
    assertNotNull (x);
    assertEquals (1, x.length);
    assertEquals ("aaa", x[0]);

    x = RegExHelper.getSplitToArray ("", "b");
    assertNotNull (x);
    assertEquals (1, x.length);
    assertEquals ("", x[0]);

    x = RegExHelper.getSplitToArray ("ab9cd14ef", "[0-9]+");
    assertNotNull (x);
    assertEquals (3, x.length);
    assertEquals ("ab", x[0]);
    assertEquals ("cd", x[1]);
    assertEquals ("ef", x[2]);

    x = RegExHelper.getSplitToArray ("line1\nline2bline3", "b");
    assertNotNull (x);
    assertEquals (2, x.length);
    assertEquals ("line1\nline2", x[0]);
    assertEquals ("line3", x[1]);

    x = RegExHelper.getSplitToArray (null, "b");
    assertNotNull (x);
    assertEquals (0, x.length);

    x = RegExHelper.getSplitToArray (null, null);
    assertNotNull (x);
    assertEquals (0, x.length);

    try
    {
      // empty regex not allowed
      RegExHelper.getSplitToArray ("ab9cd14ef", null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // empty regex not allowed
      RegExHelper.getSplitToArray ("ab9cd14ef", "");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  /**
   * Test for method split
   */
  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testSplitWithLimit ()
  {
    String [] x = RegExHelper.getSplitToArray ("abc", "b", 2);
    assertNotNull (x);
    assertEquals (2, x.length);
    assertEquals ("a", x[0]);
    assertEquals ("c", x[1]);

    // a limit <= 0 means -> all tokens
    x = RegExHelper.getSplitToArray ("aaacbccca", "b", 0);
    assertNotNull (x);
    assertEquals (2, x.length);
    assertEquals ("aaac", x[0]);
    assertEquals ("ccca", x[1]);

    x = RegExHelper.getSplitToArray ("aaacbccca", "b", 1);
    assertNotNull (x);
    assertEquals (1, x.length);
    assertEquals ("aaacbccca", x[0]);

    x = RegExHelper.getSplitToArray ("aaacbccca", "b", 2);
    assertNotNull (x);
    assertEquals (2, x.length);
    assertEquals ("aaac", x[0]);
    assertEquals ("ccca", x[1]);

    x = RegExHelper.getSplitToArray ("aaacbccca", "b", 3);
    assertNotNull (x);
    assertEquals (2, x.length);
    assertEquals ("aaac", x[0]);
    assertEquals ("ccca", x[1]);

    x = RegExHelper.getSplitToArray ("aaa", "b", 2);
    assertNotNull (x);
    assertEquals (1, x.length);
    assertEquals ("aaa", x[0]);

    x = RegExHelper.getSplitToArray ("", "b", 2);
    assertNotNull (x);
    assertEquals (1, x.length);
    assertEquals ("", x[0]);

    x = RegExHelper.getSplitToArray ("ab9cd14ef", "[0-9]+", 2);
    assertNotNull (x);
    assertEquals (2, x.length);
    assertEquals ("ab", x[0]);
    assertEquals ("cd14ef", x[1]);

    x = RegExHelper.getSplitToArray (null, "b", 2);
    assertNotNull (x);
    assertEquals (0, x.length);

    try
    {
      x = RegExHelper.getSplitToArray (null, null, 2);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // empty regex not allowed
      RegExHelper.getSplitToArray ("ab9cd14ef", null, 2);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // empty regex not allowed
      RegExHelper.getSplitToArray ("ab9cd14ef", "", 2);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  /**
   * Test for method splitToList
   */
  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testSplitToListNoLimit ()
  {
    List <String> x = RegExHelper.getSplitToList ("abc", "b");
    assertNotNull (x);
    assertEquals (2, x.size ());
    assertEquals ("a", x.get (0));
    assertEquals ("c", x.get (1));

    x = RegExHelper.getSplitToList ("aaacbccca", "b");
    assertNotNull (x);
    assertEquals (2, x.size ());
    assertEquals ("aaac", x.get (0));
    assertEquals ("ccca", x.get (1));

    x = RegExHelper.getSplitToList ("aaa", "b");
    assertNotNull (x);
    assertEquals (1, x.size ());
    assertEquals ("aaa", x.get (0));

    x = RegExHelper.getSplitToList ("", "b");
    assertNotNull (x);
    assertEquals (1, x.size ());
    assertEquals ("", x.get (0));

    x = RegExHelper.getSplitToList ("ab9cd14ef", "[0-9]+");
    assertNotNull (x);
    assertEquals (3, x.size ());
    assertEquals ("ab", x.get (0));
    assertEquals ("cd", x.get (1));
    assertEquals ("ef", x.get (2));

    x = RegExHelper.getSplitToList (null, "b");
    assertNotNull (x);

    x = RegExHelper.getSplitToList (null, null);
    assertNotNull (x);

    try
    {
      // empty regex not allowed
      RegExHelper.getSplitToList ("ab9cd14ef", null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // empty regex not allowed
      RegExHelper.getSplitToList ("ab9cd14ef", "");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  /**
   * Test for method splitToList
   */
  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testSplitToListWithLimit ()
  {
    List <String> x = RegExHelper.getSplitToList ("abc", "b", 2);
    assertNotNull (x);
    assertEquals (2, x.size ());
    assertEquals ("a", x.get (0));
    assertEquals ("c", x.get (1));

    // a limit <= 0 means -> all tokens
    x = RegExHelper.getSplitToList ("aaacbccca", "b", 0);
    assertNotNull (x);
    assertEquals (2, x.size ());
    assertEquals ("aaac", x.get (0));
    assertEquals ("ccca", x.get (1));

    x = RegExHelper.getSplitToList ("aaacbccca", "b", 1);
    assertNotNull (x);
    assertEquals (1, x.size ());
    assertEquals ("aaacbccca", x.get (0));

    x = RegExHelper.getSplitToList ("aaacbccca", "b", 2);
    assertNotNull (x);
    assertEquals (2, x.size ());
    assertEquals ("aaac", x.get (0));
    assertEquals ("ccca", x.get (1));

    x = RegExHelper.getSplitToList ("aaacbccca", "b", 3);
    assertNotNull (x);
    assertEquals (2, x.size ());
    assertEquals ("aaac", x.get (0));
    assertEquals ("ccca", x.get (1));

    x = RegExHelper.getSplitToList ("aaa", "b", 2);
    assertNotNull (x);
    assertEquals (1, x.size ());
    assertEquals ("aaa", x.get (0));

    x = RegExHelper.getSplitToList ("", "b", 2);
    assertNotNull (x);
    assertEquals (1, x.size ());
    assertEquals ("", x.get (0));

    x = RegExHelper.getSplitToList ("ab9cd14ef", "[0-9]+", 2);
    assertNotNull (x);
    assertEquals (2, x.size ());
    assertEquals ("ab", x.get (0));
    assertEquals ("cd14ef", x.get (1));

    x = RegExHelper.getSplitToList (null, "b", 2);
    assertNotNull (x);

    try
    {
      x = RegExHelper.getSplitToList (null, null, 2);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // empty regex not allowed
      RegExHelper.getSplitToList ("ab9cd14ef", null, 2);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // empty regex not allowed
      RegExHelper.getSplitToList ("ab9cd14ef", "", 2);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  /**
   * Test for method makeIdentifier
   */
  @Test
  public void testMakeIdentifier ()
  {
    assertNull (RegExHelper.getAsIdentifier (null));
    assertEquals ("", RegExHelper.getAsIdentifier (""));
    assertEquals ("abc", RegExHelper.getAsIdentifier ("abc"));
    assertEquals ("ABC", RegExHelper.getAsIdentifier ("ABC"));
    assertEquals ("_0ABC", RegExHelper.getAsIdentifier ("0ABC"));
    assertEquals ("abc0ABC", RegExHelper.getAsIdentifier ("0ABC", "abc"));
    assertEquals ("_0ABC", RegExHelper.getAsIdentifier ("_0ABC"));
    assertEquals ("___", RegExHelper.getAsIdentifier (";;;"));
    assertEquals ("aaa", RegExHelper.getAsIdentifier (";;;", "a"));
    if (false)
      assertEquals ("$$$", RegExHelper.getAsIdentifier (";;;", "$"));
    assertEquals ("aaa", RegExHelper.getAsIdentifier (";;;", 'a'));
    assertEquals ("$$$", RegExHelper.getAsIdentifier (";;;", '$'));
    for (char c = Character.MIN_VALUE; c < Character.MAX_VALUE; ++c)
    {
      final int nLenExpected = c == '\\' ? 5 : Character.isJavaIdentifierStart (c) ? 3 : 4;
      assertEquals ((int) c +
                    " <" +
                    c +
                    "> " +
                    nLenExpected,
                    StringHelper.getRepeated (c, nLenExpected),
                    RegExHelper.getAsIdentifier (";;;", c));
    }

    assertEquals ("^^^^", RegExHelper.getAsIdentifier (";;;", '^'));
    assertEquals ("", RegExHelper.getAsIdentifier (";;;", ""));
  }

  /**
   * Test method getMatcher
   */
  @Test
  public void testGetMatcher ()
  {
    final Matcher m = RegExHelper.getMatcher ("Hallo (\\d+)\\.(\\d+) Welt", "This is Hallo 2.3 Welt Text");
    assertNotNull (m);
    assertEquals (2, m.groupCount ());

    // start over and replace all at once
    {
      m.reset ();
      final StringBuffer sb = new StringBuffer ();
      assertTrue (m.find ());

      // Get the overall match result
      assertEquals ("Hallo 2.3 Welt", m.group (0));

      // Insert replacement
      m.appendReplacement (sb, "<$1.$2>");
      assertFalse (m.find ());

      m.appendTail (sb);
      assertEquals ("This is <2.3> Text", sb.toString ());
    }

    // start over and replace all at once
    {
      m.reset ();
      final StringBuffer sb = new StringBuffer ();
      assertTrue (m.find ());

      // Get the first match result
      assertEquals ("2", m.group (1));
      assertEquals ("3", m.group (2));

      // Insert replacement
      m.appendReplacement (sb, "4");

      m.appendTail (sb);
      assertEquals ("This is 4 Text", sb.toString ());
    }

    // easy cheesy version
    {
      m.reset ();
      assertEquals ("This is <2.3> Text", m.replaceAll ("<$1.$2>"));
    }
  }

  /**
   * Test for method stringMatchesPattern
   */
  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testStringMatchesPattern ()
  {
    assertTrue (RegExHelper.stringMatchesPattern ("[0-9]+", "1234"));
    assertTrue (RegExHelper.stringMatchesPattern ("[0-9]+", "0"));
    assertFalse (RegExHelper.stringMatchesPattern ("[0-9]+", ""));
    assertTrue (RegExHelper.stringMatchesPattern ("[0-9]*", ""));
    assertTrue (RegExHelper.stringMatchesPattern ("abc", "abc"));
    assertTrue (RegExHelper.stringMatchesPattern ("abc+", "abcccccccccccc"));
    assertFalse (RegExHelper.stringMatchesPattern (".*[0-9]+.*", "abc\nx1234y\nzzz"));
    assertTrue (RegExHelper.stringMatchesPattern ("(?s).*[0-9]+.*", "abc\nx1234y\nzzz"));
    assertTrue (RegExHelper.stringMatchesPattern (".*[0-9]+.*", Pattern.DOTALL, "abc\nx1234y\nzzz"));

    try
    {
      // null regular expression not allowed
      RegExHelper.stringMatchesPattern (null, "any");
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // empty regular expression not allowed
      RegExHelper.stringMatchesPattern ("", "any");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      // null text not allowed
      RegExHelper.stringMatchesPattern ("[0-9]+", null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  /**
   * Test for method stringReplacePattern
   */
  @Test
  public void testStringReplacePattern ()
  {
    assertEquals ("xy", RegExHelper.stringReplacePattern ("\\$email", "$emaily", "x"));
  }

  @Test
  public void testIsValidPattern ()
  {
    assertTrue (RegExHelper.isValidPattern (""));
    assertTrue (RegExHelper.isValidPattern ("abc"));
    assertTrue (RegExHelper.isValidPattern ("ab.+c"));
    assertTrue (RegExHelper.isValidPattern ("^ab.+c"));
    assertTrue (RegExHelper.isValidPattern ("ab.+c$"));
    assertTrue (RegExHelper.isValidPattern ("^ab.+c$"));
    assertFalse (RegExHelper.isValidPattern ("*-[]"));

    try
    {
      RegExHelper.isValidPattern (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testGetAllMatchingGroupValues ()
  {
    // Non-matching
    assertNull (RegExHelper.getAllMatchingGroupValues ("H(.)llo", "Servus"));
    assertNull (RegExHelper.getAllMatchingGroupValues ("H([al]+)(.)", "Hello"));

    // With a single group
    assertArrayEquals (new String [] { "a" }, RegExHelper.getAllMatchingGroupValues ("H(.)llo", "Hallo"));
    assertArrayEquals (new String [] { "e" }, RegExHelper.getAllMatchingGroupValues ("H(.)llo", "Hello"));

    // With multiple groups
    assertArrayEquals (new String [] { "all", "o" }, RegExHelper.getAllMatchingGroupValues ("H([al]+)(.)", "Hallo"));
    assertArrayEquals (new String [] { "all", "o" }, RegExHelper.getAllMatchingGroupValues ("H([ael]+)(.)", "Hallo"));
    assertArrayEquals (new String [] { "ell", "o" }, RegExHelper.getAllMatchingGroupValues ("H([ael]+)(.)", "Hello"));
    assertArrayEquals (new String [] { "allall", "o" },
                       RegExHelper.getAllMatchingGroupValues ("H([ael]+)(.).*", "Hallallodrio"));

    // With a repeat indicator -> last match
    assertArrayEquals (new String [] { "l" }, RegExHelper.getAllMatchingGroupValues ("H([al]){1,3}o", "Hallo"));
    assertArrayEquals (new String [] { "all" }, RegExHelper.getAllMatchingGroupValues ("H([al]{1,3})o", "Hallo"));
    assertArrayEquals (new String [] { "l" }, RegExHelper.getAllMatchingGroupValues ("H([al]){1,2}lo", "Hallo"));
    assertArrayEquals (new String [] { "al" }, RegExHelper.getAllMatchingGroupValues ("H([al]{1,2})lo", "Hallo"));
    assertArrayEquals (new String [] { "a" }, RegExHelper.getAllMatchingGroupValues ("H([al]){1}llo", "Hallo"));
    assertArrayEquals (new String [] { "aa" }, RegExHelper.getAllMatchingGroupValues ("H([al]{1,2})llo", "Haallo"));

    // With nested groups
    assertArrayEquals (new String [] { "allallo", "allall", "o" },
                       RegExHelper.getAllMatchingGroupValues ("H(([ael]+)(.)).*", "Hallallodrio"));
    assertArrayEquals (new String [] { "allall", "o" },
                       RegExHelper.getAllMatchingGroupValues ("H(?:([ael]+)(.)).*", "Hallallodrio"));

    // With a non-capturing group
    assertArrayEquals (new String [] { "o" },
                       RegExHelper.getAllMatchingGroupValues ("H(?:[ael]+)(.).*", "Hallallodrio"));

    // With only non-capturing groups
    assertArrayEquals (new String [] {}, RegExHelper.getAllMatchingGroupValues ("H(?:[ael]+)(?:.).*", "Hallallodrio"));

    // Without groups
    assertArrayEquals (new String [] {}, RegExHelper.getAllMatchingGroupValues ("H.llo", "Hallo"));
    assertArrayEquals (new String [] {}, RegExHelper.getAllMatchingGroupValues ("H.llo", "Hello"));

    // With a back reference
    assertArrayEquals (new String [] { "H", "allo", "H" },
                       RegExHelper.getAllMatchingGroupValues ("(.)(.+)(\\1)", "HalloH"));
    assertArrayEquals (new String [] { "H", "H" }, RegExHelper.getAllMatchingGroupValues ("(.).+(\\1).*", "HalloHH"));
  }
}
