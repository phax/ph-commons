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
package com.helger.security.authentication.credentials;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.IsSPIInterface;
import com.helger.security.authentication.subject.IAuthSubject;

/**
 * This interface needs to be implemented by all classes that are used for
 * resolving credentials to a certain subject.
 *
 * @author Philip Helger
 */
@IsSPIInterface
public interface IAuthCredentialToSubjectResolverSPI
{
  /**
   * Check if this class can handle the passed credential implementation.
   *
   * @param aCredentials
   *        The credentials to be validated. Never <code>null</code>.
   * @return <code>true</code> if this class can handle the given credentials,
   *         <code>false</code> otherwise.
   */
  boolean supportsCredentials (@Nonnull IAuthCredentials aCredentials);

  /**
   * This method is only called if
   * {@link #supportsCredentials(IAuthCredentials)} returned <code>true</code>.
   * It should try to resolve the credentials to an {@link IAuthSubject}. If
   * resolving fails, no exception should be thrown, but <code>null</code>
   * should be returned.
   *
   * @param aCredentials
   *        The credentials to be resolved to a subject. Never <code>null</code>
   *        .
   * @return <code>null</code> if the credentials could not be resolved, the
   *         subject otherwise.
   */
  @Nullable
  IAuthSubject getSubjectFromCredentials (@Nonnull IAuthCredentials aCredentials);
}
