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
package com.helger.wsclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.security.KeyManagementException;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import org.jspecify.annotations.NonNull;
import org.junit.Test;

import com.helger.base.url.URLHelper;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.ICommonsMap;

import jakarta.xml.ws.Binding;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.EndpointReference;

/**
 * Additional test class for class {@link WSClientConfig}, covering the setters, the SSL helpers and
 * the property application.
 *
 * @author Philip Helger
 */
public final class WSClientConfigExtTest
{
  private static final String URL_STR = "http://www.helger.com/service";

  /**
   * A minimal {@link BindingProvider} that only carries the request context.
   *
   * @author Philip Helger
   */
  private static final class MockBP implements BindingProvider
  {
    private final Map <String, Object> m_aRC;

    MockBP (@NonNull final Map <String, Object> aRC)
    {
      m_aRC = aRC;
    }

    public Map <String, Object> getRequestContext ()
    {
      return m_aRC;
    }

    public Map <String, Object> getResponseContext ()
    {
      throw new UnsupportedOperationException ();
    }

    public Binding getBinding ()
    {
      throw new UnsupportedOperationException ();
    }

    public EndpointReference getEndpointReference ()
    {
      throw new UnsupportedOperationException ();
    }

    public <T extends EndpointReference> T getEndpointReference (final Class <T> aClass)
    {
      throw new UnsupportedOperationException ();
    }
  }

  @NonNull
  private static WSClientConfig _create ()
  {
    return new WSClientConfig (URLHelper.getAsURL (URL_STR));
  }

  @Test
  public void testDefaults ()
  {
    final WSClientConfig a = _create ();
    assertNotNull (a.getEndpointAddress ());
    assertNull (a.getSSLSocketFactory ());
    assertNull (a.getHostnameVerifier ());

    assertEquals (WSClientConfig.DEFAULT_CONNECTION_TIMEOUT_MS, a.getConnectionTimeoutMS ());
    assertEquals (WSClientConfig.DEFAULT_REQUEST_TIMEOUT_MS, a.getRequestTimeoutMS ());
    assertEquals (WSClientConfig.DEFAULT_CHUNK_SIZE, a.getChunkSize ());

    assertNull (a.getUserName ());
    assertFalse (a.hasUserName ());
    assertNull (a.getPassword ());
    assertFalse (a.hasPassword ());
    assertNull (a.getSOAPAction ());
    assertFalse (a.hasSOAPAction ());

    assertNotNull (a.httpHeaders ());
    assertNotNull (a.handlers ());
    assertNotNull (a.toString ());

    // A null endpoint address is allowed
    assertNull (new WSClientConfig (null).getEndpointAddress ());
  }

  @Test
  public void testTimeoutsAndChunkSize ()
  {
    final WSClientConfig a = _create ();

    assertTrue (a.hasConnectionTimeoutMS ());
    assertSame (a, a.setConnectionTimeoutMS (-1));
    assertFalse (a.hasConnectionTimeoutMS ());
    a.setConnectionTimeoutMS (1000);
    assertEquals (1000, a.getConnectionTimeoutMS ());

    assertTrue (a.hasRequestTimeoutMS ());
    a.setRequestTimeoutMS (-1);
    assertFalse (a.hasRequestTimeoutMS ());
    a.setRequestTimeoutMS (2000);
    assertEquals (2000, a.getRequestTimeoutMS ());

    assertFalse (a.hasChunkSize ());
    a.setChunkSize (4096);
    assertTrue (a.hasChunkSize ());
    assertEquals (4096, a.getChunkSize ());
  }

  @Test
  public void testCredentialsAndSoapAction ()
  {
    final WSClientConfig a = _create ();

    assertSame (a, a.setUserName ("user"));
    assertTrue (a.hasUserName ());
    assertEquals ("user", a.getUserName ());
    a.setUserName (null);
    assertFalse (a.hasUserName ());

    a.setPassword ("pw");
    assertTrue (a.hasPassword ());
    assertEquals ("pw", a.getPassword ());
    a.setPassword (null);
    assertFalse (a.hasPassword ());

    a.setSOAPAction ("urn:action");
    assertTrue (a.hasSOAPAction ());
    assertEquals ("urn:action", a.getSOAPAction ());
    a.setSOAPAction (null);
    assertFalse (a.hasSOAPAction ());
  }

