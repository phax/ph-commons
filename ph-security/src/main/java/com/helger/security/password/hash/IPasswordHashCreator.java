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

import java.io.Serializable;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;
import com.helger.security.password.salt.IPasswordSalt;

/**
 * Interface for a password hash creator.
 *
 * @author Philip Helger
 */
public interface IPasswordHashCreator extends Serializable
{
  /**
   * @return The name of the algorithm used in this creator. May neither be
   *         <code>null</code> nor empty.
   */
  @Nonnull
  @Nonempty
  String getAlgorithmName ();

  /**
   * @return <code>true</code> if this hash creator requires a salt,
   *         <code>false</code> if not.
   */
  boolean requiresSalt ();

  /**
   * The method to create a message digest hash from a password.
   *
   * @param aSalt
   *        Optional salt to be used. This parameter is only <code>null</code>
   *        for backwards compatibility reasons.
   * @param sPlainTextPassword
   *        Plain text password. May not be <code>null</code>.
   * @return The String representation of the password hash. Must be valid to
   *         encode in UTF-8.
   */
  @Nonnull
  String createPasswordHash (IPasswordSalt aSalt, @Nonnull String sPlainTextPassword);
}
