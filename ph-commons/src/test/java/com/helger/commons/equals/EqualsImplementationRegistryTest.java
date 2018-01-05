/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.commons.equals;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.mock.CommonsAssert;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link EqualsImplementationRegistry}.
 *
 * @author Philip Helger
 */
public final class EqualsImplementationRegistryTest
{
  @Test
  @SuppressFBWarnings ("EC_BAD_ARRAY_COMPARE")
  public void testEquals () throws ParserConfigurationException
  {
    final DocumentBuilderFactory aDocumentBuilderFactory = DocumentBuilderFactory.newInstance ();
    final DocumentBuilder aDB = aDocumentBuilderFactory.newDocumentBuilder ();

    final Document d1 = aDB.newDocument ();
    final Node aRoot1 = d1.appendChild (d1.createElementNS ("urn:my:helger:test-namespace", "root"));
    ((Element) aRoot1.appendChild (d1.createElement ("child"))).setAttribute ("any", "works");

    final Document d2 = aDB.newDocument ();
    final Node aRoot2 = d2.appendChild (d2.createElementNS ("urn:my:helger:test-namespace", "root"));
    ((Element) aRoot2.appendChild (d2.createElement ("child"))).setAttribute ("any", "works");

    // Regular
    CommonsAssert.assertEquals (d1, d2);

    // 1 level array
    CommonsAssert.assertEquals (ArrayHelper.newArray (d1), ArrayHelper.newArray (d2));

    // 2 level array
    CommonsAssert.assertEquals (ArrayHelper.newArray (ArrayHelper.newArray (d1)),
                                ArrayHelper.newArray (ArrayHelper.newArray (d2)));
  }
}
