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
import java.io.IOException;
import java.io.UncheckedIOException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.exception.InitializationException;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.io.file.FileOperationManager;
import com.helger.commons.io.file.FileSystemRecursiveIterator;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.timing.StopWatch;

/**
 * Default implementation of {@link IFileRelativeIO}.
 *
 * @author Philip Helger
 */
@Immutable
public class FileRelativeIO implements IFileRelativeIO
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (FileRelativeIO.class);

  private final File m_aBasePath;

  public static void internalCheckAccessRights (@Nonnull final File aBasePath)
  {
    // Check read/write/execute
    final StopWatch aSW = StopWatch.createdStarted ();
    if (s_aLogger.isInfoEnabled ())
      s_aLogger.info ("Checking file access in " + aBasePath);
    int nFiles = 0;
    int nDirs = 0;
    for (final File aFile : new FileSystemRecursiveIterator (aBasePath))
      if (aFile.isFile ())
      {
        // Check if files are read-write
        if (!aFile.canRead ())
          throw new IllegalArgumentException ("Cannot read file " + aFile);
        if (!aFile.canWrite ())
          if (s_aLogger.isWarnEnabled ())
            s_aLogger.warn ("Cannot write file " + aFile);
        ++nFiles;
      }
      else
        if (aFile.isDirectory ())
        {
          if (!aFile.canRead ())
            throw new IllegalArgumentException ("Cannot read in directory " + aFile);
          if (!aFile.canWrite ())
          {
            if (s_aLogger.isWarnEnabled ())
              s_aLogger.warn ("Cannot write in directory " + aFile);
          }
          if (!aFile.canExecute ())
          {
            if (s_aLogger.isWarnEnabled ())
              s_aLogger.warn ("Cannot execute in directory " + aFile);
          }
          ++nDirs;
        }
        else
        {
          if (s_aLogger.isWarnEnabled ())
            s_aLogger.warn ("Neither file nor directory: " + aFile);
        }

    if (s_aLogger.isInfoEnabled ())
      s_aLogger.info ("Finished checking file access for " +
                      nFiles +
                      " files and " +
                      nDirs +
                      " directories in " +
                      aSW.stopAndGetMillis () +
                      " milliseconds");
  }

  public FileRelativeIO (@Nonnull final File aBasePath)
  {
    ValueEnforcer.notNull (aBasePath, "BasePath");
    if (!aBasePath.isAbsolute ())
      throw new IllegalArgumentException ("Please provide an absolute path: " + aBasePath);

    // Ensure the directory is present
    FileOperationManager.INSTANCE.createDirRecursiveIfNotExisting (aBasePath);

    // Must be an existing directory (and not e.g. a file)
    if (!aBasePath.isDirectory ())
      throw new InitializationException ("The passed base path " + aBasePath + " exists but is not a directory!");
    try
    {
      m_aBasePath = aBasePath.getCanonicalFile ();
    }
    catch (final IOException ex)
    {
      throw new UncheckedIOException (ex);
    }
  }

  @Nonnull
  public File getBasePathFile ()
  {
    return m_aBasePath;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final FileRelativeIO rhs = (FileRelativeIO) o;
    return m_aBasePath.equals (rhs.m_aBasePath);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aBasePath).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("BasePath", m_aBasePath).getToString ();
  }

  @Nonnull
  public static FileRelativeIO createForCurrentDir ()
  {
    return new FileRelativeIO (new File (".").getAbsoluteFile ());
  }
}
