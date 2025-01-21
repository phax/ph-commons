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
package com.helger.security.keystore;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test class for class {@link TrustStoreDescriptor}.
 *
 * @author Philip Helger
 */
public final class TrustStoreDescriptorTest
{
  @Test
  public void testBasic ()
  {
    final TrustStoreDescriptor aTD = TrustStoreDescriptor.builder ()
                                                         .type (EKeyStoreType.JKS)
                                                         .path ("keystores/truststore-peppol-pilot.jks")
                                                         .password ("peppol")
                                                         .build ();
    assertNotNull (aTD);
    assertNotNull (aTD.loadTrustStore ());
    assertTrue (aTD.loadTrustStore ().isSuccess ());
    assertNotNull (aTD.toString ());

    // Copy
    final TrustStoreDescriptor aTD2 = TrustStoreDescriptor.builder (aTD).build ();
    assertNotNull (aTD2);
    assertNotNull (aTD2.loadTrustStore ());
    assertTrue (aTD2.loadTrustStore ().isSuccess ());
  }
}
