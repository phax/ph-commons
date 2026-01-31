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
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.io.EAppend;
import com.helger.base.io.stream.StreamHelper;
import com.helger.base.tostring.ToStringGenerator;

/**
 * Implementation of the {@link IInputStreamProvider} and {@link IOutputStreamProvider} interfaces
 * for {@link File} objects.
 *
 * @author Philip Helger
 */
@Immutable
public final class FileSystemCharStreamProvider implements
                                                IInputStreamProvider,
                                                IOutputStreamProvider,
                                                IReaderProvider,
                                                IWriterProvider
{
  private final FileSystemByteStreamProvider m_aByteStreamResolver;
  private final Charset m_aCharset;

  public FileSystemCharStreamProvider (@NonNull final String sBasePath, @NonNull final Charset aCharset)
  {
    this (new File (sBasePath), aCharset);
  }

  public FileSystemCharStreamProvider (@NonNull final File aBasePath, @NonNull final Charset aCharset)
  {
    m_aByteStreamResolver = new FileSystemByteStreamProvider (aBasePath);
    m_aCharset = ValueEnforcer.notNull (aCharset, "Charset");
  }

  @NonNull
  public File getBasePath ()
  {
    return m_aByteStreamResolver.getBasePath ();
  }

  @NonNull
  public Charset getCharset ()
  {
    return m_aCharset;
  }

  @Nullable
  public InputStream getInputStream (@NonNull final String sName)
  {
    return m_aByteStreamResolver.getInputStream (sName);
  }

  @Nullable
  public OutputStream getOutputStream (@NonNull final String sName, @NonNull final EAppend eAppend)
  {
    return m_aByteStreamResolver.getOutputStream (sName, eAppend);
  }

  @Nullable
  public Reader getReader (@NonNull final String sName)
  {
    return StreamHelper.createReader (getInputStream (sName), m_aCharset);
  }

  @Nullable
  public Writer getWriter (@NonNull final String sName, @NonNull final EAppend eAppend)
  {
    return StreamHelper.createWriter (getOutputStream (sName, eAppend), m_aCharset);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final FileSystemCharStreamProvider rhs = (FileSystemCharStreamProvider) o;
    return m_aByteStreamResolver.equals (rhs.m_aByteStreamResolver) && m_aCharset.equals (rhs.m_aCharset);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aByteStreamResolver).append (m_aCharset).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("byteStreamResolver", m_aByteStreamResolver)
                                       .append ("charset", m_aCharset)
                                       .getToString ();
  }
}
