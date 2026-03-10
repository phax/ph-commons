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
package com.helger.io.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.hashcode.IHashCodeGenerator;
import com.helger.base.io.EAppend;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.io.file.FileHelper;
import com.helger.io.file.FilenameHelper;

/**
 * Implementation of the
 * {@link com.helger.io.resource.IReadableResource} and
 * {@link com.helger.io.resource.IWritableResource} interfaces for file
 * system objects.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class FileSystemResource implements IReadWriteResource
{
  private final File m_aFile;
  private final String m_sPath;
  // Status var
  private int m_nHashCode = IHashCodeGenerator.ILLEGAL_HASHCODE;

  /**
   * Create a new file system resource from a URI.
   *
   * @param aURI
   *        The URI to create the resource from. May not be <code>null</code>.
   */
  public FileSystemResource (@NonNull final URI aURI)
  {
    this (new File (aURI));
  }

  /**
   * Create a new file system resource from a parent path and a child path.
   *
   * @param sParentPath
   *        The parent path. May not be <code>null</code>.
   * @param sChildPath
   *        The child path. May not be <code>null</code>.
   */
  public FileSystemResource (@NonNull final String sParentPath, @NonNull final String sChildPath)
  {
    this (new File (sParentPath, sChildPath));
  }

  /**
   * Create a new file system resource from a filename.
   *
   * @param sFilename
   *        The filename of the resource. May not be <code>null</code>.
   */
  public FileSystemResource (@NonNull final String sFilename)
  {
    this (new File (sFilename));
  }

  /**
   * Create a new file system resource from a parent file and a child path.
   *
   * @param aParentFile
   *        The parent file. May not be <code>null</code>.
   * @param sChildPath
   *        The child path.
   */
  public FileSystemResource (@NonNull final File aParentFile, final String sChildPath)
  {
    this (new File (aParentFile, sChildPath));
  }

  /**
   * Create a new file system resource from a {@link File}. The file path is made absolute and
   * cleaned.
   *
   * @param aFile
   *        The file to use. May not be <code>null</code>.
   */
  public FileSystemResource (@NonNull final File aFile)
  {
    ValueEnforcer.notNull (aFile, "File");

    // Make absolute and try to remove all ".." etc paths
    // Note: using getCleanPath with String is much faster compared to
    // getCleanPath with a File parameter, as on Unix the
    // UnixFileSystem.getCanonicalPath method is a bottleneck
    final String sPath = FilenameHelper.getCleanPath (aFile.getAbsolutePath ());
    m_aFile = new File (sPath);

    // Note: cache absolute path for performance reasons
    // Note: this path always uses the platform dependent path separator
    m_sPath = m_aFile.getAbsolutePath ();
  }

  /**
   * Create a new file system resource from a {@link Path}.
   *
   * @param aPath
   *        The path to use. May not be <code>null</code>.
   */
  public FileSystemResource (@NonNull final Path aPath)
  {
    this (aPath.toFile ());
  }

  /**
   * Get the unique resource ID of this file system resource.
   *
   * @return The absolute path of the file. Never <code>null</code>.
   */
  @NonNull
  public String getResourceID ()
  {
    return getPath ();
  }

  /**
   * Get the absolute path of this file system resource.
   *
   * @return The absolute path. Never <code>null</code>.
   */
  @NonNull
  public String getPath ()
  {
    return m_sPath;
  }

  /**
   * Get an input stream for reading from this resource.
   *
   * @return <code>null</code> if the file does not exist or cannot be opened.
   */
  @Nullable
  public FileInputStream getInputStream ()
  {
    return FileHelper.getInputStream (m_aFile);
  }

  @Override
  @Nullable
  public Reader getReader (@NonNull final Charset aCharset)
  {
    return FileHelper.getReader (m_aFile, aCharset);
  }

  /**
   * Check if this resource can be read multiple times. File system resources always return
   * <code>true</code>.
   *
   * @return Always <code>true</code>.
   */
  public final boolean isReadMultiple ()
  {
    return true;
  }

  /**
   * Get an output stream for writing to this resource.
   *
   * @param eAppend
   *        Appending mode. May not be <code>null</code>.
   * @return <code>null</code> if the file could not be opened.
   */
  @Nullable
  public FileOutputStream getOutputStream (@NonNull final EAppend eAppend)
  {
    return FileHelper.getOutputStream (m_aFile, eAppend);
  }

  @Override
  @Nullable
  public Writer getWriter (@NonNull final Charset aCharset, @NonNull final EAppend eAppend)
  {
    return FileHelper.getWriter (m_aFile, eAppend, aCharset);
  }

  /**
   * Check if this resource can be written multiple times. File system resources always return
   * <code>true</code>.
   *
   * @return Always <code>true</code>.
   */
  public final boolean isWriteMultiple ()
  {
    return true;
  }

  /**
   * Check if the underlying file exists.
   *
   * @return <code>true</code> if the file exists, <code>false</code> otherwise.
   */
  public boolean exists ()
  {
    return m_aFile.exists ();
  }

  /**
   * Get this resource as a {@link URL}.
   *
   * @return <code>null</code> if the conversion to URL failed.
   */
  @Nullable
  public URL getAsURL ()
  {
    return FileHelper.getAsURL (m_aFile);
  }

  /**
   * Get this resource as a {@link File}.
   *
   * @return The underlying file. Never <code>null</code>.
   */
  @NonNull
  public File getAsFile ()
  {
    return m_aFile;
  }

  /**
   * Create a new {@link FileSystemResource} for a different path.
   *
   * @param sPath
   *        The new path to use. May not be <code>null</code>.
   * @return A new {@link FileSystemResource} instance. Never <code>null</code>.
   */
  @NonNull
  public FileSystemResource getReadableCloneForPath (@NonNull final String sPath)
  {
    return new FileSystemResource (sPath);
  }

  /**
   * Create a new writable {@link FileSystemResource} for a different path.
   *
   * @param sPath
   *        The new path to use. May not be <code>null</code>.
   * @return A new {@link FileSystemResource} instance. Never <code>null</code>.
   */
  @NonNull
  public FileSystemResource getWritableCloneForPath (@NonNull final String sPath)
  {
    return new FileSystemResource (sPath);
  }

  /**
   * Tests whether the application can read the file denoted by this abstract
   * pathname.
   *
   * @return <code>true</code> if and only if the file specified by this
   *         abstract pathname exists <em>and</em> can be read by the
   *         application; <code>false</code> otherwise
   */
  public boolean canRead ()
  {
    return m_aFile.canRead ();
  }

  /**
   * Tests whether the application can modify the file denoted by this abstract
   * pathname.
   *
   * @return <code>true</code> if and only if the file system actually contains
   *         a file denoted by this abstract pathname <em>and</em> the
   *         application is allowed to write to the file; <code>false</code>
   *         otherwise.
   */
  public boolean canWrite ()
  {
    return m_aFile.canWrite ();
  }

  /**
   * Tests whether the application can execute the file denoted by this abstract
   * pathname.
   *
   * @return <code>true</code> if and only if the abstract pathname exists
   *         <em>and</em> the application is allowed to execute the file
   */
  public boolean canExecute ()
  {
    return m_aFile.canExecute ();
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final FileSystemResource rhs = (FileSystemResource) o;
    return m_aFile.equals (rhs.m_aFile);
  }

  @Override
  public int hashCode ()
  {
    // We need a cached one!
    int ret = m_nHashCode;
    if (ret == IHashCodeGenerator.ILLEGAL_HASHCODE)
      ret = m_nHashCode = new HashCodeGenerator (this).append (m_aFile).getHashCode ();
    return ret;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("file", m_aFile).getToString ();
  }
}
