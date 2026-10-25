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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.cert.CertPathValidatorException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.jspecify.annotations.NonNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.wrapper.Wrapper;
import com.helger.security.certificate.MockCertificateHelper;
import com.helger.security.crl.CRLCache;

/**
 * Test class verifying that the PKIX revocation checker options derived from
 * {@link ERevocationCheckMode} and the soft fail setting are actually applied by
 * {@link AbstractRevocationCheckBuilder#build()}.
 *
 * @author GT
 */
public final class RevocationCheckBuilderOptionsTest
{
  /**
   * A local endpoint that counts the incoming connections and closes them without answering. Used
   * as OCSP responder and as CRL distribution point, so that every network access of the JDK is
   * visible to the test.
   */
  private static final class CountingServer implements AutoCloseable
  {
    private static final Logger LOGGER = LoggerFactory.getLogger (RevocationCheckBuilderOptionsTest.CountingServer.class);

    private final ServerSocket m_aServerSocket;
    private final AtomicInteger m_aRequests = new AtomicInteger (0);

    CountingServer (@NonNull final String sName) throws IOException
    {
      m_aServerSocket = new ServerSocket (0, 50, InetAddress.getLoopbackAddress ());
      final Thread aThread = new Thread (() -> {
        while (!m_aServerSocket.isClosed ())
          try (final Socket aSocket = m_aServerSocket.accept ())
          {
            LOGGER.info ("Request at " + sName);
            m_aRequests.incrementAndGet ();
          }
          catch (final IOException ex)
          {
            // Server socket was closed
          }
      }, sName);
      aThread.setDaemon (true);
      aThread.start ();
    }

    @NonNull
    String getURL (@NonNull final String sPath)
    {
      return "http://127.0.0.1:" + m_aServerSocket.getLocalPort () + sPath;
    }

    int getRequestCount ()
    {
      return m_aRequests.get ();
    }

    public void close () throws IOException
    {
      m_aServerSocket.close ();
    }
  }

  private CountingServer m_aOCSP;
  private CountingServer m_aCRLDP;
  private KeyPair m_aCAKeyPair;
  private X509Certificate m_aCACert;
  private X509Certificate m_aEECert;

  @Before
  public void setUp () throws Exception
  {
    m_aOCSP = new CountingServer ("mock-ocsp-responder");
    m_aCRLDP = new CountingServer ("mock-crl-dp");
    m_aCAKeyPair = MockCertificateHelper.createKeyPair ();
    m_aCACert = MockCertificateHelper.createSelfSignedCA (m_aCAKeyPair);
    m_aEECert = MockCertificateHelper.createEndEntityWithCRLDPAndOCSP (m_aCACert,
                                                                       m_aCAKeyPair.getPrivate (),
                                                                       MockCertificateHelper.createKeyPair ()
                                                                                            .getPublic (),
                                                                       m_aCRLDP.getURL ("/ca.crl"),
                                                                       m_aOCSP.getURL ("/ocsp"));
  }

  @After
  public void tearDown () throws IOException
  {
    m_aOCSP.close ();
    m_aCRLDP.close ();
  }

  @NonNull
  private CRLCache _createValidCRLCache () throws Exception
  {
    return MockCertificateHelper.createFixedCRLCache (MockCertificateHelper.createCRL (m_aCACert,
                                                                                       m_aCAKeyPair.getPrivate ()));
  }

  @NonNull
  private CRLCache _createRevokingCRLCache () throws Exception
  {
    return MockCertificateHelper.createFixedCRLCache (MockCertificateHelper.createCRL (m_aCACert,
                                                                                       m_aCAKeyPair.getPrivate (),
                                                                                       m_aEECert));
  }

  @NonNull
  private RevocationCheckBuilder _builder (@NonNull final ERevocationCheckMode eMode, final boolean bAllowSoftFail)
  {
    return new RevocationCheckBuilder ().certificate (m_aEECert)
                                        .validCA (m_aCACert)
                                        .checkMode (eMode)
                                        .allowSoftFail (bAllowSoftFail)
                                        .exceptionHandler (ex -> {
                                          // swallow - we only care about the result
                                        });
  }

  @Test
  public void testCRLBeforeOCSPUsesCRLWithoutOCSP () throws Exception
  {
    final ERevoked eResult = _builder (ERevocationCheckMode.CRL_BEFORE_OCSP, false).crlCache (_createValidCRLCache ())
                                                                                   .build ();
    assertSame (ERevoked.NOT_REVOKED, eResult);
    // Because a constant CRL is provided without download
    assertEquals (0, m_aCRLDP.getRequestCount ());
    assertEquals ("A valid CRL is available, so OCSP must not be contacted", 0, m_aOCSP.getRequestCount ());
    assertEquals ("The CRL comes from the cache, so the JDK must not download it itself",
                  0,
                  m_aCRLDP.getRequestCount ());
  }

