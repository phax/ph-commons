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
import java.util.zip.ZipException;
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
 * @since 12.8.0
 */
@Immutable
public final class ZipWalker
{
  @PresentForCodeCoverage
  private static final ZipWalker INSTANCE = new ZipWalker ();

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
   * fails with an {@link InvalidZipException} if it is exceeded. Closing it does not close the
   * archive.
   */
  private static final class LimitingInputStream extends FilterInputStream
  {
    private final ZipLimitCounter m_aCounter;

    LimitingInputStream (@NonNull final InputStream aIS, @NonNull final ZipLimitCounter aCounter)
    {
      super (aIS);
      m_aCounter = aCounter;
    }

    @Override
    public int read () throws IOException
    {
      final int ret = super.read ();
      if (ret >= 0)
        m_aCounter.onBytes (1);
      return ret;
    }

    @Override
    public int read (final byte [] aBuf, final int nOfs, final int nLen) throws IOException
    {
      final int ret = super.read (aBuf, nOfs, nLen);
      m_aCounter.onBytes (ret);
      return ret;
    }

    @Override
    public long skip (final long nCount) throws IOException
    {
      final long ret = super.skip (nCount);
      m_aCounter.onBytes (ret);
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

  /**
   * Get the next entry. The JDK throws an {@link IllegalArgumentException} if an entry name is not
   * valid in the charset of the archive - that is a broken archive like any other.
   *
   * @param aZIS
   *        The stream to read from. May not be <code>null</code>.
   * @return The next entry or <code>null</code> at the end of the archive.
   * @throws IOException
   *         On read error, including malformed entry names
   */
  @Nullable
  private static ZipEntry _getNextEntry (@NonNull final ZipInputStream aZIS) throws IOException
  {
    try
    {
      return aZIS.getNextEntry ();
    }
    catch (final IllegalArgumentException ex)
    {
      final ZipException ret = new ZipException ("Malformed ZIP entry: " + ex.getMessage ());
      ret.initCause (ex);
      throw ret;
    }
  }

  /**
   * Determine the result for an exception that ended the walk.
   *
   * @param ex
   *        The exception. May not be <code>null</code>.
   * @param aCounter
   *        The counter of the walk. May not be <code>null</code>.
   * @return The result. Never <code>null</code>.
   */
  @NonNull
  private static EZipWalkResult _getResult (@NonNull final IOException ex, @NonNull final ZipLimitCounter aCounter)
  {
    // A limit may have been exceeded although the callback caught the exception
    final EZipWalkResult eViolation = aCounter.getViolation ();
    if (eViolation != null)
      return eViolation;
    if (ex instanceof final InvalidZipException aIZEx)
      return aIZEx.getReason ();
    return EZipWalkResult.BROKEN;
  }

  @Nullable
  private static Failure _walkZipRecursive (@NonNull final InputStream aIS,
                                            @Nullable final String sArchivePath,
                                            @Nonnegative final int nDepth,
                                            @NonNull final ZipLimitCounter aCounter,
                                            @NonNull final IZipWalkerCallback aCallback)
  {
    // Check header
    final NonBlockingPushbackInputStream aPIS = new NonBlockingPushbackInputStream (aIS,
                                                                                    ZipLimitCounter.ZIP_HEADER_LENGTH);
    try
    {
      ZipLimitCounter.checkZipHeader (aPIS);
    }
    catch (final IOException ex)
    {
      return new Failure (_getResult (ex, aCounter), sArchivePath, ex);
    }

    final String sPathPrefix = sArchivePath == null ? "" : sArchivePath + "/";
    String sPath = sArchivePath;

    // Not closed on purpose: the outermost stream is closed by the caller, and a nested archive
    // must not close the enclosing one
    final ZipInputStream aZIS = new ZipInputStream (aPIS);
    try
    {
      ZipEntry aEntry;
      while ((aEntry = _getNextEntry (aZIS)) != null)
      {
        sPath = sPathPrefix + aEntry.getName ();
        aCounter.onEntry ();

        if (aEntry.isDirectory ())
        {
          if (aCallback.onDirectoryEntry (sPath, aEntry, nDepth).isBreak ())
            return new Failure (EZipWalkResult.STOPPED_BY_CALLBACK, sPath, null);
          continue;
        }

        if (aCallback.isNestedArchive (sPath, aEntry))
        {
          if (nDepth >= aCounter.getSettings ().getMaxNestingDepth ())
            return new Failure (EZipWalkResult.MAX_NESTING_DEPTH_EXCEEDED, sPath, null);

          // The bytes of the nested archive are not counted - only the bytes of its entries
          final Failure aNestedFailure = _walkZipRecursive (new NonClosingInputStream (aZIS),
                                                            sPath,
                                                            nDepth + 1,
                                                            aCounter,
                                                            aCallback);
          if (aNestedFailure != null)
            return aNestedFailure;

          // Anything after the end of the nested archive is counted
          _readRest (new LimitingInputStream (aZIS, aCounter));
        }
        else
        {
          final InputStream aEntryIS = new LimitingInputStream (aZIS, aCounter);
          final boolean bContinue = aCallback.onEntry (sPath, aEntry, nDepth, aEntryIS).isContinue ();

          // The callback may have caught the exception of the limiting stream
          final EZipWalkResult eViolation = aCounter.getViolation ();
          if (eViolation != null)
            return new Failure (eViolation, sPath, null);
          if (!bContinue)
            return new Failure (EZipWalkResult.STOPPED_BY_CALLBACK, sPath, null);

          // Read what the callback left, to apply the limits and detect corrupt entries
          _readRest (aEntryIS);
        }
      }
    }
    catch (final IOException ex)
    {
      return new Failure (_getResult (ex, aCounter), sPath, ex);
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

    final ZipLimitCounter aCounter = new ZipLimitCounter (aSettings);
    Failure aFailure;
    try (final InputStream aRealIS = aIS)
    {
      aFailure = _walkZipRecursive (aRealIS, null, 0, aCounter, aCallback);
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
                                aCounter.getEntryCount (),
                                aCounter.getTotalUncompressedBytes ());
    }
    return new ZipWalkResult (aFailure.m_eResult,
                              aFailure.m_sEntryPath,
                              aFailure.m_aException,
                              aCounter.getEntryCount (),
                              aCounter.getTotalUncompressedBytes ());
  }
}
