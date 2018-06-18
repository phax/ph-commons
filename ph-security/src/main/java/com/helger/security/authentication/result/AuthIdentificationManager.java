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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.security.authentication.credentials.AuthCredentialToSubjectResolverManager;
import com.helger.security.authentication.credentials.AuthCredentialValidatorManager;
import com.helger.security.authentication.credentials.IAuthCredentials;
import com.helger.security.authentication.credentials.ICredentialValidationResult;
import com.helger.security.authentication.subject.IAuthSubject;

/**
 * This is the main class for creating an {@link IAuthToken} from
 * {@link IAuthCredentials}.
 *
 * @author Philip Helger
 */
@Immutable
public final class AuthIdentificationManager
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (AuthIdentificationManager.class);

  private AuthIdentificationManager ()
  {}

  /**
   * Validate the login credentials, try to resolve the subject and create a
   * token upon success.
   *
   * @param aCredentials
   *        The credentials to validate. If <code>null</code> it is treated as
   *        error.
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static AuthIdentificationResult validateLoginCredentialsAndCreateToken (@Nonnull final IAuthCredentials aCredentials)
  {
    ValueEnforcer.notNull (aCredentials, "Credentials");

    // validate credentials
    final ICredentialValidationResult aValidationResult = AuthCredentialValidatorManager.validateCredentials (aCredentials);
    if (aValidationResult.isFailure ())
    {
      if (s_aLogger.isWarnEnabled ())
        s_aLogger.warn ("Credentials have been rejected: " + aCredentials);
      return AuthIdentificationResult.createFailure (aValidationResult);
    }

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("Credentials have been accepted: " + aCredentials);

    // try to get AuthSubject from passed credentials
    final IAuthSubject aSubject = AuthCredentialToSubjectResolverManager.getSubjectFromCredentials (aCredentials);
    if (aSubject != null)
    {
      if (s_aLogger.isDebugEnabled ())
        s_aLogger.debug ("Credentials " + aCredentials + " correspond to subject " + aSubject);
    }
    else
    {
      if (s_aLogger.isErrorEnabled ())
        s_aLogger.error ("Failed to resolve credentials " + aCredentials + " to an auth subject!");
    }

    // Create the identification element
    final AuthIdentification aIdentification = new AuthIdentification (aSubject);

    // create the token (without expiration seconds)
    final IAuthToken aNewAuthToken = AuthTokenRegistry.createToken (aIdentification,
                                                                    IAuthToken.EXPIRATION_SECONDS_INFINITE);
    return AuthIdentificationResult.createSuccess (aNewAuthToken);
  }
}
