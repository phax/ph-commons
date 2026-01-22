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
import com.helger.cache.impl.Cache;
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
  private static class CRLInternalCache extends Cache <String, ExpiringObject <CRL>>
  {
    public CRLInternalCache (@NonNull final Function <String, ExpiringObject <CRL>> aCacheValueProvider,
                             final int nMaxSize)
    {
      super (aCacheValueProvider, nMaxSize, "CRLCache", true);
    }

    private void _insertManually (final String aKey, final ExpiringObject <CRL> aValue)
    {
      super.putInCache (aKey, aValue);
    }
  }

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

    m_aCache = new CRLInternalCache (url -> {
      final CRL aCRL = aDownloader.downloadCRL (url);
      return aCRL == null ? null : ExpiringObject.ofDuration (aCRL, aCachingDuration);
    }, 200);
    m_aDownloader = aDownloader;
    m_aCachingDuration = aCachingDuration;
  }

  @NonNull
  public final CRLDownloader getDownloader ()
  {
    return m_aDownloader;
  }

  @NonNull
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
    if (StringHelper.isNotEmpty (sCRLURL))
    {
      final ExpiringObject <CRL> aObject = m_aCache.getFromCache (sCRLURL);
      if (aObject != null)
      {
        // maximum life time check
        if (aObject.isExpiredNow ())
        {
          LOGGER.info ("The cached entry for CRL URL '" + sCRLURL + "' is expired and needs to be re-fetched.");

          // Object expired - re-fetch
          m_aCache.removeFromCache (sCRLURL);
          final ExpiringObject <CRL> aObjectNew = m_aCache.getFromCache (sCRLURL);
          if (aObjectNew != null)
          {
            // We got something new from the fetch
            return aObjectNew.getObject ();
          }

          // The re-fetch was not successful - keep the old one and start a new cycle, but only half
          // the time
          LOGGER.warn ("The re-fetch for CRL URL '" +
                       sCRLURL +
                       "' was unsuccessful, so keeping the previous CRL version.");
          m_aCache._insertManually (sCRLURL,
                                    ExpiringObject.ofDuration (aObject.getObject (), m_aCachingDuration.dividedBy (2)));
        }

        return aObject.getObject ();
      }
    }
    return null;
  }

  /**
   * Allow to manually add a downloaded CRL into the cache, for the provided URL.
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
    m_aCache._insertManually (sCRLURL, ExpiringObject.ofDuration (aCRL, m_aCachingDuration));
  }

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

  @NonNull
  public static CRLCache createDefault ()
  {
    return new CRLCache (new CRLDownloader (), DEFAULT_CACHING_DURATION);
  }
}
