/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.equals.EqualsUtils;
import com.helger.commons.hash.HashCodeGenerator;
import com.helger.commons.io.IReadableResource;
import com.helger.commons.io.IWritableResource;
import com.helger.commons.io.IWritableResourceProvider;
import com.helger.commons.io.file.FileUtils;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.io.resource.URLResource;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * Simple resource provider that only uses files.
 * 
 * @author Philip Helger
 */
@Immutable
public final class FileSystemResourceProvider implements IWritableResourceProvider
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (FileSystemResourceProvider.class);
  private final File m_aBasePath;

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
      if (!aBasePath.exists ())
        throw new IllegalArgumentException ("Passed base path '" + aBasePath + "' does not exist!");
      if (!aBasePath.isDirectory ())
        throw new IllegalArgumentException ("Passed base path '" + aBasePath + "' is not a directory!");
      if (!FileUtils.canRead (aBasePath))
        s_aLogger.warn ("Cannot read passed base path '" + aBasePath + "'!");
      if (!FileUtils.canWrite (aBasePath))
        s_aLogger.warn ("Cannot write passed base path '" + aBasePath + "'!");
      if (!FileUtils.canExecute (aBasePath))
        s_aLogger.warn ("Cannot execute in passed base path '" + aBasePath + "'!");
    }
    m_aBasePath = aBasePath;
  }

  @Nullable
  public File getBasePath ()
  {
    return m_aBasePath;
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
    if (ClassPathResource.isExplicitClassPathResource (sName))
      return false;
    if (URLResource.isExplicitURLResource (sName))
      return false;

    return StringHelper.hasText (sName) && _getFile (sName).isAbsolute ();
  }

  public boolean supportsWriting (@Nullable final String sName)
  {
    if (ClassPathResource.isExplicitClassPathResource (sName))
      return false;
    if (URLResource.isExplicitURLResource (sName))
      return false;

    return StringHelper.hasText (sName);
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
    if (!(o instanceof FileSystemResourceProvider))
      return false;
    final FileSystemResourceProvider rhs = (FileSystemResourceProvider) o;
    return EqualsUtils.equals (m_aBasePath, rhs.m_aBasePath);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aBasePath).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).appendIfNotNull ("basePath", m_aBasePath).toString ();
  }
}
