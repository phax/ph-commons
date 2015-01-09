/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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

import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.concurrent.ThreadSafe;

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
   * By default trace mode is disabled
   */
  public static final boolean DEFAULT_TRACE_MODE = false;
  /**
   * By default debug mode is enabled
   */
  public static final boolean DEFAULT_DEBUG_MODE = true;
  /**
   * By default production mode is disable
   */
  public static final boolean DEFAULT_PRODUCTION_MODE = false;

  private static final AtomicBoolean s_aTraceMode = new AtomicBoolean (DEFAULT_TRACE_MODE);
  private static final AtomicBoolean s_aDebugMode = new AtomicBoolean (DEFAULT_DEBUG_MODE);
  private static final AtomicBoolean s_aProductionMode = new AtomicBoolean (DEFAULT_PRODUCTION_MODE);

  // to set it per dependency injection
  public void setTraceMode (final boolean bTraceMode)
  {
    setTraceModeDirect (bTraceMode);
  }

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
   * Enable or disable trace mode. If trace mode is enabled, also debug mode is
   * enabled.
   *
   * @param bTraceMode
   *        <code>true</code> to enable, <code>false</code> to disable
   */
  public static void setTraceModeDirect (final boolean bTraceMode)
  {
    s_aTraceMode.set (bTraceMode);

    // If enabling trace mode, also enable debug mode
    if (bTraceMode)
      setDebugModeDirect (true);
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
    // Enable or disable JAXP debugging!
    // Note: this property is read only on Ubuntu, defined by the following
    // policy file: /etc/tomcat6/policy.d/04webapps.policy
    SystemProperties.setPropertyValue ("jaxp.debug", Boolean.toString (bDebugMode));

    // Enable javax.activation debugging
    SystemProperties.setPropertyValue ("javax.activation.debug", Boolean.toString (bDebugMode));

    // Enable javax.mail debugging
    SystemProperties.setPropertyValue ("mail.debug", Boolean.toString (bDebugMode));
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
    s_aDebugMode.set (bDebugMode);

    setJavaCommonComponentsDebugMode (bDebugMode);

    // If disabling debug mode, also disable trace mode
    if (!bDebugMode)
      setTraceModeDirect (false);
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
    s_aProductionMode.set (bProductionMode);

    // If enabling production mode, disable debug and trace mode
    if (bProductionMode)
      setDebugModeDirect (false);
  }

  /**
   * @return <code>true</code> if trace mode is active, <code>false</code> if
   *         not
   */
  public static boolean isTraceMode ()
  {
    return s_aTraceMode.get ();
  }

  /**
   * @return <code>true</code> if debug mode is active, <code>false</code> if
   *         not
   */
  public static boolean isDebugMode ()
  {
    return s_aDebugMode.get ();
  }

  /**
   * @return <code>true</code> if production mode is active, <code>false</code>
   *         if not
   */
  public static boolean isProductionMode ()
  {
    return s_aProductionMode.get ();
  }
}
