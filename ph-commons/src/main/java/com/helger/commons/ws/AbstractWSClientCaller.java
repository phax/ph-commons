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

import java.net.URL;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.system.SystemProperties;

/**
 * Abstract base class for a webservice client caller.
 *
 * @author Philip Helger
 */
public abstract class AbstractWSClientCaller
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractWSClientCaller.class);

  private final URL m_aEndpointAddress;
  private SSLSocketFactory m_aSSLSocketFactory;
  private HostnameVerifier m_aHostnameVerifier;

  /**
   * Creates a service caller for the service meta data interface
   *
   * @param aEndpointAddress
   *        The address of the SML management interface. May not be
   *        <code>null</code>.
   */
  public AbstractWSClientCaller (@Nonnull final URL aEndpointAddress)
  {
    ValueEnforcer.notNull (aEndpointAddress, "EndpointAddress");
    m_aEndpointAddress = aEndpointAddress;

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("Using SML endpoint address '" + m_aEndpointAddress.toExternalForm () + "'");
  }

  /**
   * @return The endpoint address as specified in the constructor. Never
   *         <code>null</code>.
   */
  @Nonnull
  public URL getEndpointAddress ()
  {
    return m_aEndpointAddress;
  }

  /**
   * @return The {@link SSLSocketFactory} to be used by this client. Is
   *         <code>null</code> by default.
   */
  @Nullable
  public SSLSocketFactory getSSLSocketFactory ()
  {
    return m_aSSLSocketFactory;
  }

  /**
   * Change the {@link SSLSocketFactory} to be used by this client.
   *
   * @param aSSLSocketFactory
   *        The factory to use. Maybe <code>null</code> to indicate, that the
   *        default {@link SSLSocketFactory} is to be used.
   */
  public void setSSLSocketFactory (@Nullable final SSLSocketFactory aSSLSocketFactory)
  {
    m_aSSLSocketFactory = aSSLSocketFactory;
  }

  /**
   * @return The {@link HostnameVerifier} to be used by this client. Is
   *         <code>null</code> by default.
   */
  @Nullable
  public HostnameVerifier getHostnameVerifier ()
  {
    return m_aHostnameVerifier;
  }

  /**
   * Change the {@link HostnameVerifier} to be used by this client.
   *
   * @param aHostnameVerifier
   *        The factory to use. Maybe <code>null</code> to indicate, that the
   *        default {@link HostnameVerifier} is to be used.
   */
  public void setHostnameVerifier (@Nullable final HostnameVerifier aHostnameVerifier)
  {
    m_aHostnameVerifier = aHostnameVerifier;
  }

  /**
   * Implement this method in your derived class to add custom handlers.
   *
   * @param aHandlerList
   *        The handler list to be filled. Never <code>null</code>.
   */
  @OverrideOnDemand
  @SuppressWarnings ("rawtypes")
  protected void addHandlers (@Nonnull final List <Handler> aHandlerList)
  {}

  @OverrideOnDemand
  @OverridingMethodsMustInvokeSuper
  protected void applyWSSettingsToBindingProvider (@Nonnull final BindingProvider aBP)
  {
    aBP.getRequestContext ().put (BindingProvider.ENDPOINT_ADDRESS_PROPERTY, m_aEndpointAddress.toString ());
    if (m_aSSLSocketFactory != null)
    {
      aBP.getRequestContext ().put ("com.sun.xml.ws.transport.https.client.SSLSocketFactory", m_aSSLSocketFactory);
      aBP.getRequestContext ().put ("com.sun.xml.internal.ws.transport.https.client.SSLSocketFactory",
                                    m_aSSLSocketFactory);
    }
    if (m_aHostnameVerifier != null)
    {
      aBP.getRequestContext ().put ("com.sun.xml.ws.transport.https.client.hostname.verifier", m_aHostnameVerifier);
      aBP.getRequestContext ().put ("com.sun.xml.internal.ws.transport.https.client.hostname.verifier",
                                    m_aHostnameVerifier);
    }

    final int nConnectTimeoutMS = 5000;
    aBP.getRequestContext ().put ("com.sun.xml.ws.connect.timeout", Integer.valueOf (nConnectTimeoutMS));
    aBP.getRequestContext ().put ("com.sun.xml.internal.ws.connect.timeout", Integer.valueOf (nConnectTimeoutMS));

    final int nRequestTimeoutMS = 5000;
    aBP.getRequestContext ().put ("com.sun.xml.ws.request.timeout", Integer.valueOf (nRequestTimeoutMS));
    aBP.getRequestContext ().put ("com.sun.xml.internal.ws.request.timeout", Integer.valueOf (nRequestTimeoutMS));

    @SuppressWarnings ("rawtypes")
    final List <Handler> aHandlers = aBP.getBinding ().getHandlerChain ();
    // Fill handlers
    addHandlers (aHandlers);
    aBP.getBinding ().setHandlerChain (aHandlers);
  }

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
}
