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
package com.helger.xml;

import java.util.Locale;

import com.helger.annotation.Nonnull;
import com.helger.annotation.Nullable;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.CodingStyleguideUnaware;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.lang.EnumHelper;
import com.helger.commons.lang.GenericReflection;
import com.helger.commons.name.IHasName;

/**
 * Contains constants for parser properties.
 *
 * @author Philip Helger
 */
public enum EXMLParserProperty implements IHasName
{
  /**
   * Get the string of characters associated with the current event. If the parser recognizes and
   * supports this property but is not currently parsing text, it should return null.<br>
   * Xerces: This property is currently not supported because the contents of the XML string
   * returned by this property is not well defined.
   */
  GENERAL_XML_STRING (EXMLParserPropertyType.GENERAL, "http://xml.org/sax/properties/xml-string", String.class),
  /**
   * The XML Schema Recommendation explicitly states that the inclusion of
   * schemaLocation/noNamespaceSchemaLocation attributes is only a hint; it does not mandate that
   * these attributes must be used to locate schemas. Similar situation happens to &lt;import&gt;
   * element in schema documents. This property allows the user to specify a list of schemas to use.
   * If the targetNamespace of a schema (specified using this property) matches the targetNamespace
   * of a schema occurring in the instance document in schemaLocation attribute, or if the
   * targetNamespace matches the namespace attribute of &lt;import&gt; element, the schema specified
   * by the user using this property will be used (i.e., the schemaLocation attribute in the
   * instance document or on the &lt;import&gt; element will be effectively ignored).<br>
   * The syntax is the same as for schemaLocation attributes in instance documents: e.g,
   * "http://www.example.com file_name.xsd". The user can specify more than one XML Schema in the
   * list.
   */
  GENERAL_EXTERNAL_SCHEMALOCATION (EXMLParserPropertyType.GENERAL,
                                   "http://apache.org/xml/properties/schema/external-schemaLocation",
                                   String.class),
  /**
   * This property allows the user to specify an XML Schema with no namespace. <br>
   * he syntax is a same as for the noNamespaceSchemaLocation attribute that may occur in an
   * instance document: e.g."file_name.xsd". The user may specify only one XML Schema. For more
   * information see the documentation for the
   * http://apache.org/xml/properties/schema/external-schemaLocation property.
   */
  GENERAL_EXTERNAL_NONAMESPACE_SCHEMALOCATION (EXMLParserPropertyType.GENERAL,
                                               "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",
                                               String.class),
  /**
   * A QName or XSElementDeclaration object representing the top-level element declaration used when
   * validating the root element of a document or document fragment (also known as the validation
   * root). If the value of this property is non-null the validation root will be validated against
   * the specified element declaration regardless of the actual name of the root element in the
   * instance document. If the value is a QName and a element declaration cannot be found an error
   * will be reported.<br>
   * If the 'root-type-definition' property has been set this property takes precedence if its value
   * is non-null.<br>
   * If the value specified is an XSElementDeclaration it must be an object obtained from Xerces and
   * must also be an object which is known to the schema validator, for example one which would be
   * returned from an XMLGrammarPool. If these constraints are not met a ClassCastException may be
   * thrown or processing of substitution groups, xsi:type and wildcards may fail to locate
   * otherwise available schema components.
   */
  GENERAL_ROOT_ELEMENT_DECLARATION (EXMLParserPropertyType.GENERAL,
                                    "http://apache.org/xml/properties/validation/schema/root-element-declaration",
                                    javax.xml.namespace.QName.class),
  /**
   * A QName or XSTypeDefinition object representing the top-level type definition used when
   * validating the root element of a document or document fragment (also known as the validation
   * root). If the value of this property is non-null and the 'root-element-declaration' property is
   * not set the validation root will not be validated against any element declaration. If the value
   * is a QName and a type definition cannot be found an error will be reported.<br>
   * If the 'root-element-declaration' property has been set this property is ignored.<br>
   * Prior to Xerces-J 2.10.0 setting the value of this property to an XSTypeDefinition was not
   * supported.<br>
   * If the value specified is an XSTypeDefinition it must be an object obtained from Xerces and
   * must also be an object which is known to the schema validator, for example one which would be
   * returned from an XMLGrammarPool. If these constraints are not met a ClassCastException may be
   * thrown or processing of substitution groups, xsi:type and wildcards may fail to locate
   * otherwise available schema components.
   */
  GENERAL_ROOT_TYPE_DECLARATION (EXMLParserPropertyType.GENERAL,
                                 "http://apache.org/xml/properties/validation/schema/root-type-definition",
                                 javax.xml.namespace.QName.class),
  /**
   * The size of the input buffer in the readers. This determines how many bytes to read for each
   * chunk.<br>
   * Some tests indicate that a bigger buffer size can improve the parsing performance for
   * relatively large files. The default buffer size in Xerces is 2K. This would give a good
   * performance for small documents (less than 10K). For documents larger than 10K, specifying the
   * buffer size to 4K or 8K will significantly improve the performance. But it's not recommended to
   * set it to a value larger than 16K. For really tiny documents (1K, for example), you can also
   * set it to a value less than 2K, to get the best performance. <br>
   * There are some conditions where the size of the parser's internal buffers may be increased
   * beyond the size specified for the input buffer. This would happen in places where the text in
   * the document cannot be split, for instance if the document contains a name which is longer than
   * the input buffer.
   */
  GENERAL_INPUT_BUFFER_SIZE (EXMLParserPropertyType.GENERAL,
                             "http://apache.org/xml/properties/input-buffer-size",
                             Integer.class),
  /**
   * The locale to use for reporting errors and warnings. When the value of this property is null
   * the platform default returned from java.util.Locale.getDefault() will be used.<br>
   * If no messages are available for the specified locale the platform default will be used. If the
   * platform default is not English and no messages are available for this locale then messages
   * will be reported in English.
   */
  GENERAL_LOCALE (EXMLParserPropertyType.GENERAL, "http://apache.org/xml/properties/locale", java.util.Locale.class),
  /**
   * It is possible to create XML documents whose processing could result in the use of all system
   * resources. This property enables Xerces to detect such documents, and abort their
   * processing.<br>
   * The org.apache.xerces.util.SecurityManager class contains a number of methods that allow
   * applications to tailor Xerces's tolerance of document constructs that could result in the heavy
   * consumption of system resources (see the javadoc of this class for details). Default values
   * that should be appropriate for many environments are provided when the class is instantiated.
   * Xerces will not behave in a strictly spec-compliant way when this property is set. By default,
   * this property is not set; Xerces's behaviour is therefore strictly spec-compliant by default.
   */
  GENERAL_SECURITY_MANAGER (EXMLParserPropertyType.GENERAL,
                            "http://apache.org/xml/properties/security-manager",
                            "org.apache.xerces.util.SecurityManager"),

