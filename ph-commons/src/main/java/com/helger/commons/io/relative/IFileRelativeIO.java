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
package com.helger.commons.io.relative;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.io.EAppend;
import com.helger.commons.io.file.FileIOError;
import com.helger.commons.io.file.FileOperationManager;
import com.helger.commons.io.file.FilenameHelper;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.state.ESuccess;

/**
 * An extended {@link IPathRelativeIO} version that uses files as the basis.
 *
 * @author Philip Helger
 * @since 7.1.1
 */
public interface IFileRelativeIO extends IPathRelativeIO
{
  /**
   * @return The base path. Never <code>null</code>.
   */
  @Nonnull
  File getBasePathFile ();

  /**
   * @return The absolute base path that is used. Neither <code>null</code> nor
   *         empty.
   */
  @Nonnull
  @Nonempty
  default String getBasePath ()
  {
    return getBasePathFile ().getAbsolutePath ();
  }

  /**
   * Get a {@link File} relative to the base path.
   *
   * @param sRelativePath
   *        the relative path
   * @return The "absolute" {@link File} and never <code>null</code>.
   * @see #getBasePathFile()
   */
  @Nonnull
  default File getFile (@Nonnull final String sRelativePath)
  {
    return new File (getBasePathFile (), sRelativePath);
  }

  /**
   * Get the file system resource relative to the base path
   *
   * @param sRelativePath
   *        the relative path
   * @return The "absolute" {@link FileSystemResource} and never
   *         <code>null</code>.
   * @see #getBasePathFile()
   */
  @Nonnull
  default FileSystemResource getResource (@Nonnull final String sRelativePath)
  {
    return new FileSystemResource (getFile (sRelativePath));
  }

  /**
   * Get the relative file name for the passed absolute file.
   *
   * @param aAbsoluteFile
   *        The non-<code>null</code> absolute file to make relative.
   * @return <code>null</code> if the passed file is not a child of this base
   *         directory.
   */
  @Nullable
  default String getRelativeFilename (@Nonnull final File aAbsoluteFile)
  {
    return FilenameHelper.getRelativeToParentDirectory (aAbsoluteFile, getBasePathFile ());
  }

  /**
   * Check if a file relative to the base path exists
   *
   * @param sRelativePath
   *        the relative path
   * @return <code>true</code> if the {@link File} is a file and exists,
   *         <code>false</code> otherwise.
   * @see #getBasePathFile()
   */
  default boolean existsFile (@Nonnull final String sRelativePath)
  {
    return getFile (sRelativePath).isFile ();
  }

  /**
   * Check if a directory relative to the base path exists
   *
   * @param sRelativePath
   *        the relative path
   * @return <code>true</code> if the {@link File} is a directory and exists,
   *         <code>false</code> otherwise.
   * @see #getBasePathFile()
   */
  default boolean existsDir (@Nonnull final String sRelativePath)
  {
    return getFile (sRelativePath).isDirectory ();
  }

  /**
   * Get the {@link OutputStream} relative to the base path. An eventually
   * existing file is truncated.
   *
   * @param sRelativePath
   *        the relative path
   * @return <code>null</code> if the path is not writable
   * @see #getBasePathFile()
   */
  @Nullable
  default OutputStream getOutputStream (@Nonnull final String sRelativePath)
  {
    return getOutputStream (sRelativePath, EAppend.TRUNCATE);
  }

  /**
   * Get the {@link OutputStream} relative to the base path
   *
   * @param sRelativePath
   *        the relative path
   * @param eAppend
   *        Append or truncate mode. May not be <code>null</code>.
   * @return <code>null</code> if the path is not writable
   * @see #getBasePathFile()
   */
  @Nullable
  default OutputStream getOutputStream (@Nonnull final String sRelativePath, @Nonnull final EAppend eAppend)
  {
    return getResource (sRelativePath).getOutputStream (eAppend);
  }

  /**
   * Get the {@link Writer} relative to the base path. An eventually existing
   * file is truncated.
   *
   * @param sRelativePath
   *        the relative path
   * @param aCharset
   *        The charset to use. May not be <code>null</code>.
   * @return <code>null</code> if the path is not writable
   * @see #getBasePathFile()
   */
  @Nullable
  default Writer getWriter (@Nonnull final String sRelativePath, @Nonnull final Charset aCharset)
  {
    return getWriter (sRelativePath, aCharset, EAppend.TRUNCATE);
  }

  /**
   * Get the {@link Writer} relative to the base path.
   *
   * @param sRelativePath
   *        the relative path
   * @param aCharset
   *        The charset to use. May not be <code>null</code>.
   * @param eAppend
   *        Append or truncate mode. May not be <code>null</code>.
   * @return <code>null</code> if the path is not writable
   * @see #getBasePathFile()
   */
  @Nullable
  default Writer getWriter (@Nonnull final String sRelativePath,
                            @Nonnull final Charset aCharset,
                            @Nonnull final EAppend eAppend)
  {
    return getResource (sRelativePath).getWriter (aCharset, eAppend);
  }

