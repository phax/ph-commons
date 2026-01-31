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
package com.helger.io.provider;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.io.EAppend;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.io.file.FileHelper;

/**
 * Implementation of the {@link IInputStreamProvider} and
 * {@link IOutputStreamProvider} interfaces for {@link File} objects.
 *
 * @author Philip Helger
 */
@Immutable
public class FileSystemByteStreamProvider implements IInputStreamProvider, IOutputStreamProvider
{
  private final File m_aBasePath;

  public FileSystemByteStreamProvider (@NonNull final String sBasePath)
  {
    this (new File (sBasePath));
  }

  public FileSystemByteStreamProvider (@NonNull final File aBasePath)
  {
    ValueEnforcer.notNull (aBasePath, "BasePath");
    ValueEnforcer.isTrue (aBasePath.exists (), () -> "Base path does not exist: " + aBasePath);
    ValueEnforcer.isTrue (aBasePath.isDirectory (), () -> "Only directories are allowed as base path: " + aBasePath);
    m_aBasePath = aBasePath;
  }

  @NonNull
  public File getBasePath ()
  {
    return m_aBasePath;
  }

  @Nullable
  public InputStream getInputStream (@NonNull final String sName)
  {
    return FileHelper.getInputStream (new File (m_aBasePath, sName));
  }

  @Nullable
  public OutputStream getOutputStream (@NonNull final String sName, @NonNull final EAppend eAppend)
  {
    return FileHelper.getOutputStream (new File (m_aBasePath, sName), eAppend);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final FileSystemByteStreamProvider rhs = (FileSystemByteStreamProvider) o;
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
    return new ToStringGenerator (this).appendIfNotNull ("basePath", m_aBasePath).getToString ();
  }
}
