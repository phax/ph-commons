/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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

import java.io.IOException;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.XMLConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.EntityResolver2;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ext.Locator2;

import com.helger.commons.CGlobal;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.error.level.EErrorLevel;
import com.helger.commons.error.level.IErrorLevel;
import com.helger.commons.location.SimpleLocation;
import com.helger.commons.string.StringHelper;
import com.helger.xml.microdom.IMicroCDATA;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroDocumentType;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.IMicroNode;
import com.helger.xml.microdom.IMicroText;
import com.helger.xml.microdom.MicroDocument;
import com.helger.xml.microdom.MicroDocumentType;
import com.helger.xml.sax.AbstractSAXErrorHandler;

/**
 * The SAX handler used by the {@link MicroReader}.
 *
 * @author Philip Helger
 */
public class MicroSAXHandler implements EntityResolver2, DTDHandler, ContentHandler, ErrorHandler, LexicalHandler
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (MicroSAXHandler.class);

  private IMicroDocument m_aDoc;
  private IMicroDocumentType m_aDocType;
  private IMicroNode m_aParent;
  private boolean m_bDTDMode = false;
  private boolean m_bCDATAMode = false;
  // Members
  private final boolean m_bSaveIgnorableWhitespaces;
  private final EntityResolver m_aEntityResolver;
  private final EntityResolver2 m_aEntityResolver2;
  private final boolean m_bTrackPosition;
  private Locator m_aLocator;
  private String m_sSourceXMLVersion;
  private String m_sSourceXMLEncoding;

  public MicroSAXHandler (final boolean bSaveIgnorableWhitespaces,
                          @Nullable final EntityResolver aEntityResolver,
                          final boolean bTrackPosition)
  {
    m_bSaveIgnorableWhitespaces = bSaveIgnorableWhitespaces;
    m_aEntityResolver = aEntityResolver;
    m_aEntityResolver2 = aEntityResolver instanceof EntityResolver2 ? (EntityResolver2) aEntityResolver : null;
    m_bTrackPosition = bTrackPosition;
  }

  private void _createParentDocument ()
  {
    if (m_aParent == null)
    {
      m_aDoc = new MicroDocument (m_aDocType);
      m_aParent = m_aDoc;
    }
  }

  private void _updatePosition (@Nonnull final String sWhat)
  {
    if (m_aLocator != null)
    {
      // Handle location
      final SimpleLocation aLocation = SimpleLocation.create (m_aLocator);
      if (false)
        if (s_aLogger.isInfoEnabled ())
          s_aLogger.info (sWhat + " " + aLocation.toString ());
    }
  }

  // Called before startDocument (if called)
  public void setDocumentLocator (@Nullable final Locator aLocator)
  {
    if (m_bTrackPosition)
    {
      m_aLocator = aLocator;
      _updatePosition ("setLocator");
      if (aLocator instanceof Locator2)
      {
        m_sSourceXMLVersion = ((Locator2) aLocator).getXMLVersion ();
        m_sSourceXMLEncoding = ((Locator2) aLocator).getEncoding ();
      }
    }
  }

  public void startDocument ()
  {
    _updatePosition ("startDocument");
  }

  public void endDocument ()
  {
    _updatePosition ("endDocument");
  }

  public void startDTD (final String sName, final String sPublicId, final String sSystemId) throws SAXException
  {
    _updatePosition ("startDTD");
    if (m_aDocType == null)
      m_aDocType = new MicroDocumentType (sName, sPublicId, sSystemId);
    else
      s_aLogger.warn ("DocType already present!");
    m_bDTDMode = true;
  }

  public void endDTD () throws SAXException
  {
    _updatePosition ("endDTD");
    m_bDTDMode = false;
  }

  public void startElement (@Nullable final String sNamespaceURI,
                            @Nonnull final String sLocalName,
                            @Nullable final String sQName,
                            @Nullable final Attributes aAttributes)
  {
    _updatePosition ("startElement");
    _createParentDocument ();

    IMicroElement aElement;
    if (StringHelper.hasText (sNamespaceURI))
      aElement = m_aParent.appendElement (sNamespaceURI, sLocalName);
    else
      aElement = m_aParent.appendElement (sLocalName);

    // copy attributes
    if (aAttributes != null)
    {
      final int nAttrCount = aAttributes.getLength ();
      for (int i = 0; i < nAttrCount; ++i)
      {
        final String sAttrNamespaceURI = aAttributes.getURI (i);
        final String sAttrName = aAttributes.getLocalName (i);
        final String sAttrValue = aAttributes.getValue (i);

        // Ignore the "xmlns" attributes, as the SAX handler passes the correct
        // namespace URIs
        if (!sAttrName.startsWith (XMLConstants.XMLNS_ATTRIBUTE))
          aElement.setAttribute (sAttrNamespaceURI, sAttrName, sAttrValue);
      }
    }

    m_aParent = aElement;
  }

  public void endElement (final String sNamespaceURI, final String sLocalName, final String sQName)
  {
    _updatePosition ("endElement");

    // Go one level up in the stack
    m_aParent = m_aParent.getParent ();
  }

  public void processingInstruction (final String sTarget, final String sData)
  {
    _updatePosition ("processingInstruction");
    _createParentDocument ();
    m_aParent.appendProcessingInstruction (sTarget, sData);
  }

  public void characters (@Nonnull final char [] aChars, @Nonnegative final int nStart, @Nonnegative final int nLength)
  {
    _updatePosition ("characters");
    if (m_bCDATAMode)
    {
      // CDATA mode
      final IMicroNode aLastChild = m_aParent.getLastChild ();
      if (aLastChild != null && aLastChild.getType ().isCDATA ())
      {
        final IMicroCDATA aLastDATA = (IMicroCDATA) aLastChild;
        // Merge directly following text nodes to one node!
        // This may happen when compiling with JDK 1.6.0_04
        aLastDATA.appendData (aChars, nStart, nLength);
      }
      else
      {
        // Add to parent
        m_aParent.appendCDATA (aChars, nStart, nLength);
      }
    }
    else
    {
      // Regular text node
      final IMicroNode aLastChild = m_aParent.getLastChild ();
      if (aLastChild != null && aLastChild.getType ().isText ())
      {
        final IMicroText aLastText = (IMicroText) aLastChild;
        if (!aLastText.isElementContentWhitespace ())
        {
          // Merge directly following text nodes to one node!
          // This may happen when compiling with JDK 1.6.0_04
          aLastText.appendData (aChars, nStart, nLength);
        }
        else
        {
          // Add to parent
          m_aParent.appendText (aChars, nStart, nLength);
        }
      }
      else
      {
        // Add to parent
        m_aParent.appendText (aChars, nStart, nLength);
      }
    }
  }

  public void comment (@Nonnull final char [] aChars,
                       @Nonnegative final int nStart,
                       @Nonnegative final int nLength) throws SAXException
  {
    _updatePosition ("comment");
    // Ignore comments in DTD
    if (!m_bDTDMode)
    {
      // In case the comment comes before the root element....
      _createParentDocument ();

      m_aParent.appendComment (aChars, nStart, nLength);
    }
  }

  public void ignorableWhitespace (@Nonnull final char [] aChars,
                                   @Nonnegative final int nStart,
                                   @Nonnegative final int nLength)
  {
    _updatePosition ("ignorableWhitespace");
    if (m_bSaveIgnorableWhitespaces)
    {
      final IMicroNode aLastChild = m_aParent.getLastChild ();
      if (aLastChild != null && aLastChild.getType ().isText ())
      {
        final IMicroText aLastText = (IMicroText) aLastChild;
        if (aLastText.isElementContentWhitespace ())
        {
          // Merge directly following text nodes to one node!
          // This may happen when compiling with JDK 1.6.0_04
          aLastText.appendData (aChars, nStart, nLength);
        }
        else
          m_aParent.appendIgnorableWhitespaceText (aChars, nStart, nLength);
      }
      else
        m_aParent.appendIgnorableWhitespaceText (aChars, nStart, nLength);
    }
  }

  @Nullable
  public InputSource resolveEntity (final String sPublicId, final String sSystemId) throws IOException, SAXException
  {
    _updatePosition ("resolveEntity");
    final EntityResolver aER = m_aEntityResolver;
    if (aER != null)
      return aER.resolveEntity (sPublicId, sSystemId);

    // If using XHTML this should be replaced by using the LocalEntityResolver
    // instead
    if (sPublicId == null)
    {
      if (s_aLogger.isInfoEnabled ())
        s_aLogger.info ("Need to resolve entity with system ID '" + sSystemId + "'");
    }
    else
      if (sSystemId == null)
      {
        if (s_aLogger.isInfoEnabled ())
          s_aLogger.info ("Need to resolve entity with public ID '" + sPublicId + "'");
      }
      else
      {
        if (s_aLogger.isInfoEnabled ())
          s_aLogger.info ("Need to resolve entity with public ID '" +
                          sPublicId +
                          "' and system ID '" +
                          sSystemId +
                          "'");
      }
    return null;
  }

  @Nullable
  public InputSource getExternalSubset (final String sName, @Nullable final String sBaseURI) throws SAXException,
                                                                                             IOException
  {
    _updatePosition ("getExternalSubset");
    final EntityResolver2 aER2 = m_aEntityResolver2;
    if (aER2 != null)
      return aER2.getExternalSubset (sName, sBaseURI);
    return null;
  }

  @Nullable
  public InputSource resolveEntity (@Nullable final String sName,
                                    @Nullable final String sPublicId,
                                    @Nullable final String sBaseURI,
                                    @Nonnull final String sSystemId) throws SAXException, IOException
  {
    _updatePosition ("resolveEntity2");
    final EntityResolver2 aER2 = m_aEntityResolver2;
    if (aER2 != null)
      return aER2.resolveEntity (sName, sPublicId, sBaseURI, sSystemId);

    final EntityResolver aER = m_aEntityResolver;
    if (aER != null)
      return aER.resolveEntity (sPublicId, sSystemId);

    if (s_aLogger.isInfoEnabled ())
      s_aLogger.info ("Need to resolve entity with name '" +
                      sName +
                      "', public ID '" +
                      sPublicId +
                      "' base URI '" +
                      sBaseURI +
                      "' and system ID '" +
                      sSystemId +
                      "'");
    return null;
  }

  public void unparsedEntityDecl (final String sName,
                                  final String sPublicId,
                                  final String sSystemId,
                                  final String sNotationName)
  {
    _updatePosition ("unparsedEntityDecl");
    if (s_aLogger.isWarnEnabled ())
      s_aLogger.warn ("Unparsed entity decl: " + sName + "--" + sPublicId + "--" + sSystemId + "--" + sNotationName);
  }

  public void notationDecl (final String sName, final String sPublicId, final String sSystemId) throws SAXException
  {
    _updatePosition ("notationDecl");
    if (s_aLogger.isWarnEnabled ())
      s_aLogger.warn ("Unparsed notation decl: " + sName + "--" + sPublicId + "--" + sSystemId);
  }

  public void skippedEntity (final String sName)
  {
    _updatePosition ("skippedEntity");
    if (s_aLogger.isWarnEnabled ())
      s_aLogger.warn ("Skipped entity: " + sName);
  }

  public void startEntity (final String sName) throws SAXException
  {
    _updatePosition ("startEntity");
    if (false)
      if (s_aLogger.isWarnEnabled ())
        s_aLogger.warn ("Start entity: " + sName);
  }

  public void endEntity (final String sName) throws SAXException
  {
    _updatePosition ("endEntity");
    if (false)
      if (s_aLogger.isWarnEnabled ())
        s_aLogger.warn ("End entity: " + sName);
  }

  public void startCDATA () throws SAXException
  {
    _updatePosition ("startCDATA");
    // Begin of CDATA
    m_bCDATAMode = true;
  }

  public void endCDATA () throws SAXException
  {
    _updatePosition ("endCDATA");
    // End of CDATA
    m_bCDATAMode = false;
  }

  // For namespace handling
  public void startPrefixMapping (@Nonnull final String sPrefix,
                                  @Nonnull final String sNamespaceURI) throws SAXException
  {}

  // for namespace handling
  public void endPrefixMapping (@Nonnull final String sPrefix) throws SAXException
  {}

  @Nonnull
  @Nonempty
  private static String _getMsg (@Nonnull final IErrorLevel aErrorLevel, @Nonnull final SAXParseException ex)
  {
    return AbstractSAXErrorHandler.getSaxParseError (aErrorLevel, ex).getAsString (CGlobal.DEFAULT_LOCALE);
  }

  public void warning (final SAXParseException ex)
  {
    if (s_aLogger.isWarnEnabled ())
      s_aLogger.warn (_getMsg (EErrorLevel.WARN, ex));
  }

  public void error (final SAXParseException ex)
  {
    if (s_aLogger.isErrorEnabled ())
      s_aLogger.error (_getMsg (EErrorLevel.ERROR, ex));
  }

  public void fatalError (final SAXParseException ex)
  {
    if (s_aLogger.isErrorEnabled ())
      s_aLogger.error (_getMsg (EErrorLevel.FATAL_ERROR, ex));
  }

  /**
   * @return The created and filled micro document. May be <code>null</code> if
   *         no document start event came in.
   */
  @Nullable
  public IMicroDocument getDocument ()
  {
    return m_aDoc;
  }

  /**
   * @return The XML version read. May be <code>null</code>.
   */
  @Nullable
  public String getSourceXMLVersion ()
  {
    return m_sSourceXMLVersion;
  }

  /**
   * @return The source encoding of the read XML. May be <code>null</code>.
   */
  @Nullable
  public String getSourceXMLEncoding ()
  {
    return m_sSourceXMLEncoding;
  }
}
