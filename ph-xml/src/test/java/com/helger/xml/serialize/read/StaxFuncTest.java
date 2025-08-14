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
package com.helger.xml.serialize.read;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.string.Strings;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.io.resourceresolver.DefaultResourceResolver;
import com.helger.commons.location.SimpleLocation;
import com.helger.xml.microdom.IMicroCDATA;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroDocumentType;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.IMicroNode;
import com.helger.xml.microdom.IMicroText;
import com.helger.xml.microdom.MicroDocument;
import com.helger.xml.microdom.serialize.MicroWriter;

public final class StaxFuncTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (StaxFuncTest.class);

  @Test
  public void testStax () throws XMLStreamException
  {
    final XMLReporter aReporter = (sMessage, sErrorType, aRelatedInformation, aLocation) -> LOGGER.info (sMessage +
                                                                                                         " [" +
                                                                                                         sErrorType +
                                                                                                         "] " +
                                                                                                         aRelatedInformation +
                                                                                                         " @ " +
                                                                                                         SimpleLocation.create (aLocation));

    final XMLResolver aResolver = (sPublicID, sSystemID, sBaseURI, sNamespace) -> {
      LOGGER.info ("Resolving " + sPublicID + "/" + sSystemID + "/" + sBaseURI + "/" + sNamespace);
      final IReadableResource aResolvedRes = DefaultResourceResolver.getResolvedResource (sSystemID, sBaseURI);
      return aResolvedRes.getInputStream ();
    };

    // get a factory instance
    final XMLInputFactory aFactory = XMLInputFactory.newInstance ();
    // set error reporter (similar to setting ErrorReporter in SAX)
    aFactory.setXMLReporter (aReporter);
    // set resolver (similar to setting EntityResolver in SAX)
    aFactory.setXMLResolver (aResolver);
    // configure the factory, e.g. validating or non-validating
    aFactory.setProperty (XMLConstants.ACCESS_EXTERNAL_DTD, Boolean.FALSE.toString ());

    // create new XMLStreamReader
    final XMLStreamReader aReader = aFactory.createXMLStreamReader (new FileSystemResource ("src/test/resources/xml/xml-stax-test.xml").getInputStream ());

    // document encoding from the XML declaration
    final String encoding = aReader.getEncoding ();
    LOGGER.info ("Using encoding " + encoding + " and version " + aReader.getVersion ());

    // loop through document for XML constructs of interest
    IMicroDocument aDoc = null;
    final IMicroDocumentType aDocType = null;
    IMicroNode aParent = null;
    final boolean bDTDMode = false;
    final boolean bSaveIgnorableWhitespaces = false;
    while (aReader.hasNext ())
    {
      final int nEvent = aReader.next ();
      switch (nEvent)
      {
        case XMLStreamConstants.START_ELEMENT:
        {
          if (aParent == null)
          {
            aDoc = new MicroDocument (aDocType);
            aParent = aDoc;
          }
          final String sNamespaceURI = aReader.getNamespaceURI ();
          final String sLocalName = aReader.getLocalName ();

          IMicroElement aElement;
          if (Strings.isNotEmpty (sNamespaceURI))
            aElement = aParent.addElement (sNamespaceURI, sLocalName);
          else
            aElement = aParent.addElement (sLocalName);

          final int nAttrs = aReader.getAttributeCount ();
          if (nAttrs > 0)
            for (int i = 0; i < nAttrs; ++i)
            {
              final String sAttrNamespaceURI = aReader.getAttributeNamespace (i);
              final String sAttrName = aReader.getAttributeLocalName (i);
              final String sAttrValue = aReader.getAttributeValue (i);

              // Ignore the "xmlns" attributes, as the SAX handler passes the
              // correct namespace URIs
              if (!sAttrName.startsWith (XMLConstants.XMLNS_ATTRIBUTE))
                aElement.setAttribute (sAttrNamespaceURI, sAttrName, sAttrValue);
            }

          aParent = aElement;
          break;
        }
        case XMLStreamConstants.END_ELEMENT:
        {
          // Go one level up in the stack
          aParent = aParent.getParent ();
          break;
        }
        case XMLStreamConstants.PROCESSING_INSTRUCTION:
        {
          if (aParent == null)
          {
            aDoc = new MicroDocument (aDocType);
            aParent = aDoc;
          }
          final String sTarget = aReader.getPITarget ();
          final String sData = aReader.getPIData ();
          aParent.addProcessingInstruction (sTarget, sData);
          break;
        }
        case XMLStreamConstants.CHARACTERS:
        {
          final String sText = aReader.getText ();
          // Regular text node
          final IMicroNode aLastChild = aParent.getLastChild ();
          if (aLastChild != null && aLastChild.getType ().isText ())
          {
            final IMicroText aLastText = (IMicroText) aLastChild;
            if (!aLastText.isElementContentWhitespace ())
            {
              // Merge directly following text nodes to one node!
              // This may happen when compiling with JDK 1.6.0_04
              aLastText.appendData (sText);
            }
            else
            {
              // Add to parent
              aParent.addText (sText);
            }
          }
          else
          {
            // Add to parent
            aParent.addText (sText);
          }
          break;
        }
        case XMLStreamConstants.COMMENT:
        {
          final String sText = aReader.getText ();

          // Ignore comments in DTD
          if (!bDTDMode)
          {
            // In case the comment comes before the root element....
            if (aParent == null)
            {
              aDoc = new MicroDocument (aDocType);
              aParent = aDoc;
            }
            aParent.addComment (sText);
          }
          break;
        }
        case XMLStreamConstants.SPACE:
        {
          final String sText = aReader.getText ();
          if (bSaveIgnorableWhitespaces)
          {
            final IMicroNode aLastChild = aParent.getLastChild ();
            if (aLastChild != null && aLastChild.getType ().isText ())
            {
              final IMicroText aLastText = (IMicroText) aLastChild;
              if (aLastText.isElementContentWhitespace ())
              {
                // Merge directly following text nodes to one node!
                // This may happen when compiling with JDK 1.6.0_04
                aLastText.appendData (sText);
              }
              else
                aParent.addIgnorableWhitespaceText (sText);
            }
            else
              aParent.addIgnorableWhitespaceText (sText);
          }
          break;
        }
        case XMLStreamConstants.START_DOCUMENT:
        {
          // Handled in START_ELEMENT
          break;
        }
        case XMLStreamConstants.END_DOCUMENT:
        {
          // Nothing to do
          break;
        }
        case XMLStreamConstants.ENTITY_REFERENCE:
        {
          // FIXME crap
          final String sPublicId = aReader.getLocalName ();
          final String sSystemId = aReader.getText ();
          LOGGER.info ("ENTITY_REFERENCE: " + sPublicId + " / " + sSystemId);
          break;
        }
        case XMLStreamConstants.ATTRIBUTE:
        {
          // Not to be called in XML parsing, but maybe used in other scenarios
          if (LOGGER.isDebugEnabled ())
          {
            final String sText = aReader.getText ();
            LOGGER.debug ("ATTRIBUTE: " + sText);
          }
          break;
        }
        case XMLStreamConstants.DTD:
        {
          // DTD string is not really of interest to us
          if (LOGGER.isDebugEnabled ())
          {
            final String sText = aReader.getText ();
            LOGGER.debug ("DTD: " + sText);
          }
          break;
        }
        case XMLStreamConstants.CDATA:
        {
          final String sText = aReader.getText ();

          final IMicroNode aLastChild = aParent.getLastChild ();
          if (aLastChild != null && aLastChild.getType ().isCDATA ())
          {
            final IMicroCDATA aLastDATA = (IMicroCDATA) aLastChild;
            // Merge directly following text nodes to one node!
            // This may happen when compiling with JDK 1.6.0_04
            aLastDATA.appendData (sText);
          }
          else
          {
            // Add to parent
            aParent.addCDATA (sText);
          }
          break;
        }
        case XMLStreamConstants.NAMESPACE:
        {
          // FIXME crap
          LOGGER.info ("NAMESPACE: " + aReader.getNamespaceCount () + " namespaces");
          break;
        }
        case XMLStreamConstants.NOTATION_DECLARATION:
        {
          // FIXME crap
          LOGGER.info ("NOTATION_DECLARATION: " + aReader.getText ());
          break;
        }
        case XMLStreamConstants.ENTITY_DECLARATION:
        {
          // FIXME crap
          LOGGER.info ("ENTITY_DECLARATION: " + aReader.getText ());
          break;
        }
        default:
          throw new IllegalStateException ("Invalid event: " + nEvent);
      }
    }
    aReader.close ();

    LOGGER.info (MicroWriter.getNodeAsString (aDoc));
  }
}
