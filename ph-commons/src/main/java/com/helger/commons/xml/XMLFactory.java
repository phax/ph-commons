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
package com.helger.commons.xml;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.exception.InitializationException;
import com.helger.commons.system.SystemProperties;
import com.helger.commons.xml.serialize.read.DOMReaderDefaultSettings;

/**
 * Utility class for creating XML DOM documents.
 *
 * @author Philip Helger
 */
public final class XMLFactory
{
  /** DocumentBuilderFactory is by default namespace aware */
  public static final boolean DEFAULT_DOM_NAMESPACE_AWARE = true;
  /** DocumentBuilderFactory is by default not DTD validating */
  public static final boolean DEFAULT_DOM_VALIDATING = false;
  /**
   * DocumentBuilderFactory is by default not ignoring element content
   * whitespace
   */
  public static final boolean DEFAULT_DOM_IGNORING_ELEMENT_CONTENT_WHITESPACE = false;
  /** DocumentBuilderFactory is by default entity reference expanding */
  public static final boolean DEFAULT_DOM_EXPAND_ENTITY_REFERENCES = true;
  /** DocumentBuilderFactory is by default ignoring comments */
  public static final boolean DEFAULT_DOM_IGNORING_COMMENTS = true;
  /** DocumentBuilderFactory is by default coalescing */
  public static final boolean DEFAULT_DOM_COALESCING = true;
  /** DocumentBuilderFactory is by default not XInclude aware */
  public static final boolean DEFAULT_DOM_XINCLUDE_AWARE = false;

  private static final SimpleReadWriteLock s_aRWLock = new SimpleReadWriteLock ();

  /** The DOM DocumentBuilderFactory. */
  @GuardedBy ("s_aRWLock")
  private static DocumentBuilderFactory s_aDefaultDocBuilderFactory;

  /** The DOM DocumentBuilder. Lazily inited */
  @GuardedBy ("s_aRWLock")
  private static DocumentBuilder s_aDefaultDocBuilder;

  static
  {
    // Explicitly use Apache Xerces for XSD reading? No in case Xerces is not in
    // the classpath.
    if (false)
      SystemProperties.setPropertyValue ("javax.xml.validation.SchemaFactory:http://www.w3.org/2001/XMLSchema",
                                         "org.apache.xerces.jaxp.validation.XMLSchemaFactory");

    reinitialize ();
  }

  /**
   * Reinitialize the document builder factory. This may be necessary if the
   * system properties change!
   */
  static void reinitialize ()
  {
    s_aRWLock.writeLocked ( () -> {
      // create DOM document builder
      s_aDefaultDocBuilderFactory = createDefaultDocumentBuilderFactory ();
      s_aDefaultDocBuilder = null;
    });
  }

  @PresentForCodeCoverage
  private static final XMLFactory s_aInstance = new XMLFactory ();

  private XMLFactory ()
  {}

  /**
   * Create a new {@link DocumentBuilderFactory} using the defaults defined in
   * this class ({@link #DEFAULT_DOM_NAMESPACE_AWARE},
   * {@link #DEFAULT_DOM_VALIDATING} etc.).
   *
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static DocumentBuilderFactory createDefaultDocumentBuilderFactory ()
  {
    final DocumentBuilderFactory aDocumentBuilderFactory = DocumentBuilderFactory.newInstance ();
    aDocumentBuilderFactory.setNamespaceAware (DEFAULT_DOM_NAMESPACE_AWARE);
    aDocumentBuilderFactory.setValidating (DEFAULT_DOM_VALIDATING);
    aDocumentBuilderFactory.setIgnoringElementContentWhitespace (DEFAULT_DOM_IGNORING_ELEMENT_CONTENT_WHITESPACE);
    aDocumentBuilderFactory.setExpandEntityReferences (DEFAULT_DOM_EXPAND_ENTITY_REFERENCES);
    aDocumentBuilderFactory.setIgnoringComments (DEFAULT_DOM_IGNORING_COMMENTS);
    aDocumentBuilderFactory.setCoalescing (DEFAULT_DOM_COALESCING);
    try
    {
      // Set secure processing to be the default. This is anyway the default in
      // JDK8
      aDocumentBuilderFactory.setFeature (EXMLParserFeature.SECURE_PROCESSING.getName (), true);
    }
    catch (final ParserConfigurationException ex1)
    {
      // Ignore
    }
    try
    {
      aDocumentBuilderFactory.setXIncludeAware (DEFAULT_DOM_XINCLUDE_AWARE);
    }
    catch (final UnsupportedOperationException ex)
    {
      // Ignore
    }
    return aDocumentBuilderFactory;
  }

  /**
   * Create a new {@link DocumentBuilderFactory} for the specified schema, with
   * the following settings: coalescing, comment ignoring and namespace aware.
   *
   * @param aSchema
   *        The schema to use. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static DocumentBuilderFactory createDocumentBuilderFactory (@Nonnull final Schema aSchema)
  {
    ValueEnforcer.notNull (aSchema, "Schema");

    final DocumentBuilderFactory aDocumentBuilderFactory = createDefaultDocumentBuilderFactory ();
    aDocumentBuilderFactory.setSchema (aSchema);
    return aDocumentBuilderFactory;
  }

  /**
   * @return The default document builder factory that is not schema specific.
   *         Never <code>null</code>.
   */
  @Nonnull
  public static DocumentBuilderFactory getDocumentBuilderFactory ()
  {
    return s_aRWLock.readLocked ( () -> s_aDefaultDocBuilderFactory);
  }

