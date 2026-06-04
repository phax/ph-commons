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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.Test;

/**
 * Test class for class {@link ExpiringObject}.
 *
 * @author Philip Helger
 */
public final class ExpiringObjectTest
{
  @Test
  public void testSimple ()
  {
    final ExpiringObject <String> aObj = ExpiringObject.ofDuration ("Hello", Duration.ofMillis (100));
    assertNotNull (aObj);
    assertFalse (aObj.isExpiredNow ());
    assertTrue (aObj.isExpiredIn (Duration.ofMillis (100)));
    assertTrue (aObj.isExpiredIn (Duration.ofSeconds (1)));
  }

  @Test
  public void testNullExpirationDateTime ()
  {
    // Since 12.3.0: null expiration is allowed and means "never expires"
    final ExpiringObject <String> aObj = new ExpiringObject <> ("Hello", (LocalDateTime) null);
    assertNotNull (aObj);
    assertNull (aObj.getExpirationDateTime ());
    assertFalse (aObj.isExpirationDefined ());
    assertFalse (aObj.isExpiredNow ());
    assertFalse (aObj.isExpiredIn (Duration.ofDays (365)));
  }
}
