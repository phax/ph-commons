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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Interface for a manager operating on the file system. Is implemented by
 * {@link FileOperationManager}.
 *
 * @author Philip Helger
 */
public interface IFileOperationManager
{
  /**
   * @return The last error that occurred. May be <code>null</code> if no action
   *         was yet performed.
   */
  @Nullable
  FileIOError getLastError ();

  /**
   * @return The last operation that was executed, independent of error or
   *         success. May be <code>null</code> if no action was performed yet.
   */
  @Nullable
  EFileIOOperation getLastOperation ();

  /**
   * Create a new directory. The direct parent directory already needs to exist.
   *
   * @param aDir
   *        The directory to be created. May not be <code>null</code>.
   * @return A non-<code>null</code> error code.
   */
  @NonNull
  FileIOError createDir (@NonNull File aDir);

  /**
   * Create a new directory if it does not exist. The direct parent directory
   * already needs to exist.
   *
   * @param aDir
   *        The directory to be created if it does not exist. May not be
   *        <code>null</code>.
   * @return A non-<code>null</code> error code.
   */
  @NonNull
  FileIOError createDirIfNotExisting (@NonNull File aDir);

  /**
   * Create a new directory. The parent directories are created if they are
   * missing.
   *
   * @param aDir
   *        The directory to be created. May not be <code>null</code>.
   * @return A non-<code>null</code> error code.
   */
  @NonNull
  FileIOError createDirRecursive (@NonNull File aDir);

  /**
   * Create a new directory if it does not exist. The direct parent directory
   * already needs to exist.
   *
   * @param aDir
   *        The directory to be created if it does not exist. May not be
   *        <code>null</code>.
   * @return A non-<code>null</code> error code.
   */
  @NonNull
  FileIOError createDirRecursiveIfNotExisting (@NonNull File aDir);

  /**
   * Delete an existing directory. The directory needs to be empty before it can
   * be deleted.
   *
   * @param aDir
   *        The directory to be deleted. May not be <code>null</code>.
   * @return A non-<code>null</code> error code.
   */
  @NonNull
  FileIOError deleteDir (@NonNull File aDir);

  /**
   * Delete an existing directory if it is existing. The directory needs to be
   * empty before it can be deleted.
   *
   * @param aDir
   *        The directory to be deleted. May not be <code>null</code>.
   * @return A non-<code>null</code> error code.
   */
  @NonNull
  FileIOError deleteDirIfExisting (@NonNull File aDir);

  /**
   * Delete an existing directory including all child objects.
   *
   * @param aDir
   *        The directory to be deleted. May not be <code>null</code>.
   * @return A non-<code>null</code> error code.
   */
  @NonNull
  FileIOError deleteDirRecursive (@NonNull File aDir);

  /**
   * Delete an existing directory including all child objects if it is existing.
   *
   * @param aDir
   *        The directory to be deleted. May not be <code>null</code>.
   * @return A non-<code>null</code> error code.
   */
  @NonNull
  FileIOError deleteDirRecursiveIfExisting (@NonNull File aDir);

  /**
   * Delete an existing file.
   *
   * @param aFile
   *        The file to be deleted. May not be <code>null</code>.
   * @return A non-<code>null</code> error code.
   */
  @NonNull
  FileIOError deleteFile (@NonNull File aFile);

  /**
   * Delete a file if it is existing.
   *
   * @param aFile
   *        The file to be deleted. May not be <code>null</code>.
   * @return A non-<code>null</code> error code.
   */
  @NonNull
  FileIOError deleteFileIfExisting (@NonNull File aFile);

  /**
   * Rename a directory.
   *
   * @param aSourceDir
   *        The original directory name. May not be <code>null</code>.
   * @param aTargetDir
   *        The destination directory name. May not be <code>null</code>.
   * @return A non-<code>null</code> error code.
   */
  @NonNull
  FileIOError renameDir (@NonNull File aSourceDir, @NonNull File aTargetDir);

  /**
   * Rename a file.
   *
   * @param aSourceFile
   *        The original file name. May not be <code>null</code>.
   * @param aTargetFile
   *        The destination file name. May not be <code>null</code>.
   * @return A non-<code>null</code> error code.
   */
  @NonNull
  FileIOError renameFile (@NonNull File aSourceFile, @NonNull File aTargetFile);

  /**
   * Copy a directory including all child objects.
   *
   * @param aSourceDir
   *        The source directory to be copied. May not be <code>null</code>.
   * @param aTargetDir
   *        The destination directory where to be copied. This directory may not
   *        be existing. May not be <code>null</code>.
   * @return A non-<code>null</code> error code.
   */
  @NonNull
  FileIOError copyDirRecursive (@NonNull File aSourceDir, @NonNull File aTargetDir);

  /**
   * Copies the source file to the target file.
   *
   * @param aSourceFile
   *        The source file to use. May not be <code>null</code>. Needs to be an
   *        existing file.
   * @param aTargetFile
   *        The destination files. May not be <code>null</code> and may not be
   *        an existing file.
   * @return A non-<code>null</code> error code.
   */
  @NonNull
  FileIOError copyFile (@NonNull File aSourceFile, @NonNull File aTargetFile);
}
