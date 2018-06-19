/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.commons.version;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

/**
 * JUnit test for class {@link VersionRange}.
 *
 * @author Philip Helger
 */
public final class VersionRangeTest
{
  @Test
  public void testVersionRange ()
  {
    VersionRange vr = VersionRange.parse ("[1.2.3, 4.5.6)");
    assertTrue (vr.isIncludingFloor ());
    assertEquals ("1.2.3", vr.getFloorVersion ().getAsString ());
    assertFalse (vr.isIncludingCeil ());
    assertEquals ("4.5.6", vr.getCeilVersion ().getAsString ());
    assertNotNull (vr.toString ());

    vr = VersionRange.parse ("   [1.2.3, 4.5.6)   ");
    assertTrue (vr.isIncludingFloor ());
    assertEquals ("1.2.3", vr.getFloorVersion ().getAsString ());
    assertFalse (vr.isIncludingCeil ());
    assertEquals ("4.5.6", vr.getCeilVersion ().getAsString ());

    vr = VersionRange.parse ("[1.2.3, 4.5.6]");
    assertTrue (vr.isIncludingFloor ());
    assertEquals ("1.2.3", vr.getFloorVersion ().getAsString ());
    assertTrue (vr.isIncludingCeil ());
    assertEquals ("4.5.6", vr.getCeilVersion ().getAsString ());

    vr = VersionRange.parse ("(1.2.3, 4.5.6]");
    assertFalse (vr.isIncludingFloor ());
    assertEquals ("1.2.3", vr.getFloorVersion ().getAsString ());
    assertTrue (vr.isIncludingCeil ());
    assertEquals ("4.5.6", vr.getCeilVersion ().getAsString ());

    vr = VersionRange.parse ("(1.2.3, 4.5.6)");
    assertFalse (vr.isIncludingFloor ());
    assertEquals ("1.2.3", vr.getFloorVersion ().getAsString ());
    assertFalse (vr.isIncludingCeil ());
    assertEquals ("4.5.6", vr.getCeilVersion ().getAsString ());

    vr = VersionRange.parse ("1.2.3");
    assertTrue (vr.isIncludingFloor ());
    assertEquals ("1.2.3", vr.getFloorVersion ().getAsString ());
    assertFalse (vr.isIncludingCeil ());
    assertNull (vr.getCeilVersion ());

    vr = VersionRange.parse ("[1.2.3");
    assertTrue (vr.isIncludingFloor ());
    assertEquals ("1.2.3", vr.getFloorVersion ().getAsString ());
    assertFalse (vr.isIncludingCeil ());
    assertNull (vr.getCeilVersion ());

    vr = VersionRange.parse ("(1.2.3");
    assertFalse (vr.isIncludingFloor ());
    assertEquals ("1.2.3", vr.getFloorVersion ().getAsString ());
    assertFalse (vr.isIncludingCeil ());
    assertNull (vr.getCeilVersion ());

    vr = VersionRange.parse ("1.2.3]");
    assertTrue (vr.isIncludingFloor ());
    assertEquals ("1.2.3", vr.getFloorVersion ().getAsString ());
    assertTrue (vr.isIncludingCeil ());
    assertNull (vr.getCeilVersion ());

    vr = VersionRange.parse ("1.2.3)");
    assertTrue (vr.isIncludingFloor ());
    assertEquals ("1.2.3", vr.getFloorVersion ().getAsString ());
    assertFalse (vr.isIncludingCeil ());
    assertNull (vr.getCeilVersion ());

    vr = VersionRange.parse ("[1.2.3]");
    assertTrue (vr.isIncludingFloor ());
    assertEquals ("1.2.3", vr.getFloorVersion ().getAsString ());
    assertTrue (vr.isIncludingCeil ());
    assertNull (vr.getCeilVersion ());

    vr = VersionRange.parse ("(1.2.3]");
    assertFalse (vr.isIncludingFloor ());
    assertEquals ("1.2.3", vr.getFloorVersion ().getAsString ());
    assertTrue (vr.isIncludingCeil ());
    assertNull (vr.getCeilVersion ());

    vr = VersionRange.parse ("5");
    assertTrue (vr.isIncludingFloor ());
    assertEquals ("5", vr.getFloorVersion ().getAsString ());
    assertFalse (vr.isIncludingCeil ());
    assertNull (vr.getCeilVersion ());

    vr = VersionRange.parse ("5,6");
    assertTrue (vr.isIncludingFloor ());
    assertEquals ("5", vr.getFloorVersion ().getAsString ());
    assertFalse (vr.isIncludingCeil ());
    assertEquals ("6", vr.getCeilVersion ().getAsString ());

    vr = VersionRange.parse (",6");
    assertTrue (vr.isIncludingFloor ());
    assertEquals (Version.DEFAULT_VERSION_STRING, vr.getFloorVersion ().getAsString ());
    assertFalse (vr.isIncludingCeil ());
    assertEquals ("6", vr.getCeilVersion ().getAsString ());

    vr = VersionRange.parse ("(,6]");
    assertFalse (vr.isIncludingFloor ());
    assertEquals (Version.DEFAULT_VERSION_STRING, vr.getFloorVersion ().getAsString ());
    assertTrue (vr.isIncludingCeil ());
    assertEquals ("6", vr.getCeilVersion ().getAsString ());

    vr = VersionRange.parse ("(]");
    assertFalse (vr.isIncludingFloor ());
    assertEquals (Version.DEFAULT_VERSION_STRING, vr.getFloorVersion ().getAsString ());
    assertTrue (vr.isIncludingCeil ());
    assertNull (vr.getCeilVersion ());

    // empty string: okay
    vr = VersionRange.parse ("");
    assertTrue (vr.isIncludingFloor ());
    assertEquals (Version.DEFAULT_VERSION_STRING, vr.getFloorVersion ().getAsString ());
    assertFalse (vr.isIncludingCeil ());
    assertNull (vr.getCeilVersion ());

    // null: okay
    vr = VersionRange.parse (null);
    assertTrue (vr.isIncludingFloor ());
    assertEquals (Version.DEFAULT_VERSION_STRING, vr.getFloorVersion ().getAsString ());
    assertFalse (vr.isIncludingCeil ());
    assertNull (vr.getCeilVersion ());

    // check floor > ceil
    try
    {
      VersionRange.parse ("1.2.3,0.0.1");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // okay
    }
  }

