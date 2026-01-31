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
package com.helger.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.validation.Schema;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.xml.sax.SAXException;

import com.helger.annotation.concurrent.GuardedBy;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.concurrent.SimpleReadWriteLock;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.exception.InitializationException;
import com.helger.base.system.SystemProperties;
import com.helger.xml.serialize.read.DOMReaderDefaultSettings;

/**
 * Utility class for creating XML DOM documents.
 *
 * @author Philip Helger
 */
public final class XMLFactory
{
  /** DocumentBuilderFactory is by default not namespace aware */
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

  /** SAXParserFactory is by default not namespace aware */
  public static final boolean DEFAULT_SAX_NAMESPACE_AWARE = DEFAULT_DOM_NAMESPACE_AWARE;
  /** SAXParserFactory is by default not DTD validating */
  public static final boolean DEFAULT_SAX_VALIDATING = DEFAULT_DOM_VALIDATING;
  /** SAXParserFactory is by default not XInclude aware */
  public static final boolean DEFAULT_SAX_XINCLUDE_AWARE = DEFAULT_DOM_XINCLUDE_AWARE;

  private static final Logger LOGGER = LoggerFactory.getLogger (XMLFactory.class);
  private static final SimpleReadWriteLock RW_LOCK = new SimpleReadWriteLock ();

  /** The DOM DocumentBuilderFactory. */
  @GuardedBy ("RW_LOCK")
  private static DocumentBuilderFactory s_aDefaultDocBuilderFactory;

  /** The DOM DocumentBuilder. Lazily inited */
  @GuardedBy ("RW_LOCK")
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
  public static void reinitialize ()
  {
    RW_LOCK.writeLocked ( () -> {
      // create DOM document builder
      s_aDefaultDocBuilderFactory = createDefaultDocumentBuilderFactory ();
      s_aDefaultDocBuilder = null;
    });
  }

  @PresentForCodeCoverage
  private static final XMLFactory INSTANCE = new XMLFactory ();

  private XMLFactory ()
  {}

  public static void setFeature (@NonNull final DocumentBuilderFactory aFactory,
                                 @NonNull final EXMLParserFeature eFeature,
                                 final boolean bValue)
  {
    try
    {
      aFactory.setFeature (eFeature.getName (), bValue);
    }
    catch (final ParserConfigurationException ex)
    {
      LOGGER.warn ("Failed to set feature " +
                   eFeature +
                   " to " +
                   bValue +
                   " on XML DocumentBuilderFactory: " +
                   ex.getMessage ());
    }
  }

  public static void defaultCustomizeDocumentBuilderFactory (@NonNull final DocumentBuilderFactory aFactory)
  {
    /*
     * Secure processing is enabled by default since JDK 8. See class
     * "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl"
     * field "fSecure" is initially "true". However, if someone uses an external
     * XML parser library (like Xerces) it might be disabled.
     */
    setFeature (aFactory, EXMLParserFeature.SECURE_PROCESSING, true);
    setFeature (aFactory, EXMLParserFeature.DISALLOW_DOCTYPE_DECL, true);
    setFeature (aFactory, EXMLParserFeature.EXTERNAL_GENERAL_ENTITIES, false);
    setFeature (aFactory, EXMLParserFeature.EXTERNAL_PARAMETER_ENTITIES, false);
    setFeature (aFactory, EXMLParserFeature.LOAD_EXTERNAL_DTD, false);
    aFactory.setNamespaceAware (DEFAULT_DOM_NAMESPACE_AWARE);
    aFactory.setValidating (DEFAULT_DOM_VALIDATING);
    aFactory.setIgnoringElementContentWhitespace (DEFAULT_DOM_IGNORING_ELEMENT_CONTENT_WHITESPACE);
    aFactory.setExpandEntityReferences (DEFAULT_DOM_EXPAND_ENTITY_REFERENCES);
    aFactory.setIgnoringComments (DEFAULT_DOM_IGNORING_COMMENTS);
    aFactory.setCoalescing (DEFAULT_DOM_COALESCING);
    try
    {
      aFactory.setXIncludeAware (DEFAULT_DOM_XINCLUDE_AWARE);
    }
    catch (final UnsupportedOperationException ex)
    {
      // Ignore
    }
  }

  /**
   * Create a new {@link DocumentBuilderFactory} using the defaults defined in
   * this class ({@link #DEFAULT_DOM_NAMESPACE_AWARE},
   * {@link #DEFAULT_DOM_VALIDATING} ,
   * {@link #DEFAULT_DOM_IGNORING_ELEMENT_CONTENT_WHITESPACE},
   * {@link #DEFAULT_DOM_EXPAND_ENTITY_REFERENCES},
   * {@link #DEFAULT_DOM_IGNORING_COMMENTS} and
   * {@link #DEFAULT_DOM_COALESCING}.).
   *
   * @return Never <code>null</code>.
   */
  @NonNull
  public static DocumentBuilderFactory createDefaultDocumentBuilderFactory ()
  {
    final DocumentBuilderFactory aFactory = DocumentBuilderFactory.newInstance ();
    defaultCustomizeDocumentBuilderFactory (aFactory);
    return aFactory;
  }

