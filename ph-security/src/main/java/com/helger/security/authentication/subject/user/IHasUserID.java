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
package com.helger.security.authentication.subject.user;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;

/**
 * Interface for objects having a user ID.
 *
 * @author Philip Helger
 */
public interface IHasUserID
{
  /**
   * @return The ID of the user. Must be present.
   */
  @Nonnull
  @Nonempty
  String getUserID ();

  /**
   * @return <code>true</code> if the user ID equals
   *         {@link com.helger.security.authentication.subject.user.CUserID#USER_ID_GUEST}
   */
  default boolean isAnonymousUser ()
  {
    return CUserID.USER_ID_GUEST.equals (getUserID ());
  }
}
