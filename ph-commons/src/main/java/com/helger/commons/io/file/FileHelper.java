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

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.CGlobal;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.io.EAppend;
import com.helger.commons.io.misc.SizeHelper;
import com.helger.commons.io.stream.CountingFileInputStream;
import com.helger.commons.io.stream.CountingFileOutputStream;
import com.helger.commons.io.stream.NonBlockingBufferedInputStream;
import com.helger.commons.io.stream.NonBlockingBufferedOutputStream;
import com.helger.commons.io.stream.NonBlockingBufferedReader;
import com.helger.commons.io.stream.NonBlockingBufferedWriter;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.state.EChange;
import com.helger.commons.state.EValidity;
import com.helger.commons.system.SystemProperties;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Miscellaneous file utility methods.
 *
 * @author Philip Helger
 */
@Immutable
public final class FileHelper
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (FileHelper.class);

  @PresentForCodeCoverage
  private static final FileHelper s_aInstance = new FileHelper ();

  private FileHelper ()
  {}

  /**
   * Check if the passed file exists. Must be existing and a file.
   *
   * @param aFile
   *        The file to be checked for existence. May be <code>null</code> .
   * @return <code>true</code> if the passed file is non-<code>null</code>, is a
   *         file and exists, <code>false</code> otherwise.
   */
  public static boolean existsFile (@Nullable final File aFile)
  {
    // returns true if it exists() AND is a file!
    return aFile != null && aFile.isFile ();
  }

  /**
   * Check if the passed directory exists. Must be existing and must be a
   * directory!
   *
   * @param aDir
   *        The directory to be checked for existence. May be <code>null</code>.
   * @return <code>true</code> if the passed directory is not <code>null</code>,
   *         is a directory and exists, <code>false</code> otherwise.
   */
  public static boolean existsDir (@Nullable final File aDir)
  {
    // returns true if it exists() AND is a directory!
    return aDir != null && aDir.isDirectory ();
  }

  /**
   * Check if the passed file can read and write. If the file already exists,
   * the file itself is checked. If the file does not exist, the parent
   * directory
   *
   * @param aFile
   *        The file to be checked. May be <code>null</code>.
   * @return <code>true</code> if the file can be read and written
   */
  public static boolean canReadAndWriteFile (@Nullable final File aFile)
  {
    if (aFile == null)
      return false;

    if (aFile.exists ())
    {
      // File exists
      if (!aFile.canRead () || !aFile.canWrite ())
        return false;
    }
    else
    {
      // Path does not exist (yet)
      // Check parent directory
      final File aParentFile = aFile.getAbsoluteFile ().getParentFile ();
      if (aParentFile == null || !aParentFile.isDirectory () || !aParentFile.canRead () || !aParentFile.canWrite ())
        return false;
    }
    return true;
  }

  @Nonnull
  public static EChange ensureParentDirectoryIsPresent (@Nonnull final File aFile)
  {
    ValueEnforcer.notNull (aFile, "File");

    // If the file has no parent, it is located in the root...
    final File aParent = aFile.getParentFile ();
    if (aParent == null || aParent.exists ())
    {
      if (aParent != null && !aParent.isDirectory ())
        if (s_aLogger.isWarnEnabled ())
          s_aLogger.warn ("Parent object specified is not a directory: '" + aParent + "'");
      return EChange.UNCHANGED;
    }

    // Now try to create the directory
    final FileIOError aError = FileOperations.createDirRecursive (aParent);
    if (aError.isFailure ())
      throw new IllegalStateException ("Failed to create parent of " + aFile.getAbsolutePath () + ": " + aError);

    // Check again if it exists, to be 100% sure :)
    if (!aParent.exists ())
      throw new IllegalStateException ("Parent of " + aFile.getAbsolutePath () + " is still not existing!");
    return EChange.CHANGED;
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
  public static File getCanonicalFile (@Nullable final File aFile) throws IOException
  {
    return aFile == null ? null : aFile.getCanonicalFile ();
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
  public static File getCanonicalFileOrNull (@Nullable final File aFile)
  {
    if (aFile != null)
      try
      {
        return aFile.getCanonicalFile ();
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
  public static String getCanonicalPath (@Nullable final File aFile) throws IOException
  {
    // Note: getCanonicalPath may be a bottleneck on Unix based file systems!
    return aFile == null ? null : aFile.getCanonicalPath ();
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
  public static String getCanonicalPathOrNull (@Nullable final File aFile)
  {
    if (aFile != null)
      try
      {
        // Note: getCanonicalPath may be a bottleneck on Unix based file
        // systems!
        return aFile.getCanonicalPath ();
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
   * @see #getCanonicalFile(File)
   */
  @SuppressFBWarnings ("IL_INFINITE_LOOP")
  public static boolean isParentDirectory (@Nonnull final File aSearchDirectory, @Nonnull final File aStartDirectory)
  {
    ValueEnforcer.notNull (aSearchDirectory, "SearchDirectory");
    ValueEnforcer.notNull (aStartDirectory, "StartDirectory");

    File aRealSearchDirectory = aSearchDirectory.getAbsoluteFile ();
    File aRealStartDirectory = aStartDirectory.getAbsoluteFile ();
    try
    {
      aRealSearchDirectory = getCanonicalFile (aRealSearchDirectory);
      aRealStartDirectory = getCanonicalFile (aRealStartDirectory);
    }
    catch (final IOException ex)
    {
      // ignore
    }
    if (!aRealSearchDirectory.isDirectory ())
      return false;

    File aParent = aRealStartDirectory;
    while (aParent != null)
    {
      if (aParent.equals (aRealSearchDirectory))
        return true;
      aParent = aParent.getParentFile ();
    }
    return false;
  }

  @Nullable
  public static FileInputStream getInputStream (@Nonnull final File aFile)
  {
    ValueEnforcer.notNull (aFile, "File");

    try
    {
      return new CountingFileInputStream (aFile);
    }
    catch (final FileNotFoundException ex)
    {
      return null;
    }
  }

  @Nullable
  public static NonBlockingBufferedInputStream getBufferedInputStream (@Nonnull final File aFile)
  {
    ValueEnforcer.notNull (aFile, "File");

    final FileInputStream aIS = getInputStream (aFile);
    if (aIS == null)
      return null;
    return new NonBlockingBufferedInputStream (aIS);
  }

  @Nullable
  public static InputStreamReader getReader (@Nonnull final File aFile, @Nonnull final Charset aCharset)
  {
    ValueEnforcer.notNull (aFile, "File");
    ValueEnforcer.notNull (aCharset, "Charset");

    return StreamHelper.createReader (getInputStream (aFile), aCharset);
  }

  @Nullable
  public static NonBlockingBufferedReader getBufferedReader (@Nonnull final File aFile, @Nonnull final Charset aCharset)
  {
    ValueEnforcer.notNull (aFile, "File");
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
  public static FileOutputStream getOutputStream (@Nonnull final File aFile)
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
  public static FileOutputStream getOutputStream (@Nonnull final File aFile, @Nonnull final EAppend eAppend)
  {
    ValueEnforcer.notNull (aFile, "File");
    ValueEnforcer.notNull (eAppend, "Append");

    if (internalCheckParentDirectoryExistanceAndAccess (aFile).isInvalid ())
      return null;

    // OK, parent is present and writable
    try
    {
      return new CountingFileOutputStream (aFile, eAppend);
    }
    catch (final FileNotFoundException ex)
    {
      if (s_aLogger.isWarnEnabled ())
        s_aLogger.warn ("Failed to create output stream for '" +
                        aFile +
                        "'; append: " +
                        eAppend +
                        ": " +
                        ex.getClass ().getName () +
                        " - " +
                        ex.getMessage ());
      return null;
    }
  }

  @Nullable
  public static NonBlockingBufferedOutputStream getBufferedOutputStream (@Nonnull final File aFile)
  {
    return getBufferedOutputStream (aFile, EAppend.DEFAULT);
  }

  @Nullable
  public static NonBlockingBufferedOutputStream getBufferedOutputStream (@Nonnull final File aFile,
                                                                         @Nonnull final EAppend eAppend)
  {
    ValueEnforcer.notNull (aFile, "File");
    ValueEnforcer.notNull (eAppend, "Append");

    final FileOutputStream aFOS = getOutputStream (aFile, eAppend);
    if (aFOS == null)
      return null;
    return new NonBlockingBufferedOutputStream (aFOS);
  }

  @Nullable
  public static OutputStreamWriter getWriter (@Nonnull final File aFile, @Nonnull final Charset aCharset)
  {
    return getWriter (aFile, EAppend.DEFAULT, aCharset);
  }

  @Nullable
  public static OutputStreamWriter getWriter (@Nonnull final File aFile,
                                              @Nonnull final EAppend eAppend,
                                              @Nonnull final Charset aCharset)
  {
    ValueEnforcer.notNull (aFile, "File");
    ValueEnforcer.notNull (aCharset, "Charset");

    return StreamHelper.createWriter (getOutputStream (aFile, eAppend), aCharset);
  }

  @Nullable
  public static NonBlockingBufferedWriter getBufferedWriter (@Nonnull final File aFile, @Nonnull final Charset aCharset)
  {
    return getBufferedWriter (aFile, EAppend.DEFAULT, aCharset);
  }

  @Nullable
  public static NonBlockingBufferedWriter getBufferedWriter (@Nonnull final File aFile,
                                                             @Nonnull final EAppend eAppend,
                                                             @Nonnull final Charset aCharset)
  {
    ValueEnforcer.notNull (aFile, "File");
    ValueEnforcer.notNull (aCharset, "Charset");

    final Writer aWriter = getWriter (aFile, eAppend, aCharset);
    if (aWriter == null)
      return null;

    return new NonBlockingBufferedWriter (aWriter);
  }

  @Nullable
  public static PrintWriter getPrintWriter (@Nonnull final File aFile, @Nonnull final Charset aCharset)
  {
    return getPrintWriter (aFile, EAppend.DEFAULT, aCharset);
  }

  @Nullable
  public static PrintWriter getPrintWriter (@Nonnull final File aFile,
                                            @Nonnull final EAppend eAppend,
                                            @Nonnull final Charset aCharset)
  {
    return new PrintWriter (getBufferedWriter (aFile, eAppend, aCharset));
  }

  @Nonnull
  static EValidity internalCheckParentDirectoryExistanceAndAccess (@Nonnull final File aFile)
  {
    try
    {
      ensureParentDirectoryIsPresent (aFile);
    }
    catch (final IllegalStateException ex)
    {
      // Happens e.g. when the parent directory is " "
      if (s_aLogger.isWarnEnabled ())
        s_aLogger.warn ("Failed to create parent directory of '" + aFile + "'", ex);
      return EValidity.INVALID;
    }

    // Check if parent directory is writable, to avoid catching the
    // FileNotFoundException with "permission denied" afterwards
    final File aParentDir = aFile.getParentFile ();
    if (aParentDir != null && !aParentDir.canWrite ())
    {
      if (s_aLogger.isWarnEnabled ())
        s_aLogger.warn ("Parent directory '" +
                        aParentDir +
                        "' of '" +
                        aFile +
                        "' is not writable for current user '" +
                        SystemProperties.getUserName () +
                        "'");
      return EValidity.INVALID;
    }

    return EValidity.VALID;
  }

  @Nullable
  public static RandomAccessFile getRandomAccessFile (@Nonnull final String sFilename,
                                                      @Nonnull final ERandomAccessFileMode eMode)
  {
    return getRandomAccessFile (new File (sFilename), eMode);
  }

  @Nullable
  public static RandomAccessFile getRandomAccessFile (@Nonnull final File aFile,
                                                      @Nonnull final ERandomAccessFileMode eMode)
  {
    ValueEnforcer.notNull (aFile, "File");
    ValueEnforcer.notNull (eMode, "Mode");

    try
    {
      return new RandomAccessFile (aFile, eMode.getMode ());
    }
    catch (final FileNotFoundException ex)
    {
      return null;
    }
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
  public static boolean isFileNewer (@Nonnull final File aFile1, @Nonnull final File aFile2)
  {
    ValueEnforcer.notNull (aFile1, "File1");
    ValueEnforcer.notNull (aFile2, "aFile2");

    // Compare with the same file?
    if (aFile1.equals (aFile2))
      return false;

    // if the first file does not exists, always false
    if (!aFile1.exists ())
      return false;

    // first file exists, but second file does not
    if (!aFile2.exists ())
      return true;

    // both exist, compare file times
    return aFile1.lastModified () > aFile2.lastModified ();
  }

  @Nonnull
  public static String getFileSizeDisplay (@Nullable final File aFile)
  {
    return getFileSizeDisplay (aFile, 0);
  }

  @Nonnull
  public static String getFileSizeDisplay (@Nullable final File aFile, @Nonnegative final int nDecimals)
  {
    if (aFile != null && aFile.exists ())
      return getFileSizeDisplay (aFile.length (), nDecimals);
    return "";
  }

  @Nonnull
  public static String getFileSizeDisplay (final long nFileSize)
  {
    return getFileSizeDisplay (nFileSize, 0);
  }

  @Nonnull
  public static String getFileSizeDisplay (@Nonnegative final long nFileSize, @Nonnegative final int nDecimals)
  {
    ValueEnforcer.isGE0 (nFileSize, "FileSize");
    ValueEnforcer.isGE0 (nDecimals, "Decimals");

    return SizeHelper.getSizeHelperOfLocale (CGlobal.LOCALE_FIXED_NUMBER_FORMAT).getAsMatching (nFileSize, nDecimals);
  }

  /**
   * Get a secure {@link File} object based on the passed file object. First all
   * relative paths ("." and "..") are resolved and all eventually contained
   * '\0' characters are eliminated. Than all file names are checked for
   * validity (so that no special characters are contained).
   *
   * @param aFile
   *        The file to be secured.
   * @return <code>null</code> if the passed file is <code>null</code>.
   */
  @Nullable
  public static File getSecureFile (@Nullable final File aFile)
  {
    if (aFile == null)
      return null;

    String sRet = null;
    // Go through all directories and secure them
    File aWork = new File (FilenameHelper.getCleanPath (aFile));
    while (aWork != null)
    {
      String sSecuredName;
      if (aWork.getParent () == null)
      {
        // For the root directory
        sSecuredName = aWork.getPath ();
      }
      else
      {
        // Secure path element name
        sSecuredName = FilenameHelper.getAsSecureValidFilename (aWork.getName ());
      }
      sRet = sRet == null ? sSecuredName : sSecuredName + "/" + sRet;
      aWork = aWork.getParentFile ();
    }
    return new File (sRet);
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
  public static int getDirectoryObjectCount (@Nonnull final File aDirectory)
  {
    ValueEnforcer.notNull (aDirectory, "Directory");
    ValueEnforcer.isTrue (aDirectory.isDirectory (), "Passed object is not a directory: " + aDirectory);

    int ret = 0;
    for (final File aChild : getDirectoryContent (aDirectory))
      if (!FilenameHelper.isSystemInternalDirectory (aChild.getName ()))
        ret++;
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  private static ICommonsList <File> _getDirectoryContent (@Nonnull final File aDirectory,
                                                           @Nullable final File [] aSelectedContent)
  {
    if (aSelectedContent == null)
    {
      // No content returned
      if (!aDirectory.isDirectory ())
        s_aLogger.warn ("Cannot list non-directory: " + aDirectory.getAbsolutePath ());
      else
        if (!aDirectory.canExecute ())
        {
          // If this happens, the resulting File objects are neither files nor
          // directories (isFile() and isDirectory() both return false!!)
          s_aLogger.warn ("Existing directory is missing the listing permission: " + aDirectory.getAbsolutePath ());
        }
        else
          if (!aDirectory.canRead ())
            s_aLogger.warn ("Cannot list directory because of no read-rights: " + aDirectory.getAbsolutePath ());
          else
            s_aLogger.warn ("Directory listing failed because of IO error: " + aDirectory.getAbsolutePath ());
    }
    else
    {
      if (!aDirectory.canExecute ())
      {
        // If this happens, the resulting File objects are neither files nor
        // directories (isFile() and isDirectory() both return false!!)
        s_aLogger.warn ("Directory is missing the listing permission: " + aDirectory.getAbsolutePath ());
      }
    }
    return new CommonsArrayList <> (aSelectedContent);
  }

  /**
   * This is a replacement for <code>File.listFiles()</code> doing some
   * additional checks on permissions. The order of the returned files is
   * defined by the underlying {@link File#listFiles()} method.
   *
   * @param aDirectory
   *        The directory to be listed. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsList <File> getDirectoryContent (@Nonnull final File aDirectory)
  {
    ValueEnforcer.notNull (aDirectory, "Directory");

    return _getDirectoryContent (aDirectory, aDirectory.listFiles ());
  }

  /**
   * This is a replacement for <code>File.listFiles(FilenameFilter)</code> doing
   * some additional checks on permissions. The order of the returned files is
   * defined by the underlying {@link File#listFiles(FilenameFilter)} method.
   *
   * @param aDirectory
   *        The directory to be listed. May not be <code>null</code>.
   * @param aFilenameFilter
   *        The filename filter to be used. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsList <File> getDirectoryContent (@Nonnull final File aDirectory,
                                                         @Nonnull final FilenameFilter aFilenameFilter)
  {
    ValueEnforcer.notNull (aDirectory, "Directory");
    ValueEnforcer.notNull (aFilenameFilter, "FilenameFilter");

    return _getDirectoryContent (aDirectory, aDirectory.listFiles (aFilenameFilter));
  }

  /**
   * This is a replacement for <code>File.listFiles(FileFilter)</code> doing
   * some additional checks on permissions. The order of the returned files is
   * defined by the underlying {@link File#listFiles(FileFilter)} method.
   *
   * @param aDirectory
   *        The directory to be listed. May not be <code>null</code>.
   * @param aFileFilter
   *        The file filter to be used. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsList <File> getDirectoryContent (@Nonnull final File aDirectory,
                                                         @Nonnull final FileFilter aFileFilter)
  {
    ValueEnforcer.notNull (aDirectory, "Directory");
    ValueEnforcer.notNull (aFileFilter, "FileFilter");

    return _getDirectoryContent (aDirectory, aDirectory.listFiles (aFileFilter));
  }

  @Nullable
  public static URL getAsURL (@Nonnull final File aFile)
  {
    ValueEnforcer.notNull (aFile, "File");

    try
    {
      return aFile.toURI ().toURL ();
    }
    catch (final MalformedURLException ex)
    {
      s_aLogger.warn ("Failed to convert file to URL: " + aFile, ex);
      return null;
    }
  }

  @Nullable
  public static String getAsURLString (@Nonnull final File aFile)
  {
    ValueEnforcer.notNull (aFile, "File");

    try
    {
      return aFile.toURI ().toURL ().toExternalForm ();
    }
    catch (final MalformedURLException ex)
    {
      s_aLogger.warn ("Failed to convert file to URL: " + aFile, ex);
      return null;
    }
  }
}
