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
package com.helger.http.bearerauth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Test class for class {@link HttpBearerAuth}.
 *
 * @author Philip Helger
 */
public final class HttpBearerAuthTest
{
  @Test
  public void testBasic ()
  {
    final BearerAuthClientCredentials aCredentials = new BearerAuthClientCredentials ("mF_9.B5f-4.1JqM");
    final String sValue = aCredentials.getRequestValue ();
    assertEquals ("Bearer mF_9.B5f-4.1JqM", sValue);
    final BearerAuthClientCredentials aDecoded = HttpBearerAuth.getBearerAuthClientCredentials (sValue);
    assertNotNull (aDecoded);
    assertEquals (aCredentials, aDecoded);

    // Scheme is case-insensitive
    assertEquals (aCredentials, HttpBearerAuth.getBearerAuthClientCredentials ("bearer mF_9.B5f-4.1JqM"));
    assertEquals (aCredentials, HttpBearerAuth.getBearerAuthClientCredentials ("  BEARER   mF_9.B5f-4.1JqM  "));

    // Token must not be revealed
    assertFalse (aCredentials.toString ().contains ("mF_9"));
  }

  @Test
  public void testInvalid ()
  {
    assertNull (HttpBearerAuth.getBearerAuthClientCredentials (null));
    assertNull (HttpBearerAuth.getBearerAuthClientCredentials (""));
    assertNull (HttpBearerAuth.getBearerAuthClientCredentials ("Bearer"));
    assertNull (HttpBearerAuth.getBearerAuthClientCredentials ("Basic abc"));
    assertNull (HttpBearerAuth.getBearerAuthClientCredentials ("Bearer abc def"));

    assertTrue (HttpBearerAuth.isValidToken ("abc"));
    assertFalse (HttpBearerAuth.isValidToken (null));
    assertFalse (HttpBearerAuth.isValidToken (""));
    assertFalse (HttpBearerAuth.isValidToken ("a b"));
    assertFalse (HttpBearerAuth.isValidToken ("a\r\nb"));

    try
    {
      new BearerAuthClientCredentials ("a\nb");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }
}
