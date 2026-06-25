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

import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Arrays;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.CheckForSigned;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.builder.IBuilder;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.state.ETriState;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
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
  private final boolean m_bSynchronizedRevocationCheck;

  /**
   * Constructor
   *
   * @param aCACerts
   *        The trusted CA certificates to be used. May neither be <code>null</code> nor empty.
   * @deprecated Use {@link #builder()} instead.
   */
  @Deprecated (forRemoval = true, since = "12.3.1")
  public TrustedCAChecker (@NonNull final X509Certificate... aCACerts)
  {
    this (CertificateRevocationCheckerDefaults.getRevocationCheckMode (),
          CertificateRevocationCheckerDefaults.DEFAULT_REVOCATION_CHECK_CACHING_DURATION,
          RevocationCheckResultCache.DEFAULT_MAX_SIZE,
          CertificateRevocationCheckerDefaults.isExecuteInSynchronizedBlock (),
          Arrays.asList (aCACerts));
  }

  /**
   * @param eCheckMode
   *        The revocation check mode to use. May not be <code>null</code>.
   * @param aCachingDuration
   *        The caching duration until the next revocation check needs to be performed. May not be
   *        <code>null</code>.
   * @param nCacheMaxSize
   *        The maximum cache size for the revocation check. Values &le; 0 means no maximum size.
   * @param aCACerts
   *        The trusted CA certificates to be used. May neither be <code>null</code> nor empty.
   * @since 12.1.4
   * @deprecated Use {@link #builder()} instead.
   */
  @Deprecated (forRemoval = true, since = "12.3.1")
  public TrustedCAChecker (@NonNull final ERevocationCheckMode eCheckMode,
                           @NonNull final Duration aCachingDuration,
                           @CheckForSigned final int nCacheMaxSize,
                           @NonNull final X509Certificate... aCACerts)
  {
    this (eCheckMode,
          aCachingDuration,
          nCacheMaxSize,
          CertificateRevocationCheckerDefaults.isExecuteInSynchronizedBlock (),
          Arrays.asList (aCACerts));
  }

  /**
   * @param eCheckMode
   *        The revocation check mode to use. May not be <code>null</code>.
   * @param aCachingDuration
   *        The caching duration until the next revocation check needs to be performed. May not be
   *        <code>null</code>.
   * @param nCacheMaxSize
   *        The maximum cache size for the revocation check. Values &le; 0 means no maximum size.
   * @param bSynchronizedRevocationCheck
   *        <code>true</code> to perform the revocation check in a <code>synchronized</code> block,
   *        <code>false</code> to run it unsynchronized.
   * @param aCACerts
   *        The trusted CA certificates to be used. May neither be <code>null</code> nor empty.
   * @since 12.3.1
   */
  private TrustedCAChecker (@NonNull final ERevocationCheckMode eCheckMode,
                            @NonNull final Duration aCachingDuration,
                            @CheckForSigned final int nCacheMaxSize,
                            final boolean bSynchronizedRevocationCheck,
                            @NonNull final Iterable <X509Certificate> aCACerts)
  {
    ValueEnforcer.notNull (eCheckMode, "CheckMode");
    ValueEnforcer.notNull (aCachingDuration, "CachingDuration");
    ValueEnforcer.isFalse (aCachingDuration::isNegative, "CachingDuration must not be negative");
    ValueEnforcer.notNullNoNullValue (aCACerts, "CACerts");

    m_bSynchronizedRevocationCheck = bSynchronizedRevocationCheck;

    for (final X509Certificate aCACert : aCACerts)
    {
      // This method checks if the cert is a CA or not
      m_aTrustedCAs.addTrustedCACertificate (aCACert);
    }

    // The cache always uses "now" as the checking date and time
    m_aRevocationCache = new RevocationCheckResultCache (aCert -> new RevocationCheckBuilder ().certificate (aCert)
                                                                                               .validCAs (aCACerts)
                                                                                               .checkMode (eCheckMode)
                                                                                               .executeInSynchronizedBlock (m_bSynchronizedRevocationCheck)
                                                                                               .build (),
                                                         aCachingDuration,
                                                         nCacheMaxSize);
  }

  /**
   * @return A copy of the trusted CA certificates object used internally. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public TrustedCACertificates getAllTrustedAPCertificates ()
  {
    return new TrustedCACertificates (m_aTrustedCAs);
  }

  /**
   * @return The internal revocation cache that is used. Never <code>null</code>.
   */
  @NonNull
  public RevocationCheckResultCache getRevocationCache ()
  {
    return m_aRevocationCache;
  }

  /**
   * @return <code>true</code> if the revocation check is performed in a <code>synchronized</code>
   *         block, <code>false</code> if not.
   * @since 12.3.1
   */
  public boolean isSynchronizedRevocationCheck ()
  {
    return m_bSynchronizedRevocationCheck;
  }

  /**
   * Check if the provided certificate is a valid Peppol certificate according to the configured CA.
   *
   * @param aCert
   *        The certificate to be checked. May be <code>null</code>.
   * @return {@link ECertificateCheckResult} and never <code>null</code>.
   */
  @NonNull
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
  @NonNull
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
  @NonNull
  public ECertificateCheckResult checkCertificate (@Nullable final X509Certificate aCert,
                                                   @Nullable final OffsetDateTime aCheckDT,
                                                   @NonNull final ETriState eCacheRevocationCheckResult,
                                                   @Nullable final ERevocationCheckMode eCheckMode)
  {
    final boolean bUseRevocationCache = eCacheRevocationCheckResult.isUndefined () ? CertificateRevocationCheckerDefaults.isCacheRevocationCheckResults ()
                                                                                   : eCacheRevocationCheckResult.isTrue ();

    return CertificateHelper.checkCertificate (m_aTrustedCAs.getAllTrustedCAIssuers (),
                                               bUseRevocationCache ? m_aRevocationCache : null,
                                               new RevocationCheckBuilder ().certificate (aCert)
                                                                            .checkDate (aCheckDT)
                                                                            .validCAs (m_aTrustedCAs.getAllTrustedCACertificates ())
                                                                            .checkMode (eCheckMode)
                                                                            .executeInSynchronizedBlock (m_bSynchronizedRevocationCheck));
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("TrustedCAs", m_aTrustedCAs)
                                       .append ("RevocationCache", m_aRevocationCache)
                                       .append ("SynchronizedRevocationCheck", m_bSynchronizedRevocationCheck)
                                       .getToString ();
  }

  /**
   * @return A new builder for {@link TrustedCAChecker} objects. Never <code>null</code>.
   * @since 12.3.1
   */
  @NonNull
  public static TrustedCACheckerBuilder builder ()
  {
    return new TrustedCACheckerBuilder ();
  }

  /**
   * Builder class for {@link TrustedCAChecker}.
   *
   * @author Philip Helger
   * @since 12.3.1
   */
  public static class TrustedCACheckerBuilder implements IBuilder <TrustedCAChecker>
  {
    private ERevocationCheckMode m_eCheckMode = CertificateRevocationCheckerDefaults.getRevocationCheckMode ();
    private Duration m_aCachingDuration = CertificateRevocationCheckerDefaults.DEFAULT_REVOCATION_CHECK_CACHING_DURATION;
    private int m_nCacheMaxSize = RevocationCheckResultCache.DEFAULT_MAX_SIZE;
    private boolean m_bSynchronizedRevocationCheck = CertificateRevocationCheckerDefaults.isExecuteInSynchronizedBlock ();
    private final ICommonsList <X509Certificate> m_aCACerts = new CommonsArrayList <> ();

    /**
     * Default constructor.
     */
    public TrustedCACheckerBuilder ()
    {}

    /**
     * Set the revocation check mode to use.
     *
     * @param e
     *        The revocation check mode. May be <code>null</code>.
     * @return this for chaining
     */
    @NonNull
    public final TrustedCACheckerBuilder checkMode (@Nullable final ERevocationCheckMode e)
    {
      m_eCheckMode = e;
      return this;
    }

    /**
     * Set the caching duration until the next revocation check needs to be performed.
     *
     * @param a
     *        The caching duration. May be <code>null</code>.
     * @return this for chaining
     */
    @NonNull
    public final TrustedCACheckerBuilder cachingDuration (@Nullable final Duration a)
    {
      m_aCachingDuration = a;
      return this;
    }

    /**
     * Set the maximum cache size for the revocation check. Values &le; 0 means no maximum size.
     *
     * @param n
     *        The maximum cache size.
     * @return this for chaining
     */
    @NonNull
    public final TrustedCACheckerBuilder cacheMaxSize (@CheckForSigned final int n)
    {
      m_nCacheMaxSize = n;
      return this;
    }

    /**
     * Set whether the revocation check should be performed in a <code>synchronized</code> block.
     *
     * @param b
     *        <code>true</code> to perform the revocation check in a <code>synchronized</code>
     *        block, <code>false</code> to run it unsynchronized.
     * @return this for chaining
     * @see CertificateRevocationCheckerDefaults#isExecuteInSynchronizedBlock()
     */
    @NonNull
    public final TrustedCACheckerBuilder synchronizedRevocationCheck (final boolean b)
    {
      m_bSynchronizedRevocationCheck = b;
      return this;
    }

    /**
     * Set the trusted CA certificates to be used. This overwrites all previously contained
     * certificates.
     *
     * @param aCACerts
     *        The trusted CA certificates. May be <code>null</code>.
     * @return this for chaining
     */
    @NonNull
    public final TrustedCACheckerBuilder trustedCACertificates (@Nullable final X509Certificate... aCACerts)
    {
      m_aCACerts.setAll (aCACerts);
      return this;
    }

    /**
     * Set the trusted CA certificates to be used. This overwrites all previously contained
     * certificates.
     *
     * @param aCACerts
     *        The trusted CA certificates. May be <code>null</code>.
     * @return this for chaining
     */
    @NonNull
    public final TrustedCACheckerBuilder trustedCACertificates (@Nullable final Iterable <? extends X509Certificate> aCACerts)
    {
      m_aCACerts.setAll (aCACerts);
      return this;
    }

    /**
     * Add a single trusted CA certificate to be used.
     *
     * @param aCACert
     *        The trusted CA certificate to add. May be <code>null</code> in which case it is
     *        ignored.
     * @return this for chaining
     */
    @NonNull
    public final TrustedCACheckerBuilder addTrustedCACertificate (@Nullable final X509Certificate aCACert)
    {
      if (aCACert != null)
        m_aCACerts.add (aCACert);
      return this;
    }

    /**
     * Build the {@link TrustedCAChecker} from the provided parameters.
     *
     * @return A new {@link TrustedCAChecker} instance. Never <code>null</code>.
     * @throws IllegalStateException
     *         if any required parameter is missing.
     */
    @NonNull
    public TrustedCAChecker build () throws IllegalStateException
    {
      if (m_eCheckMode == null)
        throw new IllegalStateException ("CheckMode is missing");
      if (m_aCachingDuration == null)
        throw new IllegalStateException ("CachingDuration is missing");
      if (m_aCachingDuration.isNegative ())
        throw new IllegalStateException ("CachingDuration must not be negative");
      if (m_aCACerts.isEmpty ())
        throw new IllegalStateException ("At least one trusted CA certificate is required");

      return new TrustedCAChecker (m_eCheckMode,
                                   m_aCachingDuration,
                                   m_nCacheMaxSize,
                                   m_bSynchronizedRevocationCheck,
                                   m_aCACerts);
    }
  }
}
