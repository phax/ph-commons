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
package com.helger.security.revocation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test class for class {@link ERevoked}.
 *
 * @author Philip Helger
 */
public final class ERevokedTest
{
  @Test
  public void testRevoked ()
  {
    assertTrue (ERevoked.REVOKED.isRevoked ());
    assertFalse (ERevoked.REVOKED.isNotRevoked ());
    assertFalse (ERevoked.REVOKED.isUnknown ());
    assertTrue (ERevoked.REVOKED.isKnown ());
  }

  @Test
  public void testNotRevoked ()
  {
    assertFalse (ERevoked.NOT_REVOKED.isRevoked ());
    assertTrue (ERevoked.NOT_REVOKED.isNotRevoked ());
    assertFalse (ERevoked.NOT_REVOKED.isUnknown ());
    assertTrue (ERevoked.NOT_REVOKED.isKnown ());
  }

  @Test
  public void testUnknown ()
  {
    // UNKNOWN must not be reported as "revoked" - that's the whole point of having this state
    assertFalse (ERevoked.UNKNOWN.isRevoked ());
    assertTrue (ERevoked.UNKNOWN.isNotRevoked ());
    assertTrue (ERevoked.UNKNOWN.isUnknown ());
    assertFalse (ERevoked.UNKNOWN.isKnown ());
  }

  @Test
  public void testValueOfBoolean ()
  {
    assertSame (ERevoked.REVOKED, ERevoked.valueOf (true));
    assertSame (ERevoked.NOT_REVOKED, ERevoked.valueOf (false));
  }

  @Test
  public void testValueOfIndicator ()
  {
    assertSame (ERevoked.REVOKED, ERevoked.valueOf (ERevoked.REVOKED));
    assertSame (ERevoked.NOT_REVOKED, ERevoked.valueOf (ERevoked.NOT_REVOKED));
    // UNKNOWN.isRevoked() is false, so the binary conversion collapses to NOT_REVOKED
    assertSame (ERevoked.NOT_REVOKED, ERevoked.valueOf (ERevoked.UNKNOWN));
  }

  @Test
  public void testOrCombinator ()
  {
    assertSame (ERevoked.REVOKED, ERevoked.REVOKED.or (ERevoked.REVOKED));
    assertSame (ERevoked.REVOKED, ERevoked.REVOKED.or (ERevoked.NOT_REVOKED));
    assertSame (ERevoked.REVOKED, ERevoked.NOT_REVOKED.or (ERevoked.REVOKED));
    assertSame (ERevoked.NOT_REVOKED, ERevoked.NOT_REVOKED.or (ERevoked.NOT_REVOKED));
    // UNKNOWN behaves like NOT_REVOKED in the binary OR
    assertSame (ERevoked.NOT_REVOKED, ERevoked.UNKNOWN.or (ERevoked.NOT_REVOKED));
    assertSame (ERevoked.REVOKED, ERevoked.UNKNOWN.or (ERevoked.REVOKED));
  }

  @Test
  public void testAndCombinator ()
  {
    assertSame (ERevoked.REVOKED, ERevoked.REVOKED.and (ERevoked.REVOKED));
    assertSame (ERevoked.NOT_REVOKED, ERevoked.REVOKED.and (ERevoked.NOT_REVOKED));
    assertSame (ERevoked.NOT_REVOKED, ERevoked.NOT_REVOKED.and (ERevoked.REVOKED));
    assertSame (ERevoked.NOT_REVOKED, ERevoked.NOT_REVOKED.and (ERevoked.NOT_REVOKED));
    // UNKNOWN behaves like NOT_REVOKED in the binary AND
    assertSame (ERevoked.NOT_REVOKED, ERevoked.UNKNOWN.and (ERevoked.REVOKED));
  }
}
