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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.timing.StopWatch;
import com.helger.commons.url.EURLProtocol;
import com.helger.commons.url.IURLDownloader;

/**
 * A class for downloading CRL data. This class is as thread-safe as the used
 * {@link IURLDownloader}
 *
 * @author Philip Helger
 * @since 11.2.0
 */
public class CRLDownloader
{
  private static final Logger LOGGER = LoggerFactory.getLogger (CRLDownloader.class);

  private final IURLDownloader m_aURLDownloader;

  /**
   * Use the system default URL downloader.
   */
  public CRLDownloader ()
  {
    this (IURLDownloader.createDefault ());
  }

  /**
   * Constructor using the provided URL downloader.
   *
   * @param aUrlDownloader
   *        The URL downloader to use. May not be <code>null</code>.
   */
  public CRLDownloader (@Nonnull final IURLDownloader aUrlDownloader)
  {
    ValueEnforcer.notNull (aUrlDownloader, "UrlDownloader");
    m_aURLDownloader = aUrlDownloader;
  }

  /**
   * @return The internal URL downloader used. Never <code>null</code>.
   */
  @Nonnull
  public final IURLDownloader getURLDownloader ()
  {
    return m_aURLDownloader;
  }

  @Nullable
  public CRL downloadCRL (@Nonnull final String sCRLURL)
  {
    if (EURLProtocol.HTTP.isUsedInURL (sCRLURL) || EURLProtocol.HTTPS.isUsedInURL (sCRLURL))
    {
      // Try to download from remote URL
      LOGGER.info ("Downloading CRL from URL '" + sCRLURL + "'");
      final StopWatch aSW = StopWatch.createdStarted ();
      int nByteCount = 0;
      try
      {
        final byte [] aCRLBytes = getURLDownloader ().downloadURL (sCRLURL);
        if (aCRLBytes != null)
        {
          nByteCount = aCRLBytes.length;
          if (LOGGER.isDebugEnabled ())
            LOGGER.debug ("Finished downloading CRL and received " + nByteCount + " bytes");

          return CRLHelper.convertToCRL (aCRLBytes);
        }

        LOGGER.error ("Failed to download CRL from URL '" + sCRLURL + "' - null array returned");
      }
      catch (final Exception ex)
      {
        LOGGER.error ("Error downloading CRL from URL '" + sCRLURL + "'", ex);
      }
      finally
      {
        aSW.stop ();
        if (aSW.getMillis () > 1_000)
          LOGGER.info ("Downloading the CRL from '" +
                       sCRLURL +
                       "' took " +
                       aSW.getMillis () +
                       " milliseconds for " +
                       nByteCount +
                       " bytes");
      }
    }

    return null;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("URLDownloader", m_aURLDownloader).getToString ();
  }
}
