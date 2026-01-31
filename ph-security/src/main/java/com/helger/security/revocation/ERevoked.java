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
  NOT_REVOKED;

  public boolean isRevoked ()
  {
    return this == REVOKED;
  }

  @NonNull
  public static ERevoked valueOf (final boolean bEnabled)
  {
    return bEnabled ? REVOKED : NOT_REVOKED;
  }

  @NonNull
  public static ERevoked valueOf (@NonNull final IRevokedIndicator aEnabledIndicator)
  {
    return valueOf (aEnabledIndicator.isRevoked ());
  }
}