  @Test
  public void testCRLBeforeOCSPDetectsRevocationWithoutOCSP () throws Exception
  {
    final ERevoked eResult = _builder (ERevocationCheckMode.CRL_BEFORE_OCSP, false).crlCache (
                                                                                              _createRevokingCRLCache ())
                                                                                   .build ();
    assertSame (ERevoked.REVOKED, eResult);
    // Because a constant CRL is provided without download
    assertEquals (0, m_aCRLDP.getRequestCount ());
    assertEquals ("The CRL lists the certificate, so OCSP must not be contacted", 0, m_aOCSP.getRequestCount ());
  }

  @Test
  public void testCRLBeforeOCSPFallsBackToOCSP () throws Exception
  {
    // No CRL can be obtained, so the check must continue with OCSP (which fails as well)
    final ERevoked eResult = _builder (ERevocationCheckMode.CRL_BEFORE_OCSP, false).crlCache (MockCertificateHelper
                                                                                                                   .createAlwaysFailingCRLCache ())
                                                                                   .build ();
    assertNotSame (ERevoked.NOT_REVOKED, eResult);
    // If no CRL could be determined from the cache a separate download is tried
    assertTrue (m_aCRLDP.getRequestCount () > 0);
    assertTrue ("OCSP must be used as the fallback", m_aOCSP.getRequestCount () > 0);
  }

  @Test
  public void testCRLDoesNotUseOCSP () throws Exception
  {
    ERevoked eResult = _builder (ERevocationCheckMode.CRL, false).crlCache (_createValidCRLCache ()).build ();
    assertSame (ERevoked.NOT_REVOKED, eResult);

    eResult = _builder (ERevocationCheckMode.CRL, false).crlCache (_createRevokingCRLCache ()).build ();
    assertSame (ERevoked.REVOKED, eResult);

    // No CRL available - must not fall back to OCSP
    eResult = _builder (ERevocationCheckMode.CRL, false).crlCache (MockCertificateHelper.createAlwaysFailingCRLCache ())
                                                        .build ();
    assertSame (ERevoked.UNKNOWN, eResult);

    assertEquals ("Mode CRL must never contact OCSP", 0, m_aOCSP.getRequestCount ());
  }

  @Test
  public void testCRLDownloadFailureContactsDistributionPointDirectly () throws Exception
  {
    // If the CRL cache has nothing, the JDK revocation checker downloads the CRL from the
    // distribution point itself (bypassing the CRLCache and its downloader)
    final ERevoked eResult = _builder (ERevocationCheckMode.CRL, false).crlCache (MockCertificateHelper
                                                                                                       .createAlwaysFailingCRLCache ())
                                                                       .build ();
    assertSame (ERevoked.UNKNOWN, eResult);
    assertTrue ("The JDK is expected to access the CRL distribution point itself", m_aCRLDP.getRequestCount () > 0);
  }

  @Test
  public void testOCSPDoesNotFallBackToCRL () throws Exception
  {
    // The CRL would say "not revoked", but mode OCSP must not use it
    final ERevoked eResult = _builder (ERevocationCheckMode.OCSP, false).crlCache (_createValidCRLCache ()).build ();
    assertNotSame (ERevoked.NOT_REVOKED, eResult);
    assertTrue ("The OCSP responder must have been contacted", m_aOCSP.getRequestCount () > 0);
    assertEquals ("Mode OCSP must not download a CRL", 0, m_aCRLDP.getRequestCount ());
  }

  @Test
  public void testOCSPBeforeCRLFallsBackToCRL () throws Exception
  {
    ERevoked eResult = _builder (ERevocationCheckMode.OCSP_BEFORE_CRL, false).crlCache (_createValidCRLCache ())
                                                                             .build ();
    assertSame (ERevoked.NOT_REVOKED, eResult);
    assertTrue ("OCSP must be tried first", m_aOCSP.getRequestCount () > 0);

    eResult = _builder (ERevocationCheckMode.OCSP_BEFORE_CRL, false).crlCache (_createRevokingCRLCache ()).build ();
    assertSame (ERevoked.REVOKED, eResult);
  }

  @Test
  public void testOCSPSoftFail () throws Exception
  {
    // The OCSP responder fails, and soft fail is explicitly allowed
    final Wrapper <List <? extends CertPathValidatorException>> aSoftFailures = new Wrapper <> ();
    final ERevoked eResult = _builder (ERevocationCheckMode.OCSP, true).softFailExceptionHandler (aSoftFailures::set)
                                                                       .build ();
    assertSame (ERevoked.NOT_REVOKED, eResult);
    assertTrue ("The OCSP responder must have been contacted", m_aOCSP.getRequestCount () > 0);
    assertNotNull ("The soft failure must be reported", aSoftFailures.get ());
    assertFalse (aSoftFailures.get ().isEmpty ());
  }

  @Test
  public void testOCSPNoSoftFail () throws Exception
  {
    // The OCSP responder fails, and soft fail is not allowed
    final Wrapper <List <? extends CertPathValidatorException>> aSoftFailures = new Wrapper <> ();
    final ERevoked eResult = _builder (ERevocationCheckMode.OCSP, false).softFailExceptionHandler (aSoftFailures::set)
                                                                        .build ();
    assertNotSame (ERevoked.NOT_REVOKED, eResult);
    assertNull ("Without soft fail, no soft failure must be reported", aSoftFailures.get ());
  }
}
