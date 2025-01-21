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

import javax.annotation.Nonnull;

import com.helger.commons.annotation.IsSPIInterface;
import com.helger.scope.IRequestScope;

/**
 * SPI for handling the request scope lifecycle. Is invoked for non-web and web
 * scopes.
 *
 * @author Philip Helger
 */
@IsSPIInterface
public interface IRequestScopeSPI
{
  /**
   * Called after the request scope was started
   *
   * @param aRequestScope
   *        The request scope object to be used. Never <code>null</code>.
   */
  void onRequestScopeBegin (@Nonnull IRequestScope aRequestScope);

  /**
   * Called before the request scope is shut down
   *
   * @param aRequestScope
   *        The request scope object to be used. Never <code>null</code>.
   */
  void onRequestScopeEnd (@Nonnull IRequestScope aRequestScope);
}
