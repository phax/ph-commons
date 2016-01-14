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
package com.helger.commons.thirdparty;

import javax.annotation.Nullable;

import com.helger.commons.annotation.IsSPIInterface;

/**
 * Implement this SPI interface if your JAR file contains external third party
 * modules.
 *
 * @author Philip Helger
 */
@IsSPIInterface
public interface IThirdPartyModuleProviderSPI
{
  /**
   * @return A collection of all third party modules to be registered. May be
   *         <code>null</code> or empty. Note: only modules that are required
   *         for execution should be returned, and not modules only present for
   *         testing.
   */
  @Nullable
  IThirdPartyModule [] getAllThirdPartyModules ();
}
