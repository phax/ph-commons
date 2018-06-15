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
package com.helger.commons.io.file;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.equals.EqualsHelper;

/**
 * Wraps file operations.
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class PathOperations
{
  /**
   * The default value for warning if we're about to delete the root directory.
   */
  public static final boolean DEFAULT_EXCEPTION_ON_DELETE_ROOT = true;

  private static volatile boolean s_bExceptionOnDeleteRoot = DEFAULT_EXCEPTION_ON_DELETE_ROOT;

  @PresentForCodeCoverage
  private static final PathOperations s_aInstance = new PathOperations ();

  private PathOperations ()
  {}

  public static boolean isExceptionOnDeleteRoot ()
  {
    return s_bExceptionOnDeleteRoot;
  }

  public static void setExceptionOnDeleteRoot (final boolean bExceptionOnDeleteRoot)
  {
    s_bExceptionOnDeleteRoot = bExceptionOnDeleteRoot;
  }

  /**
   * Internal dummy interface for operations with one parameter.
   *
   * @author Philip Helger
   */
  @FunctionalInterface
  private static interface IOpPath
  {
    void op (Path aPath) throws IOException;
  }

  /**
   * Internal dummy interface for operations with two parameters.
   *
   * @author Philip Helger
   */
  @FunctionalInterface
  private static interface IOpPath2
  {
    void op (Path aPath1, Path aPath2) throws IOException;
  }

  @Nonnull
  private static FileIOError _perform (@Nonnull final EFileIOOperation eOperation,
                                       @Nonnull final IOpPath aRunnable,
                                       @Nonnull final Path aPath)
  {
    try
    {
      aRunnable.op (aPath);
      return EFileIOErrorCode.NO_ERROR.getAsIOError (eOperation, aPath);
    }
    catch (final SecurityException ex)
    {
      return EFileIOErrorCode.getSecurityAsIOError (eOperation, ex);
    }
    catch (final IOException ex)
    {
      return EFileIOErrorCode.getAsIOError (eOperation, ex);
    }
    catch (final UncheckedIOException ex)
    {
      return EFileIOErrorCode.getAsIOError (eOperation, ex);
    }
  }

  @Nonnull
  private static FileIOError _perform (@Nonnull final EFileIOOperation eOp,
                                       @Nonnull final IOpPath2 aRunnable,
                                       @Nonnull final Path aPath1,
                                       @Nonnull final Path aPath2)
  {
    try
    {
      aRunnable.op (aPath1, aPath2);
      return EFileIOErrorCode.NO_ERROR.getAsIOError (eOp, aPath1, aPath2);
    }
    catch (final SecurityException ex)
    {
      return EFileIOErrorCode.getSecurityAsIOError (eOp, ex);
    }
    catch (final IOException ex)
    {
      return EFileIOErrorCode.getAsIOError (eOp, ex);
    }
    catch (final UncheckedIOException ex)
    {
      return EFileIOErrorCode.getAsIOError (eOp, ex);
    }
  }

  @Nonnull
  private static Path _getUnifiedPath (final Path aPath)
  {
    return aPath.toAbsolutePath ().normalize ();
  }

  /**
   * Create a new directory. The direct parent directory already needs to exist.
   *
   * @param aDir
   *        The directory to be created. May not be <code>null</code>.
   * @return A non-<code>null</code> error code.
   */
  @Nonnull
  public static FileIOError createDir (@Nonnull final Path aDir)
  {
    ValueEnforcer.notNull (aDir, "Directory");

    final Path aRealDir = _getUnifiedPath (aDir);
    return FileOperations.createDir (aRealDir.toFile ());

    // // Does the directory already exist?
    // if (Files.exists (aRealDir))
    // return EFileIOErrorCode.TARGET_ALREADY_EXISTS.getAsIOError
    // (EFileIOOperation.CREATE_DIR, aRealDir);
    //
    // // Is the parent directory writable?
    // final Path aParentDir = aRealDir.getParent ();
    // if (aParentDir != null && Files.exists (aParentDir) && !Files.isWritable
    // (aParentDir))
    // return EFileIOErrorCode.SOURCE_PARENT_NOT_WRITABLE.getAsIOError
    // (EFileIOOperation.CREATE_DIR, aRealDir);
    //
    // return _perform (EFileIOOperation.CREATE_DIR, Files::createDirectory,
    // aRealDir);
  }

  /**
   * Create a new directory if it does not exist. The direct parent directory
   * already needs to exist.
   *
   * @param aDir
   *        The directory to be created if it does not exist. May not be
   *        <code>null</code>.
   * @return A non-<code>null</code> error code.
   */
  @Nonnull
  public static FileIOError createDirIfNotExisting (@Nonnull final Path aDir)
  {
    final FileIOError aError = createDir (aDir);
    if (aError.getErrorCode ().equals (EFileIOErrorCode.TARGET_ALREADY_EXISTS))
      return aError.withoutErrorCode ();
    return aError;
  }

  /**
   * Create a new directory. The parent directories are created if they are
   * missing.
   *
   * @param aDir
   *        The directory to be created. May not be <code>null</code>.
   * @return A non-<code>null</code> error code.
   */
  @Nonnull
  public static FileIOError createDirRecursive (@Nonnull final Path aDir)
  {
    ValueEnforcer.notNull (aDir, "Directory");

    final Path aRealDir = _getUnifiedPath (aDir);
    return FileOperations.createDirRecursive (aRealDir.toFile ());

    // // Does the directory already exist?
    // if (Files.exists (aRealDir))
    // return EFileIOErrorCode.TARGET_ALREADY_EXISTS.getAsIOError
    // (EFileIOOperation.CREATE_DIR_RECURSIVE, aRealDir);
    //
    // // Is the parent directory writable?
    // final Path aParentDir = aRealDir.getParent ();
    // if (aParentDir != null && Files.exists (aParentDir) && !Files.isWritable
    // (aParentDir))
    // return EFileIOErrorCode.SOURCE_PARENT_NOT_WRITABLE.getAsIOError
    // (EFileIOOperation.CREATE_DIR_RECURSIVE, aRealDir);
    //
    // return _perform (EFileIOOperation.CREATE_DIR_RECURSIVE,
    // Files::createDirectories, aRealDir);
  }

  /**
   * Create a new directory if it does not exist. The direct parent directory
   * already needs to exist.
   *
   * @param aDir
   *        The directory to be created if it does not exist. May not be
   *        <code>null</code>.
   * @return A non-<code>null</code> error code.
   * @see #createDirRecursive(Path)
   */
  @Nonnull
  public static FileIOError createDirRecursiveIfNotExisting (@Nonnull final Path aDir)
  {
    final FileIOError aError = createDirRecursive (aDir);
    if (aError.getErrorCode ().equals (EFileIOErrorCode.TARGET_ALREADY_EXISTS))
      return aError.withoutErrorCode ();
    return aError;
  }

  /**
   * Delete an existing directory. The directory needs to be empty before it can
   * be deleted.
   *
   * @param aDir
   *        The directory to be deleted. May not be <code>null</code>.
   * @return A non-<code>null</code> error code.
   */
  @Nonnull
  public static FileIOError deleteDir (@Nonnull final Path aDir)
  {
    ValueEnforcer.notNull (aDir, "Directory");

    final Path aRealDir = _getUnifiedPath (aDir);

    // Does the directory not exist?
    if (!Files.isDirectory (aRealDir))
      return EFileIOErrorCode.SOURCE_DOES_NOT_EXIST.getAsIOError (EFileIOOperation.DELETE_DIR, aRealDir);

    if (isExceptionOnDeleteRoot ())
    {
      // Check that we're not deleting the complete hard drive...
      if (aRealDir.getParent () == null || aRealDir.getNameCount () == 0)
        throw new IllegalArgumentException ("Aren't we deleting the full drive: '" + aRealDir + "'");
    }

    // Is the parent directory writable?
    final Path aParentDir = aRealDir.getParent ();
    if (aParentDir != null && !Files.isWritable (aParentDir))
      return EFileIOErrorCode.SOURCE_PARENT_NOT_WRITABLE.getAsIOError (EFileIOOperation.DELETE_DIR, aRealDir);

    return _perform (EFileIOOperation.DELETE_DIR, Files::delete, aRealDir);
  }

  /**
   * Delete an existing directory if it is existing. The directory needs to be
   * empty before it can be deleted.
   *
   * @param aDir
   *        The directory to be deleted. May not be <code>null</code>.
   * @return A non-<code>null</code> error code.
   * @see #deleteDir(Path)
   */
  @Nonnull
  public static FileIOError deleteDirIfExisting (@Nonnull final Path aDir)
  {
    final FileIOError aError = deleteDir (aDir);
    if (aError.getErrorCode ().equals (EFileIOErrorCode.SOURCE_DOES_NOT_EXIST))
      return aError.withoutErrorCode ();
    return aError;
  }

  /**
   * Delete an existing directory including all child objects.
   *
   * @param aDir
   *        The directory to be deleted. May not be <code>null</code>.
   * @return A non-<code>null</code> error code.
   */
  @Nonnull
  public static FileIOError deleteDirRecursive (@Nonnull final Path aDir)
  {
    ValueEnforcer.notNull (aDir, "Directory");

    final Path aRealDir = _getUnifiedPath (aDir);

    // Non-existing directory?
    if (!Files.isDirectory (aRealDir))
      return EFileIOErrorCode.SOURCE_DOES_NOT_EXIST.getAsIOError (EFileIOOperation.DELETE_DIR_RECURSIVE, aRealDir);

    if (isExceptionOnDeleteRoot ())
    {
      // Check that we're not deleting the complete hard drive...
      if (aRealDir.getParent () == null || aRealDir.getNameCount () == 0)
        throw new IllegalArgumentException ("Aren't we deleting the full drive: '" + aRealDir + "'");
    }

    // Is the parent directory writable?
    final Path aParentDir = aRealDir.getParent ();
    if (aParentDir != null && !Files.isWritable (aParentDir))
      return EFileIOErrorCode.SOURCE_PARENT_NOT_WRITABLE.getAsIOError (EFileIOOperation.DELETE_DIR_RECURSIVE, aRealDir);

    // iterate directory
    for (final Path aChild : PathHelper.getDirectoryContent (aRealDir))
    {
      // is it a file or a directory or ...
      if (Files.isDirectory (aChild))
      {
        // Ignore "." and ".." directory
        if (FilenameHelper.isSystemInternalDirectory (aChild))
          continue;

        // recursive call
        final FileIOError eCode = deleteDirRecursive (aChild);
        if (eCode.isFailure ())
          return eCode;
      }
      else
        if (Files.isRegularFile (aChild))
        {
          // delete file
          final FileIOError eCode = deleteFile (aChild);
          if (eCode.isFailure ())
            return eCode;
        }
        else
        {
          // Neither directory no file - don't know how to handle
          return EFileIOErrorCode.OBJECT_CANNOT_BE_HANDLED.getAsIOError (EFileIOOperation.DELETE_DIR_RECURSIVE, aChild);
        }
    }

    // Now this directory should be empty -> delete as if empty
    return deleteDir (aRealDir);
  }

  /**
   * Delete an existing directory including all child objects if it is existing.
   *
   * @param aDir
   *        The directory to be deleted. May not be <code>null</code>.
   * @return A non-<code>null</code> error code.
   */
  @Nonnull
  public static FileIOError deleteDirRecursiveIfExisting (@Nonnull final Path aDir)
  {
    final FileIOError aError = deleteDirRecursive (aDir);
    if (aError.getErrorCode ().equals (EFileIOErrorCode.SOURCE_DOES_NOT_EXIST))
      return aError.withoutErrorCode ();
    return aError;
  }

  /**
   * Delete an existing file.
   *
   * @param aFile
   *        The file to be deleted. May not be <code>null</code>.
   * @return A non-<code>null</code> error code.
   */
  @Nonnull
  public static FileIOError deleteFile (@Nonnull final Path aFile)
  {
    ValueEnforcer.notNull (aFile, "Path");

    final Path aRealFile = _getUnifiedPath (aFile);

    if (!Files.isRegularFile (aRealFile))
      return EFileIOErrorCode.SOURCE_DOES_NOT_EXIST.getAsIOError (EFileIOOperation.DELETE_FILE, aRealFile);

    // Is the parent directory writable?
    final Path aParentDir = aRealFile.getParent ();
    if (aParentDir != null && !Files.isWritable (aParentDir))
      return EFileIOErrorCode.SOURCE_PARENT_NOT_WRITABLE.getAsIOError (EFileIOOperation.DELETE_FILE, aRealFile);

    return _perform (EFileIOOperation.DELETE_FILE, Files::delete, aRealFile);
  }

  /**
   * Delete a file if it is existing.
   *
   * @param aFile
   *        The file to be deleted. May not be <code>null</code>.
   * @return A non-<code>null</code> error code.
   */
  @Nonnull
  public static FileIOError deleteFileIfExisting (@Nonnull final Path aFile)
  {
    final FileIOError aError = deleteFile (aFile);
    if (aError.getErrorCode ().equals (EFileIOErrorCode.SOURCE_DOES_NOT_EXIST))
      return aError.withoutErrorCode ();
    return aError;
  }

  private static void _atomicMove (@Nonnull final Path aSourcePath, @Nonnull final Path aTargetPath) throws IOException
  {
    Files.move (aSourcePath, aTargetPath, StandardCopyOption.ATOMIC_MOVE);
  }

  /**
   * Rename a file.
   *
   * @param aSourceFile
   *        The original file name. May not be <code>null</code>.
   * @param aTargetFile
   *        The destination file name. May not be <code>null</code>.
   * @return A non-<code>null</code> error code.
   */
  @Nonnull
  public static FileIOError renameFile (@Nonnull final Path aSourceFile, @Nonnull final Path aTargetFile)
  {
    ValueEnforcer.notNull (aSourceFile, "SourceFile");
    ValueEnforcer.notNull (aTargetFile, "TargetFile");

    final Path aRealSourceFile = _getUnifiedPath (aSourceFile);
    final Path aRealTargetFile = _getUnifiedPath (aTargetFile);

    // Does the source file exist?
    if (!Files.isRegularFile (aRealSourceFile))
      return EFileIOErrorCode.SOURCE_DOES_NOT_EXIST.getAsIOError (EFileIOOperation.RENAME_FILE, aRealSourceFile);

    // Are source and target different?
    if (EqualsHelper.equals (aRealSourceFile, aRealTargetFile))
      return EFileIOErrorCode.SOURCE_EQUALS_TARGET.getAsIOError (EFileIOOperation.RENAME_FILE, aRealSourceFile);

    // Does the target file already exist?
    if (Files.exists (aRealTargetFile))
      return EFileIOErrorCode.TARGET_ALREADY_EXISTS.getAsIOError (EFileIOOperation.RENAME_FILE, aRealTargetFile);

    // Is the source parent directory writable?
    final Path aSourceParentDir = aRealSourceFile.getParent ();
    if (aSourceParentDir != null && !Files.isWritable (aSourceParentDir))
      return EFileIOErrorCode.SOURCE_PARENT_NOT_WRITABLE.getAsIOError (EFileIOOperation.RENAME_FILE, aRealSourceFile);

    // Is the target parent directory writable?
    final Path aTargetParentDir = aRealTargetFile.getParent ();
    if (aTargetParentDir != null && Files.exists (aTargetParentDir) && !Files.isWritable (aTargetParentDir))
      return EFileIOErrorCode.TARGET_PARENT_NOT_WRITABLE.getAsIOError (EFileIOOperation.RENAME_FILE, aRealTargetFile);

    // Ensure parent of target directory is present
    PathHelper.ensureParentDirectoryIsPresent (aRealTargetFile);

    return _perform (EFileIOOperation.RENAME_FILE, PathOperations::_atomicMove, aRealSourceFile, aRealTargetFile);
  }

  /**
   * Rename a directory.
   *
   * @param aSourceDir
   *        The original directory name. May not be <code>null</code>.
   * @param aTargetDir
   *        The destination directory name. May not be <code>null</code>.
   * @return A non-<code>null</code> error code.
   */
  @Nonnull
  public static FileIOError renameDir (@Nonnull final Path aSourceDir, @Nonnull final Path aTargetDir)
  {
    ValueEnforcer.notNull (aSourceDir, "SourceDirectory");
    ValueEnforcer.notNull (aTargetDir, "TargetDirectory");

    final Path aRealSourceDir = _getUnifiedPath (aSourceDir);
    final Path aRealTargetDir = _getUnifiedPath (aTargetDir);

    // Does the source directory exist?
    if (!Files.isDirectory (aRealSourceDir))
      return EFileIOErrorCode.SOURCE_DOES_NOT_EXIST.getAsIOError (EFileIOOperation.RENAME_DIR, aRealSourceDir);

    // Are source and target different?
    if (EqualsHelper.equals (aRealSourceDir, aRealTargetDir))
      return EFileIOErrorCode.SOURCE_EQUALS_TARGET.getAsIOError (EFileIOOperation.RENAME_DIR, aRealSourceDir);

    // Does the target directory already exist?
    if (Files.exists (aRealTargetDir))
      return EFileIOErrorCode.TARGET_ALREADY_EXISTS.getAsIOError (EFileIOOperation.RENAME_DIR, aRealTargetDir);

    // Is the source a parent of target?
    if (PathHelper.isParentDirectory (aRealSourceDir, aRealTargetDir))
      return EFileIOErrorCode.TARGET_IS_CHILD_OF_SOURCE.getAsIOError (EFileIOOperation.RENAME_DIR,
                                                                      aRealSourceDir,
                                                                      aRealTargetDir);

    // Is the source parent directory writable?
    final Path aSourceParentDir = aRealSourceDir.getParent ();
    if (aSourceParentDir != null && !Files.isWritable (aSourceParentDir))
      return EFileIOErrorCode.SOURCE_PARENT_NOT_WRITABLE.getAsIOError (EFileIOOperation.RENAME_DIR, aRealSourceDir);

    // Is the target parent directory writable?
    final Path aTargetParentDir = aRealTargetDir.getParent ();
    if (aTargetParentDir != null && Files.exists (aTargetParentDir) && !Files.isWritable (aTargetParentDir))
      return EFileIOErrorCode.TARGET_PARENT_NOT_WRITABLE.getAsIOError (EFileIOOperation.RENAME_DIR, aRealTargetDir);

    // Ensure parent of target directory is present
    PathHelper.ensureParentDirectoryIsPresent (aRealTargetDir);

    return _perform (EFileIOOperation.RENAME_DIR, PathOperations::_atomicMove, aRealSourceDir, aRealTargetDir);
  }

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
  @Nonnull
  public static FileIOError copyFile (@Nonnull final Path aSourceFile, @Nonnull final Path aTargetFile)
  {
    ValueEnforcer.notNull (aSourceFile, "SourceFile");
    ValueEnforcer.notNull (aTargetFile, "TargetFile");

    final Path aRealSourceFile = _getUnifiedPath (aSourceFile);
    final Path aRealTargetFile = _getUnifiedPath (aTargetFile);

    // Does the source file exist?
    if (!Files.isRegularFile (aRealSourceFile))
      return EFileIOErrorCode.SOURCE_DOES_NOT_EXIST.getAsIOError (EFileIOOperation.COPY_FILE, aRealSourceFile);

    // Are source and target different?
    if (EqualsHelper.equals (aRealSourceFile, aRealTargetFile))
      return EFileIOErrorCode.SOURCE_EQUALS_TARGET.getAsIOError (EFileIOOperation.COPY_FILE, aRealSourceFile);

    // Does the target file already exist?
    if (Files.exists (aRealTargetFile))
      return EFileIOErrorCode.TARGET_ALREADY_EXISTS.getAsIOError (EFileIOOperation.COPY_FILE, aRealTargetFile);

    // Is the source file readable?
    if (!Files.isReadable (aRealSourceFile))
      return EFileIOErrorCode.SOURCE_NOT_READABLE.getAsIOError (EFileIOOperation.COPY_FILE, aRealSourceFile);

    // Is the target parent directory writable?
    final Path aTargetParentDir = aRealTargetFile.getParent ();
    if (aTargetParentDir != null && Files.exists (aTargetParentDir) && !Files.isWritable (aTargetParentDir))
      return EFileIOErrorCode.TARGET_PARENT_NOT_WRITABLE.getAsIOError (EFileIOOperation.COPY_FILE, aRealTargetFile);

    // Ensure the targets parent directory is present
    PathHelper.ensureParentDirectoryIsPresent (aRealTargetFile);

    return _perform (EFileIOOperation.COPY_FILE, Files::copy, aRealSourceFile, aRealTargetFile);
  }

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
  @Nonnull
  public static FileIOError copyDirRecursive (@Nonnull final Path aSourceDir, @Nonnull final Path aTargetDir)
  {
    ValueEnforcer.notNull (aSourceDir, "SourceDirectory");
    ValueEnforcer.notNull (aTargetDir, "TargetDirectory");

    final Path aRealSourceDir = _getUnifiedPath (aSourceDir);
    final Path aRealTargetDir = _getUnifiedPath (aTargetDir);

    // Does the source directory exist?
    if (!Files.isDirectory (aRealSourceDir))
      return EFileIOErrorCode.SOURCE_DOES_NOT_EXIST.getAsIOError (EFileIOOperation.COPY_DIR_RECURSIVE, aRealSourceDir);

    // Are source and target different?
    if (EqualsHelper.equals (aRealSourceDir, aRealTargetDir))
      return EFileIOErrorCode.SOURCE_EQUALS_TARGET.getAsIOError (EFileIOOperation.COPY_DIR_RECURSIVE, aRealSourceDir);

    // Is the source a parent of target?
    if (PathHelper.isParentDirectory (aRealSourceDir, aRealTargetDir))
      return EFileIOErrorCode.TARGET_IS_CHILD_OF_SOURCE.getAsIOError (EFileIOOperation.COPY_DIR_RECURSIVE,
                                                                      aRealSourceDir,
                                                                      aRealTargetDir);

    // Does the target directory already exist?
    if (Files.exists (aRealTargetDir))
      return EFileIOErrorCode.TARGET_ALREADY_EXISTS.getAsIOError (EFileIOOperation.COPY_DIR_RECURSIVE, aRealTargetDir);

    // Is the source directory readable?
    if (!Files.isReadable (aRealSourceDir))
      return EFileIOErrorCode.SOURCE_NOT_READABLE.getAsIOError (EFileIOOperation.COPY_DIR_RECURSIVE, aRealSourceDir);

    // Is the target parent directory writable?
    final Path aTargetParentDir = aRealTargetDir.getParent ();
    if (aTargetParentDir != null && Files.exists (aTargetParentDir) && !Files.isWritable (aTargetParentDir))
      return EFileIOErrorCode.TARGET_PARENT_NOT_WRITABLE.getAsIOError (EFileIOOperation.COPY_DIR_RECURSIVE,
                                                                       aRealTargetDir);

    FileIOError eCode;

    // Ensure the targets parent directory is present
    eCode = createDirRecursive (aRealTargetDir);
    if (eCode.isFailure ())
      return eCode;

    for (final Path aChild : PathHelper.getDirectoryContent (aRealSourceDir))
    {
      if (Files.isDirectory (aChild))
      {
        // Skip "." and ".."
        if (FilenameHelper.isSystemInternalDirectory (aChild))
          continue;

        // Copy directory
        eCode = copyDirRecursive (aChild, aRealTargetDir.resolve (aChild.getFileName ()));
        if (eCode.isFailure ())
          return eCode;
      }
      else
        if (Files.isRegularFile (aChild))
        {
          // Copy a file
          eCode = copyFile (aChild, aRealTargetDir.resolve (aChild.getFileName ()));
          if (eCode.isFailure ())
            return eCode;
        }
        else
        {
          // Neither directory not file - don't know how to handle
          return EFileIOErrorCode.OBJECT_CANNOT_BE_HANDLED.getAsIOError (EFileIOOperation.COPY_DIR_RECURSIVE, aChild);
        }
    }

    // Done
    return EFileIOErrorCode.NO_ERROR.getAsIOError (EFileIOOperation.COPY_DIR_RECURSIVE, aRealSourceDir, aRealTargetDir);
  }
}
