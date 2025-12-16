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
package com.helger.http.tls;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.id.IHasID;
import com.helger.base.lang.EnumHelper;
import com.helger.collection.commons.ICommonsList;

/**
 * TLS cipher suite configuration modes according to
 * https://wiki.mozilla.org/Security/Server_Side_TLS from 2018-10-09
 * <p>
 * See the tool MainMapCipherSuites for the cipher suite name mapping
 *
 * @author Philip Helger
 * @since 9.0.5
 * @deprecated Use {@link ETLSConfigurationMode_2020_02} instead
 */
@Deprecated (forRemoval = true, since = "10.5.0")
public enum ETLSConfigurationMode_2018_10 implements IHasID <String>, ITLSConfigurationMode
{
  /**
   * For services that don't need backward compatibility, the parameters below provide a higher
   * level of security. This configuration is compatible with Firefox 27, Chrome 30, IE 11 on
   * Windows 7, Edge, Opera 17, Safari 9, Android 5.0, and Java 8.
   */
  @Deprecated
  MODERN ("modern",
          new ETLSVersion [] { ETLSVersion.TLS_12 },
          new String [] { "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",
                          "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
                          "TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305",
                          "TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305",
                          "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
                          "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
                          "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384",
                          "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384",
                          "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256",
                          "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256" }),
  /**
   * For services that don't need compatibility with legacy clients (mostly WinXP), but still need
   * to support a wide range of clients, this configuration is recommended. It is is compatible with
   * Firefox 1, Chrome 1, IE 7, Opera 5 and Safari 1.
   */
  @Deprecated
  INTERMEDIATE ("intermediate",
                new ETLSVersion [] { ETLSVersion.TLS_12, ETLSVersion.TLS_11, ETLSVersion.TLS_10 },
                new String [] { "TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305",
                                "TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305",
                                "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
                                "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
                                "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",
                                "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
                                "TLS_DHE_RSA_WITH_AES_128_GCM_SHA256",
                                "TLS_DHE_RSA_WITH_AES_256_GCM_SHA384",
                                "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256",
                                "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256",
                                "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA",
                                "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384",
                                "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA",
                                "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384",
                                "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA",
                                "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA",
                                "TLS_DHE_RSA_WITH_AES_128_CBC_SHA256",
                                "TLS_DHE_RSA_WITH_AES_128_CBC_SHA",
                                "TLS_DHE_RSA_WITH_AES_256_CBC_SHA256",
                                "TLS_DHE_RSA_WITH_AES_256_CBC_SHA",
                                "TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA",
                                "TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA",
                                "TLS_RSA_WITH_AES_128_GCM_SHA256",
                                "TLS_RSA_WITH_AES_256_GCM_SHA384",
                                "TLS_RSA_WITH_AES_128_CBC_SHA256",
                                "TLS_RSA_WITH_AES_256_CBC_SHA256",
                                "TLS_RSA_WITH_AES_128_CBC_SHA",
                                "TLS_RSA_WITH_AES_256_CBC_SHA",
                                "TLS_RSA_WITH_3DES_EDE_CBC_SHA" }),
  /**
   * This is the old ciphersuite that works with all clients back to Windows XP/IE6. It should be
   * used as a last resort only.
   */
  @Deprecated
  OLD ("old",
       new ETLSVersion [] { ETLSVersion.TLS_12, ETLSVersion.TLS_11, ETLSVersion.TLS_10, ETLSVersion.SSL_V3 },
       new String [] { "TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305",
                       "TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305",
                       "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
                       "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
                       "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
                       "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",
                       "TLS_DHE_RSA_WITH_AES_128_GCM_SHA256",
                       "TLS_DHE_DSS_WITH_AES_128_GCM_SHA256",
                       "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256",
                       "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256",
                       "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA",
                       "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA",
                       "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384",
                       "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384",
                       "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA",
                       "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA",
                       "TLS_DHE_RSA_WITH_AES_128_CBC_SHA256",
                       "TLS_DHE_RSA_WITH_AES_128_CBC_SHA",
                       "TLS_DHE_DSS_WITH_AES_128_CBC_SHA256",
                       "TLS_DHE_RSA_WITH_AES_256_CBC_SHA256",
                       "TLS_DHE_DSS_WITH_AES_256_CBC_SHA",
                       "TLS_DHE_RSA_WITH_AES_256_CBC_SHA",
                       "TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA",
                       "TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA",
                       "TLS_EDH_RSA_WITH_3DES_EDE_CBC_SHA" });

  private final String m_sID;
  private final TLSConfigurationMode m_aMode;

  ETLSConfigurationMode_2018_10 (@NonNull @Nonempty final String sID,
                                 @NonNull @Nonempty final ETLSVersion [] aTLSVersions,
                                 @NonNull @Nonempty final String [] aCipherSuites)
  {
    m_sID = sID;
    m_aMode = new TLSConfigurationMode (aTLSVersions, aCipherSuites);
  }

  @Deprecated
  @NonNull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  @Deprecated
  @NonNull
  @ReturnsMutableCopy
  public ICommonsList <String> getAllCipherSuites ()
  {
    return m_aMode.getAllCipherSuites ();
  }

  @Deprecated
  @NonNull
  @ReturnsMutableCopy
  @Override
  public String [] getAllCipherSuitesAsArray ()
  {
    return m_aMode.getAllCipherSuitesAsArray ();
  }

  @Deprecated
  @NonNull
  @ReturnsMutableCopy
  public ICommonsList <ETLSVersion> getAllTLSVersions ()
  {
    return m_aMode.getAllTLSVersions ();
  }

  @Deprecated
  @NonNull
  @ReturnsMutableCopy
  @Override
  public ICommonsList <String> getAllTLSVersionIDs ()
  {
    return m_aMode.getAllTLSVersionIDs ();
  }

  @Deprecated
  @NonNull
  @ReturnsMutableCopy
  @Override
  public String [] getAllTLSVersionIDsAsArray ()
  {
    return m_aMode.getAllTLSVersionIDsAsArray ();
  }

  @Deprecated
  @Nullable
  public static ETLSConfigurationMode_2018_10 getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (ETLSConfigurationMode_2018_10.class, sID);
  }
}
