/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.base.thirdparty;

import org.jspecify.annotations.Nullable;

import com.helger.annotation.style.IsSPIImplementation;
import com.helger.base.version.Version;

/**
 * Implement this SPI interface if your JAR file contains external third party modules.
 *
 * @author Philip Helger
 */
@IsSPIImplementation
public final class ThirdPartyModuleProvider_ph_base implements IThirdPartyModuleProviderSPI
{
  public static final IThirdPartyModule JSPECIFY = ThirdPartyModule.builder ()
                                                                   .displayName ("JSpecify")
                                                                   .copyrightOwner ("JSpecify Authors")
                                                                   .license (ELicense.APACHE2)
                                                                   .version (new Version (1, 0, 1))
                                                                   .webSiteUrl ("https://jspecify.dev/")
                                                                   .build ();
  public static final IThirdPartyModule SLF4J = ThirdPartyModule.builder ()
                                                                .displayName ("SLF4J API")
                                                                .copyrightOwner ("QOS.ch")
                                                                .license (ELicense.MIT)
                                                                .version (new Version (2, 0, 20))
                                                                .webSiteUrl ("http://www.slf4j.org/")
                                                                .build ();
  public static final IThirdPartyModule BASE64 = ThirdPartyModule.builder ()
                                                                 .displayName ("Base64")
                                                                 .copyrightOwner ("Robert Harder")
                                                                 .license (ELicense.PUBLIC_DOMAIN)
                                                                 .version (new Version (2, 3, 7))
                                                                 .webSiteUrl ("http://iharder.net/base64")
                                                                 .build ();

  /** {@inheritDoc} */
  @Nullable
  public IThirdPartyModule [] getAllThirdPartyModules ()
  {
    return new IThirdPartyModule [] { JSPECIFY, SLF4J, BASE64 };
  }
}
