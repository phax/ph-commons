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

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.bc.PBCProvider;
import com.helger.security.crl.CRLCache;
import com.helger.security.crl.CRLDownloader;

/**
 * Test-only helper for generating throw-away X.509 certificates and CRL caches used by the
 * revocation tests. Centralises the BouncyCastle plumbing so individual tests stay focused on the
 * behaviour they verify.
 *
 * @author Philip Helger
 */
@Immutable
public final class MockCertificateHelper
{
  /**
   * A URL that is never going to be downloaded - used to simulate a missing CRL distribution point.
   */
  public static final String UNREACHABLE_CRL_URL = "http://crl.invalid.example/missing.crl";

  @PresentForCodeCoverage
  private static final MockCertificateHelper INSTANCE = new MockCertificateHelper ();

  private MockCertificateHelper ()
  {}

  /**
   * @return A fresh 2048-bit RSA key pair. Never <code>null</code>.
   * @throws Exception
   *         on any key generation failure.
   */
  @NonNull
  public static KeyPair createKeyPair () throws Exception
  {
    final KeyPairGenerator aGen = KeyPairGenerator.getInstance ("RSA");
    aGen.initialize (2048);
    return aGen.generateKeyPair ();
  }

  /**
   * Build a self-signed root CA certificate that is valid from 10 minutes ago to 1 day from now,
   * with BasicConstraints CA:true, KeyUsage keyCertSign+cRLSign and matching SKI/AKI extensions.
   *
   * @param aKeyPair
   *        Key pair to use for both signing and as the CA's public key. May not be
   *        <code>null</code>.
   * @return The generated CA certificate. Never <code>null</code>.
   * @throws Exception
   *         on any signing/encoding failure.
   */
  @NonNull
  public static X509Certificate createSelfSignedCA (@NonNull final KeyPair aKeyPair) throws Exception
  {
    final X500Principal aSubject = new X500Principal ("CN=Test Root CA,O=ph-commons,C=AT");
    final long nNow = System.currentTimeMillis ();
    final Date aNotBefore = new Date (nNow - Duration.ofMinutes (10).toMillis ());
    final Date aNotAfter = new Date (nNow + Duration.ofDays (1).toMillis ());

    final JcaX509v3CertificateBuilder aBuilder = new JcaX509v3CertificateBuilder (aSubject,
                                                                                  BigInteger.valueOf (nNow),
                                                                                  aNotBefore,
                                                                                  aNotAfter,
                                                                                  aSubject,
                                                                                  aKeyPair.getPublic ());
    aBuilder.addExtension (Extension.basicConstraints, true, new BasicConstraints (true));
    aBuilder.addExtension (Extension.keyUsage, true, new KeyUsage (KeyUsage.keyCertSign | KeyUsage.cRLSign));
    final JcaX509ExtensionUtils aExtUtils = new JcaX509ExtensionUtils ();
    aBuilder.addExtension (Extension.subjectKeyIdentifier,
                           false,
                           aExtUtils.createSubjectKeyIdentifier (aKeyPair.getPublic ()));
    aBuilder.addExtension (Extension.authorityKeyIdentifier,
                           false,
                           aExtUtils.createAuthorityKeyIdentifier (aKeyPair.getPublic ()));

    final ContentSigner aSigner = new JcaContentSignerBuilder ("SHA256WithRSA").setProvider (PBCProvider.getProvider ())
                                                                               .build (aKeyPair.getPrivate ());
    final X509CertificateHolder aHolder = aBuilder.build (aSigner);
    return new JcaX509CertificateConverter ().setProvider (PBCProvider.getProvider ()).getCertificate (aHolder);
  }

