/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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

    // Does the directory already exist?
    if (Files.exists (aDir))
      return EFileIOErrorCode.TARGET_ALREADY_EXISTS.getAsIOError (EFileIOOperation.CREATE_DIR, aDir);

    // Is the parent directory writable?
    final Path aParentDir = aDir.getParent ();
    if (aParentDir != null && Files.exists (aParentDir) && !Files.isWritable (aParentDir))
      return EFileIOErrorCode.SOURCE_PARENT_NOT_WRITABLE.getAsIOError (EFileIOOperation.CREATE_DIR, aDir);

    return _perform (EFileIOOperation.CREATE_DIR, Files::createDirectory, aDir);
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

    // Does the directory already exist?
    if (Files.exists (aDir))
      return EFileIOErrorCode.TARGET_ALREADY_EXISTS.getAsIOError (EFileIOOperation.CREATE_DIR_RECURSIVE, aDir);

    // Is the parent directory writable?
    final Path aParentDir = aDir.getParent ();
    if (aParentDir != null && Files.exists (aParentDir) && !Files.isWritable (aParentDir))
      return EFileIOErrorCode.SOURCE_PARENT_NOT_WRITABLE.getAsIOError (EFileIOOperation.CREATE_DIR_RECURSIVE, aDir);

    return _perform (EFileIOOperation.CREATE_DIR_RECURSIVE, Files::createDirectories, aDir);
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

    // Does the directory not exist?
    if (!Files.isDirectory (aDir))
      return EFileIOErrorCode.SOURCE_DOES_NOT_EXIST.getAsIOError (EFileIOOperation.DELETE_DIR, aDir);

    if (isExceptionOnDeleteRoot ())
    {
      // Check that we're not deleting the complete hard drive...
      if (aDir.toAbsolutePath ().getParent () == null)
        throw new IllegalArgumentException ("Aren't we deleting the full drive: '" + aDir.toAbsolutePath () + "'");
    }

    // Is the parent directory writable?
    final Path aParentDir = aDir.getParent ();
    if (aParentDir != null && !Files.isWritable (aParentDir))
      return EFileIOErrorCode.SOURCE_PARENT_NOT_WRITABLE.getAsIOError (EFileIOOperation.DELETE_DIR, aDir);

    return _perform (EFileIOOperation.DELETE_DIR, Files::delete, aDir);
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

    // Non-existing directory?
    if (!Files.isDirectory (aDir))
      return EFileIOErrorCode.SOURCE_DOES_NOT_EXIST.getAsIOError (EFileIOOperation.DELETE_DIR_RECURSIVE, aDir);

    if (isExceptionOnDeleteRoot ())
    {
      // Check that we're not deleting the complete hard drive...
      if (aDir.toAbsolutePath ().getParent () == null)
        throw new IllegalArgumentException ("Aren't we deleting the full drive: '" + aDir.toAbsolutePath () + "'");
    }

    // Is the parent directory writable?
    final Path aParentDir = aDir.getParent ();
    if (aParentDir != null && !Files.isWritable (aParentDir))
      return EFileIOErrorCode.SOURCE_PARENT_NOT_WRITABLE.getAsIOError (EFileIOOperation.DELETE_DIR_RECURSIVE, aDir);

    // iterate directory
    for (final Path aChild : PathHelper.getDirectoryContent (aDir))
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
    return deleteDir (aDir);
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

    if (!Files.isRegularFile (aFile))
      return EFileIOErrorCode.SOURCE_DOES_NOT_EXIST.getAsIOError (EFileIOOperation.DELETE_FILE, aFile);

    // Is the parent directory writable?
    final Path aParentDir = aFile.getParent ();
    if (aParentDir != null && !Files.isWritable (aParentDir))
      return EFileIOErrorCode.SOURCE_PARENT_NOT_WRITABLE.getAsIOError (EFileIOOperation.DELETE_FILE, aFile);

    return _perform (EFileIOOperation.DELETE_FILE, Files::delete, aFile);
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

  public static void atomicMove (@Nonnull final Path aSourcePath, @Nonnull final Path aTargetPath) throws IOException
  {
    ValueEnforcer.notNull (aSourcePath, "SourceFile");
    ValueEnforcer.notNull (aTargetPath, "TargetFile");

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

    // Does the source file exist?
    if (!Files.isRegularFile (aSourceFile))
      return EFileIOErrorCode.SOURCE_DOES_NOT_EXIST.getAsIOError (EFileIOOperation.RENAME_FILE, aSourceFile);

    // Are source and target different?
    if (EqualsHelper.equals (aSourceFile, aTargetFile))
      return EFileIOErrorCode.SOURCE_EQUALS_TARGET.getAsIOError (EFileIOOperation.RENAME_FILE, aSourceFile);

    // Does the target file already exist?
    if (Files.exists (aTargetFile))
      return EFileIOErrorCode.TARGET_ALREADY_EXISTS.getAsIOError (EFileIOOperation.RENAME_FILE, aTargetFile);

    // Is the source parent directory writable?
    final Path aSourceParentDir = aSourceFile.getParent ();
    if (aSourceParentDir != null && !Files.isWritable (aSourceParentDir))
      return EFileIOErrorCode.SOURCE_PARENT_NOT_WRITABLE.getAsIOError (EFileIOOperation.RENAME_FILE, aSourceFile);

    // Is the target parent directory writable?
    final Path aTargetParentDir = aTargetFile.getParent ();
    if (aTargetParentDir != null && Files.exists (aTargetParentDir) && !Files.isWritable (aTargetParentDir))
      return EFileIOErrorCode.TARGET_PARENT_NOT_WRITABLE.getAsIOError (EFileIOOperation.RENAME_FILE, aTargetFile);

    // Ensure parent of target directory is present
    PathHelper.ensureParentDirectoryIsPresent (aTargetFile);

    return _perform (EFileIOOperation.RENAME_FILE, PathOperations::atomicMove, aSourceFile, aTargetFile);
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

    // Does the source directory exist?
    if (!Files.isDirectory (aSourceDir))
      return EFileIOErrorCode.SOURCE_DOES_NOT_EXIST.getAsIOError (EFileIOOperation.RENAME_DIR, aSourceDir);

    // Are source and target different?
    if (EqualsHelper.equals (aSourceDir, aTargetDir))
      return EFileIOErrorCode.SOURCE_EQUALS_TARGET.getAsIOError (EFileIOOperation.RENAME_DIR, aSourceDir);

    // Does the target directory already exist?
    if (Files.exists (aTargetDir))
      return EFileIOErrorCode.TARGET_ALREADY_EXISTS.getAsIOError (EFileIOOperation.RENAME_DIR, aTargetDir);

    // Is the source a parent of target?
    if (PathHelper.isParentDirectory (aSourceDir, aTargetDir))
      return EFileIOErrorCode.TARGET_IS_CHILD_OF_SOURCE.getAsIOError (EFileIOOperation.RENAME_DIR,
                                                                      aSourceDir,
                                                                      aTargetDir);

    // Is the source parent directory writable?
    final Path aSourceParentDir = aSourceDir.getParent ();
    if (aSourceParentDir != null && !Files.isWritable (aSourceParentDir))
      return EFileIOErrorCode.SOURCE_PARENT_NOT_WRITABLE.getAsIOError (EFileIOOperation.RENAME_DIR, aSourceDir);

    // Is the target parent directory writable?
    final Path aTargetParentDir = aTargetDir.getParent ();
    if (aTargetParentDir != null && Files.exists (aTargetParentDir) && !Files.isWritable (aTargetParentDir))
      return EFileIOErrorCode.TARGET_PARENT_NOT_WRITABLE.getAsIOError (EFileIOOperation.RENAME_DIR, aTargetDir);

    // Ensure parent of target directory is present
    PathHelper.ensureParentDirectoryIsPresent (aTargetDir);

    return _perform (EFileIOOperation.RENAME_DIR, PathOperations::atomicMove, aSourceDir, aTargetDir);
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

    // Does the source file exist?
    if (!Files.isRegularFile (aSourceFile))
      return EFileIOErrorCode.SOURCE_DOES_NOT_EXIST.getAsIOError (EFileIOOperation.COPY_FILE, aSourceFile);

    // Are source and target different?
    if (EqualsHelper.equals (aSourceFile, aTargetFile))
      return EFileIOErrorCode.SOURCE_EQUALS_TARGET.getAsIOError (EFileIOOperation.COPY_FILE, aSourceFile);

    // Does the target file already exist?
    if (Files.exists (aTargetFile))
      return EFileIOErrorCode.TARGET_ALREADY_EXISTS.getAsIOError (EFileIOOperation.COPY_FILE, aTargetFile);

    // Is the source file readable?
    if (!Files.isReadable (aSourceFile))
      return EFileIOErrorCode.SOURCE_NOT_READABLE.getAsIOError (EFileIOOperation.COPY_FILE, aSourceFile);

    // Is the target parent directory writable?
    final Path aTargetParentDir = aTargetFile.getParent ();
    if (aTargetParentDir != null && Files.exists (aTargetParentDir) && !Files.isWritable (aTargetParentDir))
      return EFileIOErrorCode.TARGET_PARENT_NOT_WRITABLE.getAsIOError (EFileIOOperation.COPY_FILE, aTargetFile);

    // Ensure the targets parent directory is present
    PathHelper.ensureParentDirectoryIsPresent (aTargetFile);

    return _perform (EFileIOOperation.COPY_FILE, Files::copy, aSourceFile, aTargetFile);
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

    // Does the source directory exist?
    if (!Files.isDirectory (aSourceDir))
      return EFileIOErrorCode.SOURCE_DOES_NOT_EXIST.getAsIOError (EFileIOOperation.COPY_DIR_RECURSIVE, aSourceDir);

    // Are source and target different?
    if (EqualsHelper.equals (aSourceDir, aTargetDir))
      return EFileIOErrorCode.SOURCE_EQUALS_TARGET.getAsIOError (EFileIOOperation.COPY_DIR_RECURSIVE, aSourceDir);

    // Is the source a parent of target?
    if (PathHelper.isParentDirectory (aSourceDir, aTargetDir))
      return EFileIOErrorCode.TARGET_IS_CHILD_OF_SOURCE.getAsIOError (EFileIOOperation.COPY_DIR_RECURSIVE,
                                                                      aSourceDir,
                                                                      aTargetDir);

    // Does the target directory already exist?
    if (Files.exists (aTargetDir))
      return EFileIOErrorCode.TARGET_ALREADY_EXISTS.getAsIOError (EFileIOOperation.COPY_DIR_RECURSIVE, aTargetDir);

    // Is the source directory readable?
    if (!Files.isReadable (aSourceDir))
      return EFileIOErrorCode.SOURCE_NOT_READABLE.getAsIOError (EFileIOOperation.COPY_DIR_RECURSIVE, aSourceDir);

    // Is the target parent directory writable?
    final Path aTargetParentDir = aTargetDir.getParent ();
    if (aTargetParentDir != null && Files.exists (aTargetParentDir) && !Files.isWritable (aTargetParentDir))
      return EFileIOErrorCode.TARGET_PARENT_NOT_WRITABLE.getAsIOError (EFileIOOperation.COPY_DIR_RECURSIVE, aTargetDir);

    FileIOError eCode;

    // Ensure the targets parent directory is present
    eCode = createDirRecursive (aTargetDir);
    if (eCode.isFailure ())
      return eCode;

    for (final Path aChild : PathHelper.getDirectoryContent (aSourceDir))
    {
      if (Files.isDirectory (aChild))
      {
        // Skip "." and ".."
        if (FilenameHelper.isSystemInternalDirectory (aChild))
          continue;

        // Copy directory
        eCode = copyDirRecursive (aChild, aTargetDir.resolve (aChild.getFileName ()));
        if (eCode.isFailure ())
          return eCode;
      }
      else
        if (Files.isRegularFile (aChild))
        {
          // Copy a file
          eCode = copyFile (aChild, aTargetDir.resolve (aChild.getFileName ()));
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
    return EFileIOErrorCode.NO_ERROR.getAsIOError (EFileIOOperation.COPY_DIR_RECURSIVE, aSourceDir, aTargetDir);
  }
}
