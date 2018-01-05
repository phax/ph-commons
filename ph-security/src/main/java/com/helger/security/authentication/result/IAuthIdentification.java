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
import java.time.LocalDateTime;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.state.ISuccessIndicator;
import com.helger.security.authentication.subject.IAuthSubject;

/**
 * The interface representing the authentication result of an
 * {@link IAuthSubject} (e.g. user) at a given date and time.
 *
 * @author Philip Helger
 */
public interface IAuthIdentification extends ISuccessIndicator, Serializable
{
  /**
   * @return The identified subject. May be <code>null</code> if no subject
   *         matched the credentials.
   */
  @Nullable
  IAuthSubject getAuthSubject ();

  default boolean isSuccess ()
  {
    return getAuthSubject () != null;
  }

  default boolean hasAuthSubject (@Nullable final IAuthSubject aSubject)
  {
    return EqualsHelper.equals (getAuthSubject (), aSubject);
  }

  /**
   * Method to retrieve the time stamp of when this object was created.
   *
   * @return The date and time the identification occurred. Never
   *         <code>null</code>.
   */
  @Nonnull
  LocalDateTime getIdentificationDateTime ();
}
