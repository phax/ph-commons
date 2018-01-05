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

import java.io.Serializable;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;

/**
 * Base interface for a password salt.
 *
 * @author Philip Helger
 */
@MustImplementEqualsAndHashcode
public interface IPasswordSalt extends Serializable
{
  /**
   * @return The number of salt bytes used. Always &gt; 0.
   */
  int getSaltByteCount ();

  /**
   * @return A copy of the pure salt bytes. Never <code>null</code> nor empty.
   */
  @Nonnull
  @Nonempty
  @ReturnsMutableCopy
  byte [] getSaltBytes ();

  /**
   * @return The salt bytes in a string representation for easy serialization.
   *         Never <code>null</code> nor empty.
   */
  @Nonnull
  @Nonempty
  String getSaltString ();
}
