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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.w3c.dom.Node;

import com.helger.commons.xml.dom.EXMLDOMFeatureVersion;
import com.helger.commons.xml.dom.EXMLDOMNodeType;

/**
 * Test class for class {@link XMLDebug}.
 *
 * @author Philip Helger
 */
public final class XMLDebugTest
{
  @Test
  public void testDebugFeatures ()
  {
    XMLDebug.debugLogDOMFeatures ();
    assertNotNull (XMLDebug.getAllSupportedFeatures ());

    for (final EXMLDOMFeatureVersion eFeatureVersion : EXMLDOMFeatureVersion.values ())
      assertNotNull (XMLDebug.getAllSupportedFeatures (eFeatureVersion));
  }

  @Test
  public void testGetNodeTypeAsString ()
  {
    assertNotNull (XMLDebug.getNodeTypeAsString (Node.ELEMENT_NODE));
    assertNotNull (XMLDebug.getNodeTypeAsString (Node.ATTRIBUTE_NODE));
    assertNotNull (XMLDebug.getNodeTypeAsString (Node.TEXT_NODE));
    assertNotNull (XMLDebug.getNodeTypeAsString (Node.CDATA_SECTION_NODE));
    assertNotNull (XMLDebug.getNodeTypeAsString (Node.ENTITY_REFERENCE_NODE));
    assertNotNull (XMLDebug.getNodeTypeAsString (Node.ENTITY_NODE));
    assertNotNull (XMLDebug.getNodeTypeAsString (Node.PROCESSING_INSTRUCTION_NODE));
    assertNotNull (XMLDebug.getNodeTypeAsString (Node.COMMENT_NODE));
    assertNotNull (XMLDebug.getNodeTypeAsString (Node.DOCUMENT_NODE));
    assertNotNull (XMLDebug.getNodeTypeAsString (Node.DOCUMENT_TYPE_NODE));
    assertNotNull (XMLDebug.getNodeTypeAsString (Node.DOCUMENT_FRAGMENT_NODE));
    assertNotNull (XMLDebug.getNodeTypeAsString (Node.NOTATION_NODE));
    assertEquals ("999", XMLDebug.getNodeTypeAsString (999));

    for (final EXMLDOMNodeType eType : EXMLDOMNodeType.values ())
      assertNotNull (XMLDebug.getNodeTypeAsString (eType.getID ()));
  }
}
