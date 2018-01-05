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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.io.file.FileHelper;
import com.helger.commons.io.file.FilenameHelper;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.io.resourceresolver.DefaultResourceResolver;
import com.helger.commons.string.ToStringGenerator;

/**
 * Default implementation of {@link IPathRelativeIO}.
 *
 * @author Philip Helger
 */
@Immutable
public class PathRelativeIO implements IPathRelativeIO
{
  private final String m_sBasePath;
  private final String m_sBaseURL;

  public PathRelativeIO (@Nonnull @Nonempty final String sBasePath)
  {
    ValueEnforcer.notEmpty (sBasePath, "BasePath");
    m_sBasePath = sBasePath;

    // Use special base URL if base path is an existing file!
    String sBaseURL = null;
    final File aFile = new File (sBasePath);
    if (aFile.exists ())
      sBaseURL = FileHelper.getAsURLString (aFile);
    m_sBaseURL = sBaseURL != null ? sBaseURL : sBasePath;
  }

  @Nonnull
  @Nonempty
  public String getBasePath ()
  {
    return m_sBasePath;
  }

  @Nonnull
  @Nonempty
  public String getBaseURL ()
  {
    return m_sBaseURL;
  }

  @Nonnull
  public IReadableResource getResource (@Nonnull @Nonempty final String sRelativePath)
  {
    ValueEnforcer.notEmpty (sRelativePath, "RelativePath");

    // Remove any leading slash to avoid that the resource resolver considers
    // this an absolute URL on Linux!
    final String sEffectiveRelativePath = FilenameHelper.startsWithPathSeparatorChar (sRelativePath) ? sRelativePath.substring (1)
                                                                                                     : sRelativePath;

    return DefaultResourceResolver.getResolvedResource (sEffectiveRelativePath, m_sBaseURL);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final PathRelativeIO rhs = (PathRelativeIO) o;
    return m_sBasePath.equals (rhs.m_sBasePath);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sBasePath).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("BasePath", m_sBasePath).append ("BaseURL", m_sBaseURL).getToString ();
  }
}
