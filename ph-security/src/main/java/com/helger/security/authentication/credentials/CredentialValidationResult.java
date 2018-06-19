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

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.string.ToStringGenerator;

/**
 * Default implementation of {@link ICredentialValidationResult}.
 *
 * @author Philip Helger
 */
public class CredentialValidationResult implements ICredentialValidationResult
{
  public static final CredentialValidationResult SUCCESS = new CredentialValidationResult ();

  private final String m_sErrorMsg;

  /**
   * Success only constructor.
   */
  protected CredentialValidationResult ()
  {
    m_sErrorMsg = null;
  }

  /**
   * Constructor with an error message
   *
   * @param sErrorMsg
   *        The error message. May neither be <code>null</code> nor empty.
   */
  public CredentialValidationResult (@Nonnull @Nonempty final String sErrorMsg)
  {
    m_sErrorMsg = ValueEnforcer.notEmpty (sErrorMsg, "ErrorMessage");
  }

  public boolean isSuccess ()
  {
    return m_sErrorMsg == null;
  }

  @Override
  public boolean isFailure ()
  {
    return m_sErrorMsg != null;
  }

  @Nullable
  public String getDisplayText (@Nonnull final Locale aDisplayLocale)
  {
    return m_sErrorMsg;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("ErrorMsg", m_sErrorMsg).getToString ();
  }
}