  /**
   * Create the appropriate directory if it is not existing
   *
   * @param sRelativePath
   *        the relative path
   * @param bRecursive
   *        if <code>true</code> all missing parent directories will be created
   * @return Success indicator. Never <code>null</code>.
   * @see #getBasePathFile()
   */
  @Nonnull
  default FileIOError createDirectory (@Nonnull final String sRelativePath, final boolean bRecursive)
  {
    final File aDir = getFile (sRelativePath);
    return bRecursive ? FileOperationManager.INSTANCE.createDirRecursiveIfNotExisting (aDir)
                      : FileOperationManager.INSTANCE.createDirIfNotExisting (aDir);
  }

  @Nonnull
  default FileIOError deleteDirectory (@Nonnull final String sRelativePath, final boolean bDeleteRecursively)
  {
    final File aDir = getFile (sRelativePath);
    return bDeleteRecursively ? FileOperationManager.INSTANCE.deleteDirRecursive (aDir)
                              : FileOperationManager.INSTANCE.deleteDir (aDir);
  }

  @Nonnull
  default FileIOError deleteDirectoryIfExisting (@Nonnull final String sRelativePath, final boolean bDeleteRecursively)
  {
    final File aDir = getFile (sRelativePath);
    return bDeleteRecursively ? FileOperationManager.INSTANCE.deleteDirRecursiveIfExisting (aDir)
                              : FileOperationManager.INSTANCE.deleteDirIfExisting (aDir);
  }

  @Nonnull
  default FileIOError deleteFile (@Nonnull final String sRelativePath)
  {
    return FileOperationManager.INSTANCE.deleteFile (getFile (sRelativePath));
  }

  @Nonnull
  default FileIOError deleteFileIfExisting (@Nonnull final String sRelativePath)
  {
    return FileOperationManager.INSTANCE.deleteFileIfExisting (getFile (sRelativePath));
  }

  @Nonnull
  default FileIOError renameDir (@Nonnull final String sOldDirName, @Nonnull final String sNewDirName)
  {
    final File fOld = getFile (sOldDirName);
    final File fNew = getFile (sNewDirName);
    return FileOperationManager.INSTANCE.renameDir (fOld, fNew);
  }

  @Nonnull
  default FileIOError renameFile (@Nonnull final String sOldFilename, @Nonnull final String sNewFilename)
  {
    final File fOld = getFile (sOldFilename);
    final File fNew = getFile (sNewFilename);
    return FileOperationManager.INSTANCE.renameFile (fOld, fNew);
  }

  /**
   * Helper function for saving a file with correct error handling.
   *
   * @param sRelativePath
   *        name of the file. May not be <code>null</code>.
   * @param eAppend
   *        Appending mode. May not be <code>null</code>.
   * @param aBytes
   *        the bytes to be written. May not be <code>null</code>.
   * @return {@link ESuccess}
   */
  @Nonnull
  default ESuccess writeFile (@Nonnull final String sRelativePath,
                              @Nonnull final EAppend eAppend,
                              @Nonnull final byte [] aBytes)
  {
    // save to file
    final OutputStream aOS = getOutputStream (sRelativePath, eAppend);
    if (aOS == null)
      return ESuccess.FAILURE;

    // Close the OS automatically!
    return StreamHelper.writeStream (aOS, aBytes);
  }

  /**
   * Helper function for saving a file with correct error handling.
   *
   * @param sRelativePath
   *        name of the file. May not be <code>null</code>.
   * @param sContent
   *        the content to save. May not be <code>null</code>.
   * @param aCharset
   *        The character set to use. May not be <code>null</code>.
   * @return {@link ESuccess}
   */
  @Nonnull
  default ESuccess saveFile (@Nonnull final String sRelativePath,
                             @Nonnull final String sContent,
                             @Nonnull final Charset aCharset)
  {
    return saveFile (sRelativePath, sContent.getBytes (aCharset));
  }

  /**
   * Helper function for saving a file with correct error handling.
   *
   * @param sRelativePath
   *        name of the file. May not be <code>null</code>.
   * @param aBytes
   *        the bytes to be written. May not be <code>null</code>.
   * @return {@link ESuccess}
   */
  @Nonnull
  default ESuccess saveFile (@Nonnull final String sRelativePath, final byte [] aBytes)
  {
    return writeFile (sRelativePath, EAppend.TRUNCATE, aBytes);
  }

  /**
   * Helper function for saving a file with correct error handling.
   *
   * @param sRelativePath
   *        name of the file. May not be <code>null</code>.
   * @param sContent
   *        the content to save. May not be <code>null</code>.
   * @param aCharset
   *        The character set to use. May not be <code>null</code>.
   * @return {@link ESuccess}
   */
  @Nonnull
  default ESuccess appendFile (@Nonnull final String sRelativePath,
                               @Nonnull final String sContent,
                               @Nonnull final Charset aCharset)
  {
    return appendFile (sRelativePath, sContent.getBytes (aCharset));
  }

  /**
   * Helper function for saving a file with correct error handling.
   *
   * @param sRelativePath
   *        name of the file. May not be <code>null</code>.
   * @param aBytes
   *        the bytes to be written. May not be <code>null</code>.
   * @return {@link ESuccess}
   */
  @Nonnull
  default ESuccess appendFile (@Nonnull final String sRelativePath, @Nonnull final byte [] aBytes)
  {
    return writeFile (sRelativePath, EAppend.APPEND, aBytes);
  }
}
