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
package com.helger.bc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.security.Provider;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;

/**
 * Test class for class {@link PBCProvider}.
 *
 * @author Philip Helger
 */
public final class PBCProviderTest
{
  @Test
  public void testGetProvider ()
  {
    final Provider aProvider = PBCProvider.getProvider ();
    assertNotNull (aProvider);
    assertTrue (aProvider instanceof BouncyCastleProvider);

    // Always the very same instance
    assertSame (aProvider, PBCProvider.getProvider ());

    // Must be registered in the global list of Security providers
    assertSame (aProvider, Security.getProvider (PBCProvider.PROVIDER_NAME_BC));
  }

  @Test
  public void testProviderNames ()
  {
    assertEquals (BouncyCastleProvider.PROVIDER_NAME, PBCProvider.PROVIDER_NAME_BC);
    assertEquals ("BCFIPS", PBCProvider.PROVIDER_NAME_BC_FIPS);
  }
}
