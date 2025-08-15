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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.xml.namespace.NamespaceContext;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.equals.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.lang.ICloneable;
import com.helger.base.string.StringHex;
import com.helger.base.system.ENewLineMode;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.xml.EXMLVersion;
import com.helger.xml.namespace.MapBasedNamespaceContext;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Default implementation of the {@link IXMLWriterSettings} interface.<br>
 * Describes the export settings for the MicroWriter. Defaults to indented and aligned XML in the
 * UTF-8 charset.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class XMLWriterSettings implements IXMLWriterSettings, ICloneable <XMLWriterSettings>
{
  // Must be before the IXMLWriterSettings constants!
  /** Default default XML version is 1.0 */
  public static final EXMLSerializeVersion DEFAULT_XML_SERIALIZE_VERSION = EXMLSerializeVersion.XML_10;
  /** By default all fields of the XML declaration are emitted */
  public static final EXMLSerializeXMLDeclaration DEFAULT_SERIALIZE_XML_DECLARATION = EXMLSerializeXMLDeclaration.EMIT;
  /** By default a newline character is emitted afterwards */
  public static final boolean DEFAULT_NEW_LINE_AFTER_XML_DECLARATION = true;
  /** By default the document type is emitted */
  public static final EXMLSerializeDocType DEFAULT_SERIALIZE_DOC_TYPE = EXMLSerializeDocType.EMIT;
  /** By default comments are emitted */
  public static final EXMLSerializeComments DEFAULT_SERIALIZE_COMMENTS = EXMLSerializeComments.EMIT;
  /** By default the output is indented and aligned (newlines are added) */
  public static final EXMLSerializeIndent DEFAULT_SERIALIZE_INDENT = EXMLSerializeIndent.INDENT_AND_ALIGN;
  /**
   * By default invalid characters are not written and a log message is emitted
   */
  public static final EXMLIncorrectCharacterHandling DEFAULT_INCORRECT_CHARACTER_HANDLING = EXMLIncorrectCharacterHandling.DO_NOT_WRITE_LOG_WARNING;
  /** The default charset is UTF-8 */
  public static final Charset DEFAULT_XML_CHARSET_OBJ = StandardCharsets.UTF_8;
  /** The default charset is UTF-8 */
  public static final String DEFAULT_XML_CHARSET = DEFAULT_XML_CHARSET_OBJ.name ();
  /** By default double quotes are used to wrap attribute values */
  public static final boolean DEFAULT_USE_DOUBLE_QUOTES_FOR_ATTRIBUTES = true;
  /**
   * By default a leading space is inserted before a self closed element (e.g.
   * <code>&lt;b /&gt;</code> in contrast to <code>&lt;b/&gt;</code>).
   */
  public static final boolean DEFAULT_SPACE_ON_SELF_CLOSED_ELEMENT = true;
  /** By default indentation happens with 2 spaces */
  public static final String DEFAULT_INDENTATION_STRING = "  ";
  /**
   * By default namespaces are written.
   */
  public static final boolean DEFAULT_EMIT_NAMESPACES = true;
  /**
   * By default namespace context prefixes are put inside the root element
   */
  public static final boolean DEFAULT_PUT_NAMESPACE_CONTEXT_PREFIXES_IN_ROOT = false;
  /** By default CDATA children are emitted as CDATA */
  public static final boolean DEFAULT_WRITE_CDATA_AS_TEXT = false;
  /** By default the insertion order of attributes is maintained */
  public static final boolean DEFAULT_ORDER_ATTRIBUTES_AND_NAMESPACES = false;

  /** The default settings to use - last constant */
  public static final IXMLWriterSettings DEFAULT_XML_SETTINGS = new XMLWriterSettings ();

  private EXMLSerializeVersion m_eSerializeVersion = DEFAULT_XML_SERIALIZE_VERSION;
  private EXMLSerializeXMLDeclaration m_eSerializeXMLDecl = DEFAULT_SERIALIZE_XML_DECLARATION;
  private boolean m_bNewLineAfterXMLDeclaration = DEFAULT_NEW_LINE_AFTER_XML_DECLARATION;
  private EXMLSerializeDocType m_eSerializeDocType = DEFAULT_SERIALIZE_DOC_TYPE;
  private EXMLSerializeComments m_eSerializeComments = DEFAULT_SERIALIZE_COMMENTS;
  private EXMLSerializeIndent m_eIndent = DEFAULT_SERIALIZE_INDENT;
  private IXMLIndentDeterminator m_aIndentDeterminator = new XMLIndentDeterminatorXML ();
  private EXMLIncorrectCharacterHandling m_eIncorrectCharacterHandling = DEFAULT_INCORRECT_CHARACTER_HANDLING;
  private Charset m_aCharset = DEFAULT_XML_CHARSET_OBJ;
  private NamespaceContext m_aNamespaceContext = new MapBasedNamespaceContext ();
  private boolean m_bUseDoubleQuotesForAttributes = DEFAULT_USE_DOUBLE_QUOTES_FOR_ATTRIBUTES;
  private IXMLBracketModeDeterminator m_aBracketModeDeterminator = new XMLBracketModeDeterminatorXML ();
  private boolean m_bSpaceOnSelfClosedElement = DEFAULT_SPACE_ON_SELF_CLOSED_ELEMENT;
  private ENewLineMode m_eNewLineMode = ENewLineMode.DEFAULT;
  private String m_sIndentationString = DEFAULT_INDENTATION_STRING;
  private boolean m_bEmitNamespaces = DEFAULT_EMIT_NAMESPACES;
  private boolean m_bPutNamespaceContextPrefixesInRoot = DEFAULT_PUT_NAMESPACE_CONTEXT_PREFIXES_IN_ROOT;
  private boolean m_bWriteCDATAAsText = DEFAULT_WRITE_CDATA_AS_TEXT;
  private boolean m_bOrderAttributesAndNamespaces = DEFAULT_ORDER_ATTRIBUTES_AND_NAMESPACES;

  // Status vars
  private String m_sIndentationStringToString;

  /**
   * Creates a default settings object with the following settings:
   * <ul>
   * <li>XML version 1.0</li>
   * <li>with XML declaration</li>
   * <li>with document type</li>
   * <li>with comments</li>
   * <li>Indented and aligned</li>
   * <li>Writing invalid characters to the file as is - may result in invalid XML files</li>
   * <li>Default character set UTF-8</li>
   * <li>No namespace context</li>
   * </ul>
   */
  public XMLWriterSettings ()
  {}

  /**
   * Copy constructor.
   *
   * @param aOther
   *        The object to copy the settings from. May not be <code>null</code>.
   */
  public XMLWriterSettings (@Nonnull final IXMLWriterSettings aOther)
  {
    ValueEnforcer.notNull (aOther, "Other");

    setSerializeVersion (aOther.getSerializeVersion ());
    setSerializeXMLDeclaration (aOther.getSerializeXMLDeclaration ());
    setNewLineAfterXMLDeclaration (aOther.isNewLineAfterXMLDeclaration ());
    setSerializeDocType (aOther.getSerializeDocType ());
    setSerializeComments (aOther.getSerializeComments ());
    setIndent (aOther.getIndent ());
    setIndentDeterminator (aOther.getIndentDeterminator ());
    setIncorrectCharacterHandling (aOther.getIncorrectCharacterHandling ());
    setCharset (aOther.getCharset ());
    setNamespaceContext (aOther.getNamespaceContext ());
    setBracketModeDeterminator (aOther.getBracketModeDeterminator ());
    setUseDoubleQuotesForAttributes (aOther.isUseDoubleQuotesForAttributes ());
    setSpaceOnSelfClosedElement (aOther.isSpaceOnSelfClosedElement ());
    setNewLineMode (aOther.getNewLineMode ());
    setIndentationString (aOther.getIndentationString ());
    setEmitNamespaces (aOther.isEmitNamespaces ());
    setPutNamespaceContextPrefixesInRoot (aOther.isPutNamespaceContextPrefixesInRoot ());
    setWriteCDATAAsText (aOther.isWriteCDATAAsText ());
    setOrderAttributesAndNamespaces (aOther.isOrderAttributesAndNamespaces ());
  }

  @Nonnull
  public EXMLVersion getXMLVersion ()
  {
    return m_eSerializeVersion.getXMLVersionOrDefault (EXMLVersion.XML_10);
  }

  @Nonnull
  public EXMLSerializeVersion getSerializeVersion ()
  {
    return m_eSerializeVersion;
  }

  /**
   * Set the preferred XML version to use.
   *
   * @param eSerializeVersion
   *        The XML serialize version. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public final XMLWriterSettings setSerializeVersion (@Nonnull final EXMLSerializeVersion eSerializeVersion)
  {
    m_eSerializeVersion = ValueEnforcer.notNull (eSerializeVersion, "Version");
    return this;
  }

  @Nonnull
  public EXMLSerializeXMLDeclaration getSerializeXMLDeclaration ()
  {
    return m_eSerializeXMLDecl;
  }

  /**
   * Set the way how to handle the XML declaration.
   *
   * @param eSerializeXMLDecl
   *        XML declaration handling. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public final XMLWriterSettings setSerializeXMLDeclaration (@Nonnull final EXMLSerializeXMLDeclaration eSerializeXMLDecl)
  {
    m_eSerializeXMLDecl = ValueEnforcer.notNull (eSerializeXMLDecl, "SerializeXMLDecl");
    return this;
  }

  public boolean isNewLineAfterXMLDeclaration ()
  {
    return m_bNewLineAfterXMLDeclaration;
  }

  /**
   * Change whether a newline should be printed after the XML declaration or not.
   *
   * @param bNewLineAfterXMLDeclaration
   *        <code>true</code> to print it, <code>false</code> to not print a new line.
   * @return this for chaining
   */
  @Nonnull
  public final XMLWriterSettings setNewLineAfterXMLDeclaration (final boolean bNewLineAfterXMLDeclaration)
  {
    m_bNewLineAfterXMLDeclaration = bNewLineAfterXMLDeclaration;
    return this;
  }

  @Nonnull
  public EXMLSerializeDocType getSerializeDocType ()
  {
    return m_eSerializeDocType;
  }

  /**
   * Set the way how to handle the doc type.
   *
   * @param eSerializeDocType
   *        Doc type handling. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public final XMLWriterSettings setSerializeDocType (@Nonnull final EXMLSerializeDocType eSerializeDocType)
  {
    m_eSerializeDocType = ValueEnforcer.notNull (eSerializeDocType, "SerializeDocType");
    return this;
  }

  @Nonnull
  public EXMLSerializeComments getSerializeComments ()
  {
    return m_eSerializeComments;
  }

  /**
   * Set the way how comments should be handled.
   *
   * @param eSerializeComments
   *        The comment handling. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public final XMLWriterSettings setSerializeComments (@Nonnull final EXMLSerializeComments eSerializeComments)
  {
    m_eSerializeComments = ValueEnforcer.notNull (eSerializeComments, "SerializeComments");
    return this;
  }

  @Nonnull
  public EXMLSerializeIndent getIndent ()
  {
    return m_eIndent;
  }

  /**
   * Set the way how to indent/align
   *
   * @param eIndent
   *        Indent and align definition. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public final XMLWriterSettings setIndent (@Nonnull final EXMLSerializeIndent eIndent)
  {
    m_eIndent = ValueEnforcer.notNull (eIndent, "Indent");
    return this;
  }

  @Nonnull
  public IXMLIndentDeterminator getIndentDeterminator ()
  {
    return m_aIndentDeterminator;
  }

  /**
   * Set the dynamic (per-element) indent determinator to be used.
   *
   * @param aIndentDeterminator
   *        The object to use. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public final XMLWriterSettings setIndentDeterminator (@Nonnull final IXMLIndentDeterminator aIndentDeterminator)
  {
    m_aIndentDeterminator = ValueEnforcer.notNull (aIndentDeterminator, "IndentDeterminator");
    return this;
  }

  @Nonnull
  public EXMLIncorrectCharacterHandling getIncorrectCharacterHandling ()
  {
    return m_eIncorrectCharacterHandling;
  }

  /**
   * Set the way how to handle invalid characters.
   *
   * @param eIncorrectCharacterHandling
   *        The invalid character handling. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public final XMLWriterSettings setIncorrectCharacterHandling (@Nonnull final EXMLIncorrectCharacterHandling eIncorrectCharacterHandling)
  {
    m_eIncorrectCharacterHandling = ValueEnforcer.notNull (eIncorrectCharacterHandling, "IncorrectCharacterHandling");
    return this;
  }

  @Nonnull
  public Charset getCharset ()
  {
    return m_aCharset;
  }

  /**
   * Set the serialization charset.
   *
   * @param aCharset
   *        The charset to be used. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public final XMLWriterSettings setCharset (@Nonnull final Charset aCharset)
  {
    m_aCharset = ValueEnforcer.notNull (aCharset, "Charset");
    return this;
  }

  @Nonnull
  public NamespaceContext getNamespaceContext ()
  {
    return m_aNamespaceContext;
  }

  /**
   * Set the namespace context to be used.
   *
   * @param aNamespaceContext
   *        The namespace context to be used. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  public final XMLWriterSettings setNamespaceContext (@Nullable final NamespaceContext aNamespaceContext)
  {
    // A namespace context must always be present, to resolve default namespaces
    m_aNamespaceContext = aNamespaceContext != null ? aNamespaceContext : new MapBasedNamespaceContext ();
    return this;
  }

  public boolean isUseDoubleQuotesForAttributes ()
  {
    return m_bUseDoubleQuotesForAttributes;
  }

  @Nonnull
  public final XMLWriterSettings setUseDoubleQuotesForAttributes (final boolean bUseDoubleQuotesForAttributes)
  {
    m_bUseDoubleQuotesForAttributes = bUseDoubleQuotesForAttributes;
    return this;
  }

  @Nonnull
  public IXMLBracketModeDeterminator getBracketModeDeterminator ()
  {
    return m_aBracketModeDeterminator;
  }

  @Nonnull
  public final XMLWriterSettings setBracketModeDeterminator (@Nonnull final IXMLBracketModeDeterminator aBracketModeDeterminator)
  {
    ValueEnforcer.notNull (aBracketModeDeterminator, "BracketModeDeterminator");
    m_aBracketModeDeterminator = aBracketModeDeterminator;
    return this;
  }

  public boolean isSpaceOnSelfClosedElement ()
  {
    return m_bSpaceOnSelfClosedElement;
  }

  @Nonnull
  public final XMLWriterSettings setSpaceOnSelfClosedElement (final boolean bSpaceOnSelfClosedElement)
  {
    m_bSpaceOnSelfClosedElement = bSpaceOnSelfClosedElement;
    return this;
  }

  @Nonnull
  public ENewLineMode getNewLineMode ()
  {
    return m_eNewLineMode;
  }

  @Nonnull
  public final XMLWriterSettings setNewLineMode (@Nonnull final ENewLineMode eNewLineMode)
  {
    m_eNewLineMode = ValueEnforcer.notNull (eNewLineMode, "NewLineMode");
    return this;
  }

  @Nonnull
  @Nonempty
  public String getIndentationString ()
  {
    return m_sIndentationString;
  }

  @Nonnull
  public final XMLWriterSettings setIndentationString (@Nonnull @Nonempty final String sIndentationString)
  {
    m_sIndentationString = ValueEnforcer.notEmpty (sIndentationString, "IndentationString");
    m_sIndentationStringToString = null;
    return this;
  }

  public boolean isEmitNamespaces ()
  {
    return m_bEmitNamespaces;
  }

  @Nonnull
  public final XMLWriterSettings setEmitNamespaces (final boolean bEmitNamespaces)
  {
    m_bEmitNamespaces = bEmitNamespaces;
    return this;
  }

  public boolean isPutNamespaceContextPrefixesInRoot ()
  {
    return m_bPutNamespaceContextPrefixesInRoot;
  }

  @Nonnull
  public final XMLWriterSettings setPutNamespaceContextPrefixesInRoot (final boolean bPutNamespaceContextPrefixesInRoot)
  {
    m_bPutNamespaceContextPrefixesInRoot = bPutNamespaceContextPrefixesInRoot;
    return this;
  }

  public boolean isWriteCDATAAsText ()
  {
    return m_bWriteCDATAAsText;
  }

  @Nonnull
  public final XMLWriterSettings setWriteCDATAAsText (final boolean bWriteCDATAAsText)
  {
    m_bWriteCDATAAsText = bWriteCDATAAsText;
    return this;
  }

  public boolean isOrderAttributesAndNamespaces ()
  {
    return m_bOrderAttributesAndNamespaces;
  }

  @Nonnull
  public final XMLWriterSettings setOrderAttributesAndNamespaces (final boolean bOrderAttributesAndNamespaces)
  {
    m_bOrderAttributesAndNamespaces = bOrderAttributesAndNamespaces;
    return this;
  }

  @Nonnull
  public XMLWriterSettings getClone ()
  {
    return new XMLWriterSettings (this);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final XMLWriterSettings rhs = (XMLWriterSettings) o;
    final Object aObj1 = m_aNamespaceContext;
    // namespace context does not necessarily implement equals/hashCode
    return m_eSerializeVersion.equals (rhs.m_eSerializeVersion) &&
           m_eSerializeXMLDecl.equals (rhs.m_eSerializeXMLDecl) &&
           m_bNewLineAfterXMLDeclaration == rhs.m_bNewLineAfterXMLDeclaration &&
           m_eSerializeDocType.equals (rhs.m_eSerializeDocType) &&
           m_eSerializeComments.equals (rhs.m_eSerializeComments) &&
           m_eIndent.equals (rhs.m_eIndent) &&
           m_aIndentDeterminator.equals (rhs.m_aIndentDeterminator) &&
           m_eIncorrectCharacterHandling.equals (rhs.m_eIncorrectCharacterHandling) &&
           m_aCharset.equals (rhs.m_aCharset) &&
           EqualsHelper.equals (aObj1, rhs.m_aNamespaceContext) &&
           m_bUseDoubleQuotesForAttributes == rhs.m_bUseDoubleQuotesForAttributes &&
           m_aBracketModeDeterminator.equals (rhs.m_aBracketModeDeterminator) &&
           m_bSpaceOnSelfClosedElement == rhs.m_bSpaceOnSelfClosedElement &&
           m_eNewLineMode.equals (rhs.m_eNewLineMode) &&
           m_sIndentationString.equals (rhs.m_sIndentationString) &&
           m_bEmitNamespaces == rhs.m_bEmitNamespaces &&
           m_bPutNamespaceContextPrefixesInRoot == rhs.m_bPutNamespaceContextPrefixesInRoot &&
           m_bWriteCDATAAsText == rhs.m_bWriteCDATAAsText &&
           m_bOrderAttributesAndNamespaces == rhs.m_bOrderAttributesAndNamespaces;
  }

  @Override
  public int hashCode ()
  {
    // namespace context does not necessarily implement equals/hashCode
    return new HashCodeGenerator (this).append (m_eSerializeVersion)
                                       .append (m_eSerializeXMLDecl)
                                       .append (m_bNewLineAfterXMLDeclaration)
                                       .append (m_eSerializeDocType)
                                       .append (m_eSerializeComments)
                                       .append (m_eIndent)
                                       .append (m_aIndentDeterminator)
                                       .append (m_eIncorrectCharacterHandling)
                                       .append (m_aCharset)
                                       .append (m_aNamespaceContext)
                                       .append (m_bUseDoubleQuotesForAttributes)
                                       .append (m_aBracketModeDeterminator)
                                       .append (m_bSpaceOnSelfClosedElement)
                                       .append (m_eNewLineMode)
                                       .append (m_sIndentationString)
                                       .append (m_bEmitNamespaces)
                                       .append (m_bPutNamespaceContextPrefixesInRoot)
                                       .append (m_bWriteCDATAAsText)
                                       .append (m_bOrderAttributesAndNamespaces)
                                       .getHashCode ();
  }

  @Override
  public String toString ()
  {
    if (m_sIndentationStringToString == null)
      m_sIndentationStringToString = StringHex.getHexEncoded (m_sIndentationString.getBytes (StandardCharsets.ISO_8859_1));
    return new ToStringGenerator (this).append ("SerializeVersion", m_eSerializeVersion)
                                       .append ("SerializeXMLDecl", m_eSerializeXMLDecl)
                                       .append ("NewLineAfterXMLDeclaration", m_bNewLineAfterXMLDeclaration)
                                       .append ("SerializeDocType", m_eSerializeDocType)
                                       .append ("SerializeComments", m_eSerializeComments)
                                       .append ("Indent", m_eIndent)
                                       .append ("IndentDeterminator", m_aIndentDeterminator)
                                       .append ("IncorrectCharHandling", m_eIncorrectCharacterHandling)
                                       .append ("Charset", m_aCharset)
                                       .append ("NamespaceContext", m_aNamespaceContext)
                                       .append ("DoubleQuotesForAttrs", m_bUseDoubleQuotesForAttributes)
                                       .append ("BracketModeDeterminator", m_aBracketModeDeterminator)
                                       .append ("SpaceOnSelfClosedElement", m_bSpaceOnSelfClosedElement)
                                       .append ("NewlineMode", m_eNewLineMode)
                                       .append ("IndentationString", m_sIndentationStringToString)
                                       .append ("EmitNamespaces", m_bEmitNamespaces)
                                       .append ("PutNamespaceContextPrefixesInRoot",
                                                m_bPutNamespaceContextPrefixesInRoot)
                                       .append ("WriteCDATAAsText", m_bWriteCDATAAsText)
                                       .append ("OrderAttributesAndNamespaces", m_bOrderAttributesAndNamespaces)
                                       .getToString ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public static XMLWriterSettings createForHTML4 ()
  {
    return new XMLWriterSettings ().setSerializeVersion (EXMLSerializeVersion.HTML)
                                   .setSerializeXMLDeclaration (EXMLSerializeXMLDeclaration.IGNORE)
                                   .setIndentDeterminator (new XMLIndentDeterminatorHTML ())
                                   .setBracketModeDeterminator (new XMLBracketModeDeterminatorHTML4 ())
                                   .setSpaceOnSelfClosedElement (true)
                                   .setPutNamespaceContextPrefixesInRoot (true);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static XMLWriterSettings createForXHTML ()
  {
    return new XMLWriterSettings ().setSerializeVersion (EXMLSerializeVersion.HTML)
                                   .setSerializeXMLDeclaration (EXMLSerializeXMLDeclaration.IGNORE)
                                   .setIndentDeterminator (new XMLIndentDeterminatorHTML ())
                                   .setBracketModeDeterminator (new XMLBracketModeDeterminatorXML ())
                                   .setSpaceOnSelfClosedElement (true)
                                   .setPutNamespaceContextPrefixesInRoot (true);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static XMLWriterSettings createForHTML5 ()
  {
    return new XMLWriterSettings ().setSerializeVersion (EXMLSerializeVersion.HTML)
                                   .setSerializeXMLDeclaration (EXMLSerializeXMLDeclaration.IGNORE)
                                   .setIndentDeterminator (new XMLIndentDeterminatorHTML ())
                                   .setBracketModeDeterminator (new XMLBracketModeDeterminatorHTML5 ())
                                   .setSpaceOnSelfClosedElement (true)
                                   .setPutNamespaceContextPrefixesInRoot (true);
  }

  /**
   * The canonical form of an XML document is physical representation of the document produced by
   * the method described in this specification. The changes are summarized in the following list:
   * <ul>
   * <li>The document is encoded in UTF-8</li>
   * <li>Line breaks normalized to #xA on input, before parsing</li>
   * <li>Attribute values are normalized, as if by a validating processor</li>
   * <li>Character and parsed entity references are replaced</li>
   * <li>CDATA sections are replaced with their character content</li>
   * <li>The XML declaration and document type declaration are removed</li>
   * <li>Empty elements are converted to start-end tag pairs</li>
   * <li>Whitespace outside of the document element and within start and end tags is normalized</li>
   * <li>All whitespace in character content is retained (excluding characters removed during line
   * feed normalization)</li>
   * <li>Attribute value delimiters are set to quotation marks (double quotes)</li>
   * <li>Special characters in attribute values and character content are replaced by character
   * references</li>
   * <li>Superfluous namespace declarations are removed from each element</li>
   * <li>Default attributes are added to each element</li>
   * <li>Fixup of xml:base attributes [C14N-Issues] is performed</li>
   * <li>Lexicographic order is imposed on the namespace declarations and attributes of each
   * element</li>
   * </ul>
   *
   * @return {@link XMLWriterSettings} for canonicalization
   */
  @Nonnull
  @ReturnsMutableCopy
  public static XMLWriterSettings createForCanonicalization ()
  {
    // TODO some Canonicalization settings are missing
    return new XMLWriterSettings ().setSerializeVersion (EXMLSerializeVersion.XML_10)
                                   .setSerializeXMLDeclaration (EXMLSerializeXMLDeclaration.IGNORE)
                                   .setSerializeDocType (EXMLSerializeDocType.IGNORE)
                                   .setCharset (StandardCharsets.UTF_8)
                                   .setNewLineMode (ENewLineMode.UNIX)
                                   .setUseDoubleQuotesForAttributes (true)
                                   .setBracketModeDeterminator (new XMLBracketModeDeterminatorXMLC14 ())
                                   .setWriteCDATAAsText (true)
                                   .setOrderAttributesAndNamespaces (true);
  }
}