  @Test
  public void testSSLAndHostnameVerifier () throws KeyManagementException
  {
    final WSClientConfig a = _create ();

    assertSame (a, a.setSSLSocketFactoryTrustAll ());
    assertNotNull (a.getSSLSocketFactory ());
    a.setSSLSocketFactoryTrustAll (true);
    assertNotNull (a.getSSLSocketFactory ());
    a.setSSLSocketFactory ((SSLSocketFactory) null);
    assertNull (a.getSSLSocketFactory ());

    assertSame (a, a.setHostnameVerifierTrustAll ());
    assertNotNull (a.getHostnameVerifier ());
    a.setHostnameVerifierTrustAll (true);
    assertNotNull (a.getHostnameVerifier ());
    a.setHostnameVerifier ((HostnameVerifier) null);
    assertNull (a.getHostnameVerifier ());
  }

  @Test
  public void testCompressionAndCookies ()
  {
    final WSClientConfig a = _create ();

    assertSame (a, a.setCompressedRequest (true));
    assertTrue (a.httpHeaders ().containsHeaders ("Content-Encoding"));
    a.setCompressedRequest (false);
    assertFalse (a.httpHeaders ().containsHeaders ("Content-Encoding"));

    a.setCompressedResponse (true);
    assertTrue (a.httpHeaders ().containsHeaders ("Accept-Encoding"));
    a.setCompressedResponse (false);
    assertFalse (a.httpHeaders ().containsHeaders ("Accept-Encoding"));

    assertFalse (a.isCookiesSupportEnabled ());
    assertSame (a, a.setCookiesSupportEnabled (true));
    assertTrue (a.isCookiesSupportEnabled ());
  }

  @Test
  public void testApplyWSSettingsMinimal ()
  {
    final ICommonsMap <String, Object> aRC = new CommonsHashMap <> ();
    _create ().applyWSSettingsToBindingProvider (new MockBP (aRC));

    assertEquals (URL_STR, aRC.get (BindingProvider.ENDPOINT_ADDRESS_PROPERTY));
    assertEquals (Integer.valueOf (WSClientConfig.DEFAULT_CONNECTION_TIMEOUT_MS),
                  aRC.get ("com.sun.xml.ws.connect.timeout"));
    assertEquals (Integer.valueOf (WSClientConfig.DEFAULT_REQUEST_TIMEOUT_MS),
                  aRC.get ("com.sun.xml.ws.request.timeout"));
  }

  @Test
  public void testApplyWSSettingsFull () throws KeyManagementException
  {
    final WSClientConfig a = _create ();
    a.setSSLSocketFactoryTrustAll ();
    a.setHostnameVerifierTrustAll ();
    a.setChunkSize (4096);
    a.setUserName ("user");
    a.setPassword ("pw");
    a.setSOAPAction ("urn:action");
    a.setCookiesSupportEnabled (true);
    a.setCompressedRequest (true);
    a.setCompressedResponse (true);

    final ICommonsMap <String, Object> aRC = new CommonsHashMap <> ();
    a.applyWSSettingsToBindingProvider (new MockBP (aRC));

    assertNotNull (aRC.get ("com.sun.xml.ws.transport.https.client.SSLSocketFactory"));
    assertNotNull (aRC.get ("com.sun.xml.ws.transport.https.client.hostname.verifier"));
    assertEquals (Integer.valueOf (4096), aRC.get ("com.sun.xml.ws.transport.http.client.streaming.chunk.size"));
    assertEquals ("user", aRC.get (BindingProvider.USERNAME_PROPERTY));
    assertEquals ("pw", aRC.get (BindingProvider.PASSWORD_PROPERTY));
  }

  @Test
  public void testApplyWSSettingsWithoutEndpoint ()
  {
    final ICommonsMap <String, Object> aRC = new CommonsHashMap <> ();
    new WSClientConfig (null).applyWSSettingsToBindingProvider (new MockBP (aRC));
    assertNull (aRC.get (BindingProvider.ENDPOINT_ADDRESS_PROPERTY));
  }
}
