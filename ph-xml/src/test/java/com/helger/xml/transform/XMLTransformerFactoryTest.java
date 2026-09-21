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
package com.helger.xml.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.helger.base.io.nonblocking.NonBlockingStringReader;
import com.helger.base.io.nonblocking.NonBlockingStringWriter;
import com.helger.io.resource.ClassPathResource;
import com.helger.io.resource.IReadableResource;
import com.helger.xml.EXMLVersion;
import com.helger.xml.XMLFactory;
import com.helger.xml.serialize.read.DOMReader;
import com.helger.xml.serialize.write.EXMLIncorrectCharacterHandling;
import com.helger.xml.serialize.write.EXMLSerializeIndent;
import com.helger.xml.serialize.write.EXMLSerializeVersion;
import com.helger.xml.serialize.write.XMLCharHelper;
import com.helger.xml.serialize.write.XMLWriter;
import com.helger.xml.serialize.write.XMLWriterSettings;

/**
 * Test class for class {@link XMLTransformerFactory}.
 *
 * @author Philip Helger
 */
public final class XMLTransformerFactoryTest
{
  @Test
  public void testMakeTransformerFactorySecureDeniesAllByDefault ()
  {
    final TransformerFactory aFactory = TransformerFactory.newInstance ();
    XMLTransformerFactory.makeTransformerFactorySecure (aFactory);

    // The empty String is what the JAXP specification defines for "deny all external access"
    assertEquals ("", aFactory.getAttribute (XMLConstants.ACCESS_EXTERNAL_DTD));
    assertEquals ("", aFactory.getAttribute (XMLConstants.ACCESS_EXTERNAL_STYLESHEET));
  }

  @Test
  public void testMakeTransformerFactorySecureWithAllowedSchemes ()
  {
    final TransformerFactory aFactory = TransformerFactory.newInstance ();
    XMLTransformerFactory.makeTransformerFactorySecure (aFactory, "file", null, "", "http");

    assertEquals ("file,http", aFactory.getAttribute (XMLConstants.ACCESS_EXTERNAL_DTD));
    assertEquals ("file,http", aFactory.getAttribute (XMLConstants.ACCESS_EXTERNAL_STYLESHEET));
  }

  @Test
  public void testRemoteStylesheetImportIsDenied ()
  {
    final TransformerFactory aFactory = TransformerFactory.newInstance ();
    XMLTransformerFactory.makeTransformerFactorySecure (aFactory);

    // No connection is attempted - the access is refused before that
    final String sXSLT = "<?xml version='1.0'?>" +
                         "<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform' version='1.0'>" +
                         "<xsl:import href='http://localhost:1/nothing.xsl' />" +
                         "<xsl:template match='/'><x /></xsl:template>" +
                         "</xsl:stylesheet>";
    try
    {
      aFactory.newTemplates (new StreamSource (new NonBlockingStringReader (sXSLT), "file:/dummy.xsl"));
      fail ("The remote xsl:import must not be resolved");
    }
    catch (final TransformerConfigurationException ex)
    {
      // Expected
    }
  }

  @Test
  public void testGetDefaultTransformerFactory ()
  {
    final TransformerFactory fac = XMLTransformerFactory.getDefaultTransformerFactory ();
    assertNotNull (fac);
    // Must be the same!
    assertSame (fac, XMLTransformerFactory.getDefaultTransformerFactory ());
  }

