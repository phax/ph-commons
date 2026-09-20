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
package com.helger.text.codepoint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.function.IntPredicate;

import org.junit.Test;

/**
 * Test class for class {@link ECodepointProfile}.
 *
 * @author Philip Helger
 */
public final class ECodepointProfileTest
{
  /** Covers the BMP as well as a few supplementary codepoints */
  private static final int MAX_CODEPOINT = 0x11000;

  @Test
  public void testGetFilter ()
  {
    for (final ECodepointProfile e : ECodepointProfile.values ())
    {
      final IntPredicate aFilter = e.getFilter ();
      assertNotNull (aFilter);
      assertEquals (e.name (), e, ECodepointProfile.valueOf (e.name ()));
    }
  }

  @Test
  public void testCheckAllProfiles ()
  {
    // Invoke every profile filter with a wide range of codepoints, so that all
    // branches of the underlying CodepointHelper methods are hit
    for (final ECodepointProfile e : ECodepointProfile.values ())
      for (int nCodepoint = 0; nCodepoint < MAX_CODEPOINT; ++nCodepoint)
        assertEquals (Boolean.valueOf (e.getFilter ().test (nCodepoint)),
                      Boolean.valueOf (e.check (nCodepoint)));
  }

  @Test
  public void testNone ()
  {
    // NONE accepts everything
    for (int nCodepoint = 0; nCodepoint < 0x100; ++nCodepoint)
      assertTrue (ECodepointProfile.NONE.check (nCodepoint));
  }

  @Test
  public void testAlpha ()
  {
    // The filter returns true for everything that is *not* matching
    assertFalse (ECodepointProfile.ALPHA.check ('a'));
    assertFalse (ECodepointProfile.ALPHA.check ('Z'));
    assertTrue (ECodepointProfile.ALPHA.check ('1'));
    assertTrue (ECodepointProfile.ALPHA.check ('-'));
  }

  @Test
  public void testAlphaNum ()
  {
    assertFalse (ECodepointProfile.ALPHANUM.check ('a'));
    assertFalse (ECodepointProfile.ALPHANUM.check ('Z'));
    assertFalse (ECodepointProfile.ALPHANUM.check ('1'));
    assertTrue (ECodepointProfile.ALPHANUM.check ('-'));
  }

  @Test
  public void testScheme ()
  {
    assertFalse (ECodepointProfile.SCHEME.check ('h'));
    assertFalse (ECodepointProfile.SCHEME.check ('1'));
    assertFalse (ECodepointProfile.SCHEME.check ('+'));
    assertTrue (ECodepointProfile.SCHEME.check (' '));
  }

  @Test
  public void testUnreserved ()
  {
    assertFalse (ECodepointProfile.UNRESERVED.check ('a'));
    assertFalse (ECodepointProfile.UNRESERVED.check ('-'));
    assertFalse (ECodepointProfile.UNRESERVED.check ('~'));
    assertTrue (ECodepointProfile.UNRESERVED.check (' '));
  }

  @Test
  public void testReserved ()
  {
    assertFalse (ECodepointProfile.RESERVED.check ('/'));
    assertFalse (ECodepointProfile.RESERVED.check ('?'));
    assertTrue (ECodepointProfile.RESERVED.check ('a'));
  }
}
