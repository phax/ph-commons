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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import javax.xml.validation.Schema;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.helger.base.array.ArrayHelper;
import com.helger.base.iface.IThrowingRunnable;
import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.io.nonblocking.NonBlockingStringReader;
import com.helger.base.io.stream.StreamHelper;
import com.helger.base.string.Strings;
import com.helger.commons.charset.EUnicodeBOM;
import com.helger.commons.io.file.FileHelper;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.io.resource.URLResource;
import com.helger.commons.io.stream.StringInputStream;
import com.helger.commons.mock.CommonsTestHelper;
import com.helger.commons.system.EJavaVersion;
import com.helger.xml.EXMLParserFeature;
import com.helger.xml.XMLSystemProperties;
import com.helger.xml.sax.CachingSAXInputSource;
import com.helger.xml.sax.CollectingSAXErrorHandler;
import com.helger.xml.sax.DoNothingSAXErrorHandler;
import com.helger.xml.sax.InputSourceFactory;
import com.helger.xml.sax.LoggingSAXErrorHandler;
import com.helger.xml.sax.ReadableResourceSAXInputSource;
import com.helger.xml.sax.StringSAXInputSource;
import com.helger.xml.schema.XMLSchemaCache;
import com.helger.xml.serialize.write.XMLWriter;

/**
 * Test class for {@link DOMReader}
 *
 * @author Philip Helger
 */
