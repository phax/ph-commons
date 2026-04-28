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
package com.helger.base.url;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.base.io.stream.StreamHelper;

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
   * The default connection timeout used by {@link #createDefault()} in milliseconds (10 seconds).
   *
   * @since 12.2.4
   */
  int DEFAULT_CONNECT_TIMEOUT_MS = 10_000;

  /**
   * The default read timeout used by {@link #createDefault()} in milliseconds (60 seconds).
   *
   * @since 12.2.4
   */
  int DEFAULT_READ_TIMEOUT_MS = 60_000;

  /**
   * Download the content of the provided URL
   *
   * @param sURL
   *        The CRL URL to download. Neither <code>null</code> nor empty.
   * @return <code>null</code> if no payload was returned
   * @throws Exception
   *         In case of error
   */
  byte @Nullable [] downloadURL (@NonNull @Nonempty String sURL) throws Exception;

  /**
   * @return The default URL downloader using {@link URL#openConnection()} with the default
   *         connection timeout of {@link #DEFAULT_CONNECT_TIMEOUT_MS} milliseconds and the default
   *         read timeout of {@link #DEFAULT_READ_TIMEOUT_MS} milliseconds. Never
   *         <code>null</code>.
   */
  @NonNull
  static IURLDownloader createDefault ()
  {
    return createDefault (DEFAULT_CONNECT_TIMEOUT_MS, DEFAULT_READ_TIMEOUT_MS);
  }

  /**
   * Create a default URL downloader using {@link URL#openConnection()} with the provided
   * connection and read timeouts. A timeout value of 0 means infinite (the JVM default), so this
   * is strongly discouraged for downloads from the public Internet.
   *
   * @param nConnectTimeoutMS
   *        The connection timeout in milliseconds. Must be &ge; 0.
   * @param nReadTimeoutMS
   *        The read timeout in milliseconds. Must be &ge; 0.
   * @return Never <code>null</code>.
   * @since 12.2.4
   */
  @NonNull
  static IURLDownloader createDefault (final int nConnectTimeoutMS, final int nReadTimeoutMS)
  {
    return sURL -> {
      // Use the built in HTTP client here (global proxy, etc.)
      final URLConnection aConn = new URL (sURL).openConnection ();
      aConn.setConnectTimeout (nConnectTimeoutMS);
      aConn.setReadTimeout (nReadTimeoutMS);
      try (final InputStream aIS = aConn.getInputStream ())
      {
        return StreamHelper.getAllBytes (aIS);
      }
    };
  }
}
