/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
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
package com.helger.commons;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotations.DevelopersNote;
import com.helger.commons.annotations.PresentForCodeCoverage;
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.priviledged.AccessControllerHelper;
import com.helger.commons.priviledged.PrivilegedActionSystemClearProperty;
import com.helger.commons.priviledged.PrivilegedActionSystemGetProperties;
import com.helger.commons.priviledged.PrivilegedActionSystemGetProperty;
import com.helger.commons.priviledged.PrivilegedActionSystemSetProperty;

/**
 * This class wraps all the Java system properties like version number etc.
 * 
 * @author Philip Helger
 */
@Immutable
public final class SystemProperties
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (SystemProperties.class);
  private static final Set <String> s_aWarnedPropertyNames = new HashSet <String> ();

  @PresentForCodeCoverage
  @SuppressWarnings ("unused")
  private static final SystemProperties s_aInstance = new SystemProperties ();

  private SystemProperties ()
  {}

  @Nullable
  public static String getPropertyValueOrNull (@Nullable final String sKey)
  {
    return sKey == null ? null : AccessControllerHelper.call (new PrivilegedActionSystemGetProperty (sKey));
  }

  @Nullable
  public static String getPropertyValue (@Nullable final String sKey)
  {
    String ret = null;
    if (sKey != null)
    {
      ret = getPropertyValueOrNull (sKey);
      if (ret == null && s_aWarnedPropertyNames.add (sKey))
      {
        // don't show anything for phloc ;-)
        if (!sKey.contains ("phloc"))
        {
          // Warn about each property once
          s_aLogger.warn ("System property '" + sKey + "' is not set!");
        }
      }
    }
    return ret;
  }

  /**
   * Set a system property value under consideration of an eventually present
   * {@link SecurityManager}.
   * 
   * @param sKey
   *        The key of the system property. May not be <code>null</code>.
   * @param sValue
   *        The value of the system property. If the value is <code>null</code>
   *        the property is removed.
   */
  public static void setPropertyValue (@Nonnull final String sKey, @Nullable final String sValue)
  {
    if (sValue == null)
      removePropertyValue (sKey);
    else
      AccessControllerHelper.run (new PrivilegedActionSystemSetProperty (sKey, sValue));
  }

  /**
   * Remove a system property value under consideration of an eventually present
   * {@link SecurityManager}.
   * 
   * @param sKey
   *        The key of the system property to be removed. May not be
   *        <code>null</code>.
   * @return the previous string value of the system property, or
   *         <code>null</code> if there was no property with that key.
   */
  @Nullable
  public static String removePropertyValue (@Nonnull final String sKey)
  {
    return AccessControllerHelper.call (new PrivilegedActionSystemClearProperty (sKey));
  }

  @Nullable
  public static String getJavaVersion ()
  {
    return getPropertyValue ("java.version");
  }

  @Nullable
  public static String getJavaVendor ()
  {
    return getPropertyValue ("java.vendor");
  }

  @Nullable
  public static String getJavaVendorURL ()
  {
    return getPropertyValue ("java.vendor.url");
  }

  @Nullable
  public static String getJavaHome ()
  {
    return getPropertyValue ("java.home");
  }

  @Nullable
  public static String getJavaClassVersion ()
  {
    return getPropertyValue ("java.class.version");
  }

  @Nullable
  public static String getJavaClassPath ()
  {
    return getPropertyValue ("java.class.path");
  }

  @Nullable
  public static String getJavaLibraryPath ()
  {
    return getPropertyValue ("java.library.path");
  }

  @Nullable
  public static String getOsName ()
  {
    return getPropertyValue ("os.name");
  }

  @Nullable
  public static String getOsArch ()
  {
    return getPropertyValue ("os.arch");
  }

  @Nullable
  public static String getOsVersion ()
  {
    return getPropertyValue ("os.version");
  }

  @Nullable
  public static String getFileSeparator ()
  {
    return getPropertyValue ("file.separator");
  }

  @Nullable
  public static String getPathSeparator ()
  {
    return getPropertyValue ("path.separator");
  }

  @Nullable
  public static String getLineSeparator ()
  {
    return getPropertyValue ("line.separator");
  }

  @Nullable
  public static String getUserName ()
  {
    return getPropertyValue ("user.name");
  }

  @Nullable
  public static String getUserHome ()
  {
    return getPropertyValue ("user.home");
  }

  @Nullable
  public static String getUserDir ()
  {
    return getPropertyValue ("user.dir");
  }

  @Nullable
  public static String getJavaVmSpecificationVersion ()
  {
    return getPropertyValue ("java.vm.specification.version");
  }

  @Nullable
  public static String getJavaVmSpecificationVendor ()
  {
    return getPropertyValue ("java.vm.specification.vendor");
  }

  @Nullable
  public static String getJavaVmSpecificationUrl ()
  {
    return getPropertyValue ("java.vm.specification.url");
  }

  @Nullable
  public static String getJavaVmVersion ()
  {
    return getPropertyValue ("java.vm.version");
  }

  @Nullable
  public static String getJavaVmVendor ()
  {
    return getPropertyValue ("java.vm.vendor");
  }

  @Nullable
  public static String getJavaVmUrl ()
  {
    return getPropertyValue ("java.vm.url");
  }

  @Nullable
  public static String getJavaSpecificationVersion ()
  {
    return getPropertyValue ("java.specification.version");
  }

  @Nullable
  public static String getJavaSpecificationVendor ()
  {
    return getPropertyValue ("java.specification.vendor");
  }

  @Nullable
  public static String getJavaSpecificationUrl ()
  {
    return getPropertyValue ("java.specification.url");
  }

  @DevelopersNote ("This property is not part of the language but part of the Sun SDK")
  @Nullable
  public static String getTmpDir ()
  {
    return getPropertyValue ("java.io.tmpdir");
  }

  /**
   * @return A set with all defined property names. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static Set <String> getAllPropertyNames ()
  {
    return new HashSet <String> (getAllProperties ().keySet ());
  }

  /**
   * @return A map with all system properties where the key is the system
   *         property name and the value is the system property value.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static Map <String, String> getAllProperties ()
  {
    final Map <String, String> ret = new HashMap <String, String> ();
    final Properties aProperties = AccessControllerHelper.call (new PrivilegedActionSystemGetProperties ());
    if (aProperties != null)
      for (final Map.Entry <Object, Object> aEntry : aProperties.entrySet ())
      {
        final String sKey = (String) aEntry.getKey ();
        ret.put (sKey, (String) aEntry.getValue ());
      }
    return ret;
  }

  /**
   * Check if a system property with the given name exists.
   * 
   * @param sPropertyName
   *        The name of the property.
   * @return <code>true</code> if such a system property is present,
   *         <code>false</code> otherwise
   */
  public static boolean containsPropertyName (final String sPropertyName)
  {
    return getAllProperties ().containsKey (sPropertyName);
  }

  /**
   * Limit the number of entity expansions.
   * 
   * @param nEntityExpansionLimit
   *        A positive integer. Values &ge; 0 are treated as no limit.
   */
  public static void setXMLEntityExpansionLimit (final int nEntityExpansionLimit)
  {
    setPropertyValue ("entityExpansionLimit", Integer.toString (nEntityExpansionLimit));
    setPropertyValue ("jdx.xml.entityExpansionLimit", Integer.toString (nEntityExpansionLimit));
  }

  public static int getXMLEntityExpansionLimit ()
  {
    // Default value depends.
    // JDK 1.6: 100.000
    // JDK 1.7+: 64.0000
    String sPropertyValue = getPropertyValueOrNull ("jdx.xml.entityExpansionLimit");
    if (sPropertyValue == null)
      sPropertyValue = getPropertyValueOrNull ("entityExpansionLimit");
    if (sPropertyValue == null)
      return 64000;
    return Integer.parseInt (sPropertyValue);
  }

  /**
   * Limit the number of attributes an element can have
   * 
   * @param nElementAttributeLimit
   *        A positive integer. Values &ge; 0 are treated as no limit.
   */
  public static void setXMLElementAttributeLimit (final int nElementAttributeLimit)
  {
    setPropertyValue ("elementAttributeLimit", Integer.toString (nElementAttributeLimit));
    setPropertyValue ("jdx.xml.elementAttributeLimit", Integer.toString (nElementAttributeLimit));
  }

  public static int getXMLElementAttributeLimit ()
  {
    // Default value depends.
    // JDK 1.7+: 10.0000
    String sPropertyValue = getPropertyValueOrNull ("jdx.xml.elementAttributeLimit");
    if (sPropertyValue == null)
      sPropertyValue = getPropertyValueOrNull ("elementAttributeLimit");
    if (sPropertyValue == null)
      return 10000;
    return Integer.parseInt (sPropertyValue);
  }

  /**
   * Limit the number of contentmodel nodes that may be created when building a
   * grammar for a W3C XML Schema that contains maxOccurs attributes with values
   * other than "unbounded".
   * 
   * @param nMaxOccur
   *        A positive integer. Values &ge; 0 are treated as no limit.
   */
  public static void setXMLMaxOccur (final int nMaxOccur)
  {
    setPropertyValue ("maxOccur", Integer.toString (nMaxOccur));
    setPropertyValue ("jdx.xml.maxOccur", Integer.toString (nMaxOccur));
  }

  public static int getXMLMaxOccur ()
  {
    // Default value depends.
    // JDK 1.7+: 5.0000
    String sPropertyValue = getPropertyValueOrNull ("jdx.xml.maxOccur");
    if (sPropertyValue == null)
      sPropertyValue = getPropertyValueOrNull ("maxOccur");
    if (sPropertyValue == null)
      return 5000;
    return Integer.parseInt (sPropertyValue);
  }

  /**
   * Limit the total size of all entities that include general and parameter
   * entities. The size is calculated as an aggregation of all entities.<br>
   * This is available since JDK 1.7.0_45/1.8
   * 
   * @param nTotalEntitySizeLimit
   *        A positive integer. Values &ge; 0 are treated as no limit.
   */
  public static void setXMLTotalEntitySizeLimit (final int nTotalEntitySizeLimit)
  {
    setPropertyValue ("jdx.xml.totalEntitySizeLimit", Integer.toString (nTotalEntitySizeLimit));
  }

  public static int getXMLTotalEntitySizeLimit ()
  {
    // Default value:
    // JDK 1.7.0_45: 5x10^7
    final String sPropertyValue = getPropertyValueOrNull ("jdx.xml.totalEntitySizeLimit");
    if (sPropertyValue == null)
      return 5 * (int) 10e7;
    return Integer.parseInt (sPropertyValue);
  }

  /**
   * Limit the maximum size of any general entities. It is recommended that
   * users set the limit to the smallest possible number so that malformed xml
   * files can be caught quickly.<br>
   * This is available since JDK 1.7.0_45/1.8
   * 
   * @param nMaxGeneralEntitySizeLimit
   *        A positive integer. Values &ge; 0 are treated as no limit.
   */
  public static void setXMLMaxGeneralEntitySizeLimit (final int nMaxGeneralEntitySizeLimit)
  {
    setPropertyValue ("jdx.xml.maxGeneralEntitySizeLimit", Integer.toString (nMaxGeneralEntitySizeLimit));
  }

  public static int getXMLMaxGeneralEntitySizeLimit ()
  {
    // Default value:
    // JDK 1.7.0_45: 0
    final String sPropertyValue = getPropertyValueOrNull ("jdx.xml.maxGeneralEntitySizeLimit");
    if (sPropertyValue == null)
      return 0;
    return Integer.parseInt (sPropertyValue);
  }

  /**
   * Limit the maximum size of any parameter entities, including the result of
   * nesting multiple parameter entities. It is recommended that users set the
   * limit to the smallest possible number so that malformed xml files can be
   * caught quickly.<br>
   * This is available since JDK 1.7.0_45/1.8
   * 
   * @param nMaxParameterEntitySizeLimit
   *        A positive integer. Values &ge; 0 are treated as no limit.
   */
  public static void setXMLMaxParameterEntitySizeLimit (final int nMaxParameterEntitySizeLimit)
  {
    setPropertyValue ("jdx.xml.maxParameterEntitySizeLimit", Integer.toString (nMaxParameterEntitySizeLimit));
  }

  public static int getXMLMaxParameterEntitySizeLimit ()
  {
    // Default value:
    // JDK 1.7.0_45: 0
    final String sPropertyValue = getPropertyValueOrNull ("jdx.xml.maxParameterEntitySizeLimit");
    if (sPropertyValue == null)
      return 0;
    return Integer.parseInt (sPropertyValue);
  }
}
