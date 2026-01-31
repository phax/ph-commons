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
package com.helger.http.tls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.helger.base.CGlobal;

/**
 * Test class for class {@link TLSConfigurationMode}.
 *
 * @author Philip Helger
 */
public final class TLSConfigurationModeTest
{
  @Test
  public void testNoCipherSuite ()
  {
    final TLSConfigurationMode e = new TLSConfigurationMode (new ETLSVersion [] { ETLSVersion.TLS_12 },
                                                             CGlobal.EMPTY_STRING_ARRAY);
    assertEquals (1, e.getAllTLSVersions ().size ());
    assertEquals (1, e.getAllTLSVersionIDs ().size ());
    assertEquals (1, e.getAllTLSVersionIDsAsArray ().length);

    assertEquals (0, e.getAllCipherSuites ().size ());
    assertNull (e.getAllCipherSuitesAsArray ());
  }
}
