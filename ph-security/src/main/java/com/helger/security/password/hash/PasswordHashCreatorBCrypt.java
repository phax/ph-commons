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

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.security.bcrypt.BCrypt;
import com.helger.security.password.salt.IPasswordSalt;
import com.helger.security.password.salt.PasswordSaltBCrypt;

/**
 * The new default implementation of {@link IPasswordHashCreator} that requires
 * a salt and uses the BCrypt algorithm.
 *
 * @author Philip Helger
 */
public final class PasswordHashCreatorBCrypt extends AbstractPasswordHashCreator
{
  public static final String ALGORITHM = "BCrypt";

  public PasswordHashCreatorBCrypt ()
  {
    super (ALGORITHM);
  }

  public boolean requiresSalt ()
  {
    return true;
  }

  /**
   * {@inheritDoc} The password salt must be of type {@link PasswordSaltBCrypt}.
   */
  @Nonnull
  public String createPasswordHash (@Nonnull final IPasswordSalt aSalt, @Nonnull final String sPlainTextPassword)
  {
    ValueEnforcer.notNull (aSalt, "Salt");
    ValueEnforcer.isInstanceOf (aSalt, PasswordSaltBCrypt.class, "Salt");
    ValueEnforcer.notNull (sPlainTextPassword, "PlainTextPassword");

    return BCrypt.hashpw (sPlainTextPassword, aSalt.getSaltString ());
  }
}
