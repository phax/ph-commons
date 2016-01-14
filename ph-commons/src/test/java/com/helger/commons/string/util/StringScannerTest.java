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
package com.helger.commons.string.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link StringScanner}.
 *
 * @author Philip Helger
 */
public final class StringScannerTest
{
  @Test
  @SuppressFBWarnings ("TQ_NEVER_VALUE_USED_WHERE_ALWAYS_REQUIRED")
  public void testAll ()
  {
    final String sInput = "  abc [def][ghi][xx[yy] [zz]]AbAbBBXrest";
    final StringScanner ss = new StringScanner (sInput);
    assertEquals (0, ss.getCurrentIndex ());
    assertEquals (0, ss.findFirstIndex (' '));
    assertEquals (-1, ss.findFirstIndex ('0'));
    assertEquals (2, ss.findFirstIndex ("abc".toCharArray ()));
    assertEquals (2, ss.findFirstIndex ("cba".toCharArray ()));
    assertEquals (0, ss.getCurrentIndex ());
    ss.skip (1).skipbackWhitespaces ();
    assertEquals (0, ss.getCurrentIndex ());
    ss.skipWhitespaces ();
    assertEquals (2, ss.getCurrentIndex ());
    assertEquals ('a', ss.getCurrentChar ());
    assertEquals ("abc", ss.getUntilWhiteSpace ());
    assertEquals (5, ss.getCurrentIndex ());
    ss.skipWhitespaces ();
    assertEquals (6, ss.getCurrentIndex ());
    ss.skip (1);
    assertEquals (7, ss.getCurrentIndex ());
    assertEquals ("def", ss.getUntil (']'));
    assertEquals (10, ss.getCurrentIndex ());
    assertEquals (']', ss.getCurrentChar ());
    ss.skip (2);
    assertEquals (12, ss.getCurrentIndex ());
    assertEquals ("ghi", ss.getUntilBalanced (1, '[', ']'));
    assertEquals (16, ss.getCurrentIndex ());
    ss.skip (1);
    assertEquals ("xx[yy] [zz]", ss.getUntilBalanced (1, '[', ']'));
    assertEquals (29, ss.getCurrentIndex ());
    ss.skip (1);
    assertEquals ("bAbB", ss.getUntilBalanced (1, 'A', 'B'));
    assertEquals (35, ss.getCurrentIndex ());
    assertEquals (5, ss.getRemainingChars ());
    assertEquals ("X", ss.getUntilIndex (36));
    assertEquals (36, ss.getCurrentIndex ());
    assertEquals (4, ss.getRemainingChars ());
    assertTrue (ss.isCurrentChar ('r'));
    assertFalse (ss.isCurrentChar ('e'));
    assertEquals ("rest", ss.getRest ());
    assertEquals (40, ss.getCurrentIndex ());
    assertEquals (0, ss.getRemainingChars ());
    ss.skip (1234567);
    assertEquals (40, ss.getCurrentIndex ());
    assertEquals (0, ss.getRemainingChars ());
    ss.skipWhitespaces ();
    assertEquals (40, ss.getCurrentIndex ());
    ss.skipbackWhitespaces ();
    assertEquals (40, ss.getCurrentIndex ());
    assertEquals ("", ss.getUntilBalanced (1, 'a', 'b'));

    try
    {
      // And end of string
      ss.getCurrentChar ();
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      // null not allowed
      new StringScanner (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    assertNotNull (ss.toString ());
    ss.setIndex (0);
    assertEquals (0, ss.getCurrentIndex ());
    ss.setIndex (sInput.length () - 4);
    assertEquals ("rest", ss.getUntilWhiteSpace ());

    try
    {
      // illegal
      ss.setIndex (-1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      // illegal
      ss.setIndex (sInput.length () + 1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }
}