  @Test
  public void testCtor1 ()
  {
    VersionRange vr1 = new VersionRange (Version.parse ("1.0"), true, Version.parse ("2.0"), false);
    assertEquals ("[1,2)", vr1.getAsString ());

    // ceiling version null is OK
    vr1 = new VersionRange (Version.parse ("1.0"), true, null, false);
    assertEquals ("[1)", vr1.getAsString ());

    try
    {
      // null floor not allowed
      new VersionRange (null, true, Version.parse ("2.0"), false);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // floor > ceil
      new VersionRange (Version.parse ("2.1"), true, Version.parse ("2.0"), false);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testCtor2 ()
  {
    new VersionRange (Version.parse ("1.0"), Version.parse ("2.0"));
    try
    {
      // null floor not allowed
      new VersionRange (null, Version.parse ("2.0"));
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // floor > ceil
      new VersionRange (Version.parse ("2.1"), Version.parse ("2.0"));
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testVersionMatches ()
  {
    VersionRange vr = VersionRange.parse ("[1.2.3, 4.5.6]");
    assertTrue (vr.versionMatches (new Version (2)));
    assertTrue (vr.versionMatches (new Version (2, 99)));
    assertFalse (vr.versionMatches (new Version (1, 2)));
    assertTrue (vr.versionMatches (new Version (4, 5)));
    // check borders
    assertTrue (vr.versionMatches (new Version (1, 2, 3)));
    assertTrue (vr.versionMatches (new Version (1, 2, 3, "alpha")));
    assertTrue (vr.versionMatches (new Version (4, 5, 6)));
    assertFalse (vr.versionMatches (new Version (4, 5, 6, "beta")));

    vr = VersionRange.parse ("[1.2.3, 4.5.6)");
    assertTrue (vr.versionMatches (new Version (2)));
    assertTrue (vr.versionMatches (new Version (2, 99)));
    assertFalse (vr.versionMatches (new Version (1, 2)));
    assertTrue (vr.versionMatches (new Version (4, 5)));
    // check borders
    assertTrue (vr.versionMatches (new Version (1, 2, 3)));
    assertTrue (vr.versionMatches (new Version (1, 2, 3, "alpha")));
    assertFalse (vr.versionMatches (new Version (4, 5, 6)));
    assertFalse (vr.versionMatches (new Version (4, 5, 6, "beta")));

    vr = VersionRange.parse ("(1.2.3, 4.5.6]");
    assertTrue (vr.versionMatches (new Version (2)));
    assertTrue (vr.versionMatches (new Version (2, 99)));
    assertFalse (vr.versionMatches (new Version (1, 2)));
    assertTrue (vr.versionMatches (new Version (4, 5)));
    // check borders
    assertFalse (vr.versionMatches (new Version (1, 2, 3)));
    assertTrue (vr.versionMatches (new Version (1, 2, 3, "alpha")));
    assertTrue (vr.versionMatches (new Version (4, 5, 6)));
    assertFalse (vr.versionMatches (new Version (4, 5, 6, "beta")));

    vr = VersionRange.parse ("(1.2.3, 4.5.6)");
    assertTrue (vr.versionMatches (new Version (2)));
    assertTrue (vr.versionMatches (new Version (2, 99)));
    assertFalse (vr.versionMatches (new Version (1, 2)));
    assertTrue (vr.versionMatches (new Version (4, 5)));
    // check borders
    assertFalse (vr.versionMatches (new Version (1, 2, 3)));
    assertTrue (vr.versionMatches (new Version (1, 2, 3, "alpha")));
    assertFalse (vr.versionMatches (new Version (4, 5, 6)));
    assertFalse (vr.versionMatches (new Version (4, 5, 6, "beta")));
  }

  @Test
  public void testGetAsString ()
  {
    final String [] aTrueTests = new String [] { "[1.2.3]",
                                                 "(1.2.3)",
                                                 "[1.2)",
                                                 "(3.4]",
                                                 "[12,12]",
                                                 "(1,4]",
                                                 "[1.2.3,4.5.6)",
                                                 "(47.11.0.alpha,58]",
                                                 "[1.2.3,1.2.3]",
                                                 VersionRange.DEFAULT_VERSION_RANGE_STRING };
    for (final String element : aTrueTests)
    {
      final VersionRange vr = VersionRange.parse (element);
      assertEquals (vr.getAsString (), element);
    }
    final String [] aFalseTests = new String [] { " 1", "1.2.3", "[1.2.3, 4.5]", "00" };
    for (final String element : aFalseTests)
    {
      final VersionRange vr = VersionRange.parse (element);
      assertFalse (vr.getAsString ().equals (element));
    }
  }

  @Test
  public void testGetAsStringPrintZeroElements ()
  {
    final String [] aTrueTests = new String [] { "[1.2.3]",
                                                 "(1.2.3)",
                                                 "[1.2.0)",
                                                 "(3.4.0]",
                                                 "[12.0.0,12.0.0]",
                                                 "(1.0.0,4.0.0]",
                                                 "[1.2.3,4.5.6)",
                                                 "(47.11.0.alpha,58.0.0]",
                                                 "[1.2.3,1.2.3]",
                                                 "[0.0.0)" };
    for (final String element : aTrueTests)
    {
      final VersionRange vr = VersionRange.parse (element);
      assertEquals (vr.getAsString (true), element);
    }
    final String [] aFalseTests = new String [] { " 1.0.0", "1.2.3", "[1.2.3, 4.5.0]", "00.0.0" };
    for (final String element : aFalseTests)
    {
      final VersionRange vr = VersionRange.parse (element);
      assertFalse (vr.getAsString (true).equals (element));
    }
  }

  private static int _compare (final String sStr1, final String sStr2)
  {
    return VersionRange.parse (sStr1).compareTo (VersionRange.parse (sStr2));
  }

  @Test
  public void testCompareTo ()
  {
    assertEquals (0, _compare ("[1.2,2.0]", "[1.2,2.0]"));
    assertEquals (+1, _compare ("[1.2,2.0]", "[1.2,2.0)"));
    assertEquals (-1, _compare ("[1.2,2.0]", "(1.2,2.0]"));
    assertEquals (-1, _compare ("[1.2,2.0]", "(1.2,2.0)"));

    assertEquals (-1, _compare ("[1.2,2.0)", "[1.2,2.0]"));
    assertEquals (0, _compare ("[1.2,2.0)", "[1.2,2.0)"));
    assertEquals (-1, _compare ("[1.2,2.0)", "(1.2,2.0]"));
    assertEquals (-1, _compare ("[1.2,2.0)", "(1.2,2.0)"));

    assertEquals (+1, _compare ("(1.2,2.0]", "[1.2,2.0]"));
    assertEquals (+1, _compare ("(1.2,2.0]", "[1.2,2.0)"));
    assertEquals (0, _compare ("(1.2,2.0]", "(1.2,2.0]"));
    assertEquals (+1, _compare ("(1.2,2.0]", "(1.2,2.0)"));

    assertEquals (+1, _compare ("(1.2,2.0)", "[1.2,2.0]"));
    assertEquals (+1, _compare ("(1.2,2.0)", "[1.2,2.0)"));
    assertEquals (-1, _compare ("(1.2,2.0)", "(1.2,2.0]"));
    assertEquals (0, _compare ("(1.2,2.0)", "(1.2,2.0)"));

    assertEquals (+1, _compare ("(1.2,)", "[1.2,]"));
    assertEquals (+1, _compare ("(1.2,)", "[1.2,)"));
    assertEquals (-1, _compare ("(1.2,)", "(1.2,]"));
    assertEquals (0, _compare ("(1.2,)", "(1.2,)"));

    assertEquals (+1, _compare ("(,2.0)", "[,2.0]"));
    assertEquals (+1, _compare ("(,2.0)", "[,2.0)"));
    assertEquals (-1, _compare ("(,2.0)", "(,2.0]"));
    assertEquals (0, _compare ("(,2.0)", "(,2.0)"));

    assertEquals (+1, _compare ("(1.2,)", "[1.2,2.0]"));
    assertEquals (+1, _compare ("(1.2,)", "[1.2,2.0)"));
    assertEquals (+1, _compare ("(1.2,)", "(1.2,2.0]"));
    assertEquals (+1, _compare ("(1.2,)", "(1.2,2.0)"));

    assertEquals (-1, _compare ("[1.2,2.0]", "(1.2,)"));
    assertEquals (-1, _compare ("[1.2,2.0)", "(1.2,)"));
    assertEquals (-1, _compare ("(1.2,2.0]", "(1.2,)"));
    assertEquals (-1, _compare ("(1.2,2.0)", "(1.2,)"));

    assertEquals (-1, _compare ("(,2.0)", "[1.2,2.0]"));
    assertEquals (-1, _compare ("(,2.0)", "[1.2,2.0)"));
    assertEquals (-1, _compare ("(,2.0)", "(1.2,2.0]"));
    assertEquals (-1, _compare ("(,2.0)", "(1.2,2.0)"));

    // test single versions
    assertEquals (+1, _compare ("1.2", "1.1"));
    assertEquals (+1, _compare ("1.2", "1.1.9"));
    assertEquals (0, _compare ("1.2", "1.2"));
    assertEquals (0, _compare ("1.2", "1.2.0"));
    assertEquals (-1, _compare ("1.2", "1.2.0.alpha"));
    assertEquals (-1, _compare ("1.2", "1.2.1"));
    assertEquals (-1, _compare ("1.2", "1.3"));

    // test qualifier stuff
    assertEquals (+1, _compare ("1.2.0.beta", "1.2.0.alpha"));
    assertEquals (0, _compare ("1.2.0.beta", "1.2.0.beta"));
    assertEquals (-1, _compare ("1.2.0.beta", "1.2.0.beta1"));
    assertEquals (-1, _compare ("1.2.0.beta", "1.2.0.gamma"));
  }

  @Test
  public void testEquals ()
  {
    final VersionRange vr1 = VersionRange.parse ("(1.2,2.0]");
    assertEquals (vr1, vr1);
    assertNotEquals (vr1, null);
    assertNotEquals (vr1, "Not a VersionRange");
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (vr1, VersionRange.parse ("(1.2,2.0]"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (vr1, VersionRange.parse ("[1.2,2.0]"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (vr1, VersionRange.parse ("(1.2,2.0)"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (vr1, VersionRange.parse ("[1.2,2.0)"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (vr1, VersionRange.parse ("(1.2,]"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (vr1, VersionRange.parse ("(,2.0]"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (vr1, VersionRange.parse ("(1.3,2.0]"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (vr1, VersionRange.parse ("(1.2,2.1]"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (VersionRange.parse ("(,2.0]"),
                                                                           VersionRange.parse ("(,2.1]"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (VersionRange.parse ("(1.2]"),
                                                                           VersionRange.parse ("(1.3]"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (VersionRange.parse ("(1.2,]"),
                                                                           VersionRange.parse ("(,2.0]"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (VersionRange.parse ("(,2.0]"),
                                                                           VersionRange.parse ("(1.2,]"));

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (VersionRange.parse ("(1.2]"),
                                                                       VersionRange.parse ("(1.2]"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (VersionRange.parse ("(1.2]"),
                                                                           VersionRange.parse ("(1.2)"));
  }

  @Test
  public void testSerialization () throws Exception
  {
    CommonsTestHelper.testDefaultSerialization (VersionRange.parse ("(1.2]"));
    CommonsTestHelper.testDefaultSerialization (VersionRange.parse ("(0)"));
  }
}
