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
package com.helger.commons.microdom.serialize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;

import org.junit.Test;
import org.xml.sax.InputSource;

import com.helger.commons.charset.CCharset;
import com.helger.commons.charset.CharsetManager;
import com.helger.commons.io.IHasInputStream;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.io.stream.NonBlockingByteArrayInputStream;
import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;
import com.helger.commons.io.stream.NonBlockingStringReader;
import com.helger.commons.io.streamprovider.StringInputStreamProvider;
import com.helger.commons.microdom.IMicroDocument;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.system.ENewLineMode;
import com.helger.commons.xml.CXML;
import com.helger.commons.xml.namespace.MapBasedNamespaceContext;
import com.helger.commons.xml.sax.EmptyEntityResolver;
import com.helger.commons.xml.sax.InputSourceFactory;
import com.helger.commons.xml.sax.LoggingSAXErrorHandler;
import com.helger.commons.xml.sax.StringSAXInputSource;
import com.helger.commons.xml.serialize.read.ISAXReaderSettings;
import com.helger.commons.xml.serialize.read.SAXReader;
import com.helger.commons.xml.serialize.read.SAXReaderSettings;
import com.helger.commons.xml.serialize.write.EXMLSerializeIndent;
import com.helger.commons.xml.serialize.write.XMLWriterSettings;

/**
 * Test class for class {@link MicroReader}.
 *
 * @author Philip Helger
 */
public final class MicroReaderTest
{
  private static final String CRLF = ENewLineMode.DEFAULT.getText ();
  private static final String INDENT = XMLWriterSettings.DEFAULT_INDENTATION_STRING;

  @Test
  public void testNull ()
  {
    assertNull (MicroReader.readMicroXML ((InputSource) null));
    assertNull (MicroReader.readMicroXML ((InputStream) null));
    assertNull (MicroReader.readMicroXML ((IReadableResource) null));
    assertNull (MicroReader.readMicroXML ((IHasInputStream) null));
    assertNull (MicroReader.readMicroXML ((Reader) null));
    assertNull (MicroReader.readMicroXML ((String) null));
    assertNull (MicroReader.readMicroXML ((CharSequence) null));
    assertNull (MicroReader.readMicroXML ((ByteBuffer) null));
    assertNull (MicroReader.readMicroXML ((byte []) null));
  }

  @Test
  public void testSimple ()
  {
    final String s = "<?xml version=\"1.0\"?>" +
                     "<verrryoot>" +
                     "<root xmlns=\"myuri\">" +
                     "<child xmlns=\"\">" +
                     "<a:child2 xmlns:a=\"foo\">Value text - no entities!</a:child2>" +
                     "</child>" +
                     "</root>" +
                     "</verrryoot>";
    IMicroDocument aDoc = MicroReader.readMicroXML (s);
    assertNotNull (aDoc);

    aDoc = MicroReader.readMicroXML (new StringSAXInputSource (s));
    assertNotNull (aDoc);

    aDoc = MicroReader.readMicroXML (new NonBlockingStringReader (s));
    assertNotNull (aDoc);

    aDoc = MicroReader.readMicroXML (new StringInputStreamProvider (s, CCharset.CHARSET_ISO_8859_1_OBJ));
    assertNotNull (aDoc);

    aDoc = MicroReader.readMicroXML (new NonBlockingByteArrayInputStream (CharsetManager.getAsBytes (s,
                                                                                                     CCharset.CHARSET_ISO_8859_1_OBJ)));
    assertNotNull (aDoc);

    aDoc = MicroReader.readMicroXML (s, new SAXReaderSettings ().setErrorHandler (new LoggingSAXErrorHandler ()));
    assertNotNull (aDoc);

    final NonBlockingByteArrayOutputStream baos = new NonBlockingByteArrayOutputStream ();
    new MicroSerializer ().write (aDoc, baos);
    assertEquals ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                  CRLF +
                  "<verrryoot>" +
                  CRLF +
                  INDENT +
                  "<root xmlns=\"myuri\">" +
                  CRLF +
                  INDENT +
                  INDENT +
                  "<ns0:child xmlns:ns0=\"\">" +
                  CRLF +
                  INDENT +
                  INDENT +
                  INDENT +
                  "<ns1:child2 xmlns:ns1=\"foo\">Value text - no entities!</ns1:child2>" +
                  CRLF +
                  INDENT +
                  INDENT +
                  "</ns0:child>" +
                  CRLF +
                  INDENT +
                  "</root>" +
                  CRLF +
                  "</verrryoot>" +
                  CRLF,
                  baos.getAsString (CCharset.CHARSET_UTF_8_OBJ));

