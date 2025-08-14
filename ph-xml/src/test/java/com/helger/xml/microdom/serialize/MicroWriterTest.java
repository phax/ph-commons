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
package com.helger.xml.microdom.serialize;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.Ignore;
import org.junit.Test;

import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.commons.text.codepoint.CodepointHelper;
import com.helger.xml.EXMLParserFeature;
import com.helger.xml.EXMLVersion;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.IMicroNode;
import com.helger.xml.microdom.MicroCDATA;
import com.helger.xml.microdom.MicroComment;
import com.helger.xml.microdom.MicroContainer;
import com.helger.xml.microdom.MicroDocument;
import com.helger.xml.microdom.MicroElement;
import com.helger.xml.microdom.MicroEntityReference;
import com.helger.xml.microdom.MicroText;
import com.helger.xml.namespace.MapBasedNamespaceContext;
import com.helger.xml.sax.StringSAXInputSource;
import com.helger.xml.serialize.read.SAXReaderSettings;
import com.helger.xml.serialize.write.EXMLIncorrectCharacterHandling;
import com.helger.xml.serialize.write.EXMLSerializeComments;
import com.helger.xml.serialize.write.EXMLSerializeDocType;
import com.helger.xml.serialize.write.EXMLSerializeIndent;
import com.helger.xml.serialize.write.EXMLSerializeVersion;
import com.helger.xml.serialize.write.XMLCharHelper;
import com.helger.xml.serialize.write.XMLWriterSettings;

/**
 * Test class for class {@link MicroWriter}.
 *
 * @author Philip Helger
 */
public final class MicroWriterTest
{
  public static final String SURROGATE_PAIR_TEST_STRING = "株式会社 髙橋 𩸽𠮷商事";

  private static final String TEST_XML = "<?xml version=\"1.0\"?>" +
                                         "<!DOCTYPE verrryoot>" +
                                         "<verrryoot xmlns=\"sthgelse\">" +
                                         "<!-- arg - a comment -->" +
                                         "<root xmlns=\"myuri\">" +
                                         "<child xmlns=\"www.helger.com\">" +
                                         "<a:child2 xmlns:a=\"foo\">Value text - no entities!</a:child2>" +
                                         "&lt;entity&gt;<![CDATA[xxx]]></child>" +
                                         "</root>" +
                                         "<?target value?>" +
                                         "</verrryoot>";

  private static void _testGetNodeAsXHTMLString (final IMicroNode aNode)
  {
    // try all permutations
    final XMLWriterSettings aSettings = XMLWriterSettings.createForXHTML ();
    for (final Charset aCharset : new Charset [] { StandardCharsets.ISO_8859_1,
                                                   StandardCharsets.UTF_8,
                                                   StandardCharsets.UTF_16,
                                                   StandardCharsets.UTF_16BE,
                                                   StandardCharsets.UTF_16LE })
    {
      aSettings.setCharset (aCharset);
      for (final EXMLSerializeIndent eIndent : EXMLSerializeIndent.values ())
      {
        aSettings.setIndent (eIndent);
        for (final EXMLSerializeDocType eDocType : EXMLSerializeDocType.values ())
        {
          aSettings.setSerializeDocType (eDocType);
          assertNotNull (MicroWriter.getNodeAsString (aNode, aSettings));
        }
      }
    }
  }