public final class DOMReaderTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (DOMReaderTest.class);

  /**
   * Test method readXMLDOM @ never
   */
  @Test
  public void testReadXMLDOMInputSource ()
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
                                                       StandardCharsets.ISO_8859_1));
    assertNotNull (doc);
    try
    {
      // null reader not allowed
      DOMReader.readXMLDOM ((InputSource) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    // non-XML
    assertNull (DOMReader.readXMLDOM (new StringSAXInputSource ("")));

    // non-XML
    assertNull (DOMReader.readXMLDOM (new StringSAXInputSource ("<bla>")));
  }

  /**
   * Test method readXMLDOM @ never
   */
  @Test
  public void testReadXMLDOMString ()
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
    // non-XML
    assertNull (DOMReader.readXMLDOM (""));
  }

  @Test
  public void testReadXMLDOMWithBOM ()
  {
    byte [] aXML = "<root/>".getBytes (StandardCharsets.ISO_8859_1);
    for (final EUnicodeBOM eBOM : EUnicodeBOM.values ())
      if (eBOM.isSupportedByXmlReader ())
      {
        byte [] aXMLWithBOM = ArrayHelper.getConcatenated (eBOM.getAllBytes (), aXML);
        Document aDoc = DOMReader.readXMLDOM (aXMLWithBOM);
        assertNotNull ("Failed to read XML with BOM " + eBOM, aDoc);
      }
  }

  /**
   * Test method readXMLDOM @ never
   */
  @Test
  public void testReadXMLDOMInputStream ()
  {
    Document doc = DOMReader.readXMLDOM (new StringInputStream ("<root/>", StandardCharsets.ISO_8859_1));
    assertNotNull (doc);
    doc = DOMReader.readXMLDOM (new StringInputStream ("<?xml version=\"1.0\"?>\n<root/>",
                                                       StandardCharsets.ISO_8859_1));
    assertNotNull (doc);
    try
    {
      // null reader not allowed
      DOMReader.readXMLDOM ((InputStream) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    // non-XML
    assertNull (DOMReader.readXMLDOM (new NonBlockingByteArrayInputStream (new byte [0])));

    doc = DOMReader.readXMLDOM (new StringInputStream ("<?xml version=\"1.0\"?>\n<root/>",
                                                       StandardCharsets.ISO_8859_1));
    assertNotNull (doc);
  }

  @Test
  public void testReadWithSchema ()
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

    doc = DOMReader.readXMLDOM (new StringInputStream (sValid, StandardCharsets.ISO_8859_1),
                                new DOMReaderSettings ().setSchema (aSchema));
    assertNotNull (doc);
    doc = DOMReader.readXMLDOM (new StringInputStream (sValid, StandardCharsets.ISO_8859_1),
                                new DOMReaderSettings ().setErrorHandler (new LoggingSAXErrorHandler ()));
    assertNotNull (doc);
    doc = DOMReader.readXMLDOM (new StringInputStream (sValid, StandardCharsets.ISO_8859_1),
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
    doc = DOMReader.readXMLDOM (new StringInputStream (sInvalid, StandardCharsets.ISO_8859_1),
                                new DOMReaderSettings ().setSchema (aSchema));
    assertNull (doc);
    doc = DOMReader.readXMLDOM (new StringInputStream (sInvalid, StandardCharsets.ISO_8859_1),
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
  public void testReadProcessingInstruction ()
  {
    // Read file with processing instruction
    final Document doc = DOMReader.readXMLDOM (new ClassPathResource ("xml/xml-processing-instruction.xml"));
    assertNotNull (doc);

    // Write again
    assertNotNull (XMLWriter.getNodeAsString (doc));
  }

  @Test
  public void testReadNotation ()
  {
    // Read file with processing instruction
    final Document doc = DOMReader.readXMLDOM (new ClassPathResource ("xml/xml-notation.xml"),
                                               new DOMReaderSettings ().setFeatureValue (EXMLParserFeature.DISALLOW_DOCTYPE_DECL,
                                                                                         false));
    assertNotNull (doc);

    // Write again
    assertNotNull (XMLWriter.getNodeAsString (doc));
  }

  @Test
  public void testOtherSources ()
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
  public void testExternalEntityExpansion ()
  {
    // Include a dummy file
    final File aFile = new File ("src/test/resources/test1.txt");
    assertTrue (aFile.exists ());
    final String sFileContent = StreamHelper.getAllBytesAsString (new FileSystemResource (aFile),
                                                                  StandardCharsets.ISO_8859_1);

    // The XML with XXE problem
    final String sXML = "<?xml version='1.0' encoding='utf-8'?>" +
                        "<!DOCTYPE root [" +
                        " <!ELEMENT root ANY >" +
                        " <!ENTITY xxe SYSTEM \"" +
                        FileHelper.getAsURLString (aFile) +
                        "\" >]>" +
                        "<root>&xxe;</root>";
    final DOMReaderSettings aDRS = new DOMReaderSettings ().setFeatureValue (EXMLParserFeature.DISALLOW_DOCTYPE_DECL,
                                                                             false)
                                                           .setFeatureValue (EXMLParserFeature.EXTERNAL_GENERAL_ENTITIES,
                                                                             true)
                                                           .setEntityResolver ( (publicId,
                                                                                 systemId) -> InputSourceFactory.create (new URLResource (systemId)));

    // Read successful - entity expansion!
    final Document aDoc = DOMReader.readXMLDOM (sXML, aDRS);
    assertNotNull (aDoc);
    assertNotNull (aDoc.getDocumentElement ());
    assertEquals (sFileContent, aDoc.getDocumentElement ().getTextContent ());

    // Should fail because inline DTD is present
    final CollectingSAXErrorHandler aCEH = new CollectingSAXErrorHandler ();
    assertNull (DOMReader.readXMLDOM (sXML,
                                      aDRS.getClone ()
                                          .removeFeatures (EXMLParserFeature.DISALLOW_DOCTYPE_DECL,
                                                           EXMLParserFeature.EXTERNAL_GENERAL_ENTITIES)
                                          .setErrorHandler (aCEH)));
    // Expected
    assertEquals (1, aCEH.getErrorList ().size ());
    assertTrue (aCEH.getErrorList ()
                    .getFirstOrNull ()
                    .getErrorText (Locale.ROOT)
                    .contains ("http://apache.org/xml/features/disallow-doctype-decl"));
  }

  @Test
  public void testEntityExpansionLimit ()
  {
    final boolean bIsJava24Plus = EJavaVersion.getCurrentVersion ().isNewerOrEqualsThan (EJavaVersion.JDK_24);

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
    // e4 expands 1.111 times to 100 times "value"
    // e5 expands 11.111 times to 1.000 times "value"
    // e6 expands 111.111 times to 10.000 times "value"
    final DOMReaderSettings aDRS = new DOMReaderSettings ().setFeatureValue (EXMLParserFeature.DISALLOW_DOCTYPE_DECL,
                                                                             false);

    // Read successful - entity expansion!
    Document aDoc = DOMReader.readXMLDOM (sXMLEntities + "<root>&e4;</root>", aDRS);
    assertNotNull (aDoc);
    assertEquals (Strings.getRepeated ("value", (int) Math.pow (10, 3)), aDoc.getDocumentElement ().getTextContent ());

    if (!bIsJava24Plus)
    {
      // Read successful - entity expansion!
      aDoc = DOMReader.readXMLDOM (sXMLEntities + "<root>&e5;</root>", aDRS);
      assertNotNull (aDoc);
      assertEquals (Strings.getRepeated ("value", (int) Math.pow (10, 4)),
                    aDoc.getDocumentElement ().getTextContent ());
    }

    // Should fail because too many entity expansions
    LOGGER.info ("Current XML Entity Expansion Limit is " + XMLSystemProperties.getXMLEntityExpansionLimit ());
    final CollectingSAXErrorHandler aCEH = new CollectingSAXErrorHandler ();
    assertNull (DOMReader.readXMLDOM (sXMLEntities + "<root>&e6;</root>", aDRS.getClone ().setErrorHandler (aCEH)));
    assertEquals (1, aCEH.getErrorList ().size ());
    assertTrue (aCEH.getErrorList ()
                    .getFirstOrNull ()
                    .getErrorText (Locale.ROOT)
                    .contains (Integer.toString (XMLSystemProperties.getXMLEntityExpansionLimit ())));

    XMLSystemProperties.setXMLEntityExpansionLimit (111_111);
    XMLSystemProperties.setXMLTotalEntitySizeLimit (111_111 * 5);
    try
    {
      // Use default DOMReaderSettings
      assertNotNull (DOMReader.readXMLDOM (sXMLEntities + "<root>&e6;</root>", aDRS));

      // Set directly in settings
      aDoc = DOMReader.readXMLDOM (sXMLEntities + "<root>&e6;</root>",
                                   new DOMReaderSettings ().setFeatureValue (EXMLParserFeature.DISALLOW_DOCTYPE_DECL,
                                                                             false));
      assertNotNull (aDoc);
      final int nCount = (int) Math.pow (10, 5);
      assertEquals ("value".length () * nCount, aDoc.getDocumentElement ().getTextContent ().length ());
      assertEquals (Strings.getRepeated ("value", nCount), aDoc.getDocumentElement ().getTextContent ());

      // Less logging from here on
      aDRS.setErrorHandler (new DoNothingSAXErrorHandler ()).exceptionCallbacks ().removeAll ();

      // 1 expansion missing
      XMLSystemProperties.setXMLEntityExpansionLimit (1111 - 1);
      assertNull (DOMReader.readXMLDOM (sXMLEntities + "<root>&e4;</root>", aDRS));
      XMLSystemProperties.setXMLEntityExpansionLimit (1111);
      assertNotNull (DOMReader.readXMLDOM (sXMLEntities + "<root>&e4;</root>", aDRS));

      // 1 expansion missing
      XMLSystemProperties.setXMLEntityExpansionLimit (11111 - 1);
      assertNull (DOMReader.readXMLDOM (sXMLEntities + "<root>&e5;</root>", aDRS));
      XMLSystemProperties.setXMLEntityExpansionLimit (11111);
      assertNotNull (DOMReader.readXMLDOM (sXMLEntities + "<root>&e5;</root>", aDRS));

      // 1 expansion missing
      XMLSystemProperties.setXMLEntityExpansionLimit (111111 - 1);
      assertNull (DOMReader.readXMLDOM (sXMLEntities + "<root>&e6;</root>", aDRS));
      XMLSystemProperties.setXMLEntityExpansionLimit (111111);
      assertNotNull (DOMReader.readXMLDOM (sXMLEntities + "<root>&e6;</root>", aDRS));
    }
    finally
    {
      XMLSystemProperties.setXMLEntityExpansionLimit (null);
      XMLSystemProperties.setXMLTotalEntitySizeLimit (null);
    }
  }
}