    final String sXHTML = "<content>" +
                          "<div class=\"css1\">" +
                          "<span class=\"css2\">" +
                          "<span>Text1 " +
                          "<span>Text1b</span>" +
                          "</span>" +
                          " " +
                          "<span>Text1c</span>" +
                          "<span class=\"css3\">" +
                          "<span>Text2</span>" +
                          "</span>" +
                          "</span>" +
                          "</div>" +
                          "</content>";
    final IMicroDocument docXHTML = MicroReader.readMicroXML (new NonBlockingStringReader (sXHTML));
    assertNotNull (docXHTML);
    final String sResult = MicroWriter.getNodeAsString (docXHTML,
                                                        new XMLWriterSettings ().setIndent (EXMLSerializeIndent.NONE));

    assertEquals ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                  "<content>" +
                  "<div class=\"css1\">" +
                  "<span class=\"css2\"><span>Text1 <span>Text1b</span></span> <span>Text1c</span>" +
                  "<span class=\"css3\"><span>Text2</span></span>" +
                  "</span>" +
                  "</div>" +
                  "</content>",
                  sResult);
  }

  /**
   * Test: use namespaces all over the place and mix them quite complex
   */
  @Test
  public void testNamespaces ()
  {
    final XMLWriterSettings xs = new XMLWriterSettings ();
    xs.setIndent (EXMLSerializeIndent.NONE);

    final String s = "<?xml version=\"1.0\"?>" +
                     "<verrryoot>" +
                     "<root xmlns=\"myuri\" xmlns:a='foo'>" +
                     "<child xmlns=\"\">" +
                     "<a:child2>Value text - no entities!</a:child2>" +
                     "</child>" +
                     "</root>" +
                     "</verrryoot>";
    final IMicroDocument aDoc = MicroReader.readMicroXML (s);
    assertNotNull (aDoc);

    final NonBlockingByteArrayOutputStream baos = new NonBlockingByteArrayOutputStream ();
    new MicroSerializer (xs).write (aDoc, baos);
    final String sXML = baos.getAsString (CCharset.CHARSET_UTF_8_OBJ);
    assertEquals ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                  "<verrryoot>" +
                  "<root xmlns=\"myuri\">" +
                  "<ns0:child xmlns:ns0=\"\">" +
                  "<ns1:child2 xmlns:ns1=\"foo\">Value text - no entities!</ns1:child2>" +
                  "</ns0:child>" +
                  "</root>" +
                  "</verrryoot>",
                  sXML);
  }

  /**
   * Test: Use 2 different namespaces and use them both more than once
   */
  @Test
  public void testNamespaces2 ()
  {
    final XMLWriterSettings xs = new XMLWriterSettings ();
    xs.setIndent (EXMLSerializeIndent.NONE);

    final String s = "<?xml version=\"1.0\"?>" +
                     "<verrryoot xmlns='uri1'>" +
                     "<root>" +
                     "<child xmlns='uri2'>" +
                     "<child2>Value text - no entities!</child2>" +
                     "</child>" +
                     "</root>" +
                     "</verrryoot>";
    final IMicroDocument aDoc = MicroReader.readMicroXML (s);
    assertNotNull (aDoc);

    final NonBlockingByteArrayOutputStream baos = new NonBlockingByteArrayOutputStream ();
    new MicroSerializer (xs).write (aDoc, baos);
    final String sXML = baos.getAsString (CCharset.CHARSET_UTF_8_OBJ);
    assertEquals ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                  "<verrryoot xmlns=\"uri1\">" +
                  "<root>" +
                  "<ns0:child xmlns:ns0=\"uri2\">" +
                  "<ns0:child2>Value text - no entities!</ns0:child2>" +
                  "</ns0:child>" +
                  "</root>" +
                  "</verrryoot>",
                  sXML);
  }

  /**
   * Test: declare all namespaces in the root element and use them in nested
   * elements
   */
  @Test
  public void testNamespaces3 ()
  {
    final XMLWriterSettings xs = new XMLWriterSettings ();
    xs.setIndent (EXMLSerializeIndent.NONE);
    xs.setUseDoubleQuotesForAttributes (false);

    final String s = "<?xml version=\"1.0\"?>" +
                     "<verrryoot xmlns='uri1' xmlns:a='uri2'>" +
                     "<root>" +
                     "<a:child>" +
                     "<a:child2>Value text - no entities!</a:child2>" +
                     "</a:child>" +
                     "</root>" +
                     "</verrryoot>";
    final IMicroDocument aDoc = MicroReader.readMicroXML (s);
    assertNotNull (aDoc);

    final NonBlockingByteArrayOutputStream baos = new NonBlockingByteArrayOutputStream ();
    new MicroSerializer (xs).write (aDoc, baos);
    final String sXML = baos.getAsString (CCharset.CHARSET_UTF_8_OBJ);
    assertEquals ("<?xml version='1.0' encoding='UTF-8'?>" +
                  "<verrryoot xmlns='uri1'>" +
                  "<root>" +
                  "<ns0:child xmlns:ns0='uri2'>" +
                  "<ns0:child2>Value text - no entities!</ns0:child2>" +
                  "</ns0:child>" +
                  "</root>" +
                  "</verrryoot>",
                  sXML);
  }

  /**
   * Test: same as namespace3 test but with a namespace context map
   */
  @Test
  public void testNamespaces3a ()
  {
    final XMLWriterSettings xs = new XMLWriterSettings ();
    xs.setIndent (EXMLSerializeIndent.NONE);
    xs.setUseDoubleQuotesForAttributes (false);
    xs.setNamespaceContext (new MapBasedNamespaceContext ().addMapping ("a1", "uri1").addMapping ("a2", "uri2"));

    final String s = "<?xml version=\"1.0\"?>" +
                     "<verrryoot xmlns='uri1' xmlns:a='uri2'>" +
                     "<root>" +
                     "<a:child>" +
                     "<a:child2>Value text - no entities!</a:child2>" +
                     "</a:child>" +
                     "</root>" +
                     "</verrryoot>";
    final IMicroDocument aDoc = MicroReader.readMicroXML (s);
    assertNotNull (aDoc);

    String sXML = MicroWriter.getNodeAsString (aDoc, xs);
    assertEquals ("<?xml version='1.0' encoding='UTF-8'?>" +
                  "<a1:verrryoot xmlns:a1='uri1'>" +
                  "<a1:root>" +
                  "<a2:child xmlns:a2='uri2'>" +
                  "<a2:child2>Value text - no entities!</a2:child2>" +
                  "</a2:child>" +
                  "</a1:root>" +
                  "</a1:verrryoot>",
                  sXML);

    xs.setPutNamespaceContextPrefixesInRoot (true);
    sXML = MicroWriter.getNodeAsString (aDoc, xs);
    assertEquals ("<?xml version='1.0' encoding='UTF-8'?>" +
                  "<a1:verrryoot xmlns:a1='uri1' xmlns:a2='uri2'>" +
                  "<a1:root>" +
                  "<a2:child>" +
                  "<a2:child2>Value text - no entities!</a2:child2>" +
                  "</a2:child>" +
                  "</a1:root>" +
                  "</a1:verrryoot>",
                  sXML);
  }

  @Test
  public void testReadNonExistingResource ()
  {
    assertNull (MicroReader.readMicroXML (new ClassPathResource ("does-not-exist.xml")));
    assertNull (MicroReader.readMicroXML ((IHasInputStream) new ClassPathResource ("does-not-exist.xml")));
  }

  @Test
  public void testReadProcessingInstruction ()
  {
    // Read file with processing instruction
    final IMicroDocument doc = MicroReader.readMicroXML (new ClassPathResource ("xml/xml-processing-instruction.xml"));
    assertNotNull (doc);

    // Write again
    assertNotNull (MicroWriter.getXMLString (doc));
  }

  @Test
  public void testReadNotation ()
  {
    // Read file with notation
    final IMicroDocument doc = MicroReader.readMicroXML (new ClassPathResource ("xml/xml-notation.xml"));
    assertNotNull (doc);

    // Write again
    assertNotNull (MicroWriter.getXMLString (doc));
  }

  @Test
  public void testReadEntity ()
  {
    // Read file with notation
    final IMicroDocument doc = MicroReader.readMicroXML (new ClassPathResource ("xml/xml-entity-public.xml"),
                                                         new SAXReaderSettings ().setEntityResolver (new EmptyEntityResolver ()));
    assertNotNull (doc);

    final MicroSAXHandler aHdl = new MicroSAXHandler (true, new EmptyEntityResolver ());
    final ISAXReaderSettings aSettings = new SAXReaderSettings ().setEntityResolver (aHdl)
                                                                 .setDTDHandler (aHdl)
                                                                 .setContentHandler (aHdl)
                                                                 .setErrorHandler (aHdl)
                                                                 .setLexicalHandler (aHdl);
    assertTrue (SAXReader.readXMLSAX (InputSourceFactory.create (ClassPathResource.getInputStream ("xml/xml-entity-public.xml")),
                                      aSettings)
                         .isSuccess ());
    assertNotNull (aHdl.getDocument ());

    // Write again
    assertNotNull (MicroWriter.getXMLString (doc));
  }

  @Test
  public void testRealInvalid ()
  {
    assertNull (MicroReader.readMicroXML ("not XML!"));
  }

  @Test
  public void testIsEqualContent ()
  {
    final String s = "<?xml version=\"1.1\"?>\n" +
                     "<!DOCTYPE root [ <!ENTITY sc \"value\"> ]>" +
                     "<root>" +
                     "<![CDATA[x<>]]>" +
                     INDENT +
                     "<l1>" +
                     INDENT +
                     INDENT +
                     "<l2>x</l2>" +
                     "text" +
                     "<b opt='true'><!--because who cares-->c</b>" +
                     "end" +
                     "&sc;" +
                     INDENT +
                     "</l1>" +
                     INDENT +
                     "<?important value?>" +
                     "</root>";
    assertTrue (MicroReader.readMicroXML (s).isEqualContent (MicroReader.readMicroXML (s)));
  }

  @Test
  public void testSpecialXMLAttrs ()
  {
    final String s = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                     "<root xml:lang=\"en\" xml:space=\"preserve\" xml:base=\"baseuri\" xml:id=\"4711\">" +
                     "Bla" +
                     "</root>";
    final IMicroDocument aDoc = MicroReader.readMicroXML (s);
    assertNotNull (aDoc);
    final IMicroElement eRoot = aDoc.getDocumentElement ();
    assertEquals ("en", eRoot.getAttributeValue (CXML.XML_NS_XML, "lang"));
    assertNull (eRoot.getAttributeValue ("lang"));
    assertEquals ("preserve", eRoot.getAttributeValue (CXML.XML_NS_XML, "space"));
    assertEquals ("baseuri", eRoot.getAttributeValue (CXML.XML_NS_XML, "base"));
    assertEquals ("4711", eRoot.getAttributeValue (CXML.XML_NS_XML, "id"));

    // Ensure they are written as well
    assertEquals (s, MicroWriter.getNodeAsString (aDoc, new XMLWriterSettings ().setIndent (EXMLSerializeIndent.NONE)));
  }
}
