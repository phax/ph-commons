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
package com.helger.commons.xml.serialize.read;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;

import javax.xml.validation.Schema;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.helger.commons.callback.IThrowingRunnable;
import com.helger.commons.charset.CCharset;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.io.resource.URLResource;
import com.helger.commons.io.stream.NonBlockingByteArrayInputStream;
import com.helger.commons.io.stream.NonBlockingStringReader;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.io.stream.StringInputStream;
import com.helger.commons.mock.CommonsTestHelper;
import com.helger.commons.string.StringHelper;
import com.helger.commons.xml.EXMLParserFeature;
import com.helger.commons.xml.XMLSystemProperties;
import com.helger.commons.xml.sax.CachingSAXInputSource;
import com.helger.commons.xml.sax.CollectingSAXErrorHandler;
import com.helger.commons.xml.sax.InputSourceFactory;
import com.helger.commons.xml.sax.LoggingSAXErrorHandler;
import com.helger.commons.xml.sax.ReadableResourceSAXInputSource;
import com.helger.commons.xml.sax.StringSAXInputSource;
import com.helger.commons.xml.schema.XMLSchemaCache;
import com.helger.commons.xml.serialize.write.XMLWriter;

/**
 * Test class for {@link DOMReader}
 *
 * @author Philip Helger
 */
public final class DOMReaderTest
{
  @SuppressWarnings ("unused")
  private static final Logger s_aLogger = LoggerFactory.getLogger (DOMReaderTest.class);

