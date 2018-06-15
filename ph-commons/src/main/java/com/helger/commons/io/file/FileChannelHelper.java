/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.commons.io.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import javax.annotation.CheckForSigned;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillNotClose;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.CGlobal;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.io.EAppend;
import com.helger.commons.io.stream.ByteBufferInputStream;
import com.helger.commons.io.stream.ByteBufferOutputStream;
import com.helger.commons.io.stream.StreamHelper;

/**
 * Miscellaneous {@link FileChannel} utility methods.
 *
 * @author Philip Helger
 */
@Immutable
public final class FileChannelHelper
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (FileChannelHelper.class);

  @PresentForCodeCoverage
  private static final FileChannelHelper s_aInstance = new FileChannelHelper ();

  private FileChannelHelper ()
  {}

  @CheckForSigned
  public static long getFileSize (@Nullable final FileChannel aChannel)
  {
    if (aChannel != null)
      try
      {
        return aChannel.size ();
      }
      catch (final IOException ex)
      {
        // fall-through
      }
    return -1;
  }

  @Nullable
  private static InputStream _getMappedInputStream (@Nonnull @WillNotClose final FileChannel aChannel,
                                                    @Nonnull final File aFile)
  {
    try
    {
      final MappedByteBuffer aBuffer = aChannel.map (MapMode.READ_ONLY, 0, aChannel.size ());
      if (s_aLogger.isInfoEnabled ())
        s_aLogger.info ("Created memory mapped input stream for " + aFile);
      return new ByteBufferInputStream (aBuffer);
    }
    catch (final IOException ex)
    {
      s_aLogger.warn ("Failed to create memory mapped input stream for " + aFile, ex);
      return null;
    }
  }

  @Nullable
  public static InputStream getInputStream (@Nonnull final File aFile)
  {
    ValueEnforcer.notNull (aFile, "File");

    final FileInputStream aFIS = FileHelper.getInputStream (aFile);
    if (aFIS != null)
    {
      // Check if using a memory mapped file makes sense (file size > 1MB)
      final FileChannel aChannel = aFIS.getChannel ();
      if (getFileSize (aChannel) > CGlobal.BYTES_PER_MEGABYTE)
      {
        // Check if mapping is possible
        final InputStream aIS = _getMappedInputStream (aChannel, aFile);
        if (aIS != null)
          return aIS;

        // Mapping failed - fall through
      }
    }
    return aFIS;
  }

  @Nullable
  public static FileChannel getFileReadChannel (@Nonnull final String sFilename)
  {
    return getFileReadChannel (new File (sFilename));
  }

  @Nullable
  public static FileChannel getFileReadChannel (@Nonnull final File aFile)
  {
    ValueEnforcer.notNull (aFile, "File");

    final FileInputStream aFIS = FileHelper.getInputStream (aFile);
    return aFIS == null ? null : aFIS.getChannel ();
  }

  /**
   * Get an input stream to the specified file, using memory mapping. If memory
   * mapping fails, a regular {@link FileInputStream} is returned.
   *
   * @param aFile
   *        The file to use. May not be <code>null</code>.
   * @return The Input stream to use.
   */
  @Nullable
  public static InputStream getMappedInputStream (@Nonnull final File aFile)
  {
    ValueEnforcer.notNull (aFile, "File");

    // Open regular
    final FileInputStream aFIS = FileHelper.getInputStream (aFile);
    if (aFIS == null)
      return null;

    // Try to memory map it
    final InputStream aIS = _getMappedInputStream (aFIS.getChannel (), aFile);
    if (aIS != null)
      return aIS;

    // Memory mapping failed - return the original input stream
    if (s_aLogger.isWarnEnabled ())
      s_aLogger.warn ("Failed to map file " + aFile + ". Falling though to regular FileInputStream");
    return aFIS;
  }

  @Nullable
  private static OutputStream _getMappedOutputStream (@Nonnull @WillNotClose final FileChannel aChannel,
                                                      @Nonnull final File aFile)
  {
    try
    {
      // Maximum is Integer.MAX_VALUE even if a long is taken!
      final MappedByteBuffer aBuffer = aChannel.map (MapMode.READ_WRITE, 0, Integer.MAX_VALUE);
      if (s_aLogger.isInfoEnabled ())
        s_aLogger.info ("Created memory mapped output stream for " + aFile);
      return new ByteBufferOutputStream (aBuffer, false);
    }
    catch (final IOException ex)
    {
      s_aLogger.warn ("Failed to create memory mapped output stream for " + aFile, ex);
      return null;
    }
  }

  @Nullable
  public static FileChannel getFileWriteChannel (@Nonnull final String sFilename)
  {
    return getFileWriteChannel (sFilename, EAppend.DEFAULT);
  }

  @Nullable
  public static FileChannel getFileWriteChannel (@Nonnull final String sFilename, @Nonnull final EAppend eAppend)
  {
    return getFileWriteChannel (new File (sFilename), eAppend);
  }

  @Nullable
  public static FileChannel getFileWriteChannel (@Nonnull final File aFile)
  {
    return getFileWriteChannel (aFile, EAppend.DEFAULT);
  }

  @Nullable
  public static FileChannel getFileWriteChannel (@Nonnull final File aFile, @Nonnull final EAppend eAppend)
  {
    ValueEnforcer.notNull (aFile, "File");
    ValueEnforcer.notNull (eAppend, "Append");

    final FileOutputStream aFOS = FileHelper.getOutputStream (aFile, eAppend);
    return aFOS == null ? null : aFOS.getChannel ();
  }

  @Nullable
  public static OutputStream getMappedOutputStream (@Nonnull final String sFilename)
  {
    return getMappedOutputStream (new File (sFilename));
  }

  @Nullable
  public static OutputStream getMappedOutputStream (@Nonnull final String sFilename, @Nonnull final EAppend eAppend)
  {
    return getMappedOutputStream (new File (sFilename), eAppend);
  }

  @Nullable
  public static OutputStream getMappedOutputStream (@Nonnull final File aFile)
  {
    return getMappedOutputStream (aFile, EAppend.DEFAULT);
  }

  @Nullable
  public static OutputStream getMappedOutputStream (@Nonnull final File aFile, @Nonnull final EAppend eAppend)
  {
    ValueEnforcer.notNull (aFile, "File");
    ValueEnforcer.notNull (eAppend, "Append");

    if (FileHelper.internalCheckParentDirectoryExistanceAndAccess (aFile).isInvalid ())
      return null;

    // Open random access file, as only those files deliver a channel that is
    // readable and writable
    final RandomAccessFile aRAF = FileHelper.getRandomAccessFile (aFile, ERandomAccessFileMode.READ_WRITE);
    if (aRAF == null)
    {
      if (s_aLogger.isErrorEnabled ())
        s_aLogger.error ("Failed to open random access file " + aFile);
      return null;
    }

    // Try to memory map it
    final OutputStream aOS = _getMappedOutputStream (aRAF.getChannel (), aFile);
    if (aOS != null)
      return aOS;

    // Memory mapping failed - return the original output stream
    StreamHelper.close (aRAF);
    if (s_aLogger.isWarnEnabled ())
      s_aLogger.warn ("Failed to map file " + aFile + ". Falling though to regular FileOutputStream");
    return FileHelper.getOutputStream (aFile, eAppend);
  }
}
