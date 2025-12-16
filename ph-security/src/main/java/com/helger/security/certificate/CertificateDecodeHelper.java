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

  @NonNull
  public CertificateDecodeHelper source (final byte @Nullable [] aBytes)
  {
    m_aSrcBytes = aBytes;
    m_sSrcString = null;
    return this;
  }

  @NonNull
  public CertificateDecodeHelper source (@Nullable final String s)
  {
    m_aSrcBytes = null;
    m_sSrcString = s;
    return this;
  }

  @NonNull
  public CertificateDecodeHelper pemEncoded (final boolean b)
  {
    m_bPEMEncoded = b;
    return this;
  }

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
