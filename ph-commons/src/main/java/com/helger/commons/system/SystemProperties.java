/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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
import com.helger.commons.state.EChange;

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
  public static final String SYSTEM_PROPERTY_JAVA_RUNTIME_NAME = "java.runtime.name";
  public static final String SYSTEM_PROPERTY_JAVA_RUNTIME_VERSION = "java.runtime.version";
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

  private static final Logger LOGGER = LoggerFactory.getLogger (SystemProperties.class);
  private static final ICommonsSet <String> WARNED_PROP_NAMES = new CommonsCopyOnWriteArraySet <> ();

  @PresentForCodeCoverage
  private static final SystemProperties INSTANCE = new SystemProperties ();

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
      if (ret == null && WARNED_PROP_NAMES.add (sKey))
      {
        // Warn about each property once
        if (LOGGER.isWarnEnabled ())
          LOGGER.warn ("System property '" + sKey + "' is not set!");
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
    WARNED_PROP_NAMES.clear ();
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
    return new CommonsHashSet <> (WARNED_PROP_NAMES);
  }

  /**
   * Set a system property value under consideration of an eventually present
   * {@link SecurityManager}.
   *
   * @param sKey
   *        The key of the system property. May not be <code>null</code>.
   * @param bValue
   *        The value of the system property.
   * @return {@link EChange}
   */
  @Nonnull
  public static EChange setPropertyValue (@Nonnull final String sKey, final boolean bValue)
  {
    return setPropertyValue (sKey, Boolean.toString (bValue));
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
   * @return {@link EChange}
   */
  @Nonnull
  public static EChange setPropertyValue (@Nonnull final String sKey, final int nValue)
  {
    return setPropertyValue (sKey, Integer.toString (nValue));
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
   * @return {@link EChange}
   */
  @Nonnull
  public static EChange setPropertyValue (@Nonnull final String sKey, final long nValue)
  {
    return setPropertyValue (sKey, Long.toString (nValue));
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
   * @return {@link EChange}
   */
  @Nonnull
  public static EChange setPropertyValue (@Nonnull final String sKey, @Nullable final String sValue)
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
      if (LOGGER.isDebugEnabled () && bChanged)
        LOGGER.debug ("Set system property '" + sKey + "' to '" + sValue + "'");
    }
    return EChange.valueOf (bChanged);
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
    if (LOGGER.isDebugEnabled ())
      if (ret != null)
        LOGGER.debug ("Removed system property '" + sKey + "' with value '" + ret + "'");
      else
        LOGGER.debug ("Remove system property '" + sKey + "' failed");
    return ret;
  }

  /**
   * @return System property value <code>java.version</code>
   */
  @Nullable
  public static String getJavaVersion ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_VERSION);
  }

  /**
   * @return System property value <code>java.vendor</code>
   */
  @Nullable
  public static String getJavaVendor ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_VENDOR);
  }

  /**
   * @return System property value <code>java.vendor.url</code>
   */
  @Nullable
  public static String getJavaVendorURL ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_VENDOR_URL);
  }

  /**
   * @return System property value <code>java.home</code>
   */
  @Nullable
  public static String getJavaHome ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_HOME);
  }

  /**
   * @return System property value <code>java.class.version</code>
   */
  @Nullable
  public static String getJavaClassVersion ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_CLASS_VERSION);
  }

  /**
   * @return System property value <code>java.class.path</code>
   */
  @Nullable
  public static String getJavaClassPath ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_CLASS_PATH);
  }

  /**
   * @return System property value <code>java.library.path</code>
   */
  @Nullable
  public static String getJavaLibraryPath ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_LIBRARY_PATH);
  }

  /**
   * @return System property value <code>os.name</code>
   */
  @Nullable
  public static String getOsName ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_OS_NAME);
  }

  /**
   * @return System property value <code>os.arch</code>
   */
  @Nullable
  public static String getOsArch ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_OS_ARCH);
  }

  /**
   * @return System property value <code>os.version</code>
   */
  @Nullable
  public static String getOsVersion ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_OS_VERSION);
  }

  /**
   * @return System property value <code>file.separator</code>
   */
  @Nullable
  public static String getFileSeparator ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_FILE_SEPARATOR);
  }

  /**
   * @return System property value <code>path.separator</code>
   */
  @Nullable
  public static String getPathSeparator ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_PATH_SEPARATOR);
  }

  /**
   * @return System property value <code>line.separator</code>
   */
  @Nullable
  public static String getLineSeparator ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_LINE_SEPARATOR);
  }

  /**
   * @return System property value <code>user.name</code>
   */
  @Nullable
  public static String getUserName ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_USER_NAME);
  }

  /**
   * @return System property value <code>user.home</code>
   */
  @Nullable
  public static String getUserHome ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_USER_HOME);
  }

  /**
   * @return System property value <code>user.dir</code>
   */
  @Nullable
  public static String getUserDir ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_USER_DIR);
  }

  /**
   * @return System property value <code>java.vm.name</code>
   */
  @Nullable
  public static String getJavaVmName ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_VM_NAME);
  }

  /**
   * @return System property value <code>java.vm.specification.version</code>
   */
  @Nullable
  public static String getJavaVmSpecificationVersion ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_VM_SPECIFICATION_VERSION);
  }

  /**
   * @return System property value <code>java.vm.specification.vendor</code>
   */
  @Nullable
  public static String getJavaVmSpecificationVendor ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_VM_SPECIFICATION_VENDOR);
  }

  /**
   * @return System property value <code>java.vm.specification.url</code>
   */
  @Nullable
  public static String getJavaVmSpecificationUrl ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_VM_SPECIFICATION_URL);
  }

  /**
   * @return System property value <code>java.vm.version</code>
   */
  @Nullable
  public static String getJavaVmVersion ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_VM_VERSION);
  }

  /**
   * @return System property value <code>java.vm.vendor</code>
   */
  @Nullable
  public static String getJavaVmVendor ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_VM_VENDOR);
  }

  /**
   * @return System property value <code>java.vm.url</code>
   */
  @Nullable
  public static String getJavaVmUrl ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_VM_URL);
  }

  /**
   * @return System property value <code>java.specification.version</code>
   */
  @Nullable
  public static String getJavaSpecificationVersion ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_SPECIFICATION_VERSION);
  }

  /**
   * @return System property value <code>java.specification.vendor</code>
   */
  @Nullable
  public static String getJavaSpecificationVendor ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_SPECIFICATION_VENDOR);
  }

  /**
   * @return System property value <code>java.specification.url</code>
   */
  @Nullable
  public static String getJavaSpecificationUrl ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_SPECIFICATION_URL);
  }

  /**
   * @return System property value <code>java.io.tmpdir</code>
   */
  @DevelopersNote ("This property is not part of the language but part of the Sun SDK")
  @Nullable
  public static String getTmpDir ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_IO_TMPDIR);
  }

  /**
   * @return System property value <code>java.runtime.version</code>
   */
  @Nullable
  public static String getJavaRuntimeVersion ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_RUNTIME_VERSION);
  }

  /**
   * @return System property value <code>java.runtime.version</code>
   */
  @Nullable
  public static String getJavaRuntimeName ()
  {
    return getPropertyValue (SYSTEM_PROPERTY_JAVA_RUNTIME_NAME);
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
