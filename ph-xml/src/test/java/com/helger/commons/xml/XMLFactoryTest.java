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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Schema;

import org.junit.Test;
import org.w3c.dom.Document;

import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.mock.AbstractCommonsTestCase;
import com.helger.commons.xml.schema.XMLSchemaCache;

/**
 * Test class for class {@link XMLFactory}.
 *
 * @author Philip Helger
 */
public final class XMLFactoryTest extends AbstractCommonsTestCase
{
  @Test
  public void testCreateDefaultDocumentBuilderFactory ()
  {
    final DocumentBuilderFactory dbf = XMLFactory.createDefaultDocumentBuilderFactory ();
    assertNotNull (dbf);
    assertTrue (dbf.isCoalescing ());
    assertTrue (dbf.isIgnoringComments ());
    assertTrue (dbf.isNamespaceAware ());
  }

  @Test
  public void testGetDocumentBuilder ()
  {
    assertNotNull (XMLFactory.getDocumentBuilder ());
    assertSame (XMLFactory.getDocumentBuilder (), XMLFactory.getDocumentBuilder ());
  }

  @Test
  public void testGetDOMImplementation ()
  {
    assertNotNull (XMLFactory.getDOMImplementation ());
  }

  @Test
  public void testCreateDocumentBuilder ()
  {
    final Schema sch = XMLSchemaCache.getInstance ().getSchema (new ClassPathResource ("xml/schema1.xsd"));
    assertNotNull (sch);
    DocumentBuilder db = XMLFactory.createDocumentBuilder (sch);
    assertNotNull (db);
    assertNotNull (db.getSchema ());

    try
    {
      XMLFactory.createDocumentBuilder ((Schema) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    db = XMLFactory.createDocumentBuilder ();
    assertNotNull (db);
    assertNull (db.getSchema ());
  }

  @Test
  public void testNewDocument ()
  {
    Document doc = XMLFactory.newDocument ();
    assertNotNull (doc);
    assertNull (doc.getDoctype ());
    assertNull (doc.getDocumentElement ());
    assertEquals (EXMLVersion.XML_10.getVersion (), doc.getXmlVersion ());

    doc = XMLFactory.newDocument (EXMLVersion.XML_11);
    assertNotNull (doc);
    assertNull (doc.getDoctype ());
    assertNull (doc.getDocumentElement ());
    assertEquals (EXMLVersion.XML_11.getVersion (), doc.getXmlVersion ());

    doc = XMLFactory.newDocument ("qname", null, null);
    assertNotNull (doc);
    assertNotNull (doc.getDoctype ());
    assertEquals ("qname", doc.getDoctype ().getName ());
    assertNull (doc.getDoctype ().getPublicId ());
    assertNull (doc.getDoctype ().getSystemId ());
    assertNotNull (doc.getDocumentElement ());
    assertEquals ("qname", doc.getDocumentElement ().getTagName ());
    assertEquals (EXMLVersion.XML_10.getVersion (), doc.getXmlVersion ());

    doc = XMLFactory.newDocument (EXMLVersion.XML_11, "qname", "pubid", "sysid");
    assertNotNull (doc);
    assertNotNull (doc.getDoctype ());
    assertEquals ("qname", doc.getDoctype ().getName ());
    assertEquals ("pubid", doc.getDoctype ().getPublicId ());
    assertEquals ("sysid", doc.getDoctype ().getSystemId ());
    assertNotNull (doc.getDocumentElement ());
    assertEquals ("qname", doc.getDocumentElement ().getTagName ());
    assertEquals (EXMLVersion.XML_11.getVersion (), doc.getXmlVersion ());
  }
}
