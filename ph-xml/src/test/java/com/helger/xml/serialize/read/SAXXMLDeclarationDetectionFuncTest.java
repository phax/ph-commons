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
package com.helger.xml.serialize.read;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.jspecify.annotations.NonNull;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ext.Locator2;
import org.xml.sax.helpers.DefaultHandler;

import com.helger.base.state.ESuccess;
import com.helger.xml.EXMLParserFeature;
import com.helger.xml.EXMLParserProperty;
import com.helger.xml.sax.StringSAXInputSource;

/**
 * Integration test that explores whether the SAX parser exposes enough information to decide if an
 * XML instance starts with an XML declaration ( <code>&lt;?xml ...?&gt;</code>) or not.
 * <p>
 * Candidates from the SAX API tried here:
 * <ol>
 * <li>{@link org.xml.sax.ContentHandler#declaration(String, String, String)} — added in SAX 2.0.2
 * (JDK 14+). <strong>This is the answer:</strong> it fires once if and only if the XML declaration
 * is present, and reports the literal <code>version</code>, <code>encoding</code> and
 * <code>standalone</code> from the declaration (with <code>null</code> for fields the declaration
 * omitted).</li>
 * <li>{@link Locator2#getXMLVersion()} / {@link Locator2#getEncoding()} captured in
 * {@link DefaultHandler#startDocument()} — unreliable; see findings below.</li>
 * <li>{@link org.xml.sax.ContentHandler#processingInstruction(String, String)} — the XML
 * declaration syntactically looks like a PI but per spec is not one, so this never fires for
 * it.</li>
 * <li>{@link LexicalHandler} and {@link DeclHandler} — neither has a matching method.
 * {@link DeclHandler} is the DTD-internal declaration handler (attribute/element/entity), not an
 * XML-declaration handler.</li>
 * </ol>
 * <p>
 * Findings observed against the JDK default SAX implementation (see the JUL output of the
 * individual tests for the raw values):
 * <ul>
 * <li><strong>{@code ContentHandler.declaration()} is the reliable signal.</strong> It is called
 * exactly once if an XML declaration is present, and the parameter values reflect the literal
 * declaration content.</li>
 * <li>{@code processingInstruction()} is not called for the XML declaration; {@link LexicalHandler}
 * has no matching callback; {@link DeclHandler} only fires for DTD-internal declarations.</li>
 * <li>{@link Locator2#getEncoding()} reports the encoding the parser is <em>using</em> — for byte
 * input that is the auto-detected or declaration-supplied encoding (so it is non-<code>null</code>
 * even when no XML declaration is present), and for character-stream input it is always
 * <code>null</code>. It therefore cannot be used to detect the presence of an XML declaration.</li>
 * <li>{@link Locator2#getXMLVersion()} consistently returns <code>"1.0"</code> with the JDK default
 * parser — even when the declaration says <code>version="1.1"</code>. So the version field cannot
 * be used to detect a declaration either.</li>
 * </ul>
 *
 * @author Philip Helger
 */
