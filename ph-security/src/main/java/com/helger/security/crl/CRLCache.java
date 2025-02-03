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
package com.helger.security.crl;

import java.security.cert.CRL;
import java.time.Duration;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.cache.Cache;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.datetime.expiration.ExpiringObject;

/**
 * A cache for CRLs read from remote locations.
 *
 * @author Philip Helger
 * @since 11.2.0
 */
public class CRLCache
{
  @ThreadSafe
  private static class MyCache extends Cache <String, ExpiringObject <CRL>>
  {
    public MyCache (@Nonnull final Function <String, ExpiringObject <CRL>> aCacheValueProvider,
                    final int nMaxSize,
                    @Nonnull @Nonempty final String sCacheName,
                    final boolean bAllowNullValues)
    {
      super (aCacheValueProvider, nMaxSize, sCacheName, bAllowNullValues);
    }

    private void insertManually (final String aKey, final ExpiringObject <CRL> aValue)
    {
      super.putInCache (aKey, aValue);
    }
  }

  public static final Duration DEFAULT_CACHING_DURATION = Duration.ofHours (24);
  private static final Logger LOGGER = LoggerFactory.getLogger (CRLCache.class);

  private final MyCache m_aCache;
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
  public CRLCache (@Nonnull final CRLDownloader aDownloader, @Nonnull final Duration aCachingDuration)
  {
    m_aCache = new MyCache (url -> {
      final CRL aCRL = aDownloader.downloadCRL (url);
      return aCRL == null ? null : ExpiringObject.ofDuration (aCRL, aCachingDuration);
    }, 100, "PeppolCRLCache", true);
    ValueEnforcer.notNull (aDownloader, "CRLDownloader");
    ValueEnforcer.notNull (aCachingDuration, "CachingDuration");
    ValueEnforcer.isFalse (aCachingDuration::isNegative, "CachingDuration must not be negative");
    m_aDownloader = aDownloader;
    m_aCachingDuration = aCachingDuration;
  }

  @Nonnull
  public final CRLDownloader getDownloader ()
  {
    return m_aDownloader;
  }

  @Nonnull
  public final Duration getCachingDuration ()
  {
    return m_aCachingDuration;
  }

  /**
   * Get the CRL object from the provided URL. Uses caching internally.
   *
   * @param sCRLURL
   *        The URL to read the CRL from.
   * @return <code>null</code> if the CRL could not be read from that URL.
   */
  @Nullable
  public CRL getCRLFromURL (@Nullable final String sCRLURL)
  {
    if (StringHelper.hasText (sCRLURL))
    {
      ExpiringObject <CRL> aObject = m_aCache.getFromCache (sCRLURL);
      if (aObject != null)
      {
        // maximum life time check
        if (aObject.isExpiredNow ())
        {
          LOGGER.info ("The cached entry for CRL URL '" + sCRLURL + "' is expired and needs to be re-fetched.");

          // Object expired - re-fetch
          m_aCache.removeFromCache (sCRLURL);
          aObject = m_aCache.getFromCache (sCRLURL);
        }
        if (aObject != null)
          return aObject.getObject ();
      }
    }
    return null;
  }

  /**
   * Allow to manually add a downloaded CRL into the cache, for the provided
   * URL.
   *
   * @param sCRLURL
   *        The URL for which the cached URL should be used. May neither be
   *        <code>null</code> nor empty.
   * @param aCRL
   *        The CRL to be used. May not be <code>null</code>.
   */
  public void setCRLOfURL (@Nonnull @Nonempty final String sCRLURL, @Nonnull final CRL aCRL)
  {
    ValueEnforcer.notEmpty (sCRLURL, "CRLURL");
    ValueEnforcer.notNull (aCRL, "CRL");
    m_aCache.insertManually (sCRLURL, ExpiringObject.ofDuration (aCRL, m_aCachingDuration));
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("Cache", m_aCache)
                                       .append ("Downloader", m_aDownloader)
                                       .append ("CachingDuration", m_aCachingDuration)
                                       .getToString ();
  }

  @Nonnull
  public static CRLCache createDefault ()
  {
    return new CRLCache (new CRLDownloader (), DEFAULT_CACHING_DURATION);
  }
}
