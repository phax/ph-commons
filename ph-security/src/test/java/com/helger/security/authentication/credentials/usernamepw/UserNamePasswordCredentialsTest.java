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
package com.helger.security.authentication.credentials.usernamepw;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link UserNamePasswordCredentials}.
 *
 * @author Philip Helger
 */
public final class UserNamePasswordCredentialsTest
{
  @Test
  public void testBasic ()
  {
    final UserNamePasswordCredentials aCred = new UserNamePasswordCredentials ("user", "pw");
    assertEquals ("user", aCred.getUserName ());
    assertEquals ("pw", aCred.getPassword ());
    // The password must not be part of the String representation
    assertNotNull (aCred.toString ());
    assertFalse (aCred.toString ().contains ("pw"));

    TestHelper.testDefaultImplementationWithEqualContentObject (aCred, new UserNamePasswordCredentials ("user", "pw"));
    TestHelper.testDefaultImplementationWithDifferentContentObject (aCred,
                                                                    new UserNamePasswordCredentials ("user2", "pw"));
    TestHelper.testDefaultImplementationWithDifferentContentObject (aCred,
                                                                    new UserNamePasswordCredentials ("user", "pw2"));
  }

  @Test
  public void testNullValues ()
  {
    final UserNamePasswordCredentials aCred = new UserNamePasswordCredentials (null, null);
    assertNull (aCred.getUserName ());
    assertNull (aCred.getPassword ());
    assertNotNull (aCred.toString ());

    TestHelper.testDefaultImplementationWithEqualContentObject (aCred, new UserNamePasswordCredentials (null, null));
    TestHelper.testDefaultImplementationWithDifferentContentObject (aCred,
                                                                    new UserNamePasswordCredentials ("user", null));
  }
}
