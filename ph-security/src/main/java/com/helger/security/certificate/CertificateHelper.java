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
package com.helger.security.certificate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnull;
import com.helger.annotation.Nullable;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.misc.PresentForCodeCoverage;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.base64.Base64;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.io.stream.NonBlockingByteArrayInputStream;
import com.helger.commons.io.stream.StringInputStream;
import com.helger.commons.string.StringHelper;
import com.helger.security.revocation.AbstractRevocationCheckBuilder;
import com.helger.security.revocation.RevocationCheckResultCache;

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

  public static final String BEGIN_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----";
  public static final String END_PRIVATE_KEY = "-----END PRIVATE KEY-----";

  public static final String CRLF = "\r\n";

  /** Character set used for String-Certificate conversion */
  public static final Charset CERT_CHARSET = StandardCharsets.ISO_8859_1;

  public static final String PRINCIPAL_TYPE_CN = "CN";
  public static final String PRINCIPAL_TYPE_O = "O";

  private static final Logger LOGGER = LoggerFactory.getLogger (CertificateHelper.class);

  @PresentForCodeCoverage
  private static final CertificateHelper INSTANCE = new CertificateHelper ();

  private CertificateHelper ()
  {}

  @Nonnull
  public static CertificateFactory getX509CertificateFactory () throws CertificateException
  {
    return CertificateFactory.getInstance ("X.509");
  }

  /**
   * Make sure, the provided String is surrounded by the PEM headers {@link #BEGIN_CERTIFICATE} and
   * {@link #END_CERTIFICATE}
   *
   * @param sCertString
   *        Certificate string to use.
   * @return The String with the surrounding headers and footers
   * @since 11.1.1
   */
  @Nonnull
  public static String getCertificateWithPEMHeader (@Nonnull final String sCertString)
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
   * {@value #END_CERTIFICATE} values from the passed certificate string. Additionally all
   * whitespaces of the string are removed.
   *
   * @param sCertificate
   *        The source certificate string. May be <code>null</code>.
   * @return <code>null</code> if the input string is <code>null</code> or empty, the stripped down
   *         string otherwise.
   */
  @Nullable
  public static String getWithoutPEMHeader (@Nullable final String sCertificate)
  {
    if (StringHelper.hasNoText (sCertificate))
      return null;

    // Remove special begin and end stuff
    String sRealCertificate = sCertificate.trim ();

    // Remove regular PEM headers also
    sRealCertificate = StringHelper.trimStart (sRealCertificate, BEGIN_CERTIFICATE);
    sRealCertificate = StringHelper.trimEnd (sRealCertificate, END_CERTIFICATE);

    // Remove all existing whitespace characters
    return StringHelper.getWithoutAnySpaces (sRealCertificate);
  }

  /**
   * The certificate string needs to be emitted in portions of 64 characters. If characters are
   * left, than &lt;CR&gt;&lt;LF&gt; ("\r\n") must be added to the string so that the next
   * characters start on a new line. After the last part, no &lt;CR&gt;&lt;LF&gt; is needed.
   * Respective RFC parts are 1421 4.3.2.2 and 4.3.2.4
   *
   * @param sCertificate
   *        Original certificate string as stored in the DB
   * @param bIncludePEMHeader
   *        <code>true</code> to include {@link #BEGIN_CERTIFICATE} header and
   *        {@link #END_CERTIFICATE} footer.
   * @return The RFC 1421 compliant string. May be <code>null</code> if the original string is
   *         <code>null</code> or empty.
   */
  @Nullable
  public static String getRFC1421CompliantString (@Nullable final String sCertificate, final boolean bIncludePEMHeader)
  {
    return getRFC1421CompliantString (sCertificate, bIncludePEMHeader, CRLF);
  }

  /**
   * The certificate string needs to be emitted in portions of 64 characters. If characters are
   * left, than a line separator (e.g. &lt;CR&gt;&lt;LF&gt; - "\r\n") must be added to the string so
   * that the next characters start on a new line. After the last part, no line separator is needed.
   * Respective RFC parts are 1421 4.3.2.2 and 4.3.2.4
   *
   * @param sCertificate
   *        Original certificate string as stored in the DB
   * @param bIncludePEMHeader
   *        <code>true</code> to include {@link #BEGIN_CERTIFICATE} header and
   *        {@link #END_CERTIFICATE} footer.
   * @param sLineSeparator
   *        The line separator to be used. May not be <code>null</code>. Usually this is "\r\n" but
   *        may also be just "\n".
   * @return The RFC 1421 compliant string. May be <code>null</code> if the original string is
   *         <code>null</code> or empty.
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
   * @return <code>null</code> if the passed byte array is <code>null</code> or empty
   * @throws CertificateException
   *         In case the passed string cannot be converted to an X.509 certificate.
   */
  @Nullable
  public static X509Certificate convertByteArrayToCertficate (@Nullable final byte [] aCertBytes) throws CertificateException
  {
    if (ArrayHelper.isEmpty (aCertBytes))
      return null;

    // Certificate is always ISO-8859-1 encoded
    return convertStringToCertficate (new String (aCertBytes, CERT_CHARSET), false);
  }

  /**
   * Convert the passed byte array to an X.509 certificate object.
   *
   * @param aCertBytes
   *        The original certificate bytes. May be <code>null</code> or empty.
   * @return <code>null</code> if the passed byte array is <code>null</code>, empty or not a valid
   *         certificate.
   * @since 9.4.0
   */
  @Nullable
  public static X509Certificate convertByteArrayToCertficateOrNull (@Nullable final byte [] aCertBytes)
  {
    try
    {
      return convertByteArrayToCertficate (aCertBytes);
    }
    catch (final CertificateException ex)
    {
      return null;
    }
  }

  /**
   * Convert the passed String to an X.509 certificate without converting it to a String first.
   *
   * @param aCertBytes
   *        The certificate bytes. May be <code>null</code>.
   * @return <code>null</code> if the passed array is <code>null</code> or empty
   * @throws CertificateException
   *         In case the passed bytes[] cannot be converted to an X.509 certificate.
   * @since 9.3.4
   */
  @Nullable
  public static X509Certificate convertByteArrayToCertficateDirect (@Nullable final byte [] aCertBytes) throws CertificateException
  {
    if (ArrayHelper.isEmpty (aCertBytes))
    {
      // No string -> no certificate
      return null;
    }

    final CertificateFactory aCertificateFactory = getX509CertificateFactory ();
    try (final NonBlockingByteArrayInputStream aBAIS = new NonBlockingByteArrayInputStream (aCertBytes))
    {
      return (X509Certificate) aCertificateFactory.generateCertificate (aBAIS);
    }
  }

  @Nonnull
  private static X509Certificate _str2cert (@Nonnull final String sCertString,
                                            @Nonnull final CertificateFactory aCertificateFactory) throws CertificateException
  {
    final String sRealCertString = getRFC1421CompliantString (sCertString, true);

    try (final StringInputStream aIS = new StringInputStream (sRealCertString, CERT_CHARSET))
    {
      return (X509Certificate) aCertificateFactory.generateCertificate (aIS);
    }
  }

  /**
   * Convert the passed String to an X.509 certificate.
   *
   * @param sCertString
   *        The original text string. May be <code>null</code> or empty. The String must be
   *        ISO-8859-1 encoded for the binary certificate to be read!
   * @return <code>null</code> if the passed string is <code>null</code> or empty
   * @throws CertificateException
   *         In case the passed string cannot be converted to an X.509 certificate.
   * @throws IllegalArgumentException
   *         If the input string is e.g. invalid Base64 encoded.
   * @since 2.1.1
   */
  @Nullable
  public static X509Certificate convertStringToCertficate (@Nullable final String sCertString) throws CertificateException
  {
    return convertStringToCertficate (sCertString, false);
  }

  /**
   * Convert the passed String to an X.509 certificate.
   *
   * @param sCertString
   *        The original text string. May be <code>null</code> or empty. The String must be
   *        ISO-8859-1 encoded for the binary certificate to be read!
   * @param bWithFallback
   *        <code>true</code> to enable legacy fallback parsing
   * @return <code>null</code> if the passed string is <code>null</code> or empty
   * @throws CertificateException
   *         In case the passed string cannot be converted to an X.509 certificate.
   * @throws IllegalArgumentException
   *         If the input string is e.g. invalid Base64 encoded.
   * @since 2.1.1
   */
  @Nullable
  public static X509Certificate convertStringToCertficate (@Nullable final String sCertString,
                                                           final boolean bWithFallback) throws CertificateException
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
      if (LOGGER.isDebugEnabled ())
        LOGGER.debug ("Failed to decode provided X.509 certificate string: " + sCertString);

      if (!bWithFallback)
        throw ex;

      String sHexDecodedString;
      try
      {
        sHexDecodedString = new String (StringHelper.getHexDecoded (sCertString), CERT_CHARSET);
      }
      catch (final IllegalArgumentException ex2)
      {
        // Can happen, when the source string has an odd length (like 3 or 117).
        // In this case the original exception is re-thrown
        throw ex;
      }

      return _str2cert (sHexDecodedString, aCertificateFactory);
    }
  }

  /**
   * Convert the passed String to an X.509 certificate, swallowing all errors.
   *
   * @param sCertString
   *        The certificate string to be parsed.
   * @return <code>null</code> in case the certificate cannot be converted.
   * @see #convertStringToCertficate(String)
   * @since 9.3.4
   */
  @Nullable
  public static X509Certificate convertStringToCertficateOrNull (@Nullable final String sCertString)
  {
    try
    {
      return convertStringToCertficate (sCertString, false);
    }
    catch (final CertificateException | IllegalArgumentException ex)
    {
      return null;
    }
  }

  /**
   * Convert the passed X.509 certificate string to a byte array.
   *
   * @param sCertificate
   *        The original certificate string. May be <code>null</code> or empty.
   * @return <code>null</code> if the passed string is <code>null</code> or empty or an invalid
   *         Base64 string
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
   * Get the provided certificate as a byte array.
   *
   * @param aCert
   *        The certificate to encode. May not be <code>null</code>.
   * @return The byte array
   * @throws IllegalArgumentException
   *         If the certificate could not be encoded. Cause is a
   *         {@link CertificateEncodingException}.
   * @since 10.0.0
   */
  @Nonnull
  @Nonempty
  public static byte [] getEncodedCertificate (@Nonnull final Certificate aCert)
  {
    ValueEnforcer.notNull (aCert, "Cert");
    try
    {
      return aCert.getEncoded ();
    }
    catch (final CertificateEncodingException ex)
    {
      throw new IllegalArgumentException ("Failed to encode certificate " + aCert, ex);
    }
  }

  /**
   * Get the provided certificate as PEM (Base64) encoded String.
   *
   * @param aCert
   *        The certificate to encode. May not be <code>null</code>.
   * @return The PEM string with {@link #BEGIN_CERTIFICATE} and {@link #END_CERTIFICATE}.
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

  /**
   * Check if the "not valid before"/"not valid after" of the provided X509 certificate is valid per
   * "now".
   *
   * @param aCert
   *        The certificate to check. May not be <code>null</code>.
   * @return <code>true</code> if it is valid, <code>false</code> if not.
   * @since 9.3.8
   */
  public static boolean isCertificateValidPerNow (@Nonnull final X509Certificate aCert)
  {
    ValueEnforcer.notNull (aCert, "Cert");
    try
    {
      aCert.checkValidity ();
      return true;
    }
    catch (final CertificateExpiredException | CertificateNotYetValidException ex)
    {
      return false;
    }
  }

  @Nullable
  public static PrivateKey convertStringToPrivateKey (@Nullable final String sPrivateKey) throws GeneralSecurityException
  {
    if (StringHelper.hasNoText (sPrivateKey))
      return null;

    String sRealPrivateKey = StringHelper.trimStart (sPrivateKey, BEGIN_PRIVATE_KEY);
    sRealPrivateKey = StringHelper.trimEnd (sRealPrivateKey, END_PRIVATE_KEY);
    sRealPrivateKey = StringHelper.getWithoutAnySpaces (sRealPrivateKey);
    final byte [] aPrivateKeyBytes = Base64.safeDecode (sRealPrivateKey);
    if (aPrivateKeyBytes == null)
      return null;

    final KeyFactory aKeyFactory = KeyFactory.getInstance ("RSA");
    final PKCS8EncodedKeySpec aKeySpec = new PKCS8EncodedKeySpec (aPrivateKeyBytes);
    return aKeyFactory.generatePrivate (aKeySpec);
  }

  /**
   * Check if the provided certificate is a CA (Certificate Authority) or not.
   *
   * @param aCert
   *        The certificate to check. May not be <code>null</code>.
   * @return <code>true</code> if it is a CA, <code>false</code> if not.
   */
  public static boolean isCA (@Nonnull final X509Certificate aCert)
  {
    ValueEnforcer.notNull (aCert, "Cert");

    final byte [] aBCBytes = aCert.getExtensionValue (Extension.basicConstraints.getId ());
    if (aBCBytes != null)
    {
      try
      {
        final ASN1Encodable aBCDecoded = JcaX509ExtensionUtils.parseExtensionValue (aBCBytes);
        if (aBCDecoded instanceof ASN1Sequence)
        {
          final ASN1Sequence aBCSequence = (ASN1Sequence) aBCDecoded;
          final BasicConstraints aBasicConstraints = BasicConstraints.getInstance (aBCSequence);
          if (aBasicConstraints != null)
            return aBasicConstraints.isCA ();
        }
      }
      catch (final IOException e)
      {
        // Fall through
      }
    }
    // Defaults to "no"
    return false;
  }

  /**
   * Check if the provided certificate (from the revocation checker) is a valid certificate. It
   * checks:
   * <ol>
   * <li>Validity at the provided date time (aRevocationChecker.checkDate) or per now if none was
   * provided</li>
   * <li>If the certificate issuer is part of the provided list of issuers</li>
   * <li>If the certificate is revoked</li>
   * </ol>
   *
   * @param aIssuers
   *        The list of valid certificate issuers to check against. May be <code>null</code> to not
   *        perform this check.
   * @param aRevocationCache
   *        The cache. May be <code>null</code> to disable caching.
   * @param aRevocationChecker
   *        The revocation checker builder with all necessary parameters already set. May not be
   *        <code>null</code>.
   * @return {@link ECertificateCheckResult} and never <code>null</code>.
   * @since 11.2.1
   */
  @Nonnull
  public static ECertificateCheckResult checkCertificate (@Nullable final ICommonsSet <X500Principal> aIssuers,
                                                          @Nullable final RevocationCheckResultCache aRevocationCache,
                                                          @Nonnull final AbstractRevocationCheckBuilder <?> aRevocationChecker)
  {
    ValueEnforcer.notNull (aRevocationChecker, "RevocationChecker");

    if (LOGGER.isDebugEnabled ())
      LOGGER.debug ("Running Certificate Check" +
                    (aIssuers != null ? " against a list of " + aIssuers.size () + " certificate issuers" : "") +
                    (aRevocationCache != null ? "; a cache is provided" : "; not using a cache"));

    // Get the certificate to be validated
    final X509Certificate aCert = aRevocationChecker.certificate ();
    if (aCert == null)
    {
      LOGGER.warn ("No Certificate was provided to the certificate check");
      return ECertificateCheckResult.NO_CERTIFICATE_PROVIDED;
    }

    // Check validity date
    final Date aCheckDate = aRevocationChecker.checkDate ();
    try
    {
      // null means now
      if (aCheckDate == null)
      {
        if (LOGGER.isDebugEnabled ())
          LOGGER.debug ("Checking the Certificate validity against the current date time");
        aCert.checkValidity ();
      }
      else
      {
        if (LOGGER.isDebugEnabled ())
          LOGGER.debug ("Checking the Certificate validity against the provided date time " + aCheckDate);
        aCert.checkValidity (aCheckDate);
      }
    }
    catch (final CertificateNotYetValidException ex)
    {
      LOGGER.warn ("The provided Certificate is not yet valid per " +
                   (aCheckDate == null ? "now" : aCheckDate.toString ()));
      return ECertificateCheckResult.NOT_YET_VALID;
    }
    catch (final CertificateExpiredException ex)
    {
      LOGGER.warn ("The provided Certificate is expired per " + (aCheckDate == null ? "now" : aCheckDate.toString ()));
      return ECertificateCheckResult.EXPIRED;
    }

    if (aIssuers != null)
    {
      // Check if issuer is known
      final X500Principal aIssuer = aCert.getIssuerX500Principal ();
      if (!aIssuers.contains (aIssuer))
      {
        // Not a valid certificate
        LOGGER.warn ("The provided Certificate issuer '" +
                     aIssuer +
                     "' is not in the list of trusted issuers " +
                     aIssuers);
        return ECertificateCheckResult.UNSUPPORTED_ISSUER;
      }
    }
    else
    {
      if (LOGGER.isDebugEnabled ())
        LOGGER.debug ("Not testing against known Certificate issuers");
    }

    // Check revocation OCSP/CLR
    if (aRevocationCache != null)
    {
      // Caching is enabled
      if (LOGGER.isDebugEnabled ())
        LOGGER.debug ("Testing if the Certificate is revoked, using a cache");

      final boolean bRevoked = aRevocationCache.isRevoked (aCert);
      if (bRevoked)
      {
        LOGGER.warn ("The Certificate is revoked [caching used]");
        return ECertificateCheckResult.REVOKED;
      }
    }
    else
    {
      // No caching desired
      if (LOGGER.isDebugEnabled ())
        LOGGER.debug ("Testing if the Certificate is revoked, without a cache");

      if (aRevocationChecker.build ().isRevoked ())
      {
        LOGGER.warn ("The Certificate is revoked [no caching]");
        return ECertificateCheckResult.REVOKED;
      }
    }

    // Done
    if (LOGGER.isDebugEnabled ())
      LOGGER.debug ("The Certificate seems to be valid");

    return ECertificateCheckResult.VALID;
  }

  @Nullable
  public static String getPrincipalTypeValue (@Nullable final String sPrincipal, @Nonnull final String sType)
                                                                                                              throws InvalidNameException
  {
    ValueEnforcer.notNull (sType, "Type");
    if (sPrincipal != null)
      for (final Rdn aRdn : new LdapName (sPrincipal).getRdns ())
        if (aRdn.getType ().equalsIgnoreCase (sType))
          return (String) aRdn.getValue ();
    return null;
  }

  @Nullable
  public static String getCN (@Nullable final String sPrincipal) throws InvalidNameException
  {
    return getPrincipalTypeValue (sPrincipal, PRINCIPAL_TYPE_CN);
  }

  @Nullable
  public static String getSubjectCN (@Nullable final X509Certificate aCert)
  {
    return aCert != null ? getCNOrNull (aCert.getSubjectX500Principal ()) : null;
  }

  @Nullable
  public static String getCNOrNull (@Nullable final X500Principal aPrincipal)
  {
    return aPrincipal != null ? getCNOrNull (aPrincipal.getName ()) : null;
  }

  @Nullable
  public static String getCNOrNull (@Nullable final String sPrincipal)
  {
    try
    {
      return getCN (sPrincipal);
    }
    catch (final InvalidNameException ex)
    {
      return null;
    }
  }

  @Nullable
  public static String getO (@Nullable final String sPrincipal) throws InvalidNameException
  {
    return getPrincipalTypeValue (sPrincipal, PRINCIPAL_TYPE_O);
  }

  @Nullable
  public static String getSubjectO (@Nullable final X509Certificate aCert)
  {
    return aCert != null ? getOOrNull (aCert.getSubjectX500Principal ()) : null;
  }

  @Nullable
  public static String getOOrNull (@Nullable final X500Principal aPrincipal)
  {
    return aPrincipal != null ? getOOrNull (aPrincipal.getName ()) : null;
  }

  @Nullable
  public static String getOOrNull (@Nullable final String sPrincipal)
  {
    try
    {
      return getO (sPrincipal);
    }
    catch (final InvalidNameException ex)
    {
      return null;
    }
  }
}
