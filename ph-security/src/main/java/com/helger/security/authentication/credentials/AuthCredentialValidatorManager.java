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
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.exception.InitializationException;
import com.helger.commons.lang.ServiceLoaderHelper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Immutable
public final class AuthCredentialValidatorManager
{
  private static ICommonsList <IAuthCredentialValidatorSPI> s_aHdlList;

  static
  {
    s_aHdlList = ServiceLoaderHelper.getAllSPIImplementations (IAuthCredentialValidatorSPI.class);
    if (s_aHdlList.isEmpty ())
      throw new InitializationException ("No class implementing " + IAuthCredentialValidatorSPI.class + " was found!");
  }

  private AuthCredentialValidatorManager ()
  {}

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsList <IAuthCredentialValidatorSPI> getAllAuthCredentialValidators ()
  {
    return s_aHdlList.getClone ();
  }

  @Nonnull
  @SuppressFBWarnings ("RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE")
  public static ICredentialValidationResult validateCredentials (@Nonnull final IAuthCredentials aCredentials)
  {
    ValueEnforcer.notNull (aCredentials, "Credentials");

    // Collect all strings of all supporting credential validators
    final ICommonsList <ICredentialValidationResult> aFailedValidations = new CommonsArrayList <> ();

    // Check all credential handlers if the can handle the passed credentials
    for (final IAuthCredentialValidatorSPI aHdl : s_aHdlList)
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
