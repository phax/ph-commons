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
package com.helger.commons.xml.serialize.write;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.helger.commons.charset.CCharset;
import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;
import com.helger.commons.mock.AbstractCommonsTestCase;
import com.helger.commons.mock.CommonsTestHelper;
import com.helger.commons.system.ENewLineMode;
import com.helger.commons.xml.EXMLVersion;
import com.helger.commons.xml.XMLFactory;
import com.helger.commons.xml.namespace.MapBasedNamespaceContext;
import com.helger.commons.xml.serialize.read.DOMReader;
import com.helger.commons.xml.transform.StringStreamResult;
import com.helger.commons.xml.transform.XMLTransformerFactory;

/**
 * Test class for {@link XMLWriter}
 *
 * @author Philip Helger
 */
public final class XMLWriterTest extends AbstractCommonsTestCase
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
      assertEquals ("<html xmlns=\"" +
                    DOCTYPE_XHTML10_URI +
                    "\"><head>Hallo</head>" +
                    sSerTagName +
                    "</html>",
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
      final String sResult = XMLWriter.getXMLString (doc);
      assertEquals ("<?xml version=\"1.0\" encoding=\"" +
                    CCharset.CHARSET_UTF_8 +
                    "\"?>" +
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
                    CCharset.CHARSET_UTF_8 +
                    "\"?>" +
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
                    CCharset.CHARSET_UTF_8 +
                    "\"?>\n" +
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
                    CCharset.CHARSET_UTF_8 +
                    "\"?>\n" +
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

    CommonsTestHelper.testInParallel (1000, (Runnable) () -> {
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
    assertEquals ("<a><![CDATA[a]]]]><![CDATA[>b]]></a>" + CRLF, XMLWriter.getXMLString (e));

    // Containing more than one forbidden CDATA end marker
    e = doc.createElement ("a");
    e.appendChild (doc.createCDATASection ("a]]>b]]>c"));
    assertEquals ("<a><![CDATA[a]]]]><![CDATA[>b]]]]><![CDATA[>c]]></a>" + CRLF, XMLWriter.getXMLString (e));

    // Containing a complete CDATA section
    e = doc.createElement ("a");
    e.appendChild (doc.createCDATASection ("a<![CDATA[x]]>b"));
    assertEquals ("<a><![CDATA[a<![CDATA[x]]]]><![CDATA[>b]]></a>" + CRLF, XMLWriter.getXMLString (e));
  }

  @Test
  public void testWithNamespaceContext ()
  {
    final Document aDoc = XMLFactory.newDocument ();
    final Element eRoot = (Element) aDoc.appendChild (aDoc.createElementNS ("ns1url", "root"));
    eRoot.appendChild (aDoc.createElementNS ("ns2url", "child1"));
    eRoot.appendChild (aDoc.createElementNS ("ns2url", "child2"));

    final XMLWriterSettings aSettings = new XMLWriterSettings ().setCharset (CCharset.CHARSET_ISO_8859_1_OBJ)
                                                                .setIndent (EXMLSerializeIndent.NONE);
    String s = XMLWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>" +
                  "<root xmlns=\"ns1url\">" +
                  "<ns0:child1 xmlns:ns0=\"ns2url\" />" +
                  "<ns0:child2 xmlns:ns0=\"ns2url\" />" +
                  "</root>",
                  s);

    final MapBasedNamespaceContext aCtx = new MapBasedNamespaceContext ();
    aCtx.addMapping ("a", "ns1url");
    aSettings.setNamespaceContext (aCtx);
    s = XMLWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>" +
                  "<a:root xmlns:a=\"ns1url\">" +
                  "<ns0:child1 xmlns:ns0=\"ns2url\" />" +
                  "<ns0:child2 xmlns:ns0=\"ns2url\" />" +
                  "</a:root>",
                  s);

    aCtx.addMapping ("xy", "ns2url");
    s = XMLWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>" +
                  "<a:root xmlns:a=\"ns1url\">" +
                  "<xy:child1 xmlns:xy=\"ns2url\" />" +
                  "<xy:child2 xmlns:xy=\"ns2url\" />" +
                  "</a:root>",
                  s);

    aSettings.setUseDoubleQuotesForAttributes (false);
    s = XMLWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version='1.0' encoding='ISO-8859-1' standalone='yes'?>" +
                  "<a:root xmlns:a='ns1url'>" +
                  "<xy:child1 xmlns:xy='ns2url' />" +
                  "<xy:child2 xmlns:xy='ns2url' />" +
                  "</a:root>",
                  s);

    aSettings.setSpaceOnSelfClosedElement (false);
    s = XMLWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version='1.0' encoding='ISO-8859-1' standalone='yes'?>" +
                  "<a:root xmlns:a='ns1url'>" +
                  "<xy:child1 xmlns:xy='ns2url'/>" +
                  "<xy:child2 xmlns:xy='ns2url'/>" +
                  "</a:root>",
                  s);

    aSettings.setPutNamespaceContextPrefixesInRoot (true);
    s = XMLWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version='1.0' encoding='ISO-8859-1' standalone='yes'?>" +
                  "<a:root xmlns:a='ns1url' xmlns:xy='ns2url'>" +
                  "<xy:child1/>" +
                  "<xy:child2/>" +
                  "</a:root>",
                  s);

    eRoot.appendChild (aDoc.createElementNS ("ns3url", "zz"));
    s = XMLWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version='1.0' encoding='ISO-8859-1' standalone='yes'?>" +
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

    final XMLWriterSettings aSettings = new XMLWriterSettings ().setCharset (CCharset.CHARSET_ISO_8859_1_OBJ)
                                                                .setIndent (EXMLSerializeIndent.NONE);
    String s = XMLWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>" +
                  "<root xmlns=\"ns1url\">" +
                  "<ns0:child1 xmlns:ns0=\"ns2url\" />" +
                  "<ns0:child2 xmlns:ns0=\"ns2url\" />" +
                  "</root>",
                  s);

    aSettings.setEmitNamespaces (false);
    s = XMLWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>" +
                  "<root>" +
                  "<child1 />" +
                  "<child2 />" +
                  "</root>",
                  s);

    aSettings.setPutNamespaceContextPrefixesInRoot (true);
    s = XMLWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>" +
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
    String sXML = XMLWriter.getXMLString (aDoc);
    assertEquals ("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + CRLF + "<any />" + CRLF, sXML);

    aDoc = XMLFactory.newDocument (EXMLVersion.XML_11);
    aDoc.appendChild (aDoc.createElement ("any"));
    sXML = XMLWriter.getXMLString (aDoc);
    assertEquals ("<?xml version=\"1.1\" encoding=\"UTF-8\" standalone=\"yes\"?>" + CRLF + "<any />" + CRLF, sXML);
  }

  @Test
  public void testNumericReferencesXML10 () throws SAXException, TransformerException
  {
    for (int i = Character.MIN_VALUE; i <= Character.MAX_VALUE; ++i)
      if (!XMLCharHelper.isInvalidXMLTextChar (EXMLSerializeVersion.XML_10, (char) i))
      {
        final String sText = "abc" + (char) i + "def";
        final Document aDoc = XMLFactory.newDocument (EXMLVersion.XML_10);
        final Element eRoot = (Element) aDoc.appendChild (aDoc.createElement ("root"));
        eRoot.appendChild (aDoc.createTextNode (sText));

        // Use regular transformer
        final Transformer aTransformer = XMLTransformerFactory.newTransformer ();
        aTransformer.setOutputProperty (OutputKeys.ENCODING, CCharset.CHARSET_UTF_8);
        aTransformer.setOutputProperty (OutputKeys.INDENT, "yes");
        aTransformer.setOutputProperty (OutputKeys.VERSION, EXMLVersion.XML_10.getVersion ());
        final StringStreamResult aRes = new StringStreamResult ();
        aTransformer.transform (new DOMSource (aDoc), aRes);

        final String sXML = aRes.getAsString ();
        final Document aDoc2 = DOMReader.readXMLDOM (sXML);
        assertNotNull (aDoc2);
      }
  }

  @Test
  public void testNumericReferencesXML11 () throws SAXException, TransformerException
  {
    for (int i = Character.MIN_VALUE; i <= Character.MAX_VALUE; ++i)
      if (!XMLCharHelper.isInvalidXMLTextChar (EXMLSerializeVersion.XML_11, (char) i))
      {
        final String sText = "abc" + (char) i + "def";
        final Document aDoc = XMLFactory.newDocument (EXMLVersion.XML_11);
        final Element eRoot = (Element) aDoc.appendChild (aDoc.createElement ("root"));
        eRoot.appendChild (aDoc.createTextNode (sText));

        final Transformer aTransformer = XMLTransformerFactory.newTransformer ();
        aTransformer.setOutputProperty (OutputKeys.ENCODING, CCharset.CHARSET_UTF_8);
        aTransformer.setOutputProperty (OutputKeys.INDENT, "no");
        aTransformer.setOutputProperty (OutputKeys.VERSION, EXMLVersion.XML_11.getVersion ());
        final StringStreamResult aRes = new StringStreamResult ();
        aTransformer.transform (new DOMSource (aDoc), aRes);

        final String sXML = aRes.getAsString ();
        final Document aDoc2 = DOMReader.readXMLDOM (sXML);
        assertNotNull (aDoc2);
      }
  }

  @Test
  public void testAttributesWithNamespaces () throws SAXException
  {
    final XMLWriterSettings aSettings = new XMLWriterSettings ().setIndent (EXMLSerializeIndent.NONE)
                                                                .setCharset (CCharset.CHARSET_ISO_8859_1_OBJ);
    final Document aDoc = XMLFactory.newDocument ();
    final Element eRoot = (Element) aDoc.appendChild (aDoc.createElementNS ("ns1url", "root"));
    final Element e1 = (Element) eRoot.appendChild (aDoc.createElementNS ("ns2url", "child1"));
    e1.setAttributeNS ("ns2url", "attr1", "value1");
    final Element e2 = (Element) eRoot.appendChild (aDoc.createElementNS ("ns2url", "child2"));
    e2.setAttributeNS ("ns3url", "attr2", "value2");

    String s = XMLWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>" +
                  "<root xmlns=\"ns1url\">" +
                  "<ns0:child1 xmlns:ns0=\"ns2url\" ns0:attr1=\"value1\" />" +
                  "<ns0:child2 xmlns:ns0=\"ns2url\" xmlns:ns1=\"ns3url\" ns1:attr2=\"value2\" />" +
                  "</root>",
                  s);
    assertEquals (s, XMLWriter.getNodeAsString (DOMReader.readXMLDOM (s), aSettings));

    aSettings.setEmitNamespaces (false);
    s = XMLWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>" +
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
}
