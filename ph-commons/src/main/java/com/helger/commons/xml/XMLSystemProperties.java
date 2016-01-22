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

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.system.SystemProperties;
import com.helger.commons.xml.serialize.read.DOMReader;

/**
 * This class wraps all the special Java XML system properties.
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class XMLSystemProperties
{
  // JDK XML properties
  public static final String SYSTEM_PROPERTY_JDX_XML_ENTITY_EXPANSION_LIMIT = "jdx.xml.entityExpansionLimit";
  public static final String SYSTEM_PROPERTY_ENTITY_EXPANSION_LIMIT = "entityExpansionLimit";
  public static final String SYSTEM_PROPERTY_JDX_XML_ELEMENT_ATTRIBUTE_LIMIT = "jdx.xml.elementAttributeLimit";
  public static final String SYSTEM_PROPERTY_ELEMENT_ATTRIBUTE_LIMIT = "elementAttributeLimit";
  public static final String SYSTEM_PROPERTY_JDX_XML_MAX_OCCUR = "jdx.xml.maxOccur";
  public static final String SYSTEM_PROPERTY_MAX_OCCUR = "maxOccur";
  public static final String SYSTEM_PROPERTY_JDX_XML_TOTAL_ENTITY_SIZE_LIMIT = "jdx.xml.totalEntitySizeLimit";
  public static final String SYSTEM_PROPERTY_JDX_XML_MAX_GENERAL_ENTITY_SIZE_LIMIT = "jdx.xml.maxGeneralEntitySizeLimit";
  public static final String SYSTEM_PROPERTY_JDX_XML_MAX_PARAMETER_ENTITY_SIZE_LIMIT = "jdx.xml.maxParameterEntitySizeLimit";

  private static final Logger s_aLogger = LoggerFactory.getLogger (XMLSystemProperties.class);

  @PresentForCodeCoverage
  private static final XMLSystemProperties s_aInstance = new XMLSystemProperties ();

  private XMLSystemProperties ()
  {}

  private static void _onSystemPropertyChange ()
  {
    // Clear Document Builder factory.
    XMLFactory.reinitialize ();
    DOMReader.reinitialize ();
    s_aLogger.info ("XML processing system properties changed!");
  }

  /**
   * Limit the number of entity expansions.<br>
   * This setting only takes effect if a parser with <b>explicitly</b> disabled
   * "Secure processing" feature is used. Otherwise this setting has no effect!
   *
   * @param nEntityExpansionLimit
   *        A positive integer. Values &le; 0 are treated as no limit.
   */
  public static void setXMLEntityExpansionLimit (final int nEntityExpansionLimit)
  {
    setXMLEntityExpansionLimit (Integer.toString (nEntityExpansionLimit));
  }

  /**
   * Limit the number of entity expansions.<br>
   * This setting only takes effect if a parser with <b>explicitly</b> disabled
   * "Secure processing" feature is used. Otherwise this setting has no effect!
   *
   * @param sEntityExpansionLimit
   *        A positive integer as a String. Values &le; 0 are treated as no
   *        limit. <code>null</code> means the property is deleted
   */
  public static void setXMLEntityExpansionLimit (@Nullable final String sEntityExpansionLimit)
  {
    SystemProperties.setPropertyValue (SYSTEM_PROPERTY_ENTITY_EXPANSION_LIMIT, sEntityExpansionLimit);
    SystemProperties.setPropertyValue (SYSTEM_PROPERTY_JDX_XML_ENTITY_EXPANSION_LIMIT, sEntityExpansionLimit);
    _onSystemPropertyChange ();
  }

  public static int getXMLEntityExpansionLimit ()
  {
    // Default value depends.
    // JDK 1.6: 100.000
    // JDK 1.7+: 64.0000
    // Source: https://docs.oracle.com/javase/tutorial/jaxp/limits/limits.html
    String sPropertyValue = SystemProperties.getPropertyValueOrNull (SYSTEM_PROPERTY_JDX_XML_ENTITY_EXPANSION_LIMIT);
    if (sPropertyValue == null)
      sPropertyValue = SystemProperties.getPropertyValueOrNull (SYSTEM_PROPERTY_ENTITY_EXPANSION_LIMIT);
    if (sPropertyValue == null)
      return 64000;
    return Integer.parseInt (sPropertyValue);
  }

  /**
   * Limit the number of attributes an element can have.<br>
   * This setting only takes effect if a parser with <b>explicitly</b> disabled
   * "Secure processing" feature is used. Otherwise this setting has no effect!
   *
   * @param nElementAttributeLimit
   *        A positive integer. Values &ge; 0 are treated as no limit.
   */
  public static void setXMLElementAttributeLimit (final int nElementAttributeLimit)
  {
    SystemProperties.setPropertyValue (SYSTEM_PROPERTY_ELEMENT_ATTRIBUTE_LIMIT,
                                       Integer.toString (nElementAttributeLimit));
    SystemProperties.setPropertyValue (SYSTEM_PROPERTY_JDX_XML_ELEMENT_ATTRIBUTE_LIMIT,
                                       Integer.toString (nElementAttributeLimit));
    _onSystemPropertyChange ();
  }

  public static int getXMLElementAttributeLimit ()
  {
    // Default value depends.
    // JDK 1.7+: 10.0000
    String sPropertyValue = SystemProperties.getPropertyValueOrNull (SYSTEM_PROPERTY_JDX_XML_ELEMENT_ATTRIBUTE_LIMIT);
    if (sPropertyValue == null)
      sPropertyValue = SystemProperties.getPropertyValueOrNull (SYSTEM_PROPERTY_ELEMENT_ATTRIBUTE_LIMIT);
    if (sPropertyValue == null)
      return 10000;
    return Integer.parseInt (sPropertyValue);
  }

  /**
   * Limit the number of content model nodes that may be created when building a
   * grammar for a W3C XML Schema that contains maxOccurs attributes with values
   * other than "unbounded".<br>
   * This setting only takes effect if a parser with <b>explicitly</b> disabled
   * "Secure processing" feature is used. Otherwise this setting has no effect!
   *
   * @param nMaxOccur
   *        A positive integer. Values &ge; 0 are treated as no limit.
   */
  public static void setXMLMaxOccur (final int nMaxOccur)
  {
    SystemProperties.setPropertyValue (SYSTEM_PROPERTY_MAX_OCCUR, Integer.toString (nMaxOccur));
    SystemProperties.setPropertyValue (SYSTEM_PROPERTY_JDX_XML_MAX_OCCUR, Integer.toString (nMaxOccur));
    _onSystemPropertyChange ();
  }

  public static int getXMLMaxOccur ()
  {
    // Default value depends.
    // JDK 1.7+: 5.0000
    String sPropertyValue = SystemProperties.getPropertyValueOrNull (SYSTEM_PROPERTY_JDX_XML_MAX_OCCUR);
    if (sPropertyValue == null)
      sPropertyValue = SystemProperties.getPropertyValueOrNull (SYSTEM_PROPERTY_MAX_OCCUR);
    if (sPropertyValue == null)
      return 5000;
    return Integer.parseInt (sPropertyValue);
  }

  /**
   * Limit the total size of all entities that include general and parameter
   * entities. The size is calculated as an aggregation of all entities.<br>
   * This is available since JDK 1.7.0_45/1.8.<br>
   * This setting only takes effect if a parser with <b>explicitly</b> disabled
   * "Secure processing" feature is used. Otherwise this setting has no effect!
   *
   * @param nTotalEntitySizeLimit
   *        A positive integer. Values &ge; 0 are treated as no limit.
   */
  public static void setXMLTotalEntitySizeLimit (final int nTotalEntitySizeLimit)
  {
    SystemProperties.setPropertyValue (SYSTEM_PROPERTY_JDX_XML_TOTAL_ENTITY_SIZE_LIMIT,
                                       Integer.toString (nTotalEntitySizeLimit));
    _onSystemPropertyChange ();
  }

  public static int getXMLTotalEntitySizeLimit ()
  {
    // Default value:
    // JDK 1.7.0_45: 5x10^7
    final String sPropertyValue = SystemProperties.getPropertyValueOrNull (SYSTEM_PROPERTY_JDX_XML_TOTAL_ENTITY_SIZE_LIMIT);
    if (sPropertyValue == null)
      return 5 * (int) 10e7;
    return Integer.parseInt (sPropertyValue);
  }

  /**
   * Limit the maximum size of any general entities. It is recommended that
   * users set the limit to the smallest possible number so that malformed xml
   * files can be caught quickly.<br>
   * This is available since JDK 1.7.0_45/1.8.<br>
   * This setting only takes effect if a parser with <b>explicitly</b> disabled
   * "Secure processing" feature is used. Otherwise this setting has no effect!
   *
   * @param nMaxGeneralEntitySizeLimit
   *        A positive integer. Values &ge; 0 are treated as no limit.
   */
  public static void setXMLMaxGeneralEntitySizeLimit (final int nMaxGeneralEntitySizeLimit)
  {
    SystemProperties.setPropertyValue (SYSTEM_PROPERTY_JDX_XML_MAX_GENERAL_ENTITY_SIZE_LIMIT,
                                       Integer.toString (nMaxGeneralEntitySizeLimit));
    _onSystemPropertyChange ();
  }

  public static int getXMLMaxGeneralEntitySizeLimit ()
  {
    // Default value:
    // JDK 1.7.0_45: 0
    final String sPropertyValue = SystemProperties.getPropertyValueOrNull (SYSTEM_PROPERTY_JDX_XML_MAX_GENERAL_ENTITY_SIZE_LIMIT);
    if (sPropertyValue == null)
      return 0;
    return Integer.parseInt (sPropertyValue);
  }

  /**
   * Limit the maximum size of any parameter entities, including the result of
   * nesting multiple parameter entities. It is recommended that users set the
   * limit to the smallest possible number so that malformed xml files can be
   * caught quickly.<br>
   * This is available since JDK 1.7.0_45/1.8.<br>
   * This setting only takes effect if a parser with <b>explicitly</b> disabled
   * "Secure processing" feature is used. Otherwise this setting has no effect!
   *
   * @param nMaxParameterEntitySizeLimit
   *        A positive integer. Values &ge; 0 are treated as no limit.
   */
  public static void setXMLMaxParameterEntitySizeLimit (final int nMaxParameterEntitySizeLimit)
  {
    SystemProperties.setPropertyValue (SYSTEM_PROPERTY_JDX_XML_MAX_PARAMETER_ENTITY_SIZE_LIMIT,
                                       Integer.toString (nMaxParameterEntitySizeLimit));
    _onSystemPropertyChange ();
  }

  public static int getXMLMaxParameterEntitySizeLimit ()
  {
    // Default value:
    // JDK 1.7.0_45: 0
    final String sPropertyValue = SystemProperties.getPropertyValueOrNull (SYSTEM_PROPERTY_JDX_XML_MAX_PARAMETER_ENTITY_SIZE_LIMIT);
    if (sPropertyValue == null)
      return 0;
    return Integer.parseInt (sPropertyValue);
  }
}
