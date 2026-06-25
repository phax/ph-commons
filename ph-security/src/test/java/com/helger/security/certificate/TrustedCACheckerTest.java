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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.time.Duration;

import org.junit.Test;

import com.helger.base.state.ETriState;
import com.helger.security.crl.CRLCache;
import com.helger.security.revocation.CertificateRevocationCheckerDefaults;
import com.helger.security.revocation.ERevocationCheckMode;

/**
 * End-to-end test that ensures {@link TrustedCAChecker} report
 * {@link ECertificateCheckResult#REVOCATION_STATUS_UNKNOWN} - and not
 * {@link ECertificateCheckResult#REVOKED} or {@link ECertificateCheckResult#VALID} - when the CRL
 * download fails for an otherwise valid certificate.
 *
 * @author Philip Helger
 */
public final class TrustedCACheckerTest
{
  @Test
  public void testTrustedCAChecker_CRLFailureWithCacheReturnsRevocationUnknown () throws Exception
  {
    final KeyPair aCAKeyPair = MockCertificateHelper.createKeyPair ();
    final X509Certificate aCACert = MockCertificateHelper.createSelfSignedCA (aCAKeyPair);

    final KeyPair aEEKeyPair = MockCertificateHelper.createKeyPair ();
    final X509Certificate aEECert = MockCertificateHelper.createEndEntityWithCRLDP (aCACert,
                                                                                    aCAKeyPair.getPrivate (),
                                                                                    aEEKeyPair.getPublic (),
                                                                                    MockCertificateHelper.UNREACHABLE_CRL_URL);

    // Set the global CRL cache used by TrustedCAChecker so the failing CRL endpoint is consulted.
    final CRLCache aPreviousCache = CertificateRevocationCheckerDefaults.getDefaultCRLCache ();
    CertificateRevocationCheckerDefaults.setDefaultCRLCache (MockCertificateHelper.createAlwaysFailingCRLCache ());
    try
    {
      final TrustedCAChecker aChecker = TrustedCAChecker.builder ()
                                                        .checkMode (ERevocationCheckMode.CRL)
                                                        .cachingDuration (Duration.ofMinutes (1))
                                                        .cacheMaxSize (10)
                                                        .trustedCACertificates (aCACert)
                                                        .build ();
      // Force caching ON so we go through RevocationCheckResultCache.getRevocationStatus.
      final ECertificateCheckResult eResult = aChecker.checkCertificate (aEECert,
                                                                         null,
                                                                         ETriState.TRUE,
                                                                         ERevocationCheckMode.CRL);
      assertSame (ECertificateCheckResult.REVOCATION_STATUS_UNKNOWN, eResult);
    }
    finally
    {
      // Restore previous cache
      CertificateRevocationCheckerDefaults.setDefaultCRLCache (aPreviousCache);
    }
  }

  @Test
  public void testBuilderDefaults () throws Exception
  {
    final KeyPair aCAKeyPair = MockCertificateHelper.createKeyPair ();
    final X509Certificate aCACert = MockCertificateHelper.createSelfSignedCA (aCAKeyPair);

    final TrustedCAChecker aChecker = TrustedCAChecker.builder ().trustedCACertificates (aCACert).build ();
    assertNotNull (aChecker);
    assertNotNull (aChecker.getRevocationCache ());
    // Default is taken from the global defaults
    assertTrue (CertificateRevocationCheckerDefaults.isExecuteInSynchronizedBlock () ==
                aChecker.isSynchronizedRevocationCheck ());
  }

  @Test
  public void testBuilderSynchronizedRevocationCheck () throws Exception
  {
    final KeyPair aCAKeyPair = MockCertificateHelper.createKeyPair ();
    final X509Certificate aCACert = MockCertificateHelper.createSelfSignedCA (aCAKeyPair);

    final TrustedCAChecker aCheckerSync = TrustedCAChecker.builder ()
                                                          .checkMode (ERevocationCheckMode.CRL)
                                                          .cachingDuration (Duration.ofMinutes (1))
                                                          .cacheMaxSize (10)
                                                          .synchronizedRevocationCheck (true)
                                                          .trustedCACertificates (aCACert)
                                                          .build ();
    assertTrue (aCheckerSync.isSynchronizedRevocationCheck ());

    final TrustedCAChecker aCheckerNoSync = TrustedCAChecker.builder ()
                                                            .checkMode (ERevocationCheckMode.CRL)
                                                            .cachingDuration (Duration.ofMinutes (1))
                                                            .cacheMaxSize (10)
                                                            .synchronizedRevocationCheck (false)
                                                            .trustedCACertificates (aCACert)
                                                            .build ();
    assertFalse (aCheckerNoSync.isSynchronizedRevocationCheck ());
  }

  @Test
  public void testBuilderNoCACertificatesFails ()
  {
    try
    {
      TrustedCAChecker.builder ().build ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // Expected, because at least one trusted CA certificate is required
    }
  }

  @Test
  public void testBuilderNegativeCachingDurationFails () throws Exception
  {
    final KeyPair aCAKeyPair = MockCertificateHelper.createKeyPair ();
    final X509Certificate aCACert = MockCertificateHelper.createSelfSignedCA (aCAKeyPair);

    try
    {
      TrustedCAChecker.builder ().cachingDuration (Duration.ofMinutes (-1)).trustedCACertificates (aCACert).build ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // Expected, because the caching duration must not be negative
    }
  }
}
