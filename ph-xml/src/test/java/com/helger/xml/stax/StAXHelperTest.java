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
package com.helger.xml.stax;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;


import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.jspecify.annotations.NonNull;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.helger.base.io.nonblocking.NonBlockingStringReader;
import com.helger.xml.XMLFactory;
import com.helger.xml.serialize.read.DOMReader;
import com.helger.xml.serialize.write.XMLWriter;

/**
 * Test class for class {@link StAXHelper}.
 *
 * @author Philip Helger
 */
public final class StAXHelperTest
{
  @NonNull
  private static XMLStreamReader _createOnFirstChild (@NonNull final String sXML) throws XMLStreamException
  {
    final XMLStreamReader aReader = XMLFactory.createNonCoalescingXMLInputFactory ()
                                              .createXMLStreamReader (new NonBlockingStringReader (sXML));
    aReader.nextTag ();
    aReader.nextTag ();
    return aReader;
  }

  @Test
  public void testReadCurrentElementAsDOM () throws XMLStreamException
  {
    // Namespaces "a" and "x" and the default namespace are declared outside of the subtree
    final XMLStreamReader aReader = _createOnFirstChild ("<a:Root xmlns:a='urn:a' xmlns:x='urn:x' xmlns='urn:d'>" +
                                                         "<a:Payload x:attr='1' plain='2'>" +
                                                         "<Child>text &amp; more<![CDATA[<cdata>]]></Child>" +
                                                         "<y:Other xmlns:y='urn:y'><!--c--><x:Deep/></y:Other>" +
                                                         "<NoNS xmlns=''/>" +
                                                         "</a:Payload><a:Next/></a:Root>");
    final Element aElement = StAXHelper.readCurrentElementAsDOM (aReader);
    assertNotNull (aElement);
    assertSame (aElement, aElement.getOwnerDocument ().getDocumentElement ());

    // Positioned on the matching end element
    assertEquals (XMLStreamConstants.END_ELEMENT, aReader.getEventType ());
    assertEquals ("Payload", aReader.getLocalName ());

    assertEquals ("urn:a", aElement.getNamespaceURI ());
    assertEquals ("Payload", aElement.getLocalName ());
    assertEquals ("1", aElement.getAttributeNS ("urn:x", "attr"));
    assertEquals ("2", aElement.getAttribute ("plain"));

    // The serialised subtree must be namespace well-formed on its own
    final String sXML = XMLWriter.getNodeAsString (aElement);
    final Document aReRead = DOMReader.readXMLDOM (sXML);
    assertNotNull (sXML, aReRead);
    final Element aReReadRoot = aReRead.getDocumentElement ();
    assertEquals ("urn:a", aReReadRoot.getNamespaceURI ());
    assertEquals ("1", aReReadRoot.getAttributeNS ("urn:x", "attr"));

    final Element aChild = (Element) aReReadRoot.getElementsByTagNameNS ("urn:d", "Child").item (0);
    assertNotNull (sXML, aChild);
    assertEquals ("text & more<cdata>", aChild.getTextContent ());
    assertNotNull (sXML, aReReadRoot.getElementsByTagNameNS ("urn:y", "Other").item (0));
    assertNotNull (sXML, aReReadRoot.getElementsByTagNameNS ("urn:x", "Deep").item (0));
    final Element aNoNS = (Element) aReReadRoot.getElementsByTagName ("NoNS").item (0);
    assertNotNull (sXML, aNoNS);
    assertNull (aNoNS.getNamespaceURI ());
  }

  @Test (expected = XMLStreamException.class)
  public void testNotOnStartElement () throws XMLStreamException
  {
    final XMLStreamReader aReader = XMLFactory.createNonCoalescingXMLInputFactory ()
                                              .createXMLStreamReader (new NonBlockingStringReader ("<Root/>"));
    StAXHelper.readCurrentElementAsDOM (aReader);
  }
}
