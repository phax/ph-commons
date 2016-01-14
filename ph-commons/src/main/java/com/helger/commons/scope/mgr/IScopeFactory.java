/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.scope.mgr;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.scope.IApplicationScope;
import com.helger.commons.scope.IGlobalScope;
import com.helger.commons.scope.IRequestScope;
import com.helger.commons.scope.ISessionApplicationScope;
import com.helger.commons.scope.ISessionScope;

/**
 * Interface for a non-web scope factory.
 *
 * @author Philip Helger
 */
public interface IScopeFactory
{
  /**
   * Create a new global scope.
   *
   * @param sScopeID
   *        The scope ID to use. May neither be <code>null</code> nor empty.
   * @return Never <code>null</code>.
   */
  @Nonnull
  IGlobalScope createGlobalScope (@Nonnull @Nonempty String sScopeID);

  /**
   * Create a new application scope
   *
   * @param sScopeID
   *        The scope ID to use
   * @return Never <code>null</code>.
   */
  @Nonnull
  IApplicationScope createApplicationScope (@Nonnull @Nonempty String sScopeID);

  /**
   * Create a new session scope
   *
   * @param sScopeID
   *        The scope ID to use
   * @return Never <code>null</code>.
   */
  @Nonnull
  ISessionScope createSessionScope (@Nonnull @Nonempty String sScopeID);

  /**
   * Create a new session application scope
   *
   * @param sScopeID
   *        The scope ID to use
   * @return Never <code>null</code>.
   */
  @Nonnull
  ISessionApplicationScope createSessionApplicationScope (@Nonnull @Nonempty String sScopeID);

  /**
   * Create a new request scope
   *
   * @param sScopeID
   *        The scope ID to use. May neither be <code>null</code> nor empty.
   * @param sSessionID
   *        The session scope ID to use. May be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nonnull
  IRequestScope createRequestScope (@Nonnull @Nonempty String sScopeID, @Nonnull @Nonempty String sSessionID);
}
