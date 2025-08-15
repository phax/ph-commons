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
package com.helger.xml.serialize.write;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.charset.StandardCharsets;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.base.system.ENewLineMode;
import com.helger.commons.mock.CommonsTestHelper;
import com.helger.xml.EXMLParserFeature;
import com.helger.xml.EXMLVersion;
import com.helger.xml.XMLFactory;
import com.helger.xml.namespace.MapBasedNamespaceContext;
import com.helger.xml.sax.StringSAXInputSource;
import com.helger.xml.serialize.read.DOMReader;
import com.helger.xml.serialize.read.DOMReaderSettings;
import com.helger.xml.transform.StringStreamResult;
import com.helger.xml.transform.XMLTransformerFactory;

/**
 * Test class for {@link XMLWriter}
 *
 * @author Philip Helger
 */
public final class XMLWriterTest
{
  private static final String DOCTYPE_XHTML10_QNAME = "-//W3C//DTD XHTML 1.0 Strict//EN";
  private static final String DOCTYPE_XHTML10_URI = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd";
  private static final String CRLF = ENewLineMode.DEFAULT.getText ();

  @Test
  public void testGetXHTMLString ()
  {
    final String sSPACER = " ";
    final String sINDENT = XMLWriterSettings.DEFAULT_INDENTATION_STRING;
    final String sTAGNAME = "notext";

    // Java 1.6 JAXP handles things differently
    final String sSerTagName = "<" + sTAGNAME + "></" + sTAGNAME + ">";

    final Document doc = XMLFactory.newDocument ("html", DOCTYPE_XHTML10_QNAME, DOCTYPE_XHTML10_URI);
    final Element aHead = (Element) doc.getDocumentElement ()
                                       .appendChild (doc.createElementNS (DOCTYPE_XHTML10_URI, "head"));
    aHead.appendChild (doc.createTextNode ("Hallo"));
    final Element aNoText = (Element) doc.getDocumentElement ()
                                         .appendChild (doc.createElementNS (DOCTYPE_XHTML10_URI, sTAGNAME));
    aNoText.appendChild (doc.createTextNode (""));

    // test including doc type
    {
      final String sResult = XMLWriter.getNodeAsString (doc, XMLWriterSettings.createForXHTML ());
      assertEquals ("<!DOCTYPE html PUBLIC \"" +
                    DOCTYPE_XHTML10_QNAME +
                    "\"" +
                    sSPACER +
                    "\"" +
                    DOCTYPE_XHTML10_URI +
                    "\">" +
                    CRLF +
                    "<html xmlns=\"" +
                    DOCTYPE_XHTML10_URI +
                    "\">" +
                    CRLF +
                    sINDENT +
                    "<head>Hallo</head>" +
                    CRLF +
                    sINDENT +
                    sSerTagName +
                    CRLF +
                    "</html>" +
                    CRLF,
                    sResult);
      assertEquals (sResult, XMLWriter.getNodeAsString (doc, XMLWriterSettings.createForXHTML ()));
    }

    // test without doc type
    {
      final String sResult = XMLWriter.getNodeAsString (doc,
                                                        XMLWriterSettings.createForXHTML ()
                                                                         .setSerializeDocType (EXMLSerializeDocType.IGNORE));
      assertEquals ("<html xmlns=\"" +
                    DOCTYPE_XHTML10_URI +
                    "\">" +
                    CRLF +
                    sINDENT +
                    "<head>Hallo</head>" +
                    CRLF +
                    sINDENT +
                    sSerTagName +
                    CRLF +
                    "</html>" +
                    CRLF,
                    sResult);
    }

    {
      final String sResult = XMLWriter.getNodeAsString (doc,
                                                        XMLWriterSettings.createForXHTML ()
                                                                         .setSerializeDocType (EXMLSerializeDocType.IGNORE)
                                                                         .setIndent (EXMLSerializeIndent.NONE));
      assertEquals ("<html xmlns=\"" + DOCTYPE_XHTML10_URI + "\"><head>Hallo</head>" + sSerTagName + "</html>",
                    sResult);
      assertEquals (sResult,
                    XMLWriter.getNodeAsString (doc,
                                               XMLWriterSettings.createForXHTML ()
                                                                .setSerializeDocType (EXMLSerializeDocType.IGNORE)
                                                                .setIndent (EXMLSerializeIndent.NONE)));
    }

    // add text element
    aNoText.appendChild (doc.createTextNode ("Hallo "));
    final Element b = (Element) aNoText.appendChild (doc.createElementNS (DOCTYPE_XHTML10_URI, "strong"));
    b.appendChild (doc.createTextNode ("Welt"));
    aNoText.appendChild (doc.createCDATASection ("!!!"));
    aNoText.appendChild (doc.createComment ("No"));

    // test without doc type
    {
      final String sResult = XMLWriter.getNodeAsString (doc,
                                                        XMLWriterSettings.createForXHTML ()
                                                                         .setSerializeDocType (EXMLSerializeDocType.IGNORE)
                                                                         .setIndent (EXMLSerializeIndent.INDENT_AND_ALIGN));
      assertEquals ("<html xmlns=\"" +
                    DOCTYPE_XHTML10_URI +
                    "\">" +
                    CRLF +
                    sINDENT +
                    "<head>Hallo</head>" +
                    CRLF +
                    sINDENT +
                    "<notext>Hallo <strong>Welt</strong><![CDATA[!!!]]><!--No--></notext>" +
                    CRLF +
                    "</html>" +
                    CRLF,
                    sResult);
    }

    // test as XML (with doc type and indent)
    {
      final String sResult = XMLWriter.getNodeAsString (doc);
      assertEquals ("<?xml version=\"1.0\" encoding=\"" +
                    StandardCharsets.UTF_8.name () +
                    "\" standalone=\"no\"?>" +
                    CRLF +
                    "<!DOCTYPE html PUBLIC \"" +
                    DOCTYPE_XHTML10_QNAME +
                    "\"" +
                    sSPACER +
                    "\"" +
                    DOCTYPE_XHTML10_URI +
                    "\">" +
                    CRLF +
                    "<html xmlns=\"" +
                    DOCTYPE_XHTML10_URI +
                    "\">" +
                    CRLF +
                    sINDENT +
                    "<head>Hallo</head>" +
                    CRLF +
                    sINDENT +
                    "<notext>Hallo <strong>Welt</strong><![CDATA[!!!]]><!--No--></notext>" +
                    CRLF +
                    "</html>" +
                    CRLF,
                    sResult);
    }

    // test as XML (without doc type and comments but indented)
    {
      final String sResult = XMLWriter.getNodeAsString (doc,
                                                        new XMLWriterSettings ().setSerializeDocType (EXMLSerializeDocType.IGNORE)
                                                                                .setSerializeComments (EXMLSerializeComments.IGNORE)
                                                                                .setIndent (EXMLSerializeIndent.INDENT_AND_ALIGN));
      assertEquals ("<?xml version=\"1.0\" encoding=\"" +
                    StandardCharsets.UTF_8.name () +
                    "\" standalone=\"no\"?>" +
                    CRLF +
                    "<html xmlns=\"" +
                    DOCTYPE_XHTML10_URI +
                    "\">" +
                    CRLF +
                    sINDENT +
                    "<head>Hallo</head>" +
                    CRLF +
                    sINDENT +
                    "<notext>Hallo <strong>Welt</strong><![CDATA[!!!]]></notext>" +
                    CRLF +
                    "</html>" +
                    CRLF,
                    sResult);
    }

    // test as XML (without doc type and comments but indented) with different
    // newline String
    {
      final String sResult = XMLWriter.getNodeAsString (doc,
                                                        new XMLWriterSettings ().setSerializeDocType (EXMLSerializeDocType.IGNORE)
                                                                                .setSerializeComments (EXMLSerializeComments.IGNORE)
                                                                                .setIndent (EXMLSerializeIndent.INDENT_AND_ALIGN)
                                                                                .setNewLineMode (ENewLineMode.UNIX));
      assertEquals ("<?xml version=\"1.0\" encoding=\"" +
                    StandardCharsets.UTF_8.name () +
                    "\" standalone=\"no\"?>\n" +
                    "<html xmlns=\"" +
                    DOCTYPE_XHTML10_URI +
                    "\">\n" +
                    sINDENT +
                    "<head>Hallo</head>\n" +
                    sINDENT +
                    "<notext>Hallo <strong>Welt</strong><![CDATA[!!!]]></notext>\n" +
                    "</html>\n",
                    sResult);
    }

    // test as XML (without doc type and comments but indented) with different
    // newline String and different indent
    {
      final String sResult = XMLWriter.getNodeAsString (doc,
                                                        new XMLWriterSettings ().setSerializeDocType (EXMLSerializeDocType.IGNORE)
                                                                                .setSerializeComments (EXMLSerializeComments.IGNORE)
                                                                                .setIndent (EXMLSerializeIndent.INDENT_AND_ALIGN)
                                                                                .setNewLineMode (ENewLineMode.UNIX)
                                                                                .setIndentationString ("\t"));
      assertEquals ("<?xml version=\"1.0\" encoding=\"" +
                    StandardCharsets.UTF_8.name () +
                    "\" standalone=\"no\"?>\n" +
                    "<html xmlns=\"" +
                    DOCTYPE_XHTML10_URI +
                    "\">\n" +
                    "\t<head>Hallo</head>\n" +
                    "\t<notext>Hallo <strong>Welt</strong><![CDATA[!!!]]></notext>\n" +
                    "</html>\n",
                    sResult);
    }

    assertTrue (XMLWriter.writeToStream (doc, new NonBlockingByteArrayOutputStream ()).isSuccess ());
  }

