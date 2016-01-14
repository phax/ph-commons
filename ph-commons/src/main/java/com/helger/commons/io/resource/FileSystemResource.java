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
package com.helger.commons.io.resource;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.io.EAppend;
import com.helger.commons.io.file.FileHelper;
import com.helger.commons.io.file.FilenameHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * Implementation of the
 * {@link com.helger.commons.io.resource.IReadableResource} and
 * {@link com.helger.commons.io.resource.IWritableResource} interfaces for file
 * system objects.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class FileSystemResource implements IReadWriteResource
{
  private final File m_aFile;
  private final String m_sPath;
  private Integer m_aHashCode;

  public FileSystemResource (@Nonnull final URI aURI)
  {
    this (new File (aURI));
  }

  public FileSystemResource (@Nonnull final String sParentPath, @Nonnull final String sChildPath)
  {
    this (new File (sParentPath, sChildPath));
  }

  public FileSystemResource (@Nonnull final String sFilename)
  {
    this (new File (sFilename));
  }

  public FileSystemResource (@Nonnull final File aParentFile, final String sChildPath)
  {
    this (new File (aParentFile, sChildPath));
  }

  public FileSystemResource (@Nonnull final File aFile)
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

  @Nonnull
  public String getResourceID ()
  {
    return getPath ();
  }

  @Nonnull
  public String getPath ()
  {
    return m_sPath;
  }

  @Nullable
  public InputStream getInputStream ()
  {
    return FileHelper.getInputStream (m_aFile);
  }

  @Nullable
  public Reader getReader (@Nonnull final Charset aCharset)
  {
    return FileHelper.getReader (m_aFile, aCharset);
  }

  @Nullable
  public OutputStream getOutputStream (@Nonnull final EAppend eAppend)
  {
    return FileHelper.getOutputStream (m_aFile, eAppend);
  }

  @Nullable
  public Writer getWriter (@Nonnull final Charset aCharset, @Nonnull final EAppend eAppend)
  {
    return FileHelper.getWriter (m_aFile, eAppend, aCharset);
  }

  public boolean exists ()
  {
    return m_aFile.exists ();
  }

  @Nullable
  public URL getAsURL ()
  {
    return FileHelper.getAsURL (m_aFile);
  }

  @Nonnull
  public File getAsFile ()
  {
    return m_aFile;
  }

  @Nonnull
  public FileSystemResource getReadableCloneForPath (@Nonnull final String sPath)
  {
    return new FileSystemResource (sPath);
  }

  @Nonnull
  public FileSystemResource getWritableCloneForPath (@Nonnull final String sPath)
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
    return FileHelper.canRead (m_aFile);
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
    return FileHelper.canWrite (m_aFile);
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
    return FileHelper.canExecute (m_aFile);
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
    if (m_aHashCode == null)
      m_aHashCode = new HashCodeGenerator (this).append (m_aFile).getHashCodeObj ();
    return m_aHashCode.intValue ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("file", m_aFile).toString ();
  }
}
