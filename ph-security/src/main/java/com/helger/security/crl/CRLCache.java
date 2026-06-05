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

import java.security.cert.CRL;
import java.time.Duration;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.state.EChange;
import com.helger.base.string.StringHelper;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.cache.impl.CacheEntry;
import com.helger.cache.impl.ManualCache;

/**
 * A cache for CRLs read from remote locations.
 *
 * @author Philip Helger
 * @since 11.2.0
 */
public class CRLCache
{
  /**
   * Inner cache that stores CRLs by URL with the configured time-to-live. CRLs are downloaded
   * explicitly by the enclosing {@link CRLCache}; this subclass only exposes a peek helper used by
   * the stale-fallback path in {@link CRLCache#getCRLFromURL(String)}.
   *
   * @author Philip Helger
   */
  @ThreadSafe
  private static class CRLInternalCache extends ManualCache <String, CRL>
  {
    /**
     * Constructor.
     *
     * @param nMaxSize
     *        The maximum number of entries in the cache.
     * @param aCachingDuration
     *        The default time-to-live for new entries. May not be <code>null</code>.
     */
    public CRLInternalCache (final int nMaxSize, @NonNull final Duration aCachingDuration)
    {
      super ("CRLCache", nMaxSize, false, aCachingDuration, DEFAULT_CLOCK_SUPPLIER);
    }

    /**
     * Peek at the raw cached CRL for the provided key, including entries whose TTL has already
     * passed but have not yet been evicted. Used by the fallback path to keep a previously cached
     * CRL available when a re-fetch fails.
     *
     * @param sCRLURL
     *        The URL to look up.
     * @return The cached CRL value (possibly stale), or <code>null</code> if no entry exists.
     */
    @Nullable
    final CRL peekStale (@Nullable final String sCRLURL)
    {
      final CacheEntry <CRL> aEntry = getFromCacheNoStats (sCRLURL);
      return aEntry == null ? null : aEntry.getValue ();
    }
  }

  /** Default caching duration is set to 24 hours */
  public static final Duration DEFAULT_CACHING_DURATION = Duration.ofHours (24);

  private static final Logger LOGGER = LoggerFactory.getLogger (CRLCache.class);

  private final CRLInternalCache m_aCache;
  private final CRLDownloader m_aDownloader;
  private final Duration m_aCachingDuration;

  /**
   * Constructor
   *
   * @param aDownloader
   *        The downloader to be used to grab data from the Internet.
   * @param aCachingDuration
   *        The caching duration to be used. Must not be <code>null</code>.
   */
  public CRLCache (@NonNull final CRLDownloader aDownloader, @NonNull final Duration aCachingDuration)
  {
    ValueEnforcer.notNull (aDownloader, "CRLDownloader");
    ValueEnforcer.notNull (aCachingDuration, "CachingDuration");
    ValueEnforcer.isFalse (aCachingDuration::isNegative, "CachingDuration must not be negative");

    m_aCache = new CRLInternalCache (200, aCachingDuration);
    m_aDownloader = aDownloader;
    m_aCachingDuration = aCachingDuration;
  }

  /**
   * @return The downloader to use. May not be <code>null</code>.
   */
  @NonNull
  public final CRLDownloader getDownloader ()
  {
    return m_aDownloader;
  }

  /**
   * @return The caching duration to use by default. May not be <code>null</code>.
   */
  @NonNull
  public final Duration getCachingDuration ()
  {
    return m_aCachingDuration;
  }