  @Test
  public void testGetXHTMLString ()
  {
    final IMicroDocument aDoc = MicroReader.readMicroXML (TEST_XML,
                                                          new SAXReaderSettings ().setFeatureValue (EXMLParserFeature.DISALLOW_DOCTYPE_DECL,
                                                                                                    false));
    _testGetNodeAsXHTMLString (aDoc);
    _testGetNodeAsXHTMLString (aDoc.getDocumentElement ());
    _testGetNodeAsXHTMLString (new MicroDocument ());
    _testGetNodeAsXHTMLString (new MicroElement ("xyz"));
    _testGetNodeAsXHTMLString (new MicroContainer ());
    _testGetNodeAsXHTMLString (new MicroComment ("useless"));
    _testGetNodeAsXHTMLString (new MicroEntityReference ("xyz"));
    try
    {
      _testGetNodeAsXHTMLString (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  private static void _testGetNodeAsXMLString (final IMicroNode aNode)
  {
    // try all permutations
    final XMLWriterSettings aSettings = new XMLWriterSettings ();
    for (final Charset aCharset : new Charset [] { StandardCharsets.ISO_8859_1,
                                                   StandardCharsets.UTF_8,
                                                   StandardCharsets.UTF_16,
                                                   StandardCharsets.UTF_16BE,
                                                   StandardCharsets.UTF_16LE })
    {
      aSettings.setCharset (aCharset);
      for (final EXMLSerializeIndent eIndent : EXMLSerializeIndent.values ())
      {
        aSettings.setIndent (eIndent);
        for (final EXMLSerializeDocType eDocType : EXMLSerializeDocType.values ())
        {
          aSettings.setSerializeDocType (eDocType);
          assertNotNull (MicroWriter.getNodeAsString (aNode, aSettings));
        }
      }
    }
  }

  @Test
  public void testGetXMLString ()
  {
    final IMicroDocument aDoc = MicroReader.readMicroXML (TEST_XML,
                                                          new SAXReaderSettings ().setFeatureValue (EXMLParserFeature.DISALLOW_DOCTYPE_DECL,
                                                                                                    false));
    _testGetNodeAsXMLString (aDoc);
    _testGetNodeAsXMLString (aDoc.getDocumentElement ());
    _testGetNodeAsXMLString (new MicroElement ("xyz"));
    _testGetNodeAsXMLString (new MicroElement ("xyz").setAttribute ("nsuri", "attr", "1"));
    _testGetNodeAsXMLString (new MicroContainer ());
    _testGetNodeAsXMLString (new MicroContainer (new MicroComment ("useless"), new MicroElement ("xyz")));
    _testGetNodeAsXMLString (new MicroComment ("useless"));
    _testGetNodeAsXMLString (new MicroEntityReference ("xyz"));
    _testGetNodeAsXMLString (new MicroCDATA ("xyz"));

    try
    {
      _testGetNodeAsXMLString (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      MicroWriter.getNodeAsString (null, XMLWriterSettings.DEFAULT_XML_SETTINGS);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      MicroWriter.getNodeAsString (aDoc, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testSurrogatePairs ()
  {
    // Surrogate pairs are contained
    // See https://github.com/phax/phase4/discussions/123
    assertEquals (14, SURROGATE_PAIR_TEST_STRING.length ());
    assertEquals (12, CodepointHelper.length (SURROGATE_PAIR_TEST_STRING));
    assertArrayEquals (new char [] { '株',
                                     '式',
                                     '会',
                                     '社',
                                     0x20,
                                     '髙',
                                     '橋',
                                     0x20,
                                     0xd867,
                                     0xde3d,
                                     0xd842,
                                     0xdfb7,
                                     '商',
                                     '事' },
                       SURROGATE_PAIR_TEST_STRING.toCharArray ());
    final XMLWriterSettings aSettings = new XMLWriterSettings ();
    aSettings.setIncorrectCharacterHandling (EXMLIncorrectCharacterHandling.THROW_EXCEPTION);
    aSettings.setCharset (StandardCharsets.UTF_16);
    aSettings.setIndent (EXMLSerializeIndent.NONE);

    // Text only
    {
      final String sXML = MicroWriter.getNodeAsString (new MicroText (SURROGATE_PAIR_TEST_STRING), aSettings);
      assertEquals (SURROGATE_PAIR_TEST_STRING, sXML);
    }

    // As an attribute
    {
      final IMicroElement e = new MicroElement ("bla");
      e.setAttribute ("attr", SURROGATE_PAIR_TEST_STRING);
      final String sXML = MicroWriter.getNodeAsString (e, aSettings);
      assertEquals ("<bla attr=\"" + SURROGATE_PAIR_TEST_STRING + "\" />", sXML);
    }

    // As an element value
    {
      final IMicroElement e = new MicroElement ("bla");
      e.addText (SURROGATE_PAIR_TEST_STRING);
      final String sXML = MicroWriter.getNodeAsString (e, aSettings);
      assertEquals ("<bla>" + SURROGATE_PAIR_TEST_STRING + "</bla>", sXML);
    }
  }

  @Test
  public void testSaveToStream ()
  {
    try
    {
      MicroWriter.writeToStream (null, new NonBlockingByteArrayOutputStream (), XMLWriterSettings.DEFAULT_XML_SETTINGS);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      MicroWriter.writeToStream (new MicroDocument (), null, XMLWriterSettings.DEFAULT_XML_SETTINGS);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      MicroWriter.writeToStream (new MicroDocument (), new NonBlockingByteArrayOutputStream (), null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testXMLVersion ()
  {
    for (final EXMLSerializeVersion eVersion : EXMLSerializeVersion.values ())
    {
      final IMicroDocument aDoc = MicroReader.readMicroXML (TEST_XML,
                                                            new SAXReaderSettings ().setFeatureValue (EXMLParserFeature.DISALLOW_DOCTYPE_DECL,
                                                                                                      false));
      final XMLWriterSettings aSettings = new XMLWriterSettings ();
      aSettings.setSerializeVersion (eVersion);
      final String sXML = MicroWriter.getNodeAsString (aDoc, aSettings);
      assertNotNull (sXML);
      assertTrue (sXML.contains ("version=\"" +
                                 eVersion.getXMLVersionOrDefault (EXMLVersion.XML_10).getVersion () +
                                 "\""));
    }
  }

  @Test
  public void testNestedCDATASections ()
  {
    final XMLWriterSettings aSettings = new XMLWriterSettings ().setIndent (EXMLSerializeIndent.NONE);

    // Simple CDATA
    IMicroElement e = new MicroElement ("a");
    e.addCDATA ("foobar");
    assertEquals ("<a><![CDATA[foobar]]></a>", MicroWriter.getNodeAsString (e, aSettings));

    // Containing the forbidden CDATA end marker
    e = new MicroElement ("a");
    e.addCDATA ("a]]>b");
    assertEquals ("<a><![CDATA[a]]]]><![CDATA[>b]]></a>", MicroWriter.getNodeAsString (e, aSettings));

    // Containing more than one forbidden CDATA end marker
    e = new MicroElement ("a");
    e.addCDATA ("a]]>b]]>c");
    assertEquals ("<a><![CDATA[a]]]]><![CDATA[>b]]]]><![CDATA[>c]]></a>", MicroWriter.getNodeAsString (e, aSettings));

    // Containing a complete CDATA section
    e = new MicroElement ("a");
    e.addCDATA ("a<![CDATA[x]]>b");
    assertEquals ("<a><![CDATA[a<![CDATA[x]]]]><![CDATA[>b]]></a>", MicroWriter.getNodeAsString (e, aSettings));
  }

  @Test
  public void testWriteCDATAAsText ()
  {
    final XMLWriterSettings aSettings = new XMLWriterSettings ().setIndent (EXMLSerializeIndent.NONE)
                                                                .setWriteCDATAAsText (true);

    // Simple CDATA
    IMicroElement e = new MicroElement ("a");
    e.addCDATA ("foobar");
    assertEquals ("<a>foobar</a>", MicroWriter.getNodeAsString (e, aSettings));

    // Containing the forbidden CDATA end marker
    e = new MicroElement ("a");
    e.addCDATA ("a]]>b");
    assertEquals ("<a>a]]&gt;b</a>", MicroWriter.getNodeAsString (e, aSettings));

    // Containing more than one forbidden CDATA end marker
    e = new MicroElement ("a");
    e.addCDATA ("a]]>b]]>c");
    assertEquals ("<a>a]]&gt;b]]&gt;c</a>", MicroWriter.getNodeAsString (e, aSettings));

    // Containing a complete CDATA section
    e = new MicroElement ("a");
    e.addCDATA ("a<![CDATA[x]]>b");
    assertEquals ("<a>a&lt;![CDATA[x]]&gt;b</a>", MicroWriter.getNodeAsString (e, aSettings));
  }

  @Test
  public void testWithNamespaceContext ()
  {
    final XMLWriterSettings aSettings = new XMLWriterSettings ().setIndent (EXMLSerializeIndent.NONE)
                                                                .setCharset (StandardCharsets.ISO_8859_1);
    final IMicroDocument aDoc = new MicroDocument ();
    final IMicroElement eRoot = aDoc.addElement ("ns1url", "root");
    eRoot.addElement ("ns2url", "child1");
    eRoot.addElement ("ns2url", "child2").setAttribute ("attr1", "a");
    eRoot.addElement ("ns3url", "child3").setAttribute ("ns3url", "attr1", "a");
    eRoot.addElement ("ns3url", "child4").setAttribute ("ns4url", "attr1", "a");

    String s = MicroWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
                  "<root xmlns=\"ns1url\">" +
                  "<ns0:child1 xmlns:ns0=\"ns2url\" />" +
                  "<ns0:child2 xmlns:ns0=\"ns2url\" attr1=\"a\" />" +
                  "<ns0:child3 xmlns:ns0=\"ns3url\" ns0:attr1=\"a\" />" +
                  "<ns0:child4 xmlns:ns0=\"ns3url\" xmlns:ns1=\"ns4url\" ns1:attr1=\"a\" />" +
                  "</root>",
                  s);

    final MapBasedNamespaceContext aCtx = new MapBasedNamespaceContext ();
    aCtx.addMapping ("a", "ns1url");
    aSettings.setNamespaceContext (aCtx);
    s = MicroWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
                  "<a:root xmlns:a=\"ns1url\">" +
                  "<ns0:child1 xmlns:ns0=\"ns2url\" />" +
                  "<ns0:child2 xmlns:ns0=\"ns2url\" attr1=\"a\" />" +
                  "<ns0:child3 xmlns:ns0=\"ns3url\" ns0:attr1=\"a\" />" +
                  "<ns0:child4 xmlns:ns0=\"ns3url\" xmlns:ns1=\"ns4url\" ns1:attr1=\"a\" />" +
                  "</a:root>",
                  s);

    // Add mapping to namespace context
    aCtx.addMapping ("xy", "ns2url");
    s = MicroWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
                  "<a:root xmlns:a=\"ns1url\">" +
                  "<xy:child1 xmlns:xy=\"ns2url\" />" +
                  "<xy:child2 xmlns:xy=\"ns2url\" attr1=\"a\" />" +
                  "<ns0:child3 xmlns:ns0=\"ns3url\" ns0:attr1=\"a\" />" +
                  "<ns0:child4 xmlns:ns0=\"ns3url\" xmlns:ns1=\"ns4url\" ns1:attr1=\"a\" />" +
                  "</a:root>",
                  s);

    // Put namespace context mappings in root
    aSettings.setPutNamespaceContextPrefixesInRoot (true);
    s = MicroWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
                  "<a:root xmlns:a=\"ns1url\" xmlns:xy=\"ns2url\">" +
                  "<xy:child1 />" +
                  "<xy:child2 attr1=\"a\" />" +
                  "<ns0:child3 xmlns:ns0=\"ns3url\" ns0:attr1=\"a\" />" +
                  "<ns0:child4 xmlns:ns0=\"ns3url\" xmlns:ns1=\"ns4url\" ns1:attr1=\"a\" />" +
                  "</a:root>",
                  s);

    eRoot.addElement ("ns3url", "zz");
    s = MicroWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
                  "<a:root xmlns:a=\"ns1url\" xmlns:xy=\"ns2url\">" +
                  "<xy:child1 />" +
                  "<xy:child2 attr1=\"a\" />" +
                  "<ns0:child3 xmlns:ns0=\"ns3url\" ns0:attr1=\"a\" />" +
                  "<ns0:child4 xmlns:ns0=\"ns3url\" xmlns:ns1=\"ns4url\" ns1:attr1=\"a\" />" +
                  "<ns0:zz xmlns:ns0=\"ns3url\" />" +
                  "</a:root>",
                  s);
  }

  @Test
  public void testWithoutEmitNamespaces ()
  {
    final XMLWriterSettings aSettings = new XMLWriterSettings ().setIndent (EXMLSerializeIndent.NONE)
                                                                .setCharset (StandardCharsets.ISO_8859_1);
    final IMicroDocument aDoc = new MicroDocument ();
    final IMicroElement eRoot = aDoc.addElement ("ns1url", "root");
    eRoot.addElement ("ns2url", "child1");
    eRoot.addElement ("ns2url", "child2").setAttribute ("attr1", "a");
    eRoot.addElement ("ns3url", "child3").setAttribute ("ns3url", "attr1", "a");
    eRoot.addElement ("ns3url", "child4").setAttribute ("ns4url", "attr1", "a");

    String s = MicroWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
                  "<root xmlns=\"ns1url\">" +
                  "<ns0:child1 xmlns:ns0=\"ns2url\" />" +
                  "<ns0:child2 xmlns:ns0=\"ns2url\" attr1=\"a\" />" +
                  "<ns0:child3 xmlns:ns0=\"ns3url\" ns0:attr1=\"a\" />" +
                  "<ns0:child4 xmlns:ns0=\"ns3url\" xmlns:ns1=\"ns4url\" ns1:attr1=\"a\" />" +
                  "</root>",
                  s);

    aSettings.setPutNamespaceContextPrefixesInRoot (true);
    s = MicroWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
                  "<root xmlns=\"ns1url\">" +
                  "<ns0:child1 xmlns:ns0=\"ns2url\" />" +
                  "<ns0:child2 xmlns:ns0=\"ns2url\" attr1=\"a\" />" +
                  "<ns0:child3 xmlns:ns0=\"ns3url\" ns0:attr1=\"a\" />" +
                  "<ns0:child4 xmlns:ns0=\"ns3url\" xmlns:ns1=\"ns4url\" ns1:attr1=\"a\" />" +
                  "</root>",
                  s);

    aSettings.setPutNamespaceContextPrefixesInRoot (false);
    aSettings.setEmitNamespaces (false);
    s = MicroWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
                  "<root>" +
                  "<child1 />" +
                  "<child2 attr1=\"a\" />" +
                  "<child3 attr1=\"a\" />" +
                  "<child4 attr1=\"a\" />" +
                  "</root>",
                  s);

    aSettings.setPutNamespaceContextPrefixesInRoot (true);
    s = MicroWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
                  "<root>" +
                  "<child1 />" +
                  "<child2 attr1=\"a\" />" +
                  "<child3 attr1=\"a\" />" +
                  "<child4 attr1=\"a\" />" +
                  "</root>",
                  s);
  }

  @Test
  @Ignore ("Takes too long and was already tested with JDK 1.8 runtime parser")
  public void testSpecialCharactersXML10Text ()
  {
    final EXMLSerializeVersion eXMLSerializeVersion = EXMLSerializeVersion.XML_10;

    final XMLWriterSettings aSettings = new XMLWriterSettings ().setSerializeVersion (eXMLSerializeVersion);
    for (int i = Character.MIN_VALUE; i <= Character.MAX_VALUE; ++i)
      if (!XMLCharHelper.isInvalidXMLTextChar (eXMLSerializeVersion, (char) i))
      {
        final String sText = "abc" + (char) i + "def";
        assertEquals (7, sText.length ());
        final IMicroDocument aDoc = new MicroDocument ();
        aDoc.addElement ("root").addText (sText);
        final String sXML = MicroWriter.getNodeAsString (aDoc, aSettings);
        final IMicroDocument aDoc2 = MicroReader.readMicroXML (sXML);
        assertNotNull ("Failed to read with byte " + i + "\n" + sXML, aDoc2);
        assertEquals ("Length for byte " + i, i == 0 ? 6 : 7, aDoc2.getDocumentElement ().getTextContent ().length ());
        assertTrue ("Difference in byte 0x" + Integer.toHexString (i), aDoc.isEqualContent (aDoc2));
      }
  }

  @Test
  @Ignore ("Takes too long and was already tested with JDK 1.8 runtime parser")
  public void testSpecialCharactersXML10CDATA ()
  {
    final EXMLSerializeVersion eXMLSerializeVersion = EXMLSerializeVersion.XML_10;

    final XMLWriterSettings aSettings = new XMLWriterSettings ().setSerializeVersion (eXMLSerializeVersion);
    for (int i = Character.MIN_VALUE; i <= Character.MAX_VALUE; ++i)
      if (!XMLCharHelper.isInvalidXMLCDATAChar (eXMLSerializeVersion, (char) i))
      {
        final String sText = "abc" + (char) i + "def";
        assertEquals (7, sText.length ());
        final IMicroDocument aDoc = new MicroDocument ();
        aDoc.addElement ("root").addCDATA (sText);
        final String sXML = MicroWriter.getNodeAsString (aDoc, aSettings);
        final IMicroDocument aDoc2 = MicroReader.readMicroXML (sXML);
        assertNotNull ("Failed to read with byte " + i + "\n" + sXML, aDoc2);
        assertEquals ("Length for byte " + i, i == 0 ? 6 : 7, aDoc2.getDocumentElement ().getTextContent ().length ());

        // Difference between created "\r" and read "\n"
        if (i != '\r')
          if (!aDoc.isEqualContent (aDoc2))
          {
            final String sXML2 = MicroWriter.getNodeAsString (aDoc2, aSettings);
            fail ("0x" + Integer.toHexString (i) + "\n" + sXML + "\n" + sXML2);
          }
      }
  }

  @Test
  @Ignore ("Takes too long and was already tested with JDK 1.8 runtime parser")
  public void testSpecialCharactersXML11Text ()
  {
    final EXMLSerializeVersion eXMLSerializeVersion = EXMLSerializeVersion.XML_11;

    final XMLWriterSettings aSettings = new XMLWriterSettings ().setSerializeVersion (eXMLSerializeVersion);
    for (int i = Character.MIN_VALUE; i <= Character.MAX_VALUE; ++i)
      if (!XMLCharHelper.isInvalidXMLTextChar (eXMLSerializeVersion, (char) i))
      {
        final String sText = "abc" + (char) i + "def";
        assertEquals (7, sText.length ());
        final IMicroDocument aDoc = new MicroDocument ();
        aDoc.addElement ("root").addText (sText);
        final String sXML = MicroWriter.getNodeAsString (aDoc, aSettings);
        final IMicroDocument aDoc2 = MicroReader.readMicroXML (sXML);
        assertNotNull ("Failed to read with byte " + i + "\n" + sXML, aDoc2);
        assertEquals ("Length for byte " + i, i == 0 ? 6 : 7, aDoc2.getDocumentElement ().getTextContent ().length ());

        // Difference between created "0x2028" and read "\n"
        if (i != 0x2028)
          if (!aDoc.isEqualContent (aDoc2))
          {
            final String sXML2 = MicroWriter.getNodeAsString (aDoc2, aSettings);
            fail ("0x" + Integer.toHexString (i) + "\n" + sXML + "\n" + sXML2);
          }
      }
  }

  @Test
  @Ignore ("Takes too long and was already tested with JDK 1.8 runtime parser")
  public void testSpecialCharactersXML11CDATA ()
  {
    final EXMLSerializeVersion eXMLSerializeVersion = EXMLSerializeVersion.XML_11;

    final XMLWriterSettings aSettings = new XMLWriterSettings ().setSerializeVersion (eXMLSerializeVersion);
    for (int i = Character.MIN_VALUE; i <= Character.MAX_VALUE; ++i)
      if (!XMLCharHelper.isInvalidXMLCDATAChar (eXMLSerializeVersion, (char) i))
      {
        final String sText = "abc" + (char) i + "def";
        assertEquals (7, sText.length ());
        final IMicroDocument aDoc = new MicroDocument ();
        aDoc.addElement ("root").addCDATA (sText);
        final String sXML = MicroWriter.getNodeAsString (aDoc, aSettings);
        final IMicroDocument aDoc2 = MicroReader.readMicroXML (sXML);
        assertNotNull ("Failed to read with byte " + i + "\n" + sXML, aDoc2);
        assertEquals ("Length for byte " + i, i == 0 ? 6 : 7, aDoc2.getDocumentElement ().getTextContent ().length ());

        // Difference between created "\r" and read "\n"
        // Difference between created "0x2028" and read "\n"
        if (i != '\r' && i != 0x2028)
          if (!aDoc.isEqualContent (aDoc2))
          {
            final String sXML2 = MicroWriter.getNodeAsString (aDoc2, aSettings);
            fail ("0x" + Integer.toHexString (i) + "\n" + sXML + "\n" + sXML2);
          }
      }
  }

  @Test
  public void testAttributesWithNamespaces ()
  {
    final XMLWriterSettings aSettings = new XMLWriterSettings ().setIndent (EXMLSerializeIndent.NONE)
                                                                .setCharset (StandardCharsets.ISO_8859_1);
    final IMicroDocument aDoc = new MicroDocument ();
    final IMicroElement eRoot = aDoc.addElement ("ns1url", "root");
    eRoot.addElement ("ns2url", "child1").setAttribute ("ns2url", "attr1", "value1");
    eRoot.addElement ("ns2url", "child2").setAttribute ("ns3url", "attr2", "value2");

    {
      final String s = MicroWriter.getNodeAsString (aDoc, aSettings);
      assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
                    "<root xmlns=\"ns1url\">" +
                    "<ns0:child1 xmlns:ns0=\"ns2url\" ns0:attr1=\"value1\" />" +
                    "<ns0:child2 xmlns:ns0=\"ns2url\" xmlns:ns1=\"ns3url\" ns1:attr2=\"value2\" />" +
                    "</root>",
                    s);
      assertEquals (s, MicroWriter.getNodeAsString (MicroReader.readMicroXML (s), aSettings));
      final IMicroDocument aDoc2 = MicroReader.readMicroXML (s);
      assertTrue (aDoc.isEqualContent (aDoc2));
    }

    {
      final MapBasedNamespaceContext aNC = new MapBasedNamespaceContext ();
      aNC.addMapping ("n1", "ns1url");
      aNC.addMapping ("n2", "ns2url");
      aNC.addMapping ("n3", "ns3url");
      final String s = MicroWriter.getNodeAsString (aDoc, new XMLWriterSettings (aSettings).setNamespaceContext (aNC));
      assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
                    "<n1:root xmlns:n1=\"ns1url\">" +
                    "<n2:child1 xmlns:n2=\"ns2url\" n2:attr1=\"value1\" />" +
                    "<n2:child2 xmlns:n2=\"ns2url\" xmlns:n3=\"ns3url\" n3:attr2=\"value2\" />" +
                    "</n1:root>",
                    s);
      final IMicroDocument aDoc2 = MicroReader.readMicroXML (s);
      assertNotNull (aDoc2);
      assertTrue (aDoc.isEqualContent (aDoc2));
    }

