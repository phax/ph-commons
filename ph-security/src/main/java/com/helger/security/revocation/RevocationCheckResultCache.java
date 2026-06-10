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

import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.function.Function;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.CheckForSigned;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.state.EChange;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.cache.impl.MappedKeyProviderCache;

/**
 * An revocation cache that checks the revocation status of each certificate and keeps the status
 * for a provided duration.
 *
 * @author Philip Helger
 * @since 11.2.0
 */
@ThreadSafe
public class RevocationCheckResultCache
{
  public static final int DEFAULT_MAX_SIZE = 1_000;

  private final Function <X509Certificate, ERevoked> m_aRevocationChecker;
  private final Duration m_aTimeToLive;
  private final MappedKeyProviderCache <X509Certificate, String, ERevoked> m_aCache;

  @NonNull
  private static String _getKey (@NonNull final X509Certificate aCert)
  {
    return aCert.getSubjectX500Principal ().getName () +
           "-" +
           aCert.getIssuerX500Principal ().getName () +
           "-" +
           aCert.getSerialNumber ().toString ();
  }

  /**
   * Constructor using the {@link #DEFAULT_MAX_SIZE} as the maximum cache size.
   *
   * @param aRevocationChecker
   *        The function that checks revocation status for a given certificate. May not be
   *        <code>null</code>.
   * @param aCachingDuration
   *        The duration to cache each revocation check result. May not be <code>null</code> and
   *        must not be negative.
   */
  public RevocationCheckResultCache (@NonNull final Function <X509Certificate, ERevoked> aRevocationChecker,
                                     @NonNull final Duration aCachingDuration)
  {
    this (aRevocationChecker, aCachingDuration, DEFAULT_MAX_SIZE);
  }

  /**
   * Constructor with explicit maximum cache size.
   *
   * @param aRevocationChecker
   *        The function that checks revocation status for a given certificate. May not be
   *        <code>null</code>.
   * @param aCachingDuration
   *        The duration to cache each revocation check result. May not be <code>null</code> and
   *        must not be negative.
   * @param nMaxSize
   *        The maximum number of entries in the cache. Values &le; 0 mean no maximum size.
   */
  public RevocationCheckResultCache (@NonNull final Function <X509Certificate, ERevoked> aRevocationChecker,
                                     @NonNull final Duration aCachingDuration,
                                     @CheckForSigned final int nMaxSize)
  {
    ValueEnforcer.notNull (aCachingDuration, "CachingDuration");
    ValueEnforcer.isFalse (aCachingDuration::isNegative, "CachingDuration must not be negative");

    m_aRevocationChecker = aRevocationChecker;
    m_aTimeToLive = aCachingDuration;
    m_aCache = MappedKeyProviderCache.<X509Certificate, String, ERevoked> builder ()
                                     .name ("CertificateRevocationCache")
                                     .maxSize (nMaxSize)
                                     .expireAfterWrite (m_aTimeToLive)
                                     .evictionInterval (Duration.ofMinutes (1))
                                     .keyMapper (RevocationCheckResultCache::_getKey)
                                     .valueProvider (m_aRevocationChecker::apply)
                                     .build ();
  }

  /**
   * @return The revocation checker function as provided in the constructor. Never
   *         <code>null</code>.
   */
  @NonNull
  public final Function <X509Certificate, ERevoked> getRevocationChecker ()
  {
    return m_aRevocationChecker;
  }

  /**
   * @return The caching duration as provided in the constructor. Never <code>null</code>.
   */
  @NonNull
  public final Duration getCachingDuration ()
  {
    return m_aTimeToLive;
  }

  /**
   * Get the full {@link ERevoked} revocation status for the provided certificate. The result is
   * cached for the configured caching duration. If the cached entry is expired, it is automatically
   * re-fetched.
   *
   * @param aCert
   *        The certificate to check. May not be <code>null</code>.
   * @return The revocation status. Never <code>null</code>. Returns {@link ERevoked#UNKNOWN} if the
   *         underlying check could not determine the status (e.g. CRL endpoint unreachable).
   * @since 12.2.4
   */
  @NonNull
  public ERevoked getRevocationStatus (@NonNull final X509Certificate aCert)
  {
    ValueEnforcer.notNull (aCert, "Cert");

    return m_aCache.getFromCache (aCert);
  }

  /**
   * Check whether the provided certificate is revoked. The result is cached for the configured
   * caching duration. If the cached entry is expired, it is automatically re-fetched.
   *
   * @param aCert
   *        The certificate to check. May not be <code>null</code>.
   * @return <code>true</code> if the certificate is revoked, <code>false</code> if not. Note that
   *         "not revoked" also covers the {@link ERevoked#UNKNOWN} case - use
   *         {@link #getRevocationStatus(X509Certificate)} to distinguish "verified not revoked"
   *         from "could not be determined".
   */
  public boolean isRevoked (@NonNull final X509Certificate aCert)
  {
    return getRevocationStatus (aCert).isRevoked ();
  }

  /**
   * Remove all entries from the cache.
   *
   * @return {@link EChange#CHANGED} if the cache was not empty and was cleared,
   *         {@link EChange#UNCHANGED} otherwise.
   */
  @NonNull
  public EChange clearCache ()
  {
    return m_aCache.clearCache ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("RevocationChecker", m_aRevocationChecker)
                                       .append ("CachingDuration", m_aTimeToLive)
                                       .append ("Cache", m_aCache)
                                       .getToString ();
  }
}
