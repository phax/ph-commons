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
package com.helger.security.password.salt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Test class for class {@link PasswordSalt}.
 *
 * @author Philip Helger
 */
public final class PasswordSaltTest
{
  @Test
  public void testBasic ()
  {
    final PasswordSalt aSalt = new PasswordSalt ();
    assertEquals (PasswordSalt.DEFAULT_SALT_BYTES, aSalt.getSaltByteCount ());
    assertEquals (PasswordSalt.DEFAULT_SALT_BYTES, aSalt.getSaltBytes ().length);
    assertEquals (PasswordSalt.DEFAULT_SALT_BYTES * 2, aSalt.getSaltString ().length ());

    final PasswordSalt aSalt2 = new PasswordSalt (20);
    assertEquals (20, aSalt2.getSaltByteCount ());
    assertEquals (20, aSalt2.getSaltBytes ().length);
    assertEquals (20 * 2, aSalt2.getSaltString ().length ());
  }

  @Test
  public void testNotEquals ()
  {
    final PasswordSalt aSalt = new PasswordSalt ();
    for (int i = 0; i < 100; ++i)
      assertFalse (aSalt.equals (new PasswordSalt ()));
  }

  @Test
  public void testEqualsFromString ()
  {
    for (int i = 0; i < 100; ++i)
    {
      final PasswordSalt aSalt = new PasswordSalt ();
      final PasswordSalt aSalt2 = PasswordSalt.createFromStringMaybe (aSalt.getSaltString ());
      assertNotNull (aSalt2);
      assertEquals (aSalt, aSalt2);
    }
    assertNull (PasswordSalt.createFromStringMaybe (null));
    assertNull (PasswordSalt.createFromStringMaybe (""));
    try
    {
      PasswordSalt.createFromStringMaybe ("xy");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }
}