  /**
   * Test method readXMLDOM
   *
   * @throws SAXException
   *         never
   */
  @Test
  public void testReadXMLDOMInputSource () throws SAXException
  {
    Document doc = DOMReader.readXMLDOM ("<root/>");
    assertNotNull (doc);
    doc = DOMReader.readXMLDOM (new NonBlockingStringReader ("<root/>"));
    assertNotNull (doc);
    doc = DOMReader.readXMLDOM (new StringSAXInputSource ("<root/>"));
    assertNotNull (doc);
    doc = DOMReader.readXMLDOM (new StringSAXInputSource ("<?xml version=\"1.0\"?>\n" + "<root/>"));
    assertNotNull (doc);
    doc = DOMReader.readXMLDOM (new StringInputStream ("<?xml version=\"1.0\"?>\n<root/>",
                                                       CCharset.CHARSET_ISO_8859_1_OBJ));
    assertNotNull (doc);

    try
    {
      // null reader not allowed
      DOMReader.readXMLDOM ((InputSource) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // non-XML
      DOMReader.readXMLDOM (new StringSAXInputSource (""));
      fail ();
    }
    catch (final SAXException ex)
    {}

    try
    {
      // non-XML
      DOMReader.readXMLDOM (new StringSAXInputSource ("<bla>"));
      fail ();
    }
    catch (final SAXException ex)
    {}
  }

  /**
   * Test method readXMLDOM
   *
   * @throws SAXException
   *         never
   */
  @Test
  public void testReadXMLDOMString () throws SAXException
  {
    Document doc = DOMReader.readXMLDOM ("<root/>");
    assertNotNull (doc);
    doc = DOMReader.readXMLDOM ("<?xml version=\"1.0\"?>\n" + "<root/>");
    assertNotNull (doc);
    doc = DOMReader.readXMLDOM ("<?xml version=\"1.0\"?>\n" + "<root></root>");
    assertNotNull (doc);
    doc = DOMReader.readXMLDOM ("<?xml version=\"1.0\"?>\n" + "<root><![CDATA[x<>]]></root>");
    assertNotNull (doc);

    try
    {
      // null reader not allowed
      DOMReader.readXMLDOM ((Reader) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // null string not allowed
      DOMReader.readXMLDOM ((String) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // non-XML
      DOMReader.readXMLDOM ("");
      fail ();
    }
    catch (final SAXException ex)
    {}
  }

  /**
   * Test method readXMLDOM
   *
   * @throws SAXException
   *         never
   */
  @Test
  public void testReadXMLDOMInputStream () throws SAXException
  {
    Document doc = DOMReader.readXMLDOM (new StringInputStream ("<root/>", CCharset.CHARSET_ISO_8859_1_OBJ));
    assertNotNull (doc);
    doc = DOMReader.readXMLDOM (new StringInputStream ("<?xml version=\"1.0\"?>\n<root/>",
                                                       CCharset.CHARSET_ISO_8859_1_OBJ));
    assertNotNull (doc);

    try
    {
      // null reader not allowed
      DOMReader.readXMLDOM ((InputStream) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // non-XML
      DOMReader.readXMLDOM (new NonBlockingByteArrayInputStream (new byte [0]));
      fail ();
    }
    catch (final SAXException ex)
    {}

    doc = DOMReader.readXMLDOM (new StringInputStream ("<?xml version=\"1.0\"?>\n<root/>",
                                                       CCharset.CHARSET_ISO_8859_1_OBJ));
    assertNotNull (doc);
  }

  @Test
  public void testReadWithSchema () throws SAXException
  {
    final Schema aSchema = XMLSchemaCache.getInstance ().getSchema (new ClassPathResource ("xml/schema1.xsd"));
    assertNotNull (aSchema);

    // read valid
    final String sValid = "<?xml version='1.0'?><root xmlns='http://www.example.org/schema1'><a>1</a><b>2</b></root>";
    Document doc = DOMReader.readXMLDOM (sValid, new DOMReaderSettings ().setSchema (aSchema));
    assertNotNull (doc);
    doc = DOMReader.readXMLDOM (sValid, new DOMReaderSettings ().setErrorHandler (new LoggingSAXErrorHandler ()));
    assertNotNull (doc);
    doc = DOMReader.readXMLDOM (sValid,
                                new DOMReaderSettings ().setSchema (aSchema)
                                                        .setErrorHandler (new LoggingSAXErrorHandler ()));
    assertNotNull (doc);

    DOMReader.readXMLDOM (new NonBlockingStringReader (sValid), new DOMReaderSettings ().setSchema (aSchema));
    assertNotNull (doc);
    doc = DOMReader.readXMLDOM (new NonBlockingStringReader (sValid),
                                new DOMReaderSettings ().setErrorHandler (new CollectingSAXErrorHandler ()));
    assertNotNull (doc);
    doc = DOMReader.readXMLDOM (new NonBlockingStringReader (sValid),
                                new DOMReaderSettings ().setSchema (aSchema)
                                                        .setErrorHandler (new LoggingSAXErrorHandler ()));
    assertNotNull (doc);

    doc = DOMReader.readXMLDOM (new StringInputStream (sValid, CCharset.CHARSET_ISO_8859_1_OBJ),
                                new DOMReaderSettings ().setSchema (aSchema));
    assertNotNull (doc);
    doc = DOMReader.readXMLDOM (new StringInputStream (sValid, CCharset.CHARSET_ISO_8859_1_OBJ),
                                new DOMReaderSettings ().setErrorHandler (new LoggingSAXErrorHandler ()));
    assertNotNull (doc);
    doc = DOMReader.readXMLDOM (new StringInputStream (sValid, CCharset.CHARSET_ISO_8859_1_OBJ),
                                new DOMReaderSettings ().setSchema (aSchema)
                                                        .setErrorHandler (new LoggingSAXErrorHandler ()));
    assertNotNull (doc);

    doc = DOMReader.readXMLDOM (new StringSAXInputSource (sValid), new DOMReaderSettings ().setSchema (aSchema));
    assertNotNull (doc);
    doc = DOMReader.readXMLDOM (new StringSAXInputSource (sValid),
                                new DOMReaderSettings ().setErrorHandler (new LoggingSAXErrorHandler ()));
    assertNotNull (doc);
    doc = DOMReader.readXMLDOM (new StringSAXInputSource (sValid),
                                new DOMReaderSettings ().setSchema (aSchema)
                                                        .setErrorHandler (new LoggingSAXErrorHandler ()));

    assertNotNull (doc);
    doc = DOMReader.readXMLDOM (new ClassPathResource ("xml/schema1-valid.xml"),
                                new DOMReaderSettings ().setSchema (aSchema));
    assertNotNull (doc);
    doc = DOMReader.readXMLDOM (new ClassPathResource ("xml/schema1-valid.xml"),
                                new DOMReaderSettings ().setErrorHandler (new LoggingSAXErrorHandler ()));
    assertNotNull (doc);
    doc = DOMReader.readXMLDOM (new ClassPathResource ("xml/schema1-valid.xml"),
                                new DOMReaderSettings ().setSchema (aSchema)
                                                        .setErrorHandler (new LoggingSAXErrorHandler ()));
    assertNotNull (doc);

    // Read invalid (<c> tag is unknown)
    final String sInvalid = "<?xml version='1.0'?><root xmlns='http://www.example.org/schema1'><a>1</a><b>2</b><c>3</c></root>";
    doc = DOMReader.readXMLDOM (sInvalid, new DOMReaderSettings ().setSchema (aSchema));
    assertNull (doc);
    doc = DOMReader.readXMLDOM (sInvalid,
                                new DOMReaderSettings ().setSchema (aSchema)
                                                        .setErrorHandler (new LoggingSAXErrorHandler ()));
    assertNull (doc);
    doc = DOMReader.readXMLDOM (new NonBlockingStringReader (sInvalid), new DOMReaderSettings ().setSchema (aSchema));
    assertNull (doc);
    doc = DOMReader.readXMLDOM (new NonBlockingStringReader (sInvalid),
                                new DOMReaderSettings ().setSchema (aSchema)
                                                        .setErrorHandler (new LoggingSAXErrorHandler ()));
    assertNull (doc);
    doc = DOMReader.readXMLDOM (new StringInputStream (sInvalid, CCharset.CHARSET_ISO_8859_1_OBJ),
                                new DOMReaderSettings ().setSchema (aSchema));
    assertNull (doc);
    doc = DOMReader.readXMLDOM (new StringInputStream (sInvalid, CCharset.CHARSET_ISO_8859_1_OBJ),
                                new DOMReaderSettings ().setSchema (aSchema)
                                                        .setErrorHandler (new LoggingSAXErrorHandler ()));
    assertNull (doc);
    doc = DOMReader.readXMLDOM (new StringSAXInputSource (sInvalid), new DOMReaderSettings ().setSchema (aSchema));
    assertNull (doc);
    doc = DOMReader.readXMLDOM (new StringSAXInputSource (sInvalid),
                                new DOMReaderSettings ().setSchema (aSchema)
                                                        .setErrorHandler (new LoggingSAXErrorHandler ()));
    assertNull (doc);
    doc = DOMReader.readXMLDOM (new ClassPathResource ("xml/schema1-invalid.xml"),
                                new DOMReaderSettings ().setSchema (aSchema));
    assertNull (doc);
    doc = DOMReader.readXMLDOM (new ClassPathResource ("xml/schema1-invalid.xml"),
                                new DOMReaderSettings ().setSchema (aSchema)
                                                        .setErrorHandler (new LoggingSAXErrorHandler ()));
    assertNull (doc);

    try
    {
      DOMReader.readXMLDOM ((Reader) null, new DOMReaderSettings ());
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      DOMReader.readXMLDOM ((InputStream) null, new DOMReaderSettings ());
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      DOMReader.readXMLDOM ((InputSource) null, new DOMReaderSettings ());
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      DOMReader.readXMLDOM ((IReadableResource) null, new DOMReaderSettings ());
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testReadProcessingInstruction () throws SAXException
  {
    // Read file with processing instruction
    final Document doc = DOMReader.readXMLDOM (new ClassPathResource ("xml/xml-processing-instruction.xml"));
    assertNotNull (doc);

    // Write again
    assertNotNull (XMLWriter.getXMLString (doc));
  }

  @Test
  public void testReadNotation () throws SAXException
  {
    // Read file with processing instruction
    final Document doc = DOMReader.readXMLDOM (new ClassPathResource ("xml/xml-notation.xml"));
    assertNotNull (doc);

    // Write again
    assertNotNull (XMLWriter.getXMLString (doc));
  }

  @Test
  public void testOtherSources () throws SAXException
  {
    assertNotNull (DOMReader.readXMLDOM (new CachingSAXInputSource (new ClassPathResource ("xml/buildinfo.xml"))));
    assertNotNull (DOMReader.readXMLDOM (new ReadableResourceSAXInputSource (new ClassPathResource ("xml/buildinfo.xml"))));
  }

  @Test
  public void testMultithreadedDOM ()
  {
    CommonsTestHelper.testInParallel (100,
                                      (IThrowingRunnable <SAXException>) () -> assertNotNull (DOMReader.readXMLDOM (new ClassPathResource ("xml/buildinfo.xml"))));
  }

  @Test
  public void testExternalEntityExpansion () throws SAXException, MalformedURLException
  {
    // Include a dummy file
    final File aFile = new File ("src/test/resources/test1.txt");
    assertTrue (aFile.exists ());
    final String sFileContent = StreamHelper.getAllBytesAsString (new FileSystemResource (aFile),
                                                                  CCharset.CHARSET_ISO_8859_1_OBJ);

    // The XML with XXE problem
    final String sXML = "<?xml version='1.0' encoding='utf-8'?>" +
                        "<!DOCTYPE root [" +
                        " <!ELEMENT root ANY >" +
                        " <!ENTITY xxe SYSTEM \"" +
                        aFile.toURI ().toURL ().toExternalForm () +
                        "\" >]>" +
                        "<root>&xxe;</root>";
    final DOMReaderSettings aDRS = new DOMReaderSettings ().setEntityResolver ( (publicId,
                                                                                 systemId) -> InputSourceFactory.create (new URLResource (systemId)));

    // Read successful - entity expansion!
    final Document aDoc = DOMReader.readXMLDOM (sXML, aDRS);
    assertNotNull (aDoc);
    assertEquals (sFileContent, aDoc.getDocumentElement ().getTextContent ());

    // Should fail because inline DTD is present
    try
    {
      DOMReader.readXMLDOM (sXML, aDRS.getClone ().setFeatureValues (EXMLParserFeature.AVOID_XXE_SETTINGS));
      fail ();
    }
    catch (final SAXParseException ex)
    {
      // Expected
      assertTrue (ex.getMessage ().contains ("http://apache.org/xml/features/disallow-doctype-decl"));
    }
  }

  @Test
  public void testEntityExpansionLimit () throws SAXException
  {
    // 64.000 is the default value for JDK7+
    assertEquals (64000, XMLSystemProperties.getXMLEntityExpansionLimit ());

    // The XML with XXE problem
    final String sXMLEntities = "<?xml version='1.0' encoding='utf-8'?>" +
                                "<!DOCTYPE root [" +
                                " <!ELEMENT root ANY >" +
                                " <!ENTITY e1 \"value\" >" +
                                " <!ENTITY e2 \"&e1;&e1;&e1;&e1;&e1;&e1;&e1;&e1;&e1;&e1;\" >" +
                                " <!ENTITY e3 \"&e2;&e2;&e2;&e2;&e2;&e2;&e2;&e2;&e2;&e2;\" >" +
                                " <!ENTITY e4 \"&e3;&e3;&e3;&e3;&e3;&e3;&e3;&e3;&e3;&e3;\" >" +
                                " <!ENTITY e5 \"&e4;&e4;&e4;&e4;&e4;&e4;&e4;&e4;&e4;&e4;\" >" +
                                " <!ENTITY e6 \"&e5;&e5;&e5;&e5;&e5;&e5;&e5;&e5;&e5;&e5;\" >" +
                                " <!ENTITY e7 \"&e6;&e6;&e6;&e6;&e6;&e6;&e6;&e6;&e6;&e6;\">" +
                                " <!ENTITY e8 \"&e7;&e7;&e7;&e7;&e7;&e7;&e7;&e7;&e7;&e7;\">" +
                                " <!ENTITY e9 \"&e8;&e8;&e8;&e8;&e8;&e8;&e8;&e8;&e8;&e8;\">" +
                                " <!ENTITY e10 \"&e9;&e9;&e9;&e9;&e9;&e9;&e9;&e9;&e9;&e9;\">" +
                                "]>";
    // e4 expands to 5.000 times "value"
    // e5 expands to 50.000 times "value"
    // e6 expands to 500.000 times "value"
    final DOMReaderSettings aDRS = new DOMReaderSettings ();

    // Read successful - entity expansion!
    final Document aDoc = DOMReader.readXMLDOM (sXMLEntities + "<root>&e5;</root>", aDRS);
    assertNotNull (aDoc);
    assertEquals (StringHelper.getRepeated ("value", (int) Math.pow (10, 4)),
                  aDoc.getDocumentElement ().getTextContent ());

    // Should fail because too many entity expansions
    try
    {
      DOMReader.readXMLDOM (sXMLEntities +
                            "<root>&e6;</root>",
                            aDRS.getClone ().setFeatureValues (EXMLParserFeature.AVOID_DOS_SETTINGS));
      fail ();
    }
    catch (final SAXParseException ex)
    {
      // Expected
      assertTrue (ex.getMessage (),
                  ex.getMessage ().contains (Integer.toString (XMLSystemProperties.getXMLEntityExpansionLimit ())));
    }

    XMLSystemProperties.setXMLEntityExpansionLimit (500000);

    DOMReaderDefaultSettings.setFeatureValue (EXMLParserFeature.SECURE_PROCESSING, false);
    try
    {
      // Use default DOMReaderSettings
      assertNotNull (DOMReader.readXMLDOM (sXMLEntities + "<root>&e6;</root>"));
    }
    finally
    {
      DOMReaderDefaultSettings.removeFeature (EXMLParserFeature.SECURE_PROCESSING);
    }

    // Set directly in settings
    assertNotNull (DOMReader.readXMLDOM (sXMLEntities +
                                         "<root>&e6;</root>",
                                         new DOMReaderSettings ().setFeatureValue (EXMLParserFeature.SECURE_PROCESSING,
                                                                                   false)));
    XMLSystemProperties.setXMLEntityExpansionLimit (null);
  }
}
