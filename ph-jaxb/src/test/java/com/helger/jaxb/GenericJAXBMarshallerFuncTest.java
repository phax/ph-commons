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
package com.helger.jaxb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.helpers.DefaultHandler;

import com.helger.base.io.iface.IHasInputStream;
import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.io.nonblocking.NonBlockingStringReader;
import com.helger.base.io.nonblocking.NonBlockingStringWriter;
import com.helger.io.file.FileOperationManager;
import com.helger.io.resource.ClassPathResource;
import com.helger.io.resource.FileSystemResource;
import com.helger.jaxb.mock.JAXBMarshallerMockArchive;
import com.helger.jaxb.mock.external.MockJAXBArchive;
import com.helger.jaxb.mock.external.MockJAXBCollection;
import com.helger.xml.XMLFactory;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.sax.InputSourceFactory;
import com.helger.xml.serialize.read.SAXReaderSettings;
import com.helger.xml.transform.TransformResultFactory;
import com.helger.xml.transform.TransformSourceFactory;

/**
 * Test class for the read and write methods of {@link GenericJAXBMarshaller}, {@link IJAXBReader}
 * and {@link IJAXBWriter}.
 *
 * @author Philip Helger
 */
public final class GenericJAXBMarshallerFuncTest
{
  private static final File TEST_DIR = new File ("target/junit-jaxb");

  private JAXBMarshallerMockArchive m_aMarshaller;

  private static MockJAXBArchive _createArchive ()
  {
    final MockJAXBArchive aArc = new MockJAXBArchive ();
    aArc.setVersion ("1.23");
    final MockJAXBCollection aCollection = new MockJAXBCollection ();
    aCollection.setDescription ("Test collection");
    aCollection.setID (12345);
    aCollection.setName ("Test");
    aArc.getCollection ().add (aCollection);
    return aArc;
  }

  private static void _assertArchive (final MockJAXBArchive aArc)
  {
    assertNotNull (aArc);
    assertEquals ("1.23", aArc.getVersion ());
    assertEquals (1, aArc.getCollection ().size ());
    assertEquals ("Test", aArc.getOnlyCollection ().getName ());
  }

  @Before
  public void before ()
  {
    // No XSD validation - the objects are created manually
    m_aMarshaller = new JAXBMarshallerMockArchive (false);
  }

  @Test
  public void testSettings ()
  {
    assertTrue (m_aMarshaller.isFormattedOutput ());
    assertNotNull (m_aMarshaller.getNamespaceContext ());
    assertNotNull (m_aMarshaller.getXMLWriterSettings ());
    assertNotNull (m_aMarshaller.toString ());

    // A charset is present by default
    assertTrue (m_aMarshaller.hasCharset ());
    m_aMarshaller.setCharset (StandardCharsets.ISO_8859_1);
    assertEquals (StandardCharsets.ISO_8859_1, m_aMarshaller.getCharset ());
    m_aMarshaller.setCharset (null);
    assertFalse (m_aMarshaller.hasCharset ());

    assertFalse (m_aMarshaller.hasIndentString ());
    m_aMarshaller.setIndentString ("\t");
    assertTrue (m_aMarshaller.hasIndentString ());
    assertEquals ("\t", m_aMarshaller.getIndentString ());

    assertFalse (m_aMarshaller.hasSchemaLocation ());
    m_aMarshaller.setSchemaLocation ("urn:example file.xsd");
    assertTrue (m_aMarshaller.hasSchemaLocation ());
    assertEquals ("urn:example file.xsd", m_aMarshaller.getSchemaLocation ());

    assertFalse (m_aMarshaller.hasNoNamespaceSchemaLocation ());
    m_aMarshaller.setNoNamespaceSchemaLocation ("file.xsd");
    assertTrue (m_aMarshaller.hasNoNamespaceSchemaLocation ());
    assertEquals ("file.xsd", m_aMarshaller.getNoNamespaceSchemaLocation ());
  }

  @Test
  public void testGetAs ()
  {
    final MockJAXBArchive aArc = _createArchive ();

    final String sXML = m_aMarshaller.getAsString (aArc);
    assertNotNull (sXML);
    assertTrue (sXML, sXML.contains ("1.23"));

    final byte [] aBytes = m_aMarshaller.getAsBytes (aArc);
    assertNotNull (aBytes);
    assertTrue (aBytes.length > 0);

    final ByteBuffer aBB = m_aMarshaller.getAsByteBuffer (aArc);
    assertNotNull (aBB);

    try (final NonBlockingByteArrayInputStream aIS = m_aMarshaller.getAsInputStream (aArc))
    {
      assertNotNull (aIS);
    }

    assertNotNull (m_aMarshaller.getAsDocument (aArc));
    assertNotNull (m_aMarshaller.getAsElement (aArc));

    final IMicroDocument aMicroDoc = m_aMarshaller.getAsMicroDocument (aArc);
    assertNotNull (aMicroDoc);
    final IMicroElement aMicroElement = m_aMarshaller.getAsMicroElement (aArc);
    assertNotNull (aMicroElement);
  }

