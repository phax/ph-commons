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
 * Simple interface for all objects that can be enabled and disabled.
 *
 * @author Philip Helger
 * @since 11.2.0
 */
public interface IRevokedIndicator
{
  /**
   * @return <code>true</code> if the object is currently revoked,
   *         <code>false</code> otherwise.
   */
  boolean isRevoked ();

  /**
   * @return <code>true</code> if the object is currently not revoked,
   *         <code>false</code> otherwise.
   */
  default boolean isNotRevoked ()
  {
    return !isRevoked ();
  }

  @NonNull
  default ERevoked or (@NonNull final IRevokedIndicator aEnabled)
  {
    return ERevoked.valueOf (isRevoked () || aEnabled.isRevoked ());
  }

  @NonNull
  default ERevoked and (@NonNull final IRevokedIndicator aEnabled)
  {
    return ERevoked.valueOf (isRevoked () && aEnabled.isRevoked ());
  }
}
