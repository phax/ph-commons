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
import com.helger.security.password.salt.PasswordSaltBCrypt;

/**
 * Test class for class {@link PasswordHashCreatorBCrypt}.
 *
 * @author Philip Helger
 */
public final class PasswordHashCreatorBCryptTest
{
  @Test
  public void testBasicSameSalt ()
  {
    final IPasswordSalt aSalt = new PasswordSaltBCrypt ();
    final String sPlainTextPassword = "123456";
    final PasswordHashCreatorBCrypt a = new PasswordHashCreatorBCrypt ();
    final String sPW1 = a.createPasswordHash (aSalt, sPlainTextPassword);
    final String sPW2 = a.createPasswordHash (aSalt, sPlainTextPassword);
    assertEquals (sPW1, sPW2);
  }

  @Test
  public void testBasicDifferentSalt ()
  {
    final String sPlainTextPassword = "123456";
    final PasswordHashCreatorBCrypt a = new PasswordHashCreatorBCrypt ();
    final String sPW1 = a.createPasswordHash (new PasswordSaltBCrypt (), sPlainTextPassword);
    final String sPW2 = a.createPasswordHash (new PasswordSaltBCrypt (), sPlainTextPassword);
    assertFalse (sPW1.equals (sPW2));
  }

  @Test
  public void testCreateMultipleHashes ()
  {
    final IPasswordSalt aSalt = new PasswordSaltBCrypt ();
    final String sPlainTextPassword = "123456";
    final PasswordHashCreatorBCrypt a = new PasswordHashCreatorBCrypt ();
    for (int i = 0; i < 20; ++i)
      a.createPasswordHash (aSalt, sPlainTextPassword);
  }
}
