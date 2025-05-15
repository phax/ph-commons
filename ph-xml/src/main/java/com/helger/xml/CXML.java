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

import com.helger.annotation.Nonnull;
import com.helger.annotation.concurrent.Immutable;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.compare.CompareHelper;
import com.helger.commons.compare.IComparator;

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

  // xml:*

  /** The preferred XML Schema namespace prefix */
  public static final String XML_NS_PREFIX_XSD = "xsd";
  /** Special XSD attribute targetNamespace */
  public static final String XML_ATTR_XSD_TARGETNAMESPACE = "targetNamespace";

  /** Preferred XML Schema instance namespace prefix */
  public static final String XML_NS_PREFIX_XSI = "xsi";
  /** Special XSI attribute schemaLocation */
  public static final String XML_ATTR_XSI_SCHEMALOCATION = "schemaLocation";
  /** Special XSI attribute noNamespaceSchemaLocation */
  public static final String XML_ATTR_XSI_NONAMESPACESCHEMALOCATION = "noNamespaceSchemaLocation";

  @PresentForCodeCoverage
  private static final CXML INSTANCE = new CXML ();

  private CXML ()
  {}

  @Nonnull
  public static IComparator <QName> getComparatorQNameNamespaceURIBeforeLocalPart ()
  {
    return (o1, o2) -> {
      int ret = CompareHelper.compare (o1.getNamespaceURI (), o2.getNamespaceURI (), true);
      if (ret == 0)
        ret = o1.getLocalPart ().compareTo (o2.getLocalPart ());
      return ret;
    };
  }

  @Nonnull
  public static IComparator <QName> getComparatorQNameLocalPartBeforeNamespaceURI ()
  {
    return (o1, o2) -> {
      int ret = o1.getLocalPart ().compareTo (o2.getLocalPart ());
      if (ret == 0)
        ret = CompareHelper.compare (o1.getNamespaceURI (), o2.getNamespaceURI (), true);
      return ret;
    };
  }

  @Nonnull
  public static IComparator <QName> getComparatorQNameForNamespacePrefix ()
  {
    return (o1, o2) -> {
      String sPrefix1 = o1.getPrefix ();
      if (XMLConstants.XMLNS_ATTRIBUTE.equals (sPrefix1))
        sPrefix1 = o1.getLocalPart ();
      String sPrefix2 = o2.getPrefix ();
      if (XMLConstants.XMLNS_ATTRIBUTE.equals (sPrefix2))
        sPrefix2 = o2.getLocalPart ();
      return sPrefix1.compareTo (sPrefix2);
    };
  }
}
