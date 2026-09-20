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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.base.state.ESuccess;
import com.helger.collection.commons.ICommonsList;
import com.helger.security.authentication.subject.AuthSubject;
import com.helger.security.authentication.subject.IAuthSubject;

/**
 * Test class for class {@link AuthTokenRegistry}.
 *
 * @author Philip Helger
 */
public final class AuthTokenRegistryTest
{
  @Test
  public void testCreateAndRemoveToken ()
  {
    final IAuthSubject aSubject = new AuthSubject ("reg-id1", "Display Name");
    final IAuthToken aToken = AuthTokenRegistry.createToken (new AuthIdentification (aSubject),
                                                             IAuthToken.EXPIRATION_SECONDS_INFINITE);
    assertNotNull (aToken);
    final String sTokenID = aToken.getID ();

    assertSame (aToken, AuthTokenRegistry.getValidToken (sTokenID));
    assertSame (aToken, AuthTokenRegistry.validateTokenAndUpdateLastAccess (sTokenID));

    assertSame (ESuccess.SUCCESS, AuthTokenRegistry.removeToken (sTokenID));
    assertNull (AuthTokenRegistry.getValidToken (sTokenID));
    assertNull (AuthTokenRegistry.validateTokenAndUpdateLastAccess (sTokenID));

    // Second removal fails
    assertSame (ESuccess.FAILURE, AuthTokenRegistry.removeToken (sTokenID));
  }

  @Test
  public void testGetValidTokenInvalidIDs ()
  {
    assertNull (AuthTokenRegistry.getValidToken (null));
    assertNull (AuthTokenRegistry.getValidToken (""));
    assertNull (AuthTokenRegistry.getValidToken ("does-not-exist"));
    assertNull (AuthTokenRegistry.validateTokenAndUpdateLastAccess (null));
  }

  @Test
  public void testTokensOfSubject ()
  {
    final IAuthSubject aSubject = new AuthSubject ("reg-id2", "Display Name");
    final IAuthSubject aOtherSubject = new AuthSubject ("reg-id3", "Display Name");

    assertTrue (AuthTokenRegistry.getAllTokensOfSubject (aSubject).isEmpty ());

    AuthTokenRegistry.createToken (new AuthIdentification (aSubject), IAuthToken.EXPIRATION_SECONDS_INFINITE);
    AuthTokenRegistry.createToken (new AuthIdentification (aSubject), IAuthToken.EXPIRATION_SECONDS_INFINITE);
    AuthTokenRegistry.createToken (new AuthIdentification (aOtherSubject), IAuthToken.EXPIRATION_SECONDS_INFINITE);

    final ICommonsList <IAuthToken> aTokens = AuthTokenRegistry.getAllTokensOfSubject (aSubject);
    assertEquals (2, aTokens.size ());

    assertEquals (2, AuthTokenRegistry.removeAllTokensOfSubject (aSubject));
    assertTrue (AuthTokenRegistry.getAllTokensOfSubject (aSubject).isEmpty ());

    // The other subject is untouched
    assertEquals (1, AuthTokenRegistry.removeAllTokensOfSubject (aOtherSubject));
  }

  @Test
  public void testExpiredTokenIsNotValid ()
  {
    final IAuthSubject aSubject = new AuthSubject ("reg-id4", "Display Name");
    final IAuthToken aToken = AuthTokenRegistry.createToken (new AuthIdentification (aSubject), 60);
    final String sTokenID = aToken.getID ();
    assertNotNull (AuthTokenRegistry.getValidToken (sTokenID));

    // Removing marks the token as expired
    assertSame (ESuccess.SUCCESS, AuthTokenRegistry.removeToken (sTokenID));
    assertTrue (aToken.isExpired ());
  }

  @Test
  public void testInvalidParams ()
  {
    try
    {
      AuthTokenRegistry.getAllTokensOfSubject (null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      AuthTokenRegistry.removeAllTokensOfSubject (null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }
}