  /**
   * Create a new {@link DocumentBuilderFactory} for the specified schema, with
   * the following settings: coalescing, comment ignoring and namespace aware.
   *
   * @param aSchema
   *        The schema to use. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static DocumentBuilderFactory createDocumentBuilderFactory (@NonNull final Schema aSchema)
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
  @NonNull
  public static DocumentBuilderFactory getDocumentBuilderFactory ()
  {
    return RW_LOCK.readLockedGet ( () -> s_aDefaultDocBuilderFactory);
  }

  /**
   * @return The default document builder that is not schema specific. Never
   *         <code>null</code>.
   */
  @NonNull
  public static DocumentBuilder getDocumentBuilder ()
  {
    // Lazily init
    final DocumentBuilder ret = RW_LOCK.readLockedGet ( () -> s_aDefaultDocBuilder);
    if (ret != null)
      return ret;

    return RW_LOCK.writeLockedGet ( () -> {
      DocumentBuilder ret2 = s_aDefaultDocBuilder;
      if (ret2 == null)
        ret2 = s_aDefaultDocBuilder = createDocumentBuilder (s_aDefaultDocBuilderFactory);
      return ret2;
    });
  }

  /**
   * @return The DOM implementation of the default document builder. Never
   *         <code>null</code>.
   */
  @NonNull
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
  @NonNull
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
  @NonNull
  public static DocumentBuilder createDocumentBuilder (@NonNull final Schema aSchema)
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
  @NonNull
  public static DocumentBuilder createDocumentBuilder (@NonNull final DocumentBuilderFactory aDocBuilderFactory)
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
  @NonNull
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
  @NonNull
  public static Document newDocument (@NonNull final DocumentBuilder aDocBuilder)
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
  @NonNull
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
  @NonNull
  public static Document newDocument (@NonNull final DocumentBuilder aDocBuilder, @Nullable final EXMLVersion eVersion)
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
  @NonNull
  public static Document newDocument (@NonNull final String sQualifiedName,
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
  @NonNull
  public static Document newDocument (@Nullable final EXMLVersion eVersion,
                                      @NonNull final String sQualifiedName,
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
  @NonNull
  public static Document newDocument (@NonNull final DocumentBuilder aDocBuilder,
                                      @Nullable final EXMLVersion eVersion,
                                      @NonNull final String sQualifiedName,
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

  public static void setFeature (@NonNull final SAXParserFactory aFactory,
                                 @NonNull final EXMLParserFeature eFeature,
                                 final boolean bValue)
  {
    try
    {
      aFactory.setFeature (eFeature.getName (), bValue);
    }
    catch (final SAXException | ParserConfigurationException ex)
    {
      LOGGER.warn ("Failed to set feature " +
                   eFeature +
                   " to " +
                   bValue +
                   " on XML SAXParserFactory: " +
                   ex.getMessage ());
    }
  }

  public static void defaultCustomizeSAXParserFactory (@NonNull final SAXParserFactory aFactory)
  {
    setFeature (aFactory, EXMLParserFeature.SECURE_PROCESSING, true);
    setFeature (aFactory, EXMLParserFeature.DISALLOW_DOCTYPE_DECL, true);
    setFeature (aFactory, EXMLParserFeature.EXTERNAL_GENERAL_ENTITIES, false);
    setFeature (aFactory, EXMLParserFeature.EXTERNAL_PARAMETER_ENTITIES, false);
    setFeature (aFactory, EXMLParserFeature.LOAD_EXTERNAL_DTD, false);
    aFactory.setNamespaceAware (DEFAULT_SAX_NAMESPACE_AWARE);
    aFactory.setValidating (DEFAULT_SAX_VALIDATING);
    aFactory.setXIncludeAware (DEFAULT_SAX_XINCLUDE_AWARE);
  }

  @NonNull
  public static SAXParserFactory createDefaultSAXParserFactory ()
  {
    SAXParserFactory aFactory;
    try
    {
      // Java 9 method
      aFactory = SAXParserFactory.newDefaultInstance ();
    }
    catch (final UnsupportedOperationException ex)
    {
      // Java 8 method - see #41
      aFactory = SAXParserFactory.newInstance ();
    }
    defaultCustomizeSAXParserFactory (aFactory);
    return aFactory;
  }

  public static void setFeature (@NonNull final TransformerFactory aFactory,
                                 @NonNull final EXMLParserFeature eFeature,
                                 final boolean bValue,
                                 final boolean bLogOnError)
  {
    try
    {
      aFactory.setFeature (eFeature.getName (), bValue);
    }
    catch (final TransformerConfigurationException ex)
    {
      if (bLogOnError)
        LOGGER.warn ("Failed to set feature " +
                     eFeature +
                     " to " +
                     bValue +
                     " on XML TransformerFactory: " +
                     ex.getMessage ());
    }
  }

  public static void defaultCustomizeTransformerFactory (@NonNull final TransformerFactory aFactory)
  {
    if (false)
    {
      // This prevents to use XSLT includes
      setFeature (aFactory, EXMLParserFeature.SECURE_PROCESSING, true, true);
    }
    /*
     * The following properties might not be applied - e.g. default JDK does not
     * support them. But as other implementations might allow it...
     */
    setFeature (aFactory, EXMLParserFeature.DISALLOW_DOCTYPE_DECL, true, false);
    setFeature (aFactory, EXMLParserFeature.EXTERNAL_GENERAL_ENTITIES, false, false);
    setFeature (aFactory, EXMLParserFeature.EXTERNAL_PARAMETER_ENTITIES, false, false);
    setFeature (aFactory, EXMLParserFeature.LOAD_EXTERNAL_DTD, false, false);
  }

  @NonNull
  public static TransformerFactory createDefaultTransformerFactory ()
  {
    try
    {
      final TransformerFactory aFactory = TransformerFactory.newInstance ();
      defaultCustomizeTransformerFactory (aFactory);
      return aFactory;
    }
    catch (final TransformerFactoryConfigurationError ex)
    {
      throw new InitializationException ("Failed to create XML TransformerFactory", ex);
    }
  }
}
