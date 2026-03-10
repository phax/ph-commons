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
package com.helger.io.file;

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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.CheckForSigned;
import com.helger.annotation.WillNotClose;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.CGlobal;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.io.EAppend;
import com.helger.base.io.stream.ByteBufferInputStream;
import com.helger.base.io.stream.ByteBufferOutputStream;
import com.helger.base.io.stream.StreamHelper;

/**
 * Miscellaneous {@link FileChannel} utility methods.
 *
 * @author Philip Helger
 */
@Immutable
public final class FileChannelHelper
{
  private static final Logger LOGGER = LoggerFactory.getLogger (FileChannelHelper.class);

  @PresentForCodeCoverage
  private static final FileChannelHelper INSTANCE = new FileChannelHelper ();

  private FileChannelHelper ()
  {}

  /**
   * Get the size of the file represented by the passed channel.
   *
   * @param aChannel
   *        The file channel to get the size of. May be <code>null</code>.
   * @return The file size in bytes, or -1 if the channel is <code>null</code> or an I/O error
   *         occurred.
   */
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
  private static InputStream _getMappedInputStream (@NonNull @WillNotClose final FileChannel aChannel,
                                                    @NonNull final File aFile)
  {
    try
    {
      final MappedByteBuffer aBuffer = aChannel.map (MapMode.READ_ONLY, 0, aChannel.size ());
      LOGGER.info ("Created memory mapped input stream for " + aFile);
      return new ByteBufferInputStream (aBuffer);
    }
    catch (final IOException ex)
    {
      LOGGER.warn ("Failed to create memory mapped input stream for " + aFile, ex);
      return null;
    }
  }

  /**
   * Get an input stream for the passed file. If the file is larger than 1 MB, a memory-mapped input
   * stream is used; otherwise a regular {@link FileInputStream} is returned.
   *
   * @param aFile
   *        The file to read. May not be <code>null</code>.
   * @return <code>null</code> if the file does not exist or cannot be opened.
   */
  @Nullable
  public static InputStream getInputStream (@NonNull final File aFile)
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

  /**
   * Get a read-only {@link FileChannel} for the passed filename.
   *
   * @param sFilename
   *        The name of the file to open. May not be <code>null</code>.
   * @return <code>null</code> if the file could not be opened.
   */
  @Nullable
  public static FileChannel getFileReadChannel (@NonNull final String sFilename)
  {
    return getFileReadChannel (new File (sFilename));
  }

  /**
   * Get a read-only {@link FileChannel} for the passed file.
   *
   * @param aFile
   *        The file to open. May not be <code>null</code>.
   * @return <code>null</code> if the file could not be opened.
   */
  @Nullable
  public static FileChannel getFileReadChannel (@NonNull final File aFile)
  {
    ValueEnforcer.notNull (aFile, "File");

    final FileInputStream aFIS = FileHelper.getInputStream (aFile);
    return aFIS == null ? null : aFIS.getChannel ();
  }