  @Test
  public void testWriteXMLMultiThreaded ()
  {
    final String sSPACER = " ";
    final String sINDENT = XMLWriterSettings.DEFAULT_INDENTATION_STRING;
    final String sTAGNAME = "notext";

    CommonsTestHelper.testInParallel (1000, () -> {
      // Java 1.6 JAXP handles things differently
      final String sSerTagName = "<" + sTAGNAME + "></" + sTAGNAME + ">";

      final Document doc = XMLFactory.newDocument ("html", DOCTYPE_XHTML10_QNAME, DOCTYPE_XHTML10_URI);
      final Element aHead = (Element) doc.getDocumentElement ()
                                         .appendChild (doc.createElementNS (DOCTYPE_XHTML10_URI, "head"));
      aHead.appendChild (doc.createTextNode ("Hallo"));
      final Element aNoText = (Element) doc.getDocumentElement ()
                                           .appendChild (doc.createElementNS (DOCTYPE_XHTML10_URI, sTAGNAME));
      aNoText.appendChild (doc.createTextNode (""));

      // test including doc type
      final String sResult = XMLWriter.getNodeAsString (doc, XMLWriterSettings.createForXHTML ());
      assertEquals ("<!DOCTYPE html PUBLIC \"" +
                    DOCTYPE_XHTML10_QNAME +
                    "\"" +
                    sSPACER +
                    "\"" +
                    DOCTYPE_XHTML10_URI +
                    "\">" +
                    CRLF +
                    "<html xmlns=\"" +
                    DOCTYPE_XHTML10_URI +
                    "\">" +
                    CRLF +
                    sINDENT +
                    "<head>Hallo</head>" +
                    CRLF +
                    sINDENT +
                    sSerTagName +
                    CRLF +
                    "</html>" +
                    CRLF,
                    sResult);
      assertEquals (sResult, XMLWriter.getNodeAsString (doc, XMLWriterSettings.createForXHTML ()));
    });
  }

