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
package com.helger.base.system;

import java.util.HashSet;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.misc.DevelopersNote;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.CGlobal;
import com.helger.base.log.ConditionalLogger;
import com.helger.base.log.IHasConditionalLogger;
import com.helger.base.rt.NonBlockingProperties;
import com.helger.base.state.EChange;
import com.helger.base.string.StringParser;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * This class wraps all the Java system properties like version number etc.
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class SystemProperties implements IHasConditionalLogger
{
  /** The global Java class version as a double value. */
  public static final double JAVA_CLASS_VERSION = StringParser.parseDouble (getJavaClassVersion (),
                                                                            CGlobal.ILLEGAL_DOUBLE);

  private static final Logger LOGGER = LoggerFactory.getLogger (SystemProperties.class);
  private static final ConditionalLogger CONDLOG = new ConditionalLogger (LOGGER);
  private static final Set <String> WARNED_PROP_NAMES = new CopyOnWriteArraySet <> ();

  @PresentForCodeCoverage
  private static final SystemProperties INSTANCE = new SystemProperties ();

  private SystemProperties ()
  {}

  /**
   * @return <code>true</code> if logging is disabled, <code>false</code> if it is enabled.
   */
  public static boolean isSilentMode ()
  {
    return CONDLOG.isDisabled ();
  }

  /**
   * Enable or disable certain regular log messages.
   *
   * @param bSilentMode
   *        <code>true</code> to disable logging, <code>false</code> to enable logging
   * @return The previous value of the silent mode.
   */
  public static boolean setSilentMode (final boolean bSilentMode)
  {
    return !CONDLOG.setEnabled (!bSilentMode);
  }

  @Nullable
  public static String getPropertyValueOrNull (@Nullable final String sKey)
  {
    return sKey == null ? null : System.getProperty (sKey);
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
        CONDLOG.warn ( () -> "System property '" + sKey + "' cannot be read because it is not set");
      }
    }
    return ret;
  }

  /**
   * Clear the cache with the property names, for which warnings were emitted that keys don't exist.
   */
  public static void clearWarnedPropertyNames ()
  {
    WARNED_PROP_NAMES.clear ();
  }

  /**
   * @return A copy of the set with all property names for which warnings were emitted.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static Set <String> getAllWarnedPropertyNames ()
  {
    // Convert from CopyOnWrite to regular HashSet
    return new HashSet <> (WARNED_PROP_NAMES);
  }

  /**
   * Set a system property value.
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
   * Set a system property value .
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
   * Set a system property value
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
   * Set a system property value
   *
   * @param sKey
   *        The key of the system property. May not be <code>null</code>.
   * @param sValue
   *        The value of the system property. If the value is <code>null</code> the property is
   *        removed.
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
      final String sOld = System.setProperty (sKey, sValue);
      bChanged = sOld != null && !sValue.equals (sOld);
      if (bChanged)
        CONDLOG.info ( () -> "Set system property '" + sKey + "' to '" + sValue + "'");
    }
    return EChange.valueOf (bChanged);
  }

  /**
   * Remove a system property value
   *
   * @param sKey
   *        The key of the system property to be removed. May not be <code>null</code>.
   * @return the previous string value of the system property, or <code>null</code> if there was no
   *         property with that key.
   */
  @Nullable
  public static String removePropertyValue (@Nonnull final String sKey)
  {
    final String sOldValue = System.clearProperty (sKey);
    if (sOldValue != null)
      CONDLOG.info ( () -> "Removed system property '" + sKey + "' with value '" + sOldValue + "'");
    else
      CONDLOG.warn ( () -> "Remove system property '" + sKey + "' failed");
    return sOldValue;
  }

  /**
   * @return System property value <code>java.version</code>
   */
  @Nullable
  public static String getJavaVersion ()
  {
    return getPropertyValue (CSystemProperties.SYSTEM_PROPERTY_JAVA_VERSION);
  }

  /**
   * @return System property value <code>java.vendor</code>
   */
  @Nullable
  public static String getJavaVendor ()
  {
    return getPropertyValue (CSystemProperties.SYSTEM_PROPERTY_JAVA_VENDOR);
  }

  /**
   * @return System property value <code>java.vendor.url</code>
   */
  @Nullable
  public static String getJavaVendorURL ()
  {
    return getPropertyValue (CSystemProperties.SYSTEM_PROPERTY_JAVA_VENDOR_URL);
  }

  /**
   * @return System property value <code>java.home</code>
   */
  @Nullable
  public static String getJavaHome ()
  {
    return getPropertyValue (CSystemProperties.SYSTEM_PROPERTY_JAVA_HOME);
  }

  /**
   * @return System property value <code>java.class.version</code>
   */
  @Nullable
  public static String getJavaClassVersion ()
  {
    return getPropertyValue (CSystemProperties.SYSTEM_PROPERTY_JAVA_CLASS_VERSION);
  }

  /**
   * @return System property value <code>java.class.path</code>
   */
  @Nullable
  public static String getJavaClassPath ()
  {
    return getPropertyValue (CSystemProperties.SYSTEM_PROPERTY_JAVA_CLASS_PATH);
  }

  /**
   * @return System property value <code>java.library.path</code>
   */
  @Nullable
  public static String getJavaLibraryPath ()
  {
    return getPropertyValue (CSystemProperties.SYSTEM_PROPERTY_JAVA_LIBRARY_PATH);
  }

  /**
   * @return System property value <code>os.name</code>
   */
  @Nullable
  public static String getOsName ()
  {
    return getPropertyValue (CSystemProperties.SYSTEM_PROPERTY_OS_NAME);
  }

  /**
   * @return System property value <code>os.arch</code>
   */
  @Nullable
  public static String getOsArch ()
  {
    return getPropertyValue (CSystemProperties.SYSTEM_PROPERTY_OS_ARCH);
  }

  /**
   * @return System property value <code>os.version</code>
   */
  @Nullable
  public static String getOsVersion ()
  {
    return getPropertyValue (CSystemProperties.SYSTEM_PROPERTY_OS_VERSION);
  }

  /**
   * @return System property value <code>file.separator</code>
   */
  @Nullable
  public static String getFileSeparator ()
  {
    return getPropertyValue (CSystemProperties.SYSTEM_PROPERTY_FILE_SEPARATOR);
  }

  /**
   * @return System property value <code>path.separator</code>
   */
  @Nullable
  public static String getPathSeparator ()
  {
    return getPropertyValue (CSystemProperties.SYSTEM_PROPERTY_PATH_SEPARATOR);
  }

  /**
   * @return System property value <code>line.separator</code>
   */
  @Nullable
  public static String getLineSeparator ()
  {
    return System.lineSeparator ();
  }

  /**
   * @return System property value <code>user.name</code>
   */
  @Nullable
  public static String getUserName ()
  {
    return getPropertyValue (CSystemProperties.SYSTEM_PROPERTY_USER_NAME);
  }

  /**
   * @return System property value <code>user.home</code>
   */
  @Nullable
  public static String getUserHome ()
  {
    return getPropertyValue (CSystemProperties.SYSTEM_PROPERTY_USER_HOME);
  }

  /**
   * @return System property value <code>user.dir</code>
   */
  @Nullable
  public static String getUserDir ()
  {
    return getPropertyValue (CSystemProperties.SYSTEM_PROPERTY_USER_DIR);
  }

  /**
   * @return System property value <code>java.vm.name</code>
   */
  @Nullable
  public static String getJavaVmName ()
  {
    return getPropertyValue (CSystemProperties.SYSTEM_PROPERTY_JAVA_VM_NAME);
  }

  /**
   * @return System property value <code>java.vm.specification.version</code>
   */
  @Nullable
  public static String getJavaVmSpecificationVersion ()
  {
    return getPropertyValue (CSystemProperties.SYSTEM_PROPERTY_JAVA_VM_SPECIFICATION_VERSION);
  }

  /**
   * @return System property value <code>java.vm.specification.vendor</code>
   */
  @Nullable
  public static String getJavaVmSpecificationVendor ()
  {
    return getPropertyValue (CSystemProperties.SYSTEM_PROPERTY_JAVA_VM_SPECIFICATION_VENDOR);
  }

  /**
   * @return System property value <code>java.vm.specification.url</code>
   */
  @Nullable
  public static String getJavaVmSpecificationUrl ()
  {
    return getPropertyValue (CSystemProperties.SYSTEM_PROPERTY_JAVA_VM_SPECIFICATION_URL);
  }

  /**
   * @return System property value <code>java.vm.version</code>
   */
  @Nullable
  public static String getJavaVmVersion ()
  {
    return getPropertyValue (CSystemProperties.SYSTEM_PROPERTY_JAVA_VM_VERSION);
  }

  /**
   * @return System property value <code>java.vm.vendor</code>
   */
  @Nullable
  public static String getJavaVmVendor ()
  {
    return getPropertyValue (CSystemProperties.SYSTEM_PROPERTY_JAVA_VM_VENDOR);
  }

  /**
   * @return System property value <code>java.vm.url</code>
   */
  @Nullable
  public static String getJavaVmUrl ()
  {
    return getPropertyValue (CSystemProperties.SYSTEM_PROPERTY_JAVA_VM_URL);
  }

  /**
   * @return System property value <code>java.specification.version</code>
   */
  @Nullable
  public static String getJavaSpecificationVersion ()
  {
    return getPropertyValue (CSystemProperties.SYSTEM_PROPERTY_JAVA_SPECIFICATION_VERSION);
  }

  /**
   * @return System property value <code>java.specification.vendor</code>
   */
  @Nullable
  public static String getJavaSpecificationVendor ()
  {
    return getPropertyValue (CSystemProperties.SYSTEM_PROPERTY_JAVA_SPECIFICATION_VENDOR);
  }

  /**
   * @return System property value <code>java.specification.url</code>
   */
  @Nullable
  public static String getJavaSpecificationUrl ()
  {
    return getPropertyValue (CSystemProperties.SYSTEM_PROPERTY_JAVA_SPECIFICATION_URL);
  }

  /**
   * @return System property value <code>java.io.tmpdir</code>
   */
  @DevelopersNote ("This property is not part of the language but part of the Sun SDK")
  @Nullable
  public static String getTmpDir ()
  {
    return getPropertyValue (CSystemProperties.SYSTEM_PROPERTY_JAVA_IO_TMPDIR);
  }

  /**
   * @return System property value <code>java.runtime.version</code>
   */
  @Nullable
  public static String getJavaRuntimeVersion ()
  {
    return getPropertyValue (CSystemProperties.SYSTEM_PROPERTY_JAVA_RUNTIME_VERSION);
  }

  /**
   * @return System property value <code>java.runtime.version</code>
   */
  @Nullable
  public static String getJavaRuntimeName ()
  {
    return getPropertyValue (CSystemProperties.SYSTEM_PROPERTY_JAVA_RUNTIME_NAME);
  }

  /**
   * @return A map with all system properties where the key is the system property name and the
   *         value is the system property value.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static NonBlockingProperties getAllProperties ()
  {
    final Properties aProperties = System.getProperties ();
    if (aProperties == null)
      return new NonBlockingProperties ();

    final NonBlockingProperties ret = new NonBlockingProperties ();
    for (final var e : aProperties.entrySet ())
      ret.put (Objects.toString (e.getKey ()), Objects.toString (e.getValue ()));
    return ret;
  }

  /**
   * @return A set with all defined property names. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static Set <String> getAllPropertyNames ()
  {
    final Set <String> ret = new HashSet <> ();
    final Properties aProperties = System.getProperties ();
    if (aProperties != null)
      for (final Object aKey : aProperties.keySet ())
        ret.add (Objects.toString (aKey));
    return ret;
  }

  /**
   * Check if a system property with the given name exists.
   *
   * @param sPropertyName
   *        The name of the property.
   * @return <code>true</code> if such a system property is present, <code>false</code> otherwise
   */
  public static boolean containsPropertyName (final String sPropertyName)
  {
    return getAllProperties ().containsKey (sPropertyName);
  }

  /**
   * Get a set of system property names which are relevant for network debugging/proxy handling.
   * This method is meant to be used for reading the appropriate settings from a configuration file.
   *
   * @return An array with all system property names which are relevant for debugging/proxy
   *         handling. Never <code>null</code> and never empty. Each call returns a new array.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static String [] getAllJavaNetSystemProperties ()
  {
    // http://docs.oracle.com/javase/7/docs/technotes/guides/security/jsse/ReadDebug.html
    // http://download.oracle.com/javase/6/docs/technotes/guides/net/proxies.html
    // The first 2 (*.debug) should both be set to "all" to have the most
    // effects
    return new String [] { CSystemProperties.SYSTEM_PROPERTY_JAVAX_NET_DEBUG,
                           CSystemProperties.SYSTEM_PROPERTY_JAVA_SECURITY_DEBUG,
                           "java.net.useSystemProxies",
                           "http.proxyHost",
                           "http.proxyPort",
                           "http.nonProxyHosts",
                           "https.proxyHost",
                           "https.proxyPort" };
  }
}
