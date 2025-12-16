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
package com.helger.http.tls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.base.string.StringHelper;

/**
 * Test class for class {@link ETLSConfigurationMode_2018_10}.
 *
 * @author Philip Helger
 */
@Deprecated (forRemoval = true, since = "10.5.0")
public final class ETLSConfigurationMode_2018_10Test
{
  @Deprecated
  @Test
  public void testBasic ()
  {
    for (final ETLSConfigurationMode_2018_10 e : ETLSConfigurationMode_2018_10.values ())
    {
      assertNotNull (e.getID ());
      assertTrue (StringHelper.isNotEmpty (e.getID ()));
      assertSame (e, ETLSConfigurationMode_2018_10.getFromIDOrNull (e.getID ()));
      assertTrue (e.getAllCipherSuites ().isNotEmpty ());
      assertTrue (e.getAllCipherSuitesAsArray ().length > 0);
      assertEquals (e.getAllCipherSuites ().size (), e.getAllCipherSuitesAsArray ().length);
      assertTrue (e.getAllTLSVersions ().isNotEmpty ());
      assertTrue (e.getAllTLSVersionIDs ().isNotEmpty ());
      assertEquals (e.getAllTLSVersions ().size (), e.getAllTLSVersionIDs ().size ());
      assertTrue (e.getAllTLSVersionIDsAsArray ().length > 0);
      assertEquals (e.getAllTLSVersionIDs ().size (), e.getAllTLSVersionIDsAsArray ().length);
    }
    assertNull (ETLSConfigurationMode_2018_10.getFromIDOrNull ("bla"));
    assertNull (ETLSConfigurationMode_2018_10.getFromIDOrNull (null));
  }
}