  /**
   * Get an input stream to the specified file, using memory mapping. If memory mapping fails, a
   * regular {@link FileInputStream} is returned.
   *
   * @param aFile
   *        The file to use. May not be <code>null</code>.
   * @return The Input stream to use.
   */
  @Nullable
  public static InputStream getMappedInputStream (@NonNull final File aFile)
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
    LOGGER.warn ("Failed to map file " + aFile + ". Falling though to regular FileInputStream");
    return aFIS;
  }

  @Nullable
  private static OutputStream _getMappedOutputStream (@NonNull @WillNotClose final FileChannel aChannel,
                                                      @NonNull final File aFile)
  {
    try
    {
      // Maximum is Integer.MAX_VALUE even if a long is taken!
      final MappedByteBuffer aBuffer = aChannel.map (MapMode.READ_WRITE, 0, Integer.MAX_VALUE);
      LOGGER.info ("Created memory mapped output stream for " + aFile);
      return new ByteBufferOutputStream (aBuffer, false);
    }
    catch (final IOException ex)
    {
      LOGGER.warn ("Failed to create memory mapped output stream for " + aFile, ex);
      return null;
    }
  }

  /**
   * Get a writable {@link FileChannel} for the passed filename using the default append mode.
   *
   * @param sFilename
   *        The name of the file to open. May not be <code>null</code>.
   * @return <code>null</code> if the file could not be opened.
   */
  @Nullable
  public static FileChannel getFileWriteChannel (@NonNull final String sFilename)
  {
    return getFileWriteChannel (sFilename, EAppend.DEFAULT);
  }

  /**
   * Get a writable {@link FileChannel} for the passed filename.
   *
   * @param sFilename
   *        The name of the file to open. May not be <code>null</code>.
   * @param eAppend
   *        Appending mode. May not be <code>null</code>.
   * @return <code>null</code> if the file could not be opened.
   */
  @Nullable
  public static FileChannel getFileWriteChannel (@NonNull final String sFilename, @NonNull final EAppend eAppend)
  {
    return getFileWriteChannel (new File (sFilename), eAppend);
  }

  /**
   * Get a writable {@link FileChannel} for the passed file using the default append mode.
   *
   * @param aFile
   *        The file to open. May not be <code>null</code>.
   * @return <code>null</code> if the file could not be opened.
   */
  @Nullable
  public static FileChannel getFileWriteChannel (@NonNull final File aFile)
  {
    return getFileWriteChannel (aFile, EAppend.DEFAULT);
  }

  /**
   * Get a writable {@link FileChannel} for the passed file.
   *
   * @param aFile
   *        The file to open. May not be <code>null</code>.
   * @param eAppend
   *        Appending mode. May not be <code>null</code>.
   * @return <code>null</code> if the file could not be opened.
   */
  @Nullable
  public static FileChannel getFileWriteChannel (@NonNull final File aFile, @NonNull final EAppend eAppend)
  {
    ValueEnforcer.notNull (aFile, "File");
    ValueEnforcer.notNull (eAppend, "Append");

    final FileOutputStream aFOS = FileHelper.getOutputStream (aFile, eAppend);
    return aFOS == null ? null : aFOS.getChannel ();
  }

  /**
   * Get a memory-mapped output stream for the passed filename using the default append mode.
   *
   * @param sFilename
   *        The name of the file to open. May not be <code>null</code>.
   * @return <code>null</code> if the file could not be opened or mapped.
   */
  @Nullable
  public static OutputStream getMappedOutputStream (@NonNull final String sFilename)
  {
    return getMappedOutputStream (new File (sFilename));
  }

  /**
   * Get a memory-mapped output stream for the passed filename.
   *
   * @param sFilename
   *        The name of the file to open. May not be <code>null</code>.
   * @param eAppend
   *        Appending mode. May not be <code>null</code>.
   * @return <code>null</code> if the file could not be opened or mapped.
   */
  @Nullable
  public static OutputStream getMappedOutputStream (@NonNull final String sFilename, @NonNull final EAppend eAppend)
  {
    return getMappedOutputStream (new File (sFilename), eAppend);
  }

  /**
   * Get a memory-mapped output stream for the passed file using the default append mode.
   *
   * @param aFile
   *        The file to open. May not be <code>null</code>.
   * @return <code>null</code> if the file could not be opened or mapped.
   */
  @Nullable
  public static OutputStream getMappedOutputStream (@NonNull final File aFile)
  {
    return getMappedOutputStream (aFile, EAppend.DEFAULT);
  }

  /**
   * Get a memory-mapped output stream for the passed file. If memory mapping fails, a regular
   * {@link FileOutputStream} is returned as a fallback.
   *
   * @param aFile
   *        The file to open. May not be <code>null</code>.
   * @param eAppend
   *        Appending mode. May not be <code>null</code>.
   * @return <code>null</code> if the file could not be opened.
   */
  @Nullable
  public static OutputStream getMappedOutputStream (@NonNull final File aFile, @NonNull final EAppend eAppend)
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
      LOGGER.error ("Failed to open random access file " + aFile);
      return null;
    }
    // Try to memory map it
    final OutputStream aOS = _getMappedOutputStream (aRAF.getChannel (), aFile);
    if (aOS != null)
      return aOS;

    // Memory mapping failed - return the original output stream
    StreamHelper.close (aRAF);
    LOGGER.warn ("Failed to map file " + aFile + ". Falling though to regular FileOutputStream");
    return FileHelper.getOutputStream (aFile, eAppend);
  }
}