  /**
   * @return The default document builder that is not schema specific. Never
   *         <code>null</code>.
   */
  @Nonnull
  public static DocumentBuilder getDocumentBuilder ()
  {
    // Lazily init
    final DocumentBuilder ret = s_aRWLock.readLocked ( () -> s_aDefaultDocBuilder);
    if (ret != null)
      return ret;

    return s_aRWLock.writeLocked ( () -> {
      if (s_aDefaultDocBuilder == null)
        s_aDefaultDocBuilder = createDocumentBuilder (s_aDefaultDocBuilderFactory);
      return s_aDefaultDocBuilder;
    });
  }

  /**
   * @return The DOM implementation of the default document builder. Never
   *         <code>null</code>.
   */
  @Nonnull
  public static DOMImplementation getDOMImplementation ()
  {
    return getDocumentBuilder ().getDOMImplementation ();
  }

  /**
   * Create a document builder without a certain schema, using the default
   * {@link DocumentBuilderFactory}.
   *
   * @return The created document builder. Never <code>null</code>.
   */
  @Nonnull
  public static DocumentBuilder createDocumentBuilder ()
  {
    return createDocumentBuilder (getDocumentBuilderFactory ());
  }

  /**
   * Create a document builder for a certain schema.
   *
   * @param aSchema
   *        The schema to use. May not be <code>null</code>.
   * @return The created document builder. Never <code>null</code>.
   */
  @Nonnull
  public static DocumentBuilder createDocumentBuilder (@Nonnull final Schema aSchema)
  {
    return createDocumentBuilder (createDocumentBuilderFactory (aSchema));
  }

  /**
   * Create a document builder without a certain schema, using the passed
   * {@link DocumentBuilderFactory}.
   *
   * @param aDocBuilderFactory
   *        The document builder factory to be used. May not be
   *        <code>null</code>.
   * @return The created document builder. Never <code>null</code>.
   * @throws InitializationException
   *         In case some DOM initialization goes wrong
   */
  @Nonnull
  public static DocumentBuilder createDocumentBuilder (@Nonnull final DocumentBuilderFactory aDocBuilderFactory)
  {
    ValueEnforcer.notNull (aDocBuilderFactory, "DocBuilderFactory");

    try
    {
      final DocumentBuilder aDocBuilder = aDocBuilderFactory.newDocumentBuilder ();
      aDocBuilder.setErrorHandler (DOMReaderDefaultSettings.getErrorHandler ());
      return aDocBuilder;
    }
    catch (final ParserConfigurationException ex)
    {
      throw new InitializationException ("Failed to create document builder", ex);
    }
  }

  /**
   * Create a new XML document without document type using version
   * {@link EXMLVersion#XML_10}. The default document builder is used.
   *
   * @return The created document. Never <code>null</code>.
   */
  @Nonnull
  public static Document newDocument ()
  {
    return newDocument (getDocumentBuilder (), (EXMLVersion) null);
  }

