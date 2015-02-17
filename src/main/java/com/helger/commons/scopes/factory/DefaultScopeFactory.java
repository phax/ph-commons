/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.scopes.factory;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.annotations.Nonempty;
import com.helger.commons.scopes.domain.IApplicationScope;
import com.helger.commons.scopes.domain.IGlobalScope;
import com.helger.commons.scopes.domain.IRequestScope;
import com.helger.commons.scopes.domain.ISessionApplicationScope;
import com.helger.commons.scopes.domain.ISessionScope;
import com.helger.commons.scopes.impl.ApplicationScope;
import com.helger.commons.scopes.impl.GlobalScope;
import com.helger.commons.scopes.impl.RequestScope;
import com.helger.commons.scopes.impl.SessionApplicationScope;
import com.helger.commons.scopes.impl.SessionScope;

/**
 * Standalone version of the scope factory. No dependencies to Web components.
 * 
 * @author Philip Helger
 */
@NotThreadSafe
public class DefaultScopeFactory implements IScopeFactory
{
  public DefaultScopeFactory ()
  {}

  @Nonnull
  public IGlobalScope createGlobalScope (@Nonnull @Nonempty final String sScopeID)
  {
    return new GlobalScope (sScopeID);
  }

  @Nonnull
  public IApplicationScope createApplicationScope (@Nonnull @Nonempty final String sScopeID)
  {
    return new ApplicationScope (sScopeID);
  }

  @Nonnull
  public ISessionScope createSessionScope (@Nonnull @Nonempty final String sScopeID)
  {
    return new SessionScope (sScopeID);
  }

  @Nonnull
  public ISessionApplicationScope createSessionApplicationScope (@Nonnull @Nonempty final String sScopeID)
  {
    return new SessionApplicationScope (sScopeID);
  }

  @Nonnull
  public IRequestScope createRequestScope (@Nonnull @Nonempty final String sScopeID,
                                           @Nonnull @Nonempty final String sSessionID)
  {
    return new RequestScope (sScopeID, sSessionID);
  }
}
