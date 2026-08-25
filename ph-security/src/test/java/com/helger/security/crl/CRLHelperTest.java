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
package com.helger.security.crl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.junit.Test;

import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.security.keystore.EKeyStoreType;
import com.helger.security.keystore.KeyStoreHelper;

/**
 * Test class for class {@link CRLHelper}.
 *
 * @author Philip Helger
 */
public final class CRLHelperTest
{
  /**
   * The reference implementation that was used up to and including v12.3.5. It is kept here to
   * ensure that the BouncyCastle free {@link CRLDistributionPointParser} returns exactly the same
   * results for all available test certificates.
   *
   * @param aCert
   *        The certificate to extract the CRLs from
   * @return Never <code>null</code> but maybe empty list of distribution points.
   */
  private static ICommonsList <String> _getAllDistributionPointsViaBC (@NonNull final X509Certificate aCert)
  {
    final ICommonsList <String> ret = new CommonsArrayList <> ();
    final byte [] aExtensionValue = aCert.getExtensionValue (Extension.cRLDistributionPoints.getId ());
    if (aExtensionValue != null)
      try (final ASN1InputStream aAsn1IS = new ASN1InputStream (aExtensionValue))
      {
        final DEROctetString aCrlDEROctetString = (DEROctetString) aAsn1IS.readObject ();
        final CRLDistPoint aDistPoint;
        try (final ASN1InputStream aAsn1InOctets = new ASN1InputStream (aCrlDEROctetString.getOctets ()))
        {
          final ASN1Primitive aCrlDERObject = aAsn1InOctets.readObject ();
          aDistPoint = CRLDistPoint.getInstance (aCrlDERObject);
        }

        for (final DistributionPoint aDP : aDistPoint.getDistributionPoints ())
        {
          final DistributionPointName aDPName = aDP.getDistributionPoint ();
          if (aDPName != null && aDPName.getType () == DistributionPointName.FULL_NAME)
            for (final GeneralName aGenName : GeneralNames.getInstance (aDPName.getName ()).getNames ())
              if (aGenName.getTagNo () == GeneralName.uniformResourceIdentifier)
                ret.add (ASN1IA5String.getInstance (aGenName.getName ()).getString ().trim ());
        }
      }
      catch (final IOException ex)
      {
        throw new UncheckedIOException (ex);
      }
    return ret;
  }

  private static void _collectAllCertificates (@NonNull final EKeyStoreType eType,
                                               @Nullable final String sPath,
                                               final char @Nullable [] aPassword,
                                               @NonNull final ICommonsList <@NonNull X509Certificate> aTarget) throws Exception
  {
    final KeyStore aKS = KeyStoreHelper.loadKeyStoreDirect (eType, sPath, aPassword);
    assertNotNull (aKS);

    final Enumeration <String> aAliases = aKS.aliases ();
    while (aAliases.hasMoreElements ())
    {
      final String sAlias = aAliases.nextElement ();

      final Certificate aCert = aKS.getCertificate (sAlias);
      if (aCert instanceof final X509Certificate aX509Cert)
        aTarget.add (aX509Cert);

      final Certificate [] aChain = aKS.getCertificateChain (sAlias);
      if (aChain != null)
        for (final Certificate aChainCert : aChain)
          if (aChainCert instanceof final X509Certificate aX509Cert)
            aTarget.add (aX509Cert);
    }
  }

  @Test
  public void testGetAllDistributionPoints () throws KeyStoreException
  {
    final File fAP = new File ("src/test/resources/keystores/keystore-pw-peppol-expired-2023.p12");

    final KeyStore aKS = KeyStoreHelper.loadKeyStore (EKeyStoreType.PKCS12,
                                                      fAP.getAbsolutePath (),
                                                      "peppol".toCharArray ()).getKeyStore ();
    assertNotNull (aKS);

    final X509Certificate aCert = (X509Certificate) aKS.getCertificate (aKS.aliases ().nextElement ());
    assertNotNull (aCert);

    final ICommonsList <String> aList = CRLHelper.getAllDistributionPoints (aCert);
    assertNotNull (aList);
    assertEquals (1, aList.size ());
    assertEquals ("http://pki-crl.symauth.com/ca_6a937734a393a0805bf33cda8b331093/LatestCRL.crl", aList.get (0));
  }

  /**
   * Differential test: for every certificate available in the test resources the new parser must
   * return exactly what the previous BouncyCastle based implementation returned.
   *
   * @throws Exception
   *         in case of error
   */
  @Test
  public void testSameResultAsBouncyCastleForAllTestCertificates () throws Exception
  {
    final ICommonsList <X509Certificate> aAllCerts = new CommonsArrayList <> ();
    _collectAllCertificates (EKeyStoreType.PKCS12,
                             "keystores/keystore-pw-peppol-expired-2023.p12",
                             "peppol".toCharArray (),
                             aAllCerts);
    _collectAllCertificates (EKeyStoreType.JKS, "keystores/keystore-pw-peppol.jks", "peppol".toCharArray (), aAllCerts);
    _collectAllCertificates (EKeyStoreType.JKS, "keystores/keystore-no-pw.jks", null, aAllCerts);
    _collectAllCertificates (EKeyStoreType.JKS,
                             "keystores/truststore-peppol-prod.jks",
                             "peppol".toCharArray (),
                             aAllCerts);
    _collectAllCertificates (EKeyStoreType.JKS,
                             "keystores/truststore-peppol-pilot.jks",
                             "peppol".toCharArray (),
                             aAllCerts);
    assertTrue ("No test certificates found at all", aAllCerts.size () >= 10);

    int nCertsWithDistributionPoints = 0;
    for (final X509Certificate aCert : aAllCerts)
    {
      final ICommonsList <String> aExpected = _getAllDistributionPointsViaBC (aCert);
      final ICommonsList <String> aActual = CRLHelper.getAllDistributionPoints (aCert);
      assertEquals ("Mismatch for certificate '" + aCert.getSubjectX500Principal ().getName () + "'",
                    aExpected,
                    aActual);
      if (aActual.isNotEmpty ())
        nCertsWithDistributionPoints++;
    }
    // Make sure the comparison above was not vacuous
    assertTrue ("Not a single test certificate contains CRL distribution points", nCertsWithDistributionPoints > 0);
  }
}
