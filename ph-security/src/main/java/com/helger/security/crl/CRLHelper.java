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

import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;

/**
 * Helper class to deal with CRLs.
 *
 * @author Philip Helger
 * @since 11.2.0
 */
@Immutable
public final class CRLHelper
{
  private static final Logger LOGGER = LoggerFactory.getLogger (CRLHelper.class);
  private static final String CRL_DISTRIBUTION_POINTS_OID = "2.5.29.31";

  private CRLHelper ()
  {}

  /**
   * Convert the provided CRL bytes into a {@link X509CRL} object.
   *
   * @param aCRLBytes
   *        The CRL bytes received from an external source. May neither be
   *        <code>null</code> nor empty.
   * @return The parsed CRL object.
   * @throws IllegalArgumentException
   *         In case of conversion errors
   */
  @NonNull
  public static X509CRL convertToCRL (final byte @NonNull @Nonempty [] aCRLBytes)
  {
    ValueEnforcer.notEmpty (aCRLBytes, "CRLBytes");

    try (final NonBlockingByteArrayInputStream aIS = new NonBlockingByteArrayInputStream (aCRLBytes))
    {
      final CertificateFactory aCertFactory = CertificateFactory.getInstance ("X.509");
      return (X509CRL) aCertFactory.generateCRL (aIS);
    }
    catch (final CertificateException ex)
    {
      throw new IllegalArgumentException (ex);
    }
    catch (final CRLException ex)
    {
      throw new IllegalArgumentException ("Cannot generate X.509 CRL from the stream data", ex);
    }
  }

  /**
   * Extracts all CRL distribution point URLs from the "CRL Distribution Point"
   * extension in a X.509 certificate. If CRL distribution point extension is
   * unavailable, returns an empty list.
   *
   * @param aCert
   *        The certificate to extract the CRLs from
   * @return Never <code>null</code> but maybe empty list of distribution
   *         points.
   * @throws java.io.UncheckedIOException
   *         If the certificate contains a "CRL Distribution Points" extension
   *         that is not valid DER. Up to and including v12.3.5 this was decoded
   *         by BouncyCastle, which also accepted certain BER encodings and
   *         reported structural problems as
   *         {@link IllegalArgumentException} instead.
   */
  @NonNull
  public static ICommonsList <String> getAllDistributionPoints (@NonNull final X509Certificate aCert)
  {
    ValueEnforcer.notNull (aCert, "Certificate");
    final byte [] aExtensionValue = aCert.getExtensionValue (CRL_DISTRIBUTION_POINTS_OID);
    if (aExtensionValue == null)
      return new CommonsArrayList <> ();

    final ICommonsList <String> ret = CRLDistributionPointParser.parse (aExtensionValue);
    if (LOGGER.isDebugEnabled ())
      for (final String sURL : ret)
        LOGGER.debug ("Found CRL URL '" + sURL + "' in certificate");
    return ret;
  }
}
