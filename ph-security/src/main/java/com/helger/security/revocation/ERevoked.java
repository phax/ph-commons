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
package com.helger.security.revocation;

import org.jspecify.annotations.NonNull;

/**
 * Small enum for setter method to identify whether an object is revoked or not.
 *
 * @author Philip Helger
 * @since 11.2.0
 */
public enum ERevoked implements IRevokedIndicator
{
  REVOKED,
  NOT_REVOKED,
  /**
   * The revocation status could not be determined - e.g. because a CRL could not be downloaded or
   * an OCSP responder was unreachable. This is treated as "not revoked" by {@link #isRevoked()} but
   * can be distinguished via {@link #isUnknown()}.
   *
   * @since 12.2.4
   */
  UNKNOWN;

  /**
   * {@inheritDoc}
   */
  public boolean isRevoked ()
  {
    return this == REVOKED;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isUnknown ()
  {
    return this == UNKNOWN;
  }

  /**
   * Convert a boolean value to an {@link ERevoked} instance.
   *
   * @param bEnabled
   *        <code>true</code> for {@link #REVOKED}, <code>false</code> for {@link #NOT_REVOKED}.
   * @return The matching enum value. Never <code>null</code>.
   */
  @NonNull
  public static ERevoked valueOf (final boolean bEnabled)
  {
    return bEnabled ? REVOKED : NOT_REVOKED;
  }

  /**
   * Convert a {@link IRevokedIndicator} to an {@link ERevoked} instance. Note that this conversion
   * only preserves the binary "revoked / not revoked" information - any {@link #UNKNOWN} state is
   * collapsed to {@link #NOT_REVOKED}.
   *
   * @param aEnabledIndicator
   *        The indicator to convert. May not be <code>null</code>.
   * @return The matching enum value. Never <code>null</code>.
   */
  @NonNull
  public static ERevoked valueOf (@NonNull final IRevokedIndicator aEnabledIndicator)
  {
    return valueOf (aEnabledIndicator.isRevoked ());
  }
}
