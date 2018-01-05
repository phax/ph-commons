/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.system.SystemProperties;
import com.helger.xml.serialize.read.DOMReader;

/**
 * This class wraps all the special Java XML system properties.
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class XMLSystemProperties
{
  // JDK XML properties
  public static final String SYSTEM_PROPERTY_JDX_XML_ENTITY_EXPANSION_LIMIT = "jdk.xml.entityExpansionLimit";
  public static final String SYSTEM_PROPERTY_ENTITY_EXPANSION_LIMIT = "entityExpansionLimit";
  public static final String SYSTEM_PROPERTY_JDX_XML_ELEMENT_ATTRIBUTE_LIMIT = "jdk.xml.elementAttributeLimit";
  public static final String SYSTEM_PROPERTY_ELEMENT_ATTRIBUTE_LIMIT = "elementAttributeLimit";
  public static final String SYSTEM_PROPERTY_JDX_XML_MAX_OCCUR = "jdk.xml.maxOccurLimit";
  public static final String SYSTEM_PROPERTY_MAX_OCCUR = "maxOccurLimit";
  public static final String SYSTEM_PROPERTY_JDX_XML_TOTAL_ENTITY_SIZE_LIMIT = "jdk.xml.totalEntitySizeLimit";
  public static final String SYSTEM_PROPERTY_JDX_XML_MAX_GENERAL_ENTITY_SIZE_LIMIT = "jdk.xml.maxGeneralEntitySizeLimit";
  public static final String SYSTEM_PROPERTY_JDX_XML_MAX_PARAMETER_ENTITY_SIZE_LIMIT = "jdk.xml.maxParameterEntitySizeLimit";

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

  /**
   * The same as {@link #setXMLEntityExpansionLimit(int)} but just that the
   * value is only set, if the limit is increased!
   *
   * @param nMaxOccur
   *        A positive integer. Values &le; 0 are treated as no limit.
   * @since 8.6.2
   * @see #setXMLEntityExpansionLimit(int)
   */
  public static void setXMLEntityExpansionLimitIfLarger (final int nMaxOccur)
  {
    final int nOldValue = getXMLEntityExpansionLimit ();
    if (nOldValue > 0)
    {
      // Current value is limited
      if (nMaxOccur <= 0 || nMaxOccur > nOldValue)
      {
        // New value is unlimited or higher
        setXMLEntityExpansionLimit (nMaxOccur);
      }
    }
    // else -> cannot be increased
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
   *        A positive integer. Values &le; 0 are treated as no limit.
   */
  public static void setXMLElementAttributeLimit (final int nElementAttributeLimit)
  {
    setXMLElementAttributeLimit (Integer.toString (nElementAttributeLimit));
  }

  /**
   * Limit the number of attributes an element can have.<br>
   * This setting only takes effect if a parser with <b>explicitly</b> disabled
   * "Secure processing" feature is used. Otherwise this setting has no effect!
   *
   * @param sElementAttributeLimit
   *        A positive integer. Values &le; 0 are treated as no limit.
   *        <code>null</code> means the property is deleted
   * @since 8.6.2
   */
  public static void setXMLElementAttributeLimit (@Nullable final String sElementAttributeLimit)
  {
    SystemProperties.setPropertyValue (SYSTEM_PROPERTY_ELEMENT_ATTRIBUTE_LIMIT, sElementAttributeLimit);
    SystemProperties.setPropertyValue (SYSTEM_PROPERTY_JDX_XML_ELEMENT_ATTRIBUTE_LIMIT, sElementAttributeLimit);
    _onSystemPropertyChange ();
  }

  /**
   * The same as {@link #setXMLElementAttributeLimit(int)} but just that the
   * value is only set, if the limit is increased!
   *
   * @param nElementAttributeLimit
   *        A positive integer. Values &le; 0 are treated as no limit.
   * @since 8.6.2
   * @see #setXMLElementAttributeLimit(int)
   */
  public static void setXMLElementAttributeLimitIfLarger (final int nElementAttributeLimit)
  {
    final int nOldValue = getXMLElementAttributeLimit ();
    if (nOldValue > 0)
    {
      // Current value is limited
      if (nElementAttributeLimit <= 0 || nElementAttributeLimit > nOldValue)
      {
        // New value is unlimited or higher
        setXMLElementAttributeLimit (nElementAttributeLimit);
      }
    }
    // else -> cannot be increased
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
   *        A positive integer. Values &le; 0 are treated as no limit.
   */
  public static void setXMLMaxOccur (final int nMaxOccur)
  {
    setXMLMaxOccur (Integer.toString (nMaxOccur));
  }

  /**
   * Limit the number of content model nodes that may be created when building a
   * grammar for a W3C XML Schema that contains maxOccurs attributes with values
   * other than "unbounded".<br>
   * This setting only takes effect if a parser with <b>explicitly</b> disabled
   * "Secure processing" feature is used. Otherwise this setting has no effect!
   *
   * @param sMaxOccur
   *        A positive integer. Values &le; 0 are treated as no limit.
   *        <code>null</code> means the property is deleted.
   * @since 8.6.2
   */
  public static void setXMLMaxOccur (@Nullable final String sMaxOccur)
  {
    SystemProperties.setPropertyValue (SYSTEM_PROPERTY_MAX_OCCUR, sMaxOccur);
    SystemProperties.setPropertyValue (SYSTEM_PROPERTY_JDX_XML_MAX_OCCUR, sMaxOccur);
    _onSystemPropertyChange ();
  }

  /**
   * The same as {@link #setXMLMaxOccur(int)} but just that the value is only
   * set, if the limit is increased!
   *
   * @param nMaxOccur
   *        A positive integer. Values &le; 0 are treated as no limit.
   * @since 8.6.2
   * @see #setXMLMaxOccur(int)
   */
  public static void setXMLMaxOccurIfLarger (final int nMaxOccur)
  {
    final int nOldValue = getXMLMaxOccur ();
    if (nOldValue > 0)
    {
      // Current value is limited
      if (nMaxOccur <= 0 || nMaxOccur > nOldValue)
      {
        // New value is unlimited or higher
        setXMLMaxOccur (nMaxOccur);
      }
    }
    // else -> cannot be increased
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
   *        A positive integer. Values &le; 0 are treated as no limit.
   */
  public static void setXMLTotalEntitySizeLimit (final int nTotalEntitySizeLimit)
  {
    setXMLTotalEntitySizeLimit (Integer.toString (nTotalEntitySizeLimit));
  }

  /**
   * Limit the total size of all entities that include general and parameter
   * entities. The size is calculated as an aggregation of all entities.<br>
   * This is available since JDK 1.7.0_45/1.8.<br>
   * This setting only takes effect if a parser with <b>explicitly</b> disabled
   * "Secure processing" feature is used. Otherwise this setting has no effect!
   *
   * @param sTotalEntitySizeLimit
   *        A positive integer. Values &le; 0 are treated as no limit.
   *        <code>null</code> means the property is deleted.
   * @since 8.6.2
   */
  public static void setXMLTotalEntitySizeLimit (@Nullable final String sTotalEntitySizeLimit)
  {
    SystemProperties.setPropertyValue (SYSTEM_PROPERTY_JDX_XML_TOTAL_ENTITY_SIZE_LIMIT, sTotalEntitySizeLimit);
    _onSystemPropertyChange ();
  }

  /**
   * The same as {@link #setXMLTotalEntitySizeLimit(int)} but just that the
   * value is only set, if the limit is increased!
   *
   * @param nTotalEntitySizeLimit
   *        A positive integer. Values &le; 0 are treated as no limit.
   * @since 8.6.2
   * @see #setXMLTotalEntitySizeLimit(int)
   */
  public static void setXMLTotalEntitySizeLimitIfLarger (final int nTotalEntitySizeLimit)
  {
    final int nOldValue = getXMLTotalEntitySizeLimit ();
    if (nOldValue > 0)
    {
      // Current value is limited
      if (nTotalEntitySizeLimit <= 0 || nTotalEntitySizeLimit > nOldValue)
      {
        // New value is unlimited or higher
        setXMLTotalEntitySizeLimit (nTotalEntitySizeLimit);
      }
    }
    // else -> cannot be increased
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
   *        A positive integer. Values &le; 0 are treated as no limit.
   */
  public static void setXMLMaxGeneralEntitySizeLimit (final int nMaxGeneralEntitySizeLimit)
  {
    setXMLMaxGeneralEntitySizeLimit (Integer.toString (nMaxGeneralEntitySizeLimit));
  }

  /**
   * Limit the maximum size of any general entities. It is recommended that
   * users set the limit to the smallest possible number so that malformed xml
   * files can be caught quickly.<br>
   * This is available since JDK 1.7.0_45/1.8.<br>
   * This setting only takes effect if a parser with <b>explicitly</b> disabled
   * "Secure processing" feature is used. Otherwise this setting has no effect!
   *
   * @param sMaxGeneralEntitySizeLimit
   *        A positive integer. Values &le; 0 are treated as no limit.
   *        <code>null</code> means the property is deleted.
   * @since 8.6.2
   */
  public static void setXMLMaxGeneralEntitySizeLimit (@Nullable final String sMaxGeneralEntitySizeLimit)
  {
    SystemProperties.setPropertyValue (SYSTEM_PROPERTY_JDX_XML_MAX_GENERAL_ENTITY_SIZE_LIMIT,
                                       sMaxGeneralEntitySizeLimit);
    _onSystemPropertyChange ();
  }

  /**
   * The same as {@link #setXMLMaxGeneralEntitySizeLimit(int)} but just that the
   * value is only set, if the limit is increased!
   *
   * @param nMaxGeneralEntitySizeLimit
   *        A positive integer. Values &le; 0 are treated as no limit.
   * @since 8.6.2
   * @see #setXMLMaxGeneralEntitySizeLimit(int)
   */
  public static void setXMLMaxGeneralEntitySizeLimitIfLarger (final int nMaxGeneralEntitySizeLimit)
  {
    final int nOldValue = getXMLMaxGeneralEntitySizeLimit ();
    if (nOldValue > 0)
    {
      // Current value is limited
      if (nMaxGeneralEntitySizeLimit <= 0 || nMaxGeneralEntitySizeLimit > nOldValue)
      {
        // New value is unlimited or higher
        setXMLMaxGeneralEntitySizeLimit (nMaxGeneralEntitySizeLimit);
      }
    }
    // else -> cannot be increased
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
   *        A positive integer. Values &le; 0 are treated as no limit.
   */
  public static void setXMLMaxParameterEntitySizeLimit (final int nMaxParameterEntitySizeLimit)
  {
    setXMLMaxParameterEntitySizeLimit (Integer.toString (nMaxParameterEntitySizeLimit));
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
   * @param sMaxParameterEntitySizeLimit
   *        A positive integer. Values &le; 0 are treated as no limit.
   *        <code>null</code> means the property is deleted.
   * @since 8.6.2
   */
  public static void setXMLMaxParameterEntitySizeLimit (@Nullable final String sMaxParameterEntitySizeLimit)
  {
    SystemProperties.setPropertyValue (SYSTEM_PROPERTY_JDX_XML_MAX_PARAMETER_ENTITY_SIZE_LIMIT,
                                       sMaxParameterEntitySizeLimit);
    _onSystemPropertyChange ();
  }

  /**
   * The same as {@link #setXMLMaxParameterEntitySizeLimit(int)} but just that
   * the value is only set, if the limit is increased!
   *
   * @param nMaxParameterEntitySizeLimit
   *        A positive integer. Values &le; 0 are treated as no limit.
   * @since 8.6.2
   * @see #setXMLMaxParameterEntitySizeLimit(int)
   */
  public static void setXMLMaxParameterEntitySizeLimitIfLarger (final int nMaxParameterEntitySizeLimit)
  {
    final int nOldValue = getXMLMaxParameterEntitySizeLimit ();
    if (nOldValue > 0)
    {
      // Current value is limited
      if (nMaxParameterEntitySizeLimit <= 0 || nMaxParameterEntitySizeLimit > nOldValue)
      {
        // New value is unlimited or higher
        setXMLMaxParameterEntitySizeLimit (nMaxParameterEntitySizeLimit);
      }
    }
    // else -> cannot be increased
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
