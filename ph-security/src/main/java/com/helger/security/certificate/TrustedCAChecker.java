/*
 * Copyright (C) 2015-2025 Philip Helger
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

import java.security.cert.X509Certificate;
import java.time.OffsetDateTime;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.state.ETriState;
import com.helger.commons.string.ToStringGenerator;
import com.helger.security.revocation.CertificateRevocationCheckerDefaults;
import com.helger.security.revocation.ERevocationCheckMode;
import com.helger.security.revocation.RevocationCheckBuilder;
import com.helger.security.revocation.RevocationCheckResultCache;

/**
 * This is a specific helper class to check the validity of certificates based on specific trusted
 * CAs. This class assumes the trust model with explicit, non-shared CAs (like in the Peppol
 * Network).
 *
 * @author Philip Helger
 * @since 11.2.1
 */
@NotThreadSafe
public class TrustedCAChecker
{
  private final TrustedCACertificates m_aTrustedCAs = new TrustedCACertificates ();
  private final RevocationCheckResultCache m_aRevocationCache;

  /**
   * Constructor
   *
   * @param aCACerts
   *        The trusted CA certificates to be used. May neither be <code>null</code> nor empty.
   */
  public TrustedCAChecker (@Nonnull final X509Certificate... aCACerts)
  {
    ValueEnforcer.notNullNoNullValue (aCACerts, "CACerts");
    for (final X509Certificate aCACert : aCACerts)
      m_aTrustedCAs.addTrustedCACertificate (aCACert);
    // The cache always uses "now" as the checking date and time
    m_aRevocationCache = new RevocationCheckResultCache (aCert -> new RevocationCheckBuilder ().certificate (aCert)
                                                                                               .validCAs (aCACerts)
                                                                                               .checkMode (CertificateRevocationCheckerDefaults.getRevocationCheckMode ())
                                                                                               .build (),
                                                         CertificateRevocationCheckerDefaults.DEFAULT_REVOCATION_CHECK_CACHING_DURATION);
  }

  /**
   * @return A copy of the trusted CA certificates object used internally. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public TrustedCACertificates getAllTrustedAPCertificates ()
  {
    return new TrustedCACertificates (m_aTrustedCAs);
  }

  /**
   * @return The internal revocation cache that is used. Never <code>null</code>.
   */
  @Nonnull
  public RevocationCheckResultCache getRevocationCache ()
  {
    return m_aRevocationCache;
  }

  /**
   * Check if the provided certificate is a valid Peppol certificate according to the configured CA.
   *
   * @param aCert
   *        The certificate to be checked. May be <code>null</code>.
   * @return {@link ECertificateCheckResult} and never <code>null</code>.
   */
  @Nonnull
  public ECertificateCheckResult checkCertificate (@Nullable final X509Certificate aCert)
  {
    return checkCertificate (aCert, null);
  }

  /**
   * Check if the provided certificate is a valid Peppol certificate according to the configured CA.
   *
   * @param aCert
   *        The certificate to be checked. May be <code>null</code>.
   * @param aCheckDT
   *        The check date and time to use. May be <code>null</code> which means "now".
   * @return {@link ECertificateCheckResult} and never <code>null</code>.
   */
  @Nonnull
  public ECertificateCheckResult checkCertificate (@Nullable final X509Certificate aCert,
                                                   @Nullable final OffsetDateTime aCheckDT)
  {
    return checkCertificate (aCert, aCheckDT, ETriState.UNDEFINED, null);
  }

  /**
   * Check if the provided certificate is a valid Peppol certificate according to the configured CA.
   *
   * @param aCert
   *        The certificate to be checked. May be <code>null</code>.
   * @param aCheckDT
   *        The check date and time to use. May be <code>null</code> which means "now".
   * @param eCacheRevocationCheckResult
   *        Define whether to cache the revocation check results or not. Use
   *        {@link ETriState#UNDEFINED} to solely use the default.
   * @param eCheckMode
   *        Possibility to override the revocation checking mode for each check. May be
   *        <code>null</code> to use the global state from
   *        {@link CertificateRevocationCheckerDefaults#getRevocationCheckMode()}.
   * @return {@link ECertificateCheckResult} and never <code>null</code>.
   */
  @Nonnull
  public ECertificateCheckResult checkCertificate (@Nullable final X509Certificate aCert,
                                                   @Nullable final OffsetDateTime aCheckDT,
                                                   @Nonnull final ETriState eCacheRevocationCheckResult,
                                                   @Nullable final ERevocationCheckMode eCheckMode)
  {
    final boolean bUseRevocationCache = eCacheRevocationCheckResult.isUndefined () ? CertificateRevocationCheckerDefaults.isCacheRevocationCheckResults ()
                                                                                   : eCacheRevocationCheckResult.isTrue ();

    return CertificateHelper.checkCertificate (m_aTrustedCAs.getAllTrustedCAIssuers (),
                                               bUseRevocationCache && aCheckDT == null ? m_aRevocationCache : null,
                                               new RevocationCheckBuilder ().certificate (aCert)
                                                                            .checkDate (aCheckDT)
                                                                            .validCAs (m_aTrustedCAs.getAllTrustedCACertificates ())
                                                                            .checkMode (eCheckMode));
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("TrustedCAs", m_aTrustedCAs)
                                       .append ("RevocationCache", m_aRevocationCache)
                                       .getToString ();
  }
}
