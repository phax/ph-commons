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
package com.helger.xml.sax;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Test class for class {@link DefaultEntityResolver}.
 *
 * @author Philip Helger
 */
public final class DefaultEntityResolverTest
{
  @Test
  public void testRemoteSchemeBlocked () throws SAXException, IOException
  {
    final DefaultEntityResolver aER = new DefaultEntityResolver ("http://127.0.0.1:1/base/");
    assertTrue (aER.getAllAllowedRemoteSchemes ().isEmpty ());

    // Port 1 is never listening - the test must not even try to connect
    final InputSource aIS = aER.resolveEntity (null, "http://127.0.0.1:1/evil.xml");
    // Must not be null: a parser treats null as "not handled" and opens the system ID itself
    assertNotNull (aIS);
    assertNotNull (aIS.getCharacterStream ());
    assertEquals (-1, aIS.getCharacterStream ().read ());
  }

  @Test
  public void testRemoteSchemeAllowed () throws SAXException, IOException
  {
    final DefaultEntityResolver aER = new DefaultEntityResolver ("http://127.0.0.1:1/base/");
    aER.setAllowedRemoteSchemes ("http");
    assertTrue (aER.getAllAllowedRemoteSchemes ().contains ("http"));

    // Resolved as a regular URL resource - it is not read here, so no connection is opened
    final InputSource aIS = aER.resolveEntity (null, "http://127.0.0.1:1/evil.xml");
    assertNotNull (aIS);
    assertEquals ("http://127.0.0.1:1/evil.xml", aIS.getSystemId ());
  }

  @Test
  public void testLocalSchemeAlwaysAllowed () throws SAXException, IOException
  {
    final DefaultEntityResolver aER = new DefaultEntityResolver ("classpath:xml/buildinfo.xml");
    final InputSource aIS = aER.resolveEntity (null, "buildinfo.xml");
    assertNotNull (aIS);
  }
}
