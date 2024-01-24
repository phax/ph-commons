/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
package com.helger.commons.io.resourceprovider;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.io.resource.IWritableResource;
import com.helger.commons.io.resource.URLResource;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * Simple resource provider that only uses files.
 *
 * @author Philip Helger
 */
@ThreadSafe
public class FileSystemResourceProvider implements IWritableResourceProvider
{
  private static final Logger LOGGER = LoggerFactory.getLogger (FileSystemResourceProvider.class);

  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
  private final File m_aBasePath;
  @GuardedBy ("m_aRWLock")
  private boolean m_bCanReadRelativePaths = false;

  public FileSystemResourceProvider ()
  {
    this ((File) null);
  }

  public FileSystemResourceProvider (@Nonnull final String sBasePath)
  {
    this (new File (sBasePath));
  }

  public FileSystemResourceProvider (@Nullable final File aBasePath)
  {
    if (aBasePath != null)
    {
      ValueEnforcer.isTrue (aBasePath.exists (), () -> "Passed base path '" + aBasePath + "' does not exist!");
      ValueEnforcer.isTrue (aBasePath.isDirectory (), () -> "Passed base path '" + aBasePath + "' is not a directory!");
      if (!aBasePath.canRead ())
        LOGGER.warn ("Cannot read passed base path '" + aBasePath + "'!");
      if (!aBasePath.canWrite ())
        LOGGER.warn ("Cannot write passed base path '" + aBasePath + "'!");
      if (!aBasePath.canExecute ())
        LOGGER.warn ("Cannot execute in passed base path '" + aBasePath + "'!");
    }
    m_aBasePath = aBasePath;
  }

  @Nullable
  public final File getBasePath ()
  {
    return m_aBasePath;
  }

  public final boolean isCanReadRelativePaths ()
  {
    return m_aRWLock.readLockedBoolean ( () -> m_bCanReadRelativePaths);
  }

  @Nonnull
  public final FileSystemResourceProvider setCanReadRelativePaths (final boolean bCanReadRelativePaths)
  {
    m_aRWLock.writeLockedBoolean ( () -> m_bCanReadRelativePaths = bCanReadRelativePaths);
    return this;
  }

  @Nonnull
  private File _getFile (@Nonnull final String sName)
  {
    ValueEnforcer.notNull (sName, "Name");

    // Don't add special handling for absolute paths here!
    // Breaks Unix handling!
    return m_aBasePath == null ? new File (sName) : new File (m_aBasePath, sName);
  }

  public boolean supportsReading (@Nullable final String sName)
  {
    if (StringHelper.hasNoText (sName))
      return false;
    if (ClassPathResource.isExplicitClassPathResource (sName))
      return false;
    if (URLResource.isExplicitURLResource (sName))
      return false;

    if (isCanReadRelativePaths ())
      return true;
    // Must be an absolute path (for backwards compatibility)
    return _getFile (sName).isAbsolute ();
  }

  public boolean supportsWriting (@Nullable final String sName)
  {
    if (StringHelper.hasNoText (sName))
      return false;
    if (ClassPathResource.isExplicitClassPathResource (sName))
      return false;
    if (URLResource.isExplicitURLResource (sName))
      return false;

    return true;
  }

  @Nonnull
  public IReadableResource getReadableResource (@Nonnull final String sName)
  {
    return new FileSystemResource (_getFile (sName));
  }

  @Nonnull
  public IWritableResource getWritableResource (@Nonnull final String sName)
  {
    return new FileSystemResource (_getFile (sName));
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final FileSystemResourceProvider rhs = (FileSystemResourceProvider) o;
    return EqualsHelper.equals (m_aBasePath, rhs.m_aBasePath) && m_bCanReadRelativePaths == rhs.m_bCanReadRelativePaths;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aBasePath).append (m_bCanReadRelativePaths).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).appendIfNotNull ("BasePath", m_aBasePath)
                                       .append ("CanReadRelativePaths", m_bCanReadRelativePaths)
                                       .getToString ();
  }
}
