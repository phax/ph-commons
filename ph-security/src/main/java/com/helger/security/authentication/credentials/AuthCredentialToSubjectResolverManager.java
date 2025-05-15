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

import com.helger.annotation.Nonnegative;
import com.helger.annotation.Nonnull;
import com.helger.annotation.Nullable;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.misc.ReturnsMutableCopy;
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
  private static final ICommonsList <IAuthCredentialToSubjectResolverSPI> HDL_LIST;

  static
  {
    HDL_LIST = ServiceLoaderHelper.getAllSPIImplementations (IAuthCredentialToSubjectResolverSPI.class);
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
    return HDL_LIST.getClone ();
  }

  /**
   * @return The number of registered handlers. Always &ge; 0.
   */
  @Nonnegative
  public static int getAuthCredentialToSubjectResolverCount ()
  {
    return HDL_LIST.size ();
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
    for (final IAuthCredentialToSubjectResolverSPI aHdl : HDL_LIST)
      if (aHdl.supportsCredentials (aCredentials))
      {
        final IAuthSubject aSubject = aHdl.getSubjectFromCredentials (aCredentials);
        if (aSubject != null)
          return aSubject;
      }
    return null;
  }
}
