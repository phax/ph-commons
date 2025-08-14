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
package com.helger.xml.microdom.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.helger.base.CGlobal;
import com.helger.base.array.ArrayHelper;
import com.helger.collection.helper.CollectionHelperExt;
import com.helger.commons.io.stream.StringInputStream;
import com.helger.xml.EXMLParserFeature;
import com.helger.xml.XMLFactory;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.IMicroNode;
import com.helger.xml.microdom.MicroDocument;
import com.helger.xml.microdom.MicroElement;
import com.helger.xml.mock.MockHasDisplayName;
import com.helger.xml.serialize.read.DOMReader;

/**
 * Test class for class {@link MicroHelper}.
 *
 * @author Philip Helger
 */
public final class MicroHelperTest
{
  @Test
  public void testAppend ()
  {
    final IMicroDocument aDoc = new MicroDocument ();
    final IMicroElement eRoot = aDoc.addElement ("root");
    MicroHelper.append (eRoot, "Any text");
    MicroHelper.append (eRoot, new MicroElement ("child"));
    MicroHelper.append (eRoot, CollectionHelperExt.newList ("t1", "t2"));
    MicroHelper.append (eRoot, ArrayHelper.newArray ("t1", "t2"));
    try
    {
      MicroHelper.append (null, "any");
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      MicroHelper.append (eRoot, new MockHasDisplayName (5));
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testGetPath ()
  {
    final IMicroDocument aDoc = new MicroDocument ();
    assertEquals ("#document", MicroHelper.getPath (aDoc, "/"));
    final IMicroElement eRoot = aDoc.addElement ("root");
    assertEquals ("#document/root", MicroHelper.getPath (eRoot, "/"));
    final IMicroElement eChild = eRoot.addElement ("child");
    assertEquals ("#document/root/child", MicroHelper.getPath (eChild, "/"));
    assertEquals ("", MicroHelper.getPath (null, "/"));
    try
    {
      MicroHelper.getPath (eChild, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testGetDocumentRootElementTagName ()
  {
    assertNull (MicroHelper.getDocumentRootElementTagName (null));
    final IMicroDocument aDoc = new MicroDocument ();
    assertNull (MicroHelper.getDocumentRootElementTagName (aDoc));
    aDoc.addElement ("root");
    assertEquals ("root", MicroHelper.getDocumentRootElementTagName (aDoc));
  }

  @Test
  public void testConvertToMicroNode () throws SAXException, IOException, ParserConfigurationException
  {
    final String sXML = "<?xml version='1.0'?>" +
                        "<!DOCTYPE root [ <!ENTITY sc \"sc.exe\"> <!ELEMENT root (child, child2)> <!ELEMENT child (#PCDATA)> <!ELEMENT child2 (#PCDATA)> ]>" +
                        "<root attr='value'>" +
                        "<![CDATA[hihi]]>" +
                        "text" +
                        "&sc;" +
                        "<child xmlns='http://myns' a='b' />" +
                        "<child2 />" +
                        "<!-- comment -->" +
                        "<?stylesheet x y z?>" +
                        "</root>";
    final DocumentBuilderFactory aDBF = XMLFactory.createDefaultDocumentBuilderFactory ();
    aDBF.setCoalescing (false);
    aDBF.setIgnoringComments (false);
    aDBF.setFeature (EXMLParserFeature.DISALLOW_DOCTYPE_DECL.getName (), false);
    final Document doc = aDBF.newDocumentBuilder ().parse (new StringInputStream (sXML, StandardCharsets.ISO_8859_1));
    assertNotNull (doc);
    final IMicroNode aNode = MicroHelper.convertToMicroNode (doc);
    assertNotNull (aNode);
    try
    {
      MicroHelper.convertToMicroNode (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testGetChildTextContent ()
  {
    final IMicroElement e = new MicroElement ("x");
    assertNull (MicroHelper.getChildTextContent (e, "y"));
    final IMicroElement y = e.addElement ("y");
    assertNull (MicroHelper.getChildTextContent (e, "y"));
    y.addText ("Text");
    assertEquals ("Text", MicroHelper.getChildTextContent (e, "y"));
    y.addElement ("z1");
    assertEquals ("Text", MicroHelper.getChildTextContent (e, "y"));
    y.addCDATA ("data");
    assertEquals ("Textdata", MicroHelper.getChildTextContent (e, "y"));
  }

  @Test
  public void testGetChildTextContentWithConversion ()
  {
    final IMicroElement e = new MicroElement ("x");
    assertNull (MicroHelper.getChildTextContentWithConversion (e, "y", BigInteger.class));
    final IMicroElement y = e.addElement ("y");
    assertNull (MicroHelper.getChildTextContentWithConversion (e, "y", BigInteger.class));
    y.addText ("100");
    assertEquals (CGlobal.BIGINT_100, MicroHelper.getChildTextContentWithConversion (e, "y", BigInteger.class));
    y.addElement ("a");
    assertEquals (CGlobal.BIGINT_100, MicroHelper.getChildTextContentWithConversion (e, "y", BigInteger.class));
    y.addCDATA ("234");
    assertEquals (BigInteger.valueOf (100234),
                  MicroHelper.getChildTextContentWithConversion (e, "y", BigInteger.class));
  }

  @Test
  public void testGetChildTextContentWithNS ()
  {
    final String sNSURI = "my-namespace-uri";
    final IMicroElement e = new MicroElement (sNSURI, "x");
    assertNull (MicroHelper.getChildTextContent (e, sNSURI, "y"));
    final IMicroElement y = e.addElement (sNSURI, "y");
    assertNull (MicroHelper.getChildTextContent (e, sNSURI, "y"));
    y.addText ("Text");
    assertEquals ("Text", MicroHelper.getChildTextContent (e, sNSURI, "y"));
    y.addElement ("z1");
    assertEquals ("Text", MicroHelper.getChildTextContent (e, sNSURI, "y"));
    y.addCDATA ("data");
    assertEquals ("Textdata", MicroHelper.getChildTextContent (e, sNSURI, "y"));
  }

  @Test
  public void testGetChildTextContentWithConversionAndNS ()
  {
    final String sNSURI = "my-namespace-uri";
    final IMicroElement e = new MicroElement (sNSURI, "x");
    assertNull (MicroHelper.getChildTextContentWithConversion (e, sNSURI, "y", BigInteger.class));
    final IMicroElement y = e.addElement (sNSURI, "y");
    assertNull (MicroHelper.getChildTextContentWithConversion (e, sNSURI, "y", BigInteger.class));
    y.addText ("100");
    assertEquals (CGlobal.BIGINT_100, MicroHelper.getChildTextContentWithConversion (e, sNSURI, "y", BigInteger.class));
    y.addElement ("a");
    assertEquals (CGlobal.BIGINT_100, MicroHelper.getChildTextContentWithConversion (e, sNSURI, "y", BigInteger.class));
    y.addCDATA ("234");
    assertEquals (BigInteger.valueOf (100234),
                  MicroHelper.getChildTextContentWithConversion (e, sNSURI, "y", BigInteger.class));
  }

  @Test
  public void testConvertToMicroElementWithNS ()
  {
    final String sNS = "<root xmlns='blafoo'><ns2:element xmlns:ns2='ns2:uri' ns2:attr='value'>content</ns2:element></root>";
    final Document aDoc = DOMReader.readXMLDOM (sNS);
    assertNotNull (aDoc);
    final IMicroDocument aMicroDoc = (IMicroDocument) MicroHelper.convertToMicroNode (aDoc);
    assertNotNull (aMicroDoc);
    final IMicroElement eRoot = aMicroDoc.getDocumentElement ();
    assertNotNull (eRoot);
    assertEquals ("blafoo", eRoot.getNamespaceURI ());
    assertEquals ("root", eRoot.getLocalName ());
    assertEquals ("root", eRoot.getTagName ());
    assertEquals (0, eRoot.getAttributeCount ());
    assertEquals (1, eRoot.getChildElementCount ());
    final IMicroElement eElement = eRoot.getFirstChildElement ();
    assertEquals ("ns2:uri", eElement.getNamespaceURI ());
    assertEquals ("element", eElement.getLocalName ());
    assertEquals ("element", eElement.getTagName ());
  }
}
