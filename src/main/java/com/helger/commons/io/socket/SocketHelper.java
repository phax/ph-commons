/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.io.socket;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillClose;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.mock.exception.IMockException;
import com.helger.commons.state.ESuccess;

/**
 * Helper class for some common Socket stuff.
 *
 * @author Philip Helger
 */
@Immutable
public final class SocketHelper
{
  /** The logger to use. */
  private static final Logger s_aLogger = LoggerFactory.getLogger (SocketHelper.class);

  @PresentForCodeCoverage
  private static final SocketHelper s_aInstance = new SocketHelper ();

  private SocketHelper ()
  {}

  /**
   * Special close version for {@link Socket} as they are not implementing
   * {@link Closeable} :(
   *
   * @param aSocket
   *        The socket to be closed. May be <code>null</code>.
   * @return {@link ESuccess} if the object was successfully closed.
   */
  @Nonnull
  public static ESuccess close (@Nullable @WillClose final Socket aSocket)
  {
    if (aSocket != null && !aSocket.isClosed ())
    {
      try
      {
        // close object
        aSocket.close ();
        return ESuccess.SUCCESS;
      }
      catch (final IOException ex)
      {
        if (!StreamHelper.isKnownEOFException (ex))
          s_aLogger.error ("Failed to close socket " + aSocket.getClass ().getName (),
                           ex instanceof IMockException ? null : ex);
      }
    }
    return ESuccess.FAILURE;
  }

  /**
   * Special close version for {@link ServerSocket} as they are not implementing
   * {@link Closeable} :(
   *
   * @param aSocket
   *        The socket to be closed. May be <code>null</code>.
   * @return {@link ESuccess} if the object was successfully closed.
   */
  @Nonnull
  public static ESuccess close (@Nullable @WillClose final ServerSocket aSocket)
  {
    if (aSocket != null && !aSocket.isClosed ())
    {
      try
      {
        // close object
        aSocket.close ();
        return ESuccess.SUCCESS;
      }
      catch (final IOException ex)
      {
        if (!StreamHelper.isKnownEOFException (ex))
          s_aLogger.error ("Failed to close server socket " + aSocket.getClass ().getName (),
                           ex instanceof IMockException ? null : ex);
      }
    }
    return ESuccess.FAILURE;
  }
}
