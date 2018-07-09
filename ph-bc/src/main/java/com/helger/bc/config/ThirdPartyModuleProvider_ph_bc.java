/**
 * Original copyright by Apache Software Foundation
 * Copyright (C) 2017-2018 Philip Helger (www.helger.com)
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
package com.helger.bc.config;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.IsSPIImplementation;
import com.helger.commons.thirdparty.ELicense;
import com.helger.commons.thirdparty.IThirdPartyModule;
import com.helger.commons.thirdparty.IThirdPartyModuleProviderSPI;
import com.helger.commons.thirdparty.ThirdPartyModule;
import com.helger.commons.version.Version;

/**
 * Implement this SPI interface if your JAR file contains external third party
 * modules.
 *
 * @author Philip Helger
 */
@IsSPIImplementation
public final class ThirdPartyModuleProvider_ph_bc implements IThirdPartyModuleProviderSPI
{
  public static final IThirdPartyModule BOUNCY_CASTLE = new ThirdPartyModule ("Bouncy Castle",
                                                                              "Legion of the Bouncy Castle",
                                                                              ELicense.MIT,
                                                                              new Version (1, 60),
                                                                              "https://www.bouncycastle.org/");

  @Nonnull
  public IThirdPartyModule [] getAllThirdPartyModules ()
  {
    return new IThirdPartyModule [] { BOUNCY_CASTLE };
  }
}
