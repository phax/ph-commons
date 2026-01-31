/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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

import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.base.debug.GlobalDebug;
import com.helger.base.system.CSystemProperty;
import com.helger.base.system.SystemProperties;

/**
 * Extension to class {@link GlobalDebug}
 *
 * @author Philip
 */
@ThreadSafe
public final class GlobalDebugExt extends GlobalDebug
{
  /**
   * Set the debug mode for the common Java components:
   * <ul>
   * <li>JAXP</li>
   * <li>Javax Activation</li>
   * <li>Javax Mail</li>
   * </ul>
   *
   * @param bDebugMode
   *        <code>true</code> to enable debug mode, <code>false</code> to disable it
   */
  public static void setJavaCommonComponentsDebugMode (final boolean bDebugMode)
  {
    // Set JAXP debugging!
    // Note: this property is read-only on Ubuntu, defined by the following
    // policy file: /etc/tomcat6/policy.d/04webapps.policy
    SystemProperties.setPropertyValue (CSystemProperty.SYSTEM_PROPERTY_JAXP_DEBUG, bDebugMode);

    // Set javax.activation debugging
    SystemProperties.setPropertyValue (CSystemProperty.SYSTEM_PROPERTY_JAVAX_ACTIVATION_DEBUG, bDebugMode);

    // Set javax.mail debugging
    SystemProperties.setPropertyValue (CSystemProperty.SYSTEM_PROPERTY_MAIL_DEBUG, bDebugMode);

    // Set serialization debugging
    SystemProperties.setPropertyValue (CSystemProperty.SYSTEM_PROPERTY_SERIALIZATION_DEBUG, bDebugMode);
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
    SystemProperties.setPropertyValue (CSystemProperty.SYSTEM_PROPERTY_JAVAX_NET_DEBUG, sValue);
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
    SystemProperties.setPropertyValue (CSystemProperty.SYSTEM_PROPERTY_JAVA_SECURITY_DEBUG, sValue);
  }
}
