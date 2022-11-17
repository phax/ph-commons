/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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
package com.helger.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.RoundingMode;
import java.util.Iterator;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.iterate.IterableIterator;
import com.helger.xml.namespace.MapBasedNamespaceContext;
import com.helger.xml.serialize.write.XMLWriter;
import com.helger.xml.xpath.XPathExpressionHelper;
import com.helger.xml.xpath.XPathHelper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link XMLHelper}.
 *
 * @author Philip Helger
 */
public final class XMLHelperTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (XMLHelperTest.class);
  private static final String TEST_NS = "http://www.helger.com/dev/unittests/commons/";
  private static final String TEST_NS2 = TEST_NS + "alt/";

  @Nonnull
  private static Document _getTestDoc ()
  {
    final Document doc = XMLFactory.newDocument ();
    final Element eRoot = (Element) doc.appendChild (doc.createElement ("root"));
    final Element eAx = (Element) eRoot.appendChild (doc.createElementNS (TEST_NS, "ax"));
    eAx.appendChild (doc.createElement ("ax1"));
    final Element eA = (Element) eRoot.appendChild (doc.createElement ("a"));
    eA.appendChild (doc.createElement ("a1"));
    eRoot.appendChild (doc.createTextNode ("Dummy text between a and b"));
    final Element eB = (Element) eRoot.appendChild (doc.createElement ("b"));
    eB.appendChild (doc.createElement ("b1"));
    eRoot.appendChild (doc.createComment ("Dummy comment between b and c"));
    final Element eC = (Element) eRoot.appendChild (doc.createElement ("c"));
    final Element eC1 = (Element) eC.appendChild (doc.createElement ("c1"));
    eC1.appendChild (doc.createElement ("c11"));
    eC1.appendChild (doc.createComment ("Comment between c11 and c12"));
    eC1.appendChild (doc.createTextNode ("Text between c11 and c12"));
    eC1.appendChild (doc.createElement ("c12"));
    eC.appendChild (doc.createElement ("c2"));
    final Element eD = (Element) eRoot.appendChild (doc.createElement ("d"));
    eD.setAttribute ("attr1", "val1");
    eD.setAttribute ("attr2", "val2");
    eD.appendChild (doc.createElement ("d1"));
    eD.appendChild (doc.createElement ("d2"));
    eRoot.appendChild (doc.createComment ("Dummy comment 1 after d"));
    eRoot.appendChild (doc.createComment ("Dummy comment 2 after d"));
    {
      final Element eE = (Element) eRoot.appendChild (doc.createElementNS (TEST_NS, "e"));
      eE.appendChild (doc.createElement ("e1"));
      eE.appendChild (doc.createElement ("e1"));
    }
    {
      final Element eE = (Element) eRoot.appendChild (doc.createElementNS (TEST_NS2, "e"));
      eE.appendChild (doc.createElementNS (TEST_NS2, "e1"));
      final Element e1 = (Element) eE.appendChild (doc.createElementNS (TEST_NS2, "e1"));
      e1.setAttributeNS (TEST_NS2, "attr3", "val3");
    }

    if (false)
      LOGGER.info (XMLWriter.getNodeAsString (doc));

    return doc;
  }

  @Test
  public void testGetChildElementIterator1 ()
  {
    final Document doc = _getTestDoc ();
    final String [] aExpected = new String [] { "a", "b", "c", "d" };
    final Iterator <Element> it = XMLHelper.getChildElementIteratorNoNS (doc.getDocumentElement ());
    int nCount = 0;
    while (it.hasNext ())
    {
      final Element aElement = it.next ();
      assertTrue (nCount < aExpected.length);
      assertEquals (aExpected[nCount], aElement.getTagName ());
      ++nCount;
    }
    assertEquals (aExpected.length, nCount);
  }

  @Test
  public void testGetChildElementIterator2 ()
  {
    final Document doc = _getTestDoc ();
    final String sExpectedTagName = "b";
    final Iterator <Element> it = XMLHelper.getChildElementIteratorNoNS (doc.getDocumentElement (), sExpectedTagName);
    int nCount = 0;
    while (it.hasNext ())
    {
      final Element aElement = it.next ();
      assertEquals (sExpectedTagName, aElement.getTagName ());
      ++nCount;
    }
    assertEquals (1, nCount);
  }

  @Test
  public void testGetChildElementIteratorNS1 ()
  {
    final Document doc = _getTestDoc ();
    final String [] aExpected = new String [] { "ax", "e" };
    final Iterator <Element> it = XMLHelper.getChildElementIteratorNS (doc.getDocumentElement (), TEST_NS);
    int nCount = 0;
    while (it.hasNext ())
    {
      final Element aElement = it.next ();
      assertTrue (nCount < aExpected.length);
      assertEquals (TEST_NS, aElement.getNamespaceURI ());
      assertEquals (aExpected[nCount], aElement.getLocalName ());
      ++nCount;
    }
    assertEquals (aExpected.length, nCount);
  }

  @Test
  public void testGetChildElementIteratorNS2 ()
  {
    final Document doc = _getTestDoc ();
    final String sExpectedTagName = "e";
    final Iterator <Element> it = XMLHelper.getChildElementIteratorNS (doc.getDocumentElement (),
                                                                       TEST_NS,
                                                                       sExpectedTagName);
    int nCount = 0;
    while (it.hasNext ())
    {
      final Element aElement = it.next ();
      assertEquals (TEST_NS, aElement.getNamespaceURI ());
      assertEquals (sExpectedTagName, aElement.getLocalName ());
      ++nCount;
    }
    assertEquals (1, nCount);
  }

  @Test
  public void testGetFirstChildElement ()
  {
    // Empty document has no child element
    assertNull (XMLHelper.getFirstChildElement (XMLFactory.newDocument ()));
    assertFalse (XMLHelper.hasChildElementNodes (XMLFactory.newDocument ()));

    Document doc = _getTestDoc ();
    assertTrue (XMLHelper.hasChildElementNodes (doc));
    final Element aNode = XMLHelper.getFirstChildElement (doc);
    assertNotNull (aNode);
    assertEquals ("root", aNode.getTagName ());
    assertTrue (XMLHelper.hasChildElementNodes (aNode));
    final Element aNode2 = XMLHelper.getFirstChildElement (aNode);
    assertNotNull (aNode2);
    assertEquals (TEST_NS, aNode2.getNamespaceURI ());
    assertEquals ("ax", aNode2.getTagName ());

    // Special case: the first child is not an element!
    doc = XMLFactory.newDocument ();
    final Node root = doc.appendChild (doc.createElement ("x"));
    root.appendChild (doc.createTextNode ("text"));
    root.appendChild (doc.createElement ("y"));
    assertNotNull (XMLHelper.getFirstChildElement (root));
  }

  @Test
  public void testGetFirstChildElementOfName ()
  {
    // Empty document has no child element
    assertNull (XMLHelper.getFirstChildElementOfName (XMLFactory.newDocument (), "root"));

    final Document doc = _getTestDoc ();
    assertNull (XMLHelper.getFirstChildElementOfName (doc, "anytag"));
    final Element aNode = XMLHelper.getFirstChildElementOfName (doc, "root");
    assertNotNull (aNode);
    assertEquals ("root", aNode.getTagName ());
    final Element aNode2 = XMLHelper.getFirstChildElementOfName (aNode, "ax");
    assertNotNull (aNode2);
    assertEquals (TEST_NS, aNode2.getNamespaceURI ());
    assertEquals ("ax", aNode2.getTagName ());
  }

  @Test
  public void testGetOwnerDocument ()
  {
    assertNull (XMLHelper.getOwnerDocument (null));

    final Document doc = XMLFactory.newDocument ();
    assertEquals (doc, XMLHelper.getOwnerDocument (doc));

    final Element e = (Element) doc.appendChild (doc.createElement ("root"));
    assertEquals (doc, XMLHelper.getOwnerDocument (e));

    final Element e2 = (Element) e.appendChild (doc.createElement ("child"));
    assertEquals (doc, XMLHelper.getOwnerDocument (e2));
  }

  @Test
  public void testAppend ()
  {
    final Document doc = XMLFactory.newDocument ();
    final Element eRoot = (Element) doc.appendChild (doc.createElement ("root"));
    final Document doc2 = XMLFactory.newDocument ();
    XMLHelper.append (eRoot, doc2.createElement ("child"));
    XMLHelper.append (eRoot, "TextNode");
    XMLHelper.append (eRoot, doc2.createElement ("child"));
    XMLHelper.append (eRoot, CollectionHelper.newList ("Text 1", " ", "Text 2"));
    XMLHelper.append (eRoot, new IterableIterator <> (CollectionHelper.newList ("Text 1", " ", "Text 2")));
    XMLHelper.append (eRoot, doc.createElement ("foobar"));
    XMLHelper.append (eRoot, _getTestDoc ());
    XMLHelper.append (eRoot, CollectionHelper.newSet (doc.createElement ("e1"), doc.createElement ("e2")));
    XMLHelper.append (eRoot, new Element [] { doc.createElement ("e3"), doc.createElement ("e4") });

    try
    {
      // null parent not allowed
      XMLHelper.append (null, eRoot);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // Cannot append a node to itself
      XMLHelper.append (eRoot, eRoot);
      fail ();
    }
    catch (final DOMException ex)
    {}

    try
    {
      XMLHelper.append (eRoot, RoundingMode.CEILING);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testGetDirectChildElementCount ()
  {
    final Document doc = XMLFactory.newDocument ();
    final Element eRoot = (Element) doc.appendChild (doc.createElement ("root"));
    eRoot.appendChild (doc.createElement ("child"));
    eRoot.appendChild (doc.createElement ("child2"));
    eRoot.appendChild (doc.createElement ("child"));
    eRoot.appendChild (doc.createElementNS (TEST_NS, "child"));
    eRoot.appendChild (doc.createElementNS (TEST_NS, "child2"));
    eRoot.appendChild (doc.createElementNS (TEST_NS, "child3"));

    assertEquals (0, XMLHelper.getDirectChildElementCountNoNS (null));
    assertEquals (0, XMLHelper.getDirectChildElementCountNoNS (null, "tag"));
    assertEquals (0, XMLHelper.getDirectChildElementCountNS (null, TEST_NS));
    assertEquals (0, XMLHelper.getDirectChildElementCountNS (null, TEST_NS, "tag"));

    assertEquals (3, XMLHelper.getDirectChildElementCountNoNS (eRoot));
    assertEquals (3, XMLHelper.getDirectChildElementCount (eRoot, "child"));
    assertEquals (0, XMLHelper.getDirectChildElementCount (eRoot, "child1"));
    assertEquals (2, XMLHelper.getDirectChildElementCount (eRoot, "child2"));
    assertEquals (2, XMLHelper.getDirectChildElementCountNoNS (eRoot, "child"));
    assertEquals (0, XMLHelper.getDirectChildElementCountNoNS (eRoot, "child1"));
    assertEquals (1, XMLHelper.getDirectChildElementCountNoNS (eRoot, "child2"));
    assertEquals (3, XMLHelper.getDirectChildElementCountNS (eRoot, TEST_NS));
    assertEquals (1, XMLHelper.getDirectChildElementCountNS (eRoot, TEST_NS, "child"));
    assertEquals (0, XMLHelper.getDirectChildElementCountNS (eRoot, TEST_NS, "child1"));
    assertEquals (1, XMLHelper.getDirectChildElementCountNS (eRoot, TEST_NS, "child2"));
    assertEquals (1, XMLHelper.getDirectChildElementCountNS (eRoot, TEST_NS, "child3"));

    final String sOtherNS = TEST_NS + "2";
    assertEquals (0, XMLHelper.getDirectChildElementCountNS (eRoot, sOtherNS));
    assertEquals (0, XMLHelper.getDirectChildElementCountNS (eRoot, sOtherNS, "child"));
    assertEquals (0, XMLHelper.getDirectChildElementCountNS (eRoot, sOtherNS, "child1"));
    assertEquals (0, XMLHelper.getDirectChildElementCountNS (eRoot, sOtherNS, "child2"));
    assertEquals (0, XMLHelper.getDirectChildElementCountNS (eRoot, sOtherNS, "child3"));

    try
    {
      XMLHelper.getDirectChildElementCountNoNS (eRoot, "");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      XMLHelper.getDirectChildElementCountNS (eRoot, TEST_NS, "");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testGetPathToNode ()
  {
    final Document doc = _getTestDoc ();

    // Document root
    assertEquals (doc.getNodeName () + "/", XMLHelper.getPathToNode (doc));
    assertEquals (doc.getNodeName () + "$$", XMLHelper.getPathToNode (doc, "$$"));

    Node e = doc.getDocumentElement ();

    // Different separators
    assertEquals (doc.getNodeName () + "/root[0]/", XMLHelper.getPathToNode (e));
    assertEquals (doc.getNodeName () + "$$root[0]$$", XMLHelper.getPathToNode (e, "$$"));
    assertEquals (doc.getNodeName () + "root[0]", XMLHelper.getPathToNode (e, ""));

    // Check with specific nodes
    e = XPathExpressionHelper.evalXPathToNode ("//e1[2]", doc);
    assertNotNull (e);
    assertNull (e.getNamespaceURI ());
    assertSame (e, XPathExpressionHelper.evalXPathToNode ("//*[local-name()='e1'][2]", doc));
    assertEquals (doc.getNodeName () + "/root[0]/e[0]/e1[1]/", XMLHelper.getPathToNode (e));

    // Special case with same name but different
    final MapBasedNamespaceContext aCtx = new MapBasedNamespaceContext ();
    aCtx.addMapping ("c1", TEST_NS);
    aCtx.addMapping ("c2", TEST_NS2);
    final XPath aXPath = XPathHelper.createNewXPath ();
    aXPath.setNamespaceContext (aCtx);

    e = XPathExpressionHelper.evalXPathToNode (aXPath, "/root/c2:e[1]/c2:e1[1]", doc);
    assertNotNull (e);
    assertEquals (doc.getNodeName () + "/root[0]/e[1]/e1[0]/", XMLHelper.getPathToNode (e));

    try
    {
      XMLHelper.getPathToNode (null);
      fail ();
    }
    catch (final IllegalStateException ex)
    {}
    try
    {
      XMLHelper.getPathToNode (e, null);
      fail ();
    }
    catch (final IllegalStateException ex)
    {}
  }

  @Test
  public void testGetPathToNode2 ()
  {
    final Document doc = _getTestDoc ();
    assertEquals (doc.getNodeName (), XMLHelper.getPathToNode2 (doc));
    assertEquals (doc.getNodeName (), XMLHelper.getPathToNode2 (doc, "$$"));

    Node e = doc.getDocumentElement ();
    assertEquals ("/root", XMLHelper.getPathToNode2 (e));
    assertEquals ("$$root", XMLHelper.getPathToNode2 (e, "$$"));
    assertEquals ("root", XMLHelper.getPathToNode2 (e, ""));

    // Query by XPath is much more comfortable :)
    e = XPathExpressionHelper.evalXPathToNode ("//e1[2]", doc);
    assertNotNull (e);
    assertNull (e.getNamespaceURI ());
    assertSame (e, XPathExpressionHelper.evalXPathToNode ("//*[local-name()='e1'][2]", doc));
    assertEquals ("/root/e[0]/e1[1]", XMLHelper.getPathToNode2 (e));

    // Special case with same name but different
    final MapBasedNamespaceContext aCtx = new MapBasedNamespaceContext ();
    aCtx.addMapping ("c1", TEST_NS);
    aCtx.addMapping ("c2", TEST_NS2);
    final XPath aXPath = XPathHelper.createNewXPath ();
    aXPath.setNamespaceContext (aCtx);

    e = XPathExpressionHelper.evalXPathToNode (aXPath, "/root/c2:e[1]/c2:e1[1]", doc);
    assertNotNull (e);
    assertEquals ("/root/e[1]/e1[0]", XMLHelper.getPathToNode2 (e));

    try
    {
      XMLHelper.getPathToNode2 (null);
      fail ();
    }
    catch (final IllegalStateException ex)
    {}
    try
    {
      XMLHelper.getPathToNode2 (e, null);
      fail ();
    }
    catch (final IllegalStateException ex)
    {}
  }

  @Test
  public void testGetPathToNodeBuilder ()
  {
    final MapBasedNamespaceContext aCtx = new MapBasedNamespaceContext ();
    aCtx.addMapping ("x1", TEST_NS);
    aCtx.addMapping ("y2", TEST_NS2);

    final Document aDoc = _getTestDoc ();

    final Function <Node, String> funcToNode = aNode -> {
      return XMLHelper.pathToNodeBuilder ()
                      .node (aNode)
                      .separator ("/")
                      .excludeDocumentNode ()
                      .oneBasedIndex ()
                      .forceUseIndex (false)
                      .trailingSeparator (false)
                      .compareIncludingNamespaceURI (true)
                      .namespaceContext (aCtx)
                      .build ();
    };

    Node e;

    // Query by XPath is much more comfortable :)
    e = XPathExpressionHelper.evalXPathToNode ("//e1[2]", aDoc);
    assertNotNull (e);
    assertNull (e.getNamespaceURI ());
    assertEquals ("/root/x1:e/e1[2]", funcToNode.apply (e));

    final XPath aXPath = XPathHelper.createNewXPath ();
    aXPath.setNamespaceContext (aCtx);
    e = XPathExpressionHelper.evalXPathToNode (aXPath, "/root/y2:e[1]/y2:e1[1]", aDoc);
    assertNotNull (e);
    assertEquals (Node.ELEMENT_NODE, e.getNodeType ());
    assertSame (e, XPathExpressionHelper.evalXPathToNode ("(//*[local-name()='e1'])[3]", aDoc));
    assertEquals ("/root/y2:e/y2:e1[1]", funcToNode.apply (e));

    e = XPathExpressionHelper.evalXPathToNode ("//d/@attr1", aDoc);
    assertNotNull (e);
    assertEquals (Node.ATTRIBUTE_NODE, e.getNodeType ());
    assertNull (e.getNamespaceURI ());
    assertEquals ("/root/d/@attr1", funcToNode.apply (e));

    e = XPathExpressionHelper.evalXPathToNode (aXPath, "//y2:e1[2]/@y2:attr3", aDoc);
    assertNotNull (e);
    assertEquals (Node.ATTRIBUTE_NODE, e.getNodeType ());
    assertEquals (TEST_NS2, e.getNamespaceURI ());
    assertEquals ("/root/y2:e/y2:e1[2]/@y2:attr3", funcToNode.apply (e));
  }

  @Test
  public void testRemoveAllChildElements ()
  {
    final Document doc = _getTestDoc ();
    final Element e = doc.getDocumentElement ();
    assertEquals (11, e.getChildNodes ().getLength ());
    XMLHelper.removeAllChildElements (e);
    assertEquals (0, e.getChildNodes ().getLength ());
  }

  @Test
  public void testIsInlineNode ()
  {
    final Document doc = XMLFactory.newDocument ();
    assertFalse (XMLHelper.isInlineNode (doc.createAttribute ("attr")));
    assertFalse (XMLHelper.isInlineNode (doc.createAttributeNS (TEST_NS, "attr")));
    assertTrue (XMLHelper.isInlineNode (doc.createCDATASection ("cdata")));
    assertFalse (XMLHelper.isInlineNode (doc.createComment ("comment")));
    assertFalse (XMLHelper.isInlineNode (doc.createDocumentFragment ()));
    assertFalse (XMLHelper.isInlineNode (doc.createElement ("el")));
    assertFalse (XMLHelper.isInlineNode (doc.createElementNS (TEST_NS, "el")));
    assertTrue (XMLHelper.isInlineNode (doc.createEntityReference ("entref")));
    assertFalse (XMLHelper.isInlineNode (doc.createProcessingInstruction ("target", "data")));
    assertTrue (XMLHelper.isInlineNode (doc.createTextNode ("text")));
  }

  @Test
  public void testGetFirstChildText ()
  {
    assertNull (XMLHelper.getFirstChildText (null));
    final Document doc = XMLFactory.newDocument ();
    assertNull (XMLHelper.getFirstChildText (doc));
    final Node root = doc.appendChild (doc.createElement ("root"));
    assertNull (XMLHelper.getFirstChildText (doc));
    assertNull (XMLHelper.getFirstChildText (root));
    root.appendChild (doc.createElement ("child"));
    assertNull (XMLHelper.getFirstChildText (root));
    root.appendChild (doc.createCDATASection ("<>"));
    assertEquals ("<>", XMLHelper.getFirstChildText (root));
  }

  @Test
  @SuppressFBWarnings ("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
  public void testGetAttributeValue ()
  {
    final Document doc = XMLFactory.newDocument ();
    final Element root = (Element) doc.appendChild (doc.createElement ("root"));
    assertNull (XMLHelper.getAttributeValue (root, "key"));
    root.setAttribute ("attr", "ibute");
    assertNull (XMLHelper.getAttributeValue (root, "key"));
    root.setAttribute ("key", "value");
    assertEquals ("value", XMLHelper.getAttributeValue (root, "key"));

    try
    {
      XMLHelper.getAttributeValue (root, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testGetAllAttributesAsMap ()
  {
    assertNotNull (XMLHelper.getAllAttributesAsMap (null));
    final Document doc = XMLFactory.newDocument ();
    final Element eRoot = (Element) doc.appendChild (doc.createElement ("root"));
    assertTrue (XMLHelper.getAllAttributesAsMap (eRoot).isEmpty ());
    eRoot.setAttribute ("name", "value");
    assertEquals (1, XMLHelper.getAllAttributesAsMap (eRoot).size ());
  }

  @Test
  public void testGetNamespaceURI ()
  {
    assertNull (XMLHelper.getNamespaceURI (null));

    Document doc = XMLFactory.newDocument ();
    assertNull (XMLHelper.getNamespaceURI (doc));
    doc.appendChild (doc.createElement ("any"));
    assertNull (XMLHelper.getNamespaceURI (doc));

    doc = XMLFactory.newDocument ();
    doc.appendChild (doc.createElementNS ("myuri", "any"));
    assertEquals ("myuri", XMLHelper.getNamespaceURI (doc));
    assertEquals ("myuri", XMLHelper.getNamespaceURI (doc.createElementNS ("myuri", "any")));
    assertNull (XMLHelper.getNamespaceURI (doc.createAttribute ("attr")));
    assertEquals ("myuri", XMLHelper.getNamespaceURI (doc.createAttributeNS ("myuri", "attr")));
  }

  @Test
  public void testGetQName ()
  {
    final Document doc = XMLFactory.newDocument ();
    final Element e1 = (Element) doc.appendChild (doc.createElement ("any"));
    final Element e2 = (Element) e1.appendChild (doc.createElementNS ("myuri", "any"));

    QName q = XMLHelper.getQName (e1);
    assertNotNull (q);
    assertEquals ("any", q.toString ());

    q = XMLHelper.getQName (e2);
    assertNotNull (q);
    assertEquals ("{myuri}any", q.toString ());
  }
}
