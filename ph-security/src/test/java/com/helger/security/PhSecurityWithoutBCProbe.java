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
package com.helger.security;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

import com.helger.collection.commons.ICommonsList;
import com.helger.security.certificate.CertificateHelper;
import com.helger.security.certificate.TrustedCACertificates;
import com.helger.security.crl.CRLHelper;
import com.helger.security.oscp.EOCSPResponseStatus;

/** Invoked through an isolated class loader by {@link PhSecurityWithoutBCTest}. */
public final class PhSecurityWithoutBCProbe
{
  private PhSecurityWithoutBCProbe ()
  {}

  public static String verify () throws Exception
  {
    final KeyStore aAPKeyStore = KeyStore.getInstance ("PKCS12");
    try (final FileInputStream aIS = new FileInputStream ("src/test/resources/keystores/keystore-pw-peppol-expired-2023.p12"))
    {
      aAPKeyStore.load (aIS, "peppol".toCharArray ());
    }
    final X509Certificate aAPCert = (X509Certificate) aAPKeyStore.getCertificate (aAPKeyStore.aliases ().nextElement ());
    if (CertificateHelper.isCA (aAPCert))
      throw new IllegalStateException ("The AP certificate must not be treated as a CA");

    final ICommonsList <String> aCRLURLs = CRLHelper.getAllDistributionPoints (aAPCert);
    if (aCRLURLs.size () != 1)
      throw new IllegalStateException ("Expected one CRL distribution point but found " + aCRLURLs.size ());

    final KeyStore aTrustStore = KeyStore.getInstance ("JKS");
    try (final FileInputStream aIS = new FileInputStream ("src/test/resources/keystores/truststore-peppol-prod.jks"))
    {
      aTrustStore.load (aIS, "peppol".toCharArray ());
    }
    final X509Certificate aCACert = (X509Certificate) aTrustStore.getCertificate (aTrustStore.aliases ().nextElement ());
    final TrustedCACertificates aTrustedCAs = new TrustedCACertificates ();
    aTrustedCAs.addTrustedCACertificate (aCACert);

    return aCRLURLs.get (0) +
           '|' +
           aTrustedCAs.getAllTrustedCACertificates ().size () +
           '|' +
           EOCSPResponseStatus.SUCCESSFUL.getID () +
           ',' +
           EOCSPResponseStatus.UNAUTHORIZED.getID ();
  }
}
