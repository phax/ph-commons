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
package com.helger.commons.xml;

import javax.annotation.concurrent.Immutable;
import javax.xml.XMLConstants;

import com.helger.commons.annotations.PresentForCodeCoverage;

/**
 * This is just a workaround if Xerces is not in the build path. Normally you
 * would use the constants from the file org.apache.xerces.impl.Constants
 * 
 * @author Philip
 */
@Immutable
public final class CXML
{
  // DOM Level 3 features defined in Core:
  /** DOM level 3 feature */
  public static final String DOM_DISCARD_DEFAULT_CONTENT = "discard-default-content";
  /** DOM level 3 feature */
  public static final String DOM_NORMALIZE_CHARACTERS = "normalize-characters";
  /** DOM level 3 feature */
  public static final String DOM_CHECK_CHAR_NORMALIZATION = "check-character-normalization";
  /** DOM level 3 feature */
  public static final String DOM_WELLFORMED = "well-formed";
  /** DOM level 3 feature */
  public static final String DOM_SPLIT_CDATA = "split-cdata-sections";

  // DOM Load and Save:
  /** DOM LS feature */
  public static final String DOM_FORMAT_PRETTY_PRINT = "format-pretty-print";
  /** DOM LS feature */
  public static final String DOM_XMLDECL = "xml-declaration";
  /** DOM LS feature */
  public static final String DOM_UNKNOWNCHARS = "unknown-characters";
  /** DOM LS feature */
  public static final String DOM_CERTIFIED = "certified";
  /** DOM LS feature */
  public static final String DOM_DISALLOW_DOCTYPE = "disallow-doctype";
  /** DOM LS feature */
  public static final String DOM_IGNORE_UNKNOWN_CHARACTER_DENORMALIZATIONS = "ignore-unknown-character-denormalizations";

  /** DOM event */
  public static final String EVENT_DOMNODE_INSERTED = "DOMNodeInserted";

  // XML default names, namespaces and prefixes

  // xmlns:*
  /** separator between namespace prefix and element name */
  public static final char XML_PREFIX_NAMESPACE_SEP = ':';
  /** separator between namespace prefix and element name */
  public static final String XML_PREFIX_NAMESPACE_SEP_STR = Character.toString (XML_PREFIX_NAMESPACE_SEP);
  /** The default xmlns attribute */
  public static final String XML_ATTR_XMLNS = XMLConstants.XMLNS_ATTRIBUTE;
  /** The default xmlns attribute including the separator == xmlns: */
  public static final String XML_ATTR_XMLNS_WITH_SEP = XML_ATTR_XMLNS + XML_PREFIX_NAMESPACE_SEP;

  // xml:*
  /** The special namespace prefix "xml" and the separator == xml: */
  public static final String XML_ATTR_XML_WITH_SEP = XMLConstants.XML_NS_PREFIX + XML_PREFIX_NAMESPACE_SEP;
  /**
   * The special attribute xml:space. A special attribute named xml:space MAY be
   * attached to an element to signal an intention that in that element, white
   * space should be preserved by applications. (values preserve/default)
   */
  public static final String XML_ATTR_SPACE = XML_ATTR_XML_WITH_SEP + "space";
  /**
   * The special attribute xml:lang. The values of the attribute are language
   * identifiers as defined by [IETF RFC 3066], Tags for the Identification of
   * Languages, or its successor; in addition, the empty string MAY be
   * specified.
   */
  public static final String XML_ATTR_LANG = XML_ATTR_XML_WITH_SEP + "lang";
  /**
   * The special attribute xml:base. Specify a base URI (used to resolve
   * relative URI's) other than the base URI of the document or external entity.
   */
  public static final String XML_ATTR_BASE = XML_ATTR_XML_WITH_SEP + "base";
  /**
   * The special attribute xml:id. Instead of using the 'ID' type. This can make
   * sure the value for this attribute is unique within the XML document
   */
  public static final String XML_ATTR_ID = XML_ATTR_XML_WITH_SEP + "id";

  // XML Schema Definition (XS and XSD) stuff:
  /** The XML schema namespace URI */
  public static final String XML_NS_XSD = XMLConstants.W3C_XML_SCHEMA_NS_URI;
  /** The preferred XML Schema namespace prefix */
  public static final String XML_NS_PREFIX_XSD = "xsd";
  /** The complete XML Schema namespace attribute */
  public static final String XMLNS_XSD = XML_ATTR_XMLNS_WITH_SEP + XML_NS_PREFIX_XSD;
  /** Special XSD attribute targetNamespace */
  public static final String XML_ATTR_XSD_TARGETNAMESPACE = "targetNamespace";
  /** Special XSD attribute targetNamespace */
  public static final String XSD_TARGETNAMESPACE = XML_NS_PREFIX_XSD +
                                                   XML_PREFIX_NAMESPACE_SEP +
                                                   XML_ATTR_XSD_TARGETNAMESPACE;

  // XML Schema Instance (XSI) stuff:
  /** XML Schema instance namespace URI */
  public static final String XML_NS_XSI = XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
  /** Preferred XML Schema instance namespace prefix */
  public static final String XML_NS_PREFIX_XSI = "xsi";
  /** The complete XML Schema instance namespace attribute */
  public static final String XMLNS_XSI = XML_ATTR_XMLNS_WITH_SEP + XML_NS_PREFIX_XSI;
  /** Special XSI attribute schemaLocation */
  public static final String XML_ATTR_XSI_SCHEMALOCATION = "schemaLocation";
  /** Special XSI attribute schemaLocation */
  public static final String XSI_SCHEMALOCATION = XML_NS_PREFIX_XSI +
                                                  XML_PREFIX_NAMESPACE_SEP +
                                                  XML_ATTR_XSI_SCHEMALOCATION;
  /** Special XSI attribute noNamespaceSchemaLocation */
  public static final String XML_ATTR_XSI_NONAMESPACESCHEMALOCATION = "noNamespaceSchemaLocation";
  /** Special XSI attribute noNamespaceSchemaLocation */
  public static final String XSI_NONAMESPACESCHEMALOCATION = XML_NS_PREFIX_XSI +
                                                             XML_PREFIX_NAMESPACE_SEP +
                                                             XML_ATTR_XSI_NONAMESPACESCHEMALOCATION;

  @PresentForCodeCoverage
  @SuppressWarnings ("unused")
  private static final CXML s_aInstance = new CXML ();

  private CXML ()
  {}
}
