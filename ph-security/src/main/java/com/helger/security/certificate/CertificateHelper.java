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
package com.helger.security.certificate;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.base64.Base64;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.io.stream.StringInputStream;
import com.helger.commons.string.StringHelper;

/**
 * Some utility methods handling X.509 certificates.
 *
 * @author Philip Helger
 */
@Immutable
public final class CertificateHelper
{
  public static final String BEGIN_CERTIFICATE = "-----BEGIN CERTIFICATE-----";
  public static final String END_CERTIFICATE = "-----END CERTIFICATE-----";
  public static final String BEGIN_CERTIFICATE_INVALID = "-----BEGINCERTIFICATE-----";
  public static final String END_CERTIFICATE_INVALID = "-----ENDCERTIFICATE-----";
  public static final String CRLF = "\r\n";

  /** Character set used for String-Certificate conversion */
  public static final Charset CERT_CHARSET = StandardCharsets.ISO_8859_1;

  private static final Logger s_aLogger = LoggerFactory.getLogger (CertificateHelper.class);

  @PresentForCodeCoverage
  private static final CertificateHelper s_aInstance = new CertificateHelper ();

  private CertificateHelper ()
  {}

  @Nonnull
  public static CertificateFactory getX509CertificateFactory () throws CertificateException
  {
    return CertificateFactory.getInstance ("X.509");
  }

  @Nonnull
  public static String getWithPEMHeader (@Nonnull final String sCertString)
  {
    String sRealCertString = sCertString;
    // Check without newline in case there are blanks between the string the
    // certificate
    if (!sRealCertString.startsWith (BEGIN_CERTIFICATE))
      sRealCertString = BEGIN_CERTIFICATE + "\n" + sRealCertString;
    if (!sRealCertString.trim ().endsWith (END_CERTIFICATE))
      sRealCertString += "\n" + END_CERTIFICATE;
    return sRealCertString;
  }

  /**
   * Remove any eventually preceding {@value #BEGIN_CERTIFICATE} and succeeding
   * {@value #END_CERTIFICATE} values from the passed certificate string.
   * Additionally all whitespaces of the string are removed.
   *
   * @param sCertificate
   *        The source certificate string. May be <code>null</code>.
   * @return <code>null</code> if the input string is <code>null</code> or
   *         empty, the stripped down string otherwise.
   */
  @Nullable
  public static String getWithoutPEMHeader (@Nullable final String sCertificate)
  {
    if (StringHelper.hasNoText (sCertificate))
      return null;

    // Remove special begin and end stuff
    String sRealCertificate = sCertificate.trim ();

    /**
     * Handle certain misconfiguration issues. E.g. for 9906:testconsip on
     *
     * <pre>
     * http://b-c073e04afb234f70e74d3444ba3f8eaa.iso6523-actorid-upis.acc.edelivery.tech.ec.europa.eu/iso6523-actorid-upis%3A%3A9906%3Atestconsip/services/busdox-docid-qns%3A%3Aurn%3Aoasis%3Anames%3Aspecification%3Aubl%3Aschema%3Axsd%3AOrder-2%3A%3AOrder%23%23urn%3Awww.cenbii.eu%3Atransaction%3Abiitrns001%3Aver2.0%3Aextended%3Aurn%3Awww.peppol.eu%3Abis%3Apeppol3a%3Aver2.0%3A%3A2.1
     * </pre>
     */
    sRealCertificate = StringHelper.trimStart (sRealCertificate, BEGIN_CERTIFICATE_INVALID);
    sRealCertificate = StringHelper.trimEnd (sRealCertificate, END_CERTIFICATE_INVALID);

    // Remove regular PEM headers also
    sRealCertificate = StringHelper.trimStart (sRealCertificate, BEGIN_CERTIFICATE);
    sRealCertificate = StringHelper.trimEnd (sRealCertificate, END_CERTIFICATE);

    // Remove all existing whitespace characters
    return StringHelper.getWithoutAnySpaces (sRealCertificate);
  }

