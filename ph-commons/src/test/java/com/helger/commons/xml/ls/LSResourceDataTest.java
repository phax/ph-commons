/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.xml.ls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link LSResourceData}.
 *
 * @author Philip Helger
 */
public final class LSResourceDataTest
{
  @Test
  public void testDefault ()
  {
    final LSResourceData rd = new LSResourceData ("http://www.w3.org/2001/XMLSchema",
                                                  "http://www.example.org/schema1",
                                                  null,
                                                  "schema1.xsd",
                                                  "my/path/xml/schema2.xsd");
    assertEquals ("http://www.w3.org/2001/XMLSchema", rd.getType ());
    assertEquals ("http://www.example.org/schema1", rd.getNamespaceURI ());
    assertNull (rd.getPublicID ());
    assertEquals ("schema1.xsd", rd.getSystemID ());
    assertNotNull (rd.getBaseURI ());
    assertTrue (rd.getBaseURI ().endsWith ("xml/schema2.xsd"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (rd,
                                                                           new LSResourceData (null,
                                                                                               null,
                                                                                               null,
                                                                                               null,
                                                                                               null));
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (rd,
                                                                       new LSResourceData (rd.getType (),
                                                                                           rd.getNamespaceURI (),
                                                                                           rd.getPublicID (),
                                                                                           rd.getSystemID (),
                                                                                           rd.getBaseURI ()));
  }
}
