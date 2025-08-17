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
package com.helger.http.basicauth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Test class for class {@link HttpBasicAuth}.
 *
 * @author Philip Helger
 */
public final class HttpBasicAuthTest
{
  @Test
  public void testBasic ()
  {
    final BasicAuthClientCredentials aCredentials = new BasicAuthClientCredentials ("Alladin", "open sesame");
    final String sValue = aCredentials.getRequestValue ();
    assertNotNull (sValue);
    final BasicAuthClientCredentials aDecoded = HttpBasicAuth.getBasicAuthClientCredentials (sValue);
    assertNotNull (aDecoded);
    assertEquals (aCredentials, aDecoded);
  }

  @Test
  public void testUserNameOnly ()
  {
    BasicAuthClientCredentials aCredentials = new BasicAuthClientCredentials ("Alladin");
    String sValue = aCredentials.getRequestValue ();
    assertNotNull (sValue);
    BasicAuthClientCredentials aDecoded = HttpBasicAuth.getBasicAuthClientCredentials (sValue);
    assertNotNull (aDecoded);
    assertEquals (aCredentials, aDecoded);

    aCredentials = new BasicAuthClientCredentials ("Alladin", "");
    sValue = aCredentials.getRequestValue ();
    assertNotNull (sValue);
    aDecoded = HttpBasicAuth.getBasicAuthClientCredentials (sValue);
    assertNotNull (aDecoded);
    assertEquals (aCredentials, aDecoded);
  }

  @Test
  public void testGetBasicAuthValues ()
  {
    assertNull (HttpBasicAuth.getBasicAuthClientCredentials ((String) null));
    assertNull (HttpBasicAuth.getBasicAuthClientCredentials (""));
    assertNull (HttpBasicAuth.getBasicAuthClientCredentials ("bla"));
    assertNull (HttpBasicAuth.getBasicAuthClientCredentials ("bla foor"));
    assertNull (HttpBasicAuth.getBasicAuthClientCredentials ("Basic"));
    assertNull (HttpBasicAuth.getBasicAuthClientCredentials ("  Basic  "));
    // Base64 with blanks is OK!
    BasicAuthClientCredentials aUP = HttpBasicAuth.getBasicAuthClientCredentials ("  Basic  QWxsYW  Rp   bjpvcG  VuIH Nlc2F tZQ   =  =   ");
    assertNotNull (aUP);
    assertEquals ("Alladin", aUP.getUserName ());
    assertEquals ("open sesame", aUP.getPassword ());

    aUP = HttpBasicAuth.getBasicAuthClientCredentials ("  Basic  QWxsYWRpbjpvcGVuIHNlc2FtZQ==   ");
    assertNotNull (aUP);
    assertEquals ("Alladin", aUP.getUserName ());
    assertEquals ("open sesame", aUP.getPassword ());
  }
}
