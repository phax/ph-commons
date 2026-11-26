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
import static org.junit.Assert.fail;

import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.jspecify.annotations.NonNull;
import org.junit.Test;
import org.w3c.dom.Element;

import com.helger.base.io.nonblocking.NonBlockingStringReader;
import com.helger.xml.XMLFactory;
import com.helger.xml.serialize.write.EXMLSerializeIndent;
import com.helger.xml.serialize.write.XMLWriter;
import com.helger.xml.serialize.write.XMLWriterSettings;

/**
 * Test class for class {@link ShallowXMLStreamReader}.
 *
 * @author Philip Helger
 */
public final class ShallowXMLStreamReaderTest
{
  private static final String NS = "urn:test";

  // Shallow are all children of the root except "Header"
  private static final IShallowElementSelector SELECTOR_ENVELOPE = (aAncestors,
                                                                     aElement) -> aAncestors.size () == 1 &&
                                                                                  !"Header".equals (aElement.getLocalPart ());

  @NonNull
  private static XMLStreamReader _create (@NonNull final String sXML,
                                          @NonNull final IShallowElementSelector aSelector) throws XMLStreamException
  {
    return new ShallowXMLStreamReader (XMLFactory.createNonCoalescingXMLInputFactory ()
                                                 .createXMLStreamReader (new NonBlockingStringReader (sXML)),
                                       aSelector);
  }

  @NonNull
  private static String _readAll (@NonNull final XMLStreamReader aReader) throws XMLStreamException
  {
    while (aReader.getEventType () != XMLStreamConstants.START_ELEMENT)
      aReader.next ();
    final Element aElement = StAXHelper.readCurrentElementAsDOM (aReader);
    return XMLWriter.getNodeAsString (aElement, new XMLWriterSettings ().setIndent (EXMLSerializeIndent.NONE));
  }

  @Test
  public void testShallowElement () throws XMLStreamException
  {
    final String sXML = "<Envelope xmlns='" +
                        NS +
                        "'><Header><ID>1</ID></Header><Body a='b' xmlns:x='urn:x'><x:Data>abc<x:Child/>def</x:Data>text</Body></Envelope>";
    final XMLStreamReader aReader = _create (sXML, SELECTOR_ENVELOPE);
    // Check the namespace declaration on the shallow element directly, as XMLWriter omits unused ones
    assertEquals (XMLStreamConstants.START_ELEMENT, aReader.nextTag ());
    assertEquals (XMLStreamConstants.START_ELEMENT, aReader.nextTag ());
    assertEquals ("Header", aReader.getLocalName ());
    assertEquals (XMLStreamConstants.START_ELEMENT, aReader.nextTag ());
    assertEquals ("ID", aReader.getLocalName ());
    assertEquals ("1", aReader.getElementText ());
    assertEquals (XMLStreamConstants.END_ELEMENT, aReader.nextTag ());
    assertEquals ("Header", aReader.getLocalName ());
    assertEquals (XMLStreamConstants.START_ELEMENT, aReader.nextTag ());
    assertEquals ("Body", aReader.getLocalName ());
    assertEquals (1, aReader.getNamespaceCount ());
    assertEquals ("x", aReader.getNamespacePrefix (0));
    assertEquals ("urn:x", aReader.getNamespaceURI (0));
    assertEquals ("b", aReader.getAttributeValue (null, "a"));
    assertEquals (XMLStreamConstants.END_ELEMENT, aReader.nextTag ());
    assertEquals ("Body", aReader.getLocalName ());
    assertEquals (XMLStreamConstants.END_ELEMENT, aReader.nextTag ());
    assertEquals ("Envelope", aReader.getLocalName ());

    // Header is complete, Body is reported without content but with attributes
    assertEquals ("<Envelope xmlns=\"" + NS + "\"><Header><ID>1</ID></Header><Body a=\"b\" /></Envelope>",
                  _readAll (_create (sXML, SELECTOR_ENVELOPE)));
  }

  @Test
  public void testSkipText () throws XMLStreamException
  {
    final String sXML = "<Envelope xmlns='" + NS + "'><Content>Some text<Inner>keep</Inner>more text</Content></Envelope>";
    final IShallowElementSelector aSelector = new IShallowElementSelector ()
    {
      public boolean isShallowElement (final List <QName> aAncestors,
                                       final QName aElement)
      {
        return "Inner".equals (aElement.getLocalPart ());
      }

      @Override
      public boolean isSkipText (final List <QName> aElementPath)
      {
        return "Content".equals (aElementPath.get (aElementPath.size () - 1).getLocalPart ());
      }
    };
    assertEquals ("<Envelope xmlns=\"" + NS + "\"><Content><Inner /></Content></Envelope>",
                  _readAll (_create (sXML, aSelector)));
  }

  @Test
  public void testShallowRootElement () throws XMLStreamException
  {
    final XMLStreamReader aReader = _create ("<Root a='1'><Child/></Root>", (aAncestors, aElement) -> true);
    assertEquals (XMLStreamConstants.START_ELEMENT, aReader.nextTag ());
    assertEquals ("Root", aReader.getLocalName ());
    assertEquals ("1", aReader.getAttributeValue (null, "a"));
    assertEquals (XMLStreamConstants.END_ELEMENT, aReader.nextTag ());
    assertEquals ("Root", aReader.getLocalName ());
    assertEquals (XMLStreamConstants.END_DOCUMENT, aReader.next ());
  }

  @Test
  public void testGetElementText () throws XMLStreamException
  {
    final XMLStreamReader aReader = _create ("<Envelope><Header>a&amp;b<![CDATA[<c>]]></Header><Body>x<y/></Body></Envelope>",
                                             SELECTOR_ENVELOPE);
    assertEquals (XMLStreamConstants.START_ELEMENT, aReader.nextTag ());
    assertEquals (XMLStreamConstants.START_ELEMENT, aReader.nextTag ());
    assertEquals ("Header", aReader.getLocalName ());
    assertEquals ("a&b<c>", aReader.getElementText ());
    assertEquals (XMLStreamConstants.START_ELEMENT, aReader.nextTag ());
    assertEquals ("Body", aReader.getLocalName ());
    // Content of the shallow element is skipped, so there is no child element
    assertEquals ("", aReader.getElementText ());
    assertEquals ("Body", aReader.getLocalName ());
    assertEquals (XMLStreamConstants.END_ELEMENT, aReader.nextTag ());
    assertEquals ("Envelope", aReader.getLocalName ());
  }

  @Test
  public void testMalformedInsideShallowElement () throws XMLStreamException
  {
    final XMLStreamReader aReader = _create ("<Envelope><Header/><Body><a></b></Body></Envelope>", SELECTOR_ENVELOPE);
    try
    {
      while (aReader.hasNext ())
        aReader.next ();
      fail ();
    }
    catch (final XMLStreamException ex)
    {
      // Expected - the skipped content is still parsed
      assertNotNull (ex.getMessage ());
    }
  }

  @Test
  public void testLargeTextChunked () throws XMLStreamException
  {
    final int nLen = 1_000_000;
    final String sXML = "<Envelope><Header/><Body>" + "x".repeat (nLen) + "</Body></Envelope>";
    final XMLStreamReader aReader = _create (sXML, SELECTOR_ENVELOPE);
    int nEvents = 0;
    while (aReader.hasNext ())
    {
      aReader.next ();
      nEvents++;
    }
    // Start and end of Envelope, Header, Body and end document
    assertEquals (7, nEvents);
  }
}