  /**
   * The current DOM element node while parsing.<br>
   * This property is useful for determining the location with a DOM document when an error occurs.
   */
  DOM_CURRENT_ELEMENT_NODE (EXMLParserPropertyType.DOM,
                            "http://apache.org/xml/properties/dom/current-element-node",
                            org.w3c.dom.Element.class),
  /**
   * The fully qualified name of the class implementing the org.w3c.dom.Document interface. The
   * implementation used must have a zero argument constructor. <br>
   * When the document class name is set to a value other than the name of the default document
   * factory, the deferred node expansion feature does not work.
   */
  DOM_DOCUMENT_CLASS_NAME (EXMLParserPropertyType.DOM,
                           "http://apache.org/xml/properties/dom/document-class-name",
                           String.class),

  /** The handler for DTD declarations. */
  SAX_DECLARATION_HANDLER (EXMLParserPropertyType.SAX,
                           "http://xml.org/sax/properties/declaration-handler",
                           org.xml.sax.ext.DeclHandler.class),
  /** The handler for lexical parsing events. */
  SAX_LEXICAL_HANDLER (EXMLParserPropertyType.SAX,
                       "http://xml.org/sax/properties/lexical-handler",
                       org.xml.sax.ext.LexicalHandler.class),
  /**
   * The DOM node currently being visited, if SAX is being used as a DOM iterator. If the parser
   * recognizes and supports this property but is not currently visiting a DOM node, it should
   * return null.<br>
   * This property is only for SAX parser implementations used as DOM tree walkers. Currently,
   * Xerces does not have this functionality.
   */
  SAX_DOM_NODE (EXMLParserPropertyType.SAX, "http://xml.org/sax/properties/dom-node", org.w3c.dom.Node.class),
  /**
   * A literal string describing the actual XML version of the document, such as "1.0" or "1.1".<br>
   * This property may only be examined during a parse after the startDocument callback has been
   * completed.
   */
  SAX_XML_VERSION (EXMLParserPropertyType.SAX, "http://xml.org/sax/properties/document-xml-version", String.class),
  /**
   * The Schema language to be used. E.g. <code>http://www.w3.org/2001/XMLSchema</code>.
   */
  JAXP_SCHEMA_LANGUAGE (EXMLParserPropertyType.GENERAL,
                        "http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                        String.class),
  /**
   * Sets the location of the schema. This is the one of most interest. It accepts many values, such
   * as:
   * <ul>
   * <li>A string with the URI of the schema.</li>
   * <li>An {@link java.io.InputStream} object with the content of the schema.</li>
   * <li>An {@link org.xml.sax.InputSource} object pointing to the schema.</li>
   * <li>A {@link java.io.File} object pointing to the schema file.</li>
   * <li>An array with one of these defined types. The array is useful if your application accepts
   * documents that can conform to different schemas.</li>
   * </ul>
   */
  JAXP_SCHEMA_SOURCE (EXMLParserPropertyType.GENERAL,
                      "http://java.sun.com/xml/jaxp/properties/schemaSource",
                      Object.class);

  private static final Logger LOGGER = LoggerFactory.getLogger (EXMLParserProperty.class);

  private final EXMLParserPropertyType m_ePropertyType;
  private final String m_sName;
  private final Class <?> m_aValueClass;
  @CodingStyleguideUnaware
  private boolean m_bWarnedOnceXMLReader = false;
  private boolean m_bWarnedOnceSchemaFactory = false;
  private boolean m_bWarnedOnceValidator = false;
  private final String m_sValueClassName;

  EXMLParserProperty (@Nonnull final EXMLParserPropertyType ePropertyType,
                      @Nonnull @Nonempty final String sName,
                      @Nonnull @Nonempty final String sValueClassName)
  {
    m_ePropertyType = ePropertyType;
    m_sName = sName;
    // May be null
    m_aValueClass = GenericReflection.getClassFromNameSafe (sValueClassName);
    m_sValueClassName = sValueClassName;
  }

  EXMLParserProperty (@Nonnull final EXMLParserPropertyType ePropertyType,
                      @Nonnull @Nonempty final String sName,
                      @Nonnull final Class <?> aValueClass)
  {
    m_ePropertyType = ePropertyType;
    m_sName = sName;
    m_aValueClass = aValueClass;
    m_sValueClassName = aValueClass.getName ();
  }

  @Nonnull
  public EXMLParserPropertyType getPropertyType ()
  {
    return m_ePropertyType;
  }

  @Nonnull
  @Nonempty
  public String getName ()
  {
    return m_sName;
  }

  @Nullable
  public Class <?> getValueClass ()
  {
    return m_aValueClass;
  }

  @Nonnull
  @Nonempty
  public String getValueClassName ()
  {
    return m_sValueClassName;
  }

  @Nonnull
  private Object _getFixedValue (@Nonnull final Object aValue)
  {
    if (this == EXMLParserProperty.GENERAL_LOCALE && aValue instanceof Locale)
    {
      final Locale aLocale = (Locale) aValue;

      if (Locale.ENGLISH.getLanguage ().equals (aLocale.getLanguage ()))
      {
        /**
         * This hack is needed, because if the "en_US" locale is used and the system default Locale
         * would e.g. be "de_AT" and a
         * <code>com.sun.org.apache.xerces.internal.impl.msg.XMLSchemaMessages_de_AT.properties</code>
         * or
         * <code>com.sun.org.apache.xerces.internal.impl.msg.XMLSchemaMessages_de.properties</code>
         * is present it would have precedence over the provided locale because it is more specific
         * than the fallback locale "".<br>
         * Therefore by explicitly providing the locale "" it is matched to
         * <code>com.sun.org.apache.xerces.internal.impl.msg.XMLSchemaMessages.properties</code>
         * which are the English texts, and this is what we want
         */
        return Locale.ROOT;
      }
    }
    return aValue;
  }

  public void applyTo (@Nonnull final org.xml.sax.XMLReader aParser, final Object aValue)
  {
    ValueEnforcer.notNull (aParser, "Parser");

    if (m_ePropertyType != EXMLParserPropertyType.GENERAL && m_ePropertyType != EXMLParserPropertyType.SAX)
      LOGGER.warn ("Parser property '" + name () + "' is not applicable for SAX parsers!");

    try
    {
      final Object aRealValue = _getFixedValue (aValue);
      aParser.setProperty (m_sName, aRealValue);
    }
    catch (final SAXNotRecognizedException ex)
    {
      if (!m_bWarnedOnceXMLReader)
      {
        LOGGER.warn ("XML Parser does not recognize property '" + name () + "'");
        m_bWarnedOnceXMLReader = true;
      }
    }
    catch (final SAXNotSupportedException ex)
    {
      LOGGER.warn ("XML Parser does not support property '" + name () + "'");
    }
  }

  public void applyTo (@Nonnull final DocumentBuilderFactory aDocumentBuilderFactory, final Object aValue)
  {
    ValueEnforcer.notNull (aDocumentBuilderFactory, "DocumentBuilderFactory");

    if (m_ePropertyType != EXMLParserPropertyType.GENERAL && m_ePropertyType != EXMLParserPropertyType.DOM)
      LOGGER.warn ("Parser property '" + name () + "' is not applicable for DOM parsers!");

    try
    {
      final Object aRealValue = _getFixedValue (aValue);
      aDocumentBuilderFactory.setAttribute (m_sName, aRealValue);
    }
    catch (final IllegalArgumentException ex)
    {
      LOGGER.warn ("DOM parser does not support property '" + name () + "'");
    }
  }

  /**
   * Apply this property safely onto the passed {@link SchemaFactory}. Only properties of type
   * {@link EXMLParserPropertyType#GENERAL} can be used with this method.
   *
   * @param aSchemaFactory
   *        The Schema factory to apply it onto. May not be <code>null</code>.
   * @param aValue
   *        The value to use. May be <code>null</code> depending on the context.
   * @since 9.0.1
   */
  public void applyTo (@Nonnull final SchemaFactory aSchemaFactory, final Object aValue)
  {
    ValueEnforcer.notNull (aSchemaFactory, "SchemaFactory");

    if (m_ePropertyType != EXMLParserPropertyType.GENERAL)
      LOGGER.warn ("Parser property '" + name () + "' is not applicable for SchemaFactory!");

    try
    {
      final Object aRealValue = _getFixedValue (aValue);
      aSchemaFactory.setProperty (m_sName, aRealValue);
    }
    catch (final SAXNotRecognizedException ex)
    {
      if (!m_bWarnedOnceSchemaFactory)
      {
        LOGGER.warn ("SchemaFactory does not recognize property '" + name () + "'");
        m_bWarnedOnceSchemaFactory = true;
      }
    }
    catch (final SAXNotSupportedException ex)
    {
      LOGGER.warn ("SchemaFactory does not support property '" + name () + "'");
    }
  }

  /**
   * Apply this property safely onto the passed {@link Validator}. Only properties of type
   * {@link EXMLParserPropertyType#GENERAL} can be used with this method.
   *
   * @param aValidator
   *        The Validator to apply it onto. May not be <code>null</code>.
   * @param aValue
   *        The value to use. May be <code>null</code> depending on the context.
   * @since 9.0.1
   */
  public void applyTo (@Nonnull final Validator aValidator, final Object aValue)
  {
    ValueEnforcer.notNull (aValidator, "Validator");

    if (m_ePropertyType != EXMLParserPropertyType.GENERAL)
      LOGGER.warn ("Parser property '" + name () + "' is not applicable for Validator!");

    try
    {
      final Object aRealValue = _getFixedValue (aValue);
      aValidator.setProperty (m_sName, aRealValue);
    }
    catch (final SAXNotRecognizedException ex)
    {
      if (!m_bWarnedOnceValidator)
      {
        LOGGER.warn ("Validator does not recognize property '" + name () + "'");
        m_bWarnedOnceValidator = true;
      }
    }
    catch (final SAXNotSupportedException ex)
    {
      LOGGER.warn ("Validator does not support property '" + name () + "'");
    }
  }

  @Nullable
  public static EXMLParserProperty getFromNameOrNull (@Nullable final String sName)
  {
    return EnumHelper.getFromNameOrNull (EXMLParserProperty.class, sName);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsList <EXMLParserProperty> getAllPropertiesOfType (@Nonnull final EXMLParserPropertyType ePropertyType)
  {
    ValueEnforcer.notNull (ePropertyType, "PropertyType");

    return CommonsArrayList.createFiltered (values (), x -> x.getPropertyType () == ePropertyType);
  }
}
