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
import static org.junit.Assert.assertSame;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.cert.CertPathValidatorException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.helger.security.certificate.MockCertificateHelper;

/**
 * Test class verifying that the PKIX revocation checker options derived from
 * {@link ERevocationCheckMode} and the soft fail setting are actually applied by
 * {@link AbstractRevocationCheckBuilder#build()}.
 *
 * @author GT
 */
public final class RevocationCheckBuilderOptionsTest
{
  private ServerSocket m_aOCSPServer;
  private final AtomicInteger m_aOCSPRequests = new AtomicInteger (0);
  private String m_sOCSPURL;

  @Before
  public void startOCSPResponder () throws Exception
  {
    // An OCSP responder that counts the connections and closes them without answering
    m_aOCSPServer = new ServerSocket (0, 50, InetAddress.getLoopbackAddress ());
    final Thread aThread = new Thread ( () -> {
      while (!m_aOCSPServer.isClosed ())
        try (final Socket aSocket = m_aOCSPServer.accept ())
        {
          m_aOCSPRequests.incrementAndGet ();
        }
        catch (final IOException ex)
        {
          // Server socket was closed
        }
    }, "mock-ocsp-responder");
    aThread.setDaemon (true);
    aThread.start ();
    m_sOCSPURL = "http://127.0.0.1:" + m_aOCSPServer.getLocalPort () + "/ocsp";
  }

  @After
  public void stopOCSPResponder () throws IOException
  {
    m_aOCSPServer.close ();
  }

  @Test
  public void testCRLBeforeOCSPUsesCRLWithoutOCSP () throws Exception
  {
    final KeyPair aCAKeyPair = MockCertificateHelper.createKeyPair ();
    final X509Certificate aCACert = MockCertificateHelper.createSelfSignedCA (aCAKeyPair);
    final X509Certificate aEECert = MockCertificateHelper.createEndEntityWithCRLDPAndOCSP (aCACert,
                                                                                           aCAKeyPair.getPrivate (),
                                                                                           MockCertificateHelper.createKeyPair ()
                                                                                                                .getPublic (),
                                                                                           "http://crl.example.org/ca.crl",
                                                                                           m_sOCSPURL);

    final ERevoked eResult = new RevocationCheckBuilder ().certificate (aEECert)
                                                          .validCA (aCACert)
                                                          .checkMode (ERevocationCheckMode.CRL_BEFORE_OCSP)
                                                          .allowSoftFail (false)
                                                          .crlCache (MockCertificateHelper.createFixedCRLCache (MockCertificateHelper.createCRL (aCACert,
                                                                                                                                                 aCAKeyPair.getPrivate ())))
                                                          .build ();
    assertSame (ERevoked.NOT_REVOKED, eResult);
    assertEquals ("A valid CRL is available, so OCSP must not be contacted", 0, m_aOCSPRequests.get ());
  }

  @Test
  public void testCRLBeforeOCSPDetectsRevocationWithoutOCSP () throws Exception
  {
    final KeyPair aCAKeyPair = MockCertificateHelper.createKeyPair ();
    final X509Certificate aCACert = MockCertificateHelper.createSelfSignedCA (aCAKeyPair);
    final X509Certificate aEECert = MockCertificateHelper.createEndEntityWithCRLDPAndOCSP (aCACert,
                                                                                           aCAKeyPair.getPrivate (),
                                                                                           MockCertificateHelper.createKeyPair ()
                                                                                                                .getPublic (),
                                                                                           "http://crl.example.org/ca.crl",
                                                                                           m_sOCSPURL);

    final ERevoked eResult = new RevocationCheckBuilder ().certificate (aEECert)
                                                          .validCA (aCACert)
                                                          .checkMode (ERevocationCheckMode.CRL_BEFORE_OCSP)
                                                          .allowSoftFail (false)
                                                          .crlCache (MockCertificateHelper.createFixedCRLCache (MockCertificateHelper.createCRL (aCACert,
                                                                                                                                                 aCAKeyPair.getPrivate (),
                                                                                                                                                 aEECert)))
                                                          .exceptionHandler (ex -> {
                                                            // swallow - we only care about the
                                                            // result
                                                          })
                                                          .build ();
    assertSame (ERevoked.REVOKED, eResult);
    assertEquals ("The CRL lists the certificate, so OCSP must not be contacted", 0, m_aOCSPRequests.get ());
  }

  @Test
  public void testOCSPSoftFail () throws Exception
  {
    final KeyPair aCAKeyPair = MockCertificateHelper.createKeyPair ();
    final X509Certificate aCACert = MockCertificateHelper.createSelfSignedCA (aCAKeyPair);
    final X509Certificate aEECert = MockCertificateHelper.createEndEntityWithCRLDPAndOCSP (aCACert,
                                                                                           aCAKeyPair.getPrivate (),
                                                                                           MockCertificateHelper.createKeyPair ()
                                                                                                                .getPublic (),
                                                                                           "http://crl.example.org/ca.crl",
                                                                                           m_sOCSPURL);

    // The OCSP responder fails, and soft fail is explicitly allowed
    final AtomicReference <List <? extends CertPathValidatorException>> aSoftFailures = new AtomicReference <> ();
    final ERevoked eResult = new RevocationCheckBuilder ().certificate (aEECert)
                                                          .validCA (aCACert)
                                                          .checkMode (ERevocationCheckMode.OCSP)
                                                          .allowSoftFail (true)
                                                          .softFailExceptionHandler (aSoftFailures::set)
                                                          .build ();
    assertSame (ERevoked.NOT_REVOKED, eResult);
    assertFalse ("The OCSP responder must have been contacted", m_aOCSPRequests.get () == 0);
    assertNotNull ("The soft failure must be reported", aSoftFailures.get ());
    assertFalse (aSoftFailures.get ().isEmpty ());
  }
}
