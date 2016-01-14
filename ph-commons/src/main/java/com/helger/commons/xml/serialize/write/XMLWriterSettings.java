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

import java.nio.charset.Charset;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.namespace.NamespaceContext;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.charset.CCharset;
import com.helger.commons.charset.CharsetManager;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.lang.ICloneable;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.system.ENewLineMode;
import com.helger.commons.xml.EXMLVersion;
import com.helger.commons.xml.namespace.MapBasedNamespaceContext;

/**
 * Default implementation of the {@link IXMLWriterSettings} interface.<br>
 * Describes the export settings for the MicroWriter. Defaults to indented and
 * aligned XML in the UTF-8 charset.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class XMLWriterSettings implements IXMLWriterSettings, ICloneable <XMLWriterSettings>
{
  // Must be before the IXMLWriterSettings constants!
  /** The default charset is UTF-8 */
  public static final String DEFAULT_XML_CHARSET = CCharset.CHARSET_UTF_8;
  /** The default charset is UTF-8 */
  public static final Charset DEFAULT_XML_CHARSET_OBJ = CCharset.CHARSET_UTF_8_OBJ;
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

  /** The default settings to use */
  public static final IXMLWriterSettings DEFAULT_XML_SETTINGS = new XMLWriterSettings ();

  private EXMLVersion m_eXMLVersion = EXMLVersion.XML_10;
  private EXMLSerializeVersion m_eSerializeVersion = EXMLSerializeVersion.XML_10;
  private EXMLSerializeXMLDeclaration m_eSerializeXMLDecl = EXMLSerializeXMLDeclaration.EMIT;
  private EXMLSerializeDocType m_eSerializeDocType = EXMLSerializeDocType.EMIT;
  private EXMLSerializeComments m_eSerializeComments = EXMLSerializeComments.EMIT;
  private EXMLSerializeIndent m_eIndent = EXMLSerializeIndent.INDENT_AND_ALIGN;
  private IXMLIndentDeterminator m_aIndentDeterminator = new XMLIndentDeterminatorXML ();
  private EXMLIncorrectCharacterHandling m_eIncorrectCharacterHandling = EXMLIncorrectCharacterHandling.DO_NOT_WRITE_LOG_WARNING;
  private Charset m_aCharset = DEFAULT_XML_CHARSET_OBJ;
  private NamespaceContext m_aNamespaceContext = new MapBasedNamespaceContext ();
  private boolean m_bUseDoubleQuotesForAttributes = DEFAULT_USE_DOUBLE_QUOTES_FOR_ATTRIBUTES;
  private IXMLBracketModeDeterminator m_aBracketModeDeterminator = new XMLBracketModeDeterminatorXML ();
  private boolean m_bSpaceOnSelfClosedElement = DEFAULT_SPACE_ON_SELF_CLOSED_ELEMENT;
  private ENewLineMode m_eNewLineMode = ENewLineMode.DEFAULT;
  private String m_sIndentationString = DEFAULT_INDENTATION_STRING;
  private boolean m_bEmitNamespaces = DEFAULT_EMIT_NAMESPACES;
  private boolean m_bPutNamespaceContextPrefixesInRoot = DEFAULT_PUT_NAMESPACE_CONTEXT_PREFIXES_IN_ROOT;

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
   * <li>Writing invalid characters to the file as is - may result in invalid
   * XML files</li>
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
    setSerializeDocType (aOther.getSerializeDocType ());
    setSerializeComments (aOther.getSerializeComments ());
    setIndent (aOther.getIndent ());
    setIndentDeterminator (aOther.getIndentDeterminator ());
    setIncorrectCharacterHandling (aOther.getIncorrectCharacterHandling ());
    setCharset (aOther.getCharsetObj ());
    setNamespaceContext (aOther.getNamespaceContext ());
    setBracketModeDeterminator (aOther.getBracketModeDeterminator ());
    setUseDoubleQuotesForAttributes (aOther.isUseDoubleQuotesForAttributes ());
    setSpaceOnSelfClosedElement (aOther.isSpaceOnSelfClosedElement ());
    setNewLineMode (aOther.getNewLineMode ());
    setIndentationString (aOther.getIndentationString ());
    setEmitNamespaces (aOther.isEmitNamespaces ());
    setPutNamespaceContextPrefixesInRoot (aOther.isPutNamespaceContextPrefixesInRoot ());
  }

  @Nonnull
  public EXMLVersion getXMLVersion ()
  {
    return m_eXMLVersion;
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
    m_eXMLVersion = eSerializeVersion.getXMLVersionOrDefault (EXMLVersion.XML_10);
    return this;
  }

  @Nonnull
  public EXMLSerializeVersion getSerializeVersion ()
  {
    return m_eSerializeVersion;
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

  @Nonnull
  public EXMLSerializeXMLDeclaration getSerializeXMLDeclaration ()
  {
    return m_eSerializeXMLDecl;
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
  public EXMLSerializeDocType getSerializeDocType ()
  {
    return m_eSerializeDocType;
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
  public EXMLSerializeComments getSerializeComments ()
  {
    return m_eSerializeComments;
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
  public EXMLSerializeIndent getIndent ()
  {
    return m_eIndent;
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
  public IXMLIndentDeterminator getIndentDeterminator ()
  {
    return m_aIndentDeterminator;
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
  public EXMLIncorrectCharacterHandling getIncorrectCharacterHandling ()
  {
    return m_eIncorrectCharacterHandling;
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
  public String getCharset ()
  {
    return m_aCharset.name ();
  }

  @Nonnull
  public Charset getCharsetObj ()
  {
    return m_aCharset;
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

  @Nonnull
  public NamespaceContext getNamespaceContext ()
  {
    return m_aNamespaceContext;
  }

  @Nonnull
  public final XMLWriterSettings setUseDoubleQuotesForAttributes (final boolean bUseDoubleQuotesForAttributes)
  {
    m_bUseDoubleQuotesForAttributes = bUseDoubleQuotesForAttributes;
    return this;
  }

  public boolean isUseDoubleQuotesForAttributes ()
  {
    return m_bUseDoubleQuotesForAttributes;
  }

  @Nonnull
  public final XMLWriterSettings setBracketModeDeterminator (@Nonnull final IXMLBracketModeDeterminator aBracketModeDeterminator)
  {
    ValueEnforcer.notNull (aBracketModeDeterminator, "BracketModeDeterminator");
    m_aBracketModeDeterminator = aBracketModeDeterminator;
    return this;
  }

  @Nonnull
  public IXMLBracketModeDeterminator getBracketModeDeterminator ()
  {
    return m_aBracketModeDeterminator;
  }

  @Nonnull
  public final XMLWriterSettings setSpaceOnSelfClosedElement (final boolean bSpaceOnSelfClosedElement)
  {
    m_bSpaceOnSelfClosedElement = bSpaceOnSelfClosedElement;
    return this;
  }

  public boolean isSpaceOnSelfClosedElement ()
  {
    return m_bSpaceOnSelfClosedElement;
  }

  @Nonnull
  public final XMLWriterSettings setNewLineMode (@Nonnull final ENewLineMode eNewLineMode)
  {
    m_eNewLineMode = ValueEnforcer.notNull (eNewLineMode, "NewLineMode");
    return this;
  }

  @Nonnull
  public ENewLineMode getNewLineMode ()
  {
    return m_eNewLineMode;
  }

  @Nonnull
  @Nonempty
  public String getNewLineString ()
  {
    return m_eNewLineMode.getText ();
  }

  @Nonnull
  public final XMLWriterSettings setIndentationString (@Nonnull @Nonempty final String sIndentationString)
  {
    m_sIndentationString = ValueEnforcer.notEmpty (sIndentationString, "IndentationString");
    m_sIndentationStringToString = null;
    return this;
  }

  @Nonnull
  @Nonempty
  public String getIndentationString ()
  {
    return m_sIndentationString;
  }

  @Nonnull
  public final XMLWriterSettings setEmitNamespaces (final boolean bEmitNamespaces)
  {
    m_bEmitNamespaces = bEmitNamespaces;
    return this;
  }

  public boolean isEmitNamespaces ()
  {
    return m_bEmitNamespaces;
  }

  @Nonnull
  public final XMLWriterSettings setPutNamespaceContextPrefixesInRoot (final boolean bPutNamespaceContextPrefixesInRoot)
  {
    m_bPutNamespaceContextPrefixesInRoot = bPutNamespaceContextPrefixesInRoot;
    return this;
  }

  public boolean isPutNamespaceContextPrefixesInRoot ()
  {
    return m_bPutNamespaceContextPrefixesInRoot;
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
    // namespace context does not necessarily implement equals/hashCode
    return m_eXMLVersion.equals (rhs.m_eXMLVersion) &&
           m_eSerializeXMLDecl.equals (rhs.m_eSerializeXMLDecl) &&
           m_eSerializeDocType.equals (rhs.m_eSerializeDocType) &&
           m_eSerializeComments.equals (rhs.m_eSerializeComments) &&
           m_eIndent.equals (rhs.m_eIndent) &&
           m_aIndentDeterminator.equals (rhs.m_aIndentDeterminator) &&
           m_eIncorrectCharacterHandling.equals (rhs.m_eIncorrectCharacterHandling) &&
           m_aCharset.equals (rhs.m_aCharset) &&
           EqualsHelper.equals (m_aNamespaceContext, rhs.m_aNamespaceContext) &&
           m_bUseDoubleQuotesForAttributes == rhs.m_bUseDoubleQuotesForAttributes &&
           m_aBracketModeDeterminator.equals (rhs.m_aBracketModeDeterminator) &&
           m_bSpaceOnSelfClosedElement == rhs.m_bSpaceOnSelfClosedElement &&
           m_eNewLineMode.equals (rhs.m_eNewLineMode) &&
           m_sIndentationString.equals (rhs.m_sIndentationString) &&
           m_bEmitNamespaces == rhs.m_bEmitNamespaces &&
           m_bPutNamespaceContextPrefixesInRoot == rhs.m_bPutNamespaceContextPrefixesInRoot;
  }

  @Override
  public int hashCode ()
  {
    // namespace context does not necessarily implement equals/hashCode
    return new HashCodeGenerator (this).append (m_eXMLVersion)
                                       .append (m_eSerializeXMLDecl)
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
                                       .getHashCode ();
  }

  @Override
  public String toString ()
  {
    if (m_sIndentationStringToString == null)
      m_sIndentationStringToString = StringHelper.getHexEncoded (CharsetManager.getAsBytes (m_sIndentationString,
                                                                                            CCharset.CHARSET_ISO_8859_1_OBJ));
    return new ToStringGenerator (this).append ("xmlVersion", m_eXMLVersion)
                                       .append ("serializeXMLDecl", m_eSerializeXMLDecl)
                                       .append ("serializeDocType", m_eSerializeDocType)
                                       .append ("serializeComments", m_eSerializeComments)
                                       .append ("indent", m_eIndent)
                                       .append ("indentDeterminator", m_aIndentDeterminator)
                                       .append ("incorrectCharHandling", m_eIncorrectCharacterHandling)
                                       .append ("charset", m_aCharset)
                                       .append ("namespaceContext", m_aNamespaceContext)
                                       .append ("doubleQuotesForAttrs", m_bUseDoubleQuotesForAttributes)
                                       .append ("bracketModeDeterminator", m_aBracketModeDeterminator)
                                       .append ("spaceOnSelfClosedElement", m_bSpaceOnSelfClosedElement)
                                       .append ("newlineMode", m_eNewLineMode)
                                       .append ("indentationString", m_sIndentationStringToString)
                                       .append ("emitNamespaces", m_bEmitNamespaces)
                                       .append ("putNamespaceContextPrefixesInRoot",
                                                m_bPutNamespaceContextPrefixesInRoot)
                                       .toString ();
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
}
