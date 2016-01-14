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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.commons.charset.CCharset;
import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;
import com.helger.commons.microdom.IMicroDocument;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.IMicroNode;
import com.helger.commons.microdom.MicroCDATA;
import com.helger.commons.microdom.MicroComment;
import com.helger.commons.microdom.MicroContainer;
import com.helger.commons.microdom.MicroDocument;
import com.helger.commons.microdom.MicroElement;
import com.helger.commons.microdom.MicroEntityReference;
import com.helger.commons.xml.EXMLVersion;
import com.helger.commons.xml.namespace.MapBasedNamespaceContext;
import com.helger.commons.xml.serialize.write.EXMLSerializeDocType;
import com.helger.commons.xml.serialize.write.EXMLSerializeIndent;
import com.helger.commons.xml.serialize.write.EXMLSerializeVersion;
import com.helger.commons.xml.serialize.write.XMLCharHelper;
import com.helger.commons.xml.serialize.write.XMLWriterSettings;

/**
 * Test class for class {@link MicroWriter}.
 *
 * @author Philip Helger
 */
public final class MicroWriterTest
{
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
    for (int nCharSet = 0; nCharSet < 2; ++nCharSet)
    {
      aSettings.setCharset (nCharSet == 1 ? CCharset.CHARSET_ISO_8859_1_OBJ : CCharset.CHARSET_UTF_8_OBJ);
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
    final IMicroDocument aDoc = MicroReader.readMicroXML (TEST_XML);
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
    for (int nCharSet = 0; nCharSet < 2; ++nCharSet)
    {
      aSettings.setCharset (nCharSet == 1 ? CCharset.CHARSET_ISO_8859_1_OBJ : CCharset.CHARSET_UTF_8_OBJ);
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
    final IMicroDocument aDoc = MicroReader.readMicroXML (TEST_XML);
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
      final IMicroDocument aDoc = MicroReader.readMicroXML (TEST_XML);
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
    e.appendCDATA ("foobar");
    assertEquals ("<a><![CDATA[foobar]]></a>", MicroWriter.getNodeAsString (e, aSettings));

    // Containing the forbidden CDATA end marker
    e = new MicroElement ("a");
    e.appendCDATA ("a]]>b");
    assertEquals ("<a><![CDATA[a]]]]><![CDATA[>b]]></a>", MicroWriter.getNodeAsString (e, aSettings));

    // Containing more than one forbidden CDATA end marker
    e = new MicroElement ("a");
    e.appendCDATA ("a]]>b]]>c");
    assertEquals ("<a><![CDATA[a]]]]><![CDATA[>b]]]]><![CDATA[>c]]></a>", MicroWriter.getNodeAsString (e, aSettings));

    // Containing a complete CDATA section
    e = new MicroElement ("a");
    e.appendCDATA ("a<![CDATA[x]]>b");
    assertEquals ("<a><![CDATA[a<![CDATA[x]]]]><![CDATA[>b]]></a>", MicroWriter.getNodeAsString (e, aSettings));
  }

  @Test
  public void testWithNamespaceContext ()
  {
    final XMLWriterSettings aSettings = new XMLWriterSettings ().setIndent (EXMLSerializeIndent.NONE)
                                                                .setCharset (CCharset.CHARSET_ISO_8859_1_OBJ);
    final IMicroDocument aDoc = new MicroDocument ();
    final IMicroElement eRoot = aDoc.appendElement ("ns1url", "root");
    eRoot.appendElement ("ns2url", "child1");
    eRoot.appendElement ("ns2url", "child2").setAttribute ("attr1", "a");
    eRoot.appendElement ("ns3url", "child3").setAttribute ("ns3url", "attr1", "a");
    eRoot.appendElement ("ns3url", "child4").setAttribute ("ns4url", "attr1", "a");

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

    eRoot.appendElement ("ns3url", "zz");
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
                                                                .setCharset (CCharset.CHARSET_ISO_8859_1_OBJ);
    final IMicroDocument aDoc = new MicroDocument ();
    final IMicroElement eRoot = aDoc.appendElement ("ns1url", "root");
    eRoot.appendElement ("ns2url", "child1");
    eRoot.appendElement ("ns2url", "child2").setAttribute ("attr1", "a");
    eRoot.appendElement ("ns3url", "child3").setAttribute ("ns3url", "attr1", "a");
    eRoot.appendElement ("ns3url", "child4").setAttribute ("ns4url", "attr1", "a");

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
        aDoc.appendElement ("root").appendText (sText);
        final String sXML = MicroWriter.getNodeAsString (aDoc, aSettings);
        final IMicroDocument aDoc2 = MicroReader.readMicroXML (sXML);
        assertNotNull ("Failed to read with byte " + i + "\n" + sXML, aDoc2);
        assertEquals ("Length for byte " + i, i == 0 ? 6 : 7, aDoc2.getDocumentElement ().getTextContent ().length ());
        assertTrue ("Difference in byte 0x" + Integer.toHexString (i), aDoc.isEqualContent (aDoc2));
      }
  }

  @Test
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
        aDoc.appendElement ("root").appendCDATA (sText);
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
        aDoc.appendElement ("root").appendText (sText);
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
        aDoc.appendElement ("root").appendCDATA (sText);
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
                                                                .setCharset (CCharset.CHARSET_ISO_8859_1_OBJ);
    final IMicroDocument aDoc = new MicroDocument ();
    final IMicroElement eRoot = aDoc.appendElement ("ns1url", "root");
    eRoot.appendElement ("ns2url", "child1").setAttribute ("ns2url", "attr1", "value1");
    eRoot.appendElement ("ns2url", "child2").setAttribute ("ns3url", "attr2", "value2");

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
}
