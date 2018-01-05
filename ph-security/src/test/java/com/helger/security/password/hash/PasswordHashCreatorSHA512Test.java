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

import org.junit.Test;

/**
 * Test class for class {@link PasswordHashCreatorSHA512}.
 *
 * @author Philip Helger
 */
public final class PasswordHashCreatorSHA512Test
{
  @Test
  public void testBasic ()
  {
    final String sPlainTextPassword = "123456";
    final PasswordHashCreatorSHA512 a = new PasswordHashCreatorSHA512 ();
    final String sPW1 = a.createPasswordHash (null, sPlainTextPassword);
    final String sPW2 = a.createPasswordHash (null, sPlainTextPassword);
    assertEquals (sPW1, sPW2);
  }
}
