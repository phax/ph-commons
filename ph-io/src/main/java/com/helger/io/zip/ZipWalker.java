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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.io.nonblocking.NonBlockingPushbackInputStream;
import com.helger.base.io.stream.NonClosingInputStream;

/**
 * Walks all entries of a ZIP archive, optionally including nested archives, and passes each entry
 * to an {@link IZipWalkerCallback}. While walking, the limits of the
 * {@link ZipWalkerSettings} are applied, so that ZIP bombs and similar archives are refused instead
 * of exhausting memory, disk or stack. Content that does not start with a ZIP header is refused as
 * well - a plain {@link ZipInputStream} would silently return no entries for it.
 *
 * @author Philip Helger
 * @since 12.7.2
 */
@Immutable
public final class ZipWalker
{
  /** The length of the ZIP header that is checked */
  private static final int ZIP_HEADER_LENGTH = 4;

  @PresentForCodeCoverage
  private static final ZipWalker INSTANCE = new ZipWalker ();

  /**
   * The mutable state of one walk, shared by an archive and all its nested archives.
   */
  private static final class WalkState
  {
    private final ZipWalkerSettings m_aSettings;
    private int m_nEntryCount;
    private long m_nTotalUncompressedBytes;
    private boolean m_bSizeExceeded;

    WalkState (@NonNull final ZipWalkerSettings aSettings)
    {
      m_aSettings = aSettings;
    }
  }

  /**
   * The reason why a walk ended unsuccessfully.
   */
  private static final class Failure
  {
    private final EZipWalkResult m_eResult;
    private final String m_sEntryPath;
    private final Exception m_aException;

    Failure (@NonNull final EZipWalkResult eResult,
             @Nullable final String sEntryPath,
             @Nullable final Exception aException)
    {
      m_eResult = eResult;
      m_sEntryPath = sEntryPath;
      m_aException = aException;
    }
  }

  /**
   * An input stream that counts the read bytes against the allowed total uncompressed size and
   * fails with an {@link IOException} if it is exceeded. Closing it does not close the archive.
   */
  private static final class LimitingInputStream extends FilterInputStream
  {
    private final WalkState m_aState;

    LimitingInputStream (@NonNull final InputStream aIS, @NonNull final WalkState aState)
    {
      super (aIS);
      m_aState = aState;
    }

    private void _consumed (final long nBytes) throws IOException
    {
      if (nBytes > 0)
      {
        m_aState.m_nTotalUncompressedBytes += nBytes;
        if (m_aState.m_nTotalUncompressedBytes > m_aState.m_aSettings.getMaxTotalUncompressedBytes ())
        {
          m_aState.m_bSizeExceeded = true;
          throw new IOException ("The total uncompressed size exceeds " +
                                 m_aState.m_aSettings.getMaxTotalUncompressedBytes () +
                                 " bytes");
        }
      }
    }

    @Override
    public int read () throws IOException
    {
      final int ret = super.read ();
      if (ret >= 0)
        _consumed (1);
      return ret;
    }

    @Override
    public int read (final byte [] aBuf, final int nOfs, final int nLen) throws IOException
    {
      final int ret = super.read (aBuf, nOfs, nLen);
      _consumed (ret);
      return ret;
    }

    @Override
    public long skip (final long nCount) throws IOException
    {
      final long ret = super.skip (nCount);
      _consumed (ret);
      return ret;
    }

    @Override
    public void close ()
    {
      // Never close the archive - the next entry follows
    }
  }

