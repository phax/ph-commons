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
package com.helger.commons.ws;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.xml.ws.handler.MessageContext;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.system.SystemProperties;

/**
 * Helper class for Webservice invocations and servics
 *
 * @author Philip Helger
 */
@Immutable
public final class WSHelper
{
  private WSHelper ()
  {}

  /**
   * Enable the JAX-WS SOAP debugging. This shows the exchanged SOAP messages in
   * the log file. By default this logging is disabled.
   *
   * @param bDebug
   *        <code>true</code> to enable debugging, <code>false</code> to disable
   *        it.
   */
  public static void enableSoapLogging (final boolean bDebug)
  {
    SystemProperties.setPropertyValue ("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump",
                                       Boolean.toString (bDebug));
    SystemProperties.setPropertyValue ("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump",
                                       Boolean.toString (bDebug));
  }

  /**
   * Enable advanced JAX-WS debugging on more or less all relevant layers. This
   * method internally calls {@link #enableSoapLogging(boolean)} so it does not
   * need to be called explicitly. By default all this logging is disabled.
   *
   * @param bDebug
   *        <code>true</code> to enabled debugging, <code>false</code> to
   *        disable it.
   */
  public static void setMetroDebugSystemProperties (final boolean bDebug)
  {
    // Depending on the used JAX-WS version, the property names are
    // different....
    enableSoapLogging (bDebug);

    SystemProperties.setPropertyValue ("com.sun.xml.ws.transport.http.HttpAdapter.dump", Boolean.toString (bDebug));
    SystemProperties.setPropertyValue ("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump",
                                       Boolean.toString (bDebug));

    SystemProperties.setPropertyValue ("com.sun.xml.ws.fault.SOAPFaultBuilder.disableCaptureStackTrace",
                                       bDebug ? null : "false");

    SystemProperties.setPropertyValue ("com.sun.metro.soap.dump", Boolean.toString (bDebug));
    SystemProperties.setPropertyValue ("com.sun.xml.wss.provider.wsit.SecurityTubeFactory.dump",
                                       Boolean.toString (bDebug));
    SystemProperties.setPropertyValue ("com.sun.xml.wss.jaxws.impl.SecurityServerTube.dump", Boolean.toString (bDebug));
    SystemProperties.setPropertyValue ("com.sun.xml.wss.jaxws.impl.SecurityClientTube.dump", Boolean.toString (bDebug));
    SystemProperties.setPropertyValue ("com.sun.xml.ws.rx.rm.runtime.ClientTube.dump", Boolean.toString (bDebug));
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
    return new String [] { "javax.net.debug",
                           "java.security.debug",
                           "java.net.useSystemProxies",
                           "http.proxyHost",
                           "http.proxyPort",
                           "http.nonProxyHosts",
                           "https.proxyHost",
                           "https.proxyPort" };
  }

  public static boolean isOutboundMessage (@Nonnull final MessageContext aContext)
  {
    return ((Boolean) aContext.get (MessageContext.MESSAGE_OUTBOUND_PROPERTY)).booleanValue ();
  }
}
