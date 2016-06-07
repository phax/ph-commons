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
package com.helger.commons.xml;

import static org.junit.Assert.assertNotNull;

import org.dom4j.dom.DOMDocument;
import org.dom4j.dom.DOMElement;
import org.dom4j.dom.DOMEntityReference;
import org.dom4j.dom.DOMNamespace;
import org.dom4j.dom.DOMText;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.helger.commons.mock.AbstractCommonsTestCase;
import com.helger.commons.xml.serialize.write.XMLWriter;

/**
 * Test class for {@link XMLWriter} for DOM4J objects.
 *
 * @author Philip Helger
 */
public final class DOM4JFuncTest extends AbstractCommonsTestCase
{
  @Test
  public void testMisc ()
  {
    final Document aXML = new DOMDocument ();
    final Element aRoot = aXML.createElement ("rootElement");
    assertNotNull (XMLWriter.getXMLString (aRoot));
  }

  @Test
  public void testMisc2 ()
  {
    final DOMDocument aXML = new DOMDocument ();
    final Node aChild = aXML.appendChild (new DOMElement ("rootElement",
                                                          new DOMNamespace ("xyz", "http://www.example.org")));
    aChild.appendChild (new DOMText ("anyText"));
    aChild.appendChild (new DOMEntityReference ("abc"));

    assertNotNull (XMLWriter.getXMLString (aXML));
  }
}
