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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.CodingStyleguideUnaware;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.lang.EnumHelper;
import com.helger.commons.name.IHasName;

/**
 * Contains constants for parser features.<br>
 * Source: http://xerces.apache.org/xerces2-j/features.html
 *
 * @author Philip Helger
 */
public enum EXMLParserFeature implements IHasName
{
  /**
   * When true: instructs the implementation to process XML securely. This may
   * set limits on XML constructs to avoid conditions such as denial of service
   * attacks.<br>
   * When false: instructs the implementation to process XML acording the letter
   * of the XML specifications ingoring security issues such as limits on XML
   * constructs to avoid conditions such as denial of service attacks.<br>
   * Default: false<br>
   * ({@link XMLConstants#FEATURE_SECURE_PROCESSING})
   */
  SECURE_PROCESSING (EXMLParserFeatureType.GENERAL, XMLConstants.FEATURE_SECURE_PROCESSING),

  /**
   * When true: Perform namespace processing: prefixes will be stripped off
   * element and attribute names and replaced with the corresponding namespace
   * URIs. By default, the two will simply be concatenated, but the
   * namespace-sep core property allows the application to specify a delimiter
   * string for separating the URI part and the local part.<br>
   * When false: Do not perform namespace processing.<br>
   * Default: true<br>
   * (http://xml.org/sax/features/namespaces)
   */
  NAMESPACES (EXMLParserFeatureType.GENERAL, "http://xml.org/sax/features/namespaces"),

  /**
   * When true: The methods of the org.xml.sax.ext.EntityResolver2 interface
   * will be used when an object implementing this interface is registered with
   * the parser using setEntityResolver.<br>
   * When false: The methods of the org.xml.sax.ext.EntityResolver2 interface
   * will not be used.<br>
   * Default: true<br>
   * (http://xml.org/sax/features/use-entity-resolver2)
   */
  USE_ENTITY_RESOLVER2 (EXMLParserFeatureType.GENERAL, "http://xml.org/sax/features/use-entity-resolver2"),

  /**
   * When true: Validate the document and report validity errors.<br>
   * When false: Do not report validity errors.<br>
   * Default: false<br>
   * (http://xml.org/sax/features/validation) Default: false
   */
  VALIDATION (EXMLParserFeatureType.GENERAL, "http://xml.org/sax/features/validation"),

