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
package com.helger.xml.serialize.write;

import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.WillNotClose;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.state.ETriState;
import com.helger.base.string.StringHelper;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsTreeMap;
import com.helger.collection.commons.ICommonsSortedMap;
import com.helger.xml.CXML;
import com.helger.xml.EXMLVersion;
import com.helger.xml.microdom.IMicroDocumentType;

/**
 * Converts XML constructs into a string representation.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class XMLEmitter implements AutoCloseable, Flushable
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
  private final IXMLWriterSettings m_aXMLWriterSettings;
  // Status vars
  private EXMLSerializeVersion m_eXMLVersion;
  private final char m_cAttrValueBoundary;
  private final EXMLCharMode m_eAttrValueCharMode;
  private final boolean m_bOrderAttributesAndNamespaces;

  /**
   * Define whether nested XML comments throw an exception or not.
   *
   * @param bThrowExceptionOnNestedComments
   *        <code>true</code> to throw an exception, <code>false</code> to ignore nested comments.
   */
  public static void setThrowExceptionOnNestedComments (final boolean bThrowExceptionOnNestedComments)
  {
    s_bThrowExceptionOnNestedComments = bThrowExceptionOnNestedComments;
  }

  /**
   * @return <code>true</code> if nested XML comments will throw an error. Default is
   *         {@value #DEFAULT_THROW_EXCEPTION_ON_NESTED_COMMENTS}.
   */
  public static boolean isThrowExceptionOnNestedComments ()
  {
    return s_bThrowExceptionOnNestedComments;
  }

  public XMLEmitter (@NonNull @WillNotClose final Writer aWriter, @NonNull final IXMLWriterSettings aSettings)
  {
    m_aWriter = ValueEnforcer.notNull (aWriter, "Writer");
    m_aXMLWriterSettings = ValueEnforcer.notNull (aSettings, "Settings");
    m_eXMLVersion = aSettings.getSerializeVersion ();
    m_cAttrValueBoundary = aSettings.isUseDoubleQuotesForAttributes () ? '"' : '\'';
    m_eAttrValueCharMode = aSettings.isUseDoubleQuotesForAttributes () ? EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES
                                                                       : EXMLCharMode.ATTRIBUTE_VALUE_SINGLE_QUOTES;
    m_bOrderAttributesAndNamespaces = aSettings.isOrderAttributesAndNamespaces ();
  }

  /**
   * @return The XML writer settings as provided in the constructor. Never <code>null</code>.
   */
  @NonNull
  public IXMLWriterSettings getXMLWriterSettings ()
  {
    return m_aXMLWriterSettings;
  }

  @NonNull
  private XMLEmitter _append (@NonNull final String sValue)
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

  @NonNull
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

  @NonNull
  private XMLEmitter _append (final char [] aValue, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    try
    {
      m_aWriter.write (aValue, nOfs, nLen);
      return this;
    }
    catch (final IOException ex)
    {
      throw new IllegalStateException ("Failed to append character '" + new String (aValue, nOfs, nLen) + "'", ex);
    }
  }

  @NonNull
  private XMLEmitter _appendMasked (@NonNull final EXMLCharMode eXMLCharMode, @Nullable final String sValue)
  {
    try
    {
      XMLMaskHelper.maskXMLTextTo (m_eXMLVersion,
                                   eXMLCharMode,
                                   m_aXMLWriterSettings.getIncorrectCharacterHandling (),
                                   sValue,
                                   m_aWriter);
      return this;
    }
    catch (final IOException ex)
    {
      throw new IllegalStateException ("Failed to append masked string '" + sValue + "'", ex);
    }
  }

  @NonNull
  private XMLEmitter _appendMasked (@NonNull final EXMLCharMode eXMLCharMode,
                                    final char @NonNull [] aText,
                                    @Nonnegative final int nOfs,
                                    @Nonnegative final int nLen)
  {
    try
    {
      XMLMaskHelper.maskXMLTextTo (m_eXMLVersion,
                                   eXMLCharMode,
                                   m_aXMLWriterSettings.getIncorrectCharacterHandling (),
                                   aText,
                                   nOfs,
                                   nLen,
                                   m_aWriter);
      return this;
    }
    catch (final IOException ex)
    {
      throw new IllegalStateException ("Failed to append masked char[] '" + new String (aText, nOfs, nLen) + "'", ex);
    }
  }

  @NonNull
  private XMLEmitter _appendAttrValue (@Nullable final String sValue)
  {
    return _append (m_cAttrValueBoundary)._appendMasked (m_eAttrValueCharMode, sValue)._append (m_cAttrValueBoundary);
  }

  @NonNull
  public XMLEmitter newLine ()
  {
    if (m_aXMLWriterSettings.getIndent ().isAlign ())
      _append (m_aXMLWriterSettings.getNewLineString ());
    return this;
  }

  /**
   * At the very beginning of the document (XML declaration).
   *
   * @param eXMLVersion
   *        The XML version to use. If <code>null</code> is passed, {@link EXMLVersion#XML_10} will
   *        be used.
   * @param sEncoding
   *        The encoding to be used for this document. It may be <code>null</code> but it is
   *        strongly recommended to write a correct charset.
   * @param eStandalone
   *        if <code>true</code> this is a standalone XML document without a connection to an
   *        existing DTD or XML schema
   * @param bWithNewLine
   *        <code>true</code> to add a newline, <code>false</code> if not
   * @since 9.2.1
   */
  public void onXMLDeclaration (@Nullable final EXMLVersion eXMLVersion,
                                @Nullable final String sEncoding,
                                @NonNull final ETriState eStandalone,
                                final boolean bWithNewLine)
  {
    if (eXMLVersion != null)
    {
      // Maybe switch from 1.0 to 1.1 or vice versa at the very beginning of the
      // document
      m_eXMLVersion = EXMLSerializeVersion.getFromXMLVersionOrThrow (eXMLVersion);
    }

    _append (PI_START)._append ("xml");

    // May be null for HTML
    final String sVersionString = m_eXMLVersion.getXMLVersionString ();
    if (StringHelper.isNotEmpty (sVersionString))
      _append (" version=")._appendAttrValue (sVersionString);

    if (StringHelper.isNotEmpty (sEncoding))
      _append (" encoding=")._appendAttrValue (sEncoding);

    if (eStandalone.isDefined ())
      _append (" standalone=")._appendAttrValue (eStandalone.isTrue () ? "yes" : "no");

    _append (PI_END);
    if (bWithNewLine)
      newLine ();
  }

  /**
   * Write a DTD section. This string represents the entire doctypedecl production from the XML 1.0
   * specification.
   *
   * @param sDTD
   *        the DTD to be written. May not be <code>null</code>.
   */
  public void onDTD (@NonNull final String sDTD)
  {
    _append (sDTD);
    newLine ();
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
  @NonNull
  public static String getDocTypeHTMLRepresentation (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                     @NonNull final EXMLIncorrectCharacterHandling eIncorrectCharHandling,
                                                     @NonNull final IMicroDocumentType aDocType)
  {
    return getDocTypeXMLRepresentation (eXMLVersion,
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
   *        The optional public ID. May be <code>null</code>. If the public ID is not
   *        <code>null</code> the system ID must also be set!
   * @param sSystemID
   *        The optional system ID. May be <code>null</code>.
   * @return The string DOCTYPE representation.
   */
  @NonNull
  public static String getDocTypeXMLRepresentation (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                    @NonNull final EXMLIncorrectCharacterHandling eIncorrectCharHandling,
                                                    @NonNull final String sQualifiedName,
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

  /**
   * On XML document type.
   *
   * @param sQualifiedElementName
   *        Qualified name of the root element.
   * @param sPublicID
   *        Document type public ID
   * @param sSystemID
   *        Document type system ID
   */
  public void onDocumentType (@NonNull final String sQualifiedElementName,
                              @Nullable final String sPublicID,
                              @Nullable final String sSystemID)
  {
    ValueEnforcer.notNull (sQualifiedElementName, "QualifiedElementName");

    final String sDocType = getDocTypeXMLRepresentation (m_eXMLVersion,
                                                         m_aXMLWriterSettings.getIncorrectCharacterHandling (),
                                                         sQualifiedElementName,
                                                         sPublicID,
                                                         sSystemID);
    _append (sDocType);
    newLine ();
  }

  /**
   * On processing instruction
   *
   * @param sTarget
   *        The target
   * @param sData
   *        The data (attributes as a string)
   */
  public void onProcessingInstruction (@NonNull final String sTarget, @Nullable final String sData)
  {
    _append (PI_START)._append (sTarget);
    if (StringHelper.isNotEmpty (sData))
      _append (' ')._append (sData);
    _append (PI_END);
    newLine ();
  }

  /**
   * On entity reference.
   *
   * @param sEntityRef
   *        The reference (without '&amp;' and ';' !!)
   */
  public void onEntityReference (@NonNull final String sEntityRef)
  {
    _append (ER_START)._append (sEntityRef)._append (ER_END);
  }

  /**
   * Ignorable whitespace characters.
   *
   * @param aWhitespaces
   *        The whitespace character sequence
   */
  public void onContentElementWhitespace (@Nullable final CharSequence aWhitespaces)
  {
    if (StringHelper.isNotEmpty (aWhitespaces))
      _append (aWhitespaces.toString ());
  }

  /**
   * Comment node.
   *
   * @param sComment
   *        The comment text
   */
  public void onComment (@Nullable final String sComment)
  {
    if (StringHelper.isNotEmpty (sComment))
    {
      if (isThrowExceptionOnNestedComments ())
        if (sComment.contains (COMMENT_START) || sComment.contains (COMMENT_END))
          throw new IllegalArgumentException ("XML comment contains nested XML comment: " + sComment);

      _append (COMMENT_START)._append (sComment)._append (COMMENT_END);
    }
  }

  /**
   * XML text node.
   *
   * @param sText
   *        The contained text
   */
  public void onText (@Nullable final String sText)
  {
    onText (sText, true);
  }

  /**
   * XML text node.
   *
   * @param aText
   *        The contained text array
   * @param nOfs
   *        Offset into the array where to start
   * @param nLen
   *        Number of chars to use, starting from the provided offset.
   */
  public void onText (final char @NonNull [] aText, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    onText (aText, nOfs, nLen, true);
  }

  /**
   * Text node.
   *
   * @param sText
   *        The contained text
   * @param bEscape
   *        If <code>true</code> the text should be XML masked (the default), <code>false</code> if
   *        not. The <code>false</code> case is especially interesting for HTML inline JS and CSS
   *        code.
   */
  public void onText (@Nullable final String sText, final boolean bEscape)
  {
    if (bEscape)
      _appendMasked (EXMLCharMode.TEXT, sText);
    else
      _append (sText);
  }

  /**
   * Text node.
   *
   * @param aText
   *        The contained text array
   * @param nOfs
   *        Offset into the array where to start
   * @param nLen
   *        Number of chars to use, starting from the provided offset.
   * @param bEscape
   *        If <code>true</code> the text should be XML masked (the default), <code>false</code> if
   *        not. The <code>false</code> case is especially interesting for HTML inline JS and CSS
   *        code.
   */
  public void onText (final char @NonNull [] aText,
                      @Nonnegative final int nOfs,
                      @Nonnegative final int nLen,
                      final boolean bEscape)
  {
    if (bEscape)
      _appendMasked (EXMLCharMode.TEXT, aText, nOfs, nLen);
    else
      _append (aText, nOfs, nLen);
  }

  /**
   * CDATA node.
   *
   * @param sText
   *        The contained text
   */
  public void onCDATA (@Nullable final String sText)
  {
    if (StringHelper.isNotEmpty (sText))
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

  public void elementStartOpen (@Nullable final String sNamespacePrefix, @NonNull final String sTagName)
  {
    _append ('<');
    if (StringHelper.isNotEmpty (sNamespacePrefix))
    {
      // We have an element namespace prefix
      _appendMasked (EXMLCharMode.ELEMENT_NAME, sNamespacePrefix)._append (CXML.XML_PREFIX_NAMESPACE_SEP);
    }
    _appendMasked (EXMLCharMode.ELEMENT_NAME, sTagName);
  }

  public void elementAttr (@Nullable final String sAttrNamespacePrefix,
                           @NonNull final String sAttrName,
                           @NonNull final String sAttrValue)
  {
    _append (' ');
    if (StringHelper.isNotEmpty (sAttrNamespacePrefix))
    {
      // We have an attribute namespace prefix
      _append (sAttrNamespacePrefix)._append (CXML.XML_PREFIX_NAMESPACE_SEP);
    }
    _appendMasked (EXMLCharMode.ATTRIBUTE_NAME, sAttrName)._append ('=')._appendAttrValue (sAttrValue);
  }

  public void elementStartClose (@NonNull final EXMLSerializeBracketMode eBracketMode)
  {
    if (eBracketMode.isSelfClosed ())
    {
      // Note: according to HTML compatibility guideline a space should be added
      // before the self-closing
      _append (m_aXMLWriterSettings.isSpaceOnSelfClosedElement () ? " />" : "/>");
    }
    else
    {
      // Either "open close" or "open only"
      _append ('>');
    }
  }

  /**
   * Start of an element.
   *
   * @param sNamespacePrefix
   *        Optional namespace prefix. May be <code>null</code>.
   * @param sTagName
   *        Tag name
   * @param aAttrs
   *        Optional set of attributes.
   * @param eBracketMode
   *        Bracket mode to use. Never <code>null</code>.
   */
  public void onElementStart (@Nullable final String sNamespacePrefix,
                              @NonNull final String sTagName,
                              @Nullable final Map <QName, String> aAttrs,
                              @NonNull final EXMLSerializeBracketMode eBracketMode)
  {
    elementStartOpen (sNamespacePrefix, sTagName);

    if (aAttrs != null && !aAttrs.isEmpty ())
    {
      if (m_bOrderAttributesAndNamespaces)
      {
        // first separate in NS and non-NS attributes
        // Sort namespace attributes by assigned prefix
        final ICommonsSortedMap <QName, String> aNamespaceAttrs = new CommonsTreeMap <> (CXML.getComparatorQNameForNamespacePrefix ());
        final ICommonsSortedMap <QName, String> aNonNamespaceAttrs = new CommonsTreeMap <> (CXML.getComparatorQNameNamespaceURIBeforeLocalPart ());
        for (final Map.Entry <QName, String> aEntry : aAttrs.entrySet ())
        {
          final QName aAttrName = aEntry.getKey ();
          if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals (aAttrName.getNamespaceURI ()))
            aNamespaceAttrs.put (aAttrName, aEntry.getValue ());
          else
            aNonNamespaceAttrs.put (aAttrName, aEntry.getValue ());
        }
        // emit namespace attributes first
        for (final Map.Entry <QName, String> aEntry : aNamespaceAttrs.entrySet ())
        {
          final QName aAttrName = aEntry.getKey ();
          elementAttr (aAttrName.getPrefix (), aAttrName.getLocalPart (), aEntry.getValue ());
        }
        // emit non-namespace attributes afterwards
        for (final Map.Entry <QName, String> aEntry : aNonNamespaceAttrs.entrySet ())
        {
          final QName aAttrName = aEntry.getKey ();
          elementAttr (aAttrName.getPrefix (), aAttrName.getLocalPart (), aEntry.getValue ());
        }
      }
      else
      {
        // assuming that the order of the passed attributes is consistent!
        // Emit all attributes
        for (final Map.Entry <QName, String> aEntry : aAttrs.entrySet ())
        {
          final QName aAttrName = aEntry.getKey ();
          elementAttr (aAttrName.getPrefix (), aAttrName.getLocalPart (), aEntry.getValue ());
        }
      }
    }

    elementStartClose (eBracketMode);
  }

  /**
   * End of an element.
   *
   * @param sNamespacePrefix
   *        Optional namespace prefix. May be <code>null</code>.
   * @param sTagName
   *        Tag name
   * @param eBracketMode
   *        Bracket mode to use. Never <code>null</code>.
   */
  public void onElementEnd (@Nullable final String sNamespacePrefix,
                            @NonNull final String sTagName,
                            @NonNull final EXMLSerializeBracketMode eBracketMode)
  {
    if (eBracketMode.isOpenClose ())
    {
      _append ("</");
      if (StringHelper.isNotEmpty (sNamespacePrefix))
        _appendMasked (EXMLCharMode.ELEMENT_NAME, sNamespacePrefix)._append (CXML.XML_PREFIX_NAMESPACE_SEP);
      _appendMasked (EXMLCharMode.ELEMENT_NAME, sTagName)._append ('>');
    }
  }

  public void flush () throws IOException
  {
    m_aWriter.flush ();
  }

  public void close () throws IOException
  {
    m_aWriter.close ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Writer", m_aWriter)
                                       .append ("XMLWriterSettings", m_aXMLWriterSettings)
                                       .append ("XMLVersion", m_eXMLVersion)
                                       .append ("AttrValueBoundary", m_cAttrValueBoundary)
                                       .append ("AttrValueCharMode", m_eAttrValueCharMode)
                                       .getToString ();
  }
}
