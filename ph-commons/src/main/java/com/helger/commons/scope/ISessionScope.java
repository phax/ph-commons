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
import com.helger.commons.state.EContinue;

/**
 * Interface for a single session scope object.
 *
 * @author Philip Helger
 */
public interface ISessionScope extends IScope
{
  /**
   * A special internal method that destroys the session. This is especially
   * relevant for session web scope, because it is all done via the invalidation
   * of the underlying HTTP session.
   *
   * @return {@link EContinue#BREAK} to indicate that the regular destruction
   *         should not be performed!
   */
  @Nonnull
  EContinue selfDestruct ();

  /**
   * Create the unique ID, under which a session application scope will be
   * created within this scope. The default implementation is
   * <code>getID () + "." + sApplicationID</code>.
   *
   * @param sApplicationID
   *        The application ID to be used. May neither be <code>null</code> nor
   *        empty.
   * @return The application scope ID to be used.
   * @see #getApplicationIDFromApplicationScopeID(String) to get the application
   *      ID from an application scope ID (reverse operation)
   */
  @Nonnull
  @Nonempty
  String createApplicationScopeID (@Nonnull @Nonempty String sApplicationID);

  /**
   * Extract the application ID from an application scope ID.
   *
   * @param sApplicationScopeID
   *        The application scope ID to use. May be <code>null</code>.
   * @return <code>null</code> if no application ID could be extracted
   * @see #createApplicationScopeID(String) To creation an application scope ID
   *      from an application ID
   */
  @Nullable
  String getApplicationIDFromApplicationScopeID (@Nullable String sApplicationScopeID);

  /**
   * Create an application specific scope within the session.
   *
   * @param sApplicationID
   *        The application ID to use. May not be <code>null</code>.
   * @param bCreateIfNotExisting
   *        Create the session application scope if does not yet exist. If
   *        <code>false</code> and the scope does not exist than
   *        <code>null</code> is returned.
   * @return <code>null</code> if bCreateIfNotExisting is <code>false</code> and
   *         the scope is not present
   */
  @Nullable
  ISessionApplicationScope getSessionApplicationScope (@Nonnull @Nonempty String sApplicationID,
                                                       boolean bCreateIfNotExisting);

  /**
   * Restore a persisted session application scope
   *
   * @param sScopeID
   *        The ID of the restored application scope. May neither be
   *        <code>null</code> nor empty.
   * @param aScope
   *        The scope to be restored. May not be <code>null</code>.
   */
  void restoreSessionApplicationScope (@Nonnull @Nonempty String sScopeID, @Nonnull ISessionApplicationScope aScope);

  /**
   * @return A non-<code>null</code> map with all available session application
   *         scopes. The key is the application ID and the value is the scope.
   */
  @Nonnull
  Map <String, ISessionApplicationScope> getAllSessionApplicationScopes ();

  /**
   * @return The number of contained session application scopes. Always &ge; 0.
   */
  @Nonnegative
  int getSessionApplicationScopeCount ();
}
