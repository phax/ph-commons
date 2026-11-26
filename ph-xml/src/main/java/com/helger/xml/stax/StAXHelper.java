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

import javax.xml.XMLConstants;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.string.StringHelper;
import com.helger.xml.XMLFactory;

/**
 * Helper methods for StAX.
 *
 * @author Philip Helger
 * @since 12.9.0
 */
@Immutable
public final class StAXHelper
{
  @PresentForCodeCoverage
  private static final StAXHelper INSTANCE = new StAXHelper ();

  private StAXHelper ()
  {}

  @Nullable
  private static String _emptyToNull (@Nullable final String s)
  {
    return StringHelper.isEmpty (s) ? null : s;
  }

  @NonNull
  private static String _getQualifiedName (@Nullable final String sPrefix, @NonNull final String sLocalName)
  {
    return StringHelper.isEmpty (sPrefix) ? sLocalName : sPrefix + ":" + sLocalName;
  }

  /**
   * Make sure the passed prefix is bound to the passed namespace URI in the scope of the passed
   * element, and declare it on the element otherwise. This is needed, because the declaration may
   * be on an ancestor outside of the subtree that is read.
   */
  private static void _ensureNamespaceDeclared (@NonNull final Element aElement,
                                                @Nullable final String sPrefix,
                                                @Nullable final String sNamespaceURI)
  {
    final String sRealPrefix = _emptyToNull (sPrefix);
    final String sRealNamespaceURI = _emptyToNull (sNamespaceURI);
    if (!EqualsHelper.equals (aElement.lookupNamespaceURI (sRealPrefix), sRealNamespaceURI))
    {
      final String sAttrName = sRealPrefix == null ? XMLConstants.XMLNS_ATTRIBUTE : XMLConstants.XMLNS_ATTRIBUTE +
                                                                                    ":" +
                                                                                    sRealPrefix;
      aElement.setAttributeNS (XMLConstants.XMLNS_ATTRIBUTE_NS_URI,
                               sAttrName,
                               sRealNamespaceURI == null ? "" : sRealNamespaceURI);
    }
  }

  @NonNull
  private static Element _createElement (@NonNull final Document aDoc,
                                         @NonNull final Node aParent,
                                         @NonNull final XMLStreamReader aReader)
  {
    final String sPrefix = aReader.getPrefix ();
    final String sNamespaceURI = aReader.getNamespaceURI ();
    final Element aElement = aDoc.createElementNS (_emptyToNull (sNamespaceURI),
                                                   _getQualifiedName (sPrefix, aReader.getLocalName ()));
    aParent.appendChild (aElement);

    // The namespace declarations of this element
    for (int i = 0; i < aReader.getNamespaceCount (); ++i)
    {
      final String sDeclPrefix = _emptyToNull (aReader.getNamespacePrefix (i));
      final String sDeclURI = aReader.getNamespaceURI (i);
      aElement.setAttributeNS (XMLConstants.XMLNS_ATTRIBUTE_NS_URI,
                               sDeclPrefix == null ? XMLConstants.XMLNS_ATTRIBUTE : XMLConstants.XMLNS_ATTRIBUTE +
                                                                                    ":" +
                                                                                    sDeclPrefix,
                               sDeclURI == null ? "" : sDeclURI);
    }
    // Declarations from ancestors outside of the subtree
    _ensureNamespaceDeclared (aElement, sPrefix, sNamespaceURI);

    // The attributes
    for (int i = 0; i < aReader.getAttributeCount (); ++i)
    {
      final String sAttrPrefix = aReader.getAttributePrefix (i);
      final String sAttrNamespaceURI = _emptyToNull (aReader.getAttributeNamespace (i));
      aElement.setAttributeNS (sAttrNamespaceURI,
                               _getQualifiedName (sAttrPrefix, aReader.getAttributeLocalName (i)),
                               aReader.getAttributeValue (i));
      // Unprefixed attributes are in no namespace and need no declaration
      if (sAttrNamespaceURI != null && StringHelper.isNotEmpty (sAttrPrefix))
        _ensureNamespaceDeclared (aElement, sAttrPrefix, sAttrNamespaceURI);
    }
    return aElement;
  }

  /**
   * Read the element the passed reader is positioned on, including all its content, into a new DOM
   * document. Afterwards the reader is positioned on the matching end element.
   * <p>
   * Namespace declarations of ancestor elements are added to the new document where an element or
   * attribute name of the subtree needs them. Prefixes that are only used inside of text or
   * attribute values (e.g. in <code>xsi:type</code>) cannot be detected and are not declared.
   * </p>
   *
   * @param aReader
   *        The reader to read from. Must be positioned on a start element. May not be
   *        <code>null</code>.
   * @return The document element of a new document, representing the read element. Never
   *         <code>null</code>.
   * @throws XMLStreamException
   *         If the reader is not positioned on a start element or in case of a read error
   */
  @NonNull
  public static Element readCurrentElementAsDOM (@NonNull final XMLStreamReader aReader) throws XMLStreamException
  {
    ValueEnforcer.notNull (aReader, "Reader");
    if (aReader.getEventType () != XMLStreamConstants.START_ELEMENT)
      throw new XMLStreamException ("The XMLStreamReader must be positioned on a start element", aReader.getLocation ());

    final Document aDoc = XMLFactory.newDocument ();
    final Element aRoot = _createElement (aDoc, aDoc, aReader);
    Node aParent = aRoot;
    while (true)
    {
      final int nEventType = aReader.next ();
      switch (nEventType)
      {
        case XMLStreamConstants.START_ELEMENT:
          aParent = _createElement (aDoc, aParent, aReader);
          break;
        case XMLStreamConstants.END_ELEMENT:
          if (aParent == aRoot)
          {
            // Merge adjacent text nodes from chunked character data
            aRoot.normalize ();
            return aRoot;
          }
          aParent = aParent.getParentNode ();
          break;
        case XMLStreamConstants.CHARACTERS:
        case XMLStreamConstants.SPACE:
        case XMLStreamConstants.ENTITY_REFERENCE:
          aParent.appendChild (aDoc.createTextNode (aReader.getText ()));
          break;
        case XMLStreamConstants.CDATA:
          aParent.appendChild (aDoc.createCDATASection (aReader.getText ()));
          break;
        case XMLStreamConstants.COMMENT:
          aParent.appendChild (aDoc.createComment (aReader.getText ()));
          break;
        case XMLStreamConstants.PROCESSING_INSTRUCTION:
          aParent.appendChild (aDoc.createProcessingInstruction (aReader.getPITarget (), aReader.getPIData ()));
          break;
        case XMLStreamConstants.END_DOCUMENT:
          throw new XMLStreamException ("Unexpected end of document", aReader.getLocation ());
        default:
          // Ignore all other events
          break;
      }
    }
  }
}