    {
      final MapBasedNamespaceContext aNC = new MapBasedNamespaceContext ();
      aNC.addMapping ("n1", "ns1url");
      aNC.addMapping ("n2", "ns2url");
      aNC.addMapping ("", "ns3url");
      final String s = MicroWriter.getNodeAsString (aDoc, new XMLWriterSettings (aSettings).setNamespaceContext (aNC));
      assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
                    "<n1:root xmlns:n1=\"ns1url\">" +
                    "<n2:child1 xmlns:n2=\"ns2url\" n2:attr1=\"value1\" />" +
                    "<n2:child2 xmlns:n2=\"ns2url\" xmlns=\"ns3url\" attr2=\"value2\" />" +
                    "</n1:root>",
                    s);
      final IMicroDocument aDoc2 = MicroReader.readMicroXML (s);
      assertNotNull (aDoc2);
      if (false)
      {
        // Different namespace for attr2
        assertTrue (aDoc.isEqualContent (aDoc2));
      }
    }

    {
      final MapBasedNamespaceContext aNC = new MapBasedNamespaceContext ();
      aNC.addMapping ("n1", "ns1url");
      aNC.addMapping ("n2", "ns2url");
      final String s = MicroWriter.getNodeAsString (aDoc, new XMLWriterSettings (aSettings).setNamespaceContext (aNC));
      assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
                    "<n1:root xmlns:n1=\"ns1url\">" +
                    "<n2:child1 xmlns:n2=\"ns2url\" n2:attr1=\"value1\" />" +
                    "<n2:child2 xmlns:n2=\"ns2url\" xmlns:ns0=\"ns3url\" ns0:attr2=\"value2\" />" +
                    "</n1:root>",
                    s);
      final IMicroDocument aDoc2 = MicroReader.readMicroXML (s);
      assertNotNull (aDoc2);
      // Different namespace for attr2
      assertTrue (aDoc.isEqualContent (aDoc2));
    }

    {
      final String s = MicroWriter.getNodeAsString (aDoc, new XMLWriterSettings (aSettings).setEmitNamespaces (false));
      assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
                    "<root>" +
                    "<child1 attr1=\"value1\" />" +
                    "<child2 attr2=\"value2\" />" +
                    "</root>",
                    s);
      final IMicroDocument aDoc2 = MicroReader.readMicroXML (s);
      assertNotNull (aDoc2);
    }
  }

  @Test
  public void testOrderAttributes ()
  {
    XMLWriterSettings aSettings = new XMLWriterSettings ().setIndent (EXMLSerializeIndent.NONE)
                                                          .setUseDoubleQuotesForAttributes (false);

    // default order
    final IMicroElement e = new MicroElement ("a");
    e.setAttribute ("c", "1");
    e.setAttribute ("b", "2");
    e.setAttribute ("a", "3");
    assertEquals ("<a c='1' b='2' a='3' />", MicroWriter.getNodeAsString (e, aSettings));

    // Lexicographic order
    aSettings = aSettings.setOrderAttributesAndNamespaces (true);
    assertEquals ("<a a='3' b='2' c='1' />", MicroWriter.getNodeAsString (e, aSettings));
  }

  @Test
  public void testOrderNamespaces ()
  {
    XMLWriterSettings aSettings = new XMLWriterSettings ().setIndent (EXMLSerializeIndent.NONE)
                                                          .setUseDoubleQuotesForAttributes (false);

    // default order
    final IMicroElement e = new MicroElement ("urn:stringdefault", "a");
    e.setAttribute ("urn:string3", "c", "1");
    e.setAttribute ("urn:string2", "b", "2");
    e.setAttribute ("urn:string1", "a", "3");
    // Attributes are ordered automatically in DOM!
    assertEquals ("<a xmlns='urn:stringdefault' xmlns:ns0='urn:string3' ns0:c='1' xmlns:ns1='urn:string2' ns1:b='2' xmlns:ns2='urn:string1' ns2:a='3' />",
                  MicroWriter.getNodeAsString (e, aSettings));

    aSettings = aSettings.setOrderAttributesAndNamespaces (true);
    assertEquals ("<a xmlns='urn:stringdefault' xmlns:ns0='urn:string3' xmlns:ns1='urn:string2' xmlns:ns2='urn:string1' ns2:a='3' ns1:b='2' ns0:c='1' />",
                  MicroWriter.getNodeAsString (e, aSettings));
  }

  private static void _testC14 (final String sSrc, final String sDst)
  {
    final IMicroDocument aDoc = MicroReader.readMicroXML (sSrc,
                                                          new SAXReaderSettings ().setFeatureValue (EXMLParserFeature.DISALLOW_DOCTYPE_DECL,
                                                                                                    false)
                                                                                  .setFeatureValue (EXMLParserFeature.EXTERNAL_GENERAL_ENTITIES,
                                                                                                    true)
                                                                                  .setEntityResolver ( (x,
                                                                                                        y) -> "world.txt".equals (y) ? new StringSAXInputSource ("world")
                                                                                                                                     : new StringSAXInputSource ("")));
    assertNotNull (aDoc);

    final MapBasedNamespaceContext aCtx = new MapBasedNamespaceContext ();
    aCtx.addMapping ("a", "http://www.w3.org");
    aCtx.addMapping ("b", "http://www.ietf.org");
    final String sC14 = MicroWriter.getNodeAsString (aDoc,
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
}
