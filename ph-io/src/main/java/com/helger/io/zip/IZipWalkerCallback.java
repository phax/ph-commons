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
import java.io.InputStream;
import java.util.zip.ZipEntry;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.base.state.EContinue;

/**
 * Callback for the {@link ZipWalker}, invoked for every entry of an archive: directory entries are
 * passed to {@link #onDirectoryEntry(String, ZipEntry, int)}, nested archives to
 * {@link #isNestedArchive(String, ZipEntry)} and all other file entries to
 * {@link #onEntry(String, ZipEntry, int, InputStream)}.
 *
 * @author Philip Helger
 * @since 12.8.0
 */
@FunctionalInterface
public interface IZipWalkerCallback
{
  /**
   * Called for every directory entry. Directory entries have no content.
   *
   * @param sPath
   *        The path of the entry, including the paths of the enclosing nested archives separated by
   *        <code>/</code>, e.g. <code>inner.zip/dir/</code>. Never <code>null</code>.
   * @param aEntry
   *        The ZIP entry. Never <code>null</code>.
   * @param nDepth
   *        The nesting depth. 0 for the entries of the outermost archive.
   * @return {@link EContinue#CONTINUE} to continue with the next entry, {@link EContinue#BREAK} to
   *         stop the walk with {@link EZipWalkResult#STOPPED_BY_CALLBACK}. May not be
   *         <code>null</code>. The default implementation returns {@link EContinue#CONTINUE}.
   */
  @NonNull
  default EContinue onDirectoryEntry (@NonNull @Nonempty final String sPath,
                                      @NonNull final ZipEntry aEntry,
                                      @Nonnegative final int nDepth)
  {
    return EContinue.CONTINUE;
  }

  /**
   * Check if the provided entry is a nested archive that the walker should descend into. Nested
   * archives are not passed to {@link #onEntry(String, ZipEntry, int, InputStream)}, but their
   * entries are.
   *
   * @param sPath
   *        The path of the entry, including the paths of the enclosing nested archives separated by
   *        <code>/</code>, e.g. <code>inner.zip/schema.xsd</code>. Never <code>null</code>.
   * @param aEntry
   *        The ZIP entry. Never <code>null</code>.
   * @return <code>true</code> to descend into the entry as nested archive. The default
   *         implementation returns <code>false</code>.
   */
  default boolean isNestedArchive (@NonNull @Nonempty final String sPath, @NonNull final ZipEntry aEntry)
  {
    return false;
  }

  /**
   * Called for every file entry that is not a nested archive. The callback does not need to read
   * the stream completely: the walker reads the rest, so that the limits are applied and a corrupt
   * entry is detected in any case.
   *
   * @param sPath
   *        The path of the entry, including the paths of the enclosing nested archives separated by
   *        <code>/</code>, e.g. <code>inner.zip/schema.xsd</code>. Never <code>null</code>.
   * @param aEntry
   *        The ZIP entry. Never <code>null</code>.
   * @param nDepth
   *        The nesting depth. 0 for the entries of the outermost archive.
   * @param aIS
   *        The uncompressed content of the entry. It is limited to the remaining allowed size and
   *        closing it does not close the archive. Never <code>null</code>.
   * @return {@link EContinue#CONTINUE} to continue with the next entry, {@link EContinue#BREAK} to
   *         stop the walk with {@link EZipWalkResult#STOPPED_BY_CALLBACK}. May not be
   *         <code>null</code>.
   * @throws IOException
   *         If reading the entry fails. The walk ends with {@link EZipWalkResult#BROKEN}, or with
   *         {@link EZipWalkResult#MAX_TOTAL_UNCOMPRESSED_BYTES_EXCEEDED} if the size limit was hit.
   */
  @NonNull
  EContinue onEntry (@NonNull @Nonempty String sPath,
                     @NonNull ZipEntry aEntry,
                     @Nonnegative int nDepth,
                     @NonNull InputStream aIS) throws IOException;
}