  private ZipWalker ()
  {}

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
    return aHeader.length >= ZIP_HEADER_LENGTH &&
           aHeader[0] == 'P' &&
           aHeader[1] == 'K' &&
           ((aHeader[2] == 3 && aHeader[3] == 4) || (aHeader[2] == 5 && aHeader[3] == 6));
  }

  /**
   * Read the rest of a stream, so that the limits are applied to the complete content.
   *
   * @param aIS
   *        The stream to read. May not be <code>null</code>.
   * @throws IOException
   *         On read error or if a limit is exceeded
   */
  private static void _readRest (@NonNull final InputStream aIS) throws IOException
  {
    final byte [] aBuffer = new byte [16 * 1024];
    while (aIS.read (aBuffer) >= 0)
    {
      // Just read
    }
  }

  @Nullable
  private static Failure _walkZipRecursive (@NonNull final InputStream aIS,
                                            @Nullable final String sArchivePath,
                                            @Nonnegative final int nDepth,
                                            @NonNull final WalkState aState,
                                            @NonNull final IZipWalkerCallback aCallback)
  {
    // Check header
    final NonBlockingPushbackInputStream aPIS = new NonBlockingPushbackInputStream (aIS, ZIP_HEADER_LENGTH);
    try
    {
      final byte [] aHeader = aPIS.readNBytes (ZIP_HEADER_LENGTH);
      if (!isZipHeader (aHeader))
        return new Failure (EZipWalkResult.NOT_A_ZIP, sArchivePath, null);
      aPIS.unread (aHeader);
    }
    catch (final IOException ex)
    {
      return new Failure (aState.m_bSizeExceeded ? EZipWalkResult.MAX_TOTAL_UNCOMPRESSED_BYTES_EXCEEDED
                                                 : EZipWalkResult.BROKEN, sArchivePath, ex);
    }

    final String sPathPrefix = sArchivePath == null ? "" : sArchivePath + "/";
    String sPath = sArchivePath;

    // Not closed on purpose: the outermost stream is closed by the caller, and a nested archive
    // must not close the enclosing one
    final ZipInputStream aZIS = new ZipInputStream (aPIS);
    try
    {
      ZipEntry aEntry;
      while ((aEntry = aZIS.getNextEntry ()) != null)
      {
        sPath = sPathPrefix + aEntry.getName ();
        aState.m_nEntryCount++;
        if (aState.m_nEntryCount > aState.m_aSettings.getMaxEntryCount ())
          return new Failure (EZipWalkResult.MAX_ENTRY_COUNT_EXCEEDED, sPath, null);

        if (aEntry.isDirectory ())
        {
          if (aCallback.onDirectoryEntry (sPath, aEntry, nDepth).isBreak ())
            return new Failure (EZipWalkResult.STOPPED_BY_CALLBACK, sPath, null);
          continue;
        }

        if (aCallback.isNestedArchive (sPath, aEntry))
        {
          if (nDepth >= aState.m_aSettings.getMaxNestingDepth ())
            return new Failure (EZipWalkResult.MAX_NESTING_DEPTH_EXCEEDED, sPath, null);

          // The bytes of the nested archive are not counted - only the bytes of its entries
          final Failure aNestedFailure = _walkZipRecursive (new NonClosingInputStream (aZIS),
                                                            sPath,
                                                            nDepth + 1,
                                                            aState,
                                                            aCallback);
          if (aNestedFailure != null)
            return aNestedFailure;

          // Anything after the end of the nested archive is counted
          _readRest (new LimitingInputStream (aZIS, aState));
        }
        else
        {
          final InputStream aEntryIS = new LimitingInputStream (aZIS, aState);
          final boolean bContinue = aCallback.onEntry (sPath, aEntry, nDepth, aEntryIS).isContinue ();

          // The callback may have caught the exception of the limiting stream
          if (aState.m_bSizeExceeded)
            return new Failure (EZipWalkResult.MAX_TOTAL_UNCOMPRESSED_BYTES_EXCEEDED, sPath, null);
          if (!bContinue)
            return new Failure (EZipWalkResult.STOPPED_BY_CALLBACK, sPath, null);

          // Read what the callback left, to apply the limits and detect corrupt entries
          _readRest (aEntryIS);
        }
      }
    }
    catch (final IOException ex)
    {
      return new Failure (aState.m_bSizeExceeded ? EZipWalkResult.MAX_TOTAL_UNCOMPRESSED_BYTES_EXCEEDED
                                                 : EZipWalkResult.BROKEN, sPath, ex);
    }
    return null;
  }

  /**
   * Walk all entries of a ZIP archive and pass each entry to the callback.
   *
   * @param aIS
   *        The archive to walk. It is closed by this method. May not be <code>null</code>.
   * @param aSettings
   *        The limits to apply. May not be <code>null</code>.
   * @param aCallback
   *        The callback for each entry. It also decides which entries are nested archives. May
   *        not be <code>null</code>.
   * @return The result of the walk. Never <code>null</code>.
   */
  @NonNull
  public static ZipWalkResult walk (@NonNull final InputStream aIS,
                                    @NonNull final ZipWalkerSettings aSettings,
                                    @NonNull final IZipWalkerCallback aCallback)
  {
    ValueEnforcer.notNull (aIS, "InputStream");
    ValueEnforcer.notNull (aSettings, "Settings");
    ValueEnforcer.notNull (aCallback, "Callback");

    final WalkState aState = new WalkState (aSettings);
    Failure aFailure;
    try (final InputStream aRealIS = aIS)
    {
      aFailure = _walkZipRecursive (aRealIS, null, 0, aState, aCallback);
    }
    catch (final IOException ex)
    {
      // Only from closing
      aFailure = new Failure (EZipWalkResult.BROKEN, null, ex);
    }

    if (aFailure == null)
    {
      // We managed to walk
      return new ZipWalkResult (EZipWalkResult.SUCCESS,
                                null,
                                null,
                                aState.m_nEntryCount,
                                aState.m_nTotalUncompressedBytes);
    }
    return new ZipWalkResult (aFailure.m_eResult,
                              aFailure.m_sEntryPath,
                              aFailure.m_aException,
                              aState.m_nEntryCount,
                              aState.m_nTotalUncompressedBytes);
  }
}
