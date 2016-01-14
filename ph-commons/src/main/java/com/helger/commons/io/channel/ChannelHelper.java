/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.io.channel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillNotClose;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.exception.mock.IMockException;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.state.ESuccess;

/**
 * Some very basic NIO channel utility stuff.
 *
 * @author Philip Helger
 */
@Immutable
public final class ChannelHelper
{
  /** The logger to use. */
  private static final Logger s_aLogger = LoggerFactory.getLogger (ChannelHelper.class);

  // Use version 1 as it seems to be faster
  private static final boolean USE_COPY_V1 = true;

  @PresentForCodeCoverage
  private static final ChannelHelper s_aInstance = new ChannelHelper ();

  private ChannelHelper ()
  {}

  /**
   * Copy all content from the source channel to the destination channel.
   *
   * @param aSrc
   *        Source channel. May not be <code>null</code>. Is not closed after
   *        the operation.
   * @param aDest
   *        Destination channel. May not be <code>null</code>. Is not closed
   *        after the operation.
   * @return The number of bytes written.
   * @throws IOException
   *         In case of IO error
   */
  @Nonnegative
  public static long channelCopy (@Nonnull @WillNotClose final ReadableByteChannel aSrc,
                                  @Nonnull @WillNotClose final WritableByteChannel aDest) throws IOException
  {
    ValueEnforcer.notNull (aSrc, "SourceChannel");
    ValueEnforcer.isTrue (aSrc.isOpen (), "SourceChannel is not open!");
    ValueEnforcer.notNull (aDest, "DestinationChannel");
    ValueEnforcer.isTrue (aDest.isOpen (), "DestinationChannel is not open!");

    long nBytesWritten;
    if (USE_COPY_V1)
      nBytesWritten = _channelCopy1 (aSrc, aDest);
    else
      nBytesWritten = _channelCopy2 (aSrc, aDest);
    return nBytesWritten;
  }

  /**
   * Channel copy method 1. This method copies data from the src channel and
   * writes it to the dest channel until EOF on src. This implementation makes
   * use of compact( ) on the temp buffer to pack down the data if the buffer
   * wasn't fully drained. This may result in data copying, but minimizes system
   * calls. It also requires a cleanup loop to make sure all the data gets sent.
   * <br>
   * Source: Java NIO, page 60
   *
   * @param aSrc
   *        Source channel. May not be <code>null</code>. Is not closed after
   *        the operation.
   * @param aDest
   *        Destination channel. May not be <code>null</code>. Is not closed
   *        after the operation.
   * @return The number of bytes written.
   */
  @Nonnegative
  private static long _channelCopy1 (@Nonnull @WillNotClose final ReadableByteChannel aSrc,
                                     @Nonnull @WillNotClose final WritableByteChannel aDest) throws IOException
  {
    long nBytesWritten = 0;
    final ByteBuffer aBuffer = ByteBuffer.allocateDirect (16 * 1024);
    while (aSrc.read (aBuffer) != -1)
    {
      // Prepare the buffer to be drained
      aBuffer.flip ();

      // Write to the channel; may block
      nBytesWritten += aDest.write (aBuffer);

      // If partial transfer, shift remainder down
      // If buffer is empty, same as doing clear()
      aBuffer.compact ();
    }

    // EOF will leave buffer in fill state
    aBuffer.flip ();

    // Make sure that the buffer is fully drained
    while (aBuffer.hasRemaining ())
      nBytesWritten += aDest.write (aBuffer);

    return nBytesWritten;
  }

  /**
   * Channel copy method 2. This method performs the same copy, but assures the
   * temporary buffer is empty before reading more data. This never requires
   * data copying but may result in more systems calls. No post-loop cleanup is
   * needed because the buffer will be empty when the loop is exited.<br>
   * Source: Java NIO, page 60
   *
   * @param aSrc
   *        Source channel. May not be <code>null</code>. Is not closed after
   *        the operation.
   * @param aDest
   *        Destination channel. May not be <code>null</code>. Is not closed
   *        after the operation.
   * @return The number of bytes written.
   */
  private static long _channelCopy2 (@Nonnull @WillNotClose final ReadableByteChannel aSrc,
                                     @Nonnull @WillNotClose final WritableByteChannel aDest) throws IOException
  {
    long nBytesWritten = 0;
    final ByteBuffer aBuffer = ByteBuffer.allocateDirect (16 * 1024);
    while (aSrc.read (aBuffer) != -1)
    {
      // Prepare the buffer to be drained
      aBuffer.flip ();

      // Make sure that the buffer was fully drained
      while (aBuffer.hasRemaining ())
        nBytesWritten += aDest.write (aBuffer);

      // Make the buffer empty, ready for filling
      aBuffer.clear ();
    }
    return nBytesWritten;
  }

  @Nonnull
  public static ESuccess close (@Nullable final Channel aChannel)
  {
    if (aChannel != null && aChannel.isOpen ())
      return StreamHelper.close (aChannel);
    return ESuccess.FAILURE;
  }

  @Nonnull
  public static ESuccess release (@Nullable final FileLock aFileLock)
  {
    if (aFileLock != null)
    {
      try
      {
        aFileLock.release ();
        return ESuccess.SUCCESS;
      }
      catch (final IOException ex)
      {
        s_aLogger.error ("Failed to release object " + aFileLock, ex instanceof IMockException ? null : ex);
      }
    }

    return ESuccess.FAILURE;
  }
}