  /**
   * The certificate string needs to be emitted in portions of 64 characters. If
   * characters are left, than &lt;CR&gt;&lt;LF&gt; ("\r\n") must be added to
   * the string so that the next characters start on a new line. After the last
   * part, no &lt;CR&gt;&lt;LF&gt; is needed. Respective RFC parts are 1421
   * 4.3.2.2 and 4.3.2.4
   *
   * @param sCertificate
   *        Original certificate string as stored in the DB
   * @param bIncludePEMHeader
   *        <code>true</code> to include {@link #BEGIN_CERTIFICATE} header and
   *        {@link #END_CERTIFICATE} footer.
   * @return The RFC 1421 compliant string. May be <code>null</code> if the
   *         original string is <code>null</code> or empty.
   */
  @Nullable
  public static String getRFC1421CompliantString (@Nullable final String sCertificate, final boolean bIncludePEMHeader)
  {
    return getRFC1421CompliantString (sCertificate, bIncludePEMHeader, CRLF);
  }

  /**
   * The certificate string needs to be emitted in portions of 64 characters. If
   * characters are left, than a line separator (e.g. &lt;CR&gt;&lt;LF&gt; -
   * "\r\n") must be added to the string so that the next characters start on a
   * new line. After the last part, no line separator is needed. Respective RFC
   * parts are 1421 4.3.2.2 and 4.3.2.4
   *
   * @param sCertificate
   *        Original certificate string as stored in the DB
   * @param bIncludePEMHeader
   *        <code>true</code> to include {@link #BEGIN_CERTIFICATE} header and
   *        {@link #END_CERTIFICATE} footer.
   * @param sLineSeparator
   *        The line separator to be used. May not be <code>null</code>. Usually
   *        this is "\r\n" but may also be just "\n".
   * @return The RFC 1421 compliant string. May be <code>null</code> if the
   *         original string is <code>null</code> or empty.
   * @since 8.5.5
   */
  @Nullable
  public static String getRFC1421CompliantString (@Nullable final String sCertificate,
                                                  final boolean bIncludePEMHeader,
                                                  @Nonnull final String sLineSeparator)
  {
    ValueEnforcer.notNull (sLineSeparator, "LineSeparator");

    // Remove special begin and end stuff
    String sPlainString = getWithoutPEMHeader (sCertificate);
    if (StringHelper.hasNoText (sPlainString))
      return null;

    // Start building the result
    final int nMaxLineLength = 64;
    // Start with the prefix
    final StringBuilder aSB = new StringBuilder ();
    if (bIncludePEMHeader)
      aSB.append (BEGIN_CERTIFICATE).append ('\n');
    while (sPlainString.length () > nMaxLineLength)
    {
      // Append line + line separator
      aSB.append (sPlainString, 0, nMaxLineLength).append (sLineSeparator);

      // Remove the start of the string
      sPlainString = sPlainString.substring (nMaxLineLength);
    }

    // Append the rest
    aSB.append (sPlainString);

    // Add trailer
    if (bIncludePEMHeader)
      aSB.append ('\n').append (END_CERTIFICATE);

    return aSB.toString ();
  }

  /**
   * Convert the passed byte array to an X.509 certificate object.
   *
   * @param aCertBytes
   *        The original certificate bytes. May be <code>null</code> or empty.
   * @return <code>null</code> if the passed byte array is <code>null</code> or
   *         empty
   * @throws CertificateException
   *         In case the passed string cannot be converted to an X.509
   *         certificate.
   */
  @Nullable
  public static X509Certificate convertByteArrayToCertficate (@Nullable final byte [] aCertBytes) throws CertificateException
  {
    if (ArrayHelper.isEmpty (aCertBytes))
      return null;

    // Certificate is always ISO-8859-1 encoded
    return convertStringToCertficate (new String (aCertBytes, CERT_CHARSET));
  }

