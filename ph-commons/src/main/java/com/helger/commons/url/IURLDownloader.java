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
package com.helger.commons.url;

import java.io.InputStream;
import java.net.URL;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnull;
import com.helger.annotation.Nullable;
import com.helger.commons.io.stream.StreamHelper;

/**
 * Callback interface to download data from web. Used e.g. for CRL downloads.
 *
 * @author Philip Helger
 * @since 11.2.0
 */
@FunctionalInterface
public interface IURLDownloader
{
  /**
   * Download the content of the provided URL
   *
   * @param sURL
   *        The CRL URL to download. Neither <code>null</code> nor empty.
   * @return <code>null</code> if no payload was returned
   * @throws Exception
   *         In case of error
   */
  @Nullable
  byte [] downloadURL (@Nonnull @Nonempty String sURL) throws Exception;

  /**
   * @return The default URL downloader using {@link URL#openStream()}. Never
   *         <code>null</code>.
   */
  @Nonnull
  static IURLDownloader createDefault ()
  {
    return sURL -> {
      // Use the built in HTTP client here (global proxy, etc.)
      try (final InputStream aIS = new URL (sURL).openStream ())
      {
        return StreamHelper.getAllBytes (aIS);
      }
    };
  }
}
