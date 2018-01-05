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
package com.helger.scope;

import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.scope.spi.ScopeSPIManager;

/**
 * The sole purpose of this class to clear all caches, that reside in this
 * library.
 *
 * @author Philip Helger
 */
@Immutable
public final class ScopeCleanup
{
  @PresentForCodeCoverage
  private static final ScopeCleanup s_aInstance = new ScopeCleanup ();

  private ScopeCleanup ()
  {}

  /**
   * Cleanup all custom caches contained in this library. Loaded SPI
   * implementations are not affected by this method! This method does not
   * destroy any scopes!
   */
  public static void cleanup ()
  {
    if (ScopeSPIManager.isInstantiated ())
      ScopeSPIManager.getInstance ().reinitialize ();
  }
}