  /**
   * Get the CRL object from the provided URL. Uses caching internally.
   * <p>
   * If a previously cached CRL has expired and a re-fetch fails (e.g. because the CRL distribution
   * point is temporarily unreachable), the cache keeps serving the previously cached CRL with a
   * shortened lifetime of {@code cachingDuration / 2}. This is a deliberate availability trade-off:
   * it prevents transient outages of the CRL distribution point from breaking certificate
   * validation, but it also means that during a sustained outage of the CRL endpoint, a revoked
   * certificate may continue to be accepted up until the previous CRL's <code>nextUpdate</code> is
   * reached. The PKIX validator enforces the CRL's <code>nextUpdate</code> field, which acts as the
   * ultimate upper bound for how long a stale CRL can be used.
   * </p>
   *
   * @param sCRLURL
   *        The URL to read the CRL from.
   * @return <code>null</code> if the CRL could not be read from that URL.
   */
  @Nullable
  public CRL getCRLFromURL (@Nullable final String sCRLURL)
  {
    if (StringHelper.isEmpty (sCRLURL))
      return null;

    // Fast path: non-expired entry available?
    if (m_aCache.isInCache (sCRLURL))
      return m_aCache.getFromCache (sCRLURL);

    // Slow path: nothing cached or the entry has expired. Peek for a stale value to fall back to
    // and try to re-download.
    final CRL aStaleCRL = m_aCache.peekStale (sCRLURL);
    if (aStaleCRL != null)
      LOGGER.info ("The cached entry for CRL URL '" + sCRLURL + "' is expired and needs to be re-fetched.");

    final CRL aFreshCRL = m_aDownloader.downloadCRL (sCRLURL);
    if (aFreshCRL != null)
    {
      m_aCache.putInCache (sCRLURL, aFreshCRL);
      return aFreshCRL;
    }

    // Re-fetch failed - keep the stale value with a reduced lifetime if one was previously cached
    if (aStaleCRL != null)
    {
      LOGGER.warn ("The re-fetch for CRL URL '" +
                   sCRLURL +
                   "' was unsuccessful, so keeping the previous CRL version.");
      m_aCache.putInCache (sCRLURL, aStaleCRL, m_aCachingDuration.dividedBy (2));
      return aStaleCRL;
    }
    return null;
  }

  /**
   * Allow to manually add a downloaded CRL into the cache, for the provided URL.
   * <p>
   * <b>Trust boundary:</b> this method bypasses the {@link CRLDownloader} and stores the provided
   * {@link CRL} verbatim. Any in-process caller that obtains a reference to this cache can use
   * this method (or the inherited {@code putInCache(...)} overloads) to plant an arbitrary
   * {@link CRL} - including one with an empty revoked-cert list - and thereby subvert CRL-based
   * revocation checking for the affected URL. The PKIX validator only enforces the cached CRL's
   * {@code nextUpdate} field; CRL signature / issuer validation must happen <em>before</em> a CRL
   * is handed to this method. Treat this method as a privileged API and only call it with CRLs
   * obtained from a trusted source.
   *
   * @param sCRLURL
   *        The URL for which the cached URL should be used. May neither be <code>null</code> nor
   *        empty.
   * @param aCRL
   *        The CRL to be used. May not be <code>null</code>.
   */
  public void setCRLOfURL (@NonNull @Nonempty final String sCRLURL, @NonNull final CRL aCRL)
  {
    ValueEnforcer.notEmpty (sCRLURL, "CRLURL");
    ValueEnforcer.notNull (aCRL, "CRL");
    m_aCache.putInCache (sCRLURL, aCRL);
  }

  /**
   * Remove all cache items.
   *
   * @return {@link EChange} and never <code>null</code>.
   */
  @NonNull
  public EChange clearCache ()
  {
    return m_aCache.clearCache ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("Cache", m_aCache)
                                       .append ("Downloader", m_aDownloader)
                                       .append ("CachingDuration", m_aCachingDuration)
                                       .getToString ();
  }

  /**
   * @return A new {@link CRLCache} using the {@link CRLDownloader} and the
   *         {@link #DEFAULT_CACHING_DURATION} of 24h.
   */
  @NonNull
  public static CRLCache createDefault ()
  {
    return new CRLCache (new CRLDownloader (), DEFAULT_CACHING_DURATION);
  }
}