  @Test
  public void testWriteToAndReadFromString ()
  {
    final String sXML = m_aMarshaller.getAsString (_createArchive ());
    assertNotNull (sXML);

    // String
    _assertArchive (m_aMarshaller.read (sXML));
    // char[]
    _assertArchive (m_aMarshaller.read (sXML.toCharArray ()));
    // byte[]
    final byte [] aBytes = sXML.getBytes (StandardCharsets.UTF_8);
    _assertArchive (m_aMarshaller.read (aBytes));
    _assertArchive (m_aMarshaller.read (aBytes, 0, aBytes.length));
    // ByteBuffer
    _assertArchive (m_aMarshaller.read (ByteBuffer.wrap (aBytes)));
    // InputStream
    _assertArchive (m_aMarshaller.read (new NonBlockingByteArrayInputStream (aBytes)));
    // Reader
    _assertArchive (m_aMarshaller.read (new NonBlockingStringReader (sXML)));
    // InputSource
    _assertArchive (m_aMarshaller.read (InputSourceFactory.create (sXML)));
    _assertArchive (m_aMarshaller.read (new SAXReaderSettings (), InputSourceFactory.create (sXML)));
    // Source
    _assertArchive (m_aMarshaller.read (TransformSourceFactory.create (sXML)));
    // Node
    _assertArchive (m_aMarshaller.read (m_aMarshaller.getAsDocument (_createArchive ())));
  }

  @Test
  public void testWriteToAndReadFromFile ()
  {
    final MockJAXBArchive aArc = _createArchive ();
    final FileOperationManager aFOM = new FileOperationManager ();
    aFOM.createDirRecursiveIfNotExisting (TEST_DIR);
    try
    {
      final File aFile = new File (TEST_DIR, "file.xml");
      assertTrue (m_aMarshaller.write (aArc, aFile).isSuccess ());
      _assertArchive (m_aMarshaller.read (aFile));

      final Path aPath = new File (TEST_DIR, "path.xml").toPath ();
      assertTrue (m_aMarshaller.write (aArc, aPath).isSuccess ());
      _assertArchive (m_aMarshaller.read (aPath));

      final FileSystemResource aRes = new FileSystemResource (new File (TEST_DIR, "resource.xml"));
      assertTrue (m_aMarshaller.write (aArc, aRes).isSuccess ());
      _assertArchive (m_aMarshaller.read (aRes));
      // IHasInputStream
      _assertArchive (m_aMarshaller.read (new IHasInputStream ()
      {
        public InputStream getInputStream ()
        {
          return aRes.getInputStream ();
        }

        public boolean isReadMultiple ()
        {
          return true;
        }
      }));

      // The parent is an existing file, so no stream can be opened
      assertTrue (m_aMarshaller.write (aArc, new File (aFile, "child.xml")).isFailure ());
    }
    finally
    {
      aFOM.deleteDirRecursiveIfExisting (TEST_DIR);
    }
  }

  @Test
  public void testWriteToTargets () throws Exception
  {
    final MockJAXBArchive aArc = _createArchive ();

    try (final NonBlockingStringWriter aSW = new NonBlockingStringWriter ())
    {
      assertTrue (m_aMarshaller.write (aArc, aSW).isSuccess ());
      _assertArchive (m_aMarshaller.read (aSW.getAsString ()));
    }

    // Result
    try (final NonBlockingStringWriter aSW = new NonBlockingStringWriter ())
    {
      assertTrue (m_aMarshaller.write (aArc, TransformResultFactory.create (aSW)).isSuccess ());
      assertTrue (aSW.getAsString ().length () > 0);
    }

    // ByteBuffer
    final ByteBuffer aBB = ByteBuffer.allocate (10 * 1024);
    assertTrue (m_aMarshaller.write (aArc, aBB).isSuccess ());
    assertTrue (aBB.position () > 0);

    // ContentHandler
    assertTrue (m_aMarshaller.write (aArc, new DefaultHandler ()).isSuccess ());

    // XMLStreamWriter
    try (final NonBlockingStringWriter aSW = new NonBlockingStringWriter ())
    {
      final XMLStreamWriter aXSW = XMLOutputFactory.newFactory ().createXMLStreamWriter (aSW);
      assertTrue (m_aMarshaller.write (aArc, aXSW).isSuccess ());
      assertTrue (aSW.getAsString ().length () > 0);
    }
  }

  @Test
  public void testReadFromStAX () throws Exception
  {
    final String sXML = m_aMarshaller.getAsString (_createArchive ());
    final XMLInputFactory aIF = XMLFactory.createDefaultXMLInputFactory ();

    final XMLStreamReader aSR = aIF.createXMLStreamReader (new NonBlockingStringReader (sXML));
    _assertArchive (m_aMarshaller.read (aSR));

    final XMLEventReader aER = aIF.createXMLEventReader (new NonBlockingStringReader (sXML));
    _assertArchive (m_aMarshaller.read (aER));
  }

  @Test
  public void testReadInvalid ()
  {
    // Not XML at all
    assertNull (m_aMarshaller.read ("This is not XML"));
    // Empty XML
    assertNull (m_aMarshaller.read (""));
  }

  @Test
  public void testReadFromClassPath ()
  {
    // With XSD validation
    final JAXBMarshallerMockArchive aMarshaller = new JAXBMarshallerMockArchive ();
    assertTrue (aMarshaller.isUseSchema ());
    final MockJAXBArchive aArc = aMarshaller.read (new ClassPathResource ("xml/test-archive-01.xml"));
    assertNotNull (aArc);
  }
}