  /**
   * Create a new XML document without document type using version
   * {@link EXMLVersion#XML_10}. A custom document builder is used.
   *
   * @param aDocBuilder
   *        The document builder to use. May not be <code>null</code>.
   * @return The created document. Never <code>null</code>.
   */
  @Nonnull
  public static Document newDocument (@Nonnull final DocumentBuilder aDocBuilder)
  {
    return newDocument (aDocBuilder, (EXMLVersion) null);
  }

  /**
   * Create a new XML document without document type using the default document
   * builder.
   *
   * @param eVersion
   *        The XML version to use. If <code>null</code> is passed,
   *        {@link EXMLVersion#XML_10} will be used.
   * @return The created document. Never <code>null</code>.
   */
  @Nonnull
  public static Document newDocument (@Nullable final EXMLVersion eVersion)
  {
    return newDocument (getDocumentBuilder (), eVersion);
  }

  /**
   * Create a new XML document without document type using a custom document
   * builder.
   *
   * @param aDocBuilder
   *        The document builder to use. May not be <code>null</code>.
   * @param eVersion
   *        The XML version to use. If <code>null</code> is passed,
   *        {@link EXMLVersion#XML_10} will be used.
   * @return The created document. Never <code>null</code>.
   */
  @Nonnull
  public static Document newDocument (@Nonnull final DocumentBuilder aDocBuilder, @Nullable final EXMLVersion eVersion)
  {
    ValueEnforcer.notNull (aDocBuilder, "DocBuilder");

    final Document aDoc = aDocBuilder.newDocument ();
    aDoc.setXmlVersion ((eVersion != null ? eVersion : EXMLVersion.XML_10).getVersion ());
    return aDoc;
  }

  /**
   * Create a new document with a document type using version
   * {@link EXMLVersion#XML_10}.
   *
   * @param sQualifiedName
   *        The qualified name to use.
   * @param sPublicId
   *        The public ID of the document type.
   * @param sSystemId
   *        The system ID of the document type.
   * @return The created document. Never <code>null</code>.
   */
  @Nonnull
  public static Document newDocument (@Nonnull final String sQualifiedName,
                                      @Nullable final String sPublicId,
                                      @Nullable final String sSystemId)
  {
    return newDocument ((EXMLVersion) null, sQualifiedName, sPublicId, sSystemId);
  }

  /**
   * Create a new document with a document type using the default document
   * builder.
   *
   * @param eVersion
   *        The XML version to use. If <code>null</code> is passed,
   *        {@link EXMLVersion#XML_10} will be used.
   * @param sQualifiedName
   *        The qualified name to use.
   * @param sPublicId
   *        The public ID of the document type.
   * @param sSystemId
   *        The system ID of the document type.
   * @return The created document. Never <code>null</code>.
   */
  @Nonnull
  public static Document newDocument (@Nullable final EXMLVersion eVersion,
                                      @Nonnull final String sQualifiedName,
                                      @Nullable final String sPublicId,
                                      @Nullable final String sSystemId)
  {
    return newDocument (getDocumentBuilder (), eVersion, sQualifiedName, sPublicId, sSystemId);
  }

  /**
   * Create a new document with a document type using a custom document builder.
   *
   * @param aDocBuilder
   *        the document builder to be used. May not be <code>null</code>.
   * @param eVersion
   *        The XML version to use. If <code>null</code> is passed,
   *        {@link EXMLVersion#XML_10} will be used.
   * @param sQualifiedName
   *        The qualified name to use.
   * @param sPublicId
   *        The public ID of the document type.
   * @param sSystemId
   *        The system ID of the document type.
   * @return The created document. Never <code>null</code>.
   */
  @Nonnull
  public static Document newDocument (@Nonnull final DocumentBuilder aDocBuilder,
                                      @Nullable final EXMLVersion eVersion,
                                      @Nonnull final String sQualifiedName,
                                      @Nullable final String sPublicId,
                                      @Nullable final String sSystemId)
  {
    ValueEnforcer.notNull (aDocBuilder, "DocBuilder");

    final DOMImplementation aDomImpl = aDocBuilder.getDOMImplementation ();
    final DocumentType aDocType = aDomImpl.createDocumentType (sQualifiedName, sPublicId, sSystemId);

    final Document aDoc = aDomImpl.createDocument (sSystemId, sQualifiedName, aDocType);
    aDoc.setXmlVersion ((eVersion != null ? eVersion : EXMLVersion.XML_10).getVersion ());
    return aDoc;
  }
}
