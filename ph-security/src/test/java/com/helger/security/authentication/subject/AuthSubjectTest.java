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
package com.helger.security.authentication.subject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link AuthSubject}.
 *
 * @author Philip Helger
 */
public final class AuthSubjectTest
{
  @Test
  public void testBasic ()
  {
    final AuthSubject aSubject = new AuthSubject ("id1", "Display Name");
    assertEquals ("id1", aSubject.getID ());
    assertEquals ("Display Name", aSubject.getDisplayName ());
    assertNotNull (aSubject.toString ());

    TestHelper.testDefaultImplementationWithEqualContentObject (aSubject, new AuthSubject ("id1", "Display Name"));
    TestHelper.testDefaultImplementationWithDifferentContentObject (aSubject, new AuthSubject ("id2", "Display Name"));
    TestHelper.testDefaultImplementationWithDifferentContentObject (aSubject, new AuthSubject ("id1", "Other Name"));
  }

  @Test
  public void testInvalidParams ()
  {
    try
    {
      new AuthSubject (null, "Display Name");
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      new AuthSubject ("", "Display Name");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      new AuthSubject ("id1", null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      new AuthSubject ("id1", "");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }
}
