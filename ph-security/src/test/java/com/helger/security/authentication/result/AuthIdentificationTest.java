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

import org.junit.Test;

import com.helger.security.authentication.subject.AuthSubject;
import com.helger.security.authentication.subject.IAuthSubject;

/**
 * Test class for class {@link AuthIdentification}.
 *
 * @author Philip Helger
 */
public final class AuthIdentificationTest
{
  private static final IAuthSubject SUBJECT = new AuthSubject ("id1", "Display Name");

  @Test
  public void testBasic ()
  {
    final AuthIdentification aIdent = new AuthIdentification (SUBJECT);
    assertSame (SUBJECT, aIdent.getAuthSubject ());
    assertNotNull (aIdent.getIdentificationDateTime ());
    assertNotNull (aIdent.toString ());

    assertTrue (aIdent.hasAuthSubject (SUBJECT));
    assertFalse (aIdent.hasAuthSubject (new AuthSubject ("id2", "Other Name")));
    assertFalse (aIdent.hasAuthSubject (null));
  }

  @Test
  public void testNullSubject ()
  {
    final AuthIdentification aIdent = new AuthIdentification (null);
    assertNull (aIdent.getAuthSubject ());
    assertNotNull (aIdent.getIdentificationDateTime ());
    assertNotNull (aIdent.toString ());
    assertFalse (aIdent.hasAuthSubject (SUBJECT));
  }

  @Test
  public void testEqualsHashcode ()
  {
    // The identification date time is created in the constructor, so two
    // instances are never equal
    final AuthIdentification aIdent = new AuthIdentification (SUBJECT);
    assertEquals (aIdent, aIdent);
    assertEquals (aIdent.hashCode (), aIdent.hashCode ());
    assertFalse (aIdent.equals (null));
    assertFalse (aIdent.equals ("any other type"));
    assertFalse (aIdent.equals (new AuthIdentification (null)));
  }
}
