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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.concurrent.NotThreadSafe;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.multimap.IMultiMapListBased;
import com.helger.commons.collection.multimap.MultiLinkedHashMapArrayListBased;
import com.helger.commons.lang.ClassLoaderHelper;
import com.helger.commons.state.EChange;
import com.helger.commons.state.ETriState;
import com.helger.commons.string.StringHelper;

/**
 * Abstract base class for a webservice client caller.
 *
 * @author Philip Helger
 */
@NotThreadSafe
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
  private String m_sUserName;
  private String m_sPassword;
  private String m_sSOAPAction;
  private IMultiMapListBased <String, String> m_aHTTPHeaders;
  private ETriState m_eCookiesSupport = ETriState.UNDEFINED;

  private boolean m_bWorkAroundMASM0003 = true;

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
   * @return The connection timeout in milliseconds. Default is
   *         {@link #DEFAULT_CONNECTION_TIMEOUT_MS}.
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
   * @return The request timeout in milliseconds. Default is
   *         {@link #DEFAULT_REQUEST_TIMEOUT_MS}.
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
   * @return The user name for HTTP Basic Auth. May be <code>null</code>.
   *         Default is <code>null</code>.
   */
  @Nullable
  public String getUserName ()
  {
    return m_sUserName;
  }

  /**
   * @return <code>true</code> if a HTTP Basic Auth user name is defined,
   *         <code>false</code> otherwise.
   */
  public boolean hasUserName ()
  {
    return StringHelper.hasText (m_sUserName);
  }

  /**
   * Set the HTTP Basic Auth user name to be used.
   *
   * @param sUserName
   *        The user name to use. May be <code>null</code>.
   */
  public void setUserName (@Nullable final String sUserName)
  {
    m_sUserName = sUserName;
  }

  /**
   * @return The password for HTTP Basic Auth. May be <code>null</code>. Default
   *         is <code>null</code>.
   */
  @Nullable
  public String getPassword ()
  {
    return m_sPassword;
  }

  /**
   * @return <code>true</code> if a HTTP Basic Auth password is defined,
   *         <code>false</code> otherwise.
   */
  public boolean hasPassword ()
  {
    return StringHelper.hasText (m_sPassword);
  }

  /**
   * Set the HTTP Basic Auth password to be used.
   *
   * @param sPassword
   *        The password to use. May be <code>null</code>.
   */
  public void setPassword (@Nullable final String sPassword)
  {
    m_sPassword = sPassword;
  }

  /**
   * @return The SOAP Action to be used. May be <code>null</code>. Default is
   *         <code>null</code>.
   */
  @Nullable
  public String getSOAPAction ()
  {
    return m_sSOAPAction;
  }

  /**
   * @return <code>true</code> if a SOAP Action is defined, <code>false</code>
   *         otherwise.
   */
  public boolean hasSOAPAction ()
  {
    return StringHelper.hasText (m_sSOAPAction);
  }

  /**
   * Set the SOAP Action to be used.
   *
   * @param sSOAPAction
   *        The SOAP Action to use. May be <code>null</code>.
   */
  public void setSOAPAction (@Nullable final String sSOAPAction)
  {
    m_sSOAPAction = sSOAPAction;
  }

  @Nonnull
  private IMultiMapListBased <String, String> _getHeaderMap ()
  {
    if (m_aHTTPHeaders == null)
      m_aHTTPHeaders = new MultiLinkedHashMapArrayListBased <> ();
    return m_aHTTPHeaders;
  }

  /**
   * Add an HTTP header to be send. If another HTTP header with the same name is
   * already present the passed value is appended!
   *
   * @param sName
   *        Header name. May neither be <code>null</code> nor empty.
   * @param sValue
   *        Header value. May not be <code>null</code>.
   * @see #addHTTPHeader(String, Collection)
   * @see #setHTTPHeader(String, String)
   * @see #setHTTPHeader(String, Collection)
   */
  public void addHTTPHeader (@Nonnull @Nonempty final String sName, @Nonnull final String sValue)
  {
    ValueEnforcer.notEmpty (sName, "Name");
    ValueEnforcer.notNull (sValue, "Value");

    _getHeaderMap ().putSingle (sName, sValue);
  }

  /**
   * Add an HTTP header to be send. This method adds values to an existing
   * header with the same name.
   *
   * @param sName
   *        Header name. May neither be <code>null</code> nor empty.
   * @param aValues
   *        Header values to be added. May not be <code>null</code> but may not
   *        contain <code>null</code> values. If this collection is empty
   *        nothing happens.
   * @see #addHTTPHeader(String, String)
   * @see #setHTTPHeader(String, String)
   * @see #setHTTPHeader(String, Collection)
   */
  public void addHTTPHeader (@Nonnull @Nonempty final String sName, @Nonnull final Collection <String> aValues)
  {
    ValueEnforcer.notEmpty (sName, "Name");
    ValueEnforcer.notNullNoNullValue (aValues, "Values");

    _getHeaderMap ().getOrCreate (sName).addAll (aValues);
  }

  /**
   * Add an HTTP header to be send. If another HTTP header with the same name is
   * already present it will be overwritten!
   *
   * @param sName
   *        Header name. May neither be <code>null</code> nor empty.
   * @param sValue
   *        Header value. May not be <code>null</code>.
   * @see #addHTTPHeader(String, String)
   * @see #addHTTPHeader(String, Collection)
   * @see #setHTTPHeader(String, Collection)
   */
  public void setHTTPHeader (@Nonnull @Nonempty final String sName, @Nonnull final String sValue)
  {
    ValueEnforcer.notEmpty (sName, "Name");
    ValueEnforcer.notNull (sValue, "Value");

    _getHeaderMap ().put (sName, CollectionHelper.newList (sValue));
  }

  /**
   * Add an HTTP header to be send. This method overwrites an existing header
   * with the same name.
   *
   * @param sName
   *        Header name. May neither be <code>null</code> nor empty.
   * @param aValues
   *        Header values to be added. May not be <code>null</code> but may not
   *        contain <code>null</code> values. If this collection is empty the
   *        header will be removed.
   * @see #addHTTPHeader(String, String)
   * @see #addHTTPHeader(String, Collection)
   * @see #setHTTPHeader(String, String)
   */
  public void setHTTPHeader (@Nonnull @Nonempty final String sName, @Nonnull final Collection <String> aValues)
  {
    ValueEnforcer.notEmpty (sName, "Name");
    ValueEnforcer.notNullNoNullValue (aValues, "Values");

    if (aValues.isEmpty ())
      removeHTTPHeader (sName);
    else
      _getHeaderMap ().put (sName, CollectionHelper.newList (aValues));
  }

  /**
   * Remove all HTTP header values with the passed name.
   *
   * @param sName
   *        Header name. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if remove was successful.
   */
  @Nonnull
  public EChange removeHTTPHeader (@Nonnull final String sName)
  {
    if (m_aHTTPHeaders == null || StringHelper.hasNoText (sName))
      return EChange.UNCHANGED;

    return EChange.valueOf (m_aHTTPHeaders.remove (sName) != null);
  }

  /**
   * Remove the HTTP header with the passed name and value.
   *
   * @param sName
   *        Header name. May be <code>null</code>.
   * @param sValue
   *        Header value. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if remove was successful.
   */
  @Nonnull
  public EChange removeHTTPHeader (@Nullable final String sName, @Nullable final String sValue)
  {
    if (m_aHTTPHeaders == null || StringHelper.hasNoText (sName) || sValue == null)
      return EChange.UNCHANGED;

    final List <String> aValues = m_aHTTPHeaders.get (sName);
    if (aValues != null)
      if (aValues.remove (sValue))
      {
        // If no more value for the passed name is present, remove the whole
        // list
        if (aValues.isEmpty ())
          m_aHTTPHeaders.remove (sName);

        return EChange.CHANGED;
      }
    return EChange.UNCHANGED;
  }

  /**
   * @param sName
   *        HTTP header name
   * @return <code>true</code> if the HTTP is contained and contains at least
   *         one value.
   */
  public boolean containsHTTPHeader (@Nullable final String sName)
  {
    if (m_aHTTPHeaders == null || StringHelper.hasNoText (sName))
      return false;
    return CollectionHelper.isNotEmpty (m_aHTTPHeaders.get (sName));
  }

  /**
   * @param sName
   *        HTTP header name. May be <code>null</code>.
   * @return The list of all HTTP header values registered for the past name.
   *         Never <code>null</code> but maybe empty.
   */
  @Nonnull
  @ReturnsMutableCopy
  public List <String> getAllHTTPHeaderValues (@Nullable final String sName)
  {
    if (m_aHTTPHeaders == null || StringHelper.hasNoText (sName))
      return CollectionHelper.newList (0);
    return CollectionHelper.newList (m_aHTTPHeaders.get (sName));
  }

  @Nonnull
  @ReturnsMutableCopy
  public Map <String, List <String>> getAllHTTPHeaders ()
  {
    return CollectionHelper.newOrderedMap (m_aHTTPHeaders);
  }

  /**
   * Forces this client to send compressed HTTP content. Disabled by default.
   *
   * @param bCompress
   *        <code>true</code> to enable, <code>false</code> to disable.
   */
  public void setCompressedRequest (final boolean bCompress)
  {
    if (bCompress)
      setHTTPHeader ("Content-Encoding", "gzip");
    else
      removeHTTPHeader ("Content-Encoding");
  }

  /**
   * Add a hint that this client understands compressed HTTP content. Disabled
   * by default.
   *
   * @param bCompress
   *        <code>true</code> to enable, <code>false</code> to disable.
   */
  public void setCompressedResponse (final boolean bCompress)
  {
    if (bCompress)
      setHTTPHeader ("Accept-Encoding", "gzip");
    else
      removeHTTPHeader ("Accept-Encoding");
  }

  public boolean isCookiesSupportEnabled ()
  {
    return m_eCookiesSupport.isTrue ();
  }

  public void setCookiesSupportEnabled (final boolean bEnabled)
  {
    m_eCookiesSupport = ETriState.valueOf (bEnabled);
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

  protected final boolean isWorkAroundMASM0003 ()
  {
    return m_bWorkAroundMASM0003;
  }

  protected final void setWorkAroundMASM0003 (final boolean bWorkAroundMASM0003)
  {
    m_bWorkAroundMASM0003 = bWorkAroundMASM0003;
  }

  @OverrideOnDemand
  @OverridingMethodsMustInvokeSuper
  protected void applyWSSettingsToBindingProvider (@Nonnull final BindingProvider aBP)
  {
    final Map <String, Object> aRequestContext = aBP.getRequestContext ();

    aRequestContext.put (BindingProvider.ENDPOINT_ADDRESS_PROPERTY, m_aEndpointAddress.toExternalForm ());
    if (m_aSSLSocketFactory != null)
    {
      aRequestContext.put ("com.sun.xml.ws.transport.https.client.SSLSocketFactory", m_aSSLSocketFactory);
      aRequestContext.put ("com.sun.xml.internal.ws.transport.https.client.SSLSocketFactory", m_aSSLSocketFactory);
    }
    if (m_aHostnameVerifier != null)
    {
      aRequestContext.put ("com.sun.xml.ws.transport.https.client.hostname.verifier", m_aHostnameVerifier);
      aRequestContext.put ("com.sun.xml.internal.ws.transport.https.client.hostname.verifier", m_aHostnameVerifier);
    }
    if (m_nConnectionTimeoutMS >= 0)
    {
      aRequestContext.put ("com.sun.xml.ws.connect.timeout", Integer.valueOf (m_nConnectionTimeoutMS));
      aRequestContext.put ("com.sun.xml.internal.ws.connect.timeout", Integer.valueOf (m_nConnectionTimeoutMS));
    }
    if (m_nRequestTimeoutMS >= 0)
    {
      aRequestContext.put ("com.sun.xml.ws.request.timeout", Integer.valueOf (m_nRequestTimeoutMS));
      aRequestContext.put ("com.sun.xml.internal.ws.request.timeout", Integer.valueOf (m_nRequestTimeoutMS));
    }
    if (StringHelper.hasText (m_sUserName))
    {
      aRequestContext.put (BindingProvider.USERNAME_PROPERTY, m_sUserName);
      aRequestContext.put (BindingProvider.PASSWORD_PROPERTY, m_sPassword);
    }
    if (hasSOAPAction ())
    {
      aRequestContext.put (BindingProvider.SOAPACTION_USE_PROPERTY, Boolean.TRUE);
      aRequestContext.put (BindingProvider.SOAPACTION_URI_PROPERTY, m_sSOAPAction);
    }
    if (CollectionHelper.isNotEmpty (m_aHTTPHeaders))
    {
      aRequestContext.put (MessageContext.HTTP_REQUEST_HEADERS, m_aHTTPHeaders);
    }
    if (m_eCookiesSupport.isDefined ())
    {
      aRequestContext.put (BindingProvider.SESSION_MAINTAIN_PROPERTY, m_eCookiesSupport.getAsBooleanObj ());
    }

    customizeRequestContext (aRequestContext);

    @SuppressWarnings ("rawtypes")
    final List <Handler> aHandlers = aBP.getBinding ().getHandlerChain ();
    // Fill handlers
    addHandlers (aHandlers);
    aBP.getBinding ().setHandlerChain (aHandlers);

    if (m_bWorkAroundMASM0003)
    {
      // Introduced with Java 1.8.0_31??
      // MASM0003: Default [ jaxws-tubes-default.xml ] configuration file was
      // not loaded
      final ClassLoader aContextClassLoader = ClassLoaderHelper.getContextClassLoader ();
      final ClassLoader aThisClassLoader = getClass ().getClassLoader ();
      if (aContextClassLoader == null)
      {
        s_aLogger.info ("Manually setting thread context class loader to work around MASM0003 bug");
        ClassLoaderHelper.setContextClassLoader (aThisClassLoader);
      }
      else
      {
        if (aContextClassLoader != aThisClassLoader)
        {
          s_aLogger.warn ("Manually overriding thread context class loader to work around MASM0003 bug");
          ClassLoaderHelper.setContextClassLoader (aThisClassLoader);
        }
      }
    }
  }
}
