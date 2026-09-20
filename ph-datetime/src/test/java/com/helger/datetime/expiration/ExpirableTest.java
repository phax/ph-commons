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
package com.helger.datetime.expiration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.Test;

import com.helger.base.state.EChange;
import com.helger.datetime.helper.PDTFactory;
import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link Expirable}.
 *
 * @author Philip Helger
 */
public final class ExpirableTest
{
  private static final LocalDateTime DT = LocalDateTime.of (2026, 9, 20, 12, 30, 15);

  @Test
  public void testDefaultCtor ()
  {
    final Expirable aExp = new Expirable ();
    assertNull (aExp.getExpirationDateTime ());
    assertFalse (aExp.isExpirationDefined ());
    // Without an expiration date time nothing ever expires
    assertFalse (aExp.isExpiredNow ());
    assertFalse (aExp.isExpiredAt (DT));
    assertFalse (aExp.isExpiredIn (Duration.ofDays (365)));
    assertNotNull (aExp.toString ());
  }

  @Test
  public void testCtorWithExpiration ()
  {
    final Expirable aExp = new Expirable (DT);
    assertEquals (DT, aExp.getExpirationDateTime ());
    assertTrue (aExp.isExpirationDefined ());

    assertFalse (aExp.isExpiredAt (DT));
    assertFalse (aExp.isExpiredAt (DT.minusSeconds (1)));
    assertTrue (aExp.isExpiredAt (DT.plusSeconds (1)));
  }

  @Test
  public void testIsExpiredNowAndIn ()
  {
    // Expired a year ago
    final Expirable aPast = new Expirable (PDTFactory.getCurrentLocalDateTime ().minusYears (1));
    assertTrue (aPast.isExpiredNow ());
    assertTrue (aPast.isExpiredIn (Duration.ofSeconds (1)));

    // Expires in a year
    final Expirable aFuture = new Expirable (PDTFactory.getCurrentLocalDateTime ().plusYears (1));
    assertFalse (aFuture.isExpiredNow ());
    assertFalse (aFuture.isExpiredIn (Duration.ofDays (1)));
    assertTrue (aFuture.isExpiredIn (Duration.ofDays (400)));
  }

  @Test
  public void testSetAndResetExpiration ()
  {
    final Expirable aExp = new Expirable ();
    assertSame (EChange.UNCHANGED, aExp.setExpirationDateTime (null));
    assertSame (EChange.CHANGED, aExp.setExpirationDateTime (DT));
    assertSame (EChange.UNCHANGED, aExp.setExpirationDateTime (DT));
    assertEquals (DT, aExp.getExpirationDateTime ());

    assertSame (EChange.CHANGED, aExp.resetExpiration ());
    assertNull (aExp.getExpirationDateTime ());
    assertSame (EChange.UNCHANGED, aExp.resetExpiration ());
  }

  @Test
  public void testEqualsHashcode ()
  {
    TestHelper.testDefaultImplementationWithEqualContentObject (new Expirable (DT), new Expirable (DT));
    TestHelper.testDefaultImplementationWithEqualContentObject (new Expirable (), new Expirable ());
    TestHelper.testDefaultImplementationWithDifferentContentObject (new Expirable (DT), new Expirable ());
    TestHelper.testDefaultImplementationWithDifferentContentObject (new Expirable (DT),
                                                                    new Expirable (DT.plusDays (1)));
  }
}