  /**
   * When true: The parser will validate the document only if a grammar is
   * specified.<br>
   * When false: Validation is determined by the state of the
   * {@link #VALIDATION} feature.<br>
   * Default: false<br>
   * (http://apache.org/xml/features/validation/dynamic)
   */
  DYNAMIC (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/validation/dynamic"),

  /**
   * When true: Turn on XML Schema validation by inserting an XML Schema
   * validator into the pipeline.<br>
   * When false: Do not report validation errors against XML Schema.<br>
   * Default: false<br>
   * (http://apache.org/xml/features/validation/schema) Default: false
   */
  SCHEMA (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/validation/schema"),

  /**
   * When true: Enable full schema grammar constraint checking, including
   * checking which may be time-consuming or memory intensive. Currently, unique
   * particle attribution constraint checking and particle derivation
   * restriction checking are controlled by this option.<br>
   * When false: Disable full constraint checking.<br>
   * Default: false<br>
   * (http://apache.org/xml/features/validation/schema-full-checking)
   */
  SCHEMA_FULL_CHECKING (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/validation/schema-full-checking"),

  /**
   * When true: Expose via SAX and DOM XML Schema normalized values for
   * attributes and elements.<br>
   * When false: Expose the infoset values<br>
   * Default: true<br>
   * XML Schema normalized values will be exposed only if both {@link #SCHEMA}
   * and {@link #VALIDATION} features are set to true.<br>
   * (http://apache.org/xml/features/validation/schema/normalized-value)
   */
  NORMALIZED_VALUE (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/validation/schema/normalized-value"),

  /**
   * When true: Send XML Schema element default values via characters()<br>
   * When false: Do not send XML Schema default values in XNI<br>
   * Default: true<br>
   * XML Schema normalized values will be exposed only if both {@link #SCHEMA}
   * and {@link #VALIDATION} features are set to true.<br>
   * (http://apache.org/xml/features/validation/schema/element-default)
   */
  ELEMENT_DEFAULT (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/validation/schema/element-default"),

  /**
   * When true: Augment Post-Schema-Validation-Infoset.<br>
   * When false: Do not augment Post-Schema-Validation-Infoset.<br>
   * Default: true<br>
   * (http://apache.org/xml/features/validation/schema/augment-psvi)
   */
  AUGMENT_PSVI (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/validation/schema/augment-psvi"),

  /**
   * When true: xsi:type attributes will be ignored until a global element
   * declaration has been found, at which point xsi:type attributes will be
   * processed on the element for which the global element declaration was found
   * as well as its descendants.<br>
   * When false: Do not ignore xsi:type attributes.<br>
   * Default: false<br>
   * (http://apache.org/xml/features/validation/schema
   * /ignore-xsi-type-until-elemdecl)
   */
  IGNORE_XSI_TYPE_UNTIL_ELEMDECL (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/validation/schema/ignore-xsi-type-until-elemdecl"),

  /**
   * When true: Enable generation of synthetic annotations. A synthetic
   * annotation will be generated when a schema component has non-schema
   * attributes but no child annotation.<br>
   * When false: Do not generate synthetic annotations.<br>
   * Default: false<br>
   * (http://apache.org/xml/features/generate-synthetic-annotations)
   */
  GENERATE_SYNTHETIC_ANNOTATIONS (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/generate-synthetic-annotations"),

  /**
   * When true: Schema annotations will be laxly validated against available
   * schema components.<br>
   * When false: Do not validate schema annotations.<br>
   * Default: false<br>
   * (http://apache.org/xml/features/validate-annotations)
   */
  VALIDATE_ANNOTATIONS (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/validate-annotations"),

  /**
   * When true: All schema location hints will be used to locate the components
   * for a given target namespace.<br>
   * When false: Only the first schema location hint encountered by the
   * processor will be used to locate the components for a given target
   * namespace.<br>
   * Default: false<br>
   * (http://apache.org/xml/features/honour-all-schemaLocations)
   */
  HONOUR_ALL_SCHEMA_LOCATIONS (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/honour-all-schemaLocations"),

  /**
   * When true: Include external general entities.<br>
   * When false: Do not include external general entities.<br>
   * Default: true<br>
   * Note: set to false to avoid XXE -
   * https://www.owasp.org/index.php/XML_External_Entity_%28XXE%29_Processing
   * <br>
   * (http://xml.org/sax/features/external-general-entities)
   */
  EXTERNAL_GENERAL_ENTITIES (EXMLParserFeatureType.GENERAL, "http://xml.org/sax/features/external-general-entities"),

  /**
   * When true: Include external parameter entities and the external DTD subset.
   * <br>
   * When false: Do not include external parameter entities or the external DTD
   * subset.<br>
   * Default: true<br>
   * Note: set to false to avoid XXE -
   * https://www.owasp.org/index.php/XML_External_Entity_%28XXE%29_Processing
   * <br>
   * (http://xml.org/sax/features/external-parameter-entities)
   */
  EXTERNAL_PARAMETER_ENTITIES (EXMLParserFeatureType.GENERAL, "http://xml.org/sax/features/external-parameter-entities"),

  /**
   * When true: Construct an optimal representation for DTD content models to
   * significantly reduce the likelihood a StackOverflowError will occur when
   * large content models are processed.<br>
   * When false: Do not invest processing time to construct an optimal
   * representation for DTD content models.<br>
   * Default: false<br>
   * (http://apache.org/xml/features/validation/balance-syntax-trees)
   */
  BALANCE_SYNTAX_TREES (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/validation/balance-syntax-trees"),

  /**
   * When true: Enable checking of ID/IDREF constraints.<br>
   * When false: Disable checking of ID/IDREF constraints. Validation will not
   * fail if there are non-unique ID values or dangling IDREF values in the
   * document.<br>
   * Default: true<br>
   * (http://apache.org/xml/features/validation/id-idref-checking)
   */
  ID_IDREF_CHECKING (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/validation/id-idref-checking"),

  /**
   * When true: Enable identity constraint checking.<br>
   * When false: Disable identity constraint checking.<br>
   * Default: true<br>
   * (http://apache.org/xml/features/validation/identity-constraint-checking)
   */
  IDENTITY_CONSTRAINT_CHECKING (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/validation/identity-constraint-checking"),

  /**
   * When true: Check that each value of type ENTITY matches the name of an
   * unparsed entity declared in the DTD.<br>
   * When false: Do not check that each value of type ENTITY matches the name of
   * an unparsed entity declared in the DTD.<br>
   * Default: true<br>
   * (http://apache.org/xml/features/validation/unparsed-entity-checking)
   */
  UNPARSED_ENTITY_CHECKING (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/validation/unparsed-entity-checking"),

  /**
   * When true: Report a warning when a duplicate attribute is re-declared.<br>
   * When false: Do not report a warning when a duplicate attribute is
   * re-declared.<br>
   * Default: false<br>
   * (http://apache.org/xml/features/validation/warn-on-duplicate-attdef)
   */
  WARN_ON_DUPLICATE_ATTDEF (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/validation/warn-on-duplicate-attdef"),

  /**
   * When true: Report a warning if an element referenced in a content model is
   * not declared.<br>
   * When false: Do not report a warning if an element referenced in a content
   * model is not declared.<br>
   * Default: false<br>
   * (http://apache.org/xml/features/validation/warn-on-undeclared-elemdef)
   */
  WARN_ON_UNDECLARED_ELEMDEF (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/validation/warn-on-undeclared-elemdef"),

  /**
   * When true: Report a warning for duplicate entity declaration.<br>
   * When false: Do not report warning for duplicate entity declaration.<br>
   * Default: false<br>
   * (http://apache.org/xml/features/warn-on-duplicate-entitydef)
   */
  WARN_ON_DUPLICATE_ENTITYDEF (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/warn-on-duplicate-entitydef"),

  /**
   * When true: Allow Java encoding names in XMLDecl and TextDecl line.<br>
   * When false: Do not allow Java encoding names in XMLDecl and TextDecl line.
   * <br>
   * Default: false<br>
   * (http://apache.org/xml/features/allow-java-encodings)
   */
  ALLOW_JAVA_ENCODINGS (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/allow-java-encodings"),

  /**
   * When true: Attempt to continue parsing after a fatal error.<br>
   * When false: Stops parse on first fatal error.<br>
   * Default: false<br>
   * (http://apache.org/xml/features/continue-after-fatal-error)
   */
  CONTINUE_AFTER_FATAL_ERROR (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/continue-after-fatal-error"),

  /**
   * When true: Load the DTD and use it to add default attributes and set
   * attribute types when parsing.<br>
   * When false: Build the grammar but do not use the default attributes and
   * attribute types information it contains.<br>
   * Default: true<br>
   * (http://apache.org/xml/features/nonvalidating/load-dtd-grammar)
   */
  LOAD_DTD_GRAMMAR (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/nonvalidating/load-dtd-grammar"),

  /**
   * When true: Load the external DTD.<br>
   * When false: Ignore the external DTD completely.<br>
   * Default: true<br>
   * (http://apache.org/xml/features/nonvalidating/load-external-dtd)
   */
  LOAD_EXTERNAL_DTD (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/nonvalidating/load-external-dtd"),

  /**
   * When true: Notifies the handler of character reference boundaries in the
   * document via the start/endEntity callbacks.<br>
   * When false: Does not notify of character reference boundaries.<br>
   * Default: false<br>
   * (http://apache.org/xml/features/scanner/notify-char-refs)
   */
  NOTIFY_CHAR_REFS (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/scanner/notify-char-refs"),

  /**
   * When true: Notifies the handler of built-in entity boundaries (e.g &amp;)
   * in the document via the start/endEntity callbacks.<br>
   * When false: Does not notify of built-in entity boundaries.<br>
   * Default: false<br>
   * (http://apache.org/xml/features/scanner/notify-builtin-refs)
   */
  NOTIFY_BUILTIN_REFS (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/scanner/notify-builtin-refs"),

  /**
   * When true: A fatal error is thrown if the incoming document contains a
   * DOCTYPE declaration.<br>
   * When false: DOCTYPE declaration is allowed.<br>
   * Default: false<br>
   * Note: set to true to avoid XXE -
   * https://www.owasp.org/index.php/XML_External_Entity_%28XXE%29_Processing
   * <br>
   * (http://apache.org/xml/features/disallow-doctype-decl)
   */
  DISALLOW_DOCTYPE_DECL (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/disallow-doctype-decl"),

  /**
   * When true: Requires that a URI has to be provided where a URI is expected.
   * <br>
   * When false: Some invalid URI's are accepted as valid values when a URI is
   * expected. Examples include: using platform dependent file separator in
   * place of '/'; using Windows/DOS path names like "c:\blah" and
   * "\\host\dir\blah"; using invalid URI characters (space, for example)<br>
   * Default: false<br>
   * (http://apache.org/xml/features/standard-uri-conformant)
   */
  STANDARD_URI_CONFORMANT (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/standard-uri-conformant"),

  /**
   * When true: Enable XInclude processing.<br>
   * When false: Do not perform XInclude processing.<br>
   * Default: false<br>
   * (http://apache.org/xml/features/xinclude)
   */
  XINCLUDE (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/xinclude"),

  /**
   * When true: Perform base URI fixup as specified by the XInclude
   * Recommendation.<br>
   * When false: Do not perform base URI fixup. The XInclude processor will not
   * add xml:base attributes.<br>
   * Default: true<br>
   * (http://apache.org/xml/features/xinclude/fixup-base-uris)
   */
  XINCLUDE_FIXUP_BASE_URIS (EXMLParserFeatureType.XINCLUDE, "http://apache.org/xml/features/xinclude/fixup-base-uris"),

  /**
   * When true: Perform language fixup as specified by the XInclude
   * Recommendation.<br>
   * When false: Do not perform language fixup. The XInclude processor will not
   * add xml:lang attributes.<br>
   * Default: true<br>
   * (http://apache.org/xml/features/xinclude/fixup-language)
   */
  XINCLUDE_FIXUP_LANGUAGE (EXMLParserFeatureType.XINCLUDE, "http://apache.org/xml/features/xinclude/fixup-language"),

  /**
   * When true: Lazily expand the DOM nodes.<br>
   * When false: Fully expand the DOM nodes.<br>
   * Default: true - In the LSParser implementation the default value of this
   * feature is false.<br>
   * (http://apache.org/xml/features/dom/defer-node-expansion)
   */
  DOM_DEFER_NODE_EXPANSION (EXMLParserFeatureType.DOM, "http://apache.org/xml/features/dom/defer-node-expansion"),

  /**
   * When true: Create EntityReference nodes in the DOM tree. The
   * EntityReference nodes and their child nodes will be read-only.<br>
   * When false: Do not create EntityReference nodes in the DOM tree. No
   * EntityReference nodes will be created, only the nodes corresponding to
   * their fully expanded substitution text will be created.<br>
   * Default: true<br>
   * (http://apache.org/xml/features/dom/create-entity-ref-nodes)
   */
  DOM_CREATE_ENTITY_REF_NODES (EXMLParserFeatureType.DOM, "http://apache.org/xml/features/dom/create-entity-ref-nodes"),

  /**
   * When true: Include text nodes that can be considered "ignorable whitespace"
   * in the DOM tree.<br>
   * When false: Do not include ignorable whitespace in the DOM tree.<br>
   * Default: true<br>
   * (http://apache.org/xml/features/dom/include-ignorable-whitespace)
   */
  DOM_INCLUDE_IGNORABLE_WHITESPACE (EXMLParserFeatureType.DOM, "http://apache.org/xml/features/dom/include-ignorable-whitespace"),

  /**
   * When true: Report the original prefixed names and attributes used for
   * namespace declarations.<br>
   * When false: Do not report attributes used for Namespace declarations, and
   * optionally do not report original prefixed names.<br>
   * Default: false<br>
   * (http://xml.org/sax/features/namespace-prefixes)
   */
  SAX_NAMESPACE_PREFIXES (EXMLParserFeatureType.SAX, "http://xml.org/sax/features/namespace-prefixes"),

  /**
   * When true: All element names, prefixes, attribute names, namespace URIs,
   * and local names are internalized using the
   * java.lang.String#intern(String):String method.<br>
   * When false: Names are not necessarily internalized.<br>
   * Default: true<br>
   * (http://xml.org/sax/features/string-interning)
   */
  SAX_STRING_INTERNING (EXMLParserFeatureType.SAX, "http://xml.org/sax/features/string-interning"),

  /**
   * When true: Report the beginning and end of parameter entities to a
   * registered LexicalHandler.<br>
   * When false: Do not report the beginning and end of parameter entities to a
   * registered LexicalHandler.<br>
   * Default: true<br>
   * (http://xml.org/sax/features/lexical-handler/parameter-entities)
   */
  SAX_PARAMETER_ENTITIES (EXMLParserFeatureType.SAX, "http://xml.org/sax/features/lexical-handler/parameter-entities"),

  /**
   * When true: The document specified standalone="yes" in its XML declaration.
   * <br>
   * When false: The document specified standalone="no" in its XML declaration
   * or the standalone document declaration was absent.<br>
   * Read-only!<br>
   * (http://xml.org/sax/features/is-standalone)
   */
  SAX_FEATURE_IS_STANDALONE (EXMLParserFeatureType.SAX, "http://xml.org/sax/features/is-standalone"),

  /**
   * When true: The system identifiers passed to the notationDecl,
   * unparsedEntityDecl, and externalEntityDecl events will be absolutized
   * relative to their base URIs before reporting.<br>
   * When false: System identifiers in declarations will not be absolutized
   * before reporting.<br>
   * Default: true<br>
   * (http://xml.org/sax/features/resolve-dtd-uris)
   */
  SAX_RESOLVE_DTD_URIS (EXMLParserFeatureType.SAX, "http://xml.org/sax/features/resolve-dtd-uris"),

  /**
   * When true: Perform Unicode normalization checking (as described in section
   * 2.13 and Appendix B of the XML 1.1 Recommendation) and report normalization
   * errors.<br>
   * When false: Do not report Unicode normalization errors.<br>
   * Default: false<br>
   * (http://xml.org/sax/features/unicode-normalization-checking)
   */
  SAX_UNICODE_NORMALIZATION_CHECKING (EXMLParserFeatureType.SAX, "http://xml.org/sax/features/unicode-normalization-checking"),

  /**
   * When true: The Attributes objects passed by the parser in
   * org.xml.sax.ContentHandler.startElement() implement the
   * org.xml.sax.ext.Attributes2 interface.<br>
   * When false: The Attributes objects passed by the parser do not implement
   * the org.xml.sax.ext.Attributes2 interface.<br>
   * Read-only!<br>
   * Xerces-J will always report Attributes objects that also implement
   * org.xml.sax.ext.Attributes2 so the value of this feature will always be
   * true.<br>
   * (http://xml.org/sax/features/use-attributes2)
   */
  SAX_USE_ATTRIBUTES2 (EXMLParserFeatureType.SAX, "http://xml.org/sax/features/use-attributes2"),

  /**
   * When true: The Locator objects passed by the parser in
   * org.xml.sax.ContentHandler.setDocumentLocator() implement the
   * org.xml.sax.ext.Locator2 interface.<br>
   * When false: The Locator objects passed by the parser do not implement the
   * org.xml.sax.ext.Locator2 interface.<br>
   * Read-only!<br>
   * Xerces-J will always report Locator objects that also implement
   * org.xml.sax.ext.Locator2 so the value of this feature will always be true.
   * <br>
   * (http://xml.org/sax/features/use-locator2)
   */
  SAX_USE_LOCATOR2 (EXMLParserFeatureType.SAX, "http://xml.org/sax/features/use-locator2"),

  /**
   * When true: When the namespace-prefixes feature is set to true, namespace
   * declaration attributes will be reported as being in the
   * http://www.w3.org/2000/xmlns/ namespace.<br>
   * When false: Namespace declaration attributes are reported as having no
   * namespace.<br>
   * Default: false<br>
   * (http://xml.org/sax/features/xmlns-uris)
   */
  SAX_XMLNS_URIS (EXMLParserFeatureType.SAX, "http://xml.org/sax/features/xmlns-uris"),

  /**
   * When true: The parser supports both XML 1.0 and XML 1.1.<br>
   * When false: The parser supports only XML 1.0.<br>
   * Read-only!<br>
   * (http://xml.org/sax/features/xml-1.1)
   */
  SAX_IS_XML11_PARSER (EXMLParserFeatureType.SAX, "http://xml.org/sax/features/xml-1.1");

  /**
   * This map contains all necessary settings to avoid XXE attacks.
   */
  public static final Map <EXMLParserFeature, Boolean> AVOID_XXE_SETTINGS = CollectionHelper.newUnmodifiableMap (new EXMLParserFeature [] { DISALLOW_DOCTYPE_DECL,
                                                                                                                                            EXTERNAL_GENERAL_ENTITIES,
                                                                                                                                            EXTERNAL_PARAMETER_ENTITIES },
                                                                                                                 new Boolean [] { Boolean.TRUE,
                                                                                                                                  Boolean.FALSE,
                                                                                                                                  Boolean.FALSE });

  /**
   * This map contains all necessary settings to avoid entity expansion overflow
   * attacks.
   */
  public static final Map <EXMLParserFeature, Boolean> AVOID_DOS_SETTINGS = CollectionHelper.newUnmodifiableMap (new EXMLParserFeature [] { SECURE_PROCESSING },
                                                                                                                 new Boolean [] { Boolean.TRUE });

  /**
   * This map contains all necessary settings to avoid all known XML attacks
   */
  public static final Map <EXMLParserFeature, Boolean> AVOID_XML_ATTACKS = CollectionHelper.newUnmodifiableMap (ArrayHelper.newArray (AVOID_XXE_SETTINGS,
                                                                                                                                      AVOID_DOS_SETTINGS));

  private static final Logger s_aLogger = LoggerFactory.getLogger (EXMLParserFeature.class);

  private final EXMLParserFeatureType m_eType;
  private final String m_sName;
  @CodingStyleguideUnaware
  private boolean m_bWarnedOnce = false;

  private EXMLParserFeature (@Nonnull final EXMLParserFeatureType eType, @Nonnull @Nonempty final String sName)
  {
    m_eType = eType;
    m_sName = sName;
  }

  @Nonnull
  public EXMLParserFeatureType getFeatureType ()
  {
    return m_eType;
  }

  @Nonnull
  @Nonempty
  public String getName ()
  {
    return m_sName;
  }

  public void applyTo (@Nonnull final org.xml.sax.XMLReader aParser, final boolean bValue)
  {
    ValueEnforcer.notNull (aParser, "Parser");

    if (m_eType != EXMLParserFeatureType.GENERAL && m_eType != EXMLParserFeatureType.SAX)
      s_aLogger.warn ("Parser feature '" + name () + "' is not applicable for SAX parsers!");

    try
    {
      // This call is very slow as it might throw an XMLConfigurationException
      // which internally calls Throwable.fillStackTrace which takes approx. 50%
      // of the parsing time for small documents
      aParser.setFeature (m_sName, bValue);
    }
    catch (final SAXNotRecognizedException ex)
    {
      if (!m_bWarnedOnce)
      {
        s_aLogger.warn ("XML Parser does not recognize feature '" + name () + "'");
        m_bWarnedOnce = true;
      }
    }
    catch (final SAXNotSupportedException ex)
    {
      s_aLogger.warn ("XML Parser does not support feature '" + name () + "'");
    }
  }

  public void applyTo (@Nonnull final DocumentBuilderFactory aDocumentBuilderFactory, final boolean bValue)
  {
    ValueEnforcer.notNull (aDocumentBuilderFactory, "DocumentBuilderFactory");

    if (m_eType != EXMLParserFeatureType.GENERAL && m_eType != EXMLParserFeatureType.DOM)
      s_aLogger.warn ("Parser feature '" + name () + "' is not applicable for DOM parsers!");

    try
    {
      aDocumentBuilderFactory.setFeature (m_sName, bValue);
    }
    catch (final ParserConfigurationException ex)
    {
      s_aLogger.warn ("DOM parser does not support feature '" + name () + "'");
    }
  }

  @Nullable
  public static EXMLParserFeature getFromNameOrNull (@Nullable final String sName)
  {
    return EnumHelper.getFromNameOrNull (EXMLParserFeature.class, sName);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static List <EXMLParserFeature> getAllFeaturesOfType (@Nonnull final EXMLParserFeatureType eFeatureType)
  {
    ValueEnforcer.notNull (eFeatureType, "FeatureType");

    final List <EXMLParserFeature> ret = new ArrayList <EXMLParserFeature> ();
    for (final EXMLParserFeature eFeature : values ())
      if (eFeature.getFeatureType () == eFeatureType)
        ret.add (eFeature);
    return ret;
  }
}
