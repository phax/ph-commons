/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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

import org.junit.Test;

import com.helger.security.password.salt.IPasswordSalt;
import com.helger.security.password.salt.PasswordSalt;

/**
 * Test class for class {@link PasswordHashCreatorPBKDF2_1000_48}.
 *
 * @author Philip Helger
 */
public final class PasswordHashCreatorPBKDF2_1000_48Test
{
  @Test
  public void testBasicSameSalt ()
  {
    final IPasswordSalt aSalt = new PasswordSalt ();
    final String sPlainTextPassword = "123456";
    final PasswordHashCreatorPBKDF2_1000_48 a = new PasswordHashCreatorPBKDF2_1000_48 ();
    final String sPW1 = a.createPasswordHash (aSalt, sPlainTextPassword);
    final String sPW2 = a.createPasswordHash (aSalt, sPlainTextPassword);
    assertEquals (sPW1, sPW2);
  }

  @Test
  public void testBasicDifferentSalt ()
  {
    final String sPlainTextPassword = "123456";
    final PasswordHashCreatorPBKDF2_1000_48 a = new PasswordHashCreatorPBKDF2_1000_48 ();
    final String sPW1 = a.createPasswordHash (new PasswordSalt (), sPlainTextPassword);
    final String sPW2 = a.createPasswordHash (new PasswordSalt (), sPlainTextPassword);
    assertFalse (sPW1.equals (sPW2));
  }

  @Test
  public void testCreateMultipleHashes ()
  {
    final IPasswordSalt aSalt = new PasswordSalt ();
    final String sPlainTextPassword = "123456";
    final PasswordHashCreatorPBKDF2_1000_48 a = new PasswordHashCreatorPBKDF2_1000_48 ();
    for (int i = 0; i < 1000; ++i)
      a.createPasswordHash (aSalt, sPlainTextPassword);
  }
}
