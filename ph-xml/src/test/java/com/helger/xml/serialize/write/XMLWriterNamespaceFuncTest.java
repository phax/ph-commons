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
package com.helger.xml.serialize.write;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;

import org.jspecify.annotations.NonNull;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.helger.xml.XMLFactory;
import com.helger.xml.serialize.read.DOMReader;
import com.helger.xml.transform.StringStreamResult;
import com.helger.xml.transform.XMLTransformerFactory;

/**
 * Test class for the namespace handling of {@link XMLWriter}. The main focus is on serializing a
 * DOM tree in a way that keeps the XML Infoset intact, because that is a precondition for keeping
 * XML signatures (XMLDsig) valid.
 *
 * @author Philip Helger
 */
public final class XMLWriterNamespaceFuncTest
{
  private static final String NS_SOAP12 = "http://www.w3.org/2003/05/soap-envelope";
  private static final String NS_WSSE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
  private static final String NS_WSU = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";

  /**
   * A stripped down but structurally faithful AS4 message. The relevant details are:
   * <ul>
   * <li>the root element uses a namespace prefix</li>
   * <li>the attributes <code>S12:mustUnderstand</code> and <code>ns3:mustUnderstand</code> use the
   * namespace of the root element - they are covered by the signature and their prefix is part of
   * the canonicalized form</li>
   * <li>the same namespace URI is bound to two different prefixes (<code>S12</code> and
   * <code>ns3</code>)</li>
   * <li>superfluous namespace declarations are present (e.g. <code>xmlns:wsu</code> on
   * <code>ec:InclusiveNamespaces</code>)</li>
   * <li>a nested element uses a default namespace</li>
   * </ul>
   */
  private static final String SOAP_XML = "<S12:Envelope xmlns:S12=\"" +
                                         NS_SOAP12 +
                                         "\">" +
                                         "<S12:Header>" +
                                         "<wsse:Security xmlns:wsse=\"" +
                                         NS_WSSE +
                                         "\" xmlns:wsu=\"" +
                                         NS_WSU +
                                         "\" S12:mustUnderstand=\"true\">" +
                                         "<ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" Id=\"SIG-1\">" +
                                         "<ds:SignedInfo>" +
                                         "<ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\">" +
                                         "<ec:InclusiveNamespaces xmlns:ec=\"http://www.w3.org/2001/10/xml-exc-c14n#\" xmlns:wsu=\"" +
                                         NS_WSU +
                                         "\" PrefixList=\"S12\"/>" +
                                         "</ds:CanonicalizationMethod>" +
                                         "</ds:SignedInfo>" +
                                         "<ds:SignatureValue>Zm9vYmFy</ds:SignatureValue>" +
                                         "</ds:Signature>" +
                                         "</wsse:Security>" +
                                         "<eb:Messaging xmlns:eb=\"http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/\" xmlns:ns3=\"" +
                                         NS_SOAP12 +
                                         "\" xmlns:wsu=\"" +
                                         NS_WSU +
                                         "\" ns3:mustUnderstand=\"true\" wsu:Id=\"phase4-msg-1\">" +
                                         "<eb:SignalMessage>" +
                                         "<eb:MessageInfo>" +
                                         "<eb:MessageId>1234@phase4</eb:MessageId>" +
                                         "</eb:MessageInfo>" +
                                         "<phase4 xmlns=\"urn:fdc:com.helger.phase4:ns:info\" version=\"4.0.1\"/>" +
                                         "</eb:SignalMessage>" +
                                         "</eb:Messaging>" +
                                         "</S12:Header>" +
                                         "<S12:Body xmlns:wsu=\"" +
                                         NS_WSU +
                                         "\" wsu:Id=\"id-1\"/>" +
                                         "</S12:Envelope>";

  /**
   * Serialize using the XML runtime (the JAXP identity transformer). This is the reference
   * implementation this test compares against.
   *
   * @param aNode
   *        The node to serialize. May not be <code>null</code>.
   * @return The serialized XML. Never <code>null</code>.
   */
  @NonNull
  private static String _serializeRuntime (@NonNull final Node aNode)
  {
    try
    {
      final Transformer aTransformer = XMLTransformerFactory.newTransformer ();
      final StringStreamResult aResult = new StringStreamResult ();
      aTransformer.transform (new DOMSource (aNode), aResult);
      return aResult.getAsString ();
    }
    catch (final TransformerException ex)
    {
      throw new IllegalStateException ("Failed to serialize XML", ex);
    }
  }

