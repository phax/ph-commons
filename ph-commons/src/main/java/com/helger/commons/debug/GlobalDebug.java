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
package com.helger.commons.debug;

import java.util.concurrent.atomic.AtomicBoolean;

import com.helger.annotation.Nullable;
import com.helger.annotation.concurrent.ThreadSafe;

import com.helger.commons.system.SystemProperties;

/**
 * Global class for handling the following typical application modes:
 * <ul>
 * <li>trace</li>
 * <li>debug</li>
 * <li>production</li>
 * </ul>
 * trace is the lowest mode, debug is one level higher and production is the
 * highest value. By default all modes are deactivated.
 *
 * @author Philip
 */
@ThreadSafe
public final class GlobalDebug
{
  /**
   * By default debug mode is enabled
   */
  public static final boolean DEFAULT_DEBUG_MODE = true;
  /**
   * By default production mode is disable
   */
  public static final boolean DEFAULT_PRODUCTION_MODE = false;
  /**
   * By default silent mode is enabled.
   * 
   * @since 9.4.0
   */
  public static final boolean DEFAULT_SILENT_MODE = true;

  public static final String SYSTEM_PROPERTY_MAIL_DEBUG = "mail.debug";
  public static final String SYSTEM_PROPERTY_JAVA_SECURITY_DEBUG = "java.security.debug";
  public static final String SYSTEM_PROPERTY_JAVAX_ACTIVATION_DEBUG = "javax.activation.debug";
  public static final String SYSTEM_PROPERTY_JAVAX_NET_DEBUG = "javax.net.debug";
  public static final String SYSTEM_PROPERTY_JAXP_DEBUG = "jaxp.debug";
  public static final String SYSTEM_PROPERTY_SERIALIZATION_DEBUG = "sun.io.serialization.extendedDebugInfo";

  private static final AtomicBoolean DEBUG_MODE = new AtomicBoolean (DEFAULT_DEBUG_MODE);
  private static final AtomicBoolean PRODUCTION_MODE = new AtomicBoolean (DEFAULT_PRODUCTION_MODE);

  // to set it per dependency injection
  public void setDebugMode (final boolean bDebugMode)
  {
    setDebugModeDirect (bDebugMode);
  }

  // to set it per dependency injection
  public void setProductionMode (final boolean bProductionMode)
  {
    setProductionModeDirect (bProductionMode);
  }

  /**
   * Set the debug mode for the common Java components:
   * <ul>
   * <li>JAXP</li>
   * <li>Javax Activation</li>
   * <li>Javax Mail</li>
   * </ul>
   *
   * @param bDebugMode
   *        <code>true</code> to enable debug mode, <code>false</code> to
   *        disable it
   */
  public static void setJavaCommonComponentsDebugMode (final boolean bDebugMode)
  {
    // Set JAXP debugging!
    // Note: this property is read-only on Ubuntu, defined by the following
    // policy file: /etc/tomcat6/policy.d/04webapps.policy
    SystemProperties.setPropertyValue (SYSTEM_PROPERTY_JAXP_DEBUG, bDebugMode);

    // Set javax.activation debugging
    SystemProperties.setPropertyValue (SYSTEM_PROPERTY_JAVAX_ACTIVATION_DEBUG, bDebugMode);

    // Set javax.mail debugging
    SystemProperties.setPropertyValue (SYSTEM_PROPERTY_MAIL_DEBUG, bDebugMode);

    // Set serialization debugging
    SystemProperties.setPropertyValue (SYSTEM_PROPERTY_SERIALIZATION_DEBUG, bDebugMode);
  }

  /**
   * Enable or disable Java net debugging.
   *
   * @param sValue
   *        Debug property value. Valid values are:
   *        <ul>
   *        <li><code>null</code></li>
   *        <li>all</li>
   *        <li>ssl</li>
   *        <li>ssl:xxx (see Java docs what xxx can be)</li>
   *        </ul>
   * @since 8.6.1
   */
  public static void setJavaNetDebugMode (@Nullable final String sValue)
  {
    SystemProperties.setPropertyValue (SYSTEM_PROPERTY_JAVAX_NET_DEBUG, sValue);
  }

  /**
   * Enable or disable Java security debugging.
   *
   * @param sValue
   *        Debug property value. Valid values are:
   *        <ul>
   *        <li><code>null</code></li>
   *        <li>all</li>
   *        <li>access</li>
   *        <li>certpath</li>
   *        <li>combiner</li>
   *        <li>gssloginconfig</li>
   *        <li>configfile</li>
   *        <li>configparser</li>
   *        <li>jar</li>
   *        <li>logincontext</li>
   *        <li>policy</li>
   *        <li>provider</li>
   *        <li>scl</li>
   *        </ul>
   * @since 8.6.1
   */
  public static void setJavaSecurityDebugMode (@Nullable final String sValue)
  {
    SystemProperties.setPropertyValue (SYSTEM_PROPERTY_JAVA_SECURITY_DEBUG, sValue);
  }

  /**
   * Enable or disable debug mode. If debug mode is disabled, also trace mode is
   * disabled.
   *
   * @param bDebugMode
   *        <code>true</code> to enable, <code>false</code> to disable
   */
  public static void setDebugModeDirect (final boolean bDebugMode)
  {
    DEBUG_MODE.set (bDebugMode);

    setJavaCommonComponentsDebugMode (bDebugMode);
  }

  /**
   * Enable or disable production mode. If production mode is enabled, also
   * trace mode and debug mode are disabled.
   *
   * @param bProductionMode
   *        <code>true</code> to enable, <code>false</code> to disable
   */
  public static void setProductionModeDirect (final boolean bProductionMode)
  {
    PRODUCTION_MODE.set (bProductionMode);

    // If enabling production mode, disable debug mode
    if (bProductionMode)
      setDebugModeDirect (false);
  }

  /**
   * @return <code>true</code> if debug mode is active, <code>false</code> if
   *         not
   */
  public static boolean isDebugMode ()
  {
    return DEBUG_MODE.get ();
  }

  /**
   * @return <code>true</code> if production mode is active, <code>false</code>
   *         if not
   */
  public static boolean isProductionMode ()
  {
    return PRODUCTION_MODE.get ();
  }
}
