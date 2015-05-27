/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.microdom.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.math.BigInteger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.helger.commons.CGlobal;
import com.helger.commons.charset.CCharset;
import com.helger.commons.collections.ArrayHelper;
import com.helger.commons.collections.CollectionHelper;
import com.helger.commons.io.streams.StringInputStream;
import com.helger.commons.microdom.IMicroDocument;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.IMicroNode;
import com.helger.commons.microdom.MicroDocument;
import com.helger.commons.microdom.MicroElement;
import com.helger.commons.name.MockHasDisplayName;
import com.helger.commons.xml.XMLFactory;

/**
 * Test class for class {@link MicroUtils}.
 *
 * @author Philip Helger
 */
public final class MicroUtilsTest
{
  @Test
  public void testAppend ()
  {
    final IMicroDocument aDoc = new MicroDocument ();
    final IMicroElement eRoot = aDoc.appendElement ("root");
    MicroUtils.append (eRoot, "Any text");
    MicroUtils.append (eRoot, new MicroElement ("child"));
    MicroUtils.append (eRoot, CollectionHelper.newList ("t1", "t2"));
    MicroUtils.append (eRoot, ArrayHelper.newArray ("t1", "t2"));
    try
    {
      MicroUtils.append (null, "any");
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      MicroUtils.append (eRoot, new MockHasDisplayName (5));
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testGetPath ()
  {
    final IMicroDocument aDoc = new MicroDocument ();
    assertEquals ("#document", MicroUtils.getPath (aDoc, "/"));
    final IMicroElement eRoot = aDoc.appendElement ("root");
    assertEquals ("#document/root", MicroUtils.getPath (eRoot, "/"));
    final IMicroElement eChild = eRoot.appendElement ("child");
    assertEquals ("#document/root/child", MicroUtils.getPath (eChild, "/"));
    assertEquals ("", MicroUtils.getPath (null, "/"));
    try
    {
      MicroUtils.getPath (eChild, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testGetDocumentRootElementTagName ()
  {
    assertNull (MicroUtils.getDocumentRootElementTagName (null));
    final IMicroDocument aDoc = new MicroDocument ();
    assertNull (MicroUtils.getDocumentRootElementTagName (aDoc));
    aDoc.appendElement ("root");
    assertEquals ("root", MicroUtils.getDocumentRootElementTagName (aDoc));
  }

  @Test
  public void testConvertToMicroNode () throws SAXException, IOException, ParserConfigurationException
  {
    final String sXML = "<?xml version='1.0'?>"
                        + "<!DOCTYPE root [ <!ENTITY sc \"sc.exe\"> <!ELEMENT root (child, child2)> <!ELEMENT child (#PCDATA)> <!ELEMENT child2 (#PCDATA)> ]>"
                        + "<root attr='value'>"
                        + "<![CDATA[hihi]]>"
                        + "text"
                        + "&sc;"
                        + "<child xmlns='http://myns' a='b' />"
                        + "<child2 />"
                        + "<!-- comment -->"
                        + "<?stylesheet x y z?>"
                        + "</root>";
    final DocumentBuilderFactory aDBF = XMLFactory.createDefaultDocumentBuilderFactory ();
    aDBF.setCoalescing (false);
    aDBF.setIgnoringComments (false);
    final Document doc = aDBF.newDocumentBuilder ()
                             .parse (new StringInputStream (sXML, CCharset.CHARSET_ISO_8859_1_OBJ));
    assertNotNull (doc);
    final IMicroNode aNode = MicroUtils.convertToMicroNode (doc);
    assertNotNull (aNode);
    try
    {
      MicroUtils.convertToMicroNode (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testGetChildTextContent ()
  {
    final IMicroElement e = new MicroElement ("x");
    assertNull (MicroUtils.getChildTextContent (e, "y"));
    final IMicroElement y = e.appendElement ("y");
    assertNull (MicroUtils.getChildTextContent (e, "y"));
    y.appendText ("Text");
    assertEquals ("Text", MicroUtils.getChildTextContent (e, "y"));
    y.appendElement ("z1");
    assertEquals ("Text", MicroUtils.getChildTextContent (e, "y"));
    y.appendCDATA ("data");
    assertEquals ("Textdata", MicroUtils.getChildTextContent (e, "y"));
  }

  @Test
  public void testGetChildTextContentWithConversion ()
  {
    final IMicroElement e = new MicroElement ("x");
    assertNull (MicroUtils.getChildTextContentWithConversion (e, "y", BigInteger.class));
    final IMicroElement y = e.appendElement ("y");
    assertNull (MicroUtils.getChildTextContentWithConversion (e, "y", BigInteger.class));
    y.appendText ("100");
    assertEquals (CGlobal.BIGINT_100, MicroUtils.getChildTextContentWithConversion (e, "y", BigInteger.class));
    y.appendElement ("a");
    assertEquals (CGlobal.BIGINT_100, MicroUtils.getChildTextContentWithConversion (e, "y", BigInteger.class));
    y.appendCDATA ("234");
    assertEquals (BigInteger.valueOf (100234), MicroUtils.getChildTextContentWithConversion (e, "y", BigInteger.class));
  }

  @Test
  public void testGetChildTextContentWithNS ()
  {
    final String sNSURI = "my-namespace-uri";
    final IMicroElement e = new MicroElement (sNSURI, "x");
    assertNull (MicroUtils.getChildTextContent (e, sNSURI, "y"));
    final IMicroElement y = e.appendElement (sNSURI, "y");
    assertNull (MicroUtils.getChildTextContent (e, sNSURI, "y"));
    y.appendText ("Text");
    assertEquals ("Text", MicroUtils.getChildTextContent (e, sNSURI, "y"));
    y.appendElement ("z1");
    assertEquals ("Text", MicroUtils.getChildTextContent (e, sNSURI, "y"));
    y.appendCDATA ("data");
    assertEquals ("Textdata", MicroUtils.getChildTextContent (e, sNSURI, "y"));
  }

  @Test
  public void testGetChildTextContentWithConversionAndNS ()
  {
    final String sNSURI = "my-namespace-uri";
    final IMicroElement e = new MicroElement (sNSURI, "x");
    assertNull (MicroUtils.getChildTextContentWithConversion (e, sNSURI, "y", BigInteger.class));
    final IMicroElement y = e.appendElement (sNSURI, "y");
    assertNull (MicroUtils.getChildTextContentWithConversion (e, sNSURI, "y", BigInteger.class));
    y.appendText ("100");
    assertEquals (CGlobal.BIGINT_100, MicroUtils.getChildTextContentWithConversion (e, sNSURI, "y", BigInteger.class));
    y.appendElement ("a");
    assertEquals (CGlobal.BIGINT_100, MicroUtils.getChildTextContentWithConversion (e, sNSURI, "y", BigInteger.class));
    y.appendCDATA ("234");
    assertEquals (BigInteger.valueOf (100234),
                  MicroUtils.getChildTextContentWithConversion (e, sNSURI, "y", BigInteger.class));
  }
}
