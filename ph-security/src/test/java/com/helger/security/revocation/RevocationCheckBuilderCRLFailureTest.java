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

import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.helger.security.certificate.MockCertificateHelper;
import com.helger.security.crl.CRLCache;

/**
 * Test class verifying that {@link AbstractRevocationCheckBuilder#build()} returns
 * {@link ERevoked#UNKNOWN} (and not {@link ERevoked#REVOKED}) when CRL retrieval fails for an
 * otherwise valid certificate.
 *
 * @author Philip Helger
 */
public final class RevocationCheckBuilderCRLFailureTest
{
  @Test
  public void testGeneratedChainValidates () throws Exception
  {
    // Sanity test: ensure the synthetic CA + EE chain validates without revocation, so any
    // failure of the revocation tests below isolates to revocation handling.
    final KeyPair aCAKeyPair = MockCertificateHelper.createKeyPair ();
    final X509Certificate aCACert = MockCertificateHelper.createSelfSignedCA (aCAKeyPair);
    final KeyPair aEEKeyPair = MockCertificateHelper.createKeyPair ();
    final X509Certificate aEECert = MockCertificateHelper.createEndEntityWithCRLDP (aCACert,
                                                                                    aCAKeyPair.getPrivate (),
                                                                                    aEEKeyPair.getPublic (),
                                                                                    MockCertificateHelper.UNREACHABLE_CRL_URL);

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
    final KeyPair aCAKeyPair = MockCertificateHelper.createKeyPair ();
    final X509Certificate aCACert = MockCertificateHelper.createSelfSignedCA (aCAKeyPair);
    final KeyPair aEEKeyPair = MockCertificateHelper.createKeyPair ();
    final X509Certificate aEECert = MockCertificateHelper.createEndEntityWithCRLDP (aCACert,
                                                                                    aCAKeyPair.getPrivate (),
                                                                                    aEEKeyPair.getPublic (),
                                                                                    MockCertificateHelper.UNREACHABLE_CRL_URL);

    final AtomicInteger aDownloadAttempts = new AtomicInteger (0);
    final CRLCache aCRLCache = MockCertificateHelper.createAlwaysFailingCRLCache (aDownloadAttempts);

    // Soft fail OFF: PKIX will throw UNDETERMINED_REVOCATION_STATUS, the catch block must convert
    // that to UNKNOWN (and not REVOKED).
    final ERevoked eResult = new RevocationCheckBuilder ().certificate (aEECert)
                                                          .validCA (aCACert)
                                                          .checkMode (ERevocationCheckMode.CRL)
                                                          .allowSoftFail (false)
                                                          .crlCache (aCRLCache)
                                                          .exceptionHandler (ex -> {
                                                            // swallow - we only care about the result
                                                          })
                                                          .build ();
    assertNotNull (eResult);
    assertSame ("CRL download failure must not be reported as REVOKED", ERevoked.UNKNOWN, eResult);
    assertEquals ("Downloader should have been asked at least once", 1, aDownloadAttempts.get ());
  }

  @Test
  public void testCRLDownloadFailureReturnsUnknown_SoftFail () throws Exception
  {
    final KeyPair aCAKeyPair = MockCertificateHelper.createKeyPair ();
    final X509Certificate aCACert = MockCertificateHelper.createSelfSignedCA (aCAKeyPair);
    final KeyPair aEEKeyPair = MockCertificateHelper.createKeyPair ();
    final X509Certificate aEECert = MockCertificateHelper.createEndEntityWithCRLDP (aCACert,
                                                                                    aCAKeyPair.getPrivate (),
                                                                                    aEEKeyPair.getPublic (),
                                                                                    MockCertificateHelper.UNREACHABLE_CRL_URL);

    final AtomicInteger aDownloadAttempts = new AtomicInteger (0);
    final CRLCache aCRLCache = MockCertificateHelper.createAlwaysFailingCRLCache (aDownloadAttempts);

    // Soft fail ON: PKIX would happily return success despite the missing CRL. The aCRLDownloadFailed
    // tracking flag must still flip the final result to UNKNOWN, otherwise the caller would think
    // the certificate was successfully verified as not-revoked.
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
