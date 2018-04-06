/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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

import java.util.Map;

import javax.annotation.Nonnull;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.EndpointReference;

import org.junit.Test;

import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.url.URLHelper;
import com.helger.wsclient.WSClientConfig;

/**
 * Test class for class {@link WSClientConfig}.
 * 
 * @author Philip Helger
 */
public final class WSClientConfigTest
{
  private static final class MockBP implements BindingProvider
  {
    private final Map <String, Object> m_aRC;

    public MockBP (@Nonnull final Map <String, Object> aRC)
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

    public <T extends EndpointReference> T getEndpointReference (final Class <T> aClazz)
    {
      throw new UnsupportedOperationException ();
    }
  }

  @Test
  public void testBasic ()
  {
    final WSClientConfig aCfg = new WSClientConfig (URLHelper.getAsURL ("http://www.example.org"));
    final ICommonsMap <String, Object> aMap = new CommonsHashMap<> ();
    final BindingProvider aBP = new MockBP (aMap);
    aCfg.applyWSSettingsToBindingProvider (aBP);
    assertEquals (5, aMap.size ());
    assertEquals ("http://www.example.org", aMap.get (BindingProvider.ENDPOINT_ADDRESS_PROPERTY));
    // 2 versions
    assertEquals (Integer.valueOf (WSClientConfig.DEFAULT_CONNECTION_TIMEOUT_MS),
                  aMap.get ("com.sun.xml.ws.connect.timeout"));
    assertEquals (Integer.valueOf (WSClientConfig.DEFAULT_CONNECTION_TIMEOUT_MS),
                  aMap.get ("com.sun.xml.internal.ws.connect.timeout"));
    // 2 versions
    assertEquals (Integer.valueOf (WSClientConfig.DEFAULT_REQUEST_TIMEOUT_MS),
                  aMap.get ("com.sun.xml.ws.request.timeout"));
    assertEquals (Integer.valueOf (WSClientConfig.DEFAULT_REQUEST_TIMEOUT_MS),
                  aMap.get ("com.sun.xml.internal.ws.request.timeout"));
  }
}
