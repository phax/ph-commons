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

import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.callback.CallbackList;
import com.helger.base.tostring.ToStringGenerator;

/**
 * Manage file operations, and persist the last errors for later retrieval, as well as offering a
 * callback mechanism for success/error handling.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class FileOperationManager implements IFileOperationManager
{
  public static final IFileOperationManager INSTANCE;
  static
  {
    // Add a logging callback by default
    final FileOperationManager aFOM = new FileOperationManager ();
    aFOM.callbacks ().add (new LoggingFileOperationCallback ());
    INSTANCE = aFOM;
  }

  private FileIOError m_aLastError;
  private final CallbackList <IFileOperationCallback> m_aCallbacks = new CallbackList <> ();

  public FileOperationManager ()
  {}

  @NonNull
  @ReturnsMutableObject
  public CallbackList <IFileOperationCallback> callbacks ()
  {
    return m_aCallbacks;
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

  private void _handleLastError (@NonNull final FileIOError aLastError)
  {
    if (m_aCallbacks.isNotEmpty ())
    {
      // Invoke callback
      if (aLastError.isSuccess ())
      {
        m_aCallbacks.forEach (x -> x.onSuccess (aLastError.getOperation (),
                                                aLastError.getFile1 (),
                                                aLastError.getFile2 ()));
      }
      else
      {
        m_aCallbacks.forEach (x -> x.onError (aLastError.getOperation (),
                                              aLastError.getErrorCode (),
                                              aLastError.getFile1 (),
                                              aLastError.getFile2 (),
                                              aLastError.getException ()));
      }
    }
  }

  @NonNull
  public FileIOError createDir (@NonNull final File aDir)
  {
    m_aLastError = FileOperations.createDir (aDir);
    _handleLastError (m_aLastError);
    return m_aLastError;
  }

  @NonNull
  public FileIOError createDirIfNotExisting (@NonNull final File aDir)
  {
    m_aLastError = FileOperations.createDirIfNotExisting (aDir);
    _handleLastError (m_aLastError);
    return m_aLastError;
  }

  @NonNull
  public FileIOError createDirRecursive (@NonNull final File aDir)
  {
    m_aLastError = FileOperations.createDirRecursive (aDir);
    _handleLastError (m_aLastError);
    return m_aLastError;
  }

  @NonNull
  public FileIOError createDirRecursiveIfNotExisting (@NonNull final File aDir)
  {
    m_aLastError = FileOperations.createDirRecursiveIfNotExisting (aDir);
    _handleLastError (m_aLastError);
    return m_aLastError;
  }

  @NonNull
  public FileIOError deleteDir (@NonNull final File aDir)
  {
    m_aLastError = FileOperations.deleteDir (aDir);
    _handleLastError (m_aLastError);
    return m_aLastError;
  }

  @NonNull
  public FileIOError deleteDirIfExisting (@NonNull final File aDir)
  {
    m_aLastError = FileOperations.deleteDirIfExisting (aDir);
    _handleLastError (m_aLastError);
    return m_aLastError;
  }

  @NonNull
  public FileIOError deleteDirRecursive (@NonNull final File aDir)
  {
    m_aLastError = FileOperations.deleteDirRecursive (aDir);
    _handleLastError (m_aLastError);
    return m_aLastError;
  }

  @NonNull
  public FileIOError deleteDirRecursiveIfExisting (@NonNull final File aDir)
  {
    m_aLastError = FileOperations.deleteDirRecursiveIfExisting (aDir);
    _handleLastError (m_aLastError);
    return m_aLastError;
  }

  @NonNull
  public FileIOError deleteFile (@NonNull final File aFile)
  {
    m_aLastError = FileOperations.deleteFile (aFile);
    _handleLastError (m_aLastError);
    return m_aLastError;
  }

  @NonNull
  public FileIOError deleteFileIfExisting (@NonNull final File aFile)
  {
    m_aLastError = FileOperations.deleteFileIfExisting (aFile);
    _handleLastError (m_aLastError);
    return m_aLastError;
  }

  @NonNull
  public FileIOError renameDir (@NonNull final File aSourceDir, @NonNull final File aTargetDir)
  {
    m_aLastError = FileOperations.renameDir (aSourceDir, aTargetDir);
    _handleLastError (m_aLastError);
    return m_aLastError;
  }

  @NonNull
  public FileIOError renameFile (@NonNull final File aSourceFile, @NonNull final File aTargetFile)
  {
    m_aLastError = FileOperations.renameFile (aSourceFile, aTargetFile);
    _handleLastError (m_aLastError);
    return m_aLastError;
  }

  @NonNull
  public FileIOError copyDirRecursive (@NonNull final File aSourceDir, @NonNull final File aTargetDir)
  {
    m_aLastError = FileOperations.copyDirRecursive (aSourceDir, aTargetDir);
    _handleLastError (m_aLastError);
    return m_aLastError;
  }

  @NonNull
  public FileIOError copyFile (@NonNull final File aSourceFile, @NonNull final File aTargetFile)
  {
    m_aLastError = FileOperations.copyFile (aSourceFile, aTargetFile);
    _handleLastError (m_aLastError);
    return m_aLastError;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("LastError", m_aLastError)
                                       .append ("Callback", m_aCallbacks)
                                       .getToString ();
  }
}
