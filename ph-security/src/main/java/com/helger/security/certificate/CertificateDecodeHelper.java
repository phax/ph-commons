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
package com.helger.security.certificate;

import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.io.stream.StringInputStream;

/**
 * Simple Certificate decoding helper that differentiates between String and byte source and can
 * handle PEM and non-PEM inputs.
 *
 * @author Philip Helger
 * @since 12.1.1
 */
public class CertificateDecodeHelper
{
  public static final boolean DEFAULT_PEM_ENCODED = false;

  private byte [] m_aSrcBytes;
  private String m_sSrcString;
  private boolean m_bPEMEncoded = DEFAULT_PEM_ENCODED;

  public CertificateDecodeHelper ()
  {}

  /**
   * Set the certificate source as a byte array. This resets any previously set String source.
   *
   * @param aBytes
   *        The certificate bytes. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public CertificateDecodeHelper source (final byte @Nullable [] aBytes)
  {
    m_aSrcBytes = aBytes;
    m_sSrcString = null;
    return this;
  }

  /**
   * Set the certificate source as a String. This resets any previously set byte array source.
   *
   * @param s
   *        The certificate string. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public CertificateDecodeHelper source (@Nullable final String s)
  {
    m_aSrcBytes = null;
    m_sSrcString = s;
    return this;
  }

  /**
   * Set whether the certificate source is PEM encoded or not.
   *
   * @param b
   *        <code>true</code> if the source is PEM (Base64) encoded, <code>false</code> if it is
   *        in binary (DER) format.
   * @return this for chaining
   */
  @NonNull
  public CertificateDecodeHelper pemEncoded (final boolean b)
  {
    m_bPEMEncoded = b;
    return this;
  }

  /**
   * Decode the configured certificate source into an {@link X509Certificate}.
   *
   * @return The decoded certificate, or <code>null</code> if no source was set or the source is
   *         empty.
   * @throws CertificateException
   *         If the source data cannot be converted to a valid X.509 certificate.
   */
  @Nullable
  public X509Certificate getDecodedOrThrow () throws CertificateException
  {
    if (m_aSrcBytes == null && m_sSrcString == null)
      return null;

    if (m_bPEMEncoded)
    {
      // Certificate is always ISO-8859-1 encoded
      final String sEncodedString = (m_sSrcString != null ? m_sSrcString : new String (m_aSrcBytes,
                                                                                       CertificateHelper.CERT_CHARSET)).trim ();
      if (!sEncodedString.isEmpty ())
      {
        // Source is not empty
        final CertificateFactory aCertificateFactory = CertificateHelper.getX509CertificateFactory ();

        // Make sure PEM header is present
        final String sRealCertString = CertificateHelper.getRFC1421CompliantString (sEncodedString, true);
        try (final StringInputStream aIS = new StringInputStream (sRealCertString, CertificateHelper.CERT_CHARSET))
        {
          return (X509Certificate) aCertificateFactory.generateCertificate (aIS);
        }
      }
    }
    else
    {
      final byte [] aEncodedBytes = m_aSrcBytes != null ? m_aSrcBytes : m_sSrcString.trim ()
                                                                                    .getBytes (CertificateHelper.CERT_CHARSET);
      if (aEncodedBytes.length > 0)
      {
        final CertificateFactory aCertificateFactory = CertificateHelper.getX509CertificateFactory ();
        try (final NonBlockingByteArrayInputStream aBAIS = new NonBlockingByteArrayInputStream (aEncodedBytes))
        {
          return (X509Certificate) aCertificateFactory.generateCertificate (aBAIS);
        }
      }
    }

    return null;
  }

  /**
   * Decode the configured certificate source into an {@link X509Certificate}, swallowing all
   * errors.
   *
   * @return The decoded certificate, or <code>null</code> if decoding fails for any reason.
   */
  @Nullable
  public X509Certificate getDecodedOrNull ()
  {
    try
    {
      return getDecodedOrThrow ();
    }
    catch (final IllegalArgumentException | CertificateException ex)
    {
      return null;
    }
  }
}
