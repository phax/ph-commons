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
package com.helger.wsclient;

import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.OverridingMethodsMustInvokeSuper;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.OverrideOnDemand;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.classloader.ClassLoaderHelper;
import com.helger.base.debug.GlobalDebug;
import com.helger.base.state.ETriState;
import com.helger.base.string.StringHelper;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.commons.http.CHttpHeader;
import com.helger.commons.http.HttpHeaderMap;
import com.helger.commons.ws.HostnameVerifierVerifyAll;
import com.helger.commons.ws.TrustManagerTrustAll;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.handler.Handler;
import jakarta.xml.ws.handler.MessageContext;

/**
 * Base configuration for a webservice client caller.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class WSClientConfig
{
  public static final int DEFAULT_CONNECTION_TIMEOUT_MS = 5000;
  public static final int DEFAULT_REQUEST_TIMEOUT_MS = 5000;
  public static final int DEFAULT_CHUNK_SIZE = -1;

  private static final Logger LOGGER = LoggerFactory.getLogger (WSClientConfig.class);

  private final URL m_aEndpointAddress;
  private SSLSocketFactory m_aSSLSocketFactory;
  private HostnameVerifier m_aHostnameVerifier;
  private int m_nConnectionTimeoutMS = DEFAULT_CONNECTION_TIMEOUT_MS;
  private int m_nRequestTimeoutMS = DEFAULT_REQUEST_TIMEOUT_MS;
  private int m_nChunkSize = DEFAULT_CHUNK_SIZE;
  private String m_sUserName;
  private String m_sPassword;
  private String m_sSOAPAction;
  private final HttpHeaderMap m_aHTTPHeaders = new HttpHeaderMap ();
  private ETriState m_eCookiesSupport = ETriState.UNDEFINED;
  private final ICommonsList <Handler <? extends MessageContext>> m_aHandlers = new CommonsArrayList <> ();

  private boolean m_bWorkAroundMASM0003 = true;

  /**
   * Creates a service caller for the service meta data interface
   *
   * @param aEndpointAddress
   *        The endpoint address of the WS server to be invoked. May be <code>null</code> if the
   *        endpoint from the WSDL should be used.
   */
  public WSClientConfig (@Nullable final URL aEndpointAddress)
  {
    m_aEndpointAddress = aEndpointAddress;

    if (LOGGER.isDebugEnabled () && aEndpointAddress != null)
      LOGGER.debug ("Using endpoint address '" + m_aEndpointAddress.toExternalForm () + "'");
  }

  /**
   * @return The endpoint address as specified in the constructor. May be <code>null</code> if the
   *         endpoint from the WSDL should be used.
   */
  @Nullable
  public final URL getEndpointAddress ()
  {
    return m_aEndpointAddress;
  }

  /**
   * @return The {@link SSLSocketFactory} to be used by this client. Is <code>null</code> by
   *         default.
   */
  @Nullable
  public SSLSocketFactory getSSLSocketFactory ()
  {
    return m_aSSLSocketFactory;
  }

  /**
   * Set the {@link SSLSocketFactory} to be used by this client to one that trusts all servers. It
   * defaults to at least TLS 1.2 and disables certificate checks. This is NOT recommended for
   * secure, production code!
   *
   * @throws KeyManagementException
   *         if initializing the SSL context failed
   * @return this for chaining
   */
  @Nonnull
  public final WSClientConfig setSSLSocketFactoryTrustAll () throws KeyManagementException
  {
    return setSSLSocketFactoryTrustAll (GlobalDebug.isDebugMode ());
  }

  /**
   * Set the {@link SSLSocketFactory} to be used by this client to one that trusts all servers. It
   * defaults to at least TLS 1.2 and disables certificate checks. This is NOT recommended for
   * secure, production code!
   *
   * @param bDebugMode
   *        <code>true</code> for extended debug logging, <code>false</code> for production.
   * @throws KeyManagementException
   *         if initializing the SSL context failed
   * @return this for chaining
   * @since 9.1.5
   */
  @Nonnull
  public final WSClientConfig setSSLSocketFactoryTrustAll (final boolean bDebugMode) throws KeyManagementException
  {
    try
    {
      final SSLContext aSSLContext = SSLContext.getInstance ("TLSv1.2");
      aSSLContext.init (null, new TrustManager [] { new TrustManagerTrustAll (bDebugMode) }, null);
      final SSLSocketFactory aSF = aSSLContext.getSocketFactory ();
      return setSSLSocketFactory (aSF);
    }
    catch (final NoSuchAlgorithmException ex)
    {
      throw new IllegalStateException ("TLS 1.2 is not supported", ex);
    }
  }

  /**
   * Change the {@link SSLSocketFactory} to be used by this client.
   *
   * @param aSSLSocketFactory
   *        The factory to use. Maybe <code>null</code> to indicate, that the default
   *        {@link SSLSocketFactory} is to be used.
   * @return this for chaining
   */
  @Nonnull
  public final WSClientConfig setSSLSocketFactory (@Nullable final SSLSocketFactory aSSLSocketFactory)
  {
    m_aSSLSocketFactory = aSSLSocketFactory;
    return this;
  }

  /**
   * @return The {@link HostnameVerifier} to be used by this client. Is <code>null</code> by
   *         default.
   */
  @Nullable
  public HostnameVerifier getHostnameVerifier ()
  {
    return m_aHostnameVerifier;
  }

  /**
   * Set the {@link HostnameVerifier} to a "trust all" verifier. This is NOT recommended for secure,
   * production code!
   *
   * @return this for chaining
   */
  @Nonnull
  public final WSClientConfig setHostnameVerifierTrustAll ()
  {
    return setHostnameVerifierTrustAll (GlobalDebug.isDebugMode ());
  }

  /**
   * Set the {@link HostnameVerifier} to a "trust all" verifier. This is NOT recommended for secure,
   * production code!
   *
   * @param bDebugMode
   *        <code>true</code> for extended debug logging, <code>false</code> for production.
   * @return this for chaining
   * @since 9.1.5
   */
  @Nonnull
  public final WSClientConfig setHostnameVerifierTrustAll (final boolean bDebugMode)
  {
    return setHostnameVerifier (new HostnameVerifierVerifyAll (bDebugMode));
  }

  /**
   * Change the {@link HostnameVerifier} to be used by this client.
   *
   * @param aHostnameVerifier
   *        The factory to use. Maybe <code>null</code> to indicate, that the default
   *        {@link HostnameVerifier} is to be used.
   * @return this for chaining
   */
  @Nonnull
  public final WSClientConfig setHostnameVerifier (@Nullable final HostnameVerifier aHostnameVerifier)
  {
    m_aHostnameVerifier = aHostnameVerifier;
    return this;
  }

  /**
   * @return The connection timeout in milliseconds. Default is
   *         {@link #DEFAULT_CONNECTION_TIMEOUT_MS}.
   */
  public int getConnectionTimeoutMS ()
  {
    return m_nConnectionTimeoutMS;
  }

  /**
   * @return <code>true</code> if a connection timeout is defined, <code>false</code> if not.
   * @since 9.1.5
   */
  public boolean hasConnectionTimeoutMS ()
  {
    return m_nConnectionTimeoutMS >= 0;
  }

  /**
   * Set the connection timeout in milliseconds.
   *
   * @param nConnectionTimeoutMS
   *        Milliseconds. Only values &ge; 0 are considered.
   * @return this for chaining
   */
  @Nonnull
  public final WSClientConfig setConnectionTimeoutMS (final int nConnectionTimeoutMS)
  {
    m_nConnectionTimeoutMS = nConnectionTimeoutMS;
    return this;
  }

  /**
   * @return The request (response) timeout in milliseconds. Default is
   *         {@link #DEFAULT_REQUEST_TIMEOUT_MS}.
   */
  public int getRequestTimeoutMS ()
  {
    return m_nRequestTimeoutMS;
  }

  /**
   * @return <code>true</code> if a request (response) timeout is defined, <code>false</code> if
   *         not.
   * @since 9.1.5
   */
  public boolean hasRequestTimeoutMS ()
  {
    return m_nRequestTimeoutMS >= 0;
  }

  /**
   * Set the request (response) timeout in milliseconds.
   *
   * @param nRequestTimeoutMS
   *        Milliseconds. Only values &ge; 0 are considered.
   * @return this for chaining
   */
  @Nonnull
  public final WSClientConfig setRequestTimeoutMS (final int nRequestTimeoutMS)
  {
    m_nRequestTimeoutMS = nRequestTimeoutMS;
    return this;
  }

  /**
   * @return The chunk size in bytes. Default is {@link #DEFAULT_CHUNK_SIZE}. Only values &gt; 0 are
   *         considered.
   * @since 8.5.7
   */
  public int getChunkSize ()
  {
    return m_nChunkSize;
  }

  /**
   * @return <code>true</code> if a chunk size is defined, <code>false</code> if not.
   * @since 9.1.5
   */
  public boolean hasChunkSize ()
  {
    return m_nChunkSize >= 0;
  }

  /**
   * Set the chunk size to enable HTTP chunked encoding.
   *
   * @param nChunkSize
   *        Number of bytes. Only values &ge; 0 are considered. If the value is 0 than the JDK
   *        default chunk size (Oracle: 4096 bytes) is used. Values &lt; 0 mean no chunked encoding!
   * @return this for chaining
   * @since 8.5.7
   */
  @Nonnull
  public final WSClientConfig setChunkSize (final int nChunkSize)
  {
    m_nChunkSize = nChunkSize;
    return this;
  }

  /**
   * @return The user name for HTTP Basic Auth. May be <code>null</code>. Default is
   *         <code>null</code>.
   */
  @Nullable
  public String getUserName ()
  {
    return m_sUserName;
  }

  /**
   * @return <code>true</code> if a HTTP Basic Auth user name is defined, <code>false</code>
   *         otherwise.
   */
  public boolean hasUserName ()
  {
    final String sStr = m_sUserName;
    return StringHelper.isNotEmpty (sStr);
  }

  /**
   * Set the HTTP Basic Auth user name to be used.
   *
   * @param sUserName
   *        The user name to use. May be <code>null</code>.
   * @return this for chaining
   */
  @Nonnull
  public final WSClientConfig setUserName (@Nullable final String sUserName)
  {
    m_sUserName = sUserName;
    return this;
  }

  /**
   * @return The password for HTTP Basic Auth. May be <code>null</code>. Default is
   *         <code>null</code>.
   */
  @Nullable
  public String getPassword ()
  {
    return m_sPassword;
  }

  /**
   * @return <code>true</code> if a HTTP Basic Auth password is defined, <code>false</code>
   *         otherwise.
   */
  public boolean hasPassword ()
  {
    final String sStr = m_sPassword;
    return StringHelper.isNotEmpty (sStr);
  }

  /**
   * Set the HTTP Basic Auth password to be used.
   *
   * @param sPassword
   *        The password to use. May be <code>null</code>.
   * @return this for chaining
   */
  @Nonnull
  public final WSClientConfig setPassword (@Nullable final String sPassword)
  {
    m_sPassword = sPassword;
    return this;
  }

  /**
   * @return The SOAP Action to be used. May be <code>null</code>. Default is <code>null</code>.
   */
  @Nullable
  public String getSOAPAction ()
  {
    return m_sSOAPAction;
  }

  /**
   * @return <code>true</code> if a SOAP Action is defined, <code>false</code> otherwise.
   */
  public boolean hasSOAPAction ()
  {
    final String sStr = m_sSOAPAction;
    return StringHelper.isNotEmpty (sStr);
  }

  /**
   * Set the SOAP Action to be used.
   *
   * @param sSOAPAction
   *        The SOAP Action to use. May be <code>null</code>.
   * @return this for chaining
   */
  @Nonnull
  public final WSClientConfig setSOAPAction (@Nullable final String sSOAPAction)
  {
    m_sSOAPAction = sSOAPAction;
    return this;
  }

  /**
   * @return The mutable HTTP header map used inside
   */
  @Nonnull
  @ReturnsMutableObject
  public HttpHeaderMap httpHeaders ()
  {
    return m_aHTTPHeaders;
  }

  /**
   * Forces this client to send compressed HTTP content. Disabled by default.
   *
   * @param bCompress
   *        <code>true</code> to enable, <code>false</code> to disable.
   * @return this for chaining
   */
  @Nonnull
  public final WSClientConfig setCompressedRequest (final boolean bCompress)
  {
    if (bCompress)
      m_aHTTPHeaders.setHeader (CHttpHeader.CONTENT_ENCODING, "gzip");
    else
      m_aHTTPHeaders.removeHeaders (CHttpHeader.CONTENT_ENCODING);
    return this;
  }

  /**
   * Add a hint that this client understands compressed HTTP content. Disabled by default.
   *
   * @param bCompress
   *        <code>true</code> to enable, <code>false</code> to disable.
   * @return this for chaining
   */
  @Nonnull
  public final WSClientConfig setCompressedResponse (final boolean bCompress)
  {
    if (bCompress)
      m_aHTTPHeaders.setHeader (CHttpHeader.ACCEPT_ENCODING, "gzip");
    else
      m_aHTTPHeaders.removeHeaders (CHttpHeader.ACCEPT_ENCODING);
    return this;
  }

  public boolean isCookiesSupportEnabled ()
  {
    return m_eCookiesSupport.isTrue ();
  }

  @Nonnull
  public final WSClientConfig setCookiesSupportEnabled (final boolean bEnabled)
  {
    m_eCookiesSupport = ETriState.valueOf (bEnabled);
    return this;
  }

  @Nonnull
  @ReturnsMutableObject
  public ICommonsList <Handler <? extends MessageContext>> handlers ()
  {
    return m_aHandlers;
  }

  /**
   * Add custom properties to the request context.
   *
   * @param aRequestContext
   *        The request context to be filled. Never <code>null</code>.
   */
  @OverrideOnDemand
  protected void customizeRequestContext (@Nonnull final Map <String, Object> aRequestContext)
  {}

  protected final boolean isWorkAroundMASM0003 ()
  {
    return m_bWorkAroundMASM0003;
  }

  protected final WSClientConfig setWorkAroundMASM0003 (final boolean bWorkAroundMASM0003)
  {
    m_bWorkAroundMASM0003 = bWorkAroundMASM0003;
    return this;
  }

  @OverrideOnDemand
  @OverridingMethodsMustInvokeSuper
  public void applyWSSettingsToBindingProvider (@Nonnull final BindingProvider aBP)
  {
    final Map <String, Object> aRequestContext = aBP.getRequestContext ();

    if (m_aEndpointAddress != null)
    {
      aRequestContext.put (BindingProvider.ENDPOINT_ADDRESS_PROPERTY, m_aEndpointAddress.toExternalForm ());
    }

    // Note: all the constants are defined in class JAXWSProperties, but they
    // are implementation specific and not part of the shared API
    if (m_aSSLSocketFactory != null)
    {
      aRequestContext.put ("com.sun.xml.ws.transport.https.client.SSLSocketFactory", m_aSSLSocketFactory);
    }
    if (m_aHostnameVerifier != null)
    {
      aRequestContext.put ("com.sun.xml.ws.transport.https.client.hostname.verifier", m_aHostnameVerifier);
    }
    if (hasConnectionTimeoutMS ())
    {
      aRequestContext.put ("com.sun.xml.ws.connect.timeout", Integer.valueOf (m_nConnectionTimeoutMS));
    }
    if (hasRequestTimeoutMS ())
    {
      aRequestContext.put ("com.sun.xml.ws.request.timeout", Integer.valueOf (m_nRequestTimeoutMS));
    }
    if (hasChunkSize ())
    {
      aRequestContext.put ("com.sun.xml.ws.transport.http.client.streaming.chunk.size", Integer.valueOf (m_nChunkSize));
    }
    final String sStr = m_sUserName;
    if (StringHelper.isNotEmpty (sStr))
    {
      aRequestContext.put (BindingProvider.USERNAME_PROPERTY, m_sUserName);
      aRequestContext.put (BindingProvider.PASSWORD_PROPERTY, m_sPassword);
    }
    if (hasSOAPAction ())
    {
      aRequestContext.put (BindingProvider.SOAPACTION_USE_PROPERTY, Boolean.TRUE);
      aRequestContext.put (BindingProvider.SOAPACTION_URI_PROPERTY, m_sSOAPAction);
    }
    if (m_aHTTPHeaders.isNotEmpty ())
    {
      // Type must be
      // java.util.Map<java.lang.String,java.util.List<java.lang.String>>
      aRequestContext.put (MessageContext.HTTP_REQUEST_HEADERS, m_aHTTPHeaders.getAsMapStringToListString ());
    }
    if (m_eCookiesSupport.isDefined ())
    {
      aRequestContext.put (BindingProvider.SESSION_MAINTAIN_PROPERTY, m_eCookiesSupport.getAsBooleanObj ());
    }

    if (m_aHandlers.isNotEmpty ())
    {
      @SuppressWarnings ("rawtypes")
      final List <Handler> aHandlers = aBP.getBinding ().getHandlerChain ();
      aHandlers.addAll (m_aHandlers);
      aBP.getBinding ().setHandlerChain (aHandlers);
    }

    customizeRequestContext (aRequestContext);

    if (m_bWorkAroundMASM0003)
    {
      // Introduced with Java 1.8.0_31??
      // MASM0003: Default [ jaxws-tubes-default.xml ] configuration file was
      // not loaded
      final ClassLoader aContextClassLoader = ClassLoaderHelper.getContextClassLoader ();
      final ClassLoader aThisClassLoader = getClass ().getClassLoader ();
      if (aContextClassLoader == null)
      {
        LOGGER.info ("Manually setting thread context class loader to work around MASM0003 bug");
        ClassLoaderHelper.setContextClassLoader (aThisClassLoader);
      }
      else
      {
        if (!aContextClassLoader.equals (aThisClassLoader))
        {
          LOGGER.warn ("Manually overriding thread context class loader to work around MASM0003 bug");
          ClassLoaderHelper.setContextClassLoader (aThisClassLoader);
        }
      }
    }
  }
}
