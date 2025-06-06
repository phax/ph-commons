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
package com.helger.security.authentication.credentials;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.IsSPIInterface;

/**
 * This SPI needs to be implemented by all implementing application to validate
 * the specified credentials.<br>
 * Note: each class implementing this file needs to register itself in a file
 * called
 * <code>/META-INF/services/com.helger.photon.basic.auth.credentials.IAuthCredentialValidatorSPI</code>
 *
 * @author Philip Helger
 */
@IsSPIInterface
public interface IAuthCredentialValidatorSPI
{
  /**
   * Check if this implementation supports the passed credentials.
   *
   * @param aCredentials
   *        The credentials to be validated. May not be <code>null</code>.
   * @return <code>true</code> if this implementation supports the specified
   *         credentials.
   */
  boolean supportsCredentials (@Nonnull IAuthCredentials aCredentials);

  /**
   * Validate the specified credentials. This method is only called if a
   * previous call to {@link #supportsCredentials(IAuthCredentials)} returned
   * <code>true</code> .
   *
   * @param aCredentials
   *        The credentials to be validated. Never <code>null</code>.
   * @return The credential validation result. May not be <code>null</code>.
   */
  @Nonnull
  ICredentialValidationResult validateCredentials (@Nonnull IAuthCredentials aCredentials);
}
