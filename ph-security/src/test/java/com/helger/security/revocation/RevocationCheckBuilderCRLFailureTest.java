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
package com.helger.security.revocation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

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
import org.junit.Test;

import com.helger.bc.PBCProvider;
import com.helger.datetime.helper.PDTFactory;
import com.helger.security.crl.CRLCache;
import com.helger.security.crl.CRLDownloader;

/**
 * Test class verifying that {@link AbstractRevocationCheckBuilder#build()} returns
 * {@link ERevoked#UNKNOWN} (and not {@link ERevoked#REVOKED}) when CRL retrieval fails for an
 * otherwise valid certificate.
 *
 * @author Philip Helger
 */
public final class RevocationCheckBuilderCRLFailureTest
{
  // A URL that is never going to be downloaded (the test's CRL cache short-circuits everything)
  private static final String UNREACHABLE_CRL_URL = "http://crl.invalid.example/missing.crl";

  @NonNull
  private static KeyPair _createKeyPair () throws Exception
  {
    final KeyPairGenerator aGen = KeyPairGenerator.getInstance ("RSA");
    aGen.initialize (2048);
    return aGen.generateKeyPair ();
  }

  @NonNull
  private static X509Certificate _createSelfSignedCA (@NonNull final KeyPair aKeyPair) throws Exception
  {
    final X500Principal aSubject = new X500Principal ("CN=Test Root CA,O=ph-commons,C=AT");
    final long nNow = PDTFactory.getCurrentMillis ();
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

  @NonNull
  private static X509Certificate _createEndEntityWithCRLDP (@NonNull final X509Certificate aIssuerCert,
                                                            @NonNull final PrivateKey aIssuerKey,
                                                            @NonNull final PublicKey aSubjectKey,
                                                            @NonNull final String sCRLURL) throws Exception
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

    // CRL Distribution Points pointing to an unreachable URL
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

  @NonNull
  private static CRLCache _createAlwaysFailingCRLCache (@NonNull final AtomicInteger aDownloadAttempts)
  {
    // URL downloader that always reports a failed download (returns null bytes)
    final CRLDownloader aDownloader = new CRLDownloader (sURL -> {
      aDownloadAttempts.incrementAndGet ();
      return null;
    });
    return new CRLCache (aDownloader, Duration.ofMinutes (1));
  }

  @Test
  public void testGeneratedChainValidates () throws Exception
  {
    // Sanity test: ensure the synthetic CA + EE chain validates without revocation, so any
    // failure of the revocation tests below isolates to revocation handling.
    final KeyPair aCAKeyPair = _createKeyPair ();
    final X509Certificate aCACert = _createSelfSignedCA (aCAKeyPair);
    final KeyPair aEEKeyPair = _createKeyPair ();
    final X509Certificate aEECert = _createEndEntityWithCRLDP (aCACert,
                                                               aCAKeyPair.getPrivate (),
                                                               aEEKeyPair.getPublic (),
                                                               UNREACHABLE_CRL_URL);

    aEECert.verify (aCACert.getPublicKey ());
    aCACert.verify (aCACert.getPublicKey ());

    // Run with revocation off - should succeed (so any failure in the revocation tests is on the
    // revocation path, not the chain itself)
    final ERevoked eResult = new RevocationCheckBuilder ().certificate (aEECert)
                                                          .validCA (aCACert)
                                                          .checkMode (ERevocationCheckMode.NONE)
                                                          .build ();
    assertSame (ERevoked.NOT_REVOKED, eResult);
  }

  @Test
  public void testCRLDownloadFailureReturnsUnknown_NoSoftFail () throws Exception
  {
    final KeyPair aCAKeyPair = _createKeyPair ();
    final X509Certificate aCACert = _createSelfSignedCA (aCAKeyPair);
    final KeyPair aEEKeyPair = _createKeyPair ();
    final X509Certificate aEECert = _createEndEntityWithCRLDP (aCACert,
                                                               aCAKeyPair.getPrivate (),
                                                               aEEKeyPair.getPublic (),
                                                               UNREACHABLE_CRL_URL);

    final AtomicInteger aDownloadAttempts = new AtomicInteger (0);
    final CRLCache aCRLCache = _createAlwaysFailingCRLCache (aDownloadAttempts);

    // Soft fail OFF: PKIX will throw UNDETERMINED_REVOCATION_STATUS, the catch block must convert
    // that to UNKNOWN (and not REVOKED).
    final ERevoked eResult = new RevocationCheckBuilder ().certificate (aEECert)
                                                          .validCA (aCACert)
                                                          .checkMode (ERevocationCheckMode.CRL)
                                                          .allowSoftFail (false)
                                                          .crlCache (aCRLCache)
                                                          .exceptionHandler (ex -> {
                                                            /*
                                                             * swallow - we only care about the
                                                             * result
                                                             */
                                                          })
                                                          .build ();
    assertNotNull (eResult);
    assertSame ("CRL download failure must not be reported as REVOKED", ERevoked.UNKNOWN, eResult);
    assertEquals ("Downloader should have been asked at least once", 1, aDownloadAttempts.get ());
  }

  @Test
  public void testCRLDownloadFailureReturnsUnknown_SoftFail () throws Exception
  {
    final KeyPair aCAKeyPair = _createKeyPair ();
    final X509Certificate aCACert = _createSelfSignedCA (aCAKeyPair);
    final KeyPair aEEKeyPair = _createKeyPair ();
    final X509Certificate aEECert = _createEndEntityWithCRLDP (aCACert,
                                                               aCAKeyPair.getPrivate (),
                                                               aEEKeyPair.getPublic (),
                                                               UNREACHABLE_CRL_URL);

    final AtomicInteger aDownloadAttempts = new AtomicInteger (0);
    final CRLCache aCRLCache = _createAlwaysFailingCRLCache (aDownloadAttempts);

    // Soft fail ON: PKIX would happily return success despite the missing CRL. The
    // aCRLDownloadFailed tracking flag must still flip the final result to UNKNOWN, otherwise the
    // caller would think the certificate was successfully verified as not-revoked.
    final ERevoked eResult = new RevocationCheckBuilder ().certificate (aEECert)
                                                          .validCA (aCACert)
                                                          .checkMode (ERevocationCheckMode.CRL)
                                                          .allowSoftFail (true)
                                                          .crlCache (aCRLCache)
                                                          .build ();
    assertSame (ERevoked.UNKNOWN, eResult);
    assertEquals (1, aDownloadAttempts.get ());
  }
}
