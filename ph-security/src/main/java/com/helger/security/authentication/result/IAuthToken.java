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
package com.helger.security.authentication.result;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.id.IHasID;

/**
 * Interface for an auth token.
 *
 * @author Philip Helger
 */
public interface IAuthToken extends IHasID <String>, Serializable
{
  /** The value indicating, that a token never expires */
  int EXPIRATION_SECONDS_INFINITE = 0;

  /**
   * @return The secret key token representing a session of a subject. Never
   *         <code>null</code>.
   */
  @Nonnull
  String getID ();

  /**
   * @return The underlying identification object. Never <code>null</code>.
   */
  @Nonnull
  IAuthIdentification getIdentification ();

  /**
   * @return The date and time when the token was created. Never
   *         <code>null</code>.
   */
  @Nonnull
  LocalDateTime getCreationDate ();

  /**
   * @return The date and time when the token was last accessed. If the token
   *         was never accessed before, the creation date time is returned.
   *         Never <code>null</code>.
   */
  @Nonnull
  LocalDateTime getLastAccessDate ();

  /**
   * @return The expiration seconds. Always &ge; 0. A value of
   *         {@value #EXPIRATION_SECONDS_INFINITE} means no expiration.
   * @see #isExpirationPossible()
   */
  @Nonnegative
  int getExpirationSeconds ();

  /**
   * Check if this token can expire (expiration seconds &gt; 0) or not
   * (expiration seconds = {@value #EXPIRATION_SECONDS_INFINITE}).
   *
   * @return <code>true</code> if this token can expire, <code>false</code>
   *         otherwise.
   * @see #getExpirationSeconds()
   */
  boolean isExpirationPossible ();

  /**
   * Get the date time when this token will expire. This date time changes every
   * time the last access is updated.
   *
   * @return The expiration date and time or <code>null</code> if this token
   *         cannot expire.
   * @see #isExpirationPossible()
   */
  @Nullable
  LocalDateTime getExpirationDate ();

  /**
   * Check if the token is expired. Expired tokens are considered invalid.
   *
   * @return <code>true</code> if the token is already expired,
   *         <code>false</code> if the token is still valid.
   */
  boolean isExpired ();
}