  @NonNull
  private static XMLWriterSettings _createPlainSettings ()
  {
    return new XMLWriterSettings ().setIndent (EXMLSerializeIndent.NONE)
                                   .setSerializeXMLDeclaration (EXMLSerializeXMLDeclaration.IGNORE)
                                   .setSpaceOnSelfClosedElement (false);
  }

  @Test
  public void testAttributeInDefaultNamespaceKeepsPrefix ()
  {
    // "p:attr" is in the same namespace as the default namespace of "a", but
    // the default namespace never applies to attribute names. So the attribute
    // must be emitted with a prefix - otherwise it would end up in no
    // namespace at all.
    final Document aDoc = DOMReader.readXMLDOM ("<a xmlns='urn:def' xmlns:p='urn:def' p:attr='v'><b/></a>");
    assertNotNull (aDoc);

    assertEquals ("<a xmlns=\"urn:def\" xmlns:ns0=\"urn:def\" ns0:attr=\"v\"><b/></a>",
                  XMLWriter.getNodeAsString (aDoc, _createPlainSettings ()));

    // The namespace URI of the attribute survives the round trip
    final Document aDoc2 = DOMReader.readXMLDOM (XMLWriter.getNodeAsString (aDoc, _createPlainSettings ()));
    assertNotNull (aDoc2);
    final Attr aAttr = (Attr) aDoc2.getDocumentElement ().getAttributes ().getNamedItemNS ("urn:def", "attr");
    assertNotNull (aAttr);
    assertEquals ("v", aAttr.getValue ());
  }

  @Test
  public void testNamespacedAttributeOnElementWithoutNamespace ()
  {
    // The element itself has no namespace, so no namespace prefix is "in use"
    // yet. The attribute nevertheless requires a non-empty prefix.
    final Document aDoc = DOMReader.readXMLDOM ("<a xmlns:p='urn:p' p:attr='v'/>");
    assertNotNull (aDoc);

    assertEquals ("<a xmlns:ns0=\"urn:p\" ns0:attr=\"v\"/>", XMLWriter.getNodeAsString (aDoc, _createPlainSettings ()));
  }

  @Test
  public void testElementWithoutNamespaceInsideDefaultNamespace ()
  {
    // A namespace prefix can never be bound to the empty namespace URI, so the
    // default namespace must be undeclared using xmlns=""
    final Document aDoc = DOMReader.readXMLDOM ("<a xmlns='urn:def'><b xmlns=''><c xmlns='urn:def'/></b></a>");
    assertNotNull (aDoc);

    final String sXML = XMLWriter.getNodeAsString (aDoc, _createPlainSettings ());
    assertEquals ("<a xmlns=\"urn:def\"><b xmlns=\"\"><ns0:c xmlns:ns0=\"urn:def\"/></b></a>", sXML);

    // Most important: the result is well-formed and all namespace URIs are
    // retained
    final Document aDoc2 = DOMReader.readXMLDOM (sXML);
    assertNotNull (aDoc2);
    final Element eB = (Element) aDoc2.getDocumentElement ().getFirstChild ();
    assertEquals ("b", eB.getLocalName ());
    assertNull (eB.getNamespaceURI ());
    assertEquals ("urn:def", eB.getFirstChild ().getNamespaceURI ());
  }

  @Test
  public void testUseExistingNamespaceDeclarationsKeepsEverything ()
  {
    final Document aDoc = DOMReader.readXMLDOM (SOAP_XML);
    assertNotNull (aDoc);

    final String sXML = XMLWriter.getNodeAsString (aDoc,
                                                   _createPlainSettings ().setUseExistingNamespaceDeclarations (true));
    // All prefixes and all namespace declarations are emitted as they are
    // present in the source document
    assertEquals (SOAP_XML, sXML);
  }

