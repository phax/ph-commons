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

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.lang.ServiceLoaderHelper;
import com.helger.security.authentication.subject.IAuthSubject;

/**
 * This handler is used to resolve a subject from validated credentials. This is
 * necessary because the {@link IAuthCredentials} interface does not state
 * anything about the subject by default.
 *
 * @author Philip Helger
 */
@Immutable
public final class AuthCredentialToSubjectResolverManager
{
  private static volatile ICommonsList <IAuthCredentialToSubjectResolverSPI> s_aHdlList;

  static
  {
    s_aHdlList = ServiceLoaderHelper.getAllSPIImplementations (IAuthCredentialToSubjectResolverSPI.class);
    // list may be empty...
  }

  private AuthCredentialToSubjectResolverManager ()
  {}

  /**
   * @return A list of all contained implementations of
   *         {@link IAuthCredentialToSubjectResolverSPI}. Never
   *         <code>null</code> but maybe empty.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsList <IAuthCredentialToSubjectResolverSPI> getAllAuthCredentialToSubjectResolvers ()
  {
    return s_aHdlList.getClone ();
  }

  /**
   * @return The number of registered handlers. Always &ge; 0.
   */
  @Nonnegative
  public static int getAuthCredentialToSubjectResolverCount ()
  {
    return s_aHdlList.size ();
  }

  /**
   * Resolve the {@link IAuthSubject} from the specified credentials.
   *
   * @param aCredentials
   *        The credentials to be transformed.
   * @return <code>null</code> if no subject matches the specified credentials.
   */
  @Nullable
  public static IAuthSubject getSubjectFromCredentials (@Nonnull final IAuthCredentials aCredentials)
  {
    for (final IAuthCredentialToSubjectResolverSPI aHdl : s_aHdlList)
      if (aHdl.supportsCredentials (aCredentials))
      {
        final IAuthSubject aSubject = aHdl.getSubjectFromCredentials (aCredentials);
        if (aSubject != null)
          return aSubject;
      }
    return null;
  }
}
