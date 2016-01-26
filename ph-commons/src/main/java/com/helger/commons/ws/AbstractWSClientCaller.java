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

/**
 * Abstract base class for a webservice client caller.
 *
 * @author Philip Helger
 */
public abstract class AbstractWSClientCaller
{
  public static final int DEFAULT_CONNECTION_TIMEOUT_MS = 5000;
  public static final int DEFAULT_REQUEST_TIMEOUT_MS = 5000;

  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractWSClientCaller.class);

  private final URL m_aEndpointAddress;
  private SSLSocketFactory m_aSSLSocketFactory;
  private HostnameVerifier m_aHostnameVerifier;
  private int m_nConnectionTimeoutMS = DEFAULT_CONNECTION_TIMEOUT_MS;
  private int m_nRequestTimeoutMS = DEFAULT_REQUEST_TIMEOUT_MS;

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
   * @return The connection timeout in milliseconds.
   */
  public int getConnectionTimeoutMS ()
  {
    return m_nConnectionTimeoutMS;
  }

  /**
   * Set the connection timeout in milliseconds.
   *
   * @param nConnectionTimeoutMS
   *        Milliseconds. Only values &ge; 0 are considered.
   */
  public void setConnectionTimeoutMS (final int nConnectionTimeoutMS)
  {
    m_nConnectionTimeoutMS = nConnectionTimeoutMS;
  }

  /**
   * @return The request timeout in milliseconds.
   */
  public int getRequestTimeoutMS ()
  {
    return m_nRequestTimeoutMS;
  }

  /**
   * Set the request timeout in milliseconds.
   *
   * @param nRequestTimeoutMS
   *        Milliseconds. Only values &ge; 0 are considered.
   */
  public void setRequestTimeoutMS (final int nRequestTimeoutMS)
  {
    m_nRequestTimeoutMS = nRequestTimeoutMS;
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

    if (m_nConnectionTimeoutMS >= 0)
    {
      aBP.getRequestContext ().put ("com.sun.xml.ws.connect.timeout", Integer.valueOf (m_nConnectionTimeoutMS));
      aBP.getRequestContext ().put ("com.sun.xml.internal.ws.connect.timeout",
                                    Integer.valueOf (m_nConnectionTimeoutMS));
    }

    if (m_nRequestTimeoutMS >= 0)
    {
      aBP.getRequestContext ().put ("com.sun.xml.ws.request.timeout", Integer.valueOf (m_nRequestTimeoutMS));
      aBP.getRequestContext ().put ("com.sun.xml.internal.ws.request.timeout", Integer.valueOf (m_nRequestTimeoutMS));
    }

    @SuppressWarnings ("rawtypes")
    final List <Handler> aHandlers = aBP.getBinding ().getHandlerChain ();
    // Fill handlers
    addHandlers (aHandlers);
    aBP.getBinding ().setHandlerChain (aHandlers);
  }
}