  @Test
  public void testNestedCDATAs ()
  {
    final Document doc = XMLFactory.newDocument ();

    // Containing the forbidden CDATA end marker
    Element e = doc.createElement ("a");
    e.appendChild (doc.createCDATASection ("a]]>b"));
    assertEquals ("<a><![CDATA[a]]]]><![CDATA[>b]]></a>" + CRLF, XMLWriter.getNodeAsString (e));

    // Containing more than one forbidden CDATA end marker
    e = doc.createElement ("a");
    e.appendChild (doc.createCDATASection ("a]]>b]]>c"));
    assertEquals ("<a><![CDATA[a]]]]><![CDATA[>b]]]]><![CDATA[>c]]></a>" + CRLF, XMLWriter.getNodeAsString (e));

    // Containing a complete CDATA section
    e = doc.createElement ("a");
    e.appendChild (doc.createCDATASection ("a<![CDATA[x]]>b"));
    assertEquals ("<a><![CDATA[a<![CDATA[x]]]]><![CDATA[>b]]></a>" + CRLF, XMLWriter.getNodeAsString (e));
  }

  @Test
  public void testWriteCDATAAsText ()
  {
    final Document doc = XMLFactory.newDocument ();
    final XMLWriterSettings aXWS = new XMLWriterSettings ().setWriteCDATAAsText (true);

    // Containing the forbidden CDATA end marker
    Element e = doc.createElement ("a");
    e.appendChild (doc.createCDATASection ("a]]>b"));
    assertEquals ("<a>a]]&gt;b</a>" + CRLF, XMLWriter.getNodeAsString (e, aXWS));

    // Containing more than one forbidden CDATA end marker
    e = doc.createElement ("a");
    e.appendChild (doc.createCDATASection ("a]]>b]]>c"));
    assertEquals ("<a>a]]&gt;b]]&gt;c</a>" + CRLF, XMLWriter.getNodeAsString (e, aXWS));

    // Containing a complete CDATA section
    e = doc.createElement ("a");
    e.appendChild (doc.createCDATASection ("a<![CDATA[x]]>b"));
    assertEquals ("<a>a&lt;![CDATA[x]]&gt;b</a>" + CRLF, XMLWriter.getNodeAsString (e, aXWS));
  }

  @Test
  public void testWithNamespaceContext ()
  {
    final Document aDoc = XMLFactory.newDocument ();
    final Element eRoot = (Element) aDoc.appendChild (aDoc.createElementNS ("ns1url", "root"));
    eRoot.appendChild (aDoc.createElementNS ("ns2url", "child1"));
    eRoot.appendChild (aDoc.createElementNS ("ns2url", "child2"));

    final XMLWriterSettings aSettings = new XMLWriterSettings ().setCharset (StandardCharsets.ISO_8859_1)
                                                                .setIndent (EXMLSerializeIndent.NONE);
    String s = XMLWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>" +
                  "<root xmlns=\"ns1url\">" +
                  "<ns0:child1 xmlns:ns0=\"ns2url\" />" +
                  "<ns0:child2 xmlns:ns0=\"ns2url\" />" +
                  "</root>",
                  s);

