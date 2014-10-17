/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.xml.serialize;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillNotClose;
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.namespace.QName;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.microdom.IMicroDocumentType;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.xml.CXML;
import com.helger.commons.xml.DefaultXMLIterationHandler;
import com.helger.commons.xml.EXMLCharMode;
import com.helger.commons.xml.EXMLIncorrectCharacterHandling;
import com.helger.commons.xml.EXMLVersion;

/**
 * Converts XML constructs into a string representation.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class XMLEmitter extends DefaultXMLIterationHandler
{
  /** By default an exception is thrown for nested comments */
  public static final boolean DEFAULT_THROW_EXCEPTION_ON_NESTED_COMMENTS = true;
  public static final String CDATA_START = "<![CDATA[";
  public static final String CDATA_END = "]]>";
  public static final String COMMENT_START = "<!--";
  public static final String COMMENT_END = "-->";
  public static final char ER_START = '&';
  public static final char ER_END = ';';
  public static final String PI_START = "<?";
  public static final String PI_END = "?>";

  private static boolean s_bThrowExceptionOnNestedComments = DEFAULT_THROW_EXCEPTION_ON_NESTED_COMMENTS;

  private final Writer m_aWriter;
  private final IXMLWriterSettings m_aSettings;
  private EXMLSerializeVersion m_eXMLVersion;
  private final char m_cAttrValueBoundary;
  private final EXMLCharMode m_eAttrValueCharMode;

  public XMLEmitter (@Nonnull @WillNotClose final Writer aWriter, @Nonnull final IXMLWriterSettings aSettings)
  {
    m_aWriter = ValueEnforcer.notNull (aWriter, "Writer");
    m_aSettings = ValueEnforcer.notNull (aSettings, "Settings");
    if (aSettings.getFormat ().isHTML ())
      m_eXMLVersion = EXMLSerializeVersion.HTML;
    else
      if (aSettings.getFormat ().isXHTML ())
        m_eXMLVersion = EXMLSerializeVersion.XHTML;
      else
        m_eXMLVersion = EXMLSerializeVersion.getFromXMLVersionOrThrow (aSettings.getXMLVersion ());
    m_cAttrValueBoundary = aSettings.isUseDoubleQuotesForAttributes () ? '"' : '\'';
    m_eAttrValueCharMode = aSettings.isUseDoubleQuotesForAttributes () ? EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES
                                                                      : EXMLCharMode.ATTRIBUTE_VALUE_SINGLE_QUOTES;
  }

  /**
   * Define whether nested XML comments throw an exception or not.
   *
   * @param bThrowExceptionOnNestedComments
   *        <code>true</code> to throw an exception, <code>false</code> to
   *        ignore nested comments.
   */
  public static void setThrowExceptionOnNestedComments (final boolean bThrowExceptionOnNestedComments)
  {
    s_bThrowExceptionOnNestedComments = bThrowExceptionOnNestedComments;
  }

  /**
   * @return <code>true</code> if nested XML comments will throw an error.
   *         Default is {@value #DEFAULT_THROW_EXCEPTION_ON_NESTED_COMMENTS}.
   */
  public static boolean isThrowExceptionOnNestedComments ()
  {
    return s_bThrowExceptionOnNestedComments;
  }

  @Nonnull
  private XMLEmitter _append (@Nonnull final String sValue)
  {
    try
    {
      m_aWriter.write (sValue);
      return this;
    }
    catch (final IOException ex)
    {
      throw new IllegalStateException ("Failed to append string '" + sValue + "'", ex);
    }
  }

  @Nonnull
  private XMLEmitter _append (final char cValue)
  {
    try
    {
      m_aWriter.write (cValue);
      return this;
    }
    catch (final IOException ex)
    {
      throw new IllegalStateException ("Failed to append character '" + cValue + "'", ex);
    }
  }

  @Nonnull
  private XMLEmitter _appendMasked (@Nonnull final EXMLCharMode eXMLCharMode, @Nullable final String sValue)
  {
    try
    {
      XMLMaskHelper.maskXMLTextTo (m_eXMLVersion,
                                   eXMLCharMode,
                                   m_aSettings.getIncorrectCharacterHandling (),
                                   sValue,
                                   m_aWriter);
      return this;
    }
    catch (final IOException ex)
    {
      throw new IllegalStateException ("Failed to append masked string '" + sValue + "'", ex);
    }
  }

  @Nonnull
  private XMLEmitter _appendAttrValue (@Nullable final String sValue)
  {
    return _append (m_cAttrValueBoundary)._appendMasked (m_eAttrValueCharMode, sValue)._append (m_cAttrValueBoundary);
  }

  @Override
  public void onDocumentStart (@Nullable final EXMLVersion eVersion,
                               @Nullable final String sEncoding,
                               final boolean bStandalone)
  {
    if (eVersion != null && m_eXMLVersion.isXML ())
      m_eXMLVersion = EXMLSerializeVersion.getFromXMLVersionOrThrow (eVersion);
    if (m_eXMLVersion.requiresXMLDeclaration ())
    {
      _append (PI_START)._append ("xml version=")._appendAttrValue (m_eXMLVersion.getXMLVersionString ());
      if (sEncoding != null)
        _append (" encoding=")._appendAttrValue (sEncoding);
      if (bStandalone)
        _append (" standalone=")._appendAttrValue ("yes");
      _append (PI_END);
      if (m_aSettings.getIndent ().isAlign ())
        _append (m_aSettings.getNewlineString ());
    }
  }

  /**
   * Get the XML representation of a document type.
   *
   * @param eXMLVersion
   *        The XML version to use. May not be <code>null</code>.
   * @param eIncorrectCharHandling
   *        The incorrect character handling. May not be <code>null</code>.
   * @param aDocType
   *        The structure document type. May not be <code>null</code>.
   * @return The string DOCTYPE representation.
   */
  @Nonnull
  public static String getDocTypeHTMLRepresentation (@Nonnull final EXMLSerializeVersion eXMLVersion,
                                                     @Nonnull final EXMLIncorrectCharacterHandling eIncorrectCharHandling,
                                                     @Nonnull final IMicroDocumentType aDocType)
  {
    return getDocTypeHTMLRepresentation (eXMLVersion,
                                         eIncorrectCharHandling,
                                         aDocType.getQualifiedName (),
                                         aDocType.getPublicID (),
                                         aDocType.getSystemID ());
  }

  /**
   * Get the XML representation of a document type.
   *
   * @param eXMLVersion
   *        The XML version to use. May not be <code>null</code>.
   * @param eIncorrectCharHandling
   *        The incorrect character handling. May not be <code>null</code>.
   * @param sQualifiedName
   *        The qualified element name. May not be <code>null</code>.
   * @param sPublicID
   *        The optional public ID. May be <code>null</code>. If the public ID
   *        is not <code>null</code> the system ID must also be set!
   * @param sSystemID
   *        The optional system ID. May be <code>null</code>.
   * @return The string DOCTYPE representation.
   */
  @Nonnull
  public static String getDocTypeHTMLRepresentation (@Nonnull final EXMLSerializeVersion eXMLVersion,
                                                     @Nonnull final EXMLIncorrectCharacterHandling eIncorrectCharHandling,
                                                     @Nonnull final String sQualifiedName,
                                                     @Nullable final String sPublicID,
                                                     @Nullable final String sSystemID)
  {
    // do not return a line break at the end! (JS variable assignment)
    final StringBuilder aSB = new StringBuilder (128);
    aSB.append ("<!DOCTYPE ").append (sQualifiedName);
    if (sPublicID != null && sSystemID != null)
    {
      // Public and system ID present
      aSB.append (" PUBLIC \"")
         .append (XMLMaskHelper.getMaskedXMLText (eXMLVersion,
                                                  EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                  eIncorrectCharHandling,
                                                  sPublicID))
         .append ("\" \"")
         .append (XMLMaskHelper.getMaskedXMLText (eXMLVersion,
                                                  EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                  eIncorrectCharHandling,
                                                  sSystemID))
         .append ('"');
    }
    else
      if (sSystemID != null)
      {
        // Only system ID present
        aSB.append (" SYSTEM \"")
           .append (XMLMaskHelper.getMaskedXMLText (eXMLVersion,
                                                    EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                    eIncorrectCharHandling,
                                                    sSystemID))
           .append ('"');
      }
    return aSB.append ('>').toString ();
  }

  @Override
  public void onDocumentType (@Nonnull final String sQualifiedElementName,
                              @Nullable final String sPublicID,
                              @Nullable final String sSystemID)
  {
    ValueEnforcer.notNull (sQualifiedElementName, "QualifiedElementName");

    final String sDocType = getDocTypeHTMLRepresentation (m_eXMLVersion,
                                                          m_aSettings.getIncorrectCharacterHandling (),
                                                          sQualifiedElementName,
                                                          sPublicID,
                                                          sSystemID);
    _append (sDocType);
    if (m_aSettings.getIndent ().isAlign ())
      _append (m_aSettings.getNewlineString ());
  }

  @Override
  public void onProcessingInstruction (@Nonnull final String sTarget, @Nullable final String sData)
  {
    _append (PI_START)._append (sTarget);
    if (StringHelper.hasText (sData))
      _append (' ')._append (sData);
    _append (PI_END);
    if (m_aSettings.getIndent ().isAlign ())
      _append (m_aSettings.getNewlineString ());
  }

  @Override
  public void onEntityReference (@Nonnull final String sEntityRef)
  {
    _append (ER_START)._append (sEntityRef)._append (ER_END);
  }

  @Override
  public void onContentElementWhitespace (@Nullable final CharSequence aWhitespaces)
  {
    if (StringHelper.hasText (aWhitespaces))
      _append (aWhitespaces.toString ());
  }

  @Override
  public void onComment (@Nullable final String sComment)
  {
    if (StringHelper.hasText (sComment))
    {
      if (isThrowExceptionOnNestedComments ())
        if (sComment.contains (COMMENT_START) || sComment.contains (COMMENT_END))
          throw new IllegalArgumentException ("XML comment contains nested XML comment: " + sComment);

      _append (COMMENT_START)._append (sComment)._append (COMMENT_END);
    }
  }

  @Override
  public void onText (@Nullable final String sText, final boolean bEscape)
  {
    if (bEscape)
      _appendMasked (EXMLCharMode.TEXT, sText);
    else
      _append (sText);
  }

  @Override
  public void onCDATA (@Nullable final String sText)
  {
    if (StringHelper.hasText (sText))
    {
      if (sText.indexOf (CDATA_END) >= 0)
      {
        // Split CDATA sections if they contain the illegal "]]>" marker
        final List <String> aParts = StringHelper.getExploded (CDATA_END, sText);
        final int nParts = aParts.size ();
        for (int i = 0; i < nParts; ++i)
        {
          _append (CDATA_START);
          if (i > 0)
            _append ('>');
          _appendMasked (EXMLCharMode.CDATA, aParts.get (i));
          if (i < nParts - 1)
            _append ("]]");
          _append (CDATA_END);
        }
      }
      else
      {
        // No special handling required
        _append (CDATA_START)._appendMasked (EXMLCharMode.CDATA, sText)._append (CDATA_END);
      }
    }
  }

  @Override
  public void onElementStart (@Nullable final String sNamespacePrefix,
                              @Nonnull final String sTagName,
                              @Nullable final Map <QName, String> aAttrs,
                              final boolean bHasChildren)
  {
    _append ('<');
    if (StringHelper.hasText (sNamespacePrefix))
      _appendMasked (EXMLCharMode.ELEMENT_NAME, sNamespacePrefix)._append (CXML.XML_PREFIX_NAMESPACE_SEP);
    _appendMasked (EXMLCharMode.ELEMENT_NAME, sTagName);
    if (aAttrs != null && !aAttrs.isEmpty ())
    {
      // assuming that the order of the passed attributes is consistent!
      // Emit all attributes
      for (final Map.Entry <QName, String> aEntry : aAttrs.entrySet ())
      {
        final QName aAttrName = aEntry.getKey ();
        final String sAttrNamespacePrefix = aAttrName.getPrefix ();
        final String sAttrName = aAttrName.getLocalPart ();

        if (StringHelper.hasText (sAttrNamespacePrefix))
        {
          // We have a namespace prefix
          _append (' ')._append (sAttrNamespacePrefix)._append (CXML.XML_PREFIX_NAMESPACE_SEP);
        }
        else
        {
          // No namespace prefix present
          _append (' ');
        }
        _appendMasked (EXMLCharMode.ATTRIBUTE_NAME, sAttrName)._append ('=')._appendAttrValue (aEntry.getValue ());
      }
    }

    if (m_aSettings.getFormat ().isHTML ())
    {
      // HTML has no self closed tags!
      _append ('>');
    }
    else
    {
      // Either leave tag open or close it
      // Note: according to HTML compatibility guideline a space should be added
      // before the self-closing
      _append (bHasChildren ? ">" : m_aSettings.isSpaceOnSelfClosedElement () ? " />" : "/>");
    }
  }

  @Override
  public void onElementEnd (@Nullable final String sNamespacePrefix,
                            @Nonnull final String sTagName,
                            final boolean bHasChildren)
  {
    boolean bPrintClosingTag;
    if (m_aSettings.getFormat ().isHTML ())
    {
      // In HTML all tags are closed, if not explicitly marked as empty
      bPrintClosingTag = bHasChildren || !HTMLdtd.isEmptyTag (sTagName);
    }
    else
      bPrintClosingTag = bHasChildren;

    if (bPrintClosingTag)
    {
      _append ("</");
      if (StringHelper.hasText (sNamespacePrefix))
        _appendMasked (EXMLCharMode.ELEMENT_NAME, sNamespacePrefix)._append (CXML.XML_PREFIX_NAMESPACE_SEP);
      _appendMasked (EXMLCharMode.ELEMENT_NAME, sTagName)._append ('>');
    }
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("writer", m_aWriter)
                                       .append ("settings", m_aSettings)
                                       .append ("version", m_eXMLVersion)
                                       .toString ();
  }
}
