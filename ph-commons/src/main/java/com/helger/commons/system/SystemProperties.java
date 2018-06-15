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
package com.helger.commons.system;

import java.util.Properties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.DevelopersNote;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsCopyOnWriteArraySet;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.debug.GlobalDebug;
import com.helger.commons.lang.PropertiesHelper;
import com.helger.commons.lang.priviledged.IPrivilegedAction;

/**
 * This class wraps all the Java system properties like version number etc.
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class SystemProperties
{
  public static final String SYSTEM_PROPERTY_FILE_SEPARATOR = "file.separator";
  public static final String SYSTEM_PROPERTY_JAVA_CLASS_PATH = "java.class.path";
  public static final String SYSTEM_PROPERTY_JAVA_CLASS_VERSION = "java.class.version";
  public static final String SYSTEM_PROPERTY_JAVA_LIBRARY_PATH = "java.library.path";
  public static final String SYSTEM_PROPERTY_JAVA_HOME = "java.home";
  public static final String SYSTEM_PROPERTY_JAVA_IO_TMPDIR = "java.io.tmpdir";
  public static final String SYSTEM_PROPERTY_JAVA_SPECIFICATION_URL = "java.specification.url";
  public static final String SYSTEM_PROPERTY_JAVA_SPECIFICATION_VENDOR = "java.specification.vendor";
  public static final String SYSTEM_PROPERTY_JAVA_SPECIFICATION_VERSION = "java.specification.version";
  public static final String SYSTEM_PROPERTY_JAVA_VENDOR = "java.vendor";
  public static final String SYSTEM_PROPERTY_JAVA_VENDOR_URL = "java.vendor.url";
  public static final String SYSTEM_PROPERTY_JAVA_VERSION = "java.version";
  public static final String SYSTEM_PROPERTY_JAVA_VM_NAME = "java.vm.name";
  public static final String SYSTEM_PROPERTY_JAVA_VM_SPECIFICATION_URL = "java.vm.specification.url";
  public static final String SYSTEM_PROPERTY_JAVA_VM_SPECIFICATION_VENDOR = "java.vm.specification.vendor";
  public static final String SYSTEM_PROPERTY_JAVA_VM_SPECIFICATION_VERSION = "java.vm.specification.version";
  public static final String SYSTEM_PROPERTY_JAVA_VM_URL = "java.vm.url";
  public static final String SYSTEM_PROPERTY_JAVA_VM_VENDOR = "java.vm.vendor";
  public static final String SYSTEM_PROPERTY_JAVA_VM_VERSION = "java.vm.version";
  public static final String SYSTEM_PROPERTY_LINE_SEPARATOR = "line.separator";
  public static final String SYSTEM_PROPERTY_OS_ARCH = "os.arch";
  public static final String SYSTEM_PROPERTY_OS_NAME = "os.name";
  public static final String SYSTEM_PROPERTY_OS_VERSION = "os.version";
  public static final String SYSTEM_PROPERTY_PATH_SEPARATOR = "path.separator";
  public static final String SYSTEM_PROPERTY_USER_DIR = "user.dir";
  public static final String SYSTEM_PROPERTY_USER_HOME = "user.home";
  public static final String SYSTEM_PROPERTY_USER_NAME = "user.name";

  // JDK serialization properties
  public static final String SYSTEM_PROPERTY_SUN_IO_SERIALIZATION_EXTENDEDDEBUGINFO = "sun.io.serialization.extendedDebugInfo";

  private static final Logger s_aLogger = LoggerFactory.getLogger (SystemProperties.class);
  private static final ICommonsSet <String> s_aWarnedPropertyNames = new CommonsCopyOnWriteArraySet <> ();

  @PresentForCodeCoverage
  private static final SystemProperties s_aInstance = new SystemProperties ();

  private SystemProperties ()
  {}

  @Nullable
  public static String getPropertyValueOrNull (@Nullable final String sKey)
  {
    return sKey == null ? null : IPrivilegedAction.systemGetProperty (sKey).invokeSafe ();
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
        // Warn about each property once
        if (s_aLogger.isWarnEnabled ())
          s_aLogger.warn ("System property '" + sKey + "' is not set!");
      }
    }
    return ret;
  }

  /**
   * Clear the cache with the property names, for which warnings were emitted
   * that keys don't exist.
   */
  public static void clearWarnedPropertyNames ()
  {
    s_aWarnedPropertyNames.clear ();
  }

  /**
   * @return A copy of the set with all property names for which warnings were
   *         emitted.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsSet <String> getAllWarnedPropertyNames ()
  {
    // Convert from CopyOnWrite to regular HashSet
    return new CommonsHashSet <> (s_aWarnedPropertyNames);
  }

  /**
   * Set a system property value under consideration of an eventually present
   * {@link SecurityManager}.
   *
   * @param sKey
   *        The key of the system property. May not be <code>null</code>.
   * @param bValue
   *        The value of the system property.
   */
  public static void setPropertyValue (@Nonnull final String sKey, final boolean bValue)
  {
    setPropertyValue (sKey, Boolean.toString (bValue));
  }

  /**
   * Set a system property value under consideration of an eventually present
   * {@link SecurityManager}.
   *
   * @param sKey
   *        The key of the system property. May not be <code>null</code>.
   * @param nValue
   *        The value of the system property.
   * @since 8.5.7
   */
  public static void setPropertyValue (@Nonnull final String sKey, final int nValue)
  {
    setPropertyValue (sKey, Integer.toString (nValue));
  }

  /**
   * Set a system property value under consideration of an eventually present
   * {@link SecurityManager}.
   *
   * @param sKey
   *        The key of the system property. May not be <code>null</code>.
   * @param nValue
   *        The value of the system property.
   * @since 8.5.7
   */
  public static void setPropertyValue (@Nonnull final String sKey, final long nValue)
  {
    setPropertyValue (sKey, Long.toString (nValue));
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
    boolean bChanged;
    if (sValue == null)
    {
      // Was something removed?
      bChanged = removePropertyValue (sKey) != null;
    }
    else
    {
      final String sOld = IPrivilegedAction.systemSetProperty (sKey, sValue).invokeSafe ();
      bChanged = sOld != null && !sValue.equals (sOld);
      if (s_aLogger.isDebugEnabled () && bChanged)
        s_aLogger.debug ("Set system property '" + sKey + "' to '" + sValue + "'");
    }
    // TODO next minor release - change to EChange return type
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
    final String ret = IPrivilegedAction.systemClearProperty (sKey).invokeSafe ();
    if (s_aLogger.isDebugEnabled ())
      if (ret != null)
        s_aLogger.debug ("Removed system property '" + sKey + "' with value '" + ret + "'");
      else
        s_aLogger.debug ("Remove system property '" + sKey + "' failed");
    return ret;
  }

  @Nullable
  public static String getJavaVersion ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_VERSION);
  }

  @Nullable
  public static String getJavaVendor ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_VENDOR);
  }

  @Nullable
  public static String getJavaVendorURL ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_VENDOR_URL);
  }

  @Nullable
  public static String getJavaHome ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_HOME);
  }

  @Nullable
  public static String getJavaClassVersion ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_CLASS_VERSION);
  }

  @Nullable
  public static String getJavaClassPath ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_CLASS_PATH);
  }

  @Nullable
  public static String getJavaLibraryPath ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_LIBRARY_PATH);
  }

  @Nullable
  public static String getOsName ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_OS_NAME);
  }

  @Nullable
  public static String getOsArch ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_OS_ARCH);
  }

  @Nullable
  public static String getOsVersion ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_OS_VERSION);
  }

  @Nullable
  public static String getFileSeparator ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_FILE_SEPARATOR);
  }

  @Nullable
  public static String getPathSeparator ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_PATH_SEPARATOR);
  }

  @Nullable
  public static String getLineSeparator ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_LINE_SEPARATOR);
  }

  @Nullable
  public static String getUserName ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_USER_NAME);
  }

  @Nullable
  public static String getUserHome ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_USER_HOME);
  }

  @Nullable
  public static String getUserDir ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_USER_DIR);
  }

  @Nullable
  public static String getJavaVmName ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_VM_NAME);
  }

  @Nullable
  public static String getJavaVmSpecificationVersion ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_VM_SPECIFICATION_VERSION);
  }

  @Nullable
  public static String getJavaVmSpecificationVendor ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_VM_SPECIFICATION_VENDOR);
  }

  @Nullable
  public static String getJavaVmSpecificationUrl ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_VM_SPECIFICATION_URL);
  }

  @Nullable
  public static String getJavaVmVersion ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_VM_VERSION);
  }

  @Nullable
  public static String getJavaVmVendor ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_VM_VENDOR);
  }

  @Nullable
  public static String getJavaVmUrl ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_VM_URL);
  }

  @Nullable
  public static String getJavaSpecificationVersion ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_SPECIFICATION_VERSION);
  }

  @Nullable
  public static String getJavaSpecificationVendor ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_SPECIFICATION_VENDOR);
  }

  @Nullable
  public static String getJavaSpecificationUrl ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_SPECIFICATION_URL);
  }

  @DevelopersNote ("This property is not part of the language but part of the Sun SDK")
  @Nullable
  public static String getTmpDir ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_IO_TMPDIR);
  }

  /**
   * @return A set with all defined property names. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsSet <String> getAllPropertyNames ()
  {
    return getAllProperties ().copyOfKeySet ();
  }

  /**
   * @return A map with all system properties where the key is the system
   *         property name and the value is the system property value.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsMap <String, String> getAllProperties ()
  {
    final Properties aProperties = IPrivilegedAction.systemGetProperties ().invokeSafe ();
    if (aProperties == null)
      return new CommonsHashMap <> ();
    return PropertiesHelper.getAsStringMap (aProperties);
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
   * Get a set of system property names which are relevant for network
   * debugging/proxy handling. This method is meant to be used for reading the
   * appropriate settings from a configuration file.
   *
   * @return An array with all system property names which are relevant for
   *         debugging/proxy handling. Never <code>null</code> and never empty.
   *         Each call returns a new array.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static String [] getAllJavaNetSystemProperties ()
  {
    // http://docs.oracle.com/javase/7/docs/technotes/guides/security/jsse/ReadDebug.html
    // http://download.oracle.com/javase/6/docs/technotes/guides/net/proxies.html
    // The first 2 (*.debug) should both be set to "all" to have the most
    // effects
    return new String [] { GlobalDebug.SYSTEM_PROPERTY_JAVAX_NET_DEBUG,
                           GlobalDebug.SYSTEM_PROPERTY_JAVA_SECURITY_DEBUG,
                           "java.net.useSystemProxies",
                           "http.proxyHost",
                           "http.proxyPort",
                           "http.nonProxyHosts",
                           "https.proxyHost",
                           "https.proxyPort" };
  }
}
