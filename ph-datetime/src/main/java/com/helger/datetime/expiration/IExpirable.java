/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.datetime.expiration;

import java.time.Duration;
import java.time.LocalDateTime;

import com.helger.datetime.helper.PDTFactory;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Read-only interface for objects that can expire.
 *
 * @author Philip Helger
 */
public interface IExpirable
{
  /**
   * @return The date time when the object will expire/expired. May be
   *         <code>null</code> if no expiration is defined.
   */
  @Nullable
  LocalDateTime getExpirationDateTime ();

  /**
   * Check if the object has an expiration date defined. To check if the object
   * is already expired, use {@link #isExpiredNow()} instead.
   *
   * @return <code>true</code> if an expiration date is defined,
   *         <code>false</code> otherwise.
   */
  default boolean isExpirationDefined ()
  {
    return getExpirationDateTime () != null;
  }

  /**
   * Check if this object is already expired. This is only possible if an
   * expiration date is defined.
   *
   * @return <code>true</code> if an expiration date is defined and the
   *         expiration date is in the past, <code>false</code> otherwise.
   * @see #isExpirationDefined()
   */
  default boolean isExpiredNow ()
  {
    return isExpiredAt (PDTFactory.getCurrentLocalDateTime ());
  }

  /**
   * Check if this object is expired at the provided date time. This is only
   * possible if an expiration date is defined.
   *
   * @param aDT
   *        The date time to check against. May not be <code>null</code>.
   * @return <code>true</code> if an expiration date is defined and the
   *         expiration date is in the past, <code>false</code> otherwise.
   * @see #isExpirationDefined()
   */
  default boolean isExpiredAt (@Nonnull final LocalDateTime aDT)
  {
    return isExpirationDefined () && aDT.isAfter (getExpirationDateTime ());
  }

  /**
   * Check if this object is expired within now plus the provided duration. This
   * is only possible if an expiration date is defined.
   *
   * @param aDuration
   *        The duration to check against. May not be <code>null</code>.
   * @return <code>true</code> if an expiration date is defined and the
   *         expiration date is within now plus the provided duration,
   *         <code>false</code> otherwise.
   * @see #isExpirationDefined()
   * @see #isExpiredAt(LocalDateTime)
   * @since 11.2.0
   */
  default boolean isExpiredIn (@Nonnull final Duration aDuration)
  {
    return isExpiredAt (PDTFactory.getCurrentLocalDateTime ().plus (aDuration));
  }
}
