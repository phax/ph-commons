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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.security.authentication.subject.AuthSubject;
import com.helger.security.authentication.subject.IAuthSubject;

/**
 * Test class for class {@link AuthToken}.
 *
 * @author Philip Helger
 */
public final class AuthTokenTest
{
  private static final IAuthSubject SUBJECT = new AuthSubject ("id1", "Display Name");

  @Test
  public void testInfiniteExpiration ()
  {
    final AuthIdentification aIdent = new AuthIdentification (SUBJECT);
    final AuthToken aToken = new AuthToken (aIdent, IAuthToken.EXPIRATION_SECONDS_INFINITE);

    assertNotNull (aToken.getID ());
    assertSame (aIdent, aToken.getIdentification ());
    assertNotNull (aToken.getCreationDate ());
    assertNotNull (aToken.getLastAccessDate ());
    assertEquals (IAuthToken.EXPIRATION_SECONDS_INFINITE, aToken.getExpirationSeconds ());
    assertFalse (aToken.isExpirationPossible ());
    assertNull (aToken.getExpirationDate ());
    assertFalse (aToken.isExpired ());
    assertNotNull (aToken.toString ());
  }

  @Test
  public void testWithExpiration ()
  {
    final AuthToken aToken = new AuthToken (new AuthIdentification (SUBJECT), 60);
    assertEquals (60, aToken.getExpirationSeconds ());
    assertTrue (aToken.isExpirationPossible ());
    assertNotNull (aToken.getExpirationDate ());
    assertFalse (aToken.isExpired ());
  }

  @Test
  public void testSetExpired ()
  {
    final AuthToken aToken = new AuthToken (new AuthIdentification (SUBJECT), 60);
    assertFalse (aToken.isExpired ());
    aToken.setExpired ();
    assertTrue (aToken.isExpired ());
  }

  @Test
  public void testUpdateLastAccess ()
  {
    final AuthToken aToken = new AuthToken (new AuthIdentification (SUBJECT), 60);
    final var aFirstAccess = aToken.getLastAccessDate ();
    aToken.updateLastAccess ();
    assertFalse (aToken.getLastAccessDate ().isBefore (aFirstAccess));
  }

  @Test
  public void testEqualsHashcode ()
  {
    final AuthToken aToken = new AuthToken (new AuthIdentification (SUBJECT), 60);
    assertEquals (aToken, aToken);
    assertEquals (aToken.hashCode (), aToken.hashCode ());
    assertFalse (aToken.equals (null));
    assertFalse (aToken.equals ("any other type"));
    // Every token has a unique ID
    assertFalse (aToken.equals (new AuthToken (new AuthIdentification (SUBJECT), 60)));
  }

  @Test
  public void testInvalidParams ()
  {
    try
    {
      new AuthToken (null, 60);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      new AuthToken (new AuthIdentification (SUBJECT), -1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }
}
