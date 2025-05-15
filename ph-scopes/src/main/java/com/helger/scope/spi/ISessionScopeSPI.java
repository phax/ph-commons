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
package com.helger.scope.spi;

import com.helger.annotation.Nonnull;
import com.helger.annotation.misc.IsSPIInterface;
import com.helger.scope.ISessionScope;

/**
 * SPI for handling the session scope lifecycle. Is invoked for non-web and web
 * scopes.
 *
 * @author Philip Helger
 */
@IsSPIInterface
public interface ISessionScopeSPI
{
  /**
   * Called after the session scope was started
   *
   * @param aSessionScope
   *        The session scope object to be used. Never <code>null</code>.
   */
  void onSessionScopeBegin (@Nonnull ISessionScope aSessionScope);

  /**
   * Called before the session scope is shut down
   *
   * @param aSessionScope
   *        The session scope object to be used. Never <code>null</code>.
   */
  void onSessionScopeEnd (@Nonnull ISessionScope aSessionScope);
}
