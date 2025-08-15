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
package com.helger.commons.version;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link Version}.
 *
 * @author Philip Helger
 */
public final class VersionTest
{
  @Test
  public void testVersionIntIntInt ()
  {
    // use 0.0.0
    Version v = new Version (0, 0, 0);
    assertEquals (0, v.getMajor ());
    assertEquals (0, v.getMinor ());
    assertEquals (0, v.getMicro ());
    assertNull (v.getQualifier ());
    assertNotNull (v.toString ());

    // use 0.0
    v = new Version (0, 0);
    assertEquals (0, v.getMajor ());
    assertEquals (0, v.getMinor ());
    assertEquals (0, v.getMicro ());
    assertNull (v.getQualifier ());

    // use 9.7
    v = new Version (9, 7);
    assertEquals (9, v.getMajor ());
    assertEquals (7, v.getMinor ());
    assertEquals (0, v.getMicro ());
    assertNull (v.getQualifier ());

    // use 0
    v = new Version (0);
    assertEquals (0, v.getMajor ());
    assertEquals (0, v.getMinor ());
    assertEquals (0, v.getMicro ());
    assertNull (v.getQualifier ());

    // use 2
    v = new Version (2);
    assertEquals (2, v.getMajor ());
    assertEquals (0, v.getMinor ());
    assertEquals (0, v.getMicro ());
    assertNull (v.getQualifier ());

    // try 97.98.99
    v = new Version (97, 98, 99);
    assertEquals (97, v.getMajor ());
    assertEquals (98, v.getMinor ());
    assertEquals (99, v.getMicro ());
    assertNull (v.getQualifier ());

    // try 97.98.99.alpha
    v = new Version (97, 98, 99, "alpha");
    assertEquals (97, v.getMajor ());
    assertEquals (98, v.getMinor ());
    assertEquals (99, v.getMicro ());
    assertEquals ("alpha", v.getQualifier ());

    try
    {
      // try negative value - needs to fail
      new Version (-1, 1, 1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      // try negative value - needs to fail
      new Version (1, -1, 1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      // try negative value - needs to fail
      new Version (1, 1, -1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testVersionString ()
  {
    // check valid
    Version v = Version.parseDotOnly ("1.2.3.alpha");
    assertEquals (1, v.getMajor ());
    assertEquals (2, v.getMinor ());
    assertEquals (3, v.getMicro ());
    assertEquals ("alpha", v.getQualifier ());

    v = Version.parse ("1.2.3.alpha");
    assertEquals (1, v.getMajor ());
    assertEquals (2, v.getMinor ());
    assertEquals (3, v.getMicro ());
    assertEquals ("alpha", v.getQualifier ());

    // no qualifier
    v = Version.parseDotOnly ("4.5.6");
    assertEquals (4, v.getMajor ());
    assertEquals (5, v.getMinor ());
    assertEquals (6, v.getMicro ());
    assertNull (v.getQualifier ());

    v = Version.parse ("4.5.6");
    assertEquals (4, v.getMajor ());
    assertEquals (5, v.getMinor ());
    assertEquals (6, v.getMicro ());
    assertNull (v.getQualifier ());

    // no micro
    v = Version.parseDotOnly ("7.8");
    assertEquals (7, v.getMajor ());
    assertEquals (8, v.getMinor ());
    assertEquals (0, v.getMicro ());
    assertNull (v.getQualifier ());

    v = Version.parse ("7.8");
    assertEquals (7, v.getMajor ());
    assertEquals (8, v.getMinor ());
    assertEquals (0, v.getMicro ());
    assertNull (v.getQualifier ());

    // no minor
    v = Version.parseDotOnly ("9");
    assertEquals (9, v.getMajor ());
    assertEquals (0, v.getMinor ());
    assertEquals (0, v.getMicro ());
    assertNull (v.getQualifier ());

    v = Version.parse ("9");
    assertEquals (9, v.getMajor ());
    assertEquals (0, v.getMinor ());
    assertEquals (0, v.getMicro ());
    assertNull (v.getQualifier ());

    try
    {
      // try negative value - needs to fail
      Version.parseDotOnly ("-1.1.1");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      // try negative value - needs to fail
      Version.parse ("-1.1.1");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      Version.parseDotOnly ("1.-1.1");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      Version.parse ("1.-1.1");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      Version.parseDotOnly ("1.1.-1");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      Version.parse ("1.1.-1");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    // empty string
    v = Version.parseDotOnly ("");
    assertEquals (0, v.getMajor ());
    assertEquals (0, v.getMinor ());
    assertEquals (0, v.getMicro ());
    assertNull (v.getQualifier ());

    v = Version.parse ("");
    assertEquals (0, v.getMajor ());
    assertEquals (0, v.getMinor ());
    assertEquals (0, v.getMicro ());
    assertNull (v.getQualifier ());

    // very weird stuff - fails because String.split does not split
    v = Version.parseDotOnly ("...");
    assertEquals (0, v.getMajor ());
    assertEquals (0, v.getMinor ());
    assertEquals (0, v.getMicro ());
    assertNull (v.getQualifier ());

    v = Version.parse ("...");
    assertEquals (0, v.getMajor ());
    assertEquals (0, v.getMinor ());
    assertEquals (0, v.getMicro ());
    assertNull (v.getQualifier ());

    // very weird stuff - fails because String.split does not split
    v = Version.parseDotOnly ("..");
    assertEquals (0, v.getMajor ());
    assertEquals (0, v.getMinor ());
    assertEquals (0, v.getMicro ());
    assertNull (v.getQualifier ());

    v = Version.parse ("..");
    assertEquals (0, v.getMajor ());
    assertEquals (0, v.getMinor ());
    assertEquals (0, v.getMicro ());
    assertNull (v.getQualifier ());

    // very weird stuff - fails because String.split does not split
    v = Version.parseDotOnly (".");
    assertEquals (0, v.getMajor ());
    assertEquals (0, v.getMinor ());
    assertEquals (0, v.getMicro ());
    assertNull (v.getQualifier ());

    v = Version.parse (".");
    assertEquals (0, v.getMajor ());
    assertEquals (0, v.getMinor ());
    assertEquals (0, v.getMicro ());
    assertNull (v.getQualifier ());

    // null string
    v = Version.parseDotOnly (null);
    assertEquals (0, v.getMajor ());
    assertEquals (0, v.getMinor ());
    assertEquals (0, v.getMicro ());
    assertNull (v.getQualifier ());

    v = Version.parse (null);
    assertEquals (0, v.getMajor ());
    assertEquals (0, v.getMinor ());
    assertEquals (0, v.getMicro ());
    assertNull (v.getQualifier ());

    // alphabetic string
    v = Version.parseDotOnly ("a.b.c.d");
    assertEquals (0, v.getMajor ());
    assertEquals (0, v.getMinor ());
    assertEquals (0, v.getMicro ());
    assertEquals ("d", v.getQualifier ());

    // alphabetic string
    v = Version.parse ("a.b.c.d");
    assertEquals (0, v.getMajor ());
    assertEquals (0, v.getMinor ());
    assertEquals (0, v.getMicro ());
    assertEquals ("a.b.c.d", v.getQualifier ());

    // alphabetic string
    v = Version.parseDotOnly ("a.b.c");
    assertEquals (0, v.getMajor ());
    assertEquals (0, v.getMinor ());
    assertEquals (0, v.getMicro ());
    assertNull (v.getQualifier ());

    // alphabetic string
    v = Version.parse ("a.b.c");
    assertEquals (0, v.getMajor ());
    assertEquals (0, v.getMinor ());
    assertEquals (0, v.getMicro ());
    assertEquals ("a.b.c", v.getQualifier ());

    // alphabetic string
    v = Version.parseDotOnly ("a5.b5.c5");
    assertEquals (0, v.getMajor ());
    assertEquals (0, v.getMinor ());
    assertEquals (0, v.getMicro ());
    assertNull (v.getQualifier ());

    // alphabetic string
    v = Version.parse ("a5.b5.c5");
    assertEquals (0, v.getMajor ());
    assertEquals (0, v.getMinor ());
    assertEquals (0, v.getMicro ());
    assertEquals ("a5.b5.c5", v.getQualifier ());

    // invalid numeric string
    v = Version.parseDotOnly ("3a.3b.3c");
    assertEquals (0, v.getMajor ());
    assertEquals (0, v.getMinor ());
    assertEquals (0, v.getMicro ());
    assertNull (v.getQualifier ());

    // invalid numeric string
    v = Version.parse ("3a.3b.3c");
    assertEquals (0, v.getMajor ());
    assertEquals (0, v.getMinor ());
    assertEquals (0, v.getMicro ());
    assertEquals ("3a.3b.3c", v.getQualifier ());

    // Dash separator
    v = Version.parseDotOnly ("3.0.0-RC1");
    assertEquals (3, v.getMajor ());
    assertEquals (0, v.getMinor ());
    assertEquals (0, v.getMicro ());
    assertNull (v.getQualifier ());

    // Dash separator
    v = Version.parse ("3.0.0-RC1");
    assertEquals (3, v.getMajor ());
    assertEquals (0, v.getMinor ());
    assertEquals (0, v.getMicro ());
    assertEquals ("RC1", v.getQualifier ());
  }

  @Test
  public void testCompareTo ()
  {
    final Version v1 = new Version (1, 2, 3);
    final Version v2 = new Version (1, 2, 3);
    final Version v3 = Version.parse ("1.2.3");
    final Version v4 = Version.parse ("1.2.3.alpha");
    final Version v5 = Version.parse ("1.2.3.beta");
    final Version v6 = Version.parse ("1.2");
    final Version v7 = Version.parse ("1");
    final Version v8 = Version.parse (null);
    final Version v9 = new Version (0, 0, 0);

    // check v1
    assertEquals (0, v1.compareTo (v1));
    assertEquals (0, v1.compareTo (v2));
    assertEquals (0, v1.compareTo (v3));
    assertTrue (v1.compareTo (v4) < 0);
    assertTrue (v1.compareTo (v5) < 0);
    assertTrue (v1.compareTo (v6) > 0);
    assertTrue (v1.compareTo (v7) > 0);
    assertTrue (v1.compareTo (v8) > 0);
    assertTrue (v1.compareTo (v9) > 0);

    // check v2
    assertEquals (0, v2.compareTo (v1));
    assertEquals (0, v2.compareTo (v2));
    assertEquals (0, v2.compareTo (v3));
    assertTrue (v2.compareTo (v4) < 0);
    assertTrue (v2.compareTo (v5) < 0);
    assertTrue (v2.compareTo (v6) > 0);
    assertTrue (v2.compareTo (v7) > 0);
    assertTrue (v2.compareTo (v8) > 0);
    assertTrue (v2.compareTo (v9) > 0);

    // check v3
    assertEquals (0, v3.compareTo (v1));
    assertEquals (0, v3.compareTo (v2));
    assertEquals (0, v3.compareTo (v3));
    assertTrue (v3.compareTo (v4) < 0);
    assertTrue (v3.compareTo (v5) < 0);
    assertTrue (v3.compareTo (v6) > 0);
    assertTrue (v3.compareTo (v7) > 0);
    assertTrue (v3.compareTo (v8) > 0);
    assertTrue (v3.compareTo (v9) > 0);

    // check v4
    assertTrue (v4.compareTo (v1) > 0);
    assertTrue (v4.compareTo (v2) > 0);
    assertTrue (v4.compareTo (v3) > 0);
    assertEquals (0, v4.compareTo (v4));
    assertTrue (v4.compareTo (v5) < 0);
    assertTrue (v4.compareTo (v6) > 0);
    assertTrue (v4.compareTo (v7) > 0);
    assertTrue (v4.compareTo (v8) > 0);
    assertTrue (v4.compareTo (v9) > 0);

    // check v5
    assertTrue (v5.compareTo (v1) > 0);
    assertTrue (v5.compareTo (v2) > 0);
    assertTrue (v5.compareTo (v3) > 0);
    assertTrue (v5.compareTo (v4) > 0);
    assertEquals (0, v5.compareTo (v5));
    assertTrue (v5.compareTo (v6) > 0);
    assertTrue (v5.compareTo (v7) > 0);
    assertTrue (v5.compareTo (v8) > 0);
    assertTrue (v5.compareTo (v9) > 0);

    // check v6
    assertTrue (v6.compareTo (v1) < 0);
    assertTrue (v6.compareTo (v2) < 0);
    assertTrue (v6.compareTo (v3) < 0);
    assertTrue (v6.compareTo (v4) < 0);
    assertTrue (v6.compareTo (v5) < 0);
    assertEquals (0, v6.compareTo (v6));
    assertTrue (v6.compareTo (v7) > 0);
    assertTrue (v6.compareTo (v8) > 0);
    assertTrue (v6.compareTo (v9) > 0);

    // check v7
    assertTrue (v7.compareTo (v1) < 0);
    assertTrue (v7.compareTo (v2) < 0);
    assertTrue (v7.compareTo (v3) < 0);
    assertTrue (v7.compareTo (v4) < 0);
    assertTrue (v7.compareTo (v5) < 0);
    assertTrue (v7.compareTo (v6) < 0);
    assertEquals (0, v7.compareTo (v7));
    assertTrue (v7.compareTo (v8) > 0);
    assertTrue (v7.compareTo (v9) > 0);

    // check v8
    assertTrue (v8.compareTo (v1) < 0);
    assertTrue (v8.compareTo (v2) < 0);
    assertTrue (v8.compareTo (v3) < 0);
    assertTrue (v8.compareTo (v4) < 0);
    assertTrue (v8.compareTo (v5) < 0);
    assertTrue (v8.compareTo (v6) < 0);
    assertTrue (v8.compareTo (v7) < 0);
    assertEquals (0, v8.compareTo (v8));
    assertEquals (0, v8.compareTo (v9));

    // check v9
    assertTrue (v9.compareTo (v1) < 0);
    assertTrue (v9.compareTo (v2) < 0);
    assertTrue (v9.compareTo (v3) < 0);
    assertTrue (v9.compareTo (v4) < 0);
    assertTrue (v9.compareTo (v5) < 0);
    assertTrue (v9.compareTo (v6) < 0);
    assertTrue (v9.compareTo (v7) < 0);
    assertEquals (0, v9.compareTo (v8));
    assertEquals (0, v9.compareTo (v9));

    // test weird stuff
    try
    {
      v1.compareTo (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testEqualsObject ()
  {
    final Version v1 = new Version (1, 2, 3);
    final Version v2 = new Version (1, 2, 3);
    final Version v3 = Version.parse ("1.2.3");
    final Version v4 = Version.parse ("1.2.3.alpha");
    final Version v5 = Version.parse ("1.2.3.beta");
    final Version v6 = Version.parse ("1.2");
    final Version v7 = Version.parse ("1");
    final Version v8 = Version.parse (null);
    final Version v9 = new Version (0, 0, 0);

    // test v1
    assertEquals (v1, v1);
    assertEquals (v1, v2);
    assertEquals (v1, v3);
    assertNotEquals (v1, (v4));
    assertNotEquals (v1, (v5));
    assertNotEquals (v1, (v6));
    assertNotEquals (v1, (v7));
    assertNotEquals (v1, (v8));
    assertNotEquals (v1, (v9));

    // test v2
    assertEquals (v2, v1);
    assertEquals (v2, v2);
    assertEquals (v2, v3);
    assertNotEquals (v2, (v4));
    assertNotEquals (v2, (v5));
    assertNotEquals (v2, (v6));
    assertNotEquals (v2, (v7));
    assertNotEquals (v2, (v8));
    assertNotEquals (v2, (v9));

    // test v3
    assertEquals (v3, v1);
    assertEquals (v3, v2);
    assertEquals (v3, v3);
    assertNotEquals (v3, (v4));
    assertNotEquals (v3, (v5));
    assertNotEquals (v3, (v6));
    assertNotEquals (v3, (v7));
    assertNotEquals (v3, (v8));
    assertNotEquals (v3, (v9));

    // test v4
    assertNotEquals (v4, (v1));
    assertNotEquals (v4, (v2));
    assertNotEquals (v4, (v3));
    assertEquals (v4, v4);
    assertNotEquals (v4, (v5));
    assertNotEquals (v4, (v6));
    assertNotEquals (v4, (v7));
    assertNotEquals (v4, (v8));
    assertNotEquals (v4, (v9));

    // test v5
    assertNotEquals (v5, (v1));
    assertNotEquals (v5, (v2));
    assertNotEquals (v5, (v3));
    assertNotEquals (v5, (v4));
    assertEquals (v5, v5);
    assertNotEquals (v5, (v6));
    assertNotEquals (v5, (v7));
    assertNotEquals (v5, (v8));
    assertNotEquals (v5, (v9));

    // test v6
    assertNotEquals (v6, (v1));
    assertNotEquals (v6, (v2));
    assertNotEquals (v6, (v3));
    assertNotEquals (v6, (v4));
    assertNotEquals (v6, (v5));
    assertEquals (v6, v6);
    assertNotEquals (v6, (v7));
    assertNotEquals (v6, (v8));
    assertNotEquals (v6, (v9));

    // test v7
    assertNotEquals (v7, (v1));
    assertNotEquals (v7, (v2));
    assertNotEquals (v7, (v3));
    assertNotEquals (v7, (v4));
    assertNotEquals (v7, (v5));
    assertNotEquals (v7, (v6));
    assertEquals (v7, v7);
    assertNotEquals (v7, (v8));
    assertNotEquals (v7, (v9));

    // test v8
    assertNotEquals (v8, (v1));
    assertNotEquals (v8, (v2));
    assertNotEquals (v8, (v3));
    assertNotEquals (v8, (v4));
    assertNotEquals (v8, (v5));
    assertNotEquals (v8, (v6));
    assertNotEquals (v8, (v7));
    assertEquals (v8, v8);
    assertEquals (v8, v9);

    // test v9
    assertNotEquals (v9, (v1));
    assertNotEquals (v9, (v2));
    assertNotEquals (v9, (v3));
    assertNotEquals (v9, (v4));
    assertNotEquals (v9, (v5));
    assertNotEquals (v9, (v6));
    assertNotEquals (v9, (v7));
    assertEquals (v9, v8);
    assertEquals (v9, v9);

    // test weird stuff
    assertNotEquals (v1, null);
    assertNotEquals (v1, "anyString");

    assertEquals (Version.parse ("2.1.0"), Version.parse ("2.1"));
    assertEquals (Version.parse ("2.0.0"), Version.parse ("2"));
  }

  @Test
  public void testGetAsString ()
  {
    Version v = new Version (1, 2, 3);
    assertEquals ("1.2.3", v.getAsString ());
    assertEquals (v, Version.parse (v.getAsString ()));

    v = Version.parse ("1.2.3.");
    assertEquals ("1.2.3", v.getAsString ());
    assertEquals (v, Version.parse (v.getAsString ()));

    v = Version.parse ("1.2.3.alpha");
    assertEquals ("1.2.3.alpha", v.getAsString ());
    assertEquals (v, Version.parse (v.getAsString ()));

    v = Version.parse ("1.2.0.alpha");
    assertEquals ("1.2.0.alpha", v.getAsString ());
    assertEquals (v, Version.parse (v.getAsString ()));

    v = Version.parse ("1.2.3");
    assertEquals ("1.2.3", v.getAsString ());
    assertEquals (v, Version.parse (v.getAsString ()));

    v = Version.parse ("1.2.0");
    assertEquals ("1.2", v.getAsString ());
    assertEquals (v, Version.parse (v.getAsString ()));

    v = Version.parse ("1.0.2");
    assertEquals ("1.0.2", v.getAsString ());
    assertEquals (v, Version.parse (v.getAsString ()));

    v = Version.parse ("4.5");
    assertEquals ("4.5", v.getAsString ());
    assertEquals (v, Version.parse (v.getAsString ()));

    v = Version.parse ("4.0");
    assertEquals ("4", v.getAsString ());
    assertEquals (v, Version.parse (v.getAsString ()));

    v = Version.parse ("6");
    assertEquals ("6", v.getAsString ());
    assertEquals (v, Version.parse (v.getAsString ()));

    v = Version.parse (null);
    assertEquals (Version.DEFAULT_VERSION_STRING, v.getAsString ());
    assertEquals (v, Version.parse (v.getAsString ()));

    v = Version.parse (".2");
    assertEquals ("0.2", v.getAsString ());
    assertEquals (v, Version.parse (v.getAsString ()));

    v = Version.parse ("..2");
    assertEquals ("0.0.2", v.getAsString ());
    assertEquals (v, Version.parse (v.getAsString ()));

    v = Version.parse ("...2");
    assertEquals ("0.0.0.2", v.getAsString ());
    assertEquals (v, Version.parse (v.getAsString ()));

    v = Version.parseDotOnly ("3.0.0-RC1");
    assertEquals ("3", v.getAsString ());
    assertEquals (v, Version.parse (v.getAsString ()));

    v = Version.parse ("3.0.0-RC1");
    assertEquals ("3.0.0.RC1", v.getAsString ());
    assertEquals (v, Version.parse (v.getAsString ()));
  }

  @Test
  public void testGetAsStringBoolean ()
  {
    Version v = new Version (1, 2, 3);
    assertEquals ("1.2.3", v.getAsString (true));
    assertEquals ("1.2.3", v.getAsString (true, true));
    assertEquals (v, Version.parse (v.getAsString (true)));

    v = Version.parse ("1.2.3.");
    assertEquals ("1.2.3", v.getAsString (true));
    assertEquals (v, Version.parse (v.getAsString (true)));

    v = Version.parse ("1.2.3.alpha");
    assertEquals ("1.2.3.alpha", v.getAsString (true));
    assertEquals (v, Version.parse (v.getAsString (true)));

    v = Version.parse ("1.2.0.alpha");
    assertEquals ("1.2.0.alpha", v.getAsString (true));
    assertEquals (v, Version.parse (v.getAsString (true)));

    v = Version.parse ("1.2.3");
    assertEquals ("1.2.3", v.getAsString (true));
    assertEquals ("1.2.3", v.getAsString (false));
    assertEquals ("1.2.3", v.getAsString (false, true));
    assertEquals (v, Version.parse (v.getAsString (true)));

    v = Version.parse ("1.2.0");
    assertEquals ("1.2.0", v.getAsString (true));
    assertEquals ("1.2", v.getAsString (false));
    assertEquals ("1.2", v.getAsString (false, true));
    assertEquals (v, Version.parse (v.getAsString (true)));

    v = Version.parse ("1.0.2");
    assertEquals ("1.0.2", v.getAsString (true));
    assertEquals ("1.0.2", v.getAsString (false));
    assertEquals ("1.0.2", v.getAsString (false, true));
    assertEquals (v, Version.parse (v.getAsString (true)));

    v = Version.parse ("4.5");
    assertEquals ("4.5.0", v.getAsString (true));
    assertEquals ("4.5", v.getAsString (false));
    assertEquals ("4.5", v.getAsString (false, true));
    assertEquals (v, Version.parse (v.getAsString (true)));

    v = Version.parse ("4.0");
    assertEquals ("4.0.0", v.getAsString (true));
    assertEquals ("4", v.getAsString (false));
    assertEquals ("4.0", v.getAsString (false, true));
    assertEquals (v, Version.parse (v.getAsString (true)));

    v = Version.parse ("6");
    assertEquals ("6.0.0", v.getAsString (true));
    assertEquals ("6", v.getAsString (false));
    assertEquals ("6.0", v.getAsString (false, true));
    assertEquals (v, Version.parse (v.getAsString (true)));

    v = Version.parse (null);
    assertEquals ("0.0.0", v.getAsString (true));
    assertEquals ("0", v.getAsString (false));
    assertEquals ("0.0", v.getAsString (false, true));
    assertEquals (v, Version.parse (v.getAsString (true)));

    v = Version.parse (".2");
    assertEquals ("0.2.0", v.getAsString (true));
    assertEquals (v, Version.parse (v.getAsString (true)));

    v = Version.parse ("..2");
    assertEquals ("0.0.2", v.getAsString (true));
    assertEquals (v, Version.parse (v.getAsString (true)));

    v = Version.parse ("...2");
    assertEquals ("0.0.0.2", v.getAsString (true));
    assertEquals (v, Version.parse (v.getAsString (true)));

    v = Version.parseDotOnly ("3.0.0-RC1");
    assertEquals ("3.0.0", v.getAsString (true));
    assertEquals (v, Version.parse (v.getAsString (true)));

    v = Version.parse ("3.0.0-RC1");
    assertEquals ("3.0.0.RC1", v.getAsString (true));
    assertEquals (v, Version.parse (v.getAsString (true)));

    v = Version.parseDotOnly ("3-RC1");
    assertEquals ("0.0.0", v.getAsString (true));
    assertEquals (v, Version.parse (v.getAsString (true)));

    v = Version.parse ("3-RC1");
    assertEquals ("3.0.0.RC1", v.getAsString (true));
    assertEquals (v, Version.parse (v.getAsString (true)));
  }

  @Test
  public void testHashCode ()
  {
    TestHelper.testDefaultImplementationWithEqualContentObject (new Version (1, 2, 3, "4"),
                                                                       new Version (1, 2, 3, "4"));
    TestHelper.testDefaultImplementationWithEqualContentObject (new Version (1, 2, 3, "4"),
                                                                       Version.parse ("1.2.3.4"));
    TestHelper.testDefaultImplementationWithEqualContentObject (new Version (1), Version.parse ("1"));
    TestHelper.testDefaultImplementationWithEqualContentObject (Version.parse ("1"), Version.parse ("   1"));

    TestHelper.testDefaultImplementationWithDifferentContentObject (new Version (1, 2, 3, "4"),
                                                                           new Version (1, 2, 3));
    TestHelper.testDefaultImplementationWithDifferentContentObject (new Version (1, 2, 3, "4"),
                                                                           new Version (1, 2, 3, "5"));
    TestHelper.testDefaultImplementationWithDifferentContentObject (new Version (1, 2, 3, "4"),
                                                                           Version.parse ("1.3.3.4"));
    TestHelper.testDefaultImplementationWithDifferentContentObject (new Version (2), Version.parse ("1"));
    TestHelper.testDefaultImplementationWithDifferentContentObject (Version.parse ("1"),
                                                                           Version.parse ("   11"));
  }

  @Test
  public void testCompare ()
  {
    assertTrue (new Version (1, 2).isGT (new Version (1, 1)));
    assertFalse (new Version (1, 2).isGT (new Version (1, 2)));
    assertFalse (new Version (1, 2).isGT (new Version (1, 3)));
    assertTrue (new Version (1, 2).isGE (new Version (1, 1)));
    assertTrue (new Version (1, 2).isGE (new Version (1, 2)));
    assertFalse (new Version (1, 2).isGE (new Version (1, 3)));
    assertFalse (new Version (1, 2).isLT (new Version (1, 1)));
    assertFalse (new Version (1, 2).isLT (new Version (1, 2)));
    assertTrue (new Version (1, 2).isLT (new Version (1, 3)));
    assertFalse (new Version (1, 2).isLE (new Version (1, 1)));
    assertTrue (new Version (1, 2).isLE (new Version (1, 2)));
    assertTrue (new Version (1, 2).isLE (new Version (1, 3)));
  }
}
