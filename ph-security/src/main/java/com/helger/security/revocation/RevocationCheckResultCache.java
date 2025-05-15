/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import com.helger.annotation.Nonnull;
import com.helger.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.cache.MappedCache;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;
import com.helger.datetime.expiration.ExpiringObject;

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

  private static final Logger LOGGER = LoggerFactory.getLogger (RevocationCheckResultCache.class);

  private final MappedCache <X509Certificate, String, ExpiringObject <ERevoked>> m_aCache;
  private final Function <X509Certificate, ERevoked> m_aRevocationChecker;
  private final Duration m_aCachingDuration;

  @Nonnull
  private static String _getKey (@Nonnull final X509Certificate aCert)
  {
    return aCert.getSubjectX500Principal ().getName () +
           "-" +
           aCert.getIssuerX500Principal ().getName () +
           "-" +
           aCert.getSerialNumber ().toString ();
  }

  public RevocationCheckResultCache (@Nonnull final Function <X509Certificate, ERevoked> aRevocationChecker,
                                     @Nonnull final Duration aCachingDuration)
  {
    this (aRevocationChecker, aCachingDuration, DEFAULT_MAX_SIZE);
  }

  public RevocationCheckResultCache (@Nonnull final Function <X509Certificate, ERevoked> aRevocationChecker,
                                     @Nonnull final Duration aCachingDuration,
                                     final int nMaxSize)
  {
    ValueEnforcer.notNull (aCachingDuration, "CachingDuration");
    ValueEnforcer.isFalse (aCachingDuration::isNegative, "CachingDuration must not be negative");

    final boolean bAllowNullValues = false;
    m_aCache = new MappedCache <> (RevocationCheckResultCache::_getKey, cert -> {
      final ERevoked eRevoked = aRevocationChecker.apply (cert);
      return ExpiringObject.ofDuration (eRevoked, aCachingDuration);
    }, nMaxSize, "CertificateRevocationCache", bAllowNullValues);
    m_aRevocationChecker = aRevocationChecker;
    m_aCachingDuration = aCachingDuration;
  }

  @Nonnull
  public final Function <X509Certificate, ERevoked> getRevocationChecker ()
  {
    return m_aRevocationChecker;
  }

  @Nonnull
  public final Duration getCachingDuration ()
  {
    return m_aCachingDuration;
  }

  public boolean isRevoked (@Nonnull final X509Certificate aCert)
  {
    ValueEnforcer.notNull (aCert, "Cert");

    // Cannot return null
    ExpiringObject <ERevoked> aObject = m_aCache.getFromCache (aCert);
    // maximum life time check
    if (aObject.isExpiredNow ())
    {
      LOGGER.info ("The cached entry for certificate '" + _getKey (aCert) + "' is expired and needs to be re-fetched.");

      // Object expired - re-fetch
      m_aCache.removeFromCache (aCert);
      aObject = m_aCache.getFromCache (aCert);
    }
    return aObject.getObject ().isRevoked ();
  }

  @Nonnull
  public EChange clearCache ()
  {
    return m_aCache.clearCache ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("Cache", m_aCache)
                                       .append ("RevocationChecker", m_aRevocationChecker)
                                       .append ("CachingDuration", m_aCachingDuration)
                                       .getToString ();
  }
}
