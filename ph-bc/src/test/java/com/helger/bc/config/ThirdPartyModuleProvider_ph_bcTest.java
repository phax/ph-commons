/*
 * Original copyright by Apache Software Foundation
 * Copyright (C) 2017-2026 Philip Helger (www.helger.com)
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.helger.base.thirdparty.ELicense;
import com.helger.base.thirdparty.IThirdPartyModule;

/**
 * Test class for class {@link ThirdPartyModuleProvider_ph_bc}.
 *
 * @author Philip Helger
 */
public final class ThirdPartyModuleProvider_ph_bcTest
{
  @Test
  public void testGetAllThirdPartyModules ()
  {
    final IThirdPartyModule [] aModules = new ThirdPartyModuleProvider_ph_bc ().getAllThirdPartyModules ();
    assertNotNull (aModules);
    assertEquals (1, aModules.length);
    assertSame (ThirdPartyModuleProvider_ph_bc.BOUNCY_CASTLE, aModules[0]);
  }

  @Test
  public void testBouncyCastleModule ()
  {
    final IThirdPartyModule aModule = ThirdPartyModuleProvider_ph_bc.BOUNCY_CASTLE;
    assertEquals ("Bouncy Castle", aModule.getDisplayName ());
    assertEquals ("Legion of the Bouncy Castle", aModule.getCopyrightOwner ());
    assertSame (ELicense.MIT, aModule.getLicense ());
    assertNotNull (aModule.getVersion ());
    assertEquals ("https://www.bouncycastle.org/", aModule.getWebSiteURL ());
  }
}
