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

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforcer.ValueEnforcer;
import com.helger.base.exception.InitializationException;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.lang.ServiceLoaderHelper;

import jakarta.annotation.Nonnull;

@Immutable
public final class AuthCredentialValidatorManager
{
  private static final ICommonsList <IAuthCredentialValidatorSPI> HDL_LIST;

  static
  {
    HDL_LIST = ServiceLoaderHelper.getAllSPIImplementations (IAuthCredentialValidatorSPI.class);
    if (HDL_LIST.isEmpty ())
      throw new InitializationException ("No class implementing " + IAuthCredentialValidatorSPI.class + " was found!");
  }

  private AuthCredentialValidatorManager ()
  {}

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsList <IAuthCredentialValidatorSPI> getAllAuthCredentialValidators ()
  {
    return HDL_LIST.getClone ();
  }

  @Nonnull
  public static ICredentialValidationResult validateCredentials (@Nonnull final IAuthCredentials aCredentials)
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
