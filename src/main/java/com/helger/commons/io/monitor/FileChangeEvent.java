/**
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
package com.helger.commons.io.monitor;

import java.io.File;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;

/**
 * An event fired when a file is changed.
 * 
 * @author <a href="http://commons.apache.org/vfs/team-list.html">Commons VFS
 *         team</a>
 * @author Philip Helger
 */
public class FileChangeEvent
{
  /**
   * The file object
   */
  private final File m_aFile;

  public FileChangeEvent (@Nonnull final File aFile)
  {
    m_aFile = ValueEnforcer.notNull (aFile, "File");
  }

  /**
   * Returns the file that changed.
   * 
   * @return The file that was changed. Never <code>null</code>.
   */
  @Nonnull
  public File getFile ()
  {
    return m_aFile;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("file", m_aFile).toString ();
  }
}
