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
package com.helger.security.authentication.credentials;

import java.util.List;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.CodingStyleguideUnaware;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.exception.InitializationException;
import com.helger.base.spi.ServiceLoaderHelper;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;

/**
 * Manager class that loads all {@link IAuthCredentialValidatorSPI}
 * implementations via the SPI mechanism and provides methods to validate
 * authentication credentials.
 *
 * @author Philip Helger
 */
@Immutable
public final class AuthCredentialValidatorManager
{
  @CodingStyleguideUnaware
  private static final List <IAuthCredentialValidatorSPI> HDL_LIST;

  static
  {
    HDL_LIST = ServiceLoaderHelper.getAllSPIImplementations (IAuthCredentialValidatorSPI.class);
    if (HDL_LIST.isEmpty ())
      throw new InitializationException ("No class implementing " + IAuthCredentialValidatorSPI.class + " was found!");
  }

  private AuthCredentialValidatorManager ()
  {}

  /**
   * @return A mutable copy of all registered credential validator SPI implementations. Never
   *         <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static ICommonsList <IAuthCredentialValidatorSPI> getAllAuthCredentialValidators ()
  {
    return new CommonsArrayList <> (HDL_LIST);
  }

  /**
   * Validate the provided credentials against all registered credential validators.
   *
   * @param aCredentials
   *        The credentials to validate. May not be <code>null</code>.
   * @return The validation result. Never <code>null</code>. In case of success, the first
   *         successful result is returned. In case of failure, a combined result list is returned.
   */
  @NonNull
  public static ICredentialValidationResult validateCredentials (@NonNull final IAuthCredentials aCredentials)
  {
    ValueEnforcer.notNull (aCredentials, "Credentials");

    // Collect all strings of all supporting credential validators
    final ICommonsList <ICredentialValidationResult> aFailedValidations = new CommonsArrayList <> ();

    // Check all credential handlers if the can handle the passed credentials
    for (final IAuthCredentialValidatorSPI aHdl : HDL_LIST)
      if (aHdl.supportsCredentials (aCredentials))
      {
        final ICredentialValidationResult aResult = aHdl.validateCredentials (aCredentials);
        if (aResult == null)
          throw new IllegalStateException ("validateCredentials returned a null object from " +
                                           aHdl +
                                           " for credentials " +
                                           aCredentials);
        if (aResult.isSuccess ())
        {
          // This validator successfully validated the passed credentials
          return aResult;
        }
        aFailedValidations.add (aResult);
      }

    if (aFailedValidations.isEmpty ())
      aFailedValidations.add (new CredentialValidationResult ("No credential validator supported the provided credentials: " +
                                                              aCredentials));

    return new CredentialValidationResultList (aFailedValidations);
  }
}
