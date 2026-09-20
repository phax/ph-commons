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
package com.helger.security.password.hash;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.base.string.StringHelper;
import com.helger.security.password.salt.IPasswordSalt;
import com.helger.security.password.salt.PasswordSalt;
import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link PasswordHash}.
 *
 * @author Philip Helger
 */
public final class PasswordHashTest
{
  @Test
  public void testWithSalt ()
  {
    final IPasswordSalt aSalt = PasswordSalt.createRandom ();
    final PasswordHash aPH = new PasswordHash ("algo", aSalt, "hashvalue");
    assertEquals ("algo", aPH.getAlgorithmName ());
    assertTrue (aPH.hasSalt ());
    assertSame (aSalt, aPH.getSalt ());
    assertEquals (aSalt.getSaltString (), aPH.getSaltAsString ());
    assertEquals ("hashvalue", aPH.getPasswordHashValue ());
    assertNotNull (aPH.toString ());

    TestHelper.testDefaultImplementationWithEqualContentObject (aPH, new PasswordHash ("algo", aSalt, "hashvalue"));
    TestHelper.testDefaultImplementationWithDifferentContentObject (aPH,
                                                                    new PasswordHash ("algo2", aSalt, "hashvalue"));
    TestHelper.testDefaultImplementationWithDifferentContentObject (aPH, new PasswordHash ("algo", null, "hashvalue"));
    TestHelper.testDefaultImplementationWithDifferentContentObject (aPH, new PasswordHash ("algo", aSalt, "other"));
  }

  @Test
  public void testWithoutSalt ()
  {
    final PasswordHash aPH = new PasswordHash ("algo", null, "hashvalue");
    assertFalse (aPH.hasSalt ());
    assertNull (aPH.getSalt ());
    assertNull (aPH.getSaltAsString ());
    assertNotNull (aPH.toString ());
  }

  @Test
  public void testInvalidParams ()
  {
    try
    {
      new PasswordHash (null, null, "hashvalue");
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      new PasswordHash ("", null, "hashvalue");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      // Algorithm name is too long
      new PasswordHash (StringHelper.getRepeated ('a', PasswordHash.ALGORITHM_NAME_MAX_LENGTH + 1), null, "hashvalue");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      new PasswordHash ("algo", null, "");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }
}