  @Test
  public void testNewTransformer ()
  {
    Transformer t1 = XMLTransformerFactory.newTransformer ();
    assertNotNull (t1);
    assertNotSame (t1, XMLTransformerFactory.newTransformer ());

    // Read valid XSLT
    t1 = XMLTransformerFactory.newTransformer (new ClassPathResource ("xml/test1.xslt"));
    assertNotNull (t1);

    // Read valid XSLT (with import)
    t1 = XMLTransformerFactory.newTransformer (new ClassPathResource ("xml/test2.xslt"));
    assertNotNull (t1);

    // Read invalid XSLT
    assertNull (XMLTransformerFactory.newTransformer (new ClassPathResource ("test1.txt")));

    try
    {
      XMLTransformerFactory.newTransformer ((IReadableResource) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      XMLTransformerFactory.newTransformer ((Source) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testNewTemplates ()
  {
    // Read valid XSLT
    Templates t1 = XMLTransformerFactory.newTemplates (new ClassPathResource ("xml/test1.xslt"));
    assertNotNull (t1);

    // Read valid XSLT (with import)
    t1 = XMLTransformerFactory.newTemplates (new ClassPathResource ("xml/test2.xslt"));
    assertNotNull (t1);
    t1 = XMLTransformerFactory.newTemplates (TransformSourceFactory.create (new ClassPathResource ("xml/test2.xslt")));
    assertNotNull (t1);

    // Read invalid XSLT
    assertNull (XMLTransformerFactory.newTemplates (new ClassPathResource ("test1.txt")));
    assertNull (XMLTransformerFactory.newTemplates (TransformSourceFactory.create (new ClassPathResource ("test1.txt"))));

    try
    {
      XMLTransformerFactory.newTemplates ((IReadableResource) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      XMLTransformerFactory.newTemplates ((Source) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      XMLTransformerFactory.newTemplates (null, (IReadableResource) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      XMLTransformerFactory.newTemplates (null, (Source) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testCustomFactory ()
  {
    final TransformerFactory fac = XMLTransformerFactory.createTransformerFactory (new CollectingTransformErrorListener (),
                                                                                   new LoggingTransformURIResolver (new DefaultTransformURIResolver ()));
    assertNotNull (fac);

    // Read valid XSLT
    Templates t1 = XMLTransformerFactory.newTemplates (fac, new ClassPathResource ("xml/test1.xslt"));
    assertNotNull (t1);

    // Read valid XSLT
    t1 = XMLTransformerFactory.newTemplates (fac,
                                             new CachingTransformStreamSource (new ClassPathResource ("xml/test1.xslt")));
    assertNotNull (t1);

    // Read valid XSLT (with import)
    t1 = XMLTransformerFactory.newTemplates (fac, new ClassPathResource ("xml/test2.xslt"));
    assertNotNull (t1);

    // Read invalid XSLT
    assertNull (XMLTransformerFactory.newTemplates (fac, new ClassPathResource ("test1.txt")));

    try
    {
      XMLTransformerFactory.newTemplates (fac, (IReadableResource) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      XMLTransformerFactory.newTemplates (fac, (Source) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testSpecialChars () throws Exception
  {
    final EXMLVersion eXMLVersion = EXMLVersion.XML_10;
    final EXMLSerializeVersion eXMLSerializeVersion = EXMLSerializeVersion.getFromXMLVersionOrThrow (eXMLVersion);
    final StringBuilder aAttrVal = new StringBuilder ();
    final StringBuilder aText = new StringBuilder ();
    for (char i = 0; i < 256; ++i)
    {
      if (!XMLCharHelper.isInvalidXMLAttributeValueChar (eXMLSerializeVersion, i))
        aAttrVal.append (i);
      if (!XMLCharHelper.isInvalidXMLTextChar (eXMLSerializeVersion, i))
        aText.append (i);
    }

    final Document aDoc = XMLFactory.newDocument (eXMLVersion);
    final Element eRoot = (Element) aDoc.appendChild (aDoc.createElement ("root"));
    eRoot.setAttribute ("test", aAttrVal.toString ());

    final Element e1 = (Element) eRoot.appendChild (aDoc.createElement ("a"));
    e1.appendChild (aDoc.createTextNode (aText.toString ()));

    final Element e2 = (Element) eRoot.appendChild (aDoc.createElement ("b"));
    e2.appendChild (aDoc.createCDATASection ("aaaaaaaaaaa]]>bbbbbbbbbbb]]>ccccccccc"));

    final Element e3 = (Element) eRoot.appendChild (aDoc.createElement ("c"));
    e3.appendChild (aDoc.createCDATASection ("]]>"));

    if (false)
      e3.appendChild (aDoc.createComment ("<!--"));
    e3.appendChild (aDoc.createTextNode ("abc"));
    if (false)
      e3.appendChild (aDoc.createComment ("-->"));

    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    XMLTransformerFactory.newTransformer ().transform (new DOMSource (aDoc), new StreamResult (aSW));
    final String sTransform = aSW.getAsString ();

    final Document aDoc2 = DOMReader.readXMLDOM (sTransform);
    final Node e3a = aDoc2.getDocumentElement ().getChildNodes ().item (2);
    aSW.reset ();
    XMLTransformerFactory.newTransformer ().transform (new DOMSource (e3a), new StreamResult (aSW));

    final String sXML = XMLWriter.getNodeAsString (aDoc,
                                                   new XMLWriterSettings ().setIncorrectCharacterHandling (EXMLIncorrectCharacterHandling.WRITE_TO_FILE_NO_LOG)
                                                                           .setIndent (EXMLSerializeIndent.NONE));
    assertNotNull (sXML);

    assertNotNull ("Failed to read: " + sXML, DOMReader.readXMLDOM (sXML));
  }
}