public final class SAXXMLDeclarationDetectionFuncTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (SAXXMLDeclarationDetectionFuncTest.class);

  /**
   * Captures everything that <em>might</em> reveal the presence of an XML declaration:
   * {@link Locator2}, processing instructions, lexical events and DTD-declaration events.
   */
  private static final class DeclarationCapturingHandler extends DefaultHandler implements LexicalHandler, DeclHandler
  {
    private Locator m_aLocator;
    boolean m_bLocatorIsLocator2;
    String m_sLocatorXMLVersion;
    String m_sLocatorEncoding;
    int m_nProcessingInstructionCount;
    int m_nLexicalEventCount;
    int m_nDeclHandlerEventCount;
    int m_nDeclarationCount;
    String m_sDeclaredVersion;
    String m_sDeclaredEncoding;
    String m_sDeclaredStandalone;

    @Override
    public void setDocumentLocator (final Locator aLocator)
    {
      m_aLocator = aLocator;
    }

    @Override
    public void declaration (final String sVersion, final String sEncoding, final String sStandalone)
                                                                                                      throws SAXException
    {
      m_nDeclarationCount++;
      m_sDeclaredVersion = sVersion;
      m_sDeclaredEncoding = sEncoding;
      m_sDeclaredStandalone = sStandalone;
      LOGGER.info ("  declaration(): version='" +
                   sVersion +
                   "' encoding='" +
                   sEncoding +
                   "' standalone='" +
                   sStandalone +
                   "'");
    }

    @Override
    public void startDocument ()
    {
      if (m_aLocator instanceof final Locator2 aL2)
      {
        m_bLocatorIsLocator2 = true;
        m_sLocatorXMLVersion = aL2.getXMLVersion ();
        m_sLocatorEncoding = aL2.getEncoding ();
      }
    }

    @Override
    public void processingInstruction (final String sTarget, final String sData)
    {
      m_nProcessingInstructionCount++;
      LOGGER.info ("  PI: target='" + sTarget + "' data='" + sData + "'");
    }

    // LexicalHandler
    @Override
    public void startDTD (final String sName, final String sPublicId, final String sSystemId)
    {
      m_nLexicalEventCount++;
      LOGGER.info ("  startDTD name='" + sName + "'");
    }

    @Override
    public void endDTD ()
    {
      m_nLexicalEventCount++;
    }

    @Override
    public void startEntity (final String sName)
    {
      m_nLexicalEventCount++;
    }

    @Override
    public void endEntity (final String sName)
    {
      m_nLexicalEventCount++;
    }

    @Override
    public void startCDATA ()
    {
      m_nLexicalEventCount++;
    }

    @Override
    public void endCDATA ()
    {
      m_nLexicalEventCount++;
    }

    @Override
    public void comment (final char [] aCh, final int nStart, final int nLength)
    {
      m_nLexicalEventCount++;
    }

    // DeclHandler
    @Override
    public void elementDecl (final String sName, final String sModel)
    {
      m_nDeclHandlerEventCount++;
      LOGGER.info ("  elementDecl name='" + sName + "' model='" + sModel + "'");
    }

    @Override
    public void attributeDecl (final String sEName,
                               final String sAName,
                               final String sType,
                               final String sMode,
                               final String sValue)
    {
      m_nDeclHandlerEventCount++;
      LOGGER.info ("  attributeDecl elem='" + sEName + "' attr='" + sAName + "'");
    }

    @Override
    public void internalEntityDecl (final String sName, final String sValue)
    {
      m_nDeclHandlerEventCount++;
    }

    @Override
    public void externalEntityDecl (final String sName, final String sPublicId, final String sSystemId)
    {
      m_nDeclHandlerEventCount++;
    }
  }

  @NonNull
  private static DeclarationCapturingHandler _parse (@NonNull final InputSource aSource,
                                                     @NonNull final String sLabel,
                                                     final boolean bAllowDoctype)
  {
    final DeclarationCapturingHandler aHandler = new DeclarationCapturingHandler ();
    final SAXReaderSettings aSettings = new SAXReaderSettings ().setContentHandler (aHandler)
                                                                .setLexicalHandler (aHandler)
                                                                .setDeclarationHandler (aHandler);
    if (bAllowDoctype)
      aSettings.setFeatureValue (EXMLParserFeature.DISALLOW_DOCTYPE_DECL, false);
    final ESuccess eSuccess = SAXReader.readXMLSAX (aSource, aSettings);
    assertTrue ("SAX parse failed for: " + sLabel, eSuccess.isSuccess ());
    LOGGER.info (sLabel +
                 " -> Locator2=" +
                 aHandler.m_bLocatorIsLocator2 +
                 ", version=" +
                 aHandler.m_sLocatorXMLVersion +
                 ", encoding=" +
                 aHandler.m_sLocatorEncoding +
                 ", PI#=" +
                 aHandler.m_nProcessingInstructionCount +
                 ", lex#=" +
                 aHandler.m_nLexicalEventCount +
                 ", decl#=" +
                 aHandler.m_nDeclHandlerEventCount +
                 ", declaration()#=" +
                 aHandler.m_nDeclarationCount);
    return aHandler;
  }

  @NonNull
  private static DeclarationCapturingHandler _parse (@NonNull final InputSource aSource, @NonNull final String sLabel)
  {
    return _parse (aSource, sLabel, false);
  }

  // --- Character-stream input across declaration variants.

  @Test
  public void testCharStream_NoDeclaration ()
  {
    final DeclarationCapturingHandler aH = _parse (new StringSAXInputSource ("<root/>"), "char/no-decl");
    assertTrue (aH.m_bLocatorIsLocator2);
    // declaration() is NOT called when no XML declaration is present.
    assertEquals (0, aH.m_nDeclarationCount);
    // Reliable: XML declaration is never delivered as a processing instruction
    // or via lexical/declaration handlers.
    assertEquals (0, aH.m_nProcessingInstructionCount);
    assertEquals (0, aH.m_nLexicalEventCount);
    assertEquals (0, aH.m_nDeclHandlerEventCount);
  }

  @Test
  public void testCharStream_DeclarationVersionOnly ()
  {
    final DeclarationCapturingHandler aH = _parse (new StringSAXInputSource ("<?xml version=\"1.0\"?><root/>"),
                                                   "char/version-only");
    assertTrue (aH.m_bLocatorIsLocator2);
    // declaration() fires once with the literal version and null for omitted fields.
    assertEquals (1, aH.m_nDeclarationCount);
    assertEquals ("1.0", aH.m_sDeclaredVersion);
    assertNull (aH.m_sDeclaredEncoding);
    assertNull (aH.m_sDeclaredStandalone);
    assertEquals (0, aH.m_nProcessingInstructionCount);
    assertEquals (0, aH.m_nLexicalEventCount);
    assertEquals (0, aH.m_nDeclHandlerEventCount);
  }

  @Test
  public void testCharStream_DeclarationVersionAndEncoding ()
  {
    final DeclarationCapturingHandler aH = _parse (new StringSAXInputSource ("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root/>"),
                                                   "char/version+encoding");
    assertTrue (aH.m_bLocatorIsLocator2);
    assertEquals (1, aH.m_nDeclarationCount);
    assertEquals ("1.0", aH.m_sDeclaredVersion);
    assertEquals ("UTF-8", aH.m_sDeclaredEncoding);
    assertNull (aH.m_sDeclaredStandalone);
    assertEquals (0, aH.m_nProcessingInstructionCount);
  }

  @Test
  public void testCharStream_DeclarationVersion11 ()
  {
    final DeclarationCapturingHandler aH = _parse (new StringSAXInputSource ("<?xml version=\"1.1\" encoding=\"UTF-8\"?><root/>"),
                                                   "char/v1.1");
    assertTrue (aH.m_bLocatorIsLocator2);
    // declaration() reports the literal "1.1" - unlike Locator2.getXMLVersion()
    // which keeps reporting "1.0" with the JDK default parser.
    assertEquals (1, aH.m_nDeclarationCount);
    assertEquals ("1.1", aH.m_sDeclaredVersion);
    assertEquals ("UTF-8", aH.m_sDeclaredEncoding);
    assertEquals (0, aH.m_nProcessingInstructionCount);
  }

  // --- Byte-stream input across declaration variants.

  @Test
  public void testByteStream_NoDeclaration ()
  {
    final DeclarationCapturingHandler aH = _parse (new StringSAXInputSource ("<root/>"), "byte/no-decl");
    assertTrue (aH.m_bLocatorIsLocator2);
    assertEquals (0, aH.m_nDeclarationCount);
    assertEquals (0, aH.m_nProcessingInstructionCount);
    assertEquals (0, aH.m_nLexicalEventCount);
    assertEquals (0, aH.m_nDeclHandlerEventCount);
  }

  @Test
  public void testByteStream_DeclarationVersionOnly ()
  {
    final DeclarationCapturingHandler aH = _parse (new StringSAXInputSource ("<?xml version=\"1.0\"?><root/>"),
                                                   "byte/version-only");
    assertTrue (aH.m_bLocatorIsLocator2);
    assertEquals (1, aH.m_nDeclarationCount);
    assertEquals ("1.0", aH.m_sDeclaredVersion);
    // declaration() reports null encoding because the declaration omitted it -
    // contrast with Locator2.getEncoding() which reports the auto-detected
    // "UTF-8" here.
    assertNull (aH.m_sDeclaredEncoding);
    assertEquals (0, aH.m_nProcessingInstructionCount);
  }

  @Test
  public void testByteStream_DeclarationVersionAndEncoding ()
  {
    final DeclarationCapturingHandler aH = _parse (new StringSAXInputSource ("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root/>"),
                                                   "byte/version+encoding");
    assertTrue (aH.m_bLocatorIsLocator2);
    assertEquals (1, aH.m_nDeclarationCount);
    assertEquals ("1.0", aH.m_sDeclaredVersion);
    assertEquals ("UTF-8", aH.m_sDeclaredEncoding);
    assertEquals (0, aH.m_nProcessingInstructionCount);
  }

  @Test
  public void testByteStream_DeclarationVersionAndEncodingMixedCase ()
  {
    final DeclarationCapturingHandler aH = _parse (new StringSAXInputSource ("<?xml version=\"1.0\" encoding=\"Utf-8\"?><root/>"),
                                                   "byte/version+encoding");
    assertTrue (aH.m_bLocatorIsLocator2);
    assertEquals (1, aH.m_nDeclarationCount);
    assertEquals ("1.0", aH.m_sDeclaredVersion);
    assertEquals ("Utf-8", aH.m_sDeclaredEncoding);
    assertEquals (0, aH.m_nProcessingInstructionCount);
  }

  // --- DeclHandler fires for DTD-internal declarations only - never for the
  // XML declaration. DOCTYPE must be enabled explicitly because the default
  // settings disable it for XXE safety.

  @Test
  public void testDeclHandlerSeesDTDDeclarationsButNotXMLDeclaration ()
  {
    final String sXML = "<?xml version=\"1.0\"?>" +
                        "<!DOCTYPE root [" +
                        "  <!ELEMENT root EMPTY>" +
                        "  <!ATTLIST root id CDATA #IMPLIED>" +
                        "]>" +
                        "<root/>";
    final DeclarationCapturingHandler aH = _parse (new StringSAXInputSource (sXML), "char/dtd-decls", true);
    // DeclHandler fires for the DTD declarations - element + attribute = 2.
    assertEquals (2, aH.m_nDeclHandlerEventCount);
    // LexicalHandler fires for startDTD + endDTD.
    assertTrue ("LexicalHandler should fire for the DTD, got " + aH.m_nLexicalEventCount, aH.m_nLexicalEventCount >= 2);
    // The XML declaration is still reported via ContentHandler.declaration()
    // and not via processingInstruction().
    assertEquals (1, aH.m_nDeclarationCount);
    assertEquals (0, aH.m_nProcessingInstructionCount);
  }

  // --- Regular processing instructions DO fire on processingInstruction(); the
  // XML declaration alone never does. This contrasts the two cases.

  @Test
  public void testRegularPIFiresButXMLDeclarationDoesNot ()
  {
    final DeclarationCapturingHandler aH = _parse (new StringSAXInputSource ("<?xml version=\"1.0\"?>" +
                                                                             "<?xml-stylesheet type=\"text/xsl\" href=\"x.xsl\"?>" +
                                                                             "<root/>"), "char/pi+decl");
    // Only the xml-stylesheet PI is reported via processingInstruction();
    // the XML declaration goes to declaration() instead.
    assertEquals (1, aH.m_nProcessingInstructionCount);
    assertEquals (1, aH.m_nDeclarationCount);
  }

  // --- Sanity check that the parser-property constant exists for callers that
  // want to query the document's XML version directly. Note: the property has
  // the same ambiguity as Locator2.getXMLVersion() and so cannot be used to
  // detect a declaration either.

  @Test
  public void testParserPropertyDocumentXmlVersionConstantExists ()
  {
    assertNotNull (EXMLParserProperty.SAX_XML_VERSION);
    assertEquals ("http://xml.org/sax/properties/document-xml-version", EXMLParserProperty.SAX_XML_VERSION.getName ());
  }
}