  @Nonnull
  private static X509Certificate _str2cert (@Nonnull final String sCertString,
                                            @Nonnull final CertificateFactory aCertificateFactory) throws CertificateException
  {
    final String sRealCertString = getRFC1421CompliantString (sCertString, true);

    return (X509Certificate) aCertificateFactory.generateCertificate (new StringInputStream (sRealCertString,
                                                                                             CERT_CHARSET));
  }

  /**
   * Convert the passed String to an X.509 certificate.
   *
   * @param sCertString
   *        The original text string. May be <code>null</code> or empty. The
   *        String must be ISO-8859-1 encoded for the binary certificate to be
   *        read!
   * @return <code>null</code> if the passed string is <code>null</code> or
   *         empty
   * @throws CertificateException
   *         In case the passed string cannot be converted to an X.509
   *         certificate.
   * @throws IllegalArgumentException
   *         If the input string is e.g. invalid Base64 encoded.
   */
  @Nullable
  public static X509Certificate convertStringToCertficate (@Nullable final String sCertString) throws CertificateException
  {
    if (StringHelper.hasNoText (sCertString))
    {
      // No string -> no certificate
      return null;
    }

    final CertificateFactory aCertificateFactory = getX509CertificateFactory ();

    // Convert certificate string to an object
    try
    {
      return _str2cert (sCertString, aCertificateFactory);
    }
    catch (final IllegalArgumentException | CertificateException ex)
    {
      // In some weird configurations, the result string is a hex encoded
      // certificate instead of the string
      // -> Try to work around it
      if (s_aLogger.isDebugEnabled ())
        s_aLogger.debug ("Failed to decode provided X.509 certificate string: " + sCertString);

      String sHexDecodedString;
      try
      {
        sHexDecodedString = new String (StringHelper.getHexDecoded (sCertString), CERT_CHARSET);
      }
      catch (final IllegalArgumentException ex2)
      {
        // Can happen, when the source string has an odd length (like 3 or 117).
        // In this case the original exception is rethrown
        throw ex;
      }

      return _str2cert (sHexDecodedString, aCertificateFactory);
    }
  }

  /**
   * Convert the passed X.509 certificate string to a byte array.
   *
   * @param sCertificate
   *        The original certificate string. May be <code>null</code> or empty.
   * @return <code>null</code> if the passed string is <code>null</code> or
   *         empty or an invalid Base64 string
   */
  @Nullable
  public static byte [] convertCertificateStringToByteArray (@Nullable final String sCertificate)
  {
    // Remove prefix/suffix
    final String sPlainCert = getWithoutPEMHeader (sCertificate);
    if (StringHelper.hasNoText (sPlainCert))
      return null;

    // The remaining string is supposed to be Base64 encoded -> decode
    return Base64.safeDecode (sPlainCert);
  }

  /**
   * Get the provided certificate as PEM (Base64) encoded String.
   *
   * @param aCert
   *        The certificate to encode. May not be <code>null</code>.
   * @return The PEM string with {@link #BEGIN_CERTIFICATE} and
   *         {@link #END_CERTIFICATE}.
   * @throws IllegalArgumentException
   *         If the certificate could not be encoded. Cause is a
   *         {@link CertificateEncodingException}.
   * @since 8.5.5
   */
  @Nonnull
  @Nonempty
  public static String getPEMEncodedCertificate (@Nonnull final Certificate aCert)
  {
    ValueEnforcer.notNull (aCert, "Cert");
    try
    {
      final String sEncodedCert = Base64.encodeBytes (aCert.getEncoded ());
      return BEGIN_CERTIFICATE + "\n" + sEncodedCert + "\n" + END_CERTIFICATE;
    }
    catch (final CertificateEncodingException ex)
    {
      throw new IllegalArgumentException ("Failed to encode certificate " + aCert, ex);
    }
  }
}
