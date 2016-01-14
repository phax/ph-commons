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
package com.helger.commons.io.file;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.string.ToStringGenerator;

/**
 * Manage file operations, and persist the last errors for later retrieval, as
 * well as offering a callback mechanism for success/error handling.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class FileOperationManager implements IFileOperationManager
{
  private FileIOError m_aLastError;
  private final IFileOperationCallback m_aCallback;

  public FileOperationManager ()
  {
    this (null);
  }

  public FileOperationManager (@Nullable final IFileOperationCallback aCallback)
  {
    m_aCallback = aCallback;
  }

  @Nullable
  public FileIOError getLastError ()
  {
    return m_aLastError;
  }

  @Nullable
  public EFileIOOperation getLastOperation ()
  {
    return m_aLastError == null ? null : m_aLastError.getOperation ();
  }

  private void _handleLastError (@Nonnull final FileIOError aLastError)
  {
    if (m_aCallback != null)
    {
      // Invoke callback
      if (aLastError.isSuccess ())
        m_aCallback.onSuccess (aLastError.getOperation (), aLastError.getFile1 (), aLastError.getFile2 ());
      else
        m_aCallback.onError (aLastError.getOperation (),
                             aLastError.getErrorCode (),
                             aLastError.getFile1 (),
                             aLastError.getFile2 (),
                             aLastError.getException ());
    }
  }

  @Nonnull
  public FileIOError createDir (@Nonnull final File aDir)
  {
    m_aLastError = FileOperations.createDir (aDir);
    _handleLastError (m_aLastError);
    return m_aLastError;
  }

  @Nonnull
  public FileIOError createDirIfNotExisting (@Nonnull final File aDir)
  {
    m_aLastError = FileOperations.createDirIfNotExisting (aDir);
    _handleLastError (m_aLastError);
    return m_aLastError;
  }

  @Nonnull
  public FileIOError createDirRecursive (@Nonnull final File aDir)
  {
    m_aLastError = FileOperations.createDirRecursive (aDir);
    _handleLastError (m_aLastError);
    return m_aLastError;
  }

  @Nonnull
  public FileIOError createDirRecursiveIfNotExisting (@Nonnull final File aDir)
  {
    m_aLastError = FileOperations.createDirRecursiveIfNotExisting (aDir);
    _handleLastError (m_aLastError);
    return m_aLastError;
  }

  @Nonnull
  public FileIOError deleteDir (@Nonnull final File aDir)
  {
    m_aLastError = FileOperations.deleteDir (aDir);
    _handleLastError (m_aLastError);
    return m_aLastError;
  }

  @Nonnull
  public FileIOError deleteDirIfExisting (@Nonnull final File aDir)
  {
    m_aLastError = FileOperations.deleteDirIfExisting (aDir);
    _handleLastError (m_aLastError);
    return m_aLastError;
  }

  @Nonnull
  public FileIOError deleteDirRecursive (@Nonnull final File aDir)
  {
    m_aLastError = FileOperations.deleteDirRecursive (aDir);
    _handleLastError (m_aLastError);
    return m_aLastError;
  }

  @Nonnull
  public FileIOError deleteDirRecursiveIfExisting (@Nonnull final File aDir)
  {
    m_aLastError = FileOperations.deleteDirRecursiveIfExisting (aDir);
    _handleLastError (m_aLastError);
    return m_aLastError;
  }

  @Nonnull
  public FileIOError deleteFile (@Nonnull final File aFile)
  {
    m_aLastError = FileOperations.deleteFile (aFile);
    _handleLastError (m_aLastError);
    return m_aLastError;
  }

  @Nonnull
  public FileIOError deleteFileIfExisting (@Nonnull final File aFile)
  {
    m_aLastError = FileOperations.deleteFileIfExisting (aFile);
    _handleLastError (m_aLastError);
    return m_aLastError;
  }

  @Nonnull
  public FileIOError renameDir (@Nonnull final File aSourceDir, @Nonnull final File aTargetDir)
  {
    m_aLastError = FileOperations.renameDir (aSourceDir, aTargetDir);
    _handleLastError (m_aLastError);
    return m_aLastError;
  }

  @Nonnull
  public FileIOError renameFile (@Nonnull final File aSourceFile, @Nonnull final File aTargetFile)
  {
    m_aLastError = FileOperations.renameFile (aSourceFile, aTargetFile);
    _handleLastError (m_aLastError);
    return m_aLastError;
  }

  @Nonnull
  public FileIOError copyDirRecursive (@Nonnull final File aSourceDir, @Nonnull final File aTargetDir)
  {
    m_aLastError = FileOperations.copyDirRecursive (aSourceDir, aTargetDir);
    _handleLastError (m_aLastError);
    return m_aLastError;
  }

  @Nonnull
  public FileIOError copyFile (@Nonnull final File aSourceFile, @Nonnull final File aTargetFile)
  {
    m_aLastError = FileOperations.copyFile (aSourceFile, aTargetFile);
    _handleLastError (m_aLastError);
    return m_aLastError;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("lastError", m_aLastError).append ("callback", m_aCallback).toString ();
  }
}
