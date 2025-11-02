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
 * https://wiki.mozilla.org/Security/Server_Side_TLS from 2020-02-20. Still valid on 2025-08-12.
 * <p>
 * See the tool MainMapCipherSuites for the cipher suite name mapping
 *
 * @author Philip Helger
 * @since 9.1.11
 */
public enum ETLSConfigurationMode_2020_02 implements IHasID <String>, ITLSConfigurationMode
{
  /**
   * For services with clients that support TLS 1.3 and don't need backward compatibility, the
   * Modern configuration provides an extremely high level of security.<br>
   * This configuration is compatible with Firefox 63, Android 10.0, Chrome 70, Edge 75, NOT on
   * Internet Explorer, Java 11, OpenSSL 1.1.1, Opera 57, Safari 12.1.
   */
  MODERN ("modern",
          new ETLSVersion [] { ETLSVersion.TLS_13 },
          new String [] { "TLS_AES_128_GCM_SHA256", "TLS_AES_256_GCM_SHA384", "TLS_CHACHA20_POLY1305_SHA256" }),
  /**
   * For services that don't need compatibility with legacy clients, such as Windows XP or old
   * versions of OpenSSL. This is the recommended configuration for the vast majority of services,
   * as it is highly secure and compatible with nearly every client released in the last five (or
   * more) years.<br>
   * This configuration is compatible with Firefox 27, Android 4.4.2, Chrome 31, Edge 12, Internet
   * Explorer 11, Java 8u31, OpenSSL 1.0.1, Opera 20, Safari 9.
   */
  INTERMEDIATE ("intermediate",
                new ETLSVersion [] { ETLSVersion.TLS_13, ETLSVersion.TLS_12 },
                new String [] { // TLS 1.3
                                "TLS_AES_128_GCM_SHA256",
                                "TLS_AES_256_GCM_SHA384",
                                "TLS_CHACHA20_POLY1305_SHA256", // TLS 1.2
                                "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
                                "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
                                "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",
                                "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
                                "TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256",
                                "TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256",
                                "TLS_DHE_RSA_WITH_AES_128_GCM_SHA256",
                                "TLS_DHE_RSA_WITH_AES_256_GCM_SHA384" }),
  /**
   * This configuration is compatible with a number of very old clients, and should be used only as
   * a last resort. <br>
   * This configuration is compatible with Firefox 1, Android 2.3, Chrome 1, Edge 12, Internet
   * Explorer 8, Java 6, OpenSSL 0.9.8, Opera 5, Safari 1.
   */
  OLD ("old",
       new ETLSVersion [] { ETLSVersion.TLS_13, ETLSVersion.TLS_12, ETLSVersion.TLS_11, ETLSVersion.TLS_10 },
       new String [] { // TLS 1.3
                       "TLS_AES_128_GCM_SHA256",
                       "TLS_AES_256_GCM_SHA384",
                       "TLS_CHACHA20_POLY1305_SHA256", // TLS 1.0-1.2
                       "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
                       "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
                       "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",
                       "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
                       "TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256",
                       "TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256",
                       "TLS_DHE_RSA_WITH_AES_128_GCM_SHA256",
                       "TLS_DHE_RSA_WITH_AES_256_GCM_SHA384",
                       "TLS_DHE_RSA_WITH_CHACHA20_POLY1305_SHA256",
                       "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256",
                       "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256",
                       "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA",
                       "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA",
                       "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384",
                       "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384",
                       "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA",
                       "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA",
                       "TLS_DHE_RSA_WITH_AES_128_CBC_SHA256",
                       "TLS_DHE_RSA_WITH_AES_256_CBC_SHA256",
                       "TLS_RSA_WITH_AES_128_GCM_SHA256",
                       "TLS_RSA_WITH_AES_256_GCM_SHA384",
                       "TLS_RSA_WITH_AES_128_CBC_SHA256",
                       "TLS_RSA_WITH_AES_256_CBC_SHA256",
                       "TLS_RSA_WITH_AES_128_CBC_SHA",
                       "TLS_RSA_WITH_AES_256_CBC_SHA",
                       "SSL_CK_DES_192_EDE3_CBC_WITH_SHA" });

  private final String m_sID;
  private final TLSConfigurationMode m_aMode;

  ETLSConfigurationMode_2020_02 (@NonNull @Nonempty final String sID,
                                 @NonNull @Nonempty final ETLSVersion [] aTLSVersions,
                                 @NonNull @Nonempty final String [] aCipherSuites)
  {
    m_sID = sID;
    m_aMode = new TLSConfigurationMode (aTLSVersions, aCipherSuites);
  }

  @NonNull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  @NonNull
  @ReturnsMutableCopy
  public ICommonsList <String> getAllCipherSuites ()
  {
    return m_aMode.getAllCipherSuites ();
  }

  @NonNull
  @ReturnsMutableCopy
  @Override
  public String [] getAllCipherSuitesAsArray ()
  {
    return m_aMode.getAllCipherSuitesAsArray ();
  }

  @NonNull
  @ReturnsMutableCopy
  public ICommonsList <ETLSVersion> getAllTLSVersions ()
  {
    return m_aMode.getAllTLSVersions ();
  }

  @NonNull
  @ReturnsMutableCopy
  @Override
  public ICommonsList <String> getAllTLSVersionIDs ()
  {
    return m_aMode.getAllTLSVersionIDs ();
  }

  @NonNull
  @ReturnsMutableCopy
  @Override
  public String [] getAllTLSVersionIDsAsArray ()
  {
    return m_aMode.getAllTLSVersionIDsAsArray ();
  }

  @Nullable
  public static ETLSConfigurationMode_2020_02 getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (ETLSConfigurationMode_2020_02.class, sID);
  }
}
