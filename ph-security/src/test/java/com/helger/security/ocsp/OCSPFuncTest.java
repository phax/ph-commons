/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.security.ocsp;

import java.math.BigInteger;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.ocsp.OCSPResponseStatus;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cert.ocsp.CertificateStatus;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPReq;
import org.bouncycastle.cert.ocsp.OCSPReqBuilder;
import org.bouncycastle.cert.ocsp.OCSPResp;
import org.bouncycastle.cert.ocsp.RevokedStatus;
import org.bouncycastle.cert.ocsp.SingleResp;
import org.bouncycastle.cert.ocsp.jcajce.JcaCertificateID;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

import com.helger.bc.PBCProvider;
import com.helger.commons.id.IHasIntID;
import com.helger.commons.lang.EnumHelper;
import com.helger.commons.state.ETriState;
import com.helger.commons.state.ISuccessIndicator;

public final class OCSPFuncTest
{
  @Nonnull
  public static OCSPReq generateOCSPRequest (final X509Certificate aIssuerCert,
                                             final BigInteger aCheckSerialNumber) throws OCSPException
  {
    try
    {
      final DigestCalculatorProvider aDigestCalculatorProvider = new JcaDigestCalculatorProviderBuilder ().setProvider (PBCProvider.getProvider ())
                                                                                                          .build ();
      final DigestCalculator aDigestCalculator = aDigestCalculatorProvider.get (CertificateID.HASH_SHA1);

      // CertID structure is used to uniquely identify certificates that are the
      // subject of an OCSP request or response and has an ASN.1 definition.
      // CertID structure is defined in RFC 2560
      final CertificateID aCertificateID = new JcaCertificateID (aDigestCalculator, aIssuerCert, aCheckSerialNumber);

      // create details for nonce extension. The nonce extension is used to bind
      // a request to a response to prevent replay attacks. As the name implies,
      // the nonce value is something that the client should only use once
      // within a reasonably small period.
      final BigInteger aNonce = BigInteger.valueOf (System.nanoTime ());

      // to create the request Extension
      final Extensions aExtensions = new Extensions (new Extension (OCSPObjectIdentifiers.id_pkix_ocsp_nonce,
                                                                    false,
                                                                    new DEROctetString (aNonce.toByteArray ())));

      // basic request generation with nonce
      final OCSPReqBuilder aBuilder = new OCSPReqBuilder ();
      aBuilder.addRequest (aCertificateID);
      // Extension to the whole request
      aBuilder.setRequestExtensions (aExtensions);
      return aBuilder.build ();
    }
    catch (final OperatorCreationException | CertificateEncodingException ex)
    {
      throw new IllegalStateException (ex);
    }
  }

  public static enum EOCSPResponseStatus implements ISuccessIndicator, IHasIntID
  {
    SUCCESSFUL (OCSPResponseStatus.SUCCESSFUL),
    MALFORMED_REQUEST (OCSPResponseStatus.MALFORMED_REQUEST),
    INTERNAL_ERROR (OCSPResponseStatus.INTERNAL_ERROR),
    TRY_LATER (OCSPResponseStatus.TRY_LATER),
    SIG_REQUIRED (OCSPResponseStatus.SIG_REQUIRED),
    UNAUTHORIZED (OCSPResponseStatus.UNAUTHORIZED);

    private final int m_nValue;

    private EOCSPResponseStatus (final int nValue)
    {
      m_nValue = nValue;
    }

    public int getID ()
    {
      return m_nValue;
    }

    public boolean isSuccess ()
    {
      return this == SUCCESSFUL;
    }

    @Nullable
    public static EOCSPResponseStatus getFromValueOrNull (final int nID)
    {
      return EnumHelper.getFromIDOrNull (EOCSPResponseStatus.class, nID);
    }
  }

  @Nonnull
  public static ETriState evalOCSPResponse (@Nonnull final OCSPResp aOCSPResponse) throws OCSPException
  {
    final EOCSPResponseStatus eStatus = EOCSPResponseStatus.getFromValueOrNull (aOCSPResponse.getStatus ());
    if (eStatus == null)
      throw new OCSPException ("Unsupported status code " + aOCSPResponse.getStatus () + " received!");
    if (eStatus.isFailure ())
      throw new OCSPException ("Non-success status code " + aOCSPResponse.getStatus () + " received!");

    final Object aResponseObject = aOCSPResponse.getResponseObject ();
    if (aResponseObject instanceof BasicOCSPResp)
    {
      final BasicOCSPResp aBasicResponse = (BasicOCSPResp) aResponseObject;
      final SingleResp [] aResponses = aBasicResponse.getResponses ();
      // Assume we queried only one
      if (aResponses.length == 1)
      {
        final SingleResp aResponse = aResponses[0];
        final CertificateStatus aStatus = aResponse.getCertStatus ();
        if (aStatus == CertificateStatus.GOOD)
          return ETriState.TRUE;
        if (aStatus instanceof RevokedStatus)
          return ETriState.FALSE;
        // else status is unknown
      }
    }
    return ETriState.UNDEFINED;
  }
}
