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
package com.helger.security.authentication.result;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.security.authentication.credentials.CredentialValidationResult;
import com.helger.security.authentication.subject.AuthSubject;

/**
 * Test class for class {@link AuthIdentificationResult}.
 *
 * @author Philip Helger
 */
public final class AuthIdentificationResultTest
{
  @Test
  public void testCreateSuccess ()
  {
    final IAuthToken aToken = new AuthToken (new AuthIdentification (new AuthSubject ("air-id1", "Display Name")),
                                             IAuthToken.EXPIRATION_SECONDS_INFINITE);
    final AuthIdentificationResult aResult = AuthIdentificationResult.createSuccess (aToken);
    assertTrue (aResult.isSuccess ());
    assertFalse (aResult.isFailure ());
    assertSame (aToken, aResult.getAuthToken ());
    assertNull (aResult.getCredentialValidationFailure ());
    assertNotNull (aResult.toString ());
  }

  @Test
  public void testCreateFailure ()
  {
    final CredentialValidationResult aFailure = new CredentialValidationResult ("Wrong password");
    final AuthIdentificationResult aResult = AuthIdentificationResult.createFailure (aFailure);
    assertFalse (aResult.isSuccess ());
    assertTrue (aResult.isFailure ());
    assertNull (aResult.getAuthToken ());
    assertSame (aFailure, aResult.getCredentialValidationFailure ());
    assertNotNull (aResult.toString ());
  }

  @Test
  public void testInvalidParams ()
  {
    try
    {
      AuthIdentificationResult.createSuccess (null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      AuthIdentificationResult.createFailure (null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      // A successful credential validation is not a failure
      AuthIdentificationResult.createFailure (CredentialValidationResult.SUCCESS);
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
    }
  }
}
