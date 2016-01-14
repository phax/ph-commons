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
package com.helger.commons.io.file.filter;

import java.io.File;
import java.io.FilenameFilter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * A special file filter that uses and external {@link FilenameFilter} to
 * determine the validity. This filter works for all types of {@link File}
 * objects.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class FileFilterFromFilenameFilter extends AbstractFileFilter
{
  private final FilenameFilter m_aFilenameFilter;

  public FileFilterFromFilenameFilter (@Nonnull final FilenameFilter aFilenameFilter)
  {
    m_aFilenameFilter = ValueEnforcer.notNull (aFilenameFilter, "FilenameFilter");
  }

  @Nonnull
  public FilenameFilter getFilenameFilter ()
  {
    return m_aFilenameFilter;
  }

  @Override
  public boolean directTest (@Nullable final File aFile)
  {
    return aFile != null && m_aFilenameFilter.accept (aFile.getParentFile (), aFile.getName ());
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!super.equals (o))
      return false;
    // FilenameFilter does not necessarily implement equals/hashCode :(
    return true;
  }

  @Override
  public int hashCode ()
  {
    return HashCodeGenerator.getDerived (super.hashCode ()).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("FilenameFilter", m_aFilenameFilter).toString ();
  }
}
