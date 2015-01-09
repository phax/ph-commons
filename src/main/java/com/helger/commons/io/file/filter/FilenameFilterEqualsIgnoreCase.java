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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.io.file.FilenameHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * A filename filter that checks whether a file has the specified name. The
 * implementation is done via {@link String#equalsIgnoreCase(String)} so it is
 * case insensitive.
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class FilenameFilterEqualsIgnoreCase extends AbstractFileFilter
{
  private final String m_sFilename;

  /**
   * @param sFilename
   *        The extension to use. May neither be <code>null</code> nor empty.
   */
  public FilenameFilterEqualsIgnoreCase (@Nonnull @Nonempty final String sFilename)
  {
    m_sFilename = ValueEnforcer.notEmpty (sFilename, "Filename");
  }

  @Nonnull
  @Nonempty
  public String getFilename ()
  {
    return m_sFilename;
  }

  public boolean matchesFilter (@Nullable final File aFile)
  {
    return aFile != null && FilenameHelper.getSecureFilename (aFile.getName ()).equalsIgnoreCase (m_sFilename);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("filename", m_sFilename).toString ();
  }
}
