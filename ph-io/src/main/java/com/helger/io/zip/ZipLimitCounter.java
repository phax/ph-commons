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
package com.helger.io.zip;

import java.io.IOException;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.io.nonblocking.NonBlockingPushbackInputStream;

/**
 * Counts the entries and the uncompressed bytes of a ZIP archive against the limits of
 * {@link ZipWalkerSettings}, and checks the ZIP header. Shared by {@link ZipWalker} and
 * {@link LimitedZipInputStream}, so that the limits are implemented once.
 *
 * @author Philip Helger
 * @since 12.8.0
 */
@NotThreadSafe
final class ZipLimitCounter
{
  /** The length of the ZIP header that is checked */
  static final int ZIP_HEADER_LENGTH = 4;

  private final ZipWalkerSettings m_aSettings;
  private int m_nEntryCount;
  private long m_nTotalUncompressedBytes;
  private EZipWalkResult m_eViolation;

  ZipLimitCounter (@NonNull final ZipWalkerSettings aSettings)
  {
    m_aSettings = aSettings;
  }

  /**
   * Check if the provided bytes are the start of a ZIP archive: a local file header or the end of
   * central directory record of an empty archive.
   *
   * @param aHeader
   *        The first bytes. May not be <code>null</code>.
   * @return <code>true</code> if it is a ZIP header.
   */
  public static boolean isZipHeader (final byte @NonNull [] aHeader)
  {
    return aHeader.length == ZIP_HEADER_LENGTH &&
           aHeader[0] == 'P' &&
           aHeader[1] == 'K' &&
           ((aHeader[2] == 3 && aHeader[3] == 4) || (aHeader[2] == 5 && aHeader[3] == 6));
  }

  /**
   * Read the first bytes, check that they are a ZIP header and push them back.
   *
   * @param aPIS
   *        The stream to check. Must allow to push back {@link #ZIP_HEADER_LENGTH} bytes. May not
   *        be <code>null</code>.
   * @throws InvalidZipException
   *         If the content does not start with a ZIP header
   * @throws IOException
   *         On read error
   */
  static void checkZipHeader (@NonNull final NonBlockingPushbackInputStream aPIS) throws IOException
  {
    final byte [] aHeader = aPIS.readNBytes (ZIP_HEADER_LENGTH);
    if (!isZipHeader (aHeader))
      throw new InvalidZipException (EZipWalkResult.NOT_A_ZIP, "The content is not a ZIP archive");
    aPIS.unread (aHeader);
  }

  /**
   * Count one more entry.
   *
   * @throws InvalidZipException
   *         If the maximum number of entries is exceeded
   */
  void onEntry () throws InvalidZipException
  {
    m_nEntryCount++;
    if (m_nEntryCount > m_aSettings.getMaxEntryCount ())
    {
      m_eViolation = EZipWalkResult.MAX_ENTRY_COUNT_EXCEEDED;
      throw new InvalidZipException (m_eViolation,
                                     "The ZIP archive contains more than " +
                                                   m_aSettings.getMaxEntryCount () +
                                                   " entries");
    }
  }

  /**
   * Count uncompressed bytes.
   *
   * @param nBytes
   *        The number of bytes read. Values &le; 0 are ignored.
   * @throws InvalidZipException
   *         If the maximum total uncompressed size is exceeded
   */
  void onBytes (final long nBytes) throws InvalidZipException
  {
    if (nBytes > 0)
    {
      m_nTotalUncompressedBytes += nBytes;
      if (m_nTotalUncompressedBytes > m_aSettings.getMaxTotalUncompressedBytes ())
      {
        m_eViolation = EZipWalkResult.MAX_TOTAL_UNCOMPRESSED_BYTES_EXCEEDED;
        throw new InvalidZipException (m_eViolation,
                                       "The total uncompressed size of the ZIP archive exceeds " +
                                                     m_aSettings.getMaxTotalUncompressedBytes () +
                                                     " bytes");
      }
    }
  }

  @NonNull
  ZipWalkerSettings getSettings ()
  {
    return m_aSettings;
  }

  @Nonnegative
  int getEntryCount ()
  {
    return m_nEntryCount;
  }

  @Nonnegative
  long getTotalUncompressedBytes ()
  {
    return m_nTotalUncompressedBytes;
  }

  /**
   * @return The limit that was exceeded, even if the exception was caught by someone else.
   *         <code>null</code> if no limit was exceeded.
   */
  @Nullable
  EZipWalkResult getViolation ()
  {
    return m_eViolation;
  }
}
