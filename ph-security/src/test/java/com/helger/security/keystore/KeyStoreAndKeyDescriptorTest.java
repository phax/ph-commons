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

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;

/**
 * Test class for class {@link KeyStoreAndKeyDescriptor}.
 *
 * @author Philip Helger
 */
public final class KeyStoreAndKeyDescriptorTest
{
  @Test
  public void testBasic ()
  {
    final KeyStoreAndKeyDescriptor aKD = KeyStoreAndKeyDescriptor.builder ()
                                                                 .type (EKeyStoreType.BKS)
                                                                 .path ("keystores/keystore-pw-test.bks")
                                                                 .password ("test")
                                                                 .provider (new BouncyCastleProvider ())
                                                                 .keyAlias ("alias")
                                                                 .keyPassword ("test")
                                                                 .build ();
    assertNotNull (aKD);
    assertNotNull (aKD.loadKeyStore ());
    assertTrue (aKD.loadKeyStore ().isSuccess ());
    assertNotNull (aKD.loadKey ());
    assertTrue (aKD.loadKey ().isSuccess ());
    assertNotNull (aKD.toString ());

    // Copy
    final KeyStoreAndKeyDescriptor aKD2 = KeyStoreAndKeyDescriptor.builder (aKD).build ();
    assertNotNull (aKD2);
    assertNotNull (aKD2.loadKeyStore ());
    assertTrue (aKD2.loadKeyStore ().isSuccess ());
    assertNotNull (aKD2.loadKey ());
    assertTrue (aKD2.loadKey ().isSuccess ());
  }
}
