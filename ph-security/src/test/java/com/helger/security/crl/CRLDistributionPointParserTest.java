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
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.jspecify.annotations.NonNull;
import org.junit.Test;

import com.helger.collection.commons.ICommonsList;

/**
 * Test class for class CRLDistributionPointParser
 *
 * @author Philip Helger
 */
public final class CRLDistributionPointParserTest
{
  @Test
  public void testNameRelativeToCRLIssuerIsIgnored () throws IOException
  {
    final RDN aRDN = new RDN (BCStyle.CN, new DERUTF8String ("Example CA"));
    final DistributionPoint aDP = new DistributionPoint (new DistributionPointName (DistributionPointName.NAME_RELATIVE_TO_CRL_ISSUER,
                                                                                    aRDN), null, null);
    final byte [] aEncodedExtension = new DEROctetString (new CRLDistPoint (new DistributionPoint [] { aDP }).getEncoded ()).getEncoded ();

    assertTrue (CRLDistributionPointParser.parse (aEncodedExtension).isEmpty ());
  }

  @Test
  public void testEmptyDistributionPointsSequence () throws IOException
  {
    // Not valid according to RFC 5280 (SIZE 1..MAX) but must not blow up
    final byte [] aEncodedExtension = new DEROctetString (new DERSequence ().getEncoded ()).getEncoded ();

    assertTrue (CRLDistributionPointParser.parse (aEncodedExtension).isEmpty ());
  }

  @NonNull
  private static DistributionPoint _createURIDistributionPoint (@NonNull final String sURI)
  {
    final GeneralNames aFullNames = new GeneralNames (new GeneralName (GeneralName.uniformResourceIdentifier, sURI));
    return new DistributionPoint (new DistributionPointName (DistributionPointName.FULL_NAME, aFullNames), null, null);
  }

  @Test
  public void testRejectsTruncatedDER ()
  {
    assertThrows (UncheckedIOException.class,
                  () -> CRLDistributionPointParser.parse (new byte [] { 0x04, (byte) 0x82, 0x01 }));
  }

  @Test
  public void testRejectsTrailingData () throws IOException
  {
    final byte [] aValid = new DEROctetString (new CRLDistPoint (new DistributionPoint [] { _createURIDistributionPoint ("https://crl.example.org/one.crl") }).getEncoded ()).getEncoded ();
    final byte [] aWithTrailingData = new byte [aValid.length + 1];
    System.arraycopy (aValid, 0, aWithTrailingData, 0, aValid.length);

    assertEquals (1, CRLDistributionPointParser.parse (aValid).size ());
    assertThrows (UncheckedIOException.class, () -> CRLDistributionPointParser.parse (aWithTrailingData));
  }

  @Test
  public void testMultipleDistributionPointsAndGeneralNames () throws IOException
  {
    final GeneralNames aFullNames = new GeneralNames (new GeneralName [] { new GeneralName (GeneralName.dNSName,
                                                                                            "crl.example.org"),
                                                                           new GeneralName (GeneralName.uniformResourceIdentifier,
                                                                                            "https://crl.example.org/one.crl"),
                                                                           new GeneralName (GeneralName.uniformResourceIdentifier,
                                                                                            "ldap://crl.example.org/two") });
    final DistributionPoint aNamedDistributionPoint = new DistributionPoint (new DistributionPointName (DistributionPointName.FULL_NAME,
                                                                                                        aFullNames),
                                                                             null,
                                                                             null);
    final DistributionPoint aIssuerOnlyDistributionPoint = new DistributionPoint (null,
                                                                                  null,
                                                                                  new GeneralNames (new GeneralName (GeneralName.uniformResourceIdentifier,
                                                                                                                     "https://issuer.example.org/not-a-distribution-point.crl")));
    final byte [] aEncodedExtension = new DEROctetString (new CRLDistPoint (new DistributionPoint [] { aNamedDistributionPoint,
                                                                                                       aIssuerOnlyDistributionPoint }).getEncoded ()).getEncoded ();

    final ICommonsList <String> aURLs = CRLDistributionPointParser.parse (aEncodedExtension);
    assertEquals (2, aURLs.size ());
    assertEquals ("https://crl.example.org/one.crl", aURLs.get (0));
    assertEquals ("ldap://crl.example.org/two", aURLs.get (1));
  }
}
