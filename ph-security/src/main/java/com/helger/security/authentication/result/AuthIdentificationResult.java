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

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.state.ISuccessIndicator;
import com.helger.commons.string.ToStringGenerator;
import com.helger.security.authentication.credentials.ICredentialValidationResult;

/**
 * This class contains the overall authentication result.
 *
 * @author Philip Helger
 */
public class AuthIdentificationResult implements ISuccessIndicator, Serializable
{
  private final IAuthToken m_aAuthToken;
  private final ICredentialValidationResult m_aCredentialValidationFailure;

  /**
   * Constructor.
   *
   * @param aAuthToken
   *        The auth token. May not be <code>null</code> in case of success.
   *        Must be <code>null</code> in case of failure.
   * @param aCredentialValidationFailure
   *        The validation failure. May not be <code>null</code> in case of
   *        failure. Must be <code>null</code> in case of success.
   */
  protected AuthIdentificationResult (@Nullable final IAuthToken aAuthToken,
                                      @Nullable final ICredentialValidationResult aCredentialValidationFailure)
  {
    ValueEnforcer.isFalse (aAuthToken == null && aCredentialValidationFailure == null, "One parameter must be set");
    ValueEnforcer.isFalse (aAuthToken != null && aCredentialValidationFailure != null, "Only one parameter may be set");
    if (aCredentialValidationFailure != null && aCredentialValidationFailure.isSuccess ())
      throw new IllegalStateException ("Don't call this method for successfuly credential validation!");
    m_aAuthToken = aAuthToken;
    m_aCredentialValidationFailure = aCredentialValidationFailure;
  }

  public boolean isSuccess ()
  {
    return m_aAuthToken != null;
  }

  @Override
  public boolean isFailure ()
  {
    return m_aCredentialValidationFailure != null;
  }

  /**
   * @return The auth token in case of successful identification or
   *         <code>null</code> in case of an error.
   */
  @Nullable
  public IAuthToken getAuthToken ()
  {
    return m_aAuthToken;
  }

  /**
   * @return The credential validation failure or <code>null</code> in case of
   *         success.
   */
  @Nullable
  public ICredentialValidationResult getCredentialValidationFailure ()
  {
    return m_aCredentialValidationFailure;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("AuthToken", m_aAuthToken)
                                       .append ("CredentialValidationFailure", m_aCredentialValidationFailure)
                                       .getToString ();
  }

  /**
   * Factory method for success authentication.
   *
   * @param aAuthToken
   *        The auth token. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static AuthIdentificationResult createSuccess (@Nonnull final IAuthToken aAuthToken)
  {
    ValueEnforcer.notNull (aAuthToken, "AuthToken");
    return new AuthIdentificationResult (aAuthToken, null);
  }

  /**
   * Factory method for error in authentication.
   *
   * @param aCredentialValidationFailure
   *        The validation failure. May not be <code>null</code> in case of
   *        failure!
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static AuthIdentificationResult createFailure (@Nonnull final ICredentialValidationResult aCredentialValidationFailure)
  {
    ValueEnforcer.notNull (aCredentialValidationFailure, "CredentialValidationFailure");
    return new AuthIdentificationResult (null, aCredentialValidationFailure);
  }
}
