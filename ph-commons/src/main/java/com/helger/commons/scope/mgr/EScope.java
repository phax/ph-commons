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
import javax.annotation.Nullable;

import com.helger.commons.scope.IScope;

/**
 * This enumeration defines all the possible non-web scopes including some
 * utility methods on it.
 *
 * @author Philip Helger
 */
public enum EScope
{
  /** The global scope. */
  GLOBAL,
  /** The application scope. */
  APPLICATION,
  /** The session scope */
  SESSION,
  /** The session application scope */
  SESSION_APPLICATION,
  /** The request scope. */
  REQUEST;

  @Nonnull
  public IScope getScope ()
  {
    return getScope (true);
  }

  @Nullable
  public IScope getScope (final boolean bCreateIfNotExisting)
  {
    return getScope (this, bCreateIfNotExisting);
  }

  /**
   * Resolve the currently matching scope of the given {@link EScope} value.
   *
   * @param eScope
   *        The scope to resolve to a real scope.
   * @param bCreateIfNotExisting
   *        if <code>false</code> and the scope is not existing,
   *        <code>null</code> will be returned. This parameter is only used in
   *        application scopes.
   * @return The matching {@link IScope} or <code>null</code> if
   *         bCreateIfNotExisting is <code>false</code> and no scope is present
   * @throws IllegalArgumentException
   *         If an illegal enumeration value is passed.
   */
  @Nullable
  public static IScope getScope (@Nonnull final EScope eScope, final boolean bCreateIfNotExisting)
  {
    switch (eScope)
    {
      case GLOBAL:
        return bCreateIfNotExisting ? ScopeManager.getGlobalScope () : ScopeManager.getGlobalScopeOrNull ();
      case APPLICATION:
        return ScopeManager.getApplicationScope (bCreateIfNotExisting);
      case SESSION:
        return ScopeManager.getSessionScope (bCreateIfNotExisting);
      case SESSION_APPLICATION:
        return ScopeManager.getSessionApplicationScope (bCreateIfNotExisting);
      case REQUEST:
        return bCreateIfNotExisting ? ScopeManager.getRequestScope () : ScopeManager.getRequestScopeOrNull ();
      default:
        throw new IllegalArgumentException ("Unknown scope: " + eScope);
    }
  }
}
