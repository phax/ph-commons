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
package com.helger.security.crl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Test class for class {@link CRLAllowList}.
 *
 * @author Philip Helger
 */
public final class CRLAllowListTest
{
  @Test
  public void testEmptyAllowsEverything ()
  {
    final CRLAllowList aList = new CRLAllowList ();
    assertTrue (aList.isEmpty ());
    assertEquals (0, aList.getPrefixCount ());

    // An empty allow list matches the legacy behavior - everything is allowed
    assertTrue (aList.isAllowed ("http://crl.example.org/foo.crl"));
    assertTrue (aList.isAllowed ("https://anything.evil.example/x.crl"));
    assertTrue (aList.isAllowed ("ldap://example.org/cn=foo"));
    assertTrue (aList.isAllowed (""));
    assertTrue (aList.isAllowed (null));
  }

  @Test
  public void testWithSinglePrefix ()
  {
    final CRLAllowList aList = new CRLAllowList ();
    aList.addAllowedPrefix ("https://crl.example.org/");
    assertFalse (aList.isEmpty ());
    assertEquals (1, aList.getPrefixCount ());

    assertTrue (aList.isAllowed ("https://crl.example.org/"));
    assertTrue (aList.isAllowed ("https://crl.example.org/foo.crl"));
    assertTrue (aList.isAllowed ("https://crl.example.org/path/to/some.crl?id=1"));

    // Different host
    assertFalse (aList.isAllowed ("https://evil.example.org/foo.crl"));
    // Different scheme
    assertFalse (aList.isAllowed ("http://crl.example.org/foo.crl"));
    // Subdomain confusion
    assertFalse (aList.isAllowed ("https://crl.example.org.evil.example/foo.crl"));
    // Empty / null
    assertFalse (aList.isAllowed (""));
    assertFalse (aList.isAllowed (null));
  }

  @Test
  public void testWithMultiplePrefixes ()
  {
    final CRLAllowList aList = new CRLAllowList ().addAllowedPrefix ("https://crl.example.org/")
                                                  .addAllowedPrefix ("http://crl.ca-vendor.com/");
    assertEquals (2, aList.getPrefixCount ());

    assertTrue (aList.isAllowed ("https://crl.example.org/foo.crl"));
    assertTrue (aList.isAllowed ("http://crl.ca-vendor.com/bar.crl"));
    assertFalse (aList.isAllowed ("https://other.example.org/foo.crl"));
  }

  @Test
  public void testCaseInsensitiveMatching ()
  {
    final CRLAllowList aList = new CRLAllowList ();
    aList.addAllowedPrefix ("https://CRL.Example.ORG/");

    // Same case
    assertTrue (aList.isAllowed ("https://CRL.Example.ORG/foo.crl"));
    // Different case in scheme
    assertTrue (aList.isAllowed ("HTTPS://crl.example.org/foo.crl"));
    // Different case in host
    assertTrue (aList.isAllowed ("https://crl.example.org/foo.crl"));
    // Mixed case
    assertTrue (aList.isAllowed ("HtTpS://CrL.ExAmPlE.OrG/foo.crl"));
    // Case-insensitive comparison only applies to the prefix portion - the path
    // following it is not constrained by the allow list at all
    assertTrue (aList.isAllowed ("https://crl.example.org/Path/MIXED.crl"));

    // Still rejected: different host
    assertFalse (aList.isAllowed ("https://evil.example.org/foo.crl"));
  }

  @Test
  public void testRemoveAll ()
  {
    final CRLAllowList aList = new CRLAllowList ();
    aList.addAllowedPrefix ("https://crl.example.org/");
    assertFalse (aList.isAllowed ("https://other.example.org/foo.crl"));

    aList.removeAllPrefixes ();
    assertTrue (aList.isEmpty ());
    // After clearing, we are back to "allow everything"
    assertTrue (aList.isAllowed ("https://other.example.org/foo.crl"));
  }

  @Test
  public void testGetAllAllowedPrefixesReturnsCopy ()
  {
    final CRLAllowList aList = new CRLAllowList ();
    aList.addAllowedPrefix ("https://crl.example.org/");
    aList.getAllAllowedPrefixes ().clear ();
    assertEquals (1, aList.getPrefixCount ());
  }

  @Test
  public void testInvalidPrefix ()
  {
    final CRLAllowList aList = new CRLAllowList ();
    try
    {
      aList.addAllowedPrefix (null);
      fail ("Expected NullPointerException for null prefix");
    }
    catch (final NullPointerException ex)
    {
      // expected
    }
    try
    {
      aList.addAllowedPrefix ("");
      fail ("Expected IllegalArgumentException for empty prefix");
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }
}
