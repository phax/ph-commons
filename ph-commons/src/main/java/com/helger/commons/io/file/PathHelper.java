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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.FileSystemLoopException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.io.EAppend;
import com.helger.commons.io.stream.NonBlockingBufferedInputStream;
import com.helger.commons.io.stream.NonBlockingBufferedOutputStream;
import com.helger.commons.io.stream.NonBlockingBufferedReader;
import com.helger.commons.io.stream.NonBlockingBufferedWriter;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.state.EChange;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Miscellaneous file utility methods.
 *
 * @author Philip Helger
 */
@Immutable
public final class PathHelper
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (PathHelper.class);

  @PresentForCodeCoverage
  private static final PathHelper s_aInstance = new PathHelper ();

  private PathHelper ()
  {}

  /**
   * Check if the passed file can read and write. If the file already exists,
   * the file itself is checked. If the file does not exist, the parent
   * directory
   *
   * @param aFile
   *        The file to be checked. May be <code>null</code>.
   * @return <code>true</code> if the file can be read and written
   */
  public static boolean canReadAndWriteFile (@Nullable final Path aFile)
  {
    if (aFile == null)
      return false;

    // The Files API seem to be slow
    return FileHelper.canReadAndWriteFile (aFile.toFile ());

    // if (Files.isRegularFile (aFile))
    // {
    // // Path exists
    // if (!Files.isReadable (aFile) || !Files.isWritable (aFile))
    // return false;
    // }
    // else
    // {
    // // Path does not exist (yet)
    // // Check parent directory
    // final Path aParentDir = aFile.getParent ();
    // if (aParentDir == null ||
    // !Files.isDirectory (aParentDir) ||
    // !Files.isReadable (aParentDir) ||
    // !Files.isWritable (aParentDir))
    // return false;
    // }
    // return true;
  }

  @Nonnull
  public static EChange ensureParentDirectoryIsPresent (@Nonnull final Path aFile)
  {
    ValueEnforcer.notNull (aFile, "Path");

    // The Files API seem to be slow
    return FileHelper.ensureParentDirectoryIsPresent (aFile.toFile ());

    // // If the file has no parent, it is located in the root...
    // final Path aParent = aFile.getParent ();
    // if (aParent == null || Files.exists (aParent))
    // {
    // if (aParent != null && !Files.isDirectory (aParent))
    // if (s_aLogger.isWarnEnabled ())
    // s_aLogger.warn ("Parent object specified is not a directory: '" + aParent
    // + "'");
    // return EChange.UNCHANGED;
    // }
    //
    // // Now try to create the directory
    // final FileIOError aError = PathOperations.createDirRecursive (aParent);
    // if (aError.isFailure ())
    // throw new IllegalStateException ("Failed to create parent of " +
    // aFile.toAbsolutePath () + ": " + aError);
    //
    // // Check again if it exists, to be 100% sure :)
    // if (!Files.exists (aParent))
    // throw new IllegalStateException ("Parent of " + aFile.toAbsolutePath () +
    // " is still not existing!");
    // return EChange.CHANGED;
  }

  /**
   * Get the canonical file of the passed file, if the file is not
   * <code>null</code>.
   *
   * @param aFile
   *        The file to get the canonical path from. May be <code>null</code>.
   * @return <code>null</code> if the passed file is <code>null</code>.
   * @throws IOException
   *         If an I/O error occurs, which is possible because the construction
   *         of the canonical pathname may require filesystem queries
   */
  @Nullable
  public static Path getCanonicalFile (@Nullable final Path aFile) throws IOException
  {
    return aFile == null ? null : aFile.toRealPath ();
  }

  /**
   * Get the canonical file of the passed file, if the file is not
   * <code>null</code>. In case of an {@link IOException}, <code>null</code> is
   * returned.
   *
   * @param aFile
   *        The file to get the canonical path from. May be <code>null</code>.
   * @return <code>null</code> if the passed file is <code>null</code> or an
   *         exception occurred.
   */
  @Nullable
  public static Path getCanonicalFileOrNull (@Nullable final Path aFile)
  {
    if (aFile != null)
      try
      {
        return aFile.toRealPath ();
      }
      catch (final IOException ex)
      {
        // fall through
      }
    return null;
  }

  /**
   * Get the canonical path of the passed file, if the file is not
   * <code>null</code>.
   *
   * @param aFile
   *        The file to get the canonical path from. May be <code>null</code>.
   * @return <code>null</code> if the passed file is <code>null</code>.
   * @throws IOException
   *         If an I/O error occurs, which is possible because the construction
   *         of the canonical pathname may require filesystem queries
   */
  @Nullable
  public static String getCanonicalPath (@Nullable final Path aFile) throws IOException
  {
    // Note: getCanonicalPath may be a bottleneck on Unix based file systems!
    return aFile == null ? null : aFile.toRealPath ().toString ();
  }

  /**
   * Get the canonical path of the passed file, if the file is not
   * <code>null</code>. In case of an {@link IOException}, <code>null</code> is
   * returned.
   *
   * @param aFile
   *        The file to get the canonical path from. May be <code>null</code>.
   * @return <code>null</code> if the passed file is <code>null</code>.
   */
  @Nullable
  public static String getCanonicalPathOrNull (@Nullable final Path aFile)
  {
    if (aFile != null)
      try
      {
        // Note: toRealPath may be a bottleneck on Unix based file
        // systems!
        return aFile.toRealPath ().toString ();
      }
      catch (final IOException ex)
      {
        // fall through
      }
    return null;
  }

  /**
   * Check if the searched directory is a parent object of the start directory
   *
   * @param aSearchDirectory
   *        The directory to be searched. May not be <code>null</code>.
   * @param aStartDirectory
   *        The directory where the search starts. May not be <code>null</code>.
   * @return <code>true</code> if the search directory is a parent of the start
   *         directory, <code>false</code> otherwise.
   * @see #getCanonicalFile(Path)
   */
  @SuppressFBWarnings ("IL_INFINITE_LOOP")
  public static boolean isParentDirectory (@Nonnull final Path aSearchDirectory, @Nonnull final Path aStartDirectory)
  {
    ValueEnforcer.notNull (aSearchDirectory, "SearchDirectory");
    ValueEnforcer.notNull (aStartDirectory, "StartDirectory");

    Path aRealSearchDirectory = aSearchDirectory.toAbsolutePath ();
    Path aRealStartDirectory = aStartDirectory.toAbsolutePath ();
    try
    {
      aRealSearchDirectory = getCanonicalFile (aRealSearchDirectory);
      aRealStartDirectory = getCanonicalFile (aRealStartDirectory);
    }
    catch (final IOException ex)
    {
      // ignore
    }
    if (Files.isDirectory (aRealSearchDirectory))
    {
      Path aParent = aRealStartDirectory;
      while (aParent != null)
      {
        if (aParent.equals (aRealSearchDirectory))
          return true;
        aParent = aParent.getParent ();
      }
    }
    return false;
  }

  @Nullable
  public static InputStream getInputStream (@Nonnull final Path aFile)
  {
    ValueEnforcer.notNull (aFile, "Path");

    try
    {
      return Files.newInputStream (aFile);
    }
    catch (final IOException ex)
    {
      return null;
    }
  }

  @Nullable
  public static NonBlockingBufferedInputStream getBufferedInputStream (@Nonnull final Path aFile)
  {
    ValueEnforcer.notNull (aFile, "File");

    final InputStream aIS = getInputStream (aFile);
    if (aIS == null)
      return null;
    return new NonBlockingBufferedInputStream (aIS);
  }

  @Nullable
  public static Reader getReader (@Nonnull final Path aFile, @Nonnull final Charset aCharset)
  {
    ValueEnforcer.notNull (aFile, "Path");
    ValueEnforcer.notNull (aCharset, "Charset");

    return StreamHelper.createReader (getInputStream (aFile), aCharset);
  }

  @Nullable
  public static NonBlockingBufferedReader getBufferedReader (@Nonnull final Path aFile, @Nonnull final Charset aCharset)
  {
    ValueEnforcer.notNull (aFile, "Path");
    ValueEnforcer.notNull (aCharset, "Charset");

    final Reader aReader = getReader (aFile, aCharset);
    if (aReader == null)
      return null;
    return new NonBlockingBufferedReader (aReader);
  }

  /**
   * Get an output stream for writing to a file.
   *
   * @param aFile
   *        The file to write to. May not be <code>null</code>.
   * @return <code>null</code> if the file could not be opened
   */
  @Nullable
  public static OutputStream getOutputStream (@Nonnull final Path aFile)
  {
    return getOutputStream (aFile, EAppend.DEFAULT);
  }

  /**
   * Get an output stream for writing to a file.
   *
   * @param aFile
   *        The file to write to. May not be <code>null</code>.
   * @param eAppend
   *        Appending mode. May not be <code>null</code>.
   * @return <code>null</code> if the file could not be opened
   */
  @Nullable
  public static OutputStream getOutputStream (@Nonnull final Path aFile, @Nonnull final EAppend eAppend)
  {
    ValueEnforcer.notNull (aFile, "Path");
    ValueEnforcer.notNull (eAppend, "Append");

    // OK, parent is present and writable
    try
    {
      return Files.newOutputStream (aFile, eAppend.getAsOpenOptions ());
    }
    catch (final IOException ex)
    {
      return null;
    }
  }

  @Nullable
  public static NonBlockingBufferedOutputStream getBufferedOutputStream (@Nonnull final Path aFile)
  {
    return getBufferedOutputStream (aFile, EAppend.DEFAULT);
  }

  @Nullable
  public static NonBlockingBufferedOutputStream getBufferedOutputStream (@Nonnull final Path aFile,
                                                                         @Nonnull final EAppend eAppend)
  {
    ValueEnforcer.notNull (aFile, "File");
    ValueEnforcer.notNull (eAppend, "Append");

    final OutputStream aOS = getOutputStream (aFile, eAppend);
    if (aOS == null)
      return null;
    return new NonBlockingBufferedOutputStream (aOS);
  }

  @Nullable
  public static Writer getWriter (@Nonnull final Path aFile,
                                  @Nonnull final EAppend eAppend,
                                  @Nonnull final Charset aCharset)
  {
    ValueEnforcer.notNull (aFile, "Path");
    ValueEnforcer.notNull (aCharset, "Charset");

    return StreamHelper.createWriter (getOutputStream (aFile, eAppend), aCharset);
  }

  @Nullable
  public static NonBlockingBufferedWriter getBufferedWriter (@Nonnull final Path aFile,
                                                             @Nonnull final EAppend eAppend,
                                                             @Nonnull final Charset aCharset)
  {
    ValueEnforcer.notNull (aFile, "Path");
    ValueEnforcer.notNull (aCharset, "Charset");

    final Writer aWriter = getWriter (aFile, eAppend, aCharset);
    if (aWriter == null)
      return null;

    return new NonBlockingBufferedWriter (aWriter);
  }

  /**
   * Returns <code>true</code> if the first file is newer than the second file.
   * Returns <code>true</code> if the first file exists and the second file does
   * not exist. Returns <code>false</code> if the first file is older than the
   * second file. Returns <code>false</code> if the first file does not exists
   * but the second does. Returns <code>false</code> if none of the files exist.
   *
   * @param aFile1
   *        First file. May not be <code>null</code>.
   * @param aFile2
   *        Second file. May not be <code>null</code>.
   * @return <code>true</code> if the first file is newer than the second file,
   *         <code>false</code> otherwise.
   */
  public static boolean isFileNewer (@Nonnull final Path aFile1, @Nonnull final Path aFile2)
  {
    ValueEnforcer.notNull (aFile1, "File1");
    ValueEnforcer.notNull (aFile2, "aFile2");

    // The Files API seem to be slow
    return FileHelper.isFileNewer (aFile1.toFile (), aFile2.toFile ());

    // // Compare with the same file?
    // if (aFile1.equals (aFile2))
    // return false;
    //
    // // if the first file does not exists, always false
    // if (!Files.exists (aFile1))
    // return false;
    //
    // // first file exists, but second file does not
    // if (!Files.exists (aFile2))
    // return true;
    //
    // try
    // {
    // // both exist, compare file times
    // return Files.getLastModifiedTime (aFile1).compareTo
    // (Files.getLastModifiedTime (aFile2)) > 0;
    // }
    // catch (final IOException ex)
    // {
    // throw new UncheckedIOException (ex);
    // }
  }

  /**
   * Returns the number of files and directories contained in the passed
   * directory excluding the system internal directories.
   *
   * @param aDirectory
   *        The directory to check. May not be <code>null</code> and must be a
   *        directory.
   * @return A non-negative number of objects in that directory.
   * @see FilenameHelper#isSystemInternalDirectory(CharSequence)
   */
  @Nonnegative
  public static int getDirectoryObjectCount (@Nonnull final Path aDirectory)
  {
    ValueEnforcer.notNull (aDirectory, "Directory");
    ValueEnforcer.isTrue (Files.isDirectory (aDirectory), "Passed object is not a directory: " + aDirectory);

    int ret = 0;
    for (final Path aChild : getDirectoryContent (aDirectory))
      if (!FilenameHelper.isSystemInternalDirectory (aChild))
        ret++;
    return ret;
  }

  @Nonnull
  public static Path walkFileTree (@Nonnull final Path aStart,
                                   @Nonnegative final int nMaxDepth,
                                   @Nonnull final FileVisitor <? super Path> aVisitor)
  {
    return walkFileTree (aStart, EnumSet.noneOf (FileVisitOption.class), nMaxDepth, aVisitor);
  }

  /**
   * Walks a file tree.
   * <p>
   * This method walks a file tree rooted at a given starting file. The file
   * tree traversal is <em>depth-first</em> with the given {@link FileVisitor}
   * invoked for each file encountered. File tree traversal completes when all
   * accessible files in the tree have been visited, or a visit method returns a
   * result of {@link FileVisitResult#TERMINATE TERMINATE}. Where a visit method
   * terminates due an {@code IOException}, an uncaught error, or runtime
   * exception, then the traversal is terminated and the error or exception is
   * propagated to the caller of this method.
   * <p>
   * For each file encountered this method attempts to read its
   * {@link java.nio.file.attribute.BasicFileAttributes}. If the file is not a
   * directory then the {@link FileVisitor#visitFile visitFile} method is
   * invoked with the file attributes. If the file attributes cannot be read,
   * due to an I/O exception, then the {@link FileVisitor#visitFileFailed
   * visitFileFailed} method is invoked with the I/O exception.
   * <p>
   * Where the file is a directory, and the directory could not be opened, then
   * the {@code visitFileFailed} method is invoked with the I/O exception, after
   * which, the file tree walk continues, by default, at the next
   * <em>sibling</em> of the directory.
   * <p>
   * Where the directory is opened successfully, then the entries in the
   * directory, and their <em>descendants</em> are visited. When all entries
   * have been visited, or an I/O error occurs during iteration of the
   * directory, then the directory is closed and the visitor's
   * {@link FileVisitor#postVisitDirectory postVisitDirectory} method is
   * invoked. The file tree walk then continues, by default, at the next
   * <em>sibling</em> of the directory.
   * <p>
   * By default, symbolic links are not automatically followed by this method.
   * If the {@code options} parameter contains the
   * {@link FileVisitOption#FOLLOW_LINKS FOLLOW_LINKS} option then symbolic
   * links are followed. When following links, and the attributes of the target
   * cannot be read, then this method attempts to get the
   * {@code BasicFileAttributes} of the link. If they can be read then the
   * {@code visitFile} method is invoked with the attributes of the link
   * (otherwise the {@code visitFileFailed} method is invoked as specified
   * above).
   * <p>
   * If the {@code options} parameter contains the
   * {@link FileVisitOption#FOLLOW_LINKS FOLLOW_LINKS} option then this method
   * keeps track of directories visited so that cycles can be detected. A cycle
   * arises when there is an entry in a directory that is an ancestor of the
   * directory. Cycle detection is done by recording the
   * {@link java.nio.file.attribute.BasicFileAttributes#fileKey file-key} of
   * directories, or if file keys are not available, by invoking the
   * {@link Files#isSameFile} method to test if a directory is the same file as
   * an ancestor. When a cycle is detected it is treated as an I/O error, and
   * the {@link FileVisitor#visitFileFailed visitFileFailed} method is invoked
   * with an instance of {@link FileSystemLoopException}.
   * <p>
   * The {@code maxDepth} parameter is the maximum number of levels of
   * directories to visit. A value of {@code 0} means that only the starting
   * file is visited, unless denied by the security manager. A value of
   * {@link Integer#MAX_VALUE MAX_VALUE} may be used to indicate that all levels
   * should be visited. The {@code visitFile} method is invoked for all files,
   * including directories, encountered at {@code maxDepth}, unless the basic
   * file attributes cannot be read, in which case the {@code
   * visitFileFailed} method is invoked.
   * <p>
   * If a visitor returns a result of {@code null} then {@code
   * NullPointerException} is thrown.
   * <p>
   * When a security manager is installed and it denies access to a file (or
   * directory), then it is ignored and the visitor is not invoked for that file
   * (or directory).
   *
   * @param aStart
   *        the starting file
   * @param aOptions
   *        options to configure the traversal
   * @param nMaxDepth
   *        the maximum number of directory levels to visit
   * @param aVisitor
   *        the file visitor to invoke for each file
   * @return the starting file
   * @throws UncheckedIOException
   *         if an I/O error is thrown by a visitor method
   */
  @Nonnull
  public static Path walkFileTree (@Nonnull final Path aStart,
                                   @Nonnull final Set <FileVisitOption> aOptions,
                                   @Nonnegative final int nMaxDepth,
                                   @Nonnull final FileVisitor <? super Path> aVisitor)
  {
    try
    {
      return Files.walkFileTree (aStart, aOptions, nMaxDepth, aVisitor);
    }
    catch (final IOException ex)
    {
      throw new UncheckedIOException (ex);
    }
  }

  @Nonnull
  @ReturnsMutableCopy
  private static ICommonsList <Path> _getDirectoryContent (@Nonnull final Path aDirectory,
                                                           @Nullable final Predicate <? super Path> aPathFilter)
  {
    final ICommonsList <Path> ret = new CommonsArrayList <> ();
    walkFileTree (aDirectory, 1, new SimpleFileVisitor <Path> ()
    {
      @Override
      public FileVisitResult visitFile (final Path aCurFile, final BasicFileAttributes attrs) throws IOException
      {
        if (aPathFilter == null || aPathFilter.test (aCurFile))
          ret.add (aCurFile);
        return FileVisitResult.CONTINUE;
      }
    });

    if (ret.isEmpty ())
    {
      // No content returned
      if (aPathFilter == null)
      {
        // Try some diagnosis...
        if (!Files.isDirectory (aDirectory))
        {
          if (s_aLogger.isWarnEnabled ())
            s_aLogger.warn ("Cannot list non-directory: " + aDirectory.toAbsolutePath ());
        }
        else
          if (!Files.isExecutable (aDirectory))
          {
            // If this happens, the resulting Path objects are neither files nor
            // directories (isFile() and isDirectory() both return false!!)
            if (s_aLogger.isWarnEnabled ())
              s_aLogger.warn ("Existing directory is missing the listing permission: " + aDirectory.toAbsolutePath ());
          }
          else
            if (!Files.isReadable (aDirectory))
            {
              if (s_aLogger.isWarnEnabled ())
                s_aLogger.warn ("Cannot list directory because of no read-rights: " + aDirectory.toAbsolutePath ());
            }
            else
              if (!Files.exists (aDirectory))
              {
                if (s_aLogger.isWarnEnabled ())
                  s_aLogger.warn ("Cannot list non-existing: " + aDirectory.toAbsolutePath ());
              }
      }
    }
    else
    {
      if (!Files.isExecutable (aDirectory))
      {
        // If this happens, the resulting Path objects are neither files nor
        // directories (isFile() and isDirectory() both return false!!)
        if (s_aLogger.isWarnEnabled ())
          s_aLogger.warn ("Directory is missing the listing permission: " + aDirectory.toAbsolutePath ());
      }
    }
    return ret;
  }

  /**
   * This is a replacement for <code>Path.listFiles()</code> doing some
   * additional checks on permissions. The order of the returned files is
   * undefined. "." and ".." are not contained.
   *
   * @param aDirectory
   *        The directory to be listed. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsList <Path> getDirectoryContent (@Nonnull final Path aDirectory)
  {
    ValueEnforcer.notNull (aDirectory, "Directory");

    return _getDirectoryContent (aDirectory, null);
  }

  /**
   * This is a replacement for <code>Path.listFiles(FilenameFilter)</code> doing
   * some additional checks on permissions. The order of the returned files is
   * undefined. "." and ".." are not contained.
   *
   * @param aDirectory
   *        The directory to be listed. May not be <code>null</code>.
   * @param aPathFilter
   *        The path filter to be used. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsList <Path> getDirectoryContent (@Nonnull final Path aDirectory,
                                                         @Nullable final Predicate <? super Path> aPathFilter)
  {
    ValueEnforcer.notNull (aDirectory, "Directory");

    return _getDirectoryContent (aDirectory, aPathFilter);
  }

  @Nullable
  public static URL getAsURL (@Nonnull final Path aPath)
  {
    ValueEnforcer.notNull (aPath, "Path");

    try
    {
      return aPath.toUri ().toURL ();
    }
    catch (final MalformedURLException ex)
    {
      if (s_aLogger.isWarnEnabled ())
        s_aLogger.warn ("Failed to convert path to URL: " + aPath, ex);
      return null;
    }
  }
}