    final MapBasedNamespaceContext aCtx = new MapBasedNamespaceContext ();
    aCtx.addMapping ("a", "ns1url");
    aSettings.setNamespaceContext (aCtx);
    s = XMLWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>" +
                  "<a:root xmlns:a=\"ns1url\">" +
                  "<ns0:child1 xmlns:ns0=\"ns2url\" />" +
                  "<ns0:child2 xmlns:ns0=\"ns2url\" />" +
                  "</a:root>",
                  s);

    aCtx.addMapping ("xy", "ns2url");
    s = XMLWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>" +
                  "<a:root xmlns:a=\"ns1url\">" +
                  "<xy:child1 xmlns:xy=\"ns2url\" />" +
                  "<xy:child2 xmlns:xy=\"ns2url\" />" +
                  "</a:root>",
                  s);

    aSettings.setUseDoubleQuotesForAttributes (false);
    s = XMLWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version='1.0' encoding='ISO-8859-1' standalone='no'?>" +
                  "<a:root xmlns:a='ns1url'>" +
                  "<xy:child1 xmlns:xy='ns2url' />" +
                  "<xy:child2 xmlns:xy='ns2url' />" +
                  "</a:root>",
                  s);

    aSettings.setSpaceOnSelfClosedElement (false);
    s = XMLWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version='1.0' encoding='ISO-8859-1' standalone='no'?>" +
                  "<a:root xmlns:a='ns1url'>" +
                  "<xy:child1 xmlns:xy='ns2url'/>" +
                  "<xy:child2 xmlns:xy='ns2url'/>" +
                  "</a:root>",
                  s);

    aSettings.setPutNamespaceContextPrefixesInRoot (true);
    s = XMLWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version='1.0' encoding='ISO-8859-1' standalone='no'?>" +
                  "<a:root xmlns:a='ns1url' xmlns:xy='ns2url'>" +
                  "<xy:child1/>" +
                  "<xy:child2/>" +
                  "</a:root>",
                  s);

    eRoot.appendChild (aDoc.createElementNS ("ns3url", "zz"));
    s = XMLWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version='1.0' encoding='ISO-8859-1' standalone='no'?>" +
                  "<a:root xmlns:a='ns1url' xmlns:xy='ns2url'>" +
                  "<xy:child1/>" +
                  "<xy:child2/>" +
                  "<ns0:zz xmlns:ns0='ns3url'/>" +
                  "</a:root>",
                  s);
  }

  @Test
  public void testWithoutEmitNamespaces ()
  {
    final Document aDoc = XMLFactory.newDocument ();
    final Element eRoot = (Element) aDoc.appendChild (aDoc.createElementNS ("ns1url", "root"));
    eRoot.appendChild (aDoc.createElementNS ("ns2url", "child1"));
    eRoot.appendChild (aDoc.createElementNS ("ns2url", "child2"));

    final XMLWriterSettings aSettings = new XMLWriterSettings ().setCharset (StandardCharsets.ISO_8859_1)
                                                                .setIndent (EXMLSerializeIndent.NONE);
    String s = XMLWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>" +
                  "<root xmlns=\"ns1url\">" +
                  "<ns0:child1 xmlns:ns0=\"ns2url\" />" +
                  "<ns0:child2 xmlns:ns0=\"ns2url\" />" +
                  "</root>",
                  s);

    aSettings.setEmitNamespaces (false);
    s = XMLWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>" +
                  "<root>" +
                  "<child1 />" +
                  "<child2 />" +
                  "</root>",
                  s);

    aSettings.setPutNamespaceContextPrefixesInRoot (true);
    s = XMLWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>" +
                  "<root>" +
                  "<child1 />" +
                  "<child2 />" +
                  "</root>",
                  s);
  }

  @Test
  public void testXMLVersionNumber ()
  {
    Document aDoc = XMLFactory.newDocument (EXMLVersion.XML_10);
    aDoc.appendChild (aDoc.createElement ("any"));
    String sXML = XMLWriter.getNodeAsString (aDoc);
    assertEquals ("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" + CRLF + "<any />" + CRLF, sXML);

    aDoc = XMLFactory.newDocument (EXMLVersion.XML_11);
    aDoc.appendChild (aDoc.createElement ("any"));
    sXML = XMLWriter.getNodeAsString (aDoc);
    assertEquals ("<?xml version=\"1.1\" encoding=\"UTF-8\" standalone=\"no\"?>" + CRLF + "<any />" + CRLF, sXML);
  }

  @Test
  public void testNumericReferencesXML10 () throws TransformerException
  {
    // Use regular transformer
    final Transformer aTransformer = XMLTransformerFactory.newTransformer ();
    aTransformer.setOutputProperty (OutputKeys.ENCODING, StandardCharsets.UTF_8.name ());
    aTransformer.setOutputProperty (OutputKeys.INDENT, "yes");
    aTransformer.setOutputProperty (OutputKeys.VERSION, EXMLVersion.XML_10.getVersion ());

    // Must be int to avoid endless loop
    for (int c = Character.MIN_VALUE; c <= Character.MAX_VALUE; ++c)
      if (!XMLCharHelper.isInvalidXMLTextChar (EXMLSerializeVersion.XML_10, c) &&
          !Character.isHighSurrogate ((char) c) &&
          !Character.isLowSurrogate ((char) c))
      {
        final String sText = "abc" + ((char) c) + "def";
        final Document aDoc = XMLFactory.newDocument (EXMLVersion.XML_10);
        final Element eRoot = (Element) aDoc.appendChild (aDoc.createElement ("root"));
        eRoot.appendChild (aDoc.createTextNode (sText));

        final StringStreamResult aRes = new StringStreamResult ();
        aTransformer.transform (new DOMSource (aDoc), aRes);

        final String sXML = aRes.getAsString ();
        final Document aDoc2 = DOMReader.readXMLDOM (sXML);
        assertNotNull (aDoc2);
      }
  }

  @Test
  public void testNumericReferencesXML11 () throws TransformerException
  {
    final Transformer aTransformer = XMLTransformerFactory.newTransformer ();
    aTransformer.setOutputProperty (OutputKeys.ENCODING, StandardCharsets.UTF_8.name ());
    aTransformer.setOutputProperty (OutputKeys.INDENT, "no");
    aTransformer.setOutputProperty (OutputKeys.VERSION, EXMLVersion.XML_11.getVersion ());

    // Must be int to avoid endless loop
    for (int c = Character.MIN_VALUE; c <= Character.MAX_VALUE; ++c)
      if (!XMLCharHelper.isInvalidXMLTextChar (EXMLSerializeVersion.XML_11, c) &&
          !Character.isHighSurrogate ((char) c) &&
          !Character.isLowSurrogate ((char) c))
      {
        final String sText = "abc" + ((char) c) + "def";
        final Document aDoc = XMLFactory.newDocument (EXMLVersion.XML_11);
        final Element eRoot = (Element) aDoc.appendChild (aDoc.createElement ("root"));
        eRoot.appendChild (aDoc.createTextNode (sText));

        final StringStreamResult aRes = new StringStreamResult ();
        aTransformer.transform (new DOMSource (aDoc), aRes);

        final String sXML = aRes.getAsString ();
        final Document aDoc2 = DOMReader.readXMLDOM (sXML);
        assertNotNull (aDoc2);
      }
  }

  @Test
  public void testAttributesWithNamespaces ()
  {
    final XMLWriterSettings aSettings = new XMLWriterSettings ().setIndent (EXMLSerializeIndent.NONE)
                                                                .setCharset (StandardCharsets.ISO_8859_1);
    final Document aDoc = XMLFactory.newDocument ();
    final Element eRoot = (Element) aDoc.appendChild (aDoc.createElementNS ("ns1url", "root"));
    final Element e1 = (Element) eRoot.appendChild (aDoc.createElementNS ("ns2url", "child1"));
    e1.setAttributeNS ("ns2url", "attr1", "value1");
    final Element e2 = (Element) eRoot.appendChild (aDoc.createElementNS ("ns2url", "child2"));
    e2.setAttributeNS ("ns3url", "attr2", "value2");

    String s = XMLWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>" +
                  "<root xmlns=\"ns1url\">" +
                  "<ns0:child1 xmlns:ns0=\"ns2url\" ns0:attr1=\"value1\" />" +
                  "<ns0:child2 xmlns:ns0=\"ns2url\" xmlns:ns1=\"ns3url\" ns1:attr2=\"value2\" />" +
                  "</root>",
                  s);
    assertEquals (s, XMLWriter.getNodeAsString (DOMReader.readXMLDOM (s), aSettings));

    aSettings.setEmitNamespaces (false);
    s = XMLWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>" +
                  "<root>" +
                  "<child1 attr1=\"value1\" />" +
                  "<child2 attr2=\"value2\" />" +
                  "</root>",
                  s);
  }

  @Test
  public void testHTML4BracketMode ()
  {
    final String sINDENT = XMLWriterSettings.DEFAULT_INDENTATION_STRING;

    final Document aDoc = XMLFactory.newDocument ("html", DOCTYPE_XHTML10_QNAME, DOCTYPE_XHTML10_URI);
    final Element aHead = (Element) aDoc.getDocumentElement ()
                                        .appendChild (aDoc.createElementNS (DOCTYPE_XHTML10_URI, "head"));
    aHead.appendChild (aDoc.createTextNode ("Hallo"));
    final Element aBody = (Element) aDoc.getDocumentElement ()
                                        .appendChild (aDoc.createElementNS (DOCTYPE_XHTML10_URI, "body"));
    aBody.appendChild (aDoc.createElementNS (DOCTYPE_XHTML10_URI, "img"));

    // test including doc type
    final String sResult = XMLWriter.getNodeAsString (aDoc, XMLWriterSettings.createForHTML4 ());
    assertEquals ("<!DOCTYPE html PUBLIC \"" +
                  DOCTYPE_XHTML10_QNAME +
                  "\" \"" +
                  DOCTYPE_XHTML10_URI +
                  "\">" +
                  CRLF +
                  "<html xmlns=\"" +
                  DOCTYPE_XHTML10_URI +
                  "\">" +
                  CRLF +
                  sINDENT +
                  "<head>Hallo</head>" +
                  CRLF +
                  sINDENT +
                  "<body>" +
                  CRLF +
                  sINDENT +
                  sINDENT +
                  // Unclosed img :)
                  "<img>" +
                  CRLF +
                  sINDENT +
                  "</body>" +
                  CRLF +
                  "</html>" +
                  CRLF,
                  sResult);
  }

  @Test
  public void testXHTMLBracketMode ()
  {
    final String sINDENT = XMLWriterSettings.DEFAULT_INDENTATION_STRING;

    final Document aDoc = XMLFactory.newDocument ("html", DOCTYPE_XHTML10_QNAME, DOCTYPE_XHTML10_URI);
    final Element aHead = (Element) aDoc.getDocumentElement ()
                                        .appendChild (aDoc.createElementNS (DOCTYPE_XHTML10_URI, "head"));
    aHead.appendChild (aDoc.createTextNode ("Hallo"));
    final Element aBody = (Element) aDoc.getDocumentElement ()
                                        .appendChild (aDoc.createElementNS (DOCTYPE_XHTML10_URI, "body"));
    aBody.appendChild (aDoc.createElementNS (DOCTYPE_XHTML10_URI, "img"));

    // test including doc type
    final String sResult = XMLWriter.getNodeAsString (aDoc, XMLWriterSettings.createForXHTML ());
    assertEquals ("<!DOCTYPE html PUBLIC \"" +
                  DOCTYPE_XHTML10_QNAME +
                  "\" \"" +
                  DOCTYPE_XHTML10_URI +
                  "\">" +
                  CRLF +
                  "<html xmlns=\"" +
                  DOCTYPE_XHTML10_URI +
                  "\">" +
                  CRLF +
                  sINDENT +
                  "<head>Hallo</head>" +
                  CRLF +
                  sINDENT +
                  "<body>" +
                  CRLF +
                  sINDENT +
                  sINDENT +
                  // self closed tag
                  "<img />" +
                  CRLF +
                  sINDENT +
                  "</body>" +
                  CRLF +
                  "</html>" +
                  CRLF,
                  sResult);
  }

  @Test
  public void testXHTMLIndent ()
  {
    final XMLWriterSettings xs = XMLWriterSettings.createForXHTML ().setSerializeDocType (EXMLSerializeDocType.IGNORE);
    final String sINDENT = xs.getIndentationString ();

    final Document aDoc = XMLFactory.newDocument ("html", DOCTYPE_XHTML10_QNAME, DOCTYPE_XHTML10_URI);
    final Element aHead = (Element) aDoc.getDocumentElement ()
                                        .appendChild (aDoc.createElementNS (DOCTYPE_XHTML10_URI, "head"));
    aHead.appendChild (aDoc.createTextNode ("Hallo"));
    final Element aBody = (Element) aDoc.getDocumentElement ()
                                        .appendChild (aDoc.createElementNS (DOCTYPE_XHTML10_URI, "body"));
    final Element aPre = (Element) aBody.appendChild (aDoc.createElementNS (DOCTYPE_XHTML10_URI, "pre"));
    final Element aDiv = (Element) aPre.appendChild (aDoc.createElementNS (DOCTYPE_XHTML10_URI, "div"));
    aDiv.appendChild (aDoc.createTextNode ("pre formatted"));

    assertEquals ("<html xmlns=\"" +
                  DOCTYPE_XHTML10_URI +
                  "\">" +
                  CRLF +
                  sINDENT +
                  "<head>Hallo</head>" +
                  CRLF +
                  sINDENT +
                  "<body>" +
                  CRLF +
                  sINDENT +
                  sINDENT +
                  // pre not indented
                  "<pre><div>pre formatted</div></pre>" +
                  CRLF +
                  sINDENT +
                  "</body>" +
                  CRLF +
                  "</html>" +
                  CRLF,
                  XMLWriter.getNodeAsString (aDoc, xs));

    // Special handling with void element
    aPre.removeChild (aDiv);
    aPre.appendChild (aDoc.createElementNS (DOCTYPE_XHTML10_URI, "img"));

    assertEquals ("<html xmlns=\"" +
                  DOCTYPE_XHTML10_URI +
                  "\">" +
                  CRLF +
                  sINDENT +
                  "<head>Hallo</head>" +
                  CRLF +
                  sINDENT +
                  "<body>" +
                  CRLF +
                  sINDENT +
                  sINDENT +
                  // pre not indented
                  "<pre><img /></pre>" +
                  CRLF +
                  sINDENT +
                  "</body>" +
                  CRLF +
                  "</html>" +
                  CRLF,
                  XMLWriter.getNodeAsString (aDoc, xs));
  }

  @Test
  public void testOrderAttributes ()
  {
    XMLWriterSettings aSettings = new XMLWriterSettings ().setIndent (EXMLSerializeIndent.NONE)
                                                          .setUseDoubleQuotesForAttributes (false);

    // default order
    final Document aDoc = XMLFactory.newDocument ();
    final Element e = (Element) aDoc.appendChild (aDoc.createElement ("a"));
    e.setAttribute ("c", "1");
    e.setAttribute ("b", "2");
    e.setAttribute ("a", "3");
    // Attributes are ordered automatically in DOM!
    assertEquals ("<a a='3' b='2' c='1' />", XMLWriter.getNodeAsString (e, aSettings));

    aSettings = aSettings.setOrderAttributesAndNamespaces (true);
    assertEquals ("<a a='3' b='2' c='1' />", XMLWriter.getNodeAsString (e, aSettings));
  }

  @Test
  public void testOrderNamespaces ()
  {
    XMLWriterSettings aSettings = new XMLWriterSettings ().setIndent (EXMLSerializeIndent.NONE)
                                                          .setUseDoubleQuotesForAttributes (false);

    // default order
    final Document aDoc = XMLFactory.newDocument ();
    final Element e = (Element) aDoc.appendChild (aDoc.createElement ("a"));
    e.setAttributeNS ("urn:ns3", "c", "1");
    e.setAttributeNS ("urn:ns2", "b", "2");
    e.setAttributeNS ("urn:ns1", "a", "3");
    assertEquals ("<a xmlns='urn:ns1' a='3' xmlns:ns0='urn:ns2' ns0:b='2' xmlns:ns1='urn:ns3' ns1:c='1' />",
                  XMLWriter.getNodeAsString (e, aSettings));

    aSettings = aSettings.setOrderAttributesAndNamespaces (true);
    assertEquals ("<a xmlns='urn:ns1' xmlns:ns0='urn:ns2' xmlns:ns1='urn:ns3' a='3' ns0:b='2' ns1:c='1' />",
                  XMLWriter.getNodeAsString (e, aSettings));
  }

  private static void _testC14 (final String sSrc, final String sDst)
  {
    final Document aDoc = DOMReader.readXMLDOM (sSrc,
                                                new DOMReaderSettings ().setFeatureValue (EXMLParserFeature.DISALLOW_DOCTYPE_DECL,
                                                                                          false)
                                                                        .setFeatureValue (EXMLParserFeature.EXTERNAL_GENERAL_ENTITIES,
                                                                                          true)
                                                                        .setEntityResolver ( (x,
                                                                                              y) -> ("world.txt".equals (new File (y).getName ()) ? new StringSAXInputSource ("world")
                                                                                                                                                  : new StringSAXInputSource (""))));
    assertNotNull (aDoc);

    final MapBasedNamespaceContext aCtx = new MapBasedNamespaceContext ();
    aCtx.addMapping ("a", "http://www.w3.org");
    aCtx.addMapping ("b", "http://www.ietf.org");
    final String sC14 = XMLWriter.getNodeAsString (aDoc,
                                                   XMLWriterSettings.createForCanonicalization ()
                                                                    .setIndentationString ("   ")
                                                                    .setNamespaceContext (aCtx)
                                                                    .setSerializeComments (EXMLSerializeComments.IGNORE));
    assertEquals (sDst, sC14);
  }

  @Test
  public void testCanonicalization ()
  {
    _testC14 ("<?xml version=\"1.0\"?>\r\n" +
              "\r\n" +
              "<?xml-stylesheet   href=\"doc.xsl\"\r\n" +
              "   type=\"text/xsl\"   ?>\r\n" +
              "\r\n" +
              "<!DOCTYPE doc SYSTEM \"doc.dtd\">\r\n" +
              "\r\n" +
              "<doc>Hello, world!<!-- Comment 1 --></doc>\r\n" +
              "\r\n" +
              "<?pi-without-data     ?>\r\n" +
              "\r\n" +
              "<!-- Comment 2 -->\r\n" +
              "\r\n" +
              "<!-- Comment 3 -->",
              "<?xml-stylesheet href=\"doc.xsl\"\n" +
                                    "   type=\"text/xsl\"   ?>\n" +
                                    "<doc>Hello, world!</doc>\n" +
                                    "<?pi-without-data?>\n");
    _testC14 ("<!DOCTYPE doc [<!ATTLIST e9 attr CDATA \"default\">]>\r\n" +
              "<doc>\r\n" +
              "   <e1   />\r\n" +
              "   <e2   ></e2>\r\n" +
              "   <e3   name = \"elem3\"   id=\"elem3\"   />\r\n" +
              "   <e4   name=\"elem4\"   id=\"elem4\"   ></e4>\r\n" +
              "   <e5 a:attr=\"out\" b:attr=\"sorted\" attr2=\"all\" attr=\"I'm\"\r\n" +
              "      xmlns:b=\"http://www.ietf.org\"\r\n" +
              "      xmlns:a=\"http://www.w3.org\"\r\n" +
              "      xmlns=\"http://example.org\"/>\r\n" +
              "   <e6 xmlns=\"\" xmlns:a=\"http://www.w3.org\">\r\n" +
              "      <e7 xmlns=\"http://www.ietf.org\">\r\n" +
              "         <e8 xmlns=\"\" xmlns:a=\"http://www.w3.org\">\r\n" +
              "            <e9 xmlns=\"\" xmlns:a=\"http://www.ietf.org\"/>\r\n" +
              "         </e8>\r\n" +
              "      </e7>\r\n" +
              "   </e6>\r\n" +
              "</doc>",
              "<doc>\n" +
                        "   <e1></e1>\n" +
                        "   <e2></e2>\n" +
                        "   <e3 id=\"elem3\" name=\"elem3\"></e3>\n" +
                        "   <e4 id=\"elem4\" name=\"elem4\"></e4>\n" +
                        "   <e5 xmlns=\"http://example.org\" xmlns:a=\"http://www.w3.org\" xmlns:b=\"http://www.ietf.org\" attr=\"I'm\" attr2=\"all\" b:attr=\"sorted\" a:attr=\"out\"></e5>\n" +
                        "   <e6>\n" +
                        "      <b:e7 xmlns:b=\"http://www.ietf.org\">\n" +
                        "         <e8>\n" +
                        "            <e9 attr=\"default\"></e9>\n" +
                        "         </e8>\n" +
                        "      </b:e7>\n" +
                        "   </e6>\n" +
                        "</doc>\n");
    _testC14 ("<!DOCTYPE doc [\r\n" +
              "<!ATTLIST normId id ID #IMPLIED>\r\n" +
              "<!ATTLIST normNames attr NMTOKENS #IMPLIED>\r\n" +
              "]>\r\n" +
              "<doc>\r\n" +
              "   <text>First line&#x0d;&#10;Second line</text>\r\n" +
              "   <value>&#x32;</value>\r\n" +
              "   <compute><![CDATA[value>\"0\" && value<\"10\" ?\"valid\":\"error\"]]></compute>\r\n" +
              "   <compute expr='value>\"0\" &amp;&amp; value&lt;\"10\" ?\"valid\":\"error\"'>valid</compute>\r\n" +
              "   <norm attr=' &apos;   &#x20;&#13;&#xa;&#9;   &apos; '/>\r\n" +
              "   <normNames attr='   A   &#x20;&#13;&#xa;&#9;   B   '/>\r\n" +
              "   <normId id=' &apos;   &#x20;&#13;&#xa;&#9;   &apos; '/>\r\n" +
              "</doc>",
              "<doc>\n" +
                        "   <text>First line&#xD;\n" +
                        "Second line</text>\n" +
                        "   <value>2</value>\n" +
                        "   <compute>value&gt;\"0\" &amp;&amp; value&lt;\"10\" ?\"valid\":\"error\"</compute>\n" +
                        "   <compute expr=\"value>&quot;0&quot; &amp;&amp; value&lt;&quot;10&quot; ?&quot;valid&quot;:&quot;error&quot;\">valid</compute>\n" +
                        "   <norm attr=\" '    &#xD;&#xA;&#x9;   ' \"></norm>\n" +
                        "   <normNames attr=\"A &#xD;&#xA;&#x9; B\"></normNames>\n" +
                        "   <normId id=\"' &#xD;&#xA;&#x9; '\"></normId>\n" +
                        "</doc>\n");
    _testC14 ("<!DOCTYPE doc [\r\n" +
              "<!ATTLIST doc attrExtEnt ENTITY #IMPLIED>\r\n" +
              "<!ENTITY ent1 \"Hello\">\r\n" +
              "<!ENTITY ent2 SYSTEM \"world.txt\">\r\n" +
              "<!ENTITY entExt SYSTEM \"earth.gif\" NDATA gif>\r\n" +
              "<!NOTATION gif SYSTEM \"viewgif.exe\">\r\n" +
              "]>\r\n" +
              "<doc attrExtEnt=\"entExt\">\r\n" +
              "   &ent1;, &ent2;!\r\n" +
              "</doc>\r\n" +
              "\r\n" +
              "<!-- Let world.txt contain \"world\" (excluding the quotes) -->",
              "<doc attrExtEnt=\"entExt\">\n" + "   Hello, world!\n" + "</doc>\n");
  }

  @Test
  public void testXMLDeclaration ()
  {
    final Document aDoc = XMLFactory.newDocument ();
    final Element eRoot = (Element) aDoc.appendChild (aDoc.createElement ("root"));
    eRoot.appendChild (aDoc.createElement ("child1"));

    final XMLWriterSettings aSettings = new XMLWriterSettings ();
    String s = XMLWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
                  CRLF +
                  "<root>" +
                  CRLF +
                  "  <child1 />" +
                  CRLF +
                  "</root>" +
                  CRLF,
                  s);

    aSettings.setNewLineAfterXMLDeclaration (false);
    s = XMLWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
                  "<root>" +
                  CRLF +
                  "  <child1 />" +
                  CRLF +
                  "</root>" +
                  CRLF,
                  s);

    aSettings.setNewLineAfterXMLDeclaration (true);
    aSettings.setSerializeXMLDeclaration (EXMLSerializeXMLDeclaration.EMIT_NO_STANDALONE);
    s = XMLWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                  CRLF +
                  "<root>" +
                  CRLF +
                  "  <child1 />" +
                  CRLF +
                  "</root>" +
                  CRLF,
                  s);
  }
}
