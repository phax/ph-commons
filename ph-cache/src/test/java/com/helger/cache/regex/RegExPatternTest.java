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
package com.helger.cache.regex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.regex.Pattern;

import org.junit.Test;

/**
 * Test class for {@link RegExPattern}.
 *
 * @author Philip Helger
 */
public final class RegExPatternTest
{
  /**
   * Test method checkPatternConsistency
   */
  @Test
  public void testCheckPatternConsistency ()
  {
    RegExPattern.checkPatternConsistency ("any\\$here");
    RegExPattern.checkPatternConsistency ("at the end$");
    RegExPattern.checkPatternConsistency ("group $1 of $0");
    RegExPattern.checkPatternConsistency ("$1 and $2 are OK");
    try
    {
      // RegEx contains unquoted $!
      RegExPattern.checkPatternConsistency ("any$here");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    // $ in group ending is OK
    RegExPattern.checkPatternConsistency ("(abc$)");
  }

  @Test
  public void testConstructorAndGetters ()
  {
    final RegExPattern p = new RegExPattern ("[a-z]+");
    assertEquals ("[a-z]+", p.getRegEx ());
    assertEquals (0, p.getOptions ());

    final RegExPattern p2 = new RegExPattern ("[A-Z]+", Pattern.CASE_INSENSITIVE);
    assertEquals ("[A-Z]+", p2.getRegEx ());
    assertEquals (Pattern.CASE_INSENSITIVE, p2.getOptions ());
  }

  @Test
  public void testGetAsPattern ()
  {
    final RegExPattern p = new RegExPattern ("\\d+");
    final Pattern aPattern = p.getAsPattern ();
    assertNotNull (aPattern);
    assertEquals ("\\d+", aPattern.pattern ());
    assertEquals (0, aPattern.flags ());
    // Calling again returns the same (cached) instance
    assertSame (aPattern, p.getAsPattern ());
  }

  @Test
  public void testGetAsPatternWithOptions ()
  {
    final RegExPattern p = new RegExPattern ("abc", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    final Pattern aPattern = p.getAsPattern ();
    assertNotNull (aPattern);
    assertEquals (Pattern.CASE_INSENSITIVE | Pattern.MULTILINE, aPattern.flags ());
  }

  @Test
  public void testGetAsPatternInvalidRegex ()
  {
    final RegExPattern p = new RegExPattern ("[invalid");
    try
    {
      p.getAsPattern ();
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testEqualsAndHashCode ()
  {
    final RegExPattern p1 = new RegExPattern ("abc");
    final RegExPattern p2 = new RegExPattern ("abc");
    final RegExPattern p3 = new RegExPattern ("abc", Pattern.CASE_INSENSITIVE);
    final RegExPattern p4 = new RegExPattern ("def");

    // equals
    assertTrue (p1.equals (p1));
    assertTrue (p1.equals (p2));
    assertFalse (p1.equals (p3));
    assertFalse (p1.equals (p4));
    assertFalse (p1.equals (null));
    assertFalse (p1.equals ("abc"));

    // hashCode
    assertEquals (p1.hashCode (), p2.hashCode ());
    assertNotEquals (p1.hashCode (), p3.hashCode ());
  }

  @Test
  public void testToString ()
  {
    final RegExPattern p = new RegExPattern ("test");
    final String s = p.toString ();
    assertNotNull (s);
    assertTrue (s.contains ("test"));
  }

  @Test
  public void testDebugConsistencyChecks ()
  {
    final boolean bOld = RegExPattern.areDebugConsistencyChecksEnabled ();
    try
    {
      RegExPattern.enableDebugConsistencyChecks (true);
      assertTrue (RegExPattern.areDebugConsistencyChecksEnabled ());

      // This should pass consistency checks
      new RegExPattern ("valid$");

      // This should fail consistency checks
      try
      {
        new RegExPattern ("any$here");
        fail ();
      }
      catch (final IllegalArgumentException ex)
      {
        // expected
      }

      RegExPattern.enableDebugConsistencyChecks (false);
      assertFalse (RegExPattern.areDebugConsistencyChecksEnabled ());

      // With checks disabled, inconsistent patterns should be accepted
      new RegExPattern ("any$here");
    }
    finally
    {
      RegExPattern.enableDebugConsistencyChecks (bOld);
    }
  }

  @Test
  public void testConstructorInvalidArgs ()
  {
    try
    {
      new RegExPattern (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {
      // expected
    }

    try
    {
      new RegExPattern ("");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }
}
