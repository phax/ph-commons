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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.state.ISuccessIndicator;
import com.helger.base.tostring.ToStringGenerator;

/**
 * The result of walking a ZIP archive with the {@link ZipWalker}: the outcome, the entry where the
 * walk ended unsuccessfully and some statistics.
 *
 * @author Philip Helger
 * @since 12.7.2
 */
@Immutable
public final class ZipWalkResult implements ISuccessIndicator
{
  private final EZipWalkResult m_eResult;
  private final String m_sEntryPath;
  private final Exception m_aException;
  private final int m_nEntryCount;
  private final long m_nTotalUncompressedBytes;

  /**
   * Constructor.
   *
   * @param eResult
   *        The outcome. May not be <code>null</code>.
   * @param sEntryPath
   *        The path of the entry where the walk ended unsuccessfully. May be <code>null</code>.
   * @param aException
   *        The exception that ended the walk. May be <code>null</code>.
   * @param nEntryCount
   *        The number of entries walked, including directories and nested archives.
   * @param nTotalUncompressedBytes
   *        The total uncompressed size of all entries that are not nested archives.
   */
  public ZipWalkResult (@NonNull final EZipWalkResult eResult,
                        @Nullable final String sEntryPath,
                        @Nullable final Exception aException,
                        @Nonnegative final int nEntryCount,
                        @Nonnegative final long nTotalUncompressedBytes)
  {
    ValueEnforcer.notNull (eResult, "Result");
    ValueEnforcer.isGE0 (nEntryCount, "EntryCount");
    ValueEnforcer.isGE0 (nTotalUncompressedBytes, "TotalUncompressedBytes");
    m_eResult = eResult;
    m_sEntryPath = sEntryPath;
    m_aException = aException;
    m_nEntryCount = nEntryCount;
    m_nTotalUncompressedBytes = nTotalUncompressedBytes;
  }

  /**
   * @return The outcome of the walk. Never <code>null</code>.
   */
  @NonNull
  public EZipWalkResult getResult ()
  {
    return m_eResult;
  }

  public boolean isSuccess ()
  {
    return m_eResult.isSuccess ();
  }

  /**
   * @return The path of the entry where the walk ended unsuccessfully, including the paths of the
   *         enclosing nested archives. <code>null</code> if the walk was successful or if the
   *         archive itself is affected (e.g. {@link EZipWalkResult#NOT_A_ZIP} of the outermost
   *         archive).
   */
  @Nullable
  public String getEntryPath ()
  {
    return m_sEntryPath;
  }

  /**
   * @return The exception that ended the walk, e.g. for {@link EZipWalkResult#BROKEN}. May be
   *         <code>null</code>.
   */
  @Nullable
  public Exception getException ()
  {
    return m_aException;
  }

  /**
   * @return The number of entries walked, including directories and nested archives.
   */
  @Nonnegative
  public int getEntryCount ()
  {
    return m_nEntryCount;
  }

  /**
   * @return The total uncompressed size in bytes of all entries walked that are not nested
   *         archives.
   */
  @Nonnegative
  public long getTotalUncompressedBytes ()
  {
    return m_nTotalUncompressedBytes;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Result", m_eResult)
                                       .appendIfNotNull ("EntryPath", m_sEntryPath)
                                       .appendIfNotNull ("Exception", m_aException)
                                       .append ("EntryCount", m_nEntryCount)
                                       .append ("TotalUncompressedBytes", m_nTotalUncompressedBytes)
                                       .getToString ();
  }
}
