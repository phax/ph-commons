/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.io.file.filter;

import java.io.File;
import java.io.FilenameFilter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.hash.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * A special directory file filter that uses and external filename filter to
 * determine the validity. This filter only works for directories.
 *
 * @author Philip Helger
 */
public final class FileFilterDirectoryFromFilenameFilter extends AbstractFileFilter
{
  private final FilenameFilter m_aFilenameFilter;

  public FileFilterDirectoryFromFilenameFilter (@Nonnull final FilenameFilter aFilenameFilter)
  {
    m_aFilenameFilter = ValueEnforcer.notNull (aFilenameFilter, "FilenameFilter");
  }

  @Nonnull
  public final FilenameFilter getFilenameFilter ()
  {
    return m_aFilenameFilter;
  }

  public boolean matchesFilter (@Nullable final File aFile)
  {
    return aFile != null && aFile.isDirectory () && m_aFilenameFilter.accept (aFile.getParentFile (), aFile.getName ());
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    // FilenameFilter does not necessarily implement equals/hashCode :(
    return true;
  }

  @Override
  public int hashCode ()
  {
    // FilenameFilter does not implement hashCode
    return new HashCodeGenerator (this).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("filenameFilter", m_aFilenameFilter).toString ();
  }
}
