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
package com.helger.commons.scope;

import java.util.Map;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;

/**
 * Interface for a single global scope object.
 *
 * @author Philip Helger
 */
public interface IGlobalScope extends IScope
{
  /**
   * Get or create an application scope with the given ID.
   *
   * @param sAppID
   *        The ID of the application scope to create. May neither be
   *        <code>null</code> nor empty.
   * @param bCreateIfNotExisting
   *        if <code>true</code> the scope is created on first demand if it is
   *        not yet existing.
   * @return <code>null</code> if bCreateIfNotExisting is <code>false</code> and
   *         the scope is not present
   */
  @Nullable
  IApplicationScope getApplicationScope (@Nonnull @Nonempty String sAppID, boolean bCreateIfNotExisting);

  /**
   * @return A non-<code>null</code> map with all available application scopes.
   *         The key is the application ID and the value is the scope.
   */
  @Nonnull
  Map <String, IApplicationScope> getAllApplicationScopes ();

  /**
   * @return The number of contained application scopes. Always &ge; 0.
   */
  @Nonnegative
  int getApplicationScopeCount ();
}
