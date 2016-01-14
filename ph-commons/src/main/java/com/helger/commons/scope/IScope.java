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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.callback.INonThrowingCallableWithParameter;
import com.helger.commons.callback.INonThrowingRunnableWithParameter;
import com.helger.commons.collection.attr.IMutableAttributeContainerAny;
import com.helger.commons.id.IHasID;

/**
 * This interface is used for all the common stuff of a scope. The following
 * types of scopes are present:
 * <ul>
 * <li>Global scope - once and only once</li>
 * <li>Application context - scope for an application (e.g. public and secure
 * application in one web application)</li>
 * <li>Session scope - for each user created session</li>
 * <li>Session application context - scope for an application within a session
 * </li>
 * <li>Request scope - for each user request</li>
 * </ul>
 * IMPORTANT: implementations of {@link IScope} must be thread safe!
 *
 * @author Philip Helger
 */
public interface IScope extends IMutableAttributeContainerAny <String>, IHasID <String>
{
  /**
   * Init the scope. In contrast to the constructor of a scope, this happens
   * after the scope has been registered in the scope manager.
   */
  void initScope ();

  /**
   * Get the ID of this scope. Each scope retrieves a unique ID within its type
   * of scope (request, session, application). This method needs to be callable
   * anytime and should not throw any exception!
   *
   * @return the non-null ID of this context.
   */
  String getID ();

  /**
   * @return <code>true</code> if this scope is neither in destruction nor
   *         destroyed.
   * @see #isInDestruction()
   * @see #isDestroyed()
   */
  boolean isValid ();

  /**
   * @return <code>true</code> if the scope is currently in the process of
   *         destruction.
   */
  boolean isInDestruction ();

  /**
   * @return <code>true</code> if the scope was already destroyed. This is
   *         especially important for long running scopes.
   */
  boolean isDestroyed ();

  /**
   * Destroys the scopes and all child scopes. This method is only automatically
   * called, when a scope goes out of scope.
   */
  void destroyScope ();

  /**
   * Perform stuff as a single action. All actions are executed in a write-lock!
   *
   * @param aRunnable
   *        The action to be executed. May not be <code>null</code>. The
   *        parameter to the runnable is <code>this</code> scope.
   */
  void runAtomic (@Nonnull INonThrowingRunnableWithParameter <IScope> aRunnable);

  /**
   * Perform stuff as a single action. All actions are executed in a write-lock!
   *
   * @param aCallable
   *        The action to be executed. May not be <code>null</code>. The
   *        parameter to the callable is <code>this</code> scope.
   * @return The result from the callable. May be <code>null</code>.
   * @param <T>
   *        The return type of the callable
   */
  @Nullable
  <T> T runAtomic (@Nonnull INonThrowingCallableWithParameter <T, IScope> aCallable);

  /**
   * @return The non-<code>null</code> map with all contained attributes that
   *         implement the {@link IScopeRenewalAware} interface. May be empty.
   */
  @Nonnull
  @ReturnsMutableCopy
  Map <String, IScopeRenewalAware> getAllScopeRenewalAwareAttributes ();
}