  @Test
  public void testUseExistingNamespaceDeclarationsIsSignatureSafe ()
  {
    final Document aDoc = DOMReader.readXMLDOM (SOAP_XML);
    assertNotNull (aDoc);

    // These are the settings needed to serialize a signed SOAP message without
    // invalidating the contained XML signature
    final XMLWriterSettings aSettings = XMLWriterSettings.createForCanonicalization ()
                                                         .setIndent (EXMLSerializeIndent.NONE)
                                                         .setSerializeXMLDeclaration (EXMLSerializeXMLDeclaration.EMIT_NO_STANDALONE)
                                                         .setUseExistingNamespaceDeclarations (true);
    final String sXML = XMLWriter.getNodeAsString (aDoc, aSettings);

    // The re-read document must have the very same XML Infoset - including all
    // namespace prefixes and all namespace declarations - otherwise the
    // canonicalized form and therefore the digest value would change
    final Document aDoc2 = DOMReader.readXMLDOM (sXML);
    assertNotNull (aDoc2);
    assertTrue (aDoc.getDocumentElement ().isEqualNode (aDoc2.getDocumentElement ()));

    // The XML runtime serialization of both is identical as well
    assertEquals (_serializeRuntime (aDoc), _serializeRuntime (aDoc2));
  }

  @Test
  public void testUseExistingNamespaceDeclarationsCreatesMissingOnes ()
  {
    // A DOM created via the API usually has no xmlns attributes at all, so
    // there is nothing to "use as-is". The missing namespace declarations must
    // be created, otherwise the result is not even well-formed.
    final Document aDoc = XMLFactory.newDocument ();
    final Element eRoot = (Element) aDoc.appendChild (aDoc.createElementNS ("urn:root", "r:root"));
    final Element eChild = (Element) eRoot.appendChild (aDoc.createElementNS ("urn:child", "c:child"));
    eChild.setAttributeNS ("urn:attr", "a:attr", "value");

    final String sXML = XMLWriter.getNodeAsString (aDoc,
                                                   _createPlainSettings ().setUseExistingNamespaceDeclarations (true));
    assertEquals ("<r:root xmlns:r=\"urn:root\"><c:child xmlns:c=\"urn:child\" xmlns:a=\"urn:attr\" a:attr=\"value\"/></r:root>",
                  sXML);

    // All prefixes and namespace URIs survive the round trip
    final Document aDoc2 = DOMReader.readXMLDOM (sXML);
    assertNotNull (aDoc2);
    final Element eRoot2 = aDoc2.getDocumentElement ();
    assertEquals ("r", eRoot2.getPrefix ());
    assertEquals ("urn:root", eRoot2.getNamespaceURI ());
    final Element eChild2 = (Element) eRoot2.getFirstChild ();
    assertEquals ("c", eChild2.getPrefix ());
    assertEquals ("urn:child", eChild2.getNamespaceURI ());
    final Attr aAttr2 = (Attr) eChild2.getAttributes ().getNamedItemNS ("urn:attr", "attr");
    assertNotNull (aAttr2);
    assertEquals ("a", aAttr2.getPrefix ());
    assertEquals ("value", aAttr2.getValue ());
  }

  @Test
  public void testDefaultSettingsRebuildPrefixes ()
  {
    final Document aDoc = DOMReader.readXMLDOM (SOAP_XML);
    assertNotNull (aDoc);

    // Without "useExistingNamespaceDeclarations" all namespace prefixes are
    // rebuilt from scratch. That is a valid XML representation of the same
    // Infoset, but it is NOT signature safe, because the namespace prefixes
    // are part of the canonicalized form.
    final String sXML = XMLWriter.getNodeAsString (aDoc,
                                                   _createPlainSettings ().setPutNamespaceContextPrefixesInRoot (false));
    final Document aDoc2 = DOMReader.readXMLDOM (sXML);
    assertNotNull (aDoc2);

    // The prefix "S12" is gone - the SOAP namespace became the default
    // namespace of the document
    final Element eRoot = aDoc2.getDocumentElement ();
    assertNull (eRoot.getPrefix ());
    assertEquals (NS_SOAP12, eRoot.getNamespaceURI ());

    // The namespace URI of the "mustUnderstand" attribute is nevertheless
    // retained - only the prefix used for it may be a different one
    final Element eSecurity = (Element) aDoc2.getElementsByTagNameNS (NS_WSSE, "Security").item (0);
    assertNotNull (eSecurity);
    final Attr aMustUnderstand = (Attr) eSecurity.getAttributes ().getNamedItemNS (NS_SOAP12, "mustUnderstand");
    assertNotNull (aMustUnderstand);
    assertEquals ("true", aMustUnderstand.getValue ());
  }
}