  /**
   * Build an end-entity certificate signed by the provided issuer and carrying a single CRL
   * Distribution Points extension pointing to the given URL. The certificate is valid from 10
   * minutes ago to 1 day from now.
   *
   * @param aIssuerCert
   *        Issuer (CA) certificate. May not be <code>null</code>.
   * @param aIssuerKey
   *        Issuer's private key used for signing. May not be <code>null</code>.
   * @param aSubjectKey
   *        Public key to embed in the new certificate. May not be <code>null</code>.
   * @param sCRLURL
   *        The CRL distribution point URL. Neither <code>null</code> nor empty.
   * @return The generated end-entity certificate. Never <code>null</code>.
   * @throws Exception
   *         on any signing/encoding failure.
   */
  @NonNull
  public static X509Certificate createEndEntityWithCRLDP (@NonNull final X509Certificate aIssuerCert,
                                                          @NonNull final PrivateKey aIssuerKey,
                                                          @NonNull final PublicKey aSubjectKey,
                                                          @NonNull @Nonempty final String sCRLURL) throws Exception
  {
    final X500Principal aSubject = new X500Principal ("CN=End Entity,O=ph-commons,C=AT");
    final long nNow = System.currentTimeMillis ();
    final Date aNotBefore = new Date (nNow - Duration.ofMinutes (10).toMillis ());
    final Date aNotAfter = new Date (nNow + Duration.ofDays (1).toMillis ());

    final JcaX509v3CertificateBuilder aBuilder = new JcaX509v3CertificateBuilder (aIssuerCert.getSubjectX500Principal (),
                                                                                  BigInteger.valueOf (nNow + 1),
                                                                                  aNotBefore,
                                                                                  aNotAfter,
                                                                                  aSubject,
                                                                                  aSubjectKey);
    final DistributionPointName aDPName = new DistributionPointName (DistributionPointName.FULL_NAME,
                                                                     new GeneralNames (new GeneralName (GeneralName.uniformResourceIdentifier,
                                                                                                        sCRLURL)));
    final DistributionPoint aDP = new DistributionPoint (aDPName, null, null);
    aBuilder.addExtension (Extension.cRLDistributionPoints, false, new CRLDistPoint (new DistributionPoint [] { aDP }));
    final JcaX509ExtensionUtils aExtUtils = new JcaX509ExtensionUtils ();
    aBuilder.addExtension (Extension.subjectKeyIdentifier, false, aExtUtils.createSubjectKeyIdentifier (aSubjectKey));
    aBuilder.addExtension (Extension.authorityKeyIdentifier,
                           false,
                           aExtUtils.createAuthorityKeyIdentifier (aIssuerCert.getPublicKey ()));
    aBuilder.addExtension (Extension.keyUsage,
                           true,
                           new KeyUsage (KeyUsage.digitalSignature | KeyUsage.keyEncipherment));

    final ContentSigner aSigner = new JcaContentSignerBuilder ("SHA256WithRSA").setProvider (PBCProvider.getProvider ())
                                                                               .build (aIssuerKey);
    final X509CertificateHolder aHolder = aBuilder.build (aSigner);
    return new JcaX509CertificateConverter ().setProvider (PBCProvider.getProvider ()).getCertificate (aHolder);
  }

  /**
   * Build a {@link CRLCache} whose underlying URL downloader always returns <code>null</code>.
   *
   * @return Never <code>null</code>.
   */
  @NonNull
  public static CRLCache createAlwaysFailingCRLCache ()
  {
    return createAlwaysFailingCRLCache (null);
  }

  /**
   * Build a {@link CRLCache} whose underlying URL downloader always returns <code>null</code>. Each
   * download attempt is counted in the provided counter, allowing tests to assert that the
   * downloader was actually invoked.
   *
   * @param aDownloadAttempts
   *        Optional counter incremented on every download attempt. May be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static CRLCache createAlwaysFailingCRLCache (@Nullable final AtomicInteger aDownloadAttempts)
  {
    final CRLDownloader aDownloader = new CRLDownloader (sURL -> {
      if (aDownloadAttempts != null)
        aDownloadAttempts.incrementAndGet ();
      return null;
    });
    return new CRLCache (aDownloader, Duration.ofMinutes (1));
  }
}
