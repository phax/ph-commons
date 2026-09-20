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

import java.time.LocalDateTime;

import org.junit.Test;

import com.helger.base.state.EChange;
import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link ExpirableWithReplacement}.
 *
 * @author Philip Helger
 */
public final class ExpirableWithReplacementTest
{
  private static final LocalDateTime DT = LocalDateTime.of (2026, 9, 20, 12, 30, 15);

  @Test
  public void testDefaultCtor ()
  {
    final ExpirableWithReplacement <String> aExp = new ExpirableWithReplacement <> ();
    assertNull (aExp.getExpirationDateTime ());
    assertNull (aExp.getReplacement ());
    assertFalse (aExp.isExpirationDefined ());
    assertFalse (aExp.isExpiredNow ());
    assertNotNull (aExp.toString ());
  }

  @Test
  public void testCtorWithValues ()
  {
    final ExpirableWithReplacement <String> aExp = new ExpirableWithReplacement <> (DT, "replacement");
    assertEquals (DT, aExp.getExpirationDateTime ());
    assertEquals ("replacement", aExp.getReplacement ());
    assertTrue (aExp.isExpirationDefined ());
    assertTrue (aExp.isExpiredAt (DT.plusSeconds (1)));
    assertFalse (aExp.isExpiredAt (DT));
  }

  @Test
  public void testSetters ()
  {
    final ExpirableWithReplacement <String> aExp = new ExpirableWithReplacement <> ();

    assertSame (EChange.UNCHANGED, aExp.setExpirationDateTime (null));
    assertSame (EChange.CHANGED, aExp.setExpirationDateTime (DT));
    assertSame (EChange.UNCHANGED, aExp.setExpirationDateTime (DT));
    assertSame (EChange.CHANGED, aExp.resetExpiration ());

    assertSame (EChange.UNCHANGED, aExp.setReplacement (null));
    assertSame (EChange.CHANGED, aExp.setReplacement ("replacement"));
    assertSame (EChange.UNCHANGED, aExp.setReplacement ("replacement"));
    assertEquals ("replacement", aExp.getReplacement ());
    assertSame (EChange.CHANGED, aExp.setReplacement (null));
    assertNull (aExp.getReplacement ());
  }

  @Test
  public void testEqualsHashcode ()
  {
    TestHelper.testDefaultImplementationWithEqualContentObject (new ExpirableWithReplacement <> (DT, "r"),
                                                                new ExpirableWithReplacement <> (DT, "r"));
    TestHelper.testDefaultImplementationWithEqualContentObject (new ExpirableWithReplacement <String> (),
                                                                new ExpirableWithReplacement <String> ());
    TestHelper.testDefaultImplementationWithDifferentContentObject (new ExpirableWithReplacement <> (DT, "r"),
                                                                    new ExpirableWithReplacement <> (DT, "other"));
    TestHelper.testDefaultImplementationWithDifferentContentObject (new ExpirableWithReplacement <> (DT, "r"),
                                                                    new ExpirableWithReplacement <> (DT.plusDays (1),
                                                                                                     "r"));
  }
}
